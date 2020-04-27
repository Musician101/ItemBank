package io.musician101.itembank.common.account.storage.database;

import com.google.common.base.Strings;
import com.google.common.reflect.TypeToken;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import io.musician101.itembank.common.Reference.Config;
import io.musician101.itembank.common.Reference.Database;
import io.musician101.itembank.common.Reference.Messages;
import io.musician101.itembank.common.Reference.PlayerData;
import io.musician101.itembank.common.account.Account;
import io.musician101.itembank.common.account.AccountWorld;
import io.musician101.itembank.common.account.storage.ItemStackParsing;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.SimpleConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.bson.Document;

public class MongoAccountStorage<I> extends AccountDatabaseStorage<I> {

    private MongoDatabase mongoDatabase;
    private final String uri;

    public MongoAccountStorage(@Nonnull Map<String, String> options, @Nonnull ItemStackParsing<I> itemStackParsing) {
        super(options, itemStackParsing);
        this.uri = options.get(Config.MONGO_URI);
    }

    @Override
    @Nonnull
    public List<String> load() {
        List<String> errors = new ArrayList<>();
        MongoClient mongoClient;
        if (!Strings.isNullOrEmpty(uri)) {
            mongoClient = new MongoClient(uri);
        }
        else {
            MongoCredential credential = null;
            if (!Strings.isNullOrEmpty(username)) {
                credential = MongoCredential.createCredential(username, address, password.toCharArray());
            }

            String[] addressSplit = address.split(":");
            String host = addressSplit[0];
            int port = addressSplit.length > 1 ? Integer.parseInt(addressSplit[1]) : 27017;
            ServerAddress address = new ServerAddress(host, port);
            if (credential == null) {
                mongoClient = new MongoClient(address);
            }
            else {
                mongoClient = new MongoClient(address, credential, MongoClientOptions.builder().build());
            }
        }

        mongoDatabase = mongoClient.getDatabase(database);
        MongoCollection<Document> c = mongoDatabase.getCollection(Database.TABLE_NAME);
        c.find().forEach((Consumer<Document>) document -> {
            UUID uuid = UUID.fromString(document.getString(PlayerData.ID));
            String name = document.getString(PlayerData.NAME);
            Account<I> account = new Account<>(uuid, name);
            document.getList(PlayerData.WORLDS, Document.class, Collections.emptyList()).forEach(worldDocument -> {
                AccountWorld<I> accountWorld = new AccountWorld<>(worldDocument.getString(PlayerData.WORLD));
                TreeMap<Integer, I[]> map = new TreeMap<>(Integer::compareTo);
                worldDocument.getList(PlayerData.PAGES, Document.class, Collections.emptyList()).forEach(pageDocument -> {
                    int page = pageDocument.getInteger(PlayerData.PAGE);
                    map.put(page, pageDocument.getList(PlayerData.ITEMS, Document.class, Collections.emptyList()).stream().map(itemDocument -> {
                        String itemString = itemDocument.toJson();
                        try {
                            ConfigurationNode itemNode = SimpleConfigurationNode.root();
                            //noinspection UnstableApiUsage
                            TypeSerializers.getDefaultSerializers().get(TypeToken.of(String.class)).serialize(TypeToken.of(String.class), itemString, itemNode);
                            return itemStackParsing.load(itemNode);
                        }
                        catch (ObjectMappingException e) {
                            errors.add(Messages.invalidItem(name, accountWorld.getWorldName(), page, itemDocument.getInteger(PlayerData.SLOT), itemString));
                            return null;
                        }
                    }).collect(Collectors.toList()).toArray(itemStackParsing.emptyArray()));
                });

                IntStream.range(0, map.lastKey()).forEach(i -> accountWorld.setPage(i, map.getOrDefault(i, itemStackParsing.emptyArray())));
                account.setWorld(accountWorld);
            });
        });
        mongoClient.close();
        return errors;
    }

    @Override
    @Nonnull
    public List<String> save() {
        List<String> errors = new ArrayList<>();
        MongoCollection<Document> c = mongoDatabase.getCollection(Database.TABLE_NAME);
        getAccounts().forEach(account -> {
            Document document = new Document();
            document.put(PlayerData.ID, account.getID());
            document.put(PlayerData.NAME, account.getName());
            document.put(PlayerData.WORLDS,
            account.getWorlds().stream().map(accountWorld -> {
                Document worldDocument = new Document();
                worldDocument.put(PlayerData.WORLD, accountWorld.getWorldName());
                List<I[]> pages = accountWorld.getPages();
                worldDocument.put(PlayerData.PAGES, IntStream.range(0, pages.size()).mapToObj(x -> {
                    Document pageDocument = new Document();
                    pageDocument.put(PlayerData.PAGE, x);
                    pageDocument.put(PlayerData.ITEMS, Arrays.stream(pages.get(x), 0, 45).map(itemStackParsing::save).collect(Collectors.toList()));
                    return pageDocument;
                }).collect(Collectors.toList()));
                return worldDocument;
            }).collect(Collectors.toList()));
            c.updateOne(Filters.eq(PlayerData.ID, account.getID()), document);
        });

        return errors;
    }
}

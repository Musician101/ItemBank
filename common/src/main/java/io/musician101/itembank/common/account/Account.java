package io.musician101.itembank.common.account;

import io.leangen.geantyref.TypeToken;
import io.musician101.itembank.common.Reference.AccountData;
import io.musician101.itembank.common.Reference.Database;
import io.musician101.itembank.common.Reference.Messages;
import io.musician101.itembank.common.account.storage.ItemStackSerializer;
import io.musician101.musicianlibrary.java.storage.database.mongo.MongoSerializable;
import io.musician101.musicianlibrary.java.storage.database.sql.SQLStatementSerializable;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bson.Document;
import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.gson.GsonConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

public class Account<I> {

    @Nonnull
    private final UUID uuid;
    private final BiPredicate<String, AccountWorld<I>> worldNamePredicate = (worldName, world) -> worldName.equals(world.getWorldName());
    @Nonnull
    private final List<AccountWorld<I>> worlds;

    public Account(@Nonnull UUID uuid) {
        this(uuid, new ArrayList<>());
    }

    public Account(@Nonnull UUID uuid, @Nonnull List<AccountWorld<I>> worlds) {
        this.uuid = uuid;
        this.worlds = worlds;
    }

    public void clear() {
        worlds.clear();
    }

    @Nonnull
    public UUID getID() {
        return uuid;
    }

    @Nonnull
    public Optional<AccountWorld<I>> getWorld(@Nonnull String worldName) {
        return worlds.stream().filter(w -> worldNamePredicate.test(worldName, w)).findFirst();
    }

    @Nonnull
    public List<AccountWorld<I>> getWorlds() {
        return worlds;
    }

    public void setWorld(@Nonnull AccountWorld<I> world) {
        worlds.removeIf(w -> worldNamePredicate.test(world.getWorldName(), w));
        worlds.add(world);
    }

    public static class Serializer<I> implements MongoSerializable<Account<I>>, SQLStatementSerializable<Account<I>>, TypeSerializer<Account<I>> {

        private final Class<I> itemStackClass;
        private final ItemStackSerializer<I> itemStackSerializer;
        private final TypeToken<AccountWorld<I>> worldToken;

        public Serializer(@Nonnull TypeToken<AccountWorld<I>> worldToken, @Nonnull Class<I> itemStackClass, @Nonnull ItemStackSerializer<I> itemStackSerializer) {
            this.worldToken = worldToken;
            this.itemStackClass = itemStackClass;
            this.itemStackSerializer = itemStackSerializer;
        }

        @Override
        public Account<I> deserialize(@Nullable Document document) {
            if (document == null) {
                return null;
            }

            UUID uuid = UUID.fromString(document.getString(AccountData.ID));
            List<AccountWorld<I>> worlds = document.getList(AccountData.WORLDS, Document.class).stream().map(d -> new AccountWorld.Serializer<>(itemStackClass, itemStackSerializer).deserialize(d)).collect(Collectors.toList());
            return new Account<>(uuid, worlds);
        }

        @Override
        public Account<I> deserialize(Type type, ConfigurationNode node) throws SerializationException {
            if (!type.equals(new TypeToken<Account<I>>() {

            }.getType())) {
                return null;
            }

            UUID uuid = node.node(AccountData.ID).get(UUID.class);
            if (uuid == null) {
                throw new SerializationException("Account UUID can not be null.");
            }

            List<AccountWorld<I>> worlds = node.node(AccountData.WORLDS).getList(worldToken, new ArrayList<>());
            return new Account<>(uuid, worlds);
        }

        @Nonnull
        @Override
        public List<Account<I>> fromStatement(@Nonnull Statement statement) throws SQLException {
            statement.execute(Database.CREATE_TABLE);
            ResultSet resultSet = statement.executeQuery(Database.SELECT_TABLE);
            List<Account<I>> accounts = new ArrayList<>();
            while (resultSet.next()) {
                UUID uuid = UUID.fromString(resultSet.getString(Database.UUID));
                Account<I> account = new Account<>(uuid);
                Optional<Account<I>> accountOptional = accounts.stream().filter(a -> uuid.equals(a.getID())).findFirst();
                if (accountOptional.isPresent()) {
                    account = accountOptional.get();
                }
                else {
                    accounts.add(account);
                }

                AccountWorld<I> accountWorld = new AccountWorld<>(resultSet.getString(Database.WORLD));
                Optional<AccountWorld<I>> worldOptional = account.getWorld(resultSet.getString(Database.WORLD));
                if (worldOptional.isPresent()) {
                    accountWorld = worldOptional.get();
                }
                else {
                    account.setWorld(accountWorld);
                }

                int page = resultSet.getInt(Database.PAGE);
                int slot = resultSet.getInt(Database.SLOT);
                String itemString = resultSet.getString(Database.ITEM);
                try {
                    ConfigurationNode itemNode = GsonConfigurationLoader.builder().source(() -> new BufferedReader(new StringReader(itemString))).build().load();
                    I itemStack = itemStackSerializer.deserialize(itemStackClass, itemNode);
                    accountWorld.setSlot(page, slot, itemStack);
                }
                catch (ConfigurateException e) {
                    throw new SQLException(Messages.invalidItem(account.uuid, accountWorld.getWorldName(), page, slot, itemString), e);
                }
            }

            resultSet.close();
            return accounts;
        }

        @Override
        public void serialize(Type type, @Nullable Account<I> obj, ConfigurationNode node) throws SerializationException {
            if (!type.equals(new TypeToken<Account<I>>() {

            }.getType()) || obj == null) {
                return;
            }

            node.node(AccountData.ID).set(obj.uuid);
            node.node(AccountData.WORLDS).set(obj.worlds);
        }

        @Override
        public Document serialize(@Nonnull Account<I> obj) {
            Document document = new Document();
            document.put(AccountData.ID, obj.uuid);
            document.put(AccountData.WORLDS, obj.worlds.stream().map(d -> new AccountWorld.Serializer<>(itemStackClass, itemStackSerializer).serialize(d)).collect(Collectors.toList()));
            return document;
        }

        @Override
        public void toStatement(@Nonnull Statement statement, @Nonnull List<Account<I>> data) throws SQLException {
            for (Account<I> account : data) {
                for (AccountWorld<I> world : account.getWorlds()) {
                    List<I[]> pages = world.getPages();
                    for (int i = 0; i < pages.size(); i++) {
                        I[] items = pages.get(i);
                        for (int x = 0; x < 45; x++) {
                            try {
                                ConfigurationNode itemNode = BasicConfigurationNode.root();
                                itemStackSerializer.serialize(itemStackClass, items[i], itemNode);
                                StringWriter sw = new StringWriter();
                                GsonConfigurationLoader.builder().sink(() -> new BufferedWriter(sw)).build().save(itemNode);
                                statement.addBatch(Database.addItem(account.uuid, world.getWorldName(), i, x, sw.toString()));
                            }
                            catch (ConfigurateException e) {
                                throw new SQLException("Failed to save item for " + account.uuid + " in " + world.getWorldName() + " page " + i + " slot " + x, e);
                            }
                        }
                    }
                }
            }
        }
    }
}

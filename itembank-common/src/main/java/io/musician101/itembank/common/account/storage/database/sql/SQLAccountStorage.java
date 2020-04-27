package io.musician101.itembank.common.account.storage.database.sql;

import com.google.common.reflect.TypeToken;
import io.musician101.itembank.common.Reference.Database;
import io.musician101.itembank.common.Reference.Messages;
import io.musician101.itembank.common.account.Account;
import io.musician101.itembank.common.account.AccountWorld;
import io.musician101.itembank.common.account.storage.ItemStackParsing;
import io.musician101.itembank.common.account.storage.database.AccountDatabaseStorage;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.SimpleConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;

public abstract class SQLAccountStorage<I> extends AccountDatabaseStorage<I> {

    public SQLAccountStorage(@Nonnull Map<String, String> options, @Nonnull ItemStackParsing<I> itemStackParsing) {
        super(options, itemStackParsing);
    }

    @Override
    public @Nonnull List<String> load() {
        List<String> errors = new ArrayList<>();
        String invalidItem = null;
        try {
            Connection connection = openConnection();
            Statement statement = connection.createStatement();
            statement.executeQuery(Database.CREATE_TABLE);
            ResultSet resultSet = statement.executeQuery(Database.SELECT_TABLE);
            while (resultSet.next()) {
                UUID uuid = UUID.fromString(resultSet.getString(Database.UUID));
                Account<I> account = getAccount(uuid).orElse(new Account<>(uuid, resultSet.getString(Database.NAME)));
                setAccount(account);
                AccountWorld<I> accountWorld = account.getWorld(resultSet.getString(Database.WORLD)).orElse(new AccountWorld<>(resultSet.getString(Database.WORLD)));
                account.setWorld(accountWorld);
                int page = resultSet.getInt(Database.PAGE);
                int slot = resultSet.getInt(Database.SLOT);
                String itemString = resultSet.getString(Database.ITEM);
                invalidItem = Messages.invalidItem(account.getName(), accountWorld.getWorldName(), page, slot, itemString);
                ConfigurationNode itemNode = SimpleConfigurationNode.root();
                //noinspection UnstableApiUsage
                TypeSerializers.getDefaultSerializers().get(TypeToken.of(String.class)).serialize(TypeToken.of(String.class), itemString, itemNode);
                accountWorld.setSlot(page, slot, itemStackParsing.load(itemNode));
            }

            resultSet.close();
            statement.close();
            connection.close();
        }
        catch (SQLException e) {
            errors.add(Messages.SQL_EX);
        }
        catch (ObjectMappingException e) {
            errors.add(invalidItem);
        }

        return errors;
    }

    @Override
    public @Nonnull List<String> save() {
        List<String> errors = new ArrayList<>();
        List<String> queries = new ArrayList<>();
        getAccounts().forEach(account -> {
            String name = account.getName();
            UUID uuid = account.getID();
            queries.add(Database.deleteUser(uuid));
            account.getWorlds().forEach(world -> {
                String worldName = world.getWorldName();
                List<I[]> pages = world.getPages();
                IntStream.range(0, pages.size()).forEach(x -> {
                    I[] slots = pages.get(x);
                    IntStream.range(0, 45).forEach(y -> {
                        I itemStack = slots[y];
                        ConfigurationNode node = itemStackParsing.save(itemStack);
                        try {
                            //noinspection UnstableApiUsage
                            String itemString = TypeSerializers.getDefaultSerializers().get(TypeToken.of(String.class)).deserialize(TypeToken.of(String.class), node);
                            queries.add(Database.addItem(uuid, name, worldName, x, y, itemString));
                        }
                        catch (ObjectMappingException e) {
                            e.printStackTrace();
                        }
                    });
                });
            });
        });

        try {
            Connection connection = openConnection();
            Statement statement = connection.createStatement();
            for (String query : queries) {
                statement.addBatch(query);
            }

            statement.executeBatch();
            statement.close();
            connection.close();
        }
        catch (SQLException e) {
            errors.add(Messages.SQL_EX);
        }

        return errors;
    }

    @Nonnull
    protected abstract Connection openConnection() throws SQLException;
}

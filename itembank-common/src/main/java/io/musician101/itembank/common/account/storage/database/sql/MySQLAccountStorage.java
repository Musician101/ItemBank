package io.musician101.itembank.common.account.storage.database.sql;

import io.musician101.itembank.common.account.storage.ItemStackParsing;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import javax.annotation.Nonnull;

public class MySQLAccountStorage<I> extends SQLAccountStorage<I> {

    public MySQLAccountStorage(@Nonnull Map<String, String> options, @Nonnull ItemStackParsing<I> itemStackParsing) {
        super(options, itemStackParsing);
    }

    @Nonnull
    @Override
    protected Connection openConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://" + address + "/" + database, username, password);
    }
}

package io.musician101.itembank.common.account.storage.database.sql;

import io.musician101.itembank.common.account.storage.ItemStackParsing;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import javax.annotation.Nonnull;

public class SQLiteAccountStorage<I> extends SQLAccountStorage<I> {

    public SQLiteAccountStorage(@Nonnull ItemStackParsing<I> itemStackParsing) {
        super(Collections.emptyMap(), itemStackParsing);
    }

    @Nonnull
    @Override
    protected Connection openConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:storage.db");
    }
}

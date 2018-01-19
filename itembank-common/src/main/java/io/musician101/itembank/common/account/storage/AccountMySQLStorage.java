package io.musician101.itembank.common.account.storage;

import com.google.gson.Gson;
import io.musician101.musicianlibrary.java.MySQLHandler;
import javax.annotation.Nonnull;

public abstract class AccountMySQLStorage<I, P, W> extends AccountStorage<I, P, W> {

    @Nonnull
    private MySQLHandler mysql;

    protected AccountMySQLStorage(@Nonnull MySQLHandler mysql, @Nonnull Gson gson) {
        super(gson);
        this.mysql = mysql;
        load();
    }

    @Nonnull
    protected MySQLHandler getMySQL() {
        return mysql;
    }

    public void updateMySQL(@Nonnull MySQLHandler mysql) {
        this.mysql = mysql;
    }
}

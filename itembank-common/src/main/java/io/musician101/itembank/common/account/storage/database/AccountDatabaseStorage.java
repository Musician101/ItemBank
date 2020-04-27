package io.musician101.itembank.common.account.storage.database;

import io.musician101.itembank.common.Reference.Config;
import io.musician101.itembank.common.account.storage.AccountStorage;
import io.musician101.itembank.common.account.storage.ItemStackParsing;
import java.util.Map;
import javax.annotation.Nonnull;

public abstract class AccountDatabaseStorage<I> extends AccountStorage<I> {

    @Nonnull
    protected final String address;
    @Nonnull
    protected final String database;
    @Nonnull
    protected final String username;
    @Nonnull
    protected final String password;

    public AccountDatabaseStorage(@Nonnull Map<String, String> options, @Nonnull ItemStackParsing<I> itemStackParsing) {
        super(itemStackParsing);
        this.address = options.getOrDefault(Config.ADDRESS, Config.LOCAL_HOST);
        this.database = options.getOrDefault(Config.DATABASE, Config.MINECRAFT);
        this.username = options.getOrDefault(Config.USERNAME, Config.ROOT);
        this.password = options.getOrDefault(Config.PASSWORD, "");
    }
}

package io.musician101.itembank.common.account.storage;

import io.musician101.itembank.common.account.Account;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;

public abstract class AccountStorage<I> {

    @Nonnull
    private final List<Account<I>> accounts = new ArrayList<>();
    @Nonnull
    protected final ItemStackParsing<I> itemStackParsing;

    public AccountStorage(@Nonnull ItemStackParsing<I> itemStackParsing) {
        this.itemStackParsing = itemStackParsing;
    }

    public void clear() {
        accounts.clear();
    }

    @Nonnull
    public Optional<Account<I>> getAccount(UUID owner) {
        return accounts.stream().filter(account -> owner.equals(account.getID())).findFirst();
    }

    @Nonnull
    public List<Account<I>> getAccounts() {
        return accounts;
    }

    @Nonnull
    public abstract List<String> load();

    @Nonnull
    public abstract List<String> save();

    public void setAccount(@Nonnull Account<I> account) {
        accounts.removeIf(a -> a.getID().equals(account.getID()));
        accounts.add(account);
    }
}

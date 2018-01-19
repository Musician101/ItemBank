package io.musician101.itembank.common.account;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

//TODO create different versions for each module and create MySQL versions
public abstract class AbstractAccountStorage<I, P, W> {

    @Nonnull
    private final Map<UUID, Account<I>> accounts = new HashMap<>();
    @Nonnull
    private final File storageDir;

    public AbstractAccountStorage(@Nonnull File storageDir) {
        this.storageDir = storageDir;
        load();
    }

    @Nullable
    public Account<I> getAccount(UUID owner) {
        return accounts.get(owner);
    }

    @Nonnull
    public Map<UUID, Account<I>> getAccounts() {
        return accounts;
    }

    @Nonnull
    protected File getStorageDir() {
        return storageDir;
    }

    public abstract void load();

    public abstract void openInv(@Nonnull P viewer, @Nonnull UUID uuid, @Nonnull String name, @Nonnull W world, int page);

    public void resetAccount(@Nonnull UUID uuid) {
        Account<I> account = getAccount(uuid);
        if (account != null) {
            setAccount(new Account<>(uuid, account.getName()));
        }
    }

    public void resetAll() {
        accounts.replaceAll((key, value) -> new Account<>(key, value.getName()));
    }

    public abstract void save();

    public void setAccount(@Nonnull Account<I> account) {
        accounts.put(account.getID(), account);
    }
}

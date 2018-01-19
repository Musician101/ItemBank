package io.musician101.itembank.common.account.storage;

import com.google.gson.Gson;
import io.musician101.itembank.common.account.Account;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

//TODO use optional for getting account stuffs
public abstract class AccountStorage<I, P, W> {

    @Nonnull
    private final Map<UUID, Account<I>> accounts = new HashMap<>();
    @Nonnull
    private final Gson gson;

    protected AccountStorage(@Nonnull Gson gson) {
        this.gson = gson;
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
    protected Gson getGson() {
        return gson;
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

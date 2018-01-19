package io.musician101.itembank.common;

import io.musician101.itembank.common.account.storage.AccountStorage;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ItemBank<I, L, P, W> {

    //TODO turn into Optional
    @Deprecated
    @Nullable
    AccountStorage<I, P, W> getAccountStorage();

    @Nonnull
    String getId();

    @Nonnull
    L getLogger();

    @Nonnull
    String getName();

    void reload();

    default void save() {
        if (getAccountStorage() != null) {
            getAccountStorage().save();
        }
    }
}

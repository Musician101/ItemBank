package io.musician101.itembank.common;

import io.musician101.itembank.common.account.storage.AccountStorage;
import javax.annotation.Nonnull;

public interface ItemBank<I> {

    @Nonnull
    AccountStorage<I> getAccountStorage();

    @Nonnull
    String getName();

    void reload();
}

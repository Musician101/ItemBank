package io.musician101.itembank.common;

import io.musician101.itembank.common.account.storage.AccountStorage;
import java.util.Optional;
import javax.annotation.Nonnull;

public interface ItemBank<I, L, P, W> {

    @Nonnull
    Optional<AccountStorage<I, P, W>> getAccountStorage();

    @Nonnull
    String getId();

    @Nonnull
    L getLogger();

    @Nonnull
    String getName();

    void reload();

    default void save() {
        getAccountStorage().ifPresent(AccountStorage::save);
    }
}

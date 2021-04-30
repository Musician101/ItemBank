package io.musician101.itembank.common;

import io.musician101.itembank.common.account.Account;
import io.musician101.musicianlibrary.java.storage.DataStorage;
import java.io.IOException;
import javax.annotation.Nonnull;

public interface ItemBank<I> {

    @Nonnull
    DataStorage<?, Account<I>> getAccountStorage();

    void reload() throws IOException;
}

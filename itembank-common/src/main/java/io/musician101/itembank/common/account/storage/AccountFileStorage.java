package io.musician101.itembank.common.account.storage;

import com.google.gson.Gson;
import java.io.File;
import javax.annotation.Nonnull;

public abstract class AccountFileStorage<I, P, W> extends AccountStorage<I, P, W> {

    @Nonnull
    private final File storageDir;

    protected AccountFileStorage(@Nonnull File storageDir, @Nonnull Gson gson) {
        super(gson);
        this.storageDir = storageDir;
        load();
    }

    @Nonnull
    protected File getStorageDir() {
        return storageDir;
    }
}

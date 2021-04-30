package io.musician101.itembank.common.account.storage;

import io.leangen.geantyref.TypeToken;
import io.musician101.itembank.common.account.Account;
import io.musician101.musicianlibrary.java.configurate.ConfigurateLoader;
import io.musician101.musicianlibrary.java.storage.file.DataFileStorage;
import java.io.File;
import java.nio.file.Path;
import javax.annotation.Nonnull;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

public class AccountFileStorage<I> extends DataFileStorage<Account<I>> {

    public AccountFileStorage(@Nonnull File storageDir, @Nonnull ConfigurateLoader loader, @Nonnull String extension, @Nonnull TypeToken<Account<I>> accountToken, @Nonnull TypeSerializerCollection tsc) {
        super(storageDir, loader, extension, accountToken, tsc);
    }

    @Nonnull
    @Override
    protected Path getPath(Account<I> account) {
        return new File(storageDir, account.getID() + extension).toPath();
    }
}

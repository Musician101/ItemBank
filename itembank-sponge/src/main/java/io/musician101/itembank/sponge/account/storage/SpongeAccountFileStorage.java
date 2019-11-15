package io.musician101.itembank.sponge.account.storage;

import com.google.gson.Gson;
import io.musician101.itembank.common.Reference.Messages;
import io.musician101.itembank.common.Reference.PlayerData;
import io.musician101.itembank.common.account.Account;
import io.musician101.itembank.common.account.storage.AccountFileStorage;
import io.musician101.itembank.sponge.SpongeItemBank;
import io.musician101.itembank.sponge.json.account.AccountSerializer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.World;

import static io.musician101.itembank.sponge.SpongeItemBank.GSON;

public class SpongeAccountFileStorage extends AccountFileStorage<ItemStack, Player, World> implements SpongeAccountPageOpener {

    public SpongeAccountFileStorage(File storageDir, Gson gson) {
        super(storageDir, gson);
    }

    @Override
    public void load() {
        getStorageDir().mkdirs();
        File[] files = getStorageDir().listFiles();
        if (files == null) {
            return;
        }

        Arrays.stream(files).filter(file -> file.getName().endsWith(PlayerData.FILE_EXTENSION)).map(file -> {
            try {
                return getGson().<Account<ItemStack>>fromJson(new FileReader(file), AccountSerializer.TYPE);
            }
            catch (FileNotFoundException e) {
                SpongeItemBank.instance().getLogger().error(Messages.fileLoadFail(file));
                return null;
            }
        }).filter(Objects::nonNull).forEach(this::setAccount);
    }

    @Override
    public void openInv(@Nonnull Player viewer, @Nonnull UUID uuid, @Nonnull String name, @Nonnull World world, int page) {
        SpongeAccountPageOpener.super.openInv(viewer, uuid, name, world, page);
    }

    @Override
    public void save() {
        getStorageDir().mkdirs();
        getAccounts().values().forEach(account -> {
            File file = new File(getStorageDir(), account.getID().toString() + PlayerData.FILE_EXTENSION);
            try {
                if (!file.exists()) {
                    file.createNewFile();
                }

                OutputStream os = new FileOutputStream(file);
                os.write(GSON.toJson(account).getBytes());
                os.close();
            }
            catch (IOException e) {
                SpongeItemBank.instance().getLogger().error(Messages.fileLoadFail(file));
            }
        });
    }
}

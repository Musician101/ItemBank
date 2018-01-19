package io.musician101.itembank.spigot.account.storage;

import com.google.gson.Gson;
import io.musician101.itembank.common.Reference.Messages;
import io.musician101.itembank.common.Reference.PlayerData;
import io.musician101.itembank.common.account.Account;
import io.musician101.itembank.common.account.storage.AccountFileStorage;
import io.musician101.itembank.spigot.SpigotItemBank;
import io.musician101.itembank.spigot.json.account.AccountSerializer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SpigotAccountFileStorage extends AccountFileStorage<ItemStack, Player, World> implements SpigotAccountPageOpener {

    public SpigotAccountFileStorage(File storageDir, Gson gson) {
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
                SpigotItemBank.instance().getLogger().warning(Messages.fileLoadFail(file));
                return null;
            }
        }).filter(Objects::nonNull).forEach(this::setAccount);
    }

    @Override
    public void openInv(@Nonnull Player viewer, @Nonnull UUID uuid, @Nonnull String name, @Nonnull World world, int page) {
        SpigotAccountPageOpener.super.openInv(viewer, uuid, name, world, page);
    }

    @Override
    public void save() {
        SpigotItemBank plugin = (SpigotItemBank) SpigotItemBank.instance();
        Logger logger = plugin.getLogger();
        getStorageDir().mkdirs();
        getAccounts().values().forEach(account -> {
            File file = new File(getStorageDir(), account.getID().toString() + PlayerData.FILE_EXTENSION);
            try {
                if (!file.exists()) {
                    file.createNewFile();
                }

                OutputStream os = new FileOutputStream(file);
                os.write(getGson().toJson(account).getBytes());
                os.close();
            }
            catch (IOException e) {
                logger.warning(Messages.fileLoadFail(file));
            }
        });
    }
}

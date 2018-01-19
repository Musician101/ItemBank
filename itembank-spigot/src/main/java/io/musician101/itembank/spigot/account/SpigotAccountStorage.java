package io.musician101.itembank.spigot.account;

import com.google.common.collect.Multimap;
import com.google.gson.JsonParseException;
import io.musician101.itembank.common.Reference.Messages;
import io.musician101.itembank.common.Reference.MySQL;
import io.musician101.itembank.common.Reference.PlayerData;
import io.musician101.itembank.common.account.AbstractAccountStorage;
import io.musician101.itembank.common.account.Account;
import io.musician101.itembank.common.account.AccountPage;
import io.musician101.itembank.common.account.AccountSlot;
import io.musician101.itembank.common.account.AccountWorld;
import io.musician101.itembank.spigot.SpigotItemBank;
import io.musician101.itembank.spigot.json.account.AccountSerializer;
import io.musician101.musicianlibrary.java.MySQLHandler;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static io.musician101.itembank.spigot.SpigotItemBank.GSON;

public class SpigotAccountStorage extends AbstractAccountStorage<ItemStack, Player, World> {

    public SpigotAccountStorage() {
        super(new File(SpigotItemBank.instance().getDataFolder(), PlayerData.DIRECTORY));
    }

    @Override
    public void load() {
        SpigotItemBank plugin = SpigotItemBank.instance();
        Logger logger = plugin.getLogger();
        MySQLHandler mysql = plugin.getMySQLHandler();
        if (plugin.getPluginConfig().useMySQL() && mysql != null) {
            try {
                mysql.querySQL(MySQL.CREATE_TABLE);
                List<String> uuidSkip = new ArrayList<>();
                List<String> itemStackSkip = new ArrayList<>();
                List<String> worldSkip = new ArrayList<>();
                ResultSet pageSet = mysql.querySQL(MySQL.SELECT_TABLE);
                while (pageSet.next()) {
                    String uuidString = pageSet.getString(MySQL.UUID);
                    if (uuidSkip.contains(uuidString)) {
                        continue;
                    }

                    String name = pageSet.getString(MySQL.NAME);
                    UUID uuid;
                    try {
                        uuid = UUID.fromString(uuidString);
                    }
                    catch (IllegalArgumentException e) {
                        uuidSkip.add(uuidString);
                        logger.warning(Messages.badUUID(name, uuidString));
                        continue;
                    }

                    Account<ItemStack> account = getAccount(uuid);
                    if (account == null) {
                        account = new Account<>(uuid, name);
                        setAccount(account);
                    }

                    String worldName = pageSet.getString(MySQL.WORLD);
                    if (worldSkip.contains(worldName)) {
                        logger.warning(Messages.worldDNE(name, worldName));
                        continue;
                    }

                    if (Bukkit.getWorld(worldName) == null) {
                        worldSkip.add(worldName);
                        continue;
                    }

                    AccountWorld<ItemStack> world = account.getWorld(worldName);
                    if (world == null) {
                        world = new AccountWorld<>(worldName);
                        account.setWorld(world);
                    }

                    int page = pageSet.getInt(MySQL.SLOT);
                    if (page < 1) {
                        logger.warning(Messages.invalidPage(name, worldName, page));
                        continue;
                    }

                    AccountPage<ItemStack> accountPage = world.getPage(page);
                    if (accountPage == null) {
                        accountPage = new AccountPage<>(page);
                        world.setPage(accountPage);
                    }

                    int slot = pageSet.getInt(MySQL.SLOT);
                    if (slot < 0 || slot > 53) {
                        logger.warning(Messages.invalidSlot(name, worldName, slot));
                        continue;
                    }

                    String itemStackString = pageSet.getString(MySQL.ITEM);
                    if (itemStackSkip.contains(itemStackString)) {
                        continue;
                    }

                    ItemStack itemStack;
                    try {
                        itemStack = GSON.fromJson(itemStackString, ItemStack.class);
                    }
                    catch (JsonParseException e) {
                        itemStackSkip.add(itemStackString);
                        logger.warning(Messages.invalidItem(name, worldName, page, slot, itemStackString));
                        continue;
                    }

                    AccountSlot<ItemStack> accountSlot = accountPage.getSlot(slot);
                    if (accountSlot == null) {
                        accountSlot = new AccountSlot<>(slot, itemStack);
                        accountPage.setSlot(accountSlot);
                    }
                }
            }
            catch (ClassNotFoundException | SQLException e) {
                logger.warning(Messages.SQL_EX);
            }
        }
        else {
            getStorageDir().mkdirs();
            File[] files = getStorageDir().listFiles();
            if (files == null) {
                return;
            }

            Arrays.stream(files).filter(file -> file.getName().endsWith(PlayerData.FILE_EXTENSION)).map(file -> {
                try {
                    return GSON.<Account<ItemStack>>fromJson(new FileReader(file), AccountSerializer.TYPE);
                }
                catch (FileNotFoundException e) {
                    logger.warning(Messages.fileLoadFail(file));
                    return null;
                }
            }).filter(Objects::nonNull).forEach(this::setAccount);
        }
    }

    @Override
    public void openInv(@Nonnull Player viewer, @Nonnull UUID uuid, @Nonnull String name, @Nonnull World world, int page) {
        Account<ItemStack> account = getAccount(uuid);
        if (account == null) {
            account = new Account<>(uuid, name);
            setAccount(account);
        }

        AccountWorld<ItemStack> accountWorld = account.getWorld(world.getName());
        if (accountWorld == null) {
            accountWorld = new AccountWorld<>(world.getName());
            account.setWorld(accountWorld);
        }

        AccountPage<ItemStack> accountPage = accountWorld.getPage(page);
        if (accountPage == null) {
            accountPage = new AccountPage<>(page);
            accountWorld.setPage(accountPage);
        }

        new SpigotInventoryHandler(accountPage, viewer, uuid, name, world);
    }

    @Override
    public void save() {
        SpigotItemBank plugin = SpigotItemBank.instance();
        Logger logger = plugin.getLogger();
        MySQLHandler mysql = plugin.getMySQLHandler();
        if (plugin.getPluginConfig().useMySQL() && mysql != null) {
            List<String> queries = new ArrayList<>();
            Multimap<UUID, String> changedNames = Account.getChangedNames();
            if (!changedNames.isEmpty()) {
                changedNames.forEach((uuid, name) -> queries.add(MySQL.deleteUser(uuid, name)));
            }

            getAccounts().values().forEach(account -> {
                String name = account.getName();
                UUID uuid = account.getID();
                queries.add(MySQL.deleteUser(uuid));
                account.getWorlds().values().forEach(world -> {
                    String worldName = world.getWorldName();
                    world.getPages().values().forEach(page -> {
                        int pg = page.getPage();
                        for (int i = 0; i < 54; i++) {
                            AccountSlot<ItemStack> slot = page.getSlot(i);
                            if (slot == null) {
                                queries.add(MySQL.deleteItem(uuid, name, worldName, pg, i));
                            }
                            else {
                                int s = slot.getSlot();
                                String item = GSON.toJson(slot.getItemStack());
                                queries.add(MySQL.addItem(uuid, name, worldName, pg, s, item));
                            }
                        }
                    });
                });
            });

            try {
                mysql.executeBatch(queries);
            }
            catch (ClassNotFoundException | SQLException e) {
                logger.warning(Messages.SQL_EX);
            }
        }
        else {
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
                    logger.warning(Messages.fileLoadFail(file));
                }
            });
        }
    }
}

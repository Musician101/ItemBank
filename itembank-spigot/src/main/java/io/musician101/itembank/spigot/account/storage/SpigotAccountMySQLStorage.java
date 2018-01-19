package io.musician101.itembank.spigot.account.storage;

import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import io.musician101.itembank.common.Reference.Messages;
import io.musician101.itembank.common.Reference.MySQL;
import io.musician101.itembank.common.account.Account;
import io.musician101.itembank.common.account.AccountPage;
import io.musician101.itembank.common.account.AccountSlot;
import io.musician101.itembank.common.account.AccountWorld;
import io.musician101.itembank.common.account.storage.AccountMySQLStorage;
import io.musician101.itembank.spigot.SpigotItemBank;
import io.musician101.musicianlibrary.java.MySQLHandler;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SpigotAccountMySQLStorage extends AccountMySQLStorage<ItemStack, Player, World> implements SpigotAccountPageOpener {

    public SpigotAccountMySQLStorage(MySQLHandler mysql, Gson gson) {
        super(mysql, gson);
    }

    @Override
    public void load() {
        SpigotItemBank plugin = (SpigotItemBank) SpigotItemBank.instance();
        Logger logger = plugin.getLogger();
        MySQLHandler mysql = getMySQL();
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
                    itemStack = getGson().fromJson(itemStackString, ItemStack.class);
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

    @Override
    public void openInv(@Nonnull Player viewer, @Nonnull UUID uuid, @Nonnull String name, @Nonnull World world, int page) {
        SpigotAccountPageOpener.super.openInv(viewer, uuid, name, world, page);
    }

    @Override
    public void save() {
        SpigotItemBank plugin = (SpigotItemBank) SpigotItemBank.instance();
        MySQLHandler mysql = getMySQL();
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
                            String item = getGson().toJson(slot.getItemStack());
                            queries.add(MySQL.addItem(uuid, name, worldName, pg, s, item));
                        }
                    }
                });
            });
        });

        try {
            mysql.executeBatch(queries);
            mysql.closeConnection();
        }
        catch (ClassNotFoundException | SQLException e) {
            plugin.getLogger().warning(Messages.SQL_EX);
        }
    }
}

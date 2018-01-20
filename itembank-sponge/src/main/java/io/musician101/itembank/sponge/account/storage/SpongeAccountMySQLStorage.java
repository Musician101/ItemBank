package io.musician101.itembank.sponge.account.storage;

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
import io.musician101.itembank.sponge.SpongeItemBank;
import io.musician101.musicianlibrary.java.MySQLHandler;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.World;

import static io.musician101.itembank.sponge.SpongeItemBank.GSON;

public class SpongeAccountMySQLStorage extends AccountMySQLStorage<ItemStack, Player, World> implements SpongeAccountPageOpener {

    public SpongeAccountMySQLStorage(MySQLHandler mysql, Gson gson) {
        super(mysql, gson);
    }

    @Override
    public void load() {
        SpongeItemBank.instance().map(SpongeItemBank.class::cast).ifPresent(plugin -> {
            Logger logger = plugin.getLogger();
            try {
                MySQLHandler mysql = getMySQL();
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
                        logger.error(Messages.badUUID(name, uuidString));
                        continue;
                    }

                    Account<ItemStack> account = getAccount(uuid).orElse(new Account<>(uuid, name));
                    setAccount(account);
                    String worldName = pageSet.getString(MySQL.WORLD);
                    if (worldSkip.contains(worldName)) {
                        logger.error(Messages.worldDNE(name, worldName));
                        continue;
                    }

                    if (Sponge.getServer().getWorld(worldName).isPresent()) {
                        worldSkip.add(worldName);
                        continue;
                    }

                    AccountWorld<ItemStack> world = account.getWorld(worldName).orElse(new AccountWorld<>(worldName));
                    account.setWorld(world);
                    int page = pageSet.getInt(MySQL.SLOT);
                    if (page < 1) {
                        logger.error(Messages.invalidPage(name, worldName, page));
                        continue;
                    }

                    AccountPage<ItemStack> accountPage = world.getPage(page).orElse(new AccountPage<>(page));
                    world.setPage(accountPage);
                    int slot = pageSet.getInt(MySQL.SLOT);
                    if (slot < 0 || slot > 53) {
                        logger.error(Messages.invalidSlot(name, worldName, slot));
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
                        logger.error(Messages.invalidItem(name, worldName, page, slot, itemStackString));
                        continue;
                    }

                    AccountSlot<ItemStack> accountSlot = accountPage.getSlot(slot).orElse(new AccountSlot<>(slot, itemStack));
                    accountPage.setSlot(accountSlot);
                }
            }
            catch (ClassNotFoundException | SQLException e) {
                logger.error(Messages.SQL_EX);
            }
        });
    }

    @Override
    public void openInv(@Nonnull Player viewer, @Nonnull UUID uuid, @Nonnull String name, @Nonnull World world, int page) {
        SpongeAccountPageOpener.super.openInv(viewer, uuid, name, world, page);
    }

    @Override
    public void save() {
        SpongeItemBank.instance().map(SpongeItemBank.class::cast).ifPresent(plugin -> {
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
                            Optional<AccountSlot<ItemStack>> slot = page.getSlot(i);
                            if (!slot.isPresent()) {
                                queries.add(MySQL.deleteItem(uuid, name, worldName, pg, i));
                            }
                            else {
                                int s = slot.get().getSlot();
                                String item = GSON.toJson(slot.get().getItemStack());
                                queries.add(MySQL.addItem(uuid, name, worldName, pg, s, item));
                            }
                        }
                    });
                });
            });

            try {
                getMySQL().executeBatch(queries);
            }
            catch (ClassNotFoundException | SQLException e) {
                plugin.getLogger().error(Messages.SQL_EX);
            }
        });
    }
}

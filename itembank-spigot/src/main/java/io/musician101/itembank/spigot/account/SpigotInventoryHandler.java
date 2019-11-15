package io.musician101.itembank.spigot.account;

import io.musician101.itembank.common.Reference.Messages;
import io.musician101.itembank.common.Reference.Permissions;
import io.musician101.itembank.common.account.AbstractInventoryHandler;
import io.musician101.itembank.common.account.AccountPage;
import io.musician101.itembank.common.account.AccountSlot;
import io.musician101.itembank.spigot.SpigotItemBank;
import io.musician101.itembank.spigot.config.SpigotConfig;
import java.util.Arrays;
import java.util.stream.IntStream;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class SpigotInventoryHandler extends AbstractInventoryHandler<Inventory, Player, ItemStack, String> implements Listener {

    public SpigotInventoryHandler(AccountPage<ItemStack> page, Player viewer, String owner, World world) {
        super(parseInventory(page, viewer, owner, world), parseInventoryName(page, owner, world), page, viewer);
        Bukkit.getPluginManager().registerEvents(this, SpigotItemBank.instance());
        viewer.openInventory(inventory);
    }

    private static String parseInventoryName(AccountPage<ItemStack> page, String owner, World world) {
        return owner + "'s Account for " + world.getName() + " - Page " + page.getPage();
    }

    private static Inventory parseInventory(AccountPage<ItemStack> page, Player viewer, String owner, World world) {
        Inventory inventory = Bukkit.createInventory(viewer, 54, parseInventoryName(page, owner, world));
        page.getSlots().values().forEach(slot -> inventory.setItem(slot.getSlot(), slot.getItemStack()));
        return inventory;
    }

    @EventHandler
    public void close(InventoryCloseEvent event) {
        InventoryView view = event.getView();
        if (!(view.getPlayer() instanceof Player)) {
            return;
        }

        Player player = (Player) view.getPlayer();
        if (!player.getUniqueId().equals(viewer.getUniqueId()) && player.hasPermission(Permissions.ADMIN)) {
            return;
        }

        if (!view.getTitle().equals(title)) {
            return;
        }

        SpigotConfig config = SpigotItemBank.instance().getPluginConfig();
        Inventory inventory = view.getTopInventory();
        IntStream.range(0, inventory.getSize()).filter(x -> inventory.getItem(x) != null).forEach(x -> {
            itemAmount = 0;
            ItemStack itemStack = inventory.getItem(x);
            if (itemStack == null) {
                return;
            }

            Arrays.stream(inventory.getContents()).filter(is -> is != null && itemStack.getType() == is.getType()).forEach(is -> itemAmount += itemStack.getAmount());
            int maxAmount = config.getMaxAmount(itemStack.getType());
            if (config.isWhitelist()) {
                if (maxAmount == 0) {
                    player.getWorld().dropItem(player.getLocation(), itemStack);
                    inventory.setItem(x, null);
                    hasIllegalItems = true;
                }
                else if (maxAmount < itemAmount) {
                    int amount = itemAmount;
                    while (maxAmount < amount) {
                        int maxStackSize = itemStack.getType().getMaxStackSize();
                        if (maxStackSize < amount) {
                            player.getWorld().dropItem(player.getLocation(), itemStack);
                            inventory.setItem(x, null);
                            amount -= maxStackSize;
                        }
                        else {
                            ItemStack removeItem = itemStack.clone();
                            removeItem.setAmount(amount - maxAmount);
                            if (inventory.getItem(x) == null) {
                                IntStream.range(0, inventory.getSize()).forEach(y -> {
                                    ItemStack yStack = inventory.getItem(y);
                                    if (yStack != null) {
                                        ItemStack is = inventory.getItem(y);
                                        is.setAmount(itemStack.getAmount() - removeItem.getAmount());
                                        inventory.setItem(y, is);
                                    }
                                });
                            }
                            else {
                                inventory.getItem(x).setAmount(itemStack.getAmount() - removeItem.getAmount());
                            }

                            player.getWorld().dropItem(player.getLocation(), removeItem);
                            amount -= removeItem.getAmount();
                        }
                    }

                    hasIllegalAmount = true;
                }
            }
            else if (maxAmount == 0 && !config.isWhitelist()) {
                player.getWorld().dropItem(player.getLocation(), itemStack);
                inventory.setItem(x, null);
                hasIllegalItems = true;
            }
        });

        if (hasIllegalItems) {
            player.sendMessage(ChatColor.RED + Messages.ACCOUNT_ILLEGAL_ITEM);
        }

        if (hasIllegalAmount) {
            player.sendMessage(ChatColor.RED + Messages.ACCOUNT_ILLEGAL_AMOUNT);
        }

        IntStream.range(0, inventory.getSize()).forEach(slot -> {
            ItemStack itemStack = inventory.getItem(slot);
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                page.clearSlot(slot);
            }
            else {
                page.setSlot(new AccountSlot<>(slot, itemStack));
            }
        });

        HandlerList.unregisterAll(this);
    }
}

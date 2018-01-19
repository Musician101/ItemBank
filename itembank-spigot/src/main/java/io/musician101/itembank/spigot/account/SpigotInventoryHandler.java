package io.musician101.itembank.spigot.account;

import io.musician101.itembank.common.Reference.Messages;
import io.musician101.itembank.common.Reference.Permissions;
import io.musician101.itembank.common.account.AbstractInventoryHandler;
import io.musician101.itembank.common.account.AccountPage;
import io.musician101.itembank.common.account.AccountSlot;
import io.musician101.itembank.spigot.SpigotItemBank;
import io.musician101.itembank.spigot.config.SpigotConfig;
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
import org.bukkit.inventory.ItemStack;

public class SpigotInventoryHandler extends AbstractInventoryHandler<Inventory, Player, ItemStack> implements Listener {

    public SpigotInventoryHandler(AccountPage<ItemStack> page, Player viewer, String ownerName, World world) {
        super(parseInventory(page, viewer, ownerName, world), page, viewer);
        Bukkit.getPluginManager().registerEvents(this, (SpigotItemBank) SpigotItemBank.instance());
        viewer.openInventory(inventory);
    }

    private static Inventory parseInventory(AccountPage<ItemStack> page, Player viewer, String ownerName, World world) {
        Inventory inventory = Bukkit.createInventory(viewer, 54, ownerName + "'s Account for " + world.getName() + " - Page " + page.getPage());
        page.getSlots().values().forEach(slot -> inventory.setItem(slot.getSlot(), slot.getItemStack()));
        return inventory;
    }

    @EventHandler
    public void close(InventoryCloseEvent event) {
        Inventory inv = event.getInventory();
        if (!(inv.getHolder() instanceof Player)) {
            return;
        }

        if (!((Player) inv.getHolder()).getUniqueId().equals(viewer.getUniqueId())) {
            return;
        }

        Player player = (Player) event.getPlayer();
        if (!player.getUniqueId().equals(viewer.getUniqueId()) && player.hasPermission(Permissions.ADMIN)) {
            return;
        }

        if (!inv.getName().equals(inventory.getName())) {
            return;
        }

        boolean hasIllegalItems = false;
        boolean hasIllegalAmount = false;
        SpigotConfig config = ((SpigotItemBank) SpigotItemBank.instance()).getPluginConfig();
        for (int x = 0; x < inv.getSize(); x++) {
            ItemStack itemStack = inv.getItem(x);
            if (itemStack != null) {
                int itemAmount = 0;
                for (ItemStack is : inv.getContents()) {
                    if (is != null && itemStack.getType() == is.getType() && itemStack.getDurability() == is.getDurability()) {
                        itemAmount += itemStack.getAmount();
                    }
                }

                ItemStack configItem = config.getItem(itemStack);
                if (configItem != null && config.isWhitelist()) {
                    int maxAmount = configItem.getAmount();
                    if (maxAmount == 0) {
                        player.getWorld().dropItem(player.getLocation(), itemStack);
                        inv.setItem(x, null);
                        hasIllegalItems = true;
                    }
                    else if (maxAmount < itemAmount) {
                        int amount = itemAmount;
                        while (maxAmount < amount) {
                            int maxStackSize = itemStack.getType().getMaxStackSize();
                            if (maxStackSize < amount) {
                                player.getWorld().dropItem(player.getLocation(), itemStack);
                                inv.setItem(x, null);
                                amount -= maxStackSize;
                            }
                            else {
                                ItemStack removeItem = itemStack.clone();
                                removeItem.setAmount(amount - maxAmount);
                                if (inv.getItem(x) == null) {
                                    int slot = 0;
                                    for (int y = 0; y < inv.getSize(); y++) {
                                        ItemStack yStack = inv.getItem(y);
                                        if (yStack != null && yStack.getDurability() == itemStack.getDurability()) {
                                            slot = y;
                                        }
                                    }

                                    ItemStack is = inv.getItem(slot);
                                    is.setAmount(itemStack.getAmount() - removeItem.getAmount());
                                    inv.setItem(slot, is);
                                }
                                else {
                                    inv.getItem(x).setAmount(itemStack.getAmount() - removeItem.getAmount());
                                }

                                player.getWorld().dropItem(player.getLocation(), removeItem);
                                amount -= removeItem.getAmount();
                            }
                        }

                        hasIllegalAmount = true;
                    }
                }
                else if (configItem == null && config.isWhitelist()) {
                    player.getWorld().dropItem(player.getLocation(), itemStack);
                    inv.setItem(x, null);
                    hasIllegalItems = true;
                }
            }
        }

        if (hasIllegalItems) {
            player.sendMessage(ChatColor.RED + Messages.ACCOUNT_ILLEGAL_ITEM);
        }

        if (hasIllegalAmount) {
            player.sendMessage(ChatColor.RED + Messages.ACCOUNT_ILLEGAL_AMOUNT);
        }

        for (int slot = 0; slot < inv.getSize(); slot++) {
            ItemStack itemStack = inv.getItem(slot);
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                page.clearSlot(slot);
            }
            else {
                page.setSlot(new AccountSlot<>(slot, itemStack));
            }
        }

        HandlerList.unregisterAll(this);
    }
}

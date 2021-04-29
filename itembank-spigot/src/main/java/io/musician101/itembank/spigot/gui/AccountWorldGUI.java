package io.musician101.itembank.spigot.gui;

import com.google.common.collect.ImmutableMap;
import io.musician101.itembank.common.Reference.GUIText;
import io.musician101.itembank.common.Reference.Messages;
import io.musician101.itembank.common.Reference.Permissions;
import io.musician101.itembank.common.account.AccountWorld;
import io.musician101.itembank.spigot.SpigotConfig;
import io.musician101.itembank.spigot.SpigotItemBank;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class AccountWorldGUI extends ItemBankChestGUI {

    private final AccountWorld<ItemStack> world;
    private boolean hasBlacklistedItems = false;
    private boolean hasIllegalAmount = false;
    private int page = 1;

    @SuppressWarnings("ConstantConditions")
    public AccountWorldGUI(@Nonnull AccountWorld<ItemStack> world, @Nonnull Player player) {
        super(player, String.format(GUIText.WORLD_PAGE, world.getWorldName(), 1));
        this.world = world;
        updateSlots();
        setButton(49, BACK_ICON, ImmutableMap.of(ClickType.LEFT, p -> {
            if (p.hasPermission(Permissions.PLAYER)) {
                new AccountsGUI(p);
                return;
            }

            p.closeInventory();
        }));
        extraCloseHandler = event -> {
            Player p = (Player) event.getPlayer();
            SpigotConfig config = SpigotItemBank.instance().getPluginConfig();
            Inventory inventory = event.getView().getTopInventory();
            Map<Material, Integer> amounts = new HashMap<>();
            IntStream.range(0, 45).forEach(x -> {
                ItemStack itemStack = inventory.getItem(x);
                if (itemStack == null) {
                    return;
                }

                Material type = itemStack.getType();
                if (config.isBlacklisted(type) || !config.isWhitelisted(type)) {
                    hasBlacklistedItems = true;
                    p.getWorld().dropItem(p.getLocation(), itemStack.clone());
                    inventory.setItem(x, null);
                    return;
                }

                amounts.compute(type, (t, i) -> (i == null ? 0 : i) + itemStack.getAmount());
            });

            amounts.forEach((type, amount) -> {
                int maxAmount = config.getMaxAmount(type);
                if (maxAmount > amount) {
                    return;
                }

                hasIllegalAmount = true;
                while (maxAmount < amount) {
                    int slot = inventory.first(type);
                    ItemStack itemStack = inventory.getItem(slot);
                    int maxStackSize = itemStack.getMaxStackSize();
                    if (maxStackSize < amount) {
                        p.getWorld().dropItem(p.getLocation(), itemStack.clone());
                        inventory.setItem(slot, null);
                        amount -= maxStackSize;
                    }
                    else {
                        ItemStack removed = itemStack.clone();
                        removed.setAmount(amount - maxAmount);
                        ItemStack stack = inventory.getItem(slot);
                        if (stack == null) {
                            IntStream.range(0, 45).forEach(y -> {
                                ItemStack is = inventory.getItem(y);
                                if (is != null) {
                                    is.setAmount(itemStack.getAmount() - removed.getAmount());
                                    inventory.setItem(y, is);
                                }
                            });
                        }
                        else {
                            stack.setAmount(itemStack.getAmount() - removed.getAmount());
                        }

                        p.getWorld().dropItem(p.getLocation(), removed);
                        amount -= removed.getAmount();
                    }
                }
            });

            if (hasBlacklistedItems) {
                p.sendMessage(ChatColor.RED + Messages.BLACKLISTED_ITEM);
            }

            if (hasIllegalAmount) {
                p.sendMessage(ChatColor.RED + Messages.ACCOUNT_ILLEGAL_AMOUNT);
            }

            IntStream.range(0, 45).forEach(slot -> world.setSlot(page, slot, inventory.getItem(slot)));
        };
    }

    private void updateSlots() {
        if (page > world.getPages().size()) {
            world.setPage(this.page, new ItemStack[45]);
        }

        ItemStack[] page = world.getPage(this.page);
        IntStream.range(0, 45).forEach(i -> addItem(i, page[i]));
        if (this.page == 1) {
            removeButton(45);
        }
        else {
            setButton(45, NEXT_PAGE, ImmutableMap.of(ClickType.LEFT, p -> {
                this.page--;
                updateSlots();
            }));
        }

        int maxPage = Double.valueOf(Math.ceil(SpigotItemBank.instance().getPluginConfig().getPageLimit() / 45d)).intValue();
        if (!player.hasPermission(Permissions.PAGE) && this.page < maxPage) {
            removeButton(53);
        }
        else {
            setButton(53, PREVIOUS_PAGE, ImmutableMap.of(ClickType.LEFT, p -> {
                this.page++;
                updateSlots();
            }));
        }
    }
}

package io.musician101.itembank.spigot.gui;

import com.google.common.collect.ImmutableMap;
import io.musician101.itembank.common.Reference.GUIText;
import io.musician101.itembank.common.Reference.Permissions;
import io.musician101.itembank.common.account.Account;
import io.musician101.itembank.common.account.AccountWorld;
import io.musician101.itembank.spigot.SpigotConfig;
import io.musician101.itembank.spigot.SpigotItemBank;
import io.musician101.musicianlibrary.java.minecraft.spigot.gui.chest.SpigotIconBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class AccountGUI extends ItemBankChestGUI {

    private final Account<ItemStack> account;
    private int page = 1;

    public AccountGUI(@Nonnull Account<ItemStack> account, @Nonnull Player player) {
        super(player, String.format(GUIText.ACCOUNT, getAccountName(account)));
        this.account = account;
        updateSlots();
        setButton(49, BACK_ICON, ImmutableMap.of(ClickType.LEFT, p -> {
            if (p.hasPermission(Permissions.PLAYER)) {
                new AccountsGUI(p);
                return;
            }

            p.closeInventory();
        }));
    }

    private boolean checkMultiWorld(@Nonnull AccountWorld<ItemStack> world) {
        SpigotConfig config = SpigotItemBank.instance().getPluginConfig();
        return config.isMultiWorldStorageEnabled() && !player.getWorld().getName().equals(world.getWorldName()) && (player.hasPermission(Permissions.WORLD + "." + world.getWorldName()) || player.hasPermission(Permissions.WORLD));
    }

    private void updateSlots() {
        List<AccountWorld<ItemStack>> worlds = new ArrayList<>(account.getWorlds());
        IntStream.range(0, 45).forEach(x -> {
            try {
                int index = x * (page - 1) + 45;
                AccountWorld<ItemStack> world = worlds.get(index);
                List<String> description = new ArrayList<>();
                description.add(ChatColor.GREEN + GUIText.CLICK_TO_VIEW);
                if (player.hasPermission(Permissions.PURGE)) {
                    description.add(ChatColor.RED + GUIText.CLICK_TO_PURGE);
                }

                ItemStack itemStack = SpigotIconBuilder.builder(Material.MAP).name((checkMultiWorld(world) ? ChatColor.GREEN : ChatColor.RED) + world.getWorldName()).description(description).build();
                setButton(x, itemStack, ImmutableMap.of(ClickType.LEFT, p -> new AccountWorldGUI(world, p), ClickType.RIGHT, p -> {
                    if (!player.hasPermission(Permissions.PURGE)) {
                        return;
                    }

                    world.clear();
                    updateSlots();
                }));
            }
            catch (IndexOutOfBoundsException e) {
                removeButton(x);
            }
        });

        if (page == 1) {
            removeButton(45);
        }
        else {
            setButton(45, NEXT_PAGE, ImmutableMap.of(ClickType.LEFT, p -> {
                page--;
                updateSlots();
            }));
        }

        int maxPage = Double.valueOf(Math.ceil(worlds.size() / 45d)).intValue();
        if (page < maxPage) {
            removeButton(53);
        }
        else {
            setButton(53, PREVIOUS_PAGE, ImmutableMap.of(ClickType.LEFT, p -> {
                page++;
                updateSlots();
            }));
        }
    }
}

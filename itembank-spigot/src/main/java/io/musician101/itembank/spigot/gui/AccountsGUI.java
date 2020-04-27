package io.musician101.itembank.spigot.gui;

import com.google.common.collect.ImmutableMap;
import io.musician101.itembank.common.Reference.GUIText;
import io.musician101.itembank.common.Reference.Permissions;
import io.musician101.itembank.common.account.Account;
import io.musician101.itembank.spigot.SpigotItemBank;
import io.musician101.musicianlibrary.java.minecraft.spigot.gui.SpigotIconBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class AccountsGUI extends ItemBankChestGUI {

    private int page = 1;

    public AccountsGUI(@Nonnull Player player) {
        super(player, GUIText.ACCOUNTS, 54);
        updateSlots();
        setButton(49, BACK_ICON, ImmutableMap.of(ClickType.LEFT, p -> {
            if (p.hasPermission(Permissions.PLAYER)) {
                new AccountsGUI(p);
                return;
            }

            p.closeInventory();
        }));
    }

    private void updateSlots() {
        List<Account<ItemStack>> accounts = SpigotItemBank.instance().getAccountStorage().getAccounts();
        IntStream.range(0, 45).forEach(x -> {
            try {
                int index = x * (page - 1) + 45;
                Account<ItemStack> account = accounts.get(index);
                List<String> description = Arrays.asList(ChatColor.GREEN + GUIText.CLICK_TO_VIEW, ChatColor.RED + GUIText.CLICK_TO_PURGE);
                ItemStack itemStack = SpigotIconBuilder.builder(Material.PLAYER_HEAD).name(account.getName()).description(description).build();
                setButton(x, itemStack, ImmutableMap.of(ClickType.LEFT, p -> new AccountGUI(account, p), ClickType.RIGHT, p -> {
                    if (!player.hasPermission(Permissions.PURGE)) {
                        return;
                    }

                    account.clear();
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

        int maxPage = Double.valueOf(Math.ceil(accounts.size() / 45d)).intValue();
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

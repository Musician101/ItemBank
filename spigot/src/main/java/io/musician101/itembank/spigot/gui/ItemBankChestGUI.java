package io.musician101.itembank.spigot.gui;

import io.musician101.itembank.common.Reference.GUIText;
import io.musician101.itembank.common.account.Account;
import io.musician101.itembank.spigot.SpigotItemBank;
import io.musician101.musicianlibrary.java.minecraft.spigot.gui.chest.SpigotChestGUI;
import io.musician101.musicianlibrary.java.minecraft.spigot.gui.chest.SpigotIconBuilder;
import javax.annotation.Nonnull;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class ItemBankChestGUI extends SpigotChestGUI<SpigotItemBank> {

    protected static final ItemStack BACK_ICON = SpigotIconBuilder.of(Material.BARRIER, ChatColor.RED + GUIText.BACK);
    protected static final ItemStack NEXT_PAGE = SpigotIconBuilder.of(Material.ARROW, GUIText.NEXT_PAGE);
    protected static final ItemStack PREVIOUS_PAGE = SpigotIconBuilder.of(Material.ARROW, GUIText.PREVIOUS_PAGE);

    protected ItemBankChestGUI(@Nonnull Player player, @Nonnull String name) {
        super(player, name, 54, SpigotItemBank.instance(), false);
    }

    protected static String getAccountName(@Nonnull Account<ItemStack> account) {
        String name = Bukkit.getOfflinePlayer(account.getID()).getName();
        return name == null ? account.getID().toString() : name;
    }
}

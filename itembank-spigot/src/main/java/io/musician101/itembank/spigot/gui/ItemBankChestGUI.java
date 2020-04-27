package io.musician101.itembank.spigot.gui;

import io.musician101.itembank.common.Reference.GUIText;
import io.musician101.itembank.spigot.SpigotItemBank;
import io.musician101.musicianlibrary.java.minecraft.spigot.gui.SpigotChestGUI;
import io.musician101.musicianlibrary.java.minecraft.spigot.gui.SpigotIconBuilder;
import javax.annotation.Nonnull;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class ItemBankChestGUI extends SpigotChestGUI<SpigotItemBank> {

    protected static final ItemStack BACK_ICON = SpigotIconBuilder.of(Material.BARRIER, ChatColor.RED + GUIText.BACK);
    protected static final ItemStack NEXT_PAGE = SpigotIconBuilder.of(Material.ARROW, GUIText.NEXT_PAGE);
    protected static final ItemStack PREVIOUS_PAGE = SpigotIconBuilder.of(Material.ARROW, GUIText.PREVIOUS_PAGE);

    protected ItemBankChestGUI(@Nonnull Player player, @Nonnull String name, int size) {
        super(player, name, size, SpigotItemBank.instance(), false);
    }
}

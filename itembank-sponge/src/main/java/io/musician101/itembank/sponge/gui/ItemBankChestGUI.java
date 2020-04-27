package io.musician101.itembank.sponge.gui;

import io.musician101.itembank.common.Reference.GUIText;
import io.musician101.itembank.sponge.SpongeItemBank;
import io.musician101.musicianlibrary.java.minecraft.sponge.gui.SpongeChestGUI;
import io.musician101.musicianlibrary.java.minecraft.sponge.gui.SpongeIconBuilder;
import javax.annotation.Nonnull;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public abstract class ItemBankChestGUI extends SpongeChestGUI {

    protected static final ItemStack BACK_ICON = SpongeIconBuilder.of(ItemTypes.BARRIER, Text.of(TextColors.RED, GUIText.BACK));
    protected static final ItemStack NEXT_PAGE = SpongeIconBuilder.of(ItemTypes.ARROW, Text.of(GUIText.NEXT_PAGE));
    protected static final ItemStack PREVIOUS_PAGE = SpongeIconBuilder.of(ItemTypes.ARROW, Text.of(GUIText.PREVIOUS_PAGE));

    protected ItemBankChestGUI(@Nonnull Player player, @Nonnull String name, int size) {
        super(player, Text.of(name), size, SpongeItemBank.instance().getPluginContainer(), false);
    }
}

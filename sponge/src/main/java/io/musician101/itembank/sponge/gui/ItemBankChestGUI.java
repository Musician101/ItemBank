package io.musician101.itembank.sponge.gui;

import io.musician101.itembank.common.Reference.GUIText;
import io.musician101.itembank.common.account.Account;
import io.musician101.itembank.sponge.SpongeItemBank;
import io.musician101.musicianlibrary.java.minecraft.sponge.gui.chest.SpongeChestGUI;
import io.musician101.musicianlibrary.java.minecraft.sponge.gui.chest.SpongeIconBuilder;
import java.util.Optional;
import javax.annotation.Nonnull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.profile.GameProfile;

public abstract class ItemBankChestGUI extends SpongeChestGUI {

    protected static final ItemStack BACK_ICON = SpongeIconBuilder.of(ItemTypes.BARRIER, Component.text(GUIText.BACK, NamedTextColor.RED));
    protected static final ItemStack NEXT_PAGE = SpongeIconBuilder.of(ItemTypes.ARROW, Component.text(GUIText.NEXT_PAGE));
    protected static final ItemStack PREVIOUS_PAGE = SpongeIconBuilder.of(ItemTypes.ARROW, Component.text(GUIText.PREVIOUS_PAGE));

    protected ItemBankChestGUI(@Nonnull ServerPlayer player, @Nonnull String name, boolean readOnly) {
        super(player, Component.text(name), 54, SpongeItemBank.instance().getPluginContainer(), false, readOnly);
    }

    protected static String getAccountName(@Nonnull Account<ItemStack> account) {
        String name = account.getID().toString();
        Optional<String> optional = Sponge.server().gameProfileManager().cache().findById(account.getID()).flatMap(GameProfile::name);
        if (optional.isPresent()) {
            name = optional.get();
        }

        return name;
    }
}

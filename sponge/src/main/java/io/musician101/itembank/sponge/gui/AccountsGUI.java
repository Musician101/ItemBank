package io.musician101.itembank.sponge.gui;

import com.google.common.collect.ImmutableMap;
import io.musician101.itembank.common.Reference.GUIText;
import io.musician101.itembank.common.Reference.Permissions;
import io.musician101.itembank.common.account.Account;
import io.musician101.itembank.sponge.SpongeItemBank;
import io.musician101.musicianlibrary.java.minecraft.sponge.gui.chest.SpongeIconBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.menu.ClickTypes;

public class AccountsGUI extends ItemBankChestGUI {

    private int page = 1;

    public AccountsGUI(@Nonnull ServerPlayer player) {
        super(player, GUIText.ACCOUNTS, true);
        updateSlots();
        setButton(49, BACK_ICON, ImmutableMap.of(ClickTypes.CLICK_LEFT.get(), p -> {
            if (p.hasPermission(Permissions.PLAYER)) {
                new AccountsGUI(p);
                return;
            }

            p.closeInventory();
        }));
    }

    private void updateSlots() {
        List<Account<ItemStack>> accounts = SpongeItemBank.instance().getAccountStorage().getData();
        IntStream.range(0, 45).forEach(x -> {
            try {
                int index = x * (page - 1) + 45;
                Account<ItemStack> account = accounts.get(index);
                List<Component> description = Arrays.asList(Component.text(GUIText.CLICK_TO_VIEW, NamedTextColor.GREEN), Component.text(GUIText.CLICK_TO_PURGE, NamedTextColor.RED));
                ItemStack itemStack = SpongeIconBuilder.builder(ItemTypes.PLAYER_HEAD).name(Component.text(getAccountName(account))).description(description).build();
                setButton(x, itemStack, ImmutableMap.of(ClickTypes.CLICK_LEFT.get(), p -> new AccountGUI(account, p), ClickTypes.CLICK_RIGHT.get(), p -> {
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
            setButton(45, NEXT_PAGE, ImmutableMap.of(ClickTypes.CLICK_LEFT.get(), p -> {
                page--;
                updateSlots();
            }));
        }

        int maxPage = Double.valueOf(Math.ceil(accounts.size() / 45d)).intValue();
        if (page < maxPage) {
            removeButton(53);
        }
        else {
            setButton(53, PREVIOUS_PAGE, ImmutableMap.of(ClickTypes.CLICK_LEFT.get(), p -> {
                page++;
                updateSlots();
            }));
        }
    }
}

package io.musician101.itembank.sponge.gui;

import com.google.common.collect.ImmutableMap;
import io.musician101.itembank.common.Reference.GUIText;
import io.musician101.itembank.common.Reference.Permissions;
import io.musician101.itembank.common.account.Account;
import io.musician101.itembank.sponge.SpongeItemBank;
import io.musician101.musicianlibrary.java.minecraft.sponge.gui.SpongeIconBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.SkullTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class AccountsGUI extends ItemBankChestGUI {

    private int page = 1;

    public AccountsGUI(@Nonnull Player player) {
        super(player, GUIText.ACCOUNTS, 54);
        updateSlots();
        setButton(49, BACK_ICON, ImmutableMap.of(ClickInventoryEvent.Primary.class, p -> {
            if (p.hasPermission(Permissions.PLAYER)) {
                new AccountsGUI(p);
                return;
            }

            p.closeInventory();
        }));
    }

    private void updateSlots() {
        List<Account<ItemStack>> accounts = SpongeItemBank.instance().getAccountStorage().getAccounts();
        IntStream.range(0, 45).forEach(x -> {
            try {
                int index = x * (page - 1) + 45;
                Account<ItemStack> account = accounts.get(index);
                List<Text> description = Arrays.asList(Text.of(TextColors.GREEN, GUIText.CLICK_TO_VIEW), Text.of(TextColors.RED, GUIText.CLICK_TO_PURGE));
                ItemStack itemStack = SpongeIconBuilder.builder(ItemTypes.SKULL).offer(Keys.SKULL_TYPE, SkullTypes.PLAYER).name(Text.of(account.getName())).description(description).build();
                setButton(x, itemStack, ImmutableMap.of(ClickInventoryEvent.Primary.class, p -> new AccountGUI(account, p), ClickInventoryEvent.Secondary.class, p -> {
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
            setButton(45, NEXT_PAGE, ImmutableMap.of(ClickInventoryEvent.Primary.class, p -> {
                page--;
                updateSlots();
            }));
        }

        int maxPage = Double.valueOf(Math.ceil(accounts.size() / 45d)).intValue();
        if (page < maxPage) {
            removeButton(53);
        }
        else {
            setButton(53, PREVIOUS_PAGE, ImmutableMap.of(ClickInventoryEvent.Primary.class, p -> {
                page++;
                updateSlots();
            }));
        }
    }
}

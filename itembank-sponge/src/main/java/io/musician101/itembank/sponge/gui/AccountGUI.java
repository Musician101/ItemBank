package io.musician101.itembank.sponge.gui;

import com.google.common.collect.ImmutableMap;
import io.musician101.itembank.common.Reference.GUIText;
import io.musician101.itembank.common.Reference.Permissions;
import io.musician101.itembank.common.account.Account;
import io.musician101.itembank.common.account.AccountWorld;
import io.musician101.itembank.sponge.SpongeItemBank;
import io.musician101.itembank.sponge.config.SpongeConfig;
import io.musician101.musicianlibrary.java.minecraft.sponge.gui.SpongeIconBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class AccountGUI extends ItemBankChestGUI {

    private final Account<ItemStack> account;
    private int page = 1;

    public AccountGUI(@Nonnull Account<ItemStack> account, @Nonnull Player player) {
        super(player, String.format(GUIText.ACCOUNT, account.getName()), 54);
        this.account = account;
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
        List<AccountWorld<ItemStack>> worlds = new ArrayList<>(account.getWorlds());
        IntStream.range(0, 45).forEach(x -> {
            try {
                int index = x * (page - 1) + 45;
                AccountWorld<ItemStack> world = worlds.get(index);
                List<Text> description = new ArrayList<>();
                description.add(Text.of(TextColors.GREEN, GUIText.CLICK_TO_VIEW));
                if (player.hasPermission(Permissions.PURGE)) {
                    description.add(Text.of(TextColors.RED, GUIText.CLICK_TO_PURGE));
                }

                ItemStack itemStack = SpongeIconBuilder.builder(ItemTypes.MAP).name(Text.of(checkMultiWorld(world) ? TextColors.GREEN : TextColors.RED, world.getWorldName())).description(description).build();
                setButton(x, itemStack, ImmutableMap.of(ClickInventoryEvent.Primary.class, p -> new AccountWorldGUI(world, p), ClickInventoryEvent.Secondary.class, p -> {
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
            setButton(45, NEXT_PAGE, ImmutableMap.of(ClickInventoryEvent.Primary.class, p -> {
                page--;
                updateSlots();
            }));
        }

        int maxPage = Double.valueOf(Math.ceil(worlds.size() / 45d)).intValue();
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

    private boolean checkMultiWorld(@Nonnull AccountWorld<ItemStack> world) {
        SpongeConfig config = SpongeItemBank.instance().getConfig();
        return config.isMultiWorldStorageEnabled() && !player.getWorld().getName().equals(world.getWorldName()) && (player.hasPermission(Permissions.WORLD + "." + world.getWorldName()) || player.hasPermission(Permissions.WORLD));
    }
}

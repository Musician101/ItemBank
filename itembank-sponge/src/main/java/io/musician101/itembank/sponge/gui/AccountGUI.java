package io.musician101.itembank.sponge.gui;

import com.google.common.collect.ImmutableMap;
import io.musician101.itembank.common.Reference.GUIText;
import io.musician101.itembank.common.Reference.Permissions;
import io.musician101.itembank.common.account.Account;
import io.musician101.itembank.common.account.AccountWorld;
import io.musician101.itembank.sponge.SpongeItemBank;
import io.musician101.itembank.sponge.config.SpongeConfig;
import io.musician101.musicianlibrary.java.minecraft.sponge.gui.chest.SpongeIconBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.menu.ClickTypes;

public class AccountGUI extends ItemBankChestGUI {

    private final Account<ItemStack> account;
    private int page = 1;

    public AccountGUI(@Nonnull Account<ItemStack> account, @Nonnull ServerPlayer player) {
        super(player, String.format(GUIText.ACCOUNT, getAccountName(account)), false);
        this.account = account;
        updateSlots();
        setButton(49, BACK_ICON, ImmutableMap.of(ClickTypes.CLICK_LEFT.get(), p -> {
            if (p.hasPermission(Permissions.PLAYER)) {
                new AccountsGUI(p);
                return;
            }

            p.closeInventory();
        }));
    }

    private boolean checkMultiWorld(@Nonnull AccountWorld<ItemStack> world) {
        SpongeConfig config = SpongeItemBank.instance().getConfig();
        return config.isMultiWorldStorageEnabled() && !player.world().key().asString().equals(world.getWorldName()) && (player.hasPermission(Permissions.WORLD + "." + world.getWorldName()) || player.hasPermission(Permissions.WORLD));
    }

    private void updateSlots() {
        List<AccountWorld<ItemStack>> worlds = new ArrayList<>(account.getWorlds());
        IntStream.range(0, 45).forEach(x -> {
            try {
                int index = x * (page - 1) + 45;
                AccountWorld<ItemStack> world = worlds.get(index);
                List<Component> description = new ArrayList<>();
                description.add(Component.text(GUIText.CLICK_TO_VIEW, NamedTextColor.GREEN));
                if (player.hasPermission(Permissions.PURGE)) {
                    description.add(Component.text(GUIText.CLICK_TO_PURGE, NamedTextColor.RED));
                }

                ItemStack itemStack = SpongeIconBuilder.builder(ItemTypes.MAP).name(Component.text(world.getWorldName(), checkMultiWorld(world) ? NamedTextColor.GREEN : NamedTextColor.RED)).description(description).build();
                setButton(x, itemStack, ImmutableMap.of(ClickTypes.CLICK_LEFT.get(), p -> new AccountWorldGUI(world, p), ClickTypes.CLICK_RIGHT.get(), p -> {
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
            setButton(45, NEXT_PAGE, ImmutableMap.of(ClickTypes.CLICK_LEFT.get(), p -> {
                page--;
                updateSlots();
            }));
        }

        int maxPage = Double.valueOf(Math.ceil(worlds.size() / 45d)).intValue();
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

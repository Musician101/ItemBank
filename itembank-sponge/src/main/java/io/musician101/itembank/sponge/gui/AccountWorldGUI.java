package io.musician101.itembank.sponge.gui;

import com.google.common.collect.ImmutableMap;
import io.musician101.itembank.common.Reference.GUIText;
import io.musician101.itembank.common.Reference.Messages;
import io.musician101.itembank.common.Reference.Permissions;
import io.musician101.itembank.common.account.AccountWorld;
import io.musician101.itembank.sponge.SpongeItemBank;
import io.musician101.itembank.sponge.config.SpongeConfig;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.menu.ClickTypes;
import org.spongepowered.api.item.inventory.query.QueryTypes;
import org.spongepowered.api.world.server.ServerWorld;

public class AccountWorldGUI extends ItemBankChestGUI {

    private final AccountWorld<ItemStack> accountWorld;
    private boolean hasBlacklistedItems = false;
    private boolean hasIllegalAmount = false;
    private int page = 1;

    public AccountWorldGUI(@Nonnull AccountWorld<ItemStack> accountWorld, @Nonnull ServerPlayer player) {
        super(player, String.format(GUIText.WORLD_PAGE, accountWorld.getWorldName(), 1), true);
        this.accountWorld = accountWorld;
        updateSlots();
        setButton(49, BACK_ICON, ImmutableMap.of(ClickTypes.CLICK_LEFT.get(), p -> {
            if (p.hasPermission(Permissions.PLAYER)) {
                new AccountsGUI(p);
                return;
            }

            p.closeInventory();
        }));
        extraCloseHandler = event -> event.cause().first(ServerPlayer.class).ifPresent(p -> {
            SpongeConfig config = SpongeItemBank.instance().getConfig();
            Container inventory = event.container();
            Map<ItemType, Integer> amounts = new HashMap<>();
            IntStream.range(0, 45).forEach(x -> inventory.peekAt(x).ifPresent(itemStack -> {
                ItemType type = itemStack.type();
                if (config.isBlacklisted(type) || !config.isWhitelisted(type)) {
                    hasBlacklistedItems = true;
                    dropItem(p, itemStack.copy().createSnapshot());
                    inventory.set(x, ItemStack.empty());
                }

                amounts.compute(type, (t, i) -> (i == null ? 0 : i) + itemStack.quantity());
            }));

            amounts.forEach((type, amount) -> {
                int maxAmount = config.getMaxAmount(type);
                if (maxAmount > amount) {
                    return;
                }

                hasIllegalAmount = true;
                while (maxAmount < amount) {
                    Inventory invSlot = inventory.query(QueryTypes.ITEM_TYPE.get().of(type));
                    ItemStack itemStack = invSlot.peek();
                    if (itemStack.isEmpty()) {
                        break;
                    }

                    Optional<Integer> slotOptional = invSlot.get(Keys.SLOT_INDEX);
                    if (!slotOptional.isPresent()) {
                        break;
                    }

                    int slot = slotOptional.get();
                    int maxStackSize = itemStack.maxStackQuantity();
                    if (maxStackSize < amount) {
                        dropItem(p, itemStack.copy().createSnapshot());
                        invSlot.set(slot, ItemStack.empty());
                        amount -= maxStackSize;
                    }
                    else {
                        ItemStack removed = itemStack.copy();
                        removed.setQuantity(amount - maxAmount);
                        Optional<ItemStack> stackOptional = inventory.peekAt(slot);
                        if (!stackOptional.isPresent()) {
                            IntStream.range(0, 45).forEach(y -> inventory.peekAt(y).ifPresent(is -> {
                                is.setQuantity(itemStack.quantity() - removed.quantity());
                                inventory.set(y, is);
                            }));
                        }
                        else {
                            stackOptional.get().setQuantity(itemStack.quantity() - removed.quantity());
                        }

                        dropItem(p, removed.createSnapshot());
                        amount -= removed.quantity();
                    }
                }
            });

            if (hasBlacklistedItems) {
                p.sendMessage(Identity.nil(), Component.text(Messages.BLACKLISTED_ITEM, NamedTextColor.RED));
            }

            if (hasIllegalAmount) {
                p.sendMessage(Identity.nil(), Component.text(Messages.ACCOUNT_ILLEGAL_AMOUNT, NamedTextColor.RED));
            }

            IntStream.range(0, 45).forEach(slot -> inventory.peekAt(slot).ifPresent(itemStack -> accountWorld.setSlot(page, slot, itemStack)));
        });
    }

    private void dropItem(@Nonnull ServerPlayer player, @Nonnull ItemStackSnapshot itemStack) {
        ServerWorld world = player.world();
        Entity entity = world.createEntity(EntityTypes.ITEM, player.position());
        entity.offer(Keys.ITEM_STACK_SNAPSHOT, itemStack.copy());
        world.spawnEntity(entity);
    }

    private void updateSlots() {
        if (page > accountWorld.getPages().size()) {
            accountWorld.setPage(page, new ItemStack[45]);
        }

        ItemStack[] page = accountWorld.getPage(this.page);
        IntStream.range(0, 45).forEach(i -> addItem(i, page[i]));
        if (this.page == 1) {
            removeButton(45);
        }
        else {
            setButton(45, NEXT_PAGE, ImmutableMap.of(ClickTypes.CLICK_LEFT.get(), p -> {
                this.page--;
                updateSlots();
            }));
        }

        int maxPage = Double.valueOf(Math.ceil(SpongeItemBank.instance().getConfig().getPageLimit() / 45d)).intValue();
        if (this.page < maxPage) {
            removeButton(53);
        }
        else {
            setButton(53, PREVIOUS_PAGE, ImmutableMap.of(ClickTypes.CLICK_LEFT.get(), p -> {
                this.page++;
                updateSlots();
            }));
        }
    }
}

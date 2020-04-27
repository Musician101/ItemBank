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
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

public class AccountWorldGUI extends ItemBankChestGUI {

    private final AccountWorld<ItemStack> accountWorld;
    private int page = 1;
    private boolean hasIllegalAmount = false;
    private boolean hasBlacklistedItems = false;

    public AccountWorldGUI(@Nonnull AccountWorld<ItemStack> accountWorld, @Nonnull Player player) {
        super(player, String.format(GUIText.WORLD_PAGE, accountWorld.getWorldName(), 1), 54);
        this.accountWorld = accountWorld;
        updateSlots();
        setButton(49, BACK_ICON, ImmutableMap.of(ClickInventoryEvent.Primary.class, p -> {
            if (p.hasPermission(Permissions.PLAYER)) {
                new AccountsGUI(p);
                return;
            }

            p.closeInventory();
        }));
        extraCloseHandler = event -> {
            //noinspection OptionalGetWithoutIsPresent
            Player p = event.getCause().first(Player.class).get();
            SpongeConfig config = SpongeItemBank.instance().getConfig();
            Container inventory = event.getTargetInventory();
            Map<ItemType, Integer> amounts = new HashMap<>();
            IntStream.range(0, 45).forEach(x -> {
                Inventory slot = inventory.query(QueryOperationTypes.INVENTORY_PROPERTY.of(new SlotIndex(x)));
                slot.peek().ifPresent(itemStack -> {
                    ItemType type = itemStack.getType();
                    if (config.isBlacklisted(type) || !config.isWhitelisted(type)) {
                        hasBlacklistedItems = true;
                        dropItem(p, itemStack);
                        slot.set(ItemStack.empty());
                        return;
                    }

                    amounts.compute(type, (t, i) -> (i == null ? 0 : i) + itemStack.getQuantity());
                });
            });

            amounts.forEach((type, amount) -> {
                int maxAmount = config.getMaxAmount(type);
                if (maxAmount > amount) {
                    return;
                }

                hasIllegalAmount = true;
                while (maxAmount < amount) {
                    Inventory invSlot = inventory.query(QueryOperationTypes.ITEM_TYPE.of(type));
                    Optional<ItemStack> itemStackOptional = invSlot.peek();
                    if (!itemStackOptional.isPresent()) {
                        break;
                    }

                    ItemStack itemStack = itemStackOptional.get();
                    Optional<Integer> slotOptional =  invSlot.getInventoryProperty(SlotIndex.class).map(SlotIndex::getValue);
                    if (!slotOptional.isPresent()) {
                        break;
                    }

                    int slot = slotOptional.get();
                    int maxStackSize = itemStack.getMaxStackQuantity();
                    if (maxStackSize < amount) {
                        dropItem(p, itemStack.copy());
                        invSlot.set(ItemStack.empty());
                        amount -= maxStackSize;
                    }
                    else {
                        ItemStack removed = itemStack.copy();
                        removed.setQuantity(amount - maxAmount);
                        Optional<ItemStack> stackOptional = inventory.query(QueryOperationTypes.INVENTORY_PROPERTY.of(new SlotIndex(slot))).peek();
                        if (!stackOptional.isPresent()) {
                            IntStream.range(0, 45).forEach(y -> {
                                Inventory iSlot = inventory.query(QueryOperationTypes.INVENTORY_PROPERTY.of(new SlotIndex(y)));
                                iSlot.peek().ifPresent(is -> {
                                    is.setQuantity(itemStack.getQuantity() - removed.getQuantity());
                                    iSlot.set(is);
                                });
                            });
                        }
                        else {
                            stackOptional.get().setQuantity(itemStack.getQuantity() - removed.getQuantity());
                        }

                        dropItem(p, removed);
                        amount -= removed.getQuantity();
                    }
                }
            });

            if (hasBlacklistedItems) {
                p.sendMessage(Text.of(TextColors.RED, Messages.BLACKLISTED_ITEM));
            }

            if (hasIllegalAmount) {
                p.sendMessage(Text.of(TextColors.RED, Messages.ACCOUNT_ILLEGAL_AMOUNT));
            }

            IntStream.range(0, 45).forEach(slot -> inventory.query(QueryOperationTypes.INVENTORY_PROPERTY.of(new SlotIndex(slot))).peek().ifPresent(itemStack -> accountWorld.setSlot(page, slot, itemStack)));
        };
    }

    private void dropItem(@Nonnull Player player, @Nonnull ItemStack itemStack) {
        World world = player.getWorld();
        Entity entity = world.createEntity(EntityTypes.ITEM, player.getPosition());
        entity.offer(Keys.REPRESENTED_ITEM, itemStack.copy().createSnapshot());
        world.spawnEntity(entity);
    }

    private void updateSlots() {
        ItemStack[] page = accountWorld.getPage(this.page);
        IntStream.range(0, 45).forEach(i -> addItem(i, page[i]));
        if (this.page == 1) {
            removeButton(45);
        }
        else {
            setButton(45, NEXT_PAGE, ImmutableMap.of(ClickInventoryEvent.Primary.class, p -> {
                this.page--;
                updateSlots();
            }));
        }

        int maxPage = Double.valueOf(Math.ceil(SpongeItemBank.instance().getConfig().getPageLimit() / 45d)).intValue();
        if (this.page < maxPage) {
            removeButton(53);
        }
        else {
            setButton(53, PREVIOUS_PAGE, ImmutableMap.of(ClickInventoryEvent.Primary.class, p -> {
                this.page++;
                updateSlots();
            }));
        }
    }
}

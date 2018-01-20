package io.musician101.itembank.sponge.account;

import io.musician101.itembank.common.ItemBank;
import io.musician101.itembank.common.Reference.Messages;
import io.musician101.itembank.common.Reference.Permissions;
import io.musician101.itembank.common.account.AbstractInventoryHandler;
import io.musician101.itembank.common.account.AccountPage;
import io.musician101.itembank.common.account.AccountSlot;
import io.musician101.itembank.sponge.IBUtils;
import io.musician101.itembank.sponge.SpongeItemBank;
import io.musician101.itembank.sponge.config.SpongeConfig;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryCapacity;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class SpongeInventoryHandler extends AbstractInventoryHandler<Inventory, Player, ItemStack> {

    public SpongeInventoryHandler(ItemBank<ItemStack, Logger, Player, World> plugin, AccountPage<ItemStack> page, Player viewer, String ownerName, World world) {
        super(parseInventory(plugin, page, ownerName, world), page, viewer);
        Sponge.getEventManager().registerListeners(this, plugin);
        viewer.openInventory(inventory);
    }

    private static Inventory getSlot(Inventory inventory, int slot) {
        return inventory.query(QueryOperationTypes.INVENTORY_PROPERTY.of(new SlotIndex(slot)));
    }

    private static Inventory parseInventory(ItemBank<ItemStack, Logger, Player, World> plugin, AccountPage<ItemStack> page, String ownerName, World world) {
        String name = ownerName + "'s Account for " + world.getName() + " - Page " + page.getPage();
        InventoryArchetype.Builder builder = InventoryArchetype.builder().property(new InventoryCapacity(54)).title(Text.of(name));
        for (int i = 0; i < 54; i++) {
            builder.with(InventoryArchetype.builder().from(InventoryArchetypes.SLOT).property(new SlotIndex(i)).build("minecraft:slot" + i, "Slot"));
        }

        Inventory inventory = Inventory.builder().of(builder.build(plugin.getId() + ":" + name.replace("\\s", "_").replace("'", "").replace("-", ""), name)).build(plugin);
        page.getSlots().values().forEach(slot -> setItem(inventory, slot.getSlot(), slot.getItemStack()));
        return inventory;
    }

    private static void setItem(Inventory inventory, int slot, ItemStack itemStack) {
        getSlot(inventory, slot).set(itemStack);
    }

    @Listener
    public void close(InteractInventoryEvent.Close event, @First Player player) {
        Container inv = event.getTargetInventory();
        if (!(inv.getViewers().contains(viewer))) {
            return;
        }

        if (!player.getUniqueId().equals(viewer.getUniqueId()) && player.hasPermission(Permissions.ADMIN)) {
            return;
        }

        if (!inv.getName().equals(inventory.getName())) {
            return;
        }

        SpongeItemBank.instance().map(SpongeItemBank.class::cast).ifPresent(plugin -> {
            SpongeConfig config = plugin.getConfig();
            IntStream.range(0, inv.capacity()).forEach(x -> getItem(inv, x).filter(is -> is.getType() != ItemTypes.AIR).ifPresent(itemStack -> {
                itemAmount = 0;
                StreamSupport.stream(inv.spliterator(), false).map(Inventory::peek).filter(Optional::isPresent).map(Optional::get).filter(is -> is.getType() != ItemTypes.AIR && itemStack.getType() == is.getType() && IBUtils.isSameVariant(itemStack, is)).forEach(is -> itemAmount += itemStack.getQuantity());
                ItemStack configItem = config.getItem(itemStack);
                if (configItem != null && config.isWhitelist()) {
                    handleMaxAmount(inv, x, configItem, itemStack, player);
                }
                else if (configItem == null && config.isWhitelist()) {
                    spawnItem(itemStack, player.getLocation());
                    setItem(inv, x, ItemStack.empty());
                    hasIllegalItems = true;
                }
            }));

            if (hasIllegalItems) {
                player.sendMessage(Text.builder(Messages.ACCOUNT_ILLEGAL_ITEM).color(TextColors.RED).build());
            }

            if (hasIllegalAmount) {
                player.sendMessage(Text.builder(Messages.ACCOUNT_ILLEGAL_AMOUNT).color(TextColors.RED).build());
            }

            IntStream.range(0, inv.size()).forEach(slot -> getItem(inv, slot).ifPresent(itemStack -> {
                if (itemStack.getType() == ItemTypes.AIR) {
                    page.clearSlot(slot);
                }
                else {
                    page.setSlot(new AccountSlot<>(slot, itemStack));
                }
            }));
        });

        Sponge.getEventManager().unregisterListeners(this);
    }

    private Optional<ItemStack> getItem(Inventory inventory, int slot) {
        return getSlot(inventory, slot).peek();
    }

    private void handleMaxAmount(Inventory inv, int x, ItemStack configItem, ItemStack itemStack, Player player) {
        int maxAmount = configItem.getQuantity();
        if (maxAmount == 0) {
            spawnItem(itemStack, player.getLocation());
            setItem(inv, x, ItemStack.empty());
            hasIllegalItems = true;
        }
        else if (maxAmount < itemAmount) {
            int amount = itemAmount;
            while (maxAmount < amount) {
                int maxStackSize = itemStack.getMaxStackQuantity();
                if (maxStackSize < amount) {
                    spawnItem(itemStack, player.getLocation());
                    setItem(inv, x, ItemStack.empty());
                    amount -= maxStackSize;
                }
                else {
                    ItemStack removeItem = itemStack.copy();
                    removeItem.setQuantity(amount - maxAmount);
                    if (!getItem(inv, x).isPresent()) {
                        IntStream.range(0, inv.capacity()).forEach(y -> getItem(inv, y).filter(is -> IBUtils.isSameVariant(is, itemStack)).ifPresent(is -> {
                            is.setQuantity(itemStack.getQuantity() - removeItem.getQuantity());
                            setItem(inv, y, is);
                        }));
                    }
                    else {
                        getItem(inv, x).ifPresent(itemStack1 -> itemStack1.setQuantity(itemStack.getQuantity() - removeItem.getQuantity()));
                    }

                    spawnItem(removeItem, player.getLocation());
                    amount -= removeItem.getQuantity();
                }
            }

            hasIllegalAmount = true;
        }
    }

    private void spawnItem(ItemStack itemStack, Location<World> location) {
        World world = location.getExtent();
        Entity entity = world.createEntity(EntityTypes.ITEM, location.getPosition());
        entity.offer(Keys.REPRESENTED_ITEM, itemStack.createSnapshot());
    }
}

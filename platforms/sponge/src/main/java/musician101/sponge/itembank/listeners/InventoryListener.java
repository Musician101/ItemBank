package musician101.sponge.itembank.listeners;

import com.flowpowered.math.vector.Vector3i;
import musician101.common.java.minecraft.sponge.TextUtils;
import musician101.itembank.common.Reference;
import musician101.itembank.common.Reference.Messages;
import musician101.itembank.common.Reference.Permissions;
import musician101.sponge.itembank.SpongeItemBank;
import musician101.sponge.itembank.config.SpongeConfig;
import musician101.sponge.itembank.util.AccountUtil;
import musician101.sponge.itembank.util.IBUtils;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.json.simple.parser.ParseException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.type.OrderedInventory;
import org.spongepowered.api.world.World;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

//TODO change to be similar to Spigot AccountPage
@Deprecated
public class InventoryListener
{
    private final int page;
    private final UUID viewer;
    private final String worldName;
    private final UUID owner;

    public InventoryListener(UUID viewer, UUID owner, World world, int page)
    {
        this.viewer = viewer;
        this.owner = owner;
        this.worldName = world.getName();
        this.page = page;
    }

	// player.getUniqueId() and UUID are not always the same.
	private void saveAccount(Player player, String worldName, UUID uuid, OrderedInventory topInv, OrderedInventory playerInv, int page)
	{
		OrderedInventory account;
		try
		{
			account = AccountUtil.getAccount(Sponge.getServer().getWorld(worldName).get(), uuid, page);
		}
		catch (IOException e)
		{
			player.sendMessage(TextUtils.redText(Messages.IO_EX));
            transferInv((OrderedInventory) player.getInventory(), playerInv);
			return;
		}
		catch (ClassNotFoundException | ObjectMappingException | ParseException | SQLException e)
		{
			player.sendMessage(TextUtils.redText(Messages.PARSE_EX));
            transferInv((OrderedInventory) player.getInventory(), playerInv);
			return;
		}

		transferInv(account, topInv);
		try
		{
			AccountUtil.saveAccount(worldName, uuid, account, page);
		}
		catch (IOException e)
		{
			player.sendMessage(TextUtils.redText(Messages.IO_EX));
            transferInv((OrderedInventory) player.getInventory(), playerInv);
			return;
		}
		catch (ClassNotFoundException | ObjectMappingException | SQLException e)
		{
			player.sendMessage(TextUtils.redText(Messages.PARSE_EX));
            transferInv((OrderedInventory) player.getInventory(), playerInv);
			return;
		}
		
		player.sendMessage(TextUtils.greenText(Messages.ACCOUNT_UPDATED));
	}

    private void transferInv(OrderedInventory transferTo, OrderedInventory transferFrom)
    {
        for (int x = 0; x < transferFrom.size(); x++)
        {
            Optional<ItemStack> itemStack = transferFrom.getSlot(new SlotIndex(x)).get().peek();
            if (itemStack.isPresent())
                transferTo.set(new SlotIndex(x), itemStack.get());
        }
    }
	
	@Listener
	public void onInventoryClose(InteractInventoryEvent.Close event)
	{
		//TODO missing methods
        Optional<Player> playerOptional = event.getCause().first(Player.class);
        if (!playerOptional.isPresent())
            return;

		Player player = playerOptional.get();
        if (player.getUniqueId() != viewer)
            return;

        SpongeConfig config = SpongeItemBank.config;
        OrderedInventory inv = (OrderedInventory) event.getTargetInventory();
        int pageLimit = config.getPageLimit();
        if (((pageLimit > 0 && pageLimit < page) || page == 0) && !player.hasPermission(Permissions.ADMIN))
        {
            for (Inventory slot : inv)
            {
                Optional<ItemStack> itemStackOptional = slot.peek();
                if (itemStackOptional.isPresent())
                    dropItemOnPlayer(player, itemStackOptional.get());
            }

            player.sendMessage(TextUtils.redText(Messages.ACCOUNT_ILLEGAL_PAGE));
            return;
        }

        boolean hasIllegalItems = false;
        boolean hasIllegalAmount = false;
        for (int x = 0; x < inv.size(); x++)
        {
            Optional<Slot> slotOptional = inv.getSlot(new SlotIndex(x));
            if (slotOptional.isPresent())
            {
                Optional<ItemStack> itemStackOptional = slotOptional.get().peek();
                if (itemStackOptional.isPresent())
                {
                    ItemStack itemStack = itemStackOptional.get();
                    int itemAmount = IBUtils.getAmount(inv, itemStack);
                    if (config.getItem(itemStack) != null && !config.isWhitelist())
                    {
                        int maxAmount = config.getItem(itemStack).getQuantity();
                        if (maxAmount == 0)
                        {
                            dropItemOnPlayer(player, itemStack);
                            hasIllegalItems = true;
                        }
                        else if (maxAmount < itemAmount)
                        {
                            int amount = itemAmount;
                            while (maxAmount < amount)
                            {
                                int maxStackSize = itemStack.getMaxStackQuantity();
                                if (maxStackSize < amount)
                                {
                                    dropItemOnPlayer(player, itemStack);
                                    inv.getSlot(new SlotIndex(x)).get().clear();
                                    amount -= maxStackSize;
                                }
                                else
                                {
                                    ItemStack removeItem = itemStack.copy();
                                    removeItem.setQuantity(amount - maxAmount);
                                    if (!inv.getSlot(new SlotIndex(x)).isPresent())
                                    {
                                        int slot = 0;
                                        for (int y = 0; y < inv.size(); y++)
                                        {
                                            Optional<ItemStack> itemOptional = inv.getSlot(new SlotIndex(y)).get().peek();
                                            if (itemOptional.isPresent())
                                                if (IBUtils.isSameVariant(itemOptional.get(), itemStack))
                                                    slot = y;
                                        }

                                        ItemStack is = inv.getSlot(new SlotIndex(slot)).get().peek().get();
                                        is.setQuantity(itemStack.getQuantity() - removeItem.getQuantity());
                                        inv.set(new SlotIndex(x), is);
                                    }
                                    else
                                        inv.getSlot(new SlotIndex(x)).get().peek().get().setQuantity(itemStack.getQuantity() - removeItem.getQuantity());

                                    dropItemOnPlayer(player, removeItem);
                                    amount -= removeItem.getQuantity();
                                }
                            }

                            hasIllegalAmount = true;
                        }
                    }
                    else if (config.getItem(itemStack) == null && config.isWhitelist())
                    {
                        dropItemOnPlayer(player, itemStack);
                        inv.getSlot(new SlotIndex(x)).get().clear();
                        hasIllegalItems = true;
                    }

                    if (hasIllegalItems)
                        player.sendMessage(TextUtils.redText(Messages.ACCOUNT_ILLEGAL_ITEM));

                    if (hasIllegalAmount)
                        player.sendMessage(TextUtils.redText(Messages.ACCOUNT_ILLEGAL_AMOUNT));

                    saveAccount(player, worldName, owner, inv, (OrderedInventory) player.getInventory(), page);
                }
            }
        }
	}

    private void dropItemOnPlayer(Player player, ItemStack itemStack)
    {
        World world = player.getWorld();
        Vector3i vector3i = player.getLocation().getBlockPosition();
        Item item = (Item) world.createEntity(EntityTypes.ITEM, vector3i).get();
        item.getItemData().set(Keys.REPRESENTED_ITEM, itemStack.createSnapshot());
        world.spawnEntity(item, Cause.of(Sponge.getPluginManager().getPlugin(Reference.ID), SpawnCause.builder().type(SpawnTypes.CUSTOM)));
    }
}

package musician101.itembank.forge.inventory;

import musician101.itembank.forge.config.ConfigHandler;
import musician101.itembank.forge.util.IBUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.mojang.authlib.GameProfile;

public class BankContainer extends Container
{
	BankInventory bank;
	GameProfile bankOwner;
	int rows = 6;
	int columns = 9;
	int size = rows * columns;
	
	public BankContainer(EntityPlayerMP player, InventoryPlayer playerInv, BankInventory bank, GameProfile bankOwner)
	{
		this.bank = bank;
		this.bankOwner = bankOwner;
		for (int menuRowIndex = 0; menuRowIndex < rows; menuRowIndex++)
			for (int menuColumnIndex = 0; menuColumnIndex < columns; menuColumnIndex++)
				addSlotToContainer(new BankSlot(bank, menuColumnIndex + menuRowIndex * 8, 8 + menuColumnIndex * 18, 8 + menuRowIndex * 18));
		
		for (int menuRowIndex = 0; menuRowIndex < 3; menuRowIndex++)
			for (int menuColumnIndex = 0; menuColumnIndex < 9; menuColumnIndex++)
				addSlotToContainer(new BankSlot(playerInv, menuColumnIndex + menuRowIndex * 9 + 9, 8 + menuColumnIndex * 18, 103 + menuRowIndex * 18));
		
		for (int actionBarSlotIndex = 0; actionBarSlotIndex < 9; actionBarSlotIndex++)
			addSlotToContainer(new Slot(playerInv, actionBarSlotIndex, 8 + actionBarSlotIndex * 18, 161));
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
	{
		ItemStack itemStack = null;
		Slot slot = (Slot) this.inventorySlots.get(index);
		
		if (slot != null && slot.getHasStack())
		{
			itemStack = slot.getStack();
			if (index >= bank.getSizeInventory() - 1)
			{
				int itemAmount = IBUtils.getAmount(bank, itemStack.getItem(), itemStack.getItemDamage()) + itemStack.stackSize;
				int limit = ConfigHandler.getRestrictedItem(itemStack).stackSize;
				if (itemAmount > limit)
				{
					int maxStackSize = itemStack.getMaxStackSize();
					if (maxStackSize < itemAmount)
						return null;
					else
					{
						ItemStack itemStackCopy = itemStack.copy();
						itemStackCopy.stackSize = itemAmount - limit;
						return itemStackCopy;
					}
				}
			}
		}
		
		return itemStack;
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn)
	{
		if (bankOwner.getId() != playerIn.getUniqueID())
			return IBUtils.isPlayerOpped(playerIn.getGameProfile()); 
			
		return true;
	}
}

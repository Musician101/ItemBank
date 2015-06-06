package musician101.itembank.forge.inventory;

import java.io.File;
import java.io.IOException;

import musician101.itembank.forge.config.ConfigHandler;
import musician101.itembank.forge.lib.Messages;
import musician101.itembank.forge.util.IBUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;

import com.mojang.authlib.GameProfile;

public class BankInventory extends InventoryBasic
{
	EntityPlayerMP player;
	GameProfile bankOwner;
	InventoryPlayer playerInventory;
	int page;
	
	public BankInventory(EntityPlayerMP player, GameProfile bankOwner, int page) throws IOException
	{
		super(bankOwner.getName() + " - Page " + page, true, 54);
		this.player = player;
		this.bankOwner = bankOwner;
		this.playerInventory = player.inventory;
		this.page = page;
		readFromNBT(CompressedStreamTools.read(new File(ConfigHandler.bankDirectory + "/" + bankOwner.getId().toString() + ".dat")));
		
	}
	
	@Override
	public void closeInventory(EntityPlayer player)
	{
		super.closeInventory(player);
		boolean itemMatch = false;
		for (int i = 0; i < getSizeInventory(); i++)
		{
			ItemStack item = getStackInSlot(i);
			if (item != null)
			{
				if (ConfigHandler.isItemBlacklisted(item) || !ConfigHandler.isItemWhitelisted(item))
				{
					itemMatch = true;
					returnItem(item, i);
				}
				else if (ConfigHandler.isItemRestricted(item))
				{
					int limit = ConfigHandler.getRestrictedItem(item).stackSize;
					if (IBUtils.getAmount(this, item.getItem(), item.getItemDamage()) > limit)
					{
						itemMatch = true;
						if (limit > item.getMaxStackSize())
							returnItem(item, i);
						else if (limit < item.getMaxStackSize())
						{
							ItemStack droppedItem = item.copy();
							ItemStack notDroppedItem = item.copy();
							droppedItem.stackSize = item.stackSize - limit;
							notDroppedItem.stackSize = limit;
							setInventorySlotContents(i, notDroppedItem);
							player.inventory.addItemStackToInventory(droppedItem);
							player.inventoryContainer.detectAndSendChanges();
						}
					}
				}
			}
		}
		
		if (itemMatch)
			player.addChatComponentMessage(Messages.PREFIX.appendSibling(IBUtils.getChatComponent("Some items could not be stored in your bank and were returned to you.")));
		
		File file = new File(ConfigHandler.bankDirectory + "/" + bankOwner.getId().toString() + ".dat");
		try
		{
			NBTTagCompound nbt = CompressedStreamTools.read(file);
			writeToNBT(nbt);
			CompressedStreamTools.write(nbt, file);
		}
		catch (IOException e)
		{
			player.addChatMessage(Messages.IO_EX);
			player.inventory = playerInventory;
			player.inventoryContainer.detectAndSendChanges();
			return;
		}
		
		//TODO left off here
		player.addChatMessage(Messages.ACCOUNT_UPDATED);
	}
	
	private void returnItem(ItemStack item, int slot)
	{
		setInventorySlotContents(slot, null);
		player.inventory.addItemStackToInventory(item);
		player.inventoryContainer.detectAndSendChanges();
	}
	
	//TODO remove slot tag as an itemstack has it by default
	private void readFromNBT(NBTTagCompound nbtTagCompound)
	{
		if (nbtTagCompound != null && nbtTagCompound.hasKey("" + page))
		{
			NBTTagList items = nbtTagCompound.getTagList("" + page, NBT.TAG_LIST);
			for (int i = 0; i < items.tagCount(); i++)
			{
				NBTTagCompound item = items.getCompoundTagAt(i);
				byte slotIndex = item.getByte("Slot");
				if (slotIndex >= 0 && slotIndex < getSizeInventory())
					setInventorySlotContents(slotIndex, ItemStack.loadItemStackFromNBT(item));
			}
		}
	}
	
	private void writeToNBT(NBTTagCompound nbtTagCompound)
	{
		NBTTagList items = new NBTTagList();
		for (int slot = 0; slot < getSizeInventory(); slot++)
		{
			if (getStackInSlot(slot) != null)
			{
				NBTTagCompound item = new NBTTagCompound();
				item.setByte("Slot", (byte) slot);
				getStackInSlot(slot).writeToNBT(item);
				items.appendTag(item);
			}
		}
	}
}

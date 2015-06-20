package musician101.itembank.forge.inventory;

import java.io.File;
import java.io.IOException;

import musician101.itembank.forge.config.ConfigHandler;
import musician101.itembank.forge.lib.Messages;
import net.minecraft.entity.player.EntityPlayer;
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
	GameProfile bankOwner;
	int dimension;
	int page;
	InventoryPlayer playerInventory;
	
	public BankInventory(EntityPlayer player, GameProfile bankOwner, int dimension, int page) throws IOException
	{
		super(bankOwner.getName() + " - Page " + page, true, 54);
		this.bankOwner = bankOwner;
		this.playerInventory = player.inventory;
		this.dimension = dimension;
		this.page = page;
		readFromNBT(CompressedStreamTools.read(new File(ConfigHandler.bankDirectory + "/" + bankOwner.getId().toString() + ".dat")));
	}
	
	@Override
	public void openInventory(EntityPlayer player)
	{
		super.openInventory(player);
		File file = new File(ConfigHandler.bankDirectory, bankOwner.getId().toString() + ".dat");
		try
		{
			NBTTagCompound nbt = CompressedStreamTools.read(file);
			readFromNBT(nbt);
		}
		catch (IOException e)
		{
			player.addChatMessage(Messages.IO_EX);
			return;
		}
	}
	
	@Override
	public void closeInventory(EntityPlayer player)
	{
		super.closeInventory(player);
		File file = new File(ConfigHandler.bankDirectory, bankOwner.getId().toString() + ".dat");
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
		
		player.addChatMessage(Messages.ACCOUNT_UPDATED);
	}
	
	private void readFromNBT(NBTTagCompound nbtTagCompound)
	{
		if (nbtTagCompound != null && nbtTagCompound.hasKey("" + dimension))
		{
			NBTTagCompound world = nbtTagCompound.getCompoundTag("" + dimension);
			if (world != null && world.hasKey("" + page))
			{
				NBTTagList items = nbtTagCompound.getTagList("" + page, NBT.TAG_LIST);
				for (int i = 0; i < items.tagCount(); i++)
				{
					NBTTagCompound item = items.getCompoundTagAt(i);
					byte slot = item.getByte("Slot");
					if (slot >= 0 && slot < getSizeInventory())
						setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(item));
				}
			}
		}
	}
	
	private void writeToNBT(NBTTagCompound nbtTagCompound)
	{
		NBTTagCompound world = new NBTTagCompound();
		if (nbtTagCompound == null)
			nbtTagCompound = new NBTTagCompound();
		
		if (nbtTagCompound.hasKey("" + dimension))
			world = nbtTagCompound.getCompoundTag("" + dimension);
		
		NBTTagList itemPage = new NBTTagList();
		for (int slot = 0; slot < getSizeInventory(); slot++)
			if (getStackInSlot(slot) != null)
				itemPage.appendTag(getStackInSlot(slot).writeToNBT(new NBTTagCompound()));
		
		world.setTag("" + page, itemPage);
		nbtTagCompound.setTag("" + dimension, world);
	}
}

package musician101.itembank.forge.inventory;

import java.io.File;
import java.io.IOException;

import musician101.itembank.forge.config.ConfigHandler;
import musician101.itembank.forge.lib.Messages;
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
	GameProfile bankOwner;
	InventoryPlayer playerInventory;
	int page;
	
	public BankInventory(EntityPlayerMP player, GameProfile bankOwner, int page) throws IOException
	{
		super(bankOwner.getName() + " - Page " + page, true, 54);
		this.bankOwner = bankOwner;
		this.playerInventory = player.inventory;
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
		if (nbtTagCompound != null && nbtTagCompound.hasKey("" + page))
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
	
	private void writeToNBT(NBTTagCompound nbtTagCompound)
	{
		NBTTagList items = new NBTTagList();
		for (int slot = 0; slot < getSizeInventory(); slot++)
			if (getStackInSlot(slot) != null)
				items.appendTag(getStackInSlot(slot).writeToNBT(new NBTTagCompound()));
	}
}

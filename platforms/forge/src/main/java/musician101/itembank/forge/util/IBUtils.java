package musician101.itembank.forge.util;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class IBUtils
{
	/*public static void createPlayerFile(File file) throws IOException
	{
		if (!file.exists())
		{
			file.createNewFile();
			BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
			if (file.getName().endsWith(".json"))
				bw.write("{\"_comment\":\"" + Messages.NEW_PLAYER_FILE + "\"}");
			else if (file.getName().endsWith(".csv"))
			{
				bw.write("# " + Messages.NEW_PLAYER_FILE + "\n");
				bw.write("# world|page|slot|material|damage/durability|amount|meta data");
			}
			else
				bw.write("# " + Messages.NEW_PLAYER_FILE);
			
			bw.close();
		}
	}
	
	public static void createPlayerFiles() throws IOException
	{
		for (Object obj : MinecraftServer.getServer().getConfigurationManager().playerEntityList)
		{
			EntityPlayerMP player = (EntityPlayerMP) obj;
			createPlayerFile(new File(ConfigHandler.bankDirectory, player.getUniqueID() + "." + ConfigHandler.format));
		}
	}*/
	
	public static int getAmount(IInventory inventory, Item item, int damage)
	{
		int amount = 0;
		for (int i = 0; i < inventory.getSizeInventory(); i++)
		{
			ItemStack is = inventory.getStackInSlot(i);
			if (is != null && is.getItem() == item && is.getItemDamage() == damage)
				amount += is.stackSize;
		}
		
		return amount;
	}
	
	public static boolean isNumber(String s)
	{
		try
		{
			Integer.valueOf(s);
		}
		catch (NumberFormatException e)
		{
			return false;
		}
		
		return true;
	}
	
	public static IChatComponent getTranslatedChatComponent(String key)
	{
		return getTranslatedChatComponent(key, true);
	}
	
	public static IChatComponent getTranslatedChatComponent(String key, boolean includePrefix)
	{
		ChatComponentTranslation translation = new ChatComponentTranslation(key, new Object[0]);
		if (includePrefix)
		{
			IChatComponent cct = new ChatComponentText("[ItemBank]").appendSibling(translation);
			cct.getChatStyle().setColor(EnumChatFormatting.DARK_RED);
			return cct;
		}
		
		return translation;
	}
}

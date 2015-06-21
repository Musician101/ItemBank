package musician101.itembank.forge.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import musician101.itembank.forge.ItemBank;
import musician101.itembank.forge.lib.Constants.ModInfo;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventHandler;

import org.json.simple.parser.ParseException;

public class ConfigHandler
{
	@Deprecated
	public static boolean checkForUpdate = false;
	public static boolean multiWorldAccountPages;
	public static Configuration config;
	public static double transactionCost;
	public static File bankDirectory;
	public static int pageLimit;
	public static List<ItemStack> blacklist;
	public static List<ItemStack> restricted;
	public static List<ItemStack> whitelist;
	static public String format;
	
	public static void init(File configDir)
	{
		if (config == null)
		{
			bankDirectory = new File(configDir, "banks");
			bankDirectory.mkdirs();
			config = new Configuration(new File(configDir, ModInfo.ID + ".cfg"));
			loadItemList(new File(configDir, "itemList.json"));
		}
	}
	
	private static void loadItemList(File itemListFile)
	{
		if (!itemListFile.exists())
		{
			try
			{
				itemListFile.createNewFile();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		try
		{
			ForgeJSONConfig itemLists = ForgeJSONConfig.loadForgeJSONConfig(itemListFile);
			blacklist = itemLists.getItems("blacklist");
			restricted = itemLists.getItems("restricted");
			whitelist = itemLists.getItems("whitelist");
		}
		catch (IOException | ParseException e)
		{
			ItemBank.logger.warn("An error occurred while reading the item lists.");
			blacklist = new ArrayList<ItemStack>();
			restricted = new ArrayList<ItemStack>();
			whitelist = new ArrayList<ItemStack>();
		}
		
		//TODO update checker
		format = config.getString("format", Configuration.CATEGORY_GENERAL, "nbt", "The save format account storage (nbt or json)", new String[]{"nbt", "json"});
		multiWorldAccountPages = config.getBoolean("multiWorld", Configuration.CATEGORY_GENERAL, false, "Have per-dimension account storage?");
		pageLimit = config.getInt("pageLimit", Configuration.CATEGORY_GENERAL, 0, 0, Integer.MAX_VALUE,
				"The number of pages players have available; If multiWorld is set to true, this number is applied per-dimension; Set to 0 for unlimited pages.");
		
		if (config.hasChanged())
			config.save();
	}
	
	public static ItemStack getRestrictedItem(ItemStack item)
	{
		for (ItemStack itemStack : restricted)
			if (itemStack.getItem() == item.getItem() && itemStack.getItemDamage() == item.getItemDamage())
				return itemStack;
		
		return null;
	}
	
	public static boolean isItemBlacklisted(ItemStack item)
	{
		return isInList(item, restricted);
	}
	
	public static boolean isItemRestricted(ItemStack item)
	{
		return isInList(item, restricted);
	}
	
	public static boolean isItemWhitelisted(ItemStack item)
	{
		return isInList(item, whitelist);
	}
	
	private static boolean isInList(ItemStack item, List<ItemStack> items)
	{
		for (ItemStack stack : items)
			if (stack.getItem() == item.getItem() && stack.getItemDamage() == item.getItemDamage())
				return true;
		
		return false;
	}
	
	@EventHandler
	public void onConfigurationChagnedEvent(OnConfigChangedEvent event)
	{
		//TODO need to finish this
	}
}

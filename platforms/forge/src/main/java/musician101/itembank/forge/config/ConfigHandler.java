package musician101.itembank.forge.config;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
	public static boolean isWhitelist;
	public static Configuration config;
	public static double transactionCost;
	public static File bankDirectory;
	public static int pageLimit;
	public static List<ItemStack> itemList;
	static public String format;
	
	public static void init(File configDir)
	{
		if (config == null)
		{
			bankDirectory = new File(configDir, "banks");
			bankDirectory.mkdirs();
			config = new Configuration(new File(configDir, ModInfo.ID + ".cfg"));
			loadConfiguration(new File(configDir, "itemList.json"));
		}
	}
	
	private static void loadConfiguration(File itemListFile)
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
			itemList = ForgeJSONConfig.loadForgeJSONConfig(itemListFile).getItems("items");
		}
		catch (IOException | ParseException e)
		{
			e.printStackTrace();
		}
		
		//TODO update checker
		format = config.getString("format", Configuration.CATEGORY_GENERAL, "nbt", "The save format account storage (nbt or json)", new String[]{"nbt", "json"});
		isWhitelist = config.getBoolean("isWhitelist", Configuration.CATEGORY_GENERAL, true, "Treat the item list as a whitelist (true) or a blacklist (false)");
		multiWorldAccountPages = config.getBoolean("multiWorld", Configuration.CATEGORY_GENERAL, false, "Have per-dimension account storage?");
		pageLimit = config.getInt("pageLimit", Configuration.CATEGORY_GENERAL, 0, 0, Integer.MAX_VALUE,
				"The number of pages players have available; If multiWorld is set to true, this number is applied per-dimension; Set to 0 for unlimited pages.");
		
		if (config.hasChanged())
			config.save();
	}
	
	public ItemStack getItem(ItemStack item)
	{
		for (ItemStack itemStack : itemList)
			if (itemStack.getItem() == item.getItem() && itemStack.getItemDamage() == item.getItemDamage())
				return itemStack;
		
		return null;
	}
	
	public boolean isItemRestricted(ItemStack item)
	{
		for (ItemStack itemStack : itemList)
			if (itemStack.getItem() == item.getItem() && itemStack.getItemDamage() == item.getItemDamage())
				return true;
		
		return false;
	}
	
	@EventHandler
	public void onConfigurationChagnedEvent(OnConfigChangedEvent event)
	{
		
	}
}

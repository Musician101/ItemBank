package musician101.sponge.itembank.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import musician101.sponge.itembank.ItemBank;
import musician101.sponge.itembank.lib.Reference.Constants;
import musician101.sponge.itembank.lib.Reference.Messages;

public class Config
{
	public Map<String, Integer> itemlist = new HashMap<String, Integer>();
	public boolean isWhitelist;
	public String database = "";
	public boolean enableVault;
	public String format = "";
	public String multiWorld = "";
	public int pageLimit;
	public double transactionCost;
	public boolean updateCheck;
	public boolean useMYSQL;
	
	public Config()
	{
		//TODO left pretty much as is until there's official config support
		File config = new File(ItemBank.getDataFolder(), "config.yml");
		File langFile = new File(ItemBank.getDataFolder(), "lang.yml");
		
		if (!config.exists())
		{
			//plugin.saveDefaultConfig();
		}
		
		if (!langFile.exists())
		{
			if (!langFile.getParentFile().mkdirs())
				ItemBank.getLogger().warn("Error: Could not create lang.yml directory.");
			
			//plugin.saveResource("lang.yml", false);
		}
		
		reloadConfiguration();
	}
	
	@SuppressWarnings({ "unused", "null" })
	public void reloadConfiguration()
	{
		//plugin.reloadConfig();
		final YamlConfiguration config = null;//plugin.getConfig();
		isWhitelist = config.getBoolean("whitelist", false);
		enableVault = config.getBoolean(Constants.ENABLE_VAULT, false);
		format = config.getString(Constants.FORMAT, "yml").toLowerCase();
		multiWorld = config.getString(Constants.MULTI_WORLD, "none").toLowerCase();
		pageLimit = config.getInt(Constants.PAGE_LIMIT, 0);
		transactionCost = config.getDouble(Constants.TRANSACTION_COST, 5);
		updateCheck = config.getBoolean(Constants.UPDATE_CHECK, true);
		
		for (Entry<String, Object> material : config.getConfigurationSection(Constants.ITEMLIST).getValues(true).entrySet())
		{
			/*if (!(material.getValue() instanceof MemorySection))
				itemlist.put(material.getKey(), (Integer) material.getValue());*/
		}

		try
		{
			Messages.init(config.getString(Constants.LANG, "en"), new File(ItemBank.getDataFolder(), "lang.yml"));
		}
		catch (FileNotFoundException e)
		{
			ItemBank.getLogger().warn("Error loading lang.yml (File not found).");
		}
		catch (IOException e)
		{
			ItemBank.getLogger().warn("Error loading lang.yml (Internal Error).");
		}
	}
}

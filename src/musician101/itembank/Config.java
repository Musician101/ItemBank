package musician101.itembank;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import musician101.itembank.lib.Constants;

import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;

public class Config
{
	ItemBank plugin;
	public Map<String, Integer> blacklist = new HashMap<String, Integer>();
	public boolean enableVault;
	//public YamlConfiguration lang = new YamlConfiguration();
	public int pageLimit;
	public double transactionCost;
	public boolean updateCheck;
	
	public Config(ItemBank plugin)
	{
		this.plugin = plugin;
		plugin.playerData = new File(plugin.getDataFolder(), "PlayerData");
		//plugin.langFolder = new File(plugin.getDataFolder(), "Languages");
		File config = new File(plugin.getDataFolder(), "config.yml");
		
		if (!config.exists())
		{
			if (!config.getParentFile().mkdirs())
				plugin.getLogger().warning("Error: Could not create config.yml directory.");
			
			plugin.saveDefaultConfig();
		}
		
		if (!plugin.playerData.exists())
		{
			if (!plugin.playerData.mkdirs())
				plugin.getLogger().warning("Error: Could not create PlayerData folder.");
			
			plugin.playerData.mkdirs();
		}
		
		/*if (!plugin.langFolder.exists())
		{
			if (!plugin.langFolder.mkdirs())
				plugin.getLogger().warning("Error: Could not create Languages folder.");
			
			//TODO test if testing as boolean still creates folders (it should but gotta check)
			//plugin
		}*/
		
		reloadConfiguration();
	}
	
	public void reloadConfiguration()
	{
		plugin.reloadConfig();
		final FileConfiguration config = plugin.getConfig();
		enableVault = config.getBoolean(Constants.ENABLE_VAULT, false);
		pageLimit = config.getInt(Constants.PAGE_LIMIT, 0);
		transactionCost = config.getDouble(Constants.TRANSACTION_COST, 5);
		updateCheck = config.getBoolean(Constants.UPDATE_CHECK, true);
		
		for (Entry<String, Object> material : config.getConfigurationSection(Constants.BLACKLIST).getValues(true).entrySet())
		{
			if (!(material.getValue() instanceof MemorySection))
				blacklist.put(material.getKey(), (Integer) material.getValue());
		}
		
		//lang.load(new File("", config.getString(Constants.LANG, "en")));
	}
}

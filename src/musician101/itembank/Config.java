package musician101.itembank;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import musician101.itembank.lib.Constants;
import musician101.itembank.lib.Messages;

import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;

import code.husky.mysql.MySQL;

public class Config
{
	ItemBank plugin;
	public Map<String, Integer> blacklist = new HashMap<String, Integer>();
	public boolean enableVault;
	public int pageLimit;
	public double transactionCost;
	public boolean updateCheck;
	public boolean useMYSQL;
	public String database = "";
	
	public Config(ItemBank plugin)
	{
		this.plugin = plugin;
		plugin.playerData = new File(plugin.getDataFolder(), "PlayerData");
		File config = new File(plugin.getDataFolder(), "config.yml");
		File langFile = new File(plugin.getDataFolder(), "lang.yml");
		
		if (!config.exists())
		{
			if (!config.getParentFile().mkdirs())
				plugin.getLogger().warning("Error: Could not create config.yml directory.");
			
			plugin.saveDefaultConfig();
		}
		
		if (!langFile.exists())
		{
			if (!langFile.getParentFile().mkdirs())
				plugin.getLogger().warning("Error: Could not create lang.yml directory.");
			
			plugin.saveResource("lang.yml", false);
		}
		
		if (!plugin.playerData.exists())
		{
			if (!plugin.playerData.mkdirs())
				plugin.getLogger().warning("Error: Could not create PlayerData folder.");
			
			plugin.playerData.mkdirs();
		}
		
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
		
		useMYSQL = config.getBoolean(Constants.ENABLE, false);
		if (useMYSQL)
		{
			database = config.getString(Constants.DATABASE);
			plugin.mysql = new MySQL(plugin, config.getString(Constants.HOST), config.getString(Constants.PORT), database, config.getString(Constants.USER), config.getString(Constants.PASS));
			plugin.c = plugin.mysql.openConnection();
		}

		Messages.init(config.getString(Constants.LANG, "en"), new File(plugin.getDataFolder(), "lang.yml"));
	}
}

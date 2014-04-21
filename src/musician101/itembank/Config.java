package musician101.itembank;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import musician101.itembank.lib.Constants;
import musician101.itembank.lib.Messages;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import code.husky.mysql.MySQL;

public class Config
{
	ItemBank plugin;
	public Map<String, Integer> blacklist = new HashMap<String, Integer>();
	public String database = "";
	public boolean enableVault;
	public String format = "";
	public String multiWorld = "";
	public int pageLimit;
	public Map<String, String> uuids = new HashMap<String, String>();
	public double transactionCost;
	public boolean updateCheck;
	public boolean useMYSQL;
	
	public Config(ItemBank plugin)
	{
		this.plugin = plugin;
		plugin.playerData = new File(plugin.getDataFolder(), "PlayerData");
		File config = new File(plugin.getDataFolder(), "config.yml");
		File langFile = new File(plugin.getDataFolder(), "lang.yml");
		File players = new File(plugin.getDataFolder(), "players.yml");
		
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
		
		if (!players.exists())
		{
			if (!players.getParentFile().mkdirs())
				plugin.getLogger().warning("Error: Could not create players.yml directory.");
			
			plugin.saveResource("players.yml", false);
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
		format = config.getString(Constants.FORMAT, "yml").toLowerCase();
		multiWorld = config.getString(Constants.MULTI_WORLD, "none").toLowerCase();
		pageLimit = config.getInt(Constants.PAGE_LIMIT, 0);
		transactionCost = config.getDouble(Constants.TRANSACTION_COST, 5);
		updateCheck = config.getBoolean(Constants.UPDATE_CHECK, true);
		
		for (Entry<String, Object> material : config.getConfigurationSection(Constants.BLACKLIST).getValues(true).entrySet())
		{
			if (!(material.getValue() instanceof MemorySection))
				blacklist.put(material.getKey(), (Integer) material.getValue());
		}
		
		YamlConfiguration players = new YamlConfiguration();
		try
		{
			players.load(new File(plugin.getDataFolder(), "players.yml"));
		}
		catch (IOException | InvalidConfigurationException e)
		{
			plugin.getLogger().warning("Error loading players.yml.");
		}
		
		for (Entry<String, Object> player : players.getValues(true).entrySet())
			uuids.put(player.getKey(), player.getValue().toString());
		
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

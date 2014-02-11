package musician101.itembank;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import musician101.itembank.lib.ConfigConstants;
import musician101.itembank.lib.Messages;
import musician101.itembank.opencsv.CSVReader;
import musician101.itembank.util.ItemTranslator;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Config related loading for the plugin.
 * 
 * @author Musician101
 */
public class Config
{
	private ItemBank plugin;
	public boolean checkForUpdate;
	public ConfigurationSection blacklist;
	public boolean enableVault;
	public double transactionCost;
	
	/**
	 * Config constructor.
	 * 
	 * @param plugin Reference's the plugin's main class.
	 */
	public Config(ItemBank plugin)
	{
		this.plugin = plugin;
		File config = new File(plugin.getDataFolder(), "config.yml");
		File items = new File(plugin.getDataFolder(), "items.csv");
		if (!config.exists())
		{
			if (!config.getParentFile().mkdirs()) plugin.getLogger().warning("Could not create config.yml directory.");
			plugin.saveDefaultConfig();
		}
		
		if (!items.exists())
		{
			if (!items.getParentFile().mkdirs()) plugin.getLogger().warning("Could not create items.csv directory.");
			plugin.saveResource("items.csv", false);
		}
		
		if (!plugin.playerDataDir.exists())
		{
			if (!plugin.playerDataDir.mkdirs()) plugin.getLogger().warning("Could not create PlayerData directory.");
			plugin.playerDataDir.mkdirs();
		}
		
		reloadConfiguration();
	}
	
	/** Reloads the server's configuration file and items.csv. */
	public void reloadConfiguration()
	{
		plugin.reloadConfig();
		final FileConfiguration config = plugin.getConfig();
		
		if (!config.isSet(ConfigConstants.CHECK_FOR_UPDATE))
		{
			config.set(ConfigConstants.CHECK_FOR_UPDATE, true);
			plugin.getLogger().info("Config: Missing option '" + ConfigConstants.CHECK_FOR_UPDATE + "' added. Value set to 'true'.");
		}
		
		if (!config.isSet(ConfigConstants.BLACKLIST))
		{
			config.set(ConfigConstants.BLACKLIST + ".bedrock.0", 0);
			plugin.getLogger().info("Config: Missing option '" + ConfigConstants.BLACKLIST + "' added. Value set to 'bedrock.0: 0'.");
		}
		
		boolean match = false;
		for (Material m : Material.values())
		{
			for (short data = 0; data > m.getMaxDurability(); data++)
				if (!config.isSet(ConfigConstants.BLACKLIST + "." + m.toString().toLowerCase() + "." + data))
					match = true;
		}
		
		if (!match)
		{
			config.set(ConfigConstants.BLACKLIST + ".bedrock.0", 0);
			plugin.getLogger().info("Config: Incomplete " + ConfigConstants.BLACKLIST + ". 'bedrock.0: 0' has been added.");
		}
		
		if (!config.isSet(ConfigConstants.ENABLE_VAULT))
		{
			config.set(ConfigConstants.ENABLE_VAULT, true);
			plugin.getLogger().info("Config: Missing option '" + ConfigConstants.ENABLE_VAULT + "' added. Value set to 'true'.");
		}
		
		if (!config.isSet(ConfigConstants.TRANSACTION_COST))
		{
			config.set(ConfigConstants.TRANSACTION_COST, 5.0);
			plugin.getLogger().info("Config: Missing option '" + ConfigConstants.TRANSACTION_COST + "' added. Value set to 5.0");
		}
		
		plugin.saveConfig();
		plugin.reloadConfig();
		
		checkForUpdate = config.getBoolean(ConfigConstants.CHECK_FOR_UPDATE, true);
		blacklist = config.getConfigurationSection(ConfigConstants.BLACKLIST);
		enableVault = config.getBoolean(ConfigConstants.ENABLE_VAULT, true);
		transactionCost = config.getDouble(ConfigConstants.TRANSACTION_COST, 5);
		
		try
		{
			plugin.translator = new ItemTranslator(plugin, new CSVReader(new FileReader(new File(plugin.getDataFolder() + "/items.csv"))).readAll());
		}
		catch (IOException e)
		{
			plugin.getLogger().warning(Messages.IO_EXCEPTION);
		}
	}
}

package musician101.itembank;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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
		
		if (!config.isSet("checkForUpdate"))
		{
			config.set("checkForUpdate", true);
			plugin.getLogger().info("Config: Missing option 'checkForUpdate' added. Value set to 'true'.");
		}
		
		if (!config.isSet("blacklist"))
		{
			config.set("blacklist.bedrock.0", 0);
			plugin.getLogger().info("Config: Missing option 'blacklist' added. Value set to 'bedrock.0: 0'.");
		}
		
		boolean match = false;
		for (Material m : Material.values())
		{
			for (short data = 0; data > m.getMaxDurability(); data++)
				if (!config.isSet("blacklist." + m.toString().toLowerCase() + "." + data))
					match = true;
		}
		
		if (!match)
		{
			config.set("blacklist.bedrock.0", 0);
			plugin.getLogger().info("Config: Incomplete blacklist. 'bedrock.0: 0' has been added.");
		}
		
		if (!config.isSet("enableVault"))
		{
			config.set("enableVault", true);
			plugin.getLogger().info("Config: Missing option 'enableVault' added. Value set to 'true'.");
		}
		
		if (!config.isSet("transactionCost"))
		{
			config.set("transactionCost", 5.0);
			plugin.getLogger().info("Config: Missing option 'TransactionCost' added. Value set to 5.0");
		}
		
		plugin.saveConfig();
		plugin.reloadConfig();
		
		checkForUpdate = config.getBoolean("checkForUpdate", true);
		blacklist = config.getConfigurationSection("blacklist");
		enableVault = config.getBoolean("enableVault", true);
		transactionCost = config.getDouble("transactionCost", 5);
		
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

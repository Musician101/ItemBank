package musician101.itembank;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import musician101.itembank.lib.Constants;
import musician101.itembank.opencsv.CSVReader;
import musician101.itembank.util.ItemTranslator;

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
	public static boolean checkForUpdate;
	public static ConfigurationSection blacklist;
	
	/**
	 * Config constructor.
	 * 
	 * @param plugin Reference's the plugin's main class.
	 */
	public Config(ItemBank plugin)
	{
		this.plugin = plugin;
		File config = new File(plugin.getDataFolder(), "config.yml");
		if (!config.exists())
		{
			if (!config.getParentFile().mkdirs()) plugin.getLogger().warning("Could not create config.yml directory.");
			plugin.saveDefaultConfig();
		}
		reloadConfiguration();
	}
	
	/**
	 * Reloads the server's configuration file and items.csv.
	 */
	public void reloadConfiguration()
	{
		plugin.reloadConfig();
		final FileConfiguration config = plugin.getConfig();
		
		checkForUpdate = config.getBoolean("checkForUpdate", true);
		blacklist = config.getConfigurationSection("blacklist");
		
		try
		{
			plugin.translator = new ItemTranslator(plugin, new CSVReader(new FileReader(new File(plugin.getDataFolder() + "/items.csv"))).readAll());
		}
		catch (IOException e)
		{
			plugin.getLogger().warning(Constants.IO_EXCEPTION);
		}
	}
}

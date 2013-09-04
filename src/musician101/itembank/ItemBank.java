package musician101.itembank;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import musician101.itembank.commands.IBCommand;
import musician101.itembank.listeners.PlayerListener;
import musician101.itembank.util.UpdateChecker;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The plugin's main class.
 * 
 * @author Musician101
 */
public class ItemBank extends JavaPlugin
{
	protected UpdateChecker updateChecker;
	public List<String> blacklist;
	public static File dir = new File("plugins/ItemBank/PlayerData");
	File configFile;
	FileConfiguration config;
	public File playerFile;
	public FileConfiguration playerData;
	File itemsCSV = new File(getDataFolder() + "/items.csv");
	
	/** Initializes the plugin, checks for the config, and register commands and listeners. */
	public void onEnable()
	{
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		
		// TODO: Change commands so they don't rely on the base command
		getCommand("itembank").setExecutor(new IBCommand(this));
		
		configFile = new File(getDataFolder(), "config.yml");
		saveDefaultConfig();
		config = new YamlConfiguration();
		try
		{
			config.load(configFile);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		/* TODO: Implement items.csv for blocks with multiple names.
		if (!itemsCSV.exists())
		{
			createCSV();
		}*/
		
		if (!dir.exists())
		{
			dir.mkdirs();
		}
		
		Player[] players = Bukkit.getOnlinePlayers();
		if (players.length > 0)
		{
			for (Player player : players)
			{
				playerFile = new File(getDataFolder() + "/PlayerData/" + player.getName() + ".yml");
				if (!playerFile.exists())
				{
					try
					{
						FileWriter fw;
						playerFile.createNewFile();
						fw = new FileWriter(playerFile.getAbsoluteFile());
						BufferedWriter bw = new BufferedWriter(fw);
						bw.write(PlayerListener.template);
						bw.close();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		
		if (config.getString("blacklist") != null)
		{
			blacklist = config.getStringList("blacklist");
		}
		
		if (config.getBoolean("checkForUpdate") == true)
		{
			this.updateChecker = new UpdateChecker(this, "http://dev.bukkit.org/bukkit-plugins/item_bank/files.rss");
			if (this.updateChecker.updateNeeded())
			{
				getLogger().info("Update checker is enabled.");
				getLogger().info("A new version is available: " + this.updateChecker.getVersion());
				getLogger().info("Get it from: " + this.updateChecker.getLink());
			}
			else
			{
				getLogger().info("ItemBank is up to date.");
			}
		}
		else if (config.getBoolean("checkForUpdate") == false)
		{
			getLogger().info("Update checker is not enabled.");
		}
	}
	
	/** Shuts off the plugin */
	public void onDisable()
	{
		getLogger().info("Shutting down.");
	}
	
	/** @see onEnabled() */
	/*public void createCSV()
	{
		InputStream stream = getClass().getResourceAsStream("/items.csv");
		FileOutputStream resStreamOut;
		int readBytes = 0;
		byte[] buffer = new byte[4096];
		try
		{
			resStreamOut = new FileOutputStream(itemsCSV);
			while ((readBytes = stream.read(buffer)) > 0)
			{
				resStreamOut.write(buffer, 0, readBytes);
			}
			resStreamOut.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}*/
}

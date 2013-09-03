package musician101.itembank;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import musician101.itembank.commands.IBCommandExecutor;
import musician101.itembank.listeners.PlayerListener;
import musician101.itembank.util.UpdateChecker;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

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
	
	public void onEnable()
	{
		// Get listener for player login to create file if it doesnt exists
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		
		// Set up CommandExecutor
		getCommand("itembank").setExecutor(new IBCommandExecutor(this));
		
		// Create config if it doesn't exist and load it
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
		
		if (!itemsCSV.exists())
		{
			createCSV();
		}
		
		// Create the PlayerData folder if it doesnt exist
		if (!dir.exists())
		{
			dir.mkdirs();
		}
		
		// Create files for players if they don't have one already
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
		
		// Get a list of blocks/items blacklisted in the config
		if (config.getString("blacklist") != null)
		{
			blacklist = config.getStringList("blacklist");
		}
		
		//check for update
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
	
	public void onDisable()
	{
		getLogger().info("Shutting down.");
	}
	
	public void createCSV()
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
	}
}

package musician101.itembank;

import java.io.File;

import musician101.itembank.commands.DepositCommand;
import musician101.itembank.commands.IBCommand;
import musician101.itembank.commands.WithdrawCommand;
import musician101.itembank.listeners.PlayerListener;
import musician101.itembank.util.IBUtils;
import musician101.itembank.util.ItemTranslator;
import musician101.itembank.util.UpdateChecker;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The plugin's main class.
 * 
 * @author Musician101
 */
public class ItemBank extends JavaPlugin
{
	protected UpdateChecker updateChecker;
	public ItemTranslator translator;
	public File playerDataDir;
	public File playerFile;
	public FileConfiguration playerData;
	public Config config;
	
	/** Loads the plugin's various configurations and reference files/folders. */
	public void loadConfiguration()
	{
		if (!new File(getDataFolder(), "config.yml").exists()) saveDefaultConfig();
		if (!new File(getDataFolder(), "items.csv").exists()) saveResource("items.csv", false);
		if (!playerDataDir.exists()) playerDataDir.mkdirs();
	}
	
	/** Checks if a new version is available. */
	public void versionCheck()
	{
		if (Config.checkForUpdate)
		{
			updateChecker = new UpdateChecker(this, "http://dev.bukkit.org/bukkit-plugins/item_bank/files.rss");
			getLogger().info("Update checker is enabled.");
			if (updateChecker.updateNeeded())
			{
				getLogger().info("A new version is available: " + updateChecker.getVersion());
				getLogger().info("Get it from: " + updateChecker.getLink());
			}
			else
				getLogger().info("ItemBank is up to date.");
		}
		else if (!Config.checkForUpdate)
			getLogger().info("Update checker is not enabled.");
	}
	
	/** Initializes the plugin, checks for the config, and register commands and listeners. */
	public void onEnable()
	{
		playerDataDir = new File(getDataFolder() + "/PlayerData");
		
		loadConfiguration();
		config = new Config(this);
		
		IBUtils.createPlayerFiles(this, Bukkit.getOnlinePlayers());
		
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		
		getCommand("deposit").setExecutor(new DepositCommand(this));
		getCommand("itembank").setExecutor(new IBCommand(this));
		getCommand("withdraw").setExecutor(new WithdrawCommand(this));
		
		versionCheck();
	}
	
	/** Shuts off the plugin */
	public void onDisable()
	{
		getLogger().info("Shutting down.");
	}
}

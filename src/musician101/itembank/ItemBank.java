package musician101.itembank;

import java.io.File;

import musician101.itembank.commands.DepositCommand;
import musician101.itembank.commands.IBCommand;
import musician101.itembank.commands.WithdrawCommand;
import musician101.itembank.listeners.PlayerListener;
import musician101.itembank.util.IBUtils;
import musician101.itembank.util.ItemTranslator;
import musician101.itembank.util.Update;

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
	public ItemTranslator translator;
	public File playerDataDir;
	public File playerFile;
	public FileConfiguration playerData;
	public Config config;
	//public static Economy econ = null;
	
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
		@SuppressWarnings("unused")
		Update update = null; 
		if (Config.checkForUpdate)
			update = new Update(59073, "72784c134bdbc3c2216591011a29df99fac08239");
	}
	
	/** Initializes the plugin, checks for the config, and register commands and listeners. */
	public void onEnable()
	{
		/*if (!setupEconomy())
			getLogger().info("Vault not deteceted. Disabling Economy support.");*/
		
		playerDataDir = new File(getDataFolder() + "/PlayerData");
		
		loadConfiguration();
		config = new Config(this);
		
		IBUtils.createPlayerFiles(this, Bukkit.getOnlinePlayers());
		
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		
		/*if (econ != null)
		{
			getCommand("deposit").setExecutor(new DepositCommand(this));
			getCommand("withdraw").setExecutor(new WithdrawCommand(this));
		}
		else
		{
			getCommand("deposit").setExecutor(new DepositCommand(this, econ));
			getCommand("withdraw").setExecutor(new WithdrawCommand(this));
		}*/
		getCommand("deposit").setExecutor(new DepositCommand(this));
		getCommand("withdraw").setExecutor(new WithdrawCommand(this));
		getCommand("itembank").setExecutor(new IBCommand(this));
		
		versionCheck();
	}
	
	/** Shuts off the plugin */
	public void onDisable()
	{
		getLogger().info("Shutting down.");
	}
	
	/** Vault set up (May not be implemented. 
	private boolean setupEconomy()
	{
		if (getServer().getPluginManager().getPlugin("Vault") == null)
			return false;
		
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null)
			return false;
		
		econ = rsp.getProvider();
		return econ != null;
	}*/
}

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
	public static Econ economy = null;
	
	/** Loads the plugin's various configurations and reference files/folders. */
	public void loadConfiguration()
	{
		if (!new File(getDataFolder(), "config.yml").exists()) saveDefaultConfig();
		if (!new File(getDataFolder(), "items.csv").exists()) saveResource("items.csv", false);
		if (!playerDataDir.exists()) playerDataDir.mkdirs();
	}
	
	/** Checks if a new version is available. */
	public void versionCheck(Config config)
	{
		@SuppressWarnings("unused")
		Update update = null; 
		if (config.checkForUpdate)
			update = new Update(59073, "72784c134bdbc3c2216591011a29df99fac08239");
	}
	
	/** Initializes the plugin, checks for the config, and register commands and listeners. */
	public void onEnable()
	{
		playerDataDir = new File(getDataFolder() + "/PlayerData");
		
		loadConfiguration();
		config = new Config(this);
		
		economy = new Econ();
		if (economy.isEnabled() && config.enableVault)
			getLogger().info("Vault detected and enabled in config. Using Vault for monetary transactions.");
		else if (!economy.isEnabled())
			getLogger().info("Error detecting Vault. Is it installed?");
		else if (!config.enableVault)
			getLogger().info("Vault detected but disabled in config. No monetary transactions will occur.");
		
		IBUtils.createPlayerFiles(this, Bukkit.getOnlinePlayers());
		
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		
		getCommand("deposit").setExecutor(new DepositCommand(this, config));
		getCommand("withdraw").setExecutor(new WithdrawCommand(this, config));
		getCommand("itembank").setExecutor(new IBCommand(this));
		
		versionCheck(config);
	}
	
	/** Shuts off the plugin */
	public void onDisable()
	{
		getLogger().info("Shutting down.");
	}
	
	/**
	 * Get economy related methods.
	 *
	 * @return
	 */
	public Econ getEconomy()
	{
		return economy;
	}
}

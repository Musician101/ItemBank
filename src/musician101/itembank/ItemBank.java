package musician101.itembank;

import java.io.File;

import musician101.itembank.commands.AccountCommand;
import musician101.itembank.commands.IBCommand;
import musician101.itembank.lib.Constants;
import musician101.itembank.listeners.InventoryListener;
import musician101.itembank.listeners.PlayerListener;
import musician101.itembank.util.IBUtils;
import musician101.itembank.util.Updater;
import musician101.itembank.util.Updater.UpdateResult;
import musician101.itembank.util.Updater.UpdateType;

import org.bukkit.plugin.java.JavaPlugin;

public class ItemBank extends JavaPlugin
{
	public Config config;
	public Econ economy = null;
	public File playerData;
	
	public void setupEconomy()
	{
		economy = new Econ();
		if (economy.isEnabled() && config.enableVault)
			getLogger().info("Vault detected and enabled in config. Using Vault for monetary transactions.");
		else if (!economy.isEnabled())
			getLogger().info("Error detecting Vault. Is it installed.");
		else if (!config.enableVault)
			getLogger().info("Vault detected but disabled in config. No monetary transactions will occur.");
	}
	
	public void versionCheck()
	{
		if (!config.updateCheck)
			getLogger().info("Update check is disabled.");
		else
		{
			Updater updater = new Updater(this, 59073, this.getFile(), UpdateType.NO_DOWNLOAD, true);
			if (updater.getResult() == UpdateResult.UPDATE_AVAILABLE)
				getLogger().info("A new version is available. " + updater.getLatestName());
			else if (updater.getResult() == UpdateResult.NO_UPDATE)
				getLogger().info("The current version is the latest. " + updater.getLatestName());
			else
				getLogger().info("Error: Update check failed.");
		}
	}
	
	public void onEnable()
	{
		config = new Config(this);
		versionCheck();
		setupEconomy();
		
		IBUtils.createPlayerFiles(this);
		
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
		
		getCommand(Constants.ACCOUNT_CMD).setExecutor(new AccountCommand(this));
		getCommand(Constants.ITEMBANK_CMD).setExecutor(new IBCommand(this));
	}
}

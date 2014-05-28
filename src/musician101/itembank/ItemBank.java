package musician101.itembank;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;

import musician101.itembank.commands.AccountCommand;
import musician101.itembank.commands.IBCommand;
import musician101.itembank.config.Config;
import musician101.itembank.lib.Constants;
import musician101.itembank.lib.Messages;
import musician101.itembank.listeners.InventoryListener;
import musician101.itembank.util.Updater;
import musician101.itembank.util.Updater.UpdateResult;
import musician101.itembank.util.Updater.UpdateType;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import code.husky.mysql.MySQL;

public class ItemBank extends JavaPlugin
{
	public Config config;
	public Economy econ = null;
	public File playerData;
	public MySQL mysql = null;
	public Connection c = null;
	
	private boolean setupEconomy()
	{
		if (config.enableVault == false)
			return false;
		
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null)
			return false;
		
		econ = rsp.getProvider();
		return econ != null;
	}
	
	private void versionCheck()
	{
		if (!config.updateCheck)
			getLogger().info("Update check is disabled.");
		else
		{
			Updater updater = new Updater(this, 59073, this.getFile(), UpdateType.NO_DOWNLOAD, true);
			if (updater.getResult() == UpdateResult.UPDATE_AVAILABLE)
				getLogger().info(Messages.UPDATER_NEW + " " + updater.getLatestName());
			else if (updater.getResult() == UpdateResult.NO_UPDATE)
				getLogger().info(Messages.UPDATER_CURRENT + " " + updater.getLatestName());
			else
				getLogger().info(Messages.UPDATER_ERROR);
		}
	}
	
	public void onEnable()
	{
		config = new Config(this);
		versionCheck();
		setupEconomy();
		
		getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
		
		getCommand(Constants.ACCOUNT_CMD).setExecutor(new AccountCommand(this));
		getCommand(Constants.ITEMBANK_CMD).setExecutor(new IBCommand(this));
	}
	
	public void onDisable()
	{
		try
		{
			config.uuids.saveUUIDs();
		}
		catch (FileNotFoundException e)
		{
			getLogger().warning("Error saving players.yml (File not found).");
		}
		catch (IOException e)
		{
			getLogger().warning("Error saving players.yml (Internal Error).");
		}
		catch (InvalidConfigurationException e)
		{
			getLogger().warning("Error saving players.yml (Incorrect YAML format).");
		}
		
		
		if (mysql != null)
			mysql.closeConnection();
	}
}

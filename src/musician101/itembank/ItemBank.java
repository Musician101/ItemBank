package musician101.itembank;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.Map.Entry;

import musician101.itembank.commands.AccountCommand;
import musician101.itembank.commands.IBCommand;
import musician101.itembank.lib.Constants;
import musician101.itembank.lib.Messages;
import musician101.itembank.listeners.InventoryListener;
import musician101.itembank.listeners.PlayerListener;
import musician101.itembank.util.IBUtils;
import musician101.itembank.util.Updater;
import musician101.itembank.util.Updater.UpdateResult;
import musician101.itembank.util.Updater.UpdateType;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import code.husky.mysql.MySQL;

public class ItemBank extends JavaPlugin
{
	public Config config;
	public Econ economy = null;
	public File playerData;
	public MySQL mysql = null;
	public Connection c = null;
	
	private void setupEconomy()
	{
		economy = new Econ();
		if (economy.isEnabled() && config.enableVault)
			getLogger().info(Messages.VAULT_BOTH_ENABLED);
		else if (!economy.isEnabled())
			getLogger().info(Messages.VAULT_NOT_INSTALLED);
		else if (!config.enableVault)
			getLogger().info(Messages.VAULT_NO_CONFIG);
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

		if (!config.useMYSQL)
			IBUtils.createPlayerFiles(this);
		
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
		
		getCommand(Constants.ACCOUNT_CMD).setExecutor(new AccountCommand(this));
		getCommand(Constants.ITEMBANK_CMD).setExecutor(new IBCommand(this));
	}
	
	public void onDisable()
	{
		File file = new File(getDataFolder(), "players.yml");
		YamlConfiguration players = new YamlConfiguration();
		try
		{
			players.load(file);
			for (Entry <String, String> player : config.uuids.entrySet())
				players.set(player.getKey(), player.getValue());
			
			players.save(file);
		}
		catch (IOException | InvalidConfigurationException e)
		{
			getLogger().warning("Error saving players.yml");
		}
		
		if (mysql != null)
			mysql.closeConnection();
	}
}

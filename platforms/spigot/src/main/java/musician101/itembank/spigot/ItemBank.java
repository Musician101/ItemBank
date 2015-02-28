package musician101.itembank.spigot;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import musician101.itembank.common.database.MySQLHandler;
import musician101.itembank.spigot.command.AbstractSpigotCommand;
import musician101.itembank.spigot.command.account.AccountCommand;
import musician101.itembank.spigot.command.itembank.IBCommand;
import musician101.itembank.spigot.config.PluginConfig;
import musician101.itembank.spigot.lib.Messages;
import musician101.itembank.spigot.util.Updater;
import musician101.itembank.spigot.util.Updater.UpdateResult;
import musician101.itembank.spigot.util.Updater.UpdateType;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemBank extends JavaPlugin
{ 
	Economy econ;
	List<AbstractSpigotCommand> commands;
	MySQLHandler mysql;
	PluginConfig config;
	
	private boolean setupEconomy()
	{
		if (config.enableVault() == false)
			return false;
		
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null)
			return false;
		
		econ = rsp.getProvider();
		return econ != null;
	}
	
	private void versionCheck()
	{
		if (!config.checkForUpdate())
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
	
	@Override
	public void onEnable()
	{
		config = new PluginConfig(this);
		versionCheck();
		setupEconomy();
		
		commands = Arrays.asList(new AccountCommand(this), new IBCommand(this));
	}
	
	@Override
	public void onDisable()
	{
		if (mysql != null)
		{
			try
			{
				mysql.closeConnection();
			}
			catch (SQLException e)
			{
				getLogger().warning(Messages.SQL_EX);
			}
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		for (AbstractSpigotCommand cmd : commands)
			if (command.getName().equalsIgnoreCase(cmd.getName()))
				return cmd.onCommand(sender, args);
		
		return false;
	}
	
	public PluginConfig getPluginConfig()
	{
		return config;
	}
	
	public Economy getEconomy()
	{
		return econ;
	}
	
	public MySQLHandler getMySQLHandler()
	{
		return mysql;
	}
	
	public void setMySQLHandler(MySQLHandler mysql)
	{
		this.mysql = mysql;
	}
	
	public List<AbstractSpigotCommand> getCommands()
	{
		return commands;
	}
}

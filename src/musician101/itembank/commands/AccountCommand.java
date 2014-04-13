package musician101.itembank.commands;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import musician101.itembank.ItemBank;
import musician101.itembank.lib.Constants;
import musician101.itembank.lib.Messages;
import musician101.itembank.util.IBUtils;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class AccountCommand implements CommandExecutor
{
	ItemBank plugin;
	
	public AccountCommand(ItemBank plugin)
	{
		this.plugin = plugin;
	}
	
	// sender.getName() and playerName are not always the same.
	public boolean openInv(CommandSender sender, String playerName, int page)
	{
		Inventory inv = null;
		try
		{
			inv = IBUtils.getAccount(plugin, playerName, page);
		}
		catch (FileNotFoundException e)
		{
			sender.sendMessage(Messages.NO_FILE_EX);
			return false;
		}
		catch (IOException e)
		{
			sender.sendMessage(Messages.IO_EX);
			return false;
		}
		catch (InvalidConfigurationException e)
		{
			sender.sendMessage(Messages.YAML_EX);
			return false;
		}
		catch (SQLException e)
		{
			sender.sendMessage(Messages.SQL_EX);
			return false;
		}
		
		if (sender.getName().equals(playerName) && plugin.economy.isEnabled() && plugin.config.enableVault)
		{
			//Player player = ((Player) sender).get
			
			if (plugin.economy.getMoney(sender.getName()) < plugin.config.transactionCost)
			{
				sender.sendMessage(Messages.ACCOUNT_TRANSACTION_FAIL);
				return false;
			}
			
			sender.sendMessage(Messages.ACCOUNT_ECON_SUCCESS.replace("$", "$" + plugin.config.transactionCost));
			plugin.economy.takeMoney(sender.getName(), plugin.config.transactionCost);
		}
		
		((Player) sender).openInventory(inv);
		return true;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{	
		if (!(sender instanceof Player))
		{
			sender.sendMessage(Messages.PLAYER_CMD);
			return false;
		}
		
		if (args.length > 0)
		{
			if (IBUtils.isNumber(args[0]))
				return openInv(sender, sender.getName(), Integer.valueOf(args[0]));
			
			if (!sender.hasPermission(Constants.ADMIN_ACCOUNT_PERM))
			{
				sender.sendMessage(Messages.NO_PERMISSION);
				return false;
			}
			
			OfflinePlayer player = plugin.getServer().getPlayer(args[0]);
			if (player == null)
				player = plugin.getServer().getOfflinePlayer(args[0]);
			
			if (args.length > 1)
				if (IBUtils.isNumber(args[1]))
					return openInv(sender, player.getName(), Integer.valueOf(args[1]));
			
			return openInv(sender, player.getName(), 1);
		}
		
		if (!sender.hasPermission(Constants.ACCOUNT_PERM))
		{
			sender.sendMessage(Messages.NO_PERMISSION);
			return false;
		}
		
		return openInv(sender, sender.getName(), 1);
	}
}

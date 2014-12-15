package musician101.itembank.command.account;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import musician101.itembank.ItemBank;
import musician101.itembank.lib.Constants;
import musician101.itembank.lib.Messages;
import musician101.itembank.util.IBUtils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.json.simple.parser.ParseException;

public class AccountCommand implements CommandExecutor
{
	ItemBank plugin;
	
	public AccountCommand(ItemBank plugin)
	{
		this.plugin = plugin;
	}
	
	// sender.getName() and playerName are not always the same.
	public boolean openInv(CommandSender sender, String worldName, String uuid, int page)
	{
		Inventory inv = null;
		try
		{
			inv = IBUtils.getAccount(plugin, worldName, uuid, page);
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
		catch (InvalidConfigurationException | ParseException e)
		{
			sender.sendMessage(Messages.YAML_PARSE_EX);
			return false;
		}
		catch (SQLException e)
		{
			sender.sendMessage(Messages.SQL_EX);
			return false;
		}
		
		OfflinePlayer player = plugin.getServer().getOfflinePlayer(((Player) sender).getUniqueId());
		if (player.getUniqueId().toString().equals(uuid) && plugin.econ != null && plugin.config.enableVault)
		{
			if (plugin.econ != null && !plugin.econ.withdrawPlayer(player, plugin.config.transactionCost).transactionSuccess())
			{
				sender.sendMessage(Messages.ACCOUNT_TRANSACTION_FAIL);
				return false;
			}
			
			sender.sendMessage(Messages.ACCOUNT_ECON_SUCCESS.replace("$", "$" + plugin.config.transactionCost));
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
			{
				if (!sender.hasPermission(Constants.ACCOUNT_PERM))
				{
					sender.sendMessage(Messages.NO_PERMISSION);
					return false;
				}
				
				return openInv(sender, IBUtils.getWorldName(plugin, (Player) sender), ((Player) sender).getUniqueId().toString(), Integer.valueOf(args[0]));
			}
			
			if (!sender.hasPermission(Constants.ADMIN_ACCOUNT_PERM))
			{
				sender.sendMessage(Messages.NO_PERMISSION);
				return false;
			}
			
			OfflinePlayer player = plugin.config.uuids.getPlayer(args[0]);
			if (player == null)
				player = plugin.config.uuids.getOfflinePlayer(args[0]);
			
			try
			{
				if (args.length > 1)
				{
					if (IBUtils.isNumber(args[1]))
						return openInv(sender, Bukkit.getWorlds().get(0).getName(), player.getUniqueId().toString(), Integer.valueOf(args[1]));
					
					return openInv(sender, args[0], player.getUniqueId().toString(), Integer.valueOf(args[2]));
				}
				
				return openInv(sender, Bukkit.getWorlds().get(0).getName(), player.getUniqueId().toString(), 1);
			}
			catch (NullPointerException e)
			{
				sender.sendMessage(Messages.PLAYER_DNE);
				return false;
			}
		}
		
		if (!sender.hasPermission(Constants.ACCOUNT_PERM))
		{
			sender.sendMessage(Messages.NO_PERMISSION);
			return false;
		}
		
		return openInv(sender, IBUtils.getWorldName(plugin, (Player) sender), ((Player) sender).getUniqueId().toString(), 1);
	}
}

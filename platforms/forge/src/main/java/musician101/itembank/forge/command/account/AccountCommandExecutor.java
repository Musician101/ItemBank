package musician101.itembank.forge.command.account;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

import musician101.itembank.forge.ItemBank;
import musician101.itembank.forge.lib.Constants;
import musician101.itembank.forge.lib.Messages;
import musician101.itembank.forge.util.IBUtils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.json.simple.parser.ParseException;

import com.evilmidget38.UUIDFetcher;

public class AccountCommandExecutor implements CommandExecutor
{
	ItemBank plugin;
	
	public AccountCommandExecutor(ItemBank plugin)
	{
		this.plugin = plugin;
	}
	
	// sender.getName() and playerName are not always the same.
	public boolean openInv(Player player, String worldName, UUID uuid, int page)
	{
		Inventory inv = null;
		try
		{
			inv = IBUtils.getAccount(plugin, worldName, uuid, page);
		}
		catch (FileNotFoundException e)
		{
			player.sendMessage(Messages.NO_FILE_EX);
			return false;
		}
		catch (IOException e)
		{
			player.sendMessage(Messages.IO_EX);
			return false;
		}
		catch (InvalidConfigurationException | ParseException e)
		{
			player.sendMessage(Messages.YAML_PARSE_EX);
			return false;
		}
		catch (ClassNotFoundException | SQLException e)
		{
			player.sendMessage(Messages.SQL_EX);
			return false;
		}
		
		OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(((Player) player).getUniqueId());
		if (offlinePlayer.getUniqueId().toString().equals(uuid) && plugin.getEconomy() != null && plugin.getPluginConfig().enableVault())
		{
			if (plugin.getEconomy() != null && !plugin.getEconomy().withdrawPlayer(offlinePlayer, plugin.getPluginConfig().getTransactionCost()).transactionSuccess())
			{
				player.sendMessage(Messages.ACCOUNT_TRANSACTION_FAIL);
				return false;
			}
			
			player.sendMessage(Messages.ACCOUNT_ECON_SUCCESS.replace("$", "$" + plugin.getPluginConfig().getTransactionCost()));
		}
		
		player.openInventory(inv);
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
		
		Player player = (Player) sender;
		if (args.length > 0)
		{
			if (IBUtils.isNumber(args[0]))
			{
				if (!player.hasPermission(Constants.ACCOUNT_PERM))
				{
					player.sendMessage(Messages.NO_PERMISSION);
					return false;
				}
				
				return openInv(player, IBUtils.getWorldName(plugin, player), player.getUniqueId(), Integer.valueOf(args[0]));
			}
			
			if (!player.hasPermission(Constants.ADMIN_ACCOUNT_PERM))
			{
				player.sendMessage(Messages.NO_PERMISSION);
				return false;
			}
			
			UUID inventoryOwnerId = null;
			try
			{
				inventoryOwnerId = UUIDFetcher.getUUIDOf(args[0]);
			}
			catch (Exception e)
			{
				player.sendMessage(Messages.UNKNOWN_EX);
				return false;
			}
			
			try
			{
				if (args.length > 1)
				{
					if (IBUtils.isNumber(args[1]))
						return openInv(player, Bukkit.getWorlds().get(0).getName(), inventoryOwnerId, Integer.valueOf(args[1]));
					
					return openInv(player, args[0], inventoryOwnerId, Integer.valueOf(args[2]));
				}
				
				return openInv(player, Bukkit.getWorlds().get(0).getName(), inventoryOwnerId, 1);
			}
			catch (NullPointerException e)
			{
				player.sendMessage(Messages.PLAYER_DNE);
				return false;
			}
		}
		
		if (!player.hasPermission(Constants.ACCOUNT_PERM))
		{
			player.sendMessage(Messages.NO_PERMISSION);
			return false;
		}
		
		return openInv(player, IBUtils.getWorldName(plugin, player), player.getUniqueId(), 1);
	}
}

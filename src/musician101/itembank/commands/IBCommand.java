package musician101.itembank.commands;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map.Entry;
import java.util.UUID;

import musician101.itembank.ItemBank;
import musician101.itembank.lib.Constants;
import musician101.itembank.lib.Messages;
import musician101.itembank.util.IBUtils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class IBCommand implements CommandExecutor
{
	ItemBank plugin;
	
	public IBCommand(ItemBank plugin)
	{
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{	
		if (args.length > 0)
		{
			/** Help Command */
			if (args[0].equalsIgnoreCase(Constants.HELP_CMD))
			{
				if (args.length > 1)
				{
					if (args[1].equalsIgnoreCase(Constants.ACCOUNT_CMD))
					{
						IBUtils.sendMessages((Player) sender, Messages.ACCOUNT_HELP_MSG);
						return true;
					}
					else if (args[1].equalsIgnoreCase(Constants.RELOAD_CMD))
					{
						IBUtils.sendMessages((Player) sender, Messages.RELOAD_HELP_MSG);
						return true;
					}
					else if (args[1].equalsIgnoreCase(Constants.PURGE_CMD))
					{
						IBUtils.sendMessages((Player) sender, Messages.PURGE_HELP_MSG);
						return true;
					}
					else if (args[1].equalsIgnoreCase(Constants.UUID_CMD))
					{
						IBUtils.sendMessages((Player) sender, Messages.UUID_HELP_MSG);
						return true;
					}
				}
				
				sender.sendMessage(Messages.PREFIX + Messages.HELP_MSG.get(3));
				return true;
			}
			/** Purge Command */
			else if (args[0].equalsIgnoreCase(Constants.PURGE_CMD))
			{
				if (!sender.hasPermission(Constants.PURGE_PERM))
				{
					sender.sendMessage(Messages.NO_PERMISSION);
					return false;
				}
				
				if (args.length > 1)
				{
					if (plugin.config.useMYSQL)
					{
						try
						{
							plugin.c.createStatement().execute("DROP TABLE IF EXISTS ib_" + args[0]);
						}
						catch (SQLException e)
						{
							sender.sendMessage(Messages.SQL_EX);
							return false;
						}
						
						sender.sendMessage(Messages.PURGE_SINGLE);
						return true;
					}
					
					File file = new File(plugin.playerData, args[1] + "." + plugin.config.format);
					if (!file.exists())
					{
						sender.sendMessage(Messages.PURGE_NO_FILE);
						return false;
					}
					
					file.delete();
					IBUtils.createPlayerFile(file);
					sender.sendMessage(Messages.PURGE_SINGLE);
					return true;
				}
				
				if (plugin.config.useMYSQL)
				{
					try
					{
						Statement statement = plugin.c.createStatement();
						ResultSet rs = plugin.c.getMetaData().getTables(null, null, null, new String[]{"TABLE"});
						while (rs.next())
						{
							sender.sendMessage(rs.getString(3));
							if (rs.getString(3).contains("ib_"))
								statement.execute("DROP TABLE " + rs.getString(3));
						}
					}
					catch (SQLException e)
					{
						sender.sendMessage(Messages.SQL_EX);
						return false;
					}
					
					sender.sendMessage(Messages.PURGE_MULTIPLE);
					return true;
				}
				
				for (File file : plugin.playerData.listFiles())
					file.delete();
				
				IBUtils.createPlayerFiles(plugin);
				sender.sendMessage(Messages.PURGE_MULTIPLE);
				return true;
			}
			/** Reload Command */
			else if (args[0].equalsIgnoreCase(Constants.RELOAD_CMD))
			{
				if (!sender.hasPermission(Constants.RELOAD_PERM))
				{
					sender.sendMessage(Messages.NO_PERMISSION);
					return false;
				}
				
				plugin.config.reloadConfiguration();
				sender.sendMessage(Messages.RELOAD_SUCCESS);
				return true;
			}
			/** UUID Command */
			else if (args[0].equalsIgnoreCase(Constants.UUID_CMD))
			{
				if (!sender.hasPermission(Constants.UUID_PERM))
				{
					sender.sendMessage(Messages.NO_PERMISSION);
					return false;
				}
				
				if (args.length > 1)
				{
					OfflinePlayer player = null;
					try
					{
						player = Bukkit.getServer().getOfflinePlayer(UUID.fromString(args[1]));
					}
					catch (IllegalArgumentException e)
					{
						for (Entry<String, String> uuid : plugin.config.uuids.entrySet())
							if (uuid.getValue().equals(args[1]))
								player = Bukkit.getServer().getOfflinePlayer(UUID.fromString(uuid.getKey()));
					}
					
					if (player == null)
					{
						sender.sendMessage(Messages.PLAYER_DNE);
						return false;
					}
					
					sender.sendMessage(Messages.PREFIX + args[1] + "'s UUID: " + ((OfflinePlayer) player).getUniqueId().toString());
					return true;
				}
				
				if (!(sender instanceof Player))
				{
					sender.sendMessage(Messages.PLAYER_CMD);
					return false;
				}
				
				sender.sendMessage(Messages.PREFIX + sender.getName() + "'s UUID: " + ((Player) sender).getUniqueId().toString());
				return true;
			}
		}
		
		IBUtils.sendMessages((Player) sender, Messages.HELP_MSG);
		return true;
	}
}

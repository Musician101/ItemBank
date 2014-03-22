package musician101.itembank.commands;

import java.io.File;

import musician101.itembank.ItemBank;
import musician101.itembank.lib.Constants;
import musician101.itembank.util.IBUtils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

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
						sender.sendMessage(Constants.ACCOUNT_HELP_MSG);
					else if (args[1].equalsIgnoreCase(Constants.RELOAD_CMD))
						sender.sendMessage(Constants.RELOAD_HELP_MSG);
					else if (args[1].equalsIgnoreCase(Constants.PURGE_CMD))
						sender.sendMessage(Constants.PURGE_HELP_MSG);
					
					return true;
				}
				
				sender.sendMessage(Constants.PREFIX + Constants.HELP_MSG[3]);
				return true;
			}
			/** Reload Command */
			else if (args[0].equalsIgnoreCase(Constants.RELOAD_CMD))
			{
				if (!sender.hasPermission(Constants.RELOAD_PERM))
				{
					sender.sendMessage(Constants.NO_PERMISSION);
					return false;
				}
				
				plugin.config.reloadConfiguration();
				sender.sendMessage(Constants.PREFIX + "Config reloaded.");
				return true;
			}
			/** Purge Command */
			else if (args[0].equalsIgnoreCase(Constants.PURGE_CMD))
			{
				if (!sender.hasPermission(Constants.PURGE_PERM))
				{
					sender.sendMessage(Constants.NO_PERMISSION);
					return false;
				}
				
				if (args.length > 1)
				{
					File file = new File(plugin.playerData, args[1] + ".yml");
					if (!file.exists())
					{
						sender.sendMessage(Constants.PREFIX + "File not found. Please check spelling and capitalization.");
						return false;
					}
					
					file.delete();
					IBUtils.createPlayerFile(file);
					sender.sendMessage(Constants.PREFIX + args[1] + "'s account has been reset.");
					return true;
				}
				
				for (File file : plugin.playerData.listFiles())
					file.delete();
				
				IBUtils.createPlayerFiles(plugin);
				sender.sendMessage(Constants.PREFIX + "All accounts have been reset.");
				return true;
			}
		}
		
		sender.sendMessage(Constants.HELP_MSG);
		return true;
	}
}

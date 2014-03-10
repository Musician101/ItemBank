package musician101.itembank.commands;

import musician101.itembank.ItemBank;
import musician101.itembank.lib.Constants;

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
		if (args.length == 0)
		{
			sender.sendMessage(Constants.HELP_MSG);
			return true;
		}
		
		/** Help Command */
		if (args[0].equalsIgnoreCase(Constants.HELP_CMD))
		{
			if (args.length > 1)
			{
				if (args[1].equalsIgnoreCase(Constants.ACOUNT_CMD))
					sender.sendMessage(Constants.ACCOUNT_HELP_MSG);
				else if (args[1].equals(Constants.RELOAD_CMD))
					sender.sendMessage(Constants.RELOAD_MSG);
				
				return true;
			}
			
			sender.sendMessage(Constants.PREFIX + Constants.HELP_MSG[3]);
			return true;
		}
		/** Reload Command */
		else if (args[0].equalsIgnoreCase(Constants.RELOAD_CMD))
		{
			if (!sender.hasPermission(Constants.ADMIN_PERM))
			{
				sender.sendMessage(Constants.NO_PERMISSION);
				return false;
			}
			
			plugin.config.reloadConfiguration();
			sender.sendMessage(Constants.PREFIX + "Config reloaded.");
			return true;
		}
		
		return false;
	}
}

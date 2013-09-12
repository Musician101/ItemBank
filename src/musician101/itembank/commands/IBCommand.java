package musician101.itembank.commands;

import musician101.itembank.ItemBank;
import musician101.itembank.lib.Constants;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * The code used to run when the ItemBank command is executed.
 * 
 * @author Musician101
 */
public class IBCommand implements CommandExecutor
{
	ItemBank plugin;
	/**
	 * @param plugin References the plugin's main class.
	 */
	public IBCommand(ItemBank plugin)
	{
		this.plugin = plugin;
	}
	
	/**
	 * @param sender Who sent the command.
	 * @param command Which command was executed
	 * @param label Alias of the command
	 * @param args Command parameters
	 * @return True if the command was successfully executed
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if (command.getName().equalsIgnoreCase(Constants.BASE_CMD) || command.getName().equalsIgnoreCase(Constants.BASE_ALIAS))
		{
			/** Base Command */
			if (args.length == 0)
			{
				if (sender.hasPermission(Constants.BASE_PERM + ".*") || sender.hasPermission(Constants.DEPOSIT_PERM) || sender.hasPermission(Constants.HELP_PERM) || sender.hasPermission(Constants.PURGE_PERM) || sender.hasPermission(Constants.VERSION_PERM) || sender.hasPermission(Constants.WITHDRAW_PERM))
					sender.sendMessage(Constants.PREFIX + Constants.BASE_DESC);
				else
					sender.sendMessage(Constants.NO_PERMISSION);
			}
			else if (args.length > 0)
			{
				String cmd = args[0].toLowerCase();
				/** Help Command */
				if (cmd == Constants.HELP_CMD || cmd == Constants.HELP_ALIAS)
					new HelpCommand(plugin, sender, args);
				/** Purge Command */
				else if (cmd == Constants.PURGE_CMD || cmd == Constants.PURGE_ALIAS)
					new PurgeCommand(plugin, sender, args);
			}
			return true;
		}
		return false;
	}
}

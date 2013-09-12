package musician101.itembank.commands;

import musician101.itembank.ItemBank;
import musician101.itembank.lib.Constants;

import org.bukkit.command.CommandSender;

/**
 * The code used when the Help argument is used in the ItemBank command.
 * 
 * @author Musician101
 */
public class HelpCommand
{
	/**
	 * @param plugin Referenecs the plugin's main class.
	 * @param sender Who sent the command.
	 * @param args The arguments used in the command.
	 */
	public HelpCommand(ItemBank plugin, CommandSender sender, String[] args)
	{
		if (!sender.hasPermission(Constants.HELP_PERM))
			sender.sendMessage(Constants.NO_PERMISSION);
		else
		{
			if (args.length == 1)
				sender.sendMessage(Constants.HELP_LIST);
			else
			{
				String cmd = args[1].toLowerCase();
				if (cmd == Constants.DEPOSIT_CMD || cmd == Constants.DEPOSIT_ALIAS)
					sender.sendMessage(Constants.DEPOSIT_HELP);
				else if (cmd == Constants.PURGE_CMD || cmd == Constants.PURGE_ALIAS)
					sender.sendMessage(Constants.PURGE_HELP);
				else if (cmd == Constants.WITHDRAW_CMD || cmd == Constants.WITHDRAW_ALIAS)
					sender.sendMessage(Constants.WITHDRAW_HELP);
				else
					sender.sendMessage(Constants.PREFIX + "Error: Command not recognized.");
			}
		}
	}
}

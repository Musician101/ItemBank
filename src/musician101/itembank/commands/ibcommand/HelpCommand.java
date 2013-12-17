package musician101.itembank.commands.ibcommand;

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
	public static boolean execute(ItemBank plugin, CommandSender sender, String[] args)
	{
		if (args.length == 1)
			sender.sendMessage(Constants.HELP_LIST);
		else
		{
			String cmd = args[1].toLowerCase();
			if (cmd == Constants.DEPOSIT_CMD)
				sender.sendMessage(Constants.DEPOSIT_HELP);
			else if (cmd == Constants.IA_CMD)
				sender.sendMessage(Constants.IA_HELP);
			else if (cmd == Constants.PURGE_CMD)
				sender.sendMessage(Constants.PURGE_HELP);
			else if (cmd == Constants.WITHDRAW_CMD)
				sender.sendMessage(Constants.WITHDRAW_HELP);
			else
				sender.sendMessage(Constants.PREFIX + "Error: Command not recognized.");
		}
		return true;
	}
}

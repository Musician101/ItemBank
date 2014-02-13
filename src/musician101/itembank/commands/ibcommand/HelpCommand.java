package musician101.itembank.commands.ibcommand;

import musician101.itembank.ItemBank;
import musician101.itembank.lib.Commands;
import musician101.itembank.lib.Messages;

import org.bukkit.command.CommandSender;

/**
 * The code used when the Help argument is used in the ItemBank command.
 * 
 * @author Musician101
 */
public class HelpCommand
{
	public static boolean execute(ItemBank plugin, CommandSender sender, String cmd)
	{
		if (cmd.equalsIgnoreCase(Commands.ACCOUNT_CMD))
		{
			sender.sendMessage(Commands.ACCOUNT_HELP);
			return true;
		}
		else if (cmd.equalsIgnoreCase(Commands.ALIAS_CMD))
		{
			sender.sendMessage(Commands.ALIAS_HELP);
			return true;
		}
		else if (cmd.equalsIgnoreCase(Commands.CONFIG_CMD))
		{
			sender.sendMessage(Commands.CONFIG_HELP);
			return true;
		}
		else if (cmd.equalsIgnoreCase(Commands.DEPOSIT_CMD))
		{
			sender.sendMessage(Commands.DEPOSIT_HELP);
			return true;
		}
		else if (cmd.equalsIgnoreCase(Commands.PURGE_CMD))
		{
			sender.sendMessage(Commands.PURGE_HELP);
			return true;
		}
		else if (cmd.equalsIgnoreCase(Commands.RELOAD_CMD))
		{
			sender.sendMessage(Commands.RELOAD_HELP);
			return true;
		}
		else if (cmd.equalsIgnoreCase(Commands.WITHDRAW_CMD))
		{
			sender.sendMessage(Commands.WITHDRAW_HELP);
			return true;
		}
		
		sender.sendMessage(Messages.PREFIX + "Error: Command not recognized.");
		return false;
	}
}

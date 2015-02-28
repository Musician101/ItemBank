package musician101.itembank.forge.command.itembank;

import java.util.Arrays;
import java.util.List;

import musician101.itembank.forge.ItemBank;
import musician101.itembank.forge.command.AbstractForgeCommand;
import musician101.itembank.forge.lib.Constants;
import musician101.itembank.forge.lib.Messages;
import musician101.itembank.forge.util.IBUtils;
import net.minecraft.command.ICommandSender;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HelpCommand extends AbstractForgeCommand
{
	public HelpCommand()
	{
		name = "help";
		usage = "itembank help";
		isPlayerOnly = false;
		aliases = Arrays.asList(name, "h");
	}
	
	@Override
	public boolean onCommand(ICommandSender sender, List<String> args)
	{
		if (args.size() > 1)
		{
			if (args.get(1).equalsIgnoreCase("account"))
			{
				IBUtils.addChatMessages(sender, Messages.ACCOUNT_HELP_MSG);
				return true;
			}
			else if (args.get(1).equalsIgnoreCase(Constants.RELOAD_CMD))
			{
				IBUtils.addChatMessages((Player) sender, Messages.RELOAD_HELP_MSG);
				return true;
			}
			else if (args.get(1).equalsIgnoreCase(Constants.PURGE_CMD))
			{
				IBUtils.addChatMessages((Player) sender, Messages.PURGE_HELP_MSG);
				return true;
			}
			else if (args.get(1).equalsIgnoreCase(Constants.UUID_CMD))
			{
				IBUtils.addChatMessages((Player) sender, Messages.UUID_HELP_MSG);
				return true;
			}
		}
		
		sender.sendMessage(Messages.PREFIX + Messages.HELP_MSG.get(3));
		return true;
	}
}

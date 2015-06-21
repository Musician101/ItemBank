package musician101.itembank.forge.command.itembank;

import musician101.itembank.forge.command.AbstractForgeCommand;
import musician101.itembank.forge.lib.Messages;
import musician101.itembank.forge.util.IBUtils;
import net.minecraft.command.ICommandSender;

public class HelpCommand extends AbstractForgeCommand
{
	public HelpCommand()
	{
		this.name = "help";
		this.usage = "/itembank help";
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args)
	{
		if (args.length > 1)
		{
			if (args[1].equalsIgnoreCase("account"))
			{
				IBUtils.addChatMessages(sender, Messages.ACCOUNT_HELP_MSG);
			}
			else if (args[1].equalsIgnoreCase("purge"))
			{
				IBUtils.addChatMessages(sender, Messages.PURGE_HELP_MSG);
			}
			//TODO need help information for Permission commands
			/*else if (args.get(1).equalsIgnoreCase("uuid"))
			{
				IBUtils.addChatMessages(sender, Messages.UUID_HELP_MSG);
			}*/
		}
		
		sender.addChatMessage(Messages.PREFIX.appendSibling(Messages.HELP_MSG.get(3)));
	}
}

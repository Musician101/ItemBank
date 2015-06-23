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
		this.usage = Messages.HELP_USAGE;
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args)
	{
		if (args.length > 1)
		{
			if (args[1].equalsIgnoreCase("account"))
			{
				sender.addChatMessage(IBUtils.getTranslatedChatComponent(Messages.ACCOUNT_USAGE));
				sender.addChatMessage(IBUtils.getTranslatedChatComponent(Messages.ACCOUNT_DESC));
			}
			else if (args[1].equalsIgnoreCase("permission"))
			{
				sender.addChatMessage(IBUtils.getTranslatedChatComponent(Messages.PERMISSION_USAGE));
				sender.addChatMessage(IBUtils.getTranslatedChatComponent(Messages.PERMISSION_DESC));
			}
			else if (args[1].equalsIgnoreCase("purge"))
			{
				sender.addChatMessage(IBUtils.getTranslatedChatComponent(Messages.PURGE_USAGE));
				sender.addChatMessage(IBUtils.getTranslatedChatComponent(Messages.PURGE_DESC));
			}
		}
		
		sender.addChatMessage(IBUtils.getTranslatedChatComponent(Messages.HELP_USAGE));
		sender.addChatMessage(IBUtils.getTranslatedChatComponent(Messages.HELP_DESC));
	}
}

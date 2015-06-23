package musician101.itembank.forge.command.itembank;

import musician101.itembank.forge.command.AbstractForgeCommand;
import musician101.itembank.forge.command.permission.PermissionCommand;
import musician101.itembank.forge.reference.Messages;
import musician101.itembank.forge.util.IBUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class IBCommand extends AbstractForgeCommand
{
	public IBCommand()
	{
		this.name = "itembank";
		this.usage = Messages.ITEMBANK_USAGE;
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException
	{
		if (args.length > 0)
		{
			if (args[0].equalsIgnoreCase("help"))
			{
				new HelpCommand().processCommand(sender, moveArguments(args));
				return;
			}
			else if (args[0].equalsIgnoreCase("permission"))
			{
				new PermissionCommand().processCommand(sender, moveArguments(args));
				return;
			}
			else if (args[0].equalsIgnoreCase("purge"))
			{
				new PurgeCommand().processCommand(sender, moveArguments(args));
				return;
			}
		}
		
		sender.addChatMessage(IBUtils.getTranslatedChatComponent(Messages.ITEMBANK_USAGE));
		sender.addChatMessage(IBUtils.getTranslatedChatComponent(Messages.ITEMBANK_DESC));
	}
}

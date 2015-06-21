package musician101.itembank.forge.command.permission;

import musician101.itembank.forge.command.AbstractForgeCommand;
import musician101.itembank.forge.lib.Messages;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class PermissionCommand extends AbstractForgeCommand
{
	public PermissionCommand()
	{
		this.name = "permission";
		this.usage = "/itembank permission";
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException
	{
		if (args.length > 0)
		{
			if (args[0].equalsIgnoreCase("add"))
			{
				new AddPermissionCommand().processCommand(sender, moveArguments(args));
				return;
			}
			else if (args[0].equalsIgnoreCase("remove"))
			{
				new RemovePermissionCommand().processCommand(sender, moveArguments(args));
				return;
			}
		}
		
		sender.addChatMessage(Messages.PREFIX.appendText("No help text provided."));
	}
}

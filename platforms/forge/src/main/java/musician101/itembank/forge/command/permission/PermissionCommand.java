package musician101.itembank.forge.command.permission;

import musician101.itembank.forge.command.AbstractForgeCommand;
import musician101.itembank.forge.lib.Messages;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

public class PermissionCommand extends AbstractForgeCommand
{
	public PermissionCommand()
	{
		this.name = "permission";
		this.usage = Messages.PERMISSION_USAGE;
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
		
		throw new WrongUsageException(usage, new Object[0]);
	}
}

package musician101.itembank.forge.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

public abstract class AbstractForgeCommand extends CommandBase
{
	protected boolean isPlayerOnly;
	protected String name;
	protected String usage;
	
	@Override
	public String getCommandName()
	{
		return name;
	}
	
	@Override
	public String getCommandUsage(ICommandSender commandSender)
	{
		return usage;
	}
	
	public boolean isPlayerOnly()
	{
		return isPlayerOnly;
	}
}

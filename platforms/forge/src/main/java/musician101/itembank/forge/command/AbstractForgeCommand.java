package musician101.itembank.forge.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

public abstract class AbstractForgeCommand extends CommandBase
{
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
	
	protected String[] moveArguments(String[] args)
	{
		List<String> list = new ArrayList<String>();
		Collections.addAll(list, args);
		list.remove(0);
		return (String[]) list.toArray();
	}
}

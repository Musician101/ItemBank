package musician101.itembank.forge.command.itembank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import musician101.itembank.forge.command.AbstractForgeCommand;
import musician101.itembank.forge.lib.Messages;
import musician101.itembank.forge.util.IBUtils;
import net.minecraft.command.ICommandSender;

public class IBCommand extends AbstractForgeCommand
{
	public IBCommand()
	{
		this.name = "itembank";
		this.usage = "/itembank";
		this.isPlayerOnly = false;
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args)
	{
		if (args.length > 0)
		{
			if (args[0].equalsIgnoreCase("purge"))
			{
				new PurgeCommand().processCommand(sender, moveArguments(args));
				return;
			}
		}
		
		IBUtils.addChatMessages(sender, Messages.HELP_MSG);
	}
	
	private String[] moveArguments(String[] args)
	{
		List<String> list = new ArrayList<String>();
		Collections.addAll(list, args);
		list.remove(0);
		return (String[]) list.toArray();
	}
}

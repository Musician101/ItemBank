package musician101.itembank.spigot.command.itembank;

import java.util.Arrays;

import musician101.itembank.spigot.ItemBank;
import musician101.itembank.spigot.command.AbstractSpigotCommand;
import musician101.itembank.spigot.command.HelpCommand;
import musician101.itembank.spigot.lib.Messages;

import org.bukkit.command.CommandSender;

public class IBCommand extends AbstractSpigotCommand
{
	public IBCommand(ItemBank plugin)
	{
		super(plugin, "itembank", "Virtual chest with configurable limits.", Arrays.asList("/itembank"), "", false, Arrays.asList(new PurgeCommand(plugin), new ReloadCommand(plugin), new UUIDCommand(plugin)));
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] args)
	{
		if (args.length > 0)
		{
			if (args[0].equalsIgnoreCase("help"))
				return new HelpCommand(plugin, this).onCommand(sender, moveArguments(args));
			
			for (AbstractSpigotCommand command : getSubCommands())
				if (command.getName().equalsIgnoreCase(args[0]))
					return command.onCommand(sender, args);
		}
		
		sender.sendMessage(Messages.HEADER);
		for (AbstractSpigotCommand command : plugin.getCommands())
			sender.sendMessage(new HelpCommand(plugin, command).getCommandHelpInfo());
		
		return true;
	}
}

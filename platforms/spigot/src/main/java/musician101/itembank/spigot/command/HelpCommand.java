package musician101.itembank.spigot.command;

import java.util.Arrays;

import musician101.itembank.spigot.ItemBank;
import musician101.itembank.spigot.lib.Messages;

import org.bukkit.command.CommandSender;

public class HelpCommand extends AbstractSpigotCommand
{
	AbstractSpigotCommand mainCommand;
	
	public HelpCommand(ItemBank plugin, AbstractSpigotCommand mainCommand)
	{
		super(plugin, "help", "Display help info for /" + mainCommand.getName(), Arrays.asList("/" + mainCommand.getName(), "help"), "itembank.help", false, null);
		this.mainCommand = mainCommand;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] args)
	{
		sender.sendMessage(Messages.HEADER);
		sender.sendMessage(mainCommand.getUsage());
		for (AbstractSpigotCommand command : mainCommand.getSubCommands())
			sender.sendMessage(command.getCommandHelpInfo());
		
		return true;
	}
}

package musician101.itembank.commands;

import musician101.itembank.ItemBank;
import musician101.itembank.lib.Constants;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class IBCommand implements CommandExecutor
{
	ItemBank plugin;
	
	public IBCommand(ItemBank plugin)
	{
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if (args.length == 0)
		{
			sender.sendMessage(Constants.HELP_MSG);
			return true;
		}
		
		if (args[0].equalsIgnoreCase(Constants.HELP_CMD))
		{
			if (args.length > 1)
			{
				if (args[1].equalsIgnoreCase(Constants.ACOUNT_CMD))
					sender.sendMessage(Constants.ACCOUNT_HELP_MSG);
				
				return true;
			}
			
			sender.sendMessage(Constants.PREFIX + Constants.HELP_MSG[3]);
			return true;
		}
		
		return false;
	}
}

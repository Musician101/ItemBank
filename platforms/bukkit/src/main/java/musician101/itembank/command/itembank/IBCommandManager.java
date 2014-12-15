package musician101.itembank.command.itembank;

import java.util.ArrayList;
import java.util.List;

import musician101.itembank.lib.Messages;
import musician101.luc.bukkit.command.AbstractSubCommandManager;
import musician101.luc.bukkit.command.ISubCommand;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class IBCommandManager extends AbstractSubCommandManager
{
	public IBCommandManager()
	{
		
	}
	
	@Override
	public List<String> getHelp(int page)
	{
		List<String> help = new ArrayList<String>();
		help.add(Messages.HEADER);
		help.add(ChatColor.DARK_RED + Messages.HELP_VERSION + ": " + Bukkit.getPluginManager().getPlugin("ItemBank").getDescription().getVersion());
		help.add(ChatColor.GREEN + "Recommended BukkitAPI Version: 1.7.10-R0.1");
		help.add(ChatColor.GREEN + "[] = optional, <> = mandatory");
		for (ISubCommand command : commandList)
			help.add(ChatColor.GREEN + command.getUsage() + ": " + ChatColor.AQUA + command.getDescription());
		
		return help;
	}
}

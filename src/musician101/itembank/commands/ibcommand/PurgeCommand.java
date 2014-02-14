package musician101.itembank.commands.ibcommand;

import java.io.File;

import musician101.itembank.ItemBank;
import musician101.itembank.lib.Commands;
import musician101.itembank.lib.Messages;
import musician101.itembank.util.IBUtils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

/**
 * The code used when the Purge argument is used in the ItemBank command.
 * 
 * @author Musician101
 */
public class PurgeCommand
{
	public static boolean execute(ItemBank plugin, CommandSender sender, String[] args)
	{
		if (args.length == 1)
			return execute(plugin, sender);
		
		return execute(plugin, sender, args[1].toLowerCase());
	}
	
	private static boolean execute(ItemBank plugin, CommandSender sender, String player)
	{
		if (!sender.hasPermission(Commands.PURGE_PERM))
		{
			sender.sendMessage(Messages.NO_PERMISSION);
			return false;
		}
		
		plugin.playerFile = new File(plugin.playerDataDir + "/" + player + ".yml");
		plugin.playerFile.delete();
		IBUtils.createPlayerFile(plugin, plugin.playerFile);
		sender.sendMessage(Messages.PREFIX + "Player file reset.");
		return true;
	}
	
	private static boolean execute(ItemBank plugin, CommandSender sender)
	{
		if (!sender.hasPermission(Commands.PURGE_PERM))
		{
			sender.sendMessage(Messages.NO_PERMISSION);
			return false;
		}
		
		for (File file : plugin.playerDataDir.listFiles())
			file.delete();
		
		IBUtils.createPlayerFiles(plugin, Bukkit.getOnlinePlayers());
		sender.sendMessage(Messages.PREFIX + "Purge complete.");
		return true;
	}
}

package musician101.itembank.commands.ibcommand;

import java.io.File;

import musician101.itembank.ItemBank;
import musician101.itembank.lib.Constants;
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
		if (!sender.hasPermission(Constants.PURGE_PERM))
		{
			sender.sendMessage(Constants.NO_PERMISSION);
			return false;
		}
		else
		{
			if (args.length == 1)
			{
				for (File file : plugin.playerDataDir.listFiles())
					file.delete();
				
				IBUtils.createPlayerFiles(plugin, Bukkit.getOnlinePlayers());
				sender.sendMessage(Constants.PREFIX + "Purge complete.");
			}
			else if (args.length == 2)
			{
				plugin.playerFile = new File(plugin.playerDataDir + "/" + args[1].toLowerCase() + ".yml");
				plugin.playerFile.delete();
				IBUtils.createPlayerFile(plugin, plugin.playerFile);
				sender.sendMessage(Constants.PREFIX + "Player file reset.");
			}
			return true;
		}
	}
}

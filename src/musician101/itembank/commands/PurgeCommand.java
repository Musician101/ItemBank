package musician101.itembank.commands;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import musician101.itembank.ItemBank;
import musician101.itembank.lib.Constants;
import musician101.itembank.listeners.PlayerListener;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * The code used when the Purge argument is used in the ItemBank command.
 * 
 * @author Musician101
 */
public class PurgeCommand
{
	/**
	 * @param plugin References the plugin's main class.
	 * @param sender Who sent the command.
	 * @param args The arguments used in the command.
	 */
	public PurgeCommand(ItemBank plugin, CommandSender sender, String[] args)
	{
		if (!sender.hasPermission(Constants.PURGE_PERM))
			sender.sendMessage(Constants.NO_PERMISSION);
		else
		{
			if (args.length == 1)
			{
				File files = new File(plugin.getDataFolder() + "/PlayerData");
				for (File file : files.listFiles())
					file.delete();
				
				Player[] players = Bukkit.getOnlinePlayers();
				if (players.length > 0)
				{
					for (Player player : players)
					{
						plugin.playerFile = new File(plugin.getDataFolder() + "/PlayerData/" + player.getName() + ".yml");
						try
						{
							FileWriter fw;
							plugin.playerFile.createNewFile();
							fw = new FileWriter(plugin.playerFile.getAbsoluteFile());
							BufferedWriter bw = new BufferedWriter(fw);
							bw.write(PlayerListener.template);
							bw.close();
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				}
				sender.sendMessage(Constants.PREFIX + "Purge complete.");
			}
			else if (args.length == 2)
			{
				plugin.playerFile = new File(plugin.getDataFolder() + "/PlayerData/" + args[1] + ".yml");
				try
				{
					plugin.playerFile.delete();
					FileWriter fw;
					plugin.playerFile.createNewFile();
					fw = new FileWriter(plugin.playerFile.getAbsoluteFile());
					BufferedWriter bw = new BufferedWriter(fw);
					bw.write(PlayerListener.template);
					bw.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				sender.sendMessage(Constants.PLAYER_FILE_RESET);
			}
			else
				sender.sendMessage(Constants.TOO_MANY_ARGUMENTS);
		}
	}
}

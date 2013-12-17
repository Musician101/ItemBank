package musician101.itembank.commands;

import musician101.itembank.Config;
import musician101.itembank.ItemBank;
import musician101.itembank.exceptions.InvalidAliasException;
import musician101.itembank.lib.Constants;
import musician101.itembank.util.IBUtils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * The code used to find the alias of a given block or item.
 * 
 * @author Musician101
 */
public class ItemAliasCommand implements CommandExecutor
{
	ItemBank plugin;
	Config config;
	
	/**
	 * @param plugin
	 * @param config
	 */
	public ItemAliasCommand(ItemBank plugin, Config config)
	{
		this.plugin = plugin;
		this.config = config;
	}
	
	/**
	 * @param sender Who sent the command.
	 * @param command Which command was executed.
	 * @param label Alias of the command.
	 * @param args Command parameters.
	 * @return true If the command was successfully executed.
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if (command.getName().equalsIgnoreCase(Constants.IA_CMD))
		{
			if (!sender.hasPermission(Constants.IA_PERM))
			{
				sender.sendMessage(Constants.NO_PERMISSION);
				return false;
			}
			
			ItemStack item =  null;
			int amount = 0;
			if (args.length == 0)
			{
				if (!(sender instanceof Player))
				{
					sender.sendMessage(Constants.PLAYER_COMMAND_ONLY);
					return false;
				}
				
				item = ((Player) sender).getItemInHand();
			}
			else
			{
				String name = args[0].toLowerCase();
				try
				{
					item = IBUtils.getIdFromAlias(plugin, name, amount);
				}
				catch (InvalidAliasException e)
				{
					item = IBUtils.getItem(plugin, name, amount);
				}
				catch (NullPointerException e)
				{
					sender.sendMessage(Constants.NULL_POINTER);
					return false;
				}
				
				if (item == null)
				{
					sender.sendMessage(Constants.getAliasError(name));
					return false;
				}
			}
			
			if (item == null)
			{
				sender.sendMessage(Constants.PREFIX + "Error: Your hand is empty.");
				return false;
			}
			
			/**
			 * Deprecated mtehod ItemStack.getTypeId() in Bukkit.
			 * Waiting for a proper alternative before fixing.
			 */
			sender.sendMessage(Constants.PREFIX + "Material: " + item.getType().toString() + " ID: " + item.getTypeId() + ":" + item.getDurability());
			String aliases = plugin.translator.getAliases(item);
			if (aliases != null)
				sender.sendMessage(Constants.PREFIX + "Aliases: " + aliases);
			
			return true;
		}
		return false;
	}
}

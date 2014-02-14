package musician101.itembank.commands.ibcommand;

import java.io.File;
import java.util.Map;

import musician101.itembank.Config;
import musician101.itembank.ItemBank;
import musician101.itembank.exceptions.InvalidAliasException;
import musician101.itembank.lib.Commands;
import musician101.itembank.lib.ConfigConstants;
import musician101.itembank.lib.Messages;
import musician101.itembank.util.IBUtils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.MemorySection;
import org.bukkit.inventory.ItemStack;

public class ConfigCommand
{
	public static boolean excute(ItemBank plugin, Config config, CommandSender sender, String[] args)
	{
		if (!sender.hasPermission(Commands.CONFIG_PERM))
		{
			sender.sendMessage(Messages.NO_PERMISSION);
			return false;
		}
		
		if (args.length == 1)
			return execute(plugin, config, sender);
		
		if (args.length > 2)
			return execute(plugin, config, sender, args);
		
		sender.sendMessage(Messages.NOT_ENOUGH_ARGUMENTS);
		return false;
	}

	private static boolean execute(ItemBank plugin, Config config, CommandSender sender)
	{
		sender.sendMessage("--------" + ChatColor.DARK_RED + "ItemBank Config" + ChatColor.WHITE + "--------");
		for (Map.Entry<String, Object> entry : config.blacklist.getValues(true).entrySet())
		{
			if (!(entry.getValue() instanceof MemorySection))
			{
				if (Material.getMaterial(entry.getKey().split("\\.")[0].toUpperCase()) == null)
					sender.sendMessage(Messages.getConfigValueError(entry.getKey(), entry.getValue().toString()));
				else
				{
					try
					{
						Short.valueOf(entry.getValue().toString());
						sender.sendMessage(ChatColor.DARK_RED + ConfigConstants.BLACKLIST + "." + entry.getKey() + ChatColor.WHITE + ": " + entry.getValue());
					}
					catch (NumberFormatException e)
					{
						sender.sendMessage(Messages.getConfigValueError(entry.getKey(), entry.getValue().toString()));
					}
				}
			}
		}
		
		sender.sendMessage(new String[]{ChatColor.DARK_RED + ConfigConstants.CHECK_FOR_UPDATE + ChatColor.WHITE + ": " + config.checkForUpdate,
				ChatColor.DARK_RED + ConfigConstants.ENABLE_VAULT + ChatColor.WHITE + ": " + config.enableVault,
				ChatColor.DARK_RED + ConfigConstants.TRANSACTION_COST + ChatColor.WHITE + ": " + config.transactionCost});
		
		return true;
	}
	
	private static boolean execute(ItemBank plugin, Config config, CommandSender sender, String[] args)
	{
		if (args[1].equalsIgnoreCase(ConfigConstants.BLACKLIST))
			return blacklist(plugin, config, sender, args.length, args[2].toLowerCase(), args[3].toLowerCase(), args[4]);
		else if (args[1].equalsIgnoreCase(ConfigConstants.CHECK_FOR_UPDATE))
			return checkForUpdate(plugin, config, sender, args[2].toLowerCase());
		else if (args[1].equalsIgnoreCase(ConfigConstants.ENABLE_VAULT))
			return enableVault(plugin, config, sender, args[2].toLowerCase());
		else if (args[1].equalsIgnoreCase(ConfigConstants.TRANSACTION_COST))
		{
			try
			{
				return transactionCost(plugin, config, sender, Double.valueOf(args[2]));
			}
			catch (NumberFormatException e)
			{
				sender.sendMessage(Messages.NUMBER_FORMAT);
				return false;
			}
		}
		else if (args[1].equalsIgnoreCase("regen"))
		{
			return regen(plugin, config, sender);
		}
		
		sender.sendMessage(Messages.getInvalidArgumentError(args[1]));
		return false;
	}
	
	private static boolean blacklist(ItemBank plugin, Config config, CommandSender sender, int args, String operation, String material, String value)
	{
		if (operation.equals("set"))
		{
			if (args < 3)
			{
				sender.sendMessage(Messages.NOT_ENOUGH_ARGUMENTS);
				return false;
			}
			
			if (args > 4)
			{
				try
				{
					return blacklistSet(plugin, config, sender, operation, Integer.valueOf(value));
				}
				catch (NumberFormatException e)
				{
					sender.sendMessage(Messages.NUMBER_FORMAT);
					return false;
				}
			}
			
			return blacklistSet(plugin, config, sender, operation, 0);
		}
		else if (operation.equals("remove"))
		{
			if (args < 3)
			{
				sender.sendMessage(Messages.NOT_ENOUGH_ARGUMENTS);
				return false;
			}
			
			return blacklistRemove(plugin, config, sender, material);
		}
		
		sender.sendMessage(Messages.getInvalidArgumentError(operation));
		return false;
	}
	
	private static boolean blacklistSet(ItemBank plugin, Config config, CommandSender sender, String material, int value)
	{
		ItemStack item = null;
		try
		{
			item = IBUtils.getItemFromAlias(plugin, material, 0);
		}
		catch (InvalidAliasException e)
		{
			item = IBUtils.getItem(material, 0);
		}
		catch (NullPointerException e)
		{
			sender.sendMessage(Messages.NULL_POINTER);
			return false;
		}
		
		plugin.getConfig().set(ConfigConstants.BLACKLIST + "." + item.getType().toString().toLowerCase() + "." + item.getDurability(), Integer.valueOf(value));
		plugin.saveConfig();
		config.reloadConfiguration();
		sender.sendMessage(Messages.PREFIX + ConfigConstants.BLACKLIST + "." + item.getType().toString().toLowerCase() + "." + item.getDurability() + " set to " + value + ".");
		return true;
	}
	
	private static boolean blacklistRemove(ItemBank plugin, Config config, CommandSender sender, String material)
	{
		ItemStack item = null;
		try
		{
			item = IBUtils.getItemFromAlias(plugin, material, 0);
		}
		catch (InvalidAliasException e)
		{
			item = IBUtils.getItem(material, 0);
		}
		catch (NullPointerException e)
		{
			sender.sendMessage(Messages.NULL_POINTER);
			return false;
		}
		
		if (plugin.getConfig().getConfigurationSection(ConfigConstants.BLACKLIST).getValues(true).size() == 1)
			plugin.getConfig().set(ConfigConstants.BLACKLIST + "." + item.getType().toString().toLowerCase(), null);
		else
			plugin.getConfig().set(ConfigConstants.BLACKLIST + "." + item.getType().toString().toLowerCase() + "." + item.getDurability(), null);
		
		plugin.saveConfig();
		config.reloadConfiguration();
		sender.sendMessage(Messages.PREFIX + "Config: " + ConfigConstants.BLACKLIST + "." + item.getType().toString() + "." + item.getDurability() + " removed.");
		return true;
	}
	
	private static boolean checkForUpdate(ItemBank plugin, Config config, CommandSender sender, String bool)
	{
		if (bool.equalsIgnoreCase("true"))
			plugin.getConfig().set(ConfigConstants.CHECK_FOR_UPDATE, true);
		else if (bool.equalsIgnoreCase("false"))
			plugin.getConfig().set(ConfigConstants.CHECK_FOR_UPDATE, false);
		else
		{
			sender.sendMessage(Messages.PREFIX + "Error: " + bool + " is not a valid argument.");
			return false;
		}
		
		plugin.saveConfig();
		config.reloadConfiguration();
		sender.sendMessage(Messages.PREFIX + "Config: " + ConfigConstants.CHECK_FOR_UPDATE + " set to " + bool + ".");
		return true;
	}
	
	private static boolean enableVault(ItemBank plugin, Config config, CommandSender sender, String bool)
	{
		if (bool.equalsIgnoreCase("true"))
			plugin.getConfig().set(ConfigConstants.ENABLE_VAULT, true);
		else if (bool.equalsIgnoreCase("false"))
			plugin.getConfig().set(ConfigConstants.ENABLE_VAULT, false);
		else
		{
			sender.sendMessage(Messages.PREFIX + "Error: " + bool + " is not a vaild argument.");
			return false;
		}
		
		plugin.saveConfig();
		config.reloadConfiguration();
		sender.sendMessage(Messages.PREFIX + "Config: " + ConfigConstants.ENABLE_VAULT + " set to " + bool + ".");
		return true;
	}
	
	private static boolean transactionCost(ItemBank plugin, Config config, CommandSender sender, Double amount)
	{
		if (amount <= 0)
		{
			sender.sendMessage(Messages.AMOUNT_ERROR);
			return false;
		}
		
		plugin.getConfig().set(ConfigConstants.TRANSACTION_COST, amount);
		plugin.saveConfig();
		config.reloadConfiguration();
		sender.sendMessage(Messages.PREFIX + "Config: " + ConfigConstants.TRANSACTION_COST + " set to " + amount + ".");
		return true;
	}
	
	private static boolean regen(ItemBank plugin, Config config, CommandSender sender)
	{
		File configFile = new File(plugin.getDataFolder(), "config.yml");
		configFile.delete();
		plugin.saveDefaultConfig();
		config.reloadConfiguration();
		sender.sendMessage(Messages.PREFIX + "Regenerated config file.");
		return true;
	}
}

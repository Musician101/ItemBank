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
		
		if (args.length > 1)
		{
			if (args[1].equalsIgnoreCase(ConfigConstants.BLACKLIST))
			{
				if (args.length > 2)
				{
					if (args[2].equalsIgnoreCase("set"))
					{
						if (args.length < 3)
						{
							sender.sendMessage(Messages.NOT_ENOUGH_ARGUMENTS);
							return false;
						}
						
						String material = args[3].toLowerCase();
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
						
						int value = 0;
						if (args.length > 4)
						{
							try
							{
								value = Integer.valueOf(args[4]);
							}
							catch (NumberFormatException e)
							{
								sender.sendMessage(Messages.NUMBER_FORMAT);
								return false;
							}
						}
						
						plugin.getConfig().set(ConfigConstants.BLACKLIST + "." + item.getType().toString().toLowerCase() + "." + item.getDurability(), Integer.valueOf(value));
						plugin.saveConfig();
						config.reloadConfiguration();
						sender.sendMessage(Messages.PREFIX + ConfigConstants.BLACKLIST + "." + item.getType().toString().toLowerCase() + "." + item.getDurability() + " set to " + value + ".");
						return true;
					}
					else if (args[2].equalsIgnoreCase("remove"))
					{
						ItemStack item = null;
						try
						{
							item = IBUtils.getItemFromAlias(plugin, args[3].toLowerCase(), 0);
						}
						catch (InvalidAliasException e)
						{
							item = IBUtils.getItem(args[3].toLowerCase(), 0);
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
					
					sender.sendMessage(Messages.getInvalidArgumentError(args[2]));
					return false;
				}
			}
			else if (args[1].equalsIgnoreCase(ConfigConstants.CHECK_FOR_UPDATE))
			{
				if (args.length > 2)
				{
					String bool = args[2].toLowerCase();
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
			}
			else if (args[1].equalsIgnoreCase(ConfigConstants.ENABLE_VAULT))
			{
				if (args.length > 2)
				{
					String bool = args[2];
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
			}
			else if (args[1].equalsIgnoreCase(ConfigConstants.TRANSACTION_COST))
			{
				if (args.length > 2)
				{
					Double amount = 0.0;
					try
					{
						amount = Double.valueOf(args[2]);
					}
					catch (NumberFormatException e)
					{
						sender.sendMessage(Messages.NUMBER_FORMAT);
						return false;
					}
					
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
			}
			else if (args[1].equalsIgnoreCase("regen"))
			{
				File configFile = new File(plugin.getDataFolder(), "config.yml");
				configFile.delete();
				plugin.saveDefaultConfig();
				config.reloadConfiguration();
				sender.sendMessage(Messages.PREFIX + "Regenerated config file.");
				return true;
			}
			
			sender.sendMessage(Messages.getInvalidArgumentError(args[1]));
			return false;
		}
		
		sender.sendMessage(Messages.NOT_ENOUGH_ARGUMENTS);
		return false;
	}
}

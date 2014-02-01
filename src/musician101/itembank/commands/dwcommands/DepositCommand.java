package musician101.itembank.commands.dwcommands;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import musician101.itembank.Config;
import musician101.itembank.ItemBank;
import musician101.itembank.exceptions.InvalidAliasException;
import musician101.itembank.lib.Commands;
import musician101.itembank.lib.Messages;
import musician101.itembank.util.IBUtils;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * The code used to run when the Deposit command is executed.
 * 
 * @author Musician101
 */
public class DepositCommand implements CommandExecutor
{
	ItemBank plugin;
	Config config;
	
	/**
	 * @param plugin References the plugin's main class.
	 * @param config References the config options.
	 */
	public DepositCommand(ItemBank plugin, Config config)
	{
		this.plugin = plugin;
		this.config = config;
	}
	
	/**
	 * @param sender Who sent the command.
	 * @param command Which command was executed.
	 * @param label Alias of the command.
	 * @param args Command parameters.
	 * @return True if the command was successfully executed.
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if (command.getName().equalsIgnoreCase(Commands.DEPOSIT_CMD))
		{
			if (!sender.hasPermission(Commands.DEPOSIT_PERM))
			{
				sender.sendMessage(Messages.NO_PERMISSION);
				return false;
			}
			
			if (!(sender instanceof Player) && !args[0].equalsIgnoreCase(Commands.ADMIN_CMD))
			{
				sender.sendMessage(Messages.PLAYER_COMMAND_ONLY);
				return false;
			}
			
			if (args.length == 0)
			{
				ItemStack item = ((Player) sender).getItemInHand();
				if (item == null || item.getType() == Material.AIR)
				{
					sender.sendMessage(Messages.AIR_BLOCK);
					return false;
				}
				
				/** Custom Item Check */
				if (item.hasItemMeta() || item.getType() == Material.FIREWORK || item.getType() == Material.WRITTEN_BOOK || (item.getType() == Material.SKULL_ITEM && item.getDurability() == 3))
					return CustomItem.deposit(plugin, item, (Player) sender);
				
				String itemPath = item.getType().toString().toLowerCase() + "." + item.getDurability();
				plugin.playerFile = new File(plugin.playerDataDir + "/" + sender.getName().toLowerCase() + ".yml");
				plugin.playerData = new YamlConfiguration();
				try
				{
					plugin.playerData.load(plugin.playerFile);
				}
				catch (FileNotFoundException e)
				{
					sender.sendMessage(Messages.FILE_NOT_FOUND);
					return false;
				}
				catch (IOException e)
				{
					sender.sendMessage(Messages.IO_EXCEPTION);
					return false;
				}
				catch (InvalidConfigurationException e)
				{
					sender.sendMessage(Messages.YAML_EXCEPTION);
					return false;
				}
				
				int amountInBank = plugin.playerData.getInt(itemPath);
				int oldItemAmount = item.getAmount();
				int newItemAmount = item.getAmount();
				int newAmountInBank = amountInBank + oldItemAmount;
				if (config.blacklist.isSet(itemPath))
				{
					int maxAmount = config.blacklist.getInt(itemPath);
					if (maxAmount == 0)
					{
						sender.sendMessage(Messages.NO_DEPOSIT);
						return false;
					}
					else if (maxAmount == amountInBank)
					{
						sender.sendMessage(Messages.getMaxedDepositMessage(item.getType().toString()));
						return false;
					}
					else if (maxAmount < newAmountInBank)
					{
						oldItemAmount = maxAmount - amountInBank;
						newAmountInBank = amountInBank + oldItemAmount;
						sender.sendMessage(Messages.PARTIAL_DEPOSIT);
					}
				}
				
				plugin.playerData.set(itemPath, newAmountInBank);
				try
				{
					plugin.playerData.save(plugin.playerFile);
				}
				catch (IOException e)
				{
					sender.sendMessage(Messages.IO_EXCEPTION);
					plugin.playerData.set(itemPath, amountInBank);
					return false;
				}
				
				item.setAmount(oldItemAmount);
				newItemAmount = newItemAmount - oldItemAmount;
				if (newItemAmount == 0)
					((Player) sender).getInventory().removeItem(item);
				else
					((Player) sender).getInventory().getItem(((Player) sender).getInventory().getHeldItemSlot()).setAmount(newItemAmount);
				
				sender.sendMessage(Messages.getDepositSuccess(item.getType().toString(), item.getAmount()));
				if (plugin.getEconomy().isEnabled() && config.enableVault)
				{
					sender.sendMessage(Messages.getTransactionFeeMessage(config.transactionCost));
					plugin.getEconomy().takeMoney(sender.getName(), config.transactionCost);
				}
				return true;
			}
			
			/** Admin Deposit Check */
			if (args[0].equalsIgnoreCase(Commands.ADMIN_CMD))
				return Admin.deposit(plugin, (Player) sender, args);
			
			/** Economy Check */
			if (!IBUtils.checkEconomy(plugin, config, (Player) sender))
			{
				sender.sendMessage(Messages.LACK_MONEY);
				return false;
			}
			
			/** Custom Item Check */
			if (args[0].equalsIgnoreCase(Commands.CUSTOM_ITEM_CMD))
			{
				ItemStack item = ((Player) sender).getItemInHand();
				if (args.length < 0)
				{
					for (ItemStack itemStack : ((Player) sender).getInventory())
						if (itemStack.hasItemMeta())
							if (itemStack.getItemMeta().hasDisplayName())
								if (itemStack.getItemMeta().getDisplayName() == args[1])
									item = itemStack;
					
					if (item == ((Player) sender).getItemInHand())
					{
						sender.sendMessage(Messages.ITEM_NOT_FOUND);
						return false;
					}
				}
				
				return CustomItem.deposit(plugin, ((Player) sender).getItemInHand(), (Player) sender);
			}
			
			String name = args[0].toLowerCase();
			int amount = 64;
			if (args.length == 2)
			{
				try
				{
					amount = Integer.parseInt(args[1]);
				}
				catch (NumberFormatException e)
				{
					sender.sendMessage(Messages.NUMBER_FORMAT);
					return false;
				}
			}
			
			ItemStack item = null;
			try
			{
				item = IBUtils.getItemFromAlias(plugin, name, amount);
			}
			catch (InvalidAliasException e)
			{
				item = IBUtils.getItem(name, amount);
			}
			catch (NullPointerException e)
			{
				sender.sendMessage(Messages.NULL_POINTER);
				return false;
			}
			
			if (item == null)
			{
				sender.sendMessage(Messages.getAliasError(name));
				return false;
			}
			
			if (item.getType() == Material.AIR)
			{
				sender.sendMessage(Messages.AIR_BLOCK);
				return false;
			}
			
			String itemPath = item.getType().toString().toLowerCase() + "." + item.getDurability();
			plugin.playerFile = new File(plugin.playerDataDir + "/" + sender.getName().toLowerCase() + ".yml");
			plugin.playerData = new YamlConfiguration();
			try
			{
				plugin.playerData.load(plugin.playerFile);
			}
			catch (FileNotFoundException e)
			{
				sender.sendMessage(Messages.FILE_NOT_FOUND);
				return false;
			}
			catch (IOException e)
			{
				sender.sendMessage(Messages.IO_EXCEPTION);
				return false;
			}
			catch (InvalidConfigurationException e)
			{
				sender.sendMessage(Messages.YAML_EXCEPTION);
				return false;
			}
			
			if (!((Player) sender).getInventory().containsAtLeast(item, item.getAmount()))
			{
				sender.sendMessage(Messages.ITEM_NOT_FOUND);
				return false;
			}
			
			int oldAmount = plugin.playerData.getInt(itemPath);
			int amountInInv = IBUtils.getAmount((Player) sender, item.getType(), item.getDurability());
			if (amountInInv < amount)
				amount = amountInInv;
			
			int newAmount = oldAmount + amount;
			if (config.blacklist.isSet(itemPath))
			{
				int maxAmount = config.blacklist.getInt(itemPath);
				if (maxAmount == 0)
				{
					sender.sendMessage(Messages.NO_DEPOSIT);
					return false;
				}
				else if (maxAmount == oldAmount)
				{
					sender.sendMessage(Messages.getMaxedDepositMessage(item.getType().toString()));
					return false;
				}
				else if (maxAmount < newAmount)
				{
					amount = maxAmount - oldAmount;
					newAmount = oldAmount + amount;
					sender.sendMessage(Messages.PARTIAL_DEPOSIT);
				}
			}
			
			plugin.playerData.set(itemPath, newAmount);
			try
			{
				plugin.playerData.save(plugin.playerFile);
			}
			catch (IOException e)
			{
				sender.sendMessage(Messages.IO_EXCEPTION);
				plugin.playerData.set(itemPath, oldAmount);
				return false;
			}
			
			item.setAmount(amount);
			((Player) sender).getInventory().removeItem(item);
			sender.sendMessage(Messages.getDepositSuccess(item.getType().toString(), item.getAmount()));
			if (plugin.getEconomy().isEnabled() && config.enableVault)
			{
				sender.sendMessage(Messages.getTransactionFeeMessage(config.transactionCost));
				plugin.getEconomy().takeMoney(sender.getName(), config.transactionCost);
			}
			
			return true;
		}
		return false;
	}
}

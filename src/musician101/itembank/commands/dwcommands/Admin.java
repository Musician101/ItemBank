package musician101.itembank.commands.dwcommands;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import musician101.itembank.ItemBank;
import musician101.itembank.exceptions.InvalidAliasException;
import musician101.itembank.lib.Messages;
import musician101.itembank.util.IBUtils;

import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * The admin portion of the Deposit and Withdraw commands.
 * 
 * @author Musician101
 */
public class Admin
{
	public static boolean deposit(ItemBank plugin, Player admin, String player, String name, int amount)
	{	
		if (amount == 0)
			amount = 64;
		
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
			admin.sendMessage(Messages.NULL_POINTER);
			return false;
		}
		
		if (item == null)
		{
			admin.sendMessage(Messages.getAliasError(name));
			return false;
		}
		
		if (item.getType() == Material.AIR)
		{
			admin.sendMessage(Messages.AIR_BLOCK);
			return false;
		}
		
		String itemPath = item.getType().toString().toLowerCase() + "." + item.getDurability();
		plugin.playerFile = new File(plugin.playerDataDir + "/" + player + ".yml");
		plugin.playerData = new YamlConfiguration();
		try
		{
			plugin.playerData.load(plugin.playerFile);
		}
		catch (FileNotFoundException e)
		{
			admin.sendMessage(Messages.FILE_NOT_FOUND);
			return false;
		}
		catch (IOException e)
		{
			admin.sendMessage(Messages.IO_EXCEPTION);
			return false;
		}
		catch (InvalidConfigurationException e)
		{
			admin.sendMessage(Messages.YAML_EXCEPTION);
			return false;
		}
		
		int oldAmount = plugin.playerData.getInt(itemPath);
		int newAmount = oldAmount + amount;
		plugin.playerData.set(itemPath, newAmount);
		try
		{
			plugin.playerData.save(plugin.playerFile);
		}
		catch (IOException e)
		{
			admin.sendMessage(Messages.IO_EXCEPTION);
			plugin.playerData.set(itemPath, oldAmount);
			return false;
		}
		
		item.setAmount(amount);
		admin.sendMessage(Messages.PREFIX + "Added " + amount + " " + item.getType().toString() + " to " + player + "'s account.");
		return true;
	}

	public static boolean withdraw(ItemBank plugin, Player admin, String player, String name, int amount)
	{
		if (amount == 0)
			amount = 64;
		
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
			admin.sendMessage(Messages.NULL_POINTER);
			return false;
		}
		
		if (item == null)
		{
			admin.sendMessage(Messages.getAliasError(name));
			return false;
		}
		
		if (item.getType() == Material.AIR)
		{
			admin.sendMessage(Messages.AIR_BLOCK);
			return false;
		}
		
		String itemPath = item.getType().toString().toLowerCase() + "." + item.getDurability();
		plugin.playerFile = new File(plugin.playerDataDir + "/" + player + ".yml");
		plugin.playerData = new YamlConfiguration();
		try
		{
			plugin.playerData.load(plugin.playerFile);
		}
		catch (FileNotFoundException e)
		{
			admin.sendMessage(Messages.FILE_NOT_FOUND);
			return false;
		}
		catch (IOException e)
		{
			admin.sendMessage(Messages.IO_EXCEPTION);
			return false;
		}
		catch (InvalidConfigurationException e)
		{
			admin.sendMessage(Messages.YAML_EXCEPTION);
			return false;
		}
		
		int oldAmount = plugin.playerData.getInt(itemPath);
		if (amount > oldAmount)
			amount = oldAmount;
		
		int newAmount = oldAmount - amount;
		plugin.playerData.set(itemPath, amount);
		try
		{
			plugin.playerData.save(plugin.playerFile);
		}
		catch (IOException e)
		{
			admin.sendMessage(Messages.IO_EXCEPTION);
			plugin.playerData.set(itemPath, oldAmount);
			return false;
		}
		
		admin.sendMessage(Messages.PREFIX + "Removed " + newAmount + " " + item.getType().toString() + " from " + player + "'s account.");
		return true;
	}
}

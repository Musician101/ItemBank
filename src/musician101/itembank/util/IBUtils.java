package musician101.itembank.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import musician101.itembank.Config;
import musician101.itembank.ItemBank;
import musician101.itembank.exceptions.InvalidAliasException;
import musician101.itembank.lib.Constants;
import musician101.itembank.listeners.PlayerListener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Musician101
 */
public class IBUtils
{
	/**
	 * Method for finding specific blocks/items in a player's inventory
	 * 
	 * @param player Player who's inventory is being checked.
	 * @param material The material that is being searched for.
	 * @param data The damage value of the material (i.e. if material = oak wood then dmg = 1).
	 * @return The amount of the material in the player's inventory.
	 */
	public static int getAmount(Player player, Material material, short data)
	{
		int has = 0;
		for (ItemStack item : player.getInventory().getContents())
		{
			if ((item != null) && (item.getType() == material) && (item.getAmount() > 0) && (item.getDurability() == data))
				has += item.getAmount();
		}
		return has;
	}
	
	/**
	 * Find's a material's ID and Damage value.
	 * 
	 * @param plugin Reference the plugin's main class.
	 * @param name The id of the material.
	 * @param amount The amount of the material.
	 * @return Returns the ItemStack with the proper ID, Durability and the Amount.
	 */
	public static ItemStack getItem(ItemBank plugin, String name, int amount)
	{
		if (name == null) return null;
		short data;
		String datas = null;
		name = name.trim().toUpperCase();
		if (name.contains(":"))
		{
			if (name.split(":").length < 2)
			{
				datas = null;
				name = name.split(":")[0];
			}
			else
			{
				datas = name.split(":")[1];
				name = name.split(":")[0];
			}
		}
		try
		{
			data = Short.valueOf(datas);
		}
		catch (Exception e)
		{
			if (datas != null) return null;
			else data = 0;
		}
		Material material = Material.getMaterial(name);
		if (material == null)
		{
			try
			{
				/** 
				 * Deprecated method Material.getMaterial(int) in Bukkit.
				 * Waiting for a proper alternative before fixing.
				 */
				material = Material.getMaterial(Integer.valueOf(name));
				if (material == null) return null;
			}
			catch (Exception e)
			{
				return null;
			}
		}
		ItemStack item = new ItemStack(material);
		if (amount == 0) amount = 64;
		item.setAmount(amount);
		if (data != 0) item.setDurability(data);
		return item;
	}
	
	/**
	 * Find's a material's ID and Damage value from an alias.
	 * 
	 * @param plugin Reference the plugin's main class.
	 * @param name The alias of the material.
	 * @param amount The amount of the material.
	 * @return Returns the ItemStack with the proper ID, Durability and the Amount.
	 * @throws InvalidAliasException
	 * @throws NullPointerException
	 */
	public static ItemStack getIdFromAlias(ItemBank plugin, String alias, int amount) throws InvalidAliasException, NullPointerException
	{
		ItemStack item;
		if (plugin.translator == null)
			throw new NullPointerException("Error: items.csv is not loaded.");
		
		item = getItem(plugin, plugin.translator.getIdFromAlias(alias), amount);
		if (item == null)
			throw new InvalidAliasException(alias + " is not a valid alias!");
		
		return item;
	}
	
	/**
	 * Creates a file for the specified player.
	 * 
	 * @param plugin Reference's the plugin's main class.
	 * @param player The player who's having their data file created.
	 */
	public static void createPlayerFile(ItemBank plugin, File file)
	{
		if (!file.exists())
		{
			try
			{
				file.createNewFile();
				BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
				bw.write(PlayerListener.template);
				bw.close();
			}
			catch (IOException e)
			{
				plugin.getLogger().warning(Constants.IO_EXCEPTION);
			}
		}
	}
	
	/**
	 * A loop for creating player data files.
	 * 
	 * @param plugin Reference's the plugin's main class.
	 * @param players A list of players who are online when the method is executed.
	 */
	public static void createPlayerFiles(ItemBank plugin, Player[] players)
	{
		if (players.length > 0)
		{
			for (Player player : players)
			{
				createPlayerFile(plugin, new File(plugin.playerDataDir + "/" + player.getName().toLowerCase() + ".yml"));
			}
		}
	}
	
	/**
	 * Check if the player has enough money.
	 * 
	 * @param plugin Reference's the main class.
	 * @param config Provides access to the config options.
	 * @param player The player involved.
	 * @return false if the player does not have enough money, else true.
	 */
	public static boolean checkEconomy(ItemBank plugin, Config config, Player player)
	{
		if (!(plugin.getEconomy().isEnabled() && config.enableVault))
			return true;
			
		double money = plugin.getEconomy().getMoney(player.getName());
		double cost = config.transactionCost;
		if (money < cost)
		{
			player.sendMessage(Constants.PREFIX + "You lack the money to cover the transaction fee.");
			return false;
		}
		
		plugin.getEconomy().takeMoney(player.getName(), cost);
		return true;
	}
}

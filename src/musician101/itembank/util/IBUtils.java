package musician101.itembank.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import musician101.itembank.ItemBank;
import musician101.itembank.lib.Constants;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class IBUtils
{	
	public static void createPlayerFile(File file)
	{
		if (!file.exists())
		{
			try
			{
				file.createNewFile();
				BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
				bw.write(Constants.NEW_PLAYER_FILE);
				bw.close();
			}
			catch (IOException e)
			{
				Bukkit.getLogger().warning(Constants.IO_EX);
			}
		}
	}
	
	public static void createPlayerFiles(ItemBank plugin)
	{
		Player[] players = Bukkit.getOnlinePlayers();
		if (players.length > 0)
			for (Player player : players)
				createPlayerFile(new File(plugin.playerData, player.getName() + ".yml"));
	}
	
	public static int getAmount(Inventory inv, Material material, short durability)
	{
		int amount = 0;
		for (ItemStack item : inv.getContents())
			if ((item != null) && (item.getType() == material) && item.getDurability() == durability)
				amount += item.getAmount();
		
		return amount;
	}
	
	public static Inventory getAccount(ItemBank plugin, String playerName, int page) throws FileNotFoundException, IOException, InvalidConfigurationException
	{
		final Inventory inv = Bukkit.createInventory(plugin.getServer().getPlayer(playerName), 54, playerName + " - Page " + page);
		File file = new File(plugin.playerData, playerName + ".yml");
		if (inv != null)
			createPlayerFile(file);
		
		YamlConfiguration account = new YamlConfiguration();
		account.load(file);
		for (int slot = 0; slot < 54; slot++)
			inv.setItem(slot, account.getItemStack(page + "." + slot));
		
		return inv;
	}
	
	public static void saveAccount(ItemBank plugin, String player, Inventory inventory, int page) throws FileNotFoundException, IOException, InvalidConfigurationException
	{
		for (int slot = 0; slot < inventory.getSize(); slot++)
		{
			File file = new File(plugin.playerData, player + ".yml");
			YamlConfiguration account = new YamlConfiguration();
			account.load(file);
			try
			{
				account.set(page + "." + slot, inventory.getItem(slot));
			}
			catch (StringIndexOutOfBoundsException e){}
			
			account.save(file);
		}
	}
	
	public static boolean isNumber(String s)
	{
		try
		{
			Integer.valueOf(s);
		}
		catch (NumberFormatException e)
		{
			return false;
		}
		
		return true;
	}
}

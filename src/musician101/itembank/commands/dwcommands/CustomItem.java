package musician101.itembank.commands.dwcommands;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import musician101.itembank.ItemBank;
import musician101.itembank.exceptions.InvalidAliasException;
import musician101.itembank.lib.Messages;
import musician101.itembank.util.IBUtils;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

/**
 * The custom item handler for the Deposit and Withdraw commands.
 * 
 * @author Musician101
 */
public class CustomItem
{
	public static boolean deposit(ItemBank plugin, ItemStack item, Player player)
	{
		if (item == null || item.getType() == Material.AIR)
		{
			player.sendMessage(Messages.PREFIX + "Error: You're not holding anything.");
			return false;
		}
		
		if (!item.hasItemMeta())
		{
			player.sendMessage(Messages.PREFIX + "Error: This is not a custom item.");
			return false;
		}
		
		plugin.playerFile = new File(plugin.playerDataDir + "/" + player.getName().toLowerCase() + ".yml");
		plugin.playerData = new YamlConfiguration();
		try
		{
			plugin.playerData.load(plugin.playerFile);
		}
		catch (FileNotFoundException e)
		{
			player.sendMessage(Messages.FILE_NOT_FOUND);
			return false;
		}
		catch (IOException e)
		{
			player.sendMessage(Messages.IO_EXCEPTION);
			return false;
		}
		catch (InvalidConfigurationException e)
		{
			player.sendMessage(Messages.YAML_EXCEPTION);
			return false;
		}
		
		ItemMeta meta = item.getItemMeta();
		String itemPath = item.getType().toString().toLowerCase();
		if (meta.hasDisplayName())
			itemPath = meta.getDisplayName().replace(" ", "_");
		
		if (plugin.playerData.isSet(itemPath + ".amount") && plugin.playerData.getInt(itemPath + ".amount") > 0)
		{
			player.sendMessage(Messages.PREFIX + "Sorry but there's already an item with that name in your bank.");
			return false;
		}
		
		if (item.getType() == Material.WRITTEN_BOOK)
		{
			BookMeta bookMeta = (BookMeta) item.getItemMeta();
			if (bookMeta.hasTitle())
				itemPath = bookMeta.getTitle();
			else
			{
				player.sendMessage(Messages.PREFIX + "Please sign your book before depositing it.");
				return false;
			}
			
			if (bookMeta.hasAuthor())
				plugin.playerData.set(itemPath + ".author", bookMeta.getAuthor());
			
			if (bookMeta.hasPages())
			{
				int x = 1;
				for (String page : bookMeta.getPages())
				{
					plugin.playerData.set(itemPath + ".pages." + x, page);
					x++;
				}
			}
		}
		
		if (item.getType() == Material.SKULL_ITEM)
		{
			SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
			if (skullMeta.hasOwner())
				itemPath = skullMeta.getOwner();
		}
		
		if (item.getType() == Material.FIREWORK)
		{
			FireworkMeta fwMeta = (FireworkMeta) item.getItemMeta();
			plugin.playerData.set(itemPath + ".effects.power", fwMeta.getPower());
			int x = 1;
			for (FireworkEffect effect : fwMeta.getEffects())
			{
				plugin.playerData.set(itemPath + ".effects." + x + ".flicker", effect.hasFlicker());
				plugin.playerData.set(itemPath + ".effects." + x + ".trail", effect.hasTrail());
				int y = 1;
				for (Color color : effect.getColors())
				{
					plugin.playerData.set(itemPath + ".effects." + x + ".colors." + y + ".red", color.getRed());
					plugin.playerData.set(itemPath + ".effects." + x + ".colors." + y + ".green", color.getGreen());
					plugin.playerData.set(itemPath + ".effects." + x + ".colors." + y + ".blue", color.getBlue());
					y++;
				}
				y = 1;
				for (Color color : effect.getFadeColors())
				{
					plugin.playerData.set(itemPath + ".effects." + x + ".fadeColors." + y + ".red", color.getRed());
					plugin.playerData.set(itemPath + ".effects." + x + ".fadeColors." + y + ".green", color.getGreen());
					plugin.playerData.set(itemPath + ".effects." + x + ".fadeColors." + y + ".blue", color.getBlue());
					y++;
				}
				plugin.playerData.set(itemPath + ".effects." + x + ".type", effect.getType().toString());
				x++;
			}
		}
		
		plugin.playerData.set(itemPath + ".material", item.getType().toString());
		if (item.getType() != Material.FIREWORK && item.getType() != Material.WRITTEN_BOOK)
			plugin.playerData.set(itemPath + ".durability", item.getDurability());
		
		if (item.getType() == Material.FIREWORK)
			plugin.playerData.set(itemPath + ".effects.amount", plugin.playerData.getInt(itemPath + ".effects.amount") + item.getAmount());
		else
			plugin.playerData.set(itemPath + ".amount", plugin.playerData.getInt(itemPath + ".amount") + item.getAmount());
		
		if (meta.hasLore())
			plugin.playerData.set(itemPath + ".lore", meta.getLore());
		
		if (item.getItemMeta().hasEnchants())
			for (Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet())
				plugin.playerData.set(itemPath + ".enchantments." + entry.getKey().getName(), entry.getValue());
		
		try
		{
			plugin.playerData.save(plugin.playerFile);
		}
		catch (IOException e)
		{
			player.sendMessage(Messages.IO_EXCEPTION);
			plugin.playerData.set(itemPath + ".amount", 0);
			return false;
		}
		
		player.setItemInHand(null);
		if (item.getItemMeta().hasDisplayName())
			player.sendMessage(Messages.PREFIX + "You have deposited " + item.getItemMeta().getDisplayName() + ".");
		else if (item.getType() == Material.WRITTEN_BOOK)
			player.sendMessage(Messages.PREFIX + "You have deposited " + ((BookMeta) item.getItemMeta()).getTitle() + ".");
		else if (item.getType() == Material.SKULL_ITEM && item.getDurability() == 3)
			player.sendMessage(Messages.PREFIX + "You have deposited " + ((SkullMeta) item.getItemMeta()).getOwner() + "'s Head.");
		else
			player.sendMessage(Messages.PREFIX + "You have deposited a " + item.getType().toString() + ".");
		
		return true;
	}

	public static boolean withdraw(ItemBank plugin, Player player, String name)
	{
		plugin.playerFile = new File(plugin.playerDataDir + "/" + player.getName().toLowerCase() + ".yml");
		plugin.playerData = new YamlConfiguration();
		try
		{
			plugin.playerData.load(plugin.playerFile);
		}
		catch (FileNotFoundException e)
		{
			player.sendMessage(Messages.FILE_NOT_FOUND);
			return false;
		}
		catch (IOException e)
		{
			player.sendMessage(Messages.IO_EXCEPTION);
			return false;
		}
		catch (InvalidConfigurationException e)
		{
			player.sendMessage(Messages.YAML_EXCEPTION);
			return false;
		}
		
		ItemStack item = null;
		try
		{
			item = IBUtils.getItemFromAlias(plugin, name.toLowerCase(), 1);
		}
		catch (InvalidAliasException e)
		{
			item = IBUtils.getItem(name.toLowerCase(), 1);
		}
		catch (NullPointerException e)
		{
				player.sendMessage(Messages.NULL_POINTER);
				return false;
		}
		
		ItemMeta meta = null;
		BookMeta bookMeta = null;
		FireworkMeta fwMeta = null;
		SkullMeta skullMeta = null;
		if (item == null)
		{
			if (!plugin.playerData.isSet(name))
			{
				player.sendMessage(new String[]{Messages.getAliasError(name), Messages.PREFIX + "Check for capitalization."});
				return false;
			}
			
			item = new ItemStack(Material.getMaterial(plugin.playerData.getString(name + ".material").toUpperCase()));
		}
		
		try
		{
			if (plugin.playerData.isSet(name + ".durability"))
				item.setDurability(Short.valueOf(plugin.playerData.getString(name + ".durability")));
		}
		catch (NumberFormatException e)
		{
			player.sendMessage(Messages.getCustomItemWithdrawError(name));
			return false;
		}
		
		if (item.getType() == Material.WRITTEN_BOOK)
		{
			bookMeta = (BookMeta) item.getItemMeta();
			bookMeta.setTitle(name.replace("_", " "));
			bookMeta.setAuthor(plugin.playerData.getString(name + ".author"));
			if (plugin.playerData.isSet(name + ".pages"))
			{
				for (Map.Entry<String, Object> pages : plugin.playerData.getConfigurationSection(name + ".pages").getValues(true).entrySet())
				{
					try
					{
						bookMeta.addPage(plugin.playerData.getString(name + ".pages." + pages.getKey()));
					}
					catch (IllegalArgumentException e)
					{
						player.sendMessage(Messages.getCustomItemWithdrawError(name));
						return false;
					}
				}
			}
			item.setItemMeta((ItemMeta) bookMeta);
		}
		else if (item.getType() == Material.FIREWORK)
		{
			fwMeta = (FireworkMeta) item.getItemMeta();
			fwMeta.setPower(plugin.playerData.getInt(name + ".power"));
			int x = 1;
			while (plugin.playerData.isSet(name + ".effects." + x))
			{
				FireworkEffect.Builder effect = FireworkEffect.builder();
				effect.flicker(plugin.playerData.getBoolean(name + ".effects." + x + ".flicker"));
				effect.trail(plugin.playerData.getBoolean(name + ".effects." + x + ".trail"));
				int y = 1;
				while (plugin.playerData.isSet(name + ".effects." + x + ".colors." + y))
				{
					int red = plugin.playerData.getInt(name + ".effects." + x + ".colors." + y + ".red");
					int green = plugin.playerData.getInt(name + ".effects." + x + ".colors." + y + ".green");
					int blue = plugin.playerData.getInt(name + ".effects." + x + ".colors." + y + ".blue");
					effect.withColor(Color.fromRGB(red, green, blue));
					y++;
				}
				
				y = 1;
				while (plugin.playerData.isSet(name + ".effects." + x + ".fadeColors." + y))
				{
					int red = plugin.playerData.getInt(name + ".effects." + x + ".fadeColors." + y + ".red");
					int green = plugin.playerData.getInt(name + ".effects." + x + ".fadeColors." + y + ".green");
					int blue = plugin.playerData.getInt(name + ".effects." + x + ".fadeColors." + y + ".blue");
					effect.withFade(Color.fromRGB(red, green, blue));
					y++;
				}
				
				try
				{
					effect.with(FireworkEffect.Type.valueOf(plugin.playerData.getString(name + ".effects." + x + ".type").toUpperCase()));
				}
				catch (IllegalArgumentException | NullPointerException e)
				{
					player.sendMessage(Messages.PREFIX + "Could not set FireworkEffect.Type.");
					return false;
				}
				
				fwMeta.addEffect(effect.build());
				x++;
			}
			item.setItemMeta(fwMeta);
		}
		else if (item.getType() == Material.SKULL_ITEM)
		{
			skullMeta = (SkullMeta) item.getItemMeta();
			skullMeta.setOwner(name);
			player.sendMessage(skullMeta.getOwner());
			item.setItemMeta(skullMeta);
		}
		else
		{
			meta = item.getItemMeta();
			if (!name.equalsIgnoreCase(item.getType().toString()))
				meta.setDisplayName(name.replace("_", " "));
			
			item.setItemMeta(meta);
			if (plugin.playerData.isSet(name + ".enchantments"))
			{
				for (Map.Entry<String, Object> enchant : plugin.playerData.getConfigurationSection(name + ".enchantments").getValues(true).entrySet())
				{
					try
					{						
						item.addEnchantment(Enchantment.getByName(enchant.getKey().toUpperCase()), Integer.valueOf(enchant.getValue().toString()));
					}
					catch (IllegalArgumentException e)
					{
						player.sendMessage(Messages.getCustomItemWithdrawError(name));
						return false;
					}
				}
			}
			
			if (plugin.playerData.isSet(name + ".lore"))
				meta.setLore(plugin.playerData.getStringList(name + ".lore"));
		}
		
		try
		{
			item.setAmount(plugin.playerData.getInt(name + ".amount"));
		}
		catch (NumberFormatException e)
		{
			player.sendMessage(Messages.getCustomItemWithdrawError(name));
			return false;
		}
		
		int freeSpace = 0;
		for (ItemStack is : player.getInventory())
		{
			if (is == null)
				freeSpace += item.getType().getMaxStackSize();
			else if (is.getType() == item.getType())
				freeSpace += is.getType().getMaxStackSize() - is.getAmount();
		}
		
		if (freeSpace == 0)
		{
			player.sendMessage(Messages.FULL_INV);
			return false;
		}
		
		if (item.getAmount() > freeSpace)
			item.setAmount(freeSpace);
		
		int oldAmount = plugin.playerData.getInt(name + ".amount");
		plugin.playerData.set(name + ".amount", oldAmount - item.getAmount());
		try
		{
			plugin.playerData.save(plugin.playerFile);
		}
		catch (IOException e)
		{
			player.sendMessage(Messages.IO_EXCEPTION);
			plugin.playerData.set(name + ".amount", oldAmount);
			return false;
		}
		
		player.getInventory().addItem(item);
		player.sendMessage(Messages.PREFIX + "You have withdrawn " + item.getAmount() + " " + name + " and now have a total of " + plugin.playerData.getInt(name + ".amount") + " left.");
		return true;
	}
}

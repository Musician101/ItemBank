package musician101.itembank.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import musician101.itembank.ItemBank;
import musician101.itembank.lib.Messages;
import net.minecraft.util.org.apache.commons.io.FilenameUtils;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
				if (FilenameUtils.getExtension(file.getName()).equals("json"))
					bw.write("{\"comment\":\"" + Messages.NEW_PLAYER_FILE.replace("# ", "").replace("\n", "") + "\"}");
				else
					bw.write(Messages.NEW_PLAYER_FILE);
				bw.close();
			}
			catch (IOException e)
			{
				Bukkit.getLogger().warning(Messages.IO_EX);
			}
		}
	}
	
	public static void createPlayerFiles(ItemBank plugin)
	{
		Player[] players = Bukkit.getOnlinePlayers();
		if (players.length > 0)
			for (Player player : players)
				createPlayerFile(new File(plugin.playerData, player.getName() + "." + plugin.config.format));
	}
	
	public static int getAmount(Inventory inv, Material material, short durability)
	{
		int amount = 0;
		for (ItemStack item : inv.getContents())
			if ((item != null) && (item.getType() == material) && item.getDurability() == durability)
				amount += item.getAmount();
		
		return amount;
	}
	
	public static Inventory getAccount(ItemBank plugin, String worldName, String playerName, int page) throws FileNotFoundException, IOException, InvalidConfigurationException, ParseException, SQLException
	{
		final Inventory inv = Bukkit.createInventory(plugin.getServer().getPlayer(playerName), 54, playerName + " - Page " + page);
		if (plugin.config.useMYSQL)
		{
			Statement statement = plugin.c.createStatement();
			statement.execute("CREATE TABLE IF NOT EXISTS ib_" + playerName + "(World varchar(255), Page int, Slot int, Material varchar(255), Damage int, Amount int, ItemMeta varchar(300));");
			for (int slot = 0; slot < inv.getSize(); slot++)
				inv.setItem(slot, getItem(statement.executeQuery("SELECT * FROM ib_" + playerName + " WHERE World = \"" + worldName + "\" AND Page = " + page + " AND Slot = " + slot + ";")));
			
			return inv;
		}
		
		File file = new File(plugin.playerData, playerName + "." + plugin.config.format);
		if (FilenameUtils.getExtension(file.getName()).equals("json"))
		{
			JSONParser parser = new JSONParser();
			JSONObject account = (JSONObject) parser.parse(new FileReader(file));
			
			if (!account.containsKey(worldName))
				return inv;
			
			JSONObject world = (JSONObject) account.get(worldName);
			if (!world.containsKey(page + ""))
				return inv;
			
			JSONObject pg = (JSONObject) world.get(page + "");
			for (int slot = 0; slot < inv.getSize(); slot++)
				if (pg.containsKey(slot + ""))
					inv.setItem(slot, getItem((JSONObject) pg.get(slot + "")));
			
			return inv;
		}
		
		if (inv != null)
			createPlayerFile(file);
		
		YamlConfiguration account = new YamlConfiguration();
		account.load(file);
		for (int slot = 0; slot < inv.getSize(); slot++)
			inv.setItem(slot, account.getItemStack(worldName + "." + page + "." + slot));
		
		return inv;
	}
	
	@SuppressWarnings("unchecked")
	public static void saveAccount(ItemBank plugin, String worldName, String playerName, Inventory inventory, int page) throws FileNotFoundException, IOException, InvalidConfigurationException, ParseException, SQLException
	{
		if (plugin.config.useMYSQL)
		{
			Statement statement = plugin.c.createStatement();
			statement.execute("CREATE TABLE IF NOT EXISTS ib_" + playerName + "(World varchar(255), Page int, Slot int, Material varchar(255), Damage int, Amount int, ItemMeta varchar(300));");
			for (int slot = 0; slot < inventory.getSize(); slot++)
			{
				statement.execute("DELETE FROM ib_" + playerName + " WHERE World = \"" + worldName + "\" AND Page = " + page + " AND Slot = " + slot + ";");
				ItemStack item = inventory.getItem(slot);
				if (item != null)
					statement.executeUpdate("INSERT INTO ib_" + playerName + "(World, Page, Slot, Material, Damage, Amount, ItemMeta) VALUES (\"" + worldName + "\", " + page + ", " + slot + ", \"" + item.getType().toString() + "\", " + item.getDurability() + ", " + item.getAmount() + ", \""+ metaToJson(item).toJSONString().replace("\"", "\\\"") + "\");");
			}
			
			return;
		}

		File file = new File(plugin.playerData, playerName + "." + plugin.config.format);
		if (FilenameUtils.getExtension(file.getName()).equals("json"))
		{
			JSONParser parser = new JSONParser();
			JSONObject account = (JSONObject) parser.parse(new FileReader(file));
			JSONObject pg = new JSONObject();
			JSONObject inv = new JSONObject();
			for (int slot = 0; slot < inventory.getSize(); slot++)
				if (inventory.getItem(slot) != null)
					inv.put(slot, itemToJson(inventory.getItem(slot)));
			
			pg.put(page, inv);
			if (account == null)
				account = new JSONObject();
			
			account.put(worldName, pg);
			FileWriter fw = new FileWriter(file);
			fw.write(account.toJSONString());
			fw.close();
			return;
		}
		
		YamlConfiguration account = new YamlConfiguration();
		account.load(file);
		for (int slot = 0; slot < inventory.getSize(); slot++)
			account.set(worldName + "." + page + "." + slot, inventory.getItem(slot));	
		
		account.save(file);
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
	
	public static void sendMessages(Player player, List<String> messages)
	{
		for (String message : messages)
			player.sendMessage(message);
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject itemToJson(ItemStack is)
	{
		JSONObject item = new JSONObject();
		item.put("material", is.getType().toString());
		item.put("damage", is.getDurability());
		item.put("amount", is.getAmount());
		item.put("meta", metaToJson(is));
		return item;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject metaToJson(ItemStack is)
	{
		if (is.hasItemMeta())
		{
			JSONObject meta = new JSONObject();
			if (is.getType() == Material.BOOK_AND_QUILL || is.getType() == Material.WRITTEN_BOOK)
			{
				BookMeta m = (BookMeta) is.getItemMeta();
				if (m.hasAuthor())
					meta.put("author", m.getAuthor());
				
				if (m.hasPages())
				{
					JSONArray pages = new JSONArray();
					for (String page : m.getPages())
						pages.add(page);
					
					meta.put("pages", pages);
				}
				
				if (m.hasTitle())
					meta.put("title", m.getTitle());
				
				if (m.hasDisplayName())
					meta.put("name", m.getDisplayName());
				
				if (m.hasEnchants())
				{
					JSONObject enchants = new JSONObject();
					for (Entry<Enchantment, Integer> enchant : m.getEnchants().entrySet())
						enchants.put(enchant.getKey().getName(), enchant.getValue());
					
					meta.put("enchants", enchants);
				}
				
				if (m.hasLore())
				{
					JSONArray lore = new JSONArray();
					for (String line : m.getLore())
						lore.add(line);
					
					meta.put("lore", lore);
				}
				
				return meta;
			}
			else if (is.getType() == Material.ENCHANTED_BOOK)
			{
				EnchantmentStorageMeta m = (EnchantmentStorageMeta) is.getItemMeta();
				if (m.hasStoredEnchants())
				{
					JSONObject enchants = new JSONObject();
					for (Entry<Enchantment, Integer> enchant : m.getStoredEnchants().entrySet())
						enchants.put(enchant.getKey().getName(), enchant.getValue());
					
					meta.put("stored-enchants", enchants);
				}
				
				if (m.hasDisplayName())
					meta.put("name", m.getDisplayName());
				
				if (m.hasEnchants())
				{
					JSONObject enchants = new JSONObject();
					for (Entry<Enchantment, Integer> enchant : m.getEnchants().entrySet())
						enchants.put(enchant.getKey().getName(), enchant.getValue());
					
					meta.put("enchants", enchants);
				}
				
				if (m.hasLore())
				{
					JSONArray lore = new JSONArray();
					for (String line : m.getLore())
						lore.add(line);
					
					meta.put("lore", lore);
				}
				
				return meta;
			}
			else if (is.getType() == Material.FIREWORK_CHARGE)
			{
				FireworkEffectMeta m = (FireworkEffectMeta) is.getItemMeta();
				if (m.hasEffect())
				{
					FireworkEffect fwEffect = m.getEffect();
					JSONObject effect = new JSONObject();
					effect.put("flicker", fwEffect.hasFlicker());
					effect.put("trail", fwEffect.hasTrail());
					effect.put("type", fwEffect.getType().toString());
					JSONArray colors = new JSONArray();
					for (Color fwColor : fwEffect.getColors())
					{
						JSONObject color = new JSONObject();
						color.put("BLUE", fwColor.getBlue());
						color.put("GREEN", fwColor.getGreen());
						color.put("RED", fwColor.getRed());
						colors.add(color);
					}
					
					effect.put("colors", colors);
					JSONArray fade = new JSONArray();
					for (Color fadeColor : fwEffect.getFadeColors())
					{
						JSONObject color = new JSONObject();
						color.put("BLUE", fadeColor.getBlue());
						color.put("GREEN", fadeColor.getGreen());
						color.put("RED", fadeColor.getRed());
						fade.add(fadeColor);
					}
					
					effect.put("fade-colors", fade);
					meta.put("effect", effect);
				}
				
				if (m.hasDisplayName())
					meta.put("name", m.getDisplayName());
				
				if (m.hasEnchants())
				{
					JSONObject enchants = new JSONObject();
					for (Entry<Enchantment, Integer> enchant : m.getEnchants().entrySet())
						enchants.put(enchant.getKey().getName(), enchant.getValue());
					
					meta.put("enchants", enchants);
				}
				
				if (m.hasLore())
				{
					JSONArray lore = new JSONArray();
					for (String line : m.getLore())
						lore.add(line);
					
					meta.put("lore", lore);
				}
				
				return meta;
			}
			else if (is.getType() == Material.FIREWORK)
			{
				FireworkMeta m = (FireworkMeta) is.getItemMeta();
				if (m.hasEffects())
				{
					JSONArray effects = new JSONArray();
					for (FireworkEffect fwEffect : m.getEffects())
					{
						JSONObject effect = new JSONObject();
						effect.put("flicker", fwEffect.hasFlicker());
						effect.put("trail", fwEffect.hasTrail());
						effect.put("type", fwEffect.getType().toString());
						JSONArray colors = new JSONArray();
						for (Color fwColor : fwEffect.getColors())
						{
							JSONObject color = new JSONObject();
							color.put("BLUE", fwColor.getBlue());
							color.put("GREEN", fwColor.getGreen());
							color.put("RED", fwColor.getRed());
							colors.add(color);
						}
						
						effect.put("colors", colors);
						JSONArray fade = new JSONArray();
						for (Color fadeColor : fwEffect.getFadeColors())
						{
							JSONObject color = new JSONObject();
							color.put("BLUE", fadeColor.getBlue());
							color.put("GREEN", fadeColor.getGreen());
							color.put("RED", fadeColor.getRed());
							fade.add(color);
						}
						
						effect.put("fade-colors", fade);
						effects.add(effect);
					}
					
					meta.put("power", m.getPower());
					meta.put("effects", effects);
				}
				
				if (m.hasDisplayName())
					meta.put("name", m.getDisplayName());
				
				if (m.hasEnchants())
				{
					JSONObject enchants = new JSONObject();
					for (Entry<Enchantment, Integer> enchant : m.getEnchants().entrySet())
						enchants.put(enchant.getKey().getName(), enchant.getValue());
					
					meta.put("enchants", enchants);
				}
				
				if (m.hasLore())
				{
					JSONArray lore = new JSONArray();
					for (String line : m.getLore())
						lore.add(line);
					
					meta.put("lore", lore);
				}
				
				return meta;
			}
			else if (is.getType() == Material.LEATHER_BOOTS || is.getType() == Material.LEATHER_CHESTPLATE || is.getType() == Material.LEATHER_HELMET || is.getType() == Material.LEATHER_LEGGINGS)
			{
				LeatherArmorMeta m = (LeatherArmorMeta) is.getItemMeta();
				Color c = m.getColor();
				JSONObject color = new JSONObject();
				color.put("BLUE", c.getBlue());
				color.put("GREEN", c.getGreen());
				color.put("RED", c.getRed());
				meta.put("color", color);
				if (m.hasDisplayName())
					meta.put("name", m.getDisplayName());
				
				if (m.hasEnchants())
				{
					JSONObject enchants = new JSONObject();
					for (Entry<Enchantment, Integer> enchant : m.getEnchants().entrySet())
						enchants.put(enchant.getKey().getName(), enchant.getValue());
					
					meta.put("enchants", enchants);
				}
				
				if (m.hasLore())
				{
					JSONArray lore = new JSONArray();
					for (String line : m.getLore())
						lore.add(line);
					
					meta.put("lore", lore);
				}
				
				return meta;
			}
			else if (is.getType() == Material.MAP)
			{
				MapMeta m = (MapMeta) is.getItemMeta();
				meta.put("scaling", m.isScaling());
				if (m.hasDisplayName())
					meta.put("name", m.getDisplayName());
				
				if (m.hasEnchants())
				{
					JSONObject enchants = new JSONObject();
					for (Entry<Enchantment, Integer> enchant : m.getEnchants().entrySet())
						enchants.put(enchant.getKey().getName(), enchant.getValue());
					
					meta.put("enchants", enchants);
				}
				
				if (m.hasLore())
				{
					JSONArray lore = new JSONArray();
					for (String line : m.getLore())
						lore.add(line);
					
					meta.put("lore", lore);
				}
				
				return meta;
			}
			else if (is.getType() == Material.POTION)
			{
				PotionMeta m = (PotionMeta) is.getItemMeta();
				if (m.hasCustomEffects())
				{
					JSONArray effects = new JSONArray();
					for (PotionEffect e : m.getCustomEffects())
					{
						JSONObject effect = new JSONObject();
						effect.put("duration", e.getDuration());
						effect.put("amplifier", e.getAmplifier());
						effect.put("effect", e.getType().getName());
						effect.put("ambient", e.isAmbient());
						effects.add(effect);
					}
					meta.put("effects", effects);
				}
				
				if (m.hasDisplayName())
					meta.put("name", m.getDisplayName());
				
				if (m.hasEnchants())
				{
					JSONObject enchants = new JSONObject();
					for (Entry<Enchantment, Integer> enchant : m.getEnchants().entrySet())
						enchants.put(enchant.getKey().getName(), enchant.getValue());
					
					meta.put("enchants", enchants);
				}
				
				if (m.hasLore())
				{
					JSONArray lore = new JSONArray();
					for (String line : m.getLore())
						lore.add(line);
					
					meta.put("lore", lore);
				}
				
				return meta;
			}
			else if (is.getType() == Material.SKULL_ITEM)
			{
				SkullMeta m = (SkullMeta) is.getItemMeta();
				if (m.hasOwner())
					meta.put("owner", m.getOwner());
				
				if (m.hasDisplayName())
					meta.put("name", m.getDisplayName());
				
				if (m.hasEnchants())
				{
					JSONObject enchants = new JSONObject();
					for (Entry<Enchantment, Integer> enchant : m.getEnchants().entrySet())
						enchants.put(enchant.getKey().getName(), enchant.getValue());
					
					meta.put("enchants", enchants);
				}
				
				if (m.hasLore())
				{
					JSONArray lore = new JSONArray();
					for (String line : m.getLore())
						lore.add(line);
					
					meta.put("lore", lore);
				}
				
				return meta;
			}
			
			ItemMeta m = is.getItemMeta();
			if (m.hasDisplayName())
				meta.put("name", m.getDisplayName());
			
			if (m.hasEnchants())
			{
				JSONObject enchants = new JSONObject();
				for (Entry<Enchantment, Integer> enchant : m.getEnchants().entrySet())
					enchants.put(enchant.getKey().getName(), enchant.getValue());
				
				meta.put("enchants", enchants);
			}
			
			if (m.hasLore())
			{
				JSONArray lore = new JSONArray();
				for (String line : m.getLore())
					lore.add(line);
				
				meta.put("lore", lore);
			}
			
			return meta;
		}
		
		return new JSONObject();
	}
	
	public static ItemMeta getMeta(JSONObject meta, ItemStack item)
	{
		if (item.getType() == Material.BOOK_AND_QUILL || item.getType() == Material.WRITTEN_BOOK)
		{
			BookMeta m = (BookMeta) item.getItemMeta();
			if (meta.containsKey("author"))
				m.setAuthor(meta.get("author").toString());
			
			if (meta.containsKey("pages"))
				for (Object page : (JSONArray) meta.get("pages"))
					m.addPage(page.toString());
			
			if (meta.containsKey("title"))
				m.setTitle(meta.get("title").toString());
			
			if (meta.containsKey("name"))
				m.setDisplayName(meta.get("name").toString());
			
			if (meta.containsKey("enchants"))
			{
				JSONObject enchants = (JSONObject) meta.get("enchants");
				for (Enchantment enchant : Enchantment.values())
					if (enchants.containsKey(enchant.getName()))
						m.addEnchant(enchant, Integer.valueOf(enchants.get(enchant.getName()).toString()), false);
			}
			
			if (meta.containsKey("lore"))
			{
				JSONArray lore = (JSONArray) meta.get("lore");
				List<String> l = new ArrayList<String>();
				for (Object line : lore)
					l.add(line.toString());
				
				m.setLore(l);
			}
			
			return m;
		}
		else if (item.getType() == Material.ENCHANTED_BOOK)
		{
			EnchantmentStorageMeta m = (EnchantmentStorageMeta) item.getItemMeta();
			if (meta.containsKey("stored-enchants"))
			{
				JSONObject enchants = (JSONObject) meta.get("stored-enchants");
				for (Enchantment enchant : Enchantment.values())
					if (enchants.containsKey(enchant.getName()))
						m.addStoredEnchant(enchant, Integer.valueOf(enchants.get(enchant.getName()).toString()), false);
			}
			
			if (meta.containsKey("name"))
				m.setDisplayName(meta.get("name").toString());
			
			if (meta.containsKey("enchants"))
			{
				JSONObject enchants = (JSONObject) meta.get("enchants");
				for (Enchantment enchant : Enchantment.values())
					if (enchants.containsKey(enchant.getName()))
						m.addEnchant(enchant, Integer.valueOf(enchants.get(enchant.getName()).toString()), false);
			}
			
			if (meta.containsKey("lore"))
			{
				JSONArray lore = (JSONArray) meta.get("lore");
				List<String> l = new ArrayList<String>();
				for (Object line : lore)
					l.add(line.toString());
				
				m.setLore(l);
			}
			
			return m;
		}
		else if (item.getType() == Material.FIREWORK_CHARGE)
		{
			FireworkEffectMeta m = (FireworkEffectMeta) item.getItemMeta();
			if (meta.containsKey("effect"))
			{
				JSONObject effect = (JSONObject) meta.get("effect");
				Builder fw = FireworkEffect.builder();
				if (effect.containsKey("flicker"))
					fw.flicker(Boolean.valueOf(effect.get("flicker").toString()));
				
				if (effect.containsKey("trail"))
					fw.trail(Boolean.valueOf(effect.get("trail").toString()));
				
				if (effect.containsKey("type"))
					fw.with(Type.valueOf(effect.get("type").toString()));
				
				JSONArray colors = (JSONArray) effect.get("colors");
				for (Object object : colors)
				{
					int b = 0;
					int g = 0;
					int r = 0;
					JSONObject color = (JSONObject) object;
					if (color.containsKey("BLUE"))
						b = Integer.valueOf(color.get("BLUE").toString());
					
					if (color.containsKey("GREEN"))
						g = Integer.valueOf(color.get("GREEN").toString());
					
					if (color.containsKey("RED"))
						r = Integer.valueOf(color.get("RED").toString());
					
					fw.withColor(Color.fromRGB(r, g, b));
				}
				
				JSONArray fade = (JSONArray) effect.get("fade-colors");
				for (Object object : fade)
				{
					int b = 0;
					int g = 0;
					int r = 0;
					JSONObject color = (JSONObject) object;
					if (color.containsKey("BLUE"))
						b = Integer.valueOf(color.get("BLUE").toString());
					
					if (color.containsKey("GREEN"))
						g = Integer.valueOf(color.get("GREEN").toString());
					
					if (color.containsKey("RED"))
						r = Integer.valueOf(color.get("RED").toString());
					
					fw.withFade(Color.fromRGB(r, g, b));
				}
				
				m.setEffect(fw.build());
			}
			
			if (meta.containsKey("name"))
				m.setDisplayName(meta.get("name").toString());
			
			if (meta.containsKey("enchants"))
			{
				JSONObject enchants = (JSONObject) meta.get("enchants");
				for (Enchantment enchant : Enchantment.values())
					if (enchants.containsKey(enchant.getName()))
						m.addEnchant(enchant, Integer.valueOf(enchants.get(enchant.getName()).toString()), false);
			}
			
			if (meta.containsKey("lore"))
			{
				JSONArray lore = (JSONArray) meta.get("lore");
				List<String> l = new ArrayList<String>();
				for (Object line : lore)
					l.add(line.toString());
				
				m.setLore(l);
			}
			
			return m;
		}
		else if (item.getType() == Material.FIREWORK)
		{
			FireworkMeta m = (FireworkMeta) item.getItemMeta();
			if (meta.containsKey("effects"))
			{
				JSONArray effects = (JSONArray) meta.get("effects");
				for (Object e : effects)
				{
					JSONObject effect = (JSONObject) e;
					Builder fw = FireworkEffect.builder();
					if (effect.containsKey("flicker"))
						fw.flicker(Boolean.valueOf(effect.get("flicker").toString()));
					
					if (effect.containsKey("trail"))
						fw.trail(Boolean.valueOf(effect.get("trail").toString()));
					
					if (effect.containsKey("type"))
						fw.with(Type.valueOf(effect.get("type").toString()));
					
					JSONArray colors = (JSONArray) effect.get("colors");
					for (Object object : colors)
					{
						int b = 0;
						int g = 0;
						int r = 0;
						JSONObject color = (JSONObject) object;
						if (color.containsKey("BLUE"))
							b = Integer.valueOf(color.get("BLUE").toString());
						
						if (color.containsKey("GREEN"))
							g = Integer.valueOf(color.get("GREEN").toString());
						
						if (color.containsKey("RED"))
							r = Integer.valueOf(color.get("RED").toString());
						
						fw.withColor(Color.fromRGB(r, g, b));
					}
					
					JSONArray fade = (JSONArray) effect.get("fade-colors");
					for (Object object : fade)
					{
						int b = 0;
						int g = 0;
						int r = 0;
						JSONObject color = (JSONObject) object;
						if (color.containsKey("BLUE"))
							b = Integer.valueOf(color.get("BLUE").toString());
						
						if (color.containsKey("GREEN"))
							g = Integer.valueOf(color.get("GREEN").toString());
						
						if (color.containsKey("RED"))
							r = Integer.valueOf(color.get("RED").toString());
						
						fw.withFade(Color.fromRGB(r, g, b));
					}
					
					m.addEffect(fw.build());
				}
			}
			
			if (meta.containsKey("name"))
				m.setDisplayName(meta.get("name").toString());
			
			if (meta.containsKey("enchants"))
			{
				JSONObject enchants = (JSONObject) meta.get("enchants");
				for (Enchantment enchant : Enchantment.values())
					if (enchants.containsKey(enchant.getName()))
						m.addEnchant(enchant, Integer.valueOf(enchants.get(enchant.getName()).toString()), false);
			}
			
			if (meta.containsKey("lore"))
			{
				JSONArray lore = (JSONArray) meta.get("lore");
				List<String> l = new ArrayList<String>();
				for (Object line : lore)
					l.add(line.toString());
				
				m.setLore(l);
			}
			
			return m;
		}
		else if (item.getType() == Material.LEATHER_BOOTS || item.getType() == Material.LEATHER_CHESTPLATE || item.getType() == Material.LEATHER_HELMET || item.getType() == Material.LEATHER_LEGGINGS)
		{
			LeatherArmorMeta m = (LeatherArmorMeta) item.getItemMeta();
			if (meta.containsKey("color"))
			{
				JSONObject color = (JSONObject) meta.get("color");
				int b = (color.containsKey("BLUE") ? Integer.valueOf(color.get("BLUE").toString()) : 0);
				int g = (color.containsKey("GREEN") ? Integer.valueOf(color.get("GREEN").toString()) : 0);
				int r = (color.containsKey("RED") ? Integer.valueOf(color.get("RED").toString()) : 0);
				m.setColor(Color.fromRGB(r, g, b));
			}
			
			if (meta.containsKey("name"))
				m.setDisplayName(meta.get("name").toString());
			
			if (meta.containsKey("enchants"))
			{
				JSONObject enchants = (JSONObject) meta.get("enchants");
				for (Enchantment enchant : Enchantment.values())
					if (enchants.containsKey(enchant.getName()))
						m.addEnchant(enchant, Integer.valueOf(enchants.get(enchant.getName()).toString()), false);
			}
			
			if (meta.containsKey("lore"))
			{
				JSONArray lore = (JSONArray) meta.get("lore");
				List<String> l = new ArrayList<String>();
				for (Object line : lore)
					l.add(line.toString());
				
				m.setLore(l);
			}
			
			return m;
		}
		else if (item.getType() == Material.MAP)
		{
			MapMeta m = (MapMeta) item.getItemMeta();
			if (meta.containsKey("scaling"))
				m.setScaling(Boolean.valueOf(meta.get("scaling").toString()));
			
			if (meta.containsKey("name"))
				m.setDisplayName(meta.get("name").toString());
			
			if (meta.containsKey("enchants"))
			{
				JSONObject enchants = (JSONObject) meta.get("enchants");
				for (Enchantment enchant : Enchantment.values())
					if (enchants.containsKey(enchant.getName()))
						m.addEnchant(enchant, Integer.valueOf(enchants.get(enchant.getName()).toString()), false);
			}
			
			if (meta.containsKey("lore"))
			{
				JSONArray lore = (JSONArray) meta.get("lore");
				List<String> l = new ArrayList<String>();
				for (Object line : lore)
					l.add(line.toString());
				
				m.setLore(l);
			}
			
			return m;
		}
		else if (item.getType() == Material.POTION)
		{
			PotionMeta m = (PotionMeta) item.getItemMeta();
			if (meta.containsKey("effects"))
			{
				JSONArray effects = (JSONArray) meta.get("effects");
				for (Object object : effects)
				{
					JSONObject e = (JSONObject) object;
					PotionEffectType type = (e.containsKey("effect") ? PotionEffectType.getByName(e.get("effect").toString()) : PotionEffectType.ABSORPTION);
					int duration = (e.containsKey("duration") ? Integer.valueOf(e.get("duration").toString()) : 0);
					int amplifier = (e.containsKey("amplifier") ? Integer.valueOf(e.get("amplifier").toString()) : 0);
					boolean ambient = (e.containsKey("ambient") ? Boolean.valueOf(e.get("ambient").toString()) : false);
					PotionEffect effect = new PotionEffect(type, duration, amplifier, ambient);
					m.addCustomEffect(effect, false);
				}
			}
			
			if (meta.containsKey("name"))
				m.setDisplayName(meta.get("name").toString());
			
			if (meta.containsKey("enchants"))
			{
				JSONObject enchants = (JSONObject) meta.get("enchants");
				for (Enchantment enchant : Enchantment.values())
					if (enchants.containsKey(enchant.getName()))
						m.addEnchant(enchant, Integer.valueOf(enchants.get(enchant.getName()).toString()), false);
			}
			
			if (meta.containsKey("lore"))
			{
				JSONArray lore = (JSONArray) meta.get("lore");
				List<String> l = new ArrayList<String>();
				for (Object line : lore)
					l.add(line.toString());
				
				m.setLore(l);
			}
			
			return m;
		}
		else if (item.getType() == Material.SKULL_ITEM)
		{
			SkullMeta m = (SkullMeta) item.getItemMeta();
			if (meta.containsKey("owner"))
				m.setOwner(meta.get("owner").toString());
			
			if (meta.containsKey("name"))
				m.setDisplayName(meta.get("name").toString());
			
			if (meta.containsKey("enchants"))
			{
				JSONObject enchants = (JSONObject) meta.get("enchants");
				for (Enchantment enchant : Enchantment.values())
					if (enchants.containsKey(enchant.getName()))
						m.addEnchant(enchant, Integer.valueOf(enchants.get(enchant.getName()).toString()), false);
			}
			
			if (meta.containsKey("lore"))
			{
				JSONArray lore = (JSONArray) meta.get("lore");
				List<String> l = new ArrayList<String>();
				for (Object line : lore)
					l.add(line.toString());
				
				m.setLore(l);
			}
			
			return m;
		}
		
		ItemMeta m = (ItemMeta) item.getItemMeta();
		if (meta.containsKey("name"))
			m.setDisplayName(meta.get("name").toString());
		
		if (meta.containsKey("enchants"))
		{
			JSONObject enchants = (JSONObject) meta.get("enchants");
			for (Enchantment enchant : Enchantment.values())
				if (enchants.containsKey(enchant.getName()))
					m.addEnchant(enchant, Integer.valueOf(enchants.get(enchant.getName()).toString()), false);
		}
		
		if (meta.containsKey("lore"))
		{
			JSONArray lore = (JSONArray) meta.get("lore");
			List<String> l = new ArrayList<String>();
			for (Object line : lore)
				l.add(line.toString());
			
			m.setLore(l);
		}
		
		return m;
	}
	
	public static ItemStack getItem(JSONObject item)
	{
		ItemStack is = new ItemStack(Material.getMaterial(item.get("material").toString()), Integer.valueOf(item.get("amount").toString()), Short.valueOf(item.get("damage").toString()));
		is.setItemMeta(getMeta((JSONObject) item.get("meta"), is));
		return is;
	}
	
	public static ItemStack getItem(ResultSet rs) throws SQLException
	{
		while (rs.next())
		{
			ItemStack item = new ItemStack(Material.getMaterial(rs.getString("Material")), rs.getInt("Amount"), (short) rs.getInt("Damage"));
			item.setItemMeta(getMeta((JSONObject) JSONValue.parse(rs.getString("ItemMeta")), item));
			return item;
		}
		
		return null;
	}
	
	public static void convertToMultiWorld(ItemBank plugin)
	{
		if (plugin.config.useMYSQL)
		{
			return;
		}
		
		if (!plugin.config.format.equals("yml"))
			return;
		
		for (File file : plugin.playerData.listFiles())
		{
			YamlConfiguration oldAccount = new YamlConfiguration();
			YamlConfiguration newAccount = new YamlConfiguration();
			try
			{
				oldAccount.load(file);
				if (!oldAccount.isSet("isMultiWorld") || !oldAccount.getBoolean("isMultiWorld"))
				{
					for (Entry<String, Object> entry : oldAccount.getValues(true).entrySet())
						if (!(entry.getValue() instanceof MemorySection))
							newAccount.set(Bukkit.getWorlds().get(0).getName() + "." + entry.getKey(), oldAccount.getItemStack(entry.getKey()));
					
					newAccount.set("isMultiWorld", true);
					file.delete();
					IBUtils.createPlayerFile(file);
					newAccount.save(file);
				}
			}
			catch (FileNotFoundException e)
			{e.printStackTrace();}
			catch (IOException e)
			{
				plugin.getLogger().warning("Could not convert file " + file.getName() + ": I/O Error.");
			}
			catch (InvalidConfigurationException e)
			{
				plugin.getLogger().warning("Could not convert file " + file.getName() + ": YAML Format Error.");
			}
		}
	}

	public static String getWorldName(ItemBank plugin, Player player)
	{
		if (plugin.config.multiWorld.equals("none"))
			return Bukkit.getWorlds().get(0).getName();
		else if (plugin.config.multiWorld.equals("partial"))
			return player.getWorld().getName().replace("_nether", "").replace("_the_end", "");
		
		return player.getWorld().getName();
	}
}

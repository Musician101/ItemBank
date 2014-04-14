package musician101.itembank.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
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

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
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
				createPlayerFile(new File(plugin.playerData, player.getUniqueId() + ".yml"));
	}
	
	public static int getAmount(Inventory inv, Material material, short durability)
	{
		int amount = 0;
		for (ItemStack item : inv.getContents())
			if ((item != null) && (item.getType() == material) && item.getDurability() == durability)
				amount += item.getAmount();
		
		return amount;
	}
	
	public static Inventory getAccount(ItemBank plugin, String playerName, int page) throws FileNotFoundException, IOException, InvalidConfigurationException, SQLException
	{
		final Inventory inv = Bukkit.createInventory(plugin.getServer().getPlayer(playerName), 54, playerName + " - Page " + page);
		if (plugin.config.useMYSQL)
		{
			Statement statement = plugin.c.createStatement();
			statement.execute("CREATE TABLE IF NOT EXISTS ib_" + playerName + "(Page int, Slot int, Material varchar(255), Damage int, Amount int, ItemMeta varchar(300));");
			for (int slot = 0; slot < inv.getSize(); slot++)
				inv.setItem(slot, getItem(statement.executeQuery("SELECT * FROM ib_" + playerName + " WHERE Page = " + page + " AND Slot = " + slot + ";")));
			
			return inv;
		}
		
		File file = new File(plugin.playerData, playerName + ".yml");
		if (inv != null)
			createPlayerFile(file);
		
		YamlConfiguration account = new YamlConfiguration();
		account.load(file);
		for (int slot = 0; slot < 54; slot++)
			inv.setItem(slot, account.getItemStack(page + "." + slot));
		
		return inv;
	}
	
	public static void saveAccount(ItemBank plugin, String playerName, Inventory inventory, int page) throws FileNotFoundException, IOException, InvalidConfigurationException, SQLException
	{
		if (plugin.config.useMYSQL)
		{
			Statement statement = plugin.c.createStatement();
			statement.execute("CREATE TABLE IF NOT EXISTS ib_" + playerName + "(Page int, Slot int, Material varchar(255), Damage int, Amount int, ItemMeta varchar(300));");
			for (int slot = 0; slot < inventory.getSize(); slot++)
			{
				statement.execute("DELETE FROM ib_" + playerName + " WHERE Page = " + page + " and Slot = " + slot + ";");
				ItemStack item = inventory.getItem(slot);
				if (item != null)
					statement.executeUpdate("INSERT INTO ib_" + playerName + "(Page, Slot, Material, Damage, Amount, ItemMeta) VALUES (" + page + ", " + slot + ", \"" + item.getType().toString() + "\", " + item.getDurability() + ", " + item.getAmount() + ", \""+ metaToJson(item).toJSONString().replace("\"", "\\\"") + "\");");
			}
			
			return;
		}
		
		for (int slot = 0; slot < inventory.getSize(); slot++)
		{
			File file = new File(plugin.playerData, playerName + ".yml");
			YamlConfiguration account = new YamlConfiguration();
			account.load(file);
			account.set("name", playerName);
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
	
	public static void sendMessages(Player player, List<String> messages)
	{
		for (String message : messages)
			player.sendMessage(message);
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
	
	public static ItemStack getItem(ResultSet rs) throws SQLException
	{
		while (rs.next())
		{
			ItemStack item = new ItemStack(Material.getMaterial(rs.getString("Material")), rs.getInt("Amount"), (short) rs.getInt("Damage"));
			if (item.getType() == Material.BOOK_AND_QUILL || item.getType() == Material.WRITTEN_BOOK)
			{
				JSONObject meta = (JSONObject) JSONValue.parse(rs.getString("ItemMeta"));
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
				
				item.setItemMeta(m);
				return item;
			}
			else if (item.getType() == Material.ENCHANTED_BOOK)
			{
				JSONObject meta = (JSONObject) JSONValue.parse(rs.getString("ItemMeta"));
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
				
				item.setItemMeta(m);
				return item;
			}
			else if (item.getType() == Material.FIREWORK_CHARGE)
			{
				JSONObject meta = (JSONObject) JSONValue.parse(rs.getString("ItemMeta"));
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
				
				item.setItemMeta(m);
				return item;
			}
			else if (item.getType() == Material.FIREWORK)
			{
				JSONObject meta = (JSONObject) JSONValue.parse(rs.getString("ItemMeta"));
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
				
				item.setItemMeta(m);
				return item;
			}
			else if (item.getType() == Material.LEATHER_BOOTS || item.getType() == Material.LEATHER_CHESTPLATE || item.getType() == Material.LEATHER_HELMET || item.getType() == Material.LEATHER_LEGGINGS)
			{
				JSONObject meta = (JSONObject) JSONValue.parse(rs.getString("ItemMeta"));
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
				
				item.setItemMeta(m);
				return item;
			}
			else if (item.getType() == Material.MAP)
			{
				JSONObject meta = (JSONObject) JSONValue.parse(rs.getString("ItemMeta"));
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
				
				item.setItemMeta(m);
				return item;
			}
			else if (item.getType() == Material.POTION)
			{
				JSONObject meta = (JSONObject) JSONValue.parse(rs.getString("ItemMeta"));
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
				
				item.setItemMeta(m);
				return item;
			}
			else if (item.getType() == Material.SKULL_ITEM)
			{
				JSONObject meta = (JSONObject) JSONValue.parse(rs.getString("ItemMeta"));
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
				
				item.setItemMeta(m);
				return item;
			}
			
			JSONObject meta = (JSONObject) JSONValue.parse(rs.getString("ItemMeta"));
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
			
			item.setItemMeta(m);
			return item;
		}
		
		return null;
	}
}

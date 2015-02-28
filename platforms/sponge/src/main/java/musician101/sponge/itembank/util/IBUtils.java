package musician101.sponge.itembank.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import musician101.sponge.itembank.ItemBank;
import musician101.sponge.itembank.lib.Reference;
import musician101.sponge.itembank.lib.Reference.Messages;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.item.Enchantment;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventories;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackBuilder;
import org.spongepowered.api.item.inventory.custom.CustomInventoryBuilder;
import org.spongepowered.api.item.inventory.custom.CustomInventoryFactory;
import org.spongepowered.api.item.inventory.properties.SlotIndex;
import org.spongepowered.api.item.inventory.types.OrderedInventory;
import org.spongepowered.api.text.translation.Translatable;
import org.spongepowered.api.text.translation.Translation;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.world.World;

import com.google.common.base.Optional;

import au.com.bytecode.opencsv.CSVReader;

public class IBUtils
{
	public static void createPlayerFile(File file) throws IOException
	{
		if (!file.exists())
		{
			file.createNewFile();
			BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
			if (file.getName().endsWith(".json"))
				bw.write("{\"_comment\":\"" + Messages.NEW_PLAYER_FILE + "\"}");
			else if (file.getName().endsWith(".csv"))
			{
				bw.write("# " + Messages.NEW_PLAYER_FILE + "\n");
				bw.write("# world|page|slot|material|damage/durability|amount|meta data");
			}
			else
				bw.write("# " + Messages.NEW_PLAYER_FILE);
			
			bw.close();
		}
	}
	
	public static void createPlayerFiles() throws IOException
	{
		Collection<Player> players = ItemBank.getGame().getServer().get().getOnlinePlayers();
		if (players.size() > 0)
			for (Player player : players)
				createPlayerFile(new File(ItemBank.getPlayerData(), player.getUniqueId() + "." + ItemBank.getConfig().format));
	}
	
	public static int getAmount(Inventory inv, ItemType type, short durability)
	{
		int amount = 0;
		for (Inventory slot : inv.query(type))
		{
			ItemStack item = slot.peek().get();
			if ((item != null) && (item.getItem() == type) && item.getDamage() == durability)
				amount += item.getQuantity();
		}
		
		return amount;
	}
	
	public static Inventory getAccount(String worldName, String uuid, int page) throws FileNotFoundException, IOException, ParseException
	{
		CustomInventoryBuilder invBuilder = Inventories.customInventoryBuilder();
		invBuilder.size(54);
		invBuilder.name(new Translatable()
		{
			@Override
			public Translation getTranslation()
			{
				return new Translation()
				{
					@Override
					public String get()
					{
						return "%p - Page pg#";
					}

					@Override
					public String get(Object... args)
					{
						String player = NameFetcher.getNameOf(UUID.fromString(args[0].toString()));
						String page = args[1].toString();
						return get().replace("%p", player).replace("pg#", page);
					}

					@Override
					public String getId()
					{
						return Reference.ID + ".inventory.name";
					}					
				};
			}
		});
		
		OrderedInventory inv = invBuilder.build();
		File file = new File(ItemBank.getPlayerData(), uuid + "." + ItemBank.getConfig().format);
		if (inv != null)
			createPlayerFile(file);
		
		if (file.getName().endsWith(".csv"))
		{
			for (String[] s : new CSVReader(new FileReader(file)).readAll())
			{
				if (!s[0].startsWith("#"))
				{
					if (Integer.parseInt(s[1]) == page)
					{
						ItemStackBuilder isb = getItemStackBuilder();
						isb.itemType(getItemType(s[3]));
						isb.damage(Integer.parseInt(s[4]));
						isb.quantity(Integer.parseInt(s[5]));
						ItemStack item = isb.build();
						//TODO no item meta data yet
						item.setItemMeta(getMeta((JSONObject) JSONValue.parse(s[6]), item));
						inv.set(new SlotIndex(Integer.parseInt(s[2])), item);
					}
				}
			}
			
			return inv;
		}
		else if (file.getName().endsWith("json"))
		{
			JSONParser parser = new JSONParser();
			JSONObject account = (JSONObject) parser.parse(new FileReader(file));
			if (!account.containsKey(page + ""))
				return inv;
			
			JSONObject pg = (JSONObject) account.get(page + "");
			for (int slot = 0; slot < inv.size(); slot++)
				if (pg.containsKey(slot + ""))
					inv.set(new SlotIndex(slot), getItem((JSONObject) pg.get(slot + "")));
			
			return inv;
		}
		
		return inv;
	}
	
	@SuppressWarnings("unchecked")
	public static void saveAccount(String world, String uuid, Inventory inventory, int page) throws FileNotFoundException, IOException, ParseException, SQLException
	{
		File file = new File(ItemBank.getPlayerData(), uuid + "." + ItemBank.getConfig().format);
		if (file.getName().endsWith("csv"))
		{
			List<String> account = new ArrayList<String>();
			for (String[] s : new CSVReader(new FileReader(file)).readAll())
			{
				if (!s[0].startsWith("#"))
				{
					if (Integer.parseInt(s[1]) != page)
						for (int slot = 0; slot < inventory.size(); slot++)
							if (Integer.parseInt(s[2]) != slot)
								account.add(s.toString());
						
				}
				else
					account.add(Arrays.toString(s).replace("[", "").replace("]", ""));
			}	
					
			for (int slot = 0; slot < inventory.size(); slot++)
			{
				Optional<ItemStack> item = inventory.query(new SlotIndex(slot)).peek();
				if (item.isPresent())
					account.add(page + "|" + slot + "|" + item.get().getItem().getId() + "|" + item.get().getDamage() + "|" + item.get().getQuantity() + "|" + metaToJson(item.get()).toJSONString());
			}
			
			file.delete();
			createPlayerFile(file);
			BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
			for (String line : account)
				bw.write(line + "\n");
			
			bw.close();
			return;
		}
		else if (file.getName().endsWith("json"))
		{
			JSONParser parser = new JSONParser();
			JSONObject account = (JSONObject) parser.parse(new FileReader(file));
			JSONObject pg = new JSONObject();
			JSONObject inv = new JSONObject();
			for (int slot = 0; slot < inventory.size(); slot++)
			{
				Optional<ItemStack> item = inventory.query(new SlotIndex(slot)).peek();
				if (item.isPresent())
					inv.put(slot, itemToJson(item.get()));
			}
			
			if (account == null)
				account = new JSONObject();
			
			account.put(pg, inv);
			FileWriter fw = new FileWriter(file);
			fw.write(account.toJSONString());
			fw.close();
			return;
		}
	}
	
	public static boolean isNumber(String s)
	{
		try
		{
			Integer.parseInt(s);
		}
		catch (NumberFormatException e)
		{
			return false;
		}
		
		return true;
	}
	
	public static void sendMessages(CommandSource source, List<String> messages)
	{
		for (String message : messages)
			source.sendMessage(message);
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject itemToJson(ItemStack is)
	{
		JSONObject item = new JSONObject();
		item.put("material", is.getItem().toString());
		item.put("damage", is.getDamage());
		item.put("amount", is.getQuantity());
		item.put("meta", metaToJson(is));
		return item;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject metaToJson(ItemStack is)
	{
		if (is.hasItemMeta())
		{
			JSONObject meta = new JSONObject();
			if (is.getItem() == ItemTypes.WRITABLE_BOOK || is.getItem() == ItemTypes.WRITTEN_BOOK)
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
						enchants.put(enchant.getKey().getId(), enchant.getValue());
					
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
			else if (is.getItem() == ItemTypes.ENCHANTED_BOOK)
			{
				EnchantmentStorageMeta m = (EnchantmentStorageMeta) is.getItemMeta();
				if (m.hasStoredEnchants())
				{
					JSONObject enchants = new JSONObject();
					for (Entry<Enchantment, Integer> enchant : m.getStoredEnchants().entrySet())
						enchants.put(enchant.getKey().getId(), enchant.getValue());
					
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
			else if (is.getItem() == ItemTypes.FIREWORK_CHARGE)
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
						enchants.put(enchant.getKey().getId(), enchant.getValue());
					
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
			else if (is.getItem() == ItemTypes.FIREWORKS)
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
						enchants.put(enchant.getKey().getId(), enchant.getValue());
					
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
			else if (is.getItem() == ItemTypes.LEATHER_BOOTS || is.getItem() == ItemTypes.LEATHER_CHESTPLATE || is.getItem() == ItemTypes.LEATHER_HELMET || is.getItem() == ItemTypes.LEATHER_LEGGINGS)
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
						enchants.put(enchant.getKey().getId(), enchant.getValue());
					
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
			else if (is.getItem() == ItemTypes.MAP)
			{
				MapMeta m = (MapMeta) is.getItemMeta();
				meta.put("scaling", m.isScaling());
				if (m.hasDisplayName())
					meta.put("name", m.getDisplayName());
				
				if (m.hasEnchants())
				{
					JSONObject enchants = new JSONObject();
					for (Entry<Enchantment, Integer> enchant : m.getEnchants().entrySet())
						enchants.put(enchant.getKey().getId(), enchant.getValue());
					
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
			else if (is.getItem() == ItemTypes.POTION)
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
						enchants.put(enchant.getKey().getId(), enchant.getValue());
					
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
			else if (is.getItem() == ItemTypes.SKULL)
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
						enchants.put(enchant.getKey().getId(), enchant.getValue());
					
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
					enchants.put(enchant.getKey().getId(), enchant.getValue());
				
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
		if (item.getItem() == ItemTypes.WRITABLE_BOOK || item.getItem() == ItemTypes.WRITTEN_BOOK)
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
				for (Enchantment enchant : ItemBank.getGame().getRegistry().getEnchantments())
					if (enchants.containsKey(enchant.getId()))
						m.addEnchant(enchant, Integer.parseInt(enchants.get(enchant.getId()).toString()), false);
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
		else if (item.getItem() == ItemTypes.ENCHANTED_BOOK)
		{
			EnchantmentStorageMeta m = (EnchantmentStorageMeta) item.getItemMeta();
			if (meta.containsKey("stored-enchants"))
			{
				JSONObject enchants = (JSONObject) meta.get("stored-enchants");
				for (Enchantment enchant : ItemBank.getGame().getRegistry().getEnchantments())
					if (enchants.containsKey(enchant.getId()))
						m.addStoredEnchant(enchant, Integer.parseInt(enchants.get(enchant.getId()).toString()), false);
			}
			
			if (meta.containsKey("name"))
				m.setDisplayName(meta.get("name").toString());
			
			if (meta.containsKey("enchants"))
			{
				JSONObject enchants = (JSONObject) meta.get("enchants");
				for (Enchantment enchant : ItemBank.getGame().getRegistry().getEnchantments())
					if (enchants.containsKey(enchant.getId()))
						m.addEnchant(enchant, Integer.parseInt(enchants.get(enchant.getId()).toString()), false);
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
		else if (item.getItem() == ItemTypes.FIREWORK_CHARGE)
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
						b = Integer.parseInt(color.get("BLUE").toString());
					
					if (color.containsKey("GREEN"))
						g = Integer.parseInt(color.get("GREEN").toString());
					
					if (color.containsKey("RED"))
						r = Integer.parseInt(color.get("RED").toString());
					
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
						b = Integer.parseInt(color.get("BLUE").toString());
					
					if (color.containsKey("GREEN"))
						g = Integer.parseInt(color.get("GREEN").toString());
					
					if (color.containsKey("RED"))
						r = Integer.parseInt(color.get("RED").toString());
					
					fw.withFade(Color.fromRGB(r, g, b));
				}
				
				m.setEffect(fw.build());
			}
			
			if (meta.containsKey("name"))
				m.setDisplayName(meta.get("name").toString());
			
			if (meta.containsKey("enchants"))
			{
				JSONObject enchants = (JSONObject) meta.get("enchants");
				for (Enchantment enchant : ItemBank.getGame().getRegistry().getEnchantments())
					if (enchants.containsKey(enchant.getId()))
						m.addEnchant(enchant, Integer.parseInt(enchants.get(enchant.getId()).toString()), false);
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
		else if (item.getItem() == ItemTypes.FIREWORKS)
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
							b = Integer.parseInt(color.get("BLUE").toString());
						
						if (color.containsKey("GREEN"))
							g = Integer.parseInt(color.get("GREEN").toString());
						
						if (color.containsKey("RED"))
							r = Integer.parseInt(color.get("RED").toString());
						
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
							b = Integer.parseInt(color.get("BLUE").toString());
						
						if (color.containsKey("GREEN"))
							g = Integer.parseInt(color.get("GREEN").toString());
						
						if (color.containsKey("RED"))
							r = Integer.parseInt(color.get("RED").toString());
						
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
				for (Enchantment enchant : ItemBank.getGame().getRegistry().getEnchantments())
					if (enchants.containsKey(enchant.getId()))
						m.addEnchant(enchant, Integer.parseInt(enchants.get(enchant.getId()).toString()), false);
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
		else if (item.getItem() == ItemTypes.LEATHER_BOOTS || item.getItem() == ItemTypes.LEATHER_CHESTPLATE || item.getItem() == ItemTypes.LEATHER_HELMET || item.getItem() == ItemTypes.LEATHER_LEGGINGS)
		{
			LeatherArmorMeta m = (LeatherArmorMeta) item.getItemMeta();
			if (meta.containsKey("color"))
			{
				JSONObject color = (JSONObject) meta.get("color");
				int b = (color.containsKey("BLUE") ? Integer.parseInt(color.get("BLUE").toString()) : 0);
				int g = (color.containsKey("GREEN") ? Integer.parseInt(color.get("GREEN").toString()) : 0);
				int r = (color.containsKey("RED") ? Integer.parseInt(color.get("RED").toString()) : 0);
				m.setColor(Color.fromRGB(r, g, b));
			}
			
			if (meta.containsKey("name"))
				m.setDisplayName(meta.get("name").toString());
			
			if (meta.containsKey("enchants"))
			{
				JSONObject enchants = (JSONObject) meta.get("enchants");
				for (Enchantment enchant : ItemBank.getGame().getRegistry().getEnchantments())
					if (enchants.containsKey(enchant.getId()))
						m.addEnchant(enchant, Integer.parseInt(enchants.get(enchant.getId()).toString()), false);
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
		else if (item.getItem() == ItemTypes.MAP)
		{
			MapMeta m = (MapMeta) item.getItemMeta();
			if (meta.containsKey("scaling"))
				m.setScaling(Boolean.valueOf(meta.get("scaling").toString()));
			
			if (meta.containsKey("name"))
				m.setDisplayName(meta.get("name").toString());
			
			if (meta.containsKey("enchants"))
			{
				JSONObject enchants = (JSONObject) meta.get("enchants");
				for (Enchantment enchant : ItemBank.getGame().getRegistry().getEnchantments())
					if (enchants.containsKey(enchant.getId()))
						m.addEnchant(enchant, Integer.parseInt(enchants.get(enchant.getId()).toString()), false);
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
		else if (item.getItem() == ItemTypes.POTION)
		{
			PotionMeta m = (PotionMeta) item.getItemMeta();
			if (meta.containsKey("effects"))
			{
				JSONArray effects = (JSONArray) meta.get("effects");
				for (Object object : effects)
				{
					JSONObject e = (JSONObject) object;
					PotionEffectType type = (e.containsKey("effect") ? PotionEffectType.getByName(e.get("effect").toString()) : PotionEffectType.ABSORPTION);
					int duration = (e.containsKey("duration") ? Integer.parseInt(e.get("duration").toString()) : 0);
					int amplifier = (e.containsKey("amplifier") ? Integer.parseInt(e.get("amplifier").toString()) : 0);
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
				for (Enchantment enchant : ItemBank.getGame().getRegistry().getEnchantments())
					if (enchants.containsKey(enchant.getId()))
						m.addEnchant(enchant, Integer.parseInt(enchants.get(enchant.getId()).toString()), false);
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
		else if (item.getItem() == ItemTypes.SKULL)
		{
			SkullMeta m = (SkullMeta) item.getItemMeta();
			if (meta.containsKey("owner"))
				m.setOwner(meta.get("owner").toString());
			
			if (meta.containsKey("name"))
				m.setDisplayName(meta.get("name").toString());
			
			if (meta.containsKey("enchants"))
			{
				JSONObject enchants = (JSONObject) meta.get("enchants");
				for (Enchantment enchant : ItemBank.getGame().getRegistry().getEnchantments())
					if (enchants.containsKey(enchant.getId()))
						m.addEnchant(enchant, Integer.parseInt(enchants.get(enchant.getId()).toString()), false);
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
			for (Enchantment enchant : ItemBank.getGame().getRegistry().getEnchantments())
				if (enchants.containsKey(enchant.getId()))
					m.addEnchant(enchant, Integer.parseInt(enchants.get(enchant.getId()).toString()), false);
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
		ItemStackBuilder isb = getItemStackBuilder();
		isb.itemType(getItemType(item.get("material").toString()));
		isb.damage(Integer.parseInt(item.get("damage").toString()));
		isb.quantity(Integer.parseInt(item.get("amount").toString()));
		ItemStack is = isb.build();
		is.setItemMeta(getMeta((JSONObject) item.get("meta"), is));
		return is;
	}
	
	public static ItemStack getItem(ResultSet rs) throws SQLException
	{
		while (rs.next())
		{
			ItemStackBuilder isb = getItemStackBuilder();
			isb.itemType(getItemType(rs.getString("Material")));
			isb.damage(rs.getInt("Damage"));
			isb.quantity(rs.getInt("Amount"));
			ItemStack item = isb.build();
			item.setItemMeta(getMeta((JSONObject) JSONValue.parse(rs.getString("ItemMeta")), item));
			return item;
		}
		
		return null;
	}
	
	public static String getWorldName(Player player)
	{
		if (ItemBank.getConfig().multiWorld.equals("none"))
			return getWorlds().get(0).getName();
		else if (ItemBank.getConfig().multiWorld.equals("partial"))
			return getWorlds().get(0).getName().replace("_nether", "").replace("_end", "");
		
		return player.getWorld().getName();
	}
	
	public static List<World> getWorlds()
	{
		return (List<World>) ItemBank.getGame().getServer().get().getWorlds();
	}
	
	public static ItemStackBuilder getItemStackBuilder()
	{
		return ItemBank.getGame().getRegistry().getItemBuilder();
	}
	
	public static ItemType getItemType(String id)
	{
		Optional<ItemType> optional = ItemBank.getGame().getRegistry().getItem(id);
		return optional.isPresent() ? optional.get() : null;
	}
}

package musician101.itembank.spigot.config.json;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import musician101.common.java.config.JSONConfig;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class SpigotJSONConfig extends JSONConfig
{
	public SpigotJSONConfig()
	{
		super();
	}
	
	public SpigotJSONConfig getSpigotJSONConfig(String key)
	{
		return (SpigotJSONConfig) get(key);
	}
	
	@SuppressWarnings("unchecked")
	public SpigotJSONConfig setSpigotJSONConfig(String key, SpigotJSONConfig config)
	{
		put(key, config);
		return this;
	}
	
	public List<SpigotJSONConfig> getSpigotJSONConfigList(String key)
	{
		List<SpigotJSONConfig> jsons = Lists.newArrayList();
		for (Object object : (JSONArray) get(key))
			jsons.add((SpigotJSONConfig) object);
		
		return jsons;
	}
	
	@SuppressWarnings("unchecked")
	public SpigotJSONConfig setSpigotJSONConfigList(String key, List<SpigotJSONConfig> configs)
	{
		put(key, configs);
		return this;
	}
	
	public Color getColor(String key)
	{
		SpigotJSONConfig colorJson = getSpigotJSONConfig(key);
		int blue = 0;
		int green = 0;
		int red = 0;
		
		if (colorJson.containsKey("BLUE"))
			blue = colorJson.getInteger("BLUE");
		
		if (colorJson.containsKey("GREEN"))
			green = colorJson.getInteger("GREEN");
		
		if (colorJson.containsKey("RED"))
			red = colorJson.getInteger("RED");
		
		return Color.fromRGB(red, green, blue);
	}
	
	public SpigotJSONConfig setColor(String key, Color color)
	{
		SpigotJSONConfig colorJson = new SpigotJSONConfig();
		colorJson.set("BLUE", color.getBlue());
		colorJson.set("GREEN", color.getGreen());
		colorJson.set("RED", color.getRed());
		setSpigotJSONConfig(key, colorJson);
		return this;
	}
	
	public List<Color> getColorList(String key)
	{
		List<Color> colors = Lists.newArrayList();
		for (SpigotJSONConfig colorJson : getSpigotJSONConfigList(key))
		{
			int blue = 0;
			int green = 0;
			int red = 0;
			
			if (colorJson.containsKey("BLUE"))
				blue = colorJson.getInteger("BLUE");
			
			if (colorJson.containsKey("GREEN"))
				green = colorJson.getInteger("GREEN");
			
			if (colorJson.containsKey("RED"))
				red = colorJson.getInteger("RED");
			
			colors.add(Color.fromRGB(red, green, blue));
		}
		
		return colors;
	}
	
	public SpigotJSONConfig setColorList(String key, List<Color> colors)
	{
		List<SpigotJSONConfig> colorsJson = Lists.newArrayList();
		for (Color color : colors)
		{
			SpigotJSONConfig colorJson = new SpigotJSONConfig();
			colorJson.set("BLUE", color.getBlue());
			colorJson.set("GREEN", color.getGreen());
			colorJson.set("RED", color.getRed());
			colorsJson.add(colorJson);
		}
		
		setSpigotJSONConfigList(key, colorsJson);
		return this;
	}
	
	public Map<Enchantment, Integer> getEnchants(String key)
	{
		SpigotJSONConfig enchantsJson = getSpigotJSONConfig(key);
		Map<Enchantment, Integer> enchants = Maps.newHashMap();
		for (Enchantment enchant : Enchantment.values())
			if (enchantsJson.containsKey(enchant.toString()))
				enchants.put(enchant, enchantsJson.getInteger(enchant.toString()));
		
		return enchants;
	}
	
	public SpigotJSONConfig setEnchants(String key, Map<Enchantment, Integer> enchants)
	{
		SpigotJSONConfig enchantsJson = new SpigotJSONConfig();
		for (Enchantment enchant : enchants.keySet())
			enchantsJson.set(enchant.toString(), enchants.get(enchant));
		
		setSpigotJSONConfig(key, enchantsJson);
		return this;
	}
	
	public FireworkEffect getFireworkEffect(String key)
	{
		SpigotJSONConfig fwJson = getSpigotJSONConfig(key);
		Builder fw = FireworkEffect.builder();
		if (fwJson.containsKey("flicker"))
			fw.flicker(fwJson.getBoolean("flicker"));
		
		if (fwJson.containsKey("trail"))
			fw.trail(fwJson.getBoolean("trail"));
		
		if (fwJson.containsKey("type"))
			fw.with(Type.valueOf(fwJson.getString("type")));
		
		if (fwJson.containsKey("colors"))
			for (Color color : getColorList("colors"))
				fw.withColor(color);
		
		if (fwJson.containsKey("fade-colors"))
			for (Color color : getColorList("fade-colors"))
				fw.withFade(color);
		
		return fw.build();
	}
	
	public SpigotJSONConfig setFireworkEffect(String key, FireworkEffect effect)
	{
		SpigotJSONConfig fwJson = new SpigotJSONConfig();
		fwJson.set("flicker", effect.hasFlicker());
		fwJson.set("trail", effect.hasTrail());
		fwJson.set("type", effect.getType().toString());
		fwJson.setColorList("colors", effect.getColors());
		fwJson.setColorList("fade-colors", effect.getFadeColors());
		setSpigotJSONConfig(key, fwJson);
		return this;
	}
	
	public List<FireworkEffect> getFireworkEffectList(String key)
	{
		List<FireworkEffect> effects = Lists.newArrayList();
		for (SpigotJSONConfig fwJson: getSpigotJSONConfigList(key))
		{
			Builder fw = FireworkEffect.builder();
			if (fwJson.containsKey("flicker"))
				fw.flicker(fwJson.getBoolean("flicker"));
			
			if (fwJson.containsKey("trail"))
				fw.trail(fwJson.getBoolean("trail"));
			
			if (fwJson.containsKey("type"))
				fw.with(Type.valueOf(fwJson.getString("type")));
			
			if (fwJson.containsKey("colors"))
				for (Color color : getColorList("colors"))
					fw.withColor(color);
			
			if (fwJson.containsKey("fade-colors"))
				for (Color color : getColorList("fade-colors"))
					fw.withFade(color);
			
			effects.add(fw.build());
		}
		
		return effects;
	}
	
	public SpigotJSONConfig setFireworkEffectList(String key, List<FireworkEffect> effects)
	{
		for (FireworkEffect effect : effects)
		{
			SpigotJSONConfig fwJson = new SpigotJSONConfig();
			fwJson.set("flicker", effect.hasFlicker());
			fwJson.set("trail", effect.hasTrail());
			fwJson.set("type", effect.getType().toString());
			fwJson.setColorList("colors", effect.getColors());
			fwJson.setColorList("fade-colors", effect.getFadeColors());
			setSpigotJSONConfig(key, fwJson);
		}
		
		return this;
	}
	
	public Inventory getInventory(String key)
	{
		if (!containsKey(key))
			return null;
		
		SpigotJSONConfig invJson = getSpigotJSONConfig(key);
		Inventory inv = null;
		InventoryType invType = InventoryType.valueOf(invJson.getString("type"));
		if (invType == InventoryType.CHEST)
			inv = Bukkit.createInventory(null, invJson.getInteger("slots"), invJson.getString("title"));
		else
			inv = Bukkit.createInventory(null, invType, invJson.getString("title"));
		
		if (invJson.containsKey("items"))
		{
			SpigotJSONConfig items = invJson.getSpigotJSONConfig("items");
			for (int x = 0; x < inv.getSize(); x++)
				inv.setItem(x, items.getItemStack(x));
		}
		
		return inv;
	}
	
	public SpigotJSONConfig setInventory(String key, Inventory inv)
	{
		SpigotJSONConfig invJson = new SpigotJSONConfig();
		invJson.set("type", inv.getType().toString());
		invJson.set("slots", inv.getSize());
		invJson.set("title", inv.getTitle());
		for (int x = 0; x > inv.getSize(); x++)
			if (inv.getItem(x) != null)
				invJson.setItemStack(x, inv.getItem(x));
		
		setSpigotJSONConfig(key, invJson);
		return this;
	}
	
	public ItemMeta getItemMeta(String key)
	{
		SpigotJSONConfig metaJson = getSpigotJSONConfig(key);
		return metaJson.toItemMeta();
	}
	
	public SpigotJSONConfig setItemMeta(String key, ItemMeta meta, Material material)
	{
		SpigotJSONConfig metaJson = new SpigotJSONConfig();
		String type = "";
		if (material == Material.ENCHANTED_BOOK)
		{
			setEnchantmentStorageMeta((EnchantmentStorageMeta) meta, metaJson);
			type = "ENCHANTED_STORAGE";
		}
		else if (material == Material.FIREWORK)
		{
			setFireworkMeta((FireworkMeta) meta, metaJson);
			type = "FIREWORK";
		}
		else if (material == Material.FIREWORK_CHARGE)
		{
			setFireworkEffectMeta((FireworkEffectMeta) meta, metaJson);
			type = "FIREWORK_EFFECT";
		}
		else if (material == Material.LEATHER_BOOTS || material == Material.LEATHER_CHESTPLATE || material == Material.LEATHER_HELMET || material == Material.LEATHER_LEGGINGS)
		{
			setLeatherArmorMeta((LeatherArmorMeta) meta, metaJson);
			type = "LEATHER_ARMOR";
		}
		else if (material == Material.MAP)
		{
			setMapMeta((MapMeta) meta, metaJson);
			type = "MAP";
		}
		else if (material == Material.POTION)
		{
			setPotionMeta((PotionMeta) meta, metaJson);
			type = "POTION";
		}
		else if (material == Material.SKULL_ITEM)
		{
			setSkullMeta((SkullMeta) meta, metaJson);
			type = "SKULL";
		}
		else if (material == Material.BOOK_AND_QUILL || material == Material.WRITTEN_BOOK)
		{
			setBookMeta((BookMeta) meta, metaJson);
			type = "WRITTEN_BOOK";
		}
		else
		{
			setGeneralItemMeta(meta, metaJson);
			type = "GENERAL";
		}
		
		metaJson.set("type", type);
		setSpigotJSONConfig(key, metaJson);
		return this;
	}
	
	public ItemMeta toItemMeta()
	{
		String metaType = getString("type");
		if (metaType.equals("ENCHANT_STORAGE"))
			return getEnchantedBookMeta(this);
		else if (metaType.equalsIgnoreCase("FIREWORK"))
			return getFireworkMeta(this);
		else if (metaType.equals("FIREWORK_EFFECT"))
			return getFireworkEffectMeta(this);
		else if (metaType.equals("LEATHER_ARMOR"))
			return getLeatherArmorMeta(this);
		else if (metaType.equals("MAP"))
			return getMapMeta(this);
		else if (metaType.equals("POTION"))
			return getPotionMeta(this);
		else if (metaType.equals("SKULL"))
			return getSkullMeta(this);
		else if (metaType.equals("WRITTEN_BOOK"))
			return getWrittenBookMeta(this);
		
		ItemMeta im = newItemMeta();
		getGeneralItemMeta(im, this);
		return im;
	}
	
	private EnchantmentStorageMeta getEnchantedBookMeta(SpigotJSONConfig metaJson)
	{
		EnchantmentStorageMeta meta = (EnchantmentStorageMeta) newItemMeta();
		if (metaJson.containsKey("stored-enchants"))
		{
			Map<Enchantment, Integer> enchants = getEnchants("stored-enchants");
			for (Enchantment enchant : enchants.keySet())
				meta.addStoredEnchant(enchant, enchants.get(enchant.getName()), false);
		}
		
		getGeneralItemMeta(meta, metaJson);
		return meta;
	}
	
	private SpigotJSONConfig setEnchantmentStorageMeta(EnchantmentStorageMeta meta, SpigotJSONConfig metaJson)
	{
		metaJson.setEnchants("stored-enchants", meta.getStoredEnchants());
		setGeneralItemMeta(meta, metaJson);
		return this;
	}
	
	private FireworkEffectMeta getFireworkEffectMeta(SpigotJSONConfig metaJson)
	{
		FireworkEffectMeta meta = (FireworkEffectMeta) newItemMeta();
		if (metaJson.containsKey("effect"))
			meta.setEffect(getFireworkEffect("effect"));
		
		getGeneralItemMeta(meta, metaJson);
		return meta;
	}
	
	private SpigotJSONConfig setFireworkEffectMeta(FireworkEffectMeta meta, SpigotJSONConfig metaJson)
	{
		if (meta.hasEffect())
			metaJson.setFireworkEffect("effect", meta.getEffect());
		
		setGeneralItemMeta(meta, metaJson);
		return this;
	}
	
	private FireworkMeta getFireworkMeta(SpigotJSONConfig metaJson)
	{
		FireworkMeta meta = (FireworkMeta) newItemMeta();
		if (metaJson.containsKey("effects"))
			for (FireworkEffect fw : getFireworkEffectList("effects"))
				meta.addEffect(fw);
		
		getGeneralItemMeta(meta, metaJson);
		return meta;
	}
	
	private SpigotJSONConfig setFireworkMeta(FireworkMeta meta, SpigotJSONConfig metaJson)
	{
		if (meta.hasEffects())
			metaJson.setFireworkEffectList("effects", meta.getEffects());
		
		getGeneralItemMeta(meta, metaJson);
		return this;
	}
	
	private LeatherArmorMeta getLeatherArmorMeta(SpigotJSONConfig metaJson)
	{
		LeatherArmorMeta meta = (LeatherArmorMeta) newItemMeta();
		if (metaJson.containsKey("color"))
			meta.setColor(metaJson.getColor("color"));
		
		getGeneralItemMeta(meta, metaJson);
		return meta;
	}
	
	private SpigotJSONConfig setLeatherArmorMeta(LeatherArmorMeta meta, SpigotJSONConfig metaJson)
	{
		metaJson.setColor("color", meta.getColor());
		getGeneralItemMeta(meta, metaJson);
		return this;
	}
	
	private MapMeta getMapMeta(SpigotJSONConfig metaJson)
	{
		MapMeta meta = (MapMeta) newItemMeta();
		if (metaJson.containsKey("scaling"))
			meta.setScaling(metaJson.getBoolean("scaling"));
		
		getGeneralItemMeta(meta, metaJson);
		return meta;
	}
	
	private SpigotJSONConfig setMapMeta(MapMeta meta, SpigotJSONConfig metaJson)
	{
		metaJson.set("scaling", meta.isScaling());
		getGeneralItemMeta(meta, metaJson);
		return this;
	}
	
	private PotionMeta getPotionMeta(SpigotJSONConfig metaJson)
	{
		PotionMeta meta = (PotionMeta) newItemMeta();
		if (metaJson.containsKey("effects"))
			for (PotionEffect effect : getPotionEffectList("effects"))
				meta.addCustomEffect(effect, true);
		
		getGeneralItemMeta(meta, metaJson);
		return meta;
	}
	
	private SpigotJSONConfig setPotionMeta(PotionMeta meta, SpigotJSONConfig metaJson)
	{
		if (meta.hasCustomEffects())
			metaJson.setPotionEffectList("effects", meta.getCustomEffects());
		
		setGeneralItemMeta(meta, metaJson);
		return this;
	}
	
	private SkullMeta getSkullMeta(SpigotJSONConfig metaJson)
	{
		SkullMeta meta = (SkullMeta) newItemMeta();
		if (metaJson.containsKey("owner"))
			meta.setOwner(metaJson.getString("owner"));
		
		getGeneralItemMeta(meta, metaJson);
		return meta;
	}
	
	private SpigotJSONConfig setSkullMeta(SkullMeta meta, SpigotJSONConfig metaJson)
	{
		if (meta.hasOwner())
			metaJson.set("owner", meta.getOwner());
		
		setGeneralItemMeta(meta, metaJson);
		return this;
	}
	
	private BookMeta getWrittenBookMeta(SpigotJSONConfig metaJson)
	{
		BookMeta meta = (BookMeta) newItemMeta();
		meta.setAuthor(metaJson.getString("author"));
		
		if (metaJson.containsKey("pages"))
			for (Object page : (JSONArray) metaJson.get("pages"))
				meta.addPage(page.toString());
		
		if (metaJson.containsKey("title"))
			meta.setTitle(metaJson.get("title").toString());
		
		getGeneralItemMeta(meta, metaJson);
		return meta;
	}
	
	private SpigotJSONConfig setBookMeta(BookMeta meta, SpigotJSONConfig metaJson)
	{
		if (meta.hasAuthor())
			metaJson.set("author", meta.getAuthor());
		
		if (meta.hasPages())
			metaJson.set("pages", meta.getPages());
		
		if (meta.hasTitle())
			metaJson.set("title", meta.getTitle());
		
		setGeneralItemMeta(meta, metaJson);
		return this;
	}
	
	private SpigotJSONConfig getGeneralItemMeta(ItemMeta im, SpigotJSONConfig metaJson)
	{
		if (metaJson.containsKey("displayName"))
			im.setDisplayName(metaJson.getString("displayName"));
		
		if (metaJson.containsKey("enchants"))
		{
			Map<Enchantment, Integer> enchants = getEnchants("enchants");
			for (Enchantment enchant : enchants.keySet())
				im.addEnchant(enchant, enchants.get(enchant.getName()), false);
		}
		
		if (metaJson.containsKey("lore"))
		{
			List<String> lore = metaJson.getList("lore");
			im.setLore(lore);
		}
		
		return this;
	}
	
	private SpigotJSONConfig setGeneralItemMeta(ItemMeta meta, SpigotJSONConfig metaJson)
	{
		if (meta.hasDisplayName())
			metaJson.set("name", meta.getDisplayName());
		
		if (meta.hasEnchants())
			metaJson.setEnchants("enchants", meta.getEnchants());
		
		if (meta.hasLore())
			metaJson.set("lore", meta.getLore());
		
		return this;
	}
	
	private ItemMeta newItemMeta()
	{
		return new ItemStack(Material.AIR).getItemMeta();
	}
	
	private ItemStack getItemStack(int key)
	{
		return getItemStack(key + "");
	}
	
	private SpigotJSONConfig setItemStack(int key, ItemStack item)
	{
		setItemStack(key + "", item);
		return this;
	}
	
	public ItemStack getItemStack(String key)
	{
		SpigotJSONConfig itemJson = getSpigotJSONConfig(key);
		ItemStack item = new ItemStack(itemJson.getMaterial("material"), itemJson.getInteger("amount"), itemJson.getShort("durability"));
		if (itemJson.containsKey("meta"))
			item.setItemMeta(itemJson.getItemMeta("meta"));
		
		return item;
	}
	
	public SpigotJSONConfig setItemStack(String key, ItemStack item)
	{
		SpigotJSONConfig itemJson = getSpigotJSONConfig(key);
		itemJson.setMaterial("material", item.getType());
		itemJson.set("amount", item.getAmount());
		itemJson.set("durability", item.getDurability());
		if (item.hasItemMeta())
			itemJson.setItemMeta("meta", item.getItemMeta(), item.getType());
		
		setSpigotJSONConfig(key, itemJson);
		return this;
	}
	
	public Material getMaterial(String key)
	{
		return Material.matchMaterial(getString(key).toUpperCase());
	}
	
	public SpigotJSONConfig setMaterial(String key, Material material)
	{
		set(key, material.toString());
		return this;
	}
	
	public PotionEffect getPotionEffect(String key)
	{
		SpigotJSONConfig potionJson = getSpigotJSONConfig(key);
		boolean ambient = potionJson.getBoolean("ambient");
		int amplifier = potionJson.getInteger("amplifier");
		int duration = potionJson.getInteger("duration");
		PotionEffectType type = PotionEffectType.getByName(potionJson.getString("effect"));
		return new PotionEffect(type, duration, amplifier, ambient);
	}
	
	public SpigotJSONConfig setPotionEffect(String key, PotionEffect effect)
	{
		SpigotJSONConfig potionJson = new SpigotJSONConfig();
		potionJson.set("effect", effect.getType().toString());
		potionJson.set("amplifier", effect.getAmplifier());
		potionJson.set("duration", effect.getDuration());
		potionJson.set("ambient", effect.isAmbient());
		return this;
	}
	
	public List<PotionEffect> getPotionEffectList(String key)
	{
		List<PotionEffect> effects = Lists.newArrayList();
		for (SpigotJSONConfig potionJson : getSpigotJSONConfigList(key))
		{
			boolean ambient = potionJson.getBoolean("ambient");
			int amplifier = potionJson.getInteger("amplifier");
			int duration = potionJson.getInteger("duration");
			PotionEffectType type = PotionEffectType.getByName(potionJson.getString("effect"));
			effects.add(new PotionEffect(type, duration, amplifier, ambient));
		}
		
		return effects;
	}
	
	public SpigotJSONConfig setPotionEffectList(String key, List<PotionEffect> effects)
	{
		List<SpigotJSONConfig> potionsJson = Lists.newArrayList();
		for (PotionEffect effect : effects)
		{
			SpigotJSONConfig potionJson = new SpigotJSONConfig();
			potionJson.set("effect", effect.getType().toString());
			potionJson.set("amplifier", effect.getAmplifier());
			potionJson.set("duration", effect.getDuration());
			potionJson.set("ambient", effect.isAmbient());
			potionsJson.add(potionJson);
		}
		
		setSpigotJSONConfigList(key, potionsJson);
		return this;
	}
	
	public static SpigotJSONConfig loadSpigotJSONConfig(File file) throws FileNotFoundException, IOException, ParseException
	{
		return (SpigotJSONConfig) new JSONParser().parse(new FileReader(file));
	}
	
	public static SpigotJSONConfig loadSpigotJSONConfig(String string) throws ParseException
	{
		return (SpigotJSONConfig) new JSONParser().parse(string);
	}
}

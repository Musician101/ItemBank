package musician101.itembank.spigot.config.json;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import musician101.itembank.common.config.json.AbstractJSONConfig;

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
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@SuppressWarnings("serial")
public class BukkitJSONConfig extends AbstractJSONConfig
{
	public BukkitJSONConfig()
	{
		super();
	}
	
	public BukkitJSONConfig getBukkitJSONConfig(String key)
	{
		return (BukkitJSONConfig) get(key);
	}
	
	@SuppressWarnings("unchecked")
	public BukkitJSONConfig setBukkitJSONConfig(String key, BukkitJSONConfig config)
	{
		put(key, config);
		return this;
	}
	
	public List<BukkitJSONConfig> getBukkitJSONConfigList(String key)
	{
		List<BukkitJSONConfig> jsons = Lists.newArrayList();
		for (Object object : (JSONArray) get(key))
			jsons.add((BukkitJSONConfig) object);
		
		return jsons;
	}
	
	@SuppressWarnings("unchecked")
	public BukkitJSONConfig setBukkitJSONConfigList(String key, List<BukkitJSONConfig> configs)
	{
		put(key, configs);
		return this;
	}
	
	public Color getColor(String key)
	{
		BukkitJSONConfig colorJson = getBukkitJSONConfig(key);
		int blue = 0;
		int green = 0;
		int red = 0;
		
		if (colorJson.containsKey("BLUE"))
			blue = colorJson.getInt("BLUE");
		
		if (colorJson.containsKey("GREEN"))
			green = colorJson.getInt("GREEN");
		
		if (colorJson.containsKey("RED"))
			red = colorJson.getInt("RED");
		
		return Color.fromRGB(red, green, blue);
	}
	
	public BukkitJSONConfig setColor(String key, Color color)
	{
		BukkitJSONConfig colorJson = new BukkitJSONConfig();
		colorJson.setInt("BLUE", color.getBlue());
		colorJson.setInt("GREEN", color.getGreen());
		colorJson.setInt("RED", color.getRed());
		setBukkitJSONConfig(key, colorJson);
		return this;
	}
	
	public List<Color> getColorList(String key)
	{
		List<Color> colors = Lists.newArrayList();
		for (BukkitJSONConfig colorJson : getBukkitJSONConfigList(key))
		{
			int blue = 0;
			int green = 0;
			int red = 0;
			
			if (colorJson.containsKey("BLUE"))
				blue = colorJson.getInt("BLUE");
			
			if (colorJson.containsKey("GREEN"))
				green = colorJson.getInt("GREEN");
			
			if (colorJson.containsKey("RED"))
				red = colorJson.getInt("RED");
			
			colors.add(Color.fromRGB(red, green, blue));
		}
		
		return colors;
	}
	
	public BukkitJSONConfig setColorList(String key, List<Color> colors)
	{
		List<BukkitJSONConfig> colorsJson = Lists.newArrayList();
		for (Color color : colors)
		{
			BukkitJSONConfig colorJson = new BukkitJSONConfig();
			colorJson.setInt("BLUE", color.getBlue());
			colorJson.setInt("GREEN", color.getGreen());
			colorJson.setInt("RED", color.getRed());
			colorsJson.add(colorJson);
		}
		
		setBukkitJSONConfigList(key, colorsJson);
		return this;
	}
	
	public Map<Enchantment, Integer> getEnchants(String key)
	{
		BukkitJSONConfig enchantsJson = getBukkitJSONConfig(key);
		Map<Enchantment, Integer> enchants = Maps.newHashMap();
		for (Enchantment enchant : Enchantment.values())
			if (enchantsJson.containsKey(enchant.toString()))
				enchants.put(enchant, enchantsJson.getInt(enchant.toString()));
		
		return enchants;
	}
	
	public BukkitJSONConfig setEnchants(String key, Map<Enchantment, Integer> enchants)
	{
		BukkitJSONConfig enchantsJson = new BukkitJSONConfig();
		for (Enchantment enchant : enchants.keySet())
			enchantsJson.setInt(enchant.toString(), enchants.get(enchant));
		
		setBukkitJSONConfig(key, enchantsJson);
		return this;
	}
	
	public FireworkEffect getFireworkEffect(String key)
	{
		BukkitJSONConfig fwJson = getBukkitJSONConfig(key);
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
	
	public BukkitJSONConfig setFireworkEffect(String key, FireworkEffect effect)
	{
		BukkitJSONConfig fwJson = new BukkitJSONConfig();
		fwJson.setBoolean("flicker", effect.hasFlicker());
		fwJson.setBoolean("trail", effect.hasTrail());
		fwJson.setString("type", effect.getType().toString());
		fwJson.setColorList("colors", effect.getColors());
		fwJson.setColorList("fade-colors", effect.getFadeColors());
		setBukkitJSONConfig(key, fwJson);
		return this;
	}
	
	public List<FireworkEffect> getFireworkEffectList(String key)
	{
		List<FireworkEffect> effects = Lists.newArrayList();
		for (BukkitJSONConfig fwJson: getBukkitJSONConfigList(key))
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
	
	public BukkitJSONConfig setFireworkEffectList(String key, List<FireworkEffect> effects)
	{
		for (FireworkEffect effect : effects)
		{
			BukkitJSONConfig fwJson = new BukkitJSONConfig();
			fwJson.setBoolean("flicker", effect.hasFlicker());
			fwJson.setBoolean("trail", effect.hasTrail());
			fwJson.setString("type", effect.getType().toString());
			fwJson.setColorList("colors", effect.getColors());
			fwJson.setColorList("fade-colors", effect.getFadeColors());
			setBukkitJSONConfig(key, fwJson);
		}
		
		return this;
	}
	
	public Inventory getInventory(String key)
	{
		if (!containsKey(key))
			return null;
		
		BukkitJSONConfig invJson = getBukkitJSONConfig(key);
		Inventory inv = null;
		InventoryType invType = InventoryType.valueOf(invJson.getString("type"));
		if (invType == InventoryType.CHEST)
			inv = Bukkit.createInventory(null, invJson.getInt("slots"), invJson.getString("title"));
		else
			inv = Bukkit.createInventory(null, invType, invJson.getString("title"));
		
		if (invJson.containsKey("items"))
		{
			BukkitJSONConfig items = invJson.getBukkitJSONConfig("items");
			for (int x = 0; x < inv.getSize(); x++)
				inv.setItem(x, items.getItemStack(x));
		}
		
		return inv;
	}
	
	public BukkitJSONConfig setInventory(String key, Inventory inv)
	{
		BukkitJSONConfig invJson = new BukkitJSONConfig();
		invJson.setString("type", inv.getType().toString());
		invJson.setInt("slots", inv.getSize());
		invJson.setString("title", inv.getTitle());
		for (int x = 0; x > inv.getSize(); x++)
			if (inv.getItem(x) != null)
				invJson.setItemStack(x, inv.getItem(x));
		
		setBukkitJSONConfig(key, invJson);
		return this;
	}
	
	public ItemMeta getItemMeta(String key)
	{
		BukkitJSONConfig metaJson = getBukkitJSONConfig(key);
		return metaJson.toItemMeta();
	}
	
	public BukkitJSONConfig setItemMeta(String key, ItemMeta meta, Material material)
	{
		BukkitJSONConfig metaJson = new BukkitJSONConfig();
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
		
		metaJson.setString("type", type);
		setBukkitJSONConfig(key, metaJson);
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
	
	private EnchantmentStorageMeta getEnchantedBookMeta(BukkitJSONConfig metaJson)
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
	
	private BukkitJSONConfig setEnchantmentStorageMeta(EnchantmentStorageMeta meta, BukkitJSONConfig metaJson)
	{
		metaJson.setEnchants("stored-enchants", meta.getStoredEnchants());
		setGeneralItemMeta(meta, metaJson);
		return this;
	}
	
	private FireworkEffectMeta getFireworkEffectMeta(BukkitJSONConfig metaJson)
	{
		FireworkEffectMeta meta = (FireworkEffectMeta) newItemMeta();
		if (metaJson.containsKey("effect"))
			meta.setEffect(getFireworkEffect("effect"));
		
		getGeneralItemMeta(meta, metaJson);
		return meta;
	}
	
	private BukkitJSONConfig setFireworkEffectMeta(FireworkEffectMeta meta, BukkitJSONConfig metaJson)
	{
		if (meta.hasEffect())
			metaJson.setFireworkEffect("effect", meta.getEffect());
		
		setGeneralItemMeta(meta, metaJson);
		return this;
	}
	
	private FireworkMeta getFireworkMeta(BukkitJSONConfig metaJson)
	{
		FireworkMeta meta = (FireworkMeta) newItemMeta();
		if (metaJson.containsKey("effects"))
			for (FireworkEffect fw : getFireworkEffectList("effects"))
				meta.addEffect(fw);
		
		getGeneralItemMeta(meta, metaJson);
		return meta;
	}
	
	private BukkitJSONConfig setFireworkMeta(FireworkMeta meta, BukkitJSONConfig metaJson)
	{
		if (meta.hasEffects())
			metaJson.setFireworkEffectList("effects", meta.getEffects());
		
		getGeneralItemMeta(meta, metaJson);
		return this;
	}
	
	private LeatherArmorMeta getLeatherArmorMeta(BukkitJSONConfig metaJson)
	{
		LeatherArmorMeta meta = (LeatherArmorMeta) newItemMeta();
		if (metaJson.containsKey("color"))
			meta.setColor(metaJson.getColor("color"));
		
		getGeneralItemMeta(meta, metaJson);
		return meta;
	}
	
	private BukkitJSONConfig setLeatherArmorMeta(LeatherArmorMeta meta, BukkitJSONConfig metaJson)
	{
		metaJson.setColor("color", meta.getColor());
		getGeneralItemMeta(meta, metaJson);
		return this;
	}
	
	private MapMeta getMapMeta(BukkitJSONConfig metaJson)
	{
		MapMeta meta = (MapMeta) newItemMeta();
		if (metaJson.containsKey("scaling"))
			meta.setScaling(metaJson.getBoolean("scaling"));
		
		getGeneralItemMeta(meta, metaJson);
		return meta;
	}
	
	private BukkitJSONConfig setMapMeta(MapMeta meta, BukkitJSONConfig metaJson)
	{
		metaJson.setBoolean("scaling", meta.isScaling());
		getGeneralItemMeta(meta, metaJson);
		return this;
	}
	
	private PotionMeta getPotionMeta(BukkitJSONConfig metaJson)
	{
		PotionMeta meta = (PotionMeta) newItemMeta();
		if (metaJson.containsKey("effects"))
			for (PotionEffect effect : getPotionEffectList("effects"))
				meta.addCustomEffect(effect, true);
		
		getGeneralItemMeta(meta, metaJson);
		return meta;
	}
	
	private BukkitJSONConfig setPotionMeta(PotionMeta meta, BukkitJSONConfig metaJson)
	{
		if (meta.hasCustomEffects())
			metaJson.setPotionEffectList("effects", meta.getCustomEffects());
		
		setGeneralItemMeta(meta, metaJson);
		return this;
	}
	
	private SkullMeta getSkullMeta(BukkitJSONConfig metaJson)
	{
		SkullMeta meta = (SkullMeta) newItemMeta();
		if (metaJson.containsKey("owner"))
			meta.setOwner(metaJson.getString("owner"));
		
		getGeneralItemMeta(meta, metaJson);
		return meta;
	}
	
	private BukkitJSONConfig setSkullMeta(SkullMeta meta, BukkitJSONConfig metaJson)
	{
		if (meta.hasOwner())
			metaJson.setString("owner", meta.getOwner());
		
		setGeneralItemMeta(meta, metaJson);
		return this;
	}
	
	private BookMeta getWrittenBookMeta(BukkitJSONConfig metaJson)
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
	
	private BukkitJSONConfig setBookMeta(BookMeta meta, BukkitJSONConfig metaJson)
	{
		if (meta.hasAuthor())
			metaJson.setString("author", meta.getAuthor());
		
		if (meta.hasPages())
			metaJson.setStringList("pages", meta.getPages());
		
		if (meta.hasTitle())
			metaJson.setString("title", meta.getTitle());
		
		setGeneralItemMeta(meta, metaJson);
		return this;
	}
	
	private BukkitJSONConfig getGeneralItemMeta(ItemMeta im, BukkitJSONConfig metaJson)
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
			im.setLore(metaJson.getStringList("lore"));
		return this;
	}
	
	private BukkitJSONConfig setGeneralItemMeta(ItemMeta meta, BukkitJSONConfig metaJson)
	{
		if (meta.hasDisplayName())
			metaJson.setString("name", meta.getDisplayName());
		
		if (meta.hasEnchants())
			metaJson.setEnchants("enchants", meta.getEnchants());
		
		if (meta.hasLore())
			metaJson.setStringList("lore", meta.getLore());
		
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
	
	private BukkitJSONConfig setItemStack(int key, ItemStack item)
	{
		setItemStack(key + "", item);
		return this;
	}
	
	public ItemStack getItemStack(String key)
	{
		BukkitJSONConfig itemJson = getBukkitJSONConfig(key);
		ItemStack item = new ItemStack(itemJson.getMaterial("material"), itemJson.getInt("amount"), itemJson.getShort("durability"));
		if (itemJson.containsKey("meta"))
			item.setItemMeta(itemJson.getItemMeta("meta"));
		
		return item;
	}
	
	public BukkitJSONConfig setItemStack(String key, ItemStack item)
	{
		BukkitJSONConfig itemJson = getBukkitJSONConfig(key);
		itemJson.setMaterial("material", item.getType());
		itemJson.setInt("amount", item.getAmount());
		itemJson.setShort("durability", item.getDurability());
		if (item.hasItemMeta())
			itemJson.setItemMeta("meta", item.getItemMeta(), item.getType());
		
		setBukkitJSONConfig(key, itemJson);
		return this;
	}
	
	public Material getMaterial(String key)
	{
		return Material.matchMaterial(getString(key).toUpperCase());
	}
	
	public BukkitJSONConfig setMaterial(String key, Material material)
	{
		setString(key, material.toString());
		return this;
	}
	
	public PotionEffect getPotionEffect(String key)
	{
		BukkitJSONConfig potionJson = getBukkitJSONConfig(key);
		boolean ambient = potionJson.getBoolean("ambient");
		int amplifier = potionJson.getInt("amplifier");
		int duration = potionJson.getInt("duration");
		PotionEffectType type = PotionEffectType.getByName(potionJson.getString("effect"));
		return new PotionEffect(type, duration, amplifier, ambient);
	}
	
	public BukkitJSONConfig setPotionEffect(String key, PotionEffect effect)
	{
		BukkitJSONConfig potionJson = new BukkitJSONConfig();
		potionJson.setString("effect", effect.getType().toString());
		potionJson.setInt("amplifier", effect.getAmplifier());
		potionJson.setInt("duration", effect.getDuration());
		potionJson.setBoolean("ambient", effect.isAmbient());
		return this;
	}
	
	public List<PotionEffect> getPotionEffectList(String key)
	{
		List<PotionEffect> effects = Lists.newArrayList();
		for (BukkitJSONConfig potionJson : getBukkitJSONConfigList(key))
		{
			boolean ambient = potionJson.getBoolean("ambient");
			int amplifier = potionJson.getInt("amplifier");
			int duration = potionJson.getInt("duration");
			PotionEffectType type = PotionEffectType.getByName(potionJson.getString("effect"));
			effects.add(new PotionEffect(type, duration, amplifier, ambient));
		}
		
		return effects;
	}
	
	public BukkitJSONConfig setPotionEffectList(String key, List<PotionEffect> effects)
	{
		List<BukkitJSONConfig> potionsJson = Lists.newArrayList();
		for (PotionEffect effect : effects)
		{
			BukkitJSONConfig potionJson = new BukkitJSONConfig();
			potionJson.setString("effect", effect.getType().toString());
			potionJson.setInt("amplifier", effect.getAmplifier());
			potionJson.setInt("duration", effect.getDuration());
			potionJson.setBoolean("ambient", effect.isAmbient());
			potionsJson.add(potionJson);
		}
		
		setBukkitJSONConfigList(key, potionsJson);
		return this;
	}
	
	public static BukkitJSONConfig loadBukkitJSONConfig(File file) throws FileNotFoundException, IOException, ParseException
	{
		return (BukkitJSONConfig) new JSONParser().parse(new FileReader(file));
	}
	
	public static BukkitJSONConfig loadBukkitJSONConfig(String string) throws ParseException
	{
		return (BukkitJSONConfig) new JSONParser().parse(string);
	}
}

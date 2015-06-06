package musician101.sponge.itembank.config;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import musician101.itembank.common.config.json.AbstractJSONConfig;
import musician101.sponge.itembank.ItemBank;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.spongepowered.api.CatalogTypes;
import org.spongepowered.api.item.Enchantment;
import org.spongepowered.api.item.FireworkEffect;
import org.spongepowered.api.item.FireworkEffectBuilder;
import org.spongepowered.api.item.FireworkShape;
import org.spongepowered.api.potion.PotionEffect;
import org.spongepowered.api.potion.PotionEffectBuilder;
import org.spongepowered.api.potion.PotionEffectType;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@SuppressWarnings("serial")
public class SpongeJSONConfig extends AbstractJSONConfig
{
	public SpongeJSONConfig()
	{
		super();
	}
	
	public SpongeJSONConfig getSpongeJSONConfig(String key)
	{
		return (SpongeJSONConfig) get(key);
	}
	
	@SuppressWarnings("unchecked")
	public SpongeJSONConfig setSpongeJSONConfig(String key, SpongeJSONConfig config)
	{
		put(key, config);
		return this;
	}
	
	public List<SpongeJSONConfig> getSpongeJSONConfigList(String key)
	{
		List<SpongeJSONConfig> jsons = Lists.newArrayList();
		for (Object object : (JSONArray) get(key))
			jsons.add((SpongeJSONConfig) object);
		
		return jsons;
	}
	
	@SuppressWarnings("unchecked")
	public SpongeJSONConfig setSpongeJSONConfigList(String key, List<SpongeJSONConfig> configs)
	{
		put(key, configs);
		return this;
	}
	
	public Color getColor(String key)
	{
		SpongeJSONConfig colorJson = getSpongeJSONConfig(key);
		int blue = 0;
		int green = 0;
		int red = 0;
		
		if (colorJson.containsKey("BLUE"))
			blue = colorJson.getInt("BLUE");
		
		if (colorJson.containsKey("GREEN"))
			green = colorJson.getInt("GREEN");
		
		if (colorJson.containsKey("RED"))
			red = colorJson.getInt("RED");
		
		return new Color(red, green, blue);
	}
	
	public SpongeJSONConfig setColor(String key, Color color)
	{
		SpongeJSONConfig colorJson = new SpongeJSONConfig();
		colorJson.setInt("BLUE", color.getBlue());
		colorJson.setInt("GREEN", color.getGreen());
		colorJson.setInt("RED", color.getRed());
		setSpongeJSONConfig(key, colorJson);
		return this;
	}
	
	public List<Color> getColorList(String key)
	{
		List<Color> colors = Lists.newArrayList();
		for (SpongeJSONConfig colorJson : getSpongeJSONConfigList(key))
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
			
			colors.add(new Color(red, green, blue));
		}
		
		return colors;
	}
	
	public SpongeJSONConfig setColorList(String key, List<Color> colors)
	{
		List<SpongeJSONConfig> colorsJson = Lists.newArrayList();
		for (Color color : colors)
		{
			SpongeJSONConfig colorJson = new SpongeJSONConfig();
			colorJson.setInt("BLUE", color.getBlue());
			colorJson.setInt("GREEN", color.getGreen());
			colorJson.setInt("RED", color.getRed());
			colorsJson.add(colorJson);
		}
		
		setSpongeJSONConfigList(key, colorsJson);
		return this;
	}
	
	public Map<Enchantment, Integer> getEnchants(String key)
	{
		SpongeJSONConfig enchantsJson = getSpongeJSONConfig(key);
		Map<Enchantment, Integer> enchants = Maps.newHashMap();
		for (Enchantment enchant : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.ENCHANTMENT))
			if (enchantsJson.containsKey(enchant.getName()))
				enchants.put(enchant, enchantsJson.getInt(enchant.getName()));
		
		return enchants;
	}
	
	public SpongeJSONConfig setEnchants(String key, Map<Enchantment, Integer> enchants)
	{
		SpongeJSONConfig enchantsJson = new SpongeJSONConfig();
		for (Enchantment enchant : enchants.keySet())
			enchantsJson.setInt(enchant.getName(), enchants.get(enchant));
		
		setSpongeJSONConfig(key, enchantsJson);
		return this;
	}
	
	public FireworkEffect getFireworkEffect(String key)
	{
		SpongeJSONConfig fwJson = getSpongeJSONConfig(key);
		FireworkEffectBuilder fw = ItemBank.getGame().getRegistry().getFireworkEffectBuilder();
		if (fwJson.containsKey("flicker"))
			fw.flicker(fwJson.getBoolean("flicker"));
		
		if (fwJson.containsKey("trail"))
			fw.trail(fwJson.getBoolean("trail"));
		
		if (fwJson.containsKey("shape"))
			for (FireworkShape shape : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.FIREWORK_SHAPE))
				if (shape.getName().equalsIgnoreCase(fwJson.getString("shape")))
					fw.shape(shape);
		
		if (fwJson.containsKey("colors"))
			for (Color color : getColorList("colors"))
				fw.color(color);
		
		if (fwJson.containsKey("fade-colors"))
			for (Color color : getColorList("fade-colors"))
				fw.fade(color);
		
		return fw.build();
	}
	
	public SpongeJSONConfig setFireworkEffect(String key, FireworkEffect effect)
	{
		SpongeJSONConfig fwJson = new SpongeJSONConfig();
		fwJson.setBoolean("flicker", effect.flickers());
		fwJson.setBoolean("trail", effect.hasTrail());
		fwJson.setString("shape", effect.getShape().getName());
		fwJson.setColorList("colors", effect.getColors());
		fwJson.setColorList("fade-colors", effect.getFadeColors());
		setSpongeJSONConfig(key, fwJson);
		return this;
	}
	
	public List<FireworkEffect> getFireworkEffectList(String key)
	{
		List<FireworkEffect> effects = Lists.newArrayList();
		for (SpongeJSONConfig fwJson: getSpongeJSONConfigList(key))
		{
			FireworkEffectBuilder fw = ItemBank.getGame().getRegistry().getFireworkEffectBuilder();
			if (fwJson.containsKey("flicker"))
				fw.flicker(fwJson.getBoolean("flicker"));
			
			if (fwJson.containsKey("trail"))
				fw.trail(fwJson.getBoolean("trail"));
			
			if (fwJson.containsKey("shape"))
				for (FireworkShape shape : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.FIREWORK_SHAPE))
					if (shape.getName().equalsIgnoreCase(fwJson.getString("shape")))
						fw.shape(shape);
			
			if (fwJson.containsKey("colors"))
				for (Color color : getColorList("colors"))
					fw.color(color);
			
			if (fwJson.containsKey("fade-colors"))
				for (Color color : getColorList("fade-colors"))
					fw.fade(color);
			
			effects.add(fw.build());
		}
		
		return effects;
	}
	
	public SpongeJSONConfig setFireworkEffectList(String key, List<FireworkEffect> effects)
	{
		for (FireworkEffect effect : effects)
		{
			SpongeJSONConfig fwJson = new SpongeJSONConfig();
			fwJson.setBoolean("flicker", effect.flickers());
			fwJson.setBoolean("trail", effect.hasTrail());
			fwJson.setString("shape", effect.getShape().getName());
			fwJson.setColorList("colors", effect.getColors());
			fwJson.setColorList("fade-colors", effect.getFadeColors());
			setSpongeJSONConfig(key, fwJson);
		}
		
		return this;
	}
	
	public PotionEffect getPotionEffect(String key)
	{
		SpongeJSONConfig potionJson = getSpongeJSONConfig(key);
		PotionEffectBuilder potion = ItemBank.getGame().getRegistry().getPotionEffectBuilder();
		potion.ambience(potionJson.getBoolean("is_ambient"));
		potion.amplifier(potionJson.getInt("amplifier"));
		potion.duration(potionJson.getInt("duration"));
		for (PotionEffectType type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.POTION_EFFECT_TYPE))
			if (type.getName().equalsIgnoreCase(potionJson.getString("effect")))
				potion.potionType(type);
		
		return potion.build();
	}
	
	public SpongeJSONConfig setPotionEffect(String key, PotionEffect effect)
	{
		SpongeJSONConfig potionJson = new SpongeJSONConfig();
		potionJson.setString("effect", effect.getType().getName());
		potionJson.setInt("amplifier", effect.getAmplifier());
		potionJson.setInt("duration", effect.getDuration());
		potionJson.setBoolean("is_ambient", effect.isAmbient());
		return this;
	}
	
	public List<PotionEffect> getPotionEffectList(String key)
	{
		List<PotionEffect> effects = Lists.newArrayList();
		for (SpongeJSONConfig potionJson : getSpongeJSONConfigList(key))
		{
			PotionEffectBuilder potion = ItemBank.getGame().getRegistry().getPotionEffectBuilder();
			potion.ambience(potionJson.getBoolean("is_ambient"));
			potion.amplifier(potionJson.getInt("amplifier"));
			potion.duration(potionJson.getInt("duration"));
			for (PotionEffectType type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.POTION_EFFECT_TYPE))
				if (type.getName().equalsIgnoreCase(potionJson.getString("effect")))
					potion.potionType(type);
			
			effects.add(potion.build());
		}
		
		return effects;
	}
	
	public SpongeJSONConfig setPotionEffectList(String key, List<PotionEffect> effects)
	{
		List<SpongeJSONConfig> potionsJson = Lists.newArrayList();
		for (PotionEffect effect : effects)
		{
			SpongeJSONConfig potionJson = new SpongeJSONConfig();
			potionJson.setString("effect", effect.getType().toString());
			potionJson.setInt("amplifier", effect.getAmplifier());
			potionJson.setInt("duration", effect.getDuration());
			potionJson.setBoolean("is_ambient", effect.isAmbient());
			potionsJson.add(potionJson);
		}
		
		setSpongeJSONConfigList(key, potionsJson);
		return this;
	}
	
	public static SpongeJSONConfig loadSpongeJSONConfig(File file) throws FileNotFoundException, IOException, ParseException
	{
		return (SpongeJSONConfig) new JSONParser().parse(new FileReader(file));
	}
	
	public static SpongeJSONConfig loadSpongeJSONConfig(String string) throws ParseException
	{
		return (SpongeJSONConfig) new JSONParser().parse(string);
	}
}

package musician101.sponge.itembank.config;

import static org.spongepowered.api.data.DataQuery.of;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import musician101.itembank.common.config.json.AbstractJSONConfig;
import musician101.sponge.itembank.ItemBank;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.spongepowered.api.CatalogTypes;
import org.spongepowered.api.attribute.Attribute;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.item.Enchantment;
import org.spongepowered.api.item.FireworkEffect;
import org.spongepowered.api.item.FireworkEffectBuilder;
import org.spongepowered.api.item.FireworkShape;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.potion.PotionEffect;
import org.spongepowered.api.potion.PotionEffectBuilder;
import org.spongepowered.api.potion.PotionEffectType;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

//TODO check all list methods for incorrect set()
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
	
	public List<SpongeJSONConfig> getSpongeJSONConfigList(String key)
	{
		List<SpongeJSONConfig> jsons = Lists.newArrayList();
		for (Object object : (JSONArray) get(key))
			jsons.add((SpongeJSONConfig) object);
		
		return jsons;
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
	
	public void setColor(String key, Color color)
	{
		SpongeJSONConfig colorJson = new SpongeJSONConfig();
		colorJson.set("BLUE", color.getBlue());
		colorJson.set("GREEN", color.getGreen());
		colorJson.set("RED", color.getRed());
		set(key, colorJson);
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
	
	public void setColorList(String key, List<Color> colors)
	{
		List<SpongeJSONConfig> colorsJson = Lists.newArrayList();
		for (Color color : colors)
		{
			SpongeJSONConfig colorJson = new SpongeJSONConfig();
			colorJson.set("BLUE", color.getBlue());
			colorJson.set("GREEN", color.getGreen());
			colorJson.set("RED", color.getRed());
			colorsJson.add(colorJson);
		}
		
		set(key, colorsJson);
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
	
	public void setEnchants(String key, Map<Enchantment, Integer> enchants)
	{
		SpongeJSONConfig enchantsJson = new SpongeJSONConfig();
		for (Enchantment enchant : enchants.keySet())
			enchantsJson.set(enchant.getName(), enchants.get(enchant));
		
		set(key, enchantsJson);
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
	
	public void setFireworkEffect(String key, FireworkEffect effect)
	{
		SpongeJSONConfig fwJson = new SpongeJSONConfig();
		fwJson.set("flicker", effect.flickers());
		fwJson.set("trail", effect.hasTrail());
		fwJson.set("shape", effect.getShape().getName());
		fwJson.setColorList("colors", effect.getColors());
		fwJson.setColorList("fade-colors", effect.getFadeColors());
		set(key, fwJson);
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
	
	public void setFireworkEffectList(String key, List<FireworkEffect> effects)
	{
		for (FireworkEffect effect : effects)
		{
			SpongeJSONConfig fwJson = new SpongeJSONConfig();
			fwJson.set("flicker", effect.flickers());
			fwJson.set("trail", effect.hasTrail());
			fwJson.set("shape", effect.getShape().getName());
			fwJson.setColorList("colors", effect.getColors());
			fwJson.setColorList("fade-colors", effect.getFadeColors());
			set(key, fwJson);
		}
	}
	
	public List<ItemStack> getItemStackList(String key)
	{
		return null;
	}
	
	public void setItemStackList(String key, List<ItemStack> items)
	{
		List<SpongeJSONConfig> list = new ArrayList<SpongeJSONConfig>();
		for (ItemStack item : items)
		{
			DataContainer container = item.toContainer();
			SpongeJSONConfig json = new SpongeJSONConfig();
			json.set("ItemType", container.getString(of("ItemType")).get());
			json.set("Quantity", container.getString(of("Quantity")).get());
			
			DataView dataView = container.getView(of("Data")).get();
			SpongeJSONConfig datas = new SpongeJSONConfig();
			for (DataQuery dq : dataView.getKeys(true))
			{
				SpongeJSONConfig data = new SpongeJSONConfig();
				DataView view = null;
				if (dq.toString().endsWith("AttributeData"))
				{
					view = dataView.getView(dq).get();
					for (Object o : view.getList(of("Attributes")).get())
					{
						//TODO come back to this when Attributes are implemented fully into SpongeCommon
						Attribute a = (Attribute) o;
						a.getId();
					}
				}
				else if (dq.toString().endsWith("ColoredData"))
				{
					view = dataView.getView(dq).get();
					SpongeJSONConfig color = new SpongeJSONConfig();
					color.setColor("Color", new Color(view.getInt(of("Color")).get()));
					data.set("ColorData", color);
				}
				else if (dq.toString().endsWith("CommandData"))
				{
					view = dataView.getView(dq).get();
					SpongeJSONConfig command = new SpongeJSONConfig();
					command.set("Command", view.getString(of("Command")).get());
					command.set("SuccessCount", view.getInt(of("SuccessCount")).get());
					command.set("TracksOutput", view.getBoolean(of("TracksOutput")).get());
					command.set("LastOutput", view.getString(of("LastOutput")).get());
					data.set("CommandData", command);
				}
				else if (dq.toString().endsWith("DisplayNameData"))
				{
					view = dataView.getView(dq).get();
					SpongeJSONConfig displayName = new SpongeJSONConfig();
					displayName.set("DisplayName", view.getString(of("DisplayName")).get());
					displayName.set("Visible", view.getInt(of("Visible")).get());
					data.set("DisplayNameData", displayName);
				}
				else if (dq.toString().endsWith("DyeableData"))
				{
					view = dataView.getView(dq).get();
					SpongeJSONConfig dye = new SpongeJSONConfig();
					dye.set("DyeColor", view.getString(of("DyeColor")).get());
					data.set("DyeableData", dye);
				}
				else if (dq.toString().endsWith("DyeableData"))
				{
					view = dataView.getView(dq).get();
					SpongeJSONConfig dye = new SpongeJSONConfig();
					dye.set("DyeColor", view.getString(of("DyeColor")).get());
					data.set("DyeableData", dye);
				}
				else if (dq.toString().endsWith("FireworkData"))
				{
					view = dataView.getView(dq).get();
					SpongeJSONConfig fireworks = new SpongeJSONConfig();
					fireworks.set("FlightModifier", view.getInt(of("FlightModifier")).get());
					data.set("FireworkData", fireworks);
					List<SpongeJSONConfig> effects = Lists.newArrayList();
					for (Object o : view.getList(of("FireworkEffects")).get())
					{
						SpongeJSONConfig effectJson = new SpongeJSONConfig();
						FireworkEffect effect = (FireworkEffect) o;
						effectJson.set("Flickers", effect.flickers());
						effectJson.set("Trail", effect.hasTrail());
						effectJson.set("Shape", effect.getShape());
						effectJson.setColorList("Colors", effect.getColors());
						effectJson.setColorList("FadeColors", effect.getFadeColors());
					}
					
					data.set("Effects", effects);
				}//TODO left off here
			}
			
			json.set("Data", datas);
		}
		
		set(key, list);
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
	
	public void setPotionEffect(String key, PotionEffect effect)
	{
		SpongeJSONConfig potionJson = new SpongeJSONConfig();
		potionJson.set("effect", effect.getType().getName());
		potionJson.set("amplifier", effect.getAmplifier());
		potionJson.set("duration", effect.getDuration());
		potionJson.set("is_ambient", effect.isAmbient());
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
	
	public void setPotionEffectList(String key, List<PotionEffect> effects)
	{
		List<SpongeJSONConfig> potionsJson = Lists.newArrayList();
		for (PotionEffect effect : effects)
		{
			SpongeJSONConfig potionJson = new SpongeJSONConfig();
			potionJson.set("effect", effect.getType().toString());
			potionJson.set("amplifier", effect.getAmplifier());
			potionJson.set("duration", effect.getDuration());
			potionJson.set("is_ambient", effect.isAmbient());
			potionsJson.add(potionJson);
		}
		
		set(key, potionsJson);
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

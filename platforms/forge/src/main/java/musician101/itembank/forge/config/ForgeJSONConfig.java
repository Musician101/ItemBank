package musician101.itembank.forge.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import musician101.itembank.common.config.json.AbstractJSONConfig;
import musician101.itembank.forge.ItemBank;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@SuppressWarnings("serial")
public class ForgeJSONConfig extends AbstractJSONConfig
{
	public List<ForgeJSONConfig> getForgeJSONConfigs(String key)
	{
		List<ForgeJSONConfig> jsons = new ArrayList<ForgeJSONConfig>();
		for (Object object : (JSONArray) get(key))
			jsons.add((ForgeJSONConfig) object);
		
		return jsons;
	}
	
	public List<ItemStack> getItems(String key)
	{
		List<ItemStack> items = new ArrayList<ItemStack>();
		for (ForgeJSONConfig json : getForgeJSONConfigs(key))
		{
			try
			{
				Item item = (Item) Item.itemRegistry.getObject(json.getString("unlocalizedName"));
				items.add(new ItemStack(item, json.getInt("amount"), json.getInt("meta")));
			}
			catch (Exception e)
			{
				ItemBank.logger.warn("Error while parsing items.json: " + json.toJSONString());
			}
		}
		
		return items;
	}
	
	public static ForgeJSONConfig loadForgeJSONConfig(File file) throws FileNotFoundException, IOException, ParseException
	{
		return (ForgeJSONConfig) new JSONParser().parse(new FileReader(file));
	}
}

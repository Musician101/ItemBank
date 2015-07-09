package musician101.sponge.itembank.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import musician101.itembank.common.config.json.AbstractJSONConfig;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.common.collect.Lists;

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
		
	public static SpongeJSONConfig loadSpongeJSONConfig(File file) throws FileNotFoundException, IOException, ParseException
	{
		return (SpongeJSONConfig) new JSONParser().parse(new FileReader(file));
	}
	
	public static SpongeJSONConfig loadSpongeJSONConfig(String string) throws ParseException
	{
		return (SpongeJSONConfig) new JSONParser().parse(string);
	}
}

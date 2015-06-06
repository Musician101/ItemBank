package musician101.itembank.common.config.json;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@SuppressWarnings({"serial", "unchecked"})
public class AbstractJSONConfig extends JSONObject
{
	public AbstractJSONConfig()
	{
		super();
	}
	
	public boolean getBoolean(String key)
	{
		return Boolean.valueOf(getString(key));
	}
	
	public boolean getBoolean(String key, boolean defaultValue)
	{
		return (get(key) != null ? Boolean.valueOf(getString(key)) : defaultValue);
	}
	
	public void setBoolean(String key, boolean value)
	{
		put(key, value);
	}
	
	public Double getDouble(String key)
	{
		return Double.valueOf(getString(key));
	}
	
	public double getDouble(String key, double defaultValue)
	{
		return (get(key) != null ? getDouble(key) : defaultValue);
	}
	
	public void setDouble(String key, double value)
	{
		put(key, value);
	}
	
	public int getInt(String key)
	{
		return Integer.valueOf(getString(key));
	}
	
	public int getInt(String key, int defaultValue)
	{
		return (get(key) != null ? getInt(key) : defaultValue);
	}
	
	public void setInt(String key, int value)
	{
		put(key, value);
	}
	
	public short getShort(String key)
	{
		return Short.valueOf(get(key).toString());
	}
	
	public void setShort(String key, short value)
	{
		put(key, value);
	}
	
	public String getString(String key)
	{
		return get(key).toString();
	}
	
	public String getString(String key, String defaultValue)
	{
		return (get(key) != null ? getString(key) : defaultValue);
	}
	
	public void setString(String key, String value)
	{
		put(key, value);
	}
	
	public List<String> getStringList(String key)
	{
		List<String> strings = new ArrayList<String>();
		JSONArray jsonArray = (JSONArray) get(key);
		for (Object object : jsonArray)
			strings.add(object.toString());
		
		return strings;
	}
	
	public void setStringList(String key, List<String> value)
	{
		put(key, value);
	}
	
	public boolean isSet(String key)
	{
		return (get(key) == null);
	}
}

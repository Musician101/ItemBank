package musician101.itembank.common.config.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@SuppressWarnings({"serial", "unchecked"})
public class AbstractJSONConfig extends JSONObject
{
	protected AbstractJSONConfig()
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
	
	public Double getDouble(String key)
	{
		return Double.valueOf(getString(key));
	}
	
	public double getDouble(String key, double defaultValue)
	{
		return (get(key) != null ? getDouble(key) : defaultValue);
	}
	
	public int getInt(String key)
	{
		return Integer.valueOf(getString(key));
	}
	
	public int getInt(String key, int defaultValue)
	{
		return (get(key) != null ? getInt(key) : defaultValue);
	}
	
	public <K, V> Map<K, V> getMap(String key)
	{
		return (Map<K, V>) get(key);
	}
	
	public short getShort(String key)
	{
		return Short.valueOf(get(key).toString());
	}
	
	public String getString(String key)
	{
		return get(key).toString();
	}
	
	public String getString(String key, String defaultValue)
	{
		return (get(key) != null ? getString(key) : defaultValue);
	}
	
	public <K> List<K> getList(String key)
	{
		List<K> strings = new ArrayList<K>();
		JSONArray jsonArray = (JSONArray) get(key);
		for (Object object : jsonArray)
			strings.add((K) object);
		
		return strings;
	}
	
	public <K> List<K> getList(String key, List<K> defaultValue)
	{
		if (isSet(key))
			return defaultValue;
		
		return getList(key);
	}
	
	public void set(String key, Object value)
	{
		put(key, value);
	}
	
	public boolean isSet(String key)
	{
		return (get(key) == null);
	}
}

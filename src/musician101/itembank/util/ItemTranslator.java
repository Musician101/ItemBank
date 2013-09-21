package musician101.itembank.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import musician101.itembank.ItemBank;

/**
 * @author Musician101
 */
public class ItemTranslator 
{
	/** HashMap storing all of the aliases and their data values.*/
	private final Map<String, Map<Integer, Short>> items = new HashMap<String, Map<Integer, Short>>();
	
	/** HashMap storing all of the appropriate IDs and data values. */
	private final List<Integer> ids = new ArrayList<Integer>();
	
	/**
	 * Loads up the translator for the plugin.
	 * 
	 * @param plugin References the plugin's main class.
	 * @param values List of values used to determine the aliases and their IDs.
	 */
	public ItemTranslator(ItemBank plugin, List<String[]> values)
	{
		for (String[] s : values)
		{
			if (!s[0].startsWith("#"))
			{
				if (s.length < 1) continue;
				int id = 0;
				short data = 0;
				String alias = "";
				try
				{
					alias = s[0].toLowerCase();
				}
				catch (IndexOutOfBoundsException e)
				{
					plugin.getLogger().warning("Error with aliases: " + s[0]);
					continue;
				}
				
				try
				{
					id = Integer.valueOf(s[1]);
				}
				catch (NumberFormatException e)
				{
					plugin.getLogger().warning("Error with ids: " + s[1]);
					id = -1;
				}
				
				try
				{
					data = Short.valueOf(s[2]);
				}
				catch (NumberFormatException e)
				{
					plugin.getLogger().info("Error with data: " + s[2]);
				}
				
				Map<Integer, Short> itemIds = new HashMap<Integer, Short>();
				itemIds.put(id, data);
				ids.add(id);
				synchronized (items)
				{
					items.put(alias, itemIds);
				}
			}
		}
	}
	
	/**
	 * How the plugin determines a material's ID and data value.
	 * 
	 * @param alias The alias used to match the ID and data value.
	 * @return Returns the ID as ID:Damage Value.
	 */
	public String getIdFromAlias(String alias)
	{
		boolean found = false;
		String data = "";
		if (alias.contains(":"))
		{
			String[] datas = alias.split(":");
			data = (datas.length > 1) ? datas[1] : "";
		}
		if (items.keySet().contains(alias.toLowerCase())) found = true;
		
		if (!found) return null;
		
		Map.Entry<Integer, Short> entry = items.get(alias).entrySet().iterator().next();
		if (data == null || data.isEmpty()) data = String.valueOf(entry.getValue());
		return entry.getKey() + ":" + data;
	}
}

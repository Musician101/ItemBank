package musician101.itembank.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import musician101.itembank.ItemBank;
import musician101.itembank.exceptions.InvalidMaterialException;

import org.bukkit.Material;

/**
 * @author Musician101
 */
public class ItemTranslator 
{
	/** HashMap storing all of the aliases and their data values.*/
	private final Map<String, Map<Material, Short>> items = new HashMap<String, Map<Material, Short>>();
	
	/** HashMap storing all of the appropriate IDs and data values. */
	private final List<Material> ids = new ArrayList<Material>();
	
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
				Material material = null;
				short data = 0;
				String alias = "";
				try
				{
					alias = s[0].toLowerCase();
				}
				catch (IndexOutOfBoundsException e)
				{
					plugin.getLogger().warning("Error with aliases: " + s[0]);
				}
				
				try
				{
					material = IBUtils.getMaterial(s[1]);
				}
				catch (InvalidMaterialException e)
				{
					plugin.getLogger().warning("Error with material: " + s[1]);
				}
				
				try
				{
					data = Short.valueOf(s[2]);
				}
				catch (NumberFormatException e)
				{
					plugin.getLogger().info("Error with data: " + s[2]);
				}
				
				Map<Material, Short> itemIds = new HashMap<Material, Short>();
				itemIds.put(material, data);
				ids.add(material);
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
		
		Map.Entry<Material, Short> entry = items.get(alias).entrySet().iterator().next();
		if (data == null || data.isEmpty()) data = String.valueOf(entry.getValue());
		return entry.getKey() + ":" + data;
	}
}

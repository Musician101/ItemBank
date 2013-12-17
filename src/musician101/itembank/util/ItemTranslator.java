package musician101.itembank.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import musician101.itembank.ItemBank;

import org.bukkit.inventory.ItemStack;

/**
 * @author Musician101
 */
public class ItemTranslator 
{
	/** HashMap storing all of the aliases and their data values.*/
	private final Map<String, Map<Integer, Short>> items = new HashMap<String, Map<Integer, Short>>();
	
	/** HashMap used to find aliases for a given block/item. */
	private final Map<ItemData, List<String>> names = new HashMap<ItemData, List<String>>();
	
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
				}
				
				try
				{
					id = Integer.parseInt(s[1]);
				}
				catch (NumberFormatException e)
				{
					plugin.getLogger().warning("Error with ID: " + s[1]);
				}
				
				try
				{
					data = Short.valueOf(s[2]);
				}
				catch (NumberFormatException e)
				{
					plugin.getLogger().info("Error with data: " + s[2]);
				}
				
				ItemData itemData = new ItemData(id, data);
				if (names.containsKey(itemData))
				{
					List<String> aliases = names.get(itemData);
					aliases.add(alias);
					Collections.sort(aliases);
				}
				else
				{
					List<String> aliases = new ArrayList<String>();
					aliases.add(alias);
					names.put(itemData, aliases);
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
	 * How the plugin determines an ID and data value.
	 * 
	 * @param alias The alias used to match the Material and data value.
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
	
	/**
	 * Get the list of aliases based on a given ItemStack.
	 * 
	 * @param item The item to search aliases for.
	 * @return
	 */
	public String getAliases(ItemStack item)
	{
		/**
		 * Deprecated mtehod ItemStack.getTypeId() in Bukkit.
		 * Waiting for a proper alternative before fixing.
		 */
		ItemData itemData = new ItemData(item.getTypeId(), item.getDurability());
		List<String> aliases = names.get(itemData);
		if (aliases == null)
		{
			itemData = new ItemData(item.getTypeId(), (short) 0);
			aliases = names.get(itemData);
			if (aliases == null)
				return null;
		}
		
		if (aliases.size() > 15)
			aliases = aliases.subList(0, 14);
		
		return IBUtils.joinList(", ", aliases);
	}
	
	static class ItemData
	{
		final private int id;
		final private short data;
		
		ItemData(final int id, final short data)
		{
			this.id = id;
			this.data = data;
		}
		
		public int getID()
		{
			return id;
		}
		
		public short getData()
		{
			return data;
		}
		
		@Override
		public boolean equals(Object object)
		{
			if (object == null)
				return false;
			
			if (!(object instanceof ItemData))
				return false;
			
			ItemData pairo = (ItemData) object;
			return this.id == pairo.getID() && this.getData() == pairo.getData();
		}
	}
}

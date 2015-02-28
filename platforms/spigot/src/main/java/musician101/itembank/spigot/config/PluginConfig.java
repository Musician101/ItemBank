package musician101.itembank.spigot.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import musician101.itembank.common.config.AbstractConfig;
import musician101.itembank.common.database.MySQLHandler;
import musician101.itembank.spigot.ItemBank;
import musician101.itembank.spigot.lib.Messages;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

public class PluginConfig extends AbstractConfig
{
	boolean enableVault;
	boolean useMYSQL;
	double transactionCost;
	ItemBank plugin;
	File playerData;
	List<ItemStack> itemList = new ArrayList<ItemStack>();
	
	public PluginConfig(ItemBank plugin)
	{
		super();
		this.plugin = plugin;
		playerData = new File(plugin.getDataFolder(), "PlayerData");
		plugin.saveDefaultConfig();
		if (!new File(plugin.getDataFolder(), "lang.yml").exists())
			plugin.saveResource("lang.yml", false);
		
		playerData.mkdirs();
		reloadConfiguration();
	}
	
	public void reloadConfiguration()
	{
		plugin.reloadConfig();
		FileConfiguration config = plugin.getConfig();
		setIsWhitelist(config.getBoolean("whitelist", false));
		enableVault = config.getBoolean("enableVault", false);
		setFormat(config.getString("format", "yml").toLowerCase());
		setMultiWorldStorageEnabled(config.getBoolean("multiWorld", false));
		setPageLimit(config.getInt("pageLimit", 0));
		transactionCost = config.getDouble("transactionCost", 5);
		setCheckForUpdate(config.getBoolean("updateCheck", true));
		
		if (config.isSet("itemlist"))
			for (Entry<String, Object> material : config.getConfigurationSection("itemlist").getValues(true).entrySet())
				if (!(material.getValue() instanceof MemorySection))
					if (material.getKey().contains("."))
						itemList.add(new ItemStack(Material.getMaterial(material.getKey().split("\\.")[0].toUpperCase()), (Integer) material.getValue(), Short.valueOf(material.getKey().split("\\.")[1])));
		
		String mysql = "mysql";
		ConfigurationSection mysqlCS = null;
		if (!config.isSet(mysql))
		{
			mysqlCS = config.createSection(mysql);
			plugin.getLogger().warning("Could not find 'mysql' in config. MySQL support disabled.");
		}
		else
			mysqlCS = config.getConfigurationSection(mysql);
		
		useMYSQL = mysqlCS.getBoolean("enable", false);
		if (useMYSQL)
			plugin.setMySQLHandler(new MySQLHandler(config.getString("database"), config.getString("host"), config.getString("pass"), config.getString("port"), config.getString("user")));

		try
		{
			Messages.init(plugin, config.getString("lang", "en"), new File(plugin.getDataFolder(), "lang.yml"));
		}
		catch (IOException e)
		{
			plugin.getLogger().warning("Error loading lang.yml (Internal Error).");
		}
		catch (InvalidConfigurationException e)
		{
			plugin.getLogger().warning("Error loading lang.yml (Incorrect YAML format).");
		}
	}

	public boolean enableVault()
	{
		return enableVault;
	}

	public boolean useMySQL()
	{
		return useMYSQL;
	}

	public double getTransactionCost()
	{
		return transactionCost;
	}
	
	public List<ItemStack> getItemList()
	{
		return itemList;
	}
	
	public ItemStack getItem(Material material, short durability)
	{
		for (ItemStack item : itemList)
			if (item.getType() == material && item.getDurability() == durability)
				return item;
		
		return null;
	}
	
	public File getPlayerData()
	{
		return playerData;
	}
	
	public File getPlayerFile(UUID uuid)
	{
		return new File(playerData, uuid.toString() + "." + getFormat());
	}
}

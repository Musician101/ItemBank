package musician101.itembank.spigot.config;

import musician101.itembank.common.AbstractConfig;
import musician101.itembank.common.MySQLHandler;
import musician101.itembank.spigot.SpigotItemBank;
import musician101.itembank.spigot.lib.Messages;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class SpigotConfig extends AbstractConfig
{
	private boolean enableVault;
	private boolean useMYSQL;
	private double transactionCost;
	private final SpigotItemBank plugin;
	private final File playerData;
	private final List<ItemStack> itemList = new ArrayList<>();
	
	public SpigotConfig(SpigotItemBank plugin)
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
			itemList.addAll(config.getConfigurationSection("itemlist").getValues(true).entrySet().stream().filter(material -> !(material.getValue() instanceof MemorySection)).filter(material -> material.getKey().contains(".")).map(material -> new ItemStack(Material.getMaterial(material.getKey().split("\\.")[0].toUpperCase()), (Integer) material.getValue(), Short.valueOf(material.getKey().split("\\.")[1]))).collect(Collectors.toList()));
		
		String mysql = "mysql";
		ConfigurationSection mysqlCS;
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

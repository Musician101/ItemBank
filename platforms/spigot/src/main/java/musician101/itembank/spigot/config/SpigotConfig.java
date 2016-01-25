package musician101.itembank.spigot.config;

import musician101.itembank.common.AbstractConfig;
import musician101.itembank.common.MySQLHandler;
import musician101.itembank.common.Reference.Config;
import musician101.itembank.spigot.SpigotItemBank;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

public class SpigotConfig extends AbstractConfig<ItemStack>
{
    private final SpigotItemBank plugin;

    public SpigotConfig(SpigotItemBank plugin)
    {
        super();
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        reload();
    }

    @Override
    public void reload()
    {
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();
        isWhitelist = config.getBoolean(Config.WHITELIST, false);
        enableEconomy = config.getBoolean(Config.ENABLE_ECONOMY, false);
        isMultiWorldStorageEnabled = config.getBoolean(Config.MULTI_WORLD, false);
        pageLimit = config.getInt(Config.PAGE_LIMIT, 0);
        transactionCost = config.getDouble(Config.TRANSACTION_COST, 5);
        checkForUpdate = config.getBoolean(Config.UPDATE_CHECK, true);

        if (config.isSet(Config.ITEM_LIST))
        {
            ConfigurationSection itemListSection = config.getConfigurationSection(Config.ITEM_LIST);
            for (String materialKey : config.getConfigurationSection(Config.ITEM_LIST).getValues(false).keySet())
            {
                ConfigurationSection materialSection = itemListSection.getConfigurationSection(materialKey);
                for (String durabilityKey : itemListSection.getConfigurationSection(materialKey).getValues(false).keySet())
                    itemList.add(new ItemStack(Material.getMaterial(materialKey.toUpperCase()), materialSection.getInt(durabilityKey), Short.parseShort(durabilityKey)));
            }
        }

        if (config.isSet(Config.MYSQL))
        {
            ConfigurationSection mysqlCS = config.createSection(Config.MYSQL);
            if (mysqlCS.getBoolean(Config.ENABLE_MYSQL, false))
                plugin.setMySQLHandler(new MySQLHandler(config.getString(Config.DATABASE), config.getString(Config.HOST), config.getString(Config.PASSWORD), config.getString(Config.PORT), config.getString(Config.USER)));
        }
    }

    @Override
    public ItemStack getItem(ItemStack itemStack)
    {
        for (ItemStack item : itemList)
            if (item.getType() == itemStack.getType() && item.getDurability() == itemStack.getDurability())
                return item;

        return null;
    }
}

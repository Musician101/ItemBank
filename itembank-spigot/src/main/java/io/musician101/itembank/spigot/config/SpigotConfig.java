package io.musician101.itembank.spigot.config;

import io.musician101.itembank.common.AbstractItemBankConfig;
import io.musician101.itembank.common.Reference.Config;
import io.musician101.itembank.spigot.SpigotItemBank;
import io.musician101.musicianlibrary.java.MySQLHandler;
import java.io.File;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class SpigotConfig extends AbstractItemBankConfig<Material> {

    public SpigotConfig() {
        super(new File(SpigotItemBank.instance().getDataFolder(), "config.yml"));
        reload();
    }

    @Override
    public void reload() {
        SpigotItemBank plugin = SpigotItemBank.instance();
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();
        isWhitelist = config.getBoolean(Config.WHITELIST, false);
        enableEconomy = config.getBoolean(Config.ENABLE_ECONOMY, false);
        isMultiWorldStorageEnabled = config.getBoolean(Config.MULTI_WORLD, false);
        pageLimit = config.getInt(Config.PAGE_LIMIT, 0);
        transactionCost = config.getDouble(Config.TRANSACTION_COST, 5);
        updateCheck = config.getBoolean(Config.UPDATE_CHECK, true);

        if (config.isSet(Config.ITEM_LIST)) {
            ConfigurationSection itemList = config.getConfigurationSection(Config.ITEM_LIST);
            itemList.getKeys(false).forEach(key -> {
                Material material = Material.matchMaterial(key);
                if (material == null) {
                    return;
                }

                typeList.put(material, itemList.getInt(key));
            });
        }

        if (config.isSet(Config.MYSQL)) {
            ConfigurationSection mysqlCS = config.createSection(Config.MYSQL);
            if (mysqlCS.getBoolean(Config.ENABLE_MYSQL, false)) {
                mysql = new MySQLHandler(config.getString(Config.DATABASE), config.getString(Config.HOST), config.getString(Config.PASSWORD), config.getString(Config.PORT), config.getString(Config.USER));
            }
        }
    }
}

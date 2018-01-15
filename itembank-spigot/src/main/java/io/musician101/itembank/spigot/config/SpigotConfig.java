package io.musician101.itembank.spigot.config;

import io.musician101.itembank.common.AbstractItemBankConfig;
import io.musician101.itembank.common.Reference.Config;
import io.musician101.itembank.spigot.SpigotItemBank;
import io.musician101.musicianlibrary.java.MySQLHandler;
import java.io.File;
import java.util.stream.Collectors;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

public class SpigotConfig extends AbstractItemBankConfig<ItemStack> {

    public SpigotConfig() {
        super(new File(SpigotItemBank.instance().getDataFolder(), "config.yml"));
        reload();
    }

    @Override
    public ItemStack getItem(ItemStack itemStack) {
        for (ItemStack item : itemList) {
            if (item.getType() == itemStack.getType() && item.getDurability() == itemStack.getDurability()) {
                return item;
            }
        }

        return null;
    }

    @Override
    public void reload() {
        SpigotItemBank.instance().saveDefaultConfig();
        SpigotItemBank.instance().reloadConfig();
        FileConfiguration config = SpigotItemBank.instance().getConfig();
        isWhitelist = config.getBoolean(Config.WHITELIST, false);
        enableEconomy = config.getBoolean(Config.ENABLE_ECONOMY, false);
        isMultiWorldStorageEnabled = config.getBoolean(Config.MULTI_WORLD, false);
        pageLimit = config.getInt(Config.PAGE_LIMIT, 0);
        transactionCost = config.getDouble(Config.TRANSACTION_COST, 5);
        updateCheck = config.getBoolean(Config.UPDATE_CHECK, true);

        if (config.isSet(Config.ITEM_LIST)) {
            ConfigurationSection itemListSection = config.getConfigurationSection(Config.ITEM_LIST);
            for (String materialKey : config.getConfigurationSection(Config.ITEM_LIST).getValues(false).keySet()) {
                ConfigurationSection materialSection = itemListSection.getConfigurationSection(materialKey);
                itemList.addAll(itemListSection.getConfigurationSection(materialKey).getValues(false).keySet().stream().map(durabilityKey -> new ItemStack(Material.getMaterial(materialKey.toUpperCase()), materialSection.getInt(durabilityKey), Short.parseShort(durabilityKey))).collect(Collectors.toList()));
            }
        }

        if (config.isSet(Config.MYSQL)) {
            ConfigurationSection mysqlCS = config.createSection(Config.MYSQL);
            if (mysqlCS.getBoolean(Config.ENABLE_MYSQL, false)) {
                SpigotItemBank.instance().setMySQLHandler(new MySQLHandler(config.getString(Config.DATABASE), config.getString(Config.HOST), config.getString(Config.PASSWORD), config.getString(Config.PORT), config.getString(Config.USER)));
            }
        }
    }
}

package io.musician101.itembank.spigot;

import io.musician101.itembank.common.ItemBankConfig;
import io.musician101.itembank.common.Reference.Config;
import io.musician101.itembank.common.Reference.Messages;
import java.io.File;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class SpigotConfig extends ItemBankConfig<Material> {

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
        enableEconomy = config.getBoolean(Config.ENABLE_ECONOMY, false);
        enableMultiWorldStorage = config.getBoolean(Config.MULTI_WORLD, false);
        pageLimit = config.getInt(Config.PAGE_LIMIT, 0);
        transactionCost = config.getDouble(Config.TRANSACTION_COST, 5);
        // Config.YAML is not NULL
        //noinspection ConstantConditions
        format = config.getString(Config.FORMAT, Config.YAML).toUpperCase();
        if (config.isSet(Config.WHITELIST)) {
            config.getStringList(Config.WHITELIST).forEach(s -> {
                Material material = Material.matchMaterial(s);
                if (material == null) {
                    SpigotItemBank.instance().getLogger().warning(String.format(Messages.CONFIG_INVALID_ITEM, s, Config.WHITELIST));
                    return;
                }

                whiteList.add(material);
            });
        }

        if (config.isSet(Config.BLACKLIST)) {
            config.getStringList(Config.BLACKLIST).forEach(s -> {
                Material material = Material.matchMaterial(s);
                if (material == null) {
                    SpigotItemBank.instance().getLogger().warning(String.format(Messages.CONFIG_INVALID_ITEM, s, Config.BLACKLIST));
                    return;
                }

                blackList.add(material);
            });
        }

        ConfigurationSection itemRestrictions = config.getConfigurationSection(Config.ITEM_RESTRICTIONS);
        if (itemRestrictions != null) {
            itemRestrictions.getKeys(false).forEach(key -> {
                Material material = Material.matchMaterial(key);
                if (material == null) {
                    SpigotItemBank.instance().getLogger().warning(String.format(Messages.CONFIG_INVALID_ITEM, key, Config.ITEM_RESTRICTIONS));
                    return;
                }

                this.itemRestrictions.put(material, itemRestrictions.getInt(key));
            });
        }

        this.databaseOptions.clear();
        ConfigurationSection databaseOptions = config.getConfigurationSection(Config.DATABASE);
        if (databaseOptions != null) {
            this.databaseOptions.putAll(databaseOptions.getValues(false).entrySet().stream().collect(Collectors.toMap(Entry::getKey, e -> e.getValue().toString())));
        }
    }
}

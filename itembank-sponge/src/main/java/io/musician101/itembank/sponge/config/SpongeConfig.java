package io.musician101.itembank.sponge.config;

import io.musician101.itembank.common.ItemBankConfig;
import io.musician101.itembank.common.Reference.Config;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;
import org.spongepowered.configurate.reference.ConfigurationReference;
import org.spongepowered.configurate.reference.ValueReference;

public class SpongeConfig extends ItemBankConfig<ItemType> {

    private final ConfigurationReference<ConfigurationNode> configReference;

    public SpongeConfig(ConfigurationReference<ConfigurationNode> configReference) {
        this.configReference = configReference;
    }

    @Override
    public void reload() throws IOException {
        configReference.load();
        ValueReference<DefaultConfig, ConfigurationNode> config = configReference.referenceTo(DefaultConfig.class);
        DefaultConfig defaultConfig = Objects.requireNonNull(config.get());
        replaceList(whitelist, defaultConfig.whitelist);
        replaceList(blacklist, defaultConfig.blacklist);
        replaceMap(itemRestrictions, defaultConfig.itemRestrictions);
        replaceMap(databaseOptions, defaultConfig.databaseOptions);
        enableEconomy = defaultConfig.enableEconomy;
        enableMultiWorldStorage = defaultConfig.multiWorld;
        transactionCost = defaultConfig.transactionCost;
        pageLimit = defaultConfig.pageLimit;
        format = defaultConfig.format;
    }

    private void replaceList(List<ItemType> oldList, List<ItemType> newList) {
        oldList.clear();
        oldList.addAll(newList);
    }

    private <K, V> void replaceMap(Map<K, V> oldMap, Map<K, V> newMap) {
        oldMap.clear();
        oldMap.putAll(newMap);
    }

    @SuppressWarnings({"FieldMayBeFinal", "CanBeFinal"})
    @ConfigSerializable
    public static class DefaultConfig {

        @Setting
        private List<ItemType> blacklist = new ArrayList<>();
        @Setting(Config.DATABASE)
        private Map<String, String> databaseOptions = new HashMap<>();
        @Setting(Config.ENABLE_ECONOMY)
        private boolean enableEconomy = false;
        @Setting
        private String format = "YAML";
        @Setting(Config.ITEM_RESTRICTIONS)
        private Map<ItemType, Integer> itemRestrictions = new HashMap<>();
        @Setting(Config.MULTI_WORLD)
        private boolean multiWorld = false;
        @Setting(Config.PAGE_LIMIT)
        private int pageLimit = 0;
        @Setting(Config.TRANSACTION_COST)
        private int transactionCost = 5;
        @Setting
        private List<ItemType> whitelist = new ArrayList<>();
    }
}

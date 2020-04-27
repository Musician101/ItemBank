package io.musician101.itembank.sponge.config;

import com.google.common.reflect.TypeToken;
import io.musician101.itembank.common.ItemBankConfig;
import io.musician101.itembank.common.Reference.Config;
import io.musician101.itembank.common.Reference.Messages;
import io.musician101.itembank.sponge.SpongeItemBank;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.item.ItemType;

public class SpongeConfig extends ItemBankConfig<ItemType> {

    public SpongeConfig(File configDir) {
        super(new File(configDir, "config.conf"));
        reload();
    }

    @Override
    public void reload() {
        Logger log = SpongeItemBank.instance().getLogger();
        if (!configFile.exists()) {
            configFile.mkdirs();
            Asset asset = SpongeItemBank.instance().getPluginContainer().getAsset("config.conf").orElseThrow(() -> new IllegalStateException("Default config is missing form jar. Please contact the dev."));
            try {
                asset.copyToDirectory(Paths.get(configFile.getParent()));
            }
            catch (IOException e) {
                log.warn(Messages.fileCreateFail(configFile));
                return;
            }
        }

        HoconConfigurationLoader loader = HoconConfigurationLoader.builder().setFile(configFile).build();
        ConfigurationNode config;
        try {
            config = loader.load();
        }
        catch (IOException e) {
            log.warn(Messages.fileLoadFail(configFile));
            return;
        }

        enableEconomy = config.getNode(Config.ENABLE_ECONOMY).getBoolean(false);
        enableMultiWorldStorage = config.getNode(Config.MULTI_WORLD).getBoolean(false);
        pageLimit = config.getNode(Config.PAGE_LIMIT).getInt(0);
        transactionCost = config.getNode(Config.TRANSACTION_COST).getDouble(5);
        format = config.getNode(Config.FORMAT).getString(Config.YAML);
        try {
            //noinspection UnstableApiUsage
            config.getNode(Config.WHITELIST).getList(TypeToken.of(String.class), Collections.emptyList()).forEach(s -> Sponge.getRegistry().getAllOf(ItemType.class).stream().filter(itemType -> s.equals(itemType.getId())).forEach(whiteList::add));
            //noinspection UnstableApiUsage
            config.getNode(Config.BLACKLIST).getList(TypeToken.of(String.class), Collections.emptyList()).forEach(s -> Sponge.getRegistry().getAllOf(ItemType.class).stream().filter(itemType -> s.equals(itemType.getId())).forEach(blackList::add));
        }
        catch (ObjectMappingException e) {
            e.printStackTrace();
        }

        ConfigurationNode itemRestrictions = config.getNode(Config.ITEM_RESTRICTIONS);
        if (!itemRestrictions.isVirtual()) {
            itemRestrictions.getChildrenMap().forEach((key, value) -> Sponge.getRegistry().getAllOf(ItemType.class).stream().filter(itemType -> key.equals(itemType.getId())).forEach(itemType -> this.itemRestrictions.put(itemType, value.getInt())));
        }
    }

}

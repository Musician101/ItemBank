package io.musician101.itembank.sponge.config;

import io.musician101.itembank.common.AbstractItemBankConfig;
import io.musician101.itembank.common.Reference;
import io.musician101.itembank.common.Reference.Config;
import io.musician101.itembank.common.Reference.Messages;
import io.musician101.itembank.sponge.IBUtils;
import io.musician101.itembank.sponge.SpongeItemBank;
import io.musician101.musicianlibrary.java.MySQLHandler;
import java.io.File;
import java.io.IOException;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.SimpleConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.commented.SimpleCommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.CatalogTypes;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStack.Builder;

public class SpongeConfig extends AbstractItemBankConfig<ItemStack> {

    public SpongeConfig(File configDir) {
        super(new File(configDir, "config.conf"));
        reload();
    }

    private <T extends CatalogType> void setVariant(ItemStack.Builder builder, Key<Value<T>> key, Class<T> typeClass, String variant) {
        Sponge.getRegistry().getType(typeClass, variant).ifPresent(value -> builder.add(key, value));
    }

    @Override
    public ItemStack getItem(ItemStack itemStack) {
        for (ItemStack is : itemList) {
            if (itemStack.getType() == is.getType() && IBUtils.isSameVariant(itemStack, is)) {
                return is;
            }
        }

        return null;
    }

    private void itemList(ConfigurationNode node) {
        Sponge.getRegistry().getAllOf(ItemType.class).forEach(itemType -> {
            ConfigurationNode itemNode = node.getNode(itemType.getId());
            if (!itemNode.isVirtual()) {
                ConfigurationNode amountNode = itemNode.getNode(Config.AMOUNT);
                ConfigurationNode variationNode = itemNode.getNode(Config.VARIATIONS);
                Builder builder = ItemStack.builder().itemType(itemType).quantity(0);
                if (!amountNode.isVirtual()) {
                    builder.quantity(amountNode.getInt());
                }

                if (!variationNode.isVirtual()) {
                    ItemStack tempStack = builder.build();
                    if (tempStack.supports(Keys.BRICK_TYPE)) {
                        setVariant(builder, Keys.BRICK_TYPE, CatalogTypes.BRICK_TYPE, variationNode.getString());
                    }
                    else if (tempStack.supports(Keys.COAL_TYPE)) {
                        setVariant(builder, Keys.COAL_TYPE, CatalogTypes.COAL_TYPE, variationNode.getString());
                    }
                    else if (tempStack.supports(Keys.COOKED_FISH)) {
                        setVariant(builder, Keys.COOKED_FISH, CatalogTypes.COOKED_FISH, variationNode.getString());
                    }
                    else if (tempStack.supports(Keys.DIRT_TYPE)) {
                        setVariant(builder, Keys.DIRT_TYPE, CatalogTypes.DIRT_TYPE, variationNode.getString());
                    }
                    else if (tempStack.supports(Keys.DISGUISED_BLOCK_TYPE)) {
                        setVariant(builder, Keys.DISGUISED_BLOCK_TYPE, CatalogTypes.DISGUISED_BLOCK_TYPE, variationNode.getString());
                    }
                    else if (tempStack.supports(Keys.DOUBLE_PLANT_TYPE)) {
                        setVariant(builder, Keys.DOUBLE_PLANT_TYPE, CatalogTypes.DOUBLE_PLANT_TYPE, variationNode.getString());
                    }
                    else if (tempStack.supports(Keys.DYE_COLOR)) {
                        setVariant(builder, Keys.DYE_COLOR, CatalogTypes.DYE_COLOR, variationNode.getString());
                    }
                    else if (tempStack.supports(Keys.FISH_TYPE)) {
                        setVariant(builder, Keys.FISH_TYPE, CatalogTypes.FISH, variationNode.getString());
                    }
                    else if (tempStack.supports(Keys.GOLDEN_APPLE_TYPE)) {
                        setVariant(builder, Keys.GOLDEN_APPLE_TYPE, CatalogTypes.GOLDEN_APPLE, variationNode.getString());
                    }
                    else if (tempStack.supports(Keys.PRISMARINE_TYPE)) {
                        setVariant(builder, Keys.PRISMARINE_TYPE, CatalogTypes.PRISMARINE_TYPE, variationNode.getString());
                    }
                    else if (tempStack.supports(Keys.QUARTZ_TYPE)) {
                        setVariant(builder, Keys.QUARTZ_TYPE, CatalogTypes.QUARTZ_TYPE, variationNode.getString());
                    }
                    else if (tempStack.supports(Keys.SAND_TYPE)) {
                        setVariant(builder, Keys.SAND_TYPE, CatalogTypes.SAND_TYPE, variationNode.getString());
                    }
                    else if (tempStack.supports(Keys.SANDSTONE_TYPE)) {
                        setVariant(builder, Keys.SANDSTONE_TYPE, CatalogTypes.SANDSTONE_TYPE, variationNode.getString());
                    }
                    else if (tempStack.supports(Keys.SHRUB_TYPE)) {
                        setVariant(builder, Keys.SHRUB_TYPE, CatalogTypes.SHRUB_TYPE, variationNode.getString());
                    }
                    else if (tempStack.supports(Keys.SLAB_TYPE)) {
                        setVariant(builder, Keys.SLAB_TYPE, CatalogTypes.SLAB_TYPE, variationNode.getString());
                    }
                    else if (tempStack.supports(Keys.SPAWNABLE_ENTITY_TYPE)) {
                        setVariant(builder, Keys.SPAWNABLE_ENTITY_TYPE, CatalogTypes.ENTITY_TYPE, variationNode.getString());
                    }
                    else if (tempStack.supports(Keys.STONE_TYPE)) {
                        setVariant(builder, Keys.STONE_TYPE, CatalogTypes.STONE_TYPE, variationNode.getString());
                    }
                    else if (tempStack.supports(Keys.TREE_TYPE)) {
                        setVariant(builder, Keys.TREE_TYPE, CatalogTypes.TREE_TYPE, variationNode.getString());
                    }
                    else if (tempStack.supports(Keys.WALL_TYPE)) {
                        setVariant(builder, Keys.WALL_TYPE, CatalogTypes.WALL_TYPE, variationNode.getString());
                    }
                }
            }
        });
    }

    @Override
    public void reload() {
        SpongeItemBank.instance().ifPresent(plugin -> {

            File configFolder = new File("config", Reference.ID);
            File configFile = new File(configFolder, "config.conf");
            Logger log = plugin.getLogger();
            if (!configFile.exists()) {
                if (!configFile.mkdirs()) {
                    log.error(Messages.fileCreateFail(configFile));
                    return;
                }

                try {
                    configFile.createNewFile();
                    ConfigurationNode config = SimpleCommentedConfigurationNode.root();
                    config.getNode(Config.WHITELIST).setValue(false);
                    config.getNode(Config.MULTI_WORLD).setValue(false);
                    config.getNode(Config.PAGE_LIMIT).setValue(0);
                    ConfigurationNode bedrock = SimpleConfigurationNode.root();
                    bedrock.getNode(Config.AMOUNT).setValue(0);
                    config.getNode(Config.ITEM_LIST).setValue(bedrock);
                    ConfigurationNode mysql = SimpleConfigurationNode.root();
                    mysql.getNode(Config.HOST).setValue("127.0.0.1");
                    mysql.getNode(Config.ENABLE_MYSQL).setValue(false);
                    mysql.getNode(Config.DATABASE).setValue("database");
                    mysql.getNode(Config.PASSWORD).setValue("password");
                    mysql.getNode(Config.PORT).setValue(3306);
                    config.getNode(Config.MYSQL).setValue(mysql);
                    HoconConfigurationLoader.builder().setFile(configFile).build().save(config);
                }
                catch (IOException e) {
                    log.warn(Messages.fileCreateFail(configFile));
                    return;
                }
            }

            ConfigurationLoader<CommentedConfigurationNode> configLoader = HoconConfigurationLoader.builder().setFile(configFile).build();
            ConfigurationNode config;
            try {
                config = configLoader.load();
            }
            catch (IOException e) {
                log.warn(Messages.fileLoadFail(configFile));
                return;
            }

            isWhitelist = config.getNode(Config.WHITELIST).getBoolean(false);
            isMultiWorldStorageEnabled = config.getNode(Config.MULTI_WORLD).getBoolean(false);
            pageLimit = config.getNode(Config.PAGE_LIMIT).getInt(0);
            ConfigurationNode mysql = config.getNode(Config.MYSQL);
            if (!mysql.isVirtual()) {
                useMySQL = mysql.getNode(Config.ENABLE_MYSQL).getBoolean(false);
                if (useMySQL) {
                    plugin.setMySQL(new MySQLHandler(mysql.getNode(Config.DATABASE).getString(Config.DATABASE), mysql.getNode(Config.HOST).getString(Config.LOCAL_HOST), mysql.getNode(Config.PASSWORD).getString(Config.PASSWORD), mysql.getNode(Config.PORT).getString(Config.PORT_DEFAULT), mysql.getNode(Config.USER).getString(Config.USER)));
                }
            }

            if (!config.getNode(Config.ITEM_LIST).isVirtual()) {
                itemList(config.getNode(Config.ITEM_LIST));
            }
            else {
                itemList.add(ItemStack.of(ItemTypes.BEDROCK, 0));
            }
        });
    }
}

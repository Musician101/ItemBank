package musician101.sponge.itembank.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import musician101.itembank.common.AbstractConfig;
import musician101.itembank.common.MySQLHandler;
import musician101.itembank.common.Reference;
import musician101.itembank.common.Reference.Configs;
import musician101.itembank.common.Reference.Messages;
import musician101.sponge.itembank.SpongeItemBank;
import musician101.sponge.itembank.util.IBUtils;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.CatalogTypes;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.manipulator.catalog.CatalogBlockData;
import org.spongepowered.api.data.manipulator.catalog.CatalogItemData;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;

public class SpongeConfig extends AbstractConfig
{
	private ConfigurationLoader<CommentedConfigurationNode> configLoader;
	private ConfigurationNode config;
	private final File configFile;
	private File configFolder;
    //TODO need to create abstract account page
    @Deprecated
    private File playerData;
	private final List<ItemStack> itemList = new ArrayList<>();
	
	public SpongeConfig()
	{
		configFolder = new File("mod", Reference.ID);
		Logger log = SpongeItemBank.logger;
		configFile = new File(configFolder, "config.conf");
		if (!configFile.exists())
		{
			configFile.mkdirs();
			try
			{
				configFile.createNewFile();
				URL url = SpongeItemBank.class.getClass().getClassLoader().getResource("config.conf");
                if (url == null)
                {
                    log.error(Messages.fileCreateFail(configFile));
                    return;
                }

                URLConnection connection = url.openConnection();
				connection.setUseCaches(false);
				InputStream input = connection.getInputStream();
				OutputStream output = new FileOutputStream(configFile);
				byte[] buf = new byte[1024];
				int len;
				while ((len = input.read(buf)) > 0)
					output.write(buf, 0, len);
				
				output.close();
				input.close();
			}
			catch (IOException e)
			{
				log.warn(Messages.fileCreateFail(configFile));
			}
		}
		
		configFolder = configFile.getParentFile();
		playerData = new File(configFolder, "playerdata");
		configLoader = HoconConfigurationLoader.builder().setFile(configFile).build();
		
		try
		{
			config = configLoader.load();
		}
		catch (IOException e)
		{
			log.warn("An error occurred while parsing config.conf. Falling back to defaults.");
		}
		
		reloadConfiguration();
	}
	
	@Override
	public File getPlayerFile(UUID uuid)
	{
		return new File(playerData, uuid.toString() + ".conf");
	}

    @Deprecated
	public File getPlayerData()
	{
		return playerData;
	}
	
	public void reloadConfiguration()
	{
		setIsWhitelist(config.getNode(Configs.WHITELIST).getBoolean(false));
		setMultiWorldStorageEnabled(config.getNode(Configs.MULTI_WORLD).getBoolean(false));
		setPageLimit(config.getNode(Configs.PAGE_LIMIT).getInt(0));
		ConfigurationNode mysql = config.getNode(Configs.MYSQL);
		if (!mysql.isVirtual())
            mysql.getNode(Configs.ENABLE).getBoolean(false);
		
		if (useMYSQL())
			SpongeItemBank.mysql = new MySQLHandler(mysql.getNode(Configs.DATABASE).getString(Configs.DATABASE), mysql.getNode(Configs.HOST).getString(Configs.LOCAL_HOST), mysql.getNode(Configs.PASSWORD).getString(Configs.PASSWORD), mysql.getNode(Configs.PORT).getString(Configs.PORT_DEFAULT), mysql.getNode(Configs.USER).getString(Configs.USER));
		
		if (config.getNode(Configs.ITEM_LIST) != null)
			itemList(config.getNode(Configs.ITEM_LIST));
		else
			itemList.add(ItemStack.of(ItemTypes.BEDROCK, 0));
	}
	
	private void itemList(ConfigurationNode node)
	{
        for (ItemType itemType : Sponge.getRegistry().getAllOf(ItemType.class))
		{
            ConfigurationNode itemNode = node.getNode(itemType.getId());
			if (!itemNode.isVirtual())
			{
                ConfigurationNode amountNode = itemNode.getNode(Configs.AMOUNT);
                ConfigurationNode variationNode = itemNode.getNode(Configs.VARIATIONS);
                if (!amountNode.isVirtual())
                    itemList.add(ItemStack.of(itemType, amountNode.getInt()));
                else if (!variationNode.isVirtual())
                {
                    ItemStack item = ItemStack.of(itemType, 0);
                    if (item.get(CatalogBlockData.STONE_DATA).isPresent())
                        addItem(itemType, variationNode, CatalogTypes.STONE_TYPE, CatalogBlockData.STONE_DATA, Keys.STONE_TYPE);
                    else if (item.get(CatalogBlockData.DIRT_DATA).isPresent())
                        addItem(itemType, variationNode, CatalogTypes.DIRT_TYPE, CatalogBlockData.DIRT_DATA, Keys.DIRT_TYPE);
                    else if (item.get(CatalogBlockData.TREE_DATA).isPresent())
                        addItem(itemType, variationNode, CatalogTypes.TREE_TYPE, CatalogBlockData.TREE_DATA, Keys.TREE_TYPE);
                    else if (item.get(CatalogBlockData.SAND_DATA).isPresent())
                        addItem(itemType, variationNode, CatalogTypes.SAND_TYPE, CatalogBlockData.SAND_DATA, Keys.SAND_TYPE);
                    else if (item.get(CatalogBlockData.SANDSTONE_DATA).isPresent())
                        addItem(itemType, variationNode, CatalogTypes.SANDSTONE_TYPE, CatalogBlockData.SANDSTONE_DATA, Keys.SANDSTONE_TYPE);
                    else if (item.get(CatalogBlockData.SHRUB_DATA).isPresent())
                        addItem(itemType, variationNode, CatalogTypes.SHRUB_TYPE, CatalogBlockData.SHRUB_DATA, Keys.SHRUB_TYPE);
                    else if (item.get(CatalogBlockData.DYEABLE_DATA).isPresent())
                        addItem(itemType, variationNode, CatalogTypes.DYE_COLOR, CatalogBlockData.DYEABLE_DATA, Keys.DYE_COLOR);
                    else if (item.get(CatalogBlockData.SLAB_DATA).isPresent())
                        addItem(itemType, variationNode, CatalogTypes.SLAB_TYPE, CatalogBlockData.SLAB_DATA, Keys.SLAB_TYPE);
                    else if (item.get(CatalogBlockData.DISGUISED_BLOCK_DATA).isPresent())
                        addItem(itemType, variationNode, CatalogTypes.DISGUISED_BLOCK_TYPE, CatalogBlockData.DISGUISED_BLOCK_DATA, Keys.DISGUISED_BLOCK_TYPE);
                    else if (item.get(CatalogBlockData.BRICK_DATA).isPresent())
                        addItem(itemType, variationNode, CatalogTypes.BRICK_TYPE, CatalogBlockData.BRICK_DATA, Keys.BRICK_TYPE);
                    else if (item.get(CatalogBlockData.WALL_DATA).isPresent())
                        addItem(itemType, variationNode, CatalogTypes.WALL_TYPE, CatalogBlockData.WALL_DATA, Keys.WALL_TYPE);
                    else if (item.get(CatalogBlockData.QUARTZ_DATA).isPresent())
                        addItem(itemType, variationNode, CatalogTypes.QUARTZ_TYPE, CatalogBlockData.QUARTZ_DATA, Keys.QUARTZ_TYPE);
                    else if (item.get(CatalogBlockData.PRISMARINE_DATA).isPresent())
                        addItem(itemType, variationNode, CatalogTypes.PRISMARINE_TYPE, CatalogBlockData.PRISMARINE_DATA, Keys.PRISMARINE_TYPE);
                    else if (item.get(CatalogBlockData.DOUBLE_PLANT_DATA).isPresent())
                        addItem(itemType, variationNode, CatalogTypes.DOUBLE_SIZE_PLANT_TYPE, CatalogBlockData.DOUBLE_PLANT_DATA, Keys.DOUBLE_PLANT_TYPE);
                    else if (item.get(CatalogItemData.COAL_ITEM_DATA).isPresent())
                        addItem(itemType, variationNode, CatalogTypes.COAL_TYPE, CatalogItemData.COAL_ITEM_DATA, Keys.COAL_TYPE);
                    else if (item.get(CatalogItemData.GOLDEN_APPLE_ITEM_DATA).isPresent())
                        addItem(itemType, variationNode, CatalogTypes.GOLDEN_APPLE, CatalogItemData.GOLDEN_APPLE_ITEM_DATA, Keys.GOLDEN_APPLE_TYPE);
                    else if (item.get(CatalogItemData.FISH_DATA).isPresent())
                        addItem(itemType, variationNode, CatalogTypes.FISH, CatalogItemData.FISH_DATA, Keys.FISH_TYPE);
                    else if (item.get(CatalogItemData.COOKED_FISH_ITEM_DATA).isPresent())
                        addItem(itemType, variationNode, CatalogTypes.COOKED_FISH, CatalogItemData.COOKED_FISH_ITEM_DATA, Keys.COOKED_FISH);
                    else if (item.get(CatalogItemData.SPAWNABLE_DATA).isPresent())
                        addItem(itemType, variationNode, CatalogTypes.ENTITY_TYPE, CatalogItemData.SPAWNABLE_DATA, Keys.SPAWNABLE_ENTITY_TYPE);
                }
			}
		}
	}

    private <T extends CatalogType, D extends DataManipulator<D, I>, I extends ImmutableDataManipulator<I, D>>
        void addItem(ItemType itemType, ConfigurationNode variationNode, Class<T> typeClass, Class<D> dataClass, Key<Value<T>> key)
    {
        for (T type : Sponge.getRegistry().getAllOf(typeClass))
        {
            ConfigurationNode amount = variationNode.getNode(Configs.AMOUNT);
            ConfigurationNode variation = variationNode.getNode(Configs.VARIATION);
            if (!amount.isVirtual() && type.getId().equalsIgnoreCase(variation.getString("")))
            {
                ItemStack.Builder builder = ItemStack.builder();
                builder.itemType(itemType);
                builder.quantity(amount.getInt(0));
                D data = Sponge.getDataManager().getManipulatorBuilder(dataClass).get().create().set(Sponge.getRegistry().getValueFactory().createValue(key, type));
                builder.itemData(data);
                itemList.add(builder.build());
            }
        }
    }

	public ItemStack getItem(ItemStack itemStack1)
    {
        for (ItemStack itemStack2 : itemList)
        {
            if (itemStack1.getItem() == itemStack2.getItem() && IBUtils.isSameVariant(itemStack1, itemStack2))
                return itemStack2;
        }

        return null;
    }
}

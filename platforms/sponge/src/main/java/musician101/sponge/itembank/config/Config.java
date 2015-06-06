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
import java.util.Set;
import java.util.UUID;

import musician101.itembank.common.config.AbstractConfig;
import musician101.itembank.common.database.MySQLHandler;
import musician101.sponge.itembank.ItemBank;
import musician101.sponge.itembank.lib.Reference.Messages;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import org.slf4j.Logger;
import org.spongepowered.api.CatalogTypes;
import org.spongepowered.api.data.manipulators.DyeableData;
import org.spongepowered.api.data.manipulators.blocks.BrickData;
import org.spongepowered.api.data.manipulators.blocks.DirtData;
import org.spongepowered.api.data.manipulators.blocks.DisguisedBlockData;
import org.spongepowered.api.data.manipulators.blocks.DoublePlantData;
import org.spongepowered.api.data.manipulators.blocks.PrismarineData;
import org.spongepowered.api.data.manipulators.blocks.QuartzData;
import org.spongepowered.api.data.manipulators.blocks.SandData;
import org.spongepowered.api.data.manipulators.blocks.SandstoneData;
import org.spongepowered.api.data.manipulators.blocks.SlabData;
import org.spongepowered.api.data.manipulators.blocks.StoneData;
import org.spongepowered.api.data.manipulators.blocks.TreeData;
import org.spongepowered.api.data.manipulators.blocks.WallData;
import org.spongepowered.api.data.manipulators.catalogs.CatalogBlockData;
import org.spongepowered.api.data.manipulators.catalogs.CatalogItemData;
import org.spongepowered.api.data.manipulators.items.CoalItemData;
import org.spongepowered.api.data.manipulators.items.CookedFishItemData;
import org.spongepowered.api.data.manipulators.items.FishData;
import org.spongepowered.api.data.manipulators.items.GoldenAppleItemData;
import org.spongepowered.api.data.manipulators.items.SpawnableData;
import org.spongepowered.api.data.types.BrickType;
import org.spongepowered.api.data.types.CoalType;
import org.spongepowered.api.data.types.CookedFish;
import org.spongepowered.api.data.types.DirtType;
import org.spongepowered.api.data.types.DisguisedBlockType;
import org.spongepowered.api.data.types.DoubleSizePlantType;
import org.spongepowered.api.data.types.DyeColor;
import org.spongepowered.api.data.types.Fish;
import org.spongepowered.api.data.types.GoldenApple;
import org.spongepowered.api.data.types.PrismarineType;
import org.spongepowered.api.data.types.QuartzType;
import org.spongepowered.api.data.types.SandType;
import org.spongepowered.api.data.types.SandstoneType;
import org.spongepowered.api.data.types.SlabType;
import org.spongepowered.api.data.types.StoneType;
import org.spongepowered.api.data.types.TreeType;
import org.spongepowered.api.data.types.WallType;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackBuilder;
import org.spongepowered.api.service.config.DefaultConfig;

import com.google.inject.Inject;

public class Config extends AbstractConfig
{
	@Inject
	@DefaultConfig(sharedRoot = false)
	File configFile;
	
	@Inject
	@DefaultConfig(sharedRoot = false)
	ConfigurationLoader<CommentedConfigurationNode> configLoader;
	ConfigurationNode config;
	
	boolean useMySQL;
	File configFolder;
	File playerData;
	List<ItemStack> itemlist = new ArrayList<ItemStack>();
	
	public Config()
	{
		Logger log = ItemBank.getLogger();
		configFile = new File("config.conf");
		if (!configFile.exists())
		{
			configFile.mkdirs();
			try
			{
				configFile.createNewFile();
				URL url = this.getClass().getClassLoader().getResource("config.conf");
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
				log.warn(Messages.PREFIX + "Could not create config.conf");
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
	
	public File getConfigFolder()
	{
		return configFolder;
	}
	
	public File getPlayerData()
	{
		return playerData;
	}
	
	public void reloadConfiguration()
	{
		config.getNode("config", "lang").getString();
		setIsWhitelist(config.getNode("whitelist").getBoolean(false));
		setMultiWorldStorageEnabled(config.getNode("multiworld").getBoolean(false));
		setPageLimit(config.getNode("pagelimit").getInt(0));
		ConfigurationNode mysql = config.getNode("mysql");
		if (mysql == null)
			useMySQL = false;
		else
			useMySQL = mysql.getNode("enable").getBoolean(false);
		
		if (useMySQL)
			ItemBank.setMySQLHandler(new MySQLHandler(mysql.getNode("database").getString("database"), mysql.getNode("host").getString("127.0.0.1"), mysql.getNode("password").getString("password"), mysql.getNode("port").getString("3306"), mysql.getNode("user").getString("user")));
		
		if (config.getNode("itemlist") != null)
			itemList(config.getNode("itemlist").getChildrenList());
		else
			itemlist.add(ItemBank.getGame().getRegistry().getItemBuilder().itemType(ItemTypes.BEDROCK).quantity(0).build());
		
		if (config.getNode("lang") != null)
			Messages.init(config.getNode("lang").getString("en"), new File(configFolder, "lang.conf"));
		else
			Messages.init("en", new File(configFolder, "lang.conf"));
	}
	
	private void itemList(List<? extends ConfigurationNode> list)
	{
		for (ConfigurationNode itemNode : list)
		{
			if (itemNode.getNode("id") != null)
			{
				for (Set<ItemType> itemSet : ItemBank.getGame().getRegistry().getGameDictionary().getAllItems().values())
				{
					for (ItemType item : itemSet)
					{
						if (item.getId().equalsIgnoreCase(itemNode.getNode("id").getString()))
						{
							if (itemNode.getNode("variations") != null)
							{
								for (ConfigurationNode variation : itemNode.getNode("variations").getChildrenList())
								{
									ItemStack tempItem = ItemBank.getGame().getRegistry().getItemBuilder().itemType(item).build();
									if (tempItem.getData(CatalogBlockData.STONE_DATA).isPresent())
										handleStone(item, variation);
									else if (tempItem.getData(CatalogBlockData.DIRT_DATA).isPresent())
										handleDirt(item, variation);
									else if (tempItem.getData(CatalogBlockData.TREE_DATA).isPresent())
										handleTrees(item, variation);
									else if (tempItem.getData(CatalogBlockData.SAND_DATA).isPresent())
										handleSand(item, variation);
									else if (tempItem.getData(CatalogBlockData.SANDSTONE_DATA).isPresent())
										handleSandstone(item, variation);
									else if (tempItem.getData(CatalogBlockData.SHRUB_DATA).isPresent())
										handleSandstone(item, variation);
									else if (tempItem.getData(CatalogBlockData.DYEABLE_DATA).isPresent())
										handleDyeables(item, variation);
									else if (tempItem.getData(CatalogBlockData.SLAB_DATA).isPresent())
										handleSlabs(item, variation);
									else if (tempItem.getData(CatalogBlockData.DISGUISED_BLOCK_DATA).isPresent())
										handleDisguisedBlocks(item, variation);
									else if (tempItem.getData(CatalogBlockData.BRICK_DATA).isPresent())
										handleStoneBrick(item, variation);
									else if (tempItem.getData(CatalogBlockData.WALL_DATA).isPresent())
										handleWalls(item, variation);
									else if (tempItem.getData(CatalogBlockData.QUARTZ_DATA).isPresent())
										handleQuartz(item, variation);
									else if (tempItem.getData(CatalogBlockData.PRISMARINE_DATA).isPresent())
										handlePrismarine(item, variation);
									else if (tempItem.getData(CatalogBlockData.DOUBLE_PLANT_DATA).isPresent())
										handleDoublePlant(item, variation);
									else if (tempItem.getData(CatalogItemData.COAL_ITEM_DATA).isPresent())
										handleCoal(item, variation);
									else if (tempItem.getData(CatalogItemData.GOLDEN_APPLE_ITEM_DATA).isPresent())
										handleGoldenApple(item, variation);
									else if (tempItem.getData(CatalogItemData.FISH_DATA).isPresent())
										handleFish(item, variation);
									else if (tempItem.getData(CatalogItemData.COOKED_FISH_ITEM_DATA).isPresent())
										handleCookedFish(item, variation);
									else if (tempItem.getData(CatalogItemData.SPAWNABLE_DATA).isPresent())
										handleSpawnEgg(item, variation);
									else
										handleOther(item, variation);
								}
							}
						}
					}
				}
			}
		}
	}
	
	private void handleStone(ItemType item, ConfigurationNode variation)
	{
		for (StoneType type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.STONE_TYPE))
		{
			if (variation.getNode("name") != null && variation.getNode("name").getString().equalsIgnoreCase(type.getName()))
			{
				ItemStackBuilder builder = ItemBank.getGame().getRegistry().getItemBuilder();
				builder.itemType(item);
				builder.quantity(variation.getNode("amount").getInt(0));
				ItemStack itemStack = builder.build();
				StoneData data = itemStack.getData(CatalogBlockData.STONE_DATA).get();
				data.setValue(type);
				builder.itemData(data);
				itemlist.add(builder.fromItemStack(itemStack).build());
			}
		}
	}
	
	private void handleDirt(ItemType item, ConfigurationNode variation)
	{
		for (DirtType type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.DIRT_TYPE))
		{
			if (variation.getNode("name") != null && variation.getNode("name").getString().equalsIgnoreCase(type.getName()))
			{
				ItemStackBuilder builder = ItemBank.getGame().getRegistry().getItemBuilder();
				builder.itemType(item);
				builder.quantity(variation.getNode("amount").getInt(0));
				ItemStack itemStack = builder.build();
				DirtData data = itemStack.getData(CatalogBlockData.DIRT_DATA).get();
				data.setValue(type);
				builder.itemData(data);
				itemlist.add(builder.fromItemStack(itemStack).build());
			}
		}
	}
	
	private void handleTrees(ItemType item, ConfigurationNode variation)
	{
		for (TreeType type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.TREE_TYPE))
		{
			if (variation.getNode("name") != null && variation.getNode("name").getString().equalsIgnoreCase(type.getName()))
			{
				ItemStackBuilder builder = ItemBank.getGame().getRegistry().getItemBuilder();
				builder.itemType(item);
				builder.quantity(variation.getNode("amount").getInt(0));
				ItemStack itemStack = builder.build();
				TreeData data = itemStack.getData(CatalogBlockData.TREE_DATA).get();
				data.setValue(type);
				builder.itemData(data);
				itemlist.add(builder.fromItemStack(itemStack).build());
			}
		}
	}
	
	private void handleSand(ItemType item, ConfigurationNode variation)
	{
		for (SandType type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.SAND_TYPE))
		{
			if (variation.getNode("name") != null && variation.getNode("name").getString().equalsIgnoreCase(type.getName()))
			{
				ItemStackBuilder builder = ItemBank.getGame().getRegistry().getItemBuilder();
				builder.itemType(item);
				builder.quantity(variation.getNode("amount").getInt(0));
				ItemStack itemStack = builder.build();
				SandData data = itemStack.getData(CatalogBlockData.SAND_DATA).get();
				data.setValue(type);
				builder.itemData(data);
				itemlist.add(builder.fromItemStack(itemStack).build());
			}
		}
	}
	
	private void handleSandstone(ItemType item, ConfigurationNode variation)
	{
		for (SandstoneType type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.SANDSTONE_TYPE))
		{
			if (variation.getNode("name") != null && variation.getNode("name").getString().equalsIgnoreCase(type.getName()))
			{
				ItemStackBuilder builder = ItemBank.getGame().getRegistry().getItemBuilder();
				builder.itemType(item);
				builder.quantity(variation.getNode("amount").getInt(0));
				ItemStack itemStack = builder.build();
				SandstoneData data = itemStack.getData(CatalogBlockData.SANDSTONE_DATA).get();
				data.setValue(type);
				builder.itemData(data);
				itemlist.add(builder.fromItemStack(itemStack).build());
			}
		}
	}
	
	private void handleDyeables(ItemType item, ConfigurationNode variation)
	{
		for (DyeColor type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.DYE_COLOR))
		{
			if (variation.getNode("name") != null && variation.getNode("name").getString().equalsIgnoreCase(type.getName()))
			{
				ItemStackBuilder builder = ItemBank.getGame().getRegistry().getItemBuilder();
				builder.itemType(item);
				builder.quantity(variation.getNode("amount").getInt(0));
				ItemStack itemStack = builder.build();
				DyeableData data = itemStack.getData(CatalogBlockData.DYEABLE_DATA).get();
				data.setValue(type);
				builder.itemData(data);
				itemlist.add(builder.fromItemStack(itemStack).build());
			}
		}
	}
	
	private void handleSlabs(ItemType item, ConfigurationNode variation)
	{
		for (SlabType type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.SLAB_TYPE))
		{
			if (variation.getNode("name") != null && variation.getNode("name").getString().equalsIgnoreCase(type.getName()))
			{
				ItemStackBuilder builder = ItemBank.getGame().getRegistry().getItemBuilder();
				builder.itemType(item);
				builder.quantity(variation.getNode("amount").getInt(0));
				ItemStack itemStack = builder.build();
				SlabData data = itemStack.getData(CatalogBlockData.SLAB_DATA).get();
				data.setValue(type);
				builder.itemData(data);
				itemlist.add(builder.fromItemStack(itemStack).build());
			}
		}
	}
	
	private void handleDisguisedBlocks(ItemType item, ConfigurationNode variation)
	{
		for (DisguisedBlockType type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.DISGUSED_BLOCK_TYPE))
		{
			if (variation.getNode("name") != null && variation.getNode("name").getString().equalsIgnoreCase(type.getName()))
			{
				ItemStackBuilder builder = ItemBank.getGame().getRegistry().getItemBuilder();
				builder.itemType(item);
				builder.quantity(variation.getNode("amount").getInt(0));
				ItemStack itemStack = builder.build();
				DisguisedBlockData data = itemStack.getData(CatalogBlockData.DISGUISED_BLOCK_DATA).get();
				data.setValue(type);
				builder.itemData(data);
				itemlist.add(builder.fromItemStack(itemStack).build());
			}
		}
	}
	
	private void handleStoneBrick(ItemType item, ConfigurationNode variation)
	{
		for (BrickType type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.BRICK_TYPE))
		{
			if (variation.getNode("name") != null && variation.getNode("name").getString().equalsIgnoreCase(type.getName()))
			{
				ItemStackBuilder builder = ItemBank.getGame().getRegistry().getItemBuilder();
				builder.itemType(item);
				builder.quantity(variation.getNode("amount").getInt(0));
				ItemStack itemStack = builder.build();
				BrickData data = itemStack.getData(CatalogBlockData.BRICK_DATA).get();
				data.setValue(type);
				builder.itemData(data);
				itemlist.add(builder.fromItemStack(itemStack).build());
			}
		}
	}
	
	private void handleWalls(ItemType item, ConfigurationNode variation)
	{
		for (WallType type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.WALL_TYPE))
		{
			if (variation.getNode("name") != null && variation.getNode("name").getString().equalsIgnoreCase(type.getName()))
			{
				ItemStackBuilder builder = ItemBank.getGame().getRegistry().getItemBuilder();
				builder.itemType(item);
				builder.quantity(variation.getNode("amount").getInt(0));
				ItemStack itemStack = builder.build();
				WallData data = itemStack.getData(CatalogBlockData.WALL_DATA).get();
				data.setValue(type);
				builder.itemData(data);
				itemlist.add(builder.fromItemStack(itemStack).build());
			}
		}
	}
	
	private void handleQuartz(ItemType item, ConfigurationNode variation)
	{
		for (QuartzType type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.QUARTZ_TYPE))
		{
			if (variation.getNode("name") != null && variation.getNode("name").getString().equalsIgnoreCase(type.getName()))
			{
				ItemStackBuilder builder = ItemBank.getGame().getRegistry().getItemBuilder();
				builder.itemType(item);
				builder.quantity(variation.getNode("amount").getInt(0));
				ItemStack itemStack = builder.build();
				QuartzData data = itemStack.getData(CatalogBlockData.QUARTZ_DATA).get();
				data.setValue(type);
				builder.itemData(data);
				itemlist.add(builder.fromItemStack(itemStack).build());
			}
		}
	}
	
	private void handlePrismarine(ItemType item, ConfigurationNode variation)
	{
		for (PrismarineType type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.PRISMARINE_TYPE))
		{
			if (variation.getNode("name") != null && variation.getNode("name").getString().equalsIgnoreCase(type.getName()))
			{
				ItemStackBuilder builder = ItemBank.getGame().getRegistry().getItemBuilder();
				builder.itemType(item);
				builder.quantity(variation.getNode("amount").getInt(0));
				ItemStack itemStack = builder.build();
				PrismarineData data = itemStack.getData(CatalogBlockData.PRISMARINE_DATA).get();
				data.setValue(type);
				builder.itemData(data);
				itemlist.add(builder.fromItemStack(itemStack).build());
			}
		}
	}
	
	private void handleDoublePlant(ItemType item, ConfigurationNode variation)
	{
		for (DoubleSizePlantType type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.DOUBLE_SIZE_PLANT_TYPE))
		{
			if (variation.getNode("name") != null && variation.getNode("name").getString().equalsIgnoreCase(type.getName()))
			{
				ItemStackBuilder builder = ItemBank.getGame().getRegistry().getItemBuilder();
				builder.itemType(item);
				builder.quantity(variation.getNode("amount").getInt(0));
				ItemStack itemStack = builder.build();
				DoublePlantData data = itemStack.getData(CatalogBlockData.DOUBLE_PLANT_DATA).get();
				data.setValue(type);
				builder.itemData(data);
				itemlist.add(builder.fromItemStack(itemStack).build());
			}
		}
	}
	
	private void handleCoal(ItemType item, ConfigurationNode variation)
	{
		for (CoalType type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.COAL_TYPE))
		{
			if (variation.getNode("name") != null && variation.getNode("name").getString().equalsIgnoreCase(type.getName()))
			{
				ItemStackBuilder builder = ItemBank.getGame().getRegistry().getItemBuilder();
				builder.itemType(item);
				builder.quantity(variation.getNode("amount").getInt(0));
				ItemStack itemStack = builder.build();
				CoalItemData data = itemStack.getData(CatalogItemData.COAL_ITEM_DATA).get();
				data.setValue(type);
				builder.itemData(data);
				itemlist.add(builder.fromItemStack(itemStack).build());
			}
		}
	}
	
	private void handleGoldenApple(ItemType item, ConfigurationNode variation)
	{
		for (GoldenApple type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.GOLDEN_APPLE))
		{
			if (variation.getNode("name") != null && variation.getNode("name").getString().equalsIgnoreCase(type.getName()))
			{
				ItemStackBuilder builder = ItemBank.getGame().getRegistry().getItemBuilder();
				builder.itemType(item);
				builder.quantity(variation.getNode("amount").getInt(0));
				ItemStack itemStack = builder.build();
				GoldenAppleItemData data = itemStack.getData(CatalogItemData.GOLDEN_APPLE_ITEM_DATA).get();
				data.setValue(type);
				builder.itemData(data);
				itemlist.add(builder.fromItemStack(itemStack).build());
			}
		}
	}
	
	private void handleFish(ItemType item, ConfigurationNode variation)
	{
		for (Fish type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.FISH))
		{
			if (variation.getNode("name") != null && variation.getNode("name").getString().equalsIgnoreCase(type.getName()))
			{
				ItemStackBuilder builder = ItemBank.getGame().getRegistry().getItemBuilder();
				builder.itemType(item);
				builder.quantity(variation.getNode("amount").getInt(0));
				ItemStack itemStack = builder.build();
				FishData data = itemStack.getData(CatalogItemData.FISH_DATA).get();
				data.setValue(type);
				builder.itemData(data);
				itemlist.add(builder.fromItemStack(itemStack).build());
			}
		}
	}
	
	private void handleCookedFish(ItemType item, ConfigurationNode variation)
	{
		for (CookedFish type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.COOKED_FISH))
		{
			if (variation.getNode("name") != null && variation.getNode("name").getString().equalsIgnoreCase(type.getName()))
			{
				ItemStackBuilder builder = ItemBank.getGame().getRegistry().getItemBuilder();
				builder.itemType(item);
				builder.quantity(variation.getNode("amount").getInt(0));
				ItemStack itemStack = builder.build();
				CookedFishItemData data = itemStack.getData(CatalogItemData.COOKED_FISH_ITEM_DATA).get();
				data.setValue(type);
				builder.itemData(data);
				itemlist.add(builder.fromItemStack(itemStack).build());
			}
		}
	}
	
	private void handleSpawnEgg(ItemType item, ConfigurationNode variation)
	{
		for (EntityType type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.ENTITY_TYPE))
		{
			if (variation.getNode("name") != null && variation.getNode("name").getString().equalsIgnoreCase(type.getName()))
			{
				ItemStackBuilder builder = ItemBank.getGame().getRegistry().getItemBuilder();
				builder.itemType(item);
				builder.quantity(variation.getNode("amount").getInt(0));
				ItemStack itemStack = builder.build();
				SpawnableData data = itemStack.getData(CatalogItemData.SPAWNABLE_DATA).get();
				data.setValue(type);
				builder.itemData(data);
				itemlist.add(builder.fromItemStack(itemStack).build());
			}
		}
	}
	
	private void handleOther(ItemType item, ConfigurationNode variation)
	{
		ItemStackBuilder builder = ItemBank.getGame().getRegistry().getItemBuilder();
		builder.itemType(item);
		builder.quantity(variation.getNode("amount").getInt(0));
		itemlist.add(builder.build());
	}
}

package musician101.sponge.itembank.util;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import musician101.itembank.common.database.MySQLHandler;
import musician101.sponge.itembank.ItemBank;
import musician101.sponge.itembank.config.SpongeJSONConfig;
import musician101.sponge.itembank.lib.Reference.Messages;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import org.json.simple.parser.ParseException;
import org.spongepowered.api.CatalogTypes;
import org.spongepowered.api.attribute.AttributeModifier;
import org.spongepowered.api.attribute.AttributeModifierBuilder;
import org.spongepowered.api.attribute.Operation;
import org.spongepowered.api.block.BlockStateBuilder;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.DataManipulator;
import org.spongepowered.api.data.manipulators.AttributeData;
import org.spongepowered.api.data.manipulators.ColoredData;
import org.spongepowered.api.data.manipulators.CommandData;
import org.spongepowered.api.data.manipulators.DisplayNameData;
import org.spongepowered.api.data.manipulators.DyeableData;
import org.spongepowered.api.data.manipulators.FireworkData;
import org.spongepowered.api.data.manipulators.MobSpawnerData;
import org.spongepowered.api.data.manipulators.OwnableData;
import org.spongepowered.api.data.manipulators.PotionEffectData;
import org.spongepowered.api.data.manipulators.blocks.BrickData;
import org.spongepowered.api.data.manipulators.blocks.ComparisonData;
import org.spongepowered.api.data.manipulators.blocks.DirtData;
import org.spongepowered.api.data.manipulators.blocks.DisguisedBlockData;
import org.spongepowered.api.data.manipulators.blocks.DoublePlantData;
import org.spongepowered.api.data.manipulators.blocks.PrismarineData;
import org.spongepowered.api.data.manipulators.blocks.QuartzData;
import org.spongepowered.api.data.manipulators.blocks.SandData;
import org.spongepowered.api.data.manipulators.blocks.SandstoneData;
import org.spongepowered.api.data.manipulators.blocks.ShrubData;
import org.spongepowered.api.data.manipulators.blocks.SlabData;
import org.spongepowered.api.data.manipulators.blocks.StoneData;
import org.spongepowered.api.data.manipulators.blocks.TreeData;
import org.spongepowered.api.data.manipulators.blocks.WallData;
import org.spongepowered.api.data.manipulators.catalogs.CatalogBlockData;
import org.spongepowered.api.data.manipulators.catalogs.CatalogItemData;
import org.spongepowered.api.data.manipulators.catalogs.CatalogTileEntityData;
import org.spongepowered.api.data.manipulators.items.BlockItemData;
import org.spongepowered.api.data.manipulators.items.BreakableData;
import org.spongepowered.api.data.manipulators.items.CloneableData;
import org.spongepowered.api.data.manipulators.items.CoalItemData;
import org.spongepowered.api.data.manipulators.items.CookedFishItemData;
import org.spongepowered.api.data.manipulators.items.DurabilityData;
import org.spongepowered.api.data.manipulators.items.EnchantmentData;
import org.spongepowered.api.data.manipulators.items.FishData;
import org.spongepowered.api.data.manipulators.items.GoldenAppleItemData;
import org.spongepowered.api.data.manipulators.items.InventoryItemData;
import org.spongepowered.api.data.manipulators.items.LoreData;
import org.spongepowered.api.data.manipulators.items.PagedData;
import org.spongepowered.api.data.manipulators.items.PlaceableData;
import org.spongepowered.api.data.manipulators.items.SpawnableData;
import org.spongepowered.api.data.manipulators.items.StoredEnchantmentData;
import org.spongepowered.api.data.manipulators.tileentities.BannerData;
import org.spongepowered.api.data.manipulators.tileentities.BeaconData;
import org.spongepowered.api.data.manipulators.tileentities.BrewingData;
import org.spongepowered.api.data.manipulators.tileentities.FurnaceData;
import org.spongepowered.api.data.manipulators.tileentities.LockableData;
import org.spongepowered.api.data.types.BannerPatternShape;
import org.spongepowered.api.data.types.BrickType;
import org.spongepowered.api.data.types.CoalType;
import org.spongepowered.api.data.types.Comparison;
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
import org.spongepowered.api.data.types.ShrubType;
import org.spongepowered.api.data.types.SlabType;
import org.spongepowered.api.data.types.StoneType;
import org.spongepowered.api.data.types.TreeType;
import org.spongepowered.api.data.types.WallType;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.item.Enchantment;
import org.spongepowered.api.item.FireworkEffect;
import org.spongepowered.api.item.FireworkEffectBuilder;
import org.spongepowered.api.item.FireworkShape;
import org.spongepowered.api.item.ItemBlock;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Inventories;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackBuilder;
import org.spongepowered.api.item.inventory.custom.CustomInventoryBuilder;
import org.spongepowered.api.item.inventory.properties.SlotIndex;
import org.spongepowered.api.item.inventory.types.OrderedInventory;
import org.spongepowered.api.potion.PotionEffect;
import org.spongepowered.api.potion.PotionEffectBuilder;
import org.spongepowered.api.potion.PotionEffectType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.translation.Translatable;
import org.spongepowered.api.util.weighted.WeightedEntity;

public class AccountUtil
{
	//TODO make sure maps can't be loaded or saved into accounts do to incomplete API
	public static Inventory getAccount(String worldName, UUID uuid, int page) throws ClassNotFoundException, FileNotFoundException, IOException, ParseException, SQLException
	{
		CustomInventoryBuilder invBuilder = Inventories.customInventoryBuilder();
		invBuilder.size(54);
		invBuilder.name((Translatable) Texts.builder(IBUtils.getNameOf(uuid) + " - " + Messages.ACCOUNT_PAGE + page).build());
		
		OrderedInventory inv = invBuilder.build();
		if (ItemBank.getMySQLHandler() != null)
		{
			ItemBank.getMySQLHandler().querySQL("CREATE TABLE IF NOT EXISTS ib_" + uuid.toString().replace("-", "_") + "(World varchar(255), Page int, Slot int, Material varchar(255), Damage int, Amount int, ItemMeta varchar(300));");
			for (int slot = 0; slot < inv.size(); slot++)
				inv.set(new SlotIndex(slot), getItem(ItemBank.getMySQLHandler().querySQL("SELECT * FROM ib_" + uuid + " WHERE World = \"" + worldName + "\" AND Page = " + page + " AND Slot = " + slot + ";")));
			
			return inv;
		}
		
		File file = ItemBank.getConfig().getPlayerFile(uuid);
		if (!file.exists())
			IBUtils.createPlayerFile(file);
		
		ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setFile(file).build();
		ConfigurationNode account = loader.load();
		ConfigurationNode pageNode = account.getNode(worldName, page + "");
		//TODO getNode() never returns null. use getNode().isVirtual() to check if it actually exists
		if (pageNode.isVirtual())
			return inv;
		
		for (int slot = 0; slot < inv.size(); slot++)
			if (pageNode.getNode(slot + "") != null)
				inv.set(new SlotIndex(slot), getItem(pageNode.getNode(slot + "")));
		
		return inv;
	}
	
	private static ItemStack getItem(ConfigurationNode item)
	{
		for (Set<ItemType> itemSet : ItemBank.getGame().getRegistry().getGameDictionary().getAllItems().values())
		{
			for (ItemType itemType : itemSet)
			{
				if (itemType.getId().equalsIgnoreCase(item.getNode("id").getString()))
				{
					ItemStackBuilder isb = ItemBank.getGame().getRegistry().getItemBuilder();
					isb.itemType(itemType);
					isb.quantity(item.getNode("amount").getInt());
					AccountUtil.getData(isb, item.getNode("variation").getString());
					
					/* Repair Cost is calculated when the item is created and enchantments are applied. */
					ConfigurationNode meta = item.getNode("meta");
					if (!meta.isVirtual())
					{
						if (isb.build().getData(CatalogItemData.DURABILITY_DATA).isPresent())
							getDurabilityData(isb, meta);
						
						if (isb.build().getData(CatalogItemData.BREAKABLE_DATA).isPresent())
							getBreakableData(isb, meta);
						
						if (isb.build().getData(CatalogItemData.PLACEABLE_DATA).isPresent())
							getPlaceableData(isb, meta);
						
						//TODO Need to return to this
						if (isb.build().getData(CatalogItemData.BLOCK_ITEM_DATA).isPresent())
							getBlockItemData(isb, meta);
						
						if (isb.build().getData(CatalogItemData.ENCHANTMENT_DATA).isPresent())
							getEnchantmentData(isb, meta);
						
						if (isb.build().getData(CatalogItemData.STORED_ENCHANTMENT_DATA).isPresent())
							getStoredEnchantmentData(isb, meta);
						
						if (isb.build().getData(CatalogItemData.ATTRIBUTE_DATA).isPresent())
							getAttributeData(isb, meta);
						
						if (isb.build().getData(CatalogItemData.POTION_EFFECT_DATA).isPresent())
							getPotionEffectData(isb, meta);
						
						if (isb.build().getData(CatalogItemData.COLORED_ITEM_DATA).isPresent())
							getColoredItemData(isb, meta);
						
						/* Sponge recognizes Written Book titles as display names. */
						if (isb.build().getData(CatalogItemData.DISPLAY_NAME_DATA).isPresent())
							getDisplayNameData(isb, meta);
						
						if (isb.build().getData(CatalogItemData.LORE_DATA).isPresent())
							getLoreData(isb, meta);
						
						if (isb.build().getData(CatalogItemData.CLONEABLE_DATA).isPresent())
							getCloneableData(isb, meta);
						
						if (isb.build().getData(CatalogItemData.PAGED_DATA).isPresent())
							getPagedData(isb, meta);
						
						/* This might also work with book authors. */
						if (isb.build().getData(CatalogTileEntityData.OWNABLE_DATA).isPresent())
							getOwnableData(isb, meta);
						
						if (isb.build().getData(CatalogItemData.FIREWORK_DATA).isPresent())
							getFireworkData(isb, meta);
						
						//TODO after looking at the source code, this might be incomplete
						/*if (meta.getNode("") != null)
						{
							MapItemData data = isb.build().getData(CatalogItemData.MAP_ITEM_DATA).get();
							isb.itemData(data);
						}*/
					}
					
					return isb.build();
				}
			}
		}
		
		return null;
	}
	
	private static ItemStack getItem(ResultSet rs) throws ParseException, SQLException
	{
		while (rs.next())
		{
			for (Set<ItemType> itemSet : ItemBank.getGame().getRegistry().getGameDictionary().getAllItems().values())
			{
				for (ItemType itemType : itemSet)
				{
					if (itemType.getName().equalsIgnoreCase(rs.getString("ID")))
					{
						ItemStackBuilder isb = ItemBank.getGame().getRegistry().getItemBuilder();
						isb.itemType(itemType);
						isb.quantity(rs.getInt("Amount"));
						if (isb.build().getData(CatalogBlockData.STONE_DATA).isPresent())
						{
							for (StoneType type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.STONE_TYPE))
							{
								if (rs.getString("Variation").equalsIgnoreCase(type.getName()))
								{
									StoneData data = isb.build().getData(CatalogBlockData.STONE_DATA).get();
									data.setValue(type);
									isb.itemData(data);
								}
							}
						}
						else if (isb.build().getData(CatalogBlockData.DIRT_DATA).isPresent())
						{
							for (DirtType type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.DIRT_TYPE))
							{
								if (rs.getString("Variation").equalsIgnoreCase(type.getName()))
								{
									DirtData data = isb.build().getData(CatalogBlockData.DIRT_DATA).get();
									data.setValue(type);
									isb.itemData(data);
								}
							}
						}
						else if (isb.build().getData(CatalogBlockData.TREE_DATA).isPresent())
						{
							for (DirtType type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.DIRT_TYPE))
							{
								if (rs.getString("Variation").equalsIgnoreCase(type.getName()))
								{
									DirtData data = isb.build().getData(CatalogBlockData.DIRT_DATA).get();
									data.setValue(type);
									isb.itemData(data);
								}
							}
						}
						else if (isb.build().getData(CatalogBlockData.SAND_DATA).isPresent())
						{
							for (SandType type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.SAND_TYPE))
							{
								if (rs.getString("Variation").equalsIgnoreCase(type.getName()))
								{
									SandData data = isb.build().getData(CatalogBlockData.SAND_DATA).get();
									data.setValue(type);
									isb.itemData(data);
								}
							}
						}
						else if (isb.build().getData(CatalogBlockData.SANDSTONE_DATA).isPresent())
						{
							for (SandstoneType type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.SANDSTONE_TYPE))
							{
								if (rs.getString("Variation").equalsIgnoreCase(type.getName()))
								{
									SandstoneData data = isb.build().getData(CatalogBlockData.SANDSTONE_DATA).get();
									data.setValue(type);
									isb.itemData(data);
								}
							}
						}
						else if (isb.build().getData(CatalogBlockData.SHRUB_DATA).isPresent())
						{
							for (ShrubType type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.SHRUB_TYPE))
							{
								if (rs.getString("Variation").equalsIgnoreCase(type.getName()))
								{
									ShrubData data = isb.build().getData(CatalogBlockData.SHRUB_DATA).get();
									data.setValue(type);
									isb.itemData(data);
								}
							}
						}
						else if (isb.build().getData(CatalogBlockData.DYEABLE_DATA).isPresent())
						{
							for (DyeColor type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.DYE_COLOR))
							{
								if (rs.getString("Variation").equalsIgnoreCase(type.getName()))
								{
									DyeableData data = isb.build().getData(CatalogBlockData.DYEABLE_DATA).get();
									data.setValue(type);
									isb.itemData(data);
								}
							}
						}
						else if (isb.build().getData(CatalogBlockData.SLAB_DATA).isPresent())
						{
							for (SlabType type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.SLAB_TYPE))
							{
								if (rs.getString("Variation").equalsIgnoreCase(type.getName()))
								{
									SlabData data = isb.build().getData(CatalogBlockData.SLAB_DATA).get();
									data.setValue(type);
									isb.itemData(data);
								}
							}
						}
						else if (isb.build().getData(CatalogBlockData.DISGUISED_BLOCK_DATA).isPresent())
						{
							for (DisguisedBlockType type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.DISGUSED_BLOCK_TYPE))
							{
								if (rs.getString("Variation").equalsIgnoreCase(type.getName()))
								{
									DisguisedBlockData data = isb.build().getData(CatalogBlockData.DISGUISED_BLOCK_DATA).get();
									data.setValue(type);
									isb.itemData(data);
								}
							}
						}
						else if (isb.build().getData(CatalogBlockData.BRICK_DATA).isPresent())
						{
							for (BrickType type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.BRICK_TYPE))
							{
								if (rs.getString("Variation").equalsIgnoreCase(type.getName()))
								{
									BrickData data = isb.build().getData(CatalogBlockData.BRICK_DATA).get();
									data.setValue(type);
									isb.itemData(data);
								}
							}
						}
						else if (isb.build().getData(CatalogBlockData.WALL_DATA).isPresent())
						{
							for (WallType type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.WALL_TYPE))
							{
								if (rs.getString("Variation").equalsIgnoreCase(type.getName()))
								{
									WallData data = isb.build().getData(CatalogBlockData.WALL_DATA).get();
									data.setValue(type);
									isb.itemData(data);
								}
							}
						}
						else if (isb.build().getData(CatalogBlockData.QUARTZ_DATA).isPresent())
						{
							for (QuartzType type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.QUARTZ_TYPE))
							{
								if (rs.getString("Variation").equalsIgnoreCase(type.getName()))
								{
									QuartzData data = isb.build().getData(CatalogBlockData.QUARTZ_DATA).get();
									data.setValue(type);
									isb.itemData(data);
								}
							}
						}
						else if (isb.build().getData(CatalogBlockData.PRISMARINE_DATA).isPresent())
						{
							for (PrismarineType type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.PRISMARINE_TYPE))
							{
								if (rs.getString("Variation").equalsIgnoreCase(type.getName()))
								{
									PrismarineData data = isb.build().getData(CatalogBlockData.PRISMARINE_DATA).get();
									data.setValue(type);
									isb.itemData(data);
								}
							}
						}
						else if (isb.build().getData(CatalogBlockData.DOUBLE_PLANT_DATA).isPresent())
						{
							for (DoubleSizePlantType type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.DOUBLE_SIZE_PLANT_TYPE))
							{
								if (rs.getString("Variation").equalsIgnoreCase(type.getName()))
								{
									DoublePlantData data = isb.build().getData(CatalogBlockData.DOUBLE_PLANT_DATA).get();
									data.setValue(type);
									isb.itemData(data);
								}
							}
						}
						else if (isb.build().getData(CatalogItemData.COAL_ITEM_DATA).isPresent())
						{
							for (CoalType type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.COAL_TYPE))
							{
								if (rs.getString("Variation").equalsIgnoreCase(type.getName()))
								{
									CoalItemData data = isb.build().getData(CatalogItemData.COAL_ITEM_DATA).get();
									data.setValue(type);
									isb.itemData(data);
								}
							}
						}
						else if (isb.build().getData(CatalogItemData.GOLDEN_APPLE_ITEM_DATA).isPresent())
						{
							for (GoldenApple type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.GOLDEN_APPLE))
							{
								if (rs.getString("Variation").equalsIgnoreCase(type.getName()))
								{
									GoldenAppleItemData data = isb.build().getData(CatalogItemData.GOLDEN_APPLE_ITEM_DATA).get();
									data.setValue(type);
									isb.itemData(data);
								}
							}
						}
						else if (isb.build().getData(CatalogItemData.FISH_DATA).isPresent())
						{
							for (Fish type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.FISH))
							{
								if (rs.getString("Variation").equalsIgnoreCase(type.getName()))
								{
									FishData data = isb.build().getData(CatalogItemData.FISH_DATA).get();
									data.setValue(type);
									isb.itemData(data);
								}
							}
						}
						else if (isb.build().getData(CatalogItemData.COOKED_FISH_ITEM_DATA).isPresent())
						{
							for (CookedFish type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.COOKED_FISH))
							{
								if (rs.getString("Variation").equalsIgnoreCase(type.getName()))
								{
									CookedFishItemData data = isb.build().getData(CatalogItemData.COOKED_FISH_ITEM_DATA).get();
									data.setValue(type);
									isb.itemData(data);
								}
							}
						}
						else if (isb.build().getData(CatalogItemData.SPAWNABLE_DATA).isPresent())
						{
							for (EntityType type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.ENTITY_TYPE))
							{
								if (rs.getString("Variation").equalsIgnoreCase(type.getName()))
								{
									SpawnableData data = isb.build().getData(CatalogItemData.SPAWNABLE_DATA).get();
									data.setValue(type);
									isb.itemData(data);
								}
							}
						}
						
						/* Repair Cost is calculated when the item is created and enchantments are applied. */
						SpongeJSONConfig meta = SpongeJSONConfig.loadSpongeJSONConfig(rs.getString("Meta"));
						if (meta != null)
						{
							if (meta.containsKey("durability"))
							{
								SpongeJSONConfig durability = meta.getSpongeJSONConfig("durability");
								DurabilityData data = isb.build().getData(CatalogItemData.DURABILITY_DATA).get();
								data.setDurability(durability.getInt("uses"));
								data.setBreakable(durability.getBoolean("breakable"));
								isb.itemData(data);
							}
							
							if (meta.containsKey("can_destroy"))
							{
								BreakableData data = isb.build().getData(CatalogItemData.BREAKABLE_DATA).get();
								for (String blockType : meta.getList("can_destroy"))
									for (Set<ItemType> it : ItemBank.getGame().getRegistry().getGameDictionary().getAllItems().values())
										for (ItemType item_type : it)
											if (item_type.getId().equalsIgnoreCase(blockType))
												data.add(((ItemBlock) item_type).getBlock());
								
								isb.itemData(data);
							}
							
							if (meta.containsKey("can_place_on"))
							{
								PlaceableData data = isb.build().getData(CatalogItemData.PLACEABLE_DATA).get();
								for (String blockType : meta.getList("can_place_on"))
									for (Set<ItemType> it : ItemBank.getGame().getRegistry().getGameDictionary().getAllItems().values())
										for (ItemType item_type : it)
											if (item_type.getId().equalsIgnoreCase(blockType))
												data.add(((ItemBlock) item_type).getBlock());
								
								isb.itemData(data);
							}
							
							// TODO BlockStateBuilder isn't in the snapshot build yet, don't know why
							/*if (meta.getNode("tile_entity") != null)
							{
								BlockItemData data = isb.build().getData(CatalogItemData.BLOCK_ITEM_DATA).get();
								BlockState state = ((ItemBlock) type).getBlock().getDefaultState();
								BlockStateBuilder test;
							}*/
							
							if (meta.containsKey("enchantments"))
							{
								EnchantmentData data = isb.build().getData(CatalogItemData.ENCHANTMENT_DATA).get();
								Map<Enchantment, Integer> enchants = meta.getEnchants("enchaments");
								for (Enchantment enchant : enchants.keySet())
									data.set(enchant, enchants.get(enchant.getName()));
								
								isb.itemData(data);
							}
							
							if (meta.containsKey("stored_enchantments"))
							{
								StoredEnchantmentData data = isb.build().getData(CatalogItemData.STORED_ENCHANTMENT_DATA).get();
								Map<Enchantment, Integer> enchants = meta.getEnchants("stored_enchantments");
								for (Enchantment enchant : enchants.keySet())
									data.set(enchant, enchants.get(enchant.getName()));
								
								isb.itemData(data);
							}
							
							if (meta.containsKey("attributes"))
							{
								AttributeData data = isb.build().getData(CatalogItemData.ATTRIBUTE_DATA).get();
								for (SpongeJSONConfig attribute : meta.getSpongeJSONConfigList("attributes"))
								{
									AttributeModifierBuilder amb = ItemBank.getGame().getRegistry().getAttributeModifierBuilder();
									if (attribute.containsKey("operation"))
										for (Operation operation : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.OPERATION))
											if (operation.getName().equalsIgnoreCase(attribute.getString("operation")))
												amb.operation(operation);
									
									if (attribute.containsKey("value"))
										amb.value(attribute.getDouble("value"));
									
									data.add(amb.build());
								}
								
								isb.itemData(data);
							}
							
							if (meta.containsKey("potion_effects"))
							{
								PotionEffectData data = isb.build().getData(CatalogItemData.POTION_EFFECT_DATA).get();
								for (PotionEffect potion : meta.getPotionEffectList("potion_effects"))
									data.add(potion);
								
								isb.itemData(data);
							}
							
							if (meta.containsKey("color"))
							{
								ColoredData data = isb.build().getData(CatalogItemData.COLORED_ITEM_DATA).get();
								data.setColor(meta.getColor("color"));
								isb.itemData(data);
							}
							
							/* Sponge recognizes Written Book titles as display names. */
							if (meta.containsKey("name"))
							{
								DisplayNameData data = isb.build().getData(CatalogItemData.DISPLAY_NAME_DATA).get();
								data.setDisplayName(IBUtils.stringToText(meta.getString("name")));
								isb.itemData(data);
							}
							
							if (meta.containsKey("lore"))
							{
								LoreData data = isb.build().getData(CatalogItemData.LORE_DATA).get();
								for (String line : meta.getList("lore"))
									data.add(IBUtils.stringToText(line));
								
								isb.itemData(data);
							}
							
							if (meta.containsKey("times_copied"))
							{
								CloneableData data = isb.build().getData(CatalogItemData.CLONEABLE_DATA).get();
								data.setGeneration(meta.getInt("times_copied"));
								isb.itemData(data);
							}
							
							if (meta.containsKey("pages"))
							{
								PagedData data = isb.build().getData(CatalogItemData.PAGED_DATA).get();
								for (String page : meta.getList("pages"))
									data.add(IBUtils.stringToText(page));
								
								isb.itemData(data);
							}
							
							/* This might also work with book authors. */
							if (meta.containsKey("owner"))
							{
								SpongeJSONConfig owner = meta.getSpongeJSONConfig("owner");
								OwnableData data = isb.build().getData(CatalogTileEntityData.OWNABLE_DATA).get();
								data.setProfile(ItemBank.getGame().getRegistry().createGameProfile(UUID.fromString(owner.getString("uuid")), owner.getString("name")));
								isb.itemData(data);
							}
							
							if (meta.containsKey("firework"))
							{
								SpongeJSONConfig firework = meta.getSpongeJSONConfig("firework");
								FireworkData data = isb.build().getData(CatalogItemData.FIREWORK_DATA).get();
								data.setFlightModifier(firework.getInt("flight_modifier"));
								for (FireworkEffect effect : firework.getFireworkEffectList("effects"))
									data.add(effect);
								
								isb.itemData(data);
							}
						}
							
						return isb.build();
					}
				}
			}
		}
		
		return null;
	}
	
	public static void saveAccount(String worldName, UUID uuid, Inventory inventory, int page) throws ClassNotFoundException, FileNotFoundException, IOException, ParseException, SQLException
	{
		if (ItemBank.getMySQLHandler() != null)
		{
			MySQLHandler sql = ItemBank.getMySQLHandler();
			sql.querySQL("CREATE TABLE IF NOT EXISTS ib_" + uuid.toString().replace("-", "_") + "(World varchar(255), Page int, Slot int, Id varchar(255), Variation varchar(255), Amount int, Meta varchar(500));");
			for (Inventory slot : inventory.slots())
			{
				sql.querySQL("DELETE FROM ib_" + uuid + " WHERE World = \"" + worldName + "\" AND Page = " + page + " AND Slot = " + slot + ";");
				if (slot.peek().isPresent())
				{
					ItemStack item = slot.peek().get();
					sql.updateSQL("INSERT INTO ib_" + uuid + "(World, Page, Slot, Id, Variation, Amount, Meta) VALUES (\"" + worldName + "\", " + page + ", " + slot + ", \"" + item.getItem().getName() + "\", " + getVariation(item) + ", " + item.getQuantity() + ", \""+ metaToJson(item).replace("\"", "\\\"") + "\");");
				}
			}
			
			return;
		}
		
		File file = ItemBank.getConfig().getPlayerFile(uuid);
		if (!file.exists())
			IBUtils.createPlayerFile(file);
		//TODO need to add WetData check
		ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setFile(file).build();
		ConfigurationNode account = loader.load();
		ConfigurationNode pageNode = account.getNode(worldName, page + "");
		for (Inventory slot : inventory.slots())
		{
			if (slot.peek().isPresent())
			{
				ItemStack item = slot.peek().get();
				pageNode.getNode("");
			}
		}
	}
	
	private static void getData(ItemStackBuilder isb, String variation)
	{
		ItemStack item = isb.build();
		if (item.getData(CatalogBlockData.STONE_DATA).isPresent())
			getStoneData(isb, variation);
		else if (item.getData(CatalogBlockData.DIRT_DATA).isPresent())
			getDirtData(isb, variation);
		else if (item.getData(CatalogBlockData.TREE_DATA).isPresent())
			getTreeData(isb, variation);
		else if (item.getData(CatalogBlockData.SAND_DATA).isPresent())
			getSandData(isb, variation);
		else if (item.getData(CatalogBlockData.SANDSTONE_DATA).isPresent())
			getSandstoneData(isb, variation);
		else if (item.getData(CatalogBlockData.SHRUB_DATA).isPresent())
			getShrubData(isb, variation);
		else if (item.getData(CatalogBlockData.DYEABLE_DATA).isPresent())
			getDyeableData(isb, variation);
		else if (item.getData(CatalogBlockData.SLAB_DATA).isPresent())
			getSlabData(isb, variation);
		else if (item.getData(CatalogBlockData.DISGUISED_BLOCK_DATA).isPresent())
			getDisguisedBlockData(isb, variation);
		else if (item.getData(CatalogBlockData.BRICK_DATA).isPresent())
			getBrickData(isb, variation);
		else if (item.getData(CatalogBlockData.WALL_DATA).isPresent())
			getWallData(isb, variation);
		else if (item.getData(CatalogBlockData.QUARTZ_DATA).isPresent())
			getQuartzData(isb, variation);
		else if (item.getData(CatalogBlockData.PRISMARINE_DATA).isPresent())
			getPrismarineData(isb, variation);
		else if (item.getData(CatalogBlockData.DOUBLE_PLANT_DATA).isPresent())
			getDoubleSizePlantData(isb, variation);
		else if (item.getData(CatalogItemData.COAL_ITEM_DATA).isPresent())
			getCoalItemData(isb, variation);
		else if (item.getData(CatalogItemData.GOLDEN_APPLE_ITEM_DATA).isPresent())
			getGoldenAppleItemData(isb, variation);
		else if (item.getData(CatalogItemData.FISH_DATA).isPresent())
			getFishData(isb, variation);
		else if (item.getData(CatalogItemData.COOKED_FISH_ITEM_DATA).isPresent())
			getCookedFishItemData(isb, variation);
		else if (item.getData(CatalogItemData.SPAWNABLE_DATA).isPresent())
			getSpawnableData(isb, variation);
	}
	
	private static void getStoneData(ItemStackBuilder isb, String variation)
	{
		StoneData data = isb.build().getData(CatalogBlockData.STONE_DATA).get();
		for (StoneType type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.STONE_TYPE))
			if (type.getName().equalsIgnoreCase(variation))
				isb.itemData(data.setValue(type));
	}
	
	private static void getDirtData(ItemStackBuilder isb, String variation)
	{
		DirtData data = isb.build().getData(CatalogBlockData.DIRT_DATA).get();
		for (DirtType type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.DIRT_TYPE))
			if (type.getName().equalsIgnoreCase(variation))
				isb.itemData(data.setValue(type));
	}
	
	private static void getTreeData(ItemStackBuilder isb, String variation)
	{
		TreeData data = isb.build().getData(CatalogBlockData.TREE_DATA).get();
		for (TreeType type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.TREE_TYPE))
			if (type.getName().equalsIgnoreCase(variation))
				isb.itemData(data.setValue(type));
	}
	
	private static void getSandData(ItemStackBuilder isb, String variation)
	{
		SandData data = isb.build().getData(CatalogBlockData.SAND_DATA).get();
		for (SandType type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.SAND_TYPE))
			if (type.getName().equalsIgnoreCase(variation))
				isb.itemData(data.setValue(type));
	}
	
	private static void getSandstoneData(ItemStackBuilder isb, String variation)
	{
		SandstoneData data = isb.build().getData(CatalogBlockData.SANDSTONE_DATA).get();
		for (SandstoneType type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.SANDSTONE_TYPE))
			if (type.getName().equalsIgnoreCase(variation))
				isb.itemData(data.setValue(type));
	}
	
	private static void getShrubData(ItemStackBuilder isb, String variation)
	{
		ShrubData data = isb.build().getData(CatalogBlockData.SHRUB_DATA).get();
		for (ShrubType type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.SHRUB_TYPE))
			if (type.getName().equalsIgnoreCase(variation))
				isb.itemData(data.setValue(type));
	}
	
	private static void getDyeableData(ItemStackBuilder isb, String variation)
	{
		DyeableData data = isb.build().getData(CatalogBlockData.DYEABLE_DATA).get();
		for (DyeColor type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.DYE_COLOR))
			if (type.getName().equalsIgnoreCase(variation))
				isb.itemData(data.setValue(type));
	}
	
	private static void getSlabData(ItemStackBuilder isb, String variation)
	{
		SlabData data = isb.build().getData(CatalogBlockData.SLAB_DATA).get();
		for (SlabType type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.SLAB_TYPE))
			if (type.getName().equalsIgnoreCase(variation))
				isb.itemData(data.setValue(type));
	}
	
	private static void getDisguisedBlockData(ItemStackBuilder isb, String variation)
	{
		DisguisedBlockData data = isb.build().getData(CatalogBlockData.DISGUISED_BLOCK_DATA).get();
		for (DisguisedBlockType type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.DISGUSED_BLOCK_TYPE))
			if (type.getName().equalsIgnoreCase(variation))
				isb.itemData(data.setValue(type));
	}
	
	private static void getBrickData(ItemStackBuilder isb, String variation)
	{
		BrickData data = isb.build().getData(CatalogBlockData.BRICK_DATA).get();
		for (BrickType type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.BRICK_TYPE))
			if (type.getName().equalsIgnoreCase(variation))
				isb.itemData(data.setValue(type));
	}
	
	private static void getWallData(ItemStackBuilder isb, String variation)
	{
		WallData data = isb.build().getData(CatalogBlockData.WALL_DATA).get();
		for (WallType type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.WALL_TYPE))
			if (type.getName().equalsIgnoreCase(variation))
				isb.itemData(data.setValue(type));
	}
	
	private static void getQuartzData(ItemStackBuilder isb, String variation)
	{
		QuartzData data = isb.build().getData(CatalogBlockData.QUARTZ_DATA).get();
		for (QuartzType type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.QUARTZ_TYPE))
			if (type.getName().equalsIgnoreCase(variation))
				isb.itemData(data.setValue(type));
	}
	
	private static void getPrismarineData(ItemStackBuilder isb, String variation)
	{
		PrismarineData data = isb.build().getData(CatalogBlockData.PRISMARINE_DATA).get();
		for (PrismarineType type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.PRISMARINE_TYPE))
			if (type.getName().equalsIgnoreCase(variation))
				isb.itemData(data.setValue(type));
	}
	
	private static void getDoubleSizePlantData(ItemStackBuilder isb, String variation)
	{
		DoublePlantData data = isb.build().getData(CatalogBlockData.DOUBLE_PLANT_DATA).get();
		for (DoubleSizePlantType type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.DOUBLE_SIZE_PLANT_TYPE))
			if (type.getName().equalsIgnoreCase(variation))
				isb.itemData(data.setValue(type));
	}
	
	private static void getCoalItemData(ItemStackBuilder isb, String variation)
	{
		CoalItemData data = isb.build().getData(CatalogItemData.COAL_ITEM_DATA).get();
		for (CoalType type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.COAL_TYPE))
			if (type.getName().equalsIgnoreCase(variation))
				isb.itemData(data.setValue(type));
	}
	
	private static void getGoldenAppleItemData(ItemStackBuilder isb, String variation)
	{
		GoldenAppleItemData data = isb.build().getData(CatalogItemData.GOLDEN_APPLE_ITEM_DATA).get();
		for (GoldenApple type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.GOLDEN_APPLE))
			if (type.getName().equalsIgnoreCase(variation))
				isb.itemData(data.setValue(type));
	}
	
	private static void getFishData(ItemStackBuilder isb, String variation)
	{
		FishData data = isb.build().getData(CatalogItemData.FISH_DATA).get();
		for (Fish type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.FISH))
			if (type.getName().equalsIgnoreCase(variation))
				isb.itemData(data.setValue(type));
	}
	
	private static void getCookedFishItemData(ItemStackBuilder isb, String variation)
	{
		CookedFishItemData data = isb.build().getData(CatalogItemData.COOKED_FISH_ITEM_DATA).get();
		for (CookedFish type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.COOKED_FISH))
			if (type.getName().equalsIgnoreCase(variation))
				isb.itemData(data.setValue(type));
	}
	
	private static void getSpawnableData(ItemStackBuilder isb, String variation)
	{
		SpawnableData data = isb.build().getData(CatalogItemData.SPAWNABLE_DATA).get();
		for (EntityType type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.ENTITY_TYPE))
			if (type.getName().equalsIgnoreCase(variation))
				isb.itemData(data.setValue(type));
	}
	
	private static void getDurabilityData(ItemStackBuilder isb, ConfigurationNode meta)
	{
		DurabilityData data = isb.build().getData(CatalogItemData.DURABILITY_DATA).get();
		ConfigurationNode durability = meta.getNode("durability");
		if (durability.isVirtual())
		{
			data.setBreakable(durability.getNode("unbreakable").getBoolean(false));
			data.setDurability(durability.getNode("uses").getInt(0));
		}
		
		isb.itemData(data);
	}

	private static void getBreakableData(ItemStackBuilder isb, ConfigurationNode meta)
	{
		BreakableData data = isb.build().getData(CatalogItemData.BREAKABLE_DATA).get();
		for (Object blockType : (List<?>) meta.getValue(new ArrayList<Object>()))
			for (Set<ItemType> it : ItemBank.getGame().getRegistry().getGameDictionary().getAllItems().values())
				for (ItemType item_type : it)
					if (item_type.getId().equalsIgnoreCase(blockType.toString()))
						data.add(((ItemBlock) item_type).getBlock());
		
		isb.itemData(data);
	}
	
	private static void getPlaceableData(ItemStackBuilder isb, ConfigurationNode meta)
	{
		PlaceableData data = isb.build().getData(CatalogItemData.PLACEABLE_DATA).get();
		for (Object blockType : (List<?>) meta.getValue())
			for (Set<ItemType> it : ItemBank.getGame().getRegistry().getGameDictionary().getAllItems().values())
				for (ItemType item_type : it)
					if (item_type.getId().equalsIgnoreCase(blockType.toString()))
						data.add(((ItemBlock) item_type).getBlock());
		
		isb.itemData(data);
	}
	
	private static void getEnchantmentData(ItemStackBuilder isb, ConfigurationNode meta)
	{
		ConfigurationNode enchantNode = meta.getNode("enchantments");
		EnchantmentData data = isb.build().getData(CatalogItemData.ENCHANTMENT_DATA).get();
		for (Enchantment enchant : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.ENCHANTMENT))
			if (!enchantNode.getNode(enchant.getName()).isVirtual())
				data.set(enchant, enchantNode.getNode(enchant.getName()).getInt());
		
		isb.itemData(data);
	}
	
	private static void getStoredEnchantmentData(ItemStackBuilder isb, ConfigurationNode meta)
	{
		ConfigurationNode enchantNode = meta.getNode("stored_enchantments");
		StoredEnchantmentData data = isb.build().getData(CatalogItemData.STORED_ENCHANTMENT_DATA).get();
		for (Enchantment enchant : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.ENCHANTMENT))
			if (!enchantNode.getNode(enchant.getName()).isVirtual())
				data.set(enchant, enchantNode.getNode(enchant.getName()).getInt());
		
		isb.itemData(data);
	}
	
	private static void getAttributeData(ItemStackBuilder isb, ConfigurationNode meta)
	{
		AttributeData data = isb.build().getData(CatalogItemData.ATTRIBUTE_DATA).get();
		for (ConfigurationNode attribute : meta.getNode("attributes").getChildrenList())
		{
			AttributeModifierBuilder amb = ItemBank.getGame().getRegistry().getAttributeModifierBuilder();
			if (attribute.getNode("operation") != null)
				for (Operation operation : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.OPERATION))
					if (operation.getName().equalsIgnoreCase(attribute.getNode("operation").getString()))
						amb.operation(operation);
			
			if (attribute.getNode("value") != null)
				amb.value(attribute.getNode("value").getDouble());
			
			data.add(amb.build());
		}
		
		isb.itemData(data);
	}
	
	private static void getPotionEffectData(ItemStackBuilder isb, ConfigurationNode meta)
	{
		PotionEffectData data = isb.build().getData(CatalogItemData.POTION_EFFECT_DATA).get();
		for (ConfigurationNode potion : meta.getNode("potion_effects").getChildrenList())
		{
			PotionEffectBuilder peb = ItemBank.getGame().getRegistry().getPotionEffectBuilder();
			if (potion.getNode("name") != null)
				for (PotionEffectType effect : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.POTION_EFFECT_TYPE))
					if (effect.getName().equalsIgnoreCase(potion.getNode("name").getString()))
						peb.potionType(effect);
			
			if (potion.getNode("amplifier") != null)
				peb.amplifier(potion.getNode("amplifier").getInt());
			
			if (potion.getNode("duration") != null)
				peb.duration(potion.getNode("duration").getInt());
			
			if (potion.getNode("is_ambient") != null)
				peb.ambience(potion.getNode("is_ambient").getBoolean());
			
			if (potion.getNode("show_particles") != null)
				peb.particles(potion.getNode("show_particles").getBoolean());
			
			data.add(peb.build());
		}
		
		isb.itemData(data);
	}
	
	private static void getColoredItemData(ItemStackBuilder isb, ConfigurationNode meta)
	{
		ColoredData data = isb.build().getData(CatalogItemData.COLORED_ITEM_DATA).get();
		ConfigurationNode color = meta.getNode("color");
		int red = color.getNode("red").getInt();
		int green = color.getNode("green").getInt();
		int blue = color.getNode("blue").getInt();
		data.setColor(new Color(red, green, blue));
		isb.itemData(data);
	}
	
	private static void getDisplayNameData(ItemStackBuilder isb, ConfigurationNode meta)
	{
		DisplayNameData data = isb.build().getData(CatalogItemData.DISPLAY_NAME_DATA).get();
		data.setDisplayName(IBUtils.stringToText(meta.getNode("name").getString()));
		isb.itemData(data);
	}
	
	private static void getLoreData(ItemStackBuilder isb, ConfigurationNode meta)
	{
		LoreData data = isb.build().getData(CatalogItemData.LORE_DATA).get();
		for (Object line : (List<?>) meta.getNode("lore").getValue())
			data.add(IBUtils.stringToText(line.toString()));
		
		isb.itemData(data);
	}
	
	private static void getCloneableData(ItemStackBuilder isb, ConfigurationNode meta)
	{
		CloneableData data = isb.build().getData(CatalogItemData.CLONEABLE_DATA).get();
		data.setGeneration(meta.getNode("times_copied").getInt());
		isb.itemData(data);
	}
	
	private static void getPagedData(ItemStackBuilder isb, ConfigurationNode meta)
	{
		PagedData data = isb.build().getData(CatalogItemData.PAGED_DATA).get();
		for (Object page : (List<?>) meta.getNode("pages").getValue())
			data.add(IBUtils.stringToText(page.toString()));
		
		isb.itemData(data);
	}
	
	private static void getOwnableData(ItemStackBuilder isb, ConfigurationNode meta)
	{
		ConfigurationNode owner = meta.getNode("owner");
		OwnableData data = isb.build().getData(CatalogTileEntityData.OWNABLE_DATA).get();
		data.setProfile(ItemBank.getGame().getRegistry().createGameProfile(UUID.fromString(owner.getNode("uuid").getString()), owner.getNode("name").getString()));
		isb.itemData(data);
	}
	
	private static void getFireworkData(ItemStackBuilder isb, ConfigurationNode meta)
	{
		ConfigurationNode firework = meta.getNode("firework");
		FireworkData data = isb.build().getData(CatalogItemData.FIREWORK_DATA).get();
		data.setFlightModifier(firework.getNode("flight_modifier").getInt());
		for (ConfigurationNode effect : firework.getNode("effects").getChildrenList())
		{
			FireworkEffectBuilder feb = ItemBank.getGame().getRegistry().getFireworkEffectBuilder();
			feb.flicker(effect.getNode("flicker").getBoolean());
			feb.flicker(effect.getNode("trail").getBoolean());
			for (FireworkShape shape : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.FIREWORK_SHAPE))
				if (shape.getName().equalsIgnoreCase(effect.getNode("shape").getString()))
					feb.shape(shape);
			
			for (ConfigurationNode color : effect.getNode("colors").getChildrenList())
			{
				int red = color.getNode("red").getInt();
				int blue = color.getNode("blue").getInt();
				int green = color.getNode("green").getInt();
				feb.color(new Color(red, blue, green));
			}
			
			for (ConfigurationNode color : effect.getNode("fade_colors").getChildrenList())
			{
				int red = color.getNode("red").getInt();
				int blue = color.getNode("blue").getInt();
				int green = color.getNode("green").getInt();
				feb.fade(new Color(red, blue, green));
			}
			
			data.add(feb.build());
		}
		
		isb.itemData(data);
	}
	
	private static void getBlockItemData(ItemStackBuilder isb, ConfigurationNode meta)
	{
		/* Command Stats for command blocks and signs are not supported in the API so it's likely updated in the same manner as repair cost */
		ConfigurationNode tileEntity = meta.getNode("tile_entity");
		BlockItemData data = isb.build().getData(CatalogItemData.BLOCK_ITEM_DATA).get();
		BlockStateBuilder bsb = ItemBank.getGame().getRegistry().getBuilderOf(BlockStateBuilder.class).get();
		if (bsb.build().getManipulator(CatalogTileEntityData.BANNER_DATA).isPresent())
			getBannerData(bsb, tileEntity);
		
		if (bsb.build().getManipulator(CatalogTileEntityData.LOCKABLE_DATA).isPresent())
			getLockableData(bsb, tileEntity);
		
		if (bsb.build().getManipulator(CatalogTileEntityData.BEACON_DATA).isPresent())
			getBeaconData(bsb, tileEntity);
		
		if (bsb.build().getManipulator(CatalogTileEntityData.BREWING_DATA).isPresent())
			getBrewingData(bsb, tileEntity);
		
		if (bsb.build().getManipulator(CatalogItemData.INVENTORY_ITEM_DATA).isPresent())
			getInventoryItemData(bsb, tileEntity);
		
		if (bsb.build().getManipulator(CatalogTileEntityData.COMPARISON_DATA).isPresent())
			getComparisonData(bsb, tileEntity);
		
		if (bsb.build().getManipulator(CatalogTileEntityData.COMMAND_DATA).isPresent())
			getCommandData(bsb, tileEntity);
		
		data.setState(bsb.build());
	}
	
	private static void getBannerData(BlockStateBuilder bsb, ConfigurationNode tileEntity)
	{
		BannerData data = bsb.build().getManipulator(CatalogTileEntityData.BANNER_DATA).get();
		if (!tileEntity.getNode("base_color").isVirtual())
			for (DyeColor color : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.DYE_COLOR))
				if (tileEntity.getNode("base_color").getString().equalsIgnoreCase(color.getName()))
					data.setBaseColor(color);
		
		if (!tileEntity.getNode("patterns").isVirtual())
		{
			for (ConfigurationNode patternNode : tileEntity.getNode("patterns").getChildrenList())
			{
				BannerPatternShape shape = null;
				for (BannerPatternShape bps : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.BANNER_PATTERN_SHAPE))
					if (patternNode.getNode("shape").getString().equalsIgnoreCase(bps.getName()))
						shape = bps;
				
				DyeColor color = null;
				for (DyeColor dc : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.DYE_COLOR))
					if (patternNode.getNode("color").getString().equalsIgnoreCase(dc.getName()))
						color = dc;
				
				data.addPatternLayer(shape, color);
			}
		}
		
		bsb.add(data);
	}
	
	private static void getLockableData(BlockStateBuilder bsb, ConfigurationNode tileEntity)
	{
		LockableData data = bsb.build().getManipulator(CatalogTileEntityData.LOCKABLE_DATA).get();
		bsb.add(data.setLockToken(tileEntity.getNode("lock").getString("")));
	}
	
	private static void getBeaconData(BlockStateBuilder bsb, ConfigurationNode tileEntity)
	{
		BeaconData data = bsb.build().getManipulator(CatalogTileEntityData.BEACON_DATA).get();
		for (PotionEffectType type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.POTION_EFFECT_TYPE))
			if (type.getName().equalsIgnoreCase(tileEntity.getNode("primary_effect").getString()))
				data.setPrimaryEffect(type);
		
		for (PotionEffectType type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.POTION_EFFECT_TYPE))
			if (type.getName().equalsIgnoreCase(tileEntity.getNode("secondary_effect").getString()))
				data.setSecondaryEffect(type);
		
		bsb.add(data);
	}
	
	private static void getBrewingData(BlockStateBuilder bsb, ConfigurationNode tileEntity)
	{
		BrewingData data = bsb.build().getManipulator(CatalogTileEntityData.BREWING_DATA).get();
		bsb.add(data.setRemainingBrewTime(tileEntity.getNode("brew_time").getInt(10)));
	}
	
	private static void getInventoryItemData(BlockStateBuilder bsb, ConfigurationNode tileEntity)
	{
		InventoryItemData data = bsb.build().getManipulator(CatalogItemData.INVENTORY_ITEM_DATA).get();
		for (ConfigurationNode item : tileEntity.getNode("inventory").getChildrenList())
			data.getInventory().set(getItem(item));
		
		bsb.add(data);
	}
	
	private static void getComparisonData(BlockStateBuilder bsb, ConfigurationNode tileEntity)
	{
		ComparisonData data = bsb.build().getManipulator(CatalogTileEntityData.COMPARISON_DATA).get();
		for (Comparison comparison : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.COMPARISON_TYPE))
			if (comparison.getName().equalsIgnoreCase(tileEntity.getNode("comparison").getString()))
				data.setValue(comparison);
		
		bsb.add(data);
	}
	
	private static void getCommandData(BlockStateBuilder bsb, ConfigurationNode tileEntity)
	{
		CommandData data = bsb.build().getManipulator(CatalogTileEntityData.COMMAND_DATA).get();
		data.setStoredCommand(tileEntity.getNode("command").getString(""));
		data.setLastOutput(IBUtils.stringToText(tileEntity.getNode("last_output").getString("")));
		data.setSuccessCount(tileEntity.getNode("success_count").getInt(0));
		data.shouldTrackOutput(tileEntity.getNode("track_output").getBoolean(false));
		bsb.add(data);
	}
	
	private static void getFurnaceData(BlockStateBuilder bsb, ConfigurationNode tileEntity)
	{
		FurnaceData data = bsb.build().getManipulator(CatalogTileEntityData.FURNACE_DATA).get();
		data.setRemainingBurnTime(tileEntity.getNode("burn_time").getInt(10));
		data.setRemainingCookTime(tileEntity.getNode("cook_time").getInt(10));
		bsb.add(data);
	}
	
	private static void getSpawnerData(BlockStateBuilder bsb, ConfigurationNode tileEntity)
	{
		MobSpawnerData data = bsb.build().getManipulator(CatalogTileEntityData.MOB_SPAWNER_DATA).get();
		data.setRemainingDelay((short) tileEntity.getNode("remaining_delay").getInt(10));
		data.setMinimumSpawnDelay((short) tileEntity.getNode("minimum_delay").getInt(10));
		data.setMaximumSpawnDelay((short) tileEntity.getNode("maximum_delay").getInt(20));
		data.setSpawnCount((short) tileEntity.getNode("spawn_count").getInt(1));
		data.setRequiredPlayerRange((short) tileEntity.getNode("player_range").getInt(10));
		data.setSpawnRange((short) tileEntity.getNode("spawn_range").getInt(1));
		
		ConfigurationNode entityNode = tileEntity.getNode("entity");
		EntityType entityType = null;
		for (EntityType type : ItemBank.getGame().getRegistry().getAllOf(CatalogTypes.ENTITY_TYPE))
			if (type.getName().equalsIgnoreCase(entityNode.getNode("type").getString()))
				entityType = type;
		//TODO left off here
		WeightedEntity entity = new WeightedEntity(entityType, 10, new ArrayList<DataManipulator<?>>().toArray(new DataManipulator<?>[0]));
		data.setNextEntityToSpawn(null);
		bsb.add(data);
	}
	
	private static String getVariation(ItemStack item)
	{
		if (item.getData(CatalogBlockData.STONE_DATA).isPresent())
			return item.getData(CatalogBlockData.STONE_DATA).get().getValue().getName();
		else if (item.getData(CatalogBlockData.DIRT_DATA).isPresent())
			return item.getData(CatalogBlockData.DIRT_DATA).get().getValue().getName();
		else if (item.getData(CatalogBlockData.TREE_DATA).isPresent())
			return item.getData(CatalogBlockData.DIRT_DATA).get().getValue().getName();
		else if (item.getData(CatalogBlockData.SAND_DATA).isPresent())
			return item.getData(CatalogBlockData.SAND_DATA).get().getValue().getName();
		else if (item.getData(CatalogBlockData.SANDSTONE_DATA).isPresent())
			return item.getData(CatalogBlockData.SANDSTONE_DATA).get().getValue().getName();
		else if (item.getData(CatalogBlockData.SHRUB_DATA).isPresent())
			return item.getData(CatalogBlockData.SHRUB_DATA).get().getValue().getName();
		else if (item.getData(CatalogBlockData.DYEABLE_DATA).isPresent())
			return item.getData(CatalogBlockData.DYEABLE_DATA).get().getValue().getName();
		else if (item.getData(CatalogBlockData.SLAB_DATA).isPresent())
			return item.getData(CatalogBlockData.SLAB_DATA).get().getValue().getName();
		else if (item.getData(CatalogBlockData.DISGUISED_BLOCK_DATA).isPresent())
			return item.getData(CatalogBlockData.DISGUISED_BLOCK_DATA).get().getValue().getName();
		else if (item.getData(CatalogBlockData.BRICK_DATA).isPresent())
			return item.getData(CatalogBlockData.BRICK_DATA).get().getValue().getName();
		else if (item.getData(CatalogBlockData.WALL_DATA).isPresent())
			return item.getData(CatalogBlockData.WALL_DATA).get().getValue().getName();
		else if (item.getData(CatalogBlockData.QUARTZ_DATA).isPresent())
			return item.getData(CatalogBlockData.QUARTZ_DATA).get().getValue().getName();
		else if (item.getData(CatalogBlockData.PRISMARINE_DATA).isPresent())
			return item.getData(CatalogBlockData.PRISMARINE_DATA).get().getValue().getName();
		else if (item.getData(CatalogBlockData.DOUBLE_PLANT_DATA).isPresent())
			return item.getData(CatalogBlockData.DOUBLE_PLANT_DATA).get().getValue().getName();
		else if (item.getData(CatalogItemData.COAL_ITEM_DATA).isPresent())
			return item.getData(CatalogItemData.COAL_ITEM_DATA).get().getValue().getName();
		else if (item.getData(CatalogItemData.GOLDEN_APPLE_ITEM_DATA).isPresent())
			return item.getData(CatalogItemData.GOLDEN_APPLE_ITEM_DATA).get().getValue().getName();
		else if (item.getData(CatalogItemData.FISH_DATA).isPresent())
			return item.getData(CatalogItemData.FISH_DATA).get().getValue().getName();
		else if (item.getData(CatalogItemData.COOKED_FISH_ITEM_DATA).isPresent())
			return item.getData(CatalogItemData.COOKED_FISH_ITEM_DATA).get().getValue().getName();
		else if (item.getData(CatalogItemData.SPAWNABLE_DATA).isPresent())
			return item.getData(CatalogItemData.SPAWNABLE_DATA).get().getValue().getName();
		
		return null;
	}
	
	private static String metaToJson(ItemStack item)
	{
		SpongeJSONConfig meta = new SpongeJSONConfig();
		if (item.getData(CatalogItemData.DURABILITY_DATA).isPresent())
		{
			SpongeJSONConfig durability = new SpongeJSONConfig();
			DurabilityData data = item.getData(CatalogItemData.DURABILITY_DATA).get();
			durability.set("uses", data.getDurability());
			durability.set("breakable", data.isBreakable());
			meta.setSpongeJSONConfig("durability", durability);
		}
		
		if (item.getData(CatalogItemData.BREAKABLE_DATA).isPresent())
		{
			BreakableData data = item.getData(CatalogItemData.BREAKABLE_DATA).get();
			List<String> can_destroy = new ArrayList<String>();
			for (BlockType type : data.getAll())
				can_destroy.add(type.getName());
			
			meta.set("can_destroy", can_destroy);
		}
		
		if (item.getData(CatalogItemData.PLACEABLE_DATA).isPresent())
		{
			PlaceableData data = item.getData(CatalogItemData.PLACEABLE_DATA).get();
			List<String> can_place_on = new ArrayList<String>();
			for (BlockType type : data.getAll())
				can_place_on.add(type.getName());
			
			meta.set("can_place_on", can_place_on);
		}
		
		if (item.getData(CatalogItemData.ENCHANTMENT_DATA).isPresent())
		{
			EnchantmentData data = item.getData(CatalogItemData.ENCHANTMENT_DATA).get();
			meta.setEnchants("enchantments", data.asMap());
		}
		
		if (item.getData(CatalogItemData.STORED_ENCHANTMENT_DATA).isPresent())
		{
			StoredEnchantmentData data = item.getData(CatalogItemData.STORED_ENCHANTMENT_DATA).get();
			meta.setEnchants("stored_enchantments", data.asMap());
		}
		
		if (item.getData(CatalogItemData.ATTRIBUTE_DATA).isPresent())
		{
			List<SpongeJSONConfig> attributes = new ArrayList<SpongeJSONConfig>();
			AttributeData data = item.getData(CatalogItemData.ATTRIBUTE_DATA).get();
			for (AttributeModifier am : data.getAll())
			{
				SpongeJSONConfig attribute = new SpongeJSONConfig();
				attribute.set("operation", am.getOperation().getName());
				attribute.set("value", am.getValue());
			}
			
			meta.setSpongeJSONConfigList("attirbutes", attributes);
		}
		
		if (item.getData(CatalogItemData.POTION_EFFECT_DATA).isPresent())
		{
			PotionEffectData data = item.getData(CatalogItemData.POTION_EFFECT_DATA).get();
			meta.setPotionEffectList("potion_effects", data.getAll());
		}
		
		if (item.getData(CatalogItemData.COLORED_ITEM_DATA).isPresent())
		{
			ColoredData data = item.getData(CatalogItemData.COLORED_ITEM_DATA).get();
			meta.setColor("color", data.getValue());
		}
		
		if (item.getData(CatalogItemData.DISPLAY_NAME_DATA).isPresent())
		{
			DisplayNameData data = item.getData(CatalogItemData.DISPLAY_NAME_DATA).get();
			meta.set("name", data.getValue().toString());
		}
		
		if (item.getData(CatalogItemData.LORE_DATA).isPresent())
		{
			LoreData data = item.getData(CatalogItemData.LORE_DATA).get();
			List<String> lore = new ArrayList<String>();
			for (Text line : data.getAll())
				lore.add(line.toString());
			
			meta.set("lore", lore);
		}
		
		if (item.getData(CatalogItemData.CLONEABLE_DATA).isPresent())
		{
			CloneableData data = item.getData(CatalogItemData.CLONEABLE_DATA).get();
			meta.set("times_copied", data.getValue());
		}
		
		if (item.getData(CatalogItemData.PAGED_DATA).isPresent())
		{
			PagedData data = item.getData(CatalogItemData.PAGED_DATA).get();
			List<String> pages = new ArrayList<String>();
			for (Text page : data.getAll())
				pages.add(page.toString());
			
			meta.set("pages", pages);
		}
		
		if (item.getData(CatalogTileEntityData.OWNABLE_DATA).isPresent())
		{
			SpongeJSONConfig owner = new SpongeJSONConfig();
			OwnableData data = item.getData(CatalogTileEntityData.OWNABLE_DATA).get();
			owner.set("uuid", data.getValue().getUniqueId().toString());
			owner.set("name", data.getValue().getName());
			meta.setSpongeJSONConfig("owner", owner);
		}
		
		if (item.getData(CatalogItemData.FIREWORK_DATA).isPresent())
		{
			SpongeJSONConfig firework = new SpongeJSONConfig();
			FireworkData data = item.getData(CatalogItemData.FIREWORK_DATA).get();
			firework.set("flight_modifier", data.getFlightModifier());
			firework.setFireworkEffectList("effects", data.getAll());
			meta.setSpongeJSONConfig("firework", firework);
		}
		
		return meta.toJSONString();
	}
}

package musician101.sponge.itembank.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

import musician101.itembank.common.database.MySQLHandler;
import musician101.sponge.itembank.ItemBank;
import musician101.sponge.itembank.lib.Reference.Messages;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.SimpleConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;

import org.json.simple.parser.ParseException;
import org.spongepowered.api.data.ConfigurateTranslator;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.item.inventory.Inventories;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.custom.CustomInventoryBuilder;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.type.OrderedInventory;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.translation.Translatable;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;

public class AccountUtil
{
	public static Inventory getAccount(String worldName, UUID uuid, int page) throws ClassNotFoundException, FileNotFoundException, IOException, ObjectMappingException, ParseException, SQLException
	{
		CustomInventoryBuilder invBuilder = Inventories.customInventoryBuilder();
		invBuilder.size(54);
		invBuilder.name((Translatable) Texts.builder(IBUtils.getNameOf(uuid) + " - " + Messages.ACCOUNT_PAGE + page).build());
		
		OrderedInventory inv = invBuilder.build();
		if (ItemBank.getMySQLHandler() != null)
		{
			ItemBank.getMySQLHandler().querySQL("CREATE TABLE IF NOT EXISTS ib_" + uuid.toString().replace("-", "_") + "(World TEXT, Page int, Slot int, Item TEXT);");
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
		if (pageNode.isVirtual())
			return inv;
		
		for (int slot = 0; slot < inv.size(); slot++)
			if (!pageNode.getNode(slot + "").isVirtual())
				inv.set(new SlotIndex(slot), getItem(pageNode.getNode(slot + "")));
		
		return inv;
	}
	
	private static ItemStack getItem(ConfigurationNode item)
	{
		ItemStack is = ItemBank.getGame().getRegistry().getItemBuilder().build();
		is.setRawData((DataContainer) ConfigurateTranslator.instance().translateFrom(item));
		return is;
	}
	
	private static ItemStack getItem(ResultSet rs) throws ObjectMappingException, ParseException, SQLException
	{
		while (rs.next())
		{
			ConfigurationNode node = SimpleConfigurationNode.root();
			TypeToken<String> type = TypeToken.of(String.class);
			TypeSerializers.getSerializer(type).serialize(type, rs.getString("Item"), node);
			return getItem(node);
		}
		
		return null;
	}
	
	public static void saveAccount(String worldName, UUID uuid, OrderedInventory inventory, int page) throws ClassNotFoundException, FileNotFoundException, IOException, ObjectMappingException, ParseException, SQLException
	{
		if (ItemBank.getMySQLHandler() != null)
		{
			MySQLHandler sql = ItemBank.getMySQLHandler();
			sql.querySQL("CREATE TABLE IF NOT EXISTS ib_" + uuid.toString().replace("-", "_") + "(World TEXT, Page int, Slot int, Item TEXT);");
			for (int slot = 0; slot < inventory.size(); slot++)
			{
				sql.querySQL("DELETE FROM ib_" + uuid + " WHERE World = \"" + worldName + "\" AND Page = " + page + " AND Slot = " + slot + ";");
				Optional<ItemStack> opt = inventory.getSlot(new SlotIndex(slot)).get().peek();
				if (opt.isPresent())
					sql.updateSQL("INSERT INTO ib_" + uuid + "(World, Page, Slot, Item) VALUES (\"" + worldName + "\", " + page + ", " + slot + ", \"" + itemToString(opt.get()) + ");");
			}
			
			return;
		}
		
		File file = ItemBank.getConfig().getPlayerFile(uuid);
		if (!file.exists())
			IBUtils.createPlayerFile(file);
		
		ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setFile(file).build();
		ConfigurationNode account = loader.load();
		ConfigurationNode pageNode = account.getNode(worldName, page + "");
		Map<Integer, ConfigurationNode> items = Maps.newHashMap();
		for (int slot = 0; slot < inventory.size(); slot++)
		{
			Optional<ItemStack> opt = inventory.getSlot(new SlotIndex(slot)).get().peek();
			if (opt.isPresent())
				items.put(slot, itemToConfigurationNode(opt.get()));
		}
		
		pageNode.setValue(items);
		account.getNode(worldName).getNode(page + "").setValue(pageNode);
	}
	
	private static ConfigurationNode itemToConfigurationNode(ItemStack item)
	{
		return ConfigurateTranslator.instance().translateData(item.toContainer());
	}
	
	// Might not work
	// Alternative would be just create a file, write the string to it, load the info from there and then delete the file when done
	private static String itemToString(ItemStack item) throws ObjectMappingException
	{
		Class<String> string = String.class;
		return TypeSerializers.getSerializer(TypeToken.of(string)).deserialize(TypeToken.of(string), itemToConfigurationNode(item)).toString();
	}
}

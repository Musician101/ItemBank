package musician101.sponge.itembank.util;

import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import musician101.itembank.common.MySQLHandler;
import musician101.itembank.common.Reference.Messages;
import musician101.itembank.common.Reference.MySQL;
import musician101.sponge.itembank.SpongeItemBank;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.SimpleConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.json.simple.parser.ParseException;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.translator.ConfigurateTranslator;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.custom.CustomInventory;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.type.OrderedInventory;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.translation.Translation;
import org.spongepowered.api.text.translation.locale.Locales;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class AccountUtil
{
	public static OrderedInventory getAccount(World world, UUID uuid, int page) throws ClassNotFoundException, IOException, ObjectMappingException, ParseException, SQLException
	{
		CustomInventory.Builder builder = CustomInventory.builder();
		builder.size(54);
		builder.name(Text.builder(Text.of(Messages.page(uuid, page)), new Translation()
        {
            @Nonnull
            @Override
            public String getId()
            {
                return Locales.EN_US.toLanguageTag();
            }

            @Nonnull
            @Override
            public String get(@Nonnull Locale locale)
            {
                return locale.toLanguageTag();
            }

            @Nonnull
            @Override
            public String get(@Nonnull Locale locale, @Nonnull Object... args)
            {
                return locale.toLanguageTag();
            }
        }).build().getTranslation());
		OrderedInventory inv = builder.build();
		if (SpongeItemBank.mysql != null)
		{
			SpongeItemBank.mysql.querySQL("CREATE TABLE IF NOT EXISTS ib_" + uuid.toString().replace("-", "_") + "(World TEXT, Page int, Slot int, Item TEXT);");
			for (int slot = 0; slot < inv.size(); slot++)
				inv.set(new SlotIndex(slot), getItem(SpongeItemBank.mysql.querySQL("SELECT * FROM ib_" + uuid + " WHERE World = \"" + world + "\" AND Page = " + page + " AND Slot = " + slot + ";")));
			
			return inv;
		}
		
		File file = SpongeItemBank.config.getPlayerFile(uuid);
		if (!file.exists())
			IBUtils.createPlayerFile(file);
		
		ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setFile(file).build();
		ConfigurationNode account = loader.load();
		ConfigurationNode pageNode = account.getNode(world.getName(), page + "");
		if (pageNode.isVirtual())
			return inv;
		
		for (int slot = 0; slot < inv.size(); slot++)
			if (!pageNode.getNode(slot + "").isVirtual())
				inv.set(new SlotIndex(slot), getItem(pageNode.getNode(slot + "")));
		
		return inv;
	}
	
	private static ItemStack getItem(ConfigurationNode item)
	{
		ItemStack is = ItemStack.of(ItemTypes.ITEM_FRAME, 1);
		is.setRawData((DataContainer) ConfigurateTranslator.instance().translateFrom(item));
		return is;
	}
	
	private static ItemStack getItem(ResultSet rs) throws ObjectMappingException, SQLException
	{
        ConfigurationNode node = SimpleConfigurationNode.root();
        TypeToken<String> type = TypeToken.of(String.class);
        TypeSerializers.getDefaultSerializers().get(type).serialize(type, rs.getString("Item"), node);
        return getItem(node);
	}
	
	public static void saveAccount(String worldName, UUID uuid, OrderedInventory inventory, int page) throws ClassNotFoundException, IOException, ObjectMappingException, SQLException
	{
		if (SpongeItemBank.mysql != null)
		{
			MySQLHandler sql = SpongeItemBank.mysql;
			sql.querySQL(MySQL.getTable(uuid));
			for (int slot = 0; slot < inventory.size(); slot++)
			{
				sql.querySQL(MySQL.deleteItem(uuid, worldName, page, slot));
				Optional<ItemStack> opt = inventory.getSlot(new SlotIndex(slot)).get().peek();
				if (opt.isPresent())
					sql.updateSQL("INSERT INTO ib_" + uuid + "(World, Page, Slot, Item) VALUES (\"" + worldName + "\", " + page + ", " + slot + ", \"" + itemToString(opt.get()) + ");");
			}
			
			return;
		}
		
		File file = SpongeItemBank.config.getPlayerFile(uuid);
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
		TypeToken<String> string = TypeToken.of(String.class);
		return TypeSerializers.getDefaultSerializers().get(string).deserialize(string, itemToConfigurationNode(item));
	}
}

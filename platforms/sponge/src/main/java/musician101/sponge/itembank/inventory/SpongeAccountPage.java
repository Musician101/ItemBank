package musician101.sponge.itembank.inventory;

import com.google.common.reflect.TypeToken;
import musician101.common.java.minecraft.sponge.TextUtils;
import musician101.common.java.minecraft.uuid.UUIDUtils;
import musician101.itembank.common.MySQLHandler;
import musician101.itembank.common.Reference;
import musician101.itembank.common.Reference.Messages;
import musician101.itembank.common.Reference.MySQL;
import musician101.itembank.common.Reference.Permissions;
import musician101.itembank.common.account.AbstractAccountPage;
import musician101.sponge.itembank.SpongeItemBank;
import musician101.sponge.itembank.config.SpongeConfig;
import musician101.sponge.itembank.util.IBUtils;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.SimpleConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.catalog.CatalogEntityData;
import org.spongepowered.api.data.manipulator.mutable.RepresentedItemData;
import org.spongepowered.api.data.translator.ConfigurateTranslator;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.custom.CustomInventory;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.type.OrderedInventory;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.translation.Translation;
import org.spongepowered.api.text.translation.locale.Locales;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class SpongeAccountPage extends AbstractAccountPage<InteractInventoryEvent.Close, OrderedInventory, ItemStack, Player, ConfigurationNode, World>
{
    private SpongeAccountPage(UUID owner, World world, int page)
    {
        super(owner, world, page);
    }

    @Override
    public boolean openInv(Player viewer)
    {
        this.viewer = viewer;
        OrderedInventory inv;
        try
        {
            inv = getAccount();
        }
        catch (IOException | ObjectMappingException e)
        {
            viewer.sendMessage(TextUtils.redText(Messages.fileLoadFail(SpongeItemBank.accountStorage.getFile(owner))));
            return false;
        }
        catch (ClassNotFoundException | SQLException e)
        {
            viewer.sendMessage(TextUtils.redText(Messages.SQL_EX));
            return false;
        }

        if (viewer.getUniqueId() == owner && SpongeItemBank.config.useEconomy())
        {
            Optional<EconomyService> economyOptional = Sponge.getServiceManager().provide(EconomyService.class);
            if (economyOptional.isPresent())
            {
                EconomyService economy = economyOptional.get();
                Optional<UniqueAccount> accountOptional = economy.getAccount(viewer.getUniqueId());
                if (accountOptional.isPresent())
                {
                    UniqueAccount account = accountOptional.get();
                    BigDecimal requiredAmount = BigDecimal.valueOf(SpongeItemBank.config.getTransactionCost());
                    TransactionResult result = account.withdraw(economy.getDefaultCurrency(), requiredAmount, Cause.of(NamedCause.source(Sponge.getPluginManager().getPlugin(Reference.ID)), NamedCause.simulated(viewer)));
                    if (result.getResult() == ResultType.SUCCESS)
                        viewer.sendMessage(TextUtils.redText(Messages.accountWithdrawSuccess(result.getCurrency().getSymbol().toPlain(), result.getAmount().doubleValue())));
                    else if (result.getResult() == ResultType.ACCOUNT_NO_FUNDS)
                    {
                        viewer.sendMessage(TextUtils.greenText(Messages.ACCOUNT_ECON_WITHDRAW_FAIL));
                        return false;
                    }
                    else
                        viewer.sendMessage(TextUtils.redText(Messages.ACCOUNT_ECON_UNKNOWN_FAIL));
                }
                else
                {
                    viewer.sendMessage(TextUtils.redText(Messages.ACCOUNT_ECON_GET_ACCOUNT_FAIL));
                    return false;
                }
            }
            else
            {
                viewer.sendMessage(TextUtils.redText(Messages.ACCOUNT_ECON_NOT_AVAILABLE));
            }
        }

        Sponge.getEventManager().registerListeners(Sponge.getGame().getPluginManager().getPlugin(Reference.ID), this);
        viewer.openInventory(inv);
        return true;
    }

    @Listener
    @Override
    public void onInventoryClose(InteractInventoryEvent.Close event)
    {
        Optional<Player> playerOptional = event.getCause().first(Player.class);
        if (!playerOptional.isPresent())
        {
            unregisterListener();
            return;
        }

        Player player = playerOptional.get();
        if (player.getUniqueId() != viewer.getUniqueId())
        {
            unregisterListener();
            return;
        }

        SpongeConfig config = SpongeItemBank.config;
        OrderedInventory inv = (OrderedInventory) event.getTargetInventory();
        int pageLimit = config.getPageLimit();
        if (((pageLimit > 0 && pageLimit < page) || page == 0) && !player.hasPermission(Permissions.ADMIN))
        {
            returnInv(inv, Messages.ACCOUNT_ILLEGAL_PAGE);
            return;
        }

        boolean hasIllegalItems = false;
        boolean hasIllegalAmount = false;
        for (int x = 0; x < inv.size(); x++)
        {
            Optional<Slot> slotOptional = inv.getSlot(new SlotIndex(x));
            if (slotOptional.isPresent())
            {
                Optional<ItemStack> itemStackOptional = slotOptional.get().peek();
                if (itemStackOptional.isPresent())
                {
                    ItemStack itemStack = itemStackOptional.get();
                    int itemAmount = 0;
                    for (Inventory slot : inv.query(itemStack.getItem()))
                    {
                        ItemStack inventoryItemStack = slot.peek().get();
                        if (itemStack.getItem() == inventoryItemStack.getItem() && IBUtils.isSameVariant(itemStack, inventoryItemStack))
                            itemAmount += inventoryItemStack.getQuantity();

                    }

                    if (config.getItem(itemStack) != null && !config.isWhitelist())
                    {
                        int maxAmount = config.getItem(itemStack).getQuantity();
                        if (maxAmount == 0)
                        {
                            dropItemOnPlayer(itemStack);
                            hasIllegalItems = true;
                        }
                        else if (maxAmount < itemAmount)
                        {
                            int amount = itemAmount;
                            while (maxAmount < amount)
                            {
                                int maxStackSize = itemStack.getMaxStackQuantity();
                                if (maxStackSize < amount)
                                {
                                    dropItemOnPlayer(itemStack);
                                    inv.getSlot(new SlotIndex(x)).get().clear();
                                    amount -= maxStackSize;
                                }
                                else
                                {
                                    ItemStack removeItem = itemStack.copy();
                                    removeItem.setQuantity(amount - maxAmount);
                                    if (!inv.getSlot(new SlotIndex(x)).isPresent())
                                    {
                                        int slot = 0;
                                        for (int y = 0; y < inv.size(); y++)
                                        {
                                            Optional<ItemStack> itemOptional = inv.getSlot(new SlotIndex(y)).get().peek();
                                            if (itemOptional.isPresent())
                                                if (IBUtils.isSameVariant(itemOptional.get(), itemStack))
                                                    slot = y;
                                        }

                                        ItemStack is = inv.getSlot(new SlotIndex(slot)).get().peek().get();
                                        is.setQuantity(itemStack.getQuantity() - removeItem.getQuantity());
                                        inv.set(new SlotIndex(x), is);
                                    }
                                    else
                                        inv.getSlot(new SlotIndex(x)).get().peek().get().setQuantity(itemStack.getQuantity() - removeItem.getQuantity());

                                    dropItemOnPlayer(removeItem);
                                    amount -= removeItem.getQuantity();
                                }
                            }

                            hasIllegalAmount = true;
                        }
                    }
                    else if (config.getItem(itemStack) == null && config.isWhitelist())
                    {
                        dropItemOnPlayer(itemStack);
                        inv.getSlot(new SlotIndex(x)).get().clear();
                        hasIllegalItems = true;
                    }

                    if (hasIllegalItems)
                        player.sendMessage(TextUtils.redText(Messages.ACCOUNT_ILLEGAL_ITEM));

                    if (hasIllegalAmount)
                        player.sendMessage(TextUtils.redText(Messages.ACCOUNT_ILLEGAL_AMOUNT));

                    saveAccount(inv, (OrderedInventory) player.getInventory());
                }
            }
        }
    }

    private void dropItemOnPlayer(ItemStack itemStack)
    {
        Item itemEntity = (Item) viewer.getWorld().createEntity(EntityTypes.ITEM, viewer.getLocation().getPosition()).get();
        RepresentedItemData data = itemEntity.get(CatalogEntityData.REPRESENTED_ITEM_DATA).get();
        data.set(Keys.REPRESENTED_ITEM, itemStack.createSnapshot());
        itemEntity.setRawData(data.toContainer());
        world.spawnEntity(itemEntity, Cause.of(NamedCause.source(Sponge.getPluginManager().getPlugin(Reference.ID)), NamedCause.simulated(viewer)));
    }

    @Override
    protected OrderedInventory getAccount() throws ClassNotFoundException, IOException, ObjectMappingException, SQLException
    {
        CustomInventory.Builder builder = CustomInventory.builder();
        builder.size(54);
        builder.name(Text.builder(Text.of(Messages.page(UUIDUtils.getNameOf(owner), page)), new Translation()
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
            MySQLHandler mysql = SpongeItemBank.mysql;
            mysql.querySQL(MySQL.createTable(owner));
            for (int slot = 0; slot < inv.size(); slot++)
                inv.set(new SlotIndex(slot), getItem(mysql.querySQL(MySQL.getTable(owner, world.getName(), page, slot))));

            return inv;
        }

        File file = SpongeItemBank.accountStorage.getFile(owner);
        if (!file.exists())
            throw new IOException("Could not create file.");

        ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setFile(file).build();
        ConfigurationNode account = loader.load();
        ConfigurationNode pageNode = account.getNode(world.getName(), page + "");
        if (pageNode.isVirtual())
            return inv;

        for (int slot = 0; slot < inv.size(); slot++)
            if (!pageNode.getNode(slot + "").isVirtual())
                inv.set(new SlotIndex(slot), deserializeItem(pageNode.getNode(slot + "")));

        return inv;
    }

    @Override
    protected ItemStack getItem(ResultSet rs) throws ObjectMappingException, SQLException
    {
        ConfigurationNode node = SimpleConfigurationNode.root();
        TypeToken<String> type = TypeToken.of(String.class);
        TypeSerializers.getDefaultSerializers().get(type).serialize(type, rs.getString("Item"), node);
        return deserializeItem(node);
    }

    @Override
    protected ItemStack deserializeItem(ConfigurationNode node)
    {
        return ItemStack.builder().fromContainer(ConfigurateTranslator.instance().translateFrom(node)).build();
    }

    @Override
    protected String serializeItem(ItemStack itemStack) throws ObjectMappingException
    {
        TypeToken<String> token = TypeToken.of(String.class);
        String string = "";
        TypeSerializers.getDefaultSerializers().get(token).serialize(token, string, ConfigurateTranslator.instance().translateData(itemStack.toContainer()));
        return string;
    }

    @Override
    protected void saveAccount(OrderedInventory topInv, OrderedInventory playerInv)
    {
        OrderedInventory account;
        try
        {
            account = getAccount();
        }
        catch (ClassNotFoundException | IOException | ObjectMappingException e)
        {
            returnInv(playerInv, Messages.fileLoadFail(SpongeItemBank.accountStorage.getFile(owner)));
            return;
        }
        catch (SQLException e)
        {
            returnInv(playerInv, Messages.SQL_EX);
            return;
        }

        transferInv(account, topInv);
        try
        {
            if (SpongeItemBank.mysql != null)
            {
                MySQLHandler sql = SpongeItemBank.mysql;
                sql.querySQL(MySQL.createTable(owner));
                for (int slot = 0; slot < topInv.size(); slot++)
                {
                    sql.querySQL(MySQL.deleteItem(owner, world.getName(), page, slot));
                    Optional<ItemStack> opt = topInv.getSlot(new SlotIndex(slot)).get().peek();
                    if (opt.isPresent())
                        sql.updateSQL(MySQL.addItem(owner, world.getName(), page, slot, serializeItem(opt.get())));
                }

                unregisterListener();
                return;
            }

            File file = SpongeItemBank.accountStorage.getFile(owner);
            if (file == null)
            {
                viewer.sendMessage(TextUtils.redText(Messages.fileLoadFail(SpongeItemBank.accountStorage.getFile(owner))));
                unregisterListener();
                return;
            }

            ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setFile(file).build();
            ConfigurationNode accountNode = loader.load();
            ConfigurationNode pageNode = accountNode.getNode(world.getName(), page + "");
            Map<Integer, ConfigurationNode> items = new HashMap<>();
            for (int slot = 0; slot < account.size(); slot++)
            {
                Optional<ItemStack> opt = account.getSlot(new SlotIndex(slot)).get().peek();
                if (opt.isPresent())
                    items.put(slot, ConfigurateTranslator.instance().translateData(opt.get().toContainer()));
            }

            pageNode.setValue(items);
            accountNode.getNode(world.getName(), page + "").setValue(pageNode);
        }
        catch (ClassNotFoundException | IOException | ObjectMappingException e)
        {
            returnInv(playerInv, Messages.fileLoadFail(SpongeItemBank.accountStorage.getFile(owner)));
            return;
        }
        catch (SQLException e)
        {
            returnInv(playerInv, Messages.SQL_EX);
            return;
        }

        viewer.sendMessage(TextUtils.greenText(Messages.ACCOUNT_UPDATED));
    }

    private void transferInv(OrderedInventory transferTo, OrderedInventory transferFrom)
    {
        for (int x = 0; x < transferFrom.size(); x++)
        {
            Optional<ItemStack> itemStack = transferFrom.getSlot(new SlotIndex(x)).get().peek();
            if (itemStack.isPresent())
                transferTo.set(new SlotIndex(x), itemStack.get());
        }
    }

    @Override
    protected void returnInv(OrderedInventory inventory, String message)
    {
        viewer.sendMessage(TextUtils.redText(message));
        for (Inventory slot : inventory.slots())
        {
            Optional<ItemStack> itemStackOptional = slot.peek();
            if (itemStackOptional.isPresent())
                dropItemOnPlayer(itemStackOptional.get());
        }

        unregisterListener();
    }

    @Override
    protected void unregisterListener()
    {
        Sponge.getEventManager().unregisterListeners(this);
    }

    public static SpongeAccountPage createNewPage(UUID owner, World world, int page)
    {
        return new SpongeAccountPage(owner, world, page);
    }
}

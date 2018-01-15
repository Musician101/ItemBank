package io.musician101.itembank.sponge.account;

import com.google.common.reflect.TypeToken;
import io.musician101.itembank.common.Reference;
import io.musician101.itembank.common.Reference.Messages;
import io.musician101.itembank.common.Reference.MySQL;
import io.musician101.itembank.common.Reference.Permissions;
import io.musician101.itembank.common.account.AbstractAccountPage;
import io.musician101.itembank.sponge.IBUtils;
import io.musician101.itembank.sponge.SpongeItemBank;
import io.musician101.itembank.sponge.config.SpongeConfig;
import io.musician101.musicianlibrary.java.MySQLHandler;
import io.musician101.musicianlibrary.java.minecraft.uuid.UUIDUtils;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.SimpleConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.persistence.DataTranslators;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryCapacity;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

public class SpongeAccountPage extends AbstractAccountPage<InteractInventoryEvent.Close, ClientConnectionEvent.Disconnect, Inventory, ItemStack, Player, ConfigurationNode, World> {

    private SpongeAccountPage(UUID owner, World world, int page) {
        super(owner, world, page);
    }

    @SuppressWarnings("WeakerAccess")
    public static SpongeAccountPage createNewPage(UUID owner, World world, int page) {
        return new SpongeAccountPage(owner, world, page);
    }

    @Override
    protected ItemStack deserializeItem(ConfigurationNode node) {
        return ItemStack.builder().fromContainer(DataTranslators.CONFIGURATION_NODE.translate(node)).build();
    }

    private void dropItemOnPlayer(ItemStack itemStack) {
        Item itemEntity = (Item) viewer.getWorld().createEntity(EntityTypes.ITEM, viewer.getLocation().getPosition());
        itemEntity.offer(Keys.REPRESENTED_ITEM, itemStack.createSnapshot());
        world.spawnEntity(itemEntity);
    }

    @Nullable
    @Override
    protected Inventory getAccount() throws ClassNotFoundException, IOException, ObjectMappingException, SQLException {
        Optional<SpongeItemBank> optional = SpongeItemBank.instance();
        if (!optional.isPresent()) {
            return null;
        }

        SpongeItemBank plugin = optional.get();
        String name = Messages.page(UUIDUtils.getNameOf(owner), page);
        InventoryArchetype.Builder builder = InventoryArchetype.builder().property(new InventoryCapacity(54)).title(Text.of(name));
        for (int i = 0; i < 54; i++) {
            builder.with(InventoryArchetype.builder().from(InventoryArchetypes.SLOT).property(new SlotIndex(i)).build("minecraft:slot" + i, "Slot"));
        }

        Inventory inv = Inventory.builder().of(builder.build(plugin.getId() + ":" + name.replace("\\s", "_").toLowerCase(), name)).build(optional.get());
        MySQLHandler mysql = plugin.getMySQL();
        if (mysql != null) {
            mysql.querySQL(MySQL.createTable(owner));
            for (int slot = 0; slot < inv.size(); slot++) {
                inv.query(QueryOperationTypes.INVENTORY_PROPERTY.of(new SlotIndex(slot))).set(getItem(mysql.querySQL(MySQL.getTable(owner, world.getName(), page, slot))));
            }
        }
        else {

            File file = plugin.getAccountStorage().getFile(owner);
            if (!file.exists()) {
                throw new IOException(Messages.fileLoadFail(file));
            }

            ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setFile(file).build();
            ConfigurationNode account = loader.load();
            ConfigurationNode pageNode = account.getNode(world.getName(), Integer.toString(page));
            if (!pageNode.isVirtual()) {
                for (int slot = 0; slot < inv.size(); slot++) {
                    if (!pageNode.getNode(Integer.toString(slot)).isVirtual()) {
                        inv.query(QueryOperationTypes.INVENTORY_PROPERTY.of(new SlotIndex(slot))).set(deserializeItem(pageNode.getNode(Integer.toString(slot))));
                    }
                }
            }
        }

        return inv;
    }

    @Override
    protected ItemStack getItem(ResultSet rs) throws ObjectMappingException, SQLException {
        ConfigurationNode node = SimpleConfigurationNode.root();
        TypeToken<String> type = TypeToken.of(String.class);
        TypeSerializers.getDefaultSerializers().get(type).serialize(type, rs.getString("Item"), node);
        return deserializeItem(node);
    }

    @Listener
    @Override
    public void onInventoryClose(InteractInventoryEvent.Close event) {
        Optional<Player> playerOptional = event.getCause().first(Player.class);
        if (!playerOptional.isPresent()) {
            unregisterListener();
            return;
        }

        processEvent(playerOptional.get(), event.getTargetInventory());
    }

    @Listener
    @Override
    public void onPlayerQuit(ClientConnectionEvent.Disconnect event) {
        Player player = event.getTargetEntity();
        player.getOpenInventory().ifPresent(inventory -> processEvent(player, inventory));
    }

    @Override
    public boolean openInv(Player viewer) {
        return SpongeItemBank.instance().map(plugin -> {
            this.viewer = viewer;
            Inventory inv;
            try {
                inv = getAccount();
            }
            catch (IOException | ObjectMappingException e) {
                viewer.sendMessage(Text.builder(Messages.fileLoadFail(plugin.getAccountStorage().getFile(owner))).color(TextColors.RED).build());
                return false;
            }
            catch (ClassNotFoundException | SQLException e) {
                viewer.sendMessage(Text.builder(Messages.SQL_EX).color(TextColors.RED).build());
                return false;
            }

            SpongeConfig config = plugin.getConfig();
            if (viewer.getUniqueId() == owner && config.useEconomy()) {
                boolean econAvailable = Sponge.getServiceManager().provide(EconomyService.class).map(economy -> {
                    return economy.getOrCreateAccount(viewer.getUniqueId()).map(account -> {
                        BigDecimal requiredAmount = BigDecimal.valueOf(config.getTransactionCost());
                        TransactionResult result = account.withdraw(economy.getDefaultCurrency(), requiredAmount, Cause.of(EventContext.builder().add(EventContextKeys.PLUGIN, plugin.getPluginContainer()).add(EventContextKeys.PLAYER, viewer).build(), plugin));
                        if (result.getResult() == ResultType.SUCCESS) {
                            viewer.sendMessage(Text.builder(Messages.accountWithdrawSuccess(result.getCurrency().getSymbol().toPlain(), result.getAmount().doubleValue())).color(TextColors.GREEN).build());
                            return true;
                        }
                        else if (result.getResult() == ResultType.ACCOUNT_NO_FUNDS) {
                            viewer.sendMessage(Text.builder(Messages.ACCOUNT_ECON_WITHDRAW_FAIL).color(TextColors.RED).build());
                        }
                        else {
                            viewer.sendMessage(Text.builder(Messages.ACCOUNT_ECON_UNKNOWN_FAIL).color(TextColors.RED).build());
                        }

                        return false;
                    }).orElseGet(() -> {
                        viewer.sendMessage(Text.builder(Messages.ACCOUNT_ECON_GET_ACCOUNT_FAIL).color(TextColors.RED).build());
                        return false;
                    });
                }).orElse(false);

                if (!econAvailable) {
                    viewer.sendMessage(Text.builder(Messages.ACCOUNT_ECON_NOT_AVAILABLE).color(TextColors.RED).build());
                }
            }

            Sponge.getEventManager().registerListeners(plugin, this);
            viewer.openInventory(inv);
            return true;
        }).orElseGet(() -> {
            viewer.sendMessage(Text.builder("The plugin is not initialized!").color(TextColors.RED).build());
            return false;
        });
    }

    @Override
    protected void processEvent(Player player, Inventory inv) {
        SpongeItemBank.instance().ifPresent(plugin -> {
            if (player.getUniqueId() != viewer.getUniqueId()) {
                unregisterListener();
                return;
            }

            SpongeConfig config = plugin.getConfig();
            int pageLimit = config.getPageLimit();
            if (((pageLimit > 0 && pageLimit < page) || page == 0) && !player.hasPermission(Permissions.ADMIN)) {
                returnInv(inv, Messages.ACCOUNT_ILLEGAL_PAGE);
                return;
            }

            boolean hasIllegalItems = false;
            boolean hasIllegalAmount = false;
            for (int x = 0; x < inv.size(); x++) {
                Optional<ItemStack> itemStackOptional = inv.query(QueryOperationTypes.INVENTORY_PROPERTY.of(new SlotIndex(x))).peek();
                if (itemStackOptional.isPresent()) {
                    ItemStack itemStack = itemStackOptional.get();
                    int itemAmount = 0;
                    for (Inventory slot : inv.query(QueryOperationTypes.ITEM_TYPE.of(itemStack.getType()))) {
                        //ItemStack should exist since we've queried based on ItemStack#getType().
                        ItemStack inventoryItemStack = slot.peek().get();
                        if (itemStack.getType() == inventoryItemStack.getType() && IBUtils.isSameVariant(itemStack, inventoryItemStack)) {
                            itemAmount += inventoryItemStack.getQuantity();
                        }

                    }

                    if (config.getItem(itemStack) != null && !config.isWhitelist()) {
                        int maxAmount = config.getItem(itemStack).getQuantity();
                        if (maxAmount == 0) {
                            dropItemOnPlayer(itemStack);
                            hasIllegalItems = true;
                        }
                        else if (maxAmount < itemAmount) {
                            int amount = itemAmount;
                            while (maxAmount < amount) {
                                int maxStackSize = itemStack.getMaxStackQuantity();
                                if (maxStackSize < amount) {
                                    dropItemOnPlayer(itemStack);
                                    inv.query(QueryOperationTypes.INVENTORY_PROPERTY.of(new SlotIndex(x))).clear();
                                    amount -= maxStackSize;
                                }
                                else {
                                    ItemStack removeItem = itemStack.copy();
                                    removeItem.setQuantity(amount - maxAmount);
                                    if (!inv.query(QueryOperationTypes.INVENTORY_PROPERTY.of(new SlotIndex(x))).peek().isPresent()) {
                                        int slot = 0;
                                        for (int y = 0; y < inv.size(); y++) {
                                            Optional<ItemStack> itemOptional = inv.query(QueryOperationTypes.INVENTORY_PROPERTY.of(new SlotIndex(y))).peek();
                                            if (itemOptional.isPresent()) {
                                                if (IBUtils.isSameVariant(itemOptional.get(), itemStack)) {
                                                    slot = y;
                                                }
                                            }
                                        }

                                        //We know the item exists
                                        ItemStack is = inv.query(QueryOperationTypes.INVENTORY_PROPERTY.of(new SlotIndex(slot))).peek().get();
                                        is.setQuantity(itemStack.getQuantity() - removeItem.getQuantity());
                                        inv.query(QueryOperationTypes.INVENTORY_PROPERTY.of(new SlotIndex(x))).set(is);
                                    }
                                    else {
                                        inv.query(QueryOperationTypes.INVENTORY_PROPERTY.of(new SlotIndex(x))).peek().get().setQuantity(itemStack.getQuantity() - removeItem.getQuantity());
                                    }

                                    dropItemOnPlayer(removeItem);
                                    amount -= removeItem.getQuantity();
                                }
                            }

                            hasIllegalAmount = true;
                        }
                    }
                    else if (config.getItem(itemStack) == null && config.isWhitelist()) {
                        dropItemOnPlayer(itemStack);
                        inv.query(QueryOperationTypes.INVENTORY_PROPERTY.of(new SlotIndex(x))).clear();
                        hasIllegalItems = true;
                    }

                    if (hasIllegalItems) {
                        player.sendMessage(Text.builder(Messages.ACCOUNT_ILLEGAL_ITEM).color(TextColors.RED).build());
                    }

                    if (hasIllegalAmount) {
                        player.sendMessage(Text.builder(Messages.ACCOUNT_ILLEGAL_AMOUNT).color(TextColors.RED).build());
                    }

                    saveAccount(inv, player.getInventory());
                }
            }
        });
    }

    @Override
    protected void returnInv(Inventory inventory, String message) {
        viewer.sendMessage(Text.builder(message).color(TextColors.RED).build());
        for (Inventory slot : inventory.slots()) {
            slot.peek().ifPresent(this::dropItemOnPlayer);
        }

        unregisterListener();
    }

    @Override
    protected void saveAccount(Inventory topInv, Inventory playerInv) {
        //TODO LEFT OFF HERE
        SpongeItemBank.instance().ifPresent(plugin -> {
            Inventory account;
            try {
                account = getAccount();
                if (account == null) {
                    viewer.sendMessage(Text.builder("Plugin not initialized!").color(TextColors.RED).build());
                    return;
                }
            }
            catch (ClassNotFoundException | IOException | ObjectMappingException e) {
                returnInv(playerInv, Messages.fileLoadFail(plugin.getAccountStorage().getFile(owner)));
                return;
            }
            catch (SQLException e) {
                returnInv(playerInv, Messages.SQL_EX);
                return;
            }

            transferInv(account, topInv);
            try {
                MySQLHandler sql = plugin.getMySQL();
                if (sql != null) {
                    sql.querySQL(MySQL.createTable(owner));
                    for (int slot = 0; slot < topInv.size(); slot++) {
                        sql.querySQL(MySQL.deleteItem(owner, world.getName(), page, slot));
                        Optional<ItemStack> opt = topInv.query(QueryOperationTypes.INVENTORY_PROPERTY.of(new SlotIndex(slot))).peek();
                        if (opt.isPresent()) {
                            sql.updateSQL(MySQL.addItem(owner, world.getName(), page, slot, serializeItem(opt.get())));
                        }
                    }

                    unregisterListener();
                    return;
                }
                else {
                    File file = plugin.getAccountStorage().getFile(owner);
                    if (file == null) {
                        viewer.sendMessage(Text.builder(Reference.PREFIX + "Could not delete " + owner.toString() + ".conf.").color(TextColors.RED).build());
                        unregisterListener();
                        return;
                    }

                    ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setFile(file).build();
                    ConfigurationNode accountNode = loader.load();
                    ConfigurationNode pageNode = accountNode.getNode(world.getName(), Integer.toString(page));
                    Map<Integer, ConfigurationNode> items = new HashMap<>();
                    for (int slot = 0; slot < account.size(); slot++) {
                        Optional<ItemStack> opt = account.query(QueryOperationTypes.INVENTORY_PROPERTY.of(new SlotIndex(slot))).peek();
                        if (opt.isPresent()) {
                            items.put(slot, DataTranslators.CONFIGURATION_NODE.translate(opt.get().toContainer()));
                        }
                    }

                    pageNode.setValue(items);
                    accountNode.getNode(world.getName(), Integer.toString(page)).setValue(pageNode);
                }
            }
            catch (ClassNotFoundException | IOException | ObjectMappingException e) {
                returnInv(playerInv, Messages.fileLoadFail(plugin.getAccountStorage().getFile(owner)));
                return;
            }
            catch (SQLException e) {
                returnInv(playerInv, Messages.SQL_EX);
                return;
            }

            viewer.sendMessage(Text.builder(Messages.ACCOUNT_UPDATED).color(TextColors.GREEN).build());
        });
    }

    @Override
    protected String serializeItem(ItemStack itemStack) throws ObjectMappingException {
        TypeToken<String> token = TypeToken.of(String.class);
        String string = "";
        TypeSerializers.getDefaultSerializers().get(token).serialize(token, string, DataTranslators.CONFIGURATION_NODE.translate(itemStack.toContainer()));
        return string;
    }

    private void transferInv(Inventory transferTo, Inventory transferFrom) {
        for (int x = 0; x < transferFrom.size(); x++) {
            Optional<ItemStack> itemStack = transferFrom.query(QueryOperationTypes.INVENTORY_PROPERTY.of(new SlotIndex(x))).peek();
            if (itemStack.isPresent()) {
                transferTo.query(QueryOperationTypes.INVENTORY_PROPERTY.of(new SlotIndex(x))).set(itemStack.get());
            }
        }
    }

    @Override
    protected void unregisterListener() {
        Sponge.getEventManager().unregisterListeners(this);
    }
}

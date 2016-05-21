package io.musician101.itembank.spigot.account;

import io.musician101.itembank.common.Reference.Messages;
import io.musician101.itembank.common.Reference.MySQL;
import io.musician101.itembank.common.Reference.Permissions;
import io.musician101.itembank.common.account.AbstractAccountPage;
import io.musician101.itembank.spigot.SpigotItemBank;
import io.musician101.itembank.spigot.config.SpigotConfig;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@SuppressWarnings("WeakerAccess")
public class SpigotAccountPage extends AbstractAccountPage<InventoryCloseEvent, Inventory, ItemStack, Player, String, World> implements Listener
{
    private final SpigotItemBank plugin;

    private SpigotAccountPage(SpigotItemBank plugin, UUID owner, World world, int page)
    {
        super(owner, world, page);
        this.plugin = plugin;
    }

    @Override
    public boolean openInv(Player viewer)//NOSONAR
    {
        this.viewer = viewer;
        Inventory inv;
        try
        {
            inv = getAccount();
        }
        catch (IOException | InvalidConfigurationException e)//NOSONAR
        {
            viewer.sendMessage(ChatColor.RED + Messages.fileLoadFail(plugin.getAccountStorage().getFile(owner)));
            return false;
        }
        catch (ClassNotFoundException | SQLException e)//NOSONAR
        {
            viewer.sendMessage(ChatColor.RED + Messages.SQL_EX);
            return false;
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(viewer.getUniqueId());
        if (offlinePlayer.getUniqueId() == owner && plugin.getEconomy() != null && plugin.getPluginConfig().useEconomy())
        {
            if (plugin.getEconomy() != null && !plugin.getEconomy().withdrawPlayer(offlinePlayer, plugin.getPluginConfig().getTransactionCost()).transactionSuccess())
            {
                viewer.sendMessage(ChatColor.RED + Messages.ACCOUNT_ECON_WITHDRAW_FAIL);
                return false;
            }

            viewer.sendMessage(ChatColor.GREEN + Messages.accountWithdrawSuccess("$", plugin.getPluginConfig().getTransactionCost()));
        }

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        viewer.openInventory(inv);
        return true;
    }

    @EventHandler
    @Override
    public void onInventoryClose(InventoryCloseEvent event)//NOSONAR
    {
        Player player = (Player) event.getPlayer();
        if (player.getUniqueId() != viewer.getUniqueId() && player.hasPermission(Permissions.ADMIN))
            return;

        Inventory inv = event.getView().getTopInventory();
        SpigotConfig config = plugin.getPluginConfig();
        int pageLimit = config.getPageLimit();
        if (((pageLimit > 0 && pageLimit < page) || page == 0) && !player.hasPermission(Permissions.ADMIN))
        {
            for (ItemStack item : inv.getContents())
                player.getWorld().dropItem(player.getLocation(), item);

            player.sendMessage(ChatColor.RED + Messages.ACCOUNT_ILLEGAL_PAGE);
            return;
        }

        boolean hasIllegalItems = false;
        boolean hasIllegalAmount = false;
        for (int x = 0; x < inv.getSize(); x++)
        {
            if (inv.getItem(x) != null)
            {
                ItemStack item = inv.getItem(x);
                int itemAmount = 0;
                for (ItemStack is : inv.getContents())
                    if (is != null && item.getType() == is.getType() && item.getDurability() == is.getDurability())//NOSONAR
                        itemAmount += item.getAmount();

                if (config.getItem(item) != null && !config.isWhitelist())
                {
                    int maxAmount = config.getItem(item).getAmount();
                    if (maxAmount == 0)//NOSONAR
                    {
                        player.getWorld().dropItem(player.getLocation(), item);
                        inv.setItem(x, null);
                        hasIllegalItems = true;
                    }
                    else if (maxAmount < itemAmount)
                    {
                        int amount = itemAmount;
                        while (maxAmount < amount)
                        {
                            int maxStackSize = item.getType().getMaxStackSize();
                            if (maxStackSize < amount)
                            {
                                player.getWorld().dropItem(player.getLocation(), item);
                                inv.setItem(x, null);
                                amount -= maxStackSize;
                            }
                            else
                            {
                                ItemStack removeItem = item.clone();
                                removeItem.setAmount(amount - maxAmount);
                                if (inv.getItem(x) == null)
                                {
                                    int slot = 0;
                                    for (int y = 0; y < inv.getSize(); y++)
                                        if (inv.getItem(y) != null && inv.getItem(y).getDurability() == item.getDurability())
                                            slot = y;

                                    ItemStack is = inv.getItem(slot);
                                    is.setAmount(item.getAmount() - removeItem.getAmount());
                                    inv.setItem(slot, is);
                                }
                                else
                                    inv.getItem(x).setAmount(item.getAmount() - removeItem.getAmount());

                                player.getWorld().dropItem(player.getLocation(), removeItem);
                                amount -= removeItem.getAmount();
                            }
                        }

                        hasIllegalAmount = true;
                    }
                }
                else if (config.getItem(item) == null && config.isWhitelist())
                {
                    player.getWorld().dropItem(player.getLocation(), item);
                    inv.setItem(x, null);
                    hasIllegalItems = true;
                }
            }
        }

        if (hasIllegalItems)
            player.sendMessage(ChatColor.RED + Messages.ACCOUNT_ILLEGAL_ITEM);

        if (hasIllegalAmount)
            player.sendMessage(ChatColor.RED + Messages.ACCOUNT_ILLEGAL_AMOUNT);

        saveAccount(inv, player.getInventory());
        HandlerList.unregisterAll(this);
    }

    @Override
    protected void saveAccount(Inventory topInv, Inventory playerInv)//NOSONAR
    {
        Inventory account;
        try
        {
            account = getAccount();
        }
        catch (FileNotFoundException e)//NOSONAR
        {
            returnInv(playerInv, Messages.NO_FILE_EX);
            return;
        }
        catch (InvalidConfigurationException | IOException e)//NOSONAR
        {
            returnInv(playerInv, Messages.fileLoadFail(plugin.getAccountStorage().getFile(owner)));
            return;
        }
        catch (ClassNotFoundException | SQLException e)//NOSONAR
        {
            returnInv(playerInv, Messages.SQL_EX);
            return;
        }

        account.setContents(topInv.getContents());
        File file = plugin.getAccountStorage().getFile(owner);
        try
        {
            if (plugin.getPluginConfig().useMySQL())
            {
                plugin.getMySQLHandler().querySQL(MySQL.createTable(owner));
                for (int slot = 0; slot < account.getSize(); slot++)
                {
                    plugin.getMySQLHandler().querySQL(MySQL.deleteItem(owner, world.getName(), page, slot));
                    ItemStack item = account.getItem(slot);
                    if (item != null)//NOSONAR
                        plugin.getMySQLHandler().updateSQL(MySQL.addItem(owner, world.getName(), page, slot, serializeItem(item).replace("\"", "\\\"")));
                }

                return;
            }

            if (file == null)
            {
                viewer.sendMessage(ChatColor.RED + Messages.fileLoadFail(plugin.getAccountStorage().getFile(owner)));
                return;
            }

            YamlConfiguration accountYml = new YamlConfiguration();
            accountYml.load(file);
            for (int slot = 0; slot < account.getSize(); slot++)
                accountYml.set(world.getName() + "." + page + "." + slot, account.getItem(slot));

            accountYml.save(file);
        }
        catch (FileNotFoundException e)//NOSONAR
        {
            returnInv(playerInv, Messages.NO_FILE_EX);
            return;
        }
        catch (InvalidConfigurationException | IOException e)//NOSONAR
        {
            returnInv(playerInv, Messages.fileLoadFail(file));
            return;
        }
        catch (ClassNotFoundException | SQLException e)//NOSONAR
        {
            returnInv(playerInv, Messages.SQL_EX);
            return;
        }

        viewer.sendMessage(Messages.ACCOUNT_UPDATED);
    }

    @Override
    protected void returnInv(Inventory inventory, String message)
    {
        viewer.sendMessage(ChatColor.RED + message);
        for (ItemStack itemStack : inventory.getContents())
            world.dropItem(viewer.getLocation(), itemStack);

        unregisterListener();
    }

    @Override
    protected Inventory getAccount() throws ClassNotFoundException, IOException, InvalidConfigurationException, SQLException
    {
        final Inventory inv = Bukkit.createInventory(viewer, 54, Bukkit.getOfflinePlayer(owner).getName() + " - Page " + page);
        if (plugin.getPluginConfig().useMySQL())
        {
            plugin.getMySQLHandler().querySQL("CREATE TABLE IF NOT EXISTS ib_" + owner.toString().replace("-", "_") + "(World varchar(255), Page int, Slot int, ItemStack varchar(300));");
            for (int slot = 0; slot < inv.getSize(); slot++)
                inv.setItem(slot, getItem(plugin.getMySQLHandler().querySQL("SELECT * FROM ib_" + owner + " WHERE World = \"" + world.getName() + "\" AND Page = " + page + " AND Slot = " + slot + ";")));

            return inv;
        }

        File file = plugin.getAccountStorage().getFile(owner);
        YamlConfiguration account = new YamlConfiguration();
        account.load(file);
        for (int slot = 0; slot < inv.getSize(); slot++)
            inv.setItem(slot, account.getItemStack(world.getName() + "." + page + "." + slot));

        return inv;
    }

    @Override
    protected ItemStack getItem(ResultSet resultSet) throws SQLException
    {
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("item", resultSet.getString("ItemStack"));
        return yaml.getItemStack("item");
    }

    @Override
    protected String serializeItem(ItemStack itemStack)
    {
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("item", itemStack);
        return yaml.get("item").toString();
    }

    @Override
    protected ItemStack deserializeItem(String string)
    {
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("item", string);
        return yaml.getItemStack("item");
    }

    @Override
    protected void unregisterListener()
    {
        HandlerList.unregisterAll(this);
    }

    public static SpigotAccountPage createNewPage(SpigotItemBank plugin, UUID owner, World world, int page)
    {
        return new SpigotAccountPage(plugin, owner, world, page);
    }
}

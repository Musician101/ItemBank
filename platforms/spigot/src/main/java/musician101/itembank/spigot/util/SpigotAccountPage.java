package musician101.itembank.spigot.util;

import au.com.bytecode.opencsv.CSVReader;
import musician101.itembank.common.AbstractAccountPage;
import musician101.itembank.spigot.SpigotItemBank;
import musician101.itembank.spigot.config.SpigotConfig;
import musician101.itembank.spigot.config.json.SpigotJSONConfig;
import musician101.itembank.spigot.lib.Messages;
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
import org.json.simple.parser.ParseException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class SpigotAccountPage extends AbstractAccountPage<InventoryCloseEvent, Inventory, Player, World> implements Listener
{
    private final SpigotItemBank plugin;

    public SpigotAccountPage(SpigotItemBank plugin, Player viewer, UUID owner, World world, int page)
    {
        super(viewer, owner, world, page);
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    @Override
    public void onInventoryClose(InventoryCloseEvent event)
    {
        Player player = (Player) event.getPlayer();
        if (player.getUniqueId() != viewer.getUniqueId())
            return;

        Inventory inv = event.getView().getTopInventory();
        SpigotConfig config = plugin.getPluginConfig();
        int pageLimit = config.getPageLimit();
        if (((pageLimit > 0 && pageLimit < page) || page == 0) && !player.hasPermission("itembank.account.admin"))
        {
            for (ItemStack item : inv.getContents())
                player.getWorld().dropItem(player.getLocation(), item);

            player.sendMessage(Messages.ACCOUNT_ILLEGAL_PAGE);
            return;
        }

        boolean hasIllegalItems = false;
        boolean hasIllegalAmount = false;
        for (int x = 0; x < inv.getSize(); x++)
        {
            if (inv.getItem(x) != null)
            {
                ItemStack item = inv.getItem(x);
                int itemAmount = IBUtils.getAmount(inv, item.getType(), item.getDurability());
                if (config.getItem(item.getType(), item.getDurability()) != null && !config.isWhitelist())
                {
                    int maxAmount = config.getItem(item.getType(), item.getDurability()).getAmount();
                    if (maxAmount == 0)
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
                                        if (inv.getItem(y) != null)
                                            if (inv.getItem(y).getDurability() == item.getDurability())
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
                else if (config.getItem(item.getType(), item.getDurability()) == null && config.isWhitelist())
                {
                    player.getWorld().dropItem(player.getLocation(), item);
                    inv.setItem(x, null);
                    hasIllegalItems = true;
                }
            }
        }

        if (hasIllegalItems)
            player.sendMessage(Messages.ACCOUNT_ILLEGAL_ITEM);

        if (hasIllegalAmount)
            player.sendMessage(Messages.ACCOUNT_ILLEGAL_AMOUNT);

        saveAccount(inv, player.getInventory());
        HandlerList.unregisterAll(this);
    }

    @Override
    protected void saveAccount(Inventory topInv, Inventory playerInv)
    {
        Inventory account;
        try
        {
            account = IBUtils.getAccount(plugin, world, owner, page);
        }
        catch (FileNotFoundException e)
        {
            returnInv(playerInv, Messages.NO_FILE_EX);
            return;
        }
        catch (IOException e)
        {
            returnInv(playerInv, Messages.IO_EX);
            return;
        }
        catch (InvalidConfigurationException | ParseException e)
        {
            returnInv(playerInv, Messages.YAML_PARSE_EX);
            return;
        }
        catch (ClassNotFoundException | SQLException e)
        {
            returnInv(playerInv, Messages.SQL_EX);
            return;
        }

        account.setContents(topInv.getContents());
        try
        {
            if (plugin.getPluginConfig().useMySQL())
            {
                plugin.getMySQLHandler().querySQL("CREATE TABLE IF NOT EXISTS ib_" + uuid.toString().replace("-", "_") + "(World varchar(255), Page int, Slot int, Material varchar(255), Damage int, Amount int, ItemMeta varchar(300));");
                for (int slot = 0; slot < inventory.getSize(); slot++)
                {
                    plugin.getMySQLHandler().querySQL("DELETE FROM ib_" + uuid + " WHERE World = \"" + worldName + "\" AND Page = " + page + " AND Slot = " + slot + ";");
                    ItemStack item = inventory.getItem(slot);
                    if (item != null)
                        plugin.getMySQLHandler().updateSQL("INSERT INTO ib_" + uuid + "(World, Page, Slot, Material, Damage, Amount, ItemMeta) VALUES (\"" + worldName + "\", " + page + ", " + slot + ", \"" + item.getType().toString() + "\", " + item.getDurability() + ", " + item.getAmount() + ", \"" + metaToJson(item).replace("\"", "\\\"") + "\");");
                }

                return;
            }

            File file = plugin.getPluginConfig().getPlayerFile(uuid);
            if (file.getName().endsWith("csv"))
            {
                List<String> account = new ArrayList<>();
                for (String[] s : new CSVReader(new FileReader(file), '|').readAll())
                {
                    if (!s[0].startsWith("#"))
                    {
                        if (!worldName.equals(s[0]))
                            if (Integer.valueOf(s[1]) != page)
                                for (int slot = 0; slot < inventory.getSize(); slot++)
                                    if (Integer.valueOf(s[2]) != slot)
                                        account.add(Arrays.toString(s));
                    }
                    else
                        account.add(Arrays.toString(s).replace("[", "").replace("]", ""));
                }

                for (int slot = 0; slot < inventory.getSize(); slot++)
                {
                    ItemStack item = inventory.getItem(slot);
                    if (item != null)
                        account.add(worldName + "|" + page + "|" + slot + "|" + item.getType().toString() + "|" + item.getDurability() + "|" + item.getAmount() + "|" + metaToJson(item));
                }

                file.delete();
                createPlayerFile(file);
                BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
                for (String line : account)
                    bw.write(line + "\n");

                bw.close();
                return;
            }
            else if (file.getName().endsWith("json"))
            {
                SpigotJSONConfig account = SpigotJSONConfig.loadSpigotJSONConfig(file);
                SpigotJSONConfig pg = new SpigotJSONConfig();
                pg.setInventory(page + "", inventory);
                if (account == null)
                    account = new SpigotJSONConfig();

                account.setSpigotJSONConfig(worldName, pg);
                FileWriter fw = new FileWriter(file);
                fw.write(account.toJSONString());
                fw.close();
                return;
            }

            YamlConfiguration account = new YamlConfiguration();
            account.load(file);
            for (int slot = 0; slot < inventory.getSize(); slot++)
                account.set(worldName + "." + page + "." + slot, inventory.getItem(slot));

            account.save(file);
        }
        catch (FileNotFoundException e)
        {
            returnInv(playerInv, Messages.NO_FILE_EX);
            return;
        }
        catch (IOException e)
        {
            returnInv(playerInv, Messages.IO_EX);
            return;
        }
        catch (InvalidConfigurationException | ParseException e)
        {
            returnInv(playerInv, Messages.YAML_PARSE_EX);
            return;
        }
        catch (ClassNotFoundException | SQLException e)
        {
            returnInv(playerInv, Messages.SQL_EX);
            return;
        }

        viewer.sendMessage(Messages.ACCOUNT_UPDATED);
    }

    @Override
    protected void returnInv(Inventory inventory, String message)
    {
        viewer.sendMessage(message);
        for (ItemStack itemStack : inventory.getContents())
            world.dropItem(viewer.getLocation(), itemStack);
    }
}

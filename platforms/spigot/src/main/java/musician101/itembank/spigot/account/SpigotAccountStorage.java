package musician101.itembank.spigot.account;

import musician101.itembank.common.MySQLHandler;
import musician101.itembank.common.Reference.Messages;
import musician101.itembank.common.Reference.MySQL;
import musician101.itembank.common.Reference.PlayerData;
import musician101.itembank.common.account.AbstractAccountStorage;
import musician101.itembank.spigot.SpigotItemBank;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class SpigotAccountStorage extends AbstractAccountStorage<SpigotAccountPage, Player, World>
{
    private final SpigotItemBank plugin;

    private SpigotAccountStorage(SpigotItemBank plugin)
    {
        super(new File(plugin.getDataFolder(), PlayerData.DIRECTORY));
        this.plugin = plugin;
        loadPages();
    }

    @Override
    protected void loadPages()
    {
        if (plugin.getPluginConfig().useMySQL())
        {
            MySQLHandler mysql = plugin.getMySQLHandler();
            try
            {
                ResultSet tableSet = mysql.getConnection().getMetaData().getTables(null, null, null, new String[]{"TABLE"});
                while (tableSet.next())
                {
                    String tableName = tableSet.getString(3);
                    if (tableName.startsWith(MySQL.TABLE_PREFIX))
                    {
                        List<SpigotAccountPage> pages = new ArrayList<>();
                        ResultSet pageSet = mysql.querySQL(MySQL.getTable(tableName));
                        UUID owner = MySQL.getUUIDFromTableName(tableName);
                        if (owner == null)
                        {
                            plugin.getLogger().warning(Messages.badUUID(tableName));
                            return;
                        }

                        while (pageSet.next())
                            pages.add(SpigotAccountPage.createNewPage(plugin, owner, Bukkit.getWorld(pageSet.getString(MySQL.WORLD)), pageSet.getInt(MySQL.PAGE)));

                        accountPages.put(owner, pages);
                    }
                }
            }
            catch (ClassNotFoundException | SQLException e)
            {
                plugin.getLogger().warning(Messages.SQL_EX);
            }

            return;
        }

        //noinspection ConstantConditions
        for (File file : storageDir.listFiles())
        {
            if (file.getName().endsWith(PlayerData.FILE_EXTENSION))
            {
                List<SpigotAccountPage> pages = new ArrayList<>();
                UUID owner = PlayerData.getUUIDFromFileName(file);
                if (owner == null)
                    plugin.getLogger().warning(Messages.badUUID(file.getName()));
                else
                {
                    YamlConfiguration account = new YamlConfiguration();
                    try
                    {
                        account.load(file);
                    }
                    catch (InvalidConfigurationException | IOException e)
                    {
                        plugin.getLogger().warning(Messages.fileLoadFail(file));
                        return;
                    }

                    for (String worldName : account.getValues(false).keySet())
                    {
                        ConfigurationSection world = account.getConfigurationSection(worldName);
                        pages.addAll(world.getValues(false).keySet().stream().map(pageString -> SpigotAccountPage.createNewPage(plugin, owner, Bukkit.getWorld(worldName), Integer.parseInt(pageString))).collect(Collectors.toList()));
                    }

                    accountPages.put(owner, pages);
                }
            }
        }
    }

    @Override
    public boolean openInv(Player viewer, UUID owner, World world, int page)
    {
        for (SpigotAccountPage sap : accountPages.get(owner))
            if (owner == sap.getOwner() && world.getName().equals(sap.getWorld().getName()) && page == sap.getPage())
                return sap.openInv(viewer);

        return false;
    }

    public static SpigotAccountStorage load(SpigotItemBank plugin)
    {
        return new SpigotAccountStorage(plugin);
    }
}

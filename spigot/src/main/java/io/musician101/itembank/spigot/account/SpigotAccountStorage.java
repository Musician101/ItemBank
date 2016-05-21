package io.musician101.itembank.spigot.account;

import io.musician101.itembank.common.MySQLHandler;
import io.musician101.itembank.common.Reference.Messages;
import io.musician101.itembank.common.Reference.MySQL;
import io.musician101.itembank.common.Reference.PlayerData;
import io.musician101.itembank.common.account.AbstractAccountStorage;
import io.musician101.itembank.spigot.SpigotItemBank;
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
    private SpigotAccountStorage()
    {
        super(new File(SpigotItemBank.instance().getDataFolder(), PlayerData.DIRECTORY));
        loadPages();
    }

    @Override
    protected void loadPages()//NOSONAR
    {
        if (SpigotItemBank.instance().getPluginConfig().useMySQL())
        {
            MySQLHandler mysql = SpigotItemBank.instance().getMySQLHandler();
            try
            {
                ResultSet tableSet = mysql.getConnection().getMetaData().getTables(null, null, null, new String[]{"TABLE"});
                while (tableSet.next())
                {
                    String tableName = tableSet.getString(3);
                    if (tableName.startsWith(MySQL.TABLE_PREFIX))//NOSONAR
                    {
                        List<SpigotAccountPage> pages = new ArrayList<>();
                        ResultSet pageSet = mysql.querySQL(MySQL.getTable(tableName));
                        UUID owner = MySQL.getUUIDFromTableName(tableName);
                        if (owner == null)
                        {
                            SpigotItemBank.instance().getLogger().warning(Messages.badUUID(tableName));
                            return;
                        }

                        while (pageSet.next())
                            pages.add(SpigotAccountPage.createNewPage(SpigotItemBank.instance(), owner, Bukkit.getWorld(pageSet.getString(MySQL.WORLD)), pageSet.getInt(MySQL.PAGE)));

                        accountPages.put(owner, pages);
                    }
                }
            }
            catch (ClassNotFoundException | SQLException e)//NOSONAR
            {
                SpigotItemBank.instance().getLogger().warning(Messages.SQL_EX);
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
                    SpigotItemBank.instance().getLogger().warning(Messages.badUUID(file.getName()));
                else
                {
                    YamlConfiguration account = new YamlConfiguration();
                    try//NOSONAR
                    {
                        account.load(file);
                    }
                    catch (InvalidConfigurationException | IOException e)//NOSONAR
                    {
                        SpigotItemBank.instance().getLogger().warning(Messages.fileLoadFail(file));
                        return;
                    }

                    for (String worldName : account.getValues(false).keySet())//NOSONAR
                    {
                        ConfigurationSection world = account.getConfigurationSection(worldName);
                        pages.addAll(world.getValues(false).keySet().stream().map(pageString -> SpigotAccountPage.createNewPage(SpigotItemBank.instance(), owner, Bukkit.getWorld(worldName), Integer.parseInt(pageString))).collect(Collectors.toList()));
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

    public static SpigotAccountStorage load()
    {
        return new SpigotAccountStorage();
    }
}

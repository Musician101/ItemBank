package musician101.sponge.itembank.inventory;

import musician101.itembank.common.MySQLHandler;
import musician101.itembank.common.Reference;
import musician101.itembank.common.Reference.Messages;
import musician101.itembank.common.Reference.MySQL;
import musician101.itembank.common.Reference.PlayerData;
import musician101.itembank.common.account.AbstractAccountStorage;
import musician101.sponge.itembank.SpongeItemBank;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.World;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SpongeAccountStorage extends AbstractAccountStorage<SpongeAccountPage, Player, World>
{
    private SpongeAccountStorage()
    {
        super(new File("config/" + Reference.ID, PlayerData.DIRECTORY));
        loadPages();
    }

    @Override
    protected void loadPages()
    {
        Logger logger = SpongeItemBank.logger;
        if (SpongeItemBank.config.useMySQL())
        {
            MySQLHandler mysql = SpongeItemBank.mysql;
            try
            {
                ResultSet tableSet = mysql.getConnection().getMetaData().getTables(null, null, null, new String[]{"TABLE"});
                while (tableSet.next())
                {
                    String tableName = tableSet.getString(3);
                    if (tableName.startsWith(MySQL.TABLE_PREFIX))
                    {
                        List<SpongeAccountPage> pages = new ArrayList<>();
                        ResultSet pageSet = mysql.querySQL(MySQL.getTable(tableName));
                        UUID owner = MySQL.getUUIDFromTableName(tableName);
                        if (owner == null)
                        {
                            logger.error(Messages.badUUID(tableName));
                            return;
                        }

                        while (pageSet.next())
                            pages.add(SpongeAccountPage.createNewPage(owner, Sponge.getServer().getWorld(pageSet.getString(MySQL.WORLD)).get(), pageSet.getInt(MySQL.PAGE)));

                        accountPages.put(owner, pages);
                    }
                }
            }
            catch (ClassNotFoundException | SQLException e)
            {
                logger.error(Messages.SQL_EX);
            }

            return;
        }

        //noinspection ConstantConditions
        for (File file : storageDir.listFiles())
        {
            if (file.getName().endsWith(PlayerData.FILE_EXTENSION))
            {
                List<SpongeAccountPage> pages = new ArrayList<>();
                UUID owner = PlayerData.getUUIDFromFileName(file);
                if (owner == null)
                    logger.error(Messages.badUUID(file.getName()));
                else
                {
                    ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setFile(file).build();
                    ConfigurationNode account;
                    try
                    {
                        account = loader.load();
                    }
                    catch (IOException e)
                    {
                        logger.error(Messages.fileLoadFail(file));
                        return;
                    }

                    for (Object worldName : account.getChildrenMap().keySet())
                    {
                        ConfigurationNode world = account.getNode(worldName);
                        for (Object page : world.getChildrenMap().keySet())
                            pages.add(SpongeAccountPage.createNewPage(owner, Sponge.getServer().getWorld(worldName.toString()).get(), Integer.parseInt(page.toString())));
                    }

                    accountPages.put(owner, pages);
                }
            }
        }
    }

    @Override
    public boolean openInv(Player viewer, UUID owner, World world, int page)
    {
        for (SpongeAccountPage sap : accountPages.get(owner))
            if (owner == sap.getOwner() && world.getName().equals(sap.getWorld().getName()) && page == sap.getPage())
                return sap.openInv(viewer);

        return false;
    }

    public static SpongeAccountStorage load()
    {
        return new SpongeAccountStorage();
    }
}

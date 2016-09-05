package io.musician101.itembank.sponge.account;

import io.musician101.common.java.MySQLHandler;
import io.musician101.itembank.common.Reference.Messages;
import io.musician101.itembank.common.Reference.MySQL;
import io.musician101.itembank.common.Reference.PlayerData;
import io.musician101.itembank.common.account.AbstractAccountStorage;
import io.musician101.itembank.sponge.SpongeItemBank;
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
import java.util.stream.Collectors;

public class SpongeAccountStorage extends AbstractAccountStorage<SpongeAccountPage, Player, World>
{
    private SpongeAccountStorage(File configDir)
    {
        super(new File(configDir, PlayerData.DIRECTORY));
        loadPages();
    }

    @Override
    protected void loadPages()//NOSONAR
    {
        Logger logger = SpongeItemBank.getLogger();
        if (SpongeItemBank.instance().getConfig().useMySQL())
        {
            MySQLHandler mysql = SpongeItemBank.instance().getMySQL();
            try
            {
                ResultSet tableSet = mysql.getConnection().getMetaData().getTables(null, null, null, new String[]{"TABLE"});
                while (tableSet.next())
                {
                    String tableName = tableSet.getString(3);
                    if (tableName.startsWith(MySQL.TABLE_PREFIX))//NOSONAR
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
                            //noinspection OptionalGetWithoutIsPresent
                            pages.add(SpongeAccountPage.createNewPage(owner, Sponge.getServer().getWorld(pageSet.getString(MySQL.WORLD)).get(), pageSet.getInt(MySQL.PAGE)));

                        accountPages.put(owner, pages);
                    }
                }
            }
            catch (ClassNotFoundException | SQLException e)//NOSONAR
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
                    try//NOSONAR
                    {
                        account = loader.load();
                    }
                    catch (IOException e)//NOSONAR
                    {
                        logger.error(Messages.fileLoadFail(file));
                        return;
                    }

                    for (Object worldName : account.getChildrenMap().keySet())//NOSONAR
                    {
                        ConfigurationNode world = account.getNode(worldName);
                        //noinspection OptionalGetWithoutIsPresent
                        pages.addAll(world.getChildrenMap().keySet().stream().map(page -> SpongeAccountPage.createNewPage(owner, Sponge.getServer().getWorld(worldName.toString()).get(), Integer.parseInt(page.toString()))).collect(Collectors.toList()));
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

    public static SpongeAccountStorage load(File configDir)
    {
        return new SpongeAccountStorage(configDir);
    }
}

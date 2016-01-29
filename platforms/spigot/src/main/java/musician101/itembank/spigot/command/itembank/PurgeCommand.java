package musician101.itembank.spigot.command.itembank;

import musician101.common.java.minecraft.command.AbstractCommandArgument.Syntax;
import musician101.common.java.minecraft.spigot.command.AbstractSpigotCommand;
import musician101.common.java.minecraft.spigot.command.SpigotCommandArgument;
import musician101.common.java.minecraft.uuid.UUIDUtils;
import musician101.itembank.common.Reference.Commands;
import musician101.itembank.common.Reference.Messages;
import musician101.itembank.common.Reference.MySQL;
import musician101.itembank.common.Reference.Permissions;
import musician101.itembank.spigot.SpigotItemBank;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.UUID;

public class PurgeCommand extends AbstractSpigotCommand
{
    private final SpigotItemBank plugin;

    public PurgeCommand(SpigotItemBank plugin)
    {
        super(Commands.PURGE_NAME, Commands.PURGE_DESC, Arrays.asList(new SpigotCommandArgument(Commands.IB_CMD), new SpigotCommandArgument(Commands.PURGE_NAME), new SpigotCommandArgument(Commands.PLAYER, Syntax.OPTIONAL)), 0, Permissions.PURGE, false, Messages.NO_PERMISSION, Messages.PLAYER_CMD);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args)
    {
        if (!canSenderUseCommand(sender))
            return false;

        UUID uuid;
        try
        {
            uuid = UUIDUtils.getUUIDOf(args[0]);
        }
        catch (IOException e)
        {
            sender.sendMessage(Messages.UNKNOWN_EX);
            return false;
        }

        if (args.length > 0)
        {
            if (plugin.getPluginConfig().useMySQL())
            {
                try
                {
                    plugin.getMySQLHandler().querySQL(MySQL.deleteTable(uuid));
                }
                catch (ClassNotFoundException | SQLException e)
                {
                    sender.sendMessage(Messages.SQL_EX);
                    return false;
                }

                sender.sendMessage(Messages.PURGE_SINGLE);
                return true;
            }

            File file = plugin.getAccountStorage().getFile(uuid);
            if (!file.exists())
            {
                sender.sendMessage(Messages.PURGE_NO_FILE);
                return false;
            }

            if (!file.delete())
                sender.sendMessage(Messages.fileDeleteFail(file));
            else
                sender.sendMessage(Messages.PURGE_SINGLE);

            return true;
        }

        if (plugin.getPluginConfig().useMySQL())
        {
            try
            {
                ResultSet rs = plugin.getMySQLHandler().getConnection().getMetaData().getTables(null, null, null, new String[]{"TABLE"});
                while (rs.next())
                {
                    sender.sendMessage(rs.getString(3));
                    if (rs.getString(3).startsWith(MySQL.TABLE_PREFIX))
                        plugin.getMySQLHandler().querySQL(MySQL.deleteTable(rs.getString(3)));
                }
            }
            catch (SQLException | ClassNotFoundException e)
            {
                sender.sendMessage(Messages.SQL_EX);
                return false;
            }

            sender.sendMessage(Messages.PURGE_MULTIPLE);
            return true;
        }

        for (File file : plugin.getAccountStorage().resetAll())
            sender.sendMessage(Messages.fileDeleteFail(file));

        sender.sendMessage(Messages.PURGE_MULTIPLE);
        return true;
    }
}

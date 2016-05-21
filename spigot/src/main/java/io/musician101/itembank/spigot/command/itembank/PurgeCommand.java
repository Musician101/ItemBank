package io.musician101.itembank.spigot.command.itembank;

import io.musician101.common.java.minecraft.command.AbstractCommandArgument.Syntax;
import io.musician101.common.java.minecraft.spigot.command.AbstractSpigotCommand;
import io.musician101.common.java.minecraft.spigot.command.SpigotCommandArgument;
import io.musician101.common.java.minecraft.spigot.command.SpigotCommandPermissions;
import io.musician101.common.java.minecraft.spigot.command.SpigotCommandUsage;
import io.musician101.common.java.minecraft.uuid.UUIDUtils;
import io.musician101.itembank.common.Reference.Commands;
import io.musician101.itembank.common.Reference.Messages;
import io.musician101.itembank.common.Reference.MySQL;
import io.musician101.itembank.common.Reference.Permissions;
import io.musician101.itembank.spigot.SpigotItemBank;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.UUID;

@SuppressWarnings("WeakerAccess")
public class PurgeCommand extends AbstractSpigotCommand
{
    public PurgeCommand()
    {
        super(Commands.PURGE_NAME, Commands.PURGE_DESC, new SpigotCommandUsage(Arrays.asList(new SpigotCommandArgument(Commands.IB_CMD), new SpigotCommandArgument(Commands.PURGE_NAME), new SpigotCommandArgument(Commands.PLAYER, Syntax.OPTIONAL))), new SpigotCommandPermissions(Permissions.PURGE, false, Messages.NO_PERMISSION, Messages.PLAYER_CMD));
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args)//NOSONAR
    {
        if (!canSenderUseCommand(sender))
            return false;

        UUID uuid;
        try
        {
            uuid = UUIDUtils.getUUIDOf(args[0]);
        }
        catch (IOException e)//NOSONAR
        {
            sender.sendMessage(Messages.UNKNOWN_EX);
            return false;
        }

        if (args.length > 0)
        {
            if (SpigotItemBank.instance().getPluginConfig().useMySQL())
            {
                try
                {
                    SpigotItemBank.instance().getMySQLHandler().querySQL(MySQL.deleteTable(uuid));
                }
                catch (ClassNotFoundException | SQLException e)//NOSONAR
                {
                    sender.sendMessage(Messages.SQL_EX);
                    return false;
                }

                sender.sendMessage(Messages.PURGE_SINGLE);
                return true;
            }

            File file = SpigotItemBank.instance().getAccountStorage().getFile(uuid);
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

        if (SpigotItemBank.instance().getPluginConfig().useMySQL())
        {
            try
            {
                ResultSet rs = SpigotItemBank.instance().getMySQLHandler().getConnection().getMetaData().getTables(null, null, null, new String[]{"TABLE"});
                while (rs.next())
                {
                    sender.sendMessage(rs.getString(3));
                    if (rs.getString(3).startsWith(MySQL.TABLE_PREFIX))//NOSONAR
                        SpigotItemBank.instance().getMySQLHandler().querySQL(MySQL.deleteTable(rs.getString(3)));
                }
            }
            catch (SQLException | ClassNotFoundException e)//NOSONAR
            {
                sender.sendMessage(Messages.SQL_EX);
                return false;
            }

            sender.sendMessage(Messages.PURGE_MULTIPLE);
            return true;
        }

        for (File file : SpigotItemBank.instance().getAccountStorage().resetAll())
            sender.sendMessage(Messages.fileDeleteFail(file));

        sender.sendMessage(Messages.PURGE_MULTIPLE);
        return true;
    }
}

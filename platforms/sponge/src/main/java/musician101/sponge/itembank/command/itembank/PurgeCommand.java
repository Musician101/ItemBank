package musician101.sponge.itembank.command.itembank;

import musician101.common.java.minecraft.command.AbstractCommandArgument.Syntax;
import musician101.common.java.minecraft.sponge.TextUtils;
import musician101.common.java.minecraft.sponge.command.AbstractSpongeCommand;
import musician101.common.java.minecraft.sponge.command.SpongeCommandArgument;
import musician101.itembank.common.Reference;
import musician101.itembank.common.Reference.Commands;
import musician101.itembank.common.Reference.Messages;
import musician101.itembank.common.Reference.Permissions;
import musician101.itembank.common.UUIDUtils;
import musician101.sponge.itembank.SpongeItemBank;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;

import javax.annotation.Nonnull;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.UUID;

public class PurgeCommand extends AbstractSpongeCommand
{
    public PurgeCommand()
    {
        super(Commands.PURGE_NAME, Commands.PURGE_DESC, Arrays.asList(new SpongeCommandArgument("/" + Reference.ID), new SpongeCommandArgument(Commands.PURGE_NAME), new SpongeCommandArgument(Commands.PLAYER, Syntax.OPTIONAL, Syntax.REPLACE)), 0, Permissions.PURGE, false, TextUtils.redText(Messages.NO_PERMISSION), TextUtils.redText(Messages.PLAYER_CMD));
    }

    @Nonnull
    @Override
    public CommandResult process(@Nonnull CommandSource source, @Nonnull String arguments)
    {
        String[] args = splitArgs(arguments);
        if (args.length > 0)
        {
            UUID uuid;
            try
            {
                uuid = UUIDUtils.getUUIDOf(args[0]);
            }
            catch (Exception e)
            {
                source.sendMessage(TextUtils.redText(Messages.UNKNOWN_EX));
                return CommandResult.empty();
            }

            if (SpongeItemBank.mysql != null)
            {
                try
                {
                    SpongeItemBank.mysql.querySQL("DROP TABLE IF EXISTS ib_" + uuid.toString().replace("-", "_"));
                }
                catch (ClassNotFoundException | SQLException e)
                {
                    source.sendMessage(TextUtils.redText(Messages.SQL_EX));
                    return CommandResult.empty();
                }

                source.sendMessage(TextUtils.redText(Messages.PURGE_SINGLE));
                return CommandResult.success();
            }

            File file = SpongeItemBank.config.getPlayerFile(uuid);
            if (!file.exists())
            {
                source.sendMessage(TextUtils.redText(Messages.PURGE_NO_FILE));
                return CommandResult.empty();
            }

            if (!file.delete())
            {
                source.sendMessage(TextUtils.redText(Messages.purgeFileFail(file)));
                return CommandResult.empty();
            }

            source.sendMessage(TextUtils.redText(Messages.PURGE_SINGLE));
            return CommandResult.success();
        }

        if (SpongeItemBank.mysql != null)
        {
            try
            {
                ResultSet rs = SpongeItemBank.mysql.getConnection().getMetaData().getTables(null, null, null, new String[]{"TABLE"});
                while (rs.next())
                    if (rs.getString(3).contains("ib_"))
                        SpongeItemBank.mysql.querySQL("DROP TABLE " + rs.getString(3));
            }
            catch (SQLException | ClassNotFoundException e)
            {
                source.sendMessage(TextUtils.redText(Messages.SQL_EX));
                return CommandResult.empty();
            }
        }
        else
        {
            File[] files = SpongeItemBank.config.getPlayerData().listFiles();
            if (files != null)
                for (File file : files)
                    if (!file.delete())
                        source.sendMessage(TextUtils.redText(Messages.purgeFileFail(file)));
        }

        source.sendMessage(TextUtils.redText(Messages.PURGE_MULTIPLE));
        return CommandResult.empty();
    }
}

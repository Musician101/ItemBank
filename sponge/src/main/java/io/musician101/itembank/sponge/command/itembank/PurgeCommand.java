package io.musician101.itembank.sponge.command.itembank;

import io.musician101.common.java.minecraft.command.AbstractCommandArgument.Syntax;
import io.musician101.common.java.minecraft.sponge.TextUtils;
import io.musician101.common.java.minecraft.sponge.command.AbstractSpongeCommand;
import io.musician101.common.java.minecraft.sponge.command.SpongeCommandArgument;
import io.musician101.common.java.minecraft.sponge.command.SpongeCommandPermissions;
import io.musician101.common.java.minecraft.sponge.command.SpongeCommandUsage;
import io.musician101.common.java.minecraft.uuid.UUIDUtils;
import io.musician101.itembank.common.Reference.Commands;
import io.musician101.itembank.common.Reference.Messages;
import io.musician101.itembank.common.Reference.Permissions;
import io.musician101.itembank.sponge.SpongeItemBank;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.UUID;

@SuppressWarnings("WeakerAccess")
public class PurgeCommand extends AbstractSpongeCommand
{
    public PurgeCommand()
    {
        super(Commands.PURGE_NAME, Text.of(Commands.PURGE_DESC), new SpongeCommandUsage(Arrays.asList(new SpongeCommandArgument(Commands.IB_CMD), new SpongeCommandArgument(Commands.PURGE_NAME), new SpongeCommandArgument(Commands.PLAYER, Syntax.OPTIONAL, Syntax.REPLACE))), new SpongeCommandPermissions(Permissions.PURGE, false, TextUtils.redText(Messages.NO_PERMISSION), TextUtils.redText(Messages.PLAYER_CMD)));
    }

    @Nonnull
    @Override
    public CommandResult process(@Nonnull CommandSource source, @Nonnull String arguments)//NOSONAR
    {
        String[] args = splitArgs(arguments);
        if (args.length > 0)
        {
            UUID uuid;
            try
            {
                uuid = UUIDUtils.getUUIDOf(args[0]);
            }
            catch (IOException e)//NOSONAR
            {
                source.sendMessage(TextUtils.redText(Messages.UNKNOWN_EX));
                return CommandResult.empty();
            }

            if (SpongeItemBank.instance().getMySQL() != null)
            {
                try
                {
                    SpongeItemBank.instance().getMySQL().querySQL("DROP TABLE IF EXISTS ib_" + uuid.toString().replace("-", "_"));
                }
                catch (ClassNotFoundException | SQLException e)//NOSONAR
                {
                    source.sendMessage(TextUtils.redText(Messages.SQL_EX));
                    return CommandResult.empty();
                }

                source.sendMessage(TextUtils.redText(Messages.PURGE_SINGLE));
                return CommandResult.success();
            }

            File file = SpongeItemBank.instance().getAccountStorage().getFile(uuid);
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

        if (SpongeItemBank.instance().getMySQL() != null)
        {
            try
            {
                ResultSet rs = SpongeItemBank.instance().getMySQL().getConnection().getMetaData().getTables(null, null, null, new String[]{"TABLE"});
                while (rs.next())
                    if (rs.getString(3).startsWith("ib_"))//NOSONAR
                        SpongeItemBank.instance().getMySQL().querySQL("DROP TABLE " + rs.getString(3));
            }
            catch (SQLException | ClassNotFoundException e)//NOSONAR
            {
                source.sendMessage(TextUtils.redText(Messages.SQL_EX));
                return CommandResult.empty();
            }
        }
        else
            SpongeItemBank.instance().getAccountStorage().resetAll().forEach(file -> source.sendMessage(TextUtils.redText(Messages.purgeFileFail(file))));

        source.sendMessage(TextUtils.redText(Messages.PURGE_MULTIPLE));
        return CommandResult.empty();
    }
}

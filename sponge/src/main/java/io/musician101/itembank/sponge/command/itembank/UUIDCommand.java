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
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Arrays;

@SuppressWarnings("WeakerAccess")
public class UUIDCommand extends AbstractSpongeCommand
{
    public UUIDCommand()
    {
        super(Commands.UUID_NAME, Text.of(Commands.UUID_DESC), new SpongeCommandUsage(Arrays.asList(new SpongeCommandArgument(Commands.IB_CMD), new SpongeCommandArgument(Commands.UUID_NAME), new SpongeCommandArgument(Commands.PLAYER, Syntax.REQUIRED)), 1), new SpongeCommandPermissions(Permissions.UUID, true, TextUtils.redText(Messages.NO_PERMISSION), TextUtils.redText(Messages.PLAYER_CMD)));
    }

    @Nonnull
    @Override
    public CommandResult process(@Nonnull CommandSource source, @Nonnull String arguments)
    {
        String[] args = splitArgs(arguments);
        if (!testPermission(source))
            return CommandResult.empty();

        if (args.length > 0)
        {
            try
            {
                source.sendMessage(TextUtils.greenText(Messages.uuid(args[0], UUIDUtils.getUUIDOf(args[0]))));
                return CommandResult.success();
            }
            catch (IOException e)//NOSONAR
            {
                source.sendMessage(TextUtils.redText(Messages.UNKNOWN_EX));
                return CommandResult.empty();
            }
        }

        if (!(source instanceof Player))
        {
            source.sendMessage(TextUtils.redText(Messages.PLAYER_CMD));
            return CommandResult.empty();
        }

        Player player = (Player) source;
        source.sendMessage(TextUtils.redText(Messages.uuid(player.getName(), player.getUniqueId())));
        return CommandResult.success();
    }
}

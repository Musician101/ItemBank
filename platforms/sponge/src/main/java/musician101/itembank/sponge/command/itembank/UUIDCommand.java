package musician101.itembank.sponge.command.itembank;

import musician101.common.java.minecraft.command.AbstractCommandArgument.Syntax;
import musician101.common.java.minecraft.sponge.TextUtils;
import musician101.common.java.minecraft.sponge.command.AbstractSpongeCommand;
import musician101.common.java.minecraft.sponge.command.SpongeCommandArgument;
import musician101.common.java.minecraft.uuid.UUIDUtils;
import musician101.itembank.common.Reference.Commands;
import musician101.itembank.common.Reference.Messages;
import musician101.itembank.common.Reference.Permissions;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;

import javax.annotation.Nonnull;
import java.util.Arrays;

public class UUIDCommand extends AbstractSpongeCommand
{
    public UUIDCommand()
    {
        super(Commands.UUID_NAME, Commands.UUID_DESC, Arrays.asList(new SpongeCommandArgument(Commands.IB_CMD), new SpongeCommandArgument(Commands.UUID_NAME), new SpongeCommandArgument(Commands.PLAYER, Syntax.REQUIRED)), 1, Permissions.UUID, true, TextUtils.redText(Messages.NO_PERMISSION), TextUtils.redText(Messages.PLAYER_CMD));
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
            catch (Exception e)
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

package musician101.sponge.itembank.command.itembank;

import java.util.Arrays;
import javax.annotation.Nonnull;
import musician101.common.java.minecraft.sponge.TextUtils;
import musician101.common.java.minecraft.sponge.command.AbstractSpongeCommand;
import musician101.common.java.minecraft.sponge.command.SpongeCommandArgument;
import musician101.itembank.common.Reference.Commands;
import musician101.itembank.common.Reference.Messages;
import musician101.itembank.common.Reference.Permissions;
import musician101.sponge.itembank.SpongeItemBank;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;

public class ReloadCommand extends AbstractSpongeCommand
{
    public ReloadCommand()
    {
        super(Commands.RELOAD_NAME, Commands.RELOAD_DESC, Arrays.asList(new SpongeCommandArgument(Commands.IB_CMD), new SpongeCommandArgument(Commands.RELOAD_NAME)), 0, Permissions.RELOAD, false, TextUtils.redText(Messages.NO_PERMISSION), TextUtils.redText(Messages.PLAYER_CMD));
    }

    @Nonnull
    @Override
    public CommandResult process(@Nonnull CommandSource source, @Nonnull String arguments)
    {
        if (!testPermission(source))
            return CommandResult.empty();

        SpongeItemBank.config.reloadConfiguration();
        source.sendMessage(TextUtils.greenText(Messages.RELOAD_SUCCESS));
        return CommandResult.success();
    }
}

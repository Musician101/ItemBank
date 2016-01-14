package musician101.sponge.itembank.command.itembank;

import java.util.Arrays;
import java.util.Collections;
import javax.annotation.Nonnull;
import musician101.common.java.minecraft.sponge.TextUtils;
import musician101.common.java.minecraft.sponge.command.AbstractSpongeCommand;
import musician101.common.java.minecraft.sponge.command.SpongeCommandArgument;
import musician101.common.java.minecraft.sponge.command.SpongeHelpCommand;
import musician101.itembank.common.Reference;
import musician101.itembank.common.Reference.Commands;
import musician101.itembank.common.Reference.Messages;
import musician101.sponge.itembank.command.account.AccountExecutor;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;

public class IBExecutor extends AbstractSpongeCommand
{
    public IBExecutor()
    {
        super(Reference.NAME.toLowerCase(), Reference.DESCRIPTION, Collections.singletonList(new SpongeCommandArgument(Commands.IB_CMD)), 0, "", true, TextUtils.redText(Messages.NO_PERMISSION), TextUtils.redText(Messages.PLAYER_CMD), Arrays.asList(new PurgeExecutor(), new ReloadCommand(), new UUIDCommand()));
    }

    @Nonnull
	@Override
	public CommandResult process(@Nonnull CommandSource source, @Nonnull String arguments) throws CommandException
	{
        String[] args = splitArgs(arguments);
        if (args.length > 0)
        {
            if (args[0].equalsIgnoreCase(Commands.HELP))
                return new SpongeHelpCommand(this, source).process(source, moveArguments(args));

            for (AbstractSpongeCommand command : getSubCommands())
                if (command.getName().equalsIgnoreCase(args[0]))
                    return command.process(source, moveArguments(args));
        }

		source.sendMessage(new SpongeHelpCommand(this, source).getUsage(source));
        source.sendMessage(new SpongeHelpCommand(new AccountExecutor(), source).getUsage(source));
		return CommandResult.success();
	}
}

package io.musician101.itembank.sponge.command.itembank;

import io.musician101.common.java.minecraft.sponge.TextUtils;
import io.musician101.common.java.minecraft.sponge.command.AbstractSpongeCommand;
import io.musician101.common.java.minecraft.sponge.command.SpongeCommandArgument;
import io.musician101.common.java.minecraft.sponge.command.SpongeCommandPermissions;
import io.musician101.common.java.minecraft.sponge.command.SpongeCommandUsage;
import io.musician101.common.java.minecraft.sponge.command.SpongeHelpCommand;
import io.musician101.itembank.common.Reference;
import io.musician101.itembank.common.Reference.Commands;
import io.musician101.itembank.common.Reference.Messages;
import io.musician101.itembank.sponge.SpongeItemBank;
import io.musician101.itembank.sponge.command.account.AccountCommand;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;

public class IBCommand extends AbstractSpongeCommand
{
    public IBCommand()
    {
        super(Reference.NAME.toLowerCase(), Text.of(Reference.DESCRIPTION), new SpongeCommandUsage(Collections.singletonList(new SpongeCommandArgument(Commands.IB_CMD))), new SpongeCommandPermissions("", true, TextUtils.redText(Messages.NO_PERMISSION), TextUtils.redText(Messages.PLAYER_CMD)), Arrays.asList(new PurgeCommand(), new ReloadCommand(), new UUIDCommand()));
    }

    @Nonnull
    @Override
    public CommandResult process(@Nonnull CommandSource source, @Nonnull String arguments) throws CommandException
    {
        String[] args = splitArgs(arguments);
        if (args.length > 0)
        {
            if (args[0].equalsIgnoreCase(Commands.HELP))
                return new SpongeHelpCommand(this, source, SpongeItemBank.getPluginContainer()).process(source, moveArguments(args));

            for (AbstractSpongeCommand command : getSubCommands())
                if (command.getName().equalsIgnoreCase(args[0]))
                    return command.process(source, moveArguments(args));
        }

        new SpongeHelpCommand(this, source, SpongeItemBank.getPluginContainer()).process(source, moveArguments(args));
        //noinspection OptionalGetWithoutIsPresent
        source.sendMessage(new AccountCommand().getHelp(source).get());
        return CommandResult.success();
    }
}

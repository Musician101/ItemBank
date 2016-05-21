package io.musician101.itembank.spigot.command.itembank;

import io.musician101.common.java.minecraft.spigot.command.AbstractSpigotCommand;
import io.musician101.common.java.minecraft.spigot.command.SpigotCommandArgument;
import io.musician101.common.java.minecraft.spigot.command.SpigotCommandPermissions;
import io.musician101.common.java.minecraft.spigot.command.SpigotCommandUsage;
import io.musician101.common.java.minecraft.spigot.command.SpigotHelpCommand;
import io.musician101.itembank.common.Reference;
import io.musician101.itembank.common.Reference.Commands;
import io.musician101.itembank.common.Reference.Messages;
import io.musician101.itembank.spigot.SpigotItemBank;
import io.musician101.itembank.spigot.command.account.AccountCommand;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;

public class IBCommand extends AbstractSpigotCommand
{
    public IBCommand()
    {
        super(Reference.ID, Reference.DESCRIPTION, new SpigotCommandUsage(Collections.singletonList(new SpigotCommandArgument(Commands.IB_CMD))), new SpigotCommandPermissions("", false, Messages.NO_PERMISSION, Messages.PLAYER_CMD), Arrays.asList(new PurgeCommand(), new ReloadCommand(), new UUIDCommand()));
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args)
    {
        if (args.length > 0)
        {
            if (args[0].equalsIgnoreCase(Commands.HELP))
                return new SpigotHelpCommand<>(SpigotItemBank.instance(), this).onCommand(sender, moveArguments(args));

            for (AbstractSpigotCommand command : getSubCommands())
                if (command.getName().equalsIgnoreCase(args[0]))
                    return command.onCommand(sender, args);
        }

        new SpigotHelpCommand<>(SpigotItemBank.instance(), this).onCommand(sender, moveArguments(args));
        sender.sendMessage(new AccountCommand().getCommandHelpInfo());
        return true;
    }
}

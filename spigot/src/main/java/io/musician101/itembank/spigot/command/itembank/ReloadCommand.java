package io.musician101.itembank.spigot.command.itembank;

import io.musician101.common.java.minecraft.spigot.command.AbstractSpigotCommand;
import io.musician101.common.java.minecraft.spigot.command.SpigotCommandArgument;
import io.musician101.common.java.minecraft.spigot.command.SpigotCommandPermissions;
import io.musician101.common.java.minecraft.spigot.command.SpigotCommandUsage;
import io.musician101.itembank.common.Reference.Commands;
import io.musician101.itembank.common.Reference.Messages;
import io.musician101.itembank.common.Reference.Permissions;
import io.musician101.itembank.spigot.SpigotItemBank;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

@SuppressWarnings("WeakerAccess")
public class ReloadCommand extends AbstractSpigotCommand
{
    public ReloadCommand()
    {
        super(Commands.RELOAD_NAME, Commands.RELOAD_DESC, new SpigotCommandUsage(Arrays.asList(new SpigotCommandArgument(Commands.IB_CMD), new SpigotCommandArgument(Commands.RELOAD_NAME))), new SpigotCommandPermissions(Permissions.RELOAD, false, Messages.NO_PERMISSION, Messages.PLAYER_CMD));
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args)
    {
        if (!canSenderUseCommand(sender))
            return false;

        SpigotItemBank.instance().getPluginConfig().reload();
        sender.sendMessage(Messages.RELOAD_SUCCESS);
        return true;
    }
}

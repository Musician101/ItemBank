package musician101.itembank.spigot.command.itembank;

import musician101.common.java.minecraft.spigot.command.AbstractSpigotCommand;
import musician101.common.java.minecraft.spigot.command.SpigotCommandArgument;
import musician101.itembank.common.Reference.Commands;
import musician101.itembank.common.Reference.Messages;
import musician101.itembank.common.Reference.Permissions;
import musician101.itembank.spigot.SpigotItemBank;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class ReloadCommand extends AbstractSpigotCommand<SpigotItemBank>
{
    private final SpigotItemBank plugin;

    public ReloadCommand(SpigotItemBank plugin)
    {
        super(plugin, Commands.RELOAD_NAME, Commands.RELOAD_DESC, Arrays.asList(new SpigotCommandArgument(Commands.IB_CMD), new SpigotCommandArgument(Commands.RELOAD_NAME)), 0, Permissions.RELOAD, false, Messages.NO_PERMISSION, Messages.PLAYER_CMD);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args)
    {
        if (!canSenderUseCommand(sender))
            return false;

        plugin.getPluginConfig().reload();
        sender.sendMessage(Messages.RELOAD_SUCCESS);
        return true;
    }
}

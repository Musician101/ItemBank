package musician101.itembank.spigot.command.itembank;

import musician101.common.java.minecraft.command.AbstractCommandArgument.Syntax;
import musician101.common.java.minecraft.spigot.command.AbstractSpigotCommand;
import musician101.common.java.minecraft.spigot.command.SpigotCommandArgument;
import musician101.itembank.common.Reference.Commands;
import musician101.itembank.common.Reference.Messages;
import musician101.itembank.common.Reference.Permissions;
import musician101.itembank.spigot.util.IBUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class UUIDCommand extends AbstractSpigotCommand
{
    public UUIDCommand()
    {
        super(Commands.UUID_NAME, Commands.UUID_DESC, Arrays.asList(new SpigotCommandArgument(Commands.IB_CMD), new SpigotCommandArgument(Commands.UUID_NAME), new SpigotCommandArgument(Commands.PLAYER, Syntax.REQUIRED)), 0, Permissions.UUID, false, Messages.NO_PERMISSION, Messages.PLAYER_CMD);
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args)
    {
        if (!canSenderUseCommand(sender))
            return false;

        if (args.length > 0)
        {
            try
            {
                sender.sendMessage(Messages.uuid(args[0], IBUtils.getUUIDOf(args[0])));
                return true;
            }
            catch (Exception e)
            {
                sender.sendMessage(Messages.UNKNOWN_EX);
                return false;
            }
        }

        if (!(sender instanceof Player))
        {
            sender.sendMessage(ChatColor.RED + Messages.PLAYER_CMD);
            return false;
        }

        Player player = (Player) sender;
        sender.sendMessage(Messages.uuid(player.getName(), player.getUniqueId()));
        return true;
    }
}

package io.musician101.itembank.spigot.command.account;

import io.musician101.common.java.minecraft.command.AbstractCommandArgument.Syntax;
import io.musician101.common.java.minecraft.spigot.command.AbstractSpigotCommand;
import io.musician101.common.java.minecraft.spigot.command.SpigotCommandArgument;
import io.musician101.common.java.minecraft.spigot.command.SpigotCommandPermissions;
import io.musician101.common.java.minecraft.spigot.command.SpigotCommandUsage;
import io.musician101.common.java.minecraft.spigot.command.SpigotHelpCommand;
import io.musician101.common.java.minecraft.uuid.UUIDUtils;
import io.musician101.common.java.util.Utils;
import io.musician101.itembank.common.Reference.Commands;
import io.musician101.itembank.common.Reference.Messages;
import io.musician101.itembank.common.Reference.Permissions;
import io.musician101.itembank.spigot.SpigotItemBank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

public class AccountCommand extends AbstractSpigotCommand
{
    public AccountCommand()
    {
        super(Commands.ACCOUNT_NAME, Commands.ACCOUNT_DESC, new SpigotCommandUsage(Arrays.asList(new SpigotCommandArgument("/" + Commands.ACCOUNT_NAME), new SpigotCommandArgument(Commands.getAccountArg(Commands.PAGE), Syntax.OPTIONAL), new SpigotCommandArgument(Commands.getAccountArg(Commands.PLAYER), Syntax.OPTIONAL), new SpigotCommandArgument(Commands.getAccountArg(Commands.PAGE), Syntax.OPTIONAL))), new SpigotCommandPermissions("", true, Messages.NO_PERMISSION, Messages.PLAYER_CMD));
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args)//NOSONAR
    {
        if (!(sender instanceof Player))
        {
            sender.sendMessage(ChatColor.RED + Messages.PLAYER_CMD);
            return new SpigotHelpCommand<>(SpigotItemBank.instance(), this).onCommand(sender, moveArguments(args));
        }

        Player player = (Player) sender;
        if (!player.hasPermission(Permissions.ACCOUNT) || !player.hasPermission(Permissions.ADMIN))
        {
            player.sendMessage(ChatColor.RED + Messages.NO_PERMISSION);
            return false;
        }

        int page = 1;
        UUID uuid = player.getUniqueId();
        World world = player.getWorld();
        if (args.length > 0)
        {
            if (args[0].equalsIgnoreCase(Commands.HELP))
                return new SpigotHelpCommand<>(SpigotItemBank.instance(), this).onCommand(sender, moveArguments(args));

            for (String arg : args)
            {
                if (arg.contains(":"))
                {
                    String[] argSplit = arg.split(":");
                    if (argSplit.length > 0)//NOSONAR
                    {
                        if (argSplit[0].equalsIgnoreCase(Commands.PAGE))
                        {
                            if (Utils.isInteger(argSplit[1]))
                                page = Integer.parseInt(argSplit[1]);

                            if (page <= 0)
                                page = 1;
                        }

                        if (argSplit[0].equalsIgnoreCase(Commands.PLAYER))
                        {
                            try
                            {
                                uuid = UUIDUtils.getUUIDOf(argSplit[1]);
                            }
                            catch (IOException e)//NOSONAR
                            {
                                player.sendMessage(Messages.UNKNOWN_EX);
                                return false;
                            }

                            if (uuid == null)
                            {
                                player.sendMessage(Messages.PLAYER_DNE);
                                return false;
                            }
                        }

                        if (argSplit[0].equalsIgnoreCase(Commands.WORLD))
                        {
                            world = Bukkit.getWorld(argSplit[1]);
                            if (world == null)
                            {
                                player.sendMessage(Messages.ACCOUNT_WORLD_DNE);
                                return false;
                            }
                        }
                    }
                }
            }
        }

        if (!canAccessPage(player, uuid, page, world))
        {
            player.sendMessage(ChatColor.RED + Messages.NO_PERMISSION);
            return false;
        }

        return SpigotItemBank.instance().getAccountStorage().openInv(player, uuid, world, page);
    }

    private boolean canAccessPage(Player player, UUID owner, int page, World world)//NOSONAR
    {
        if (player.hasPermission(Permissions.ADMIN))
            return true;

        if (player.getUniqueId() != owner)
            return player.hasPermission(Permissions.PLAYER);

        return SpigotItemBank.instance().getPluginConfig().isMultiWorldStorageEnabled() && player.getWorld() != world && (player.hasPermission(Permissions.WORLD + "." + world.getName()) || player.hasPermission(Permissions.WORLD)) || SpigotItemBank.instance().getPluginConfig().getPageLimit() > 0 && (player.hasPermission(Permissions.PAGE) || page < SpigotItemBank.instance().getPluginConfig().getPageLimit());//NOSONAR
    }
}

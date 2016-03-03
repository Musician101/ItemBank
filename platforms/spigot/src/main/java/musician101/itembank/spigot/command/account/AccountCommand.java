package musician101.itembank.spigot.command.account;

import musician101.common.java.minecraft.command.AbstractCommandArgument.Syntax;
import musician101.common.java.minecraft.spigot.command.AbstractSpigotCommand;
import musician101.common.java.minecraft.spigot.command.SpigotCommandArgument;
import musician101.common.java.minecraft.spigot.command.SpigotHelpCommand;
import musician101.common.java.minecraft.uuid.UUIDUtils;
import musician101.common.java.util.Utils;
import musician101.itembank.common.Reference;
import musician101.itembank.common.Reference.Commands;
import musician101.itembank.common.Reference.Messages;
import musician101.itembank.common.Reference.Permissions;
import musician101.itembank.spigot.SpigotItemBank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.UUID;

public class AccountCommand extends AbstractSpigotCommand<SpigotItemBank>
{
    public AccountCommand(SpigotItemBank plugin)
    {
        super(plugin, Commands.ACCOUNT_NAME, Commands.ACCOUNT_DESC, Arrays.asList(new SpigotCommandArgument("/" + Commands.ACCOUNT_NAME), new SpigotCommandArgument(Commands.getAccountArg(Commands.PAGE), Syntax.OPTIONAL), new SpigotCommandArgument(Commands.getAccountArg(Commands.PLAYER), Syntax.OPTIONAL), new SpigotCommandArgument(Commands.getAccountArg(Commands.PAGE), Syntax.OPTIONAL)), 0, "", true, Messages.NO_PERMISSION, Messages.PLAYER_CMD);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args)
    {
        if (!(sender instanceof Player))
        {
            sender.sendMessage(ChatColor.RED + Messages.PLAYER_CMD);
            return new SpigotHelpCommand<>(plugin, this).onCommand(sender, moveArguments(args));
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
            {
                String ends = ChatColor.DARK_GREEN + Commands.HEADER_ENDS;
                String middle = ChatColor.RESET + Reference.NAME + " " + Reference.VERSION;
                player.sendMessage(ends + middle + ends);
                return new SpigotHelpCommand<>(plugin, this).onCommand(sender, moveArguments(args));
            }

            for (String arg : args)
            {
                if (arg.contains(":"))
                {
                    String[] argSplit = arg.split(":");
                    if (argSplit.length > 0)
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
                            catch (Exception e)
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

        return plugin.getAccountStorage().openInv(player, uuid, world, page);
    }

    private boolean canAccessPage(Player player, UUID owner, int page, World world)
    {
        if (player.hasPermission(Permissions.ADMIN))
            return true;

        if (player.getUniqueId() != owner)
            return player.hasPermission(Permissions.PLAYER);

        if (plugin.getPluginConfig().isMultiWorldStorageEnabled() && player.getWorld() != world)
            if (player.hasPermission(Permissions.WORLD + "." + world.getName()) || player.hasPermission(Permissions.WORLD))
                return true;

        if (plugin.getPluginConfig().getPageLimit() > 0)
            if (player.hasPermission(Permissions.PAGE) || page < plugin.getPluginConfig().getPageLimit())
                return true;

        return false;
    }
}

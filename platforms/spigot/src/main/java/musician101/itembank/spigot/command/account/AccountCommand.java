package musician101.itembank.spigot.command.account;

import musician101.common.java.minecraft.command.AbstractCommandArgument.Syntax;
import musician101.common.java.minecraft.spigot.command.AbstractSpigotCommand;
import musician101.common.java.minecraft.spigot.command.SpigotCommandArgument;
import musician101.common.java.minecraft.spigot.command.SpigotHelpCommand;
import musician101.itembank.common.Reference.Commands;
import musician101.itembank.common.Reference.Messages;
import musician101.itembank.spigot.SpigotItemBank;
import musician101.itembank.spigot.util.IBUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.UUID;

public class AccountCommand extends AbstractSpigotCommand
{
    private final SpigotItemBank plugin;

    public AccountCommand(SpigotItemBank plugin)
    {
        super(Commands.ACCOUNT_NAME, Commands.ACCOUNT_DESC, Arrays.asList(new SpigotCommandArgument("/account"), new SpigotCommandArgument("page:<page>", Syntax.OPTIONAL), new SpigotCommandArgument("player:<player>", Syntax.OPTIONAL), new SpigotCommandArgument("world:<world>")), 0, "itembank.account", true, Messages.NO_PERMISSION, Messages.PLAYER_CMD);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args)
    {
        //TODO need to check if this has basic permission checks
        if (!(sender instanceof Player))
        {
            sender.sendMessage(Messages.PLAYER_CMD);
            return new SpigotHelpCommand(this).onCommand(sender, moveArguments(args));
        }

        int page = 1;
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        World world = player.getWorld();
        if (args.length > 0)
        {
            if (args[0].equalsIgnoreCase("help"))
            {
                player.sendMessage(Messages.HEADER);
                player.sendMessage(getCommandHelpInfo());
                player.sendMessage(getUsage() + ChatColor.RESET + " page:<page> player:<player> world:<world> " + ChatColor.RED + Messages.ACCOUNT_DESC2);
                return true;
            }

            for (String arg : args)
            {
                if (arg.contains(":"))
                {
                    String[] argSplit = arg.split(":");
                    if (argSplit.length > 0)
                    {
                        if (argSplit[0].equalsIgnoreCase("page") && (player.hasPermission("itembank.account") || player.hasPermission("itembank.account.admin") || player.hasPermission("itembank.account.world")))
                        {
                            if (IBUtils.isNumber(argSplit[1]))
                                page = Integer.parseInt(argSplit[1]);

                            if (page == 0 && !player.hasPermission("itembank.account.admin"))
                                page = 1;
                        }
                        else if (argSplit[0].equalsIgnoreCase("player") && player.hasPermission("itembank.account.admin"))
                        {
                            try
                            {
                                uuid = IBUtils.getUUIDOf(argSplit[1]);
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
                        else if (argSplit[0].equalsIgnoreCase("world") && (player.hasPermission("itembank.account.world") || player.hasPermission("itembank.account.admin")))
                        {
                            world = Bukkit.getWorld(argSplit[1]);
                            if (world == null)
                            {
                                player.sendMessage("World does not exist.");
                                return false;
                            }
                        }
                    }
                }
            }
        }

        if (player.getUniqueId() == uuid)
        {
            if (plugin.getEconomy() != null)
            {
                if (!plugin.getEconomy().withdrawPlayer(Bukkit.getOfflinePlayer(uuid), plugin.getPluginConfig().getTransactionCost()).transactionSuccess())
                {
                    player.sendMessage(Messages.ACCOUNT_ECON_FAIL);
                    return false;
                }

                player.sendMessage(Messages.ACCOUNT_ECON_SUCCESS.replace("$", "$" + plugin.getPluginConfig().getTransactionCost()));
            }
        }

        return plugin.getAccountStorage().openInv(player, uuid, world, page);
    }
}

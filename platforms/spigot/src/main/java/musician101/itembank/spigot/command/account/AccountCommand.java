package musician101.itembank.spigot.command.account;

import java.util.Arrays;
import java.util.UUID;

import musician101.itembank.spigot.ItemBank;
import musician101.itembank.spigot.command.AbstractSpigotCommand;
import musician101.itembank.spigot.command.HelpCommand;
import musician101.itembank.spigot.lib.Messages;
import musician101.itembank.spigot.util.IBUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AccountCommand extends AbstractSpigotCommand
{
	public AccountCommand(ItemBank plugin)
	{
		super(plugin, "account", Messages.ACCOUNT_DESC, Arrays.asList("/account"), "itembank.account", true, null);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] args)
	{
		if (!(sender instanceof Player))
		{
			sender.sendMessage(Messages.PLAYER_CMD);
			return new HelpCommand(plugin, this).onCommand(sender, moveArguments(args));
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
							uuid = null;
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
		
		return IBUtils.openInv(plugin, player, world.getName(), uuid, page);
	}
}
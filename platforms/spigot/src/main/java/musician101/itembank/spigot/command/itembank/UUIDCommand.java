package musician101.itembank.spigot.command.itembank;

import java.util.Arrays;

import musician101.itembank.spigot.SpigotItemBank;
import musician101.itembank.spigot.lib.Messages;
import musician101.itembank.spigot.util.IBUtils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UUIDCommand extends AbstractSpigotCommand
{
	public UUIDCommand(SpigotItemBank plugin)
	{
		super(plugin, "uuid", "Get a player's UUID.", Arrays.asList("/itembank", "uuid", "<player>"), "itembank.uuid", true, null);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] args)
	{		
		if (args.length > 0)
		{
			if (!sender.hasPermission(getPermission()))
			{
				sender.sendMessage(Messages.NO_PERMISSION);
				return false;
			}
			
			try
			{
				sender.sendMessage(Messages.PREFIX + args[0] + "'s UUID: " + IBUtils.getUUIDOf(args[0]));
				return true;
			}
			catch (Exception e)
			{
				sender.sendMessage(Messages.UNKNOWN_EX);
				return false;
			}
		}
		
		if (!canSenderUseCommand(sender))
			return false;
		
		sender.sendMessage(Messages.PREFIX + sender.getName() + "'s UUID: " + ((Player) sender).getUniqueId().toString());
		return true;
	}
}

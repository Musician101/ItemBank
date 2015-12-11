package musician101.itembank.spigot.command.itembank;

import java.util.Arrays;
import musician101.common.java.minecraft.command.AbstractCommandArgument.Syntax;
import musician101.common.java.minecraft.spigot.command.AbstractSpigotCommand;
import musician101.common.java.minecraft.spigot.command.SpigotCommandArgument;
import musician101.itembank.spigot.lib.Messages;
import musician101.itembank.spigot.util.IBUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UUIDCommand extends AbstractSpigotCommand
{
	public UUIDCommand()
	{
		super("uuid", "Get a player's UUID.", Arrays.asList(new SpigotCommandArgument("/itembank"), new SpigotCommandArgument("uuid"), new SpigotCommandArgument("player", Syntax.REQUIRED)), 1, "itembank.uuid", true, Messages.NO_PERMISSION, Messages.PLAYER_CMD);
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

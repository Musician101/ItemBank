package musician101.itembank.spigot.command.itembank;

import java.util.Arrays;

import musician101.itembank.spigot.SpigotItemBank;
import musician101.itembank.spigot.command.AbstractSpigotCommand;
import musician101.itembank.spigot.lib.Messages;

import org.bukkit.command.CommandSender;

public class ReloadCommand extends AbstractSpigotCommand
{
	public ReloadCommand(SpigotItemBank plugin)
	{
		super(plugin, "reload", "Reload the plugin's config file.", Arrays.asList("/itembank", "reload"), "itembank.reload", false, null);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] args)
	{
		if (!canSenderUseCommand(sender))
			return false;
		
		plugin.getPluginConfig().reloadConfiguration();
		sender.sendMessage(Messages.RELOAD_SUCCESS);
		return true;
	}
}

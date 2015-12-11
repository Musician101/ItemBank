package musician101.itembank.spigot.command.itembank;

import java.util.Arrays;

import musician101.common.java.minecraft.spigot.command.AbstractSpigotCommand;
import musician101.common.java.minecraft.spigot.command.SpigotCommandArgument;
import musician101.itembank.spigot.SpigotItemBank;
import musician101.itembank.spigot.lib.Messages;

import org.bukkit.command.CommandSender;

public class ReloadCommand extends AbstractSpigotCommand
{
	SpigotItemBank plugin;

	public ReloadCommand(SpigotItemBank plugin)
	{
		super("reload", "Reload the plugin's config file.", Arrays.asList(new SpigotCommandArgument("/itembank"), new SpigotCommandArgument("reload")), 0, "itembank.reload", false, Messages.NO_PERMISSION, Messages.PLAYER_CMD);
        this.plugin = plugin;
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

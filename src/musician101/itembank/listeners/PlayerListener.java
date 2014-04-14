package musician101.itembank.listeners;

import java.io.File;

import musician101.itembank.ItemBank;
import musician101.itembank.lib.Messages;
import musician101.itembank.util.IBUtils;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener
{
	ItemBank plugin;
	
	public PlayerListener(ItemBank plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerLogin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		File playerFile = new File(plugin.playerData, player.getName().toLowerCase() + "." + plugin.config.fileType);
		IBUtils.createPlayerFile(playerFile);
		if (!playerFile.exists())
			player.sendMessage(Messages.PLAYER_JOIN_FILE_FAIL);
	}
}

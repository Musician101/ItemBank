package musician101.itembank.listeners;

import musician101.itembank.ItemBank;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerListener implements Listener
{
	ItemBank plugin;
	
	public PlayerListener(ItemBank plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event)
	{
		Player player = event.getPlayer();
		if (plugin.config.uuids.getPlayer(player.getName()) != null)
			plugin.config.uuids.removePlayer(player);
		
		plugin.config.uuids.addPlayer(player);
	}
}

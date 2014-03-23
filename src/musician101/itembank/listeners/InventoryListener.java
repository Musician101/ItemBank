package musician101.itembank.listeners;

import java.io.FileNotFoundException;
import java.io.IOException;

import musician101.itembank.ItemBank;
import musician101.itembank.lib.Constants;
import musician101.itembank.util.IBUtils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryListener implements Listener
{
	ItemBank plugin;
	
	public InventoryListener(ItemBank plugin)
	{
		this.plugin = plugin;
	}
	
	// player.getName() and playerName are not always the same.
	public void saveAccount(Player player, String playerName, Inventory topInv, Inventory playerInv, int page)
	{
		Inventory account = null;
		try
		{
			account = IBUtils.getAccount(plugin, playerName, page);
		}
		catch (FileNotFoundException e)
		{
			player.sendMessage(Constants.NO_FILE_EX);
			player.getInventory().setContents(playerInv.getContents());
			return;
		}
		catch (IOException e)
		{
			player.sendMessage(Constants.IO_EX);
			player.getInventory().setContents(playerInv.getContents());
			return;
		}
		catch (InvalidConfigurationException e)
		{
			player.sendMessage(Constants.YAML_EX);
			player.getInventory().setContents(playerInv.getContents());
			return;
		}
		
		
		account.setContents(topInv.getContents());
		try
		{
			IBUtils.saveAccount(plugin, playerName, account, page);
		}
		catch (FileNotFoundException e)
		{
			player.sendMessage(Constants.NO_FILE_EX);
			player.getInventory().setContents(playerInv.getContents());
			return;
		}
		catch (IOException e)
		{
			player.sendMessage(Constants.IO_EX);
			player.getInventory().setContents(playerInv.getContents());
			return;
		}
		catch (InvalidConfigurationException e)
		{
			player.sendMessage(Constants.YAML_EX);
			player.getInventory().setContents(playerInv.getContents());
			return;
		}
		
		player.sendMessage(Constants.PREFIX + "Accout updated.");
	}
	
	@EventHandler
	public void onItemClick(InventoryClickEvent event)
	{
		if (event.getRawSlot() == event.getSlot()) return;
		Inventory account = event.getView().getTopInventory();
		Inventory clickedInv = event.getInventory();
		Player player = (Player) event.getWhoClicked();
		ItemStack item = event.getCurrentItem();
		String name = player.getName() + "'s Account - Page";
		if (player.hasPermission(Constants.EXEMPT_PERM))
			return;
		
		if (!account.getName().contains(name) && !clickedInv.getName().contains(name))
			return;
		
		int page = Integer.valueOf(account.getName().substring(account.getName().indexOf("-")).replaceAll("\\D+", ""));
		if (plugin.config.pageLimit > 0 && plugin.config.pageLimit < page)
		{
			event.setCancelled(true);
			player.sendMessage(Constants.PREFIX + "You cannot add items to this page.");
			player.closeInventory();
			return;
		}
		
		if (item == null)
			return;
		
		String blacklistPath = item.getType().toString().toLowerCase() + "." + item.getDurability();
		int amountInAccount = IBUtils.getAmount(account, item.getType(), item.getDurability());
		int newAmount = amountInAccount + item.getAmount();
		if (plugin.config.blacklist.containsKey(blacklistPath))
		{
			int maxAmount = plugin.config.blacklist.get(blacklistPath);
			if (maxAmount == 0)
			{
				event.setCancelled(true);
				player.sendMessage(Constants.PREFIX + "This item is non depositable.");
				player.closeInventory();
			}
			else if (maxAmount == amountInAccount)
			{
				event.setCancelled(true);
				player.sendMessage(Constants.PREFIX + "You are unable to do add this item to your account.");
				player.closeInventory();
			}
			else if (maxAmount < newAmount)
			{
				event.setCancelled(true);
				player.sendMessage(new String[]{Constants.PREFIX + "The stack you selected puts you over the limit. Please split the stack and try again.", Constants.PREFIX + "Maximum: " + maxAmount + ", Amount in account: " + amountInAccount});
				player.closeInventory();
			}
		}
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event)
	{
		Inventory inv = event.getView().getTopInventory();
		Player player = (Player) event.getPlayer();
		int page = 1;
		if (!inv.getName().contains(player.getName() + "'s Account - Page"))
		{
			if (!player.hasPermission(Constants.EXEMPT_PERM))
				return;
			
			//To prevent spamming of the console should an player with Exempt permission node open ANY inventory.
			try
			{
				for (OfflinePlayer p : Bukkit.getOfflinePlayers())
				{
					if (p.getName().contains(inv.getName().substring(0, inv.getName().indexOf("'"))))
					{
						page = Integer.valueOf(inv.getName().substring(inv.getName().indexOf("-")).replaceAll("\\D+", ""));
						saveAccount(player, p.getName(), inv, player.getInventory(), page);
						return;
					}
				}
			}
			catch (StringIndexOutOfBoundsException e)
			{
				return;
			}
		}
		
		page = Integer.valueOf(inv.getName().substring(inv.getName().indexOf("-")).replaceAll("\\D+", ""));
		saveAccount(player, player.getName(), inv, player.getInventory(), page);
	}
}

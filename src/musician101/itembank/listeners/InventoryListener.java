package musician101.itembank.listeners;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import musician101.itembank.ItemBank;
import musician101.itembank.lib.Constants;
import musician101.itembank.lib.Messages;
import musician101.itembank.util.IBUtils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.simple.parser.ParseException;

public class InventoryListener implements Listener
{
	ItemBank plugin;
	
	public InventoryListener(ItemBank plugin)
	{
		this.plugin = plugin;
	}
	
	// player.getUniqueId() and UUID are not always the same.
	public void saveAccount(Player player, String worldName, String uuid, Inventory topInv, Inventory playerInv, int page)
	{
		Inventory account = null;
		try
		{
			account = IBUtils.getAccount(plugin, worldName, uuid, page);
		}
		catch (FileNotFoundException e)
		{
			player.sendMessage(Messages.NO_FILE_EX);
			player.getInventory().setContents(playerInv.getContents());
			return;
		}
		catch (IOException e)
		{
			player.sendMessage(Messages.IO_EX);
			player.getInventory().setContents(playerInv.getContents());
			return;
		}
		catch (InvalidConfigurationException | ParseException e)
		{
			player.sendMessage(Messages.YAML_PARSE_EX);
			player.getInventory().setContents(playerInv.getContents());
			return;
		}
		catch (SQLException e)
		{
			player.sendMessage(Messages.SQL_EX);
			player.getInventory().setContents(playerInv.getContents());
			return;
		}
		
		account.setContents(topInv.getContents());
		try
		{
			IBUtils.saveAccount(plugin, worldName, uuid, account, page);
		}
		catch (FileNotFoundException e)
		{
			player.sendMessage(Messages.NO_FILE_EX);
			player.getInventory().setContents(playerInv.getContents());
			return;
		}
		catch (IOException e)
		{
			player.sendMessage(Messages.IO_EX);
			player.getInventory().setContents(playerInv.getContents());
			return;
		}
		catch (InvalidConfigurationException | ParseException e)
		{
			player.sendMessage(Messages.YAML_PARSE_EX);
			player.getInventory().setContents(playerInv.getContents());
			return;
		}
		catch (SQLException e)
		{
			player.sendMessage(Messages.SQL_EX);
			player.getInventory().setContents(playerInv.getContents());
			return;
		}
		
		player.sendMessage(Messages.ACCOUNT_UPDATED);
	}
	
	@EventHandler
	public void onItemClick(InventoryClickEvent event)
	{
		if (event.getRawSlot() == event.getSlot()) return;
		Inventory account = event.getView().getTopInventory();
		Inventory clickedInv = event.getInventory();
		Player player = (Player) event.getWhoClicked();
		ItemStack item = event.getCurrentItem();
		String name = player.getName() + " - " + Messages.PAGE;
		if (player.hasPermission(Constants.EXEMPT_PERM))
			return;
		
		if (!account.getName().contains(name) && !clickedInv.getName().contains(name))
			return;
		
		int page = Integer.valueOf(account.getName().substring(account.getName().indexOf("-")).replaceAll("\\D+", ""));
		if (plugin.config.pageLimit > 0 && plugin.config.pageLimit < page)
		{
			event.setCancelled(true);
			player.sendMessage(Messages.ACCOUNT_ILLEGAL_PAGE);
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
				player.sendMessage(Messages.ACCOUNT_ILLEGAL_ITEM);
				player.closeInventory();
			}
			else if (maxAmount == amountInAccount)
			{
				event.setCancelled(true);
				player.sendMessage(Messages.ACCOUNT_ILLEGAL_AMOUNT);
				player.closeInventory();
			}
			else if (maxAmount < newAmount)
			{
				event.setCancelled(true);
				player.sendMessage(new String[]{Messages.ACCOUNT_ILLEGAL_STACK_EXPLAIN,
						Messages.PREFIX + Messages.ACCOUNT_ILLEGAL_STACK_MAXIMUM + ": " + maxAmount + ", " + Messages.ACCOUNT_ILLEGAL_AMOUNT + ": " + amountInAccount});
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
		if (!inv.getName().contains(player.getName() + " - " + Messages.PAGE))
		{
			if (!player.hasPermission(Constants.EXEMPT_PERM))
				return;
			
			//To prevent spamming of the console should an player with Exempt permission node open ANY inventory.
			try
			{	
				page = Integer.valueOf(inv.getName().substring(inv.getName().indexOf("-")).replaceAll("\\D+", ""));
				saveAccount(player, Bukkit.getWorlds().get(0).getName(), plugin.config.uuids.getOfflinePlayer(inv.getName().substring(0, inv.getName().indexOf(" "))).getUniqueId().toString(), inv, player.getInventory(), page);
				return;
			}
			catch (NumberFormatException | StringIndexOutOfBoundsException e)
			{
				return;
			}
		}
		
		page = Integer.valueOf(inv.getName().substring(inv.getName().indexOf("-")).replaceAll("\\D+", ""));
		saveAccount(player, IBUtils.getWorldName(plugin, player), player.getUniqueId().toString(), inv, player.getInventory(), page);
	}
}

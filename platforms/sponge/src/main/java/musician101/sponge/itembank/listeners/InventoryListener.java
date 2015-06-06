package musician101.sponge.itembank.listeners;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import musician101.sponge.itembank.ItemBank;
import musician101.sponge.itembank.lib.Reference.Constants;
import musician101.sponge.itembank.lib.Reference.Messages;
import musician101.sponge.itembank.util.IBUtils;

import org.json.simple.parser.ParseException;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.event.inventory.InventoryClickEvent;
import org.spongepowered.api.event.inventory.InventoryCloseEvent;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.event.Subscribe;

public class InventoryListener
{
	// player.getUniqueId() and UUID are not always the same.
	public void saveAccount(Player player, String worldName, String uuid, Inventory topInv, Inventory playerInv, int page)
	{
		//TODO need a method to transfer inventory contents from one inventory to another
		Inventory account = null;
		try
		{
			account = IBUtils.getAccount(worldName, uuid, page);
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
		catch (ParseException e)
		{
			player.sendMessage(Messages.YAML_PARSE_EX);
			player.getInventory().setContents(playerInv.getContents());
			return;
		}
		
		account.setContents(topInv.getContents());
		try
		{
			IBUtils.saveAccount(worldName, uuid, account, page);
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
		catch (ParseException e)
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
	
	@Subscribe
	public void onItemClick(InventoryClickEvent event)
	{
		//TODO missing methods
		if (event.getRawSlot() == event.getSlot()) return;
		Inventory account = event.getView().getTopInventory();
		Inventory clickedInv = event.getInventory();
		Player player = (Player) event.getWhoClicked();
		ItemStack item = event.getCurrentItem();
		String name = player.getName() + " - " + Messages.ACCOUNT_PAGE;
		if (player.hasPermission(Constants.EXEMPT_PERM))
			return;
		
		if (!account.getName().contains(name) && !clickedInv.getName().contains(name))
			return;
		
		int page = Integer.valueOf(account.getName().substring(account.getName().indexOf("-")).replaceAll("\\D+", ""));
		if (ItemBank.getConfig().pageLimit > 0 && ItemBank.getConfig().pageLimit < page)
		{
			event.setCancelled(true);
			player.sendMessage(Messages.ACCOUNT_ILLEGAL_PAGE);
			player.closeInventory();
			return;
		}
		
		if (item == null)
			return;
		
		String itemlistPath = item.getItem().toString().toLowerCase() + "." + item.getDamage();
		int amountInAccount = IBUtils.getAmount(account, item.getItem(), item.getDamage());
		int newAmount = amountInAccount + item.getQuantity();
		if (ItemBank.getConfig().itemlist.containsKey(itemlistPath) && !ItemBank.getConfig().isWhitelist)
		{
			int maxAmount = ItemBank.getConfig().itemlist.get(itemlistPath);
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
		else if (!ItemBank.getConfig().itemlist.containsKey(itemlistPath) && ItemBank.getConfig().isWhitelist)
		{
			event.setCancelled(true);
			player.sendMessage(Messages.ACCOUNT_ILLEGAL_ITEM);
			player.closeInventory();
		}
	}
	
	@Subscribe
	public void onInventoryClose(InventoryCloseEvent event)
	{
		//TODO missing methods
		Inventory inv = event.getView().getTopInventory();
		Player player = (Player) event.getPlayer();
		int page = 1;
		if (!inv.getName().contains(player.getName() + " - " + Messages.ACCOUNT_PAGE))
		{
			if (!player.hasPermission(Constants.EXEMPT_PERM))
				return;
			
			//To prevent spamming of the console should an player with Exempt permission node open ANY inventory.
			try
			{	
				page = Integer.valueOf(inv.getName().substring(inv.getName().indexOf("-")).replaceAll("\\D+", ""));
				saveAccount(player, IBUtils.getWorlds().get(0).getName(), player.getUniqueId().toString(), inv, player.getInventory(), page);
				return;
			}
			catch (NumberFormatException | StringIndexOutOfBoundsException e)
			{
				return;
			}
		}
		
		page = Integer.valueOf(inv.getName().substring(inv.getName().indexOf("-")).replaceAll("\\D+", ""));
		saveAccount(player, IBUtils.getWorldName(player), player.getUniqueId().toString(), inv, player.getInventory(), page);
	}
}

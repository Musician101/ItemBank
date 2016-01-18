package musician101.itembank.spigot.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

import musician101.itembank.common.AbstractAccountPage;
import musician101.itembank.spigot.SpigotItemBank;
import musician101.itembank.spigot.config.SpigotConfig;
import musician101.itembank.spigot.lib.Messages;

import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.simple.parser.ParseException;

public class AccountPage extends AbstractAccountPage<InventoryCloseEvent, Inventory, Player, World> implements Listener
{
	private final SpigotItemBank plugin;

	public AccountPage(SpigotItemBank plugin, Player viewer, UUID owner, World world, int page)
	{
        super(viewer, owner, world, page);
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
    @Override
	public void onInventoryClose(InventoryCloseEvent event)
	{
		Player player = (Player) event.getPlayer();
		if (player.getUniqueId() != viewer.getUniqueId())
			return;
		
		Inventory inv = event.getView().getTopInventory();
		SpigotConfig config = plugin.getPluginConfig();
		int pageLimit = config.getPageLimit();
		if (((pageLimit > 0 && pageLimit < page) || page == 0) && !player.hasPermission("itembank.account.admin"))
		{
			for (ItemStack item : inv.getContents())
				player.getWorld().dropItem(player.getLocation(), item);
			
			player.sendMessage(Messages.ACCOUNT_ILLEGAL_PAGE);
			return;
		}
		
		boolean hasIllegalItems = false;
		boolean hasIllegalAmount = false;
		for (int x = 0; x < inv.getSize(); x++)
		{
			if (inv.getItem(x) != null)
			{
				ItemStack item = inv.getItem(x);
				int itemAmount = IBUtils.getAmount(inv, item.getType(), item.getDurability());
				if (config.getItem(item.getType(), item.getDurability()) != null && !config.isWhitelist())
				{
					int maxAmount = config.getItem(item.getType(), item.getDurability()).getAmount();
					if (maxAmount == 0)
					{
						player.getWorld().dropItem(player.getLocation(), item);
						inv.setItem(x, null);
						hasIllegalItems = true;
					}
					else if (maxAmount < itemAmount)
					{
						int amount = itemAmount;
						while (maxAmount < amount)
						{
							int maxStackSize = item.getType().getMaxStackSize();
							if (maxStackSize < amount)
							{
								player.getWorld().dropItem(player.getLocation(), item);
								inv.setItem(x, null);
								amount -= maxStackSize;
							}
							else
							{
								ItemStack removeItem = item.clone();
								removeItem.setAmount(amount - maxAmount);
								if (inv.getItem(x) == null)
								{
									int slot = 0;
									for (int y = 0; y < inv.getSize(); y++)
										if (inv.getItem(y) != null)
											if (inv.getItem(y).getDurability() == item.getDurability())
												slot = y;
									
									ItemStack is = inv.getItem(slot);
									is.setAmount(item.getAmount() - removeItem.getAmount());
									inv.setItem(slot, is);
								}
								else
									inv.getItem(x).setAmount(item.getAmount() - removeItem.getAmount());
								
								player.getWorld().dropItem(player.getLocation(), removeItem);
								amount -= removeItem.getAmount();
							}
						}
						
						hasIllegalAmount = true;
					}
				}
				else if (config.getItem(item.getType(), item.getDurability()) == null && config.isWhitelist())
				{
					player.getWorld().dropItem(player.getLocation(), item);
					inv.setItem(x, null);
					hasIllegalItems = true;
				}
			}
		}
		//TODO add permissions to plugin.yml?
		if (hasIllegalItems)
			player.sendMessage(Messages.ACCOUNT_ILLEGAL_ITEM);
		
		if (hasIllegalAmount)
			player.sendMessage(Messages.ACCOUNT_ILLEGAL_AMOUNT);
		
		saveAccount(player, worldName, owner, inv, player.getInventory(), page);
		HandlerList.unregisterAll(this);
	}
	
	// player.getUniqueId() and UUID are not always the same.
    @Override
	protected void saveAccount(Player player, World world, UUID uuid, Inventory topInv, Inventory playerInv, int page)
	{
		Inventory account;
		try
		{
			account = IBUtils.getAccount(plugin, world, uuid, page);
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
		catch (ClassNotFoundException | SQLException e)
		{
			player.sendMessage(Messages.SQL_EX);
			player.getInventory().setContents(playerInv.getContents());
			return;
		}
		
		account.setContents(topInv.getContents());
		try
		{
			IBUtils.saveAccount(plugin, world, uuid, account, page);
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
		catch (ClassNotFoundException | SQLException e)
		{
			player.sendMessage(Messages.SQL_EX);
			player.getInventory().setContents(playerInv.getContents());
			return;
		}
		
		player.sendMessage(Messages.ACCOUNT_UPDATED);
	}
}

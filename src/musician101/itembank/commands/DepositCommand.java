package musician101.itembank.commands;

import java.io.File;

import musician101.itembank.ItemBank;
import musician101.itembank.lib.Constants;
import musician101.itembank.util.Inventory;
import musician101.itembank.util.ItemTranslator;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * The code used to run when the Deposit command is executed.
 * 
 * @author Musician101
 */
public class DepositCommand implements CommandExecutor
{
	ItemBank plugin;
	/**
	 * @param plugin References the plugin's main class.
	 */
	public DepositCommand(ItemBank plugin)
	{
		this.plugin = plugin;
	}
	
	/**
	 * @param sender Who sent the command.
	 * @param command Which command was executed
	 * @param label Alias of the command
	 * @param args Command parameters
	 * @return True if the command was successfully executed
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if (command.getName().equalsIgnoreCase(Constants.DEPOSIT_CMD) || label.equalsIgnoreCase(Constants.DEPOSIT_ALIAS))
		{
			if (!sender.hasPermission(Constants.DEPOSIT_PERM))
				sender.sendMessage(Constants.NO_PERMISSION);
			else if (!(sender instanceof Player) && !args[0].equalsIgnoreCase(Constants.ADMIN_CMD))
				sender.sendMessage(Constants.PLAYER_COMMAND_ONLY);
			else if (args.length == 0)
				sender.sendMessage(Constants.NOT_ENOUGH);
			else if (args[0].equalsIgnoreCase(Constants.ADMIN_CMD) || args[0].equalsIgnoreCase(Constants.ADMIN_ALIAS))
			{
				String player = args[1];
				String material = args[2];
				int amount = Integer.parseInt(args[3]);
				
				if (player == "")
					sender.sendMessage(Constants.NO_PLAYER);
				else if (material == "")
					sender.sendMessage(Constants.NO_BLOCK_ITEM);
				else if (amount <= 0)
					sender.sendMessage(Constants.ERROR_AMOUNT);
				else
				{
					String path = ItemTranslator.getPath(plugin, material);
					
					plugin.playerFile = new File(plugin.getDataFolder() + "/PlayerData", player + ".yml");
					plugin.playerData = new YamlConfiguration();
					if (plugin.playerFile.exists())
					{
						try
						{
							plugin.playerData.load(plugin.playerFile);
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						
						int oldAmount = plugin.playerData.getInt(path);
						int newAmount = oldAmount + amount;
						plugin.playerData.set(path, newAmount);
						if (sender instanceof Player)
						{
							sender.sendMessage(Constants.getAdminDepositPlayerMessage(amount, material, player));
							plugin.getLogger().info(Constants.getAdminDepositConsoleMessage(sender.getName(), amount, material, player));
						}
						else
							plugin.getLogger().info(Constants.getAdminDepositConsoleMessage(sender.getName(), amount, material, player));
						
						try
						{
							plugin.playerData.save(plugin.playerFile);
						}
						catch (Exception e)
						{
							e.getStackTrace();
						}
					}
					else
						sender.sendMessage(Constants.NO_ACCOUNT);
				}
			}
			else
			{
				String materials = args[0];
				int amount = 0;
				
				for (String blacklist : plugin.blacklist)
				{
					String ID = blacklist;
					
					if (ID == materials)
						sender.sendMessage(Constants.NON_DEPOSITABLE);
					else
					{
						//List<String> data = new ArrayList<String>(ItemTranslator.getMaterial(args[1]));
						Material material = ItemTranslator.getMaterial(plugin, materials);
						byte damage = ItemTranslator.getDamage(plugin, materials);
						//ItemTranslator.parseCSV(plugin, (Player) sender);
						//int material = Integer.parseInt(data.get(0));
						//byte damage = Byte.parseByte(data.get(1);
						String path = ItemTranslator.getPath(plugin, materials);
						
						Player player = (Player) sender;
						if (args.length == 1)
							amount = Inventory.getAmount(player, material, damage);
						else if (args.length == 2)
							amount = Integer.parseInt(args[1]);
						
						if (player.getInventory().contains(new ItemStack(material, amount, damage)))
						{
							plugin.playerFile = new File(plugin.getDataFolder() + "/PlayerData", player.getName() + ".yml");
							plugin.playerData = new YamlConfiguration();
							try
							{
								plugin.playerData.load(plugin.playerFile);
							}
							catch (Exception e)
							{
								e.printStackTrace();
							}
								
							int oldAmount = plugin.playerData.getInt(path);
							int newAmount = oldAmount + amount;
							plugin.playerData.set(path, newAmount);
							player.getInventory().removeItem(new ItemStack(material, amount, damage));
							sender.sendMessage(Constants.getDepositPlayerMessage(amount, materials));
							plugin.getLogger().info(Constants.getDepositConsoleMessage(player.getName(), amount, materials));
							try
							{
								plugin.playerData.save(plugin.playerFile);
							}
							catch (Exception e)
							{
								e.getStackTrace();
							}
						}
						else
							sender.sendMessage(Constants.NOT_ENOUGH);
					}
				}
			}
			return true;
		}
		return false;
	}
}

package musician101.itembank.commands;

import java.io.File;

import musician101.itembank.ItemBank;
import musician101.itembank.lib.Constants;
import musician101.itembank.util.ItemTranslator;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * The code used to run when the Withdraw command is executed.
 * 
 * @author Musician101
 */
public class WithdrawCommand implements CommandExecutor
{
	ItemBank plugin;
	/**
	 * @param plugin References the plugin's 
	 */
	public WithdrawCommand(ItemBank plugin)
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
		String cmd = command.getName().toLowerCase();
		if (cmd == Constants.WITHDRAW_CMD || cmd == Constants.WITHDRAW_ALIAS)
		{
			if (!sender.hasPermission(Constants.WITHDRAW_PERM))
				sender.sendMessage(Constants.NO_PERMISSION);
			else if (!(sender instanceof Player) && !args[0].equalsIgnoreCase(Constants.ADMIN_CMD))
				sender.sendMessage(Constants.PLAYER_COMMAND_ONLY);
			else if (args[1] == "")
				sender.sendMessage(Constants.NO_BLOCK_ITEM);
			/** Admin Withdraw */
			else if (args[0].equalsIgnoreCase(Constants.ADMIN_CMD) || args[0].equalsIgnoreCase(Constants.ADMIN_ALIAS))
			{
				String player = args[1];
				String material = args[2];
				int amount = 0;
				if (args[3] == "")
					amount = Integer.parseInt(args[3]);
				
				if (player == "")
					sender.sendMessage(Constants.NO_PLAYER);
				else if (material == "")
					sender.sendMessage(Constants.NO_BLOCK_ITEM);
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
						
						if (amount > plugin.playerData.getInt(path))
							sender.sendMessage(Constants.NOT_ENOUGH);
						else
						{
							int oldAmount = plugin.playerData.getInt(path);
							int newAmount = oldAmount - amount;
							plugin.playerData.set(path, newAmount);
							if (sender instanceof Player)
							{
								sender.sendMessage(Constants.getAdminWithdrawPlayerMessage(amount, material, player));
								plugin.getLogger().info(Constants.getAdminWithdrawConsoleMessage(sender.getName(), amount, material, player));
							}
							else
								plugin.getLogger().info("Removed " + amount + " " + args[3] + " from " + args[1] + "'s account.");
							
							try
							{
								plugin.playerData.save(plugin.playerFile);
							}
							catch (Exception e)
							{
								e.printStackTrace();
							}
						}
					}
					else
						sender.sendMessage(Constants.NO_ACCOUNT);
				}
			}
			/** Standard Withdraw */
			else
			{
				int amount = 0;
				//List<String> data = new ArrayList<String>(ItemTranslator.getMaterial(args[1]));
				Material material = ItemTranslator.getMaterial(plugin, args[1]);
				byte damage = ItemTranslator.getDamage(plugin, args[1]);
				//int material = Integer.parseInt(data.get(0));
				//byte damage = Byte.parseByte(data.get(1));
				String path = ItemTranslator.getPath(plugin, args[1]);
				
				Player player = (Player) sender;						
				plugin.playerFile = new File(plugin.getDataFolder() + "/PlayerData/", player.getName() + ".yml");
				plugin.playerData = new YamlConfiguration();
				try
				{
					plugin.playerData.load(plugin.playerFile);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				
				if (args.length == 2)
				{
					amount = plugin.playerData.getInt(path);
					sender.sendMessage(Constants.ALL_BLOCK_ITEM);
				}
				else if (args.length == 3)
					amount = Integer.parseInt(args[2]);
				else
					sender.sendMessage(Constants.TOO_MANY_ARGUMENTS_DEFAULT_TO_ALL);
				
				if (amount > plugin.playerData.getInt(path))
					sender.sendMessage(Constants.NOT_ENOUGH);
				else
				{
					int newAmount = 0;
					int oldAmount = plugin.playerData.getInt(path);
					newAmount = oldAmount - amount;
					plugin.playerData.set(path, newAmount);
					player.getInventory().addItem(new ItemStack(material, amount, damage));
					sender.sendMessage(Constants.getWithdrawPlayerMessage(amount, args[1], plugin.playerData.getInt(path)));
					plugin.getLogger().info(Constants.getWithdrawConsoleMessage(player.getName(), amount, args[1]));
					try
					{
						plugin.playerData.save(plugin.playerFile);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
			return true;
		}
		return false;
	}
}

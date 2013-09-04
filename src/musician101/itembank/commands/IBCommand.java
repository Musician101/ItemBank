package musician101.itembank.commands;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import musician101.itembank.ItemBank;
import musician101.itembank.lib.Constants;
import musician101.itembank.listeners.PlayerListener;
import musician101.itembank.util.ItemTranslator;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * The code used to run when the ItemBank command is executed.
 * 
 * @author Musician101
 */
public class IBCommand implements CommandExecutor
{
	ItemBank plugin;
	/**
	 * @param plugin References the plugin's 
	 */
	public IBCommand(ItemBank plugin)
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
		if (command.getName().equalsIgnoreCase(Constants.BASE_CMD) || command.getName().equalsIgnoreCase(Constants.BASE_ALIAS))
		{
			/** Base Command */
			if (args.length == 0)
			{
				if (sender.hasPermission(Constants.BASE_PERM + ".*") || sender.hasPermission(Constants.DEPOSIT_PERM) || sender.hasPermission(Constants.HELP_PERM) || sender.hasPermission(Constants.PURGE_PERM) || sender.hasPermission(Constants.VERSION_PERM) || sender.hasPermission(Constants.WITHDRAW_PERM))
				{
					sender.sendMessage(Constants.PREFIX + Constants.BASE_DESC);
					return true;
				}
				else
				{
					sender.sendMessage(Constants.NO_PERMISSION);
					return false;
				}
			}
			else if (args.length > 0)
			{
				/** Admin Command */
				if (args[0].equalsIgnoreCase(Constants.ADMIN_CMD) || args[0].equalsIgnoreCase(Constants.ADMIN_ALIAS))
				{
					if (!sender.hasPermission(Constants.ADMIN_PERM))
					{
						sender.sendMessage(Constants.NO_PERMISSION);
						return false;
					}
					/** Admin Deposit */
					else if (args[2].equalsIgnoreCase(Constants.DEPOSIT_CMD) || args[2].equalsIgnoreCase(Constants.DEPOSIT_ALIAS))
					{
						if (args[2] == "")
						{
							sender.sendMessage(Constants.NO_PLAYER);
							return false;
						}
						else if (args[3] == "")
						{
							sender.sendMessage(Constants.NO_BLOCK_ITEM);
							return false;
						}
						else
						{
							int amount = Integer.parseInt(args[4]);
							String path = ItemTranslator.getPath(plugin, args[2]);
							
							plugin.playerFile = new File(plugin.getDataFolder() + "/PlayerData", args[1] + ".yml");
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
									sender.sendMessage(Constants.getAdminDepositPlayerMessage(amount, args[3], args[1]));
									plugin.getLogger().info(Constants.getAdminDepositConsoleMessage(sender.getName(), amount, args[3], args[1]));
								}
								else
								{
									plugin.getLogger().info(Constants.getAdminDepositConsoleMessage(sender.getName(), amount, args[3], args[1]));
								}
								
								try
								{
									plugin.playerData.save(plugin.playerFile);
								}
								catch (Exception e)
								{
									e.getStackTrace();
								}
								return true;
							}
							else
							{
								sender.sendMessage(Constants.NO_ACCOUNT);
								return false;
							}
						}
					}
					/** Admin Withdraw */
					else if (args[2].equalsIgnoreCase(Constants.WITHDRAW_CMD) || args[2].equalsIgnoreCase(Constants.WITHDRAW_ALIAS))
					{
						if (args[1] == "")
						{
							sender.sendMessage(Constants.NO_PLAYER);
							return false;
						}
						else if (args[3] == "")
						{
							sender.sendMessage(Constants.NO_BLOCK_ITEM);
							return false;
						}
						else
						{
							int amount = Integer.parseInt(args[4]);
							String path = ItemTranslator.getPath(plugin, args[3]);
							
							plugin.playerFile = new File(plugin.getDataFolder() + "/PlayerData", args[1] + ".yml");
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
								{
									sender.sendMessage(Constants.PREFIX + "Error: " + args[1] + " does not have enough of the specified item.");
									return false;
								}
								else
								{
									int oldAmount = plugin.playerData.getInt(path);
									int newAmount = oldAmount - amount;
									plugin.playerData.set(path, newAmount);
									if (sender instanceof Player)
									{
										sender.sendMessage(Constants.PREFIX + "Removed " + amount + " " + args[3] + " from " + args[1] + "'s account.");
										plugin.getLogger().info(sender.getName() + " has withdrawn " + amount + " " + args[3] + " from" + args[1] + "'s account.");
									}
									else
									{
										plugin.getLogger().info("Removed " + amount + " " + args[3] + " from " + args[1] + "'s account.");
									}
									
									try
									{
										plugin.playerData.save(plugin.playerFile);
									}
									catch (Exception e)
									{
										e.printStackTrace();
									}
									return true;
								}
							}
							else
							{
								sender.sendMessage(Constants.NO_ACCOUNT);
								return false;
							}
						}
					}
				}
				/** Deposit Command */
				else if (args[0].equalsIgnoreCase(Constants.DEPOSIT_CMD) || args[0].equalsIgnoreCase(Constants.DEPOSIT_ALIAS))
				{
					if (!sender.hasPermission(Constants.DEPOSIT_PERM))
					{
						sender.sendMessage(Constants.NO_PERMISSION);
						return false;
					}
					else if (!(sender instanceof Player))
					{
						sender.sendMessage(Constants.PLAYER_COMMAND_ONLY);
						return false;
					}
					else if (args[1] == "")
					{
						sender.sendMessage(Constants.NO_BLOCK_ITEM);
						return false;
					}
					else
					{
						for (String blacklist : plugin.blacklist)
						{
								String ID = blacklist;
								
								if (ID == args[1])
								{
									sender.sendMessage(Constants.NON_DEPOSITABLE);
									return false;
								}
								else
								{
									int amount = 0;
									//List<String> data = new ArrayList<String>(ItemTranslator.getMaterial(args[1]));
									Material material = ItemTranslator.getMaterial(plugin, args[1]);
									byte damage = ItemTranslator.getDamage(plugin, args[1]);
									//ItemTranslator.parseCSV(plugin, (Player) sender);
									//int material = Integer.parseInt(data.get(0));
									//byte damage = Byte.parseByte(data.get(1);
									String path = ItemTranslator.getPath(plugin, args[1]);
									
									Player player = (Player) sender;
									if (args.length == 2)
										amount = getAmount(player, material, damage);
									else if (args.length == 3)
										amount = Integer.parseInt(args[2]);
									
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
										sender.sendMessage(Constants.getDepositPlayerMessage(amount, args[1]));
										plugin.getLogger().info(Constants.getDepositConsoleMessage(player.getName(), amount, args[1]));
										try
										{
											plugin.playerData.save(plugin.playerFile);
										}
										catch (Exception e)
										{
											e.getStackTrace();
										}
										return true;
									}
									else
									{
										sender.sendMessage(Constants.NOT_ENOUGH);
										return false;
									}
								}
							}
						}
					}
				/** Help Command */
				else if (args[0].equalsIgnoreCase(Constants.HELP_CMD) || args[0].equalsIgnoreCase(Constants.HELP_ALIAS))
				{
					if (!sender.hasPermission(Constants.HELP_PERM))
					{
						sender.sendMessage(Constants.NO_PERMISSION);
						return false;
					}
					else
					{
						if (args.length == 1)
						{
							sender.sendMessage(Constants.HELP_LIST);
							return true;
						}
						else
						{
							if (args[1].equalsIgnoreCase(Constants.ADMIN_CMD) || args[1].equalsIgnoreCase(Constants.ADMIN_ALIAS))
							{
								sender.sendMessage(Constants.ADMIN_HELP);
								return true;
							}
							else if (args[1].equalsIgnoreCase(Constants.DEPOSIT_CMD) || args[1].equalsIgnoreCase(Constants.DEPOSIT_ALIAS))
							{
								sender.sendMessage(Constants.DEPOSIT_HELP);
								return true;
							}
							else if (args[1].equalsIgnoreCase(Constants.PURGE_CMD) || args[1].equalsIgnoreCase(Constants.PURGE_ALIAS))
							{
								sender.sendMessage(Constants.PURGE_HELP);
								return true;
							}
							else if (args[1].equalsIgnoreCase(Constants.WITHDRAW_CMD) || args[1].equalsIgnoreCase(Constants.WITHDRAW_ALIAS))
							{
								sender.sendMessage(Constants.WITHDRAW_HELP);
								return true;
							}
							else
							{
								sender.sendMessage(Constants.PREFIX + "Error: Command not recognized.");
								return false;
							}
						}
					}
				}
				/** Purge Command */
				else if (args[0].equalsIgnoreCase(Constants.PURGE_CMD) || args[0].equalsIgnoreCase(Constants.PURGE_ALIAS))
				{
					if (!sender.hasPermission(Constants.PURGE_PERM))
					{
						sender.sendMessage(Constants.NO_PERMISSION);
						return false;
					}
					else
					{
						if (args.length == 1)
						{
							File files = new File(plugin.getDataFolder() + "/PlayerData");
							for (File file : files.listFiles())
							{
								file.delete();
							}
							Player[] players = Bukkit.getOnlinePlayers();
							if (players.length > 0)
							{
								for (Player player : players)
								{
									plugin.playerFile = new File(plugin.getDataFolder() + "/PlayerData/" + player.getName() + ".yml");
									try
									{
										FileWriter fw;
										plugin.playerFile.createNewFile();
										fw = new FileWriter(plugin.playerFile.getAbsoluteFile());
										BufferedWriter bw = new BufferedWriter(fw);
										bw.write(PlayerListener.template);
										bw.close();
									}
									catch (Exception e)
									{
										e.printStackTrace();
									}
								}
							}
							sender.sendMessage(Constants.PREFIX + "Purge complete.");
							return true;
						}
						else if (args.length == 2)
						{
							plugin.playerFile = new File(plugin.getDataFolder() + "/PlayerData/" + args[1] + ".yml");
							try
							{
								plugin.playerFile.delete();
								FileWriter fw;
								plugin.playerFile.createNewFile();
								fw = new FileWriter(plugin.playerFile.getAbsoluteFile());
								BufferedWriter bw = new BufferedWriter(fw);
								bw.write(PlayerListener.template);
								bw.close();
							}
							catch (Exception e)
							{
								e.printStackTrace();
							}
							sender.sendMessage(Constants.PLAYER_FILE_RESET);
							return true;
						}
						else
						{
							sender.sendMessage(Constants.TOO_MANY_ARGUMENTS);
							return false;
						}
					}
				}
				/** Version Command */
				else if (args[0].equalsIgnoreCase(Constants.VERSION_CMD) || args[0].equalsIgnoreCase(Constants.VERSION_ALIAS))
				{
					if (!sender.hasPermission(Constants.VERSION_PERM))
					{
						sender.sendMessage(Constants.NO_PERMISSION);
						return false;
					}
					else
					{
						sender.sendMessage(Constants.getVersionMessage(plugin.getDescription().getVersion()));
						return true;
					}
				}
				/** Withdraw Command */
				else if (args[0].equalsIgnoreCase(Constants.WITHDRAW_CMD) || args[0].equalsIgnoreCase(Constants.WITHDRAW_ALIAS))
				{
					if (!sender.hasPermission(Constants.WITHDRAW_PERM))
					{
						sender.sendMessage(Constants.NO_PERMISSION);
						return false;
					}
					else if (!(sender instanceof Player))
					{
						sender.sendMessage(Constants.PLAYER_COMMAND_ONLY);
						return false;
					}
					else if (args[1] == "")
					{
						sender.sendMessage(Constants.NO_BLOCK_ITEM);
						return false;
					}
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
						{
							sender.sendMessage(Constants.TOO_MANY_ARGUMENTS_DEFAULT_TO_ALL);
							return false;
						}
						
						if (amount > plugin.playerData.getInt(path))
						{
							sender.sendMessage(Constants.NOT_ENOUGH);
							return false;
						}
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
							return true;
						}
					}
				}
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Method for finding specific blocks/items in a player's inventory
	 * 
	 * @param player Player who's inventory is being checked.
	 * @param material The material that is being searched for.
	 * @param dmg The damage value of the material (i.e. if material = oak wood then dmg = 1).
	 * @return The amount of the material in the player's inventory.
	 */
	public static int getAmount(Player player, Material material, byte dmg)
	{
		int has = 0;
		for (ItemStack item : player.getInventory().getContents())
		{
			if ((item !=null) && (item.getTypeId() == material.getId()) && (item.getAmount() > 0) && (item.getDurability() == dmg))
				has += item.getAmount();
		}
		return has;
	}
}

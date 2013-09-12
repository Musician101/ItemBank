package musician101.itembank.lib;

import org.bukkit.ChatColor;
 
/**
 * List of strings used throughout the plugin.
 * 
 * @author Musician101
 */
public class Constants 
{
	/** Formatting. */
	public static String PREFIX = ChatColor.DARK_RED + "[ItemBank] ";
	
	/** Error messages. */
	public static final String COMMAND_NOT_RECOGNIZED = PREFIX + "Error: Command not recognized.";
	public static final String ERROR_AMOUNT = PREFIX + "Error: Amount must be greater than 0.";
	public static final String NO_ACCOUNT = PREFIX + "Error: Account does not exist.";
	public static final String NO_BLOCK_ITEM = PREFIX + "Error: You did not specify a block/item.";
	public static final String NO_PERMISSION = PREFIX + "You do not have permission for this command.";
	public static final String NO_PLAYER = PREFIX + "Error: You did not specify a player.";
	public static final String NON_DEPOSITABLE = PREFIX + "Sorry, but that item is not depositable.";
	public static final String NOT_ENOUGH = PREFIX + "Error: You do not have enough of the specified item.";
	public static final String PLAYER_COMMAND_ONLY = PREFIX + "Error: This is a player command only.";
	public static final String TOO_LITTLE_ARGUMENTS = PREFIX + "Error: Not enough arguments.";
	public static final String TOO_MANY_ARGUMENTS = PREFIX + "Error: Too many arguments.";
	public static final String TOO_MANY_ARGUMENTS_DEFAULT_TO_ALL = PREFIX + "Error: Too many arguments. Defaulting to all of the specified block/item.";
	
	/** Command success messages. */
	public static final String ALL_BLOCK_ITEM = "Defaulting to all of the specified block/item.";
	public static final String PLAYER_FILE_RESET = PREFIX + "Player file reset.";
	
	public static String getAdminDepositConsoleMessage(String admin, int amount, String material, String player)
	{
		return admin + " has deposited " + amount + " of " + material + " to " + player + "'s account.";
	}
	
	public static String getAdminDepositPlayerMessage(int amount, String material, String player)
	{
		return PREFIX + "Added " + amount + " " + material + " to " + player + "'s account.";
	}
	
	public static String getAdminWithdrawConsoleMessage(String admin, int amount, String material, String player)
	{
		return admin + " has withdrawn " + amount + " " + material + " from " + player + "'s account.";
	}
	
	public static String getAdminWithdrawPlayerMessage(int amount, String material, String player)
	{
		return PREFIX + "Removed " + amount + " " + material + " from " + player + "'s account.";
	}
	
	public static String getDepositConsoleMessage(String player, int amount, String material)
	{
		return player + " has deposited " + amount + " of " + material + ".";
	}
	
	public static String getDepositPlayerMessage(int amount, String material)
	{
		return PREFIX + "You have deposited " + amount + " " + material + ".";
	}
	
	public static String getVersionMessage(String version)
	{
		return PREFIX + "Version " + version + " compiled with Bukkit 1.5.2R-1.0.";
	}
	
	public static String getWithdrawConsoleMessage(String player, int amount, String material)
	{
		return player + " has withdraw " + amount + " of " + material + ".";
	}
	
	public static String getWithdrawPlayerMessage(int amount, String material, int bankAmount)
	{
		return PREFIX + "You have withdrawn " + amount + " " + material + " and now have a total of " + bankAmount + " left.";
	}
	
	/** ItemBank command */
	public static final String BASE_CMD = "ItemBank";
	public static final String BASE_ALIAS = "ib";
	public static final String BASE_PERM = "itembank";
	public static final String BASE_DESC = "Base command, type /itembank help for more info.";
	
	/** Deposit command */
	public static final String DEPOSIT_CMD = "deposit";
	public static final String DEPOSIT_ALIAS = "d";
	public static final String DEPOSIT_PERM = BASE_PERM + ".deposit";
	public static final String DEPOSIT_DESC = "Deposit blocks/items into your account.";
	public static final String DEPOSIT_USAGE = "/itembank deposit [block/item] <amount>";
	public static final String[] DEPOSIT_HELP = {"--------" + ChatColor.DARK_RED + "ItemBank" + ChatColor.WHITE + "--------",
		"Help for the " + ChatColor.DARK_RED + "Deposit" + ChatColor.WHITE + " command.", "[] are required and <> are optional.",
		ChatColor.DARK_RED + "Description: " + ChatColor.WHITE + DEPOSIT_DESC, ChatColor.DARK_RED + "Usage: " + ChatColor.WHITE + DEPOSIT_USAGE}; 
	
	/** Purge command */
	public static final String PURGE_CMD = "purge";
	public static final String PURGE_ALIAS = "p";
	public static final String PURGE_PERM = BASE_PERM + ".purge";
	public static final String PURGE_DESC = "Delete all or a specified account.";
	public static final String PURGE_USAGE = "/itembank purge <player>";
	public static final String[] PURGE_HELP = {"--------" + ChatColor.DARK_RED + "ItemBank" + ChatColor.WHITE + "--------",
		"Help for the " + ChatColor.DARK_RED + "Purge" + ChatColor.WHITE + " command.", "[] are required and <> are optional.",
		ChatColor.DARK_RED + "Description: " + ChatColor.WHITE + PURGE_DESC, ChatColor.DARK_RED + "Usage: " + ChatColor.WHITE + PURGE_USAGE};
	
	/** Version command */
	public static final String VERSION_CMD = "version";
	public static final String VERSION_ALIAS = "v";
	public static final String VERSION_PERM = BASE_PERM + ".version";
	public static final String VERSION_DESC = "Shows the plugin version and which Bukkit API it uses.";
	public static final String VERSION_USAGE = "/itembank version";
	public static final String[] VERSION_HELP = {"--------" + ChatColor.DARK_RED + "ItemBank" + ChatColor.WHITE + "--------",
		"Help for the " + ChatColor.DARK_RED + "Version" + ChatColor.WHITE + " command.", "[] are required and <> are optional.",
		ChatColor.DARK_RED + "Description: " + ChatColor.WHITE + VERSION_DESC, ChatColor.DARK_RED + "Usage: " + ChatColor.WHITE + VERSION_USAGE};
	
	/** Withdraw command */
	public static final String WITHDRAW_CMD = "withdraw";
	public static final String WITHDRAW_ALIAS = "w";
	public static final String WITHDRAW_PERM = BASE_PERM + ".withdraw";
	public static final String WITHDRAW_DESC = "Withdraw blocks/items from your account";
	public static final String WITHDRAW_USAGE = "/itembank withdraw [block/item] <amount>";
	public static final String[] WITHDRAW_HELP = {"--------" + ChatColor.DARK_RED + "ItemBank" + ChatColor.WHITE + "--------",
		"Help for the " + ChatColor.DARK_RED + "Withdraw" + ChatColor.WHITE + " command.", "[] are required and <> are optional.",
		ChatColor.DARK_RED + "Description: " + ChatColor.WHITE + WITHDRAW_DESC, ChatColor.DARK_RED + "Usage: " + ChatColor.WHITE + WITHDRAW_USAGE};
	
	/** Admin command */
	public static final String ADMIN_CMD = "admin";
	public static final String ADMIN_ALIAS = "a";
	public static final String ADMIN_PERM = BASE_PERM + ".admin";
	public static final String ADMIN_DESC = "Add/Remove blocks/items from a specified account.";
	public static final String ADMIN_USAGE = "/itembank admin [player] [deposit|withdraw] [block/item] [amount]";
	public static final String[] ADMIN_HELP = {"--------" + ChatColor.DARK_RED + "ItemBank" + ChatColor.WHITE + "--------",
		"Help for the " + ChatColor.DARK_RED + "Admin" + ChatColor.WHITE + " command.", "[] are required and <> are optional.",
		ChatColor.DARK_RED + "Description: " + ChatColor.WHITE + ADMIN_DESC, ChatColor.DARK_RED + "Usage: " + ChatColor.WHITE + ADMIN_USAGE};
	
	/** Help command */
	public static final String HELP_CMD = "help";
	public static final String HELP_ALIAS = "h";
	public static final String HELP_PERM = BASE_PERM + ".help";
	public static final String HELP_DESC = "Gives a list of commands for the plugin, how they're used and what they do.";
	public static final String[] HELP_LIST = {"--------" + ChatColor.DARK_RED + "ItemBank" + ChatColor.WHITE + "--------",
		"Type " + ChatColor.DARK_RED + "/itembank help <command> " + ChatColor.WHITE + "for specific info.",
		"Deposit: " + DEPOSIT_DESC, "Purge: " + PURGE_DESC, "Version: " + VERSION_DESC, "Withdraw: " + WITHDRAW_DESC};
}

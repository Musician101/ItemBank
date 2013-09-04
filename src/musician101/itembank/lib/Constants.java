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
	public static final String NO_ACCOUNT = PREFIX + "Error: Account does not exist.";
	public static final String NO_BLOCK_ITEM = PREFIX + "Error: You did not specify a block/item.";
	public static final String NO_PERMISSION = PREFIX + "You do not have permission for this command.";
	public static final String NO_PLAYER = PREFIX + "Error: You did not specify a player.";
	public static final String NON_DEPOSITABLE = PREFIX + "Sorry, but that item is not depositable.";
	public static final String NOT_ENOUGH = PREFIX + "Error: You do not have enough of the specified item.";
	public static final String PLAYER_COMMAND_ONLY = PREFIX + "Error: This is a player command only.";
	public static final String TOO_MANY_ARGUMENTS = PREFIX + "Error: Too many arguments.";
	public static final String TOO_MANY_ARGUMENTS_DEFAULT_TO_ALL = PREFIX + "Error: Too many arguments. Defaulting to all of the specified block/item.";
	
	/** Command success messages. */
	public static final String ALL_BLOCK_ITEM = "Defaulting to all of the specified block/item.";
	
	public static String getDepositConsoleMessage(String player, int amount, String material)
	{
		return player + " has deposited " + amount + " of " + material + ".";
	}
	
	public static String getDepositPlayerMessage(int amount, String material)
	{
		return PREFIX + "You have deposited " + amount + " " + material + ".";
	}
	
	public static final String PLAYER_FILE_RESET = PREFIX + "Player file reset.";
	
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
	public static final String baseCmd = "ItemBank";
	public static final String baseAlias = "ib";
	public static final String basePerm = "itembank";
	public static final String baseDesc = "Base command, type /itembank help for more info.";
	
	/** Deposit command */
	public static final String depositCmd = "deposit";
	public static final String depositAlias = "d";
	public static final String depositPerm = basePerm + ".deposit";
	public static final String depositDesc = "Deposit blocks/items into your account.";
	public static final String depositUsage = "/itembank deposit [block/item] <amount>";
	public static final String[] depositHelp = {"--------" + ChatColor.DARK_RED + "ItemBank" + ChatColor.WHITE + "--------",
		"Help for the " + ChatColor.DARK_RED + "Deposit" + ChatColor.WHITE + " command.", "[] are required and <> are optional.",
		ChatColor.DARK_RED + "Description: " + ChatColor.WHITE + depositDesc, ChatColor.DARK_RED + "Usage: " + ChatColor.WHITE + depositUsage}; 
	
	/** Purge command */
	public static final String purgeCmd = "purge";
	public static final String purgeAlias = "p";
	public static final String purgePerm = basePerm + ".purge";
	public static final String purgeDesc = "Delete all or a specified account.";
	public static final String purgeUsage = "/itembank purge <player>";
	public static final String[] purgeHelp = {"--------" + ChatColor.DARK_RED + "ItemBank" + ChatColor.WHITE + "--------",
		"Help for the " + ChatColor.DARK_RED + "Purge" + ChatColor.WHITE + " command.", "[] are required and <> are optional.",
		ChatColor.DARK_RED + "Description: " + ChatColor.WHITE + purgeDesc, ChatColor.DARK_RED + "Usage: " + ChatColor.WHITE + purgeUsage};
	
	/** Version command */
	public static final String versionCmd = "version";
	public static final String versionAlias = "v";
	public static final String versionPerm = basePerm + ".version";
	public static final String versionDesc = "Shows the plugin version and which Bukkit API it uses.";
	public static final String versionUsage = "/itembank version";
	public static final String[] versionHelp = {"--------" + ChatColor.DARK_RED + "ItemBank" + ChatColor.WHITE + "--------",
		"Help for the " + ChatColor.DARK_RED + "Version" + ChatColor.WHITE + " command.", "[] are required and <> are optional.",
		ChatColor.DARK_RED + "Description: " + ChatColor.WHITE + versionDesc, ChatColor.DARK_RED + "Usage: " + ChatColor.WHITE + versionUsage};
	
	/** Withdraw command */
	public static final String withdrawCmd = "withdraw";
	public static final String withdrawAlias = "w";
	public static final String withdrawPerm = basePerm + ".withdraw";
	public static final String withdrawDesc = "Withdraw blocks/items from your account";
	public static final String withdrawUsage = "/itembank withdraw [block/item] <amount>";
	public static final String[] withdrawHelp = {"--------" + ChatColor.DARK_RED + "ItemBank" + ChatColor.WHITE + "--------",
		"Help for the " + ChatColor.DARK_RED + "Withdraw" + ChatColor.WHITE + " command.", "[] are required and <> are optional.",
		ChatColor.DARK_RED + "Description: " + ChatColor.WHITE + withdrawDesc, ChatColor.DARK_RED + "Usage: " + ChatColor.WHITE + withdrawUsage};
	
	/** Admin command */
	public static final String adminCmd = "admin";
	public static final String adminAlias = "a";
	public static final String adminPerm = basePerm + ".admin";
	public static final String adminDesc = "Add/Remove blocks/items from a specified account.";
	public static final String adminUsage = "/itembank admin [player] [deposit|withdraw] [block/item] [amount]";
	public static final String[] adminHelp = {"--------" + ChatColor.DARK_RED + "ItemBank" + ChatColor.WHITE + "--------",
		"Help for the " + ChatColor.DARK_RED + "Admin" + ChatColor.WHITE + " command.", "[] are required and <> are optional.",
		ChatColor.DARK_RED + "Description: " + ChatColor.WHITE + adminDesc, ChatColor.DARK_RED + "Usage: " + ChatColor.WHITE + adminUsage};
	
	/** Help command */
	public static final String helpCmd = "help";
	public static final String helpAlias = "h";
	public static final String helpPerm = basePerm + ".help";
	public static final String helpDesc = "Gives a list of commands for the plugin, how they're used and what they do.";
	public static final String[] helpMessageList = {"--------" + ChatColor.DARK_RED + "ItemBank" + ChatColor.WHITE + "--------",
		"Type " + ChatColor.DARK_RED + "/itembank help <command> " + ChatColor.WHITE + "for specific info.",
		"Deposit: " + depositDesc, "Purge: " + purgeDesc, "Version: " + versionDesc, "Withdraw: " + withdrawDesc};
}

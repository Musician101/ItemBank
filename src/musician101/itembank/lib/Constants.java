package musician101.itembank.lib;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Constants
{
	/** Formatting */
	public static final String HEADER = "--------" + ChatColor.DARK_RED + "ItemBank" + ChatColor.WHITE + "--------";
	public static final String PREFIX = ChatColor.DARK_RED + "[" + Bukkit.getPluginManager().getPlugin("ItemBank").getDescription().getPrefix() + "] ";
	
	/** Commands Names */
	public static final String ACCOUNT_CMD = "account";
	public static final String ITEMBANK_CMD = "itembank";
	public static final String PURGE_CMD = "purge";
	public static final String RELOAD_CMD = "reload";
	public static final String HELP_CMD = "help";
	
	/** Permissions */
	public static final String ACCOUNT_PERM = ITEMBANK_CMD + "." + ACCOUNT_CMD;
	public static final String ADMIN_PERM = ITEMBANK_CMD + ".admin";
	public static final String ADMIN_ACCOUNT_PERM = ADMIN_PERM + "." + ACCOUNT_CMD;
	public static final String EXEMPT_PERM = ADMIN_PERM + ".exempt";
	public static final String PURGE_PERM = ADMIN_PERM + "." + PURGE_CMD;
	public static final String RELOAD_PERM = ADMIN_PERM + "." + RELOAD_CMD;
	
	/** Command Help */
	public static final String[] ACCOUNT_HELP_MSG = {HEADER, ChatColor.DARK_RED + "Description: " + ChatColor.WHITE + "Opens a GUI with the items stored in the account.",
		ChatColor.DARK_RED + "Usage: " + ChatColor.WHITE + "/account"};
	public static final String[] PURGE_HELP_MSG = {HEADER, ChatColor.DARK_RED + "Description: " + ChatColor.WHITE + "Delete all or a specified player's account.",
		ChatColor.DARK_RED + "Usage: " + ChatColor.WHITE + "/itembank purge [player]"};
	public static final String[] RELOAD_HELP_MSG = {HEADER, ChatColor.DARK_RED + "Description: " + ChatColor.WHITE + "Reloads the plugin's config file.",
		ChatColor.DARK_RED + "Usage: " + ChatColor.WHITE + "/itembank reload"};
	public static final String[] HELP_MSG = {HEADER, "Version: " + Bukkit.getPluginManager().getPlugin("ItemBank").getDescription().getVersion() + " for Bukkit 1.7.2-R0.3",
		"Downloads, Wiki, & Bug Reporting: http://dev.bukkit.org/bukkit-plugins/item_bank/", "For specific command help use /itembank help <command>"};
	
	/** Config */
	public static final String BLACKLIST = "blacklist";
	public static final String ENABLE_VAULT = "enableVault";
	//public static final String LANG = "lang";
	public static final String PAGE_LIMIT = "pageLimit";
	public static final String TRANSACTION_COST = "transactionCost";
	public static final String UPDATE_CHECK = "updateCheck";
	
	/** Error Messages */
	public static final String NO_AIR = PREFIX + "Error: Air is not a valid material.";
	public static final String NO_PERMISSION = PREFIX + "Error: You do not have permission for this command.";
	public static final String NOT_ENOUGH_ARGS = PREFIX + "Error: Not enough arguments.";
	public static final String PLAYER_COMMAND_ONLY = PREFIX + "Error: This is a player command only.";
	
	/** Exception Messages */
	public static final String ARRAY_EX = PREFIX + "Error: Incomplete arguments.";
	public static final String IO_EX = PREFIX + "Error: An internal error has occured. Please contact an administrator immediately.";
	public static final String NO_FILE_EX = PREFIX + "Error: File not found. Please contact an administrator immediately.";
	public static final String NULL_POINTER_EX = PREFIX + "Error: The ItemTranslator has failed to load. Please contact an administrator immediately.";
	public static final String NUMBER_EX = PREFIX + "Error: A string has been used as a number. Please have contact an administrator immediately.";
	public static final String YAML_EX = PREFIX + "Error: Your account contains format errors. Please contact an administrator immediately.";
	
	/** Other */
	public static final String NEW_PLAYER_FILE = "# Do not edit this file unless it's absolutely necessary.\n";
}

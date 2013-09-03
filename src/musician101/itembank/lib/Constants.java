package musician101.itembank.lib;

import org.bukkit.ChatColor;

// Constants for command names, aliases, perms, and other things. 
public class Constants 
{
	// General Strings
	public static String prefix = ChatColor.DARK_RED + "[ItemBank] ";
	public static String noPermission = prefix + " You do not have permission for this command.";
	
	// Itembank command
	public static String baseCmd = "ItemBank";
	public static String baseAlias = "ib";
	public static String basePerm = "itembank";
	public static String baseDesc = "Base command, type /itembank help for more info.";
	
	// Deposit command
	public static String depositCmd = "deposit";
	public static String depositAlias = "d";
	public static String depositPerm = basePerm + ".deposit";
	public static String depositDesc = "Deposit blocks/items into your account.";
	public static String depositUsage = "/itembank deposit [block/item] <amount>";
	public static String[] depositHelp = {"--------" + ChatColor.DARK_RED + "ItemBank" + ChatColor.WHITE + "--------",
		"Help for the " + ChatColor.DARK_RED + "Deposit" + ChatColor.WHITE + " command.", "[] are required and <> are optional.",
		ChatColor.DARK_RED + "Description: " + ChatColor.WHITE + depositDesc, ChatColor.DARK_RED + "Usage: " + ChatColor.WHITE + depositUsage}; 
	
	// Purge command
	public static String purgeCmd = "purge";
	public static String purgeAlias = "p";
	public static String purgePerm = basePerm + ".purge";
	public static String purgeDesc = "Delete all or a specified account.";
	public static String purgeUsage = "/itembank purge <player>";
	public static String[] purgeHelp = {"--------" + ChatColor.DARK_RED + "ItemBank" + ChatColor.WHITE + "--------",
		"Help for the " + ChatColor.DARK_RED + "Purge" + ChatColor.WHITE + " command.", "[] are required and <> are optional.",
		ChatColor.DARK_RED + "Description: " + ChatColor.WHITE + purgeDesc, ChatColor.DARK_RED + "Usage: " + ChatColor.WHITE + purgeUsage};
	
	// Version command
	public static String versionCmd = "version";
	public static String versionAlias = "v";
	public static String versionPerm = basePerm + ".version";
	public static String versionDesc = "Shows the plugin version and which Bukkit API it uses.";
	public static String versionUsage = "/itembank version";
	public static String[] versionHelp = {"--------" + ChatColor.DARK_RED + "ItemBank" + ChatColor.WHITE + "--------",
		"Help for the " + ChatColor.DARK_RED + "Version" + ChatColor.WHITE + " command.", "[] are required and <> are optional.",
		ChatColor.DARK_RED + "Description: " + ChatColor.WHITE + versionDesc, ChatColor.DARK_RED + "Usage: " + ChatColor.WHITE + versionUsage};
	
	// Withdraw command
	public static String withdrawCmd = "withdraw";
	public static String withdrawAlias = "w";
	public static String withdrawPerm = basePerm + ".withdraw";
	public static String withdrawDesc = "Withdraw blocks/items from your account";
	public static String withdrawUsage = "/itembank withdraw [block/item] <amount>";
	public static String[] withdrawHelp = {"--------" + ChatColor.DARK_RED + "ItemBank" + ChatColor.WHITE + "--------",
		"Help for the " + ChatColor.DARK_RED + "Withdraw" + ChatColor.WHITE + " command.", "[] are required and <> are optional.",
		ChatColor.DARK_RED + "Description: " + ChatColor.WHITE + withdrawDesc, ChatColor.DARK_RED + "Usage: " + ChatColor.WHITE + withdrawUsage};
	
	// Admin command
	public static String adminCmd = "admin";
	public static String adminAlias = "a";
	public static String adminPerm = basePerm + ".admin";
	public static String adminDesc = "Add/Remove blocks/items from a specified account.";
	public static String adminUsage = "/itembank admin [player] [deposit|withdraw] [block/item] [amount]";
	public static String[] adminHelp = {"--------" + ChatColor.DARK_RED + "ItemBank" + ChatColor.WHITE + "--------",
		"Help for the " + ChatColor.DARK_RED + "Admin" + ChatColor.WHITE + " command.", "[] are required and <> are optional.",
		ChatColor.DARK_RED + "Description: " + ChatColor.WHITE + adminDesc, ChatColor.DARK_RED + "Usage: " + ChatColor.WHITE + adminUsage};
	
	// Help command
	public static String helpCmd = "help";
	public static String helpAlias = "h";
	public static String helpPerm = basePerm + ".help";
	public static String helpDesc = "Gives a list of commands for the plugin, how they're used and what they do.";
	public static String[] helpMessageList = {"--------" + ChatColor.DARK_RED + "ItemBank" + ChatColor.WHITE + "--------",
		"Type " + ChatColor.DARK_RED + "/itembank help <command> " + ChatColor.WHITE + "for specific info.",
		"Deposit: " + depositDesc, "Purge: " + purgeDesc, "Version: " + versionDesc, "Withdraw: " + withdrawDesc};
}

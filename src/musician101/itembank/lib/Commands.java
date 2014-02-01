package musician101.itembank.lib;

import org.bukkit.ChatColor;

public class Commands
{
	/** Command Names */
	public static final String BASE_CMD = "itembank";
	public static final String ACCOUNT_CMD = "account";
	public static final String ADMIN_CMD = "admin";
	public static final String ALIAS_CMD = "itemalias";
	public static final String CUSTOM_ITEM_CMD = "customItem";
	public static final String DEPOSIT_CMD = "deposit";
	public static final String HELP_CMD = "help";
	public static final String PURGE_CMD = "purge";
	public static final String WITHDRAW_CMD = "withdraw";

	/** Permissions */
	public static final String ACCOUNT_PERM = BASE_CMD + "." + ACCOUNT_CMD;
	public static final String ADMIN_PERM = BASE_CMD + "." + ADMIN_CMD;
	public static final String ALIAS_PERM = BASE_CMD + "." + ALIAS_CMD;
	public static final String DEPOSIT_PERM = BASE_CMD + "." + DEPOSIT_CMD;
	public static final String PURGE_PERM = BASE_CMD + "." + PURGE_CMD;
	public static final String WITHDRAW_PERM = BASE_CMD + "." + WITHDRAW_CMD;
	
	/** Command Descriptions */
	public static final String ACCOUNT_DESC = "Used for viewing a player's account.";
	public static final String ALIAS_DESC = "Check the aliases of a given block/item.";
	public static final String DEPOSIT_DESC = "Deposit blocks/items into your account.";
	public static final String HELP_DESC = "Gives a list of commands for the plugin, how they're used and what they do.";
	public static final String PURGE_DESC = "Delete all or a specified account.";
	public static final String WITHDRAW_DESC = "Withdraw blocks/items from your account";
	
	/** Command Usages */
	public static final String[] ACCOUNT_USAGE = {"/itembank account <block|item|custom name>", "/itembank account admin [player]"};
	public static final String ALIAS_USAGE = "/itemalias <material|alias|id:data>";
	public static final String[] DEPOSIT_USAGE = {"/deposit [item] <amount>", "/deposit customItem", "/deposit admin [player] [item] <amount>"};
	public static final String PURGE_USAGE = "/itembank purge <player>";
	public static final String[] WITHDRAW_USAGE = {"/withdraw [item] <amount>", "/withdraw customItem [item]", "/withdraw admin [player] [item] <amount>"};
	
	/** Command Help Info */
	public static final String[] ACCOUNT_HELP = {"--------" + ChatColor.DARK_RED + "ItemBank" + ChatColor.WHITE + "--------",
		"Help for the " + ChatColor.DARK_RED + "Account" + ChatColor.WHITE + " command.", "[] are required and <> are optional.",
		ChatColor.DARK_RED + "Description: " + ChatColor.WHITE + ACCOUNT_DESC, ChatColor.DARK_RED + "Player Usage: " + ChatColor.WHITE + ACCOUNT_USAGE[0],
		ChatColor.DARK_RED + "Admin Usage: " + ChatColor.WHITE + ACCOUNT_USAGE[1]};
	public static final String[] DEPOSIT_HELP = {"--------" + ChatColor.DARK_RED + "ItemBank" + ChatColor.WHITE + "--------",
		"Help for the " + ChatColor.DARK_RED + "Deposit" + ChatColor.WHITE + " command.", "[] are required and <> are optional.",
		ChatColor.DARK_RED + "Description: " + ChatColor.WHITE + DEPOSIT_DESC, ChatColor.DARK_RED + "Player Usage: " + ChatColor.WHITE + DEPOSIT_USAGE[0],
		ChatColor.DARK_RED + "Deposit Custom Item: " + ChatColor.WHITE + DEPOSIT_USAGE[1], ChatColor.DARK_RED + "Admin Usage: " + ChatColor.WHITE + DEPOSIT_USAGE[2]};
	public static final String[] ALIAS_HELP = {"--------" + ChatColor.DARK_RED + "ItemBank" + ChatColor.WHITE + "--------",
		"Help for the " + ChatColor.DARK_RED + "ItemAlias" + ChatColor.WHITE + " command.", "[] are required and <> are optional.",
		ChatColor.DARK_RED + "Description: " + ChatColor.WHITE + ALIAS_DESC, ChatColor.DARK_RED + "Player Usage: " + ChatColor.WHITE + ALIAS_USAGE};
	public static final String[] PURGE_HELP = {"--------" + ChatColor.DARK_RED + "ItemBank" + ChatColor.WHITE + "--------",
		"Help for the " + ChatColor.DARK_RED + "Purge" + ChatColor.WHITE + " command.", "[] are required and <> are optional.",
		ChatColor.DARK_RED + "Description: " + ChatColor.WHITE + PURGE_DESC, ChatColor.DARK_RED + "Usage: " + ChatColor.WHITE + PURGE_USAGE};
	public static final String[] WITHDRAW_HELP = {"--------" + ChatColor.DARK_RED + "ItemBank" + ChatColor.WHITE + "--------",
		"Help for the " + ChatColor.DARK_RED + "Withdraw" + ChatColor.WHITE + " command.", "[] are required and <> are optional.",
		ChatColor.DARK_RED + "Description: " + ChatColor.WHITE + WITHDRAW_DESC, ChatColor.DARK_RED + "Player Usage: " + ChatColor.WHITE + WITHDRAW_USAGE[0],
		ChatColor.DARK_RED + "Withdraw Custom Item: " + ChatColor.WHITE + WITHDRAW_USAGE[1], ChatColor.DARK_RED + "Admin Usage: " + ChatColor.WHITE + WITHDRAW_USAGE[2]};
	public static final String[] HELP_LIST = {"--------" + ChatColor.DARK_RED + "ItemBank" + ChatColor.WHITE + "--------",
		"Type " + ChatColor.DARK_RED + "/itembank help <command> " + ChatColor.WHITE + "for specific info.",
		ChatColor.DARK_RED + "Deposit: " + ChatColor.WHITE  + DEPOSIT_DESC, ChatColor.DARK_RED + "Purge: " + ChatColor.WHITE + PURGE_DESC,
		ChatColor.DARK_RED + "Withdraw: " + ChatColor.WHITE + WITHDRAW_DESC};
}

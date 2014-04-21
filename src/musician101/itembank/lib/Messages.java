package musician101.itembank.lib;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

public class Messages
{
	public static void init(String lang, File file)
	{
		YamlConfiguration langConfig = YamlConfiguration.loadConfiguration(file);
		ACCOUNT_ECON_SUCCESS = PREFIX + langConfig.getString(lang + Constants.ECONOMY + ".success", ACCOUNT_ECON_SUCCESS_DEFAULT);
		ACCOUNT_HELP_MSG = Arrays.asList(HEADER, ChatColor.DARK_RED + langConfig.getString(lang + Constants.HELP + ".description", HELP_DESCRIPTION) + ": " + ChatColor.WHITE + langConfig.getString(lang + Constants.HELP + ".account", ACCOUNT_HELP_DESC),
				ChatColor.DARK_RED + langConfig.getString(lang + Constants.USAGE + ".player", USAGE_PLAYER) + ": " + ChatColor.WHITE + "/account <page>",
				ChatColor.DARK_RED + langConfig.getString(lang + Constants.USAGE + ".admin", USAGE_ADMIN) + ": " + ChatColor.WHITE + "/account [player] <page>");
		
		ACCOUNT_ILLEGAL_AMOUNT = PREFIX + langConfig.getString(lang + Constants.INVENTORY + ".amount", ACCOUNT_ILLEGAL_AMOUNT_DEFAULT);
		ACCOUNT_ILLEGAL_ITEM = PREFIX + langConfig.getString(lang + Constants.INVENTORY + ".item", ACCOUNT_ILLEGAL_ITEM_DEFAULT);
		ACCOUNT_ILLEGAL_PAGE = PREFIX + langConfig.getString(lang + Constants.INVENTORY + ".page", ACCOUNT_ILLEGAL_PAGE_DEFAULT);
		ACCOUNT_ILLEGAL_STACK_AMOUNT = langConfig.getString(lang + Constants.STACK + ".amount", ACCOUNT_ILLEGAL_STACK_AMOUNT);
		ACCOUNT_ILLEGAL_STACK_EXPLAIN = PREFIX + langConfig.getString(lang + Constants.STACK + ".explain", ACCOUNT_ILLEGAL_STACK_EXPLAIN_DEFAULT);
		ACCOUNT_ILLEGAL_STACK_MAXIMUM = langConfig.getString(lang + Constants.STACK + ".maximum", ACCOUNT_ILLEGAL_STACK_MAXIMUM);
		ACCOUNT_TRANSACTION_FAIL = PREFIX + langConfig.getString(lang + Constants.ECONOMY + ".fail", ACCOUNT_TRANSACTION_FAIL_DEFAULT);
		ACCOUNT_UPDATED = PREFIX + langConfig.getString(lang + Constants.ACCOUNT + ".updated", ACCOUNT_UPDATED_DEFAULT);
		HELP_MSG = Arrays.asList(HEADER, ChatColor.DARK_RED + langConfig.getString(lang + Constants.HELP + ".version", HELP_VERSION) + ": " + ChatColor.WHITE + Bukkit.getPluginManager().getPlugin("ItemBank").getDescription().getVersion() + " " + langConfig.getString(lang + Constants.HELP + ".for", HELP_FOR) + " Bukkit 1.7.9-R0.1-SNAPSHOT.",
				ChatColor.DARK_RED + langConfig.getString(lang + Constants.HELP + ".downloads", HELP_DOWNLOADS) + ", Wiki, & " + langConfig.getString(lang + Constants.HELP + ".error", HELP_BUGS) + ": " + ChatColor.WHITE + "http://dev.bukkit.org/bukkit-plugins/item_bank/",
				ChatColor.DARK_RED + langConfig.getString(lang + Constants.HELP + ".specific", HELP_SPECIFIC) + ChatColor.WHITE + " /itembank help <command>");
		
		IO_EX = PREFIX + langConfig.getString(lang + ".ioex", IO_EX_DEFAULT);
		NEW_PLAYER_FILE = "# " + langConfig.getString(lang + ".newplayerfile", NEW_PLAYER_FILE_DEFAULT) + "\n";
		NO_FILE_EX = PREFIX + langConfig.getString(lang + ".nofile", NO_FILE_EX_DEFAULT);
		NO_PERMISSION = PREFIX + langConfig.getString(lang + ".nopermission", NO_PERMISSION_DEFAULT);
		PAGE = langConfig.getString(lang + Constants.ACCOUNT + ".page", PAGE_DEFAULT);
		PLAYER_CMD = PREFIX + langConfig.getString(lang + ".playercmd", PLAYER_CMD_DEFAULT);
		PLAYER_JOIN_FILE_FAIL = PREFIX + langConfig.getString(lang + ".failedfile", PLAYER_JOIN_FILE_FAIL_DEFAULT);
		PLAYER_DNE = PREFIX + langConfig.getString(lang + ".playerdne", PLAYER_DNE_DEFAULT);
		PURGE_HELP_MSG = Arrays.asList(HEADER, ChatColor.DARK_RED + langConfig.getString(lang + Constants.HELP + ".description", HELP_DESCRIPTION) + ": " + ChatColor.WHITE + langConfig.getString(lang + Constants.HELP + ".purge", PURGE_HELP_DESC),
				ChatColor.DARK_RED + langConfig.getString(lang + Constants.USAGE + ".default", USAGE_DEFAULT) + ": " + ChatColor.WHITE + "/itembank purge <player>");
		
		PURGE_NO_FILE = PREFIX + langConfig.getString(lang + Constants.PURGE + ".nofile", PURGE_NO_FILE_DEFAULT);
		PURGE_MULTIPLE = PREFIX + langConfig.getString(lang + Constants.SUCCESS + ".multiple", PURGE_MULTIPLE_DEFAULT);
		PURGE_SINGLE = PREFIX + langConfig.getString(lang + Constants.SUCCESS + ".single", PURGE_SINGLE_DEFAULT);
		RELOAD_HELP_MSG = Arrays.asList(HEADER, ChatColor.DARK_RED + langConfig.getString(lang + Constants.HELP + ".description", HELP_DESCRIPTION) + ": " + ChatColor.WHITE + langConfig.getString(lang + Constants.HELP + ".reload", RELOAD_HELP_DESC),
				ChatColor.DARK_RED + langConfig.getString(lang + Constants.USAGE + ".default", USAGE_DEFAULT) + ": " + ChatColor.WHITE + "/itembank reload");
		
		RELOAD_SUCCESS = PREFIX + langConfig.getString(lang + Constants.CMD + ".reload", RELOAD_SUCCESS_DEFAULT);
		SQL_EX = PREFIX + langConfig.getString(lang + ".sqlex", SQL_EX_DEFAULT);
		UPDATER_CURRENT = langConfig.getString(lang + Constants.UPDATER + ".current", UPDATER_CURRENT_DEFAULT);
		UPDATER_ERROR = langConfig.getString(lang + Constants.UPDATER + ".error", UPDATER_ERROR_DEFAULT);
		UPDATER_NEW = langConfig.getString(lang + Constants.UPDATER + ".new", UPDATER_NEW_DEFAULT);
		UUID_HELP_MSG = Arrays.asList(HEADER, ChatColor.DARK_RED + langConfig.getString(lang + Constants.HELP + ".description", HELP_DESCRIPTION) + ": " + ChatColor.WHITE + langConfig.getString(lang + Constants.HELP + ".uuid", UUID_HELP_DESC),
				ChatColor.DARK_RED + langConfig.getString(lang + Constants.USAGE + ".default", USAGE_DEFAULT) + ": " + ChatColor.WHITE + "/itembank uuid [player]");
		VAULT_BOTH_ENABLED = langConfig.getString(lang + Constants.VAULT + ".bothenabled", VAULT_BOTH_ENABLED_DEFAULT);
		VAULT_NO_CONFIG = langConfig.getString(lang + Constants.VAULT + ".noconfig", VAULT_NO_CONFIG_DEFAULT);
		VAULT_NOT_INSTALLED = langConfig.getString(lang + Constants.VAULT + ".novault", VAULT_NOT_INSTALLED_DEFAULT);
		YAML_PARSE_EX = PREFIX + langConfig.getString(lang + ".yamlparseex", YAML_PARSE_EX_DEFAULT);
		
		if (!langConfig.isSet(lang + ".yamlparseex"))
		{
			Bukkit.getPluginManager().getPlugin("ItemBank").getLogger().warning("An update has changed language configuration slightly. Please change yamlex to yamlparseex. This warning will only be in this version of the plugin.");
			Bukkit.getPluginManager().getPlugin("ItemBank").getLogger().warning("This warning will only be in this version of the plugin (ItemBank v" + Bukkit.getPluginManager().getPlugin("ItemBank").getDescription().getVersion() + ").");
			YAML_PARSE_EX = PREFIX + langConfig.getString(lang + ".yamlex", YAML_PARSE_EX_DEFAULT);
		}
	}
	
	/** Formatting */
	public static final String HEADER = "--------" + ChatColor.DARK_RED + Bukkit.getPluginManager().getPlugin("ItemBank").getDescription().getPrefix() + ChatColor.WHITE + "--------";
	public static final String PREFIX = ChatColor.DARK_RED + "[" + Bukkit.getPluginManager().getPlugin("ItemBank").getDescription().getPrefix() + "] ";
	
	/** Command Help */
	public static final String ACCOUNT_HELP_DESC = "Opens a GUI with the items stored in the account.";
	public static List<String> ACCOUNT_HELP_MSG;
	public static final String HELP_BUGS = "Bug Reporting";
	public static final String HELP_DESCRIPTION = "Description";
	public static final String HELP_DOWNLOADS = "Downloads";
	public static final String HELP_FOR = "for";
	public static List<String> HELP_MSG;
	public static final String HELP_SPECIFIC = "For specific command help use";
	public static final String HELP_VERSION = "Version";
	public static final String PURGE_HELP_DESC = "Delete all or a specified player's account.";
	public static List<String> PURGE_HELP_MSG;
	public static final String RELOAD_HELP_DESC = "Reloads the plugin's config file.";
	public static List<String> RELOAD_HELP_MSG;
	public static final String USAGE_ADMIN = "Admin Usage";
	public static final String USAGE_DEFAULT = "Usage";
	public static final String USAGE_PLAYER = "Player Usage";
	public static final String UUID_HELP_DESC = "Get a player's UUID.";
	public static List<String> UUID_HELP_MSG;
	
	/** Command Success */
	public static String ACCOUNT_ECON_SUCCESS;
	public static final String ACCOUNT_ECON_SUCCESS_DEFAULT = "A $ transaction fee has been deducted from your account.";
	
	public static String ACCOUNT_UPDATED;
	public static final String ACCOUNT_UPDATED_DEFAULT = "Account updated.";
	
	public static String PURGE_MULTIPLE;
	public static final String PURGE_MULTIPLE_DEFAULT = "All accounts have been reset.";
	
	public static String PURGE_SINGLE;
	public static final String PURGE_SINGLE_DEFAULT = "Account reset.";
	
	public static String RELOAD_SUCCESS;
	public static final String RELOAD_SUCCESS_DEFAULT = "Config reloaded.";
	
	/** Error Messages */
	public static String ACCOUNT_ILLEGAL_AMOUNT;
	public static final String ACCOUNT_ILLEGAL_AMOUNT_DEFAULT = "You are unable to add this item to your account.";
	
	public static String ACCOUNT_ILLEGAL_ITEM;
	public static final String ACCOUNT_ILLEGAL_ITEM_DEFAULT = "This item is non depositable.";
	
	public static String ACCOUNT_ILLEGAL_PAGE;
	public static final String ACCOUNT_ILLEGAL_PAGE_DEFAULT = "You cannot add items to this page.";
	
	public static String ACCOUNT_ILLEGAL_STACK_AMOUNT;
	public static final String ACCOUNT_ILLEGAL_STACK_AMOUNT_DEFAULT = "Amount in account";
	
	public static String ACCOUNT_ILLEGAL_STACK_EXPLAIN;
	public static final String ACCOUNT_ILLEGAL_STACK_EXPLAIN_DEFAULT = "The stack you selected puts you over the limit. Please split the stack and try again.";
	
	public static String ACCOUNT_ILLEGAL_STACK_MAXIMUM;
	public static final String ACCOUNT_ILLEGAL_STACK_MAXIMUM_DEFAULT = "Maximum";
	
	public static String ACCOUNT_TRANSACTION_FAIL;
	public static final String ACCOUNT_TRANSACTION_FAIL_DEFAULT = "A $ transaction fee has been deducted from your account.";
	
	public static String NO_PERMISSION;
	public static final String NO_PERMISSION_DEFAULT = "Error: You do not have permission for this command.";
	
	public static String PLAYER_CMD;
	public static final String PLAYER_CMD_DEFAULT = "Error: This is a player command only.";
	
	public static String PLAYER_JOIN_FILE_FAIL;
	public static final String PLAYER_JOIN_FILE_FAIL_DEFAULT = "There was an error in creating your account file. Please contact an administrator immediately.";
	
	public static String PLAYER_DNE;
	public static final String PLAYER_DNE_DEFAULT = "Error: Player not found. Check for spelling, capitalization, and if the player has ever logged onto the server.";
	
	public static String PURGE_NO_FILE;
	public static final String PURGE_NO_FILE_DEFAULT = "File not found. Please check spelling and capitalization.";
	
	/** Exception Messages */
	public static String IO_EX;
	public static final String IO_EX_DEFAULT = "Error: An internal error has occurred. Please contact an administrator immediately.";
	
	public static String NO_FILE_EX;
	public static final String NO_FILE_EX_DEFAULT = "Error: File not found. Please contact an administrator immediately.";
	
	public static String SQL_EX;
	public static final String SQL_EX_DEFAULT = "Error: Unable to connect to the database.";
	
	public static String YAML_PARSE_EX;
	public static final String YAML_PARSE_EX_DEFAULT = "Error: Your account contains format errors. Please contact an administrator immediately.";
	
	/** Updater Messages */
	public static String UPDATER_CURRENT;
	public static final String UPDATER_CURRENT_DEFAULT = "A new version is available.";
	
	public static String UPDATER_ERROR;
	public static final String UPDATER_ERROR_DEFAULT = "The current version is the latest.";
	
	public static String UPDATER_NEW;
	public static final String UPDATER_NEW_DEFAULT = "Error: Update check failed.";
	
	/** Vault Messages */
	public static String VAULT_BOTH_ENABLED;
	public static final String VAULT_BOTH_ENABLED_DEFAULT = "Vault detected and enabled in config. Using Vault for monetary transactions.";
	
	public static String VAULT_NO_CONFIG;
	public static final String VAULT_NO_CONFIG_DEFAULT = "Vault detected but disabled in config. No monetary transactions will occur.";
	
	public static String VAULT_NOT_INSTALLED;
	public static final String VAULT_NOT_INSTALLED_DEFAULT = "Error detecting Vault. Is it installed?";
	
	/** Other */
	public static String NEW_PLAYER_FILE;
	public static final String NEW_PLAYER_FILE_DEFAULT = "Do not edit this file unless it's absolutely necessary.";
	
	public static String PAGE;
	public static final String PAGE_DEFAULT = "Page";
}

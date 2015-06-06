package musician101.itembank.forge.lib;

import java.util.Arrays;
import java.util.List;

import musician101.itembank.forge.lib.Constants.ModInfo;
import musician101.itembank.forge.util.IBUtils;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class Messages
{
	//TODO re-add proper language support when conversion is complete
	/** Formatting */
	private static final EnumChatFormatting darkRed = EnumChatFormatting.DARK_RED;
	public static final IChatComponent HEADER = IBUtils.getChatComponent("===== ItemBank =====", darkRed);
	public static final IChatComponent PREFIX = IBUtils.getChatComponent("[ItemBank] ", darkRed);
	
	//TODO left off here
	/** Command Help */
	public static final List<IChatComponent> ACCOUNT_HELP_MSG = Arrays.asList(HEADER,
			IBUtils.getChatComponent("Description", darkRed).appendSibling(IBUtils.getChatComponent(": Opens a GUI with the items stored in the account.")),
			IBUtils.getChatComponent("Player Usage", darkRed).appendSibling(IBUtils.getChatComponent(": /account [page]")),
			IBUtils.getChatComponent("Admin Usage", darkRed).appendSibling(IBUtils.getChatComponent(": /account <player> [page]")));
	public static final List<IChatComponent> HELP_MSG = Arrays.asList(HEADER,
			IBUtils.getChatComponent("Version", darkRed).appendSibling(IBUtils.getChatComponent(": " + ModInfo.VERSION)),
			IBUtils.getChatComponent("Downloads, Wiki, & Bug Reporting", darkRed).appendSibling(IBUtils.getChatComponent(": https://github.com/musician101/itembank")),
			IBUtils.getChatComponent("For specific command help, use", darkRed).appendSibling(IBUtils.getChatComponent("/itembank help <command>")));
	public static final List<IChatComponent> PURGE_HELP_MSG = Arrays.asList(HEADER,
			IBUtils.getChatComponent("Description", darkRed).appendSibling(IBUtils.getChatComponent(": Delete all or a specified player's account.")),
			IBUtils.getChatComponent("Usage", darkRed).appendSibling(IBUtils.getChatComponent(": /itembank purge [player]")));
	
	/** Command Success */
	//public static String ACCOUNT_ECON_SUCCESS;
	//public static final String ACCOUNT_ECON_SUCCESS_DEFAULT = "A $ transaction fee has been deducted from your account.";
	public static final IChatComponent ACCOUNT_UPDATED = PREFIX.appendSibling(IBUtils.getChatComponent("Account updated."));
	public static final IChatComponent PURGE_MULTIPLE = PREFIX.appendSibling(IBUtils.getChatComponent("All accounts have been reset."));
	public static final IChatComponent PURGE_SINGLE = PREFIX.appendSibling(IBUtils.getChatComponent("Account reset."));
	public static final IChatComponent RELOAD_SUCCESS = PREFIX.appendSibling(IBUtils.getChatComponent("Config reloaded."));
	
	/** Error Messages */
	public static final IChatComponent ACCOUNT_ILLEGAL_AMOUNT = PREFIX.appendSibling(IBUtils.getChatComponent("You are unable to add this item to your account."));
	public static final IChatComponent ACCOUNT_ILLEGAL_ITEM = PREFIX.appendSibling(IBUtils.getChatComponent("This item is non depositable."));
	public static final IChatComponent ACCOUNT_ILLEGAL_PAGE = PREFIX.appendSibling(IBUtils.getChatComponent("You cannot add items to this page."));
	public static final IChatComponent ACCOUNT_ILLEGAL_STACK_AMOUNT = PREFIX.appendSibling(IBUtils.getChatComponent("Amount in account"));
	public static final IChatComponent ACCOUNT_ILLEGAL_STACK_EXPLAIN = PREFIX.appendSibling(IBUtils.getChatComponent("The stack you selected puts you over the limit. Please split the stack and try again."));
	//public static String ACCOUNT_TRANSACTION_FAIL;
	//public static final String ACCOUNT_TRANSACTION_FAIL_DEFAULT = "A $ transaction fee has been deducted from your account.";
	public static final IChatComponent NO_PERMISSION = PREFIX.appendSibling(IBUtils.getChatComponent("Error: You do not have permission for this command."));
	public static final IChatComponent PLAYER_CMD = PREFIX.appendSibling(IBUtils.getChatComponent("Error: This is a player command only."));
	public static final IChatComponent PLAYER_JOIN_FILE_FAIL = PREFIX.appendSibling(IBUtils.getChatComponent("There was an error in creating your account file. Please contact an administrator immediately."));
	public static final IChatComponent PLAYER_DNE = PREFIX.appendSibling(IBUtils.getChatComponent("Error: Player not found. Check for spelling, capitalization, and if the player has ever logged onto the server."));
	public static final IChatComponent PURGE_NO_FILE = PREFIX.appendSibling(IBUtils.getChatComponent("File not found. Please check spelling and capitalization."));
	
	/** Exception Messages */
	public static final IChatComponent IO_EX = PREFIX.appendSibling(IBUtils.getChatComponent("Error: An internal error has occurred. Please contact an administrator immediately."));
	public static final IChatComponent NO_FILE_EX = PREFIX.appendSibling(IBUtils.getChatComponent("Error: File not found. Please contact an administrator immediately."));
	//public static String SQL_EX;
	//public static final String SQL_EX_DEFAULT = "Error: Unable to connect to the database.";
	
	/** Updater Messages */
	//public static String UPDATER_CURRENT;
	//public static final String UPDATER_CURRENT_DEFAULT = "A new version is available.";
	//public static String UPDATER_ERROR;
	//public static final String UPDATER_ERROR_DEFAULT = "The current version is the latest.";
	//public static String UPDATER_NEW;
	//public static final String UPDATER_NEW_DEFAULT = "Error: Update check failed.";
	
	/** Vault Messages */
	//public static String VAULT_BOTH_ENABLED;
	//public static final String VAULT_BOTH_ENABLED_DEFAULT = "Vault detected and enabled in config. Using Vault for monetary transactions.";
	//public static String VAULT_NO_CONFIG;
	//public static final String VAULT_NO_CONFIG_DEFAULT = "Vault detected but disabled in config. No monetary transactions will occur.";
	//public static String VAULT_NOT_INSTALLED;
	//public static final String VAULT_NOT_INSTALLED_DEFAULT = "Error detecting Vault. Is it installed?";
	
	/** Other */
	public static final String NEW_PLAYER_FILE = "Do not edit this file unless it's absolutely necessary.";
}

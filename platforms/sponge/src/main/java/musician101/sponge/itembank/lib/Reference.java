package musician101.sponge.itembank.lib;

import java.io.File;

import musician101.sponge.itembank.ItemBank;
import musician101.sponge.itembank.config.SpongeJSONConfig;
import musician101.sponge.itembank.util.IBUtils;

import org.slf4j.Logger;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class Reference
{
	public static final String ID = "IB";
	public static final String NAME = "ItemBank";
	public static final String VERSION = "3.0";
	
	public static class Messages
	{
		public static void init(String language, File file)
		{
			Logger log = ItemBank.getLogger();
			SpongeJSONConfig langConfig;
			try
			{
				langConfig = SpongeJSONConfig.loadSpongeJSONConfig(file);
			}
			catch (Exception e)
			{
				log.warn("An error occurred while parsing lang.json. Falling back to default messages.");
				langConfig = new SpongeJSONConfig();
			}
			
			SpongeJSONConfig lang = null;
			if (!langConfig.isSet(language))
			{
				log.warn("Could not find " + language + " in lang.json. Attempting to default to 'en'");
				if (!langConfig.isSet("en"))
				{
					log.warn("Could not find 'en' in lang.json. Falling back on hardcoded messages.");
					lang = new SpongeJSONConfig();
				}
				else
					langConfig.getSpongeJSONConfig("en");
			}
			else
				lang = langConfig.getSpongeJSONConfig(language);
			
			initCommandTranslations(log, language, langConfig, "command");
			initErrorTranslations(log, language, langConfig, "error");
			NEW_PLAYER_FILE = lang.getString("newplayerfile", NEW_PLAYER_FILE_DEFAULT);
		}
		
		private static void initCommandTranslations(Logger log, String path, SpongeJSONConfig json, String key)
		{
			SpongeJSONConfig cmd = getSpongeJSONConfig(log, path + "." + key, json, key);
			initAccountCommandTranslations(log, path + "." + key, cmd, "account");
			initPurgeCommandTranslations(log, path + "." + key, cmd, "purge");
			initReloadCommandTranslations(log, path + "." + key, cmd, "reload");
			initUUIDCommandTranslations(log, path + "." + key, cmd, "uuid");
		}
		
		private static void initAccountCommandTranslations(Logger log, String path, SpongeJSONConfig json, String key)
		{
			SpongeJSONConfig account = getSpongeJSONConfig(log, path + "." + key, json, key);
			ACCOUNT_DESC = IBUtils.stringToText(account.getString("description", ACCOUNT_DESC_DEFAULT), TextColors.DARK_RED);
			ACCOUNT_DESC2 = IBUtils.stringToText(account.getString("description2", ACCOUNT_DESC2_DEFAULT), TextColors.DARK_RED);
			ACCOUNT_PAGE = account.getString("page", ACCOUNT_PAGE_DEFAULT);
			ACCOUNT_UPDATED = IBUtils.stringToText(account.getString("updated", ACCOUNT_UPDATED_DEFAULT), TextColors.DARK_RED);
			ACCOUNT_WORLD_DNE = IBUtils.stringToText(PREFIX + account.getString("worlddne", ACCOUNT_WORLD_DNE_DEFAULT), TextColors.DARK_RED);
			initAccountInventoryTranslations(log, path + "." + key, account, "inventory");
		}
		
		private static void initAccountInventoryTranslations(Logger log, String path, SpongeJSONConfig json, String key)
		{
			SpongeJSONConfig inventory = getSpongeJSONConfig(log, path + "." + key, json, key);
			ACCOUNT_ILLEGAL_AMOUNT = IBUtils.stringToText(PREFIX + inventory.getString("", ACCOUNT_ILLEGAL_AMOUNT_DEFAULT), TextColors.DARK_RED);
			ACCOUNT_ILLEGAL_ITEM = IBUtils.stringToText(PREFIX + inventory.getString("item", ACCOUNT_ILLEGAL_ITEM_DEFAULT), TextColors.DARK_RED);
			ACCOUNT_ILLEGAL_PAGE = IBUtils.stringToText(PREFIX + inventory.getString("page", ACCOUNT_ILLEGAL_PAGE_DEFAULT), TextColors.DARK_RED);
		}
		
		private static void initPurgeCommandTranslations(Logger log, String path, SpongeJSONConfig json, String key)
		{
			SpongeJSONConfig purge = getSpongeJSONConfig(log, path + "." + key, json, key);
			PURGE_DESC = IBUtils.stringToText(purge.getString("description", PURGE_DESC_DEFAULT));
			PURGE_NO_FILE = IBUtils.stringToText(PREFIX + purge.getString("nofile", PURGE_NO_FILE_DEFAULT), TextColors.DARK_RED);
			initPurgeSuccessTranslations(log, path + "." + key, purge, "success");
		}
		
		private static void initPurgeSuccessTranslations(Logger log, String path, SpongeJSONConfig json, String key)
		{
			SpongeJSONConfig success = getSpongeJSONConfig(log, path + "." + key, json, key);
			PURGE_SINGLE = IBUtils.stringToText(PREFIX + success.getString("single", PURGE_SINGLE_DEFAULT), TextColors.DARK_RED);
			PURGE_MULTIPLE = IBUtils.stringToText(PREFIX + success.getString("multiple", PURGE_MULTIPLE_DEFAULT), TextColors.DARK_RED);
		}
		
		private static void initReloadCommandTranslations(Logger log, String path, SpongeJSONConfig json, String key)
		{
			SpongeJSONConfig reload = getSpongeJSONConfig(log, path + "." + key, json, key);
			RELOAD_DESC = IBUtils.stringToText(reload.getString("description", RELOAD_DESC_DEFAULT));
			RELOAD_SUCCESS = IBUtils.stringToText(reload.getString("success", RELOAD_SUCCESS_DEFAULT), TextColors.DARK_RED);
		}
		
		private static void initUUIDCommandTranslations(Logger log, String path, SpongeJSONConfig json, String key)
		{
			SpongeJSONConfig uuid = getSpongeJSONConfig(log, path + "." + key, json, key);
			UUID_DESC = IBUtils.stringToText(uuid.getString("description", UUID_DESC_DEFAULT));
		}
		
		private static void initErrorTranslations(Logger log, String path, SpongeJSONConfig json, String key)
		{
			SpongeJSONConfig error = getSpongeJSONConfig(log, path + "." + key, json, key);
			IO_EX = IBUtils.stringToText(PREFIX + error.getString("ioex", IO_EX_DEFAULT), TextColors.DARK_RED);
			NO_FILE_EX = IBUtils.stringToText(PREFIX + error.getString("nofile", NO_FILE_EX_DEFAULT), TextColors.DARK_RED);
			NO_PERMISSION = IBUtils.stringToText(PREFIX + error.getString("nopermission", NO_PERMISSION_DEFAULT), TextColors.DARK_RED);
			PLAYER_CMD = IBUtils.stringToText(PREFIX + error.getString("playercmd", PLAYER_CMD_DEFAULT), TextColors.DARK_RED);
			PLAYER_DNE = IBUtils.stringToText(PREFIX + error.getString("playerdne", PLAYER_DNE_DEFAULT), TextColors.DARK_RED);
			SQL_EX = IBUtils.stringToText(PREFIX + error.getString("sqlex", SQL_EX_DEFAULT), TextColors.DARK_RED);
			UNKNOWN_EX = IBUtils.stringToText(PREFIX + error.getString("unknownex", UNKNOWN_EX_DEFAULT), TextColors.DARK_RED);
		}
		
		private static SpongeJSONConfig getSpongeJSONConfig(Logger log, String path, SpongeJSONConfig json, String key)
		{
			if (!json.isSet(key))
			{
				log.warn("Could not find " + path + "." + key + " in lang.yml");
				return new SpongeJSONConfig();
			}
			
			return json.getSpongeJSONConfig(key);
		}
		
		/* Formatting */
		public static final Text HEADER = IBUtils.joinTexts(IBUtils.stringToText("====="), IBUtils.stringToText(Reference.NAME + " v" + Reference.VERSION, TextColors.DARK_RED), IBUtils.stringToText("====="));
		public static final String PREFIX = "[" + Reference.NAME + "] ";
		
		/* Command Descriptions */
		public static Text ACCOUNT_DESC;
		private static final String ACCOUNT_DESC_DEFAULT = "Opens a GUI with the items stored in the account.";
		
		public static Text ACCOUNT_DESC2;
		private static final String ACCOUNT_DESC2_DEFAULT = "Opens a page with the specified parameters.";
		
		public static Text ACCOUNT_UPDATED;
		private static final String ACCOUNT_UPDATED_DEFAULT = "Account updated.";
		
		public static Text PURGE_DESC;
		public static final String PURGE_DESC_DEFAULT = "Delete all or a specified player's account.";
		
		public static Text RELOAD_DESC;
		public static final String RELOAD_DESC_DEFAULT = "Reloads the plugin's config file.";
		
		public static Text UUID_DESC;
		public static final String UUID_DESC_DEFAULT = "Get a player's UUID.";
		
		/* Command Success */
		public static String ACCOUNT_ECON_SUCCESS;
		public static final String ACCOUNT_ECON_SUCCESS_DEFAULT = "A $ transaction fee has been deducted from your account.";
		
		public static Text PURGE_MULTIPLE;
		public static final String PURGE_MULTIPLE_DEFAULT = "All accounts have been reset.";
		
		public static Text PURGE_SINGLE;
		public static final String PURGE_SINGLE_DEFAULT = "Account reset.";
		
		public static Text RELOAD_SUCCESS;
		public static final String RELOAD_SUCCESS_DEFAULT = "Config reloaded.";
		
		/* Error Messages */
		public static Text ACCOUNT_ILLEGAL_AMOUNT;
		public static final String ACCOUNT_ILLEGAL_AMOUNT_DEFAULT = "You are unable to add this item to your account.";
		
		public static Text ACCOUNT_ILLEGAL_ITEM;
		public static final String ACCOUNT_ILLEGAL_ITEM_DEFAULT = "This item is non depositable.";
		
		public static Text ACCOUNT_ILLEGAL_PAGE;
		public static final String ACCOUNT_ILLEGAL_PAGE_DEFAULT = "You cannot add items to this page.";
		
		public static String ACCOUNT_ILLEGAL_STACK_AMOUNT;
		public static final String ACCOUNT_ILLEGAL_STACK_AMOUNT_DEFAULT = "Amount in account";
		
		public static Text ACCOUNT_WORLD_DNE;
		public static final String ACCOUNT_WORLD_DNE_DEFAULT = "That world does not exist.";
		
		public static Text NO_PERMISSION;
		public static final String NO_PERMISSION_DEFAULT = "Error: You do not have permission for this command.";
		
		public static Text PLAYER_CMD;
		public static final String PLAYER_CMD_DEFAULT = "Error: This is a player command only.";
		
		public static String PLAYER_JOIN_FILE_FAIL;
		public static final String PLAYER_JOIN_FILE_FAIL_DEFAULT = "There was an error in creating your account file. Please contact an administrator immediately.";
		
		public static Text PLAYER_DNE;
		public static final String PLAYER_DNE_DEFAULT = "Error: Player not found. Check for spelling, capitalization, and if the player has ever logged onto the server.";
		
		public static Text PURGE_NO_FILE;
		public static final String PURGE_NO_FILE_DEFAULT = "File not found. Please check spelling and capitalization.";
		
		/* Exception Messages */
		public static Text IO_EX;
		public static final String IO_EX_DEFAULT = "Error: An internal error has occurred. Please contact an administrator immediately.";
		
		public static Text NO_FILE_EX;
		public static final String NO_FILE_EX_DEFAULT = "Error: File not found. Please contact an administrator immediately.";
		
		public static Text SQL_EX;
		public static final String SQL_EX_DEFAULT = "Error: Unable to connect to the database.";
		
		public static Text UNKNOWN_EX;
		public static final String UNKNOWN_EX_DEFAULT = "An unknown error has occurred while obtaining the player's UUID.";
		
		/** Other */
		public static String ACCOUNT_PAGE;
		private static final String ACCOUNT_PAGE_DEFAULT = "Page";
		
		public static String NEW_PLAYER_FILE;
		public static final String NEW_PLAYER_FILE_DEFAULT = "Do not edit this file unless it's absolutely necessary.";
	}
	
	/*public static class Constants
	{
		/** Commands Names *
		public static final String ACCOUNT_CMD = "account";
		public static final String HELP_CMD = "help";
		public static final String ITEMBANK_CMD = "itembank";
		public static final String PURGE_CMD = "purge";
		public static final String RELOAD_CMD = "reload";
		public static final String UUID_CMD = "uuid";
		
		/** Config *
		public static final String ITEMLIST = "itemlist";
		public static final String ENABLE_VAULT = "enableVault";
		public static final String FORMAT = "format";
		public static final String LANG = "lang";
		public static final String MULTI_WORLD = "multiWorld";
		public static final String PAGE_LIMIT = "pageLimit";
		public static final String TRANSACTION_COST = "transactionCost";
		public static final String UPDATE_CHECK = "updateCheck";
		
		public static final String MYSQL = "mysql.";
		public static final String DATABASE = MYSQL + "database";
		public static final String ENABLE = MYSQL + "enable";
		public static final String HOST = MYSQL + "host";
		public static final String PASS = MYSQL + "pass";
		public static final String PORT = MYSQL + "port";
		public static final String USER = MYSQL + "user";
		
		/** Language Config *
		public static final String CMD = ".command";
		public static final String ACCOUNT = CMD + "." + ACCOUNT_CMD;
		public static final String ECONOMY = ACCOUNT + ".economy";
		public static final String INVENTORY = ACCOUNT + ".inventory";
		public static final String STACK = INVENTORY + ".stack";
		public static final String HELP = CMD + "." + HELP_CMD;
		public static final String DEFAULT = HELP + ".default";
		public static final String USAGE = HELP + ".usage";
		public static final String PURGE = CMD + "." + PURGE_CMD;
		public static final String SUCCESS = PURGE + ".success";
		public static final String RELOAD = CMD + ".reload";
		public static final String UPDATER = ".updater";
		public static final String VAULT = ".vault";
		
		/** Permissions *
		public static final String ACCOUNT_PERM = ITEMBANK_CMD + "." + ACCOUNT_CMD;
		public static final String ADMIN_PERM = ITEMBANK_CMD + ".admin";
		public static final String ADMIN_ACCOUNT_PERM = ADMIN_PERM + "." + ACCOUNT_CMD;
		public static final String EXEMPT_PERM = ADMIN_PERM + ".exempt";
		public static final String PURGE_PERM = ADMIN_PERM + "." + PURGE_CMD;
		public static final String RELOAD_PERM = ADMIN_PERM + "." + RELOAD_CMD;
		public static final String UUID_PERM = ADMIN_PERM + "." + UUID_CMD;
	}*/
}

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
			Logger logger = ItemBank.logger;
			SpongeJSONConfig langConfig;
			try
			{
				langConfig = SpongeJSONConfig.loadSpongeJSONConfig(file);
			}
			catch (Exception e)
			{
				logger.warn("An error occurred while parsing lang.json. Falling back to default messages.");
				langConfig = new SpongeJSONConfig();
			}
			
			SpongeJSONConfig lang = null;
			if (!langConfig.isSet(language))
			{
				logger.warn("Could not find " + language + " in lang.json. Attempting to default to 'en'");
				if (!langConfig.isSet("en"))
				{
					logger.warn("Could not find 'en' in lang.json. Falling back on hardcoded messages.");
					lang = new SpongeJSONConfig();
				}
				else
					langConfig.getSpongeJSONConfig("en");
			}
			else
				lang = langConfig.getSpongeJSONConfig(language);
			
			initCommandTranslations(logger, language, langConfig, "command");
			initErrorTranslations(logger, language, langConfig, "error");
			NEW_PLAYER_FILE = lang.getString("newplayerfile", NEW_PLAYER_FILE_DEFAULT);
		}
		
		private static void initCommandTranslations(Logger logger, String path, SpongeJSONConfig json, String key)
		{
			SpongeJSONConfig cmd = getSpongeJSONConfig(logger, path + "." + key, json, key);
			ITEMBANK_DESC = IBUtils.stringToText(json.getString("itembank_description", ITEMBANK_DESC_DEFAULT) + " (v" + Reference.VERSION + ")", TextColors.DARK_RED);
			initAccountCommandTranslations(logger, path + "." + key, cmd, "account");
			initPurgeCommandTranslations(logger, path + "." + key, cmd, "purge");
		}
		
		private static void initAccountCommandTranslations(Logger logger, String path, SpongeJSONConfig json, String key)
		{
			SpongeJSONConfig account = getSpongeJSONConfig(logger, path + "." + key, json, key);
			ACCOUNT_DESC = IBUtils.stringToText(account.getString("description", ACCOUNT_DESC_DEFAULT), TextColors.DARK_RED);
			ACCOUNT_PAGE = account.getString("page", ACCOUNT_PAGE_DEFAULT);
			ACCOUNT_UPDATED = IBUtils.stringToText(account.getString("updated", ACCOUNT_UPDATED_DEFAULT), TextColors.DARK_RED);
			ACCOUNT_WORLD_DNE = IBUtils.stringToText(PREFIX + account.getString("worlddne", ACCOUNT_WORLD_DNE_DEFAULT), TextColors.DARK_RED);
			initAccountInventoryTranslations(logger, path + "." + key, account, "inventory");
		}
		
		private static void initAccountInventoryTranslations(Logger logger, String path, SpongeJSONConfig json, String key)
		{
			SpongeJSONConfig inventory = getSpongeJSONConfig(logger, path + "." + key, json, key);
			ACCOUNT_ILLEGAL_AMOUNT = IBUtils.stringToText(PREFIX + inventory.getString("", ACCOUNT_ILLEGAL_AMOUNT_DEFAULT), TextColors.DARK_RED);
			ACCOUNT_ILLEGAL_ITEM = IBUtils.stringToText(PREFIX + inventory.getString("item", ACCOUNT_ILLEGAL_ITEM_DEFAULT), TextColors.DARK_RED);
			ACCOUNT_ILLEGAL_PAGE = IBUtils.stringToText(PREFIX + inventory.getString("page", ACCOUNT_ILLEGAL_PAGE_DEFAULT), TextColors.DARK_RED);
		}
		
		private static void initPurgeCommandTranslations(Logger logger, String path, SpongeJSONConfig json, String key)
		{
			SpongeJSONConfig purge = getSpongeJSONConfig(logger, path + "." + key, json, key);
			PURGE_DESC = IBUtils.stringToText(purge.getString("description", PURGE_DESC_DEFAULT));
			PURGE_NO_FILE = IBUtils.stringToText(PREFIX + purge.getString("nofile", PURGE_NO_FILE_DEFAULT), TextColors.DARK_RED);
			initPurgeSuccessTranslations(logger, path + "." + key, purge, "success");
		}
		
		private static void initPurgeSuccessTranslations(Logger logger, String path, SpongeJSONConfig json, String key)
		{
			SpongeJSONConfig success = getSpongeJSONConfig(logger, path + "." + key, json, key);
			PURGE_SINGLE = IBUtils.stringToText(PREFIX + success.getString("single", PURGE_SINGLE_DEFAULT), TextColors.DARK_RED);
			PURGE_MULTIPLE = IBUtils.stringToText(PREFIX + success.getString("multiple", PURGE_MULTIPLE_DEFAULT), TextColors.DARK_RED);
		}
		
		private static void initErrorTranslations(Logger logger, String path, SpongeJSONConfig json, String key)
		{
			SpongeJSONConfig error = getSpongeJSONConfig(logger, path + "." + key, json, key);
			IO_EX = IBUtils.stringToText(PREFIX + error.getString("ioex", IO_EX_DEFAULT), TextColors.DARK_RED);
			NO_FILE_EX = IBUtils.stringToText(PREFIX + error.getString("nofile", NO_FILE_EX_DEFAULT), TextColors.DARK_RED);
			NO_PERMISSION = IBUtils.stringToText(PREFIX + error.getString("nopermission", NO_PERMISSION_DEFAULT), TextColors.DARK_RED);
			PARSE_EX = IBUtils.stringToText(PREFIX + error.getString("parseex", PARSE_EX_DEFAULT), TextColors.DARK_RED);
			PLAYER_CMD = IBUtils.stringToText(PREFIX + error.getString("playercmd", PLAYER_CMD_DEFAULT), TextColors.DARK_RED);
			PLAYER_DNE = IBUtils.stringToText(PREFIX + error.getString("playerdne", PLAYER_DNE_DEFAULT), TextColors.DARK_RED);
			SQL_EX = IBUtils.stringToText(PREFIX + error.getString("sqlex", SQL_EX_DEFAULT), TextColors.DARK_RED);
			UNKNOWN_EX = IBUtils.stringToText(PREFIX + error.getString("unknownex", UNKNOWN_EX_DEFAULT), TextColors.DARK_RED);
		}
		
		private static SpongeJSONConfig getSpongeJSONConfig(Logger logger, String path, SpongeJSONConfig json, String key)
		{
			if (!json.isSet(key))
			{
				logger.warn("Could not find " + path + "." + key + " in lang.yml");
				return new SpongeJSONConfig();
			}
			
			return json.getSpongeJSONConfig(key);
		}
		
		/* Formatting */
		public static final Text HEADER = IBUtils.joinTexts(IBUtils.stringToText("====="), IBUtils.stringToText(Reference.NAME + " v" + Reference.VERSION, TextColors.DARK_RED), IBUtils.stringToText("====="));
		public static final String PREFIX = "[" + Reference.NAME + "] ";
		
		/* Command Descriptions */
		public static Text ACCOUNT_DESC;
		private static final String ACCOUNT_DESC_DEFAULT = "Opens a page with the specified parameters.";
		
		public static Text ACCOUNT_UPDATED;
		private static final String ACCOUNT_UPDATED_DEFAULT = "Account updated.";
		
		public static Text ITEMBANK_DESC;
		private static final String ITEMBANK_DESC_DEFAULT = "Virtual chest with configurable limits.";
		
		public static Text PURGE_DESC;
		private static final String PURGE_DESC_DEFAULT = "Delete all or a specified player's account.";
		
		/* Command Success */
		public static Text PURGE_MULTIPLE;
		private static final String PURGE_MULTIPLE_DEFAULT = "All accounts have been reset.";
		
		public static Text PURGE_SINGLE;
		private static final String PURGE_SINGLE_DEFAULT = "Account reset.";
		
		/* Error Messages */
		public static Text ACCOUNT_ILLEGAL_AMOUNT;
		private static final String ACCOUNT_ILLEGAL_AMOUNT_DEFAULT = "You are unable to add this item to your account.";
		
		public static Text ACCOUNT_ILLEGAL_ITEM;
		private static final String ACCOUNT_ILLEGAL_ITEM_DEFAULT = "This item is non depositable.";
		
		public static Text ACCOUNT_ILLEGAL_PAGE;
		private static final String ACCOUNT_ILLEGAL_PAGE_DEFAULT = "You cannot add items to this page.";
		
		public static Text ACCOUNT_WORLD_DNE;
		private static final String ACCOUNT_WORLD_DNE_DEFAULT = "That world does not exist.";
		
		public static Text NO_PERMISSION;
		private static final String NO_PERMISSION_DEFAULT = "Error: You do not have permission for this command.";
		
		public static Text PLAYER_CMD;
		private static final String PLAYER_CMD_DEFAULT = "Error: This is a player command only.";
		
		public static Text PLAYER_DNE;
		private static final String PLAYER_DNE_DEFAULT = "Error: Player not found. Check for spelling, capitalization, and if the player has ever logged onto the server.";
		
		public static Text PURGE_NO_FILE;
		private static final String PURGE_NO_FILE_DEFAULT = "File not found. Please check spelling and capitalization.";
		
		/* Exception Messages */
		public static Text IO_EX;
		private static final String IO_EX_DEFAULT = "Error: An internal error has occurred. Please contact an administrator immediately.";
		
		public static Text NO_FILE_EX;
		private static final String NO_FILE_EX_DEFAULT = "Error: File not found. Please contact an administrator immediately.";
		
		public static Text PARSE_EX;
		private static final String PARSE_EX_DEFAULT = "Error: Failed to parse data.";
		
		public static Text SQL_EX;
		private static final String SQL_EX_DEFAULT = "Error: Unable to connect to the database.";
		
		public static Text UNKNOWN_EX;
		private static final String UNKNOWN_EX_DEFAULT = "An unknown error has occurred while obtaining the player's UUID.";
		
		/* Other */
		public static String ACCOUNT_PAGE;
		private static final String ACCOUNT_PAGE_DEFAULT = "Page";
		
		public static String NEW_PLAYER_FILE;
		private static final String NEW_PLAYER_FILE_DEFAULT = "Do not edit this file unless it's absolutely necessary.";
	}
}

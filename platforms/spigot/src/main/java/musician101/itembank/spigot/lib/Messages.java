package musician101.itembank.spigot.lib;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import musician101.itembank.spigot.SpigotItemBank;
import musician101.itembank.spigot.util.IBUtils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;

public class Messages
{
	public static void init(SpigotItemBank plugin, String lang, File file) throws IOException, InvalidConfigurationException
	{
		FileConfiguration langConfig = IBUtils.getYamlConfig(file, true);
		ConfigurationSection langCS = null;
		Logger log = plugin.getLogger();
		if (!langConfig.isSet(lang))
		{
			log.warning("Could not find " + lang + " in lang.yml. Attempting to default to 'en'");
			if (!langConfig.isSet("en"))
				log.warning("Could not find 'en' in lang.yml");
			
			langCS = langConfig.createSection(lang);
		}
		else
			langCS = langConfig.getConfigurationSection(lang);
		
		initCommandTranslations(log, langCS);
		initErrorTranslations(log, langCS);
		initUpdaterTranslations(log, langCS);
		initVaultTranslations(log, langCS);
		
		NEW_PLAYER_FILE = langConfig.getString(lang + ".newplayerfile", NEW_PLAYER_FILE_DEFAULT);
	}
	
	private static void initCommandTranslations(Logger log, ConfigurationSection langCS) throws InvalidConfigurationException
	{
		ConfigurationSection commandCS = getConfigurationSection(log, langCS, "command");
		initAccountCommandTranslations(log, commandCS);
		initPurgeCommandTranslations(log, commandCS);
		initReloadCommandTranslations(log, commandCS);
		initUUIDCommandTranslations(log, commandCS);
	}
	
	private static void initAccountCommandTranslations(Logger log, ConfigurationSection commandCS) throws InvalidConfigurationException
	{
		ConfigurationSection accountCS = getConfigurationSection(log, commandCS, "account");
		ACCOUNT_DESC = accountCS.getString("description", ACCOUNT_DESC_DEFAULT);
		ACCOUNT_DESC2 = accountCS.getString("description2", ACCOUNT_DESC2_DEFAULT);
		ACCOUNT_PAGE = accountCS.getString("page", ACCOUNT_PAGE_DEFAULT);
		ACCOUNT_UPDATED = PREFIX + accountCS.getString("updated", ACCOUNT_UPDATED_DEFAULT);
		ACCOUNT_WORLD_DNE = PREFIX + accountCS.getString("worlddne", ACCOUNT_WORLD_DNE_DEFAULT);
		
		ConfigurationSection economyCS = getConfigurationSection(log, accountCS, "economy");
		ACCOUNT_ECON_SUCCESS = PREFIX + economyCS.getString("success", ACCOUNT_ECON_SUCCESS_DEFAULT);
		ACCOUNT_ECON_FAIL = PREFIX + economyCS.getString("success", ACCOUNT_ECON_FAIL_DEFAULT);
		
		ConfigurationSection inventoryCS = getConfigurationSection(log, accountCS, "inventory");
		ACCOUNT_ILLEGAL_AMOUNT = PREFIX + inventoryCS.getString("amount", ACCOUNT_ILLEGAL_AMOUNT_DEFAULT);
		ACCOUNT_ILLEGAL_ITEM = PREFIX + inventoryCS.getString("item", ACCOUNT_ILLEGAL_ITEM_DEFAULT);
		ACCOUNT_ILLEGAL_PAGE = PREFIX + inventoryCS.getString("page", ACCOUNT_ILLEGAL_PAGE_DEFAULT);
	}
	
	private static void initPurgeCommandTranslations(Logger log, ConfigurationSection commandCS) throws InvalidConfigurationException
	{
		ConfigurationSection purgeCS = getConfigurationSection(log, commandCS, "purge");
		PURGE_DESC = purgeCS.getString("description", PURGE_DESC_DEFAULT);
		PURGE_NO_FILE = PREFIX + purgeCS.getString("nofile", PURGE_NO_FILE_DEFAULT);
		
		ConfigurationSection successCS = getConfigurationSection(log, purgeCS, "success");
		PURGE_SINGLE = PREFIX + successCS.getString("single", PURGE_SINGLE_DEFAULT);
		PURGE_MULTIPLE = PREFIX + successCS.getString("multiple", PURGE_MULTIPLE_DEFAULT);
	}

	private static void initReloadCommandTranslations(Logger log, ConfigurationSection commandCS) throws InvalidConfigurationException
	{
		ConfigurationSection reloadCS = getConfigurationSection(log, commandCS, "reload");
		RELOAD_DESC = reloadCS.getString("description", RELOAD_DESC_DEFAULT);
		RELOAD_SUCCESS = PREFIX + reloadCS.getString("success", RELOAD_SUCCESS_DEFAULT);
	}

	private static void initUUIDCommandTranslations(Logger log, ConfigurationSection commandCS) throws InvalidConfigurationException
	{
		ConfigurationSection uuidCS = getConfigurationSection(log, commandCS, "uuid");
		UUID_DESC = uuidCS.getString("description", UUID_DESC_DEFAULT);
	}
	
	private static void initErrorTranslations(Logger log, ConfigurationSection langCS) throws InvalidConfigurationException
	{
		ConfigurationSection errorCS = getConfigurationSection(log, langCS, "error");
		IO_EX = PREFIX + errorCS.getString("ioex", IO_EX_DEFAULT);
		NO_FILE_EX = PREFIX + errorCS.getString("nofile", NO_FILE_EX_DEFAULT);
		NO_PERMISSION = PREFIX + errorCS.getString("nopermission", NO_PERMISSION_DEFAULT);
		PLAYER_CMD = PREFIX + errorCS.getString("playercmd", PLAYER_CMD_DEFAULT);
		PLAYER_DNE = PREFIX + errorCS.getString("playerdne", PLAYER_DNE_DEFAULT);
		SQL_EX = PREFIX + errorCS.getString("sqlex", SQL_EX_DEFAULT);
		UNKNOWN_EX = PREFIX + errorCS.getString("unknownex", UNKNOWN_EX_DEFAULT);
		YAML_PARSE_EX = PREFIX + errorCS.getString("yamlparseex", YAML_PARSE_EX_DEFAULT);
	}
	
	private static void initUpdaterTranslations(Logger log, ConfigurationSection langCS) throws InvalidConfigurationException
	{
		ConfigurationSection updaterCS = getConfigurationSection(log, langCS, "updater");
		UPDATER_CURRENT = updaterCS.getString("current", UPDATER_CURRENT_DEFAULT);
		UPDATER_ERROR = updaterCS.getString("error", UPDATER_ERROR_DEFAULT);
		UPDATER_NEW = updaterCS.getString("new", UPDATER_NEW_DEFAULT);
	}
	
	private static void initVaultTranslations(Logger log, ConfigurationSection langCS) throws InvalidConfigurationException
	{
		ConfigurationSection vaultCS = getConfigurationSection(log, langCS, "vault");
		VAULT_BOTH_ENABLED = vaultCS.getString("bothenabled", VAULT_BOTH_ENABLED_DEFAULT);
		VAULT_NO_CONFIG = vaultCS.getString("noconfig", VAULT_NO_CONFIG_DEFAULT);
		VAULT_NOT_INSTALLED = vaultCS.getString("novault", VAULT_NOT_INSTALLED_DEFAULT);
	}
	
	private static ConfigurationSection getConfigurationSection(Logger log, ConfigurationSection cs, String path)
	{
		if (!cs.isSet(path))
		{
			log.warning("Could not find " + cs.getCurrentPath() + "." + path + " in lang.yml");
			return cs.createSection(path);
		}
		
		return cs.getConfigurationSection(path);
	}
	
	/* Formatting */
	public static final String HEADER = ChatColor.DARK_RED + "===== " + ChatColor.RESET + "ItemBank v3.0" + ChatColor.DARK_RED + " =====";
	public static final String PREFIX = ChatColor.DARK_RED + "[ItemBank] ";
	
	/* Command Descriptions */
	public static String ACCOUNT_DESC;
	private static final String ACCOUNT_DESC_DEFAULT = "Open the first page of your account.";
	
	public static String ACCOUNT_DESC2;
	private static final String ACCOUNT_DESC2_DEFAULT = "Opens a page with the specified parameters.";
	
	public static String PURGE_DESC;
	private static final String PURGE_DESC_DEFAULT = "Delete all or a specified player's account.";
	
	public static String RELOAD_DESC;
	private static final String RELOAD_DESC_DEFAULT = "Reloads the plugin's config file.";
	
	public static String UUID_DESC;
	private static final String UUID_DESC_DEFAULT = "Get a player's UUID.";
	
	/* Command Success */
	public static String ACCOUNT_ECON_SUCCESS;
	private static final String ACCOUNT_ECON_SUCCESS_DEFAULT = "A $ transaction fee has been deducted from your account.";
	
	public static String ACCOUNT_UPDATED;
	private static final String ACCOUNT_UPDATED_DEFAULT = "Account updated.";
	
	public static String PURGE_MULTIPLE;
	private static final String PURGE_MULTIPLE_DEFAULT = "All accounts have been reset.";
	
	public static String PURGE_SINGLE;
	private static final String PURGE_SINGLE_DEFAULT = "Account reset.";
	
	public static String RELOAD_SUCCESS;
	private static final String RELOAD_SUCCESS_DEFAULT = "Config reloaded.";
	
	/* Error Messages */
	public static String ACCOUNT_ECON_FAIL;
	private static final String ACCOUNT_ECON_FAIL_DEFAULT = "A $ transaction fee has been deducted from your account.";
	
	public static String ACCOUNT_ILLEGAL_AMOUNT;
	private static final String ACCOUNT_ILLEGAL_AMOUNT_DEFAULT = "Some of the items you deposited put you over the limit. They have been returned to you.";
	
	public static String ACCOUNT_ILLEGAL_ITEM;
	private static final String ACCOUNT_ILLEGAL_ITEM_DEFAULT = "You attempted to deposit prohibited items into your account. They have been returned to you.";
	
	public static String ACCOUNT_ILLEGAL_PAGE;
	private static final String ACCOUNT_ILLEGAL_PAGE_DEFAULT = "You are not allowed to store items on this page. The items have been returned to you. If your inventory is full then check the floor.";
	
	public static String NO_PERMISSION;
	private static final String NO_PERMISSION_DEFAULT = "Error: You do not have permission for this command.";
	
	public static String PLAYER_CMD;
	private static final String PLAYER_CMD_DEFAULT = "Error: This is a player command.";
	
	public static String PLAYER_DNE;
	private static final String PLAYER_DNE_DEFAULT = "Error: Player not found. Make sure you're spelling the name correctly.";
	
	public static String PURGE_NO_FILE;
	private static final String PURGE_NO_FILE_DEFAULT = "File not found. Please check spelling and capitalization.";
	
	public static String ACCOUNT_WORLD_DNE;
	private static final String ACCOUNT_WORLD_DNE_DEFAULT = "That world does not exist.";
	
	/* Exception Messages */
	public static String IO_EX;
	private static final String IO_EX_DEFAULT = "Error: An internal error has occurred. Please contact an administrator immediately.";
	
	public static String NO_FILE_EX;
	private static final String NO_FILE_EX_DEFAULT = "Error: File not found. Please contact an administrator immediately.";
	
	public static String SQL_EX;
	private static final String SQL_EX_DEFAULT = "Error: Unable to connect to the database.";
	
	public static String UNKNOWN_EX;
	private static final String UNKNOWN_EX_DEFAULT = "An unknown error has occurred while obtaining the player's UUID.";
	
	public static String YAML_PARSE_EX;
	private static final String YAML_PARSE_EX_DEFAULT = "Error: Your account contains format errors. Please contact an administrator immediately.";
	
	/* Updater Messages */
	public static String UPDATER_CURRENT;
	private static final String UPDATER_CURRENT_DEFAULT = "A new version is available.";
	
	public static String UPDATER_ERROR;
	private static final String UPDATER_ERROR_DEFAULT = "The current version is the latest.";
	
	public static String UPDATER_NEW;
	private static final String UPDATER_NEW_DEFAULT = "Error: Update check failed.";
	
	/* Vault Messages */
	public static String VAULT_BOTH_ENABLED;
	private static final String VAULT_BOTH_ENABLED_DEFAULT = "Vault detected and enabled in config. Using Vault for monetary transactions.";
	
	public static String VAULT_NO_CONFIG;
	private static final String VAULT_NO_CONFIG_DEFAULT = "Vault detected but disabled in config. No monetary transactions will occur.";
	
	public static String VAULT_NOT_INSTALLED;
	private static final String VAULT_NOT_INSTALLED_DEFAULT = "Error detecting Vault. Is it installed?";
	
	/* Other */
	public static String NEW_PLAYER_FILE;
	private static final String NEW_PLAYER_FILE_DEFAULT = "Do not edit this file unless it's absolutely necessary.";
	
	public static String ACCOUNT_PAGE;
	private static final String ACCOUNT_PAGE_DEFAULT = "Page";
}

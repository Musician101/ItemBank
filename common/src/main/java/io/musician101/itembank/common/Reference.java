package io.musician101.itembank.common;

import java.io.File;
import java.util.UUID;

public class Reference
{
    public static final String NAME = "ItemBank";
    public static final String ID = "io.musician101.itembank";
    public static final String DESCRIPTION = "Virtual chest with configurable limits.";
    @SuppressWarnings("WeakerAccess")
    public static final String PREFIX = "[" + NAME + "] ";
    public static final String VERSION = "3.1";

    private Reference()
    {

    }

    public static class Commands
    {
        public static final String ACCOUNT_NAME = "account";
        public static final String ACCOUNT_DESC = "Open the first page of your account.";
        public static final String HEADER_ENDS = " ==== ";
        public static final String HELP = "help";
        public static final String IB_CMD = "/ib";
        public static final String PAGE = "page";
        public static final String PLAYER = "player";
        public static final String PURGE_NAME = "purge";
        public static final String PURGE_DESC = "Delete all or a specified player's account.";
        public static final String RELOAD_NAME = "reload";
        public static final String RELOAD_DESC = "Reload the plugin's config file.";
        public static final String UUID_NAME = "uuid";
        public static final String UUID_DESC = "Get a player's UUID.";
        public static final String WORLD = "world";

        private Commands()
        {

        }

        public static String getAccountArg(String argName)
        {
            return argName + ":<" + argName + ">";
        }
    }

    public static class Config
    {
        public static final String AMOUNT = "amount";
        public static final String DATABASE = "database";
        public static final String ENABLE_ECONOMY = "enable_economy";
        public static final String ENABLE_MYSQL = "enable";
        public static final String HOST = "host";
        public static final String ITEM_LIST = "item_list";
        public static final String LOCAL_HOST = "127.0.0.1";//NOSONAR
        public static final String MULTI_WORLD = "multi_world";
        public static final String MYSQL = "mysql";
        public static final String PAGE_LIMIT = "page_limit";
        public static final String PASSWORD = "password";//NOSONAR
        public static final String PORT = "port";
        public static final String PORT_DEFAULT = "3306";
        public static final String TRANSACTION_COST = "transaction_cost";
        public static final String USER = "user";
        public static final String UPDATE_CHECK = "check_for_update";
        public static final String VARIATION = "variation";
        public static final String VARIATIONS = "variations";
        public static final String WHITELIST = "whitelist";

        private Config()
        {

        }
    }

    public static class Messages
    {
        public static final String ACCOUNT_ECON_GET_ACCOUNT_FAIL = PREFIX + "An error occurred while trying to fetch your account.";
        public static final String ACCOUNT_ECON_NOT_AVAILABLE = PREFIX + "Economy has been enabled in the config, but no implemented economy was found.";
        public static final String ACCOUNT_ECON_UNKNOWN_FAIL = PREFIX + "An unknown error occurred while trying to withdraw from your account.";
        public static final String ACCOUNT_ECON_WITHDRAW_FAIL = PREFIX + "You do not have enough money to complete the current transaction.";
        public static final String ACCOUNT_ILLEGAL_AMOUNT = PREFIX + "Some of the items you deposited put you over the limit. They have been returned to you.";
        public static final String ACCOUNT_ILLEGAL_ITEM = PREFIX + "You attempted to deposit prohibited items into your account. They have been returned to you.";
        public static final String ACCOUNT_ILLEGAL_PAGE = PREFIX + "You are not allowed to store items on this page. The items have been returned to you. If your inventory is full then check the floor.";
        public static final String ACCOUNT_UPDATED = PREFIX + "Account updated.";
        public static final String ACCOUNT_WORLD_DNE = PREFIX + "That world does not exist.";
        public static final String ECON_LOAD_FAIL_NO_SERVICE = "No economy service was detected. Disabling economy support.";
        public static final String ECON_LOAD_FAIL = "An error occurred while enabling economy support. Economy support now disabled.";
        public static final String ECON_LOAD_SUCCESS = "Economy service detected and enabled.";
        public static final String NO_FILE_EX = PREFIX + "Error: File not found. Please contact an administrator immediately.";
        public static final String NO_PERMISSION = PREFIX + "Error: You do not have permission for this command.";
        public static final String PLAYER_CMD = PREFIX + "Error: This is a player command.";
        public static final String PLAYER_DNE = PREFIX + "Error: Player not found. Make sure you're spelling the name correctly.";
        public static final String PURGE_MULTIPLE = PREFIX + "All accounts have been reset.";
        public static final String PURGE_NO_FILE = PREFIX + "File not found. Please check spelling and capitalization.";
        public static final String PURGE_SINGLE = PREFIX + "Account reset.";
        public static final String RELOAD_SUCCESS = PREFIX + "Config reloaded.";
        public static final String SQL_EX = PREFIX + "Error: Unable to connect to the database.";
        public static final String UNKNOWN_EX = PREFIX + "An unknown error has occurred while obtaining the player's UUID.";
        public static final String UPDATER_DISABLED = "The update checker is currently disabled.";
        public static final String UPDATER_FAILED = "Error: Update check failed.";
        public static final String UPDATER_UP_TO_DATE = "The current version is the latest.";

        private Messages()
        {

        }

        public static String updaterNew(String newVersionName)
        {
            return "A new version is available." + newVersionName;
        }

        public static String accountWithdrawSuccess(String currencySymbol, double amount)
        {
            return PREFIX + "A fee of " + currencySymbol + amount + " has been deducted from your account.";
        }

        public static String badUUID(String string)
        {
            return "Failed to parse " + string + " as UUID.";
        }

        public static String fileCreateFail(File file)
        {
            return PREFIX + "Could not create " + file.getName() + ".";
        }

        public static String fileDeleteFail(File file)
        {
            return PREFIX + "Could not delete " + file.getName() + ".";
        }

        public static String fileLoadFail(File file)
        {
            return PREFIX + "Could not delete " + file.getName() + ".";
        }

        public static String page(String name, int page)
        {
            return name + " - Page " + page;
        }

        public static String purgeFileFail(File file)
        {
            return PREFIX + file.getName() + " could not be deleted.";
        }

        public static String uuid(String name, UUID uuid)
        {
            return PREFIX + name + "'s UUID: " + uuid.toString();
        }
    }

    public static class MySQL
    {
        public static final String PAGE = "Page";
        public static final String TABLE_PREFIX = "ib_";
        public static final String WORLD = "World";

        private MySQL()
        {

        }

        public static String addItem(UUID owner, String worldName, int page, int slot, String itemStack)
        {
            return "INSERT INTO " + getTableName(owner) + "(World, Page, Slot, Item) VALUES (\"" + worldName + "\", " + page + ", " + slot + ", \"" + itemStack + ");";
        }

        public static String createTable(UUID owner)
        {
            return "CREATE TABLE IF NOT EXISTS " + getTableName(owner) + "(World TEXT, Page int, Slot int, ItemStack TEXT);";
        }

        public static String deleteItem(UUID owner, String worldName, int page, int slot)
        {
            return "DELETE FROM ib_" + getTableName(owner) + " WHERE World = \"" + worldName + "\" AND Page = " + page + " AND Slot = " + slot + ";";
        }

        public static String deleteTable(String tableName)
        {
            return "DROP TABLE IF EXISTS " + tableName;
        }

        public static String deleteTable(UUID owner)
        {
            return deleteTable(getTableName(owner));
        }

        public static String getTable(String tableName)
        {
            return "SELECT * FROM " + tableName + ";";
        }

        public static String getTable(UUID owner, String worldName, int page, int slot)
        {
            return "SELECT * FROM " + getTableName(owner) + " WHERE World = \"" + worldName + "\" AND Page = " + page + " AND Slot = " + slot + ";";
        }

        private static String getTableName(UUID uuid)
        {
            return TABLE_PREFIX + uuid.toString().replace("-", "_");
        }

        public static UUID getUUIDFromTableName(String tableName)
        {
            String noPrefix = tableName.replace(TABLE_PREFIX, "");
            if (!noPrefix.contains("_"))
                return null;

            return UUID.fromString(noPrefix.replace("_", "-"));
        }
    }

    public static class Permissions
    {
        public static final String ACCOUNT = ID + ".account";
        public static final String ADMIN = ACCOUNT + ".admin";
        public static final String PAGE = ACCOUNT + ".page";
        public static final String PLAYER = ACCOUNT + ".player";
        public static final String PURGE = ID + ".purge";
        public static final String RELOAD = ID + ".reload";
        public static final String UUID = ID + ".uuid";
        public static final String WORLD = ACCOUNT + ".world";

        private Permissions()
        {

        }
    }

    public static class PlayerData
    {
        public static final String FILE_EXTENSION = ".itembank";
        public static final String DIRECTORY = "player_data";

        private PlayerData()
        {

        }

        public static UUID getUUIDFromFileName(File file)
        {
            String name = file.getName().replace(FILE_EXTENSION, "");
            if (!name.contains("-"))
                return null;

            return UUID.fromString(name);
        }
    }
}

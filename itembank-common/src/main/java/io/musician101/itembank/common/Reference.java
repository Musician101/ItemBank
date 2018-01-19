package io.musician101.itembank.common;

import java.io.File;
import java.util.UUID;
import javax.annotation.Nonnull;

public class Reference {

    public static final String DESCRIPTION = "Virtual chest with configurable limits.";
    public static final String ID = "itembank";
    public static final String NAME = "ItemBank";
    public static final String PREFIX = "[" + NAME + "] ";
    public static final String VERSION = "3.2";

    private Reference() {

    }

    public static class Commands {

        public static final String ACCOUNT_DESC = "Open the first page of your account.";
        public static final String ACCOUNT_NAME = "account";
        public static final String HEADER_ENDS = "==== ";
        public static final String IB_CMD = "/ib";
        public static final String PAGE = "page";
        public static final String PLAYER = "viewer";
        public static final String PURGE_DESC = "Delete all or a specified viewer's account.";
        public static final String PURGE_NAME = "purge";
        public static final String RELOAD_DESC = "Reload the plugin's config file.";
        public static final String RELOAD_NAME = "reload";
        public static final String UUID_DESC = "Get a viewer's UUID.";
        public static final String UUID_NAME = "uuid";
        public static final String WORLD = "world";

        private Commands() {

        }
    }

    public static class Config {

        public static final String AMOUNT = "amount";
        public static final String DATABASE = "database";
        public static final String ENABLE_ECONOMY = "enable_economy";
        public static final String ENABLE_MYSQL = "enable";
        public static final String HOST = "host";
        public static final String ITEM_LIST = "item_list";
        public static final String LOCAL_HOST = "127.0.0.1";
        public static final String MULTI_WORLD = "multi_world";
        public static final String MYSQL = "mysql";
        public static final String PAGE_LIMIT = "page_limit";
        public static final String PASSWORD = "password";
        public static final String PORT = "port";
        public static final String PORT_DEFAULT = "3306";
        public static final String TRANSACTION_COST = "transaction_cost";
        public static final String UPDATE_CHECK = "check_for_update";
        public static final String USER = "user";
        public static final String VARIATIONS = "variations";
        public static final String WHITELIST = "whitelist";

        private Config() {

        }
    }

    public static class Messages {

        public static final String ACCOUNT_ECON_WITHDRAW_FAIL = PREFIX + "You do not have enough money to complete the current transaction.";
        public static final String ACCOUNT_ILLEGAL_AMOUNT = PREFIX + "Some of the items you deposited put you over the limit. They have been returned to you.";
        public static final String ACCOUNT_ILLEGAL_ITEM = PREFIX + "You attempted to deposit prohibited items into your account. They have been returned to you.";
        public static final String ACCOUNT_WORLD_DNE = PREFIX + "That world does not exist.";
        public static final String DATABASE_UNAVAILABLE = "ItemBank could not connect to the database. The /account command will not function until this is fixed.";
        public static final String ECON_LOAD_FAIL_NO_SERVICE = "No economy service was detected. Disabling economy support.";
        public static final String ECON_LOAD_SUCCESS = "Economy service detected and enabled.";
        public static final String NO_PERMISSION = PREFIX + "Error: You do not have permission for this command.";
        public static final String PLAYER_CMD = PREFIX + "Error: This is a viewer command.";
        public static final String PLAYER_DNE = PREFIX + "Error: Player not found. Make sure you're spelling the name correctly.";
        public static final String PLUGIN_NOT_INITIALIZED = "ItemBank has not been initialized.";
        public static final String PURGE_MULTIPLE = PREFIX + "All accounts have been reset.";
        public static final String PURGE_SINGLE = PREFIX + "Account reset.";
        public static final String RELOAD_SUCCESS = PREFIX + "Config reloaded.";
        public static final String SQL_EX = "Error: Unable to connect to the database.";
        public static final String UNKNOWN_EX = PREFIX + "An unknown error has occurred while obtaining the viewer's UUID.";

        private Messages() {

        }

        public static String accountWithdrawSuccess(@Nonnull String currencySymbol, double amount) {
            return PREFIX + "A fee of " + currencySymbol + amount + " has been deducted from your account.";
        }

        public static String badUUID(@Nonnull String name, @Nonnull String string) {
            return "Failed to parse " + name + "'s uuid: " + string;
        }

        public static String fileCreateFail(@Nonnull File file) {
            return PREFIX + "Could not create " + file.getName() + ".";
        }

        public static String fileLoadFail(@Nonnull File file) {
            return PREFIX + "Could not delete " + file.getName() + ".";
        }

        public static String invalidItem(@Nonnull String name, @Nonnull String worldName, int page, int slot, @Nonnull String itemStackString) {
            return "Invalid item in " + name + "'s account under " + worldName + ", page " + page + ", slot " + slot + ": " + itemStackString;
        }

        public static String invalidPage(@Nonnull String name, @Nonnull String worldName, int page) {
            return "Invalid page number in " + name + "'s account under " + worldName + ": " + page;
        }

        public static String invalidSlot(@Nonnull String name, @Nonnull String worldName, int slot) {
            return "Invalid slot number in " + name + "'s account under " + worldName + ": " + slot;
        }

        public static String page(@Nonnull String name, int page) {
            return name + " - Page " + page;
        }

        public static String uuid(@Nonnull String name, @Nonnull UUID uuid) {
            return PREFIX + name + "'s UUID: " + uuid.toString();
        }

        public static String worldDNE(@Nonnull String name, @Nonnull String worldName) {
            return "Invalid world name in " + name + "'s account: " + worldName;
        }
    }

    public static class MySQL {

        public static final String ITEM = "Item";
        public static final String NAME = "Name";
        public static final String PAGE = "Page";
        public static final String SLOT = "Slot";
        public static final String TABLE_NAME = "ib_accounts";
        public static final String SELECT_TABLE = "SELECT * FROM " + TABLE_NAME;
        public static final String UUID = "UUID";
        public static final String WORLD = "World";
        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS ib_accounts(" + UUID + " TEXT, " + NAME + " TEXT, " + WORLD + " TEXT, " + PAGE + " int, " + SLOT + " int, " + ITEM + " TEXT);";

        private MySQL() {

        }

        public static String addItem(@Nonnull UUID uuid, @Nonnull String name, @Nonnull String worldName, int page, int slot, @Nonnull String itemStack) {
            return "INSERT INTO " + TABLE_NAME + "(" + UUID + ", " + NAME + ", " + WORLD + ", " + PAGE + ", " + SLOT + ", " + ITEM + ") VALUES (\"" + uuid.toString() + "\", \"" + name + "\", \"" + worldName + "\", " + page + ", " + slot + ", \"" + itemStack + ");";
        }

        public static String deleteItem(@Nonnull UUID uuid, @Nonnull String name, @Nonnull String worldName, int page, int slot) {
            return deleteUser(uuid) + " AND " + NAME + " = \"" + name + "\" AND " + WORLD + " = \"" + worldName + "\" AND " + PAGE + " = " + page + " AND " + SLOT + " = " + slot + ";";
        }

        public static String deleteUser(@Nonnull UUID uuid) {
            return "DELETE FROM " + TABLE_NAME + " WHERE " + UUID + " = \"" + uuid.toString() + "\"";
        }

        public static String deleteUser(@Nonnull UUID uuid, @Nonnull String name) {
            return "DELETE FROM " + TABLE_NAME + " WHERE " + UUID + " = \"" + uuid.toString() + "\" AND " + NAME + " = \"" + name + "\"";
        }
    }

    public static class Permissions {

        public static final String ACCOUNT = ID + ".account";
        public static final String ADMIN = ACCOUNT + ".admin";
        public static final String PAGE = ACCOUNT + ".page";
        public static final String PLAYER = ACCOUNT + ".viewer";
        public static final String PURGE = ID + ".purge";
        public static final String RELOAD = ID + ".reload";
        public static final String UUID = ID + ".uuid";
        public static final String WORLD = ACCOUNT + ".world";

        private Permissions() {

        }
    }

    public static class PlayerData {

        public static final String DIRECTORY = "player_data";
        public static final String FILE_EXTENSION = ".json";
        public static final String ID = "uuid";
        public static final String ITEM = "item";
        public static final String ITEMS = "items";
        public static final String NAME = "name";
        public static final String PAGE = "page";
        public static final String PAGES = "page";
        public static final String SLOT = "slot";
        public static final String WORLD = "world";
        public static final String WORLDS = "worlds";

        private PlayerData() {

        }
    }
}

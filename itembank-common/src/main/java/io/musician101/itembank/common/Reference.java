package io.musician101.itembank.common;

import java.io.File;
import java.util.UUID;
import javax.annotation.Nonnull;

public class Reference {

    public static final String DESCRIPTION = "Virtual chest with configurable limits.";
    public static final String ID = "itembank";
    public static final String NAME = "ItemBank";
    public static final String PREFIX = "[" + NAME + "] ";
    public static final String VERSION = "@VERSION@";

    private Reference() {

    }

    public static class Commands {

        public static final String ACCOUNT_DESC = "Open the first page of your account.";
        public static final String ACCOUNT_NAME = "account";
        public static final String HEADER_ENDS = "==== ";
        public static final String IB_CMD = "ib";
        public static final String PAGE = "page";
        public static final String PLAYER = "player";
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

        public static final String ADDRESS = "address";
        public static final String BLACKLIST = "blacklist";
        public static final String DATABASE = "database";
        public static final String ENABLE_ECONOMY = "enable-economy";
        public static final String FORMAT = "format";
        public static final String HOCON = "HOCON";
        public static final String ITEM_RESTRICTIONS = "item-restrictions";
        public static final String JSON = "JSON";
        public static final String LOCAL_HOST = "localhost";
        public static final String MINECRAFT = "minecraft";
        public static final String MONGO_DB = "MongoDB";
        public static final String MONGO_URI = "mongo_uri";
        public static final String MULTI_WORLD = "multi_world";
        public static final String MYSQL = "MySQL";
        public static final String PAGE_LIMIT = "page-limit";
        public static final String PASSWORD = "password";
        public static final String ROOT = "root";
        public static final String SQLITE = "SQLite";
        public static final String TOML = "TOML";
        public static final String TRANSACTION_COST = "transaction-cost";
        public static final String USERNAME = "username";
        public static final String WHITELIST = "whitelist";
        public static final String YAML = "YAML";

        private Config() {

        }
    }

    public static class GUIText {

        public static final String ACCOUNT = "%s's Account";
        public static final String ACCOUNTS = "Accounts";
        public static final String BACK = "Back";
        public static final String CLICK_TO_PURGE = "RIGHT-CLICK to purge.";
        public static final String CLICK_TO_VIEW = "LEFT-CLICK to view.";
        public static final String NEXT_PAGE = "Next Page";
        public static final String WORLD_PAGE = "%s - Page %d";
        public static final String PREVIOUS_PAGE = "Previous Page";

        private GUIText() {

        }
    }

    public static class Messages {

        public static final String CONFIG_INVALID_ITEM = PREFIX + "Invalid item ID %s in %s";
        public static final String ACCOUNT_ECON_WITHDRAW_FAIL = PREFIX + "You do not have enough money to complete the current transaction.";
        public static final String ACCOUNT_ILLEGAL_AMOUNT = PREFIX + "Some of the items you deposited put you over the limit. They have been returned to you.";
        public static final String BLACKLISTED_ITEM = PREFIX + "You attempted to deposit prohibited items into your account. They have been returned to you.";
        public static final String ACCOUNT_WORLD_DNE = PREFIX + "That world does not exist.";
        public static final String DATABASE_UNAVAILABLE = "ItemBank could not connect to the database. The /account command will not function until this is fixed.";
        public static final String ECON_LOAD_FAIL_NO_SERVICE = "No economy service was detected. Disabling economy support.";
        public static final String ECON_LOAD_SUCCESS = "Economy service detected and enabled.";
        public static final String NO_PERMISSION = PREFIX + "Error: You do not have permission for this command.";
        public static final String PLAYER_CMD = PREFIX + "Error: This is a viewer command.";
        public static final String PLAYER_DNE = PREFIX + "Error: Player not found. Make sure you're spelling the name correctly.";
        public static final String PURGE_ALL = PREFIX + "All accounts have been reset.";
        public static final String PURGE_SINGLE = PREFIX + "Account reset.";
        public static final String RELOAD_SUCCESS = PREFIX + "Config reloaded.";
        public static final String SQL_EX = "Error: Unable to connect to the database.";
        public static final String UNKNOWN_EX = PREFIX + "An unknown error has occurred while obtaining the viewer's UUID.";

        private Messages() {

        }

        public static String accountWithdrawSuccess(@Nonnull String currencySymbol, double amount) {
            return PREFIX + "A fee of " + currencySymbol + amount + " has been deducted from your account.";
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

        public static String page(@Nonnull String name, int page) {
            return name + " - Page " + page;
        }

        public static String uuid(@Nonnull String name, @Nonnull UUID uuid) {
            return PREFIX + name + "'s UUID: " + uuid.toString();
        }
    }

    public static class Database {

        public static final String ITEM = "Item";
        public static final String NAME = "Name";
        public static final String PAGE = "Page";
        public static final String SLOT = "Slot";
        public static final String TABLE_NAME = "ib_accounts";
        public static final String SELECT_TABLE = "SELECT * FROM " + TABLE_NAME;
        public static final String UUID = "UUID";
        public static final String WORLD = "World";
        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS ib_accounts(" + UUID + " TEXT, " + NAME + " TEXT, " + WORLD + " TEXT, " + PAGE + " int, " + SLOT + " int, " + ITEM + " TEXT);";

        private Database() {

        }

        public static String addItem(@Nonnull UUID uuid, @Nonnull String name, @Nonnull String worldName, int page, int slot, @Nonnull String itemStack) {
            return "INSERT INTO " + TABLE_NAME + "(" + UUID + ", " + NAME + ", " + WORLD + ", " + PAGE + ", " + SLOT + ", " + ITEM + ") VALUES (\"" + uuid.toString() + "\", \"" + name + "\", \"" + worldName + "\", " + page + ", " + slot + ", \"" + itemStack + ");";
        }

        public static String deleteUser(@Nonnull UUID uuid) {
            return "DELETE FROM " + TABLE_NAME + " WHERE " + UUID + " = \"" + uuid.toString() + "\"";
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
        public static final String HOCON = ".hocon";
        public static final String JSON = ".json";
        public static final String ID = "uuid";
        public static final String ITEMS = "items";
        public static final String NAME = "name";
        public static final String PAGE = "page";
        public static final String PAGES = "pages";
        public static final String SLOT = "slot";
        public static final String TOML = ".toml";
        public static final String WORLD = "world";
        public static final String WORLDS = "worlds";
        public static final String YAML = ".yml";

        private PlayerData() {

        }
    }
}

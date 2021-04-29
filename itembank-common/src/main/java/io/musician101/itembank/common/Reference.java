package io.musician101.itembank.common;

import java.io.File;
import java.util.UUID;
import javax.annotation.Nonnull;

public interface Reference {

    String DESCRIPTION = "Virtual chest with configurable limits.";
    String ID = "itembank";
    String NAME = "ItemBank";
    String PREFIX = "[" + NAME + "] ";
    String VERSION = "@VERSION@";

    interface AccountData {

        String DIRECTORY = "player_data";
        String HOCON = ".hocon";
        String ID = "uuid";
        String ITEMS = "items";
        String JSON = ".json";
        String NAME = "name";
        String PAGE = "page";
        String PAGES = "pages";
        String SLOT = "slot";
        String WORLD = "world";
        String WORLDS = "worlds";
        String YAML = ".yml";
    }

    interface Commands {

        String ACCOUNT_DESC = "Open the first page of your account.";
        String ACCOUNT_NAME = "account";
        String HEADER_ENDS = "====";
        String IB_CMD = "ib";
        String PLAYER = "player";
        String PURGE_DESC = "Delete all or a specified viewer's account.";
        String PURGE_NAME = "purge";
        String RELOAD_DESC = "Reload the plugin's config file.";
        String RELOAD_NAME = "reload";
        String WORLD = "world";
    }

    interface Config {

        String ADDRESS = "address";
        String BLACKLIST = "blacklist";
        String DATABASE = "database";
        String ENABLE_ECONOMY = "enable-economy";
        String FORMAT = "format";
        String HOCON = "HOCON";
        String ITEM_RESTRICTIONS = "item-restrictions";
        String JSON = "JSON";
        String LOCAL_HOST = "localhost";
        String MINECRAFT = "minecraft";
        String MONGO_DB = "MongoDB";
        String MONGO_URI = "mongo_uri";
        String MULTI_WORLD = "multi_world";
        String MYSQL = "MySQL";
        String PAGE_LIMIT = "page-limit";
        String PASSWORD = "password";
        String ROOT = "root";
        String SQLITE = "SQLite";
        String TRANSACTION_COST = "transaction-cost";
        String USERNAME = "username";
        String WHITELIST = "whitelist";
        String YAML = "YAML";
    }

    interface Database {

        String ITEM = "Item";
        String PAGE = "Page";
        String SLOT = "Slot";
        String TABLE_NAME = "ib_accounts";
        String SELECT_TABLE = "SELECT * FROM " + TABLE_NAME;
        String UUID = "UUID";
        String WORLD = "World";
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS ib_accounts(" + UUID + " TEXT, " + WORLD + " TEXT, " + PAGE + " int, " + SLOT + " int, " + ITEM + " TEXT);";

        static String addItem(@Nonnull UUID uuid, @Nonnull String worldName, int page, int slot, @Nonnull String itemStack) {
            return "INSERT INTO " + TABLE_NAME + "(" + UUID + ", " + WORLD + ", " + PAGE + ", " + SLOT + ", " + ITEM + ") VALUES (\"" + uuid + "\", \"" + worldName + "\", " + page + ", " + slot + ", \"" + itemStack + ");";
        }

        static String deleteUser(@Nonnull UUID uuid) {
            return "DELETE FROM " + TABLE_NAME + " WHERE " + UUID + " = \"" + uuid + "\"";
        }
    }

    interface GUIText {

        String ACCOUNT = "%s's Account";
        String ACCOUNTS = "Accounts";
        String BACK = "Back";
        String CLICK_TO_PURGE = "RIGHT-CLICK to purge.";
        String CLICK_TO_VIEW = "LEFT-CLICK to view.";
        String NEXT_PAGE = "Next Page";
        String PREVIOUS_PAGE = "Previous Page";
        String WORLD_PAGE = "%s - Page %d";
    }

    interface Messages {

        String ACCOUNT_ECON_WITHDRAW_FAIL = PREFIX + "You do not have enough money to complete the current transaction.";
        String ACCOUNT_ILLEGAL_AMOUNT = PREFIX + "Some of the items you deposited put you over the limit. They have been returned to you.";
        String BLACKLISTED_ITEM = PREFIX + "You attempted to deposit prohibited items into your account. They have been returned to you.";
        String CONFIG_INVALID_ITEM = PREFIX + "Invalid item ID %s in %s";
        String ECON_LOAD_FAIL_NO_SERVICE = "No economy service was detected. Disabling economy support.";
        String ECON_LOAD_SUCCESS = "Economy service detected and enabled.";
        String NO_PERMISSION = PREFIX + "Error: You do not have permission for this command.";
        String PLAYER_CMD = PREFIX + "Error: This is a viewer command.";
        String PLAYER_DNE = PREFIX + "Error: Player not found. Make sure you're spelling the name correctly.";
        String PURGE_ALL = PREFIX + "All accounts have been reset.";
        String PURGE_SINGLE = PREFIX + "Account reset.";
        String RELOAD_SUCCESS = PREFIX + "Config reloaded.";
        String SQL_EX = "Error: Unable to connect to the database.";

        static String accountWithdrawSuccess(@Nonnull String currencySymbol, double amount) {
            return PREFIX + "A fee of " + currencySymbol + amount + " has been deducted from your account.";
        }

        static String fileLoadFail(@Nonnull File file) {
            return PREFIX + "Could not delete " + file.getName() + ".";
        }

        static String invalidItem(@Nonnull UUID uuid, @Nonnull String worldName, int page, int slot, @Nonnull String itemStackString) {
            return "Invalid item in " + uuid + "'s account under " + worldName + ", page " + page + ", slot " + slot + ": " + itemStackString;
        }

        static String page(@Nonnull String name, int page) {
            return name + " - Page " + page;
        }
    }

    interface Permissions {

        String ACCOUNT = ID + ".account";
        String ADMIN = ACCOUNT + ".admin";
        String PAGE = ACCOUNT + ".page";
        String PLAYER = ACCOUNT + ".viewer";
        String PURGE = ID + ".purge";
        String RELOAD = ID + ".reload";
        String WORLD = ACCOUNT + ".world";
    }
}

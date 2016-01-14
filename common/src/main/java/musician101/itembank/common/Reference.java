package musician101.itembank.common;

import java.io.File;
import java.util.UUID;

public class Reference
{
    public static final String NAME = "${project.name}";
    public static final String ID = NAME.toLowerCase();
    public static final String DESCRIPTION = "${project.description}";
    public static final String PREFIX = "[" + NAME + "] ";

    public static class Commands
    {
        public static final String ACCOUNT_NAME = "/account";
        public static final String ACCOUNT_DESC = "Open the first page of your account.";
        public static final String HELP = "help";
        public static final String IB_CMD = "/" + ID;
        public static final String PAGE_ACCOUNT = getAccountArg("page");
        public static final String PLAYER = "player";
        public static final String PLAYER_ACCOUNT = getAccountArg(PLAYER);
        public static final String PURGE_NAME = "purge";
        public static final String PURGE_DESC = "Delete all or a specified player's account.";
        public static final String RELOAD_NAME = "reload";
        public static final String RELOAD_DESC = "Reload the plugin's config file.";
        public static final String UUID_NAME = "uuid";
        public static final String UUID_DESC = "Get a player's UUID.";
        public static final String WORLD_ACCOUNT = getAccountArg("world");

        private static String getAccountArg(String argName)
        {
            return argName + ":<" + argName + ">";
        }
    }

    public static class Configs
    {
        public static final String AMOUNT = "amount";
        public static final String DATABASE = "database";
        public static final String ENABLE = "enable";
        public static final String HOST = "host";
        public static final String ID = "id";
        public static final String ITEM_LIST = "item_list";
        public static final String LOCAL_HOST = "127.0.0.1";
        public static final String MULTI_WORLD = "multi_world";
        public static final String MYSQL = "mysql";
        public static final String PAGE_LIMIT = "page_limit";
        public static final String PASSWORD = "password";
        public static final String PORT = "port";
        public static final String PORT_DEFAULT = "3306";
        public static final String USER = "user";
        public static final String VARIATION = "variation";
        public static final String VARIATIONS = "variations";
        public static final String WHITELIST = "whitelist";
    }

    public static class Messages
    {
        public static final String ACCOUNT_WORLD_DNE = "That world does not exist.";
        public static final String IO_EX = "Error: An internal error has occurred. Please contact an administrator immediately.";
        public static final String NO_FILE_EX = "Error: File not found. Please contact an administrator immediately.";
        public static final String NO_PERMISSION = "Error: You do not have permission for this command.";
        public static final String PARSE_EX = "Error: Failed to parse data.";
        public static final String PLAYER_CMD = "Error: This is a player command.";
        public static final String PLAYER_DNE = "Error: Player not found. Make sure you're spelling the name correctly.";
        public static final String PURGE_MULTIPLE = "All accounts have been reset.";
        public static final String PURGE_NO_FILE = "File not found. Please check spelling and capitalization.";
        public static final String PURGE_SINGLE = "Account reset.";
        public static final String RELOAD_SUCCESS = "Config reloaded.";
        public static final String SQL_EX = "Error: Unable to connect to the database.";
        public static final String UNKNOWN_EX = "An unknown error has occurred while obtaining the player's UUID.";

        public static String fileCreateFail(File file)
        {
            return PREFIX + "Could not create " + file.getName() + ".";
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
    }
}

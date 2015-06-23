package musician101.itembank.forge.reference;

public class Constants
{
	public static class ModInfo
	{
		public static final String ID = "itembank";
		public static final String NAME = "ItemBank";
		public static final String VERSION = "3.0";
	}
	
	/** Commands Names */
	public static class Commands
	{
		public static final String ACCOUNT_CMD = "account";
		public static final String HELP_CMD = "help";
		public static final String ITEMBANK_CMD = "itembank";
		public static final String PURGE_CMD = "purge";
		public static final String RELOAD_CMD = "reload";
		public static final String UUID_CMD = "uuid";
	}
	
	/** Config */
	public static class Configuration
	{
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
	}
	
	/** Permissions */
	public static class Permissions
	{
		public static final String ACCOUNT_PERM = Commands.ITEMBANK_CMD + "." + Commands.ACCOUNT_CMD;
		public static final String ADMIN_PERM = Commands.ITEMBANK_CMD + ".admin";
		public static final String ADMIN_ACCOUNT_PERM = ADMIN_PERM + "." + Commands.ACCOUNT_CMD;
		public static final String EXEMPT_PERM = ADMIN_PERM + ".exempt";
		public static final String HELP_PERM = Commands.ITEMBANK_CMD + "." + Commands.HELP_CMD;
		public static final String PURGE_PERM = ADMIN_PERM + "." + Commands.PURGE_CMD;
		public static final String RELOAD_PERM = ADMIN_PERM + "." + Commands.RELOAD_CMD;
		public static final String UUID_PERM = ADMIN_PERM + "." + Commands.UUID_CMD;
	}
}

package musician101.itembank.lib;

public class Constants
{
	/** Commands Names */
	public static final String ACCOUNT_CMD = "account";
	public static final String ITEMBANK_CMD = "itembank";
	public static final String PURGE_CMD = "purge";
	public static final String RELOAD_CMD = "reload";
	public static final String HELP_CMD = "help";
	
	/** Config */
	public static final String BLACKLIST = "blacklist";
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
	
	/** Language Config */
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
	
	/** Permissions */
	public static final String ACCOUNT_PERM = ITEMBANK_CMD + "." + ACCOUNT_CMD;
	public static final String ADMIN_PERM = ITEMBANK_CMD + ".admin";
	public static final String ADMIN_ACCOUNT_PERM = ADMIN_PERM + "." + ACCOUNT_CMD;
	public static final String EXEMPT_PERM = ADMIN_PERM + ".exempt";
	public static final String PURGE_PERM = ADMIN_PERM + "." + PURGE_CMD;
	public static final String RELOAD_PERM = ADMIN_PERM + "." + RELOAD_CMD;
}

package musician101.itembank.forge.reference;

public class Messages
{
	/** Commands */
	private static final String COMMANDS = "commands.itembank.";
	public static final String ITEMBANK_DESC = COMMANDS + "description";
	public static final String ITEMBANK_USAGE = COMMANDS + "usage";
	public static final String NO_PERMISSION = COMMANDS + "no-permission";
	public static final String PLAYER_COMMAND = COMMANDS + "player-command";
	public static final String PLAYER_DNE = COMMANDS + "player-dne";
	public static final String WORLD_DNE = COMMANDS + "world-dne";
	
	private static final String ACCOUNT = COMMANDS + "account.";
	public static final String ACCOUNT_DESC = ACCOUNT + "desc";
	public static final String ACCOUNT_ILLEGAL_ITEM = ACCOUNT + "illegal-item";
	public static final String ACCOUNT_ILLEGAL_PAGE = ACCOUNT + "illegal-page";
	public static final String ACCOUNT_INVALID_PAGE = ACCOUNT + "invalid-page";
	public static final String ACCOUNT_NO_PERMISSION = ACCOUNT + "no-permission";
	public static final String ACCOUNT_PAGE = ACCOUNT + "page";
	public static final String ACCOUNT_UPDATED = ACCOUNT + "updated";
	public static final String ACCOUNT_USAGE = ACCOUNT + "usage";
	
	private static final String HELP = COMMANDS + "help.";
	public static final String HELP_DESC = HELP + "description";
	public static final String HELP_USAGE = HELP + "usage";
	
	private static final String PERMISSION = COMMANDS + "permission.";
	public static final String PERMISSION_DESC = PERMISSION + "description";
	public static final String PERMISSION_SUCCESS = PERMISSION + "success";
	public static final String PERMISSION_USAGE = PERMISSION + "usage";
	
	private static final String PURGE = COMMANDS + "purge.";
	public static final String PURGE_DESC = PURGE + "description";
	public static final String PURGE_MULTIPLE = PURGE + "multiple";
	public static final String PURGE_SINGLE = PURGE + "single";
	public static final String PURGE_USAGE = PURGE + "usage";
	
	/** Exceptions */
	private static final String ERROR = "general.error.";
	public static final String IO_EX = ERROR + "io";
	public static final String FILE_DNE = ERROR + "file-dne";
}

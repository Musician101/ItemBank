package musician101.itembank.lib;

import org.bukkit.ChatColor;
 
/**
 * List of strings used throughout the plugin.
 * 
 * @author Musician101
 */
public class Messages 
{
	/** Formatting. */
	public static String PREFIX = ChatColor.DARK_RED + "[ItemBank] ";
	
	/** Error messages. */
	public static final String AIR_BLOCK = PREFIX + "Silly player, you can't use air. :3";
	public static final String AMOUNT_ERROR = PREFIX + "Error: Amount must be greater than 0.";
	public static final String FULL_INV = PREFIX + "Sorry, but you're invenotry is to full to accept any more items.";
	public static final String ITEM_NOT_FOUND = PREFIX + "Error: You do not have any of the specified item.";
	public static final String LACK_MONEY = PREFIX + "You lack the money to cover the transaction fee.";
	public static final String NO_DEPOSIT = PREFIX + "Sorry, but that item is not depositable.";
	public static final String NO_PERMISSION = PREFIX + "You do not have permission for this command.";
	public static final String NOT_ENOUGH_ARGUMENTS = PREFIX + "Error: Not enough arguments.";
	public static final String PARTIAL_DEPOSIT = PREFIX + "Sorry, but there was not enough room for your full deposit.";
	public static final String PLAYER_COMMAND_ONLY = PREFIX + "Error: This is a player command only.";
	
	public static String getAliasError(String alias)
	{
		return PREFIX + "Error: " + alias.toUpperCase() + " is not a valid alias.";
	}
	
	public static String getConfigValueError(String key, String value)
	{
		return PREFIX + "Error with '" + key + ":" + value + "'.";
	}
	
	public static String getCustomItemWithdrawError(String name)
	{
		return PREFIX + "Error getting " + name + ". Please contact an administrator.";
	}
	
	public static String getFileAmountError(String name)
	{
		return PREFIX + "Error getting amount for " + name + ". Check that the amount is a number greater than -1.";
	}
	
	public static String getFileDurabilityError(String name)
	{
		return PREFIX + "Error getting durability for " + name + ". Check that the durability is a number greater than -1.";
	}
	
	public static String getInvalidArgumentError(String arg)
	{
		return PREFIX + "Error: " + arg + " is not a valid argument.";
	}
	
	public static String getMaxedDepositMessage(String material)
	{
		return PREFIX + "Sorry, but your account cannot hold any more " + material + ".";
	}
	
	/** Exception messages. */
	public static final String FILE_NOT_FOUND = PREFIX + "Error: The file does not exist. Please contact an admin.";
	public static final String IO_EXCEPTION = PREFIX + "An internal server error has occured. Please alert an admin.";
	public static final String NULL_POINTER = PREFIX + "Error: The ItemTranslator failed to load. Please contact an admin.";
	public static final String NUMBER_FORMAT = PREFIX + "Error: The amount you entered is not a number.";
	public static final String YAML_EXCEPTION = PREFIX + "Error: Improper file format. Please check for TABs.";
	
	/** Other */
	public static String getTransactionFeeMessage(double cost)
	{
		return PREFIX + "A " + cost + " transaction fee has been deducted from your account.";
	}
	
	public static String getDepositSuccess(String material, int amount)
	{
		return PREFIX + "You have deposited " + amount + " " + material + ".";
	}
}

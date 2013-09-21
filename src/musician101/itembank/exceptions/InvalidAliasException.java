package musician101.itembank.exceptions;

/**
 * An Exception for catching invalid aliases.
 * 
 * @author Musician101
 */
@SuppressWarnings("serial")
public class InvalidAliasException extends Exception
{
	public InvalidAliasException(String s)
	{
		super(s);
	}
	
	public InvalidAliasException(Throwable cause)
	{
		super(cause);
	}
}

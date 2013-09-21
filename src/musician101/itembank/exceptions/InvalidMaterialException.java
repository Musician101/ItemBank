package musician101.itembank.exceptions;

/**
 * An exception for catching invalid Material names.
 * 
 * @author Musician101
 */
@SuppressWarnings("serial")
public class InvalidMaterialException extends Exception
{
	public InvalidMaterialException(String s)
	{
		super(s);
	}
	
	public InvalidMaterialException(Throwable cause)
	{
		super(cause);
	}
}

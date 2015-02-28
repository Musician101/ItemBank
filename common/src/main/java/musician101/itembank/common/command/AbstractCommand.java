package musician101.itembank.common.command;

public abstract class AbstractCommand
{
	boolean isPlayerOnly;
	String description;
	String name;
	String usage;
	
	public AbstractCommand(String name, String description, String usage, boolean isPlayerOnly)
	{
		this.name = name;
		this.description = description;
		this.usage = usage;
		this.isPlayerOnly = isPlayerOnly;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public String getUsage()
	{
		return usage;
	}
	
	public boolean isPlayerOnly()
	{
		return isPlayerOnly;
	}
	
	public abstract String getCommandHelpInfo();
}

package musician101.sponge.itembank.command.itembank;

import java.io.File;
import java.io.IOException;
import java.util.List;

import musician101.sponge.itembank.ItemBank;
import musician101.sponge.itembank.lib.Reference.Constants;
import musician101.sponge.itembank.lib.Reference.Messages;
import musician101.sponge.itembank.util.IBUtils;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.util.command.CommandCallable;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandSource;

import com.google.common.base.Optional;

//TODO need LUC implementation
@Deprecated
public class IBCommand implements CommandCallable
{
	@Override
	public List<String> getSuggestions(CommandSource source, String arguments) throws CommandException
	{
		return null;
	}

	@Override
	public boolean call(CommandSource source, String arguments, List<String> parents) throws CommandException
	{
		String args[] = arguments.split("\\s+");
		if (args.length > 0)
		{
			/** Help Command */
			if (args[0].equalsIgnoreCase(Constants.HELP_CMD))
			{
				if (args.length > 1)
				{
					if (args[1].equalsIgnoreCase(Constants.ACCOUNT_CMD))
					{
						IBUtils.sendMessages((Player) source, Messages.ACCOUNT_HELP_MSG);
						return true;
					}
					else if (args[1].equalsIgnoreCase(Constants.RELOAD_CMD))
					{
						IBUtils.sendMessages((Player) source, Messages.RELOAD_HELP_MSG);
						return true;
					}
					else if (args[1].equalsIgnoreCase(Constants.PURGE_CMD))
					{
						IBUtils.sendMessages((Player) source, Messages.PURGE_HELP_MSG);
						return true;
					}
					else if (args[1].equalsIgnoreCase(Constants.UUID_CMD))
					{
						IBUtils.sendMessages((Player) source, Messages.UUID_HELP_MSG);
						return true;
					}
				}
				
				source.sendMessage(Messages.PREFIX + Messages.HELP_MSG.get(3));
				return true;
			}
			/** Purge Command */
			else if (args[0].equalsIgnoreCase(Constants.PURGE_CMD))
			{
				if (!source.hasPermission(Constants.PURGE_PERM))
				{
					source.sendMessage(Messages.NO_PERMISSION);
					return true;
				}
				
				if (args.length > 1)
				{
					File file = new File(ItemBank.getPlayerData(), args[1] + "." + ItemBank.getConfig().format);
					if (!file.exists())
					{
						source.sendMessage(Messages.PURGE_NO_FILE);
						return true;
					}
					
					file.delete();
					try
					{
						IBUtils.createPlayerFile(file);
					}
					catch (IOException e)
					{
						source.sendMessage(Messages.IO_EX);
						return true;
					}
					
					source.sendMessage(Messages.PURGE_SINGLE);
					return true;
				}
				
				for (File file : ItemBank.getPlayerData().listFiles())
					file.delete();
				
				try
				{
					IBUtils.createPlayerFiles();
				}
				catch (IOException e)
				{
					source.sendMessage(Messages.IO_EX);
					return true;
				}
				
				source.sendMessage(Messages.PURGE_MULTIPLE);
				return true;
			}
			/** Reload Command */
			else if (args[0].equalsIgnoreCase(Constants.RELOAD_CMD))
			{
				if (!source.hasPermission(Constants.RELOAD_PERM))
				{
					source.sendMessage(Messages.NO_PERMISSION);
					return true;
				}
				
				ItemBank.getConfig().reloadConfiguration();
				source.sendMessage(Messages.RELOAD_SUCCESS);
				return true;
			}
		}
		
		IBUtils.sendMessages(source, Messages.HELP_MSG);
		return true;
	}

	@Override
	public boolean testPermission(CommandSource source)
	{
		return false;
	}

	@Override
	public Optional<String> getShortDescription()
	{
		return null;
	}

	@Override
	public Optional<String> getHelp()
	{
		return null;
	}

	@Override
	public String getUsage()
	{
		return null;
	}
}

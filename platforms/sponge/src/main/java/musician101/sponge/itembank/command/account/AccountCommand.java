package musician101.sponge.itembank.command.account;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import musician101.itembank.common.command.AbstractCommand;
import musician101.sponge.itembank.ItemBank;
import musician101.sponge.itembank.lib.Reference.Constants;
import musician101.sponge.itembank.lib.Reference.Messages;
import musician101.sponge.itembank.util.IBUtils;

import org.json.simple.parser.ParseException;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandCallable;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.world.World;

import com.google.common.base.Optional;

//TODO need to reorder methods for similarity between this and the interface
//TODO Special parameters for arguments to make coding easier
@Deprecated
public class AccountCommand implements CommandCallable
{
	//TODO update bukkit/spigot version to use special parameters before applying it here
	// sender.getName() and playerName are not always the same.
	@Deprecated
	public void openInv(Player player, String worldName, String uuid, int page)
	{
		Inventory inv = null;
		try
		{
			inv = IBUtils.getAccount(worldName, uuid, page);
		}
		catch (FileNotFoundException e)
		{
			player.sendMessage(Messages.NO_FILE_EX);
			return;
		}
		catch (IOException e)
		{
			player.sendMessage(Messages.IO_EX);
			return;
		}
		catch (ParseException e)
		{
			player.sendMessage(Messages.YAML_PARSE_EX);
			return;
		}
		
		player.openInventory(inv);
		return;
	}

	@Override
	public List<String> getSuggestions(CommandSource source, String arguments) throws CommandException
	{
		return null;
	}

	@Override
	public boolean call(CommandSource source, String arguments, List<String> parents) throws CommandException
	{
		String args[] = arguments.split("\\s+");
		if (!(source instanceof Player))
		{
			source.sendMessage(Texts.builder(Messages.PLAYER_CMD).build());
			return true;
		}
		
		Player player = (Player) source;
		if (args.length > 0)
		{
			if (IBUtils.isNumber(args[0]))
			{
				if (!player.hasPermission(Constants.ACCOUNT_PERM))
				{
					player.sendMessage(Messages.NO_PERMISSION);
					return true;
				}
				
				openInv(player, IBUtils.getWorldName(player), player.getUniqueId().toString(), Integer.valueOf(args[0]));
				return true;
			}
			
			if (!player.hasPermission(Constants.ADMIN_ACCOUNT_PERM))
			{
				player.sendMessage(Messages.NO_PERMISSION);
				return true;
			}
			
			//TODO no reliable way to get a player based on username
			Player p = null; //plugin.getGame().getPlayer(args[0]);
			try
			{
				if (args.length > 1)
				{
					if (IBUtils.isNumber(args[1]))
					{
						openInv(player, IBUtils.getWorlds().get(0).getName(), p.getUniqueId().toString(), Integer.valueOf(args[1]));
						return true;
					}
					
					openInv(player, args[0], p.getUniqueId().toString(), Integer.valueOf(args[2]));
					return true;
				}
				
				openInv(player, ((List<World>) ItemBank.getGame().getServer().get().getWorlds()).get(0).getName(), p.getUniqueId().toString(), 1);
				return true;
			}
			catch (NullPointerException e)
			{
				player.sendMessage(Messages.PLAYER_DNE);
				return true;
			}
		}
		
		if (!player.hasPermission(Constants.ACCOUNT_PERM))
		{
			player.sendMessage(Messages.NO_PERMISSION);
			return true;
		}
		
		openInv(player, IBUtils.getWorldName(player), player.getUniqueId().toString(), 1);
		return true;
	}

	@Override
	public boolean testPermission(CommandSource source)
	{
		return false;
	}

	@Override
	public Optional<? extends Text> getHelp(CommandSource arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<? extends Text> getShortDescription(CommandSource arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Text getUsage(CommandSource arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}
}

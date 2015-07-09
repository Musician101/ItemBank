package musician101.sponge.itembank.command.account;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

import musician101.sponge.itembank.ItemBank;
import musician101.sponge.itembank.lib.Reference;
import musician101.sponge.itembank.lib.Reference.Messages;
import musician101.sponge.itembank.util.AccountUtil;
import musician101.sponge.itembank.util.IBUtils;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import org.json.simple.parser.ParseException;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;
import org.spongepowered.api.world.World;

import com.google.common.base.Optional;

public class AccountExecutor implements CommandExecutor
{
	private final String ACCOUNT = Reference.ID + ".account";
	private final String ADMIN = ACCOUNT + ".admin";
	private final String PAGE = ACCOUNT + ".page";
	private final String PLAYER = ACCOUNT + ".player";
	private final String WORLD = ACCOUNT + ".world";
	
	public void openInv(Player player, World world, UUID uuid, int page)
	{
		Inventory inv = null;
		try
		{
			inv = AccountUtil.getAccount(world, uuid, page);
		}
		catch (ClassNotFoundException | SQLException e)
		{
			player.sendMessage(Messages.SQL_EX);
			return;
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
		catch (ObjectMappingException | ParseException e)
		{
			player.sendMessage(Messages.PARSE_EX);
			return;
		}
		
		player.openInventory(inv);
		return;
	}

	@Override
	public CommandResult execute(CommandSource source, CommandContext args) throws CommandException
	{
		if (!(source instanceof Player))
		{
			source.sendMessage(Messages.PLAYER_CMD);
			return CommandResult.empty();
		}
		
		final String accountPerm = Reference.NAME.toLowerCase() + ".account";
		Player player = (Player) source;
		int page = 1;
		Optional<Object> pageOptional = args.getOne("page");
		Optional<Object> playerOptional = args.getOne("player");
		Optional<Object> worldOptional = args.getOne("world");
		World world = player.getWorld();
		UUID uuid = player.getUniqueId();
		if (pageOptional.isPresent())
		{
			String pageString = pageOptional.get().toString();
			if (IBUtils.isNumber(pageString))
				page = Integer.parseInt(args.getOne("page").toString());
			
			if (page <= 0)
				page = 1;
		}
		
		if (playerOptional.isPresent())
		{
			String playerString = playerOptional.get().toString();
			uuid = null;
			try
			{
				uuid = IBUtils.getUUIDOf(playerString);
			}
			catch (Exception e)
			{
				player.sendMessage(Messages.UNKNOWN_EX);
				return CommandResult.empty();
			}
			
			if (uuid == null)
			{
				player.sendMessage(Messages.PLAYER_DNE);
				return CommandResult.empty();
			}
		}
		
		if (worldOptional.isPresent())
		{
			String worldString = worldOptional.get().toString();
			Optional<World> wo = ItemBank.game.getServer().getWorld(worldString);
			if (!wo.isPresent())
			{
				player.sendMessage(Messages.ACCOUNT_WORLD_DNE);
				return CommandResult.empty();
			}
			
			world = wo.get();
		}
		
		if (!player.hasPermission(accountPerm))
		{
			player.sendMessage(Messages.NO_PERMISSION);
			return CommandResult.success();
		}
		
		if (!canAccessPage(player, uuid, page, world))
		{
			player.sendMessage(Messages.NO_PERMISSION);
			return CommandResult.empty();
		}
		
		openInv(player, world, player.getUniqueId(), 1);
		return CommandResult.success();
	}
	
	private boolean canAccessPage(Player player, UUID owner, int page, World world)
	{
		if (player.hasPermission(PAGE) || page < ItemBank.config.getPageLimit())
			return true;
		
		if (player.getUniqueId() != owner)
			return player.hasPermission(PLAYER);
		
		if (player.hasPermission(WORLD) || (player.getWorld() != world && ItemBank.config.isMultiWorldStorageEnabled()))
			return true;
		
		return player.hasPermission(ADMIN);
	}
}

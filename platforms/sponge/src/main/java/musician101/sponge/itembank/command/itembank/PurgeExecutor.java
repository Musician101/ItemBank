package musician101.sponge.itembank.command.itembank;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import musician101.sponge.itembank.ItemBank;
import musician101.sponge.itembank.lib.Reference.Messages;
import musician101.sponge.itembank.util.IBUtils;

import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;

import com.google.common.base.Optional;

public class PurgeExecutor implements CommandExecutor
{

	@Override
	public CommandResult execute(CommandSource source, CommandContext args) throws CommandException
	{
		if (!source.hasPermission("itembank.purge"))
		{
			source.sendMessage(Messages.NO_PERMISSION);
			return CommandResult.empty();
		}
		
		Optional<Object> opt = args.getOne("player");
		if (opt.isPresent())
		{
			UUID uuid = null;
			try
			{
				uuid = IBUtils.getUUIDOf(opt.get().toString());
			}
			catch (Exception e)
			{
				source.sendMessage(Messages.UNKNOWN_EX);
				return CommandResult.empty();
			}
			
			if (ItemBank.mysql != null)
			{
				try
				{
					ItemBank.mysql.querySQL("DROP TABLE IF EXISTS ib_" + uuid.toString().replace("-", "_"));
				}
				catch (ClassNotFoundException | SQLException e)
				{
					source.sendMessage(Messages.SQL_EX);
					return CommandResult.empty();
				}
				
				source.sendMessage(Messages.PURGE_SINGLE);
				return CommandResult.success();
			}
			
			File file = ItemBank.config.getPlayerFile(uuid);
			if (!file.exists())
			{
				source.sendMessage(Messages.PURGE_NO_FILE);
				return CommandResult.empty();
			}
			
			file.delete();
			source.sendMessage(Messages.PURGE_SINGLE);
			return CommandResult.success();
		}
		
		if (ItemBank.mysql != null)
		{
			try
			{
				ResultSet rs = ItemBank.mysql.getConnection().getMetaData().getTables(null, null, null, new String[]{"TABLE"});
				while (rs.next())
					if (rs.getString(3).contains("ib_"))
						ItemBank.mysql.querySQL("DROP TABLE " + rs.getString(3));
			}
			catch (SQLException | ClassNotFoundException e)
			{
				source.sendMessage(Messages.SQL_EX);
				return CommandResult.empty();
			}
			
			source.sendMessage(Messages.PURGE_MULTIPLE);
			return CommandResult.success();
		}
		
		for (File file : ItemBank.config.getPlayerData().listFiles())
			file.delete();
		
		source.sendMessage(Messages.PURGE_MULTIPLE);
		return CommandResult.empty();
	}
}

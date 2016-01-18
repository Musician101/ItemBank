package musician101.itembank.spigot.command.itembank;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.UUID;

import musician101.common.java.minecraft.command.AbstractCommandArgument.Syntax;
import musician101.common.java.minecraft.spigot.command.AbstractSpigotCommand;
import musician101.common.java.minecraft.spigot.command.SpigotCommandArgument;
import musician101.itembank.spigot.SpigotItemBank;
import musician101.itembank.spigot.lib.Messages;
import musician101.itembank.spigot.util.IBUtils;

import org.bukkit.command.CommandSender;
import org.json.simple.parser.ParseException;

public class PurgeCommand extends AbstractSpigotCommand
{
	private final SpigotItemBank plugin;

	public PurgeCommand(SpigotItemBank plugin)
	{
		super("purge", "Delete all or a specified player's account.", Arrays.asList(new SpigotCommandArgument("/itembank"), new SpigotCommandArgument("purge"), new SpigotCommandArgument("player", Syntax.OPTIONAL)), 0, "itembank.purge", false, Messages.NO_PERMISSION, Messages.PLAYER_CMD);
        this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] args)
	{
		if (!canSenderUseCommand(sender))
			return false;
		
		UUID uuid;
		try
		{
			uuid = IBUtils.getUUIDOf(args[0]);
		}
		catch (InterruptedException | IOException | ParseException e)
		{
			sender.sendMessage(Messages.UNKNOWN_EX);
			return false;
		}
		
		if (args.length > 0)
		{
			if (plugin.getPluginConfig().useMySQL())
			{
				try
				{
					plugin.getMySQLHandler().querySQL("DROP TABLE IF EXISTS ib_" + uuid.toString().replace("-", "_"));
				}
				catch (ClassNotFoundException | SQLException e)
				{
					sender.sendMessage(Messages.SQL_EX);
					return false;
				}
				
				sender.sendMessage(Messages.PURGE_SINGLE);
				return true;
			}
			
			File file = plugin.getPluginConfig().getPlayerFile(uuid);
			if (!file.exists())
			{
				sender.sendMessage(Messages.PURGE_NO_FILE);
				return false;
			}
			
			file.delete();
			try
			{
				IBUtils.createPlayerFile(file);
			}
			catch (IOException e)
			{
				sender.sendMessage(Messages.IO_EX);
				return false;
			}
			
			sender.sendMessage(Messages.PURGE_SINGLE);
			return true;
		}
		
		if (plugin.getPluginConfig().useMySQL())
		{
			try
			{
				ResultSet rs = plugin.getMySQLHandler().getConnection().getMetaData().getTables(null, null, null, new String[]{"TABLE"});
				while (rs.next())
				{
					sender.sendMessage(rs.getString(3));
					if (rs.getString(3).contains("ib_"))
						plugin.getMySQLHandler().querySQL("DROP TABLE " + rs.getString(3));
				}
			}
			catch (SQLException | ClassNotFoundException e)
			{
				sender.sendMessage(Messages.SQL_EX);
				return false;
			}
			
			sender.sendMessage(Messages.PURGE_MULTIPLE);
			return true;
		}

        //noinspection ConstantConditions
        for (File file : plugin.getPluginConfig().getPlayerData().listFiles())
			file.delete();
		
		try
		{
			IBUtils.createPlayerFiles(plugin);
		}
		catch (IOException e)
		{
			sender.sendMessage(Messages.IO_EX);
			return false;
		}
		
		sender.sendMessage(Messages.PURGE_MULTIPLE);
		return true;
	}
}

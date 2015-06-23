package musician101.itembank.forge.command.itembank;

import java.io.File;

import musician101.itembank.forge.ItemBank;
import musician101.itembank.forge.command.AbstractForgeCommand;
import musician101.itembank.forge.config.ConfigHandler;
import musician101.itembank.forge.lib.Messages;
import musician101.itembank.forge.util.IBUtils;
import net.minecraft.command.ICommandSender;

public class PurgeCommand extends AbstractForgeCommand
{
	public PurgeCommand()
	{
		name = "purge";
		usage = Messages.PURGE_USAGE;
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args)
	{
		if (!ItemBank.permissions.hasPermission(sender, "itembank.purge"))
		{
			sender.addChatMessage(IBUtils.getTranslatedChatComponent(Messages.NO_PERMISSION));
			return;
		}
		
		if (args.length > 1)
		{
			/*if (plugin.getPluginConfig().useMySQL())
			{
				try
				{
					plugin.getMySQLHandler().querySQL("DROP TABLE IF EXISTS ib_" + UUIDFetcher.getUUIDOf(args.get(0)));
				}
				catch (Exception e)
				{
					if (e instanceof ClassNotFoundException || e instanceof SQLException)
						sender.sendMessage(Messages.SQL_EX);
					else
						sender.sendMessage(Messages.UNKNOWN_EX);
					
					return false;
				}
				
				sender.addChatMessage(Messages.PURGE_SINGLE);
				return true;
			}*/
			
			File file = new File(ConfigHandler.bankDirectory, args[1] + "." + ConfigHandler.format);
			if (!file.exists())
			{
				sender.addChatMessage(IBUtils.getTranslatedChatComponent(Messages.FILE_DNE));
				return;
			}
			
			file.delete();
			sender.addChatMessage(IBUtils.getTranslatedChatComponent(Messages.PURGE_SINGLE));
			return;
		}
		
		/*if (plugin.getPluginConfig().useMySQL())
		{
			try
			{
				ResultSet rs = plugin.getMySQLHandler().getConnection().getMetaData().getTables(null, null, null, new String[]{"TABLE"});
				while (rs.next())
				{
					sender.addChatMessage(rs.getString(3));
					if (rs.getString(3).contains("ib_"))
						plugin.getMySQLHandler().querySQL("DROP TABLE " + rs.getString(3));
				}
			}
			catch (SQLException | ClassNotFoundException e)
			{
				sender.sendMessage(Messages.SQL_EX);
				return false;
			}
			
			sender.addChatMessage(Messages.PURGE_MULTIPLE);
			return true;
		}*/
		
		for (File file : ConfigHandler.bankDirectory.listFiles())
			file.delete();
		
		sender.addChatMessage(IBUtils.getTranslatedChatComponent(Messages.PURGE_MULTIPLE));
	}
}

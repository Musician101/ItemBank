package musician101.itembank.forge.command.itembank;

import java.util.UUID;

import musician101.itembank.forge.ItemBank;
import musician101.itembank.forge.command.AbstractForgeCommand;
import musician101.itembank.forge.reference.Messages;
import musician101.itembank.forge.util.IBUtils;
import musician101.itembank.forge.util.permission.PermissionHolder;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;

public class PermissionCommand extends AbstractForgeCommand
{
	public PermissionCommand()
	{
		this.name = "permission";
		this.usage = Messages.PERMISSION_USAGE;
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException
	{
		if (sender instanceof EntityPlayer && !ItemBank.permissions.getPlayerPermissions((EntityPlayer) sender).canEditPermissions())
		{
			sender.addChatMessage(IBUtils.getTranslatedChatComponent(Messages.NO_PERMISSION));
			return;
		}
		
		if (args.length < 3)
			throw new WrongUsageException(usage, new Object[0]);
		
		UUID uuid = EntityPlayer.getOfflineUUID(args[0]);
		if (uuid == null)
			throw new PlayerNotFoundException();
		
		String key = args[1];
		String value = args[2];
		PermissionHolder perms = ItemBank.permissions.getPlayerPermissions(uuid);
		if (key.equalsIgnoreCase("account"))
			perms.setAccountAccess(parseBoolean(value));
		else if (key.equalsIgnoreCase("account-admin"))
			perms.setAccountAdmin(parseBoolean(value));
		else if (key.equalsIgnoreCase("account-max-pages"))
			perms.setMaxPages(parseInt(value));
		else if (key.equalsIgnoreCase("account-page-all"))
			perms.setAllPageAccess(parseBoolean(value));
		else if (key.equalsIgnoreCase("account-player"))
			perms.setAccessToOtherPlayerBanks(parseBoolean(value));
		else if (key.equalsIgnoreCase("add-world-id"))
			perms.addWorldId(parseInt(value));
		else if (key.equalsIgnoreCase("permission"))
			perms.setEditPermissions(parseBoolean(value));
		else if (key.equalsIgnoreCase("purge"))
			perms.setPurgeAccess(parseBoolean(value));
		
		ItemBank.permissions.updatePermissions(uuid, perms);
		sender.addChatMessage(IBUtils.getTranslatedChatComponent(Messages.PERMISSION_SUCCESS));
	}
}

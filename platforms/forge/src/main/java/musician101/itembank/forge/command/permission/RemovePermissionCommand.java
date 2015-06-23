package musician101.itembank.forge.command.permission;

import java.util.UUID;

import musician101.itembank.forge.ItemBank;
import musician101.itembank.forge.command.AbstractForgeCommand;
import musician101.itembank.forge.lib.Messages;
import musician101.itembank.forge.util.IBUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;

public class RemovePermissionCommand extends AbstractForgeCommand
{
	public RemovePermissionCommand()
	{
		this.name = "remove";
		this.usage = "/itembank permission remove <player> <permissions>";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException
	{
		if (!ItemBank.permissions.hasPermission(sender, "itembank.permission.add"))
		{
			sender.addChatMessage(IBUtils.getTranslatedChatComponent(Messages.NO_PERMISSION));
			return;
		}
		
		if (args.length == 0)
			throw new WrongUsageException(Messages.PERMISSION_ADD_USAGE, new Object[0]);
		
		UUID uuid = EntityPlayer.getOfflineUUID(args[0]);
		if (uuid == null)
			throw new PlayerNotFoundException();
		
		ItemBank.permissions.removePermissions(uuid, args[1].split(","));
		sender.addChatMessage(IBUtils.getTranslatedChatComponent(Messages.PERMISSION_REMOVE_SUCCESS));
	}
}

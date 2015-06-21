package musician101.itembank.forge.command.permission;

import java.util.UUID;

import musician101.itembank.forge.ItemBank;
import musician101.itembank.forge.command.AbstractForgeCommand;
import musician101.itembank.forge.lib.Messages;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
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
			sender.addChatMessage(Messages.NO_PERMISSION);
			return;
		}
		
		UUID uuid = EntityPlayer.getOfflineUUID(args[0]);
		if (uuid == null)
		{
			sender.addChatMessage(Messages.PLAYER_DNE);
			return;
		}
		
		ItemBank.permissions.removePermissions(uuid, args[1].split(","));
		sender.addChatMessage(Messages.PREFIX.appendText("Permissions removed."));
	}
}

package musician101.itembank.forge.command.account;

import java.io.IOException;

import musician101.itembank.forge.ItemBank;
import musician101.itembank.forge.command.AbstractForgeCommand;
import musician101.itembank.forge.inventory.BankInventory;
import musician101.itembank.forge.reference.Messages;
import musician101.itembank.forge.util.IBUtils;
import musician101.itembank.forge.util.permission.PermissionHolder;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import com.mojang.authlib.GameProfile;

public class AccountCommand extends AbstractForgeCommand
{
	public AccountCommand()
	{
		this.name = "account";
		this.usage = Messages.ACCOUNT_USAGE;
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException
	{
		if (!(sender instanceof EntityPlayer))
		{
			sender.addChatMessage(IBUtils.getTranslatedChatComponent(Messages.PLAYER_COMMAND));
			return;
		}
		
		EntityPlayer player = (EntityPlayer) sender;
		GameProfile owner = player.getGameProfile();
		int page = 1;
		PermissionHolder perms = ItemBank.permissions.getPlayerPermissions(player);
		World world = player.worldObj;
		if (!perms.canAccessAccount())
		{
			player.addChatMessage(IBUtils.getTranslatedChatComponent(Messages.NO_PERMISSION));
			return;
		}
		
		if (args.length > 0)
		{
			for (String arg : args)
			{
				if (arg.contains(":"))
				{
					String key = arg.split(":")[0];
					String value = arg.split(":")[1];
					if (key.equalsIgnoreCase("player") && perms.canAccessOtherPlayerBanks())
					{
						owner = new GameProfile(EntityPlayer.getOfflineUUID(value), value);
						if (!owner.isComplete())
							throw new PlayerNotFoundException();
					}
					
					if (key.equalsIgnoreCase("page"))
					{
						page = parseInt(value);
						if (page <= 0)
						{
							player.addChatMessage(IBUtils.getTranslatedChatComponent(Messages.ACCOUNT_INVALID_PAGE));
							return;
						}
					}
					
					if (key.equalsIgnoreCase("world"))
					{
						int worldId = parseInt(value);
						world = DimensionManager.getWorld(worldId);
						if (world == null)
						{
							player.addChatMessage(IBUtils.getTranslatedChatComponent((Messages.WORLD_DNE)));
							return;
						}
					}
				}
			}
		}
		
		BankInventory bank;
		try
		{
			bank = new BankInventory(player, owner, world.provider.getDimensionId(), page);
		}
		catch (IOException e)
		{
			player.addChatMessage(IBUtils.getTranslatedChatComponent(Messages.IO_EX));
			return;
		}
		
		if (bank.hasPermission(perms))
			bank.openInventory(player);
		
		player.addChatMessage(IBUtils.getTranslatedChatComponent(Messages.ACCOUNT_NO_PERMISSION));
	}
}

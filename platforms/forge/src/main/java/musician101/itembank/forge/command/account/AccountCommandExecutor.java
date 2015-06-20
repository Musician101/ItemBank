package musician101.itembank.forge.command.account;

import java.io.IOException;

import musician101.itembank.forge.ItemBank;
import musician101.itembank.forge.command.AbstractForgeCommand;
import musician101.itembank.forge.inventory.BankInventory;
import musician101.itembank.forge.lib.Messages;
import musician101.itembank.forge.util.IBUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import com.mojang.authlib.GameProfile;

public class AccountCommandExecutor extends AbstractForgeCommand
{
	ItemBank plugin;
	
	public AccountCommandExecutor(ItemBank plugin)
	{
		this.plugin = plugin;
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args)
	{	
		if (!(sender instanceof EntityPlayer))
		{
			sender.addChatMessage(Messages.PLAYER_CMD);
			return;
		}
		
		EntityPlayer player = (EntityPlayer) sender;
		GameProfile owner = player.getGameProfile();
		int page = 1;
		World world = player.worldObj;
		if (args.length > 0)
		{
			for (String arg : args)
			{
				if (arg.contains(":"))
				{
					String key = arg.split(":")[0];
					String value = arg.split(":")[1];
					if (key.equalsIgnoreCase("page") && IBUtils.isPlayerOpped(player.getGameProfile()))
					{
						if (IBUtils.isNumber(value))
							page = Integer.valueOf(value);
						
						if (page == 0)
							page = 1;
					}
					else if (key.equalsIgnoreCase("player") && IBUtils.isPlayerOpped(player.getGameProfile()))
					{
						owner = new GameProfile(EntityPlayer.getOfflineUUID(value), value);
						
						if (owner.isComplete())
							owner = player.getGameProfile();
					}
					else if (key.equalsIgnoreCase("world") && IBUtils.isPlayerOpped(player.getGameProfile()))
					{
						world = DimensionManager.getWorld(0);
						if (world == null)
							world = player.worldObj;
					}
				}
			}
		}
		
		try
		{
			new BankInventory(player, owner, world.provider.getDimensionId(), page).openInventory(player);
		}
		catch (IOException e)
		{
			player.addChatMessage(Messages.PREFIX.appendSibling(IBUtils.getChatComponent("An error occurred while attempting to open the inventory.")));
		}
	}
}

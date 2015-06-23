package musician101.itembank.forge.command.account;

import java.io.IOException;

import musician101.itembank.forge.ItemBank;
import musician101.itembank.forge.command.AbstractForgeCommand;
import musician101.itembank.forge.config.ConfigHandler;
import musician101.itembank.forge.inventory.BankInventory;
import musician101.itembank.forge.lib.Messages;
import musician101.itembank.forge.util.IBUtils;
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
		//TODO rewrite perms
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
					if (key.equalsIgnoreCase("player") && ItemBank.permissions.hasPermission(player, "itembank.player", "itembank.account.admin"))
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
						
						if (!ItemBank.permissions.hasPermission(player, "itembank.page.all", "itembank.account.admin") && page > ConfigHandler.pageLimit)
						{
							player.addChatMessage(IBUtils.getTranslatedChatComponent((Messages.ACCOUNT_ILLEGAL_PAGE)));
							return;
						}
					}
					
					if (key.equalsIgnoreCase("world") && ItemBank.permissions.hasPermission(player, "itembank.world.all", "itembank.world." + Integer.valueOf(value), "itembank.account.admin"))
					{
						world = DimensionManager.getWorld(parseInt(value));
						if (world == null)
						{
							player.addChatMessage(IBUtils.getTranslatedChatComponent((Messages.WORLD_DNE)));
							return;
						}
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
			player.addChatMessage(IBUtils.getTranslatedChatComponent(Messages.IO_EX));
		}
	}
}

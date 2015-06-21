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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import com.mojang.authlib.GameProfile;

public class AccountCommand extends AbstractForgeCommand
{
	public AccountCommand()
	{
		this.name = "Account";
		this.usage = "/account [player:name | page:number | world:id]";
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException
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
					if (key.equalsIgnoreCase("player") && ItemBank.permissions.hasPermission(player, "itembank.player", "itembank.account.admin"))
					{
						owner = new GameProfile(EntityPlayer.getOfflineUUID(value), value);
						if (!owner.isComplete())
						{
							player.addChatMessage(Messages.PLAYER_DNE);
							return;
						}
					}
					
					if (!IBUtils.isNumber(value))
					{
						player.addChatMessage(Messages.PREFIX.appendText("Error: A number is required for " + key));
						return;
					}
					
					if (key.equalsIgnoreCase("page"))
					{
						if (!IBUtils.isNumber(value))
						{
							player.addChatMessage(Messages.PREFIX.appendText("Error: Invalid page number!"));
							return;
						}
						
						if (page == 0)
						{
							player.addChatMessage(Messages.PREFIX.appendText("Error: Page number must be greater than 0."));
							return;
						}
						
						page = Integer.valueOf(value);
						
						if (!ItemBank.permissions.hasPermission(player, "itembank.page.all", "itembank.account.admin") && page > ConfigHandler.pageLimit)
						{
							player.addChatMessage(Messages.PREFIX.appendText("You do not have permission to use this page."));
							return;
						}
					}
					
					if (key.equalsIgnoreCase("world") && ItemBank.permissions.hasPermission(player, "itembank.world.all", "itembank.world." + Integer.valueOf(value), "itembank.account.admin"))
					{
						if (!IBUtils.isNumber(value))
						{
							player.addChatMessage(Messages.PREFIX.appendText("Error: Invalid dimension number!"));
							return;
						}
						
						world = DimensionManager.getWorld(Integer.valueOf(value));
						if (world == null)
						{
							player.addChatMessage(Messages.PREFIX.appendText("Error: That world does not exist."));
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
			player.addChatMessage(Messages.PREFIX.appendSibling(IBUtils.getChatComponent("An error occurred while attempting to open the inventory.")));
		}
	}
}

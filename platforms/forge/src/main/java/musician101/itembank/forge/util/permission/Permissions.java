package musician101.itembank.forge.util.permission;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import musician101.itembank.forge.ItemBank;
import musician101.itembank.forge.config.ForgeJSONConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.UserListOps;

import org.json.simple.parser.ParseException;

public class Permissions
{
	Map<UUID, PermissionHolder> permissionMap = new HashMap<UUID, PermissionHolder>();
	
	public Permissions(File file)
	{
		ForgeJSONConfig permsJson;
		try
		{
			permsJson = ForgeJSONConfig.loadForgeJSONConfig(file);
		}
		catch (IOException | ParseException e)
		{
			ItemBank.logger.warn("An error occurred when reading " + file.getName());
			return;
		}
		
		Map<String, ForgeJSONConfig> map = permsJson.getMap("permissions");
		if (map == null)
			return;
		
		for (String key : map.keySet())
		{
			UUID uuid = UUID.fromString(key);
			permissionMap.put(uuid, new PermissionHolder(uuid, isOp(uuid), map.get(key)));
		}
	}
	
	public PermissionHolder getPlayerPermissions(EntityPlayer player)
	{
		return getPlayerPermissions(player.getUniqueID());
	}
	
	public PermissionHolder getPlayerPermissions(UUID uuid)
	{
		if (!permissionMap.containsKey(uuid))
			return updatePermissions(uuid, new PermissionHolder(uuid));
		
		return permissionMap.get(uuid);
	}
	
	public PermissionHolder updatePermissions(EntityPlayer player, PermissionHolder perms)
	{
		return updatePermissions(player.getUniqueID(), perms);
	}
	
	public PermissionHolder updatePermissions(UUID uuid, PermissionHolder perms)
	{
		return permissionMap.put(uuid, perms);
	}

	public static boolean isOp(EntityPlayer player)
	{
		return isOp(player.getUniqueID());
	}
	
	public static boolean isOp(UUID uuid)
	{
		UserListOps ops = MinecraftServer.getServer().getConfigurationManager().getOppedPlayers();
		for (String name : ops.getKeys())
			if (uuid == ops.getGameProfileFromName(name).getId())
				return true;
			
		return false;
	}
	
	public void save()
	{
		
	}
}

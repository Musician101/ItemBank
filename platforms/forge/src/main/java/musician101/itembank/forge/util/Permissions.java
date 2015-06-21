package musician101.itembank.forge.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import musician101.itembank.forge.ItemBank;
import musician101.itembank.forge.config.ForgeJSONConfig;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

import org.json.simple.parser.ParseException;

public class Permissions
{
	Map<UUID, List<String>> permissionMap = new HashMap<UUID, List<String>>();
	
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
		
		Map<String, List<String>> map = permsJson.getMap("permissions");
		if (map == null)
			return;
		
		for (String key : map.keySet())
			permissionMap.put(UUID.fromString(key), map.get(key));
	}
	
	public boolean hasPermission(ICommandSender sender, String... permissions)
	{
		if (!(sender instanceof EntityPlayer))
			return true;
		
		EntityPlayer player = (EntityPlayer) sender;
		for (String name : MinecraftServer.getServer().getConfigurationManager().getOppedPlayerNames())
			if (name.equals(player.getName()))
				return true;
		
		if (!permissionMap.containsKey(player.getUniqueID()))
			return false;
		
		for (String perm : permissionMap.get(player.getUniqueID()))
			for (String permission : permissions)
				if (perm.equalsIgnoreCase(permission))
					return true;
		
		return false;
	}
	
	public void addPermissions(UUID uuid, String... permissions)
	{
		List<String> perms = permissionMap.get(uuid);
		if (perms == null)
			perms = new ArrayList<String>();
		
		for (String permission : permissions)
			if (!perms.contains(permission))
				perms.add(permission.toLowerCase());
		
		permissionMap.put(uuid, perms);
	}
	
	public void removePermissions(UUID uuid, String[] permissions)
	{
		List<String> perms = permissionMap.get(uuid);
		if (perms == null)
			return;
		
		for (String permission : permissions)
			perms.remove(permission);
		
		permissionMap.put(uuid, perms);
	}
}

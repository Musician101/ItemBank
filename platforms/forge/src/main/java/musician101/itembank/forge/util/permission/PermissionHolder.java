package musician101.itembank.forge.util.permission;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import musician101.itembank.forge.config.ConfigHandler;
import musician101.itembank.forge.config.ForgeJSONConfig;

public class PermissionHolder
{
	boolean account;
	boolean accountAdmin;
	boolean accountPageAll;
	boolean accountPlayer;
	boolean accountWorldAll;
	boolean isOp;
	boolean permission;
	boolean purge;
	int accountPageMax;
	List<Integer> accountWorldIds;
	UUID uuid;
	
	public PermissionHolder(UUID uuid)
	{
		this(uuid, false);
	}
	
	public PermissionHolder(UUID uuid, boolean isOp)
	{
		this(uuid, isOp, new ForgeJSONConfig());
	}
	
	public PermissionHolder(UUID uuid, boolean isOp, ForgeJSONConfig perms)
	{
		this.uuid = uuid;
		this.isOp = isOp;
		this.account = perms.getBoolean("account", false);
		this.accountAdmin = perms.getBoolean("account-admin", false);
		this.accountPageAll = perms.getBoolean("account-page-all", false);
		this.accountPlayer = perms.getBoolean("account-player", false);
		this.accountWorldAll = perms.getBoolean("account-world-all", false);
		this.permission = perms.getBoolean("permission", false);
		this.purge = perms.getBoolean("purge", false);
		this.accountPageMax = perms.getInt("account-max-pages", ConfigHandler.pageLimit);
		this.accountWorldIds = perms.getList("account-world-ids", Arrays.asList(0));
	}
	
	public boolean canAccessAccount()
	{
		return account || accountAdmin || isOp;
	}
	
	public void setAccountAccess(boolean account)
	{
		this.account = account;
	}
	
	public boolean canAccessWorld(int id)
	{
		return accountWorldIds.contains(id) || accountWorldAll || accountAdmin || isOp;
	}
	
	public void addWorldId(int id)
	{
		accountWorldIds.add(id);
	}
	
	public void setAccessToAllWorlds(boolean accountWorldAll)
	{
		this.accountWorldAll = accountWorldAll;
	}
	
	public boolean canAccessOtherPlayerBanks()
	{
		return accountPlayer || accountAdmin || isOp;
	}
	
	public void setAccessToOtherPlayerBanks(boolean accountPlayer)
	{
		this.accountPlayer = accountPlayer;
	}
	
	public boolean canEditPermissions()
	{
		return permission || isOp;
	}
	
	public void setEditPermissions(boolean permission)
	{
		this.permission = permission;
	}
	
	public boolean canPurgeAccounts()
	{
		return purge || isOp;
	}
	
	public void setPurgeAccess(boolean purge)
	{
		this.purge = purge;
	}
	
	public boolean canUsePage(int page)
	{
		return page <= accountPageMax || accountPageAll || accountAdmin || isOp;
	}
	
	public void setAllPageAccess(boolean accountPageAll)
	{
		this.accountPageAll = accountPageAll;
	}
	
	public void setMaxPages(int accountPageMax)
	{
		this.accountPageMax = accountPageMax;
	}
	
	public void setAccountAdmin(boolean accountAdmin)
	{
		this.accountAdmin = accountAdmin;
	}
	
	public boolean isOp()
	{
		return isOp;
	}
	
	public void setOp(boolean isOp)
	{
		this.isOp = isOp;
	}
	
	public UUID getUUID()
	{
		return uuid;
	}
	
	public ForgeJSONConfig save()
	{
		ForgeJSONConfig json = new ForgeJSONConfig();
		json.set("account", account);
		json.set("account-admin", accountAdmin);
		json.set("account-max-pages", accountPageMax);
		json.set("account-page-all", accountPageAll);
		json.set("account-player", accountPlayer);
		json.set("account-world-all", accountWorldAll);
		json.set("account-world-ids", accountWorldIds);
		json.set("permission", permission);
		json.set("purge", purge);
		return json;
	}
}

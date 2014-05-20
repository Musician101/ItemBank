package musician101.itembank;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Econ
{
	protected Economy econ = null;
	private boolean enabled = false;
	//TODO Disabled until Vault updates to 1.7.9
	protected Econ()
	{
		if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null)
				enabled = false;
		else
		{
			RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
			if (rsp == null)
				enabled = false;
			
			econ = rsp.getProvider();
			enabled = econ != null;
		}
	}
	
	protected void clearData()
	{
		econ = null;
		enabled = false;
	}
	
	public boolean isEnabled()
	{
		return enabled;
	}
	
	public double getMoney(String player)
	{
		if (!enabled)
			return 0;
		
		if (econ != null)
			return econ.getBalance(player);
		
		return 0;
	}
	
	public void giveMoney(String player, double amount)
	{
		if (!enabled || amount == 0)
			return;
		
		if (econ != null)
			econ.depositPlayer(player, amount);
	}
	
	public void takeMoney(String player, double amount)
	{
		if (!enabled || amount == 0)
			return;
		
		if (econ != null)
			econ.withdrawPlayer(player, amount);
	}
}

package musician101.sponge.itembank;

import java.io.File;

import musician101.sponge.itembank.command.account.AccountCommand;
import musician101.sponge.itembank.command.itembank.IBCommand;
import musician101.sponge.itembank.config.Config;
import musician101.sponge.itembank.lib.Reference;
import musician101.sponge.itembank.listeners.InventoryListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.state.InitializationEvent;
import org.spongepowered.api.event.state.PreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.util.event.Subscribe;

@Plugin(id="itembank", name="ItemBank", version="3.0")
public class ItemBank
{
	static Config config;
	//TODO Currently no economy to hook into
	//public Economy econ = null;
	static File dataFolder;
	static File playerData;
	static Game game;
	static Logger logger;
	static String prefix;
	
	/*private boolean setupEconomy()
	{
		if (config.enableVault == false)
			return false;
		
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null)
			return false;
		
		econ = rsp.getProvider();
		return econ != null;
	}*/
	
	@Subscribe
	public void preInit(PreInitializationEvent event)
	{
		config = new Config();
		dataFolder = event.getRecommendedConfigurationDirectory();
		dataFolder.mkdirs();
		playerData = new File(dataFolder, "playerdata");
		playerData.mkdirs();
		game = event.getGame();
		logger = LoggerFactory.getLogger(Reference.NAME);
		
		game.getEventManager().register(this, new InventoryListener());
		
		game.getCommandDispatcher().register(this, new AccountCommand());
		game.getCommandDispatcher().register(this, new IBCommand());
	}
	
	public static Config getConfig()
	{
		return config;
	}
	
	public static File getDataFolder()
	{
		return dataFolder;
	}
	
	public static File getPlayerData()
	{
		return playerData;
	}
	
	public static Game getGame()
	{
		return game;
	}
	
	public static Logger getLogger()
	{
		return logger;
	}
}

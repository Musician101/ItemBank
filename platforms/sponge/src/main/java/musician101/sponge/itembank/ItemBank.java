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
import org.spongepowered.api.event.state.PreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.config.ConfigDir;
import org.spongepowered.api.service.config.DefaultConfig;
import org.spongepowered.api.util.event.Subscribe;

@Plugin(id = "itembank", name = "ItemBank", version = "3.0")
public class ItemBank
{
	@DefaultConfig(sharedRoot = false)
	static Config config;
	@ConfigDir(sharedRoot = false)
	static File dataFolder;
	static File playerData;
	static Game game;
	static Logger logger;
	static String prefix;
	
	@Subscribe
	public void preInit(PreInitializationEvent event)
	{
		config = new Config();
		dataFolder = new File(Reference.NAME);
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

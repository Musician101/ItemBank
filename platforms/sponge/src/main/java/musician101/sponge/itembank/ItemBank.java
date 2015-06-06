package musician101.sponge.itembank;

import musician101.itembank.common.database.MySQLHandler;
import musician101.sponge.itembank.command.account.AccountCommand;
import musician101.sponge.itembank.command.itembank.IBCommand;
import musician101.sponge.itembank.config.Config;
import musician101.sponge.itembank.lib.Reference;
import musician101.sponge.itembank.listeners.InventoryListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.state.PreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;

@Plugin(id = "itembank", name = "ItemBank", version = "3.0")
public class ItemBank
{
	static Config config;
	static Game game;
	static Logger logger;
	static MySQLHandler mysql;
	static String prefix;
	//TODO AuthorData has been implemented
	@Subscribe
	public void preInit(PreInitializationEvent event)
	{
		config = new Config();
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
	
	public static Game getGame()
	{
		return game;
	}
	
	public static Logger getLogger()
	{
		return logger;
	}
	
	public static MySQLHandler getMySQLHandler()
	{
		return mysql;
	}
	
	public static void setMySQLHandler(MySQLHandler newMySQL)
	{
		mysql = newMySQL;
	}
}

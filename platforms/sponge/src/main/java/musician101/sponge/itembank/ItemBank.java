package musician101.sponge.itembank;

import musician101.itembank.common.MySQLHandler;
import musician101.itembank.common.Reference;
import musician101.itembank.common.Reference.Commands;
import musician101.sponge.itembank.command.account.AccountExecutor;
import musician101.sponge.itembank.command.itembank.IBExecutor;
import musician101.sponge.itembank.command.itembank.PurgeExecutor;
import musician101.sponge.itembank.config.Config;
import musician101.sponge.itembank.listeners.InventoryListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.args.parsing.InputTokenizers;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;

@Plugin(id = "itembank", name = "ItemBank", version = "3.0")
public class ItemBank
{
	public static Config config;
	public static Logger logger;
	public static MySQLHandler mysql;
	
	@Listener
	public void preInit(GameStartedServerEvent event)
	{
		config = new Config();
		logger = LoggerFactory.getLogger(Reference.NAME);

		//TODO refer to InventoryListener
		//game.getEventManager().register(this, new InventoryListener());
		
		Game game = Sponge.getGame();
		game.getCommandManager().register(this, new IBExecutor(), Commands.IB_CMD.replace("/", ""), "ib");
        game.getCommandManager().register(this, new AccountExecutor(), Commands.ACCOUNT_NAME.replace("/", ""));
	}
}

package musician101.sponge.itembank;

import musician101.itembank.common.MySQLHandler;
import musician101.itembank.common.Reference;
import musician101.itembank.common.Reference.Commands;
import musician101.sponge.itembank.command.account.AccountCommand;
import musician101.sponge.itembank.command.itembank.IBCommand;
import musician101.sponge.itembank.config.SpongeConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;

@Plugin(id = "itembank", name = "ItemBank", version = "3.0")
public class SpongeItemBank
{
	public static SpongeConfig config;
	public static Logger logger;
	public static MySQLHandler mysql;
	
	@Listener
	public void preInit(GameStartedServerEvent event)
	{
		config = new SpongeConfig();
		logger = LoggerFactory.getLogger(Reference.NAME);

		//TODO refer to InventoryListener
		//game.getEventManager().register(this, new InventoryListener());
		
		Game game = Sponge.getGame();
		game.getCommandManager().register(this, new IBCommand(), Commands.IB_CMD.replace("/", ""), "ib");
        game.getCommandManager().register(this, new AccountCommand(), Commands.ACCOUNT_NAME.replace("/", ""));
	}
}

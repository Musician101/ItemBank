package musician101.itembank.sponge;

import musician101.itembank.common.MySQLHandler;
import musician101.itembank.common.Reference;
import musician101.itembank.common.Reference.Commands;
import musician101.itembank.sponge.command.itembank.IBCommand;
import musician101.itembank.sponge.command.account.AccountCommand;
import musician101.itembank.sponge.config.SpongeConfig;
import musician101.itembank.sponge.inventory.SpongeAccountStorage;
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
    public static Logger logger;
    public static MySQLHandler mysql;
    public static SpongeAccountStorage accountStorage;
    public static SpongeConfig config;

    @Listener
    public void preInit(GameStartedServerEvent event)
    {
        config = new SpongeConfig();
        logger = LoggerFactory.getLogger(Reference.NAME);
        accountStorage = SpongeAccountStorage.load();
        Game game = Sponge.getGame();
        game.getCommandManager().register(this, new IBCommand(), Commands.IB_CMD.replace("/", ""), "ib");
        game.getCommandManager().register(this, new AccountCommand(), Commands.ACCOUNT_NAME.replace("/", ""));
    }
}

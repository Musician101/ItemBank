package io.musician101.itembank.sponge;

import io.musician101.common.java.minecraft.sponge.AbstractSpongePlugin;
import io.musician101.itembank.common.MySQLHandler;
import io.musician101.itembank.common.Reference;
import io.musician101.itembank.common.Reference.Commands;
import io.musician101.itembank.sponge.account.SpongeAccountStorage;
import io.musician101.itembank.sponge.command.account.AccountCommand;
import io.musician101.itembank.sponge.command.itembank.IBCommand;
import io.musician101.itembank.sponge.config.SpongeConfig;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import java.io.File;

@Plugin(id = "itembank", name = "ItemBank", version = "3.0")
public class SpongeItemBank extends AbstractSpongePlugin<SpongeConfig>
{
    private MySQLHandler mysql;
    private SpongeAccountStorage accountStorage;

    @Listener
    public void onServerStart(GameStartedServerEvent event)//NOSONAR
    {
        config = new SpongeConfig(new File("config", Reference.NAME));
        accountStorage = SpongeAccountStorage.load(new File("config", Reference.NAME));
        Game game = Sponge.getGame();
        game.getCommandManager().register(this, new IBCommand(), Reference.NAME.toLowerCase(), Commands.IB_CMD.replace("/", ""));
        game.getCommandManager().register(this, new AccountCommand(), Commands.ACCOUNT_NAME.replace("/", ""));
    }

    public MySQLHandler getMySQL()
    {
        return mysql;
    }

    public void setMySQL(MySQLHandler mysql)
    {
        this.mysql = mysql;
    }

    public SpongeAccountStorage getAccountStorage()
    {
        return accountStorage;
    }

    public static Logger getLogger()
    {
        return getPluginContainer().getLogger();
    }

    public static PluginContainer getPluginContainer()
    {
        //noinspection OptionalGetWithoutIsPresent
        return Sponge.getPluginManager().getPlugin(Reference.ID).get();
    }

    public static SpongeItemBank instance()
    {
        return (SpongeItemBank) getPluginContainer();
    }
}

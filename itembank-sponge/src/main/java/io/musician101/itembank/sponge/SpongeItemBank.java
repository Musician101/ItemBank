package io.musician101.itembank.sponge;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import io.musician101.itembank.common.Reference;
import io.musician101.itembank.common.Reference.Commands;
import io.musician101.itembank.sponge.account.SpongeAccountStorage;
import io.musician101.itembank.sponge.command.SpongeItemBankCommands;
import io.musician101.itembank.sponge.config.SpongeConfig;
import io.musician101.itembank.sponge.json.ItemStackSerializer;
import io.musician101.itembank.sponge.json.account.AccountPageSerializer;
import io.musician101.itembank.sponge.json.account.AccountSerializer;
import io.musician101.itembank.sponge.json.account.AccountSlotSerializer;
import io.musician101.itembank.sponge.json.account.AccountWorldSerializer;
import io.musician101.musicianlibrary.java.MySQLHandler;
import io.musician101.musicianlibrary.java.minecraft.sponge.plugin.AbstractSpongePlugin;
import java.io.File;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

@Plugin(id = Reference.ID, name = Reference.NAME, version = Reference.VERSION, description = Reference.DESCRIPTION, authors = {"Musician101"})
public class SpongeItemBank extends AbstractSpongePlugin<SpongeConfig> {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(AccountSerializer.TYPE, new AccountSerializer()).registerTypeAdapter(AccountPageSerializer.TYPE, new AccountPageSerializer()).registerTypeAdapter(AccountSlotSerializer.TYPE, new AccountSlotSerializer()).registerTypeAdapter(AccountWorldSerializer.TYPE, new AccountWorldSerializer()).registerTypeAdapter(ItemStack.class, new ItemStackSerializer()).create();
    private SpongeAccountStorage accountStorage;
    @Inject
    @ConfigDir(sharedRoot = true)
    private File configDir;
    private MySQLHandler mysql;
    @Inject
    private PluginContainer pluginContainer;

    public static Optional<SpongeItemBank> instance() {
        return Sponge.getPluginManager().getPlugin(Reference.ID).flatMap(PluginContainer::getInstance).filter(SpongeItemBank.class::isInstance).map(SpongeItemBank.class::cast);
    }

    public SpongeAccountStorage getAccountStorage() {
        return accountStorage;
    }

    public MySQLHandler getMySQL() {
        return mysql;
    }

    public void setMySQL(MySQLHandler mysql) {
        this.mysql = mysql;
    }

    @Nonnull
    @Override
    public PluginContainer getPluginContainer() {
        return pluginContainer;
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        config = new SpongeConfig(configDir);
        accountStorage = new SpongeAccountStorage(configDir);
        Game game = Sponge.getGame();
        game.getCommandManager().register(this, SpongeItemBankCommands.ib(), Reference.NAME.toLowerCase(), Commands.IB_CMD.replace("/", ""));
        game.getCommandManager().register(this, SpongeItemBankCommands.account(), Commands.ACCOUNT_NAME);
    }
}

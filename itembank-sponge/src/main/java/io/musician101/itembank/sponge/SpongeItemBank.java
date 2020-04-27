package io.musician101.itembank.sponge;

import com.google.inject.Inject;
import io.musician101.itembank.common.ItemBank;
import io.musician101.itembank.common.Reference;
import io.musician101.itembank.common.Reference.Commands;
import io.musician101.itembank.common.Reference.Config;
import io.musician101.itembank.common.Reference.PlayerData;
import io.musician101.itembank.common.account.storage.AccountStorage;
import io.musician101.itembank.common.account.storage.database.MongoAccountStorage;
import io.musician101.itembank.common.account.storage.database.sql.MySQLAccountStorage;
import io.musician101.itembank.common.account.storage.database.sql.SQLiteAccountStorage;
import io.musician101.itembank.common.account.storage.file.AccountFileStorage;
import io.musician101.itembank.common.account.storage.file.ConfigurateLoader;
import io.musician101.itembank.sponge.command.SpongeItemBankCommands;
import io.musician101.itembank.sponge.config.SpongeConfig;
import io.musician101.musicianlibrary.java.minecraft.sponge.plugin.AbstractSpongePlugin;
import java.io.File;
import javax.annotation.Nonnull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

@Plugin(id = Reference.ID, name = Reference.NAME, version = Reference.VERSION, description = Reference.DESCRIPTION, authors = {"Musician101"})
public class SpongeItemBank extends AbstractSpongePlugin<SpongeConfig> implements ItemBank<ItemStack> {

    private AccountStorage<ItemStack> accountStorage;
    @Inject
    @ConfigDir(sharedRoot = true)
    private File configDir;
    @Inject
    private PluginContainer pluginContainer;

    public static SpongeItemBank instance() {
        return Sponge.getPluginManager().getPlugin(Reference.ID).flatMap(PluginContainer::getInstance).filter(SpongeItemBank.class::isInstance).map(SpongeItemBank.class::cast).orElseThrow(() -> new IllegalStateException("ItemBank is not enabled."));
    }

    @Nonnull
    @Override
    public AccountStorage<ItemStack> getAccountStorage() {
        return accountStorage;
    }

    @Nonnull
    @Override
    public PluginContainer getPluginContainer() {
        return pluginContainer;
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        config = new SpongeConfig(configDir);
        reload();
        Sponge.getCommandManager().register(this, SpongeItemBankCommands.ib(), Reference.NAME.toLowerCase(), Commands.IB_CMD.replace("/", ""));
        Sponge.getCommandManager().register(this, SpongeItemBankCommands.account(), Commands.ACCOUNT_NAME);
    }

    @Listener
    public void onServerStop(GameStoppingServerEvent event) {
        getAccountStorage().save().forEach(getLogger()::warn);
    }

    @Override
    public void reload() {
        config.reload();
        if (accountStorage != null) {
            getAccountStorage().save().forEach(getLogger()::warn);
        }

        switch (config.getFormat()) {
            case Config.JSON:
                accountStorage = new AccountFileStorage<>(new File(configDir, PlayerData.DIRECTORY), ConfigurateLoader.JSON, PlayerData.JSON, new SpongeItemStackParsing());
                break;
            case Config.HOCON:
                accountStorage = new AccountFileStorage<>(new File(configDir, PlayerData.DIRECTORY), ConfigurateLoader.HOCON, PlayerData.HOCON, new SpongeItemStackParsing());
                break;
            case Config.MONGO_DB:
                accountStorage = new MongoAccountStorage<>(config.getDatabaseOptions(), new SpongeItemStackParsing());
                break;
            case Config.MYSQL:
                accountStorage = new MySQLAccountStorage<>(config.getDatabaseOptions(), new SpongeItemStackParsing());
                break;
            case Config.SQLITE:
                accountStorage = new SQLiteAccountStorage<>(new SpongeItemStackParsing());
                break;
            case Config.TOML:
                accountStorage = new AccountFileStorage<>(new File(configDir, PlayerData.DIRECTORY), ConfigurateLoader.TOML, PlayerData.TOML, new SpongeItemStackParsing());
                break;
            case Config.YAML:
            default:
                accountStorage = new AccountFileStorage<>(new File(configDir, PlayerData.DIRECTORY), ConfigurateLoader.YAML, PlayerData.YAML, new SpongeItemStackParsing());
        }
        accountStorage.load().forEach(getLogger()::warn);
    }
}

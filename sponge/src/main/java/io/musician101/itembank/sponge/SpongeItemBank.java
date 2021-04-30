package io.musician101.itembank.sponge;

import com.google.inject.Inject;
import com.mongodb.client.model.Filters;
import io.leangen.geantyref.TypeToken;
import io.musician101.itembank.common.ItemBank;
import io.musician101.itembank.common.Reference;
import io.musician101.itembank.common.Reference.AccountData;
import io.musician101.itembank.common.Reference.Commands;
import io.musician101.itembank.common.Reference.Config;
import io.musician101.itembank.common.account.Account;
import io.musician101.itembank.common.account.Account.Serializer;
import io.musician101.itembank.common.account.AccountWorld;
import io.musician101.itembank.common.account.storage.AccountFileStorage;
import io.musician101.itembank.sponge.command.SpongeItemBankCommands;
import io.musician101.itembank.sponge.config.SpongeConfig;
import io.musician101.musicianlibrary.java.configurate.ConfigurateLoader;
import io.musician101.musicianlibrary.java.minecraft.common.Location;
import io.musician101.musicianlibrary.java.storage.DataStorage;
import io.musician101.musicianlibrary.java.storage.database.mongo.MongoDataStorage;
import io.musician101.musicianlibrary.java.storage.database.sql.MySQLDataStorage;
import io.musician101.musicianlibrary.java.storage.database.sql.SQLiteDataStorage;
import java.io.File;
import java.io.IOException;
import javax.annotation.Nonnull;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.api.event.lifecycle.StartingEngineEvent;
import org.spongepowered.api.event.lifecycle.StoppingEngineEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.reference.ConfigurationReference;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.jvm.Plugin;

@Plugin(Reference.ID)
public class SpongeItemBank implements ItemBank<ItemStack> {

    @Nonnull
    private final SpongeConfig config;
    @Nonnull
    private final PluginContainer pluginContainer;
    private DataStorage<?, Account<ItemStack>> accountStorage;

    @Inject
    public SpongeItemBank(@Nonnull PluginContainer pluginContainer, @DefaultConfig(sharedRoot = false) ConfigurationReference<ConfigurationNode> configReference) {
        this.config = new SpongeConfig(configReference);
        this.pluginContainer = pluginContainer;
    }

    public static SpongeItemBank instance() {
        return Sponge.pluginManager().plugin(Reference.ID).map(PluginContainer::getInstance).filter(SpongeItemBank.class::isInstance).map(SpongeItemBank.class::cast).orElseThrow(() -> new IllegalStateException("ItemBank is not enabled."));
    }

    @Nonnull
    @Override
    public DataStorage<?, Account<ItemStack>> getAccountStorage() {
        return accountStorage;
    }

    @Nonnull
    public SpongeConfig getConfig() {
        return config;
    }

    @Nonnull
    public Logger getLogger() {
        return pluginContainer.getLogger();
    }

    @Nonnull
    public PluginContainer getPluginContainer() {
        return pluginContainer;
    }

    @Listener
    public void onServerStart(StartingEngineEvent<Server> event) {
        try {
            reload();
        }
        catch (IOException e) {
            getLogger().warn("An error occurred while loading the config and/or data.", e);
        }
    }

    @Listener
    public void onServerStop(StoppingEngineEvent<Server> event) {
        getAccountStorage().save().forEach(getLogger()::warn);
    }

    @Listener
    public void registerCommands(RegisterCommandEvent<Command> event) {
        event.register(pluginContainer, SpongeItemBankCommands.ib(), Reference.NAME.toLowerCase(), Commands.IB_CMD.replace("/", ""));
        event.register(pluginContainer, SpongeItemBankCommands.account(), Commands.ACCOUNT_NAME);
    }

    @Override
    public void reload() throws IOException {
        config.reload();
        if (accountStorage != null) {
            getAccountStorage().save().forEach(getLogger()::warn);
        }

        TypeToken<Account<ItemStack>> accountToken = new TypeToken<Account<ItemStack>>() {

        };
        SpongeItemStackSerializer itemStackSerializer = new SpongeItemStackSerializer();
        Serializer<ItemStack> accountSerializer = new Account.Serializer<>(new TypeToken<AccountWorld<ItemStack>>() {

        }, ItemStack.class, itemStackSerializer);
        TypeSerializerCollection tsc = TypeSerializerCollection.builder().register(accountToken, accountSerializer).register(new TypeToken<AccountWorld<ItemStack>>() {

        }, new AccountWorld.Serializer<>(ItemStack.class, itemStackSerializer)).register(Location.class, new Location.Serializer()).register(ItemStack.class, itemStackSerializer).build();
        File storageDir = new File(Sponge.game().gameDirectory().toFile(), Reference.ID + "/" + AccountData.DIRECTORY);
        switch (config.getFormat()) {
            case Config.JSON:
                accountStorage = new AccountFileStorage<>(storageDir, ConfigurateLoader.JSON, AccountData.JSON, accountToken, tsc);
                break;
            case Config.HOCON:
                accountStorage = new AccountFileStorage<>(storageDir, ConfigurateLoader.HOCON, AccountData.HOCON, accountToken, tsc);
                break;
            case Config.MONGO_DB:
                accountStorage = new MongoDataStorage<>(config.getDatabaseOptions(), accountSerializer, account -> Filters.eq(AccountData.ID, account.getID()));
                break;
            case Config.MYSQL:
                accountStorage = new MySQLDataStorage<>(config.getDatabaseOptions(), accountSerializer);
                break;
            case Config.SQLITE:
                accountStorage = new SQLiteDataStorage<>(storageDir.getParentFile(), accountSerializer);
                break;
            case Config.YAML:
            default:
                accountStorage = new AccountFileStorage<>(storageDir, ConfigurateLoader.YAML, AccountData.YAML, accountToken, tsc);
        }

        accountStorage.load().forEach(getLogger()::warn);
    }
}

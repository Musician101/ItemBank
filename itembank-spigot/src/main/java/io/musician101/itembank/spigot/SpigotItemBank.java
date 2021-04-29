package io.musician101.itembank.spigot;

import com.mongodb.client.model.Filters;
import io.leangen.geantyref.TypeToken;
import io.musician101.itembank.common.ItemBank;
import io.musician101.itembank.common.Reference.AccountData;
import io.musician101.itembank.common.Reference.Config;
import io.musician101.itembank.common.Reference.Messages;
import io.musician101.itembank.common.account.Account;
import io.musician101.itembank.common.account.Account.Serializer;
import io.musician101.itembank.common.account.AccountWorld;
import io.musician101.itembank.common.account.storage.AccountFileStorage;
import io.musician101.itembank.spigot.command.SpigotItemBankCommands;
import io.musician101.musicianlibrary.java.configurate.ConfigurateLoader;
import io.musician101.musicianlibrary.java.minecraft.common.Location;
import io.musician101.musicianlibrary.java.storage.DataStorage;
import io.musician101.musicianlibrary.java.storage.database.mongo.MongoDataStorage;
import io.musician101.musicianlibrary.java.storage.database.sql.MySQLDataStorage;
import io.musician101.musicianlibrary.java.storage.database.sql.SQLiteDataStorage;
import java.io.File;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

public class SpigotItemBank extends JavaPlugin implements ItemBank<ItemStack> {

    private DataStorage<?, Account<ItemStack>> accountStorage;
    private SpigotConfig config;
    @Nullable
    private Economy econ;

    public static SpigotItemBank instance() {
        return JavaPlugin.getPlugin(SpigotItemBank.class);
    }

    @Nonnull
    @Override
    public DataStorage<?, Account<ItemStack>> getAccountStorage() {
        return accountStorage;
    }

    @Nullable
    public Economy getEconomy() {
        return econ;
    }

    public SpigotConfig getPluginConfig() {
        return config;
    }

    @Override
    public void onDisable() {
        getAccountStorage().save().forEach((o, e) -> getLogger().log(Level.WARNING, "Error saving data. Caused by: " + o.toString(), e));
    }

    @Override
    public void onEnable() {
        config = new SpigotConfig();
        reload();
        SpigotItemBankCommands.init();
    }

    @Override
    public void reload() {
        config.reload();
        setupEconomy();
        if (accountStorage != null) {
            getAccountStorage().save().forEach((o, e) -> getLogger().log(Level.WARNING, "Error saving data. Caused by: " + o.toString(), e));
        }

        TypeToken<Account<ItemStack>> accountToken = new TypeToken<Account<ItemStack>>() {

        };
        SpigotItemStackSerializer itemStackSerializer = new SpigotItemStackSerializer();
        Serializer<ItemStack> accountSerializer = new Account.Serializer<>(new TypeToken<AccountWorld<ItemStack>>() {

        }, ItemStack.class, itemStackSerializer);
        TypeSerializerCollection tsc = TypeSerializerCollection.builder().register(accountToken, accountSerializer).register(new TypeToken<AccountWorld<ItemStack>>() {

        }, new AccountWorld.Serializer<>(ItemStack.class, itemStackSerializer)).register(Location.class, new Location.Serializer()).register(ItemStack.class, new SpigotItemStackSerializer()).build();
        File storageDir = new File(getDataFolder(), AccountData.DIRECTORY);
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
                accountStorage = new SQLiteDataStorage<>(getDataFolder(), accountSerializer);
                break;
            case Config.YAML:
            default:
                accountStorage = new AccountFileStorage<>(storageDir, ConfigurateLoader.YAML, AccountData.YAML, accountToken, tsc);
        }

        accountStorage.load();
    }

    private void setupEconomy() {
        if (!config.useEconomy()) {
            return;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            getLogger().warning(Messages.ECON_LOAD_FAIL_NO_SERVICE);
            return;
        }

        econ = rsp.getProvider();
        getLogger().info(Messages.ECON_LOAD_SUCCESS);
    }
}

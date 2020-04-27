package io.musician101.itembank.spigot;

import io.musician101.itembank.common.ItemBank;
import io.musician101.itembank.common.Reference.Config;
import io.musician101.itembank.common.Reference.Messages;
import io.musician101.itembank.common.Reference.PlayerData;
import io.musician101.itembank.common.account.storage.AccountStorage;
import io.musician101.itembank.common.account.storage.database.MongoAccountStorage;
import io.musician101.itembank.common.account.storage.database.sql.MySQLAccountStorage;
import io.musician101.itembank.common.account.storage.database.sql.SQLiteAccountStorage;
import io.musician101.itembank.common.account.storage.file.AccountFileStorage;
import io.musician101.itembank.common.account.storage.file.ConfigurateLoader;
import io.musician101.itembank.spigot.command.SpigotItemBankCommands;
import io.musician101.musicianlibrary.java.minecraft.spigot.plugin.AbstractSpigotPlugin;
import java.io.File;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotItemBank extends AbstractSpigotPlugin<SpigotConfig> implements ItemBank<ItemStack> {

    private AccountStorage<ItemStack> accountStorage;
    @Nullable
    private Economy econ;

    public static SpigotItemBank instance() {
        return JavaPlugin.getPlugin(SpigotItemBank.class);
    }

    @Nonnull
    @Override
    public AccountStorage<ItemStack> getAccountStorage() {
        return accountStorage;
    }

    @Nullable
    public Economy getEconomy() {
        return econ;
    }

    @Override
    public void onDisable() {
        getAccountStorage().save().forEach(getLogger()::warning);
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
            getAccountStorage().save().forEach(getLogger()::warning);
        }

        switch (config.getFormat()) {
            case Config.JSON:
                accountStorage = new AccountFileStorage<>(new File(getDataFolder(), PlayerData.DIRECTORY), ConfigurateLoader.JSON, PlayerData.JSON, new SpigotItemStackParsing());
                break;
            case Config.HOCON:
                accountStorage = new AccountFileStorage<>(new File(getDataFolder(), PlayerData.DIRECTORY), ConfigurateLoader.HOCON, PlayerData.HOCON, new SpigotItemStackParsing());
                break;
            case Config.MONGO_DB:
                accountStorage = new MongoAccountStorage<>(config.getDatabaseOptions(), new SpigotItemStackParsing());
                break;
            case Config.MYSQL:
                accountStorage = new MySQLAccountStorage<>(config.getDatabaseOptions(), new SpigotItemStackParsing());
                break;
            case Config.SQLITE:
                accountStorage = new SQLiteAccountStorage<>(new SpigotItemStackParsing());
                break;
            case Config.TOML:
                accountStorage = new AccountFileStorage<>(new File(getDataFolder(), PlayerData.DIRECTORY), ConfigurateLoader.TOML, PlayerData.TOML, new SpigotItemStackParsing());
                break;
            case Config.YAML:
            default:
                accountStorage = new AccountFileStorage<>(new File(getDataFolder(), PlayerData.DIRECTORY), ConfigurateLoader.YAML, PlayerData.YAML, new SpigotItemStackParsing());
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

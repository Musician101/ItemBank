package io.musician101.itembank.spigot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.musician101.itembank.common.Reference.Messages;
import io.musician101.itembank.spigot.account.SpigotAccountStorage;
import io.musician101.itembank.spigot.config.SpigotConfig;
import io.musician101.itembank.spigot.json.ItemStackSerializer;
import io.musician101.itembank.spigot.json.account.AccountPageSerializer;
import io.musician101.itembank.spigot.json.account.AccountSerializer;
import io.musician101.itembank.spigot.json.account.AccountSlotSerializer;
import io.musician101.itembank.spigot.json.account.AccountWorldSerializer;
import io.musician101.musicianlibrary.java.MySQLHandler;
import io.musician101.musicianlibrary.java.minecraft.spigot.plugin.AbstractSpigotPlugin;
import java.sql.SQLException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotItemBank extends AbstractSpigotPlugin<SpigotConfig, SpigotItemBank> {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(AccountSerializer.TYPE, new AccountSerializer()).registerTypeAdapter(AccountPageSerializer.TYPE, new AccountPageSerializer()).registerTypeAdapter(AccountSlotSerializer.TYPE, new AccountSlotSerializer()).registerTypeAdapter(AccountWorldSerializer.TYPE, new AccountWorldSerializer()).registerTypeAdapter(ItemStack.class, new ItemStackSerializer()).create();
    private SpigotAccountStorage accountStorage;
    @Nullable
    private Economy econ;
    @Nullable
    private MySQLHandler mysql;

    public static SpigotItemBank instance() {
        return JavaPlugin.getPlugin(SpigotItemBank.class);
    }

    @Nonnull
    public SpigotAccountStorage getAccountStorage() {
        return accountStorage;
    }

    @Nullable
    public Economy getEconomy() {
        return econ;
    }

    @Nullable
    public MySQLHandler getMySQLHandler() {
        return mysql;
    }

    public void setMySQLHandler(@Nullable MySQLHandler mysql) {
        this.mysql = mysql;
    }

    @Override
    public void onDisable() {
        accountStorage.save();
        if (mysql != null && mysql.getConnection() != null) {
            try {
                mysql.closeConnection();
            }
            catch (SQLException e) {
                getLogger().warning(Messages.SQL_EX);
            }
        }
    }

    @Override
    public void onEnable() {
        config = new SpigotConfig();
        setupEconomy();
        accountStorage = new SpigotAccountStorage();
        commands.add(SpigotItemBankCommands.account());
        commands.add(SpigotItemBankCommands.ib());
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

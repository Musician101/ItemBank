package io.musician101.itembank.spigot;

import io.musician101.itembank.common.Reference.Messages;
import io.musician101.itembank.spigot.account.SpigotAccountStorage;
import io.musician101.itembank.spigot.config.SpigotConfig;
import io.musician101.musicianlibrary.java.MySQLHandler;
import io.musician101.musicianlibrary.java.minecraft.spigot.plugin.AbstractSpigotPlugin;
import java.sql.SQLException;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotItemBank extends AbstractSpigotPlugin<SpigotConfig, SpigotItemBank> {

    private SpigotAccountStorage accountStorage;
    private Economy econ;
    private MySQLHandler mysql;

    public static SpigotItemBank instance() {
        return JavaPlugin.getPlugin(SpigotItemBank.class);
    }

    public SpigotAccountStorage getAccountStorage() {
        return accountStorage;
    }

    public Economy getEconomy() {
        return econ;
    }

    public MySQLHandler getMySQLHandler() {
        return mysql;
    }

    public void setMySQLHandler(MySQLHandler mysql) {
        this.mysql = mysql;
    }

    @Override
    public void onDisable() {
        if (mysql != null) {
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
        accountStorage = SpigotAccountStorage.load();
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

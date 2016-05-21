package io.musician101.itembank.spigot;

import io.musician101.common.java.minecraft.spigot.AbstractSpigotPlugin;
import io.musician101.common.java.minecraft.spigot.command.AbstractSpigotCommand;
import io.musician101.itembank.common.MySQLHandler;
import io.musician101.itembank.common.Reference.Messages;
import io.musician101.itembank.spigot.account.SpigotAccountStorage;
import io.musician101.itembank.spigot.command.account.AccountCommand;
import io.musician101.itembank.spigot.command.itembank.IBCommand;
import io.musician101.itembank.spigot.config.SpigotConfig;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.Arrays;

public class SpigotItemBank extends AbstractSpigotPlugin<SpigotConfig>
{
    private Economy econ;
    private MySQLHandler mysql;
    private SpigotAccountStorage accountStorage;

    private void setupEconomy()
    {
        if (!config.useEconomy())
            return;

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null)
        {
            getLogger().warning(Messages.ECON_LOAD_FAIL_NO_SERVICE);
            return;
        }

        econ = rsp.getProvider();
        getLogger().info(Messages.ECON_LOAD_SUCCESS);
    }

    @Override
    public void onEnable()
    {
        config = new SpigotConfig();
        versionCheck(59073);
        setupEconomy();
        accountStorage = SpigotAccountStorage.load();
        commands = Arrays.asList(new AccountCommand(), new IBCommand());
    }

    @Override
    public void onDisable()
    {
        if (mysql != null)
        {
            try
            {
                mysql.closeConnection();
            }
            catch (SQLException e)//NOSONAR
            {
                getLogger().warning(Messages.SQL_EX);
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        for (AbstractSpigotCommand cmd : commands)
            if (command.getName().equalsIgnoreCase(cmd.getName()))
                return cmd.onCommand(sender, args);

        return false;
    }

    public Economy getEconomy()
    {
        return econ;
    }

    public MySQLHandler getMySQLHandler()
    {
        return mysql;
    }

    public void setMySQLHandler(MySQLHandler mysql)
    {
        this.mysql = mysql;
    }

    public SpigotAccountStorage getAccountStorage()
    {
        return accountStorage;
    }

    public static SpigotItemBank instance()
    {
        return JavaPlugin.getPlugin(SpigotItemBank.class);
    }
}

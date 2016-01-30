package musician101.itembank.spigot;

import musician101.common.java.minecraft.spigot.command.AbstractSpigotCommand;
import musician101.itembank.common.MySQLHandler;
import musician101.itembank.common.Reference.Messages;
import musician101.itembank.spigot.command.account.AccountCommand;
import musician101.itembank.spigot.command.itembank.IBCommand;
import musician101.itembank.spigot.config.SpigotConfig;
import musician101.itembank.spigot.account.SpigotAccountStorage;
import musician101.itembank.spigot.util.Updater;
import musician101.itembank.spigot.util.Updater.UpdateResult;
import musician101.itembank.spigot.util.Updater.UpdateType;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class SpigotItemBank extends JavaPlugin
{
    private Economy econ;
    private List<AbstractSpigotCommand> commands;
    private MySQLHandler mysql;
    private SpigotAccountStorage accountStorage;
    private SpigotConfig config;

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

    private void versionCheck()
    {
        if (!config.checkForUpdate())
            getLogger().info(Messages.UPDATER_DISABLED);
        else
        {
            Updater updater = new Updater(this, 59073, this.getFile(), UpdateType.NO_DOWNLOAD, true);
            if (updater.getResult() == UpdateResult.UPDATE_AVAILABLE)
                getLogger().info(Messages.updaterNew(updater.getLatestName()));
            else if (updater.getResult() == UpdateResult.NO_UPDATE)
                getLogger().info(Messages.UPDATER_UP_TO_DATE);
            else
                getLogger().info(Messages.UPDATER_FAILED);
        }
    }

    @Override
    public void onEnable()
    {
        config = new SpigotConfig(this);
        versionCheck();
        setupEconomy();
        accountStorage = SpigotAccountStorage.load(this);
        commands = Arrays.asList(new AccountCommand(this), new IBCommand(this));
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
            catch (SQLException e)
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

    public SpigotConfig getPluginConfig()
    {
        return config;
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

    public List<AbstractSpigotCommand> getCommands()
    {
        return commands;
    }

    public SpigotAccountStorage getAccountStorage()
    {
        return accountStorage;
    }
}

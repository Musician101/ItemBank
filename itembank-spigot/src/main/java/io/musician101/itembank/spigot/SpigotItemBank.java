package io.musician101.itembank.spigot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.musician101.itembank.common.ItemBank;
import io.musician101.itembank.common.Reference;
import io.musician101.itembank.common.Reference.Messages;
import io.musician101.itembank.common.Reference.PlayerData;
import io.musician101.itembank.common.account.storage.AccountStorage;
import io.musician101.itembank.spigot.account.storage.SpigotAccountFileStorage;
import io.musician101.itembank.spigot.account.storage.SpigotAccountMySQLStorage;
import io.musician101.itembank.spigot.config.SpigotConfig;
import io.musician101.itembank.spigot.json.ItemStackSerializer;
import io.musician101.itembank.spigot.json.account.AccountPageSerializer;
import io.musician101.itembank.spigot.json.account.AccountSerializer;
import io.musician101.itembank.spigot.json.account.AccountSlotSerializer;
import io.musician101.itembank.spigot.json.account.AccountWorldSerializer;
import io.musician101.musicianlibrary.java.MySQLHandler;
import io.musician101.musicianlibrary.java.minecraft.spigot.plugin.AbstractSpigotPlugin;
import java.io.File;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotItemBank extends AbstractSpigotPlugin<SpigotConfig, SpigotItemBank> implements ItemBank<ItemStack, Logger, Player, World> {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(AccountSerializer.TYPE, new AccountSerializer()).registerTypeAdapter(AccountPageSerializer.TYPE, new AccountPageSerializer()).registerTypeAdapter(AccountSlotSerializer.TYPE, new AccountSlotSerializer()).registerTypeAdapter(AccountWorldSerializer.TYPE, new AccountWorldSerializer()).registerTypeAdapter(ItemStack.class, new ItemStackSerializer()).create();
    @Nullable
    private AccountStorage<ItemStack, Player, World> accountStorage;
    @Nullable
    private Economy econ;

    public static ItemBank<ItemStack, Logger, Player, World> instance() {
        return JavaPlugin.getPlugin(SpigotItemBank.class);
    }

    @Nullable
    @Override
    public AccountStorage<ItemStack, Player, World> getAccountStorage() {
        return accountStorage;
    }

    @Nullable
    public Economy getEconomy() {
        return econ;
    }

    @Nonnull
    @Override
    public String getId() {
        return Reference.ID;
    }

    @Override
    public void onDisable() {
        save();
    }

    @Override
    public void onEnable() {
        config = new SpigotConfig();
        reload();
        commands.add(SpigotItemBankCommands.account());
        commands.add(SpigotItemBankCommands.ib());
    }

    public void reload() {
        config.reload();
        setupEconomy();
        save();
        if (config.useMySQL()) {
            MySQLHandler mysql = config.getMySQL();
            if (mysql == null) {
                getLogger().warning(ChatColor.RED + Messages.DATABASE_UNAVAILABLE);
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }

            accountStorage = new SpigotAccountMySQLStorage(mysql, GSON);
        }
        else {
            accountStorage = new SpigotAccountFileStorage(new File(getDataFolder(), PlayerData.DIRECTORY), GSON);
        }
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

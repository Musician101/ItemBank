package io.musician101.itembank.spigot.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.musician101.bukkitier.Bukkitier;
import io.musician101.itembank.common.Reference.Commands;
import io.musician101.itembank.common.Reference.Messages;
import io.musician101.itembank.common.Reference.Permissions;
import io.musician101.itembank.common.account.Account;
import io.musician101.itembank.spigot.SpigotConfig;
import io.musician101.itembank.spigot.SpigotItemBank;
import io.musician101.itembank.spigot.gui.AccountGUI;
import io.musician101.musicianlibrary.java.storage.DataStorage;
import java.awt.Color;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SpigotItemBankCommands {

    private SpigotItemBankCommands() {

    }

    @SuppressWarnings("unchecked")
    private static void account() {
        Bukkitier.registerCommand(SpigotItemBank.instance(), Bukkitier.literal(Commands.ACCOUNT_NAME).requires(sender -> sender instanceof Player).executes(context -> {
            Player player = (Player) context.getSource();
            if (!player.hasPermission(Permissions.ACCOUNT) && !player.hasPermission(Permissions.ADMIN)) {
                player.sendMessage(ChatColor.RED + Messages.NO_PERMISSION);
                return 0;
            }

            SpigotItemBank plugin = SpigotItemBank.instance();
            SpigotConfig config = plugin.getPluginConfig();
            Economy economy = plugin.getEconomy();
            if (!player.hasPermission(Permissions.ADMIN) && config.useEconomy() && economy != null) {
                double cost = config.getTransactionCost();
                EconomyResponse response = economy.withdrawPlayer(player, cost);
                if (response.type != ResponseType.SUCCESS) {
                    player.sendMessage(Messages.ACCOUNT_ECON_WITHDRAW_FAIL);
                    return 0;
                }

                player.sendMessage(Messages.accountWithdrawSuccess("$", cost));
            }

            DataStorage<?, Account<ItemStack>> storage = plugin.getAccountStorage();
            Account<ItemStack> account = storage.getEntry(a -> a.getID().equals(player.getUniqueId())).orElseGet(() -> {
                Account<ItemStack> a = new Account<>(player.getUniqueId());
                storage.addEntry(a);
                return a;
            });
            new AccountGUI(account, player);
            return 1;
        }).then(Bukkitier.argument(Commands.PLAYER, new AccountArgument()).requires(sender -> sender.hasPermission(Permissions.PLAYER)).executes(context -> {
            new AccountGUI(context.getArgument(Commands.WORLD, Account.class), (Player) context.getSource());
            return 1;
        })));
    }

    private static void ib() {
        Bukkitier.registerCommand(SpigotItemBank.instance(), Bukkitier.literal(Commands.IB_CMD).then(purge()).then(reload()));
    }

    public static void init() {
        account();
        ib();
    }

    @SuppressWarnings("unchecked")
    private static LiteralArgumentBuilder<CommandSender> purge() {
        return Bukkitier.literal(Commands.PURGE_NAME).requires(sender -> sender.hasPermission(Permissions.PURGE)).executes(context -> {
            SpigotItemBank.instance().getAccountStorage().clear();
            context.getSource().sendMessage(ChatColor.GREEN + Messages.PURGE_ALL);
            return 1;
        }).then(Bukkitier.argument(Commands.PLAYER, new AccountArgument()).executes(context -> {
            CommandSender sender = context.getSource();
            Account<ItemStack> account = context.getArgument(Commands.PLAYER, Account.class);
            if (account == null) {
                sender.sendMessage(ChatColor.RED + Messages.PLAYER_DNE);
                return 0;
            }

            account.clear();
            sender.sendMessage(Color.GREEN + Messages.PURGE_SINGLE);
            return 1;
        }));
    }

    private static LiteralArgumentBuilder<CommandSender> reload() {
        return Bukkitier.literal(Commands.RELOAD_NAME).requires(sender -> sender.hasPermission(Permissions.RELOAD)).executes(context -> {
            SpigotItemBank.instance().getPluginConfig().reload();
            context.getSource().sendMessage(ChatColor.GREEN + Messages.RELOAD_SUCCESS);
            return 1;
        });
    }
}

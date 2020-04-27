package io.musician101.itembank.spigot.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import io.musician101.itembank.common.Reference.Commands;
import io.musician101.itembank.common.Reference.Messages;
import io.musician101.itembank.common.Reference.Permissions;
import io.musician101.itembank.common.account.Account;
import io.musician101.itembank.common.account.storage.AccountStorage;
import io.musician101.itembank.spigot.SpigotConfig;
import io.musician101.itembank.spigot.SpigotItemBank;
import io.musician101.itembank.spigot.gui.AccountGUI;
import java.awt.Color;
import me.lucko.commodore.Commodore;
import me.lucko.commodore.CommodoreProvider;
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

    private static LiteralArgumentBuilder<Object> account(Commodore commodore) {
        return LiteralArgumentBuilder.literal(Commands.ACCOUNT_NAME).requires(object -> commodore.getBukkitSender(object) instanceof Player).executes(context -> {
            Player player = (Player) commodore.getBukkitSender(context.getSource());
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

            AccountStorage<ItemStack> storage = plugin.getAccountStorage();
            Account<ItemStack> account = storage.getAccount(player.getUniqueId()).orElseGet(() -> {
                Account<ItemStack> a = new Account<>(player.getUniqueId(), player.getName());
                storage.setAccount(a);
                return a;
            });
            new AccountGUI(account, player);
            return 1;
        }).then(RequiredArgumentBuilder.argument(Commands.PLAYER, new AccountArgument()).requires(object -> commodore.getBukkitSender(object).hasPermission(Permissions.PLAYER)).executes(context -> {
            //noinspection unchecked
            new AccountGUI(context.getArgument(Commands.WORLD, Account.class), (Player) commodore.getBukkitSender(context.getSource()));
            return 1;
        }));
    }

    private static LiteralArgumentBuilder<Object> ib(Commodore commodore) {
        return LiteralArgumentBuilder.literal(Commands.IB_CMD).then(purge(commodore)).then(reload(commodore));
    }

    public static void init() {
        Commodore commodore = CommodoreProvider.getCommodore(SpigotItemBank.instance());
        commodore.register(account(commodore));
        commodore.register(ib(commodore));
    }

    private static LiteralArgumentBuilder<Object> purge(Commodore commodore) {
        return LiteralArgumentBuilder.literal(Commands.PURGE_NAME).requires(object -> commodore.getBukkitSender(object).hasPermission(Permissions.PURGE)).executes(context -> {
            SpigotItemBank.instance().getAccountStorage().clear();
            commodore.getBukkitSender(context.getSource()).sendMessage(ChatColor.GREEN + Messages.PURGE_ALL);
            return 1;
        }).then(RequiredArgumentBuilder.argument(Commands.PLAYER, new AccountArgument()).executes(context -> {
            CommandSender sender = commodore.getBukkitSender(context.getSource());
            //noinspection unchecked
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

    private static LiteralArgumentBuilder<Object> reload(Commodore commodore) {
        return LiteralArgumentBuilder.literal(Commands.RELOAD_NAME).requires(object -> commodore.getBukkitSender(object).hasPermission(Permissions.RELOAD)).executes(context -> {
            SpigotItemBank.instance().getPluginConfig().reload();
            commodore.getBukkitSender(context.getSource()).sendMessage(Messages.RELOAD_SUCCESS);
            return 1;
        });
    }
}

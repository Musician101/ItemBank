package io.musician101.itembank.spigot;

import io.musician101.itembank.common.Reference;
import io.musician101.itembank.common.Reference.Commands;
import io.musician101.itembank.common.Reference.Messages;
import io.musician101.itembank.common.Reference.Permissions;
import io.musician101.itembank.common.account.storage.AccountStorage;
import io.musician101.itembank.spigot.config.SpigotConfig;
import io.musician101.musicianlibrary.java.minecraft.spigot.command.SpigotCommand;
import io.musician101.musicianlibrary.java.minecraft.spigot.command.SpigotCommandArgument;
import io.musician101.musicianlibrary.java.minecraft.spigot.command.SpigotCommandArgument.Syntax;
import io.musician101.musicianlibrary.java.minecraft.spigot.command.SpigotCommandPermissions;
import io.musician101.musicianlibrary.java.minecraft.spigot.command.SpigotCommandUsage;
import io.musician101.musicianlibrary.java.minecraft.uuid.UUIDUtils;
import io.musician101.musicianlibrary.java.util.Utils;
import java.io.IOException;
import java.util.UUID;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SpigotItemBankCommands {

    private SpigotItemBankCommands() {

    }

    public static SpigotCommand<SpigotItemBank> account() {
        return SpigotCommand.<SpigotItemBank>builder().name(Commands.ACCOUNT_NAME).description(Commands.ACCOUNT_DESC).usage(SpigotCommandUsage.of(SpigotCommandArgument.of("/" + Commands.ACCOUNT_NAME), SpigotCommandArgument.of(Commands.PAGE, Syntax.OPTIONAL), SpigotCommandArgument.of(Commands.WORLD, Syntax.OPTIONAL), SpigotCommandArgument.of(Commands.PLAYER, Syntax.OPTIONAL))).permissions(SpigotCommandPermissions.builder().permissionNode("").noPermissionMessage("").isPlayerOnly(true).playerOnlyMessage(ChatColor.RED + Messages.PLAYER_CMD).build()).function((sender, args) -> {
            Player player = (Player) sender;
            if (!player.hasPermission(Permissions.ACCOUNT) && !player.hasPermission(Permissions.ADMIN)) {
                player.sendMessage(ChatColor.RED + Messages.NO_PERMISSION);
                return false;
            }

            int page = 1;
            String name = args.get(0);
            UUID uuid = player.getUniqueId();
            World world = player.getWorld();
            if (args.size() > 0) {
                if (Utils.isInteger(name)) {
                    page = Integer.parseInt(args.get(0));
                }

                if (page <= 0) {
                    page = 1;
                }

                if (args.size() > 1) {
                    try {
                        uuid = UUIDUtils.getUUIDOf(args.get(1));
                    }
                    catch (IOException e) {
                        player.sendMessage(Messages.UNKNOWN_EX);
                        return false;
                    }

                    if (uuid == null) {
                        player.sendMessage(Messages.PLAYER_DNE);
                        return false;
                    }

                    if (args.size() > 2) {
                        world = Bukkit.getWorld(args.get(2));
                        if (world == null) {
                            player.sendMessage(Messages.ACCOUNT_WORLD_DNE);
                            return false;
                        }
                    }
                }
            }

            SpigotItemBank plugin = (SpigotItemBank) SpigotItemBank.instance();
            SpigotConfig config = plugin.getPluginConfig();
            Economy economy = plugin.getEconomy();
            if (player.getUniqueId().equals(uuid) && plugin.getPluginConfig().useEconomy() && economy != null) {
                EconomyResponse economyResponse = economy.withdrawPlayer(player, config.getTransactionCost());
                if (economyResponse.type != ResponseType.SUCCESS) {
                    player.sendMessage(Messages.ACCOUNT_ECON_WITHDRAW_FAIL);
                    return false;
                }
                else {
                    player.sendMessage(Messages.accountWithdrawSuccess("$", config.getTransactionCost()));
                }
            }

            if (canAccessPage(player, uuid, page, world)) {
                AccountStorage<ItemStack, Player, World> accountStorage = plugin.getAccountStorage();
                if (accountStorage == null) {
                    player.sendMessage(ChatColor.RED + Reference.PREFIX + Messages.DATABASE_UNAVAILABLE);
                    return false;
                }

                accountStorage.openInv(player, uuid, name, world, page);
                return true;
            }

            player.sendMessage(ChatColor.RED + Messages.NO_PERMISSION);
            return false;
        }).build((SpigotItemBank) SpigotItemBank.instance());
    }

    private static boolean canAccessPage(Player player, UUID owner, int page, World world) {
        if (player.hasPermission(Permissions.ADMIN)) {
            return true;
        }

        if (player.getUniqueId() != owner) {
            return player.hasPermission(Permissions.PLAYER);
        }

        SpigotConfig config = ((SpigotItemBank) SpigotItemBank.instance()).getPluginConfig();
        return config.isMultiWorldStorageEnabled() && player.getWorld() != world && (player.hasPermission(Permissions.WORLD + "." + world.getName()) || player.hasPermission(Permissions.WORLD)) || config.getPageLimit() > 0 && (player.hasPermission(Permissions.PAGE) || page < config.getPageLimit());
    }

    public static SpigotCommand<SpigotItemBank> ib() {
        return SpigotCommand.<SpigotItemBank>builder().name(Commands.IB_CMD.replace("/", "")).description(Reference.DESCRIPTION).usage(SpigotCommandUsage.of(SpigotCommandArgument.of(Commands.IB_CMD))).permissions(SpigotCommandPermissions.blank()).addCommand(purge()).addCommand(reload()).addCommand(uuid()).build((SpigotItemBank) SpigotItemBank.instance());
    }

    private static SpigotCommand<SpigotItemBank> purge() {
        return SpigotCommand.<SpigotItemBank>builder().name(Commands.PURGE_NAME).description(Commands.PURGE_DESC).usage(SpigotCommandUsage.of(SpigotCommandArgument.of(Commands.IB_CMD), SpigotCommandArgument.of(Commands.PURGE_NAME))).permissions(SpigotCommandPermissions.builder().permissionNode(Permissions.PURGE).noPermissionMessage(ChatColor.RED + Messages.NO_PERMISSION).playerOnlyMessage("").build()).function((sender, args) -> {
            AccountStorage<ItemStack, Player, World> accountStorage = SpigotItemBank.instance().getAccountStorage();
            if (args.size() > 0) {
                UUID uuid;
                try {
                    uuid = UUIDUtils.getUUIDOf(args.get(0));
                }
                catch (IOException e) {
                    sender.sendMessage(Messages.UNKNOWN_EX);
                    return false;
                }

                accountStorage.resetAccount(uuid);
                sender.sendMessage(Messages.PURGE_SINGLE);
                return true;
            }

            accountStorage.resetAll();
            sender.sendMessage(Messages.PURGE_MULTIPLE);
            return true;
        }).build((SpigotItemBank) SpigotItemBank.instance());
    }

    private static SpigotCommand<SpigotItemBank> reload() {
        return SpigotCommand.<SpigotItemBank>builder().name(Commands.RELOAD_NAME).description(Commands.RELOAD_DESC).usage(SpigotCommandUsage.of(SpigotCommandArgument.of(Commands.RELOAD_NAME), SpigotCommandArgument.of(Commands.RELOAD_NAME))).permissions(SpigotCommandPermissions.builder().permissionNode(Permissions.RELOAD).noPermissionMessage(ChatColor.RED + Messages.NO_PERMISSION).playerOnlyMessage("").build()).function((sender, args) -> {
            ((SpigotItemBank) SpigotItemBank.instance()).getPluginConfig().reload();
            sender.sendMessage(Messages.RELOAD_SUCCESS);
            return true;
        }).build((SpigotItemBank) SpigotItemBank.instance());
    }

    private static SpigotCommand<SpigotItemBank> uuid() {
        return SpigotCommand.<SpigotItemBank>builder().name(Commands.UUID_NAME).description(Commands.UUID_DESC).usage(SpigotCommandUsage.of(SpigotCommandArgument.of(Commands.IB_CMD), SpigotCommandArgument.of(Commands.UUID_NAME))).permissions(SpigotCommandPermissions.builder().permissionNode(Permissions.UUID).noPermissionMessage(ChatColor.RED + Messages.NO_PERMISSION).playerOnlyMessage("").build()).function((sender, args) -> {
            if (args.size() > 0) {
                try {
                    sender.sendMessage(Messages.uuid(args.get(0), UUIDUtils.getUUIDOf(args.get(0))));
                    return true;
                }
                catch (IOException e) {
                    sender.sendMessage(Messages.UNKNOWN_EX);
                    return false;
                }
            }

            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + Messages.PLAYER_CMD);
                return false;
            }

            Player player = (Player) sender;
            sender.sendMessage(Messages.uuid(player.getName(), player.getUniqueId()));
            return true;
        }).build((SpigotItemBank) SpigotItemBank.instance());
    }
}

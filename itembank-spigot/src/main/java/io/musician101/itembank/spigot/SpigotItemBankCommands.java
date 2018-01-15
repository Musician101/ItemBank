package io.musician101.itembank.spigot;

import io.musician101.itembank.common.Reference;
import io.musician101.itembank.common.Reference.Commands;
import io.musician101.itembank.common.Reference.Messages;
import io.musician101.itembank.common.Reference.MySQL;
import io.musician101.itembank.common.Reference.Permissions;
import io.musician101.musicianlibrary.java.minecraft.spigot.command.SpigotCommand;
import io.musician101.musicianlibrary.java.minecraft.spigot.command.SpigotCommandArgument;
import io.musician101.musicianlibrary.java.minecraft.spigot.command.SpigotCommandArgument.Syntax;
import io.musician101.musicianlibrary.java.minecraft.spigot.command.SpigotCommandPermissions;
import io.musician101.musicianlibrary.java.minecraft.spigot.command.SpigotCommandUsage;
import io.musician101.musicianlibrary.java.minecraft.uuid.UUIDUtils;
import io.musician101.musicianlibrary.java.util.Utils;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

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
            UUID uuid = player.getUniqueId();
            World world = player.getWorld();
            if (args.size() > 0) {
                if (Utils.isInteger(args.get(0))) {
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

            if (canAccessPage(player, uuid, page, world)) {
                return SpigotItemBank.instance().getAccountStorage().openInv(player, uuid, world, page);
            }

            player.sendMessage(ChatColor.RED + Messages.NO_PERMISSION);
            return false;
        }).build(SpigotItemBank.instance());
    }

    private static boolean canAccessPage(Player player, UUID owner, int page, World world) {
        if (player.hasPermission(Permissions.ADMIN)) {
            return true;
        }

        if (player.getUniqueId() != owner) {
            return player.hasPermission(Permissions.PLAYER);
        }

        return SpigotItemBank.instance().getPluginConfig().isMultiWorldStorageEnabled() && player.getWorld() != world && (player.hasPermission(Permissions.WORLD + "." + world.getName()) || player.hasPermission(Permissions.WORLD)) || SpigotItemBank.instance().getPluginConfig().getPageLimit() > 0 && (player.hasPermission(Permissions.PAGE) || page < SpigotItemBank.instance().getPluginConfig().getPageLimit());
    }

    public static SpigotCommand<SpigotItemBank> ib() {
        return SpigotCommand.<SpigotItemBank>builder().name(Commands.IB_CMD.replace("/", "")).description(Reference.DESCRIPTION).usage(SpigotCommandUsage.of(SpigotCommandArgument.of(Commands.IB_CMD))).permissions(SpigotCommandPermissions.blank()).addCommand(purge()).addCommand(reload()).addCommand(uuid()).build(SpigotItemBank.instance());
    }

    public static SpigotCommand<SpigotItemBank> purge() {
        return SpigotCommand.<SpigotItemBank>builder().name(Commands.PURGE_NAME).description(Commands.PURGE_DESC).usage(SpigotCommandUsage.of(SpigotCommandArgument.of(Commands.IB_CMD), SpigotCommandArgument.of(Commands.PURGE_NAME))).permissions(SpigotCommandPermissions.builder().permissionNode(Permissions.PURGE).noPermissionMessage(ChatColor.RED + Messages.NO_PERMISSION).playerOnlyMessage("").build()).function((sender, args) -> {
            if (args.size() > 0) {
                UUID uuid;
                try {
                    uuid = UUIDUtils.getUUIDOf(args.get(0));
                }
                catch (IOException e) {
                    sender.sendMessage(Messages.UNKNOWN_EX);
                    return false;
                }

                if (SpigotItemBank.instance().getPluginConfig().useMySQL()) {
                    try {
                        SpigotItemBank.instance().getMySQLHandler().querySQL(MySQL.deleteTable(uuid));
                    }
                    catch (ClassNotFoundException | SQLException e) {
                        sender.sendMessage(Messages.SQL_EX);
                        return false;
                    }

                    sender.sendMessage(Messages.PURGE_SINGLE);
                    return true;
                }

                File file = SpigotItemBank.instance().getAccountStorage().getFile(uuid);
                if (!file.exists()) {
                    sender.sendMessage(Messages.PURGE_NO_FILE);
                    return false;
                }

                if (!file.delete()) {
                    sender.sendMessage(Messages.fileDeleteFail(file));
                }
                else {
                    sender.sendMessage(Messages.PURGE_SINGLE);
                }

                return true;
            }

            if (SpigotItemBank.instance().getPluginConfig().useMySQL()) {
                try {
                    ResultSet rs = SpigotItemBank.instance().getMySQLHandler().getConnection().getMetaData().getTables(null, null, null, new String[]{"TABLE"});
                    while (rs.next()) {
                        sender.sendMessage(rs.getString(3));
                        if (rs.getString(3).startsWith(MySQL.TABLE_PREFIX)) {
                            SpigotItemBank.instance().getMySQLHandler().querySQL(MySQL.deleteTable(rs.getString(3)));
                        }
                    }
                }
                catch (SQLException | ClassNotFoundException e) {
                    sender.sendMessage(Messages.SQL_EX);
                    return false;
                }

                sender.sendMessage(Messages.PURGE_MULTIPLE);
                return true;
            }

            for (File file : SpigotItemBank.instance().getAccountStorage().resetAll()) {
                sender.sendMessage(Messages.fileDeleteFail(file));
            }

            sender.sendMessage(Messages.PURGE_MULTIPLE);
            return true;
        }).build(SpigotItemBank.instance());
    }

    public static SpigotCommand<SpigotItemBank> reload() {
        return SpigotCommand.<SpigotItemBank>builder().name(Commands.RELOAD_NAME).description(Commands.RELOAD_DESC).usage(SpigotCommandUsage.of(SpigotCommandArgument.of(Commands.RELOAD_NAME), SpigotCommandArgument.of(Commands.RELOAD_NAME))).permissions(SpigotCommandPermissions.builder().permissionNode(Permissions.RELOAD).noPermissionMessage(ChatColor.RED + Messages.NO_PERMISSION).playerOnlyMessage("").build()).function((sender, args) -> {
            SpigotItemBank.instance().getPluginConfig().reload();
            sender.sendMessage(Messages.RELOAD_SUCCESS);
            return true;
        }).build(SpigotItemBank.instance());
    }

    public static SpigotCommand<SpigotItemBank> uuid() {
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
        }).build(SpigotItemBank.instance());
    }
}

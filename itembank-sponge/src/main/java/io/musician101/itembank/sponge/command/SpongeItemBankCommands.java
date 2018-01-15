package io.musician101.itembank.sponge.command;

import io.musician101.itembank.common.Reference;
import io.musician101.itembank.common.Reference.Commands;
import io.musician101.itembank.common.Reference.Messages;
import io.musician101.itembank.common.Reference.MySQL;
import io.musician101.itembank.common.Reference.Permissions;
import io.musician101.itembank.sponge.SpongeItemBank;
import io.musician101.musicianlibrary.java.MySQLHandler;
import io.musician101.musicianlibrary.java.minecraft.uuid.UUIDUtils;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

public class SpongeItemBankCommands {

    private SpongeItemBankCommands() {

    }

    public static CommandSpec account() {
        return CommandSpec.builder().description(Text.of(Commands.ACCOUNT_DESC)).arguments(GenericArguments.optional(GenericArguments.integer(Text.of(Commands.PAGE))), GenericArguments.optional(new WorldCommandElement()), GenericArguments.optional(new PlayerCommandElement())).executor((source, args) -> {
            if (source instanceof Player) {
                return SpongeItemBank.instance().map(plugin -> {
                    Player player = (Player) source;
                    int page = args.<Integer>getOne(Commands.PAGE).orElse(1);
                    UUID uuid = args.<UUID>getOne(PlayerCommandElement.KEY).orElse(player.getUniqueId());
                    World world = args.<World>getOne(WorldCommandElement.KEY).orElse(player.getWorld());
                    if (canAccessPage(player, uuid, page, world)) {
                        plugin.getAccountStorage().openInv(player, uuid, world, page);
                        return CommandResult.success();
                    }

                    player.sendMessage(Text.builder(Messages.NO_PERMISSION).color(TextColors.RED).build());
                    return CommandResult.empty();
                }).orElse(CommandResult.empty());
            }

            source.sendMessage(Text.builder(Messages.PLAYER_CMD).color(TextColors.RED).build());
            return CommandResult.empty();
        }).build();
    }

    private static boolean canAccessPage(Player player, UUID owner, int page, World world) {
        return SpongeItemBank.instance().map(SpongeItemBank::getConfig).map(config -> {
            if (player.hasPermission(Permissions.ADMIN)) {
                return true;
            }

            if (player.getUniqueId() != owner) {
                return player.hasPermission(Permissions.PLAYER);
            }

            return config.isMultiWorldStorageEnabled() && player.getWorld() != world && (player.hasPermission(Permissions.WORLD + "." + world.getName()) || player.hasPermission(Permissions.WORLD)) || config.getPageLimit() > 0 && (player.hasPermission(Permissions.PAGE) || page < config.getPageLimit());
        }).orElse(false);
    }

    public static CommandSpec ib() {
        return CommandSpec.builder().description(Text.of(Reference.DESCRIPTION)).executor((source, args) -> {
            Text ends = Text.builder(Commands.HEADER_ENDS).color(TextColors.GREEN).build();
            source.sendMessage(Text.of(ends, Text.builder(Reference.NAME + " v" + Reference.VERSION).color(TextColors.WHITE).build(), ends));
            account().getHelp(source).ifPresent(source::sendMessage);
            ib().getHelp(source).ifPresent(source::sendMessage);
            return CommandResult.success();
        }).child(purge(), Commands.PURGE_NAME).child(reload(), Commands.RELOAD_NAME).child(uuid(), Commands.UUID_NAME).build();
    }

    private static CommandSpec purge() {
        return CommandSpec.builder().description(Text.of(Commands.PURGE_DESC)).arguments(GenericArguments.optional(new PlayerCommandElement())).executor((source, args) -> SpongeItemBank.instance().map(plugin -> {
            MySQLHandler mySQL = plugin.getMySQL();
            return args.<UUID>getOne(PlayerCommandElement.KEY).map(uuid -> {
                if (mySQL != null) {
                    try {
                        mySQL.querySQL(MySQL.deleteTable(uuid));
                    }
                    catch (ClassNotFoundException | SQLException e) {
                        source.sendMessage(Text.builder(Messages.SQL_EX).color(TextColors.RED).build());
                        return CommandResult.empty();
                    }

                    source.sendMessage(Text.builder(Messages.PURGE_SINGLE).color(TextColors.RED).build());
                    return CommandResult.success();
                }

                File file = plugin.getAccountStorage().getFile(uuid);
                if (!file.exists()) {
                    source.sendMessage(Text.builder(Messages.PURGE_NO_FILE).color(TextColors.RED).build());
                    return CommandResult.empty();
                }

                if (!file.delete()) {
                    source.sendMessage(Text.builder(Messages.purgeFileFail(file)).color(TextColors.RED).build());
                    return CommandResult.empty();
                }

                source.sendMessage(Text.builder(Messages.PURGE_SINGLE).color(TextColors.GREEN).build());
                return CommandResult.success();
            }).orElseGet(() -> {
                if (mySQL != null) {
                    try {
                        ResultSet rs = mySQL.getConnection().getMetaData().getTables(null, null, null, new String[]{"TABLE"});
                        while (rs.next())
                            if (rs.getString(3).startsWith("ib_")) {
                                mySQL.querySQL(MySQL.deleteTable(rs.getString(3)));
                            }
                    }
                    catch (SQLException | ClassNotFoundException e) {
                        source.sendMessage(Text.builder(Messages.SQL_EX).color(TextColors.RED).build());
                        return CommandResult.empty();
                    }
                }
                else {
                    plugin.getAccountStorage().resetAll().forEach(file -> source.sendMessage(Text.builder(Messages.purgeFileFail(file)).color(TextColors.RED).build()));
                }

                source.sendMessage(Text.builder(Messages.PURGE_MULTIPLE).color(TextColors.GREEN).build());
                return CommandResult.empty();
            });
        }).orElse(CommandResult.empty())).permission(Permissions.PURGE).build();
    }

    private static CommandSpec reload() {
        return CommandSpec.builder().description(Text.of(Commands.RELOAD_DESC)).executor((source, args) -> SpongeItemBank.instance().map(plugin -> {
            plugin.getConfig().reload();
            source.sendMessage(Text.builder(Messages.RELOAD_SUCCESS).color(TextColors.GREEN).build());
            return CommandResult.success();
        }).orElse(CommandResult.empty())).permission(Permissions.RELOAD).build();
    }

    private static CommandSpec uuid() {
        return CommandSpec.builder().description(Text.of(Commands.UUID_DESC)).arguments(GenericArguments.optional(new PlayerCommandElement())).executor((source, args) -> args.<UUID>getOne(PlayerCommandElement.KEY).map(uuid -> {
            try {
                source.sendMessage(Text.builder(Messages.uuid(UUIDUtils.getNameOf(uuid), uuid)).color(TextColors.GREEN).build());
                return CommandResult.success();
            }
            catch (IOException e) {
                source.sendMessage(Text.builder(Messages.UNKNOWN_EX).color(TextColors.RED).build());
                return CommandResult.empty();
            }
        }).orElseGet(() -> {
            if (!(source instanceof Player)) {
                source.sendMessage(Text.builder(Messages.PLAYER_CMD).color(TextColors.RED).build());
                return CommandResult.empty();
            }

            Player player = (Player) source;
            source.sendMessage(Text.builder(Messages.uuid(player.getName(), player.getUniqueId())).color(TextColors.GREEN).build());
            return CommandResult.success();
        })).permission(Permissions.UUID).build();
    }
}

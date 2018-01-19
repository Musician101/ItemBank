package io.musician101.itembank.sponge.command;

import io.musician101.itembank.common.Reference;
import io.musician101.itembank.common.Reference.Commands;
import io.musician101.itembank.common.Reference.Messages;
import io.musician101.itembank.common.Reference.Permissions;
import io.musician101.itembank.common.account.storage.AccountStorage;
import io.musician101.itembank.sponge.SpongeItemBank;
import io.musician101.itembank.sponge.command.args.PlayerCommandElement;
import io.musician101.itembank.sponge.command.args.WorldCommandElement;
import io.musician101.itembank.sponge.config.SpongeConfig;
import io.musician101.musicianlibrary.java.minecraft.uuid.UUIDUtils;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.UUID;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

public class SpongeItemBankCommands {

    private SpongeItemBankCommands() {

    }

    public static CommandSpec account() {
        return CommandSpec.builder().description(Text.of(Commands.ACCOUNT_DESC)).arguments(GenericArguments.optional(GenericArguments.integer(Text.of(Commands.PAGE))), GenericArguments.optional(new WorldCommandElement()), GenericArguments.optional(new PlayerCommandElement())).executor((source, args) -> {
            if (source instanceof Player) {
                return SpongeItemBank.instance().map(SpongeItemBank.class::cast).map(plugin -> {
                    Player player = (Player) source;
                    int page = args.<Integer>getOne(Commands.PAGE).orElse(1);
                    Entry<UUID, String> entry = args.<Entry<UUID, String>>getOne(PlayerCommandElement.KEY).orElse(new SimpleEntry<>(player.getUniqueId(), player.getName()));
                    World world = args.<World>getOne(WorldCommandElement.KEY).orElse(player.getWorld());
                    SpongeConfig config = plugin.getConfig();
                    Sponge.getServiceManager().provide(EconomyService.class).ifPresent(economy -> {
                        Currency currency = economy.getDefaultCurrency();
                        economy.getOrCreateAccount(player.getUniqueId()).ifPresent(account -> {
                            TransactionResult result = account.withdraw(currency, new BigDecimal(config.getTransactionCost()), Cause.of(EventContext.builder().add(EventContextKeys.PLUGIN, plugin.getPluginContainer()).add(EventContextKeys.PLAYER, player).build(), plugin, player));
                            if (result.getResult() != ResultType.SUCCESS) {
                                player.sendMessage(Text.builder(Messages.ACCOUNT_ECON_WITHDRAW_FAIL).color(TextColors.RED).build());
                            }

                            player.sendMessage(Text.builder(Messages.accountWithdrawSuccess(currency.getSymbol().toPlain(), config.getTransactionCost())).color(TextColors.RED).build());
                        });
                    });

                    if (canAccessPage(player, entry.getKey(), page, world)) {
                        AccountStorage<ItemStack, Player, World> accountStorage = plugin.getAccountStorage();
                        if (accountStorage == null) {
                            player.sendMessage(Text.builder(Messages.DATABASE_UNAVAILABLE).color(TextColors.RED).build());
                            return CommandResult.empty();
                        }

                        accountStorage.openInv(player, entry.getKey(), entry.getValue(), world, page);
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
        return SpongeItemBank.instance().map(SpongeItemBank.class::cast).map(SpongeItemBank::getConfig).map(config -> {
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
        return CommandSpec.builder().description(Text.of(Commands.PURGE_DESC)).arguments(GenericArguments.optional(new PlayerCommandElement())).executor((source, args) -> SpongeItemBank.instance().map(plugin -> args.<UUID>getOne(PlayerCommandElement.KEY).map(uuid -> {
            plugin.getAccountStorage().resetAccount(uuid);
            source.sendMessage(Text.builder(Messages.PURGE_SINGLE).color(TextColors.GREEN).build());
            return CommandResult.success();
        }).orElseGet(() -> {
            plugin.getAccountStorage().resetAll();
            source.sendMessage(Text.builder(Messages.PURGE_MULTIPLE).color(TextColors.GREEN).build());
            return CommandResult.empty();
        })).orElse(CommandResult.empty())).permission(Permissions.PURGE).build();
    }

    private static CommandSpec reload() {
        return CommandSpec.builder().description(Text.of(Commands.RELOAD_DESC)).executor((source, args) -> SpongeItemBank.instance().map(SpongeItemBank.class::cast).map(plugin -> {
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

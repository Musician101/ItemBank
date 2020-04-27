package io.musician101.itembank.sponge.command;

import io.musician101.itembank.common.Reference;
import io.musician101.itembank.common.Reference.Commands;
import io.musician101.itembank.common.Reference.Messages;
import io.musician101.itembank.common.Reference.Permissions;
import io.musician101.itembank.common.account.Account;
import io.musician101.itembank.common.account.storage.AccountStorage;
import io.musician101.itembank.sponge.SpongeItemBank;
import io.musician101.itembank.sponge.config.SpongeConfig;
import io.musician101.itembank.sponge.gui.AccountGUI;
import java.math.BigDecimal;
import java.util.Optional;
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
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class SpongeItemBankCommands {

    private SpongeItemBankCommands() {

    }

    public static CommandSpec account() {
        return CommandSpec.builder().description(Text.of(Commands.ACCOUNT_DESC)).arguments(GenericArguments.optional(new AccountCommandElement())).executor((source, args) -> {
            if (source instanceof Player) {
                Player player = (Player) source;
                if (!player.hasPermission(Permissions.ACCOUNT) && !player.hasPermission(Permissions.ADMIN)) {
                    player.sendMessage(Text.of(TextColors.RED, Messages.NO_PERMISSION));
                    return CommandResult.empty();
                }

                SpongeItemBank plugin = SpongeItemBank.instance();
                SpongeConfig config = plugin.getConfig();
                return args.<Account<ItemStack>>getOne(AccountCommandElement.KEY).map(account -> {
                    if (!player.hasPermission(Permissions.PLAYER)) {
                        player.sendMessage(Text.of(TextColors.RED, Messages.NO_PERMISSION));
                        return CommandResult.empty();
                    }

                    new AccountGUI(account, player);
                    return CommandResult.success();
                }).orElseGet(() -> {
                    Optional<EconomyService> economy = Sponge.getServiceManager().provide(EconomyService.class).filter(economyService -> !player.hasPermission(Permissions.ADMIN));
                    if (economy.isPresent()) {
                        Optional<UniqueAccount> uniqueAccount = economy.get().getOrCreateAccount(player.getUniqueId());
                        if (uniqueAccount.isPresent()) {
                            Currency currency = economy.get().getDefaultCurrency();
                            TransactionResult result = uniqueAccount.get().withdraw(currency, BigDecimal.valueOf(config.getTransactionCost()), Cause.of(EventContext.builder().add(EventContextKeys.PLUGIN, plugin.getPluginContainer()).add(EventContextKeys.PLAYER, player).build(), plugin, player));
                            if (result.getResult() != ResultType.SUCCESS) {
                                player.sendMessage(Text.of(TextColors.RED, Messages.ACCOUNT_ECON_WITHDRAW_FAIL));
                                return CommandResult.empty();
                            }

                            player.sendMessage(Text.of(TextColors.RED, Messages.accountWithdrawSuccess(currency.getSymbol().toPlain(), config.getTransactionCost())));
                        }
                    }

                    AccountStorage<ItemStack> storage = plugin.getAccountStorage();
                    Account<ItemStack> account = storage.getAccount(player.getUniqueId()).orElseGet(() -> {
                        Account<ItemStack> a = new Account<>(player.getUniqueId(), player.getName());
                        storage.setAccount(a);
                        return a;
                    });

                    new AccountGUI(account, player);
                    return CommandResult.success();
                });
            }

            source.sendMessage(Text.of(TextColors.RED, Messages.PLAYER_CMD));
            return CommandResult.empty();
        }).build();
    }

    public static CommandSpec ib() {
        return CommandSpec.builder().description(Text.of(Reference.DESCRIPTION)).executor((source, args) -> {
            Text ends = Text.builder(Commands.HEADER_ENDS).color(TextColors.GREEN).build();
            source.sendMessage(Text.of(ends, Text.builder(Reference.NAME + " v" + Reference.VERSION).color(TextColors.WHITE).build(), ends));
            account().getHelp(source).ifPresent(source::sendMessage);
            ib().getHelp(source).ifPresent(source::sendMessage);
            return CommandResult.success();
        }).child(purge(), Commands.PURGE_NAME).child(reload(), Commands.RELOAD_NAME).build();
    }

    private static CommandSpec purge() {
        return CommandSpec.builder().description(Text.of(Commands.PURGE_DESC)).arguments(GenericArguments.optional(new AccountCommandElement())).executor((source, args) -> args.<Account<ItemStack>>getOne(AccountCommandElement.KEY).map(account -> {
            account.clear();
            source.sendMessage(Text.of(TextColors.GREEN, Messages.PURGE_SINGLE));
            return CommandResult.success();
        }).orElseGet(() -> {
            SpongeItemBank.instance().getAccountStorage().clear();
            source.sendMessage(Text.of(TextColors.GREEN, Messages.PURGE_ALL));
            return CommandResult.empty();
        })).permission(Permissions.PURGE).build();
    }

    private static CommandSpec reload() {
        return CommandSpec.builder().description(Text.of(Commands.RELOAD_DESC)).executor((source, args) -> {
            SpongeItemBank.instance().getConfig().reload();
            source.sendMessage(Text.of(TextColors.GREEN, Messages.RELOAD_SUCCESS));
            return CommandResult.success();
        }).permission(Permissions.RELOAD).build();
    }
}

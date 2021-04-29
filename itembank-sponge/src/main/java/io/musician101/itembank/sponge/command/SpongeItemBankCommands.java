package io.musician101.itembank.sponge.command;

import io.leangen.geantyref.TypeToken;
import io.musician101.itembank.common.Reference;
import io.musician101.itembank.common.Reference.Commands;
import io.musician101.itembank.common.Reference.Messages;
import io.musician101.itembank.common.Reference.Permissions;
import io.musician101.itembank.common.account.Account;
import io.musician101.itembank.sponge.SpongeItemBank;
import io.musician101.itembank.sponge.config.SpongeConfig;
import io.musician101.itembank.sponge.gui.AccountGUI;
import io.musician101.musicianlibrary.java.storage.DataStorage;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.Command.Parameterized;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;

public class SpongeItemBankCommands {

    private static final Parameter ACCOUNT_PARAMETER = Parameter.builder(new TypeToken<Account<ItemStack>>() {

    }).addParser(new AccountValue()).suggestions(new AccountValue()).optional().key(AccountValue.KEY).build();

    private SpongeItemBankCommands() {

    }

    public static Command account() {
        return Command.builder().shortDescription(Component.text(Commands.ACCOUNT_DESC)).addParameter(ACCOUNT_PARAMETER).executor(context -> {
            if (context.subject() instanceof ServerPlayer) {
                ServerPlayer player = (ServerPlayer) context.subject();
                if (!player.hasPermission(Permissions.ACCOUNT) && !player.hasPermission(Permissions.ADMIN)) {
                    player.sendMessage(Identity.nil(), Component.text(Messages.NO_PERMISSION, NamedTextColor.RED));
                    return CommandResult.empty();
                }

                SpongeItemBank plugin = SpongeItemBank.instance();
                SpongeConfig config = plugin.getConfig();
                return context.one(AccountValue.KEY).map(account -> {
                    if (!player.hasPermission(Permissions.PLAYER)) {
                        player.sendMessage(Identity.nil(), Component.text(Messages.NO_PERMISSION, NamedTextColor.RED));
                        return CommandResult.empty();
                    }

                    new AccountGUI(account, player);
                    return CommandResult.success();
                }).orElseGet(() -> {
                    Optional<EconomyService> economy = Sponge.serviceProvider().provide(EconomyService.class).filter(economyService -> !player.hasPermission(Permissions.ADMIN));
                    if (economy.isPresent()) {
                        Optional<UniqueAccount> uniqueAccount = economy.get().findOrCreateAccount(player.uniqueId());
                        if (uniqueAccount.isPresent()) {
                            Currency currency = economy.get().defaultCurrency();
                            TransactionResult result = uniqueAccount.get().withdraw(currency, BigDecimal.valueOf(config.getTransactionCost()));
                            if (result.result() != ResultType.SUCCESS) {
                                player.sendMessage(Identity.nil(), Component.text(Messages.ACCOUNT_ECON_WITHDRAW_FAIL, NamedTextColor.RED));
                                return CommandResult.empty();
                            }

                            player.sendMessage(Identity.nil(), Component.text(Messages.accountWithdrawSuccess(((TextComponent) currency.symbol()).content(), config.getTransactionCost()), NamedTextColor.RED));
                        }
                    }

                    DataStorage<?, Account<ItemStack>> storage = plugin.getAccountStorage();
                    Account<ItemStack> account = storage.getEntry(a -> a.getID().equals(player.uniqueId())).orElseGet(() -> {
                        Account<ItemStack> a = new Account<>(player.uniqueId());
                        storage.addEntry(a);
                        return a;
                    });

                    new AccountGUI(account, player);
                    return CommandResult.success();
                });
            }

            context.sendMessage(Identity.nil(), Component.text(Messages.PLAYER_CMD, NamedTextColor.RED));
            return CommandResult.empty();
        }).build();
    }

    public static Command ib() {
        return Command.builder().shortDescription(Component.text(Reference.DESCRIPTION)).executor(context -> {
            Component ends = Component.text(Commands.HEADER_ENDS, NamedTextColor.GREEN);
            context.sendMessage(Identity.nil(), Component.join(Component.text(" "), ends, Component.text(Reference.NAME + " v" + Reference.VERSION, NamedTextColor.WHITE), ends));
            account().help(context.cause()).ifPresent(m -> context.sendMessage(Identity.nil(), m));
            ib().help(context.cause()).ifPresent(m -> context.sendMessage(Identity.nil(), m));
            return CommandResult.success();
        }).addChild(purge(), Commands.PURGE_NAME).addChild(reload(), Commands.RELOAD_NAME).build();
    }

    private static Parameterized purge() {
        return Command.builder().shortDescription(Component.text(Commands.PURGE_DESC)).addParameter(ACCOUNT_PARAMETER).executor(context -> context.one(AccountValue.KEY).map(account -> {
            account.clear();
            context.sendMessage(Identity.nil(), Component.text(Messages.PURGE_SINGLE, NamedTextColor.GREEN));
            return CommandResult.success();
        }).orElseGet(() -> {
            SpongeItemBank.instance().getAccountStorage().clear();
            context.sendMessage(Identity.nil(), Component.text(Messages.PURGE_ALL, NamedTextColor.GREEN));
            return CommandResult.empty();
        })).permission(Permissions.PURGE).build();
    }

    private static Parameterized reload() {
        return Command.builder().shortDescription(Component.text(Commands.RELOAD_DESC)).executor(context -> {
            try {
                SpongeItemBank.instance().reload();
                context.sendMessage(Identity.nil(), Component.text(Messages.RELOAD_SUCCESS, NamedTextColor.GREEN));
                return CommandResult.success();
            }
            catch (IOException e) {
                context.sendMessage(Identity.nil(), Component.text("An error occurred while attempting to reload the config.", NamedTextColor.RED));
                return CommandResult.empty();
            }
        }).permission(Permissions.RELOAD).build();
    }
}

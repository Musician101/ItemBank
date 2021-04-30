package io.musician101.itembank.spigot.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.musician101.itembank.common.account.Account;
import io.musician101.itembank.spigot.SpigotItemBank;
import io.musician101.musicianlibrary.java.storage.DataStorage;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

public class AccountArgument implements ArgumentType<Optional<Account<ItemStack>>> {

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        Stream.of(Bukkit.getOfflinePlayers()).map(OfflinePlayer::getName).filter(Objects::nonNull).filter(s -> s.startsWith(builder.getInput())).forEach(builder::suggest);
        return builder.buildFuture();
    }

    @SuppressWarnings("deprecation")
    @Override
    public Optional<Account<ItemStack>> parse(StringReader stringReader) throws CommandSyntaxException {
        String s = stringReader.readString();
        DataStorage<?, Account<ItemStack>> accounts = SpigotItemBank.instance().getAccountStorage();
        try {
            return accounts.getEntry(a -> a.getID().equals(UUID.fromString(s)));
        }
        catch (IllegalArgumentException e) {
            return accounts.getEntry(a -> a.getID().equals(Bukkit.getOfflinePlayer(s).getUniqueId()));
        }
    }
}

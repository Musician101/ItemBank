package io.musician101.itembank.sponge.command;

import io.leangen.geantyref.TypeToken;
import io.musician101.itembank.common.Reference.Commands;
import io.musician101.itembank.common.account.Account;
import io.musician101.itembank.sponge.SpongeItemBank;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.ArgumentParseException;
import org.spongepowered.api.command.parameter.ArgumentReader.Mutable;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.CommandContext.Builder;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.Parameter.Key;
import org.spongepowered.api.command.parameter.managed.ValueCompleter;
import org.spongepowered.api.command.parameter.managed.ValueParser;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.profile.GameProfile;

public class AccountValue implements ValueCompleter, ValueParser<Account<ItemStack>> {

    public static final Key<Account<ItemStack>> KEY = Parameter.key(Commands.PLAYER, new TypeToken<Account<ItemStack>>() {

    });

    @Override
    public List<String> complete(CommandContext context, String currentInput) {
        return Sponge.server().gameProfileManager().cache().allMatches(currentInput).stream().map(GameProfile::name).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
    }

    @Override
    public Optional<? extends Account<ItemStack>> parseValue(Key<? super Account<ItemStack>> parameterKey, Mutable reader, Builder context) throws ArgumentParseException {
        String name = reader.parseString();
        return Sponge.server().gameProfileManager().cache().findByName(name).map(gp -> SpongeItemBank.instance().getAccountStorage().getEntry(a -> a.getID().equals(gp.uuid())).orElse(new Account<>(gp.uuid())));
    }
}

package io.musician101.itembank.sponge.command;

import io.musician101.itembank.common.Reference.Commands;
import io.musician101.itembank.common.Reference.Messages;
import io.musician101.itembank.common.account.Account;
import io.musician101.itembank.sponge.SpongeItemBank;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class AccountCommandElement extends CommandElement {

    public static final Text KEY = Text.of(Commands.PLAYER);

    public AccountCommandElement() {
        this(KEY);
    }

    public AccountCommandElement(@Nullable Text key) {
        super(key);
    }

    @Nonnull
    @Override
    public List<String> complete(@Nonnull CommandSource src, @Nonnull CommandArgs args, @Nonnull CommandContext context) {
        return Sponge.getServer().getGameProfileManager().getCache().getProfiles().stream().map(GameProfile::getName).filter(Optional::isPresent).map(Optional::get).filter(name -> {
            try {
                return args.hasNext() && name.startsWith(args.next());
            }
            catch (ArgumentParseException e) {
                return false;
            }
        }).collect(Collectors.toList());
    }

    @Nullable
    @Override
    protected Object parseValue(@Nonnull CommandSource source, @Nonnull CommandArgs args) throws ArgumentParseException {
        String name = args.next();
        UUID uuid;
        try {
            GameProfile gp = Sponge.getServer().getGameProfileManager().get(name).get();
            uuid = gp.getUniqueId();
            name = gp.getName().orElse(name);
        }
        catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw args.createError(Text.of(TextColors.RED, Messages.UNKNOWN_EX));
        }

        return SpongeItemBank.instance().getAccountStorage().getAccount(uuid).orElse(new Account<>(uuid, name));
    }
}

package io.musician101.itembank.sponge.command;

import io.musician101.itembank.common.Reference.Commands;
import io.musician101.itembank.common.Reference.Messages;
import io.musician101.musicianlibrary.java.minecraft.uuid.UUIDUtils;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class PlayerCommandElement extends CommandElement {

    public static Text KEY = Text.of(Commands.PLAYER);

    public PlayerCommandElement() {
        this(KEY);
    }

    public PlayerCommandElement(@Nullable Text key) {
        super(key);
    }

    @Nonnull
    @Override
    public List<String> complete(@Nonnull CommandSource src, @Nonnull CommandArgs args, @Nonnull CommandContext context) {
        Stream<String> nameStream = Sponge.getServer().getOnlinePlayers().stream().map(Player::getName);
        if (args.hasNext()) {
            nameStream = nameStream.filter(name -> {
                try {
                    return name.startsWith(args.next());
                }
                catch (ArgumentParseException e) {
                    return false;
                }
            });
        }

        return nameStream.collect(Collectors.toList());
    }

    @Nullable
    @Override
    protected Object parseValue(@Nonnull CommandSource source, @Nonnull CommandArgs args) throws ArgumentParseException {
        UUID uuid;
        try {
            uuid = UUIDUtils.getUUIDOf(args.next());
        }
        catch (IOException e) {
            throw args.createError(Text.builder(Messages.UNKNOWN_EX).color(TextColors.RED).build());
        }

        if (uuid == null) {
            throw args.createError(Text.builder(Messages.PLAYER_DNE).color(TextColors.RED).build());
        }

        return uuid;
    }
}

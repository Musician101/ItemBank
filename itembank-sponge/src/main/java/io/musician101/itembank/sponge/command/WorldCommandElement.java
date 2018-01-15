package io.musician101.itembank.sponge.command;

import io.musician101.itembank.common.Reference.Commands;
import io.musician101.itembank.common.Reference.Messages;
import java.util.List;
import java.util.Optional;
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
import org.spongepowered.api.world.World;

public class WorldCommandElement extends CommandElement {

    public static Text KEY = Text.of(Commands.WORLD);

    public WorldCommandElement() {
        this(KEY);
    }

    public WorldCommandElement(@Nullable Text key) {
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
        Optional<World> optional = Sponge.getServer().getWorld(args.next());
        if (!optional.isPresent()) {
            throw args.createError(Text.builder(Messages.ACCOUNT_WORLD_DNE).color(TextColors.RED).build());
        }

        return optional.get();
    }
}

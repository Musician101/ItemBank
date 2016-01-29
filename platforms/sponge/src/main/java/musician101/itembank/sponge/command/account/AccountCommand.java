package musician101.itembank.sponge.command.account;

import musician101.common.java.minecraft.command.AbstractCommandArgument.Syntax;
import musician101.common.java.minecraft.sponge.TextUtils;
import musician101.common.java.minecraft.sponge.command.AbstractSpongeCommand;
import musician101.common.java.minecraft.sponge.command.SpongeCommandArgument;
import musician101.common.java.minecraft.sponge.command.SpongeHelpCommand;
import musician101.common.java.minecraft.uuid.UUIDUtils;
import musician101.common.java.util.Utils;
import musician101.itembank.common.Reference;
import musician101.itembank.common.Reference.Commands;
import musician101.itembank.common.Reference.Messages;
import musician101.itembank.common.Reference.Permissions;
import musician101.itembank.sponge.SpongeItemBank;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public class AccountCommand extends AbstractSpongeCommand
{
    public AccountCommand()
    {
        super(Commands.ACCOUNT_NAME, Commands.ACCOUNT_DESC, Arrays.asList(new SpongeCommandArgument("/" + Commands.ACCOUNT_NAME), new SpongeCommandArgument(Commands.getAccountArg(Commands.PAGE), Syntax.OPTIONAL), new SpongeCommandArgument(Commands.getAccountArg(Commands.PLAYER), Syntax.OPTIONAL), new SpongeCommandArgument(Commands.getAccountArg(Commands.WORLD), Syntax.OPTIONAL)), 0, "", true, TextUtils.redText(Messages.NO_PERMISSION), TextUtils.redText(Messages.PLAYER_CMD));
    }

    @Nonnull
    @Override
    public CommandResult process(@Nonnull CommandSource source, @Nonnull String arguments) throws CommandException
    {
        if (!testPermission(source))
            return CommandResult.empty();

        Player player = (Player) source;
        String[] args = (arguments.length() == 0 ? new String[]{} : splitArgs(arguments));
        if (args.length > 1 && args[0].equalsIgnoreCase(Commands.HELP))
        {
            Text ends = Text.builder(Commands.HEADER_ENDS).color(TextColors.DARK_GREEN).build();
            Text middle = Text.builder(Reference.NAME + " " + Reference.VERSION).color(TextColors.WHITE).build();
            player.sendMessage(Text.builder().append(ends, middle, ends).build());
            return new SpongeHelpCommand(this, source).process(source, arguments);
        }

        int page = 1;
        World world = player.getWorld();
        UUID uuid = player.getUniqueId();
        for (String arg : args)
        {
            if (arg.contains(":"))
            {
                String[] argSplit = arg.split(":");
                if (argSplit[0].equalsIgnoreCase(Commands.PAGE))
                {
                    if (Utils.isInteger(argSplit[1]))
                        page = Integer.parseInt(argSplit[1]);

                    if (page <= 0)
                        page = 1;
                }

                if (argSplit[0].equalsIgnoreCase(Commands.PLAYER))
                {
                    try
                    {
                        uuid = UUIDUtils.getUUIDOf(argSplit[1]);
                    }
                    catch (Exception e)
                    {
                        player.sendMessage(TextUtils.redText(Messages.UNKNOWN_EX));
                        return CommandResult.empty();
                    }

                    if (uuid == null)
                    {
                        player.sendMessage(TextUtils.redText(Messages.PLAYER_DNE));
                        return CommandResult.empty();
                    }
                }

                if (argSplit[0].equalsIgnoreCase(Commands.WORLD))
                {
                    Optional<World> wo = Sponge.getServer().getWorld(argSplit[1]);
                    if (!wo.isPresent())
                    {
                        player.sendMessage(TextUtils.redText(Messages.ACCOUNT_WORLD_DNE));
                        return CommandResult.empty();
                    }

                    world = wo.get();
                }
            }
        }

        if (!canAccessPage(player, uuid, page, world))
        {
            player.sendMessage(TextUtils.redText(Messages.NO_PERMISSION));
            return CommandResult.empty();
        }

        if (SpongeItemBank.accountStorage.openInv(player, uuid, world, 1))
            return CommandResult.success();

        return CommandResult.empty();
    }

    private boolean canAccessPage(Player player, UUID owner, int page, World world)
    {
        if (player.hasPermission(Permissions.ADMIN))
            return true;

        if (player.getUniqueId() != owner)
            return player.hasPermission(Permissions.PLAYER);

        if (SpongeItemBank.config.isMultiWorldStorageEnabled())
            if (player.getWorld() != world)
                if (player.hasPermission(Permissions.WORLD + "." + world.getName()) || player.hasPermission(Permissions.WORLD))
                    return true;

        if (SpongeItemBank.config.getPageLimit() > 0)
            if (player.hasPermission(Permissions.PAGE) || page < SpongeItemBank.config.getPageLimit())
                return true;

        return false;
    }
}

package io.musician101.itembank.sponge.command.account;

import io.musician101.common.java.minecraft.command.AbstractCommandArgument.Syntax;
import io.musician101.common.java.minecraft.sponge.TextUtils;
import io.musician101.common.java.minecraft.sponge.command.AbstractSpongeCommand;
import io.musician101.common.java.minecraft.sponge.command.SpongeCommandArgument;
import io.musician101.common.java.minecraft.sponge.command.SpongeCommandPermissions;
import io.musician101.common.java.minecraft.sponge.command.SpongeCommandUsage;
import io.musician101.common.java.minecraft.sponge.command.SpongeHelpCommand;
import io.musician101.common.java.minecraft.uuid.UUIDUtils;
import io.musician101.common.java.util.Utils;
import io.musician101.itembank.common.Reference.Commands;
import io.musician101.itembank.common.Reference.Messages;
import io.musician101.itembank.common.Reference.Permissions;
import io.musician101.itembank.sponge.SpongeItemBank;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public class AccountCommand extends AbstractSpongeCommand
{
    public AccountCommand()
    {
        super(Commands.ACCOUNT_NAME, Text.of(Commands.ACCOUNT_DESC), new SpongeCommandUsage(Arrays.asList(new SpongeCommandArgument("/" + Commands.ACCOUNT_NAME), new SpongeCommandArgument(Commands.getAccountArg(Commands.PAGE), Syntax.OPTIONAL), new SpongeCommandArgument(Commands.getAccountArg(Commands.PLAYER), Syntax.OPTIONAL), new SpongeCommandArgument(Commands.getAccountArg(Commands.WORLD), Syntax.OPTIONAL))), new SpongeCommandPermissions("", true, TextUtils.redText(Messages.NO_PERMISSION), TextUtils.redText(Messages.PLAYER_CMD)));
    }

    @Nonnull
    @Override
    public CommandResult process(@Nonnull CommandSource source, @Nonnull String arguments) throws CommandException//NOSONAR
    {
        if (!testPermission(source))
            return CommandResult.empty();

        Player player = (Player) source;
        String[] args = arguments.length() == 0 ? new String[]{} : splitArgs(arguments);
        if (args.length > 1 && args[0].equalsIgnoreCase(Commands.HELP))
            return new SpongeHelpCommand(this, source, SpongeItemBank.getPluginContainer()).process(source, arguments);

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
                    if (Utils.isInteger(argSplit[1]))//NOSONAR
                        page = Integer.parseInt(argSplit[1]);

                    if (page <= 0)//NOSONAR
                        page = 1;
                }

                if (argSplit[0].equalsIgnoreCase(Commands.PLAYER))
                {
                    try//NOSONAR
                    {
                        uuid = UUIDUtils.getUUIDOf(argSplit[1]);
                    }
                    catch (IOException e)//NOSONAR
                    {
                        player.sendMessage(TextUtils.redText(Messages.UNKNOWN_EX));
                        return CommandResult.empty();
                    }

                    if (uuid == null)//NOSONAR
                    {
                        player.sendMessage(TextUtils.redText(Messages.PLAYER_DNE));
                        return CommandResult.empty();
                    }
                }

                if (argSplit[0].equalsIgnoreCase(Commands.WORLD))
                {
                    Optional<World> wo = Sponge.getServer().getWorld(argSplit[1]);
                    if (!wo.isPresent())//NOSONAR
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

        if (SpongeItemBank.instance().getAccountStorage().openInv(player, uuid, world, 1))
            return CommandResult.success();

        return CommandResult.empty();
    }

    private boolean canAccessPage(Player player, UUID owner, int page, World world)//NOSONAR
    {
        if (player.hasPermission(Permissions.ADMIN))
            return true;

        if (player.getUniqueId() != owner)
            return player.hasPermission(Permissions.PLAYER);

        return SpongeItemBank.instance().getConfig().isMultiWorldStorageEnabled() && player.getWorld() != world && (player.hasPermission(Permissions.WORLD + "." + world.getName()) || player.hasPermission(Permissions.WORLD)) || SpongeItemBank.instance().getConfig().getPageLimit() > 0 && (player.hasPermission(Permissions.PAGE) || page < SpongeItemBank.instance().getConfig().getPageLimit());//NOSONAR

    }
}

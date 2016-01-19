package musician101.sponge.itembank.command.account;

import musician101.common.java.minecraft.command.AbstractCommandArgument.Syntax;
import musician101.common.java.minecraft.sponge.TextUtils;
import musician101.common.java.minecraft.sponge.command.AbstractSpongeCommand;
import musician101.common.java.minecraft.sponge.command.SpongeCommandArgument;
import musician101.itembank.common.Reference.Commands;
import musician101.itembank.common.Reference.Messages;
import musician101.itembank.common.Reference.Permissions;
import musician101.itembank.common.UUIDUtils;
import musician101.sponge.itembank.SpongeItemBank;
import musician101.sponge.itembank.util.AccountUtil;
import musician101.sponge.itembank.util.IBUtils;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.json.simple.parser.ParseException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public class AccountCommand extends AbstractSpongeCommand
{
    public AccountCommand()
    {
        super(Commands.ACCOUNT_NAME, Commands.ACCOUNT_DESC, Arrays.asList(new SpongeCommandArgument("/" + Commands.ACCOUNT_NAME), new SpongeCommandArgument(Commands.PAGE_ACCOUNT, Syntax.OPTIONAL), new SpongeCommandArgument(Commands.PLAYER_ACCOUNT, Syntax.OPTIONAL), new SpongeCommandArgument(Commands.WORLD_ACCOUNT)), 0, "", true, TextUtils.redText(Messages.NO_PERMISSION), TextUtils.redText(Messages.PLAYER_CMD));
    }

    private void openInv(Player player, World world, UUID uuid, int page)
    {
        Inventory inv;
        try
        {
            inv = AccountUtil.getAccount(world, uuid, page);
        }
        catch (ClassNotFoundException | SQLException e)
        {
            player.sendMessage(TextUtils.redText(Messages.SQL_EX));
            return;
        }
        catch (FileNotFoundException e)
        {
            player.sendMessage(TextUtils.redText(Messages.NO_FILE_EX));
            return;
        }
        catch (IOException e)
        {
            player.sendMessage(TextUtils.redText(Messages.IO_EX));
            return;
        }
        catch (ObjectMappingException | ParseException e)
        {
            player.sendMessage(TextUtils.redText(Messages.PARSE_EX));
            return;
        }

        player.openInventory(inv);
    }

    @Nonnull
    @Override
    public CommandResult process(@Nonnull CommandSource source, @Nonnull String arguments)
    {
        if (!testPermission(source))
            return CommandResult.empty();

        Player player = (Player) source;
        String[] args = (arguments.length() == 0 ? new String[]{} : splitArgs(arguments));
        int page = 1;
        World world = player.getWorld();
        UUID uuid = player.getUniqueId();
        for (String arg : args)
        {
            if (arg.startsWith("page:"))
            {
                String number = arg.split(":")[1];
                if (IBUtils.isNumber(number))
                    page = Integer.parseInt(number);

                if (page <= 0)
                    page = 1;
            }

            if (arg.startsWith("player:"))
            {
                String playerString = arg.split(":")[1];
                try
                {
                    uuid = UUIDUtils.getUUIDOf(playerString);
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

            if (arg.startsWith("world:"))
            {
                String worldString = arg.split(":")[1];
                Optional<World> wo = Sponge.getServer().getWorld(worldString);
                if (!wo.isPresent())
                {
                    player.sendMessage(TextUtils.redText(Messages.ACCOUNT_WORLD_DNE));
                    return CommandResult.empty();
                }

                world = wo.get();
            }
        }

        if (!canAccessPage(player, uuid, page, world))
        {
            player.sendMessage(TextUtils.redText(Messages.NO_PERMISSION));
            return CommandResult.empty();
        }

        openInv(player, world, uuid, 1);
        return CommandResult.success();
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

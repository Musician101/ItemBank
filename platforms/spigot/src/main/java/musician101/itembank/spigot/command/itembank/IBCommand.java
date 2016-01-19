package musician101.itembank.spigot.command.itembank;

import musician101.common.java.minecraft.spigot.command.AbstractSpigotCommand;
import musician101.common.java.minecraft.spigot.command.SpigotCommandArgument;
import musician101.common.java.minecraft.spigot.command.SpigotHelpCommand;
import musician101.itembank.spigot.SpigotItemBank;
import musician101.itembank.spigot.lib.Messages;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;

public class IBCommand extends AbstractSpigotCommand
{
    private final SpigotItemBank plugin;

    public IBCommand(SpigotItemBank plugin)
    {
        super("itembank", "Virtual chest with configurable limits.", Collections.singletonList(new SpigotCommandArgument("/itembank")), 0, "", false, Messages.NO_PERMISSION, Messages.PLAYER_CMD, Arrays.asList(new PurgeCommand(plugin), new ReloadCommand(plugin), new UUIDCommand()));
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args)
    {
        if (args.length > 0)
        {
            if (args[0].equalsIgnoreCase("help"))
                return new SpigotHelpCommand(this).onCommand(sender, moveArguments(args));

            for (AbstractSpigotCommand command : getSubCommands())
                if (command.getName().equalsIgnoreCase(args[0]))
                    return command.onCommand(sender, args);
        }

        sender.sendMessage(Messages.HEADER);
        for (AbstractSpigotCommand command : plugin.getCommands())
            sender.sendMessage(new SpigotHelpCommand(command).getCommandHelpInfo());

        return true;
    }
}

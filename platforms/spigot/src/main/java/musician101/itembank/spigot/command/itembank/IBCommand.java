package musician101.itembank.spigot.command.itembank;

import musician101.common.java.minecraft.spigot.command.AbstractSpigotCommand;
import musician101.common.java.minecraft.spigot.command.SpigotCommandArgument;
import musician101.common.java.minecraft.spigot.command.SpigotHelpCommand;
import musician101.itembank.common.Reference;
import musician101.itembank.common.Reference.Commands;
import musician101.itembank.common.Reference.Messages;
import musician101.itembank.spigot.SpigotItemBank;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;

public class IBCommand extends AbstractSpigotCommand
{
    private final SpigotItemBank plugin;

    public IBCommand(SpigotItemBank plugin)
    {
        super(Reference.ID, Reference.DESCRIPTION, Collections.singletonList(new SpigotCommandArgument(Commands.IB_CMD)), 0, "", false, Messages.NO_PERMISSION, Messages.PLAYER_CMD, Arrays.asList(new PurgeCommand(plugin), new ReloadCommand(plugin), new UUIDCommand()));
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args)
    {
        if (args.length > 0)
        {
            if (args[0].equalsIgnoreCase(Commands.HELP))
            {
                String ends = ChatColor.DARK_GREEN + Commands.HEADER_ENDS;
                String middle = ChatColor.RESET + Reference.NAME + " " + Reference.VERSION;
                sender.sendMessage(ends + middle + ends);
                return new SpigotHelpCommand(this).onCommand(sender, moveArguments(args));
            }

            for (AbstractSpigotCommand command : getSubCommands())
                if (command.getName().equalsIgnoreCase(args[0]))
                    return command.onCommand(sender, args);
        }

        String ends = ChatColor.DARK_GREEN + Commands.HEADER_ENDS;
        String middle = ChatColor.RESET + Reference.NAME + " " + Reference.VERSION;
        sender.sendMessage(ends + middle + ends);
        for (AbstractSpigotCommand command : plugin.getCommands())
            sender.sendMessage(new SpigotHelpCommand(command).getCommandHelpInfo());

        return true;
    }
}

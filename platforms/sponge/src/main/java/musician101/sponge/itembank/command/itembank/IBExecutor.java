package musician101.sponge.itembank.command.itembank;

import musician101.sponge.itembank.lib.Reference.Messages;

import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;

public class IBExecutor implements CommandExecutor
{
	@Override
	public CommandResult execute(CommandSource source, CommandContext args) throws CommandException
	{
		source.sendMessage(Messages.ITEMBANK_DESC);
		return CommandResult.success();
	}
}

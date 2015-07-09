package musician101.sponge.itembank;

import musician101.itembank.common.database.MySQLHandler;
import musician101.sponge.itembank.command.account.AccountExecutor;
import musician101.sponge.itembank.command.itembank.IBExecutor;
import musician101.sponge.itembank.command.itembank.PurgeExecutor;
import musician101.sponge.itembank.config.Config;
import musician101.sponge.itembank.lib.Reference;
import musician101.sponge.itembank.lib.Reference.Messages;
import musician101.sponge.itembank.listeners.InventoryListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.state.PreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.args.GenericArguments;
import org.spongepowered.api.util.command.args.parsing.InputTokenizers;
import org.spongepowered.api.util.command.spec.CommandSpec;

@Plugin(id = "itembank", name = "ItemBank", version = "3.0")
public class ItemBank
{
	public static Config config;
	public static Game game;
	public static Logger logger;
	public static MySQLHandler mysql;
	
	@Subscribe
	public void preInit(PreInitializationEvent event)
	{
		config = new Config();
		game = event.getGame();
		logger = LoggerFactory.getLogger(Reference.NAME);
		
		game.getEventManager().register(this, new InventoryListener());
		
		game.getCommandDispatcher().register(this, CommandSpec.builder()
				.arguments(GenericArguments.optional(GenericArguments.string(Texts.of("page")),
						GenericArguments.optional(GenericArguments.string(Texts.of("player")),
						GenericArguments.optional(GenericArguments.string(Texts.of("world"))))))
				.description(Messages.ACCOUNT_DESC)
				.executor(new AccountExecutor())
				.inputTokenizer(InputTokenizers.spaceSplitString())
				.build(), "account", "a");
		
		CommandSpec purgeCmd = CommandSpec.builder()
				.arguments(GenericArguments.optional(GenericArguments.string(Texts.of("player"))))
				.description(Messages.PURGE_DESC)
				.executor(new PurgeExecutor())
				.build();
		game.getCommandDispatcher().register(this, CommandSpec.builder()
				.child(purgeCmd, "purge", "p")
				.executor(new IBExecutor())
				.build(), Reference.ID, "ib");
	}
}

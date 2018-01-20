package io.musician101.itembank.sponge;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import io.musician101.itembank.common.ItemBank;
import io.musician101.itembank.common.Reference;
import io.musician101.itembank.common.Reference.Commands;
import io.musician101.itembank.common.Reference.Messages;
import io.musician101.itembank.common.Reference.PlayerData;
import io.musician101.itembank.common.account.storage.AccountStorage;
import io.musician101.itembank.sponge.account.storage.SpongeAccountFileStorage;
import io.musician101.itembank.sponge.account.storage.SpongeAccountMySQLStorage;
import io.musician101.itembank.sponge.command.SpongeItemBankCommands;
import io.musician101.itembank.sponge.config.SpongeConfig;
import io.musician101.itembank.sponge.json.ItemStackSerializer;
import io.musician101.itembank.sponge.json.account.AccountPageSerializer;
import io.musician101.itembank.sponge.json.account.AccountSerializer;
import io.musician101.itembank.sponge.json.account.AccountSlotSerializer;
import io.musician101.itembank.sponge.json.account.AccountWorldSerializer;
import io.musician101.musicianlibrary.java.MySQLHandler;
import io.musician101.musicianlibrary.java.minecraft.sponge.plugin.AbstractSpongePlugin;
import java.io.File;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.world.World;

@Plugin(id = Reference.ID, name = Reference.NAME, version = Reference.VERSION, description = Reference.DESCRIPTION, authors = {"Musician101"})
public class SpongeItemBank extends AbstractSpongePlugin<SpongeConfig> implements ItemBank<ItemStack, Logger, Player, World> {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(AccountSerializer.TYPE, new AccountSerializer()).registerTypeAdapter(AccountPageSerializer.TYPE, new AccountPageSerializer()).registerTypeAdapter(AccountSlotSerializer.TYPE, new AccountSlotSerializer()).registerTypeAdapter(AccountWorldSerializer.TYPE, new AccountWorldSerializer()).registerTypeAdapter(ItemStack.class, new ItemStackSerializer()).create();
    @Nullable
    private AccountStorage<ItemStack, Player, World> accountStorage;
    @Inject
    @ConfigDir(sharedRoot = true)
    private File configDir;
    @Inject
    private PluginContainer pluginContainer;

    public static Optional<ItemBank<ItemStack, Logger, Player, World>> instance() {
        return Sponge.getPluginManager().getPlugin(Reference.ID).flatMap(PluginContainer::getInstance).filter(SpongeItemBank.class::isInstance).map(SpongeItemBank.class::cast);
    }

    @Nonnull
    @Override
    public Optional<AccountStorage<ItemStack, Player, World>> getAccountStorage() {
        return Optional.ofNullable(accountStorage);
    }

    @Nonnull
    @Override
    public PluginContainer getPluginContainer() {
        return pluginContainer;
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        config = new SpongeConfig(configDir);
        reload();
        Sponge.getCommandManager().register(this, SpongeItemBankCommands.ib(), Reference.NAME.toLowerCase(), Commands.IB_CMD.replace("/", ""));
        Sponge.getCommandManager().register(this, SpongeItemBankCommands.account(), Commands.ACCOUNT_NAME);
    }

    @Listener
    public void onServerStop(GameStoppingServerEvent event) {
        save();
    }

    @Override
    public void reload() {
        config.reload();
        save();
        if (config.useMySQL()) {
            MySQLHandler mysql = config.getMySQL();
            if (mysql == null) {
                getLogger().error(Messages.DATABASE_UNAVAILABLE);
                return;
            }

            accountStorage = new SpongeAccountMySQLStorage(mysql, GSON);
        }
        else {
            accountStorage = new SpongeAccountFileStorage(new File(configDir, PlayerData.DIRECTORY), GSON);
        }
    }
}

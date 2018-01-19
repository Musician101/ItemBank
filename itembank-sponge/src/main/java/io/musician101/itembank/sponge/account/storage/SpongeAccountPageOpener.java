package io.musician101.itembank.sponge.account.storage;

import io.musician101.itembank.common.ItemBank;
import io.musician101.itembank.common.Reference;
import io.musician101.itembank.common.Reference.Messages;
import io.musician101.itembank.common.account.Account;
import io.musician101.itembank.common.account.AccountPage;
import io.musician101.itembank.common.account.AccountWorld;
import io.musician101.itembank.sponge.SpongeItemBank;
import io.musician101.itembank.sponge.account.SpongeInventoryHandler;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

public interface SpongeAccountPageOpener {

    Account<ItemStack> getAccount(UUID uuid);

    default void openInv(@Nonnull Player viewer, @Nonnull UUID uuid, @Nonnull String name, @Nonnull World world, int page) {
        Account<ItemStack> account = getAccount(uuid);
        if (account == null) {
            account = new Account<>(uuid, name);
            setAccount(account);
        }

        AccountWorld<ItemStack> accountWorld = account.getWorld(world.getName());
        if (accountWorld == null) {
            accountWorld = new AccountWorld<>(world.getName());
            account.setWorld(accountWorld);
        }

        AccountPage<ItemStack> accountPage = accountWorld.getPage(page);
        if (accountPage == null) {
            accountPage = new AccountPage<>(page);
            accountWorld.setPage(accountPage);
        }

        Optional<ItemBank<ItemStack, Logger, Player, World>> plugin = SpongeItemBank.instance();
        if (!plugin.isPresent()) {
            viewer.sendMessage(Text.builder(Reference.PREFIX + Messages.PLUGIN_NOT_INITIALIZED).color(TextColors.RED).build());
            return;
        }

        new SpongeInventoryHandler(plugin.get(), accountPage, viewer, name, world);
    }

    void setAccount(Account<ItemStack> account);
}

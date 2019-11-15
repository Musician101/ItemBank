package io.musician101.itembank.sponge.account.storage;

import io.musician101.itembank.common.account.Account;
import io.musician101.itembank.common.account.AccountPage;
import io.musician101.itembank.common.account.AccountWorld;
import io.musician101.itembank.sponge.SpongeItemBank;
import io.musician101.itembank.sponge.account.SpongeInventoryHandler;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.World;

public interface SpongeAccountPageOpener {

    Optional<Account<ItemStack>> getAccount(UUID uuid);

    default void openInv(@Nonnull Player viewer, @Nonnull UUID uuid, @Nonnull String name, @Nonnull World world, int page) {
        Account<ItemStack> account = getAccount(uuid).orElse(new Account<>(uuid, name));
        setAccount(account);

        AccountWorld<ItemStack> accountWorld = account.getWorld(world.getName()).orElse(new AccountWorld<>(world.getName()));
        account.setWorld(accountWorld);
        AccountPage<ItemStack> accountPage = accountWorld.getPage(page).orElse(new AccountPage<>(page));
        accountWorld.setPage(accountPage);
        new SpongeInventoryHandler(SpongeItemBank.instance(), accountPage, viewer, name, world);
    }

    void setAccount(Account<ItemStack> account);
}

package io.musician101.itembank.spigot.account.storage;

import io.musician101.itembank.common.account.Account;
import io.musician101.itembank.common.account.AccountPage;
import io.musician101.itembank.common.account.AccountWorld;
import io.musician101.itembank.spigot.account.SpigotInventoryHandler;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface SpigotAccountPageOpener {

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

        new SpigotInventoryHandler(accountPage, viewer, name, world);
    }

    void setAccount(Account<ItemStack> account);
}

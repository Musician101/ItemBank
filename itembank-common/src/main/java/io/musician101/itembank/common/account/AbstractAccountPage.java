package io.musician101.itembank.common.account;

import java.sql.ResultSet;
import java.util.UUID;

@SuppressWarnings("unused")
public abstract class AbstractAccountPage<E, Q, I, S, P, C, W> {

    protected final UUID owner;
    protected final int page;
    protected final W world;
    protected P viewer;

    protected AbstractAccountPage(UUID owner, W world, int page) {
        this.owner = owner;
        this.world = world;
        this.page = page;
    }

    protected abstract S deserializeItem(C serialization);

    protected abstract I getAccount() throws Exception;

    protected abstract S getItem(ResultSet resultSet) throws Exception;

    public UUID getOwner() {
        return owner;
    }

    public int getPage() {
        return page;
    }

    public W getWorld() {
        return world;
    }

    public abstract void onInventoryClose(E event);

    public abstract void onPlayerQuit(Q event);

    public abstract boolean openInv(P viewer);

    protected abstract void processEvent(P player, I inventory);

    protected abstract void returnInv(I inventory, String message);

    protected abstract void saveAccount(I topInv, I playerInv);

    protected abstract String serializeItem(S itemStack) throws Exception;

    protected abstract void unregisterListener();
}

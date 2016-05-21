package io.musician101.itembank.common.account;

import java.sql.ResultSet;
import java.util.UUID;

public abstract class AbstractAccountPage<E, I, S, P, C, W>
{
    protected final int page;
    protected P viewer;
    protected final UUID owner;
    protected final W world;

    protected AbstractAccountPage(UUID owner, W world, int page)
    {
        this.owner = owner;
        this.world = world;
        this.page = page;
    }

    public abstract boolean openInv(P viewer);

    @SuppressWarnings("unused")
    public abstract void onInventoryClose(E event);

    protected abstract void saveAccount(I topInv, I playerInv);

    protected abstract void returnInv(I inventory, String message);

    protected abstract I getAccount() throws Exception;//NOSONAR

    protected abstract S getItem(ResultSet resultSet) throws Exception;//NOSONAR

    protected abstract String serializeItem(S itemStack) throws Exception;//NOSONAR

    protected abstract S deserializeItem(C serialization);

    protected abstract void unregisterListener();

    public int getPage()
    {
        return page;
    }

    public UUID getOwner()
    {
        return owner;
    }

    public W getWorld()
    {
        return world;
    }
}

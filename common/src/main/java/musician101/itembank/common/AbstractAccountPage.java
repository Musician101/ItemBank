package musician101.itembank.common;

import java.util.UUID;

public abstract class AbstractAccountPage<Event, Inventory, Player, World>
{
    protected int page;
    protected Player viewer;
    protected UUID owner;
    protected World world;

    protected AbstractAccountPage(Player viewer, UUID owner, World world, int page)
    {
        this.viewer = viewer;
        this.owner = owner;
        this.world = world;
        this.page = page;
    }

    public abstract void onInventoryClose(Event event);

    protected abstract void saveAccount(Inventory topInv, Inventory playerInv);

    protected abstract void returnInv(Inventory inventory, String message);
}

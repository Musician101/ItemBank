package musician101.itembank.common.account;

import java.sql.ResultSet;
import java.util.UUID;

public abstract class AbstractAccountPage<Event, Inventory, ItemStack, Player, Serialization, World>
{
    protected int page;
    protected Player viewer;
    protected UUID owner;
    protected World world;

    protected AbstractAccountPage(UUID owner, World world, int page)
    {
        this.owner = owner;
        this.world = world;
        this.page = page;
    }

    public abstract boolean openInv(Player viewer);

    public abstract void onInventoryClose(Event event);

    protected abstract void saveAccount(Inventory topInv, Inventory playerInv);

    protected abstract void returnInv(Inventory inventory, String message);

    protected abstract Inventory getAccount() throws Exception;

    protected abstract ItemStack getItem(ResultSet resultSet) throws Exception;

    protected abstract String serializeItem(ItemStack itemStack) throws Exception;

    protected abstract ItemStack deserializeItem(Serialization serialization);

    protected abstract void unregisterListener();

    public int getPage()
    {
        return page;
    }

    public UUID getOwner()
    {
        return owner;
    }

    public World getWorld()
    {
        return world;
    }
}

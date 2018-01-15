package io.musician101.itembank.sponge.account;

import java.util.UUID;
import org.spongepowered.api.world.World;

public class AccountPageData {

    private final int page;
    private final UUID uuid;
    private final World world;

    public AccountPageData(int page, UUID uuid, World world) {
        this.page = page;
        this.uuid = uuid;
        this.world = world;
    }

    public int getPage() {
        return page;
    }

    public UUID getUuid() {
        return uuid;
    }

    public World getWorld() {
        return world;
    }
}

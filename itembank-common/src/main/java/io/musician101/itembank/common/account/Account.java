package io.musician101.itembank.common.account;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Account<I> {

    private static final Multimap<UUID, String> CHANGED_NAMES = HashMultimap.create();
    @Nonnull
    private final UUID uuid;
    @Nonnull
    private final Map<String, AccountWorld<I>> worlds;
    @Nonnull
    private String name;

    public Account(@Nonnull UUID uuid, @Nonnull String name) {
        this(uuid, name, new HashMap<>());
    }

    public Account(@Nonnull UUID uuid, @Nonnull String name, @Nonnull Map<String, AccountWorld<I>> worlds) {
        this.uuid = uuid;
        this.name = name;
        this.worlds = worlds;
    }

    public static Multimap<UUID, String> getChangedNames() {
        return ImmutableMultimap.copyOf(CHANGED_NAMES);
    }

    public void clearWorld(@Nonnull String worldName) {
        worlds.remove(worldName);
    }

    @Nonnull
    public UUID getID() {
        return uuid;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    public void setName(@Nonnull String name) {
        this.name = name;
        CHANGED_NAMES.put(uuid, this.name);
    }

    @Nullable
    public AccountWorld<I> getWorld(@Nonnull String worldName) {
        return worlds.get(worldName);
    }

    @Nonnull
    public Map<String, AccountWorld<I>> getWorlds() {
        return worlds;
    }

    public void setWorld(@Nonnull AccountWorld<I> world) {
        worlds.put(world.getWorldName(), world);
    }

    public interface Serializer<I> extends JsonDeserializer<Account<I>>, JsonSerializer<Account<I>> {

    }
}

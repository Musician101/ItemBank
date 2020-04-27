package io.musician101.itembank.common.account;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiPredicate;
import javax.annotation.Nonnull;

public class Account<I> {

    private final BiPredicate<String, AccountWorld<I>> worldNamePredicate = (worldName, world) -> worldName.equals(world.getWorldName());
    @Nonnull
    private final UUID uuid;
    @Nonnull
    private final List<AccountWorld<I>> worlds;
    @Nonnull
    private final String name;

    public Account(@Nonnull UUID uuid, @Nonnull String name) {
        this(uuid, name, new ArrayList<>());
    }

    public Account(@Nonnull UUID uuid, @Nonnull String name, @Nonnull List<AccountWorld<I>> worlds) {
        this.uuid = uuid;
        this.name = name;
        this.worlds = worlds;
    }

    public void clear() {
        worlds.clear();
    }

    public void clearWorld(@Nonnull String worldName) {
        worlds.removeIf(w -> worldNamePredicate.test(worldName, w));
    }

    @Nonnull
    public UUID getID() {
        return uuid;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nonnull
    public Optional<AccountWorld<I>> getWorld(@Nonnull String worldName) {
        return worlds.stream().filter(w -> worldNamePredicate.test(worldName, w)).findFirst();
    }

    @Nonnull
    public List<AccountWorld<I>> getWorlds() {
        return worlds;
    }

    public void setWorld(@Nonnull AccountWorld<I> world) {
        worlds.removeIf(w -> worldNamePredicate.test(world.getWorldName(), w));
        worlds.add(world);
    }

    public interface Serializer<I> extends JsonDeserializer<Account<I>>, JsonSerializer<Account<I>> {

    }
}

package io.musician101.itembank.common.account;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;

public class AccountWorld<I> {

    @Nonnull
    private final Map<Integer, AccountPage<I>> pages;
    @Nonnull
    private final String worldName;

    public AccountWorld(@Nonnull String worldName) {
        this(worldName, new HashMap<>());
    }

    public AccountWorld(@Nonnull String worldName, @Nonnull Map<Integer, AccountPage<I>> pages) {
        this.worldName = worldName;
        this.pages = pages;
    }

    public void clearPage(int page) {
        pages.remove(page);
    }

    @Nonnull
    public Optional<AccountPage<I>> getPage(int page) {
        return Optional.ofNullable(pages.get(page));
    }

    @Nonnull
    public Map<Integer, AccountPage<I>> getPages() {
        return pages;
    }

    @Nonnull
    public String getWorldName() {
        return worldName;
    }

    public void setPage(@Nonnull AccountPage<I> page) {
        pages.put(page.getPage(), page);
    }

    public interface Serializer<I> extends JsonDeserializer<AccountWorld<I>>, JsonSerializer<AccountWorld<I>> {

    }
}

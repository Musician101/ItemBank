package io.musician101.itembank.common.account;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AccountWorld<I> {

    @Nonnull
    private final List<I[]> pages;
    @Nonnull
    private final String worldName;

    public AccountWorld(@Nonnull String worldName) {
        this(worldName, new ArrayList<>());
    }

    public AccountWorld(@Nonnull String worldName, @Nonnull List<I[]> pages) {
        this.worldName = worldName;
        this.pages = pages;
    }

    public void clear() {
        IntStream.range(0, pages.size()).forEach(this::clearPage);
    }

    public void clearPage(int page) {
        IntStream.range(0, 45).forEach(y -> pages.get(page)[y] = null);
    }

    @Nonnull
    public I[] getPage(int page) {
        return pages.get(page);
    }

    @Nonnull
    public List<I[]> getPages() {
        return pages;
    }

    @Nonnull
    public String getWorldName() {
        return worldName;
    }

    public void setPage(int page, I[] contents) {
        pages.set(page, contents);
    }

    public void setSlot(int page, int slot, @Nullable I itemStack) {
        pages.get(page)[slot] = itemStack;
    }
}

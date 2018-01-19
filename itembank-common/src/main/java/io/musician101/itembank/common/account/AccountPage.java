package io.musician101.itembank.common.account;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;

public class AccountPage<I> {

    private final int page;
    @Nonnull
    private final Map<Integer, AccountSlot<I>> slots;

    public AccountPage(int page) {
        this(page, new HashMap<>());
    }

    public AccountPage(int page, @Nonnull Map<Integer, AccountSlot<I>> slots) {
        this.page = page;
        this.slots = slots;
    }

    public void clearSlot(int slot) {
        slots.remove(slot);
    }

    public int getPage() {
        return page;
    }

    @Nonnull
    public Optional<AccountSlot<I>> getSlot(int slot) {
        return Optional.ofNullable(slots.get(slot));
    }

    @Nonnull
    public Map<Integer, AccountSlot<I>> getSlots() {
        return slots;
    }

    public void setSlot(@Nonnull AccountSlot<I> slot) {
        slots.put(slot.getSlot(), slot);
    }

    public interface Serializer<I> extends JsonDeserializer<AccountPage<I>>, JsonSerializer<AccountPage<I>> {

    }
}

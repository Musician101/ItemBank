package io.musician101.itembank.common.account;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import javax.annotation.Nonnull;

public class AccountSlot<I> {

    @Nonnull
    private final I itemStack;
    private final int slot;

    public AccountSlot(int slot, @Nonnull I itemStack) {
        this.slot = slot;
        this.itemStack = itemStack;
    }

    @Nonnull
    public I getItemStack() {
        return itemStack;
    }

    public int getSlot() {
        return slot;
    }

    public interface Serializer<I> extends JsonDeserializer<AccountSlot<I>>, JsonSerializer<AccountSlot<I>> {

    }
}

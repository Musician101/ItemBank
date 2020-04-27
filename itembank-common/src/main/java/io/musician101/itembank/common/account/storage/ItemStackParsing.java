package io.musician101.itembank.common.account.storage;

import java.lang.reflect.Array;
import javax.annotation.Nonnull;
import ninja.leaping.configurate.ConfigurationNode;

public abstract class ItemStackParsing<I> {

    @Nonnull
    private final Class<I> typeClass;

    public ItemStackParsing(@Nonnull Class<I> typeClass) {
        this.typeClass = typeClass;
    }

    public abstract I load(@Nonnull ConfigurationNode configurationNode);

    public abstract ConfigurationNode save(I itemStack);

    @SuppressWarnings("unchecked")
    public I[] emptyArray() {
        return (I[]) Array.newInstance(typeClass, 45);
    }
}

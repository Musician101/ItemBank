package io.musician101.itembank.common.account;

import io.leangen.geantyref.TypeToken;
import io.musician101.itembank.common.Reference.AccountData;
import io.musician101.itembank.common.account.storage.ItemStackSerializer;
import io.musician101.musicianlibrary.java.storage.database.mongo.MongoSerializable;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bson.Document;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

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
        if (page > pages.size()) {
            pages.add(contents);
            return;
        }

        pages.set(page, contents);
    }

    public void setSlot(int page, int slot, @Nullable I itemStack) {
        pages.get(page)[slot] = itemStack;
    }

    public static class Serializer<I> implements MongoSerializable<AccountWorld<I>>, TypeSerializer<AccountWorld<I>> {

        private final Class<I> itemStackClass;
        private final ItemStackSerializer<I> itemStackSerializer;

        public Serializer(@Nonnull Class<I> itemStackClass, @Nonnull ItemStackSerializer<I> itemStackSerializer) {
            this.itemStackClass = itemStackClass;
            this.itemStackSerializer = itemStackSerializer;
        }

        @SuppressWarnings("unchecked")
        private I[] castedArray(int i) {
            return (I[]) Array.newInstance(itemStackClass, i);
        }

        @Override
        public AccountWorld<I> deserialize(Type type, ConfigurationNode node) throws SerializationException {
            if (!type.equals(new TypeToken<AccountWorld<I>>() {

            }.getType())) {
                return null;
            }

            String worldName = node.node(AccountData.WORLD).getString();
            if (worldName == null) {
                throw new SerializationException("World name can not be null.");
            }

            AccountWorld<I> accountWorld = new AccountWorld<>(worldName);
            for (ConfigurationNode page : node.node(AccountData.PAGES).childrenList()) {
                int pg = page.node(AccountData.PAGE).getInt();
                I[] items = page.node(AccountData.ITEMS).getList(itemStackClass, new ArrayList<>()).toArray(castedArray(0));
                accountWorld.setPage(pg, items);
            }

            return accountWorld;
        }

        @Override
        public AccountWorld<I> deserialize(@Nullable Document document) {
            if (document == null) {
                return null;
            }

            String worldName = document.getString(AccountData.WORLD);
            AccountWorld<I> accountWorld = new AccountWorld<>(worldName);
            document.getList(AccountData.PAGES, Document.class).forEach(page -> {
                int pg = page.getInteger(AccountData.PAGE);
                I[] items = page.getList(AccountData.ITEMS, Document.class).stream().map(itemStackSerializer::deserialize).toArray(this::castedArray);
                accountWorld.setPage(pg, items);
            });

            return accountWorld;
        }

        @Override
        public void serialize(Type type, @Nullable AccountWorld<I> obj, ConfigurationNode node) throws SerializationException {
            if (!type.equals(new TypeToken<AccountWorld<I>>() {

            }.getType()) || obj == null) {
                return;
            }

            node.node(AccountData.WORLD).set(obj.worldName);
            List<I[]> pages = obj.pages;
            node.node(AccountData.PAGES, IntStream.range(0, pages.size()).boxed().collect(Collectors.toMap(i -> i, i -> Arrays.asList(pages.get(i)))));
        }

        @Override
        public Document serialize(@Nonnull AccountWorld<I> obj) {
            Document document = new Document();
            document.put(AccountData.WORLD, obj.worldName);
            List<I[]> pages = obj.pages;
            document.put(AccountData.PAGES, IntStream.range(0, pages.size()).boxed().collect(Collectors.toMap(i -> i, i -> Arrays.stream(pages.get(i)).map(itemStackSerializer::serialize).collect(Collectors.toList()))));
            return document;
        }
    }
}

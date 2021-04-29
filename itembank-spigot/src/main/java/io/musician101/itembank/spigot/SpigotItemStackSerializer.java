package io.musician101.itembank.spigot;

import io.musician101.itembank.common.account.storage.ItemStackSerializer;
import java.io.BufferedWriter;
import java.io.StringWriter;
import java.lang.reflect.Type;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bson.Document;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.gson.GsonConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;

public class SpigotItemStackSerializer extends ItemStackSerializer<ItemStack> {

    @Override
    public ItemStack deserialize(@Nullable Document document) {
        if (document == null) {
            return null;
        }

        return ItemStack.deserialize(document);
    }

    @Override
    public ItemStack deserialize(Type type, ConfigurationNode node) throws SerializationException {
        if (!type.equals(ItemStack.class)) {
            return null;
        }

        try {
            StringWriter sw = new StringWriter();
            GsonConfigurationLoader.builder().sink(() -> new BufferedWriter(sw)).build().save(node);
            return deserialize(Document.parse(sw.toString()));
        }
        catch (ConfigurateException e) {
            throw new SerializationException(e);
        }
    }

    @Override
    public void serialize(Type type, @Nullable ItemStack obj, ConfigurationNode node) throws SerializationException {
        if (obj == null || !type.equals(ItemStack.class)) {
            return;
        }

        node.set(obj.serialize());
    }

    @Override
    public Document serialize(@Nonnull ItemStack itemStack) {
        return new Document(itemStack.serialize());
    }
}

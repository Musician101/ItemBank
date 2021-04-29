package io.musician101.itembank.sponge;

import io.musician101.itembank.common.account.storage.ItemStackSerializer;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Type;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bson.Document;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.persistence.DataTranslator;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.gson.GsonConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;

public class SpongeItemStackSerializer extends ItemStackSerializer<ItemStack> {

    private final DataTranslator<ConfigurationNode> translator = Sponge.dataManager().translator(ConfigurationNode.class).orElseThrow(() -> new IllegalStateException("No translator for " + ConfigurationNode.class.getName() + " found."));

    @Override
    public ItemStack deserialize(@Nullable Document document) {
        if (document == null) {
            return null;
        }

        try {
            return deserialize(ItemStack.class, GsonConfigurationLoader.builder().source(() -> new BufferedReader(new StringReader(document.toJson()))).build().load());
        }
        catch (ConfigurateException e) {
            return null;
        }
    }

    @Override
    public ItemStack deserialize(Type type, ConfigurationNode node) throws SerializationException {
        if (!type.equals(ItemStack.class)) {
            return null;
        }

        return ItemStack.builder().fromContainer(translator.translate(node)).build();
    }

    @Override
    public void serialize(Type type, @Nullable ItemStack obj, ConfigurationNode node) throws SerializationException {
        if (!type.equals(ItemStack.class) || obj == null) {
            return;
        }

        node.set(translator.translate(obj.toContainer()));
    }

    @Override
    public Document serialize(@Nonnull ItemStack itemStack) {
        try {
            StringWriter sw = new StringWriter();
            ConfigurationNode node = BasicConfigurationNode.root();
            serialize(ItemStack.class, itemStack, node);
            GsonConfigurationLoader.builder().sink(() -> new BufferedWriter(sw)).build().save(node);
            return Document.parse(sw.toString());
        }
        catch (ConfigurateException e) {
            return null;
        }
    }
}

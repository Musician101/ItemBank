package io.musician101.itembank.spigot.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Map;
import javax.annotation.Nonnull;
import org.bukkit.inventory.ItemStack;

import static io.musician101.itembank.spigot.SpigotItemBank.GSON;

public class ItemStackSerializer implements JsonDeserializer<ItemStack>, JsonSerializer<ItemStack> {

    private static final Type TYPE = new TypeToken<Map<String, Object>>() {

    }.getType();

    @Nonnull
    @Override
    public ItemStack deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        Map<String, Object> map = GSON.fromJson(jsonElement, TYPE);
        return ItemStack.deserialize(map);
    }

    @Nonnull
    @Override
    public JsonElement serialize(ItemStack itemStack, Type type, JsonSerializationContext jsonSerializationContext) {
        return GSON.toJsonTree(itemStack.serialize(), TYPE);
    }
}

package io.musician101.itembank.sponge.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.io.IOException;
import java.lang.reflect.Type;
import javax.annotation.Nonnull;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.item.inventory.ItemStack;

import static io.musician101.itembank.sponge.SpongeItemBank.GSON;

public class ItemStackSerializer implements JsonDeserializer<ItemStack>, JsonSerializer<ItemStack> {

    @Nonnull
    @Override
    public ItemStack deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        try {
            return ItemStack.builder().fromContainer(DataFormats.JSON.read(jsonElement.toString())).build();
        }
        catch (IOException e) {
            return ItemStack.empty();
        }
    }

    @Nonnull
    @Override
    public JsonElement serialize(ItemStack itemStack, Type type, JsonSerializationContext jsonSerializationContext) {
        try {
            return GSON.fromJson(DataFormats.JSON.write(itemStack.toContainer()), JsonObject.class);
        }
        catch (IOException e) {
            return JsonNull.INSTANCE;
        }
    }
}

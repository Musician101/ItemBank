package io.musician101.itembank.spigot.json.account;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.reflect.TypeToken;
import io.musician101.itembank.common.account.AccountSlot;
import io.musician101.itembank.common.account.AccountSlot.Serializer;
import java.lang.reflect.Type;
import javax.annotation.Nonnull;
import org.bukkit.inventory.ItemStack;

import static io.musician101.itembank.common.Reference.PlayerData.ITEM;
import static io.musician101.itembank.common.Reference.PlayerData.SLOT;
import static io.musician101.itembank.spigot.SpigotItemBank.GSON;

public class AccountSlotSerializer implements Serializer<ItemStack> {

    public static final Type TYPE = new TypeToken<AccountSlot<ItemStack>>() {

    }.getType();

    @Nonnull
    @Override
    public AccountSlot<ItemStack> deserialize(@Nonnull JsonElement jsonElement, @Nonnull Type type, @Nonnull JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject json = jsonElement.getAsJsonObject();
        int slot = json.get(SLOT).getAsInt();
        ItemStack itemStack = GSON.fromJson(json.get(ITEM), ItemStack.class);
        return new AccountSlot<>(slot, itemStack);
    }

    @Nonnull
    @Override
    public JsonElement serialize(@Nonnull AccountSlot<ItemStack> src, @Nonnull Type type, @Nonnull JsonSerializationContext jsonSerializationContext) {
        JsonObject json = new JsonObject();
        json.addProperty(SLOT, src.getSlot());
        json.add(ITEM, GSON.toJsonTree(src.getItemStack()));
        return json;
    }
}

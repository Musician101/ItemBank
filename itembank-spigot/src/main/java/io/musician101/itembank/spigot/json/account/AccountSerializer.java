package io.musician101.itembank.spigot.json.account;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.reflect.TypeToken;
import io.musician101.itembank.common.account.Account;
import io.musician101.itembank.common.account.Account.Serializer;
import io.musician101.itembank.common.account.AccountWorld;
import io.musician101.musicianlibrary.java.util.Utils;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;
import org.bukkit.inventory.ItemStack;

import static io.musician101.itembank.common.Reference.PlayerData.ID;
import static io.musician101.itembank.common.Reference.PlayerData.NAME;
import static io.musician101.itembank.common.Reference.PlayerData.WORLD;
import static io.musician101.itembank.common.Reference.PlayerData.WORLDS;
import static io.musician101.itembank.spigot.SpigotItemBank.GSON;

public class AccountSerializer implements Serializer<ItemStack> {

    public static final Type TYPE = new TypeToken<AccountWorld<ItemStack>>() {

    }.getType();

    @Nonnull
    @Override
    public Account<ItemStack> deserialize(@Nonnull JsonElement jsonElement, @Nonnull Type type, @Nonnull JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject json = jsonElement.getAsJsonObject();
        UUID uuid = UUID.fromString(json.get(ID).getAsString());
        String name = json.get(NAME).getAsString();
        Map<String, AccountWorld<ItemStack>> items = StreamSupport.stream(json.getAsJsonArray(WORLD).spliterator(), false).map(slot -> GSON.<AccountWorld<ItemStack>>fromJson(slot, AccountWorldSerializer.TYPE)).collect(Collectors.toMap(AccountWorld::getWorldName, v -> v));
        return new Account<>(uuid, name, items);
    }

    @Nonnull
    @Override
    public JsonElement serialize(@Nonnull Account<ItemStack> src, @Nonnull Type type, @Nonnull JsonSerializationContext jsonSerializationContext) {
        JsonObject json = new JsonObject();
        json.addProperty(ID, src.getID().toString());
        json.addProperty(NAME, src.getName());
        json.add(WORLDS, src.getWorlds().values().stream().map(GSON::toJsonTree).collect(Utils.jsonArrayCollector()));
        return json;
    }
}

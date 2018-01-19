package io.musician101.itembank.sponge.json.account;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.reflect.TypeToken;
import io.musician101.itembank.common.account.AccountPage;
import io.musician101.itembank.common.account.AccountWorld;
import io.musician101.itembank.common.account.AccountWorld.Serializer;
import io.musician101.musicianlibrary.java.util.Utils;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;
import org.spongepowered.api.item.inventory.ItemStack;

import static io.musician101.itembank.common.Reference.PlayerData.PAGES;
import static io.musician101.itembank.common.Reference.PlayerData.WORLD;
import static io.musician101.itembank.sponge.SpongeItemBank.GSON;

public class AccountWorldSerializer implements Serializer<ItemStack> {

    public static final Type TYPE = new TypeToken<AccountWorld<ItemStack>>() {

    }.getType();

    @Nonnull
    @Override
    public AccountWorld<ItemStack> deserialize(@Nonnull JsonElement jsonElement, @Nonnull Type type, @Nonnull JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject json = jsonElement.getAsJsonObject();
        String worldName = json.get(WORLD).getAsString();
        Map<Integer, AccountPage<ItemStack>> items = StreamSupport.stream(json.getAsJsonArray(WORLD).spliterator(), false).map(slot -> GSON.<AccountPage<ItemStack>>fromJson(slot, AccountPageSerializer.TYPE)).collect(Collectors.toMap(AccountPage::getPage, v -> v));
        return new AccountWorld<>(worldName, items);
    }

    @Nonnull
    @Override
    public JsonElement serialize(@Nonnull AccountWorld<ItemStack> src, @Nonnull Type type, @Nonnull JsonSerializationContext jsonSerializationContext) {
        JsonObject json = new JsonObject();
        json.addProperty(WORLD, src.getWorldName());
        json.add(PAGES, src.getPages().values().stream().map(GSON::toJsonTree).collect(Utils.jsonArrayCollector()));
        return json;
    }
}

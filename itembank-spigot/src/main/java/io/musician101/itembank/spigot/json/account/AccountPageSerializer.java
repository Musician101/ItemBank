package io.musician101.itembank.spigot.json.account;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.reflect.TypeToken;
import io.musician101.itembank.common.account.AccountPage;
import io.musician101.itembank.common.account.AccountPage.Serializer;
import io.musician101.itembank.common.account.AccountSlot;
import io.musician101.musicianlibrary.java.util.Utils;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;
import org.bukkit.inventory.ItemStack;

import static io.musician101.itembank.common.Reference.PlayerData.ITEM;
import static io.musician101.itembank.common.Reference.PlayerData.ITEMS;
import static io.musician101.itembank.common.Reference.PlayerData.PAGE;
import static io.musician101.itembank.spigot.SpigotItemBank.GSON;

public class AccountPageSerializer implements Serializer<ItemStack> {

    public static final Type TYPE = new TypeToken<AccountPage<ItemStack>>() {

    }.getType();

    @Nonnull
    @Override
    public AccountPage<ItemStack> deserialize(@Nonnull JsonElement jsonElement, @Nonnull Type type, @Nonnull JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject json = jsonElement.getAsJsonObject();
        int page = json.get(PAGE).getAsInt();
        Map<Integer, AccountSlot<ItemStack>> items = StreamSupport.stream(json.getAsJsonArray(ITEMS).spliterator(), false).map(slot -> GSON.<AccountSlot<ItemStack>>fromJson(slot, AccountSlotSerializer.TYPE)).collect(Collectors.toMap(AccountSlot::getSlot, v -> v));
        return new AccountPage<>(page, items);
    }

    @Nonnull
    @Override
    public JsonElement serialize(@Nonnull AccountPage<ItemStack> src, @Nonnull Type type, @Nonnull JsonSerializationContext jsonSerializationContext) {
        JsonObject json = new JsonObject();
        json.addProperty(PAGE, src.getPage());
        json.add(ITEM, src.getSlots().values().stream().map(GSON::toJsonTree).collect(Utils.jsonArrayCollector()));
        return json;
    }
}

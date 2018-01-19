package io.musician101.itembank.sponge;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.item.inventory.ItemStack;

public class IBUtils {

    private IBUtils() {

    }

    @SuppressWarnings("unchecked")
    public static boolean isSameVariant(ItemStack itemStack1, ItemStack itemStack2) {
        if (itemStack1.getType() != itemStack2.getType()) {
            return false;
        }

        List<Key<? extends Value<? extends CatalogType>>> keys = Arrays.asList(Keys.BRICK_TYPE, Keys.COAL_TYPE, Keys.COOKED_FISH, Keys.DIRT_TYPE, Keys.DISGUISED_BLOCK_TYPE, Keys.DOUBLE_PLANT_TYPE, Keys.DYE_COLOR, Keys.FISH_TYPE, Keys.GOLDEN_APPLE_TYPE, Keys.PRISMARINE_TYPE, Keys.QUARTZ_TYPE, Keys.SAND_TYPE, Keys.SANDSTONE_TYPE, Keys.SHRUB_TYPE, Keys.STONE_TYPE, Keys.TREE_TYPE, Keys.WALL_TYPE);
        return !keys.stream().map(key -> (Key<Value<CatalogType>>) key).filter(key -> itemStack1.supports(key) && itemStack2.supports(key)).filter(key -> {
            Optional<? extends CatalogType> type1 = itemStack1.get(key);
            Optional<? extends CatalogType> type2 = itemStack2.get(key);
            return type1.isPresent() && type2.isPresent() && type1.get().getId().equals(type2.get().getId());

        }).collect(Collectors.toSet()).isEmpty();
    }
}

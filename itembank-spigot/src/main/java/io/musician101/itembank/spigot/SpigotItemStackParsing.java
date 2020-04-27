package io.musician101.itembank.spigot;

import io.musician101.itembank.common.account.storage.ItemStackParsing;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.SimpleConfigurationNode;
import org.bukkit.inventory.ItemStack;

public class SpigotItemStackParsing extends ItemStackParsing<ItemStack> {

    public SpigotItemStackParsing() {
        super(ItemStack.class);
    }

    @Override
    public ItemStack load(@Nonnull ConfigurationNode configurationNode) {
        Map<String, Object> map = ((Map<?, ?>) configurationNode.getValue()).entrySet().stream().collect(Collectors.toMap(e -> e.getKey().toString(), Entry::getValue));
        return ItemStack.deserialize(map);
    }

    @Override
    public ConfigurationNode save(ItemStack itemStack) {
        ConfigurationNode node = SimpleConfigurationNode.root();
        node.setValue(itemStack.serialize());
        return node;
    }
}

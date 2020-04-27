package io.musician101.itembank.sponge;

import io.musician101.itembank.common.account.storage.ItemStackParsing;
import javax.annotation.Nonnull;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.data.persistence.DataTranslators;
import org.spongepowered.api.item.inventory.ItemStack;

public class SpongeItemStackParsing extends ItemStackParsing<ItemStack> {

    public SpongeItemStackParsing() {
        super(ItemStack.class);
    }

    @Override
    public ItemStack load(@Nonnull ConfigurationNode configurationNode) {
        return ItemStack.builder().fromContainer(DataTranslators.CONFIGURATION_NODE.translate(configurationNode)).build();
    }

    @Override
    public ConfigurationNode save(ItemStack itemStack) {
        return DataTranslators.CONFIGURATION_NODE.translate(itemStack.toContainer());
    }
}

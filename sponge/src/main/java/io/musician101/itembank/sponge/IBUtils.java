package io.musician101.itembank.sponge;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.item.inventory.ItemStack;

public class IBUtils
{
    public static boolean isSameVariant(ItemStack itemStack1, ItemStack itemStack2)
    {
        DataContainer container1 = itemStack1.toContainer();
        DataContainer container2 = itemStack2.toContainer();
        for (DataQuery query1 : container1.getKeys(true))
            for (DataQuery query2 : container2.getKeys(true))
                if (query1 == query2)
                    if (container1.get(query1).get() == container2.get(query2).get())
                        return true;

        return false;
    }
}

package musician101.sponge.itembank.util;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.item.inventory.Inventory;
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

    //TODO move to common library
    @Deprecated
    public static boolean isNumber(String s)
    {
        if (s == null)
            return false;

        int length = s.length();
        if (length == 0)
            return false;

        int i = 0;
        if (s.charAt(0) == '-')
        {
            if (length == 1)
                return false;

            i = 1;
        }

        for (; i < length; i++)
        {
            char c = s.charAt(i);
            if (c < '0' || c > '9')
                return false;
        }

        return true;
    }
}

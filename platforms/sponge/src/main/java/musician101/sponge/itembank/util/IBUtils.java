package musician101.sponge.itembank.util;

import org.spongepowered.api.CatalogType;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.mutable.VariantData;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;

import java.io.File;
import java.io.IOException;

public class IBUtils
{
    //TODO move to common?
    @Deprecated
	public static void createPlayerFile(File file) throws IOException
	{
		if (!file.exists())
			file.createNewFile();
	}
	
	public static int getAmount(Inventory inv, ItemStack itemStack)
	{
		int amount = 0;
		for (Inventory slot : inv.query(itemStack.getItem()))
		{
            ItemStack inventoryItemStack = slot.peek().get();
            if (itemStack.getItem() == inventoryItemStack.getItem() && isSameVariant(itemStack, inventoryItemStack))
                amount += inventoryItemStack.getQuantity();

		}

		return amount;
	}

    public static boolean isSameVariant(ItemStack itemStack1, ItemStack itemStack2)
    {
        DataContainer container1 = itemStack1.toContainer();
        DataContainer container2 = itemStack2.toContainer();
        for (DataQuery query1 : container1.getKeys(true))
            for (DataQuery query2 : container2.getKeys(true))
                if (query1 == query2)
                    if (container1.get(query1).get() == container2.get(query2).get())
                        return true;

        // Code for possible future use
        /*for (DataManipulator data : itemStack1.getContainers())
        {
            if (data instanceof VariantData)
            {
                Object value = ((VariantData) data).type().get();
                String variant = (value instanceof CatalogType) ? ((CatalogType) value).getId() : value.toString();
            }
        }*/

        return false;
    }
	
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

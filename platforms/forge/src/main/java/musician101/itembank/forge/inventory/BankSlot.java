package musician101.itembank.forge.inventory;

import musician101.itembank.forge.config.ConfigHandler;
import musician101.itembank.forge.util.IBUtils;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class BankSlot extends Slot
{
	public BankSlot(IInventory p_i1824_1_, int p_i1824_2_, int p_i1824_3_, int p_i1824_4_)
	{
		super(p_i1824_1_, p_i1824_2_, p_i1824_3_, p_i1824_4_);
	}
	
	@Override
	public boolean isItemValid(ItemStack item)
	{
		if (ConfigHandler.isItemRestricted(item))
		{
			int amount = IBUtils.getAmount(inventory, item.getItem(), item.getItemDamage()) + item.stackSize;
			int limit = ConfigHandler.getRestrictedItem(item).stackSize;
			//This may or may not work. It may also might not be needed at all
			if (amount > limit)
				return false;
		}
		
		return !ConfigHandler.isItemBlacklisted(item) || ConfigHandler.isItemWhitelisted(item);
	}
}

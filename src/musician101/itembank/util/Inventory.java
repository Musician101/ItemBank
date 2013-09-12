package musician101.itembank.util;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Musician101
 */
public class Inventory
{
	/**
	 * Method for finding specific blocks/items in a player's inventory
	 * 
	 * @param player Player who's inventory is being checked.
	 * @param material The material that is being searched for.
	 * @param dmg The damage value of the material (i.e. if material = oak wood then dmg = 1).
	 * @return The amount of the material in the player's inventory.
	 */
	public static int getAmount(Player player, Material material, byte dmg)
	{
		int has = 0;
		for (ItemStack item : player.getInventory().getContents())
		{
			if ((item !=null) && (item.getTypeId() == material.getId()) && (item.getAmount() > 0) && (item.getDurability() == dmg))
				has += item.getAmount();
		}
		return has;
	}
}

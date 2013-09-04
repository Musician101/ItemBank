package musician101.itembank.util;

import musician101.itembank.ItemBank;

import org.bukkit.Material;

/**
 * Translates the command inputs into the proper data.
 * 
 * @author Musician101
 */
public class ItemTranslator
{
	
	/**
	 * Reads items.csv for id and metadata numbers. (Unimplemented)
	 * 
	 * @param materialName The material that is being deposited/withdrawn.
	 * @return The material ID and Damage Value.
	 */
	/*public static Map<Integer, Byte> getMaterial(String materialName)
	{
		Map<Integer, Byte> materialMap = null;
		try
		{
			FileReader fr = new FileReader(ItemBank.plugin.getDataFolder() + "/items.csv");
			materialMap = parseCSV();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		return materialMap;
	}*/
	
	/**
	 * Reads the items.csv. (Unimplemented)
	 * 
	 * @param plugin References the plugin's main class.
	 * @param player The player who executed the command.
	 */
	/*public static void parseCSV(ItemBank plugin, Player player)
	{
		String csvFile = plugin.getDataFolder() + "/items.csv";
		BufferedReader br = null;
		String line;
		String split = ",";
		
		try
		{
			br = new BufferedReader(new FileReader(csvFile));
			while((line = br.readLine()) != null)
			{
				String[] itemID = line.split(split);
				if (itemID.length == 3)
				{
					player.sendMessage(itemID[1] + ":" + itemID[2]);
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (br != null)
			{
				try
				{
					br.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		plugin.getLogger().info("Done");
	}*/

	/**
	 * Uses materialName to find the block/item's Material enum.
	 * 
	 * @param plugin References the plugin's main class.
	 * @param materialName The material that is being deposited/withdrawn.
	 * @return The Material of the block/item.
	 */
	public static Material getMaterial(ItemBank plugin, String materialName)
	{
		Material material = Material.matchMaterial(materialName);
		if (materialName.toLowerCase().contains("wood"))
			material = Material.WOOD;
		else if (materialName.toLowerCase().contains("sapling"))
			material = Material.SAPLING;
		else if (materialName.toLowerCase().contains("log"))
			material = Material.LOG;
		else if (materialName.toLowerCase().contains("leaves"))
			material = Material.LEAVES;
		else if (materialName.toLowerCase().contains("sandstone"))
			material = Material.SANDSTONE;
		else if (materialName.toLowerCase().contains("longgrass") || materialName.toLowerCase().contains("fern"))
			material = Material.LONG_GRASS;
		else if (materialName.toLowerCase().contains("wool"))
			material = Material.WOOL;
		else if (materialName.toLowerCase().contains("slab") || materialName.toLowerCase().contains("step") || materialName.toLowerCase().contains("halfstep"))
		{
			if (materialName.toLowerCase().contains("wood") || materialName.toLowerCase().contains("oak") || materialName.toLowerCase().contains("spruce") || materialName.toLowerCase().contains("birch") || materialName.toLowerCase().contains("jungle"))
				material = Material.WOOD_STEP;
			else
				material = Material.STEP;
		}
		else if (materialName.toLowerCase().contains("stonebrick"))
			material = Material.SMOOTH_BRICK;
		else if (materialName.toLowerCase().contains("wall"))
			material = Material.COBBLE_WALL;
		else if (materialName.toLowerCase().contains("quartz"))
		{
			if (materialName.toLowerCase().contains("nether"))
				material = Material.QUARTZ;
			else
				material = Material.QUARTZ_BLOCK;
		}
		else if (materialName.toLowerCase().contains("coal"))
			material = Material.COAL;
		else if (materialName.toLowerCase().contains("golden"))
			material = Material.GOLDEN_APPLE;
		else if (materialName.toLowerCase().contains("inksack") || materialName.toLowerCase().contains("rosered") || materialName.toLowerCase().contains("cactusgreen") || materialName.toLowerCase().contains("cocoabeans") || materialName.toLowerCase().contains("lapis") || materialName.toLowerCase().contains("dye") || materialName.toLowerCase().contains("dandelionyellow") || materialName.toLowerCase().contains("bonemeal"))
			material = Material.INK_SACK;
		else if (materialName.toLowerCase().contains("waterbottle"))
			material = Material.POTION;
		else if (materialName.toLowerCase().contains("skull") || materialName.toLowerCase().contains("head"))
			material = Material.SKULL_ITEM;		
		return material;
	}
	
	/**
	 * Sets the path within the player's account.
	 * 
	 * @param plugin References the plugin's main class.
	 * @param materialName The material that is being deposited/withdrawn.
	 * @return The path to the block/item in the player's account.
	 */
 	public static String getPath(ItemBank plugin, String materialName)
	{
		Material material = Material.matchMaterial(materialName);
		String path = "";
		if (materialName.toLowerCase().contains("wood"))
		{
			material = Material.WOOD;
			if (materialName.toLowerCase().contains("oak"))
				path = material.toString().toLowerCase() + ".oak";
			else if (materialName.toLowerCase().contains("spruce"))
				path = material.toString().toLowerCase() + ".spruce";
			else if (materialName.toLowerCase().contains("birch"))
				path = material.toString().toLowerCase() + ".birch";
			else if (materialName.toLowerCase().contains("jungle"))
				path = material.toString().toLowerCase() + ".jungle";
		}
		else if (materialName.toLowerCase().contains("sapling"))
		{
			material = Material.SAPLING;
			if (materialName.toLowerCase().contains("oak"))
				path = material.toString().toLowerCase() + ".oak";
			else if (materialName.toLowerCase().contains("spruce"))
				path = material.toString().toLowerCase() + ".spruce";
			else if (materialName.toLowerCase().contains("birch"))
				path = material.toString().toLowerCase() + ".birch";
			else if (materialName.toLowerCase().contains("jungle"))
				path = material.toString().toLowerCase() + ".jungle";
		}
		else if (materialName.toLowerCase().contains("log"))
		{
			material = Material.LOG;
			if (materialName.toLowerCase().contains("oak"))
				path = material.toString().toLowerCase() + ".oak";
			else if (materialName.toLowerCase().contains("spruce"))
				path = material.toString().toLowerCase() + ".spruce";
			else if (materialName.toLowerCase().contains("birch"))
				path = material.toString().toLowerCase() + ".birch";
			else if (materialName.toLowerCase().contains("jungle"))
				path = material.toString().toLowerCase() + ".jungle";
		}
		else if (materialName.toLowerCase().contains("leaves"))
		{
			material = Material.LEAVES;
			if (materialName.toLowerCase().contains("oak"))
				path = material.toString().toLowerCase() + ".oak";
			else if (materialName.toLowerCase().contains("spruce"))
				path = material.toString().toLowerCase() + ".spruce";
			else if (materialName.toLowerCase().contains("birch"))
				path = material.toString().toLowerCase() + ".birch";
			else if (materialName.toLowerCase().contains("jungle"))
				path = material.toString().toLowerCase() + ".jungle";
		}
		else if (materialName.toLowerCase().contains("sandstone"))
		{
			material = Material.SANDSTONE;
			if (materialName.toLowerCase().contains("chiseled"))
				path = material.toString().toLowerCase() + ".chiseled";
			else if (materialName.toLowerCase().contains("smooth"))
				path = material.toString().toLowerCase() + ".smooth";
			else
				path = material.toString().toLowerCase() + ".standard";
		}
		else if (materialName.toLowerCase().contains("longgrass"))
		{
			material = Material.LONG_GRASS;
			path = material.toString().toLowerCase() + ".grass";
		}
		else if (materialName.toLowerCase().contains("fern"))
		{
			material = Material.LONG_GRASS;
			path = material.toString().toLowerCase() + ".fern";
		}
		else if (materialName.toLowerCase().contains("wool"))
		{
			material = Material.WOOL;
			if (materialName.toLowerCase().contains("white"))
				path = material.toString().toLowerCase() + ".white";
			else if (materialName.toLowerCase().contains("orange"))
				path = material.toString().toLowerCase() + ".orange";
			else if (materialName.toLowerCase().contains("magenta"))
				path = material.toString().toLowerCase() + ".magenta";
			else if (materialName.toLowerCase().contains("lightblue"))
				path = material.toString().toLowerCase() + ".lightBlue";
			else if (materialName.toLowerCase().contains("yellow"))
				path = material.toString().toLowerCase() + ".yellow";
			else if (materialName.toLowerCase().contains("lime"))
				path = material.toString().toLowerCase() + ".lime";
			else if (materialName.toLowerCase().contains("pink"))
				path = material.toString().toLowerCase() + ".pink";
			else if (materialName.toLowerCase().contains("gray"))
				path = material.toString().toLowerCase() + ".gray";
			else if (materialName.toLowerCase().contains("lightgray"))
				path = material.toString().toLowerCase() + ".lightGray";
			else if (materialName.toLowerCase().contains("cyan"))
				path = material.toString().toLowerCase() + ".cyan";
			else if (materialName.toLowerCase().contains("purple"))
				path = material.toString().toLowerCase() + ".purple";
			else if (materialName.toLowerCase().contains("blue"))
				path = material.toString().toLowerCase() + ".blue";
			else if (materialName.toLowerCase().contains("brown"))
				path = material.toString().toLowerCase() + ".brown";
			else if (materialName.toLowerCase().contains("green"))
				path = material.toString().toLowerCase() + ".green";
			else if (materialName.toLowerCase().contains("red"))
				path = material.toString().toLowerCase() + ".red";
			else if (materialName.toLowerCase().contains("black"))
				path = material.toString().toLowerCase() + ".black";
		}
		else if (materialName.toLowerCase().contains("slab") || materialName.toLowerCase().contains("step") || materialName.toLowerCase().contains("halfstep"))
		{
			if (materialName.toLowerCase().contains("wood") || materialName.toLowerCase().contains("oak") || materialName.toLowerCase().contains("spruce") || materialName.toLowerCase().contains("birch") || materialName.toLowerCase().contains("jungle"))
			{
				material = Material.WOOD_STEP;
				if (materialName.toLowerCase().contains("oak"))
					path = material.toString().toLowerCase() + ".oak";
				else if (materialName.toLowerCase().contains("spruce"))
					path = material.toString().toLowerCase() + ".spruce";
				else if (materialName.toLowerCase().contains("birch"))
					path = material.toString().toLowerCase() + ".birch";
				else if (materialName.toLowerCase().contains("jungle"))
					path = material.toString().toLowerCase() + ".jungle";
			}
			else
			{
				material = Material.STEP;
				if (materialName.toLowerCase().contains("stone") || materialName.toLowerCase().contains("smooth") || materialName.toLowerCase().contains(("smoothstone")))
					path = material.toString().toLowerCase() + ".stone";
				else if (materialName.toLowerCase().contains("sandstone"))
					path = material.toString().toLowerCase() + ".sandstone";
				else if (materialName.toLowerCase().contains("cobble") || materialName.toLowerCase().contains("cobblestone"))
					path = material.toString().toLowerCase() + ".cobble";
				else if (materialName.toLowerCase().contains("brick"))
				{
					if (materialName.toLowerCase().contains("stone"))
						path = material.toString().toLowerCase() + ".stonebrick";
					else if (materialName.toLowerCase().contains("nether"))
						path = material.toString().toLowerCase() + ".netherbrick";
					else
						path = material.toString().toLowerCase() + ".brick";
				}
				else if (materialName.toLowerCase().contains("quartz"))
					path = material.toString().toLowerCase() + ".quartz";
			}
		}
		else if (materialName.toLowerCase().contains("stonebrick"))
		{
			material = Material.SMOOTH_BRICK;
			if (materialName.toLowerCase().contains("mossy"))
				path = material.toString().toLowerCase() + ".mossy";
			else if (materialName.toLowerCase().contains("cracked"))
				path = material.toString().toLowerCase() + ".cracked";
			else if (materialName.toLowerCase().contains("chiseled"))
				path = material.toString().toLowerCase() + ".chiseled";
			else
				path = material.toString().toLowerCase() + ".standard";
		}
		else if (materialName.toLowerCase().contains("wall"))
		{
			material = Material.COBBLE_WALL;
			if (materialName.toLowerCase().contains("mossy"))
				path = material.toString().toLowerCase() + ".mossy";
			else
				path = material.toString().toLowerCase() + ".standard";
		}
		else if (materialName.toLowerCase().contains("quartz"))
		{
			if (materialName.toLowerCase().contains("nether"))
			{
				material = Material.QUARTZ;
				path = material.toString().toLowerCase();
			}
			else
			{
				material = Material.QUARTZ_BLOCK;
				if (materialName.toLowerCase().contains("block"))
					path = material.toString().toLowerCase() + ".standard";
				else if (materialName.toLowerCase().contains("chiseled"))
					path = material.toString().toLowerCase() + ".chiseled";
				else if (materialName.toLowerCase().contains("column"))
					path = material.toString().toLowerCase() + ".column";
			}
			
		}
		else if (materialName.toLowerCase().contains("coal"))
		{
			material = Material.COAL;
			if (materialName.toLowerCase().contains("charcoal"))
				path = material.toString().toLowerCase() + ".charcoal";
			else
				path = material.toString().toLowerCase() + ".standard";
		}
		else if (materialName.toLowerCase().contains("golden"))
		{
			material = Material.GOLDEN_APPLE;
			if (materialName.toLowerCase().contains("notched"))
				path = material.toString().toLowerCase() + ".notched";
			else
				path = material.toString().toLowerCase() + ".standard";
		}
		else if (materialName.toLowerCase().contains("inksack") || materialName.toLowerCase().contains("rosered") || materialName.toLowerCase().contains("cactusgreen") || materialName.toLowerCase().contains("cocoabeans") || materialName.toLowerCase().contains("lapis") || materialName.toLowerCase().contains("dye") || materialName.toLowerCase().contains("dandelionyellow") || materialName.toLowerCase().contains("bonemeal"))
		{
			material = Material.INK_SACK;
			if (materialName.toLowerCase().contains("inksack"))
				path = material.toString().toLowerCase() + ".ink";
			else if (materialName.toLowerCase().contains("rosered"))
				path = material.toString().toLowerCase() + ".red";
			else if (materialName.toLowerCase().contains("cactusgreen"))
				path = material.toString().toLowerCase() + ".green";
			else if (materialName.toLowerCase().contains("cocoabeans"))
				path = material.toString().toLowerCase() + ".cocoa";
			else if (materialName.toLowerCase().contains("lapis"))
				path = material.toString().toLowerCase() + ".lapis";
			else if (materialName.toLowerCase().contains("purple"))
				path = material.toString().toLowerCase() + ".purple";
			else if (materialName.toLowerCase().contains("cyan"))
				path = material.toString().toLowerCase() + ".cyan";
			else if (materialName.toLowerCase().contains("lightgray"))
				path = material.toString().toLowerCase() + ".lightGray";
			else if (materialName.toLowerCase().contains("gray"))
				path = material.toString().toLowerCase() + ".gray";
			else if (materialName.toLowerCase().contains("pink"))
				path = material.toString().toLowerCase() + ".pink";
			else if (materialName.toLowerCase().contains("lime"))
				path = material.toString().toLowerCase() + ".lime";
			else if (materialName.toLowerCase().contains("dandelionyellow"))
				path = material.toString().toLowerCase() + ".yellow";
			else if (materialName.toLowerCase().contains("lightblue"))
				path = material.toString().toLowerCase() + ".lightBlue";
			else if (materialName.toLowerCase().contains("magenta"))
				path = material.toString().toLowerCase() + ".magenta";
			else if (materialName.toLowerCase().contains("orange"))
				path = material.toString().toLowerCase() + ".orange";
			else if (materialName.toLowerCase().contains("bonemeal"))
				path = material.toString().toLowerCase() + ".bonemeal";
		}
		else if (materialName.toLowerCase().contains("waterbottle"))
		{
			material = Material.POTION;
			path = "waterBottle";
		}
		else if (materialName.toLowerCase().contains("skull") || materialName.toLowerCase().contains("head"))
		{
			material = Material.SKULL_ITEM;
			if (materialName.toLowerCase().contains("skeleton"))
				path = material.toString().toLowerCase() + ".skeleton";
			else if (materialName.toLowerCase().contains("wither"))
				path = material.toString().toLowerCase() + ".wither";
			else if (materialName.toLowerCase().contains("zombie"))
				path = material.toString().toLowerCase() + ".zombie";
			else if (materialName.toLowerCase().contains("creeper"))
				path = material.toString().toLowerCase() + ".creeper";
		}
		else
			path = materialName.toString().toLowerCase();
		return path;
	}
	
 	/**
 	 * Uses materialName to find the block/item's damage value.
 	 * 
 	 * @param plugin References the plugin's main class.
 	 * @param materialName The material that is being deposited/withdrawn.
 	 * @return The damage value of the block/item.
 	 */
	public static byte getDamage(ItemBank plugin, String materialName)
	{
		byte damage = 0;
		if (materialName.toLowerCase().contains("wood"))
		{
			if (materialName.toLowerCase().contains("oak"))
				damage = 0;
			else if (materialName.toLowerCase().contains("spruce"))
				damage = 1;
			else if (materialName.toLowerCase().contains("birch"))
				damage = 2;
			else if (materialName.toLowerCase().contains("jungle"))
				damage = 3;
		}
		else if (materialName.toLowerCase().contains("sapling"))
		{
			if (materialName.toLowerCase().contains("oak"))
				damage = 0;
			else if (materialName.toLowerCase().contains("spruce"))
				damage = 1;
			else if (materialName.toLowerCase().contains("birch"))
				damage = 2;
			else if (materialName.toLowerCase().contains("jungle"))
				damage = 3;
		}
		else if (materialName.toLowerCase().contains("log"))
		{
			if (materialName.toLowerCase().contains("oak"))
				damage = 0;
			else if (materialName.toLowerCase().contains("spruce"))
				damage = 1;
			else if (materialName.toLowerCase().contains("birch"))
				damage = 2;
			else if (materialName.toLowerCase().contains("jungle"))
				damage = 3;
		}
		else if (materialName.toLowerCase().contains("leaves"))
		{
			if (materialName.toLowerCase().contains("oak"))
				damage = 0;
			else if (materialName.toLowerCase().contains("spruce"))
				damage = 1;
			else if (materialName.toLowerCase().contains("birch"))
				damage = 2;
			else if (materialName.toLowerCase().contains("jungle"))
				damage = 3;
		}
		else if (materialName.toLowerCase().contains("sandstone"))
		{
			if (materialName.toLowerCase().contains("chiseled"))
				damage = 1;
			else if (materialName.toLowerCase().contains("smooth"))
				damage = 2;
			else
				damage = 0;
		}
		else if (materialName.toLowerCase().contains("longgrass"))
			damage = 1;
		else if (materialName.toLowerCase().contains("fern"))
			damage = 2;
		else if (materialName.toLowerCase().contains("wool"))
		{
			if (materialName.toLowerCase().contains("white"))
				damage = 0;
			else if (materialName.toLowerCase().contains("orange"))
				damage = 1;
			else if (materialName.toLowerCase().contains("magenta"))
				damage = 2;
			else if (materialName.toLowerCase().contains("lightblue"))
				damage = 3;
			else if (materialName.toLowerCase().contains("yellow"))
				damage = 4;
			else if (materialName.toLowerCase().contains("lime"))
				damage = 5;
			else if (materialName.toLowerCase().contains("pink"))
				damage = 6;
			else if (materialName.toLowerCase().contains("gray"))
				damage = 7;
			else if (materialName.toLowerCase().contains("lightgray"))
				damage = 8;
			else if (materialName.toLowerCase().contains("cyan"))
				damage = 9;
			else if (materialName.toLowerCase().contains("purple"))
				damage = 10;
			else if (materialName.toLowerCase().contains("blue"))
				damage = 11;
			else if (materialName.toLowerCase().contains("brown"))
				damage = 12;
			else if (materialName.toLowerCase().contains("green"))
				damage = 13;
			else if (materialName.toLowerCase().contains("red"))
				damage = 14;
			else if (materialName.toLowerCase().contains("black"))
				damage = 15;
		}
		else if (materialName.toLowerCase().contains("slab") || materialName.toLowerCase().contains("step") || materialName.toLowerCase().contains("halfstep"))
		{
			if (materialName.toLowerCase().contains("wood") || materialName.toLowerCase().contains("oak") || materialName.toLowerCase().contains("spruce") || materialName.toLowerCase().contains("birch") || materialName.toLowerCase().contains("jungle"))
			{
				if (materialName.toLowerCase().contains("oak"))
					damage = 0;
				else if (materialName.toLowerCase().contains("spruce"))
					damage = 1;
				else if (materialName.toLowerCase().contains("birch"))
					damage = 3;
				else if (materialName.toLowerCase().contains("jungle"))
					damage = 4;
			}
			else
			{
				if (materialName.toLowerCase().contains("stone") || materialName.toLowerCase().contains("smooth") || materialName.toLowerCase().contains(("smoothstone")))
					damage = 0;
				else if (materialName.toLowerCase().contains("sandstone"))
					damage = 1;
				else if (materialName.toLowerCase().contains("cobble") || materialName.toLowerCase().contains("cobblestone"))
					damage = 3;
				else if (materialName.toLowerCase().contains("brick"))
				{
					if (materialName.toLowerCase().contains("stone"))
						damage = 5;
					else if (materialName.toLowerCase().contains("nether"))
						damage = 6;
					else
						damage = 4;
				}
				else if (materialName.toLowerCase().contains("quartz"))
					damage = 7;
			}
		}
		else if (materialName.toLowerCase().contains("stonebrick"))
		{
			if (materialName.toLowerCase().contains("mossy"))
				damage = 1;
			else if (materialName.toLowerCase().contains("cracked"))
				damage = 2;
			else if (materialName.toLowerCase().contains("chiseled"))
				damage = 3;
			else
				damage = 0;
		}
		else if (materialName.toLowerCase().contains("wall"))
		{
			if (materialName.toLowerCase().contains("mossy"))
				damage = 1;
			else
				damage = 0;
		}
		else if (materialName.toLowerCase().contains("quartz"))
		{
			if (materialName.toLowerCase().contains("block"))
				damage = 0;
			else if (materialName.toLowerCase().contains("chiseled"))
				damage = 1;
			else if (materialName.toLowerCase().contains("column"))
				damage = 2;			
		}
		else if (materialName.toLowerCase().contains("coal"))
		{
			if (materialName.toLowerCase().contains("charcoal"))
				damage = 1;
			else
				damage = 0;
		}
		else if (materialName.toLowerCase().contains("golden"))
		{
			if (materialName.toLowerCase().contains("notched"))
				damage = 1;
			else
				damage = 0;
		}
		else if (materialName.toLowerCase().contains("inksack") || materialName.toLowerCase().contains("rosered") || materialName.toLowerCase().contains("cactusgreen") || materialName.toLowerCase().contains("cocoabeans") || materialName.toLowerCase().contains("lapis") || materialName.toLowerCase().contains("dye") || materialName.toLowerCase().contains("dandelionyellow") || materialName.toLowerCase().contains("bonemeal"))
		{
			if (materialName.toLowerCase().contains("inksack"))
				damage = 0;
			else if (materialName.toLowerCase().contains("rosered"))
				damage = 1;
			else if (materialName.toLowerCase().contains("cactusgreen"))
				damage = 2;
			else if (materialName.toLowerCase().contains("cocoabeans"))
				damage = 3;
			else if (materialName.toLowerCase().contains("lapis"))
				damage = 4;
			else if (materialName.toLowerCase().contains("purple"))
				damage = 5;
			else if (materialName.toLowerCase().contains("cyan"))
				damage = 6;
			else if (materialName.toLowerCase().contains("lightgray"))
				damage = 7;
			else if (materialName.toLowerCase().contains("gray"))
				damage = 8;
			else if (materialName.toLowerCase().contains("pink"))
				damage = 9;
			else if (materialName.toLowerCase().contains("lime"))
				damage = 10;
			else if (materialName.toLowerCase().contains("dandelionyellow"))
				damage = 11;
			else if (materialName.toLowerCase().contains("lightblue"))
				damage = 12;
			else if (materialName.toLowerCase().contains("magenta"))
				damage = 13;
			else if (materialName.toLowerCase().contains("orange"))
				damage = 14;
			else if (materialName.toLowerCase().contains("bonemeal"))
				damage = 15;
		}
		else if (materialName.toLowerCase().contains("waterbottle"))
			damage = 0;
		else if (materialName.toLowerCase().contains("skull") || materialName.toLowerCase().contains("head"))
		{
			if (materialName.toLowerCase().contains("skeleton"))
				damage = 0;
			else if (materialName.toLowerCase().contains("wither"))
				damage = 1;
			else if (materialName.toLowerCase().contains("zombie"))
				damage = 2;
			else if (materialName.toLowerCase().contains("creeper"))
				damage = 4;
		}
		else
			damage = 0;
		return damage;
	}
}

package musician101.itembank.spigot.util;

import au.com.bytecode.opencsv.CSVReader;
import musician101.itembank.spigot.SpigotItemBank;
import musician101.itembank.spigot.config.json.SpigotJSONConfig;
import musician101.itembank.spigot.lib.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class IBUtils
{
	public static void createPlayerFile(File file) throws IOException
	{
		if (!file.exists())
		{
			file.createNewFile();
			BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
			if (file.getName().endsWith(".json"))
				bw.write("{\"_comment\":\"" + Messages.NEW_PLAYER_FILE + "\"}");
			else if (file.getName().endsWith(".csv"))
			{
				bw.write("# " + Messages.NEW_PLAYER_FILE + "\n");
				bw.write("# world|page|slot|material|damage/durability|amount|meta data");
			}
			else
				bw.write("# " + Messages.NEW_PLAYER_FILE);
			
			bw.close();
		}
	}
	
	public static void createPlayerFiles(SpigotItemBank plugin) throws IOException
	{
		Collection<? extends Player> players = plugin.getServer().getOnlinePlayers();
		if (players.size() > 0)
			for (Player player : players)
				createPlayerFile(plugin.getPluginConfig().getPlayerFile(player.getUniqueId()));
	}
	
	public static int getAmount(Inventory inv, Material material, short durability)
	{
		int amount = 0;
		for (ItemStack item : inv.getContents())
			if ((item != null) && (item.getType() == material) && item.getDurability() == durability)
				amount += item.getAmount();
		
		return amount;
	}

    //TODO rework in progress
	/* player.getUniqueId() and uuid are not always the same. */
    @Deprecated
	public static boolean openInv(SpigotItemBank plugin, Player player, World world, UUID uuid, int page)
	{
		Inventory inv;
		try
		{
			inv = IBUtils.getAccount(plugin, world, uuid, page);
		}
		catch (FileNotFoundException e)
		{
			player.sendMessage(Messages.NO_FILE_EX);
			return false;
		}
		catch (IOException e)
		{
			player.sendMessage(Messages.IO_EX);
			return false;
		}
		catch (InvalidConfigurationException | ParseException e)
		{
			player.sendMessage(Messages.YAML_PARSE_EX);
			return false;
		}
		catch (ClassNotFoundException | SQLException e)
		{
			player.sendMessage(Messages.SQL_EX);
			return false;
		}

		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
		if (offlinePlayer.getUniqueId() == uuid && plugin.getEconomy() != null && plugin.getPluginConfig().enableVault())
		{
			if (plugin.getEconomy() != null && !plugin.getEconomy().withdrawPlayer(offlinePlayer, plugin.getPluginConfig().getTransactionCost()).transactionSuccess())
			{
				player.sendMessage(Messages.ACCOUNT_ECON_FAIL);
				return false;
			}

			player.sendMessage(Messages.ACCOUNT_ECON_SUCCESS.replace("$", "$" + plugin.getPluginConfig().getTransactionCost()));
		}

		new AccountPage(plugin, player, uuid, world, page);
		player.openInventory(inv);
		return true;
	}

    //TODO rework in progress
    @Deprecated
	public static Inventory getAccount(SpigotItemBank plugin, World world, UUID uuid, int page) throws ClassNotFoundException, IOException, InvalidConfigurationException, ParseException, SQLException
	{
        //TODO rework in progress
        @Deprecated
		String worldName = world.getName();
		final Inventory inv = Bukkit.createInventory(Bukkit.getPlayer(uuid), 54, Bukkit.getOfflinePlayer(uuid).getName() + " - Page " + page);
		if (plugin.getPluginConfig().useMySQL())
		{
			plugin.getMySQLHandler().querySQL("CREATE TABLE IF NOT EXISTS ib_" + uuid.toString().replace("-", "_") + "(World varchar(255), Page int, Slot int, Material varchar(255), Damage int, Amount int, ItemMeta varchar(300));");
			for (int slot = 0; slot < inv.getSize(); slot++)
				inv.setItem(slot, getItem(plugin.getMySQLHandler().querySQL("SELECT * FROM ib_" + uuid + " WHERE World = \"" + worldName + "\" AND Page = " + page + " AND Slot = " + slot + ";")));

			return inv;
		}

		File file = plugin.getPluginConfig().getPlayerFile(uuid);
		if (file.exists())
			createPlayerFile(file);

		if (file.getName().endsWith(".csv"))
		{
			for (String[] s : new CSVReader(new FileReader(file), '|').readAll())
			{
				if (!s[0].startsWith("#"))
				{
					if (s[0].equals(worldName))
					{
						if (Integer.valueOf(s[1]) == page)
						{
							ItemStack item = new ItemStack(Material.getMaterial(s[3]), Integer.valueOf(s[5]), Short.valueOf(s[4]));
							item.setItemMeta(SpigotJSONConfig.loadSpigotJSONConfig(s[6]).toItemMeta());
							inv.setItem(Integer.valueOf(s[2]), item);
						}
					}
				}
			}

			return inv;
		}
		else if (file.getName().endsWith("json"))
		{
			SpigotJSONConfig account = SpigotJSONConfig.loadSpigotJSONConfig(file);

			if (!account.containsKey(worldName))
				return inv;

			SpigotJSONConfig world = account.getSpigotJSONConfig(worldName);
			if (!world.containsKey(page + ""))
				return inv;

			SpigotJSONConfig pg = world.getSpigotJSONConfig(page + "");
			for (int slot = 0; slot < inv.getSize(); slot++)
				if (pg.containsKey(slot + ""))
					inv.setItem(slot, pg.getItemStack(slot + ""));

			return inv;
		}

		YamlConfiguration account = new YamlConfiguration();
		account.load(file);
		for (int slot = 0; slot < inv.getSize(); slot++)
			inv.setItem(slot, account.getItemStack(worldName + "." + page + "." + slot));

		return inv;
	}

	public static void saveAccount(SpigotItemBank plugin, String worldName, UUID uuid, Inventory inventory, int page) throws ClassNotFoundException, IOException, InvalidConfigurationException, ParseException, SQLException
	{
		if (plugin.getPluginConfig().useMySQL())
		{
			plugin.getMySQLHandler().querySQL("CREATE TABLE IF NOT EXISTS ib_" + uuid.toString().replace("-", "_") + "(World varchar(255), Page int, Slot int, Material varchar(255), Damage int, Amount int, ItemMeta varchar(300));");
			for (int slot = 0; slot < inventory.getSize(); slot++)
			{
				plugin.getMySQLHandler().querySQL("DELETE FROM ib_" + uuid + " WHERE World = \"" + worldName + "\" AND Page = " + page + " AND Slot = " + slot + ";");
				ItemStack item = inventory.getItem(slot);
				if (item != null)
					plugin.getMySQLHandler().updateSQL("INSERT INTO ib_" + uuid + "(World, Page, Slot, Material, Damage, Amount, ItemMeta) VALUES (\"" + worldName + "\", " + page + ", " + slot + ", \"" + item.getType().toString() + "\", " + item.getDurability() + ", " + item.getAmount() + ", \""+ metaToJson(item).replace("\"", "\\\"") + "\");");
			}
			
			return;
		}

		File file = plugin.getPluginConfig().getPlayerFile(uuid);
		if (file.getName().endsWith("csv"))
		{
			List<String> account = new ArrayList<>();
			for (String[] s : new CSVReader(new FileReader(file), '|').readAll())
			{
				if (!s[0].startsWith("#"))
				{
					if (!worldName.equals(s[0]))
						if (Integer.valueOf(s[1]) != page)
							for (int slot = 0; slot < inventory.getSize(); slot++)
								if (Integer.valueOf(s[2]) != slot)
									account.add(Arrays.toString(s));
				}
				else
					account.add(Arrays.toString(s).replace("[", "").replace("]", ""));
			}	
					
			for (int slot = 0; slot < inventory.getSize(); slot++)
			{
				ItemStack item = inventory.getItem(slot);
				if (item != null)
					account.add(worldName + "|" + page + "|" + slot + "|" + item.getType().toString() + "|" + item.getDurability() + "|" + item.getAmount() + "|" + metaToJson(item));
			}
			
			file.delete();
			createPlayerFile(file);
			BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
			for (String line : account)
				bw.write(line + "\n");
			
			bw.close();
			return;
		}
		else if (file.getName().endsWith("json"))
		{
			SpigotJSONConfig account = SpigotJSONConfig.loadSpigotJSONConfig(file);
			SpigotJSONConfig pg = new SpigotJSONConfig();
			pg.setInventory(page + "", inventory);
			if (account == null)
				account = new SpigotJSONConfig();
			
			account.setSpigotJSONConfig(worldName, pg);
			FileWriter fw = new FileWriter(file);
			fw.write(account.toJSONString());
			fw.close();
			return;
		}
		
		YamlConfiguration account = new YamlConfiguration();
		account.load(file);
		for (int slot = 0; slot < inventory.getSize(); slot++)
			account.set(worldName + "." + page + "." + slot, inventory.getItem(slot));	
		
		account.save(file);
	}
	
	public static boolean isNumber(String s)
	{
		try
		{
			Integer.valueOf(s);
		}
		catch (NumberFormatException e)
		{
			return false;
		}
		
		return true;
	}
	
	public static void sendMessages(CommandSender sender, List<String> messages)
	{
		messages.forEach(sender::sendMessage);
	}
	
	private static String metaToJson(ItemStack is)
	{
		SpigotJSONConfig meta = new SpigotJSONConfig();
		meta.setItemMeta("meta", is.getItemMeta(), is.getType());
		return meta.getSpigotJSONConfig("meta").toJSONString();
	}
	
	private static ItemStack getItem(ResultSet rs) throws ParseException, SQLException
	{
		ItemStack item = new ItemStack(Material.getMaterial(rs.getString("Material")), rs.getInt("Amount"), (short) rs.getInt("Damage"));
		item.setItemMeta(SpigotJSONConfig.loadSpigotJSONConfig(rs.getString("ItemMeta")).toItemMeta());
		return item;
	}

	public static String getWorldName(SpigotItemBank plugin, Player player)
	{
		if (plugin.getPluginConfig().isMultiWorldStorageEnabled())
			return Bukkit.getWorlds().get(0).getName();
		
		return player.getWorld().getName();
	}

    //TODO need to be moved to common library
    @Deprecated
	public static UUID getUUIDOf(String name) throws InterruptedException, IOException, ParseException
	{
		return getUUIDs(Collections.singletonList(name)).get(name.toLowerCase());
	}

    //TODO need to be moved to common library
    @Deprecated
	public static Map<String, UUID> getUUIDs(List<String> names) throws InterruptedException, IOException, ParseException
	{
		Map<String, UUID> uuidMap = new HashMap<>();
		int requests = (int) Math.ceil(names.size() / 100);
		for (int i = 0; i < requests; i++)
		{
			URL url = new URL("https://api.mojang.com/profiles/minecraft");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			String body = JSONArray.toJSONString(names.subList(i * 100, Math.min((i + 1) * 100, names.size())));
			OutputStream stream = connection.getOutputStream();
			stream.write(body.getBytes());
			stream.flush();
			stream.close();
			JSONArray array = (JSONArray) new JSONParser().parse(new InputStreamReader(connection.getInputStream()));
			for (Object profile : array)
			{
				JSONObject jsonProfile = (JSONObject) profile;
				String id = jsonProfile.get("id").toString();
				String name = jsonProfile.get("name").toString();
				UUID uuid = UUID.fromString(id.substring(0, 8) + "-" + id.substring(8, 12) + "-" + id.substring(12, 16) + "-" + id.substring(16, 20) + "-" + id.substring(20, 32));
				uuidMap.put(name.toLowerCase(), uuid);
			}
			
			if (i != requests - 1)
				Thread.sleep(100L);
		}
		
		return uuidMap;
	}
}

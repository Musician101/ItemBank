package musician101.itembank.spigot.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import musician101.itembank.spigot.ItemBank;
import musician101.itembank.spigot.config.json.SpigotJSONConfig;
import musician101.itembank.spigot.config.yaml.CustomYamlConfig;
import musician101.itembank.spigot.lib.Messages;

import org.apache.commons.lang.text.StrBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import au.com.bytecode.opencsv.CSVReader;

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
	
	public static void createPlayerFiles(ItemBank plugin) throws IOException
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
	
	/* player.getUniqueId() and uuid are not always the same. */
	public static boolean openInv(ItemBank plugin, Player player, String worldName, UUID uuid, int page)
	{
		Inventory inv = null;
		try
		{
			inv = IBUtils.getAccount(plugin, worldName, uuid, page);
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
		
		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(((Player) player).getUniqueId());
		if (offlinePlayer.getUniqueId().toString().equals(uuid) && plugin.getEconomy() != null && plugin.getPluginConfig().enableVault())
		{
			if (plugin.getEconomy() != null && !plugin.getEconomy().withdrawPlayer(offlinePlayer, plugin.getPluginConfig().getTransactionCost()).transactionSuccess())
			{
				player.sendMessage(Messages.ACCOUNT_ECON_FAIL);
				return false;
			}
			
			player.sendMessage(Messages.ACCOUNT_ECON_SUCCESS.replace("$", "$" + plugin.getPluginConfig().getTransactionCost()));
		}
		
		new AccountPage(plugin, player, uuid, worldName, page);
		player.openInventory(inv);
		return true;
	}
	
	public static Inventory getAccount(ItemBank plugin, String worldName, UUID uuid, int page) throws ClassNotFoundException, FileNotFoundException, IOException, InvalidConfigurationException, ParseException, SQLException
	{
		final Inventory inv = Bukkit.createInventory(Bukkit.getPlayer(uuid), 54, Bukkit.getOfflinePlayer(uuid).getName() + " - Page " + page);
		if (plugin.getPluginConfig().useMySQL())
		{
			plugin.getMySQLHandler().querySQL("CREATE TABLE IF NOT EXISTS ib_" + uuid.toString().replace("-", "_") + "(World varchar(255), Page int, Slot int, Material varchar(255), Damage int, Amount int, ItemMeta varchar(300));");
			for (int slot = 0; slot < inv.getSize(); slot++)
				inv.setItem(slot, getItem(plugin.getMySQLHandler().querySQL("SELECT * FROM ib_" + uuid + " WHERE World = \"" + worldName + "\" AND Page = " + page + " AND Slot = " + slot + ";")));
			
			return inv;
		}
		
		File file = plugin.getPluginConfig().getPlayerFile(uuid);
		if (inv != null)
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
	
	public static void saveAccount(ItemBank plugin, String worldName, UUID uuid, Inventory inventory, int page) throws ClassNotFoundException, FileNotFoundException, IOException, InvalidConfigurationException, ParseException, SQLException
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
			List<String> account = new ArrayList<String>();
			for (String[] s : new CSVReader(new FileReader(file), '|').readAll())
			{
				if (!s[0].startsWith("#"))
				{
					if (!worldName.equals(s[0]))
						if (Integer.valueOf(s[1]) != page)
							for (int slot = 0; slot < inventory.getSize(); slot++)
								if (Integer.valueOf(s[2]) != slot)
									account.add(s.toString());
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
		for (String message : messages)
			sender.sendMessage(message);
	}
	
	private static String metaToJson(ItemStack is)
	{
		SpigotJSONConfig meta = new SpigotJSONConfig();
		meta.setItemMeta("meta", is.getItemMeta(), is.getType());
		return meta.getSpigotJSONConfig("meta").toJSONString();
	}
	
	private static ItemStack getItem(ResultSet rs) throws ParseException, SQLException
	{
		while (rs.next())
		{
			ItemStack item = new ItemStack(Material.getMaterial(rs.getString("Material")), rs.getInt("Amount"), (short) rs.getInt("Damage"));
			item.setItemMeta(SpigotJSONConfig.loadSpigotJSONConfig(rs.getString("ItemMeta")).toItemMeta());
			return item;
		}
		
		return null;
	}

	public static String getWorldName(ItemBank plugin, Player player)
	{
		if (plugin.getPluginConfig().isMultiWorldStorageEnabled())
			return Bukkit.getWorlds().get(0).getName();
		
		return player.getWorld().getName();
	}
	
	public static FileConfiguration getYamlConfig(File file, boolean forceEncode) throws IOException, InvalidConfigurationException
	{
		CustomYamlConfig config = new CustomYamlConfig();
		InputStreamReader reader = null;
		BufferedReader br = null;
		try
		{
			reader = forceEncode ? new InputStreamReader(new FileInputStream(file), "UTF-8") : new InputStreamReader(new FileInputStream(file));
			br = new BufferedReader(reader);
			StrBuilder builder = new StrBuilder();
			String line = null;
			while ((line = br.readLine()) != null)
				builder.appendln(line);
			
			config.loadFromString(builder.toString());
		}
		finally
		{
			if (br != null)
				br.close();
			
			if (reader != null)
				reader.close();
		}
		
		return config;
	}
	
	public static UUID getUUIDOf(String name) throws InterruptedException, IOException, ParseException
	{
		return getUUIDs(Arrays.asList(name)).get(name.toLowerCase());
	}
	
	public static Map<String, UUID> getUUIDs(List<String> names) throws InterruptedException, IOException, ParseException
	{
		Map<String, UUID> uuidMap = new HashMap<String, UUID>();
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
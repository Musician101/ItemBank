package musician101.sponge.itembank.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.spongepowered.api.data.DataManipulator;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

public class IBUtils
{
	public static void createPlayerFile(File file) throws IOException
	{
		if (!file.exists())
			file.createNewFile();
	}
	
	public static int getAmount(Inventory inv, ItemType type, DataManipulator<?> data)
	{
		int amount = 0;
		for (Inventory slot : inv.query(type))
		{
			ItemStack item = slot.peek().get();
			if ((item != null) && (item.getItem() == type) && item.getManipulators().contains(data))
				amount += item.getQuantity();
		}
		
		return amount;
	}
	
	public static boolean isNumber(String s)
	{
		try
		{
			Integer.parseInt(s);
		}
		catch (NumberFormatException e)
		{
			return false;
		}
		
		return true;
	}
	
	public static Text stringToText(String string)
	{
		return stringToText(string, TextColors.WHITE);
	}
	
	public static Text stringToText(String string, TextColor color)
	{
		return Texts.builder(string).color(color).build();
	}
	
	public static Text joinTexts(Text... texts)
	{
		return Texts.builder().append(texts).build();
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

	//TODO move to comon library
	@Deprecated
	public static UUID getUUIDOf(String name) throws InterruptedException, IOException, ParseException
	{
		return getUUIDs(Arrays.asList(name)).get(name.toLowerCase());
	}

	//TODO move to comon library
	public static Map<UUID, String> getNames(List<UUID> uuids) throws IOException, MalformedURLException, ParseException
	{
		String PROFILE_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";
		JSONParser parser = new JSONParser();
        Map<UUID, String> uuidStringMap = new HashMap<UUID, String>();
        for (UUID uuid: uuids)
        {
            HttpURLConnection connection = (HttpURLConnection) new URL(PROFILE_URL+uuid.toString().replace("-", "")).openConnection();
            JSONObject response = (JSONObject) parser.parse(new InputStreamReader(connection.getInputStream()));
            String name = (String) response.get("name");
            if (name == null)
                continue;
            
            String cause = (String) response.get("cause");
            String errorMessage = (String) response.get("errorMessage");
            if (cause != null && cause.length() > 0)
                throw new IllegalStateException(errorMessage);
            
            uuidStringMap.put(uuid, name);
        }
        
        return uuidStringMap;
    }
	
	public static String getNameOf(UUID uuid) throws MalformedURLException, IOException, ParseException
	{
		return getNames(Arrays.asList(uuid)).get(uuid);
	}
}

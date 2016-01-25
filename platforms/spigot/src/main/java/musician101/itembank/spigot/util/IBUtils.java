package musician101.itembank.spigot.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import musician101.itembank.spigot.SpigotItemBank;
import musician101.itembank.spigot.lib.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class IBUtils
{
    //TODO move to common library and rewrite to work like the one in Sponge module
    @Deprecated
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

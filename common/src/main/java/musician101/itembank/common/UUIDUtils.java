package musician101.itembank.common;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

//TODO move to common library
@Deprecated
public class UUIDUtils
{
    //TODO move to common library
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

    //TODO move to common library
    @Deprecated
    public static UUID getUUIDOf(String name) throws InterruptedException, IOException, ParseException
    {
        return getUUIDs(Collections.singletonList(name)).get(name.toLowerCase());
    }

    //TODO move to common library
    @Deprecated
    public static Map<UUID, String> getNames(List<UUID> uuids) throws IOException, ParseException
    {
        String PROFILE_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";
        JSONParser parser = new JSONParser();
        Map<UUID, String> uuidStringMap = new HashMap<>();
        for (UUID uuid : uuids)
        {
            HttpURLConnection connection = (HttpURLConnection) new URL(PROFILE_URL + uuid.toString().replace("-", "")).openConnection();
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

    //TODO move to common library
    @Deprecated
    public static String getNameOf(UUID uuid) throws IOException, ParseException
    {
        return getNames(Collections.singletonList(uuid)).get(uuid);
    }
}

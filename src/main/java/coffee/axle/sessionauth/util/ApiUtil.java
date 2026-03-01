package coffee.axle.sessionauth.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

public class ApiUtil {
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final String PROFILE_URL = "https://api.minecraftservices.com/minecraft/profile";
    private static final String NAME_URL = "https://api.minecraftservices.com/minecraft/profile/name/";
    private static final String SKIN_URL = "https://api.minecraftservices.com/minecraft/profile/skins";

    public static String[] getProfileInfo(String token) throws IOException {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(PROFILE_URL))
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
            String name = json.get("name").getAsString();
            String id = json.get("id").getAsString();
            return new String[] { name, id };
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Request interrupted", e);
        } catch (Exception e) {
            throw new IOException("Failed to get profile info", e);
        }
    }

    public static boolean validateSession(String token) {
        try {
            String[] info = getProfileInfo(token);
            String name = info[0];
            String uuidString = info[1];

            if (uuidString.length() == 32) {
                uuidString = uuidString.substring(0, 8) + "-"
                        + uuidString.substring(8, 12) + "-"
                        + uuidString.substring(12, 16) + "-"
                        + uuidString.substring(16, 20) + "-"
                        + uuidString.substring(20);
            }

            UUID uuid = UUID.fromString(uuidString);
            return name.equals(MinecraftClient.getInstance().getSession().getUsername())
                    && uuid.equals(MinecraftClient.getInstance().getSession().getUuidOrNull());
        } catch (Exception e) {
            return false;
        }
    }

    public static int changeName(String newName, String token) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(NAME_URL + newName))
                    .header("Authorization", "Bearer " + token)
                    .PUT(HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return -1;
        } catch (Exception e) {
            return -1;
        }
    }

    public static int changeSkin(String url, String token) {
        try {
            String json = """
                    {"variant":"classic","url":"%s"}""".formatted(url);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(SKIN_URL))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return -1;
        } catch (Exception e) {
            return -1;
        }
    }
}

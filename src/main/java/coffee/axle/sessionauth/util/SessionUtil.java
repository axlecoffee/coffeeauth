package coffee.axle.sessionauth.util;

import coffee.axle.sessionauth.CoffeeAuth;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.session.Session;

import java.util.Optional;
import java.util.UUID;

public class SessionUtil {

    public static String getUsername() {
        return MinecraftClient.getInstance().getSession().getUsername();
    }

    public static Session createSession(String username, String uuidString, String token) {
        if (uuidString.length() == 32) {
            uuidString = uuidString.substring(0, 8) + "-"
                    + uuidString.substring(8, 12) + "-"
                    + uuidString.substring(12, 16) + "-"
                    + uuidString.substring(16, 20) + "-"
                    + uuidString.substring(20);
        }
        return new Session(
                username,
                UUID.fromString(uuidString),
                token,
                Optional.empty(),
                Optional.empty());
    }

    public static Session createSession(String username, UUID uuid, String token) {
        return new Session(
                username,
                uuid,
                token,
                Optional.empty(),
                Optional.empty());
    }

    public static void setSession(Session session) {
        CoffeeAuth.currentSession = session;
        updateWindowTitle();
    }

    public static void restoreSession() {
        CoffeeAuth.currentSession = CoffeeAuth.originalSession;
        updateWindowTitle();
    }

    public static void updateWindowTitle() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.getWindow() != null) {
            String username = client.getSession().getUsername();
            client.getWindow().setTitle("CoffeeAuth v" + CoffeeAuth.MOD_VERSION + " | " + username);
        }
    }
}

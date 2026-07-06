package coffee.axle.sessionauth.util;

import coffee.axle.sessionauth.CoffeeAuth;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;

import java.util.Optional;
import java.util.UUID;

public class SessionUtil {

	public static String getUsername() {
		return Minecraft.getInstance().getUser().getName();
	}

	public static User createUser(String username, String uuidString, String token) {
		if (uuidString.length() == 32) {
			uuidString = uuidString.substring(0, 8) + "-"
					+ uuidString.substring(8, 12) + "-"
					+ uuidString.substring(12, 16) + "-"
					+ uuidString.substring(16, 20) + "-"
					+ uuidString.substring(20);
		}
		return new User(
				username,
				UUID.fromString(uuidString),
				token,
				Optional.empty(),
				Optional.empty());
	}

	public static User createUser(String username, UUID uuid, String token) {
		return new User(
				username,
				uuid,
				token,
				Optional.empty(),
				Optional.empty());
	}

	public static void setSession(User user) {
		CoffeeAuth.currentUser = user;
		refreshWindowTitle();
	}

	public static void restoreSession() {
		CoffeeAuth.currentUser = CoffeeAuth.originalUser;
		refreshWindowTitle();
	}

	public static void refreshWindowTitle() {
		Minecraft client = Minecraft.getInstance();
		if (client.getWindow() != null) {
			client.updateTitle();
		}
	}
}

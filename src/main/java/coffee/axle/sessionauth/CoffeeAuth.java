package coffee.axle.sessionauth;

import coffee.axle.sessionauth.util.SessionUtil;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoffeeAuth implements ClientModInitializer {
	public static final String MOD_ID = "coffeeauth";
	public static final String MOD_VERSION = "1.0.0";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static User originalUser;
	public static User currentUser;
	public static boolean overrideSession = false;

	public static boolean isSessionModified() {
		return currentUser != null && !currentUser.equals(originalUser);
	}

	@Override
	public void onInitializeClient() {
		originalUser = Minecraft.getInstance().getUser();
		currentUser = originalUser;
		overrideSession = true;

		SessionUtil.refreshWindowTitle();
		LOGGER.info("CoffeeAuth v{} initialized", MOD_VERSION);
	}
}

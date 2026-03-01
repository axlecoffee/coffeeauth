package coffee.axle.sessionauth;

import coffee.axle.sessionauth.util.SessionUtil;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoffeeAuth implements ClientModInitializer {
	public static final String MOD_ID = "coffeeauth";
	public static final String MOD_VERSION = "1.0.0";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Session originalSession;
	public static Session currentSession;
	public static boolean overrideSession = false;

	public static boolean isSessionModified() {
		return currentSession != null && !currentSession.equals(originalSession);
	}

	@Override
	public void onInitializeClient() {
		originalSession = MinecraftClient.getInstance().getSession();
		currentSession = originalSession;
		overrideSession = true;

		SessionUtil.updateWindowTitle();
		LOGGER.info("CoffeeAuth v{} initialized", MOD_VERSION);
	}
}
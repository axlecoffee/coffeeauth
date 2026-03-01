package coffee.axle.sessionauth.mixin;

import coffee.axle.sessionauth.screen.EditAccountScreen;
import coffee.axle.sessionauth.screen.LoginScreen;
import coffee.axle.sessionauth.util.ApiUtil;
import coffee.axle.sessionauth.util.SessionUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerScreen.class)
public abstract class MultiplayerScreenMixin extends Screen {

    @Unique
    private static Boolean isSessionValid = null;

    @Unique
    private static boolean hasValidationStarted = false;

    protected MultiplayerScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        isSessionValid = null;
        hasValidationStarted = false;

        int y = 5;
        int loginX = 5;
        int editX = 90;
        int restoreX = 175;

        this.addDrawableChild(ButtonWidget
                .builder(Text.literal("Login"), button -> MinecraftClient.getInstance().setScreen(new LoginScreen()))
                .dimensions(loginX, y, 80, 20).build());

        this.addDrawableChild(ButtonWidget
                .builder(Text.literal("Edit Account"),
                        button -> MinecraftClient.getInstance().setScreen(new EditAccountScreen()))
                .dimensions(editX, y, 80, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Restore"), button -> {
            SessionUtil.restoreSession();
            isSessionValid = null;
            hasValidationStarted = false;
        }).dimensions(restoreX, y, 80, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        String username = SessionUtil.getUsername();

        if (isSessionValid == null && !hasValidationStarted) {
            hasValidationStarted = true;
            new Thread(() -> {
                isSessionValid = ApiUtil.validateSession(
                        MinecraftClient.getInstance().getSession().getAccessToken());
            }, "CoffeeAuth-Validation").start();
        }

        Text statusText;
        if (isSessionValid == null) {
            statusText = Text.literal("Validating...").formatted(Formatting.GRAY);
        } else if (isSessionValid) {
            statusText = Text.literal("Valid").formatted(Formatting.GREEN);
        } else {
            statusText = Text.literal("Invalid").formatted(Formatting.RED);
        }

        Text display = Text.literal("User: ")
                .append(Text.literal(username).formatted(Formatting.WHITE))
                .append(Text.literal(" | ").formatted(Formatting.DARK_GRAY))
                .append(statusText);

        context.drawTextWithShadow(this.textRenderer, display, 5, 30, 0xFFFFFFFF);
    }
}

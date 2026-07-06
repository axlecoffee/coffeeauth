package coffee.axle.sessionauth.mixin;

import coffee.axle.sessionauth.screen.EditAccountScreen;
import coffee.axle.sessionauth.screen.LoginScreen;
import coffee.axle.sessionauth.util.ApiUtil;
import coffee.axle.sessionauth.util.SessionUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.ChatFormatting;
//? if <26 {
import net.minecraft.client.gui.GuiGraphics;
//?} else {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
*///?}
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(JoinMultiplayerScreen.class)
public abstract class MultiplayerScreenMixin extends Screen {

    @Unique
    private static Boolean isSessionValid = null;

    @Unique
    private static boolean hasValidationStarted = false;

    @Unique
    private Button loginButton;

    @Unique
    private Button editButton;

    @Unique
    private Button restoreButton;

    @Unique
    private StatusTextWidget statusTextWidget;

    protected MultiplayerScreenMixin(Component title) {
        super(title);
    }

    @Unique
    private void rebuildButtons() {
        int y = 5;
        int loginX = 5;
        int editX = 90;
        int restoreX = 175;

        loginButton = Button
                .builder(Component.literal("Login"), button -> {
                    //? if <26.2 {
                    Minecraft.getInstance().setScreen(new LoginScreen());
                    //?} else {
                    /*Minecraft.getInstance().gui.setScreen(new LoginScreen());
                    *///?}
                })
                .bounds(loginX, y, 80, 20).build();

        editButton = Button
                .builder(Component.literal("Edit Account"), button -> {
                    //? if <26.2 {
                    Minecraft.getInstance().setScreen(new EditAccountScreen());
                    //?} else {
                    /*Minecraft.getInstance().gui.setScreen(new EditAccountScreen());
                    *///?}
                })
                .bounds(editX, y, 80, 20).build();

        restoreButton = Button.builder(Component.literal("Restore"), button -> {
            SessionUtil.restoreSession();
            isSessionValid = null;
            hasValidationStarted = false;
        }).bounds(restoreX, y, 80, 20).build();

        statusTextWidget = new StatusTextWidget(5, 30);
    }

    @Unique
    private void addWidgetsIfMissing() {
        if (!this.children().contains(loginButton)) {
            this.addRenderableWidget(loginButton);
        }
        if (!this.children().contains(editButton)) {
            this.addRenderableWidget(editButton);
        }
        if (!this.children().contains(restoreButton)) {
            this.addRenderableWidget(restoreButton);
        }
        if (!this.children().contains(statusTextWidget)) {
            this.addRenderableOnly(statusTextWidget);
        }
    }

    @Inject(method = "repositionElements()V", at = @At("TAIL"))
    private void repositionAuthButtons(CallbackInfo ci) {
        if (loginButton == null) {
            isSessionValid = null;
            hasValidationStarted = false;
            rebuildButtons();
        } else {
            loginButton.setPosition(5, 5);
            editButton.setPosition(90, 5);
            restoreButton.setPosition(175, 5);
        }
        addWidgetsIfMissing();
    }

    @Unique
    private Component buildStatusText(String username) {
        if (isSessionValid == null) {
            return Component.literal("User: ")
                    .append(Component.literal(username).withStyle(ChatFormatting.WHITE))
                    .append(Component.literal(" | ").withStyle(ChatFormatting.DARK_GRAY))
                    .append(Component.literal("Validating...").withStyle(ChatFormatting.GRAY));
        } else if (isSessionValid) {
            return Component.literal("User: ")
                    .append(Component.literal(username).withStyle(ChatFormatting.WHITE))
                    .append(Component.literal(" | ").withStyle(ChatFormatting.DARK_GRAY))
                    .append(Component.literal("Valid").withStyle(ChatFormatting.GREEN));
        } else {
            return Component.literal("User: ")
                    .append(Component.literal(username).withStyle(ChatFormatting.WHITE))
                    .append(Component.literal(" | ").withStyle(ChatFormatting.DARK_GRAY))
                    .append(Component.literal("Invalid").withStyle(ChatFormatting.RED));
        }
    }

    @Unique
    private void startValidationIfNeeded() {
        if (isSessionValid == null && !hasValidationStarted) {
            hasValidationStarted = true;
            new Thread(() -> {
                isSessionValid = ApiUtil.validateSession(
                        Minecraft.getInstance().getUser().getAccessToken());
            }, "CoffeeAuth-Validation").start();
        }
    }

    //? if <26 {
    @Unique
    private class StatusTextWidget implements net.minecraft.client.gui.components.Renderable {
        private final int x;
        private final int y;

        StatusTextWidget(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
            startValidationIfNeeded();
            context.drawString(MultiplayerScreenMixin.this.font,
                    buildStatusText(SessionUtil.getUsername()), x, y, 0xFFFFFFFF);
        }
    }
    //?} else {
    /*@Unique
    private class StatusTextWidget implements net.minecraft.client.gui.components.Renderable {
        private final int x;
        private final int y;

        StatusTextWidget(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
            startValidationIfNeeded();
            context.text(MultiplayerScreenMixin.this.font,
                    buildStatusText(SessionUtil.getUsername()), x, y, 0xFFFFFFFF);
        }
    }
    *///?}
}

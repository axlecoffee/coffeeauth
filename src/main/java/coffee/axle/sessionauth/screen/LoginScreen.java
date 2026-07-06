package coffee.axle.sessionauth.screen;

import coffee.axle.sessionauth.CoffeeAuth;
import coffee.axle.sessionauth.util.ApiUtil;
import coffee.axle.sessionauth.util.SessionUtil;
import net.minecraft.ChatFormatting;
//? if <26 {
import net.minecraft.client.gui.GuiGraphics;
//?} else {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
*///?}
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;

import java.io.IOException;

public class LoginScreen extends Screen {
    private EditBox sessionField;
    private Button loginButton;
    private Button restoreButton;
    private Component currentTitle;

    public LoginScreen() {
        super(Component.literal(""));
        this.currentTitle = Component.literal("Input Session ID").withStyle(ChatFormatting.GOLD);
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int cy = this.height / 2;

        sessionField = new EditBox(
                this.font, cx - 100, cy, 200, 20,
                Component.literal("Session Input"));
        sessionField.setMaxLength(32767);
        sessionField.setValue("");
        sessionField.setFocused(true);
        this.addWidget(sessionField);

        loginButton = Button.builder(Component.literal("Login"), button -> {
            String token = sessionField.getValue().trim();
            if (token.isEmpty()) {
                currentTitle = Component.literal("Session ID cannot be empty").withStyle(ChatFormatting.RED);
                return;
            }
            new Thread(() -> {
                try {
                    String[] info = ApiUtil.getProfileInfo(token);
                    SessionUtil.setSession(SessionUtil.createUser(info[0], info[1], token));
                    currentTitle = Component.literal("Logged in as: " + info[0]).withStyle(ChatFormatting.GREEN);
                    restoreButton.active = true;
                } catch (IOException | RuntimeException e) {
                    currentTitle = Component.literal("Invalid Session ID").withStyle(ChatFormatting.RED);
                }
            }, "CoffeeAuth-Login").start();
        }).bounds(cx - 100, cy + 25, 97, 20).build();
        this.addRenderableWidget(loginButton);

        restoreButton = Button.builder(Component.literal("Restore"), button -> {
            SessionUtil.restoreSession();
            currentTitle = Component.literal("Restored original session").withStyle(ChatFormatting.GREEN);
            loginButton.active = true;
            restoreButton.active = false;
        }).bounds(cx + 3, cy + 25, 97, 20).build();
        this.addRenderableWidget(restoreButton);

        Button backButton = Button.builder(Component.literal("Back"), button -> {
            assert this.minecraft != null;
            //? if <26.2 {
            this.minecraft.setScreen(new JoinMultiplayerScreen(new TitleScreen()));
            //?} else {
            /*this.minecraft.gui.setScreen(new JoinMultiplayerScreen(new TitleScreen()));
            *///?}
        }).bounds(cx - 100, cy + 50, 200, 20).build();
        this.addRenderableWidget(backButton);

        if (CoffeeAuth.currentUser.equals(CoffeeAuth.originalUser)) {
            restoreButton.active = false;
        }
    }

    //? if <26 {
    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        sessionField.render(context, mouseX, mouseY, delta);
        context.drawCenteredString(this.font, this.currentTitle,
                this.width / 2, this.height / 2 - 30, 0xFFFFFFFF);
    }
    //?} else {
    /*@Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        super.extractRenderState(context, mouseX, mouseY, delta);
        sessionField.extractWidgetRenderState(context, mouseX, mouseY, delta);
        context.centeredText(this.font, this.currentTitle,
                this.width / 2, this.height / 2 - 30, 0xFFFFFFFF);
    }
    *///?}

    @Override
    public boolean keyPressed(KeyEvent keyEvent) {
        if (sessionField.keyPressed(keyEvent) || sessionField.isActive()) {
            return true;
        }
        return super.keyPressed(keyEvent);
    }

    @Override
    public boolean charTyped(CharacterEvent characterEvent) {
        if (sessionField.charTyped(characterEvent))
            return true;
        return super.charTyped(characterEvent);
    }
}

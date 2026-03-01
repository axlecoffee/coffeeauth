package coffee.axle.sessionauth.screen;

import coffee.axle.sessionauth.CoffeeAuth;
import coffee.axle.sessionauth.util.ApiUtil;
import coffee.axle.sessionauth.util.FormatUtil;
import coffee.axle.sessionauth.util.SessionUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.IOException;

public class LoginScreen extends Screen {
    private TextFieldWidget sessionField;
    private ButtonWidget loginButton;
    private ButtonWidget restoreButton;
    private Text currentTitle;

    public LoginScreen() {
        super(Text.literal(""));
        this.currentTitle = FormatUtil.surroundWithObfuscated(
                Text.literal("Input Session ID").formatted(Formatting.GOLD), 5);
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int cy = this.height / 2;

        sessionField = new TextFieldWidget(
                this.textRenderer, cx - 100, cy, 200, 20,
                Text.literal("Session Input"));
        sessionField.setMaxLength(32767);
        sessionField.setText("");
        sessionField.setFocused(true);
        this.addSelectableChild(sessionField);

        loginButton = ButtonWidget.builder(Text.literal("Login"), button -> {
            String token = sessionField.getText().trim();
            if (token.isEmpty()) {
                currentTitle = FormatUtil.surroundWithObfuscated(
                        Text.literal("Session ID cannot be empty").formatted(Formatting.RED), 5);
                return;
            }
            new Thread(() -> {
                try {
                    String[] info = ApiUtil.getProfileInfo(token);
                    SessionUtil.setSession(SessionUtil.createSession(info[0], info[1], token));
                    currentTitle = FormatUtil.surroundWithObfuscated(
                            Text.literal("Logged in as: " + info[0]).formatted(Formatting.GREEN), 5);
                    restoreButton.active = true;
                } catch (IOException | RuntimeException e) {
                    currentTitle = FormatUtil.surroundWithObfuscated(
                            Text.literal("Invalid Session ID").formatted(Formatting.RED), 7);
                }
            }, "CoffeeAuth-Login").start();
        }).dimensions(cx - 100, cy + 25, 97, 20).build();
        this.addDrawableChild(loginButton);

        restoreButton = ButtonWidget.builder(Text.literal("Restore"), button -> {
            SessionUtil.restoreSession();
            currentTitle = FormatUtil.surroundWithObfuscated(
                    Text.literal("Restored original session").formatted(Formatting.GREEN), 7);
            loginButton.active = true;
            restoreButton.active = false;
        }).dimensions(cx + 3, cy + 25, 97, 20).build();
        this.addDrawableChild(restoreButton);

        ButtonWidget backButton = ButtonWidget.builder(Text.literal("Back"), button -> {
            assert this.client != null;
            this.client.setScreen(new MultiplayerScreen(new TitleScreen()));
        }).dimensions(cx - 100, cy + 50, 200, 20).build();
        this.addDrawableChild(backButton);

        if (CoffeeAuth.currentSession.equals(CoffeeAuth.originalSession)) {
            restoreButton.active = false;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        sessionField.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(
                this.textRenderer, this.currentTitle,
                this.width / 2, this.height / 2 - 30, 0xFFFFFF);
    }

    @Override
    public boolean keyPressed(KeyInput keyInput) {
        if (sessionField.keyPressed(keyInput) || sessionField.isActive()) {
            return true;
        }
        return super.keyPressed(keyInput);
    }

    @Override
    public boolean charTyped(CharInput charInput) {
        if (sessionField.charTyped(charInput))
            return true;
        return super.charTyped(charInput);
    }
}

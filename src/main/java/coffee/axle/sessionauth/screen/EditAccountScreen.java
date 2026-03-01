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
import net.minecraft.client.gui.Click;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class EditAccountScreen extends Screen {
    private TextFieldWidget nameField;
    private TextFieldWidget skinUrlField;
    private ButtonWidget nameButton;
    private ButtonWidget skinButton;
    private Text currentTitle;

    public EditAccountScreen() {
        super(Text.literal(""));
        this.currentTitle = FormatUtil.surroundWithObfuscated(
                Text.literal("Edit Account").formatted(Formatting.AQUA), 5);
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int cy = this.height / 2;

        nameField = new TextFieldWidget(
                this.textRenderer, cx - 100, cy - 40, 200, 20,
                Text.literal("New Username"));
        nameField.setMaxLength(16);
        nameField.setFocused(true);
        this.addSelectableChild(nameField);

        skinUrlField = new TextFieldWidget(
                this.textRenderer, cx - 100, cy, 200, 20,
                Text.literal("Skin URL"));
        skinUrlField.setMaxLength(2048);
        this.addSelectableChild(skinUrlField);

        nameButton = ButtonWidget.builder(Text.literal("Change Name"), button -> {
            String newName = nameField.getText().trim();
            if (newName.isEmpty()) {
                currentTitle = FormatUtil.surroundWithObfuscated(
                        Text.literal("Please input a name").formatted(Formatting.RED), 5);
                return;
            }
            if (!newName.matches("^[a-zA-Z0-9_]{3,16}$")) {
                currentTitle = FormatUtil.surroundWithObfuscated(
                        Text.literal("Invalid name").formatted(Formatting.RED), 7);
                return;
            }
            String token = CoffeeAuth.currentSession.getAccessToken();
            new Thread(() -> {
                int statusCode = ApiUtil.changeName(newName, token);
                currentTitle = switch (statusCode) {
                    case 200 -> {
                        SessionUtil.setSession(SessionUtil.createSession(
                                newName,
                                CoffeeAuth.currentSession.getUuidOrNull(),
                                token));
                        yield FormatUtil.surroundWithObfuscated(
                                Text.literal("Successfully changed name").formatted(Formatting.GREEN), 4);
                    }
                    case 429 -> FormatUtil.surroundWithObfuscated(
                            Text.literal("Too many requests").formatted(Formatting.RED), 5);
                    case 400 -> FormatUtil.surroundWithObfuscated(
                            Text.literal("Invalid name").formatted(Formatting.RED), 7);
                    case 401 -> FormatUtil.surroundWithObfuscated(
                            Text.literal("Invalid token").formatted(Formatting.RED), 7);
                    case 403 -> FormatUtil.surroundWithObfuscated(
                            Text.literal("Name unavailable or changed in last 35 days").formatted(Formatting.RED), 2);
                    default -> FormatUtil.surroundWithObfuscated(
                            Text.literal("Unknown error").formatted(Formatting.RED), 2);
                };
            }, "CoffeeAuth-ChangeName").start();
        }).dimensions(cx - 100, cy + 25, 97, 20).build();
        this.addDrawableChild(nameButton);

        skinButton = ButtonWidget.builder(Text.literal("Change Skin"), button -> {
            String skinUrl = skinUrlField.getText().trim();
            if (skinUrl.isEmpty()) {
                currentTitle = FormatUtil.surroundWithObfuscated(
                        Text.literal("Please input a URL").formatted(Formatting.RED), 5);
                return;
            }
            String token = CoffeeAuth.currentSession.getAccessToken();
            new Thread(() -> {
                int statusCode = ApiUtil.changeSkin(skinUrl, token);
                currentTitle = switch (statusCode) {
                    case 200 -> FormatUtil.surroundWithObfuscated(
                            Text.literal("Successfully changed skin").formatted(Formatting.GREEN), 4);
                    case 429 -> FormatUtil.surroundWithObfuscated(
                            Text.literal("Too many requests").formatted(Formatting.RED), 5);
                    case 401 -> FormatUtil.surroundWithObfuscated(
                            Text.literal("Invalid token").formatted(Formatting.RED), 7);
                    default -> FormatUtil.surroundWithObfuscated(
                            Text.literal("Invalid skin").formatted(Formatting.RED), 7);
                };
            }, "CoffeeAuth-ChangeSkin").start();
        }).dimensions(cx + 3, cy + 25, 97, 20).build();
        this.addDrawableChild(skinButton);

        ButtonWidget backButton = ButtonWidget.builder(Text.literal("Back"), button -> {
            assert this.client != null;
            this.client.setScreen(new MultiplayerScreen(new TitleScreen()));
        }).dimensions(cx - 100, cy + 50, 200, 20).build();
        this.addDrawableChild(backButton);

        if (CoffeeAuth.originalSession.equals(CoffeeAuth.currentSession)) {
            nameButton.active = false;
            skinButton.active = false;
            currentTitle = FormatUtil.surroundWithObfuscated(
                    Text.literal("Cannot modify original session").formatted(Formatting.YELLOW), 4);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        context.drawTextWithShadow(this.textRenderer,
                Text.literal("Username:"), this.width / 2 - 100, this.height / 2 - 52, 0xA0A0A0);
        nameField.render(context, mouseX, mouseY, delta);

        context.drawTextWithShadow(this.textRenderer,
                Text.literal("Skin URL:"), this.width / 2 - 100, this.height / 2 - 10, 0xA0A0A0);
        skinUrlField.render(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(this.textRenderer,
                this.currentTitle, this.width / 2, this.height / 2 - 75, 0xFFFFFF);
    }

    @Override
    public boolean keyPressed(KeyInput keyInput) {
        return nameField.keyPressed(keyInput)
                || skinUrlField.keyPressed(keyInput)
                || super.keyPressed(keyInput);
    }

    @Override
    public boolean charTyped(CharInput charInput) {
        return nameField.charTyped(charInput)
                || skinUrlField.charTyped(charInput)
                || super.charTyped(charInput);
    }

    @Override
    public boolean mouseClicked(Click click, boolean bl) {
        boolean nameFocused = nameField.mouseClicked(click, bl);
        boolean skinFocused = skinUrlField.mouseClicked(click, bl);
        nameField.setFocused(nameFocused);
        skinUrlField.setFocused(skinFocused);
        return nameFocused || skinFocused || super.mouseClicked(click, bl);
    }
}

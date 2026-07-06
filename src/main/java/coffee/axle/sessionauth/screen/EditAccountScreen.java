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
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class EditAccountScreen extends Screen {
    private EditBox nameField;
    private EditBox skinUrlField;
    private Button nameButton;
    private Button skinButton;
    private Component currentTitle;

    public EditAccountScreen() {
        super(Component.literal(""));
        this.currentTitle = Component.literal("Edit Account").withStyle(ChatFormatting.AQUA);
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int cy = this.height / 2;

        nameField = new EditBox(
                this.font, cx - 100, cy - 40, 200, 20,
                Component.literal("New Username"));
        nameField.setMaxLength(16);
        nameField.setFocused(true);
        this.addWidget(nameField);

        skinUrlField = new EditBox(
                this.font, cx - 100, cy, 200, 20,
                Component.literal("Skin URL"));
        skinUrlField.setMaxLength(2048);
        this.addWidget(skinUrlField);

        nameButton = Button.builder(Component.literal("Change Name"), button -> {
            String newName = nameField.getValue().trim();
            if (newName.isEmpty()) {
                currentTitle = Component.literal("Please input a name").withStyle(ChatFormatting.RED);
                return;
            }
            if (!newName.matches("^[a-zA-Z0-9_]{3,16}$")) {
                currentTitle = Component.literal("Invalid name").withStyle(ChatFormatting.RED);
                return;
            }
            String token = CoffeeAuth.currentUser.getAccessToken();
            new Thread(() -> {
                int statusCode = ApiUtil.changeName(newName, token);
                currentTitle = switch (statusCode) {
                    case 200 -> {
                        SessionUtil.setSession(SessionUtil.createUser(
                                newName,
                                CoffeeAuth.currentUser.getProfileId(),
                                token));
                        yield Component.literal("Successfully changed name").withStyle(ChatFormatting.GREEN);
                    }
                    case 429 -> Component.literal("Too many requests").withStyle(ChatFormatting.RED);
                    case 400 -> Component.literal("Invalid name").withStyle(ChatFormatting.RED);
                    case 401 -> Component.literal("Invalid token").withStyle(ChatFormatting.RED);
                    case 403 -> Component.literal("Name unavailable or changed in last 35 days").withStyle(ChatFormatting.RED);
                    default -> Component.literal("Unknown error").withStyle(ChatFormatting.RED);
                };
            }, "CoffeeAuth-ChangeName").start();
        }).bounds(cx - 100, cy + 25, 97, 20).build();
        this.addRenderableWidget(nameButton);

        skinButton = Button.builder(Component.literal("Change Skin"), button -> {
            String skinUrl = skinUrlField.getValue().trim();
            if (skinUrl.isEmpty()) {
                currentTitle = Component.literal("Please input a URL").withStyle(ChatFormatting.RED);
                return;
            }
            String token = CoffeeAuth.currentUser.getAccessToken();
            new Thread(() -> {
                int statusCode = ApiUtil.changeSkin(skinUrl, token);
                currentTitle = switch (statusCode) {
                    case 200 -> Component.literal("Successfully changed skin").withStyle(ChatFormatting.GREEN);
                    case 429 -> Component.literal("Too many requests").withStyle(ChatFormatting.RED);
                    case 401 -> Component.literal("Invalid token").withStyle(ChatFormatting.RED);
                    default -> Component.literal("Invalid skin").withStyle(ChatFormatting.RED);
                };
            }, "CoffeeAuth-ChangeSkin").start();
        }).bounds(cx + 3, cy + 25, 97, 20).build();
        this.addRenderableWidget(skinButton);

        Button backButton = Button.builder(Component.literal("Back"), button -> {
            assert this.minecraft != null;
            //? if <26.2 {
            this.minecraft.setScreen(new JoinMultiplayerScreen(new TitleScreen()));
            //?} else {
            /*this.minecraft.gui.setScreen(new JoinMultiplayerScreen(new TitleScreen()));
            *///?}
        }).bounds(cx - 100, cy + 50, 200, 20).build();
        this.addRenderableWidget(backButton);

        if (CoffeeAuth.originalUser.equals(CoffeeAuth.currentUser)) {
            nameButton.active = false;
            skinButton.active = false;
            currentTitle = Component.literal("Cannot modify original session").withStyle(ChatFormatting.YELLOW);
        }
    }

    //? if <26 {
    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        nameField.render(context, mouseX, mouseY, delta);
        skinUrlField.render(context, mouseX, mouseY, delta);
        context.drawString(this.font,
                Component.literal("Username:"), this.width / 2 - 100, this.height / 2 - 52, 0xFFA0A0A0);
        context.drawString(this.font,
                Component.literal("Skin URL:"), this.width / 2 - 100, this.height / 2 - 10, 0xFFA0A0A0);
        context.drawCenteredString(this.font,
                this.currentTitle, this.width / 2, this.height / 2 - 75, 0xFFFFFFFF);
    }
    //?} else {
    /*@Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        super.extractRenderState(context, mouseX, mouseY, delta);

        nameField.extractWidgetRenderState(context, mouseX, mouseY, delta);
        skinUrlField.extractWidgetRenderState(context, mouseX, mouseY, delta);
        context.text(this.font,
                Component.literal("Username:"), this.width / 2 - 100, this.height / 2 - 52, 0xFFA0A0A0);
        context.text(this.font,
                Component.literal("Skin URL:"), this.width / 2 - 100, this.height / 2 - 10, 0xFFA0A0A0);
        context.centeredText(this.font,
                this.currentTitle, this.width / 2, this.height / 2 - 75, 0xFFFFFFFF);
    }
    *///?}

    @Override
    public boolean keyPressed(KeyEvent keyEvent) {
        return nameField.keyPressed(keyEvent)
                || skinUrlField.keyPressed(keyEvent)
                || super.keyPressed(keyEvent);
    }

    @Override
    public boolean charTyped(CharacterEvent characterEvent) {
        return nameField.charTyped(characterEvent)
                || skinUrlField.charTyped(characterEvent)
                || super.charTyped(characterEvent);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl) {
        boolean nameFocused = nameField.mouseClicked(mouseButtonEvent, bl);
        boolean skinFocused = skinUrlField.mouseClicked(mouseButtonEvent, bl);
        nameField.setFocused(nameFocused);
        skinUrlField.setFocused(skinFocused);
        return nameFocused || skinFocused || super.mouseClicked(mouseButtonEvent, bl);
    }
}

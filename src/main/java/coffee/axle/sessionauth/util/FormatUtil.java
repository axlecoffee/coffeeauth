package coffee.axle.sessionauth.util;

import net.minecraft.text.Style;
import net.minecraft.text.Text;

public class FormatUtil {
    public static Text surroundWithObfuscated(Text baseText, int count) {
        Style baseStyle = baseText.getStyle().withObfuscated(false);
        Style obfStyle = baseStyle.withObfuscated(true);
        String padding = "@".repeat(count);
        return Text.empty()
                .append(Text.literal(padding + " ").setStyle(obfStyle))
                .append(baseText.copy().setStyle(baseStyle))
                .append(Text.literal(" " + padding).setStyle(obfStyle));
    }
}

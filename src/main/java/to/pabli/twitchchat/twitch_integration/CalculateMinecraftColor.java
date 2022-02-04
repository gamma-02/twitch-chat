package to.pabli.twitchchat.twitch_integration;

import java.awt.Color;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.util.Formatting;

public class CalculateMinecraftColor {
  public static Formatting findNearestMinecraftColor(Color color) {
    if(color == null)
      return Formatting.WHITE;
    return Arrays.stream(Formatting.values())
        .filter(Formatting::isColor)
        .map(formatting -> {
          Color formattingColor = new Color(formatting.getColorValue());

          int distance = Math.abs(color.getRed() - formattingColor.getRed()) +
              Math.abs(color.getGreen() - formattingColor.getGreen()) +
              Math.abs(color.getBlue() - formattingColor.getBlue());
          return new FormattingAndDistance(formatting, distance);
        })
        .sorted(Comparator.comparing(FormattingAndDistance::getDistance))
        .map(FormattingAndDistance::getFormatting)
        .findFirst()
        .orElse(Formatting.WHITE);
  }


  public static final Color[] MINECRAFT_COLORS = {(Color.RED),(Color.BLUE), Color.GREEN, new Color(0xB22222), new Color(0xFF7F50), new Color(0x9ACD32), new Color(0xFF4500), new Color(0x2E8B57), new Color(0x2E8B57), new Color(0xD2691E), new Color(0x5F9EA0), new Color(0x1E90FF), new Color(0xFF69B4), new Color(0x8A2BE2), new Color(0x00FF7F) };
  // Code gotten from here https://discuss.dev.twitch.tv/t/default-user-color-in-chat/385/2 but a little bit adjusted.
  public static Map<String, Color> cachedNames = new HashMap<>();
  public static Color getDefaultUserColor(String username) {
    if(username == null)
      return Color.WHITE;
    if (cachedNames.containsKey(username)) {
      return cachedNames.get(username);
    } else {
      // If we don't have the color cached, calculate it.
      char firstChar = username.charAt(0);
      char lastChar = username.charAt(username.length() - 1);

      int n = ((int) firstChar) + ((int) lastChar);
      cachedNames.put(username, MINECRAFT_COLORS[n % MINECRAFT_COLORS.length]);
      return MINECRAFT_COLORS[n % MINECRAFT_COLORS.length];
    }
  }

  private static class FormattingAndDistance {
    private Formatting formatting;

    public Formatting getFormatting() {
      return formatting;
    }

    private int distance;

    public int getDistance() {
      return distance;
    }

    public FormattingAndDistance(Formatting formatting, int distance) {
      this.formatting = formatting;
      this.distance = distance;
    }
  }
}

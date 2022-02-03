package to.pabli.twitchchat.twitch_integration;

import net.minecraft.util.Formatting;

import java.util.HashMap;

public class FormattingColors {

    public static HashMap<String, Formatting> formattingColorCache = new HashMap<>();

    public static void putFormattingColor(String nick, Formatting color) {
        formattingColorCache.put(nick.toLowerCase(), color);
    }
    public static Formatting getFormattingColor(String nick) {
        return formattingColorCache.get(nick.toLowerCase());
    }
    public static boolean isFormattingColorCached(String nick) {
        return formattingColorCache.containsKey(nick.toLowerCase());
    }

}

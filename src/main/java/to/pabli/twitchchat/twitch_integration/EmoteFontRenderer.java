package to.pabli.twitchchat.twitch_integration;

import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import to.pabli.twitchchat.config.ModConfig;

import java.util.function.Function;

public class EmoteFontRenderer extends TextRenderer {
    public EmoteFontRenderer(Function<Identifier, FontStorage> fontStorageAccessor) {
        super(fontStorageAccessor);
    }

    @Override
    public int getWidth(String text) {
        if(text.contains(ModConfig.getConfig().getEmotePrefix())){
            String[] splited = text.split("\\s+");
            for (String element:
                 splited) {
                if(element.startsWith(ModConfig.getConfig().getEmotePrefix())) {
                    NativeImageBackedTexture i = Emotes.emoteCache.get(Integer.parseInt(element.substring(3)));
                    if(i != null){
                        return i.getImage().getWidth();
                    }else{
                        return (int) EmoteStatus.LOADING.getWidth();
                    }
                }
            }
        }
        return super.getWidth(text);

    }
}

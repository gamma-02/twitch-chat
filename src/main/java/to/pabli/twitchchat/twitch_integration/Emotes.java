package to.pabli.twitchchat.twitch_integration;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import org.lwjgl.stb.STBImage;

import javax.imageio.spi.ImageInputStreamSpi;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

public class Emotes {

    public String url;
    public static String loading = "https://cdn.discordapp.com/attachments/937808833672257576/938895916197359686/loading.png";
    public double height;
    public double width;
    public static HashMap<Integer, NativeImageBackedTexture> emoteCache = new HashMap<>();


    public Emotes(String url, double height, double width){

        this.url = url;
        this.height = height;
        this.width = width;
    }

    public static void loadEmote(String urlStr, int id) throws IOException {
        URL url = new URL(urlStr);
        InputStream is = url.openStream();
        emoteCache.put(id, new NativeImageBackedTexture(NativeImage.read(is)));

    }


}

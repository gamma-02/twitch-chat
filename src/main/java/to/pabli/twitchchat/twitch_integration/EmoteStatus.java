package to.pabli.twitchchat.twitch_integration;

import java.net.MalformedURLException;
import java.net.URL;

public enum EmoteStatus {

    LOADING( 16, 16),
    LOADED(16, 16);



    private double width;
    private double height;

    EmoteStatus( double width, double height) {

        this.width = width;
        this.height = height;
    }


    public double getHeight() {
        return height;
    }

    public double getWidth() {
        return width;
    }



}

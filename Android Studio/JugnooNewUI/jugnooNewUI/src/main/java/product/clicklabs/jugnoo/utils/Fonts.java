package product.clicklabs.jugnoo.utils;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by shankar on 4/29/15.
 */
public class Fonts {

    private static Typeface mavenMedium, latoLight, mavenRegular, mavenLight;																// fonts declaration


    public static Typeface mavenMedium(Context appContext) {											// accessing fonts functions
        if (mavenMedium == null) {
            mavenMedium = Typeface.createFromAsset(appContext.getAssets(), "fonts/MavenPro-Medium.ttf");
        }
        return mavenMedium;
    }

    public static Typeface latoLight(Context appContext) {											// accessing fonts functions
        if (latoLight == null) {
            latoLight = Typeface.createFromAsset(appContext.getAssets(), "fonts/lato_light.ttf");
        }
        return latoLight;
    }

    public static Typeface mavenRegular(Context appContext) {											// accessing fonts functions
        if (mavenRegular == null) {
            mavenRegular = Typeface.createFromAsset(appContext.getAssets(), "fonts/maven_pro_regular.otf");
        }
        return mavenRegular;
    }

    public static Typeface mavenLight(Context appContext) {											// accessing fonts functions
        if (mavenLight == null) {
            mavenLight = Typeface.createFromAsset(appContext.getAssets(), "fonts/maven_pro_light_300.otf");
        }
        return mavenLight;
    }

}

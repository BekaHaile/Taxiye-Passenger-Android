package product.clicklabs.jugnoo.utils;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by shankar on 4/29/15.
 */
public class Fonts {

    private static Typeface mavenMedium, mavenRegular, mavenLight, avenirNext, avenirMedium;																// fonts declaration


    public static Typeface mavenMedium(Context appContext) {											// accessing fonts functions
        if (mavenMedium == null) {
            mavenMedium = Typeface.createFromAsset(appContext.getAssets(), "fonts/maven_pro_medium.ttf");
        }
        return mavenMedium;
    }

    public static Typeface mavenRegular(Context appContext) {											// accessing fonts functions
        if (mavenRegular == null) {
            mavenRegular = Typeface.createFromAsset(appContext.getAssets(), "fonts/maven_pro_regular.ttf");
        }
        return mavenRegular;
    }

    public static Typeface mavenLight(Context appContext) {											// accessing fonts functions
        if (mavenLight == null) {
            mavenLight = Typeface.createFromAsset(appContext.getAssets(), "fonts/maven_pro_light_300.ttf");
        }
        return mavenLight;
    }

    public static Typeface avenirNext(Context appContext) {											// accessing fonts functions
        if (avenirNext == null) {
            avenirNext = Typeface.createFromAsset(appContext.getAssets(), "fonts/avenir_next_demi.ttf");
        }
        return avenirNext;
    }

    public static Typeface avenirMedium(Context appContext) {                                 // accessing fonts functions
        if (avenirMedium == null) {
            avenirMedium = Typeface.createFromAsset(appContext.getAssets(), "fonts/avenir_medium.ttf");
        }
        return avenirMedium;
    }

//    From fresh Application
    private static Typeface latoRegular, latoLight;																// fonts declaration


    public static Typeface latoRegular(Context appContext) {											// accessing fonts functions
        if (latoRegular == null) {
            latoRegular = Typeface.createFromAsset(appContext.getAssets(), "fonts/maven_pro_regular.ttf");
        }
        return latoRegular;
    }

    public static Typeface latoLight(Context appContext) {											// accessing fonts functions
        if (latoLight == null) {
            latoLight = Typeface.createFromAsset(appContext.getAssets(), "fonts/maven_pro_light_300.ttf");
        }
        return latoLight;
    }


}

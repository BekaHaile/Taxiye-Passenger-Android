
package sabkuchfresh.widgets;

import android.content.res.AssetManager;
import android.graphics.Typeface;

import java.util.HashMap;
import java.util.Map;

/**
 * Typeface cache to cache the typefaces
 */
public class TypefaceCache {

    //Expected Capacity is number of supported fonts
    private static final Map<String, Typeface> CACHE = new HashMap<String, Typeface>((int) (7 * 1.33f));

    public static final String REGULAR = "fonts/lato_regular.ttf";
    public static final String LIGHT = "fonts/lato_light.ttf";
    public static final String MAVEN_REGULAR = "fonts/maven_pro_regular.ttf";
    public static final String MAVEN_LIGHT = "fonts/maven_pro_light_300.ttf";


    public static Typeface get(final AssetManager manager,
                               final int typefaceCode) {
        synchronized (CACHE) {

            final String typefaceName = getTypefaceName(typefaceCode);

            if (!CACHE.containsKey(typefaceName)) {
                final Typeface t = Typeface
                        .createFromAsset(manager, typefaceName);
                CACHE.put(typefaceName, t);
            }
            return CACHE.get(typefaceName);
        }
    }

    public static Typeface get(final AssetManager manager,
                               final String typefaceName) {
        return get(manager, getCodeForTypefaceName(typefaceName));
    }

    private static int getCodeForTypefaceName(final String typefaceName) {

        if (typefaceName.equals(REGULAR)) {
            return 0;
        } else if (typefaceName.equals(LIGHT)) {
            return 1;
        } else if (typefaceName.equals(MAVEN_REGULAR)) {
            return 2;
        } else if (typefaceName.equals(MAVEN_LIGHT)) {
            return 3;
        }  else {
            return 0;
        }
    }

    private static String getTypefaceName(final int typefaceCode) {
        switch (typefaceCode) {
            case 0: {
                return REGULAR;
            }

            case 1: {
                return LIGHT;
            }

            case 2: {
                return MAVEN_REGULAR;
            }

            case 3: {
                return MAVEN_LIGHT;
            }
            default: {
                return REGULAR;
            }
        }
    }

}

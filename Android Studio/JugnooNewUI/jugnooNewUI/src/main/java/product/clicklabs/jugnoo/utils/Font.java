package product.clicklabs.jugnoo.utils;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by shankar on 1/27/17.
 */

public class Font {

	private Typeface mavenMedium, mavenRegular, mavenLight, avenirNext, avenirMedium;																// fonts declaration

	public Typeface mavenMedium(Context appContext) {											// accessing fonts functions
		if (mavenMedium == null) {
			mavenMedium = Typeface.createFromAsset(appContext.getAssets(), "fonts/maven_pro_medium.ttf");
		}
		return mavenMedium;
	}

	public Typeface mavenRegular(Context appContext) {											// accessing fonts functions
		if (mavenRegular == null) {
			mavenRegular = Typeface.createFromAsset(appContext.getAssets(), "fonts/maven_pro_regular.ttf");
		}
		return mavenRegular;
	}

	public Typeface mavenLight(Context appContext) {											// accessing fonts functions
		if (mavenLight == null) {
			mavenLight = Typeface.createFromAsset(appContext.getAssets(), "fonts/maven_pro_light_300.otf");
		}
		return mavenLight;
	}

	public Typeface avenirNext(Context appContext) {											// accessing fonts functions
		if (avenirNext == null) {
			avenirNext = Typeface.createFromAsset(appContext.getAssets(), "fonts/avenir_next_demi.otf");
		}
		return avenirNext;
	}

	public Typeface avenirMedium(Context appContext) {                                 // accessing fonts functions
		if (avenirMedium == null) {
			avenirMedium = Typeface.createFromAsset(appContext.getAssets(), "fonts/avenir_medium.otf");
		}
		return avenirMedium;
	}

}

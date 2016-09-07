package com.sabkuchfresh.analytics;

import android.content.Context;

import com.facebook.appevents.AppEventsLogger;

import product.clicklabs.jugnoo.utils.Prefs;

/**
 * Created by shankar on 12/14/15.
 */
public class FbEvents {

    public static void logEvent(Context context, String eventName, boolean oneTime){
		if(oneTime) {
			String spPrefix = "fb_";
			if (Prefs.with(context).getInt(spPrefix+eventName, 0) == 0) {
				AppEventsLogger logger = AppEventsLogger.newLogger(context);
				logger.logEvent(eventName);
				Prefs.with(context).save(spPrefix+eventName, 1);
			}
		}
		else{
			AppEventsLogger logger = AppEventsLogger.newLogger(context);
			logger.logEvent(eventName);
		}
    }

}
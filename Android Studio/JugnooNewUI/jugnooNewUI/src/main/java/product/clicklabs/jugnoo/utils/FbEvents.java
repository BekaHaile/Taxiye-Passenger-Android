package product.clicklabs.jugnoo.utils;

import android.content.Context;

import com.facebook.appevents.AppEventsLogger;

import product.clicklabs.jugnoo.datastructure.SPLabels;

/**
 * Created by shankar on 12/14/15.
 */
public class FbEvents implements FlurryEventNames{

    public static void logEventRequestRide(Context context){
        if(Prefs.with(context).getInt(SPLabels.FB_EVENT_REQUEST_RIDE_LOGGED, 0) == 0) {
            AppEventsLogger logger = AppEventsLogger.newLogger(context);
            logger.logEvent(FB_EVENT_REQUEST_RIDE);
            Prefs.with(context).save(SPLabels.FB_EVENT_REQUEST_RIDE_LOGGED, 1);
        }
    }

    public static void logEventRideCompleted(Context context){
        if(Prefs.with(context).getInt(SPLabels.FB_EVENT_RIDE_COMPLETED_LOGGED, 0) == 0) {
            AppEventsLogger logger = AppEventsLogger.newLogger(context);
            logger.logEvent(FB_EVENT_RIDE_COMPLETED);
            Prefs.with(context).save(SPLabels.FB_EVENT_RIDE_COMPLETED_LOGGED, 1);
        }
    }

}

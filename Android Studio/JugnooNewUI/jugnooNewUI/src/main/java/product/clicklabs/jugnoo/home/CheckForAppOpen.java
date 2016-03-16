package product.clicklabs.jugnoo.home;

import android.content.Context;

import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.utils.Prefs;

/**
 * Created by shankar on 3/16/16.
 */
public class CheckForAppOpen {

	public void checkAndFillParamsForIgnoringAppOpen(Context context, HashMap<String, String> params){
		long currentTime = System.currentTimeMillis();
		long lastPushReceivedTime = Prefs.with(context).getLong(Constants.KEY_SP_LAST_PUSH_RECEIVED_TIME,
				(currentTime - Constants.HOUR_MILLIS));
		long diff = System.currentTimeMillis() - lastPushReceivedTime;
		if(diff < Constants.IGNORE_APP_OPEN_MAX_TIME){
			params.put(Constants.KEY_IGNORE_APP_OPEN, "1");
			params.put(Constants.KEY_SP_LAST_PUSH_RECEIVED_TIME, String.valueOf(lastPushReceivedTime));
		} else{
			params.put(Constants.KEY_IGNORE_APP_OPEN, "0");
		}
	}

}

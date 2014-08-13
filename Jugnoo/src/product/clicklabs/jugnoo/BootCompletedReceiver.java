package product.clicklabs.jugnoo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class BootCompletedReceiver extends BroadcastReceiver {

    final static String TAG = "BootCompletedReceiver";

    @Override
    public void onReceive(Context context, Intent arg1) {
        Log.w(TAG, "starting service...");
        
        String SETTINGS_SHARED_PREF_NAME = "settingsPref", SP_DRIVER_SERVICE = "sp_driver_service";
    	SharedPreferences preferences = context.getSharedPreferences(SETTINGS_SHARED_PREF_NAME, 0);
    	String driverService = preferences.getString(SP_DRIVER_SERVICE, "off");
    	
    	if("on".equalsIgnoreCase(driverService)){
    		context.startService(new Intent(context, DriverLocationUpdateService.class));
    	}
    	
    }
}
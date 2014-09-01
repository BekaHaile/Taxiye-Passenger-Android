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
        
        Database2 database2 = new Database2(context);
        String serviceRestartOnReboot = database2.getDriverServiceRestartOnReboot();
        
    	if("yes".equalsIgnoreCase(serviceRestartOnReboot)){
    		context.startService(new Intent(context, DriverLocationUpdateService.class));
    	}
    	else{
    		String jugnooOn = database2.getJugnooOn();
    		if("on".equalsIgnoreCase(jugnooOn)){
    			context.startService(new Intent(context, DriverLocationUpdateService.class));
    		}
    	}
    	
    	database2.close();
    }
}
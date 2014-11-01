package product.clicklabs.jugnoo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompletedReceiver extends BroadcastReceiver {

    final static String TAG = "BootCompletedReceiver";

    @Override
    public void onReceive(final Context context, Intent arg1) {
        Log.w(TAG, "starting service...");
        
        Database2 database2 = new Database2(context);
        final String serviceRestartOnReboot = database2.getDriverServiceRestartOnReboot();
        database2.updateDriverLastLocationTime();
        
    	if("yes".equalsIgnoreCase(serviceRestartOnReboot)){
    		context.startService(new Intent(context, DriverLocationUpdateService.class));
    	}
    	else{
//    		String jugnooOn = database2.getJugnooOn();
//    		if("off".equalsIgnoreCase(jugnooOn)){
//    			context.startService(new Intent(context, DriverLocationUpdateService.class));
//    		}
    	}
    	
    	database2.close();
    }
}
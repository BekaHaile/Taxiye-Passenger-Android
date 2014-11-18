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
        try{
	        final String serviceRestartOnReboot = database2.getDriverServiceRun();
	        database2.updateDriverLastLocationTime();
	        
	    	if(Database2.YES.equalsIgnoreCase(serviceRestartOnReboot)){
	    		new DriverServiceOperations().startDriverService(context);
	    	}
	    	else if(Database2.NO.equalsIgnoreCase(serviceRestartOnReboot)){
	    		new DriverServiceOperations().rescheduleDriverService(context);
	    	}
        } catch(Exception e){
        	e.printStackTrace();
        } finally{
        	database2.close();
        }
    	
    }
}
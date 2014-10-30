package product.clicklabs.jugnoo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

public class DriverLocationUpdateAlarmReceiver extends BroadcastReceiver implements DisplayToast{

	private static final String SEND_LOCATION = "product.clicklabs.jugnoo.SEND_LOCATION";
	
	private static final long MAX_TIME_BEFORE_LOCATION_UPDATE = 1 * 60000;
	
	@Override
	public void onReceive(final Context context, Intent intent) {
		String action = intent.getAction();
		if (SEND_LOCATION.equals(action)) {
			
			Database2 database2 = new Database2(context);
			long lastTime = database2.getDriverLastLocationTime();
			database2.close();
			
			long currentTime = System.currentTimeMillis();
			
			Log.i("lastTime", "="+lastTime);
			Log.i("currentTime", "="+currentTime);
			Log.e("currentTime - lastTime", "="+(currentTime - lastTime));
	    	Log.writeLogToFile("AlarmReceiver", "Receiver "+new DateOperations().getCurrentTime()+" = "+(currentTime - lastTime) 
	    			+ " hasNet = "+AppStatus.getInstance(context).isOnline(context));
			
			if(currentTime >= (lastTime + MAX_TIME_BEFORE_LOCATION_UPDATE)){
				new Thread(new Runnable() {
					@Override
					public void run() {
						new DriverLocationDispatcher().sendLocationToServer(DriverLocationUpdateAlarmReceiver.this, context, "AlarmReceiver");
					}
				}).start();
			}
		}
	}

	@Override
	public void displayToast(final Context context, final String text) {
    	// Get a handler that can be used to post to the main thread
    	Handler mainHandler = new Handler(context.getMainLooper());
    	Runnable myRunnable = new Runnable() {
			@Override
			public void run() {
				Toast.makeText(context, text, Toast.LENGTH_LONG).show();
			}
		};
    	mainHandler.post(myRunnable);
	}
	
}
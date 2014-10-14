package product.clicklabs.jugnoo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DriverLocationUpdateAlarmReceiver extends BroadcastReceiver {

	private static final String SEND_LOCATION = "product.clicklabs.jugnoo.SEND_LOCATION";
	
	private static final long MAX_TIME_BEFORE_LOCATION_UPDATE = 3 * 60000;
	
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
			
			if(currentTime >= (lastTime + MAX_TIME_BEFORE_LOCATION_UPDATE)){
				new Thread(new Runnable() {
					@Override
					public void run() {
						new DriverLocationDispatcher().sendLocationToServer(context);
					}
				}).start();
			}
		}
	}
}
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
			try {
				long lastTime = database2.getDriverLastLocationTime();
				String accessToken = database2.getDLDAccessToken();
				database2.close();
				if("".equalsIgnoreCase(accessToken)){
					DriverLocationUpdateService.updateServerData(context);
				}
				long currentTime = System.currentTimeMillis();
				
				Log.e("currentTime - lastTime", "="+(currentTime - lastTime));
		    	Log.writeLogToFile("AlarmReceiver", "Receiver "+new DateOperations().getCurrentTime()+" = "+(currentTime - lastTime) 
		    			+ " hasNet = "+AppStatus.getInstance(context).isOnline(context));
				
				if(currentTime >= (lastTime + MAX_TIME_BEFORE_LOCATION_UPDATE)){
					new Thread(new Runnable() {
						@Override
						public void run() {
							new DriverLocationDispatcher().sendLocationToServer(context, "AlarmReceiver");
						}
					}).start();
				}
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
			finally{
				database2.close();
			}
			
			
		}
	}
	
}
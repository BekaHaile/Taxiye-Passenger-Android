package product.clicklabs.jugnoo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DriverLocationUpdateAlarmReceiver extends BroadcastReceiver {

	private static final String SEND_LOCATION = "product.clicklabs.jugnoo.SEND_LOCATION";
	
	@Override
	public void onReceive(final Context context, Intent intent) {
		String action = intent.getAction();
		if (SEND_LOCATION.equals(action)) {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					new DriverLocationDispatcher().sendLocationToServer(context);
				}
			}).start();
		}
	}
}
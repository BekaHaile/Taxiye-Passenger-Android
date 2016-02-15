package product.clicklabs.jugnoo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import product.clicklabs.jugnoo.utils.Log;

/**
 * Created by shankar on 2/10/16.
 */
public class RefreshGCMReceiver extends BroadcastReceiver {

	private final String TAG = RefreshGCMReceiver.class.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {

		// For our recurring task, we'll just display a message
		Log.e(TAG, "I'm running");

//		new DeviceTokenGenerator().clearAndRegenerateDeviceToken(context, new IDeviceTokenReceiver() {
//			@Override
//			public void deviceTokenReceived(String regId) {
//				Log.e(TAG, "deviceToken = " + regId);
//			}
//		});
	}
}

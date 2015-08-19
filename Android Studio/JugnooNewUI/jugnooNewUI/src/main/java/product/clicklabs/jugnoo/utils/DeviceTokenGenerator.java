package product.clicklabs.jugnoo.utils;

import android.content.Context;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import product.clicklabs.jugnoo.config.Config;

public class DeviceTokenGenerator {

	String token;

	public DeviceTokenGenerator(){
		token = "";
	}

	public void generateDeviceToken(final Context context, final IDeviceTokenReceiver deviceTokenReceiver){
		if (AppStatus.getInstance(context).isOnline(context)) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						InstanceID instanceID = InstanceID.getInstance(context);
						token = instanceID.getToken(Config.getGoogleProjectId(), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
					} catch(Exception e){
						e.printStackTrace();
					} finally{
						deviceTokenReceiver.deviceTokenReceived(token);
					}
				}
			}).start();
		}
		else{
			deviceTokenReceiver.deviceTokenReceived(token);
		}
	}

}

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
						if(token == null){
							token = "not_found";
						} else if(token.equalsIgnoreCase("")){
							token = "not_found";
						}
						deviceTokenReceiver.deviceTokenReceived(token);
					}
				}
			}).start();
		}
		else{
			deviceTokenReceiver.deviceTokenReceived(token);
		}
	}

	public String forceGenerateDeviceToken(Context context){
		String token = "";
		try {
			InstanceID instanceID = InstanceID.getInstance(context);
			token = instanceID.getToken(Config.getGoogleProjectId(), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(token == null){
				token = "not_found";
			} else if(token.equalsIgnoreCase("")){
				token = "not_found";
			}
		}
		return token;
	}

}

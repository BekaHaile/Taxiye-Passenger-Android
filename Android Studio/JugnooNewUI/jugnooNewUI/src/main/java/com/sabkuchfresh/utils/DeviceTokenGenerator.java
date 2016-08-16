package com.sabkuchfresh.utils;

import android.content.Context;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.sabkuchfresh.config.Config;

public class DeviceTokenGenerator {

	private String token;

	public DeviceTokenGenerator(){
		token = "";
	}

	public void generateDeviceToken(final Context context, final IDeviceTokenReceiver deviceTokenReceiver){
		if (AppStatus.getInstance(context).isOnline(context)) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					long currentTime = System.currentTimeMillis();
					try {
						InstanceID instanceID = InstanceID.getInstance(context);;
						try {
							long maxInterval = Prefs.with(context).getLong(Constants.KEY_SP_DEVICE_TOKEN_REFRESH_INTERVAL,
									Constants.DEFAULT_DEVICE_TOKEN_REFRESH_INTERVAL);
							long lastTime = Prefs.with(context).getLong(Constants.SP_LAST_DEVICE_TOKEN_REFRESH_TIME,
									currentTime - 2*maxInterval);
							long diff = currentTime - lastTime;
							if(diff > maxInterval){
								instanceID.deleteInstanceID();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}  //context.getResources().getString(R.string.goog)
						token = instanceID.getToken(Config.getGoogleProjectId(), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
					} catch(Exception e){
						e.printStackTrace();
					} finally{
						if(token == null){
							token = "not_found";
						} else if(token.equalsIgnoreCase("")){
							token = "not_found";
						} else{
							Prefs.with(context).save(Constants.SP_LAST_DEVICE_TOKEN_REFRESH_TIME, currentTime);
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

}

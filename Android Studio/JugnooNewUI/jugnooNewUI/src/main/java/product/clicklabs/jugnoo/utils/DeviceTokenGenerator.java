package product.clicklabs.jugnoo.utils;

import android.content.Context;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.config.Config;

public class DeviceTokenGenerator {

    private String token;

    public DeviceTokenGenerator() {
        token = "";
    }

    public void generateDeviceToken(final Context context, final IDeviceTokenReceiver deviceTokenReceiver) {
        if (AppStatus.getInstance(context).isOnline(context)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    long currentTime = System.currentTimeMillis();
                    try {
                        try {
                            long maxInterval = Prefs.with(context).getLong(Constants.KEY_SP_DEVICE_TOKEN_REFRESH_INTERVAL,
                                    Constants.DEFAULT_DEVICE_TOKEN_REFRESH_INTERVAL);
                            long lastTime = Prefs.with(context).getLong(Constants.SP_LAST_DEVICE_TOKEN_REFRESH_TIME,
                                    currentTime - 2 * maxInterval);
                            long diff = currentTime - lastTime;
                            if (diff > maxInterval) {
                                FirebaseInstanceId.getInstance().deleteInstanceId();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        token = FirebaseInstanceId.getInstance().getToken();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (token == null) {
                            token = "not_found";
                        } else if (token.equalsIgnoreCase("")) {
                            token = "not_found";
                        } else {
                            Prefs.with(context).save(Constants.SP_LAST_DEVICE_TOKEN_REFRESH_TIME, currentTime);
                        }
                        deviceTokenReceiver.deviceTokenReceived(token);
                        subscribeTopics();
                    }
                }
            }).start();
        } else {
            deviceTokenReceiver.deviceTokenReceived(token);
        }
    }

    private static final String[] TOPICS = {"global"};

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @throws Exception if unable to reach the FCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics() {
        try {
            for (String topic : TOPICS) {
                FirebaseMessaging.getInstance().subscribeToTopic("/topics/" + topic);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // [END subscribe_topics]

    public void clearRegisteration() {
        try {
            FirebaseInstanceId.getInstance().deleteInstanceId();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

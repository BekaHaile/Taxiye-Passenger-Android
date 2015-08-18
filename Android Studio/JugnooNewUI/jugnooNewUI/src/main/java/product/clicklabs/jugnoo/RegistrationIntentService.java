package product.clicklabs.jugnoo;

/**
 * Created by socomo20 on 8/18/15.
 */

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.Pair;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;

import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.utils.HttpRequester;

public class RegistrationIntentService extends IntentService {

	private static final String TAG = "RegIntentService";
	private static final String[] TOPICS = {"global"};

	public RegistrationIntentService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		try {
			// In the (unlikely) event that multiple refresh operations occur simultaneously,
			// ensure that they are processed sequentially.
			synchronized (TAG) {
				// [START register_for_gcm]
				// Initially this call goes out to the network to retrieve the token, subsequent calls
				// are local.
				// [START get_token]
				InstanceID instanceID = InstanceID.getInstance(this);
				String token = instanceID.getToken(Config.getGoogleProjectId(), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
				// [END get_token]
				Log.i(TAG, "GCM Registration Token: " + token);

				Data.deviceToken = token;

				// TODO: Implement this method to send any registration to your app's servers.
				sendRegistrationToServer(this, token);

				// Subscribe to topic channels
				subscribeTopics(token);

			}
		} catch (Exception e) {
			Log.d(TAG, "Failed to complete token refresh", e);
		}
	}

	/**
	 * Persist registration to third-party servers.
	 *
	 * Modify this method to associate the user's GCM registration token with any server-side account
	 * maintained by your application.
	 *
	 * @param token The new token.
	 */
	private void sendRegistrationToServer(Context context, String token) {
		// Add custom implementation, as needed.

		try {
			String serverUrl = Config.getServerUrl();

			Pair<String, Integer> pair = AccessTokenGenerator.getAccessTokenPair(context);
			if(!"".equalsIgnoreCase(pair.first)) {
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
				nameValuePairs.add(new BasicNameValuePair("access_token", pair.first));
				nameValuePairs.add(new BasicNameValuePair("client_id", Config.getClientId()));
				nameValuePairs.add(new BasicNameValuePair("device_token", token));


				HttpRequester simpleJSONParser = new HttpRequester();
				String result = simpleJSONParser.getJSONFromUrlParams(serverUrl + "/update_device_token", nameValuePairs);
				Log.e("update_device_token result", ""+result);

				simpleJSONParser = null;
				nameValuePairs = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
			}
		}).start();
	}

	/**
	 * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
	 *
	 * @param token GCM token
	 * @throws IOException if unable to reach the GCM PubSub service
	 */
	// [START subscribe_topics]
	private void subscribeTopics(String token) throws IOException {
		for (String topic : TOPICS) {
			GcmPubSub pubSub = GcmPubSub.getInstance(this);
			pubSub.subscribe(token, "/topics/" + topic, null);
		}
	}
	// [END subscribe_topics]

}
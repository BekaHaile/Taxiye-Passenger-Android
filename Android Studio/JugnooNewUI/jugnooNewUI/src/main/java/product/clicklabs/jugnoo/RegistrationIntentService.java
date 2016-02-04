package product.clicklabs.jugnoo;

/**
 * Created by socomo20 on 8/18/15.
 */

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Pair;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;
import java.util.HashMap;

import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.Log;
import retrofit.client.Response;

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

				sendRegistrationToServer(this, token);

				// Subscribe to topic channels
				subscribeTopics(token);

				try {
					if(intent.hasExtra(Data.INTENT_CLASS_NAME)){
						Intent registrationComplete = new Intent(this, Class.forName(intent.getStringExtra(Data.INTENT_CLASS_NAME)));
						registrationComplete.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						registrationComplete.setAction(Data.REGISTRATION_COMPLETE);
						registrationComplete.putExtra(Data.DEVICE_TOKEN, token);
						this.startActivity(registrationComplete);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		} catch (Exception e) {
			Log.d(TAG, "Failed to complete token refresh");
			try {
				if(intent.hasExtra(Data.INTENT_CLASS_NAME)){
					Intent registrationComplete = new Intent(this, Class.forName(intent.getStringExtra(Data.INTENT_CLASS_NAME)));
					registrationComplete.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					registrationComplete.setAction(Data.REGISTRATION_FAILED);
					registrationComplete.putExtra(Data.ERROR, e.toString());
					this.startActivity(registrationComplete);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
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
				HashMap<String, String> nameValuePairs = new HashMap<>();
				nameValuePairs.put("access_token", pair.first);
				nameValuePairs.put("client_id", Config.getClientId());
				nameValuePairs.put("device_token", token);
				Response response = RestClient.getApiServices().updateDeviceToken(nameValuePairs);
				Log.e("update_device_token result", ""+response.getBody());
				nameValuePairs = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

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
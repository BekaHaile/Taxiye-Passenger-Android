package product.clicklabs.jugnoo.utils;

import android.content.Context;

import com.nudgespot.api.GcmClient;
import com.nudgespot.api.auth.NudgespotCredentials;
import com.nudgespot.resource.NudgespotActivity;
import com.nudgespot.resource.NudgespotSubscriber;

import org.json.JSONObject;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;

/**
 * Created by shankar on 3/29/16.
 */
public class NudgeClient {

	private static GcmClient mGcmClient;

	public static GcmClient getGcmClient(Context context){
		if(mGcmClient == null){
			mGcmClient = GcmClient.getClient(new NudgespotCredentials(
					context.getResources().getString(R.string.nudgespot_javascript_api_key),
					context.getResources().getString(R.string.nudgespot_rest_api_key)), context);
		}
		return mGcmClient;
	}

	public static void initialize(Context context, String userId){
		try {
			getGcmClient(context).initialize(userId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void initialize(Context context, String userId, String userName, String email, String phoneNo){
		try {
			NudgespotSubscriber subscriber = new NudgespotSubscriber(userId);
			JSONObject props = new JSONObject();
			props.put(Constants.KEY_USER_NAME, userName);
			props.put(Constants.KEY_EMAIL, email);
			props.put(Constants.KEY_PHONE_NO, phoneNo);
			subscriber.setProperties(props);
			getGcmClient(context).initialize(subscriber);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void trackEvent(Context context, String eventName, JSONObject map){
		try {
			NudgespotActivity activity = new NudgespotActivity(eventName);
			activity.setProperties(map);
			getGcmClient(context).track(activity);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void logout(Context context){
		getGcmClient(context).clearRegistration();
	}


}

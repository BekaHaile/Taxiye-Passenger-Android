package product.clicklabs.jugnoo.utils;

import android.content.Context;

import com.nudgespot.api.GcmClient;
import com.nudgespot.api.auth.NudgespotCredentials;
import com.nudgespot.resource.NudgespotActivity;
import com.nudgespot.resource.NudgespotSubscriber;

import org.json.JSONObject;

import product.clicklabs.jugnoo.Constants;

/**
 * Created by shankar on 3/29/16.
 */
public class NudgeClient {

	private static GcmClient mGcmClient;

	public static GcmClient getGcmClient(Context context){
//		if(mGcmClient == null){
			mGcmClient = GcmClient.getClient(new NudgespotCredentials("43cbec12b85957e2244f91af04cf238a",
					"90ea5fdd4d74c3e13b1059fdbebf15a1"), context);
//		}
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
		NudgespotActivity activity = new NudgespotActivity(eventName);
		activity.setProperties(map);
		getGcmClient(context).track(activity);
	}

	public static void logout(Context context){
		getGcmClient(context).clearRegistration();
	}


}

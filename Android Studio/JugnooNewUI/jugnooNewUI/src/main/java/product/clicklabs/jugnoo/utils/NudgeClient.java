package product.clicklabs.jugnoo.utils;

import android.content.Context;
import android.os.AsyncTask;

import com.nudgespot.api.GcmClient;
import com.nudgespot.api.auth.NudgespotCredentials;
import com.nudgespot.resource.NudgespotActivity;
import com.nudgespot.resource.NudgespotSubscriber;

import org.json.JSONArray;
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

	public static void initialize(Context context, String userId, String userName, String email, String phoneNo,
								  String city, String cityReg){
		try {
			getGcmClient(context).initialize(getNudgespotSubscriber(userId, userName, email, phoneNo, city, cityReg));
			new UpdateAsync(context, userId, userName, email, phoneNo, city, cityReg).execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static NudgespotSubscriber getNudgespotSubscriber(String userId, String userName, String email, String phoneNo,
													   String city, String cityReg){
		NudgespotSubscriber subscriber = new NudgespotSubscriber(userId);
		try {
			JSONObject props = new JSONObject();
			props.put(Constants.KEY_USER_NAME, userName);
			props.put(Constants.KEY_EMAIL, email);
			props.put(Constants.KEY_PHONE_NO, phoneNo);
			props.put(Constants.KEY_SIGNED_UP_AT, DateOperations.getCurrentTimeInUTC());
			props.put(Constants.KEY_CITY, city);
			props.put(Constants.KEY_CITY_REG, cityReg);

			JSONArray jArray = new JSONArray();
			JSONObject jContact = new JSONObject();
			jContact.put(Constants.KEY_CONTACT_TYPE, Constants.KEY_EMAIL);
			jContact.put(Constants.KEY_CONTACT_VALUE, email);
			jContact.put(Constants.KEY_SUBSCRIPTION_STATUS, Constants.KEY_ACTIVE);
			jArray.put(jContact);
			JSONObject jContact1 = new JSONObject();
			jContact1.put(Constants.KEY_CONTACT_TYPE, Constants.KEY_PHONE);
			jContact1.put(Constants.KEY_CONTACT_VALUE, phoneNo);
			jContact1.put(Constants.KEY_SUBSCRIPTION_STATUS, Constants.KEY_ACTIVE);
			jArray.put(jContact1);
			props.put(Constants.KEY_CONTACTS, jArray);

			subscriber.setProperties(props);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return subscriber;
	}


	public static void trackEventUserId(Context context, String eventName, JSONObject map){
		try {
			if(map == null){
				map = new JSONObject();
			}
			map.put(Constants.KEY_USER_ID, Prefs.with(context).getString(Constants.SP_USER_ID, ""));
			NudgespotActivity activity = new NudgespotActivity(eventName);
			activity.setProperties(map);
			getGcmClient(context).track(activity);

			String campaignName = Prefs.with(context).getString(Constants.KEY_SP_FUGU_CAMPAIGN_NAME, "");
			if(!"".equalsIgnoreCase(campaignName)) {
				NudgespotActivity campaignActivity = new NudgespotActivity(eventName + "_" + campaignName);
				campaignActivity.setProperties(map);
				getGcmClient(context).track(campaignActivity);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void logout(Context context){
		getGcmClient(context).clearRegistration();
	}

	public static class UpdateAsync extends AsyncTask<String, Integer, String>{
		private Context context;
		private String userId, userName, email, phoneNo, city, cityReg;
		public UpdateAsync(Context context, String userId, String userName, String email, String phoneNo,
						   String city, String cityReg){
			this.context = context;
			this.userId = userId;
			this.userName = userName;
			this.email = email;
			this.phoneNo = phoneNo;
			this.city = city;
			this.cityReg = cityReg;
		}

		@Override
		protected String doInBackground(String... params) {
			while(!getGcmClient(context).isSubscriberReady()){
				try {
					Thread.sleep(5000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return "go";
		}

		@Override
		protected void onPostExecute(String s) {
			super.onPostExecute(s);
			try {
				if(s.equalsIgnoreCase("go")){
					getGcmClient(context).update(getNudgespotSubscriber(userId, userName, email, phoneNo, city, cityReg));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}

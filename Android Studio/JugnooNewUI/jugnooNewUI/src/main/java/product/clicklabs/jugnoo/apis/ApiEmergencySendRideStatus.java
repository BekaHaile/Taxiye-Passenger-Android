package product.clicklabs.jugnoo.apis;

import android.app.Activity;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.emergency.EmergencyActivity;
import product.clicklabs.jugnoo.emergency.models.ContactBean;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * For fetching emergency contacts list from server
 *
 * Created by shankar on 3/2/16.
 */
public class ApiEmergencySendRideStatus {

	private final String TAG = ApiEmergencySendRideStatus.class.getSimpleName();

	private Activity activity;
	private Callback callback;

	public ApiEmergencySendRideStatus(Activity activity, Callback callback){
		this.activity = activity;
		this.callback = callback;
	}

	public void emergencySendRideStatusMessage(String engagementId, ArrayList<ContactBean> selectedContacts) {
		try {
			if(MyApplication.getInstance().isOnline()) {

				DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));

				HashMap<String, String> params = new HashMap<>();
				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put(Constants.KEY_ENGAGEMENT_ID, engagementId);

				JSONArray jsonArray = new JSONArray();
				for(ContactBean contact : selectedContacts){
					if(jsonArray.length() < EmergencyActivity.MAX_EMERGENCY_CONTACTS_TO_SEND_RIDE_STATUS) {
						JSONObject jsonObject = new JSONObject();
						jsonObject.put(Constants.KEY_PHONE_NO, contact.getPhoneNo());
						jsonObject.put(Constants.KEY_COUNTRY_CODE, contact.getCountryCode());
						jsonArray.put(jsonObject);
					}
				}
				params.put(Constants.KEY_PHONE_NO, jsonArray.toString());

				Log.i("params", "=" + params.toString());

				new HomeUtil().putDefaultParams(params);
				RestClient.getApiService().emergencySendRideStatusMessage(params, new retrofit.Callback<SettleUserDebt>() {
					@Override
					public void success(SettleUserDebt settleUserDebt, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.i(TAG, "emergencySendRideStatusMessage response = " + responseStr);
						DialogPopup.dismissLoadingDialog();
						try {
							JSONObject jObj = new JSONObject(responseStr);
							String message = JSONParser.getServerMessage(jObj);
							if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
								int flag = jObj.getInt("flag");
								if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
									callback.onSuccess(message);
								} else {
									DialogPopup.alertPopup(activity, "", message);
								}
							}
						} catch (Exception exception) {
							exception.printStackTrace();
							retryDialog(DialogErrorType.SERVER_ERROR);
						}
						DialogPopup.dismissLoadingDialog();
					}

					@Override
					public void failure(RetrofitError error) {
						Log.e(TAG, "emergencySendRideStatusMessage error" + error.toString());
						DialogPopup.dismissLoadingDialog();
						retryDialog(DialogErrorType.CONNECTION_LOST);
						callback.onFailure();
					}
				});
			}
			else {
				retryDialog(DialogErrorType.NO_NET);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void retryDialog(DialogErrorType dialogErrorType){
		DialogPopup.dialogNoInternet(activity,
				dialogErrorType,
				new Utils.AlertCallBackWithButtonsInterface() {
					@Override
					public void positiveClick(View view) {
						callback.onRetry(view);
					}

					@Override
					public void neutralClick(View view) {

					}

					@Override
					public void negativeClick(View view) {
						callback.onNoRetry(view);
					}
				});
	}


	public interface Callback{
		void onSuccess(String message);
		void onFailure();
		void onRetry(View view);
		void onNoRetry(View view);
	}

}

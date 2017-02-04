package product.clicklabs.jugnoo.apis;

import android.app.Activity;
import android.location.Location;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.PendingCall;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.Log;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * API for raising an emergency alert to customer support
 * (Fire and forget kind,
 * but if failed it will be
 * registered in pending calls table and will be called when net is available)
 *
 * Created by shankar on 2/22/16.
 */
public class ApiEmergencyAlert {

	private final String TAG = ApiEmergencyAlert.class.getSimpleName();
	private Activity activity;
	private Callback callback;

	public ApiEmergencyAlert(Activity activity, Callback callback){
		this.activity = activity;
		this.callback = callback;
	}

	public void raiseEmergencyAlertAPI(Location myLocation, String alertType, String driverId, String engagementId) {
		try {
			if(MyApplication.getInstance().isOnline()) {
				final HashMap<String, String> params = new HashMap<>();
				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put(Constants.KEY_DRIVER_ID, driverId);
				params.put(Constants.KEY_ENGAGEMENT_ID, engagementId);
				params.put(Constants.KEY_ALERT_TYPE, alertType);

				if (myLocation != null) {
					params.put(Constants.KEY_LATITUDE, String.valueOf(myLocation.getLatitude()));
					params.put(Constants.KEY_LONGITUDE, String.valueOf(myLocation.getLongitude()));
				} else {
					params.put(Constants.KEY_LATITUDE, String.valueOf(callback.getSavedLatitude()));
					params.put(Constants.KEY_LONGITUDE, String.valueOf(callback.getSavedLongitude()));
				}

				new HomeUtil().putDefaultParams(params);
				RestClient.getApiService().emergencyAlert(params, new retrofit.Callback<SettleUserDebt>() {
					@Override
					public void success(SettleUserDebt settleUserDebt, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.i(TAG, "emergencyAlert response = " + responseStr);
						try {
							JSONObject jObj = new JSONObject(responseStr);
							int flag = jObj.optInt(Constants.KEY_FLAG, ApiResponseFlags.ACTION_COMPLETE.getOrdinal());
							if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
								callback.onSuccess();
							}
						} catch (Exception exception) {
							exception.printStackTrace();
						}
					}

					@Override
					public void failure(RetrofitError error) {
						Log.e(TAG, "emergencyAlert error" + error.toString());
						MyApplication.getInstance().getDatabase2().insertPendingAPICall(activity,
								PendingCall.EMERGENCY_ALERT.getPath(), params);
						callback.onFailure();
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public interface Callback{
		void onSuccess();
		void onFailure();
		double getSavedLatitude();
		double getSavedLongitude();
	}

}

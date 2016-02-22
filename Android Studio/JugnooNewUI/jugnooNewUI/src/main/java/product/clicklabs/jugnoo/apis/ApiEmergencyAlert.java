package product.clicklabs.jugnoo.apis;

import android.app.Activity;
import android.location.Location;

import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.Database2;
import product.clicklabs.jugnoo.LocationFetcher;
import product.clicklabs.jugnoo.datastructure.PendingCall;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.Log;
import retrofit.Callback;
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

	public ApiEmergencyAlert(Activity activity){
		this.activity = activity;
	}

	public void raiseEmergencyAlertAPI(Location myLocation, String alertType) {
		try {
			final HashMap<String, String> params = new HashMap<>();
			params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
			params.put(Constants.KEY_DRIVER_ID, Data.assignedDriverInfo.userId);
			params.put(Constants.KEY_ENGAGEMENT_ID, Data.cEngagementId);
			params.put(Constants.KEY_ALERT_TYPE, alertType);

			if (myLocation != null) {
				params.put(Constants.KEY_LATITUDE, String.valueOf(myLocation.getLatitude()));
				params.put(Constants.KEY_LONGITUDE, String.valueOf(myLocation.getLongitude()));
			} else {
				params.put(Constants.KEY_LATITUDE, String.valueOf(LocationFetcher.getSavedLatFromSP(activity)));
				params.put(Constants.KEY_LONGITUDE, String.valueOf(LocationFetcher.getSavedLngFromSP(activity)));
			}

			RestClient.getApiServices().emergencyAlert(params, new Callback<SettleUserDebt>() {
				@Override
				public void success(SettleUserDebt settleUserDebt, Response response) {
					String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
					Log.i(TAG, "emergencyAlert response = " + responseStr);
					try {
					} catch (Exception exception) {
						exception.printStackTrace();
					}
				}

				@Override
				public void failure(RetrofitError error) {
					Log.e(TAG, "emergencyAlert error"+error.toString());
					Database2.getInstance(activity).insertPendingAPICall(activity,
							PendingCall.EMERGENCY_ALERT.getPath(), params);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

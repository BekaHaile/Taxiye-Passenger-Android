package product.clicklabs.jugnoo.apis;

import android.app.Activity;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.AppStatus;
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
public class ApiCampaignAvailRequest {

	private final String TAG = ApiCampaignAvailRequest.class.getSimpleName();

	private Activity activity;
	private Callback callback;

	public ApiCampaignAvailRequest(Activity activity, Callback callback){
		this.activity = activity;
		this.callback = callback;
	}

	public void availCampaign(LatLng latLng, int campaignId) {
		try {
			if(AppStatus.getInstance(activity).isOnline(activity)) {
				callback.onPre();
				HashMap<String, String> params = new HashMap<>();
				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put(Constants.KEY_LATITUDE, String.valueOf(latLng.latitude));
				params.put(Constants.KEY_LONGITUDE, String.valueOf(latLng.longitude));
				params.put(Constants.KEY_CAMPAIGN_ID, String.valueOf(campaignId));
				params.put(Constants.KEY_DATA, Constants.VALUE_CAMPAIGN_DATA_REQUEST);

				Log.i(TAG, "availCampaign params=" + params.toString());

				RestClient.getApiServices().availCampaign(params, new retrofit.Callback<SettleUserDebt>() {
					@Override
					public void success(SettleUserDebt settleUserDebt, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.i(TAG, "availCampaign response = " + responseStr);
						try {
							JSONObject jObj = new JSONObject(responseStr);
							String message = JSONParser.getServerMessage(jObj);
							if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
								int flag = jObj.optInt(Constants.KEY_FLAG, ApiResponseFlags.ACTION_COMPLETE.getOrdinal());
								JSONObject jImage = jObj.optJSONObject(Constants.KEY_IMAGE);
								if((ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag
										|| ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag)
										&& jImage != null){
									String image = jImage.optString(Constants.KEY_URL, "");
									int width = jImage.optInt(Constants.KEY_WIDTH, 748);
									int height = jImage.optInt(Constants.KEY_HEIGHT, 374);
									callback.onSuccess(message, image, width, height);
								} else{
									DialogPopup.alertPopup(activity, "", message);
									callback.onFailure();
								}
							} else{
								callback.onFailure();
							}
						} catch (Exception exception) {
							exception.printStackTrace();
							retryDialog(DialogErrorType.SERVER_ERROR);
							callback.onFailure();
						}
					}

					@Override
					public void failure(RetrofitError error) {
						Log.e(TAG, "paytmAuthenticateRecharge error" + error.toString());
						retryDialog(DialogErrorType.CONNECTION_LOST);
						callback.onFailure();
					}
				});
			} else {
				retryDialog(DialogErrorType.NO_NET);
			}
		} catch (Exception e) {
			e.printStackTrace();
			callback.onFailure();
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
		void onPre();
		void onSuccess(String message, String image, int width, int height);
		void onFailure();
		void onRetry(View view);
		void onNoRetry(View view);
	}

}

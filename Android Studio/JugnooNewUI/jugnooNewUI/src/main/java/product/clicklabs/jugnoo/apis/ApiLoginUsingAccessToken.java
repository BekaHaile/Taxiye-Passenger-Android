package product.clicklabs.jugnoo.apis;

import android.app.Activity;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.Database2;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.datastructure.LoginVia;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.LoginResponse;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by shankar on 8/19/16.
 */
public class ApiLoginUsingAccessToken {

	private Activity activity;
	private final String TAG = ApiLoginUsingAccessToken.class.getSimpleName();

	public ApiLoginUsingAccessToken(Activity activity){
		this.activity = activity;
	}


	public void hit(String accessToken, final double latitude, final double longitude, String specificClientId, final Callback callback){

		if (AppStatus.getInstance(activity).isOnline(activity)) {

			DialogPopup.showLoadingDialogDownwards(activity, "Loading...");

			if (Data.locationFetcher != null) {
				Data.loginLatitude = Data.locationFetcher.getLatitude();
				Data.loginLongitude = Data.locationFetcher.getLongitude();
			}

			HashMap<String, String> params = new HashMap<>();
			params.put(Constants.KEY_ACCESS_TOKEN, accessToken);
			params.put(Constants.KEY_DEVICE_TOKEN, MyApplication.getInstance().getDeviceToken());


			params.put(Constants.KEY_LATITUDE, String.valueOf(latitude));
			params.put(Constants.KEY_LONGITUDE, String.valueOf(longitude));


			params.put(Constants.KEY_APP_VERSION, String.valueOf(MyApplication.getInstance().appVersion()));
			params.put(Constants.KEY_DEVICE_TYPE, Data.DEVICE_TYPE);
			params.put(Constants.KEY_UNIQUE_DEVICE_ID, Data.uniqueDeviceId);
			params.put(Constants.KEY_CLIENT_ID, Config.getAutosClientId());
			params.put(Constants.KEY_IS_ACCESS_TOKEN_NEW, "1");

			if (Utils.isDeviceRooted()) {
				params.put(Constants.KEY_DEVICE_ROOTED, "1");
			} else {
				params.put(Constants.KEY_DEVICE_ROOTED, "0");
			}

			new HomeUtil().checkAndFillParamsForIgnoringAppOpen(activity, params);
			String links = Database2.getInstance(activity).getSavedLinksUpToTime(Data.BRANCH_LINK_TIME_DIFF);
			if(links != null){
				if(!"[]".equalsIgnoreCase(links)) {
					params.put(Constants.KEY_BRANCH_REFERRING_LINKS, links);
				}
			}
			if(specificClientId != null){
				params.put(Constants.KEY_SPECIFIC_CLIENT_DATA, specificClientId);
			} else {
				params.put(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getAutosClientId()));
			}
			Log.e("params login_using_access_token", "=" + params);

			final long startTime = System.currentTimeMillis();
			RestClient.getApiServices().loginUsingAccessToken(params, new retrofit.Callback<LoginResponse>() {
				@Override
				public void success(LoginResponse loginResponse, Response response) {
					FlurryEventLogger.eventApiResponseTime(FlurryEventNames.API_LOGIN_USING_ACCESS_TOKEN, startTime);
					String responseStr = new String(((TypedByteArray)response.getBody()).getBytes());
					Log.i(TAG, "loginUsingAccessToken response = " + responseStr);
					performLoginSuccess(activity, responseStr, loginResponse, callback, new LatLng(latitude, longitude));
				}

				@Override
				public void failure(RetrofitError error) {
					Log.e(TAG, "loginUsingAccessToken error="+error.toString());
					performLoginFailure(callback);
				}
			});

		} else {
			callback.noNet();
		}


	}

	public void performLoginSuccess(Activity activity, String response, LoginResponse loginResponse, final Callback callback,
									final LatLng latLng) {
		try {
			JSONObject jObj = new JSONObject(response);

			int flag = jObj.getInt(Constants.KEY_FLAG);
			String message = JSONParser.getServerMessage(jObj);
			if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
				if (ApiResponseFlags.AUTH_LOGIN_SUCCESSFUL.getOrdinal() == flag) {
					if (!SplashNewActivity.checkIfUpdate(jObj, activity)) {
						String resp;
						try {
							resp = new JSONParser().parseAccessTokenLoginData(activity, response, loginResponse, LoginVia.ACCESS, latLng);
						} catch (Exception e) {
							e.printStackTrace();
							resp = Constants.SERVER_TIMEOUT;
						}
						if (resp.contains(Constants.SERVER_TIMEOUT)) {
							retryDialog(DialogErrorType.SERVER_ERROR, callback);
							callback.failure();
						} else {
							callback.success(Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getAutosClientId()));
						}
					} else{
						callback.failure();
					}
				} else {
					DialogPopup.alertPopupTwoButtonsWithListeners(activity, "", message,
							activity.getString(R.string.retry),
							activity.getString(R.string.cancel),
							new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									callback.onRetry(v);
								}
							}, new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									callback.onNoRetry(v);
								}
							}
							, false, false);
					callback.failure();
				}
			} else{
				callback.failure();
			}
			DialogPopup.dismissLoadingDialog();
		} catch (Exception exception) {
			exception.printStackTrace();
			retryDialog(DialogErrorType.SERVER_ERROR, callback);
			DialogPopup.dismissLoadingDialog();
			callback.failure();
		}
	}

	public void performLoginFailure(Callback callback) {
		retryDialog(DialogErrorType.CONNECTION_LOST, callback);
		DialogPopup.dismissLoadingDialog();
		callback.failure();
	}

	private void retryDialog(DialogErrorType dialogErrorType, final Callback callback){
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
		void noNet();
		void success(String clientId);
		void failure();
		void onRetry(View view);
		void onNoRetry(View view);
	}

}

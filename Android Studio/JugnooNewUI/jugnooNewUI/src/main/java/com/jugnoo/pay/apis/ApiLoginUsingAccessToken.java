package com.jugnoo.pay.apis;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import com.jugnoo.pay.R;
import com.jugnoo.pay.activities.SplashNewActivity;
import com.jugnoo.pay.config.Config;
import com.jugnoo.pay.datastructure.DialogErrorType;
import com.jugnoo.pay.datastructure.LoginVia;
import com.jugnoo.pay.models.LoginResponse;
import com.jugnoo.pay.retrofit.RestClient;
import com.jugnoo.pay.utils.ApiResponseFlags;
import com.jugnoo.pay.utils.AppStatus;
import com.jugnoo.pay.utils.CallProgressWheel;
import com.jugnoo.pay.utils.Constants;
import com.jugnoo.pay.utils.Data;
import com.jugnoo.pay.utils.DialogPopup;
import com.jugnoo.pay.utils.JSONParser;
import com.jugnoo.pay.utils.MyApplication;
import com.jugnoo.pay.utils.Prefs;
import com.jugnoo.pay.utils.Utils;

import org.json.JSONObject;

import java.util.HashMap;

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


	public void hit(String accessToken, String specificClientId, final Callback callback){

		if (AppStatus.getInstance(activity).isOnline(activity)) {

			CallProgressWheel.showLoadingDialog(activity, "Loading...");


			HashMap<String, String> params = new HashMap<>();
			params.put(Constants.KEY_ACCESS_TOKEN, accessToken);
			params.put(Constants.KEY_DEVICE_TOKEN, MyApplication.getInstance().getDeviceToken());

			params.put("latitude", "" + Data.loginLatitude);
			params.put("longitude", "" + Data.loginLongitude);
			params.put(Constants.KEY_APP_VERSION, String.valueOf(Data.appVersion));
			params.put(Constants.KEY_DEVICE_TYPE, Data.DEVICE_TYPE);
			params.put(Constants.KEY_UNIQUE_DEVICE_ID, Data.uniqueDeviceId);
			params.put(Constants.KEY_CLIENT_ID, Config.getAutosClientId());
			params.put(Constants.KEY_IS_ACCESS_TOKEN_NEW, "1");

			if (Utils.isDeviceRooted()) {
				params.put(Constants.KEY_DEVICE_ROOTED, "1");
			} else {
				params.put(Constants.KEY_DEVICE_ROOTED, "0");
			}

			if(specificClientId != null){
				params.put(Constants.KEY_SPECIFIC_CLIENT_DATA, specificClientId);
			} else {
				params.put(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getAutosClientId()));
			}
			Log.e("login_using_access_token", "=" + params);

			final long startTime = System.currentTimeMillis();
			RestClient.getApiServices().loginUsingAccessToken(params, new retrofit.Callback<LoginResponse>() {
				@Override
				public void success(LoginResponse loginResponse, Response response) {
					String responseStr = new String(((TypedByteArray)response.getBody()).getBytes());
					Log.i(TAG, "loginUsingAccessToken response = " + responseStr);

						performLoginSuccess(activity, responseStr, loginResponse, callback);

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

	public void performLoginSuccess(Activity activity, String response, LoginResponse loginResponse, final Callback callback) {
		try {
			JSONObject jObj = new JSONObject(response);

			int flag = jObj.getInt(Constants.KEY_FLAG);
			String message = JSONParser.getServerMessage(jObj);
			if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
				if (ApiResponseFlags.AUTH_LOGIN_SUCCESSFUL.getOrdinal() == flag) {
					if (!SplashNewActivity.checkIfUpdate(jObj, activity)) {
						try {
							new JSONParser().parseAccessTokenLoginData(activity, response, loginResponse, LoginVia.ACCESS);
						} catch (Exception e) {
							e.printStackTrace();
						}
						callback.success(Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getAutosClientId()));
					} else{
						callback.failure();
					}
				} else if(loginResponse.getFlag() == ApiResponseFlags.VPA_NOT_FOUND.getOrdinal()){
					callback.vpaNotFound(loginResponse);
				}else {
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
			CallProgressWheel.dismissLoadingDialog();
		} catch (Exception exception) {
			exception.printStackTrace();
			retryDialog(DialogErrorType.SERVER_ERROR, callback);
			CallProgressWheel.dismissLoadingDialog();
			callback.failure();
		}
	}

	public void performLoginFailure(Callback callback) {
		retryDialog(DialogErrorType.CONNECTION_LOST, callback);
		CallProgressWheel.dismissLoadingDialog();
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
		void vpaNotFound(LoginResponse loginResponse);
	}

}

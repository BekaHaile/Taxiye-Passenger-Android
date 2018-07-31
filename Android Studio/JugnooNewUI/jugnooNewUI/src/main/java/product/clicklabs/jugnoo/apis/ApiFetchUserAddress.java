package product.clicklabs.jugnoo.apis;

import android.app.Activity;
import android.view.View;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.FetchUserAddressResponse;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by shankar on 3/20/16.
 */
public class ApiFetchUserAddress {

	private final String TAG = ApiFetchUserAddress.class.getSimpleName();

	private Activity activity;
	private Callback callback;

	public ApiFetchUserAddress(Activity activity, Callback callback){
		this.activity = activity;
		this.callback = callback;
	}

	public void hit(boolean showDialog) {
		try {
			if (MyApplication.getInstance().isOnline()) {

				if(showDialog) {
					DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));
				}

				HashMap<String, String> params = new HashMap<>();
				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put(Constants.KEY_CLIENT_ID, Config.getAutosClientId());

				new HomeUtil().putDefaultParams(params);
				RestClient.getApiService().customerFetchUserAddress(params, new retrofit.Callback<FetchUserAddressResponse>() {
					@Override
					public void success(FetchUserAddressResponse fetchUserAddressResponse, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						try {
							JSONObject jObj = new JSONObject(responseStr);
							int flag = jObj.optInt(Constants.KEY_FLAG, ApiResponseFlags.ACTION_COMPLETE.getOrdinal());
							String message = JSONParser.getServerMessage(jObj);
							if (flag == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
								new JSONParser().parseSavedAddressesFromNew(activity, fetchUserAddressResponse);
								Data.setRecentAddressesFetched(true);
								callback.onSuccess();
							} else {
								DialogPopup.alertPopup(activity, "", message);
							}
						} catch (Exception e) {
							e.printStackTrace();
							retryDialog(DialogErrorType.SERVER_ERROR);
						}
						DialogPopup.dismissLoadingDialog();
						callback.onFinish();
					}

					@Override
					public void failure(RetrofitError error) {
						Log.e(TAG, "customerFetchUserAddress error=" + error.toString());
						DialogPopup.dismissLoadingDialog();
						retryDialog(DialogErrorType.CONNECTION_LOST);
						callback.onFailure();
						callback.onFinish();
					}
				});

			} else {
				retryDialog(DialogErrorType.NO_NET);
				callback.onFinish();
			}
		} catch (Exception e) {
			e.printStackTrace();
			callback.onFinish();
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
		void onSuccess();
		void onFailure();
		void onRetry(View view);
		void onNoRetry(View view);
		void onFinish();
	}


}

package product.clicklabs.jugnoo.apis;

import android.app.Activity;
import android.app.ProgressDialog;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;

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
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by shankar on 3/20/16.
 */
public class ApiFetchWalletBalance {

	private final String TAG = ApiFetchWalletBalance.class.getSimpleName();

	private Activity activity;
	private Callback callback;

	public ApiFetchWalletBalance(Activity activity, Callback callback){
		this.activity = activity;
		this.callback = callback;
	}


	public void getBalance(final boolean showDialog, final boolean isFromFatafatChatPay, LatLng latLng){
		try {
			if (MyApplication.getInstance().isOnline()) {

				ProgressDialog progressDialog = null;
				if (showDialog) {
					progressDialog = DialogPopup.showLoadingDialogNewInstance(activity, activity.getResources().getString(R.string.loading));
				}

				HashMap<String, String> params = new HashMap<>();
				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put(Constants.KEY_CLIENT_ID, isFromFatafatChatPay? Config.getFeedClientId():Config.getLastOpenedClientId(activity));
				params.put(Constants.KEY_IS_ACCESS_TOKEN_NEW, "1");
				params.put(Constants.KEY_LATITUDE, String.valueOf(latLng == null ? Data.latitude : latLng.latitude));
				params.put(Constants.KEY_LONGITUDE, String.valueOf(latLng == null ? Data.longitude : latLng.longitude));

				final long startTime = System.currentTimeMillis();
				final ProgressDialog finalProgressDialog = progressDialog;

				new HomeUtil().putDefaultParams(params);
				RestClient.getApiService().fetchWalletBalance(params, new retrofit.Callback<SettleUserDebt>() {
					@Override
					public void success(SettleUserDebt settleUserDebt, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.i(TAG, "fetchWalletBalance response = " + responseStr);
						try {
							JSONObject jObj = new JSONObject(responseStr);
							if (Data.userData != null) {
								int flag = jObj.optInt(Constants.KEY_FLAG, ApiResponseFlags.ACTION_COMPLETE.getOrdinal());
								String message = JSONParser.getServerMessage(jObj);
								if(flag == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()){
									Data.userData.updateWalletBalances(jObj, true);
									if(jObj.has(Constants.KEY_UPI_HANDLE)){
										Data.userData.setUpiHandle(jObj.getString(Constants.KEY_UPI_HANDLE));
									}
									MyApplication.getInstance().getWalletCore().parsePaymentModeConfigDatas(jObj);
									Prefs.with(activity).save(SPLabels.CHECK_BALANCE_LAST_TIME, System.currentTimeMillis());
								} else {
									DialogPopup.alertPopup(activity, "", message);
								}
							}
							callback.onSuccess();
						} catch (Exception e) {
							e.printStackTrace();
							retryDialog(DialogErrorType.SERVER_ERROR);
						}
						try{if(showDialog && finalProgressDialog != null){
							finalProgressDialog.dismiss();
						}} catch (Exception e){}
						callback.onFinish();
					}

					@Override
					public void failure(RetrofitError error) {
						Log.e(TAG, "fetchWalletBalance error=" + error.toString());
						try{if(showDialog && finalProgressDialog != null){
							finalProgressDialog.dismiss();
						}} catch (Exception e){}
						retryDialog(DialogErrorType.CONNECTION_LOST);
						callback.onFailure();
					}
				});

			} else {
				retryDialog(DialogErrorType.NO_NET);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getBalance(final boolean showDialog) {
		getBalance(showDialog,false, null);
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
		void onFinish();
		void onFailure();
		void onRetry(View view);
		void onNoRetry(View view);
	}


}

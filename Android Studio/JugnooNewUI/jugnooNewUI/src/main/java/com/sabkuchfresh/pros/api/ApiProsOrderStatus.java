package com.sabkuchfresh.pros.api;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;

import com.sabkuchfresh.pros.models.ProsOrderStatusResponse;

import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.datastructure.ProductType;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by shankar on 24/07/17.
 */

public class ApiProsOrderStatus {

	private Callback callback;

	public ApiProsOrderStatus(Callback callback){
		this.callback = callback;
	}

	public void getOrderData(final Activity activity, final int jobId) {
		try {
			if (MyApplication.getInstance().isOnline()) {
				DialogPopup.showLoadingDialog(activity, activity.getString(R.string.loading));
				HashMap<String, String> params = new HashMap<>();
				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put(Constants.KEY_JOB_ID, String.valueOf(jobId));
				params.put(Constants.KEY_PRODUCT_TYPE, String.valueOf(ProductType.PROS.getOrdinal()));
				params.put(Constants.KEY_CLIENT_ID, "" + Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()));

				retrofit.Callback<ProsOrderStatusResponse> callback = new retrofit.Callback<ProsOrderStatusResponse>() {
					@Override
					public void success(ProsOrderStatusResponse orderStatusResponse, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.i("Server response", "response = " + responseStr);
						try {
							if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, orderStatusResponse.getFlag(), orderStatusResponse.getError(), orderStatusResponse.getMessage())) {
								if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == orderStatusResponse.getFlag()) {
									ApiProsOrderStatus.this.callback.onSuccess(orderStatusResponse);
								} else {
									retryDialogOrderData(activity, jobId, orderStatusResponse.getMessage(), DialogErrorType.SERVER_ERROR);
								}
							}
						} catch (Exception exception) {
							exception.printStackTrace();
							retryDialogOrderData(activity, jobId, "", DialogErrorType.SERVER_ERROR);
						}
						DialogPopup.dismissLoadingDialog();
					}

					@Override
					public void failure(RetrofitError error) {
						Log.e("TAG", "getRecentRidesAPI error=" + error.toString());
						DialogPopup.dismissLoadingDialog();
						retryDialogOrderData(activity, jobId, "", DialogErrorType.CONNECTION_LOST);
					}
				};

				new HomeUtil().putDefaultParams(params);
				RestClient.getProsApiService().orderHistory(params, callback);
			} else {
				retryDialogOrderData(activity, jobId, "", DialogErrorType.NO_NET);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void retryDialogOrderData(final Activity activity, final int jobId, String message, DialogErrorType dialogErrorType) {
		if (TextUtils.isEmpty(message)) {
			DialogPopup.dialogNoInternet(activity,
					dialogErrorType,
					new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
						@Override
						public void positiveClick(View view) {
							getOrderData(activity, jobId);
						}

						@Override
						public void neutralClick(View view) {

						}

						@Override
						public void negativeClick(View view) {
						}
					});
		} else {
			DialogPopup.alertPopupTwoButtonsWithListeners(activity, "", message,
					activity.getString(R.string.retry), activity.getString(R.string.cancel),
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							getOrderData(activity, jobId);
						}
					},
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							callback.onNoRetry();
						}
					}, false, false);
		}
	}


	public interface Callback{
		void onNoRetry();
		void onSuccess(ProsOrderStatusResponse orderStatusResponse);
	}
}

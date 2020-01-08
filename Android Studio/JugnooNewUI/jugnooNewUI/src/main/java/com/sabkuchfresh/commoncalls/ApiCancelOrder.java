package com.sabkuchfresh.commoncalls;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;

import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.datastructure.ProductType;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Prefs;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by shankar on 16/03/17.
 */

public class ApiCancelOrder {

	private final String TAG = ApiCancelOrder.class.getSimpleName();

	private Activity activity;
	private Callback callback;

	public ApiCancelOrder(Activity activity, Callback callback) {
		this.activity = activity;
		this.callback = callback;
	}

	public void hit(final int orderId, final String clientId, final int storeId, final int productType){
		hit(orderId, clientId, storeId, productType, "", "");
	}

	public void hit(final int orderId, final String clientId, final int storeId, final int productType,
					String reasons, String addnReasons) {
		try {
			if (MyApplication.getInstance().isOnline()) {
				DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));

				HashMap<String, String> params = new HashMap<>();
				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put(Constants.KEY_FRESH_ORDER_ID, String.valueOf(orderId));
				params.put(Constants.KEY_CLIENT_ID, clientId);
				params.put(Constants.INTERATED, "1");

				if(!TextUtils.isEmpty(reasons)) {
					params.put(Constants.KEY_REASONS, reasons);
					params.put(Constants.KEY_ADDN_REASON, addnReasons);
				}

				if (storeId > 0) {
					params.put(Constants.STORE_ID, String.valueOf(storeId));
				}

				final retrofit.Callback<SettleUserDebt> callback = new retrofit.Callback<SettleUserDebt>() {
					@Override
					public void success(SettleUserDebt settleUserDebt, Response response) {
//						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						DialogPopup.dismissLoadingDialog();
						long time = 0L;
						Prefs.with(activity).save(SPLabels.CHECK_BALANCE_LAST_TIME, time);
						try {
							String message = settleUserDebt.getMessage();
							if (settleUserDebt.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
								ApiCancelOrder.this.callback.onSuccess(message);
							} else {
								DialogPopup.alertPopup(activity, "", message);
							}
						} catch (Exception exception) {
							exception.printStackTrace();
							retryDialog(DialogErrorType.SERVER_ERROR);
						}
						DialogPopup.dismissLoadingDialog();
					}

					@Override
					public void failure(RetrofitError error) {
						DialogPopup.dismissLoadingDialog();
						retryDialog(DialogErrorType.CONNECTION_LOST);
						ApiCancelOrder.this.callback.onFailure();
					}
				};

				new HomeUtil().putDefaultParams(params);
				if (productType == ProductType.MENUS.getOrdinal() || productType == ProductType.DELIVERY_CUSTOMER.getOrdinal()) {
					RestClient.getMenusApiService().cancelOrder(params, callback);
				} else if(productType == ProductType.FEED.getOrdinal()) {
					// if we come from feed, hit fatafat service base ( valid for pay via chat )
					RestClient.getFatafatApiService().cancelOrder(params,callback);
				} else {
					RestClient.getFreshApiService().cancelOrder(params, callback);
				}
			} else {
				retryDialog(DialogErrorType.NO_NET);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void retryDialog(DialogErrorType dialogErrorType) {
		DialogPopup.dialogNoInternet(activity,
				dialogErrorType,
				new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
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


	public interface Callback {
		void onSuccess(String message);

		void onFailure();

		void onRetry(View view);

		void onNoRetry(View view);
	}
}

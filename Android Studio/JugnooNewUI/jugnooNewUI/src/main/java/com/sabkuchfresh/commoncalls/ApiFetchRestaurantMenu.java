package com.sabkuchfresh.commoncalls;

import android.view.View;

import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.menus.MenusResponse;
import com.sabkuchfresh.retrofit.model.menus.VendorMenuResponse;
import com.sabkuchfresh.utils.AppConstant;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * For fetching feedback reviews for a particular restaurant
 */
public class ApiFetchRestaurantMenu {

	private final String TAG = ApiFetchRestaurantMenu.class.getSimpleName();

	private FreshActivity activity;
	private Callback callback;

	public ApiFetchRestaurantMenu(FreshActivity activity, Callback callback){
		this.activity = activity;
		this.callback = callback;
	}

	public void hit(final int restaurantId, final double latitude, final double longitude, final int restaurantInfo,
					final MenusResponse.Vendor vendor) {
		try {
			if (MyApplication.getInstance().isOnline()) {
				DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));

				HashMap<String, String> params = new HashMap<>();
				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put(Constants.KEY_LATITUDE, String.valueOf(latitude));
				params.put(Constants.KEY_LONGITUDE, String.valueOf(longitude));
				params.put(Constants.KEY_RESTAURANT_ID, String.valueOf(restaurantId));
				params.put(Constants.KEY_CLIENT_ID, Config.getMenusClientId());
				params.put(Constants.INTERATED, "1");
				params.put(Constants.KEY_RESTAURANT_INFO, String.valueOf(restaurantInfo));
				Log.i(TAG, "restaurantMenu params=" + params.toString());

				new HomeUtil().putDefaultParams(params);
				RestClient.getMenusApiService().restaurantMenu(params, new retrofit.Callback<VendorMenuResponse>() {
					@Override
					public void success(VendorMenuResponse productsResponse, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.i(TAG, "getVendorMenu response = " + responseStr);
						try {
							JSONObject jObj = new JSONObject(responseStr);
							String message = productsResponse.getMessage();
							if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
								if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == productsResponse.getFlag()) {
									if(vendor == null && restaurantInfo == 1){
										activity.setVendorOpened(productsResponse.getVendor());
										if(activity.getAppType() == AppConstant.ApplicationType.FEED) {
											Prefs.with(activity).save(Constants.APP_TYPE, AppConstant.ApplicationType.MENUS);
											Prefs.with(activity).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getMenusClientId());
										}
									} else {
										activity.setVendorOpened(vendor);
									}
									activity.setMenuProductsResponse(productsResponse);
									if (activity.menusSort == -1) {
										activity.menusSort = jObj.getInt(Constants.SORTED_BY);
									}
									if (activity.getVendorOpened().getIsClosed() == 1) {
										activity.clearMenusCart();
									}
									GAUtils.event(GACategory.MENUS, GAAction.HOME + GAAction.RESTAURANT_CLICKED, activity.getVendorOpened().getName());
									activity.getTransactionUtils().openVendorMenuFragment(activity, activity.getRelativeLayoutContainer());
									activity.getFabViewTest().hideJeanieHelpInSession();
									callback.onSuccess();
								} else {
									DialogPopup.alertPopup(activity, "", message);
								}
							}
						} catch (Exception exception) {
							exception.printStackTrace();
						}
						DialogPopup.dismissLoadingDialog();
					}

					@Override
					public void failure(RetrofitError error) {
						Log.e(TAG, "paytmAuthenticateRecharge error" + error.toString());
						DialogPopup.dismissLoadingDialog();
						retryDialogVendorData(DialogErrorType.CONNECTION_LOST);
					}
				});
			} else {
				retryDialogVendorData(DialogErrorType.NO_NET);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void retryDialogVendorData(DialogErrorType dialogErrorType) {
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


	public interface Callback{
		void onSuccess();
		void onFailure();
		void onRetry(View view);
		void onNoRetry(View view);
	}

}

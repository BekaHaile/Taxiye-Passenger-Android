package product.clicklabs.jugnoo.apis;

import android.app.Activity;
import android.app.ProgressDialog;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.datastructure.EndRideData;
import product.clicklabs.jugnoo.datastructure.EngagementStatus;
import product.clicklabs.jugnoo.datastructure.ProductType;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.HistoryResponse;
import product.clicklabs.jugnoo.support.ParseUtils;
import product.clicklabs.jugnoo.support.models.ShowPanelResponse;
import product.clicklabs.jugnoo.support.models.SupportCategory;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by shankar on 2/19/16.
 */
public class ApiGetRideSummary {

	private final String TAG = ApiGetRideSummary.class.getSimpleName();

	private Activity activity;
	private double fixedFare;
	private Callback callback;
	private String accessToken;
	private int engagementId;
	private int orderId;

	public ApiGetRideSummary(Activity activity, String accessToken, int engagementId, int orderId, double fixedFare, Callback callback){
		this.activity = activity;
		this.accessToken = accessToken;
		this.engagementId = engagementId;
		this.orderId = orderId;
		this.fixedFare = fixedFare;
		this.callback = callback;
	}

	public void getRideSummaryAPI(int supportCategory, final ProductType productType, final boolean fromOrderHistory) {
		boolean showRideMenu = true;
		String savedSupportVersion = Prefs.with(activity).getString(Constants.KEY_SP_TRANSACTION_SUPPORT_PANEL_VERSION, "-1");
		if(savedSupportVersion.equalsIgnoreCase(Data.userData.getInAppSupportPanelVersion())){
			showRideMenu = false;
		}
		if (!HomeActivity.checkIfUserDataNull(activity) && MyApplication.getInstance().isOnline()) {
			final ProgressDialog progressDialog = DialogPopup.showLoadingDialogNewInstance(activity, activity.getResources().getString(R.string.loading));

			HashMap<String, String> params = new HashMap<>();
			params.put(Constants.KEY_ACCESS_TOKEN, accessToken);
			if(engagementId != -1) {
				params.put(Constants.KEY_ENGAGEMENT_ID, String.valueOf(engagementId));
			} else if(orderId != -1) {
				params.put(Constants.KEY_ORDER_ID, String.valueOf(orderId));
			}
			params.put(Constants.KEY_PRODUCT_TYPE, String.valueOf(productType.getOrdinal()));
			if(showRideMenu) {
				params.put(Constants.KEY_SHOW_RIDE_MENU, "1");
			}
			if(fromOrderHistory){
				params.put(Constants.KEY_ORDER_HISTORY, "1");
			}
			if(productType == ProductType.AUTO){
				supportCategory = getSupportCategoryForEngagementStatus(supportCategory);
			}

			final boolean finalShowRideMenu = showRideMenu;
			final int finalSupportCategory = supportCategory;

			new HomeUtil().putDefaultParams(params);
			RestClient.getApiService().getRideSummary(params, new retrofit.Callback<ShowPanelResponse>() {
				@Override
				public void success(ShowPanelResponse showPanelResponse, Response response) {
					try {
						if(progressDialog != null) {
							progressDialog.dismiss();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.i(TAG, "getRideSummary jsonString=" + jsonString);
						JSONObject jObj = new JSONObject(jsonString);
						if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
							int flag = jObj.getInt(Constants.KEY_FLAG);
							String message = JSONParser.getServerMessage(jObj);
							if (ApiResponseFlags.RIDE_ENDED.getOrdinal() == flag) {
								EndRideData endRideData = null;
								try {
									endRideData = JSONParser.parseEndRideData(jObj, String.valueOf(engagementId), fixedFare);
								} catch (Exception e) {
									e.printStackTrace();
								}
								HistoryResponse.Datum finalDatum = null;
								if (productType != ProductType.MENUS && productType != ProductType.DELIVERY_CUSTOMER
										&& showPanelResponse.getDatum() != null && showPanelResponse.getDatum().getProductType() != null) {
									finalDatum = showPanelResponse.getDatum();
								} else if(productType == ProductType.MENUS && showPanelResponse.getMenusDatum() != null && showPanelResponse.getMenusDatum().getProductType() != null){
									finalDatum = showPanelResponse.getMenusDatum();
								} else if(productType == ProductType.DELIVERY_CUSTOMER && showPanelResponse.getDeliveryCustomerDatum() != null && showPanelResponse.getDeliveryCustomerDatum().getProductType() != null){
									finalDatum = showPanelResponse.getDeliveryCustomerDatum();
								}
								int supportCategory = finalSupportCategory;
								if(productType == ProductType.NOT_SURE || supportCategory == SupportCategory.NOT_SURE.getOrdinal()) {
									if (endRideData != null) {
										supportCategory = getSupportCategoryForEngagementStatus(endRideData.getStatus());
									} else if (finalDatum != null) {
										supportCategory = finalDatum.getSupportCategory();
									}
								}
								ArrayList<ShowPanelResponse.Item> itemsMain = null;
								if(finalShowRideMenu){
									itemsMain = new ParseUtils().saveAndParseAllMenu(activity, showPanelResponse, supportCategory);
									Prefs.with(activity).save(Constants.KEY_SP_TRANSACTION_SUPPORT_PANEL_VERSION,
											Data.userData.getInAppSupportPanelVersion());
								} else{
									itemsMain = MyApplication.getInstance().getDatabase2().getSupportDataItems(supportCategory);
								}
								//todo try with feedbackinfo model
								if(jObj.has(Constants.KEY_FEEDBACK_INFO))
								{
									JSONArray jsonArray=jObj.getJSONArray(Constants.KEY_FEEDBACK_INFO);
									JSONParser.parseFeedBackInfo(jsonArray);
								}
								callback.onSuccess(endRideData, finalDatum, itemsMain);

							} else if(ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag) {
								if(callback.onActionFailed(message)){
									retryDialog(message);
								}
							} else{
								retryDialog(DialogErrorType.NO_NET);
							}
						}
					} catch (Exception exception) {
						exception.printStackTrace();
						retryDialog(DialogErrorType.NO_NET);
					}
				}

				@Override
				public void failure(RetrofitError error) {
					Log.e(TAG, "getRideSummary error=>"+error.toString());
					try {
						if(progressDialog != null) {
							progressDialog.dismiss();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					retryDialog(DialogErrorType.NO_NET);
					callback.onFailure();
				}
			});
		} else {
			retryDialog(DialogErrorType.NO_NET);
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

	private void retryDialog(String message){
		DialogPopup.alertPopupTwoButtonsWithListeners(activity, "", message,
				activity.getResources().getString(R.string.retry),
				activity.getResources().getString(R.string.cancel),
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						callback.onRetry(v);
					}
				},
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						callback.onNoRetry(v);
					}
				}, false, false);
	}


	public interface Callback{
		void onSuccess(EndRideData endRideData, HistoryResponse.Datum datum, ArrayList<ShowPanelResponse.Item> items);
		boolean onActionFailed(String message);
		void onFailure();
		void onRetry(View view);
		void onNoRetry(View view);
	}



	private int getSupportCategoryForEngagementStatus(int supportCategory){
		if(supportCategory == EngagementStatus.ACCEPTED_THEN_REJECTED.getOrdinal()){
			supportCategory = SupportCategory.RIDE_CANCELLED_DRIVER_MENU.getOrdinal();
		}
		else if(supportCategory == EngagementStatus.RIDE_CANCELLED_BY_CUSTOMER.getOrdinal()){
			supportCategory = SupportCategory.RIDE_CANCELLED_USER_MENU.getOrdinal();
		}
		else if(supportCategory == EngagementStatus.ACCEPTED.getOrdinal()){
			supportCategory = SupportCategory.RIDE_ACCEPT.getOrdinal();
		}
		else if(supportCategory == EngagementStatus.ARRIVED.getOrdinal()){
			supportCategory = SupportCategory.RIDE_ARRIVED.getOrdinal();
		}
		else if(supportCategory == EngagementStatus.STARTED.getOrdinal()){
			supportCategory = SupportCategory.RIDE_STARTED.getOrdinal();
		}
		else {
			supportCategory = SupportCategory.RIDE_MENU.getOrdinal();
		}
		return supportCategory;
	}

}

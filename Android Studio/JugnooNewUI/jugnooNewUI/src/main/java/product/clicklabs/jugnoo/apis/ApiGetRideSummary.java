package product.clicklabs.jugnoo.apis;

import android.app.Activity;
import android.app.ProgressDialog;
import android.view.View;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.Database2;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.datastructure.EndRideData;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.support.ParseUtils;
import product.clicklabs.jugnoo.support.models.ShowPanelResponse;
import product.clicklabs.jugnoo.utils.AppStatus;
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

	public ApiGetRideSummary(Activity activity, String accessToken, int engagementId, double fixedFare, Callback callback){
		this.activity = activity;
		this.accessToken = accessToken;
		this.engagementId = engagementId;
		this.fixedFare = fixedFare;
		this.callback = callback;
	}

	public void getRideSummaryAPI(final int supportCategory) {
		boolean showRideMenu = true;
		String savedSupportVersion = Prefs.with(activity).getString(Constants.KEY_SP_IN_APP_SUPPORT_PANEL_VERSION, "-1");
		if(savedSupportVersion.equalsIgnoreCase(Data.userData.getInAppSupportPanelVersion())){
			showRideMenu = false;
		}
		if (!HomeActivity.checkIfUserDataNull(activity) && AppStatus.getInstance(activity).isOnline(activity)) {
			final ProgressDialog progressDialog = DialogPopup.showLoadingDialogNewInstance(activity, activity.getResources().getString(R.string.loading));

			HashMap<String, String> params = new HashMap<>();
			params.put(Constants.KEY_ACCESS_TOKEN, accessToken);
			if(engagementId != -1) {
				params.put(Constants.KEY_ENGAGEMENT_ID, String.valueOf(engagementId));
			}
			if(showRideMenu) {
				params.put(Constants.KEY_SHOW_RIDE_MENU, "1");
			}

			final boolean finalShowRideMenu = showRideMenu;
			RestClient.getApiServices().getRideSummary(params, new retrofit.Callback<ShowPanelResponse>() {
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
								ArrayList<ShowPanelResponse.Item> itemsMain = null;
								if(finalShowRideMenu){
									itemsMain = new ParseUtils().saveAndParseAllMenu(activity, showPanelResponse, supportCategory);
									Prefs.with(activity).save(Constants.KEY_SP_IN_APP_SUPPORT_PANEL_VERSION,
											Data.userData.getInAppSupportPanelVersion());
								} else{
									itemsMain = Database2.getInstance(activity).getSupportDataItems(supportCategory);
								}
								callback.onSuccess(JSONParser.parseEndRideData(jObj, String.valueOf(engagementId), fixedFare), itemsMain);
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
		void onSuccess(EndRideData endRideData, ArrayList<ShowPanelResponse.Item> items);
		boolean onActionFailed(String message);
		void onFailure();
		void onRetry(View view);
		void onNoRetry(View view);
	}

}

package product.clicklabs.jugnoo.apis;

import android.app.Activity;
import android.view.View;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.Database2;
import product.clicklabs.jugnoo.HomeActivity;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.datastructure.EndRideData;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.support.models.GetRideSummaryResponse;
import product.clicklabs.jugnoo.support.models.ShowPanelResponse;
import product.clicklabs.jugnoo.support.models.SupportCategory;
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

	public void getRideSummaryAPI() {
		boolean showRideMenu = true;
		int savedSupportVersion = Prefs.with(activity).getInt(Constants.KEY_SP_IN_APP_SUPPORT_PANEL_VERSION, -1);
		if(savedSupportVersion == Data.userData.getInAppSupportPanelVersion()){
			showRideMenu = false;
		}
		if (!HomeActivity.checkIfUserDataNull(activity) && AppStatus.getInstance(activity).isOnline(activity)) {
			DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));

			HashMap<String, String> params = new HashMap<>();
			params.put(Constants.KEY_ACCESS_TOKEN, accessToken);
			if(engagementId != -1) {
				params.put(Constants.KEY_ENGAGEMENT_ID, String.valueOf(engagementId));
			}
			if(showRideMenu){
				params.put(Constants.KEY_SHOW_RIDE_MENU, "1");
			}

			final boolean finalShowRideMenu = showRideMenu;
			RestClient.getApiServices().getRideSummary(params, new retrofit.Callback<GetRideSummaryResponse>() {
				@Override
				public void success(GetRideSummaryResponse getRideSummaryResponse, Response response) {
					DialogPopup.dismissLoadingDialog();
					try {
						String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.i(TAG, "getRideSummary jsonString=" + jsonString);
						JSONObject jObj = new JSONObject(jsonString);
						if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
							int flag = jObj.getInt(Constants.KEY_FLAG);
							if (ApiResponseFlags.RIDE_ENDED.getOrdinal() == flag) {
								if(!finalShowRideMenu){
									ArrayList<ShowPanelResponse.Item> menu = Database2.getInstance(activity)
											.getSupportDataItems(SupportCategory.RIDE_MENU.getOrdinal());
									getRideSummaryResponse.setMenu(menu);
								} else{
									Prefs.with(activity).save(Constants.KEY_SP_IN_APP_SUPPORT_PANEL_VERSION,
											Data.userData.getInAppSupportPanelVersion());
									Database2.getInstance(activity)
											.insertUpdateSupportData(SupportCategory.RIDE_MENU.getOrdinal(),
													getRideSummaryResponse.getMenu());
								}
								callback.onSuccess(JSONParser.parseEndRideData(jObj, String.valueOf(engagementId), fixedFare), getRideSummaryResponse);
							} else {
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
					DialogPopup.dismissLoadingDialog();
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


	public interface Callback{
		void onSuccess(EndRideData endRideData, GetRideSummaryResponse getRideSummaryResponse);
		void onFailure();
		void onRetry(View view);
		void onNoRetry(View view);
	}

}

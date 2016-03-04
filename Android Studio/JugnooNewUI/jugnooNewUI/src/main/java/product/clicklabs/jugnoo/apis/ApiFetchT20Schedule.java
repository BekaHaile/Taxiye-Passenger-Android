package product.clicklabs.jugnoo.apis;

import android.app.Activity;
import android.view.View;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.HomeActivity;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.t20.models.MatchScheduleResponse;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by shankar on 2/19/16.
 */
public class ApiFetchT20Schedule {

	private final String TAG = ApiFetchT20Schedule.class.getSimpleName();

	private Activity activity;
	private Callback callback;

	public ApiFetchT20Schedule(Activity activity, Callback callback){
		this.activity = activity;
		this.callback = callback;
	}

	public void fetchT20ScheduleAndUserSelections() {
		if (!HomeActivity.checkIfUserDataNull(activity) && AppStatus.getInstance(activity).isOnline(activity)) {
			DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));

			HashMap<String, String> params = new HashMap<>();
			params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);

			RestClient.getApiServices().fetchT20ScheduleAndUserSelections(params, new retrofit.Callback<MatchScheduleResponse>() {
				@Override
				public void success(MatchScheduleResponse matchScheduleResponse, Response response) {
					DialogPopup.dismissLoadingDialog();
					try {
						String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.i(TAG, "fetchT20ScheduleAndUserSelections jsonString=" + jsonString);
						JSONObject jObj = new JSONObject(jsonString);
						if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
							String message = JSONParser.getServerMessage(jObj);
							if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == matchScheduleResponse.getFlag()){
								callback.onSuccess(matchScheduleResponse);
							} else{
								DialogPopup.alertPopup(activity, "", message);
							}
						}
					} catch (Exception exception) {
						exception.printStackTrace();
						retryDialog(DialogErrorType.NO_NET);
					}
				}

				@Override
				public void failure(RetrofitError error) {
					Log.e(TAG, "getRideSummary error=>" + error.toString());
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
		void onSuccess(MatchScheduleResponse matchScheduleResponse);
		void onFailure();
		void onRetry(View view);
		void onNoRetry(View view);
	}

}

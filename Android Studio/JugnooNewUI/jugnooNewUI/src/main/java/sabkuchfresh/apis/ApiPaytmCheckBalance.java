package sabkuchfresh.apis;

import android.app.Activity;
import android.view.View;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import sabkuchfresh.retrofit.RestClient;

/**
 * Created by shankar on 3/20/16.
 */
public class ApiPaytmCheckBalance {

	private final String TAG = ApiPaytmCheckBalance.class.getSimpleName();

	private Activity activity;
	private Callback callback;

	public ApiPaytmCheckBalance(Activity activity, Callback callback){
		this.activity = activity;
		this.callback = callback;
	}

	public void getBalance(int paytmEnabled, boolean showDialog) {
		try {
			if(1 == paytmEnabled || true) {
				if (AppStatus.getInstance(activity).isOnline(activity)) {

					if(showDialog) {
						DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));
					}

					HashMap<String, String> params = new HashMap<>();
					params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
					params.put(Constants.KEY_CLIENT_ID, Config.getClientId());
					params.put(Constants.KEY_IS_ACCESS_TOKEN_NEW, "1");

					final long startTime = System.currentTimeMillis();
					RestClient.getApiServices().paytmCheckBalance(params, new retrofit.Callback<SettleUserDebt>() {
						@Override
						public void success(SettleUserDebt settleUserDebt, Response response) {
							String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
							Log.i(TAG, "paytmCheckBalance response = " + responseStr);
							try {
								JSONObject jObj = new JSONObject(responseStr);
								JSONParser.parsePaytmBalanceStatus(activity, jObj);
								Data.userData.setJugnooBalance(jObj.optDouble(Constants.KEY_JUGNOO_BALANCE, Data.userData.getJugnooBalance()));
								callback.onSuccess();
							} catch (Exception e) {
								e.printStackTrace();
								retryDialog(DialogErrorType.SERVER_ERROR);
							}
							DialogPopup.dismissLoadingDialog();
							callback.onFinish();
						}

						@Override
						public void failure(RetrofitError error) {
							Log.e(TAG, "paytmCheckBalance error=" + error.toString());
							DialogPopup.dismissLoadingDialog();
							retryDialog(DialogErrorType.CONNECTION_LOST);
							callback.onFailure();
						}
					});

				} else {
					retryDialog(DialogErrorType.NO_NET);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
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
		void onFinish();
		void onFailure();
		void onRetry(View view);
		void onNoRetry(View view);
	}


}

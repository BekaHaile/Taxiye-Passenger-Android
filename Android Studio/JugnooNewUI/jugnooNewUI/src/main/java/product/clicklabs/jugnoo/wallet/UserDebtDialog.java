package product.clicklabs.jugnoo.wallet;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.datastructure.AddPaymentPath;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Log;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by shankar on 1/4/16.
 */
public class UserDebtDialog {

	private Activity activity;
	public UserDebtDialog(Activity activity){
		this.activity = activity;
	}

	public void showUserDebtDialog(final double paytmBalance, final double userDebt) {
		DialogPopup.alertPopupWithListener(activity, "",
				String.format(activity.getResources().getString(R.string.user_debt_settle_balance_message),
						userDebt),
				activity.getResources().getString(R.string.user_debt_pay_via_paytm),
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if(paytmBalance >= userDebt){
							settleUserDebt(activity, userDebt);
						}
						else{
							Intent intent = new Intent(activity, PaymentActivity.class);
							intent.putExtra(Constants.KEY_ADD_PAYMENT_PATH, AddPaymentPath.PAYTM_RECHARGE.getOrdinal());
							activity.startActivity(intent);
							activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
							FlurryEventLogger.event(FlurryEventNames.USER_DEBT_MAKE_PAYMENT);
						}
					}
				});
	}

	private void settleUserDebt(final Activity activity, double userDebt) {
		if (AppStatus.getInstance(activity).isOnline(activity)) {

			DialogPopup.showLoadingDialog(activity, "Loading...");

			HashMap<String, String> params = new HashMap<>();
			params.put("access_token", Data.userData.accessToken);
			params.put("user_debt", "" + userDebt);
			Log.i("params", "=" + params);

			RestClient.getApiServices().settleUserDebt(params, new Callback<SettleUserDebt>() {
				@Override
				public void success(SettleUserDebt settleUserDebt, Response response) {
					Log.e("Server response settle_user_debt", "response = " + response);
					try {
						String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
						JSONObject jObj = new JSONObject(jsonString);
						if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
							int flag = jObj.getInt("flag");
							String message = JSONParser.getServerMessage(jObj);
							if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
								DialogPopup.alertPopup(activity, "", message);
							} else {
								DialogPopup.alertPopup(activity, "", message);
							}
						}
					} catch (Exception exception) {
						exception.printStackTrace();
						DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
					}
					DialogPopup.dismissLoadingDialog();
				}

				@Override
				public void failure(RetrofitError error) {
					Log.e("request fail", error.toString());
					DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
					DialogPopup.dismissLoadingDialog();
				}
			});
		} else{
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}
	}

}

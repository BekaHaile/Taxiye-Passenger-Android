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
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.AddPaymentPath;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.UserData;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by shankar on 1/4/16.
 */
public class UserDebtDialog {

	private Activity activity;
	private UserData userData;
	public UserDebtDialog(Activity activity, UserData userData){
		this.activity = activity;
		this.userData = userData;
	}

	public void showUserDebtDialog(final double userDebt, String message) {
		if(message.length() == 0){
			message = String.format(activity.getResources().getString(R.string.user_debt_settle_balance_message), userDebt);
		}
		DialogPopup.alertPopupWithListener(activity, "", message,
				activity.getResources().getString(R.string.user_debt_pay_via_paytm),
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if(userData.getPaytmStatus().equalsIgnoreCase(Data.PAYTM_STATUS_ACTIVE)){
							if(userData.getPaytmBalance() >= userDebt){
								settleUserDebt(activity);
							}
							else{
								Intent intent = new Intent(activity, PaymentActivity.class);
								intent.putExtra(Constants.KEY_ADD_PAYMENT_PATH, AddPaymentPath.PAYTM_RECHARGE.getOrdinal());
								activity.startActivity(intent);
								activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
								FlurryEventLogger.event(FlurryEventNames.USER_DEBT_MAKE_PAYMENT);
							}
						} else if(userData.getPaytmStatus().equalsIgnoreCase(Data.PAYTM_STATUS_INACTIVE)){
							Intent intent = new Intent(activity, PaymentActivity.class);
							intent.putExtra(Constants.KEY_ADD_PAYMENT_PATH, AddPaymentPath.ADD_PAYTM.getOrdinal());
							activity.startActivity(intent);
							activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
							FlurryEventLogger.event(FlurryEventNames.USER_DEBT_MAKE_PAYMENT);
						} else{
							Intent intent = new Intent(activity, PaymentActivity.class);
							intent.putExtra(Constants.KEY_ADD_PAYMENT_PATH, AddPaymentPath.WALLET.getOrdinal());
							activity.startActivity(intent);
							activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
							FlurryEventLogger.event(FlurryEventNames.USER_DEBT_MAKE_PAYMENT);
						}
					}
				}, true);
	}

	private void settleUserDebt(final Activity activity) {
		try {
			if (AppStatus.getInstance(activity).isOnline(activity)) {

				DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));

				HashMap<String, String> params = new HashMap<>();
				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put(Constants.KEY_CLIENT_ID, Config.getClientId());
				params.put(Constants.KEY_IP_ADDRESS, Utils.getLocalIpAddress());
				Log.i("params", "=" + params);

				RestClient.getApiServices().adjustUserDebt(params, new Callback<SettleUserDebt>() {
					@Override
					public void success(SettleUserDebt settleUserDebt, Response response) {
						Log.e("Server response settle_user_debt", "response = " + response);
						try {
							String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
							JSONObject jObj = new JSONObject(jsonString);
							if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
								int flag = jObj.getInt(Constants.KEY_FLAG);
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

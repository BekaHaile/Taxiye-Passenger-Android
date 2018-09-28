package product.clicklabs.jugnoo.wallet;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.sabkuchfresh.home.FreshActivity;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.datastructure.UserData;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;
import product.clicklabs.jugnoo.wallet.models.PaymentActivityPath;
import product.clicklabs.jugnoo.wallet.models.PaymentModeConfigData;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by shankar on 1/4/16.
 */
public class UserDebtDialog {

	private final String TAG = UserDebtDialog.class.getSimpleName();

	private Activity activity;
	private UserData userData;
	private double userDebt;
	private Callback callback;

	public UserDebtDialog(Activity activity, UserData userData, Callback callback){
		this.activity = activity;
		this.userData = userData;
		this.callback = callback;
	}

	public void showUserDebtDialog(final double userDebt, String message) {
		this.userDebt = userDebt;
		if(message.length() == 0){
			if(activity instanceof HomeActivity) {
				message = activity.getString(R.string.user_debt_settle_balance_message, String.valueOf(userDebt));
			} else if(activity instanceof FreshActivity){
				message = activity.getString(R.string.user_debt_settle_balance_message_fresh, String.valueOf(userDebt));
			}
		}
		DialogPopup.alertPopupWithListener(activity, "", message,
				activity.getResources().getString(R.string.user_debt_pay_via_wallet),
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						PaymentModeConfigData stripePaymentData = MyApplication.getInstance().getWalletCore().
                                getConfigData(PaymentOption.STRIPE_CARDS.getOrdinal());
						double stripeBalance = (stripePaymentData!=null &&  stripePaymentData.getCardsData()!=null
								&& stripePaymentData.getCardsData().size()>0) ? UserDebtDialog.this.userDebt+5 : 0;
						double availableBalance = (userData.getPaytmEnabled() == 1 ? userData.getPaytmBalance() : 0)
								+ (userData.getMobikwikEnabled() == 1 ? userData.getMobikwikBalance() : 0)
								+ (userData.getFreeChargeEnabled() == 1 ? userData.getFreeChargeBalance() : 0)
								+ stripeBalance;
						if (availableBalance >= UserDebtDialog.this.userDebt) {
							settleUserDebt(activity);
						} else {
							Intent intent = new Intent(activity, PaymentActivity.class);
							intent.putExtra(Constants.KEY_PAYMENT_ACTIVITY_PATH, PaymentActivityPath.WALLET.getOrdinal());
							activity.startActivity(intent);
							activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
						}
					}
				}, false);
	}

	private void settleUserDebt(final Activity activity) {
		try {
			if (MyApplication.getInstance().isOnline()) {

				DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));

				HashMap<String, String> params = new HashMap<>();
				params.put(Constants.KEY_ACCESS_TOKEN, userData.accessToken);
				params.put(Constants.KEY_CLIENT_ID, Config.getAutosClientId());
				params.put(Constants.KEY_IP_ADDRESS, Utils.getLocalIpAddress());
				Log.i("params", "=" + params);

				new HomeUtil().putDefaultParams(params);
				RestClient.getApiService().settleUserDebt(params, new retrofit.Callback<SettleUserDebt>() {
					@Override
					public void success(SettleUserDebt settleUserDebt, Response response) {
						Log.i(TAG, "adjustUserDebt response = " + response);
						try {
							String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
							JSONObject jObj = new JSONObject(jsonString);
							if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
								int flag = jObj.getInt(Constants.KEY_FLAG);
								String message = JSONParser.getServerMessage(jObj);
								if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
									DialogPopup.alertPopup(activity, "", message);
									userData.updateWalletBalances(jObj, false);
									MyApplication.getInstance().getWalletCore().parsePaymentModeConfigDatas(jObj);
									callback.successFullyDeducted(userDebt);
								} else {
									DialogPopup.alertPopup(activity, "", message);
								}
							}
						} catch (Exception exception) {
							exception.printStackTrace();
							DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
						}
						DialogPopup.dismissLoadingDialog();
					}

					@Override
					public void failure(RetrofitError error) {
						Log.e(TAG, "adjustUserDebt error"+error.toString());
						DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
						DialogPopup.dismissLoadingDialog();
					}
				});
			} else{
				DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_desc));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public interface Callback{
		void successFullyDeducted(double userDebt);
	}

}

package product.clicklabs.jugnoo.wallet;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.AddPaymentPath;
import product.clicklabs.jugnoo.datastructure.UserData;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Utils;

/**
 * Created by shankar on 1/4/16.
 */
public class UserDebtDialog {

	private Activity activity;
	public UserDebtDialog(Activity activity){
		this.activity = activity;
	}

	public void showUserDebtDialog(UserData userData){
		if(userData != null) {
			if (userData.getUserDebtDeducted() == 1) {
				DialogPopup.alertPopupWithListener(activity, "",
						String.format(activity.getResources().getString(R.string.user_debt_money_deducted),
								Utils.getMoneyDecimalFormat().format(userData.getUserDebt()), userData.getUserDebtDate())
						, null);
				userData.setUserDebtDeducted(0);
				userData.setUserDebt(0);
			}
			else if (userData.getUserDebt() > 0) {
				DialogPopup.alertPopupWithListener(activity, "",
						String.format(activity.getResources().getString(R.string.user_debt_settle_balance_message),
								userData.getUserDebtDate()),
						activity.getResources().getString(R.string.user_debt_make_payment),
						new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								Intent intent = new Intent(activity, PaymentActivity.class);
								intent.putExtra(Constants.KEY_ADD_PAYMENT_PATH, AddPaymentPath.PAYTM_RECHARGE.getOrdinal());
								activity.startActivity(intent);
								activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
								FlurryEventLogger.event(FlurryEventNames.USER_DEBT_MAKE_PAYMENT);
							}
						});
			}
		}
	}

}

package product.clicklabs.jugnoo.home.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;

public class WalletSelectionErrorDialog {

	private final String TAG = WalletSelectionErrorDialog.class.getSimpleName();
	private Activity activity;
	private Callback callback;
	private Dialog dialog = null;

	public WalletSelectionErrorDialog(Activity activity, Callback callback) {
		this.activity = activity;
		this.callback = callback;
	}

	public Dialog show(String message, boolean walletError, String okButtonText) {
		try {
			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.setContentView(R.layout.dialog_wallet_selection_error);

			RelativeLayout relative = (RelativeLayout) dialog.findViewById(R.id.relative);
			new ASSL(activity, relative, 1134, 720, false);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(true);

			LinearLayout linearLayoutInner = (LinearLayout) dialog.findViewById(R.id.linearLayoutInner);
			TextView textViewMessage = (TextView) dialog.findViewById(R.id.textViewMessage); textViewMessage.setTypeface(Fonts.mavenMedium(activity));
			textViewMessage.setText(message);

			Button buttonPositive = (Button) dialog.findViewById(R.id.buttonPositive); buttonPositive.setTypeface(Fonts.mavenMedium(activity));
			Button buttonNegative = (Button) dialog.findViewById(R.id.buttonNegative); buttonNegative.setTypeface(Fonts.mavenMedium(activity));

			if (!TextUtils.isEmpty(okButtonText)) {
				buttonPositive.setText(okButtonText);
			} else {
				buttonPositive.setText(R.string.add_money_to_wallet);
			}
			buttonNegative.setText(R.string.choose_other_mode_to_pay);

			if(walletError){
				buttonPositive.setVisibility(View.GONE);
			} else{
				buttonPositive.setVisibility(View.VISIBLE);
			}

			buttonPositive.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					callback.onPositiveClick();
				}
			});

			buttonNegative.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					callback.onNegativeClick();
				}
			});

			linearLayoutInner.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

				}
			});

			relative.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});

			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return dialog;
	}

	public interface Callback{
		void onPositiveClick();
		void onNegativeClick();
	}

}
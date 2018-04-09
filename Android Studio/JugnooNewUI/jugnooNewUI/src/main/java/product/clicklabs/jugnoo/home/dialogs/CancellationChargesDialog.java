package product.clicklabs.jugnoo.home.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Utils;

/**
 * Created by Ankit on 5/2/16.
 */
public class CancellationChargesDialog {

	private final String TAG = CancellationChargesDialog.class.getSimpleName();
	private Activity activity;
	private Callback callback;
	private Dialog dialog = null;

	public CancellationChargesDialog(Activity activity, Callback callback) {
		this.activity = activity;
		this.callback = callback;
	}



	public Dialog showCancellationChargesDialog(String line1Text, String line2Text) {
		try {
			if("".equalsIgnoreCase(line1Text) || "".equalsIgnoreCase(line2Text)){
				callback.onYes();
			} else {
				dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
				dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogScale;
				dialog.setContentView(R.layout.dialog_cancellation_charges);

				RelativeLayout relative = (RelativeLayout) dialog.findViewById(R.id.relative);
				new ASSL(activity, relative, 1134, 720, false);

				WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
				layoutParams.dimAmount = 0.6f;
				dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
				dialog.setCancelable(true);
				dialog.setCanceledOnTouchOutside(true);

				LinearLayout linearLayoutInner = (LinearLayout) dialog.findViewById(R.id.linearLayoutInner);
				TextView textViewCancelRide = (TextView) dialog.findViewById(R.id.textViewCancelRide);
				textViewCancelRide.setTypeface(Fonts.avenirNext(activity), Typeface.BOLD);
				TextView textViewOnHisWay = (TextView) dialog.findViewById(R.id.textViewOnHisWay);
				textViewOnHisWay.setTypeface(Fonts.mavenRegular(activity));
				TextView textViewContinue = (TextView) dialog.findViewById(R.id.textViewContinue);
				textViewContinue.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
				Button buttonYes = (Button) dialog.findViewById(R.id.buttonYes);
				buttonYes.setTypeface(Fonts.mavenMedium(activity));
				Button buttonNo = (Button) dialog.findViewById(R.id.buttonNo);
				buttonNo.setTypeface(Fonts.mavenMedium(activity));
				TextView textViewCancellation = (TextView) dialog.findViewById(R.id.textViewCancellation);
				textViewCancellation.setTypeface(Fonts.mavenRegular(activity));


				textViewOnHisWay.setText(replaceStringWithAmount(line1Text));
				textViewContinue.setText(replaceStringWithAmount(line2Text));


				buttonYes.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						callback.onYes();
					}
				});

				buttonNo.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						callback.onNo();
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

				dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						callback.onDialogDismiss();
					}
				});


				dialog.show();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return dialog;
	}

	private String replaceStringWithAmount(String text){
		String finalStr = "";
		//String.format(getString(R.string.rupees_value_format),"" + FeedUtils.getMoneyDecimalFormat().format(Data.autoData.getEndRideData().finalFare))
		try {
			if(text.contains("{amount}") && (Data.autoData.getAssignedDriverInfo().getCancellationCharges() > 0)){
                finalStr = text.replace("{amount}", (Utils.formatCurrencyValue(Data.autoData.getAssignedDriverInfo().getCurrency(), Data.autoData.getAssignedDriverInfo().getCancellationCharges())));
            } else{
				finalStr = text;
			}
		} catch (Exception e) {
			e.printStackTrace();
			finalStr = text;
		}
		return finalStr;
	}


	public interface Callback{
		void onDialogDismiss();
		void onYes();
		void onNo();
	}

}
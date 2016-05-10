package product.clicklabs.jugnoo.home.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Utils;

/**
 * Created by shankar on 5/2/16.
 */
public class FareDetailsDialog {

	private final String TAG = FareDetailsDialog.class.getSimpleName();
	private Activity activity;
	private Callback callback;
	private Dialog dialog = null;

	public FareDetailsDialog(Activity activity, Callback callback) {
		this.activity = activity;
		this.callback = callback;
	}

	public FareDetailsDialog show() {
		try {
			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogScale;
			dialog.setContentView(R.layout.dialog_fare_details);

			RelativeLayout relative = (RelativeLayout) dialog.findViewById(R.id.relative);
			new ASSL(activity, relative, 1134, 720, false);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(true);

			LinearLayout linearLayoutInner = (LinearLayout) dialog.findViewById(R.id.linearLayoutInner);
			((TextView) dialog.findViewById(R.id.textViewFareDetails)).setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
			TextView textViewMinimumFare = (TextView) dialog.findViewById(R.id.textViewMinimumFare);
			textViewMinimumFare.setTypeface(Fonts.mavenLight(activity));
			((TextView) dialog.findViewById(R.id.textViewKM)).setTypeface(Fonts.mavenLight(activity));
			((TextView) dialog.findViewById(R.id.textViewMin)).setTypeface(Fonts.mavenRegular(activity));
			TextView textViewKMValue = (TextView) dialog.findViewById(R.id.textViewKMValue);
			textViewKMValue.setTypeface(Fonts.mavenRegular(activity));
			TextView textViewMinValue = (TextView) dialog.findViewById(R.id.textViewMinValue);
			textViewMinValue.setTypeface(Fonts.mavenRegular(activity));
			TextView textViewThresholdDistance = (TextView) dialog.findViewById(R.id.textViewThresholdDistance);
			textViewThresholdDistance.setTypeface(Fonts.mavenLight(activity));

			RelativeLayout relativeLayoutPriorityTip = (RelativeLayout) dialog.findViewById(R.id.relativeLayoutPriorityTip);
			TextView textViewPriorityTipValue = (TextView) dialog.findViewById(R.id.textViewPriorityTipValue);
			textViewPriorityTipValue.setTypeface(Fonts.mavenLight(activity));

			ImageView imageViewClose = (ImageView) dialog.findViewById(R.id.imageViewClose);
			imageViewClose.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
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

			textViewMinimumFare.setText(String.format(activity.getResources().getString(R.string.minimum_fare_rupee_format),
					Utils.getMoneyDecimalFormat().format(Data.fareStructure.fixedFare)));
			textViewKMValue.setText(String.format(activity.getResources().getString(R.string.rupees_value_format_without_space),
					Utils.getMoneyDecimalFormat().format(Data.fareStructure.farePerKm)));
			textViewMinValue.setText(String.format(activity.getResources().getString(R.string.rupees_value_format_without_space),
					Utils.getMoneyDecimalFormat().format(Data.fareStructure.farePerMin)));

			if(Data.fareStructure.thresholdDistance > 1.0){
				textViewThresholdDistance.setVisibility(View.VISIBLE);
				DecimalFormat decimalFormat = new DecimalFormat("#.#");
				textViewThresholdDistance.setText(String.format(activity.getResources()
								.getString(R.string.fare_threshold_distance_message_format),
						decimalFormat.format(Data.fareStructure.thresholdDistance)));
			} else{
				textViewThresholdDistance.setVisibility(View.GONE);
			}

			if(Data.userData.fareFactor > 1.0){
				relativeLayoutPriorityTip.setVisibility(View.VISIBLE);
				textViewPriorityTipValue.setText(String.format(activity.getResources().getString(R.string.format_x),
						Utils.getMoneyDecimalFormat().format(Data.userData.fareFactor)));
			} else{
				relativeLayoutPriorityTip.setVisibility(View.GONE);
			}

			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}


	public interface Callback{
		void onDialogDismiss();
	}

}
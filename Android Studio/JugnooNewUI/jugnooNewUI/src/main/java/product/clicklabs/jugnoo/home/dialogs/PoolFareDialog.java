package product.clicklabs.jugnoo.home.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Utils;

/**
 * Created by Ankit on 5/2/16.
 */
public class PoolFareDialog {

	private final String TAG = PoolFareDialog.class.getSimpleName();
	private Activity activity;
	private Callback callback;
	private Dialog dialog = null;

	public PoolFareDialog(Activity activity, Callback callback) {
		this.activity = activity;
		this.callback = callback;
	}



	public Dialog showPoolFareDialog(double fare) {
		try {
			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogScale;
			dialog.setContentView(R.layout.dialog_pool_fare);

			RelativeLayout relative = (RelativeLayout) dialog.findViewById(R.id.relative);
			new ASSL(activity, relative, 1134, 720, false);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(true);

			LinearLayout linearLayoutInner = (LinearLayout) dialog.findViewById(R.id.linearLayoutInner);
			TextView textViewTotalFare = (TextView) dialog.findViewById(R.id.textViewTotalFare);
			textViewTotalFare.setTypeface(Fonts.mavenMedium(activity));
			TextView textViewForOnePerson = (TextView) dialog.findViewById(R.id.textViewForOnePerson);
			textViewForOnePerson.setTypeface(Fonts.mavenRegular(activity));
			TextView textViewValue = (TextView) dialog.findViewById(R.id.textViewValue);
			textViewValue.setTypeface(Fonts.mavenRegular(activity));
			Button buttonConfirm = (Button)dialog.findViewById(R.id.buttonConfirm);
			buttonConfirm.setTypeface(Fonts.mavenMedium(activity));
			TextView textViewCancel = (TextView) dialog.findViewById(R.id.textViewCancel);
			textViewCancel.setTypeface(Fonts.mavenRegular(activity));

			textViewValue.setText(String.format(activity.getResources().getString(R.string.rupees_value_format_without_space), Utils.getMoneyDecimalFormat().format(fare)));

			textViewCancel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});

			buttonConfirm.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					callback.onConfirmed();
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
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return dialog;
	}


	public interface Callback{
		void onDialogDismiss();
		void onConfirmed();
	}

}
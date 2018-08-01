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

/**
 * Created by Ankit on 5/2/16.
 */
public class ServiceUnavailableDialog {

	private final String TAG = ServiceUnavailableDialog.class.getSimpleName();
	private Activity activity;
	private Callback callback;
	private Dialog dialog = null;

	public ServiceUnavailableDialog(Activity activity, Callback callback) {
		this.activity = activity;
		this.callback = callback;
	}



	public Dialog showServiceUnavailableDialog() {
		try {
			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.setContentView(R.layout.dialog_service_unavailable);

			RelativeLayout relative = (RelativeLayout) dialog.findViewById(R.id.relative);
			new ASSL(activity, relative, 1134, 720, false);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(true);

			LinearLayout linearLayoutInner = (LinearLayout) dialog.findViewById(R.id.linearLayoutInner);
			TextView textViewCancelledRides = (TextView) dialog.findViewById(R.id.textViewCancelledRides);
			textViewCancelledRides.setTypeface(Fonts.mavenMedium(activity));
			TextView textViewResume = (TextView) dialog.findViewById(R.id.textViewResume);
			textViewResume.setTypeface(Fonts.mavenRegular(activity));
			Button buttonYes = (Button)dialog.findViewById(R.id.buttonYes);
			buttonYes.setTypeface(Fonts.mavenMedium(activity));



			buttonYes.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					callback.onOk();
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
		void onOk();
	}

}
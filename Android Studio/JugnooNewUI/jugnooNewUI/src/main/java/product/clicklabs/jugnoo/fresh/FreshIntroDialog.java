package product.clicklabs.jugnoo.fresh;

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

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Prefs;

/**
 * Created by shankar on 3/4/16.
 */
public class FreshIntroDialog {

	private final String TAG = FreshIntroDialog.class.getSimpleName();
	private Activity activity;
	private Callback callback;
	private Dialog dialog = null;

	public FreshIntroDialog(Activity activity, Callback callback) {
		this.activity = activity;
		this.callback = callback;
	}

	public Dialog show() {
		try {
			if(Prefs.with(activity).getInt(Constants.SP_FRESH_INTRO_SHOWN, 0) == 0) {
				dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
				dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
				dialog.setContentView(R.layout.dialog_fresh_intro);

				RelativeLayout relative = (RelativeLayout) dialog.findViewById(R.id.relative);
				new ASSL(activity, relative, 1134, 720, false);

				WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
				layoutParams.dimAmount = 0.6f;
				dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
				dialog.setCancelable(false);
				dialog.setCanceledOnTouchOutside(false);

				LinearLayout linearLayoutInner = (LinearLayout) dialog.findViewById(R.id.linearLayoutInner);
				((TextView) dialog.findViewById(R.id.textViewGetFreshness)).setTypeface(Fonts.mavenRegular(activity));
				((TextView) dialog.findViewById(R.id.textViewDeliveredNow)).setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
				((TextView) dialog.findViewById(R.id.textViewFinestFruits)).setTypeface(Fonts.mavenRegular(activity));

				Button buttonContinue = (Button) dialog.findViewById(R.id.buttonContinue);
				Button buttonLater = (Button) dialog.findViewById(R.id.buttonLater);
				buttonLater.setTypeface(Fonts.mavenRegular(activity));
				buttonContinue.setTypeface(Fonts.mavenRegular(activity));

				buttonContinue.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						callback.onContinueClicked();
					}
				});

				buttonLater.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						callback.onMayBeLaterClicked();
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
						//dialog.dismiss();
					}
				});

				dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						callback.onDialogDismiss();
					}
				});

				dialog.show();
				Prefs.with(activity).save(Constants.SP_FRESH_INTRO_SHOWN, 1);
			} else{
				callback.notShown();
			}
		} catch (Exception e) {
			e.printStackTrace();
			callback.notShown();
		}
		return dialog;
	}


	public interface Callback{
		void onContinueClicked();
		void onMayBeLaterClicked();
		void onDialogDismiss();
		void notShown();
	}

}

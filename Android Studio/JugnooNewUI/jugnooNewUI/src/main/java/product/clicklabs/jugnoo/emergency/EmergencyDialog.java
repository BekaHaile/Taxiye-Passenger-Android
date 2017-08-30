package product.clicklabs.jugnoo.emergency;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
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

/**
 * For the purpose of showing emergency options dialog
 * Options: Enable emergency mode
 * 			Send Ride Status
 * 			In App Customer Support
 *
 * Created by shankar on 2/22/16.
 */
public class EmergencyDialog {

	private Activity activity;
	private String engagementId;
	private CallBack callBack;

	public EmergencyDialog(Activity activity, String engagementId, CallBack callBack){
		this.activity = activity;
		this.engagementId = engagementId;
		this.callBack = callBack;
	}


	public Dialog show(final int modeEnabled){
		try {
			final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_emergency);

			RelativeLayout relative = (RelativeLayout) dialog.findViewById(R.id.relative);
			new ASSL(activity, relative, 1134, 720, true);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);

			LinearLayout linearLayoutInner = (LinearLayout)dialog.findViewById(R.id.linearLayoutInner);

			TextView textViewEnableEmergencyMode = (TextView) dialog.findViewById(R.id.textViewEnableEmergencyMode);
			textViewEnableEmergencyMode.setTypeface(Fonts.mavenRegular(activity));

			TextView textViewSendRideStatus = (TextView) dialog.findViewById(R.id.textViewSendRideStatus);
			textViewSendRideStatus.setTypeface(Fonts.mavenRegular(activity));

			TextView textViewInAppCustomerSupport = (TextView)dialog.findViewById(R.id.textViewInAppCustomerSupport);
			textViewInAppCustomerSupport.setTypeface(Fonts.mavenRegular(activity));
			if(Data.isFuguChatEnabled()){
				textViewInAppCustomerSupport.setText(R.string.chat_with_us);
			}

			Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
			btnCancel.setTypeface(Fonts.mavenRegular(activity));

			View.OnClickListener onClickListener = new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					switch (v.getId()){

						case R.id.textViewEnableEmergencyMode:
							if(modeEnabled == 1){
								callBack.onEmergencyModeDisabled(engagementId);
							} else{
								callBack.onEnableEmergencyModeClick(v);
							}
							dialog.dismiss();
							break;

						case R.id.textViewSendRideStatus:
							callBack.onSendRideStatusClick(v);
							dialog.dismiss();
							break;

						case R.id.textViewInAppCustomerSupport:
							callBack.onInAppCustomerSupportClick(v);
							dialog.dismiss();
							break;

						case R.id.btnCancel:
							callBack.onDialogClosed(v);
							dialog.dismiss();
							break;

						case R.id.linearLayoutInner:

							break;

						case R.id.relative:
							break;

					}
				}
			};


			textViewEnableEmergencyMode.setOnClickListener(onClickListener);
			textViewSendRideStatus.setOnClickListener(onClickListener);
			textViewInAppCustomerSupport.setOnClickListener(onClickListener);
			btnCancel.setOnClickListener(onClickListener);
			linearLayoutInner.setOnClickListener(onClickListener);
			relative.setOnClickListener(onClickListener);

			dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					callBack.onDialogDismissed();
				}
			});


			if(modeEnabled == 1){
				textViewEnableEmergencyMode.setTextColor(activity.getResources().getColorStateList(R.color.text_color_light_selector));
				textViewEnableEmergencyMode.setText(activity.getResources().getString(R.string.disable_emergency_mode));
			} else{
				textViewEnableEmergencyMode.setTextColor(activity.getResources().getColorStateList(R.color.text_color_red_dark_aplha_selector));
				textViewEnableEmergencyMode.setText(activity.getResources().getString(R.string.enable_emergency_mode));
			}

			dialog.show();
			return dialog;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}




	public interface CallBack{
		void onEnableEmergencyModeClick(View view);
		void onEmergencyModeDisabled(String engagementId);
		void onSendRideStatusClick(View view);
		void onInAppCustomerSupportClick(View view);
		void onDialogClosed(View view);
		void onDialogDismissed();
	}

}

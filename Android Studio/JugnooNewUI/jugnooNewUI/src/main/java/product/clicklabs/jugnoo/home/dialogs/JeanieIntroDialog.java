package product.clicklabs.jugnoo.home.dialogs;

/**
 * Created by ankit on 6/1/16.
 */

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;

/**
 * Created by Shankar on 3/4/16.
 */
public class JeanieIntroDialog {

	private final String TAG = JeanieIntroDialog.class.getSimpleName();
	private Activity activity;
	private Dialog dialog = null;

	public JeanieIntroDialog(Activity activity) {
		this.activity = activity;
	}

	public Dialog show() {
		try {
			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_BottomInBottomOut;
			dialog.setContentView(R.layout.dialog_jeanie_intro);

			RelativeLayout relative = (RelativeLayout) dialog.findViewById(R.id.relative);
			new ASSL(activity, relative, 1134, 720, false);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(true);

			LinearLayout linearLayoutInner = (LinearLayout) dialog.findViewById(R.id.linearLayoutInner);
			((TextView) dialog.findViewById(R.id.textViewJugnooJeanie)).setTypeface(Fonts.mavenMedium(activity), Typeface.BOLD);
			((TextView)dialog.findViewById(R.id.textViewJugnooJeanie)).setText(activity.getString(R.string.jugnoo_jeanie, activity.getString(R.string.app_name)));
			((TextView) dialog.findViewById(R.id.textViewSwitchTo)).setTypeface(Fonts.mavenMedium(activity));
			((TextView) dialog.findViewById(R.id.textViewAutos)).setTypeface(Fonts.mavenMedium(activity));
			TextView textViewAutosDesc = (TextView) dialog.findViewById(R.id.textViewAutosDesc); textViewAutosDesc.setTypeface(Fonts.mavenMedium(activity));
			((TextView) dialog.findViewById(R.id.textViewMeals)).setTypeface(Fonts.mavenMedium(activity));
			TextView textViewMealsDesc = (TextView) dialog.findViewById(R.id.textViewMealsDesc); textViewMealsDesc.setTypeface(Fonts.mavenMedium(activity));
			((TextView) dialog.findViewById(R.id.textViewFresh)).setTypeface(Fonts.mavenMedium(activity));
			TextView textViewFreshDesc = (TextView) dialog.findViewById(R.id.textViewFreshDesc); textViewFreshDesc.setTypeface(Fonts.mavenMedium(activity));
			((TextView) dialog.findViewById(R.id.textViewDelivery)).setTypeface(Fonts.mavenMedium(activity));
			TextView textViewDeliveryDesc = (TextView) dialog.findViewById(R.id.textViewDeliveryDesc); textViewDeliveryDesc.setTypeface(Fonts.mavenMedium(activity));

			LinearLayout linearLayoutMeals = (LinearLayout) dialog.findViewById(R.id.linearLayoutMeals);
			LinearLayout linearLayoutFresh = (LinearLayout) dialog.findViewById(R.id.linearLayoutFresh);
			LinearLayout linearLayoutDelivery = (LinearLayout) dialog.findViewById(R.id.linearLayoutDelivery);

			if(Data.userData != null){
				if(Data.userData.getMealsEnabled() == 1){
					linearLayoutMeals.setVisibility(View.VISIBLE);
				} else{
					linearLayoutMeals.setVisibility(View.GONE);
				}
				if(Data.userData.getFreshEnabled() == 1){
					linearLayoutFresh.setVisibility(View.VISIBLE);
				} else{
					linearLayoutFresh.setVisibility(View.GONE);
				}
				if(Data.userData.getDeliveryEnabled() == 1){
					linearLayoutDelivery.setVisibility(View.VISIBLE);
				} else{
					linearLayoutDelivery.setVisibility(View.GONE);
				}

				if(Data.userData.getJeanieIntroDialogContent() != null){
					if(Data.userData.getJeanieIntroDialogContent().getAutosText() != null) {
						textViewAutosDesc.setText(Data.userData.getJeanieIntroDialogContent().getAutosText());
					}
					if(Data.userData.getJeanieIntroDialogContent().getMealsText() != null) {
						textViewMealsDesc.setText(Data.userData.getJeanieIntroDialogContent().getMealsText());
					}
					if(Data.userData.getJeanieIntroDialogContent().getFreshText() != null) {
						textViewFreshDesc.setText(Data.userData.getJeanieIntroDialogContent().getFreshText());
					}
					if(Data.userData.getJeanieIntroDialogContent().getDeliveryText() != null) {
						textViewDeliveryDesc.setText(Data.userData.getJeanieIntroDialogContent().getDeliveryText());
					}
				}
			}

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
		}
		return dialog;
	}

}

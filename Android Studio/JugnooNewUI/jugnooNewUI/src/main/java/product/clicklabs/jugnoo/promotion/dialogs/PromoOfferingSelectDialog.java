package product.clicklabs.jugnoo.promotion.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;

/**
 * Created by shankar on 5/2/16.
 */
public class PromoOfferingSelectDialog {

	private Activity activity;
	private Callback callback;
	private Dialog dialog;

	public PromoOfferingSelectDialog(Activity activity, Callback callback) {
		this.activity = activity;
		this.callback = callback;
	}

	public Dialog show(){
		try {
			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_BottomInBottomOut;
			dialog.setContentView(R.layout.dialog_promo_offering_select);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);

			TextView tvRides = (TextView) dialog.findViewById(R.id.tvRides);
			TextView tvMeals = (TextView) dialog.findViewById(R.id.tvMeals);
			TextView tvFresh = (TextView) dialog.findViewById(R.id.tvFresh);
			TextView tvMenus = (TextView) dialog.findViewById(R.id.tvMenus);

			View.OnClickListener onClickListener = new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					switch(v.getId()){
						case R.id.tvRides:
							callback.onOfferingClick(Config.getAutosClientId());
							break;
						case R.id.tvMeals:
							callback.onOfferingClick(Config.getMealsClientId());
							break;
						case R.id.tvFresh:
							callback.onOfferingClick(Config.getFreshClientId());
							break;
						case R.id.tvMenus:
							callback.onOfferingClick(Config.getMenusClientId());
							break;
					}
				}
			};
			tvRides.setOnClickListener(onClickListener);
			tvMeals.setOnClickListener(onClickListener);
			tvFresh.setOnClickListener(onClickListener);
			tvMenus.setOnClickListener(onClickListener);

			dialog.show();
			return dialog;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void dismiss(){
		if(dialog != null && dialog.isShowing()){
			dialog.dismiss();
		}
	}

	public interface Callback{
		void onOfferingClick(String clientId);
	}
}
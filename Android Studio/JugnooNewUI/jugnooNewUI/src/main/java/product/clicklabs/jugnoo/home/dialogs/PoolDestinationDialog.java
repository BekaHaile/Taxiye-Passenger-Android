package product.clicklabs.jugnoo.home.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Utils;

/**
 * Created by shankar on 5/2/16.
 */
public class PoolDestinationDialog {

	private final String TAG = PoolDestinationDialog.class.getSimpleName();
	private Activity activity;
	private Callback callback;
	private Dialog dialog = null;

	public PoolDestinationDialog(Activity activity, Callback callback) {
		this.activity = activity;
		this.callback = callback;
	}

	public PoolDestinationDialog show() {
		try {
			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogScale;
			dialog.setContentView(R.layout.dialog_pool_destination);

			RelativeLayout relative = (RelativeLayout) dialog.findViewById(R.id.relative);
			new ASSL(activity, relative, 1134, 720, false);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(true);

			LinearLayout linearLayoutInner = (LinearLayout) dialog.findViewById(R.id.linearLayoutInner);
			TextView textViewDestination = (TextView) dialog.findViewById(R.id.textViewDestination);textViewDestination.setTypeface(Fonts.mavenRegular(activity));
			TextView textViewLocation = (TextView) dialog.findViewById(R.id.textViewLocation); textViewLocation.setTypeface(Fonts.avenirNext(activity), Typeface.BOLD);
			TextView textViewNotAble = (TextView) dialog.findViewById(R.id.textViewNotAble);textViewNotAble.setTypeface(Fonts.mavenRegular(activity));

			if(Data.userData != null) {
				textViewDestination.setText(Data.userData.getPoolDestinationPopupText1());
				textViewLocation.setText(Data.userData.getPoolDestinationPopupText2());
				textViewNotAble.setText(Data.userData.getPoolDestinationPopupText3());
			}

			Button buttonEnterDestination = (Button) dialog.findViewById(R.id.buttonEnterDestination);
			buttonEnterDestination.setTypeface(Fonts.avenirNext(activity), Typeface.BOLD);

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

			buttonEnterDestination.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					callback.onEnterDestination();
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
					//callback.onDialogDismiss();
				}
			});

			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}


	public interface Callback{
		//void onDialogDismiss();
		void onEnterDestination();
	}

}
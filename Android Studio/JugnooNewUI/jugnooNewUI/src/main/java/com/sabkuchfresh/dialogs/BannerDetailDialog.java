package com.sabkuchfresh.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.R;
import com.sabkuchfresh.utils.ASSL;
import com.sabkuchfresh.utils.Fonts;


/**
 * Created by Ankit on 5/2/16.
 */
public class BannerDetailDialog {

	private final String TAG = BannerDetailDialog.class.getSimpleName();
	private Activity activity;
	private Callback callback;
	private Dialog dialog = null;

	public BannerDetailDialog(Activity activity, Callback callback) {
		this.activity = activity;
		this.callback = callback;
	}



	public Dialog showBannerDetailDialog(String image, String desc) {
		try {
			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogDown;
			dialog.setContentView(R.layout.dialog_banner_detail);

			RelativeLayout relative = (RelativeLayout) dialog.findViewById(R.id.relative);
			new ASSL(activity, relative, 1134, 720, false);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(true);

			LinearLayout linearLayoutInner = (LinearLayout) dialog.findViewById(R.id.linearLayoutInner);
			ImageView imageView = (ImageView)dialog.findViewById(R.id.imageView);
			TextView textViewDesc = (TextView) dialog.findViewById(R.id.textViewDesc);
			textViewDesc.setTypeface(Fonts.mavenRegular(activity));
			Button button = (Button)dialog.findViewById(R.id.button);
			button.setTypeface(Fonts.mavenRegular(activity));

			textViewDesc.setText(desc);

			Picasso.with(activity).load(image)
					.placeholder(R.drawable.img_ice_cream_banner)
					.error(R.drawable.img_ice_cream_banner)
					.into(imageView);

			button.setOnClickListener(new View.OnClickListener() {
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
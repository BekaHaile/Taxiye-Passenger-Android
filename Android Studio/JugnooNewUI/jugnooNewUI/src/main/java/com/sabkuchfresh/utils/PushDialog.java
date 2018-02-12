package com.sabkuchfresh.utils;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.datastructure.PopupData;
import com.squareup.picasso.Picasso;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;

/**
 * Created by shankar on 5/2/16.
 */
public class PushDialog {

	private final String TAG = PushDialog.class.getSimpleName();
	private Activity activity;
	private Callback callback;
	private Dialog dialog = null;

	public PushDialog(Activity activity, Callback callback) {
		this.activity = activity;
		this.callback = callback;
	}

	public PushDialog show(PopupData popupData) {
		try {
                String title = popupData.title_text;
				String message = popupData.desc_text;
				final int deepindex = popupData.deep_index;
				String picture = popupData.image_url;
                final String extrUrl = popupData.ext_url;
				String buttonText = popupData.getOk_title();
                int isCancellable = popupData.getIs_cancellable();

				dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
				dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
				dialog.setContentView(R.layout.dialog_push);

				RelativeLayout relative = (RelativeLayout) dialog.findViewById(R.id.relative);
				new ASSL(activity, relative, 1134, 720, false);

				WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
				layoutParams.dimAmount = 0.6f;
				dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                if(isCancellable == 0) {
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                } else {
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(true);
                }


				ImageView imageView = (ImageView) dialog.findViewById(R.id.imageView);
				TextView textViewTitle = (TextView) dialog.findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
				TextView textViewMessage = (TextView) dialog.findViewById(R.id.textViewMessage); textViewMessage.setTypeface(Fonts.mavenRegular(activity));
				Button button = (Button) dialog.findViewById(R.id.button);button.setTypeface(Fonts.mavenRegular(activity));
				ImageView imageViewClose = (ImageView) dialog.findViewById(R.id.imageViewClose);

                if(isCancellable == 0) {
                    imageViewClose.setVisibility(View.GONE);
                }


                if(!TextUtils.isEmpty(title)) {
                    textViewTitle.setText(title);
                } else {
                    textViewTitle.setVisibility(View.GONE);
                }
                if(!TextUtils.isEmpty(message)) {
                    textViewMessage.setText(message);
                } else {
                    textViewMessage.setVisibility(View.GONE);
                }

				button.setText(buttonText);

				try {
					if(!"".equalsIgnoreCase(picture)) {
						Picasso.with(activity).load(picture)
								.placeholder(R.drawable.ic_fresh_new_placeholder)
								.error(R.drawable.ic_fresh_new_placeholder)
								.into(imageView);
					} else{
						imageView.setVisibility(View.GONE);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				button.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
                        if(!TextUtils.isEmpty(extrUrl)) {
                         Utils.openUrl(activity, extrUrl);
                        } else {
                            callback.onButtonClicked(deepindex);
                        }
						dialog.dismiss();
					}
				});

				imageViewClose.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});

				dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}

	public void dismiss(boolean clearDialogContent){
		if(clearDialogContent){

		}
		if(dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
	}


	public interface Callback{
		void onButtonClicked(int deepIndex);
	}

}
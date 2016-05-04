package product.clicklabs.jugnoo.home;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Prefs;

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

	public Dialog show() {
		try {
			String pushDialogContent = Prefs.with(activity).getString(Constants.SP_PUSH_DIALOG_CONTENT,
					Constants.EMPTY_JSON_OBJECT);
			JSONObject jObj = new JSONObject(pushDialogContent);
			if(!pushDialogContent.equalsIgnoreCase(Constants.EMPTY_JSON_OBJECT)
					&& jObj.optInt(Constants.KEY_SHOW_DIALOG, 0) == 1){
				String title = jObj.optString(Constants.KEY_TITLE, activity.getResources().getString(R.string.app_name));
				String message = jObj.optString(Constants.KEY_MESSAGE, "");
				final int deepindex = jObj.optInt(Constants.KEY_DEEPINDEX, -1);
				String picture = jObj.optString(Constants.KEY_PICTURE, "");
				if("".equalsIgnoreCase(picture)){
					picture = jObj.optString(Constants.KEY_IMAGE, "");
				}
				String buttonText = jObj.optString(Constants.KEY_BUTTON_TEXT, "Button");

				dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
				dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
				dialog.setContentView(R.layout.dialog_push);

				RelativeLayout relative = (RelativeLayout) dialog.findViewById(R.id.relative);
				new ASSL(activity, relative, 1134, 720, false);

				WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
				layoutParams.dimAmount = 0.6f;
				dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
				dialog.setCancelable(false);
				dialog.setCanceledOnTouchOutside(false);

				ImageView imageView = (ImageView) dialog.findViewById(R.id.imageView);
				TextView textViewTitle = (TextView) dialog.findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
				TextView textViewMessage = (TextView) dialog.findViewById(R.id.textViewMessage); textViewMessage.setTypeface(Fonts.mavenRegular(activity));
				Button button = (Button) dialog.findViewById(R.id.button);button.setTypeface(Fonts.mavenRegular(activity));
				ImageView imageViewClose = (ImageView) dialog.findViewById(R.id.imageViewClose);

				textViewTitle.setText(title);
				textViewMessage.setText(message);
				button.setText(buttonText);

				try {
					if(!"".equalsIgnoreCase(picture)) {
						Picasso.with(activity).load(picture)
								.placeholder(R.drawable.ic_notification_placeholder)
								.error(R.drawable.ic_notification_placeholder)
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
						callback.onButtonClicked(deepindex);
						dialog.dismiss();
					}
				});

				imageViewClose.setOnClickListener(new View.OnClickListener() {
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
				Prefs.with(activity).save(Constants.SP_PUSH_DIALOG_CONTENT,
						Constants.EMPTY_JSON_OBJECT);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dialog;
	}


	public interface Callback{
		void onButtonClicked(int deepIndex);
		void onDialogDismiss();
	}

}
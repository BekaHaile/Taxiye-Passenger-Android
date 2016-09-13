package product.clicklabs.jugnoo.home.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.home.models.RateAppDialogContent;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by shankar on 5/2/16.
 */
public class RateAppDialog {

	private final String TAG = RateAppDialog.class.getSimpleName();
	private Activity activity;
	private Dialog dialog = null;

	public RateAppDialog(Activity activity) {
		this.activity = activity;
	}

	public Dialog show(final RateAppDialogContent rateAppDialogContent) {
		try {
			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_rate_app);

			RelativeLayout relative = (RelativeLayout) dialog.findViewById(R.id.relative);
			new ASSL(activity, relative, 1134, 720, false);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);

			TextView textViewTitle = (TextView) dialog.findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.mavenMedium(activity), Typeface.BOLD);
			TextView textViewMessage = (TextView) dialog.findViewById(R.id.textViewMessage); textViewMessage.setTypeface(Fonts.mavenMedium(activity));
			Button buttonYes = (Button) dialog.findViewById(R.id.buttonYes); buttonYes.setTypeface(Fonts.mavenRegular(activity));
			Button buttonMaybe = (Button) dialog.findViewById(R.id.buttonMaybe); buttonMaybe.setTypeface(Fonts.mavenRegular(activity));
			Button buttonNo = (Button) dialog.findViewById(R.id.buttonNo); buttonNo.setTypeface(Fonts.mavenRegular(activity));

			if(rateAppDialogContent != null){
				textViewTitle.setText(rateAppDialogContent.getTitle());
				textViewMessage.setText(rateAppDialogContent.getText());
				buttonYes.setText(rateAppDialogContent.getConfirmButtonText());
				buttonMaybe.setText(rateAppDialogContent.getCancelButtonText());
				buttonNo.setText(rateAppDialogContent.getNeverButtonText());
			}

			buttonYes.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						dialog.dismiss();
						acceptAppRatingRequestAPI(activity, 0);
						if(rateAppDialogContent != null) {
							Intent intent = new Intent(Intent.ACTION_VIEW);
							intent.setData(Uri.parse(rateAppDialogContent.getUrl()));
							activity.startActivity(intent);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			buttonMaybe.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});

			buttonNo.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					acceptAppRatingRequestAPI(activity, 1);
				}
			});

			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dialog;
	}


	private void acceptAppRatingRequestAPI(final Activity activity, int neverPressed) {
		try {
			if (AppStatus.getInstance(activity).isOnline(activity)) {
				HashMap<String, String> params = new HashMap<>();
				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put(Constants.KEY_NEVER_PRESSED, String.valueOf(neverPressed));
				RestClient.getApiServices().acceptAppRatingRequest(params, new retrofit.Callback<SettleUserDebt>() {
					@Override
					public void success(SettleUserDebt settleUserDebt, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.i(TAG, "acceptAppRatingRequest response = " + responseStr);
					}

					@Override
					public void failure(RetrofitError error) {
						Log.e(TAG, "acceptAppRatingRequest error="+error.toString());
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
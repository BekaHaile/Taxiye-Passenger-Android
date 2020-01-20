package product.clicklabs.jugnoo.home.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.models.Region;
import product.clicklabs.jugnoo.home.models.RideTypeValue;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Utils;

/**
 * Created by shankar on 5/2/16.
 */
public class FareDetailsDialog {

	private final String TAG = FareDetailsDialog.class.getSimpleName();
	private HomeActivity activity;
	private Callback callback;
	private Dialog dialog = null;

	public FareDetailsDialog(HomeActivity activity, Callback callback) {
		this.activity = activity;
		this.callback = callback;
	}

	public FareDetailsDialog show() {
		try {
			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.setContentView(R.layout.dialog_fare_details);

			RelativeLayout relative = (RelativeLayout) dialog.findViewById(R.id.relative);
			new ASSL(activity, relative, 1134, 720, false);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(true);

			LinearLayout linearLayoutInner = (LinearLayout) dialog.findViewById(R.id.linearLayoutInner);
			((TextView) dialog.findViewById(R.id.textViewFareDetails)).setTypeface(Fonts.avenirNext(activity), Typeface.BOLD);
			TextView textViewMinimumFare = (TextView) dialog.findViewById(R.id.textViewMinimumFare);
			textViewMinimumFare.setTypeface(Fonts.mavenMedium(activity));
			TextView textViewKM = (TextView) dialog.findViewById(R.id.textViewKM);textViewKM.setTypeface(Fonts.mavenMedium(activity));
			((TextView) dialog.findViewById(R.id.textViewMin)).setTypeface(Fonts.mavenMedium(activity));
			TextView textViewKMValue = (TextView) dialog.findViewById(R.id.textViewKMValue);
			textViewKMValue.setTypeface(Fonts.mavenMedium(activity));
			TextView textViewMinValue = (TextView) dialog.findViewById(R.id.textViewMinValue);
			textViewMinValue.setTypeface(Fonts.mavenMedium(activity));
			TextView textViewThreshold = (TextView) dialog.findViewById(R.id.textViewThreshold);
			textViewThreshold.setTypeface(Fonts.mavenLight(activity));
			TextView textViewPoolMessage = (TextView) dialog.findViewById(R.id.textViewPoolMessage);textViewPoolMessage.setTypeface(Fonts.mavenMedium(activity));

			RelativeLayout relativeLayoutPriorityTip = (RelativeLayout) dialog.findViewById(R.id.relativeLayoutPriorityTip);
			TextView textViewPriorityTipValue = (TextView) dialog.findViewById(R.id.textViewPriorityTipValue);
			textViewPriorityTipValue.setTypeface(Fonts.mavenLight(activity), Typeface.BOLD);


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

			textViewMinimumFare.setText(Html.fromHtml(String.format(activity.getResources().getString(R.string.base_fare_format),
					Data.autoData.getFareStructure().getDisplayBaseFare(activity))));
			textViewKMValue.setText(Utils.formatCurrencyValue(Data.autoData.getFareStructure().getCurrency(), Data.autoData.getFareStructure().farePerKm, false));
			textViewMinValue.setText(Utils.formatCurrencyValue(Data.autoData.getFareStructure().getCurrency(), Data.autoData.getFareStructure().farePerMin, false));
			textViewKM.setText(activity.getString(R.string.per_format, Utils.getDistanceUnit(Data.autoData.getFareStructure().getDistanceUnit())));

			Region region = activity.getSlidingBottomPanel().getRequestRideOptionsFragment().getRegionSelected();
			double fareFactor = region.getCustomerFareFactor();
			if(fareFactor > 1.0 && activity.showSurgeIcon()){
				relativeLayoutPriorityTip.setVisibility(View.VISIBLE);
				textViewPriorityTipValue.setText(String.format(activity.getResources().getString(R.string.format_x),
						Utils.getMoneyDecimalFormat().format(fareFactor)));
			} else{
				relativeLayoutPriorityTip.setVisibility(View.GONE);
			}

			if(region.getRideType() == RideTypeValue.POOL.getOrdinal()
					&& !"".equalsIgnoreCase(Data.autoData.getBaseFarePoolText())){
				textViewPoolMessage.setVisibility(View.VISIBLE);
				textViewPoolMessage.setText(Data.autoData.getBaseFarePoolText());
			} else{
				textViewPoolMessage.setVisibility(View.GONE);
			}

			textViewThreshold.setVisibility(View.GONE);
			if(!"".equalsIgnoreCase(Data.autoData.getFareStructure().getDisplayFareText(activity))){
				textViewThreshold.setVisibility(View.VISIBLE);
				textViewThreshold.setText(Data.autoData.getFareStructure().getDisplayFareText(activity));
			}

			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}


	public interface Callback{
		void onDialogDismiss();
	}

}
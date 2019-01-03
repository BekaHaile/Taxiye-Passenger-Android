package product.clicklabs.jugnoo.home.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;

/**
 * Created by shankar on 5/2/16.
 */
public class SavedAddressPickupDialog implements GACategory, GAAction {

	private final String TAG = SavedAddressPickupDialog.class.getSimpleName();
	private HomeActivity activity;
	private Callback callback;
	private Dialog dialog = null;
	private SearchResult searchResult;

	public SavedAddressPickupDialog(HomeActivity activity, SearchResult searchResult, Callback callback) {
		this.activity = activity;
		this.callback = callback;
		this.searchResult = searchResult;
	}

	public Dialog show() {
		try {
			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.setContentView(R.layout.dialog_saved_address_pickup_request);

			RelativeLayout relative = (RelativeLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, relative, 1134, 720, false);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(true);

			RelativeLayout rlInner = (RelativeLayout) dialog.findViewById(R.id.rlInner);
			RelativeLayout rlAdressItem = (RelativeLayout) dialog.findViewById(R.id.relative);
			TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Fonts.mavenRegular(activity));
			ImageView imageViewType = (ImageView) dialog.findViewById(R.id.imageViewType);
			TextView textViewSearchName = (TextView) dialog.findViewById(R.id.textViewSearchName); textViewSearchName.setTypeface(Fonts.mavenMedium(activity));
			TextView textViewSearchAddress = (TextView) dialog.findViewById(R.id.textViewSearchAddress); textViewSearchAddress.setTypeface(Fonts.mavenRegular(activity));
			TextView textViewAddressUsed = (TextView) dialog.findViewById(R.id.textViewAddressUsed); textViewAddressUsed.setVisibility(View.GONE);
			ImageView imageViewSep = (ImageView) dialog.findViewById(R.id.imageViewSep); imageViewSep.setVisibility(View.GONE);
			ImageView ivDeleteAddress = (ImageView) dialog.findViewById(R.id.ivDeleteAddress); ivDeleteAddress.setVisibility(View.GONE);
			Button btnYes = (Button) dialog.findViewById(R.id.btnYes); btnYes.setTypeface(Fonts.mavenMedium(activity));
			Button btnNo = (Button) dialog.findViewById(R.id.btnNo);btnNo.setTypeface(Fonts.mavenMedium(activity));
			View viewSep = dialog.findViewById(R.id.viewSep);

			if(TextUtils.isEmpty(searchResult.getName())){
				textViewSearchName.setVisibility(View.GONE);
			} else{
				textViewSearchName.setVisibility(View.VISIBLE);
			}

			textViewSearchName.setText(searchResult.getName());
			textViewSearchAddress.setText(searchResult.getAddress());

			if(TextUtils.isEmpty(searchResult.getAddress())){
				viewSep.setVisibility(View.GONE);
				rlAdressItem.setVisibility(View.GONE);
				textHead.setText(R.string.close_to_point_of_interest);
				GAUtils.event(RIDES, PICKUP_LOCATION_DIFFERENT_FROM_CURRENT_LOCATION+POPUP, REQUEST_LOCATION_CLOSE_TO_POI);
			}

			if(searchResult.getName().equalsIgnoreCase(Constants.TYPE_HOME)){
				imageViewType.setImageResource(R.drawable.ic_home);
			} else if(searchResult.getName().equalsIgnoreCase(Constants.TYPE_WORK)){
				imageViewType.setImageResource(R.drawable.ic_work);
			} else {
				imageViewType.setImageResource(R.drawable.ic_loc_other);
			}

			relative.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});

			rlInner.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

				}
			});

			dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					//callback.onDialogDismiss();
				}
			});

			btnYes.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					callback.yesClicked(searchResult);
					dialog.dismiss();
				}
			});

			btnNo.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					callback.onDialogDismiss();
					dialog.dismiss();
				}
			});

			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dialog;
	}


	public interface Callback{
		void onDialogDismiss();
		void yesClicked(SearchResult searchResult);
	}

}
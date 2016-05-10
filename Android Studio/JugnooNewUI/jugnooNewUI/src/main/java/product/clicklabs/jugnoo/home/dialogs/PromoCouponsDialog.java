package product.clicklabs.jugnoo.home.dialogs;

import android.app.Dialog;
import android.graphics.Typeface;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.adapters.PromoCouponsAdapter;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;

/**
 * Created by shankar on 5/2/16.
 */
public class PromoCouponsDialog {

	private final String TAG = PromoCouponsDialog.class.getSimpleName();
	private HomeActivity activity;
	private Callback callback;
	private Dialog dialog = null;

	private RecyclerView recyclerViewPromoCoupons;
	private PromoCouponsAdapter promoCouponsAdapter;

	public PromoCouponsDialog(HomeActivity activity, Callback callback) {
		this.activity = activity;
		this.callback = callback;
	}

	public PromoCouponsDialog show() {
		try {
			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogScale;
			dialog.setContentView(R.layout.dialog_promo_coupons);

			RelativeLayout relative = (RelativeLayout) dialog.findViewById(R.id.relative);
			new ASSL(activity, relative, 1134, 720, false);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);

			((TextView) dialog.findViewById(R.id.textViewCoupons)).setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);

			recyclerViewPromoCoupons = (RecyclerView) dialog.findViewById(R.id.recyclerViewPromoCoupons);
			recyclerViewPromoCoupons.setLayoutManager(new LinearLayoutManager(activity));
			recyclerViewPromoCoupons.setItemAnimator(new DefaultItemAnimator());
			recyclerViewPromoCoupons.setHasFixedSize(false);

			promoCouponsAdapter = new PromoCouponsAdapter(activity, Data.promoCoupons);

			LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) recyclerViewPromoCoupons.getLayoutParams();
			params.height = Data.promoCoupons.size() > 3 ? (int)(84f * 3f * ASSL.Yscale())
					: (int)(84f * (float)Data.promoCoupons.size() * ASSL.Yscale());
			recyclerViewPromoCoupons.setLayoutParams(params);
			recyclerViewPromoCoupons.setAdapter(promoCouponsAdapter);

			Button buttonApply = (Button) dialog.findViewById(R.id.buttonApply);
			buttonApply.setTypeface(Fonts.mavenRegular(activity));

			ImageView imageViewClose = (ImageView) dialog.findViewById(R.id.imageViewClose);
			imageViewClose.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					callback.onCancelled();
				}
			});

			buttonApply.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					callback.onCouponApplied();
				}
			});

			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}


	public interface Callback{
		void onCouponApplied();
		void onCancelled();
	}



}
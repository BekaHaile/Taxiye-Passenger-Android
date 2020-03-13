package product.clicklabs.jugnoo.home.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.MenuInfoTags;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.adapters.PromoCouponsAdapter;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;

/**
 * Created by shankar on 5/2/16.
 */
public class PromoCouponsDialog implements GACategory, GAAction{

	private Activity activity;
	private Callback callback;
	private Dialog dialog = null;

	private PromoCouponsAdapter promoCouponsAdapter;


	private int couponSelectedIndex = -1;
	private PromoCoupon couponSelected;

	public PromoCouponsDialog(Activity activity, Callback callback) {
		this.activity = activity;
		this.callback = callback;
	}

	public PromoCouponsDialog show(final ArrayList<PromoCoupon> promoCouponsTemp) {
		try {
			ArrayList<PromoCoupon> promoCoupons = new ArrayList<>();
			if(promoCouponsTemp != null) {
				promoCoupons.clear();
				for (int i = 0; i < promoCouponsTemp.size(); i++) {
					if ((promoCouponsTemp.get(i).getCouponCardType() == 1 && promoCouponsTemp.get(i).isScratched())
							|| promoCouponsTemp.get(i).getCouponCardType() == 0) {
						promoCoupons.add(promoCouponsTemp.get(i));
					}
				}
			}
			couponSelected = ((HomeActivity)activity).getSlidingBottomPanel().getRequestRideOptionsFragment().getSelectedCoupon();

			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.setContentView(R.layout.dialog_promo_coupons);

			RelativeLayout relative =
					dialog.findViewById(R.id.relative);
			new ASSL(activity, relative, 1134, 720, false);

			if(dialog.getWindow() != null) {
				WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
				layoutParams.dimAmount = 0.6f;
				dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			}
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);

			LinearLayout linearLayoutInner = dialog.findViewById(R.id.linearLayoutInner);
			ListView listViewPromoCoupons = dialog.findViewById(R.id.listViewPromoCoupons);

			promoCouponsAdapter = new PromoCouponsAdapter(activity, R.layout.list_item_promo_coupon, promoCoupons, new PromoCouponsAdapter.Callback() {

				@Override
				public PromoCoupon getSelectedCoupon() {
					if(activity instanceof HomeActivity) {
						return couponSelected;
					} else {
						return null;
					}
				}

				@Override
				public boolean setSelectedCoupon(int position, PromoCoupon pc) {
					if(activity instanceof HomeActivity) {
						if(pc == null || HomeActivity.checkCouponDropValidity(pc)){
							couponSelectedIndex = position;
							couponSelected = pc;
							return true;
						} else {
							Utils.showToast(activity, activity.getString(R.string.offer_not_valid_for_selected_drop_location));
							return false;
						}
					} else {
						return false;
					}
				}


			});

			LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) listViewPromoCoupons.getLayoutParams();
			if(promoCoupons.size() > 2){
				params.height = (int)(520f * ASSL.Yscale());
			}
			listViewPromoCoupons.setLayoutParams(params);
			listViewPromoCoupons.setAdapter(promoCouponsAdapter);

			Button buttonSkip = dialog.findViewById(R.id.buttonSkip);
			buttonSkip.setTypeface(Fonts.mavenRegular(activity));
			Button buttonContinue = dialog.findViewById(R.id.buttonContinue);
			buttonContinue.setTypeface(Fonts.mavenRegular(activity));
			RelativeLayout relativeLayoutBottomButtons = dialog.findViewById(R.id.relativeLayoutBottomButtons);
			LinearLayout linearLayoutNoCurrentOffers = dialog.findViewById(R.id.linearLayoutNoCurrentOffers);
			TextView textViewNoCurrentOffers = dialog.findViewById(R.id.textViewNoCurrentOffers);
			textViewNoCurrentOffers.setTypeface(Fonts.mavenMedium(activity));
			textViewNoCurrentOffers.setText(activity.getString(R.string.no_current_offer_popup_text, activity.getString(R.string.app_name)));
			TextView tvAvailableOffers = dialog.findViewById(R.id.tvAvailableOffers);
			tvAvailableOffers.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
			Button buttonInviteFriends = dialog.findViewById(R.id.buttonInviteFriends);
			buttonInviteFriends.setTypeface(Fonts.mavenMedium(activity));
			ImageView imageViewOffers = dialog.findViewById(R.id.imageViewOffers);
			ImageView ivNoOffer = dialog.findViewById(R.id.ivNoOffer);

			try {
				if(promoCoupons.size() > 0){
                    listViewPromoCoupons.setVisibility(View.VISIBLE);
                    linearLayoutNoCurrentOffers.setVisibility(View.GONE);
                    imageViewOffers.setImageResource(R.drawable.ic_offer_popup);
                    if(activity instanceof HomeActivity){
                        relativeLayoutBottomButtons.setVisibility(View.VISIBLE);
                    }
                } else{
                    listViewPromoCoupons.setVisibility(View.GONE);
                    relativeLayoutBottomButtons.setVisibility(View.GONE);
                    linearLayoutNoCurrentOffers.setVisibility(Data.isMenuTagEnabled(MenuInfoTags.FREE_RIDES)?View.VISIBLE:View.GONE);
                    imageViewOffers.setImageResource(R.drawable.ic_offer_popup);
                    tvAvailableOffers.setText(activity.getResources().getString(R.string.no_available_offers));

                    if(!"".equalsIgnoreCase(Data.userData.getInviteEarnScreenImage())){
                        Picasso.with(activity).load(Data.userData.getInviteEarnScreenImage())
                                .into(ivNoOffer);
                    }

                    textViewNoCurrentOffers.setText(Data.userData.getReferralMessages().referralShortMessage);
                }
			} catch (Exception e) {
				e.printStackTrace();
			}

			buttonSkip.setOnClickListener(v -> {
				dialog.dismiss();
			});


			buttonContinue.setOnClickListener(v -> {
				if(activity instanceof  HomeActivity){
					if(couponSelected != ((HomeActivity)activity).getSlidingBottomPanel().getRequestRideOptionsFragment().getSelectedCoupon()){
						if(couponSelected != null && couponSelectedIndex > -1){
							((HomeActivity) activity).promoSelectionLastOperation = true;
							((HomeActivity)activity).getSlidingBottomPanel().getRequestRideOptionsFragment().setSelectedCoupon(couponSelectedIndex, false);
						} else {
							((HomeActivity) activity).promoSelectionLastOperation = false;
							((HomeActivity)activity).getSlidingBottomPanel().getRequestRideOptionsFragment().setSelectedCoupon(-1, false);
						}
					}
				}
				dialog.dismiss();
				callback.onCouponApplied();
			});

			buttonInviteFriends.setOnClickListener(v -> {
				dialog.dismiss();
				callback.onInviteFriends();
			});


			relative.setOnClickListener(v -> dialog.dismiss());

			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}


	public void notifyCoupons(){
		try {
			if(promoCouponsAdapter != null) {
				promoCouponsAdapter.notifyDataSetChanged();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public interface Callback{
		void onCouponApplied();
		void onInviteFriends();
	}



}
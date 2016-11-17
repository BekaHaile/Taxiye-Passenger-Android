package product.clicklabs.jugnoo.home.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.home.FreshActivity;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.CouponInfo;
import product.clicklabs.jugnoo.datastructure.ProductType;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.adapters.PromoCouponsAdapter;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.FirebaseEvents;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.Fonts;

/**
 * Created by shankar on 5/2/16.
 */
public class PromoCouponsDialog {

	private final String TAG = PromoCouponsDialog.class.getSimpleName();
	private Activity activity;
	private Callback callback;
	private Dialog dialog = null;

	private ListView listViewPromoCoupons;
	private PromoCouponsAdapter promoCouponsAdapter;
	private Button buttonContinue, buttonInviteFriends;
	private LinearLayout linearLayoutNoCurrentOffers;
	private TextView textViewNoCurrentOffers, tvAvailableOffers;
	private ImageView imageViewOffers;
	private PromoCoupon noSelectionCoupon = new CouponInfo(-1, "Don't apply coupon on this ride");

	public PromoCouponsDialog(Activity activity, Callback callback) {
		this.activity = activity;
		this.callback = callback;
	}

	public PromoCouponsDialog show(ProductType productType, final ArrayList<PromoCoupon> promoCoupons) {
		try {
			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogScale;
			dialog.setContentView(R.layout.dialog_promo_coupons);

			RelativeLayout relative = (RelativeLayout) dialog.findViewById(R.id.relative);
			new ASSL(activity, relative, 1134, 720, false);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(true);

			LinearLayout linearLayoutInner = (LinearLayout) dialog.findViewById(R.id.linearLayoutInner);
			listViewPromoCoupons = (ListView) dialog.findViewById(R.id.listViewPromoCoupons);
			if(productType == ProductType.AUTO) {
				FlurryEventLogger.eventGA(Constants.REVENUE + Constants.SLASH + Constants.ACTIVATION + Constants.SLASH + Constants.RETENTION, "Home Screen", "b_offer");
			}

			promoCouponsAdapter = new PromoCouponsAdapter(activity, R.layout.list_item_promo_coupon, promoCoupons, new PromoCouponsAdapter.Callback() {
				@Override
				public void onCouponSelected() {

				}

				@Override
				public PromoCoupon getSelectedCoupon() {
					if(activity instanceof HomeActivity) {
						return ((HomeActivity)activity).getSlidingBottomPanel().getRequestRideOptionsFragment().getSelectedCoupon();
					} else if(activity instanceof FreshActivity) {
						return ((FreshActivity)activity).getSelectedPromoCoupon();
					} else {
						return noSelectionCoupon;
					}
				}

				@Override
				public void setSelectedCoupon(int position) {
					if(activity instanceof HomeActivity) {
						((HomeActivity)activity).getSlidingBottomPanel().getRequestRideOptionsFragment().setSelectedCoupon(position);;
					} else if(activity instanceof FreshActivity) {
						PromoCoupon promoCoupon;
						if (promoCoupons != null && position > -1 && position < promoCoupons.size()) {
							promoCoupon = promoCoupons.get(position);
						} else {
							promoCoupon = noSelectionCoupon;
						}
						if(MyApplication.getInstance().getWalletCore().displayAlertAndCheckForSelectedWalletCoupon(activity,
								((FreshActivity)activity).getPaymentOption().getOrdinal(), promoCoupon)){
							((FreshActivity)activity).setSelectedPromoCoupon(promoCoupon);
						}
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								dialog.dismiss();
							}
						}, 100);
						callback.onCouponApplied();
					}
				}
			});

			LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) listViewPromoCoupons.getLayoutParams();
			params.height = promoCoupons.size() > 3 ? (int)(156f * 3f * ASSL.Yscale())
					: (int)(84f * (float)promoCoupons.size() * ASSL.Yscale());
			listViewPromoCoupons.setLayoutParams(params);
			listViewPromoCoupons.setAdapter(promoCouponsAdapter);

			Button buttonSkip = (Button) dialog.findViewById(R.id.buttonSkip);
			buttonSkip.setTypeface(Fonts.mavenRegular(activity));
			buttonContinue = (Button) dialog.findViewById(R.id.buttonContinue);
			buttonContinue.setTypeface(Fonts.mavenRegular(activity));
			RelativeLayout relativeLayoutBottomButtons = (RelativeLayout) dialog.findViewById(R.id.relativeLayoutBottomButtons);
			ImageView imageViewClose = (ImageView) dialog.findViewById(R.id.imageViewClose);
			linearLayoutNoCurrentOffers = (LinearLayout)dialog.findViewById(R.id.linearLayoutNoCurrentOffers);
			textViewNoCurrentOffers = (TextView)dialog.findViewById(R.id.textViewNoCurrentOffers);textViewNoCurrentOffers.setTypeface(Fonts.mavenMedium(activity));
			tvAvailableOffers = (TextView) dialog.findViewById(R.id.tvAvailableOffers); tvAvailableOffers.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
			buttonInviteFriends = (Button)dialog.findViewById(R.id.buttonInviteFriends);buttonInviteFriends.setTypeface(Fonts.mavenMedium(activity));
			imageViewOffers = (ImageView)dialog.findViewById(R.id.imageViewOffers);

			if(promoCoupons.size() > 0){
				listViewPromoCoupons.setVisibility(View.VISIBLE);
				linearLayoutNoCurrentOffers.setVisibility(View.GONE);
				imageViewOffers.setImageResource(R.drawable.ic_offer_popup);
				if(activity instanceof HomeActivity){
					relativeLayoutBottomButtons.setVisibility(View.VISIBLE);
				} else if(activity instanceof FreshActivity){
					relativeLayoutBottomButtons.setVisibility(View.GONE);
				}
			} else{
				listViewPromoCoupons.setVisibility(View.GONE);
				relativeLayoutBottomButtons.setVisibility(View.GONE);
				linearLayoutNoCurrentOffers.setVisibility(View.VISIBLE);
				imageViewOffers.setImageResource(R.drawable.no_current_offer);
			}

			buttonSkip.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(activity instanceof HomeActivity) {
						Bundle bundle = new Bundle();
						MyApplication.getInstance().logEvent(FirebaseEvents.TRANSACTION + "_" + FirebaseEvents.B_OFFER + "_"
								+ FirebaseEvents.SKIP, bundle);
						FlurryEventLogger.eventGA(Constants.REVENUE + Constants.SLASH + Constants.ACTIVATION + Constants.SLASH + Constants.RETENTION, "b_offer", "skip");
						((HomeActivity)activity).getSlidingBottomPanel().getRequestRideOptionsFragment().setSelectedCoupon(-1);
						dialog.dismiss();
						callback.onSkipped();
					}
				}
			});

			imageViewClose.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(activity instanceof HomeActivity) {
						((HomeActivity)activity).getSlidingBottomPanel().getRequestRideOptionsFragment().setSelectedCoupon(-1);
						dialog.dismiss();
						callback.onSkipped();
					}
				}
			});

			buttonContinue.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    MyApplication.getInstance().logEvent(FirebaseEvents.TRANSACTION+"_"+ FirebaseEvents.B_OFFER+"_"
                            +FirebaseEvents.CONTINUE, bundle);
					FlurryEventLogger.eventGA(Constants.REVENUE + Constants.SLASH + Constants.ACTIVATION + Constants.SLASH + Constants.RETENTION, "b_offer", "continue");
					dialog.dismiss();
					callback.onCouponApplied();
				}
			});

			buttonInviteFriends.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    MyApplication.getInstance().logEvent(FirebaseEvents.TRANSACTION+"_"+ FirebaseEvents.B_OFFER+"_"
                            +FirebaseEvents.INVITE_FRIENDS, bundle);
					FlurryEventLogger.eventGA(Constants.REVENUE + Constants.SLASH + Constants.ACTIVATION + Constants.SLASH + Constants.RETENTION, "b_offer", "invite friends");
					dialog.dismiss();
					callback.onInviteFriends();
				}
			});

			relative.setOnClickListener(new View.OnClickListener() {
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

			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}

	public void setContinueButton(){
		if(activity instanceof HomeActivity && buttonContinue != null) {
			if (((HomeActivity)activity).getSlidingBottomPanel().getRequestRideOptionsFragment().getSelectedCoupon().getId() != -1) {
				buttonContinue.setEnabled(true);
				buttonContinue.setAlpha(1.0f);
			} else {
				buttonContinue.setEnabled(false);
				buttonContinue.setAlpha(0.5f);
			}
		}
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
		void onSkipped();
		void onInviteFriends();
	}



}
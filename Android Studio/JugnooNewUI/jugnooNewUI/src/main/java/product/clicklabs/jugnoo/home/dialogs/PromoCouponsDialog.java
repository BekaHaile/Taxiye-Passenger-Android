package product.clicklabs.jugnoo.home.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Handler;
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
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.home.FreshActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.CouponInfo;
import product.clicklabs.jugnoo.datastructure.ProductType;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.adapters.PromoCouponsAdapter;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.NonScrollListView;

/**
 * Created by shankar on 5/2/16.
 */
public class PromoCouponsDialog implements GACategory, GAAction{

	private final String TAG = PromoCouponsDialog.class.getSimpleName();
	private Activity activity;
	private Callback callback;
	private Dialog dialog = null;

	private ListView listViewPromoCoupons;
	private PromoCouponsAdapter promoCouponsAdapter;
	private Button buttonContinue, buttonInviteFriends;
	private LinearLayout linearLayoutNoCurrentOffers;
	private TextView textViewNoCurrentOffers, tvAvailableOffers;
	private ImageView imageViewOffers, ivNoOffer;
	private PromoCoupon noSelectionCoupon = new CouponInfo(-1, "Don't apply coupon on this ride");
	private PromoCoupon couponSelectedWhenDialogShown;
	private boolean onDialogOpenPromoSelectOperation;
	public PromoCouponsDialog(Activity activity, Callback callback) {
		this.activity = activity;
		this.callback = callback;
	}

	public PromoCouponsDialog show(ProductType productType, final ArrayList<PromoCoupon> promoCoupons) {
		try {
			onDialogOpenPromoSelectOperation = ((HomeActivity)activity).promoSelectionLastOperation;
			couponSelectedWhenDialogShown =((HomeActivity)activity).getSlidingBottomPanel().getRequestRideOptionsFragment().getSelectedCoupon();
			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogScale;
			dialog.setContentView(R.layout.dialog_promo_coupons);
			dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					if(activity instanceof HomeActivity) {
						if(couponSelectedWhenDialogShown==null){
							((HomeActivity)activity).getSlidingBottomPanel().getRequestRideOptionsFragment().setSelectedCoupon(-1);

						}else{
							((HomeActivity)activity).getSlidingBottomPanel().getRequestRideOptionsFragment().setSelectedCoupon(couponSelectedWhenDialogShown);
						}
						callback.onSkipped();
					}
				}
			});
			RelativeLayout relative =
					(RelativeLayout) dialog.findViewById(R.id.relative);
			new ASSL(activity, relative, 1134, 720, false);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(true);

			LinearLayout linearLayoutInner = (LinearLayout) dialog.findViewById(R.id.linearLayoutInner);
			listViewPromoCoupons = (ListView) dialog.findViewById(R.id.listViewPromoCoupons);

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
				public boolean setSelectedCoupon(int position) {
					if(activity instanceof HomeActivity) {
						if (promoCoupons != null && position > -1 && position < promoCoupons.size() ) {
							onDialogOpenPromoSelectOperation = true;
						} else {
							onDialogOpenPromoSelectOperation = false;
						}

						return ((HomeActivity)activity).getSlidingBottomPanel().getRequestRideOptionsFragment().setSelectedCoupon(position);
					} else if(activity instanceof FreshActivity) {
						PromoCoupon promoCoupon;
						if (promoCoupons != null && position > -1 && position < promoCoupons.size()) {
							promoCoupon = promoCoupons.get(position);
						} else {
							promoCoupon = noSelectionCoupon;
						}
						boolean offerApplied = false;
						if(MyApplication.getInstance().getWalletCore().displayAlertAndCheckForSelectedWalletCoupon(activity,
								((FreshActivity)activity).getPaymentOption().getOrdinal(), promoCoupon)){
							((FreshActivity)activity).setSelectedPromoCoupon(promoCoupon);
							offerApplied = true;
						}
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								dialog.dismiss();
							}
						}, 100);
						callback.onCouponApplied();
						GAUtils.event(RIDES, TNC+CLICKED, promoCoupon.getTitle());
						return offerApplied;
					} else {
						return false;
					}
				}

				@Override
				public void applyPromoCoupon(String text) {

				}

				@Override
				public NonScrollListView getListView() {
					return null;
				}
			});

			/*LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) listViewPromoCoupons.getLayoutParams();
			params.height = promoCoupons.size() > 3 ? (int)(156f * 3f * ASSL.Yscale())
					: (int)(156f * (float)promoCoupons.size() * ASSL.Yscale());
			listViewPromoCoupons.setLayoutParams(params);*/
			LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) listViewPromoCoupons.getLayoutParams();
			if(promoCoupons.size() > 2){
				params.height = (int)(520f * ASSL.Yscale());
			}
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
			ivNoOffer = (ImageView) dialog.findViewById(R.id.ivNoOffer);

			try {
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
                    imageViewOffers.setImageResource(R.drawable.ic_offer_popup);
                    tvAvailableOffers.setText(activity.getResources().getString(R.string.no_available_offers));

                    if(!"".equalsIgnoreCase(Data.userData.getInviteEarnScreenImage())){
                        Picasso.with(activity).load(Data.userData.getInviteEarnScreenImage())
                                .placeholder(R.drawable.ic_logo_white_bg)
                                .error(R.drawable.ic_logo_white_bg)
                                .into(ivNoOffer);
                    }

                    textViewNoCurrentOffers.setText(Data.userData.getReferralMessages().referralShortMessage);
                }
			} catch (Exception e) {
				e.printStackTrace();
			}

			buttonSkip.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(activity instanceof HomeActivity) {
						if(couponSelectedWhenDialogShown==null){
							((HomeActivity)activity).getSlidingBottomPanel().getRequestRideOptionsFragment().setSelectedCoupon(-1);

						}else{
							((HomeActivity)activity).getSlidingBottomPanel().getRequestRideOptionsFragment().setSelectedCoupon(couponSelectedWhenDialogShown);
						}
						dialog.dismiss();
						callback.onSkipped();
					}
				}
			});

			imageViewClose.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(activity instanceof HomeActivity) {
						if(couponSelectedWhenDialogShown==null){
							((HomeActivity)activity).getSlidingBottomPanel().getRequestRideOptionsFragment().setSelectedCoupon(-1);

						}else{
							((HomeActivity)activity).getSlidingBottomPanel().getRequestRideOptionsFragment().setSelectedCoupon(couponSelectedWhenDialogShown);
						}
						dialog.dismiss();
						callback.onSkipped();
					}
				}
			});

			buttonContinue.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(activity instanceof  HomeActivity){
						((HomeActivity)activity).promoSelectionLastOperation = onDialogOpenPromoSelectOperation;
						couponSelectedWhenDialogShown =((HomeActivity)activity).getSlidingBottomPanel().getRequestRideOptionsFragment().getSelectedCoupon();

					}
					dialog.dismiss();
					callback.onCouponApplied();
				}
			});

			buttonInviteFriends.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
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
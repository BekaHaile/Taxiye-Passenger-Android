package product.clicklabs.jugnoo.home;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Locale;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.AddPaymentPath;
import product.clicklabs.jugnoo.datastructure.CouponInfo;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.home.fragments.RequestRideOptionsFragment;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.NudgeClient;
import product.clicklabs.jugnoo.wallet.PaymentActivity;

/**
 * Created by Ankit on 1/7/16.
 */
public class SlidingBottomPanel {

    private HomeActivity activity;
    private SlidingUpPanelLayout slidingUpPanelLayout;
    private ImageView imageViewExtraForSliding;
    private RequestRideOptionsFragment requestRideOptionsFragment;

    private final String TAG = SlidingBottomPanel.class.getSimpleName();

    private PromoCoupon selectedCoupon = null;
    private PromoCoupon noSelectionCoupon = new CouponInfo(-1, "Don't apply coupon on this ride");

    public SlidingBottomPanel(HomeActivity activity, View view) {
        this.activity = activity;
        initComponents(view);
    }

    private void initComponents(View view) {
        //SlidingUp Layout
        requestRideOptionsFragment = ((RequestRideOptionsFragment) activity.getSupportFragmentManager().findFragmentById(R.id.frag));
        imageViewExtraForSliding = (ImageView)view.findViewById(R.id.imageViewExtraForSliding);

        slidingUpPanelLayout = (SlidingUpPanelLayout) view.findViewById(R.id.slidingLayout);
        slidingUpPanelLayout.setParallaxOffset((int) (260 * ASSL.Yscale()));
        slidingUpPanelLayout.setPanelHeight((int) (108 * ASSL.Yscale()));

        slidingUpPanelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                imageViewExtraForSliding.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPanelExpanded(View panel) {
                imageViewExtraForSliding.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPanelCollapsed(View panel) {
                imageViewExtraForSliding.setVisibility(View.GONE);
            }

            @Override
            public void onPanelAnchored(View panel) {
            }

            @Override
            public void onPanelHidden(View panel) {
            }
        });

        imageViewExtraForSliding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewExtraForSliding.setVisibility(View.GONE);
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        update();

    }

    public void update() {
        try {
            if (Data.promoCoupons != null) {
				if(selectedCoupon == null) {
					if (Data.promoCoupons.size() > 0) {
						selectedCoupon = noSelectionCoupon;
                        nudgeCouponsEvent();
					} else {
						selectedCoupon = new CouponInfo(0, "");
                        NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_NO_COUPONS, null);
					}
				}
            }

            requestRideOptionsFragment.updatePaymentOption();
            requestRideOptionsFragment.updateRegionsUI();
            requestRideOptionsFragment.updateFareStructureUI();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public SlidingUpPanelLayout getSlidingUpPanelLayout() {
        return slidingUpPanelLayout;
    }

    public PromoCoupon getSelectedCoupon() {
        return selectedCoupon;
    }

    public void setSelectedCoupon(int position) {
        if (position > -1 && position < Data.promoCoupons.size()) {
            selectedCoupon = Data.promoCoupons.get(position);
        } else {
            selectedCoupon = noSelectionCoupon;
        }
        displayAlertAndCheckForSelectedPaytmCoupon(selectedCoupon);
    }

    public void setSelectedCoupon(PromoCoupon promoCoupon){
        selectedCoupon = promoCoupon;
    }

    public void updatePreferredPaymentOptionUI() {
//        Fragment frag1 = activity.getSupportFragmentManager().findFragmentByTag("android:switcher:" + viewPager.getId() + ":" + 0);
//        if (frag1 != null && frag1 instanceof SlidingBottomCashFragment) {
//            ((SlidingBottomCashFragment) frag1).updatePreferredPaymentOptionUI();
//        }
    }

    public boolean displayAlertAndCheckForSelectedPaytmCoupon() {
        return displayAlertAndCheckForSelectedPaytmCoupon(selectedCoupon);
    }

    private boolean displayAlertAndCheckForSelectedPaytmCoupon(PromoCoupon promoCoupon) {
        try {
            if (isPaytmCoupon(promoCoupon)) {
                if (PaymentOption.PAYTM.getOrdinal() != Data.pickupPaymentOption) {
                    View.OnClickListener onClickListenerPaymentOption = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openPaymentActivityInCaseOfPaytmNotAdded();
                        }
                    };
                    View.OnClickListener onClickListenerCancel = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    };
                    if (Data.userData.paytmEnabled == 1) {
                        DialogPopup.alertPopupWithListener(activity, "",
                                activity.getResources().getString(R.string.paytm_coupon_selected_but_paytm_option_not_selected),
                                onClickListenerCancel);
                    } else {
                        DialogPopup.alertPopupTwoButtonsWithListeners(activity, "",
                                activity.getResources().getString(R.string.paytm_coupon_selected_but_paytm_not_added),
                                activity.getResources().getString(R.string.ok),
                                activity.getResources().getString(R.string.cancel),
                                onClickListenerPaymentOption,
                                onClickListenerCancel,
                                true, false);
                    }
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public void openPaymentActivityInCaseOfPaytmNotAdded() {
        if (Data.userData.paytmEnabled != 1 || !Data.userData.getPaytmStatus().equalsIgnoreCase(Data.PAYTM_STATUS_ACTIVE)) {
            Intent intent = new Intent(activity, PaymentActivity.class);
            intent.putExtra(Constants.KEY_ADD_PAYMENT_PATH, AddPaymentPath.ADD_PAYTM.getOrdinal());
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
            FlurryEventLogger.event(FlurryEventNames.WALLET_BEFORE_REQUEST_RIDE);
        }
    }


    public void setRegionSelected(int position) {
//        if (position > -1 && position < Data.regions.size()) {
//            regionSelected = Data.regions.get(position);
//        }
//        vehiclesTabAdapter.notifyDataSetChanged();
//        recyclerViewVehicles.getLayoutManager().scrollToPosition(position);
//        updateFareStructureUI();
    }

    private boolean isPaytmCoupon(PromoCoupon pc){
        if(pc.getTitle().toLowerCase(Locale.ENGLISH)
                .contains(activity.getString(R.string.paytm).toLowerCase(Locale.ENGLISH))) {
            return true;
        }
        return false;
    }


    private void nudgeCouponsEvent(){
        try {
            JSONObject map = new JSONObject();
            JSONArray coups = new JSONArray();
            JSONArray coupsP = new JSONArray();
            for(PromoCoupon pc : Data.promoCoupons){
                if(isPaytmCoupon(pc)){
                    coupsP.put(pc.getTitle());
                } else{
                    coups.put(pc.getTitle());
                }
            }
            map.put(Constants.KEY_COUPONS, coups.toString());
            NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_COUPON_AVAILABLE, map);

            if(coupsP.length() > 0) {
                map.put(Constants.KEY_COUPONS, coupsP.toString());
                NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_PAYTM_COUPON_AVAILABLE, map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RequestRideOptionsFragment getRequestRideOptionsFragment(){
        return requestRideOptionsFragment;
    }

}

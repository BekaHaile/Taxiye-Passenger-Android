package product.clicklabs.jugnoo.home;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.adapters.SlidingBottomFragmentAdapter;
import product.clicklabs.jugnoo.datastructure.AddPaymentPath;
import product.clicklabs.jugnoo.datastructure.CouponInfo;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.datastructure.PromotionInfo;
import product.clicklabs.jugnoo.fragments.SlidingBottomCashFragment;
import product.clicklabs.jugnoo.fragments.SlidingBottomFareFragment;
import product.clicklabs.jugnoo.fragments.SlidingBottomOffersFragment;
import product.clicklabs.jugnoo.home.adapters.VehiclesTabAdapter;
import product.clicklabs.jugnoo.home.models.Region;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.LinearLayoutManagerForResizableRecyclerView;
import product.clicklabs.jugnoo.utils.NudgeClient;
import product.clicklabs.jugnoo.utils.Utils;
import product.clicklabs.jugnoo.wallet.PaymentActivity;
import product.clicklabs.jugnoo.widgets.PagerSlidingTabStrip;

/**
 * Created by Ankit on 1/7/16.
 */
public class SlidingBottomPanel {

    private HomeActivity activity;
    private SlidingUpPanelLayout slidingUpPanelLayout;
    private PagerSlidingTabStrip tabs;
    private final String TAG = SlidingBottomPanel.class.getSimpleName();
    private ViewPager viewPager;
    private SlidingBottomFragmentAdapter slidingBottomFragmentAdapter;
    private ImageView imageViewPaymentOp, imageViewExtraForSliding;
    private TextView textViewMinFareValue, textViewOffersValue, textViewCashValue;
    private PromoCoupon selectedCoupon = null;
    private PromoCoupon noSelectionCoupon = new CouponInfo(-1, "Don't apply coupon on this ride");
    private ArrayList<PromoCoupon> promoCoupons;

    private Region regionSelected = null;

    private RecyclerView recyclerViewVehicles;
    private VehiclesTabAdapter vehiclesTabAdapter;

    public SlidingBottomPanel(HomeActivity activity, View view) {
        this.activity = activity;
        initComponents(view);
    }

    private void initComponents(View view) {
        //SlidingUp Layout
        ((TextView) view.findViewById(R.id.textViewMinFare)).setTypeface(Fonts.mavenLight(activity));
        textViewMinFareValue = (TextView) view.findViewById(R.id.textViewMinFareValue);
        textViewMinFareValue.setTypeface(Fonts.mavenRegular(activity));
        ((TextView) view.findViewById(R.id.textViewOffers)).setTypeface(Fonts.mavenLight(activity));
        textViewOffersValue = (TextView) view.findViewById(R.id.textViewOffersValue);
        textViewOffersValue.setTypeface(Fonts.mavenRegular(activity));
        textViewCashValue = (TextView) view.findViewById(R.id.textViewCashValue);
        textViewCashValue.setTypeface(Fonts.mavenRegular(activity));
        imageViewPaymentOp = (ImageView) view.findViewById(R.id.imageViewPaymentOp);
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

        viewPager = (ViewPager) view.findViewById(R.id.viewPager);

        tabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        tabs.setTextColorResource(R.color.theme_color, R.color.grey_dark);

        slidingBottomFragmentAdapter = new SlidingBottomFragmentAdapter(activity.getSupportFragmentManager());
        viewPager.setAdapter(slidingBottomFragmentAdapter);
        tabs.setViewPager(viewPager);

        imageViewExtraForSliding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewExtraForSliding.setVisibility(View.GONE);
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        update(null);

        recyclerViewVehicles = (RecyclerView) view.findViewById(R.id.recyclerViewVehicles);
        recyclerViewVehicles.setLayoutManager(new LinearLayoutManagerForResizableRecyclerView(activity,
                LinearLayoutManager.HORIZONTAL, false));
        recyclerViewVehicles.setItemAnimator(new DefaultItemAnimator());
        recyclerViewVehicles.setHasFixedSize(false);
        vehiclesTabAdapter = new VehiclesTabAdapter(activity, Data.regions);
        recyclerViewVehicles.setAdapter(vehiclesTabAdapter);

    }

    public void slideOnClick(View view) {
        if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        }
        switch (view.getId()) {
            case R.id.linearLayoutCash:
                if(viewPager.getCurrentItem() == 0){
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                } else {
                    viewPager.setCurrentItem(0, true);
                }
                FlurryEventLogger.event(activity, FlurryEventNames.CLICKS_ON_PAYTM);
                break;

            case R.id.linearLayoutFare:
                if(viewPager.getCurrentItem() == 1){
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                } else {
                    viewPager.setCurrentItem(1, true);
                }
                FlurryEventLogger.event(activity, FlurryEventNames.CLICKS_ON_MIN_FARE);
                break;

            case R.id.linearLayoutOffers:
                if(viewPager.getCurrentItem() == 2){
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                } else {
                    viewPager.setCurrentItem(2, true);
                }
                FlurryEventLogger.event(activity, FlurryEventNames.CLICKS_ON_OFFERS);
                break;
        }
    }

    public void update(ArrayList<PromoCoupon> promoCoupons) {
        try {
            this.promoCoupons = promoCoupons;

            if (promoCoupons != null) {
				if(selectedCoupon == null) {
					if (promoCoupons.size() > 0) {
						selectedCoupon = noSelectionCoupon;
                        try {
                            JSONObject map = new JSONObject();
                            map.put(Constants.KEY_USER_ID, Data.userData.getUserId());
                            JSONArray coups = new JSONArray();
                            for(PromoCoupon pc : promoCoupons){
                                if(pc instanceof CouponInfo){
                                    coups.put(((CouponInfo) pc).title + " " + ((CouponInfo) pc).subtitle);
                                } else if(pc instanceof PromotionInfo){
                                    coups.put(((PromotionInfo) pc).title);
                                }
                            }
                            map.put(Constants.KEY_COUPONS, coups.toString());
                            NudgeClient.trackEvent(activity, FlurryEventNames.NUDGE_COUPON_AVAILABLE, map);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
					} else {
						selectedCoupon = new CouponInfo(0, "");
						textViewOffersValue.setText("");
                        try {
                            JSONObject map = new JSONObject();
                            map.put(Constants.KEY_USER_ID, Data.userData.getUserId());
                            NudgeClient.trackEvent(activity, FlurryEventNames.NUDGE_NO_COUPONS, map);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
					}
				}
				if (promoCoupons.size() > 0) {
					textViewOffersValue.setText("" + promoCoupons.size());
					textViewOffersValue.setVisibility(View.VISIBLE);
				} else{
					textViewOffersValue.setText("");
                }

            } else {
				textViewOffersValue.setText("");
			}

            Fragment frag = activity.getSupportFragmentManager().findFragmentByTag("android:switcher:" + viewPager.getId() + ":" + 2);
            if (frag != null && frag instanceof SlidingBottomOffersFragment) {
				((SlidingBottomOffersFragment) frag).setOfferAdapter(promoCoupons);
				((SlidingBottomOffersFragment) frag).update(promoCoupons);
			}
            updatePaymentOption();

            if(Data.regions.size() > 1){
                for (int i=0; i<Data.regions.size(); i++) {
                    if(Data.regions.get(i).getRegionId().equals(getRegionSelected().getRegionId())
                            && Data.regions.get(i).getVehicleType().equals(getRegionSelected().getVehicleType())){
                        regionSelected = Data.regions.get(i);
                    }
                }
                vehiclesTabAdapter.notifyDataSetChanged();
                recyclerViewVehicles.setVisibility(View.VISIBLE);

            } else if(Data.regions.size() > 0){
                activity.setVehicleTypeSelected(0);
                regionSelected = Data.regions.get(0);
                recyclerViewVehicles.setVisibility(View.GONE);

            } else{
                activity.forceFarAwayCity();
            }

            updateFareStructureUI();

            checkForGoogleLogoVisibilityBeforeRide();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public RecyclerView getRecyclerViewVehicles(){
        return recyclerViewVehicles;
    }

    public void updatePaymentOption() {
        try {
            if(Data.userData.getPaytmError() == 1){
				Data.pickupPaymentOption = PaymentOption.CASH.getOrdinal();
            }
            if (PaymentOption.PAYTM.getOrdinal() == Data.pickupPaymentOption) {
                imageViewPaymentOp.setImageResource(R.drawable.paytm_home_icon);
                textViewCashValue.setText(String.format(activity.getResources().getString(R.string.rupees_value_format_without_space),
                        Data.userData.getPaytmBalanceStr()));
			} else {
				imageViewPaymentOp.setImageResource(R.drawable.cash_home_icon);
				textViewCashValue.setText(activity.getResources().getString(R.string.cash));
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<PromoCoupon> getPromoCoupons() {
        return promoCoupons;
    }

    public SlidingUpPanelLayout getSlidingUpPanelLayout() {
        return slidingUpPanelLayout;
    }

    public PromoCoupon getSelectedCoupon() {
        return selectedCoupon;
    }

    public void setSelectedCoupon(int position) {
        if (position > -1 && position < promoCoupons.size()) {
            selectedCoupon = promoCoupons.get(position);
        } else {
            selectedCoupon = noSelectionCoupon;
        }
        displayAlertAndCheckForSelectedPaytmCoupon(selectedCoupon);
    }

    public void setSelectedCoupon(PromoCoupon promoCoupon){
        selectedCoupon = promoCoupon;
    }

    public void setPaytmLoadingVisiblity(int visiblity) {
        Fragment frag1 = activity.getSupportFragmentManager().findFragmentByTag("android:switcher:" + viewPager.getId() + ":" + 0);
        if (frag1 != null && frag1 instanceof SlidingBottomCashFragment) {
            ((SlidingBottomCashFragment) frag1).setPaytmLoadingVisiblity(visiblity);
        }
    }

    public void updatePreferredPaymentOptionUI() {
        Fragment frag1 = activity.getSupportFragmentManager().findFragmentByTag("android:switcher:" + viewPager.getId() + ":" + 0);
        if (frag1 != null && frag1 instanceof SlidingBottomCashFragment) {
            ((SlidingBottomCashFragment) frag1).updatePreferredPaymentOptionUI();
        }
    }

    public boolean displayAlertAndCheckForSelectedPaytmCoupon() {
        return displayAlertAndCheckForSelectedPaytmCoupon(selectedCoupon);
    }

    private boolean displayAlertAndCheckForSelectedPaytmCoupon(PromoCoupon promoCoupon) {
        try {
            boolean paytmCouponSelected = false;
            if (promoCoupon instanceof CouponInfo) {
                if (((CouponInfo) promoCoupon).title.toLowerCase(Locale.ENGLISH).contains(activity.getResources().getString(R.string.paytm).toLowerCase(Locale.ENGLISH))) {
                    paytmCouponSelected = true;
                }
            } else if (promoCoupon instanceof PromotionInfo) {
                if (((PromotionInfo) promoCoupon).title.toLowerCase(Locale.ENGLISH).contains(activity.getResources().getString(R.string.paytm).toLowerCase(Locale.ENGLISH))) {
                    paytmCouponSelected = true;
                }
            }

            if (paytmCouponSelected) {
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


    public Region getRegionSelected() {
        if(regionSelected == null){
            if(Data.regions.size() > 0){
                regionSelected = Data.regions.get(0);
            } else{
                regionSelected = new Region();
            }
        }
        return regionSelected;
    }
    public void setRegionSelected(int position) {
        if (position > -1 && position < Data.regions.size()) {
            regionSelected = Data.regions.get(position);
        }
        vehiclesTabAdapter.notifyDataSetChanged();
        updateFareStructureUI();
    }


    private void updateFareStructureUI(){
        for (int i = 0; i < Data.regions.size(); i++) {
            if (Data.regions.get(i).getVehicleType().equals(getRegionSelected().getVehicleType())) {
                Data.fareStructure = Data.regions.get(i).getFareStructure();
                break;
            }
        }
        updateFareFrag();
    }

    private void updateFareFrag(){
        try {
            Fragment frag1 = activity.getSupportFragmentManager().findFragmentByTag("android:switcher:" + viewPager.getId() + ":" + 1);
            if (frag1 != null && frag1 instanceof SlidingBottomFareFragment) {
				((SlidingBottomFareFragment) frag1).update();
			}
            textViewMinFareValue.setText(String.format(activity.getResources().getString(R.string.rupees_value_format_without_space)
					, Utils.getMoneyDecimalFormat().format(Data.fareStructure.fixedFare)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkForGoogleLogoVisibilityBeforeRide(){
        try{
            float padding = 20;
            if(recyclerViewVehicles.getVisibility() == View.VISIBLE){
                padding = padding + 110;
            }
            activity.setGoogleMapPadding(padding);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

}

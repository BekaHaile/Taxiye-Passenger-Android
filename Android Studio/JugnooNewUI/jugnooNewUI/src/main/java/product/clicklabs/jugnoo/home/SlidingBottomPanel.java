package product.clicklabs.jugnoo.home;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import product.clicklabs.jugnoo.home.adapters.SlidingBottomFragmentAdapter;
import product.clicklabs.jugnoo.home.adapters.VehiclesTabAdapter;
import product.clicklabs.jugnoo.home.fragments.SlidingBottomCashFragment;
import product.clicklabs.jugnoo.home.fragments.SlidingBottomFareFragment;
import product.clicklabs.jugnoo.home.fragments.SlidingBottomOffersFragment;
import product.clicklabs.jugnoo.home.models.Region;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.LinearLayoutManagerForResizableRecyclerView;
import product.clicklabs.jugnoo.utils.Log;
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
    private ImageView imageViewPaymentOp, imageViewExtraForSliding, imageViewSurgeOverSlidingBottom;
    private TextView textViewMinFareValue, textViewOffersValue, textViewCashValue, textViewPoolInfo1, textViewPoolInfo2;

    private PromoCoupon selectedCoupon = null;
    private PromoCoupon noSelectionCoupon = new CouponInfo(-1, "Don't apply coupon on this ride");
    private Region regionSelected = null;

    private RecyclerView recyclerViewVehicles;
    private VehiclesTabAdapter vehiclesTabAdapter;
    private RelativeLayout relativeLayoutPoolInfoBar;
    private View view;

    public SlidingBottomPanel(HomeActivity activity, View view) {
        this.activity = activity;
        this.view = view;
        initComponents(view);
    }

    private void initComponents(View view) {
        //SlidingUp Layout
        ((TextView) view.findViewById(R.id.textViewMinFare)).setTypeface(Fonts.mavenMedium(activity));
        textViewMinFareValue = (TextView) view.findViewById(R.id.textViewMinFareValue);
        textViewMinFareValue.setTypeface(Fonts.mavenMedium(activity));
        ((TextView) view.findViewById(R.id.textViewOffers)).setTypeface(Fonts.mavenMedium(activity));
        textViewOffersValue = (TextView) view.findViewById(R.id.textViewOffersValue);
        textViewOffersValue.setTypeface(Fonts.mavenMedium(activity));
        textViewCashValue = (TextView) view.findViewById(R.id.textViewCashValue);
        textViewCashValue.setTypeface(Fonts.mavenMedium(activity));
        imageViewPaymentOp = (ImageView) view.findViewById(R.id.imageViewPaymentOp);
        imageViewExtraForSliding = (ImageView)view.findViewById(R.id.imageViewExtraForSliding);
        imageViewSurgeOverSlidingBottom = (ImageView)view.findViewById(R.id.imageViewSurgeOverSlidingBottom);
        relativeLayoutPoolInfoBar = (RelativeLayout)view.findViewById(R.id.relativeLayoutPoolInfoBar);
        textViewPoolInfo1 = (TextView)view.findViewById(R.id.textViewPoolInfo1);
        textViewPoolInfo1.setTypeface(Fonts.mavenMedium(activity));
        textViewPoolInfo2 = (TextView)view.findViewById(R.id.textViewPoolInfo2);
        textViewPoolInfo2.setTypeface(Fonts.avenirNext(activity));

        slidingUpPanelLayout = (SlidingUpPanelLayout) view.findViewById(R.id.slidingLayout);
        slidingUpPanelLayout.setParallaxOffset((int) (260 * ASSL.Yscale()));
        updateSlidingBottomHeight();

        slidingUpPanelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i("slideOffset", ">" + slideOffset);
                imageViewExtraForSliding.setVisibility(View.VISIBLE);
                if(activity.relativeLayoutSearchContainer.getVisibility() == View.GONE
                        && slideOffset < 1f){
                    activity.relativeLayoutSearchContainer.setVisibility(View.VISIBLE);
                }
                setSurgeImageVisibility();
            }

            @Override
            public void onPanelExpanded(View panel) {
                imageViewExtraForSliding.setVisibility(View.VISIBLE);
                activity.relativeLayoutSearchContainer.setVisibility(View.GONE);
                setSurgeImageVisibility();
            }

            @Override
            public void onPanelCollapsed(View panel) {
                imageViewExtraForSliding.setVisibility(View.GONE);
                activity.relativeLayoutSearchContainer.setVisibility(View.VISIBLE);
                setSurgeImageVisibility();
            }

            @Override
            public void onPanelAnchored(View panel) {
                setSurgeImageVisibility();
            }

            @Override
            public void onPanelHidden(View panel) {
                setSurgeImageVisibility();
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

        recyclerViewVehicles = (RecyclerView) view.findViewById(R.id.recyclerViewVehicles);
        recyclerViewVehicles.setLayoutManager(new LinearLayoutManagerForResizableRecyclerView(activity,
                LinearLayoutManager.HORIZONTAL, false));
        recyclerViewVehicles.setItemAnimator(new DefaultItemAnimator());
        recyclerViewVehicles.setHasFixedSize(false);
        vehiclesTabAdapter = new VehiclesTabAdapter(activity, Data.regions);
        recyclerViewVehicles.setAdapter(vehiclesTabAdapter);

        update();

        view.findViewById(R.id.linearLayoutCash).setOnClickListener(slideOnClickListener);
        view.findViewById(R.id.linearLayoutFare).setOnClickListener(slideOnClickListener);
        view.findViewById(R.id.linearLayoutOffers).setOnClickListener(slideOnClickListener);

    }

    private View.OnClickListener slideOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            slideOnClick(v);
        }
    };

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
                NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_PAYMENT_TAB_CLICKED, null);
                break;

            case R.id.linearLayoutFare:
                if(viewPager.getCurrentItem() == 1){
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                } else {
                    viewPager.setCurrentItem(1, true);
                }
                FlurryEventLogger.event(activity, FlurryEventNames.CLICKS_ON_MIN_FARE);
                NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_FARE_TAB_CLICKED, null);
                break;

            case R.id.linearLayoutOffers:
                if(viewPager.getCurrentItem() == 2){
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                } else {
                    viewPager.setCurrentItem(2, true);
                }
                FlurryEventLogger.event(activity, FlurryEventNames.CLICKS_ON_OFFERS);
                NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_OFFERS_TAB_CLICKED, null);
                break;
        }
    }

    public void update() {
        try {
            if (Data.promoCoupons != null) {
                if (Data.promoCoupons.size() > 0) {
                    textViewOffersValue.setText(String.valueOf(Data.promoCoupons.size()));
                    nudgeCouponsEvent();
                } else {
                    textViewOffersValue.setText("-");
                    NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_NO_COUPONS, null);
                }
                initSelectedCoupon();
            } else{
                textViewOffersValue.setText("-");
            }
            updatePreferredPaymentOptionUI();
            updateRegionsUI();
            updateFareStructureUI();
            updateCouponsFrag();
            checkForGoogleLogoVisibilityBeforeRide();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateSlidingBottomHeight(){
        if(Data.userData.getIsPoolEnabled() == 1) {
            //slidingUpPanelLayout.setPanelHeight((int) (182 * ASSL.Yscale()));
            //relativeLayoutPoolInfoBar.setVisibility(View.VISIBLE);
            slidingUpPanelLayout.setPanelHeight((int) (112 * ASSL.Yscale()));
            relativeLayoutPoolInfoBar.setVisibility(View.GONE);
        } else{
            slidingUpPanelLayout.setPanelHeight((int) (112 * ASSL.Yscale()));
            relativeLayoutPoolInfoBar.setVisibility(View.GONE);
        }
    }

    public void setRecyclerViewVehiclesVisiblity(int visiblity){
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageViewExtraForSliding.getLayoutParams();
        if(visiblity == View.GONE){
            recyclerViewVehicles.setVisibility(View.GONE);
            params.setMargins(0, 0, 0, 0);
        } else{
            recyclerViewVehicles.setVisibility(View.GONE);
//            params.setMargins(0, 0, 0, (int)(140f * ASSL.Yscale()));
            params.setMargins(0, 0, 0, 0);
        }
        imageViewExtraForSliding.setLayoutParams(params);
    }

    public void updatePaymentOption() {
        try {
            Data.pickupPaymentOption = (Data.userData.paytmEnabled == 1
                    && Data.userData.getPaytmError() != 1
                    && Data.userData.getPaytmStatus().equalsIgnoreCase(Data.PAYTM_STATUS_ACTIVE)
                    && PaymentOption.PAYTM.getOrdinal() == Data.pickupPaymentOption)
                    ? PaymentOption.PAYTM.getOrdinal() : PaymentOption.CASH.getOrdinal();
            if (PaymentOption.PAYTM.getOrdinal() == Data.pickupPaymentOption) {
                imageViewPaymentOp.setImageResource(R.drawable.ic_paytm_small);
                textViewCashValue.setText(String.format(activity.getResources().getString(R.string.rupees_value_format_without_space),
                        Data.userData.getPaytmBalanceStr()));
            } else {
                imageViewPaymentOp.setImageResource(R.drawable.ic_cash_small);
                textViewCashValue.setText(activity.getResources().getString(R.string.cash));
            }
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
        recyclerViewVehicles.getLayoutManager().scrollToPosition(position);
        updateFareStructureUI();
    }


    private void updateFareStructureUI(){
        for (int i = 0; i < Data.regions.size(); i++) {
            if (Data.regions.get(i).getVehicleType().equals(getRegionSelected().getVehicleType())) {
                Data.fareStructure = Data.regions.get(i).getFareStructure();
                break;
            }
        }
        updateFareFactorUI();
    }

    public void updateFareFactorUI() {
        try {
            Fragment frag1 = activity.getSupportFragmentManager().findFragmentByTag("android:switcher:" + viewPager.getId() + ":" + 1);
            if (frag1 != null && frag1 instanceof SlidingBottomFareFragment) {
                ((SlidingBottomFareFragment) frag1).update();
            }
            textViewMinFareValue.setText(String.format(activity.getResources().getString(R.string.rupees_value_format_without_space)
                    , Utils.getMoneyDecimalFormat().format(Data.fareStructure.fixedFare)));
            setSurgeImageVisibility();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setSurgeImageVisibility(){
        try {
            if(slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED
                && Data.userData.fareFactor > 1.0){
                imageViewSurgeOverSlidingBottom.setVisibility(View.VISIBLE);
			} else{
                imageViewSurgeOverSlidingBottom.setVisibility(View.GONE);
			}
        } catch (Exception e) {
            e.printStackTrace();
            imageViewSurgeOverSlidingBottom.setVisibility(View.GONE);
        }
    }

    private void updateCouponsFrag(){
        try {
            Fragment frag = activity.getSupportFragmentManager().findFragmentByTag("android:switcher:" + viewPager.getId() + ":" + 2);
            if (frag != null && frag instanceof SlidingBottomOffersFragment) {
				((SlidingBottomOffersFragment) frag).setOfferAdapter();
				((SlidingBottomOffersFragment) frag).update();
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkForGoogleLogoVisibilityBeforeRide(){
        try{
            float padding = 0;
            if(recyclerViewVehicles.getVisibility() == View.VISIBLE){
                padding = padding + 110;
            }
            activity.setGoogleMapPadding(padding);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private boolean isPaytmCoupon(PromoCoupon pc){
        if(pc.getTitle().toLowerCase(Locale.ENGLISH)
                .contains(activity.getString(R.string.paytm).toLowerCase(Locale.ENGLISH))) {
            return true;
        }
        return false;
    }









    public void updateRegionsUI(){
        try{
            if(Data.regions.size() > 1){
                boolean matched = false;
                for (int i=0; i<Data.regions.size(); i++) {
                    if(Data.regions.get(i).getRegionId().equals(getRegionSelected().getRegionId())
                            && Data.regions.get(i).getVehicleType().equals(getRegionSelected().getVehicleType())){
                        regionSelected = Data.regions.get(i);
                        matched = true;
                        break;
                    }
                }
                if(!matched){
                    regionSelected = Data.regions.get(0);
                }
                vehiclesTabAdapter.notifyDataSetChanged();
                setRecyclerViewVehiclesVisiblity(View.VISIBLE);

            } else if(Data.regions.size() > 0){
                activity.setVehicleTypeSelected(0);
                regionSelected = Data.regions.get(0);
                setRecyclerViewVehiclesVisiblity(View.GONE);

            } else{
                activity.forceFarAwayCity();
            }
        } catch(Exception e){
            e.printStackTrace();
        }
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

    public void initSelectedCoupon(){
        try {
            if(selectedCoupon == null) {
                if (Data.promoCoupons.size() > 0) {
                    selectedCoupon = noSelectionCoupon;
                } else {
                    selectedCoupon = new CouponInfo(0, "");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}

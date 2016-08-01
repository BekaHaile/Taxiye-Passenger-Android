package product.clicklabs.jugnoo.home;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.home.adapters.SlidingBottomFragmentAdapter;
import product.clicklabs.jugnoo.home.fragments.RequestRideOptionsFragment;
import product.clicklabs.jugnoo.home.models.Region;
import product.clicklabs.jugnoo.home.models.RideTypeValue;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.FirebaseEvents;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.NudgeClient;
import product.clicklabs.jugnoo.widgets.PagerSlidingTabStrip;

/**
 * Created by Ankit on 1/7/16.
 */
public class SlidingBottomPanelV4 {

    private HomeActivity activity;
    private SlidingUpPanelLayout slidingUpPanelLayout;
    private LinearLayout linearLayoutSlidingBottomSingle, linearLayoutSlidingBottom;
    private ImageView imageViewExtraForSliding;
    private RequestRideOptionsFragment requestRideOptionsFragment;
    private ImageView imageViewPriorityTip;
    private int heightWithBar, heightWithourBar;


    private PagerSlidingTabStrip tabs;
    private ViewPager viewPager;
    private SlidingBottomFragmentAdapter slidingBottomFragmentAdapter;
    private ImageView imageViewPaymentOp, imageViewSurgeOverSlidingBottom;
    private TextView textViewMinFareValue, textViewOffersValue, textViewCashValue;
    Bundle bundle = new Bundle();

    private final String TAG = SlidingBottomPanelV4.class.getSimpleName();

    public SlidingBottomPanelV4(HomeActivity activity, View view) {
        this.activity = activity;
        heightWithBar = (int) (195 * ASSL.Yscale());
        heightWithourBar = (int) (125 * ASSL.Yscale());
        initComponents(view);
    }

    private void initComponents(View view) {
        //SlidingUp Layout

        linearLayoutSlidingBottomSingle = (LinearLayout) view.findViewById(R.id.linearLayoutSlidingBottomSingle);
        linearLayoutSlidingBottom = (LinearLayout) view.findViewById(R.id.linearLayoutSlidingBottom);

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

        requestRideOptionsFragment = ((RequestRideOptionsFragment) activity.getSupportFragmentManager().findFragmentById(R.id.frag));
        imageViewExtraForSliding = (ImageView)view.findViewById(R.id.imageViewExtraForSliding);
        imageViewPriorityTip = (ImageView) view.findViewById(R.id.imageViewPriorityTip);

        slidingUpPanelLayout = (SlidingUpPanelLayout) view.findViewById(R.id.slidingLayout);
        slidingUpPanelLayout.setParallaxOffset((int) (295 * ASSL.Yscale()));
        updatePannelHeight();

        slidingUpPanelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                //Log.v("slideOffset", "---> "+slideOffset);
                if(slideOffset > 0.2f){
                    activity.getViewPoolInfoBarAnim().setVisibility(View.VISIBLE);
                }
                imageViewExtraForSliding.setVisibility(View.VISIBLE);
                if (activity.relativeLayoutSearchContainer.getVisibility() == View.GONE
                        && slideOffset < 1f) {
                    activity.relativeLayoutSearchContainer.setVisibility(View.VISIBLE);
                }
                requestRideOptionsFragment.setSurgeImageVisibility();
            }

            @Override
            public void onPanelExpanded(View panel) {
                imageViewExtraForSliding.setVisibility(View.VISIBLE);
                activity.relativeLayoutSearchContainer.setVisibility(View.GONE);
                requestRideOptionsFragment.setSurgeImageVisibility();
                activity.getViewPoolInfoBarAnim().setVisibility(View.VISIBLE);
            }

            @Override
            public void onPanelCollapsed(View panel) {
                imageViewExtraForSliding.setVisibility(View.GONE);
                activity.relativeLayoutSearchContainer.setVisibility(View.VISIBLE);
                requestRideOptionsFragment.setSurgeImageVisibility();
                activity.showPoolInforBar();
            }

            @Override
            public void onPanelAnchored(View panel) {
                requestRideOptionsFragment.setSurgeImageVisibility();
            }

            @Override
            public void onPanelHidden(View panel) {
                requestRideOptionsFragment.setSurgeImageVisibility();
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

        view.findViewById(R.id.linearLayoutCash).setOnClickListener(slideOnClickListener);
        view.findViewById(R.id.linearLayoutFare).setOnClickListener(slideOnClickListener);
        view.findViewById(R.id.linearLayoutOffers).setOnClickListener(slideOnClickListener);

        try {
            updateBottomPanel(Data.regions.size());
        } catch (Exception e) {
            e.printStackTrace();
        }

        update();

    }

    public void update() {
        try {
            updatePannelHeight();
            if (Data.promoCoupons != null) {
                if (Data.promoCoupons.size() > 0) {
//                    nudgeCouponsEvent();
                    textViewOffersValue.setText(String.valueOf(Data.promoCoupons.size()));
                } else {
                    NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_NO_COUPONS, null);
                    textViewOffersValue.setText("-");
                }
                requestRideOptionsFragment.initSelectedCoupon();
            } else{
                textViewOffersValue.setText("-");
            }
            requestRideOptionsFragment.updateRegionsUI();
            requestRideOptionsFragment.updateFareStructureUI();
            requestRideOptionsFragment.getPromoCouponsDialog().notifyCoupons();
            requestRideOptionsFragment.updateOffersCount();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public SlidingUpPanelLayout getSlidingUpPanelLayout() {
        return slidingUpPanelLayout;
    }

    public void updatePannelHeight(){
        try {
            for(Region region : Data.regions){
                if(region.getRideType() == RideTypeValue.POOL.getOrdinal() &&
                        (!region.getOfferTexts().getText1().equalsIgnoreCase(""))){
                    //slidingUpPanelLayout.setPanelHeight(heightWithBar);
                    slidingUpPanelLayout.setPanelHeight(heightWithourBar);
                    try {
                        //getRequestRideOptionsFragment().getRelativeLayoutPoolInfoBar().setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return;
                }
            }
            slidingUpPanelLayout.setPanelHeight(heightWithourBar);
            //getRequestRideOptionsFragment().getRelativeLayoutPoolInfoBar().setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public RequestRideOptionsFragment getRequestRideOptionsFragment(){
        return requestRideOptionsFragment;
    }

    public void updateFareFactorUI(int supplyCount){
        if(supplyCount == 1) {
            if (Data.userData.fareFactor > 1 || Data.userData.fareFactor < 1) {
                imageViewPriorityTip.setVisibility(View.VISIBLE);
            } else {
                imageViewPriorityTip.setVisibility(View.GONE);
            }
        } else{
            imageViewPriorityTip.setVisibility(View.GONE);
        }
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
                MyApplication.getInstance().logEvent(FirebaseEvents.TRANSACTION+"_"+ FirebaseEvents.HOME_SCREEN+"_"
                        +FirebaseEvents.B_PAYMENT_MODE, bundle);
                NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_PAYMENT_TAB_CLICKED, null);
                break;

            case R.id.linearLayoutFare:
                if(viewPager.getCurrentItem() == 1){
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                } else {
                    viewPager.setCurrentItem(1, true);
                }
                FlurryEventLogger.event(activity, FlurryEventNames.CLICKS_ON_MIN_FARE);
                MyApplication.getInstance().logEvent(FirebaseEvents.TRANSACTION+"_"+ FirebaseEvents.HOME_SCREEN+"_"
                        +FirebaseEvents.FARE_POPUP, bundle);
                NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_FARE_TAB_CLICKED, null);
                break;

            case R.id.linearLayoutOffers:
                if(viewPager.getCurrentItem() == 2){
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                } else {
                    viewPager.setCurrentItem(2, true);
                }
                FlurryEventLogger.event(activity, FlurryEventNames.CLICKS_ON_OFFERS);
                MyApplication.getInstance().logEvent(FirebaseEvents.TRANSACTION+"_"+ FirebaseEvents.HOME_SCREEN+"_"
                        +FirebaseEvents.B_OFFER, bundle);
                NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_OFFERS_TAB_CLICKED, null);
                break;
        }
    }

    public ViewPager getViewPager(){
        return viewPager;
    }

    public ImageView getImageViewPaymentOp(){
        return imageViewPaymentOp;
    }

    public TextView getTextViewCashValue(){
        return textViewCashValue;
    }

    public ImageView getImageViewSurgeOverSlidingBottom(){
        return imageViewSurgeOverSlidingBottom;
    }

    public TextView getTextViewMinFareValue(){
        return textViewMinFareValue;
    }

    public void updateBottomPanel(int suppleCount){
        if(suppleCount > 1) {
            linearLayoutSlidingBottomSingle.setVisibility(View.GONE);
            linearLayoutSlidingBottom.setVisibility(View.VISIBLE);
        } else{
            linearLayoutSlidingBottomSingle.setVisibility(View.VISIBLE);
            linearLayoutSlidingBottom.setVisibility(View.GONE);
            activity.getViewPoolInfoBarAnim().setVisibility(View.VISIBLE);
        }
    }

    public ImageView getImageViewExtraForSliding(){
        return imageViewExtraForSliding;
    }

}

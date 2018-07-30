package product.clicklabs.jugnoo.home;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.MenuInfoTags;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.datastructure.ProductType;
import product.clicklabs.jugnoo.home.adapters.SlidingBottomFragmentAdapter;
import product.clicklabs.jugnoo.home.fragments.RequestRideOptionsFragment;
import product.clicklabs.jugnoo.home.fragments.SlidingBottomCashFragment;
import product.clicklabs.jugnoo.home.models.Region;
import product.clicklabs.jugnoo.home.models.RideTypeValue;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.widgets.PagerSlidingTabStrip;

/**
 * Created by Ankit on 1/7/16.
 */
public class SlidingBottomPanelV4 implements GAAction, GACategory{

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
                try {
                   /* if(slideOffset > 0.2f){
						activity.getViewPoolInfoBarAnim().setVisibility(View.VISIBLE);
						activity.setFabMarginInitial(true);
						activity.getFabViewTest().hideJeanieHelpInSession();
					}*/
                    imageViewExtraForSliding.setVisibility(View.VISIBLE);
                    if (activity.relativeLayoutSearchContainer.getVisibility() == View.GONE
							&& slideOffset < 1f) {
						activity.relativeLayoutSearchContainer.setVisibility(View.VISIBLE);
					}
                    requestRideOptionsFragment.setSurgeImageVisibility();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPanelExpanded(View panel) {
                try {
                    imageViewExtraForSliding.setVisibility(View.VISIBLE);
                    activity.relativeLayoutSearchContainer.setVisibility(View.GONE);
                    requestRideOptionsFragment.setSurgeImageVisibility();
                    activity.getViewPoolInfoBarAnim().setVisibility(View.VISIBLE);
                    activity.setFabMarginInitial(true, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPanelCollapsed(View panel) {
                try {
                    imageViewExtraForSliding.setVisibility(View.GONE);
                    activity.relativeLayoutSearchContainer.setVisibility(View.VISIBLE);
                    requestRideOptionsFragment.setSurgeImageVisibility();
                    activity.showPoolInforBar(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
        slidingBottomFragmentAdapter = new SlidingBottomFragmentAdapter(activity, activity.getSupportFragmentManager());
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
        if(Data.isMenuTagEnabled(MenuInfoTags.OFFERS)) {
            view.findViewById(R.id.linearLayoutOffers).setVisibility(View.VISIBLE);
            view.findViewById(R.id.ivOffersSingleDiv).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.linearLayoutOffers).setVisibility(View.GONE);
            view.findViewById(R.id.ivOffersSingleDiv).setVisibility(View.GONE);
        }

        try {
            updateBottomPanel(Data.autoData.getRegions().size());
        } catch (Exception e) {
        }

        update();

    }

    public void update() {
        try {
            updatePannelHeight();
            if (Data.userData.getCoupons(ProductType.AUTO, activity) != null) {
                if (Data.userData.getCoupons(ProductType.AUTO, activity).size() > 0) {
                    textViewOffersValue.setText(String.valueOf(Data.userData.getCoupons(ProductType.AUTO, activity).size()));
                } else {
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
            for(Region region : Data.autoData.getRegions()){
                if(region.getRideType() == RideTypeValue.POOL.getOrdinal() &&
                        (region.getOfferTexts() != null && !region.getOfferTexts().getText1().equalsIgnoreCase(""))){
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
            if (Data.autoData.getFareFactor() > 1 || Data.autoData.getFareFactor() < 1) {
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
                GAUtils.event(RIDES, HOME, WALLET+CLICKED);
                break;

            case R.id.linearLayoutFare:
                if(viewPager.getCurrentItem() == 1){
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                } else {
                    viewPager.setCurrentItem(1, true);
                }
                GAUtils.event(RIDES, HOME, FARE_DETAILS+CLICKED);
                break;

            case R.id.linearLayoutOffers:
                if(viewPager.getCurrentItem() == 2){
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                } else {
                    viewPager.setCurrentItem(2, true);
                }
                GAUtils.event(RIDES, HOME, OFFER+CLICKED);
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
            activity.setFabMarginInitial(true, false);
        }
    }

    public ImageView getImageViewExtraForSliding(){
        return imageViewExtraForSliding;
    }

    public void updatePaymentOptions(){
        try {
            if(Data.autoData != null
					&& Data.autoData.getPickupPaymentOption() == PaymentOption.RAZOR_PAY.getOrdinal()
					&& !Data.autoData.isRazorpayEnabled()){
				Data.autoData.setPickupPaymentOption(PaymentOption.CASH.getOrdinal());
			}
            if(requestRideOptionsFragment != null) {
				requestRideOptionsFragment.updatePaymentOption();
			}

            if(viewPager != null) {
				Fragment page = activity.getSupportFragmentManager().findFragmentByTag("android:switcher:" + viewPager.getId() + ":" + 0);
				if (page != null) {
					((SlidingBottomCashFragment) page).onResume();
				}
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

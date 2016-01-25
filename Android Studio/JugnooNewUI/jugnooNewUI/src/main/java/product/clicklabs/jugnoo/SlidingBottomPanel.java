package product.clicklabs.jugnoo;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Locale;

import product.clicklabs.jugnoo.adapters.SlidingBottomFragmentAdapter;
import product.clicklabs.jugnoo.datastructure.AddPaymentPath;
import product.clicklabs.jugnoo.datastructure.CouponInfo;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.datastructure.PromotionInfo;
import product.clicklabs.jugnoo.fragments.SlidingBottomCashFragment;
import product.clicklabs.jugnoo.fragments.SlidingBottomFareFragment;
import product.clicklabs.jugnoo.fragments.SlidingBottomOffersFragment;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
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
    private String TAG = "slidingPanel";
    private ViewPager viewPager;
    private SlidingBottomFragmentAdapter slidingBottomFragmentAdapter;
    private ImageView imageViewPaymentOp, imageViewExtraForSliding;
    private TextView textViewMinFareValue, textViewOffersValue, textViewCashValue;
    private LinearLayout linearLayoutSlidingBottom;
    private PromoCoupon selectedCoupon = null;
    private PromoCoupon noSelectionCoupon = new CouponInfo(-1, "Don't apply coupon on this ride");
    private ArrayList<PromoCoupon> promoCoupons;


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
        linearLayoutSlidingBottom = (LinearLayout) view.findViewById(R.id.linearLayoutSlidingBottom);
        imageViewExtraForSliding = (ImageView)view.findViewById(R.id.imageViewExtraForSliding);


        slidingUpPanelLayout = (SlidingUpPanelLayout) view.findViewById(R.id.slidingLayout);
        slidingUpPanelLayout.setParallaxOffset((int) (260 * ASSL.Yscale()));
        slidingUpPanelLayout.setPanelHeight((int) (110 * ASSL.Yscale()));

        slidingUpPanelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(TAG, "onPanelSlide, offset " + slideOffset);
                imageViewExtraForSliding.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPanelExpanded(View panel) {
                Log.i(TAG, "onPanelExpanded");
                imageViewExtraForSliding.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPanelCollapsed(View panel) {
                Log.i(TAG, "onPanelCollapsed");
                imageViewExtraForSliding.setVisibility(View.GONE);
            }

            @Override
            public void onPanelAnchored(View panel) {
                Log.i(TAG, "onPanelAnchored");
            }

            @Override
            public void onPanelHidden(View panel) {
                Log.i(TAG, "onPanelHidden");
            }
        });

        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        slidingBottomFragmentAdapter = new SlidingBottomFragmentAdapter(activity.getSupportFragmentManager());
        viewPager.setAdapter(slidingBottomFragmentAdapter);

        tabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        tabs.setTextColorResource(R.color.theme_color, R.color.grey_dark);
        tabs.setViewPager(viewPager);

        imageViewExtraForSliding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewExtraForSliding.setVisibility(View.GONE);
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        update(null);
    }

    public void slideOnClick(View view) {
        if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        }
        switch (view.getId()) {
            case R.id.linearLayoutCash:
                Log.v("on click", "linearLayoutCash");
                viewPager.setCurrentItem(0, true);
                break;

            case R.id.linearLayoutFare:
                Log.v("on click", "linearLayoutFare");
                viewPager.setCurrentItem(1, true);
                break;

            case R.id.linearLayoutOffers:
                Log.v("on click", "linearLayoutOffers");
                viewPager.setCurrentItem(2, true);
                break;
        }
    }

    public void update(ArrayList<PromoCoupon> promoCoupons) {
        this.promoCoupons = promoCoupons;

        textViewMinFareValue.setText(String.format(activity.getResources().getString(R.string.ruppes_value_format_without_space)
                , Utils.getMoneyDecimalFormat().format(Data.fareStructure.fixedFare)));

        if (promoCoupons != null) {
            if(selectedCoupon == null) {
                if (promoCoupons.size() > 0) {
                    selectedCoupon = noSelectionCoupon;
                } else {
                    selectedCoupon = new CouponInfo(0, "");
                }
            }
            textViewOffersValue.setText("" + promoCoupons.size());
        } else {
            textViewOffersValue.setText("0");
        }


        Fragment frag1 = activity.getSupportFragmentManager().findFragmentByTag("android:switcher:" + viewPager.getId() + ":" + 1);
        if (frag1 != null && frag1 instanceof SlidingBottomFareFragment) {
            ((SlidingBottomFareFragment) frag1).update();
        }

        Fragment frag = activity.getSupportFragmentManager().findFragmentByTag("android:switcher:" + viewPager.getId() + ":" + 2);
        if (frag != null && frag instanceof SlidingBottomOffersFragment) {
            ((SlidingBottomOffersFragment) frag).setOfferAdapter(promoCoupons);
            ((SlidingBottomOffersFragment) frag).update(promoCoupons);
        }
        updatePaymentOption();

    }

    public void updatePaymentOption() {
        if (PaymentOption.PAYTM.getOrdinal() == Data.pickupPaymentOption) {
            imageViewPaymentOp.setImageResource(R.drawable.paytm_home_icon);
            textViewCashValue.setText(String.format(activity.getResources().getString(R.string.ruppes_value_format_without_space)
                    , Data.userData.getPaytmBalanceStr()));
        } else {
            imageViewPaymentOp.setImageResource(R.drawable.cash_home_icon);
            textViewCashValue.setText(activity.getResources().getString(R.string.cash));
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

    public void setLinearLayoutSlidingBottom(int visibility) {
        linearLayoutSlidingBottom.setVisibility(visibility);
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
            intent.putExtra(Constants.KEY_ADD_PAYMENT_PATH, AddPaymentPath.WALLET.getOrdinal());
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
            FlurryEventLogger.event(FlurryEventNames.WALLET_BEFORE_REQUEST_RIDE);
        }
    }

}

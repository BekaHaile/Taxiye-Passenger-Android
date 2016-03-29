package product.clicklabs.jugnoo.home;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

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
import product.clicklabs.jugnoo.home.fragments.BottomVehiclesFragment;
import product.clicklabs.jugnoo.home.models.Vehicle;
import product.clicklabs.jugnoo.home.models.VehicleType;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
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

    private VehicleType vehicleTypeSelected = null,
                        vehicleTypeDefault = new VehicleType(Vehicle.AUTO.getId(), Vehicle.AUTO.getName());

    private RelativeLayout relativeLayoutVehicleType;
    private TextView textViewVehicleType;
    private ImageView imageViewVehicleType;


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

        relativeLayoutVehicleType = (RelativeLayout) view.findViewById(R.id.relativeLayoutVehicleType);
        textViewVehicleType = (TextView) view.findViewById(R.id.textViewVehicleType);
        textViewVehicleType.setTypeface(Fonts.mavenLight(activity));
        imageViewVehicleType = (ImageView) view.findViewById(R.id.imageViewVehicleType);

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

        slidingBottomFragmentAdapter = new SlidingBottomFragmentAdapter(activity.getSupportFragmentManager(), false);
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

        vehicleTypeSelected = vehicleTypeDefault;
        relativeLayoutVehicleType.setVisibility(View.GONE);
    }

    public void slideOnClick(View view) {
        if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        }
        int increment = Data.vehicleTypes.size() < 2 ? 0 : 1;
        switch (view.getId()) {
            case R.id.relativeLayoutVehicleType:
                if(viewPager.getCurrentItem() == 0){
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                } else {
                    viewPager.setCurrentItem(0, true);
                }
                FlurryEventLogger.event(activity, FlurryEventNames.CLICKS_ON_PAYTM);
                break;

            case R.id.linearLayoutCash:
                if(viewPager.getCurrentItem() == 0+increment){
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                } else {
                    viewPager.setCurrentItem(0+increment, true);
                }
                FlurryEventLogger.event(activity, FlurryEventNames.CLICKS_ON_PAYTM);
                break;

            case R.id.linearLayoutFare:
                if(viewPager.getCurrentItem() == 1+increment){
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                } else {
                    viewPager.setCurrentItem(1+increment, true);
                }
                FlurryEventLogger.event(activity, FlurryEventNames.CLICKS_ON_MIN_FARE);
                break;

            case R.id.linearLayoutOffers:
                if(viewPager.getCurrentItem() == 2+increment){
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                } else {
                    viewPager.setCurrentItem(2+increment, true);
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
					} else {
						selectedCoupon = new CouponInfo(0, "");
						textViewOffersValue.setText("");
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

            updateFareFrag();

            int increment = Data.vehicleTypes == null ? 0 : 1;
            Fragment frag = activity.getSupportFragmentManager().findFragmentByTag("android:switcher:" + viewPager.getId() + ":" + (2+increment));
            if (frag != null && frag instanceof SlidingBottomOffersFragment) {
				((SlidingBottomOffersFragment) frag).setOfferAdapter(promoCoupons);
				((SlidingBottomOffersFragment) frag).update(promoCoupons);
			}
            updatePaymentOption();

            if(Data.vehicleTypes.size() < 2){
                vehicleTypeSelected = vehicleTypeDefault;
                relativeLayoutVehicleType.setVisibility(View.GONE);
                slidingBottomFragmentAdapter.setShowVehicles(false);
                tabs.setViewPager(viewPager);
            } else{
                relativeLayoutVehicleType.setVisibility(View.VISIBLE);
                updateVehicleTypeSelectedInTab();
                slidingBottomFragmentAdapter.setShowVehicles(true);
                tabs.setViewPager(viewPager);
                updateFragmentVehicleType();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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

    public void updateVehicleTypeSelectedInTab() {
        try {
            if(vehicleTypeSelected != null) {
                textViewVehicleType.setText(vehicleTypeSelected.getName());
                if(vehicleTypeSelected.getId() == Vehicle.AUTO.getId()){
                    imageViewVehicleType.setImageResource(R.drawable.ic_auto_orange);
                } else{
                    imageViewVehicleType.setImageResource(R.drawable.ic_bike_orange);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateFragmentVehicleType(){
        Fragment fragV = activity.getSupportFragmentManager()
                .findFragmentByTag("android:switcher:" + viewPager.getId() + ":" + (0));
        if (fragV != null && fragV instanceof BottomVehiclesFragment) {
            ((BottomVehiclesFragment) fragV).update(Data.vehicleTypes);
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
        int increment = Data.vehicleTypes.size() < 2 ? 0 : 1;
        Fragment frag1 = activity.getSupportFragmentManager().findFragmentByTag("android:switcher:" + viewPager.getId() + ":" + (0+increment));
        if (frag1 != null && frag1 instanceof SlidingBottomCashFragment) {
            ((SlidingBottomCashFragment) frag1).setPaytmLoadingVisiblity(visiblity);
        }
    }

    public void updatePreferredPaymentOptionUI() {
        int increment = Data.vehicleTypes.size() < 2 ? 0 : 1;
        Fragment frag1 = activity.getSupportFragmentManager().findFragmentByTag("android:switcher:" + viewPager.getId() + ":" + (0+increment));
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


    public VehicleType getVehicleTypeSelected() {
        if(vehicleTypeSelected == null){
            vehicleTypeSelected = vehicleTypeDefault;
        }
        return vehicleTypeSelected;
    }
    public void setVehicleTypeSelected(int position) {
        if (position > -1 && position < Data.vehicleTypes.size()) {
            vehicleTypeSelected = Data.vehicleTypes.get(position);
        } else {
            vehicleTypeSelected = vehicleTypeDefault;
        }
        updateVehicleTypeSelectedInTab();
        updateFareStructureUI();
    }

    private void updateFareStructureUI(){
        for (int i = 0; i < Data.vehicleTypes.size(); i++) {
            if (Data.vehicleTypes.get(i).getId().equals(vehicleTypeSelected.getId())) {
                Data.fareStructure = Data.vehicleTypes.get(i).getFareStructure();
                break;
            }
        }
        updateFareFrag();

    }

    private void updateFareFrag(){
        try {
            int increment = Data.vehicleTypes == null ? 0 : 1;
            Fragment frag1 = activity.getSupportFragmentManager().findFragmentByTag("android:switcher:" + viewPager.getId() + ":" + (1+increment));
            if (frag1 != null && frag1 instanceof SlidingBottomFareFragment) {
				((SlidingBottomFareFragment) frag1).update();
			}
            textViewMinFareValue.setText(String.format(activity.getResources().getString(R.string.rupees_value_format_without_space)
					, Utils.getMoneyDecimalFormat().format(Data.fareStructure.fixedFare)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

package product.clicklabs.jugnoo.home.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.FareEstimateActivity;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.CouponInfo;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.adapters.VehiclesTabAdapter;
import product.clicklabs.jugnoo.home.dialogs.FareDetailsDialog;
import product.clicklabs.jugnoo.home.dialogs.PaymentOptionDialog;
import product.clicklabs.jugnoo.home.dialogs.PoolDestinationDialog;
import product.clicklabs.jugnoo.home.dialogs.PromoCouponsDialog;
import product.clicklabs.jugnoo.home.models.Region;
import product.clicklabs.jugnoo.home.models.RideTypeValue;
import product.clicklabs.jugnoo.promotion.ShareActivity;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.LinearLayoutManagerForResizableRecyclerView;
import product.clicklabs.jugnoo.utils.NudgeClient;
import product.clicklabs.jugnoo.utils.Utils;

/**
 * Created by Shankar on 1/8/16.
 */
@SuppressLint("ValidFragment")
public class RequestRideOptionsFragment extends Fragment implements Constants{

    private View rootView;
    private HomeActivity activity;
    private LinearLayout linearLayoutSlidingBottom;

    private LinearLayout linearLayoutOptionsSingleSupply, linearLayoutPaymentMode;
    private ImageView imageViewPaymentMode, imageViewPaymentModeMS;
    private TextView textViewPaymentModeValue;


    private LinearLayout linearLayoutFare;
    private TextView textViewMinFareValue;

    private LinearLayout linearLayoutFareEstimate, linearLayoutPaymentModeMS;

    private RelativeLayout relativeLayoutMultipleSupplyMain;
    private RecyclerView recyclerViewVehicles;
    private LinearLayout linearLayoutMinFareMS;
    private TextView textViewPaymentModeValueMS, textViewMinFareMSValue, textVieGetFareEstimateMS, textViewPriorityTipValueMS,
            textViewMaxPeople, textViewOffers, textViewOffersMode, textViewMinFareMS,
            textViewPriorityTipValue;
    private RelativeLayout relativeLayoutPriorityTipMS;

    private VehiclesTabAdapter vehiclesTabAdapter;

    private Region regionSelected = null;
    private PromoCoupon selectedCoupon = null;
    private PromoCoupon noSelectionCoupon = new CouponInfo(-1, "Don't apply coupon on this ride");


    public RequestRideOptionsFragment(){}

    /*public RelativeLayout getRelativeLayoutPoolInfoBar() {
        return relativeLayoutPoolInfoBar;
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_request_ride_options, container, false);
        activity = (HomeActivity) getActivity();
        linearLayoutSlidingBottom = (LinearLayout) rootView.findViewById(R.id.linearLayoutSlidingBottom);
        try {
            if(linearLayoutSlidingBottom != null) {
                new ASSL(getActivity(), linearLayoutSlidingBottom, 1134, 720, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        linearLayoutOptionsSingleSupply = (LinearLayout) rootView.findViewById(R.id.linearLayoutOptionsSingleSupply);
        linearLayoutPaymentMode = (LinearLayout) rootView.findViewById(R.id.linearLayoutPaymentMode);
        imageViewPaymentMode = (ImageView) rootView.findViewById(R.id.imageViewPaymentMode);
        textViewPaymentModeValue = (TextView) rootView.findViewById(R.id.textViewPaymentModeValue);
        textViewPaymentModeValue.setTypeface(Fonts.mavenMedium(activity));

        linearLayoutFare = (LinearLayout) rootView.findViewById(R.id.linearLayoutFare);
        ((TextView) rootView.findViewById(R.id.textViewMinFare)).setTypeface(Fonts.mavenMedium(activity));
        textViewMinFareValue = (TextView) rootView.findViewById(R.id.textViewMinFareValue);
        textViewMinFareValue.setTypeface(Fonts.mavenMedium(activity));

        linearLayoutFareEstimate = (LinearLayout) rootView.findViewById(R.id.linearLayoutFareEstimate);
        ((TextView) rootView.findViewById(R.id.textViewFareEstimate)).setTypeface(Fonts.mavenMedium(activity));

        relativeLayoutMultipleSupplyMain = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutMultipleSupplyMain);
        linearLayoutPaymentModeMS = (LinearLayout) rootView.findViewById(R.id.linearLayoutPaymentModeMS);
        imageViewPaymentModeMS = (ImageView) rootView.findViewById(R.id.imageViewPaymentModeMS);
        textViewPaymentModeValueMS = (TextView) rootView.findViewById(R.id.textViewPaymentModeValueMS);
        textViewPaymentModeValueMS.setTypeface(Fonts.avenirNext(activity), Typeface.BOLD);
        textViewOffersMode = (TextView) rootView.findViewById(R.id.textViewOffersMode);
        textViewOffersMode.setTypeface(Fonts.mavenMedium(activity));

        linearLayoutMinFareMS = (LinearLayout) rootView.findViewById(R.id.linearLayoutMinFareMS);
        textViewMinFareMS = (TextView) rootView.findViewById(R.id.textViewMinFareMS);
        textViewMinFareMS.setTypeface(Fonts.avenirNext(activity), Typeface.BOLD);
        textViewMinFareMSValue = (TextView) rootView.findViewById(R.id.textViewMinFareMSValue);
        textViewMinFareMSValue.setTypeface(Fonts.avenirNext(activity), Typeface.BOLD);
        textViewMaxPeople = (TextView) rootView.findViewById(R.id.textViewMaxPeople);
        textViewMaxPeople.setTypeface(Fonts.avenirNext(activity), Typeface.BOLD);
        textViewOffers = (TextView) rootView.findViewById(R.id.textViewOffers);
        textViewOffers.setTypeface(Fonts.avenirNext(activity), Typeface.BOLD);

        textVieGetFareEstimateMS = (TextView) rootView.findViewById(R.id.textVieGetFareEstimateMS);
        textVieGetFareEstimateMS.setTypeface(Fonts.avenirNext(activity), Typeface.BOLD);

        textViewPriorityTipValueMS = (TextView) rootView.findViewById(R.id.textViewPriorityTipValueMS);
        textViewPriorityTipValueMS.setTypeface(Fonts.mavenRegular(activity));
        textViewPriorityTipValue = (TextView) rootView.findViewById(R.id.textViewPriorityTipValue);
        textViewPriorityTipValue.setTypeface(Fonts.mavenMedium(activity));
        relativeLayoutPriorityTipMS = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutPriorityTip);

        recyclerViewVehicles = (RecyclerView) rootView.findViewById(R.id.recyclerViewVehicles);
        recyclerViewVehicles.setLayoutManager(new LinearLayoutManagerForResizableRecyclerView(activity,
                LinearLayoutManager.HORIZONTAL, false));
        recyclerViewVehicles.setItemAnimator(new DefaultItemAnimator());
        recyclerViewVehicles.setHasFixedSize(false);
        vehiclesTabAdapter = new VehiclesTabAdapter(activity, Data.regions);
        recyclerViewVehicles.setAdapter(vehiclesTabAdapter);


        linearLayoutPaymentMode.setOnClickListener(onClickListenerRequestOptions);
        linearLayoutFare.setOnClickListener(onClickListenerRequestOptions);
        linearLayoutFareEstimate.setOnClickListener(onClickListenerRequestOptions);

        linearLayoutPaymentModeMS.setOnClickListener(onClickListenerRequestOptions);
        linearLayoutMinFareMS.setOnClickListener(onClickListenerRequestOptions);
        textVieGetFareEstimateMS.setOnClickListener(onClickListenerRequestOptions);
        textViewOffers.setOnClickListener(onClickListenerRequestOptions);
        textViewOffersMode.setOnClickListener(onClickListenerRequestOptions);

        relativeLayoutMultipleSupplyMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        updateOffersCount();
        updatePaymentOption();

        try {
            textViewMaxPeople.setText(getResources().getString(R.string.max_people) + " " + getRegionSelected().getMaxPeople());
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(Data.regions.size() > 1) {
                        setRegionSelected(0);
                        activity.setRegionUI(true);
                    }
                }
            }, 500);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rootView;
    }


    View.OnClickListener onClickListenerRequestOptions = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.linearLayoutPaymentMode || v.getId() == R.id.linearLayoutPaymentModeMS){
                getPaymentOptionDialog().show();
                FlurryEventLogger.event(activity, FlurryEventNames.CLICKS_ON_PAYTM);
                NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_PAYMENT_TAB_CLICKED, null);
                FlurryEventLogger.eventGA(REVENUE + SLASH + ACTIVATION + SLASH + RETENTION, "Home Screen", "b_payment_mode");
            } else if(v.getId() == R.id.linearLayoutFare || v.getId() == R.id.linearLayoutMinFareMS){
                if(getRegionSelected().getRideType() == RideTypeValue.POOL.getOrdinal()){
                    //getPoolDestinationDialog().show();
                    getFareDetailsDialog().show();
                    FlurryEventLogger.eventGA(REVENUE + SLASH + ACTIVATION + SLASH + RETENTION, "Pool", "base fare");
                } else{
                    getFareDetailsDialog().show();
                    FlurryEventLogger.eventGA(REVENUE + SLASH + ACTIVATION + SLASH + RETENTION, "Auto", "base fare");
                }
                FlurryEventLogger.event(activity, FlurryEventNames.CLICKS_ON_MIN_FARE);
                NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_FARE_TAB_CLICKED, null);


            } else if(v.getId() == R.id.linearLayoutFareEstimate || v.getId() == R.id.textVieGetFareEstimateMS){
                Intent intent = new Intent(activity, FareEstimateActivity.class);
                intent.putExtra(Constants.KEY_RIDE_TYPE, getRegionSelected().getRideType());
                try {
                    intent.putExtra(Constants.KEY_LATITUDE, activity.map.getCameraPosition().target.latitude);
                    intent.putExtra(Constants.KEY_LONGITUDE, activity.map.getCameraPosition().target.longitude);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //activity.startActivity(intent);
                activity.startActivityForResult(intent, 4);
                activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
                FlurryEventLogger.event(FlurryEventNames.FARE_ESTIMATE);
                FlurryEventLogger.event(activity, FlurryEventNames.CLICKS_ON_GET_FARE_ESTIMATE);
                NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_FARE_ESTIMATE_CLICKED, null);
                FlurryEventLogger.eventGA(REVENUE + SLASH + ACTIVATION + SLASH + RETENTION, getRegionSelected().getRegionName(), "get fare estimate");
            } else if(v.getId() == R.id.textViewOffers || v.getId() == R.id.textViewOffersMode){
                getPromoCouponsDialog().show();
                FlurryEventLogger.event(activity, FlurryEventNames.CLICKS_ON_OFFERS);
                NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_OFFERS_TAB_CLICKED, null);
            }
        }
    };

    public void updateOffersCount(){
        try {
            if(Data.promoCoupons.size() > 0) {
                textViewOffers.setText(activity.getResources().getString(R.string.nl_offers) + "\n" + Data.promoCoupons.size());
                textViewOffersMode.setText(activity.getResources().getString(R.string.nl_offers) + "\n" + Data.promoCoupons.size());
            } else{
                textViewOffers.setText(activity.getResources().getString(R.string.nl_offers) + "\n" + "-");
                textViewOffersMode.setText(activity.getResources().getString(R.string.nl_offers) + "\n" + "-");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateCouponsFragSingle();
    }

    public PromoCouponsDialog promoCouponsDialog;
    public PromoCouponsDialog getPromoCouponsDialog(){
        if(promoCouponsDialog == null){
            promoCouponsDialog = new PromoCouponsDialog(activity, new PromoCouponsDialog.Callback() {
                @Override
                public void onCouponApplied() {
                    //onRequestRideTap();
                    activity.updateConfirmedStateCoupon();
                }

                @Override
                public void onSkipped() {
                    //onRequestRideTap();
                }

                @Override
                public void onInviteFriends() {
                    Intent intent = new Intent(activity, ShareActivity.class);
                    startActivity(intent);
                    activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
                }

            });
        }
        return promoCouponsDialog;
    }

    /*public void updatePoolInfoText(){
        try {
            for(Region region : Data.regions){
                if(region.getRideType() == RideTypeValue.POOL.getOrdinal() && (!getRegionSelected().getOfferTexts().getText1().equalsIgnoreCase(""))){
                    relativeLayoutPoolInfoBar.setVisibility(View.GONE);
                    textViewPoolInfo1.setText(getRegionSelected().getOfferTexts().getText1());
                    return;
                } else{
                    relativeLayoutPoolInfoBar.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public void updateBottomMultipleView(int rideType){
        if(rideType == RideTypeValue.POOL.getOrdinal()){
            textVieGetFareEstimateMS.setVisibility(View.GONE);
            linearLayoutPaymentModeMS.setVisibility(View.GONE);
            textViewMinFareMS.setText(activity.getResources().getString(R.string.base_fare));
            textViewMinFareMSValue.setText(activity.getResources().getString(R.string.two_hifen));
        } else{
            textVieGetFareEstimateMS.setVisibility(View.VISIBLE);
            linearLayoutPaymentModeMS.setVisibility(View.VISIBLE);
            textViewMinFareMS.setText(activity.getResources().getString(R.string.base_fare));

        }
        updateFareFactorUI();
    }


    public void updatePaymentOption() {
        try {
            Data.pickupPaymentOption = MyApplication.getInstance().getWalletCore()
                    .getPaymentOptionAccAvailability(Data.pickupPaymentOption);
            imageViewPaymentMode.setImageResource(MyApplication.getInstance().getWalletCore().getPaymentOptionIconSmall(Data.pickupPaymentOption));
            imageViewPaymentModeMS.setImageResource(MyApplication.getInstance().getWalletCore().getPaymentOptionIconSmall(Data.pickupPaymentOption));
            textViewPaymentModeValue.setText(MyApplication.getInstance().getWalletCore().getPaymentOptionBalanceText(Data.pickupPaymentOption));
            textViewPaymentModeValueMS.setText(MyApplication.getInstance().getWalletCore().getPaymentOptionBalanceText(Data.pickupPaymentOption));
            activity.getSlidingBottomPanel().getImageViewPaymentOp().setImageResource(MyApplication.getInstance().getWalletCore().getPaymentOptionIconSmall(Data.pickupPaymentOption));
            activity.getSlidingBottomPanel().getTextViewCashValue().setText(MyApplication.getInstance().getWalletCore().getPaymentOptionBalanceText(Data.pickupPaymentOption));
            updatePreferredPaymentOptionUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updatePreferredPaymentOptionUI(){
        try{
            if(getPaymentOptionDialog().getDialog() != null && getPaymentOptionDialog().getDialog().isShowing()){
                getPaymentOptionDialog().updatePreferredPaymentOptionUI();
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        updatePreferredPaymentOptionUISingle();
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
                updateSupplyUI(Data.regions.size());
                //updatePoolInfoText();
            } else if(Data.regions.size() > 0){
                activity.setVehicleTypeSelected(0);
                regionSelected = Data.regions.get(0);
                updateSupplyUI(Data.regions.size());
            } else{
                activity.forceFarAwayCity();
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public Region getRegionSelected() {
        if(regionSelected == null || regionSelected.isDefault()){
            if(Data.regions.size() > 0){
                regionSelected = Data.regions.get(0);
            } else{
                regionSelected = new Region();
            }
        }
        return regionSelected;
    }

    public void updateSupplyUI(int supplyCount){
        linearLayoutOptionsSingleSupply.setVisibility(View.GONE);
        relativeLayoutMultipleSupplyMain.setVisibility(View.GONE);

        if(supplyCount > 1){
            relativeLayoutMultipleSupplyMain.setVisibility(View.VISIBLE);
        } else{
            //linearLayoutOptionsSingleSupply.setVisibility(View.VISIBLE);

        }
        activity.getSlidingBottomPanel().updateBottomPanel(supplyCount);
    }

    public void updateFareStructureUI(){
        for (int i = 0; i < Data.regions.size(); i++) {
            if (Data.regions.get(i).getVehicleType().equals(getRegionSelected().getVehicleType())
                    && Data.regions.get(i).getRideType().equals(getRegionSelected().getRideType())) {
                Data.fareStructure = Data.regions.get(i).getFareStructure();
                break;
            }
        }
        textViewMinFareValue.setText(String.format(activity.getResources().getString(R.string.rupees_value_format_without_space)
                , Utils.getMoneyDecimalFormat().format(Data.fareStructure.fixedFare)));
        textViewMinFareMSValue.setText(String.format(activity.getResources().getString(R.string.rupees_value_format_without_space)
                , Utils.getMoneyDecimalFormat().format(Data.fareStructure.fixedFare)));
        textViewMaxPeople.setText(getResources().getString(R.string.max_people) + getRegionSelected().getMaxPeople());
        updateFareFactorUI();
        updateBottomMultipleView(getRegionSelected().getRideType());
        updateFareFactorUISingle();
    }

    public void updateFareFactorUI(){
        double fareFactor = getRegionSelected().getCustomerFareFactor();
        if (fareFactor > 1 || fareFactor < 1) {
            relativeLayoutPriorityTipMS.setVisibility(View.VISIBLE);
            textViewPriorityTipValue.setText(String.format(activity.getResources().getString(R.string.format_x_without_space),
                    Utils.getMoneyDecimalFormat().format(fareFactor)));
        } else {
            relativeLayoutPriorityTipMS.setVisibility(View.GONE);
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

    public PromoCoupon getSelectedCoupon() {
        initSelectedCoupon();
        return selectedCoupon;
    }

    public void setSelectedCoupon(int position) {
        PromoCoupon promoCoupon;
        if (position > -1 && position < Data.promoCoupons.size()) {
            promoCoupon = Data.promoCoupons.get(position);
        } else {
            promoCoupon = noSelectionCoupon;
        }
        if(MyApplication.getInstance().getWalletCore().displayAlertAndCheckForSelectedWalletCoupon(activity,
                Data.pickupPaymentOption, promoCoupon)){
            selectedCoupon = promoCoupon;
        }
    }

    public void setSelectedCoupon(PromoCoupon promoCoupon){
        selectedCoupon = promoCoupon;
    }

    public boolean displayAlertAndCheckForSelectedWalletCoupon() {
        return MyApplication.getInstance().getWalletCore().displayAlertAndCheckForSelectedWalletCoupon(activity,
                Data.pickupPaymentOption, selectedCoupon);
    }

    public void setRegionSelected(int position) {
        if (position > -1 && position < Data.regions.size()) {
            regionSelected = Data.regions.get(position);
        }
        vehiclesTabAdapter.notifyDataSetChanged();
        recyclerViewVehicles.getLayoutManager().scrollToPosition(position);
        updateFareStructureUI();
    }


    private FareDetailsDialog fareDetailsDialog;
    private FareDetailsDialog getFareDetailsDialog(){
        if(fareDetailsDialog == null){
            fareDetailsDialog = new FareDetailsDialog(activity, new FareDetailsDialog.Callback() {
                @Override
                public void onDialogDismiss() {

                }
            });
        }
        return fareDetailsDialog;
    }

    private PoolDestinationDialog poolDestinaionDialog;
    private PoolDestinationDialog getPoolDestinationDialog(){
        if(poolDestinaionDialog == null){
            poolDestinaionDialog = new PoolDestinationDialog(activity, new PoolDestinationDialog.Callback() {

                @Override
                public void onEnterDestination() {
                    if(Data.dropLatLng != null){
                        activity.openConfirmRequestView();
                    } else{
                        if(activity.getSlidingBottomPanel().getSlidingUpPanelLayout().getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED){
                            activity.getSlidingBottomPanel().getSlidingUpPanelLayout().setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                            activity.setShakeAnim(0);
                            activity.imageViewRideNowPoolCheck();
                        }
                    }
                }
            });
        }
        return poolDestinaionDialog;
    }

    public PaymentOptionDialog paymentOptionDialog;
    public PaymentOptionDialog getPaymentOptionDialog(){
        if(paymentOptionDialog == null){
            paymentOptionDialog = new PaymentOptionDialog(activity, new PaymentOptionDialog.Callback() {
                @Override
                public void onDialogDismiss() {

                }

                @Override
                public void onPaymentModeUpdated() {
                    activity.updateConfirmedStatePaymentUI();
                }
            });
        }
        return paymentOptionDialog;
    }









    private void updatePreferredPaymentOptionUISingle() {
        try {
            Fragment frag1 = activity.getSupportFragmentManager().findFragmentByTag("android:switcher:" + activity.getSlidingBottomPanel().getViewPager().getId() + ":" + 0);
            if (frag1 != null && frag1 instanceof SlidingBottomCashFragment) {
				((SlidingBottomCashFragment) frag1).updatePreferredPaymentOptionUI();
			}
        } catch (Exception e) {
        }
    }

    private void updateCouponsFragSingle(){
        try {
            Fragment frag = activity.getSupportFragmentManager().findFragmentByTag("android:switcher:" + activity.getSlidingBottomPanel().getViewPager().getId() + ":" + 2);
            if (frag != null && frag instanceof SlidingBottomOffersFragment) {
                ((SlidingBottomOffersFragment) frag).setOfferAdapter();
                ((SlidingBottomOffersFragment) frag).update();
            }
        } catch (Exception e) {
        }
    }

    public void updateFareFactorUISingle() {
        try {
            textViewMinFareValue.setText(String.format(activity.getResources().getString(R.string.rupees_value_format_without_space)
                    , Utils.getMoneyDecimalFormat().format(Data.fareStructure.fixedFare)));
            Fragment frag1 = activity.getSupportFragmentManager().findFragmentByTag("android:switcher:" + activity.getSlidingBottomPanel().getViewPager().getId() + ":" + 1);
            if (frag1 != null && frag1 instanceof SlidingBottomFareFragment) {
                ((SlidingBottomFareFragment) frag1).update();
            }
            activity.getSlidingBottomPanel().getTextViewMinFareValue().setText(String.format(activity.getResources().getString(R.string.rupees_value_format_without_space)
                    , Utils.getMoneyDecimalFormat().format(Data.fareStructure.fixedFare)));
            setSurgeImageVisibility();
        } catch (Exception e) {
        }
    }

    public void setSurgeImageVisibility(){
        try {
            if(activity.getSlidingBottomPanel().getSlidingUpPanelLayout().getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED
                    && Data.userData.fareFactor > 1.0
                    && Data.regions.size() == 1){
                activity.getSlidingBottomPanel().getImageViewSurgeOverSlidingBottom().setVisibility(View.VISIBLE);
            } else{
                activity.getSlidingBottomPanel().getImageViewSurgeOverSlidingBottom().setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            activity.getSlidingBottomPanel().getImageViewSurgeOverSlidingBottom().setVisibility(View.GONE);
        }
    }

}

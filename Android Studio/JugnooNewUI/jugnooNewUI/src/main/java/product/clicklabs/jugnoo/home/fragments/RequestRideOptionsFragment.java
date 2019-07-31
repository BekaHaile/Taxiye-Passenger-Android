package product.clicklabs.jugnoo.home.fragments;

import android.annotation.SuppressLint;
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
import android.widget.Toast;

import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.CouponInfo;
import product.clicklabs.jugnoo.datastructure.MenuInfoTags;
import product.clicklabs.jugnoo.datastructure.ProductType;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.datastructure.PromotionInfo;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.adapters.VehiclesTabAdapter;
import product.clicklabs.jugnoo.home.dialogs.FareDetailsDialog;
import product.clicklabs.jugnoo.home.dialogs.PaymentOptionDialog;
import product.clicklabs.jugnoo.home.dialogs.PromoCouponsDialog;
import product.clicklabs.jugnoo.home.models.Region;
import product.clicklabs.jugnoo.home.models.RideTypeValue;
import product.clicklabs.jugnoo.promotion.ReferralActions;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.LinearLayoutManagerForResizableRecyclerView;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;
import product.clicklabs.jugnoo.wallet.models.PaymentModeConfigData;

/**
 * Created by Shankar on 1/8/16.
 */
@SuppressLint("ValidFragment")
public class RequestRideOptionsFragment extends Fragment implements Constants, GACategory, GAAction{

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
    private ImageView ivOfferDiv, ivOfferDivOld;
    private RelativeLayout relativeLayoutPriorityTipMS;

    private LinearLayout llRideDetailsOptions, llStarSavings;
    private TextView tvStarSavings;

    private VehiclesTabAdapter vehiclesTabAdapter;

    private Region regionSelected = null;
    private static PromoCoupon selectedCoupon = null;
    private PromoCoupon noSelectionCoupon = new CouponInfo(-1, "");


    /*public RelativeLayout getRelativeLayoutPoolInfoBar() {
        return relativeLayoutPoolInfoBar;
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_request_ride_options, container, false);
        noSelectionCoupon = new CouponInfo(-1, getString(R.string.dont_apply_coupon_on_this_ride));

        activity = (HomeActivity) getActivity();
        linearLayoutSlidingBottom = (LinearLayout) rootView.findViewById(R.id.linearLayoutSlidingBottom);
        try {
            if(linearLayoutSlidingBottom != null) {
                new ASSL(getActivity(), linearLayoutSlidingBottom, 1134, 720, false);
            }
        } catch (Exception e) {
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
        ivOfferDivOld = (ImageView) rootView.findViewById(R.id.ivOfferDivOld);
        textViewOffersMode.setTypeface(Fonts.mavenMedium(activity));

        linearLayoutMinFareMS = (LinearLayout) rootView.findViewById(R.id.linearLayoutMinFareMS);
        textViewMinFareMS = (TextView) rootView.findViewById(R.id.textViewMinFareMS);
        textViewMinFareMS.setTypeface(Fonts.avenirNext(activity), Typeface.BOLD);
        textViewMinFareMSValue = (TextView) rootView.findViewById(R.id.textViewMinFareMSValue);
        textViewMinFareMSValue.setTypeface(Fonts.avenirNext(activity), Typeface.BOLD);
        textViewMaxPeople = (TextView) rootView.findViewById(R.id.textViewMaxPeople);
        textViewMaxPeople.setTypeface(Fonts.avenirNext(activity), Typeface.BOLD);
        textViewOffers = (TextView) rootView.findViewById(R.id.textViewOffers);
        ivOfferDiv = (ImageView) rootView.findViewById(R.id.ivOfferDiv);
        textViewOffers.setTypeface(Fonts.avenirNext(activity), Typeface.BOLD);
        if(Data.isMenuTagEnabled(MenuInfoTags.OFFERS)){
            textViewOffers.setVisibility(View.VISIBLE);
            ivOfferDiv.setVisibility(View.VISIBLE);
            textViewOffersMode.setVisibility(View.VISIBLE);
            ivOfferDivOld.setVisibility(View.VISIBLE);
        } else {
            textViewOffers.setVisibility(View.GONE);
            ivOfferDiv.setVisibility(View.GONE);
            textViewOffersMode.setVisibility(View.GONE);
            ivOfferDivOld.setVisibility(View.GONE);
        }

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

        try {
            vehiclesTabAdapter = new VehiclesTabAdapter(activity, Data.autoData.getRegions(),false);
            recyclerViewVehicles.setAdapter(vehiclesTabAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        llRideDetailsOptions = (LinearLayout) rootView.findViewById(R.id.llRideDetailsOptions);
        llStarSavings = (LinearLayout) rootView.findViewById(R.id.llStarSavings); llStarSavings.setVisibility(View.GONE);
        tvStarSavings = (TextView) rootView.findViewById(R.id.tvStarSavings); tvStarSavings.setTypeface(Fonts.mavenMedium(activity));


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

        relativeLayoutMultipleSupplyMain.post(new Runnable() {
            @Override
            public void run() {
                updateOffersCount();
            }
        });

        updatePaymentOption();

        try {
            textViewMaxPeople.setText(getResources().getString(R.string.max_people) + " " + getRegionSelected().getMaxPeople());
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(Data.autoData != null && Data.autoData.getRegions().size() > 1) {
                        activity.setVehicleTypeSelected(0, false, true);
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
                getPaymentOptionDialog().show(-1, null);
                GAUtils.event(RIDES, HOME, WALLET+CLICKED);
            } else if(v.getId() == R.id.linearLayoutFare || v.getId() == R.id.linearLayoutMinFareMS){
                getFareDetailsDialog().show();
                GAUtils.event(RIDES, HOME, FARE_DETAILS+CLICKED);


            } else if(v.getId() == R.id.linearLayoutFareEstimate || v.getId() == R.id.textVieGetFareEstimateMS){
                activity.openFareEstimate();

                GAUtils.event(RIDES, HOME, FARE_ESTIMATE+CLICKED);
            } else if(v.getId() == R.id.textViewOffers || v.getId() == R.id.textViewOffersMode){
                if(Data.userData.getCoupons(ProductType.AUTO, activity, false).size() > 0
                        || Data.userData.getShowOfferDialog() == 1) {
                    getPromoCouponsDialog().show(Data.userData.getCoupons(ProductType.AUTO, activity, false));
                }
                GAUtils.event(RIDES, HOME, OFFER+CLICKED);
            }
        }
    };

    public void updateOffersCount(){
        try {
            if(Data.userData.getCoupons(ProductType.AUTO, activity, false).size() > 0) {
                textViewOffers.setText(activity.getResources().getString(R.string.nl_offers) + "\n" + Data.userData.getCoupons(ProductType.AUTO, activity, false).size());
                textViewOffersMode.setText(activity.getResources().getString(R.string.nl_offers) + "\n" + Data.userData.getCoupons(ProductType.AUTO, activity, false).size());
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
                    /*Intent intent = new Intent(activity, ShareActivity.class);
                    startActivity(intent);
                    activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);*/
                    if(Utils.appInstalledOrNot(activity, "com.whatsapp")){
                        ReferralActions.shareToWhatsapp(activity);
                    } else {
                        ReferralActions.openGenericShareIntent(activity, null);
                    }
                    GAUtils.event(RIDES, NO+OFFER+GAAction.REFERRAL+DIALOG, INVITE+FRIENDS+CLICKED);
                }

            });
        }
        return promoCouponsDialog;
    }

    /*public void updatePoolInfoText(){
        try {
            for(Region region : Data.autoData.getRegions()){
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
            textViewMinFareMSValue.setText(Data.autoData.getFareStructure().getDisplayBaseFare(activity));
        } else{
            textVieGetFareEstimateMS.setVisibility(View.VISIBLE);
            linearLayoutPaymentModeMS.setVisibility(View.VISIBLE);
            textViewMinFareMS.setText(activity.getResources().getString(R.string.base_fare));

        }
        updateFareFactorUI();

        updateSubscriptionMessage();
    }


    public void updatePaymentOption() {
        try {

            int selectedPaymentOption = MyApplication.getInstance().getWalletCore()
                    .getPaymentOptionAccAvailability(Data.autoData.getPickupPaymentOption());
            Region region = (Data.autoData.getRegions().size() > 1) ? activity.slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected()
                    : (Data.autoData.getRegions().size() > 0 ? Data.autoData.getRegions().get(0) : null);
            if (region != null && region.getRestrictedPaymentModes().size() > 0) {
                if (region.getRestrictedPaymentModes().contains(selectedPaymentOption)) {
                    Data.autoData.setPickupPaymentOption(chooseNextEligiblePaymentoption());
                }
            }

            imageViewPaymentMode.setImageResource(MyApplication.getInstance().getWalletCore().getPaymentOptionIconSmall(Data.autoData.getPickupPaymentOption()));
            imageViewPaymentModeMS.setImageResource(MyApplication.getInstance().getWalletCore().getPaymentOptionIconSmall(Data.autoData.getPickupPaymentOption()));
            textViewPaymentModeValue.setText(MyApplication.getInstance().getWalletCore().getPaymentOptionBalanceText(Data.autoData.getPickupPaymentOption()));
            textViewPaymentModeValueMS.setText(MyApplication.getInstance().getWalletCore().getPaymentOptionBalanceText(Data.autoData.getPickupPaymentOption()));
            activity.getSlidingBottomPanel().getImageViewPaymentOp().setImageResource(MyApplication.getInstance().getWalletCore().getPaymentOptionIconSmall(Data.autoData.getPickupPaymentOption()));
            activity.getSlidingBottomPanel().getTextViewCashValue().setText(MyApplication.getInstance().getWalletCore().getPaymentOptionBalanceText(Data.autoData.getPickupPaymentOption()));
            updatePreferredPaymentOptionUI();
            activity.updateConfirmedStatePaymentUI();
        } catch (Exception e) {
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
            if(Data.autoData.getRegions().size() > 1){
                boolean matched = false;
                for (int i=0; i<Data.autoData.getRegions().size(); i++) {
                    if(Data.autoData.getRegions().get(i).getOperatorId() == getRegionSelected().getOperatorId()
                            && Data.autoData.getRegions().get(i).getRegionId().equals(getRegionSelected().getRegionId())
                            && Data.autoData.getRegions().get(i).getVehicleType().equals(getRegionSelected().getVehicleType())
                            ){
                        regionSelected = Data.autoData.getRegions().get(i);
                        matched = true;
                        break;
                    }
                }
                if(!matched){
                    regionSelected = Data.autoData.getRegions().get(0);
                }
                vehiclesTabAdapter.notifyDataSetChanged();
                activity.updateFareEstimateHoverButton();
                updateSupplyUI(Data.autoData.getRegions().size());
                //updatePoolInfoText();
            } else if(Data.autoData.getRegions().size() > 0){
                activity.setVehicleTypeSelected(0, false, false);
                regionSelected = Data.autoData.getRegions().get(0);
                updateSupplyUI(Data.autoData.getRegions().size());
            } else{
                activity.forceFarAwayCity();
            }
            activity.updateFareEstimateHoverButton();
        } catch(Exception e){
        }
    }

    public Region getRegionSelected() {
        if(regionSelected == null || regionSelected.isDefault()){
            if(Data.autoData.getRegions().size() > 0){
                regionSelected = Data.autoData.getRegions().get(0);
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
        activity.setDestinationBarPlaceholderText(regionSelected.getRideType());
        if(supplyCount > 0){
            Data.autoData.setFarAwayCity("");
            activity.setServiceAvailablityUI(Data.autoData.getFarAwayCity());
        }
    }

    public void updateFareStructureUI(){
        try {
            for (int i = 0; i < Data.autoData.getRegions().size(); i++) {
                if (Data.autoData.getRegions().get(i).getOperatorId() == getRegionSelected().getOperatorId()
                        && Data.autoData.getRegions().get(i).getVehicleType().equals(getRegionSelected().getVehicleType())
                        && Data.autoData.getRegions().get(i).getRideType().equals(getRegionSelected().getRideType())
                        ) {
                    Data.autoData.setFareStructure(Data.autoData.getRegions().get(i).getFareStructure());
                    break;
                }
            }
            textViewMinFareValue.setText(Data.autoData.getFareStructure().getDisplayBaseFare(activity));
            textViewMinFareMSValue.setText(Data.autoData.getFareStructure().getDisplayBaseFare(activity));
            textViewMaxPeople.setText(getResources().getString(R.string.max_people) + " " + getRegionSelected().getMaxPeople());
            updateFareFactorUI();
            updateBottomMultipleView(getRegionSelected().getRideType());
            updateFareFactorUISingle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateFareFactorUI(){
        double fareFactor = getRegionSelected().getCustomerFareFactor();
        if (activity.showSurgeIcon() && fareFactor > 1 || fareFactor < 1) {
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
                if (Data.userData.getCoupons(ProductType.AUTO, activity, false).size() > 0) {
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

    public RecyclerView getRecyclerViewVehicles() {
        return recyclerViewVehicles;
    }

    public boolean setSelectedCoupon(int position, boolean ignoreVehiclesCheck) {
        ArrayList<PromoCoupon> promoCoupons = Data.userData.getCoupons(ProductType.AUTO, activity, ignoreVehiclesCheck);
        PromoCoupon promoCoupon;
        if (position > -1 && position < promoCoupons.size()
                && (ignoreVehiclesCheck || promoCoupons.get(position).isVehicleTypeExists(activity.getVehicleTypeSelected(), activity.getOperatorIdSelected()))) {
            promoCoupon = promoCoupons.get(position);
            GAUtils.event(RIDES, HOME+OFFER+SELECTED, promoCoupon.getTitle());
        } else {
            promoCoupon = noSelectionCoupon;
        }
        if(MyApplication.getInstance().getWalletCore().displayAlertAndCheckForSelectedWalletCoupon(activity,
                Data.autoData.getPickupPaymentOption(), promoCoupon)){
            boolean callFindADriverAtCouponChange = selectedCoupon == null || selectedCoupon.getId() != promoCoupon.getId();
            selectedCoupon = promoCoupon;
            if(callFindADriverAtCouponChange) {
                activity.callFindADriverAfterCouponSelect();
            }
            return true;
        } else {
            return false;
        }
    }

    public void setSelectedCoupon(PromoCoupon promoCoupon){
        if(promoCoupon!=null && promoCoupon.isVehicleTypeExists(activity.getVehicleTypeSelected(), activity.getOperatorIdSelected())){
            selectedCoupon = promoCoupon;

        }else{
            selectedCoupon = noSelectionCoupon;
        }
    }

    public boolean displayAlertAndCheckForSelectedWalletCoupon() {
        return MyApplication.getInstance().getWalletCore().displayAlertAndCheckForSelectedWalletCoupon(activity,
                Data.autoData.getPickupPaymentOption(), selectedCoupon);
    }

    public void setRegionSelected(int position) {
        if (position > -1 && position < Data.autoData.getRegions().size()) {
            regionSelected = Data.autoData.getRegions().get(position);
        }
        vehiclesTabAdapter.notifyDataSetChanged();
        recyclerViewVehicles.getLayoutManager().scrollToPosition(position);
        updateFareStructureUI();
    }
    public void setRegionSelected(Region region) {
        regionSelected = region;
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

    public PaymentOptionDialog paymentOptionDialog;
    public PaymentOptionDialog getPaymentOptionDialog(){
        if(paymentOptionDialog == null){
            paymentOptionDialog = new PaymentOptionDialog(activity, activity.getCallbackPaymentOptionSelector(), new PaymentOptionDialog.Callback() {
                @Override
                public void onDialogDismiss() {

                }

                @Override
                public void onPaymentModeUpdated() {
                    activity.updateConfirmedStatePaymentUI();

                    try {GAUtils.event(RIDES, HOME+WALLET+SELECTED, MyApplication.getInstance().getWalletCore()
                            .getPaymentOptionName(Data.autoData.getPickupPaymentOption()));} catch (Exception e) {}
                }
            });
        }
        return paymentOptionDialog;
    }
    public void dismissPaymentDialog(){
        if(paymentOptionDialog!=null){
            paymentOptionDialog.dismiss();
        }

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

    public void updateCouponsFragSingle(){
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
            textViewMinFareValue.setText(Data.autoData.getFareStructure().getDisplayBaseFare(activity));
            Fragment frag1 = activity.getSupportFragmentManager().findFragmentByTag("android:switcher:" + activity.getSlidingBottomPanel().getViewPager().getId() + ":" + 1);
            if (frag1 != null && frag1 instanceof SlidingBottomFareFragment) {
                ((SlidingBottomFareFragment) frag1).update();
            }
            activity.getSlidingBottomPanel().getTextViewMinFareValue().setText(Data.autoData.getFareStructure().getDisplayBaseFare(activity));
            setSurgeImageVisibility();
        } catch (Exception e) {
        }
    }

    public void setSurgeImageVisibility(){
        try {
            if(activity.showSurgeIcon() && activity.getSlidingBottomPanel().getSlidingUpPanelLayout().getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED
                    && Data.autoData.getFareFactor() > 1.0
                    && Data.autoData.getRegions().size() == 1){
                if(activity.getFabViewTest() != null && !activity.getFabViewTest().getIsOpened()) {
                    activity.getSlidingBottomPanel().getImageViewSurgeOverSlidingBottom().setVisibility(View.VISIBLE);
                } else {
                    activity.getSlidingBottomPanel().getImageViewSurgeOverSlidingBottom().setVisibility(View.GONE);
                }
            } else{
                activity.getSlidingBottomPanel().getImageViewSurgeOverSlidingBottom().setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            activity.getSlidingBottomPanel().getImageViewSurgeOverSlidingBottom().setVisibility(View.GONE);
        }
    }

    public void updateSubscriptionMessage(){
        if(Data.userData != null && Data.userData.isSubscriptionActive()){
            llStarSavings.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams paramsll = (LinearLayout.LayoutParams) llRideDetailsOptions.getLayoutParams();
            paramsll.height = (int) (ASSL.Yscale() * 90f);
            llRideDetailsOptions.setLayoutParams(paramsll);
            LinearLayout.LayoutParams paramstv = (LinearLayout.LayoutParams) textVieGetFareEstimateMS.getLayoutParams();
            paramstv.height = (int) (ASSL.Yscale() * 90f);
            textVieGetFareEstimateMS.setLayoutParams(paramstv);
        }
        else {
            llStarSavings.setVisibility(View.GONE);
            LinearLayout.LayoutParams paramsll = (LinearLayout.LayoutParams) llRideDetailsOptions.getLayoutParams();
            paramsll.height = (int) (ASSL.Yscale() * 100f);
            llRideDetailsOptions.setLayoutParams(paramsll);
            LinearLayout.LayoutParams paramstv = (LinearLayout.LayoutParams) textVieGetFareEstimateMS.getLayoutParams();
            paramstv.height = (int) (ASSL.Yscale() * 100f);
            textVieGetFareEstimateMS.setLayoutParams(paramstv);
        }
    }

    /**
     * To auto apply a selected coupon from Promotions screen
     * @return returns true if some coupon is selected or can't be selected else false
     */
    public boolean selectAutoSelectedCouponAtRequestRide(){
        String clientId = Config.getAutosClientId();
        boolean couponSelected = false;
        try {
            int promoCouponId = Prefs.with(activity).getInt(Constants.SP_USE_COUPON_+clientId, -1);
            boolean isCouponInfo = Prefs.with(activity).getBoolean(Constants.SP_USE_COUPON_IS_COUPON_ + clientId, false);
            if(promoCouponId > 0){
                ArrayList<PromoCoupon> promoCoupons = Data.userData.getCoupons(ProductType.AUTO, activity, true);
                for(int i = 0; i<promoCoupons.size(); i++){
                    PromoCoupon pc = promoCoupons.get(i);
                    if(((isCouponInfo && pc instanceof CouponInfo) || (!isCouponInfo && pc instanceof PromotionInfo))
                            && pc.getId() == promoCouponId) {
                        if (pc.getIsValid() == 1 && setSelectedCoupon(i, true)) {
                            if(pc.getAllowedVehicles() != null && pc.getAllowedVehicles().size() > 0
                                    && !pc.isVehicleTypeExists(getRegionSelected().getVehicleType(), getRegionSelected().getOperatorId())){
                                for(int j=0; j<Data.autoData.getRegions().size(); j++){
                                    if(Data.autoData.getRegions().get(j).getVehicleType().equals(pc.getAllowedVehicles().get(0))){
                                        activity.setVehicleTypeSelected(j, false, false);
                                        break;
                                    }
                                }
                            }
                            Utils.showToast(activity, activity.getString(R.string.offer_auto_applied_message_format, getString(R.string.ride)), Toast.LENGTH_LONG);
                        }
                        couponSelected = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Prefs.with(activity).save(Constants.SP_USE_COUPON_ + clientId, -1);
        Prefs.with(activity).save(Constants.SP_USE_COUPON_IS_COUPON_ + clientId, false);
        return couponSelected;
    }

    private int chooseNextEligiblePaymentoption() {
        int selectedPaymentOption = MyApplication.getInstance().getWalletCore()
                .getPaymentOptionAccAvailability(Data.autoData.getPickupPaymentOption());
        ArrayList<PaymentModeConfigData> paymentModeConfigDatas = MyApplication.getInstance().getWalletCore().getPaymentModeConfigDatas();
        if (paymentModeConfigDatas != null && paymentModeConfigDatas.size() > 0) {
            List<Integer> restrictedPaymentMode = new ArrayList<>();
            if (Data.autoData.getRegions().size() > 1) {
                restrictedPaymentMode = ((HomeActivity) activity).getSlidingBottomPanel().getRequestRideOptionsFragment().getRegionSelected().getRestrictedPaymentModes();
            } else if (Data.autoData.getRegions().size() > 0) {
                restrictedPaymentMode = Data.autoData.getRegions().get(0).getRestrictedPaymentModes();
            }
            for (PaymentModeConfigData paymentModeConfigData : paymentModeConfigDatas) {
                if (paymentModeConfigData.getEnabled() == 1) {
                    if ((restrictedPaymentMode.size() > 0 && !restrictedPaymentMode.contains(paymentModeConfigData.getPaymentOption())) || restrictedPaymentMode.size() == 0) {
                        selectedPaymentOption = paymentModeConfigData.getPaymentOption();
                        break;
                    }
                }
            }
        }
        return selectedPaymentOption;
    }

}

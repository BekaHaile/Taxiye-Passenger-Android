package product.clicklabs.jugnoo.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.feed.ui.api.APICommonCallback;
import com.sabkuchfresh.feed.ui.api.ApiCommon;
import com.sabkuchfresh.feed.ui.api.ApiName;
import com.sabkuchfresh.home.CallbackPaymentOptionSelector;
import com.sabkuchfresh.home.FreshWalletBalanceLowDialog;
import com.sabkuchfresh.retrofit.model.PurchaseSubscriptionResponse;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import butterknife.ButterKnife;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.JugnooStarActivity;
import product.clicklabs.jugnoo.JugnooStarSubscribedActivity;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.RazorpayBaseActivity;
import product.clicklabs.jugnoo.apis.ApiFetchWalletBalance;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.CouponInfo;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.datastructure.PromotionInfo;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.datastructure.StarPurchaseType;
import product.clicklabs.jugnoo.datastructure.SubscriptionData;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.home.adapters.PromoCouponsAdapter;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.PaymentResponse;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.NonScrollListView;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;
import product.clicklabs.jugnoo.wallet.PaymentActivity;
import product.clicklabs.jugnoo.wallet.models.PaymentActivityPath;
import product.clicklabs.jugnoo.wallet.models.PaymentModeConfigData;
import product.clicklabs.jugnoo.widgets.slider.PaySlider;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


/**
 * Created by ankit on 30/12/16.
 */

public class StarSubscriptionCheckoutFragment extends Fragment implements PromoCouponsAdapter.Callback, GAAction, GACategory {

    private View rootView;
    private RazorpayBaseActivity activity;
    private CardView cvStarPlans;
    private TextView tvPaymentPlan, tvPlanAmount, tvActualAmount1, tvActualAmount2, tvAmount1, tvAmount2, tvPeriod1, tvPeriod2,
            tvDuration1, tvDuration2;
    private Button bPlaceOrder;
    private LinearLayout linearLayoutOffers, linearLayoutRoot, llStarPurchase;
    private NonScrollListView listViewOffers;
    private PromoCouponsAdapter promoCouponsAdapter;
    private RelativeLayout relativeLayoutPaytm, relativeLayoutMobikwik, relativeLayoutFreeCharge, rlPlan1, rlPlan2, rlStarUpgrade;
    private ImageView imageViewPaytmRadio, imageViewAddPaytm, imageViewRadioMobikwik, imageViewAddMobikwik,
            imageViewRadioFreeCharge, imageViewAddFreeCharge, ivRadio1, ivRadio2;
    private TextView textViewPaytmValue, textViewMobikwikValue, textViewFreeChargeValue;
    private PaymentOption paymentOption = PaymentOption.CASH;
    private LinearLayout linearLayoutWalletContainer;
    private SubscriptionData.Subscription subscription;
    private ArrayList<PromoCoupon> promoCoupons = new ArrayList<>();
    private int purchaseType = StarPurchaseType.PURCHARE.getOrdinal();
    private RelativeLayout rlOtherModesToPay, rlUPI;
    private ImageView ivOtherModesToPay, ivUPI;
    private TextView tvOtherModesToPay, tvUPI;
    private boolean isRazorUPI;
    private PaySlider paySlider;
    private ArrayList<SubscriptionData.Subscription> subscriptionsActivityList;

    private RelativeLayout relativeLayoutIcici;
    private EditText edtIciciVpa;
    private TextView tvLabelIciciUpi, tvUPICashback;
    private ImageView imageViewIcici;

    private LinearLayout llRideInfo;
    private TextView tvTotalFareValue, tvCashPaidValue, textViewPaymentVia;

    private boolean fromStar;
    private int engagementId;
    private double totalFare, fareToPay;
    private String jugnooVpaHandle;


    private static final String FOR_STAR_SUBSCRIPTION = "for_star_subscription";


    public static StarSubscriptionCheckoutFragment newInstance(String subscription, int type){
        StarSubscriptionCheckoutFragment fragment = new StarSubscriptionCheckoutFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(FOR_STAR_SUBSCRIPTION, true);
        bundle.putString("plan", subscription);
        bundle.putInt("type", type);
        fragment.setArguments(bundle);
        return  fragment;
    }

    public static StarSubscriptionCheckoutFragment newInstance(int engagementId, double totalFare, double fare){
        StarSubscriptionCheckoutFragment fragment = new StarSubscriptionCheckoutFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(FOR_STAR_SUBSCRIPTION, false);
        bundle.putInt(Constants.KEY_ENGAGEMENT_ID, engagementId);
        bundle.putDouble(Constants.KEY_TOTAL_FARE, totalFare);
        bundle.putDouble(Constants.KEY_FARE_TO_PAY, fare);
        fragment.setArguments(bundle);
        return  fragment;
    }

    private void parseArguments() {
        Bundle bundle = getArguments();
        fromStar = bundle.getBoolean(FOR_STAR_SUBSCRIPTION);
        if(fromStar) {
            purchaseType = bundle.getInt("type", StarPurchaseType.PURCHARE.getOrdinal());
            String plan = bundle.getString("plan", "");
            subscription = new Gson().fromJson(plan, SubscriptionData.Subscription.class);
        } else {
            engagementId = bundle.getInt(Constants.KEY_ENGAGEMENT_ID);
            totalFare = bundle.getDouble(Constants.KEY_TOTAL_FARE);
            fareToPay = bundle.getDouble(Constants.KEY_FARE_TO_PAY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_star_subscription_checkout, container, false);
        ButterKnife.bind(this,rootView);
        activity = (RazorpayBaseActivity) getActivity();

        GAUtils.trackScreenView(JUGNOO+STAR+CHECKOUT);

        linearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.linearLayoutRoot);
        try {
            if (linearLayoutRoot != null) {
                new ASSL(activity, linearLayoutRoot, 1134, 720, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            parseArguments();

            llStarPurchase = (LinearLayout) rootView.findViewById(R.id.llStarPurchase);
            rlStarUpgrade = (RelativeLayout) rootView.findViewById(R.id.rlStarUpgrade);
            cvStarPlans = (CardView) rootView.findViewById(R.id.cvStarPlans);
            tvPaymentPlan = (TextView) rootView.findViewById(R.id.tvPaymentPlan); tvPaymentPlan.setTypeface(Fonts.mavenMedium(activity));
            tvPlanAmount = (TextView) rootView.findViewById(R.id.tvPlanAmount); tvPlanAmount.setTypeface(Fonts.mavenMedium(activity));
            bPlaceOrder = (Button) rootView.findViewById(R.id.bPlaceOrder); bPlaceOrder.setTypeface(Fonts.mavenMedium(activity)); bPlaceOrder.setOnClickListener(onClickListenerPaymentOptionSelector);
            linearLayoutWalletContainer = (LinearLayout) rootView.findViewById(R.id.linearLayoutWalletContainer);
            relativeLayoutPaytm = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutPaytm);
            relativeLayoutMobikwik = (RelativeLayout)rootView.findViewById(R.id.relativeLayoutMobikwik);
            relativeLayoutFreeCharge = (RelativeLayout)rootView.findViewById(R.id.relativeLayoutFreeCharge);
            imageViewPaytmRadio = (ImageView) rootView.findViewById(R.id.imageViewPaytmRadio);
            imageViewAddPaytm = (ImageView) rootView.findViewById(R.id.imageViewAddPaytm);
            imageViewRadioMobikwik = (ImageView)rootView.findViewById(R.id.imageViewRadioMobikwik);
            imageViewAddMobikwik = (ImageView) rootView.findViewById(R.id.imageViewAddMobikwik);
            imageViewRadioFreeCharge = (ImageView)rootView.findViewById(R.id.imageViewRadioFreeCharge);
            imageViewAddFreeCharge = (ImageView) rootView.findViewById(R.id.imageViewAddFreeCharge);
            textViewPaytmValue = (TextView)rootView.findViewById(R.id.textViewPaytmValue);textViewPaytmValue.setTypeface(Fonts.mavenMedium(activity));
            textViewMobikwikValue = (TextView)rootView.findViewById(R.id.textViewMobikwikValue);textViewMobikwikValue.setTypeface(Fonts.mavenMedium(activity));
            textViewFreeChargeValue = (TextView)rootView.findViewById(R.id.textViewFreeChargeValue);textViewFreeChargeValue.setTypeface(Fonts.mavenMedium(activity));

            rlPlan1 = (RelativeLayout) rootView.findViewById(R.id.rlPlan1); rlPlan1.setOnClickListener(onClickListenerPaymentOptionSelector); rlPlan1.setVisibility(View.GONE);
            rlPlan2 = (RelativeLayout) rootView.findViewById(R.id.rlPlan2); rlPlan2.setOnClickListener(onClickListenerPaymentOptionSelector); rlPlan2.setVisibility(View.GONE);
            ivRadio1 = (ImageView) rootView.findViewById(R.id.ivRadio1);
            ivRadio2 = (ImageView) rootView.findViewById(R.id.ivRadio2);
            tvActualAmount1 = (TextView) rootView.findViewById(R.id.tvActualAmount1); tvActualAmount1.setTypeface(Fonts.mavenRegular(activity));
            tvActualAmount2 = (TextView) rootView.findViewById(R.id.tvActualAmount2); tvActualAmount2.setTypeface(Fonts.mavenRegular(activity));
            tvAmount1 = (TextView) rootView.findViewById(R.id.tvAmount1); tvAmount1.setTypeface(Fonts.mavenMedium(activity));
            tvAmount2 = (TextView) rootView.findViewById(R.id.tvAmount2); tvAmount2.setTypeface(Fonts.mavenMedium(activity));
            tvPeriod1 = (TextView) rootView.findViewById(R.id.tvPeriod1); tvPeriod1.setTypeface(Fonts.mavenRegular(activity));
            tvPeriod2 = (TextView) rootView.findViewById(R.id.tvPeriod2); tvPeriod2.setTypeface(Fonts.mavenRegular(activity));
            tvDuration1 = (TextView) rootView.findViewById(R.id.tvDuration1);
            tvDuration2 = (TextView) rootView.findViewById(R.id.tvDuration2);

            rlOtherModesToPay = (RelativeLayout) rootView.findViewById(R.id.rlOtherModesToPay);
            ivOtherModesToPay = (ImageView) rootView.findViewById(R.id.ivOtherModesToPay);
            tvOtherModesToPay = (TextView) rootView.findViewById(R.id.tvOtherModesToPay);
            rlUPI = (RelativeLayout) rootView.findViewById(R.id.rlUPI);
            ivUPI = (ImageView) rootView.findViewById(R.id.ivUPI);
            tvUPI = (TextView) rootView.findViewById(R.id.tvUPI);
            relativeLayoutIcici = (RelativeLayout) rootView.findViewById(R.id.rlIciciUpi);
            edtIciciVpa = (EditText) rootView.findViewById(R.id.edtIciciVpa);
            tvUPICashback = (TextView) rootView.findViewById(R.id.tvUPICashback);
            tvUPICashback.setTypeface(tvUPICashback.getTypeface(), Typeface.ITALIC);
            tvLabelIciciUpi = (TextView) rootView.findViewById(R.id.tv_label_below_edt_icici);
            imageViewIcici = (ImageView) rootView.findViewById(R.id.ivRadioIciciUpi);

            llRideInfo = (LinearLayout) rootView.findViewById(R.id.llRideInfo); llRideInfo.setVisibility(View.GONE);
            tvTotalFareValue = (TextView) rootView.findViewById(R.id.tvTotalFareValue); tvTotalFareValue.setTypeface(tvTotalFareValue.getTypeface(), Typeface.BOLD);
            tvCashPaidValue = (TextView) rootView.findViewById(R.id.tvCashPaidValue); tvCashPaidValue.setTypeface(tvCashPaidValue.getTypeface(), Typeface.BOLD);
            textViewPaymentVia = (TextView) rootView.findViewById(R.id.textViewPaymentVia);


            relativeLayoutPaytm.setOnClickListener(onClickListenerPaymentOptionSelector);
            relativeLayoutMobikwik.setOnClickListener(onClickListenerPaymentOptionSelector);
            relativeLayoutFreeCharge.setOnClickListener(onClickListenerPaymentOptionSelector);
            rlUPI.setOnClickListener(onClickListenerPaymentOptionSelector);
            rlOtherModesToPay.setOnClickListener(onClickListenerPaymentOptionSelector);
            relativeLayoutIcici.setOnClickListener(onClickListenerPaymentOptionSelector);

            paySlider = new PaySlider(rootView.findViewById(R.id.llPayViewContainer)) {
                @Override
                public void onPayClick() {
                    bPlaceOrder.performClick();
                }
            };


            cvStarPlans.setVisibility(View.VISIBLE);
            if(activity instanceof JugnooStarActivity) {
                llStarPurchase.setVisibility(View.VISIBLE);
                rlStarUpgrade.setVisibility(View.GONE);
                setPlan();
            } else if(activity instanceof JugnooStarSubscribedActivity){

                subscriptionsActivityList = ((JugnooStarSubscribedActivity)activity).getListToShowInCheckout();
                boolean isUprgradeAndRenewList = false ;

                if(subscriptionsActivityList.size()==1){
                    llStarPurchase.setVisibility(View.GONE);
                    rlStarUpgrade.setVisibility(View.VISIBLE);
                    subscription=subscriptionsActivityList.get(0);
                    purchaseType =subscriptionsActivityList.get(0).getStarPurchaseType().getOrdinal();
                    tvPaymentPlan.setText(subscription.getDescription());
                    tvPlanAmount.setText(String.format(activity.getResources().getString(R.string.rupees_value_format_without_space), Utils.getMoneyDecimalFormat().format(getAmount())));

                } else{

                    llStarPurchase.setVisibility(View.VISIBLE);
                    rlStarUpgrade.setVisibility(View.GONE);
                    if(subscriptionsActivityList.size()>1 && subscriptionsActivityList.get(0).getStarPurchaseType()!=null && subscriptionsActivityList.get(0).getStarPurchaseType()!=StarPurchaseType.PURCHARE) {
                        Collections.swap(subscriptionsActivityList, 0, 1);
                        isUprgradeAndRenewList=true;
                    }
                    for(int i=0; i<subscriptionsActivityList.size(); i++) {
                        if (i == 0) {
                            rlPlan1.setVisibility(View.VISIBLE);
                            if(subscriptionsActivityList.get(i).getAmountText()!=null)
                                tvAmount1.setText(subscriptionsActivityList.get(i).getAmountText());
                            else if(getAmount()!=null){
                                tvAmount1.setText(String.format(activity.getResources().getString(R.string.rupees_value_format_without_space), Utils.getMoneyDecimalFormat().format(subscriptionsActivityList.get(i).getAmount())));

                            }

                            tvPeriod1.setText(String.valueOf(subscriptionsActivityList.get(i).getDescription()));
                            if(subscriptionsActivityList.get(i).getDurationText()!=null){
                                tvDuration1.setText("/"+subscriptionsActivityList.get(i).getDurationText());
                            }

                        } else if (i == 1) {
                            rlPlan2.setVisibility(View.VISIBLE);
                            if(subscriptionsActivityList.get(i).getAmountText()!=null)
                                tvAmount2.setText(subscriptionsActivityList.get(i).getAmountText());
                            else if(getAmount()!=null){
                                tvAmount2.setText(String.format(activity.getResources().getString(R.string.rupees_value_format_without_space), Utils.getMoneyDecimalFormat().format(subscriptionsActivityList.get(i).getAmount())));

                            }
                            tvPeriod2.setText(String.valueOf(subscriptionsActivityList.get(i).getDescription()));
                            if(subscriptionsActivityList.get(i).getDurationText()!=null){
                                tvDuration2.setText("/"+subscriptionsActivityList.get(i).getDurationText());
                            }

                        }
                    }

                    if(isUprgradeAndRenewList){
                        selectedPlanUpgradeRenew(rlPlan1,ivRadio1,0);
                    }else{
                        selectedPlanUpgradeRenew(rlPlan2,ivRadio2,1);

                    }
                }
            } else if(activity instanceof HomeActivity){
                cvStarPlans.setVisibility(View.GONE);
                String fareRs = activity.getString(R.string.rupees_value_format,
                        Utils.getDoubleTwoDigits((double) Math.round(fareToPay)));
                paySlider.tvSlide.setText("PAY " + fareRs);
                paySlider.sliderText.setText(R.string.swipe_right_to_pay);
                llRideInfo.setVisibility(View.VISIBLE);
                tvCashPaidValue.setText(fareRs);
                tvTotalFareValue.setText(activity.getString(R.string.rupees_value_format,
                        Utils.getDoubleTwoDigits((double) Math.round(totalFare))));
                textViewPaymentVia.setText(R.string.choose_payment_method);
            }

            linearLayoutOffers = (LinearLayout) rootView.findViewById(R.id.linearLayoutOffers);
            listViewOffers = (NonScrollListView) rootView.findViewById(R.id.listViewOffers);
            promoCouponsAdapter = new PromoCouponsAdapter(activity, R.layout.list_item_fresh_promo_coupon, promoCoupons, this);
            listViewOffers.setAdapter(promoCouponsAdapter);


            fetchWalletBalance();
            updateCouponsDataView();

        } catch (Exception e) {
            e.printStackTrace();
        }




        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        orderPaymentModes();
        setPaymentOptionUI();
    }

    private void setPlan(){
        if(Data.userData.getSubscriptionData().getSubscriptions() != null){
            for(int i=0; i<Data.userData.getSubscriptionData().getSubscriptions().size(); i++) {
                if (i == 0) {
                    rlPlan1.setVisibility(View.VISIBLE);
                    tvAmount1.setText(Data.userData.getSubscriptionData().getSubscriptions().get(i).getAmountText());
                    tvDuration1.setText("/"+Data.userData.getSubscriptionData().getSubscriptions().get(i).getDurationText());
                    tvPeriod1.setText(String.valueOf(Data.userData.getSubscriptionData().getSubscriptions().get(i).getDescription()));
                    if(Data.userData.getSubscriptionData().getSubscriptions().get(i).getIsDefault() == 1) {
                        selectedPlan(rlPlan1, ivRadio1, i);
                    }
                } else if (i == 1) {
                    rlPlan2.setVisibility(View.VISIBLE);
                    tvAmount2.setText(Data.userData.getSubscriptionData().getSubscriptions().get(i).getAmountText());
                    tvDuration2.setText("/"+Data.userData.getSubscriptionData().getSubscriptions().get(i).getDurationText());
                    tvPeriod2.setText(String.valueOf(Data.userData.getSubscriptionData().getSubscriptions().get(i).getDescription()));
                    if(Data.userData.getSubscriptionData().getSubscriptions().get(i).getIsDefault() == 1) {
                        selectedPlan(rlPlan2, ivRadio2, i);
                    }
                }
            }
        }
    }

    private void selectedPlan(RelativeLayout rlPlan, ImageView ivRadio, int subId){
        ivRadio1.setImageResource(R.drawable.ic_radio_button_normal);
        ivRadio2.setImageResource(R.drawable.ic_radio_button_normal);

        ivRadio.setImageResource(R.drawable.ic_radio_button_selected);
        subscription = Data.userData.getSubscriptionData().getSubscriptions().get(subId);
    }

    private void selectedPlanUpgradeRenew(RelativeLayout rlPlan, ImageView ivRadio, int subId){
        ivRadio1.setImageResource(R.drawable.ic_radio_button_normal);
        ivRadio2.setImageResource(R.drawable.ic_radio_button_normal);

        ivRadio.setImageResource(R.drawable.ic_radio_button_selected);
        subscription = subscriptionsActivityList.get(subId) ;
        purchaseType = subscriptionsActivityList.get(subId).getStarPurchaseType().getOrdinal();
    }



    private void updateCouponsDataView(){
        try {
            if(fromStar) {
                promoCoupons = Data.userData.getPromoCoupons();
                if (promoCoupons != null) {
                    if (promoCoupons.size() > 0) {
                        linearLayoutOffers.setVisibility(View.VISIBLE);
                    } else {
                        linearLayoutOffers.setVisibility(View.GONE);
                    }
                    promoCouponsAdapter.setList(promoCoupons);
                }
            } else {
                linearLayoutOffers.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private View.OnClickListener onClickListenerPaymentOptionSelector = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                switch (v.getId()){
                    case R.id.bPlaceOrder:


                        if(paymentOption==null || (paymentOption.getOrdinal() != 0 && paymentOption.getOrdinal() != 1)) {
                            placeOrder();
                            GAUtils.event(SIDE_MENU, JUGNOO+STAR+CHECKOUT, PAY_NOW+CLICKED);
                        } else{
                            paySlider.setSlideInitial();
                            Utils.showToast(activity,activity.getString(R.string.star_checkout_wallet_not_selected));
                        }
                        break;
                    case R.id.relativeLayoutPaytm:
                        MyApplication.getInstance().getWalletCore().paymentOptionSelectionAtFreshCheckout(activity, PaymentOption.PAYTM,
                                callbackPaymentOptionSelector);
                        GAUtils.event(SIDE_MENU, JUGNOO+STAR+CHECKOUT+WALLET+SELECTED, PAYTM);
                        break;

                    case R.id.relativeLayoutMobikwik:
                        MyApplication.getInstance().getWalletCore().paymentOptionSelectionAtFreshCheckout(activity, PaymentOption.MOBIKWIK,
                                callbackPaymentOptionSelector);
                        GAUtils.event(SIDE_MENU, JUGNOO+STAR+CHECKOUT+WALLET+SELECTED, MOBIKWIK);
                        break;

                    case R.id.relativeLayoutFreeCharge:
                        MyApplication.getInstance().getWalletCore().paymentOptionSelectionAtFreshCheckout(activity, PaymentOption.FREECHARGE,
                                callbackPaymentOptionSelector);
                        GAUtils.event(SIDE_MENU, JUGNOO+STAR+CHECKOUT+WALLET+SELECTED, FREECHARGE);
                        break;

                    case R.id.relativeLayoutCash:
                        MyApplication.getInstance().getWalletCore().paymentOptionSelectionAtFreshCheckout(activity, PaymentOption.CASH,
                                callbackPaymentOptionSelector);
                        GAUtils.event(SIDE_MENU, JUGNOO+STAR+CHECKOUT+WALLET+SELECTED, CASH);
                        break;

                    case R.id.rlPlan1:
                        if(activity instanceof JugnooStarActivity){
                            selectedPlan(rlPlan1, ivRadio1, 0);
                            try{GAUtils.event(SIDE_MENU, JUGNOO+STAR+PLAN+CLICKED, subscription.getPlanString());}catch(Exception e){}
                        } else{
                            selectedPlanUpgradeRenew(rlPlan1, ivRadio1, 0);
                        }
                        break;

                    case R.id.rlPlan2:
                        if(activity instanceof JugnooStarActivity){
                            selectedPlan(rlPlan2, ivRadio2, 1);
                            try{GAUtils.event(SIDE_MENU, JUGNOO+STAR+PLAN+CLICKED, subscription.getPlanString());}catch(Exception e){}
                        } else{
                            selectedPlanUpgradeRenew(rlPlan1, ivRadio2, 1);
                        }
                        break;

                    case R.id.rlUPI:
                        isRazorUPI = true;
                        callbackPaymentOptionSelector.onPaymentOptionSelected(PaymentOption.RAZOR_PAY);
                        break;
                    case R.id.rlOtherModesToPay:
                        isRazorUPI = false;
                        callbackPaymentOptionSelector.onPaymentOptionSelected(PaymentOption.RAZOR_PAY);
                        break;
                    case R.id.rlIciciUpi:
                        callbackPaymentOptionSelector.onPaymentOptionSelected(PaymentOption.ICICI_UPI);
                        break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void placeOrder() {
        if(paymentOption==null || paymentOption==PaymentOption.CASH)
        {
            Toast.makeText(activity, R.string.star_checkout_wallet_not_selected, Toast.LENGTH_SHORT).show();
            paySlider.setSlideInitial();
            return;
        }

        try {
            boolean goAhead = true;
            if (getPaymentOption() == PaymentOption.PAYTM) {
                if (Data.userData.getPaytmBalance() < getAmount()) {
                    if(Data.userData.getPaytmEnabled() == 0){
                        relativeLayoutPaytm.performClick();
                    } else if (Data.userData.getPaytmBalance() < 0) {
                        DialogPopup.alertPopup(activity, "", activity.getResources().getString(R.string.paytm_error_cash_select_cash));
                    } else {
                        showWalletBalanceLowDialog(PaymentOption.PAYTM);
                    }
                    goAhead = false;
                }
            }
            else if (getPaymentOption() == PaymentOption.MOBIKWIK) {
                if (Data.userData.getMobikwikBalance() < getAmount()) {
                    if(Data.userData.getMobikwikEnabled() == 0){
                        relativeLayoutMobikwik.performClick();
                    } else if (Data.userData.getMobikwikBalance() < 0) {
                        DialogPopup.alertPopup(activity, "", activity.getResources().getString(R.string.mobikwik_error_select_cash));
                    } else {
                        showWalletBalanceLowDialog(PaymentOption.MOBIKWIK);
                    }
                    goAhead = false;
                }
            }
            else if (getPaymentOption() == PaymentOption.FREECHARGE) {
                if (Data.userData.getFreeChargeBalance() < getAmount()) {
                    if(Data.userData.getFreeChargeEnabled() == 0){
                        relativeLayoutFreeCharge.performClick();
                    } else if (Data.userData.getFreeChargeBalance() < 0) {
                        DialogPopup.alertPopup(activity, "", activity.getResources().getString(R.string.freecharge_error_case_select_cash));
                    } else {
                        showWalletBalanceLowDialog(PaymentOption.FREECHARGE);
                    }
                    goAhead = false;
                }
            }

            if (goAhead) {
                if (getPaymentOption() == PaymentOption.ICICI_UPI && TextUtils.isEmpty(edtIciciVpa.getText().toString().trim())) {
                    Utils.showToast(activity, activity.getString(R.string.error_enter_virtual_payment_address));
                    paySlider.setSlideInitial();
                    return;
                }
                hitAPI();
            }else{
                paySlider.setSlideInitial();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showWalletBalanceLowDialog(final PaymentOption paymentOption) {
        try {
            FreshWalletBalanceLowDialog.Callback callback = new FreshWalletBalanceLowDialog.Callback() {
                @Override
                public void onRechargeNowClicked() {
                    intentToWallet(paymentOption);
                }

                @Override
                public void onPayByCashClicked() {
                }
            };
            if (paymentOption == PaymentOption.PAYTM && Data.userData.getPaytmEnabled() == 1) {
                String amount = com.sabkuchfresh.utils.Utils.getMoneyDecimalFormat().format(Math.ceil(Data.userData.getPaytmBalance() - Math.ceil(getAmount())));
                new FreshWalletBalanceLowDialog(activity, callback).show(R.string.dont_have_enough_paytm_balance, amount, R.drawable.ic_paytm_big);
            }
            else if (paymentOption == PaymentOption.MOBIKWIK && Data.userData.getMobikwikEnabled() == 1) {
                String amount = com.sabkuchfresh.utils.Utils.getMoneyDecimalFormat().format(Math.ceil(Data.userData.getMobikwikBalance() - Math.ceil(getAmount())));
                new FreshWalletBalanceLowDialog(activity, callback).show(R.string.dont_have_enough_mobikwik_balance, amount, R.drawable.ic_mobikwik_big);
            }
            else if (paymentOption == PaymentOption.FREECHARGE && Data.userData.getFreeChargeEnabled() == 1) {
                String amount = com.sabkuchfresh.utils.Utils.getMoneyDecimalFormat().format(Math.ceil(Data.userData.getFreeChargeBalance() - Math.ceil(getAmount())));
                new FreshWalletBalanceLowDialog(activity, callback).show(R.string.dont_have_enough_freecharge_balance, amount, R.drawable.ic_freecharge_big);
            }
            else {
                intentToWallet(paymentOption);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void intentToWallet(PaymentOption paymentOption) {
        try {
            DecimalFormat df = new DecimalFormat("#");
            Intent intent = new Intent(activity, PaymentActivity.class);
            intent.putExtra(Constants.KEY_WALLET_TYPE, paymentOption.getOrdinal());
            if (paymentOption == PaymentOption.PAYTM) {
                intent.putExtra(Constants.KEY_PAYMENT_ACTIVITY_PATH,
                        (Data.userData.getPaytmEnabled() == 1)? PaymentActivityPath.WALLET_ADD_MONEY.getOrdinal()
                                : PaymentActivityPath.ADD_WALLET.getOrdinal());
                intent.putExtra(Constants.KEY_PAYMENT_RECHARGE_VALUE,
                        df.format(Math.ceil(getAmount()
                                - Data.userData.getPaytmBalance())));
            }
            else if (paymentOption == PaymentOption.MOBIKWIK) {
                intent.putExtra(Constants.KEY_PAYMENT_ACTIVITY_PATH,
                        (Data.userData.getMobikwikEnabled() == 1)? PaymentActivityPath.WALLET_ADD_MONEY.getOrdinal()
                                : PaymentActivityPath.ADD_WALLET.getOrdinal());
                intent.putExtra(Constants.KEY_PAYMENT_RECHARGE_VALUE,
                        df.format(Math.ceil(getAmount()
                                - Data.userData.getMobikwikBalance())));
            }
            else if (paymentOption == PaymentOption.FREECHARGE) {
                intent.putExtra(Constants.KEY_PAYMENT_ACTIVITY_PATH,
                        (Data.userData.getFreeChargeEnabled() == 1)? PaymentActivityPath.WALLET_ADD_MONEY.getOrdinal()
                                : PaymentActivityPath.ADD_WALLET.getOrdinal());
                intent.putExtra(Constants.KEY_PAYMENT_RECHARGE_VALUE,
                        df.format(Math.ceil(getAmount()
                                - Data.userData.getFreeChargeBalance())));
            }
            else {
                intent.putExtra(Constants.KEY_PAYMENT_ACTIVITY_PATH, PaymentActivityPath.WALLET.getOrdinal());
            }
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PaymentOption getPaymentOption() {
        if(paymentOption == null){
            return PaymentOption.CASH;
        }
        return paymentOption;
    }

    public void setPaymentOption(PaymentOption paymentOption) {
           this.paymentOption = paymentOption;
    }

    private ApiFetchWalletBalance apiFetchWalletBalance = null;
    private void fetchWalletBalance() {
        try {
            if(apiFetchWalletBalance == null){
                apiFetchWalletBalance = new ApiFetchWalletBalance(activity, new ApiFetchWalletBalance.Callback() {
                    @Override
                    public void onSuccess() {
                        try {
//                            setPaymentOption(getPaymentOption());
                            orderPaymentModes();
                            setPaymentOptionUI();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure() {
                        try {
//                            setPaymentOption(getPaymentOption());
                            orderPaymentModes();
                            setPaymentOptionUI();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFinish() {
                    }

                    @Override
                    public void onRetry(View view) {
                    }

                    @Override
                    public void onNoRetry(View view) {
                    }
                });
            }
            apiFetchWalletBalance.getBalance(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void orderPaymentModes(){
        try{
            ArrayList<PaymentModeConfigData> paymentModeConfigDatas = MyApplication.getInstance().getWalletCore().getPaymentModeConfigDatas();
            if(paymentModeConfigDatas != null && paymentModeConfigDatas.size() > 0){
                linearLayoutWalletContainer.removeAllViews();
                for(PaymentModeConfigData paymentModeConfigData : paymentModeConfigDatas){
                    if(paymentModeConfigData.getEnabled() == 1) {
                        if (paymentModeConfigData.getPaymentOption() == PaymentOption.PAYTM.getOrdinal()) {
                            linearLayoutWalletContainer.addView(relativeLayoutPaytm);
                        } else if (paymentModeConfigData.getPaymentOption() == PaymentOption.MOBIKWIK.getOrdinal()) {
                            linearLayoutWalletContainer.addView(relativeLayoutMobikwik);
                        } else if (paymentModeConfigData.getPaymentOption() == PaymentOption.FREECHARGE.getOrdinal()) {
                            linearLayoutWalletContainer.addView(relativeLayoutFreeCharge);
                        }
                        else if(!fromStar){
                            if (paymentModeConfigData.getPaymentOption() == PaymentOption.RAZOR_PAY.getOrdinal()) {
                                linearLayoutWalletContainer.addView(rlOtherModesToPay);
                                tvOtherModesToPay.setText(paymentModeConfigData.getDisplayName());
                            } else if (paymentModeConfigData.getPaymentOption() == PaymentOption.UPI_RAZOR_PAY.getOrdinal()) {
                                linearLayoutWalletContainer.addView(rlUPI);
                                tvUPI.setText(paymentModeConfigData.getDisplayName());
                            } else if (paymentModeConfigData.getPaymentOption() == PaymentOption.ICICI_UPI.getOrdinal()) {
                                linearLayoutWalletContainer.addView(relativeLayoutIcici);
                                edtIciciVpa.removeTextChangedListener(selectIciciPaymentTextWatcher);
                                edtIciciVpa.setText(paymentModeConfigData.getUpiHandle());
                                if (paymentModeConfigData.getUpiHandle() != null && paymentModeConfigData.getUpiHandle().length() > 0) {
                                    edtIciciVpa.setSelection(paymentModeConfigData.getUpiHandle().length() - 1);

                                }
                                edtIciciVpa.addTextChangedListener(selectIciciPaymentTextWatcher);
                                jugnooVpaHandle = paymentModeConfigData.getJugnooVpaHandle();
                                tvLabelIciciUpi.setText(activity.getString(R.string.label_below_icici_payment_edt, jugnooVpaHandle));
                                tvUPICashback.setText(!TextUtils.isEmpty(paymentModeConfigData.getUpiCashbackValue()) ? paymentModeConfigData.getUpiCashbackValue() : "");
                            }
                        }
                    }
                }
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private CallbackPaymentOptionSelector callbackPaymentOptionSelector = new CallbackPaymentOptionSelector() {
        @Override
        public void onPaymentOptionSelected(PaymentOption paymentOption) {
            setPaymentOption(paymentOption);
            setPaymentOptionUI();
        }

        @Override
        public void onWalletAdd(PaymentOption paymentOption) {
        }

        @Override
        public String getAmountToPrefill() {
            return "";
        }
    };

    private void setPaymentOptionUI() {
        try {
            setPaymentOption(MyApplication.getInstance().getWalletCore().getPaymentOptionFromInt(
                    MyApplication.getInstance().getWalletCore().getPaymentOptionAccAvailability(getPaymentOption().getOrdinal())));

            textViewPaytmValue.setText(String.format(activity.getResources()
                    .getString(R.string.rupees_value_format_without_space), Data.userData.getPaytmBalanceStr()));
            textViewPaytmValue.setTextColor(Data.userData.getPaytmBalanceColor(activity));
            textViewMobikwikValue.setText(String.format(activity.getResources()
                    .getString(R.string.rupees_value_format_without_space), Data.userData.getMobikwikBalanceStr()));
            textViewMobikwikValue.setTextColor(Data.userData.getMobikwikBalanceColor(activity));
            textViewFreeChargeValue.setText(String.format(activity.getResources()
                    .getString(R.string.rupees_value_format_without_space), Data.userData.getFreeChargeBalanceStr()));
            textViewFreeChargeValue.setTextColor(Data.userData.getFreeChargeBalanceColor(activity));

            if(Data.userData.getPaytmEnabled() == 1){
                textViewPaytmValue.setVisibility(View.VISIBLE);
                imageViewAddPaytm.setVisibility(View.GONE);
            } else{
                textViewPaytmValue.setVisibility(View.GONE);
                imageViewAddPaytm.setVisibility(View.VISIBLE);
            }
            if(Data.userData.getMobikwikEnabled() == 1){
                textViewMobikwikValue.setVisibility(View.VISIBLE);
                imageViewAddMobikwik.setVisibility(View.GONE);
            } else{
                textViewMobikwikValue.setVisibility(View.GONE);
                imageViewAddMobikwik.setVisibility(View.VISIBLE);
            }
            if(Data.userData.getFreeChargeEnabled() == 1){
                textViewFreeChargeValue.setVisibility(View.VISIBLE);
                imageViewAddFreeCharge.setVisibility(View.GONE);
            } else{
                textViewFreeChargeValue.setVisibility(View.GONE);
                imageViewAddFreeCharge.setVisibility(View.VISIBLE);
            }

            imageViewPaytmRadio.setImageResource(R.drawable.ic_radio_button_normal);
            imageViewRadioMobikwik.setImageResource(R.drawable.ic_radio_button_normal);
            imageViewRadioFreeCharge.setImageResource(R.drawable.ic_radio_button_normal);
            ivOtherModesToPay.setImageResource(R.drawable.ic_radio_button_normal);
            ivUPI.setImageResource(R.drawable.ic_radio_button_normal);
            imageViewIcici.setImageResource(R.drawable.ic_radio_button_normal);

            if (getPaymentOption() == PaymentOption.PAYTM) {
                imageViewPaytmRadio.setImageResource(R.drawable.ic_radio_button_selected);
            } else if (getPaymentOption() == PaymentOption.MOBIKWIK) {
                imageViewRadioMobikwik.setImageResource(R.drawable.ic_radio_button_selected);
            } else if (getPaymentOption() == PaymentOption.FREECHARGE) {
                imageViewRadioFreeCharge.setImageResource(R.drawable.ic_radio_button_selected);
            } else if(getPaymentOption() == PaymentOption.RAZOR_PAY){
                if(isRazorUPI){
                    ivUPI.setImageResource(R.drawable.ic_radio_button_selected);
                } else {
                    ivOtherModesToPay.setImageResource(R.drawable.ic_radio_button_selected);
                }
            } else if (getPaymentOption() == PaymentOption.ICICI_UPI) {
                imageViewIcici.setImageResource(R.drawable.ic_radio_button_selected);
            }
            edtIciciVpa.setVisibility(getPaymentOption() == PaymentOption.ICICI_UPI?View.VISIBLE:View.GONE);
            tvLabelIciciUpi.setVisibility(getPaymentOption() == PaymentOption.ICICI_UPI?View.VISIBLE:View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onCouponSelected() {

    }

    @Override
    public PromoCoupon getSelectedCoupon() {
        return getSelectedPromoCoupon();
    }

    @Override
    public boolean setSelectedCoupon(int position) {
        PromoCoupon promoCoupon;
        if (promoCoupons != null && position > -1 && position < promoCoupons.size()) {
            promoCoupon = promoCoupons.get(position);
        } else {
            promoCoupon = noSelectionCoupon;
        }
        if (MyApplication.getInstance().getWalletCore().displayAlertAndCheckForSelectedWalletCoupon(activity, getPaymentOption().getOrdinal(), promoCoupon)) {
            setSelectedPromoCoupon(promoCoupon);
            GAUtils.event(SIDE_MENU, JUGNOO+STAR+CHECKOUT+OFFER+SELECTED, promoCoupon.getTitle());
            return true;
        } else {
            return false;
        }
    }

    private void apiPurchaseSubscription() {
        if (MyApplication.getInstance().isOnline()) {
            DialogPopup.showLoadingDialog(activity, getResources().getString(R.string.loading));
            HashMap<String, String> params = new HashMap<>();
            params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
            params.put(Constants.KEY_SUB_ID, String.valueOf(subscription.getSubId()));
            params.put(Constants.KEY_PAYMENT_PREFERENCE, String.valueOf(getPaymentOption().getOrdinal()));
            params.put(Constants.KEY_LATITUDE, String.valueOf(Data.latitude));
            params.put(Constants.KEY_LONGITUDE, String.valueOf(Data.longitude));
            if(getSelectedPromoCoupon() != null && getSelectedPromoCoupon().getId() > -1){
                if(getSelectedPromoCoupon() instanceof CouponInfo){
                    params.put(Constants.KEY_ACCOUNT_ID, String.valueOf(getSelectedPromoCoupon().getId()));
                } else if(getSelectedPromoCoupon() instanceof PromotionInfo){
                    params.put(Constants.KEY_ORDER_OFFER_ID, String.valueOf(getSelectedPromoCoupon().getId()));
                }
                params.put(Constants.KEY_MASTER_COUPON, String.valueOf(getSelectedPromoCoupon().getMasterCoupon()));
            }

            new HomeUtil().putDefaultParams(params);
            RestClient.getApiService().purchaseSubscription(params, new retrofit.Callback<PurchaseSubscriptionResponse>() {
                @Override
                public void success(final PurchaseSubscriptionResponse purchaseSubscriptionResponse, Response response) {
                    DialogPopup.dismissLoadingDialog();
                    String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                    Log.i("cancel Subscription response = ", "" + responseStr);
                    try {
                        JSONObject jObj = new JSONObject(responseStr);
                        int flag = jObj.optInt(Constants.KEY_FLAG, ApiResponseFlags.ACTION_COMPLETE.getOrdinal());
                        String message = JSONParser.getServerMessage(jObj);
                        if (flag == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
                            if(jObj.has(Constants.KEY_RAZORPAY_PAYMENT_OBJECT)){
                                // razor pay case send data to RazorPay Checkout page
                                activity.setPurchaseSubscriptionResponse(purchaseSubscriptionResponse.getOrderId(),
                                        purchaseSubscriptionResponse.getRazorPaymentObject().getAuthOrderId());
                                activity.startRazorPayPayment(jObj.getJSONObject(Constants.KEY_RAZORPAY_PAYMENT_OBJECT), isRazorUPI);
                            } else {
                                DialogPopup.alertPopupWithListener(activity, "", message, getResources().getString(R.string.ok), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Data.userData.getSubscriptionData().setUserSubscriptions(purchaseSubscriptionResponse.getUserSubscriptions());
                                        Data.autoData.setCancellationChargesPopupTextLine1(purchaseSubscriptionResponse.getCancellationChargesPopupTextLine1());
                                        Data.autoData.setCancellationChargesPopupTextLine2(purchaseSubscriptionResponse.getCancellationChargesPopupTextLine2());
                                        activity.finish();
                                        activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
                                    }
                                }, false);
                                Prefs.with(activity).save(SPLabels.CHECK_BALANCE_LAST_TIME,
                                        0L);
                                //DialogPopup.alertPopup(JugnooStarSubscribedActivity.this, "", message);
                            }
                        }
                        else{
                            paySlider.setSlideInitial();

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        paySlider.setSlideInitial();
                        retryDialog(DialogErrorType.SERVER_ERROR);
                    }
                    DialogPopup.dismissLoadingDialog();
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e("customerFetchUserAddress error=", "" + error.toString());
                    paySlider.setSlideInitial();
                    DialogPopup.dismissLoadingDialog();
                    retryDialog(DialogErrorType.CONNECTION_LOST);
                }
            });

        } else {
            paySlider.setSlideInitial();
            retryDialog(DialogErrorType.NO_NET);
        }
    }

    private void retryDialog(DialogErrorType dialogErrorType) {
        DialogPopup.dialogNoInternet(activity,
                dialogErrorType,
                new Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        hitAPI();
                    }

                    @Override
                    public void neutralClick(View view) {

                    }

                    @Override
                    public void negativeClick(View view) {

                    }
                });
    }

    private void hitAPI() {
        if(paySlider.isSliderInIntialStage())
            paySlider.fullAnimate();
        if(fromStar) {
            if(purchaseType == StarPurchaseType.RENEW.getOrdinal()) {
                apiRenewSubscription();
            } else if(purchaseType == StarPurchaseType.UPGRADE.getOrdinal()){
                apiUpgradeSubscription();
            } else{
                apiPurchaseSubscription();
            }
        } else {
            initiateRideEndPaymentAPI();
        }
    }

    private void apiUpgradeSubscription() {
        if (MyApplication.getInstance().isOnline()) {
            DialogPopup.showLoadingDialog(activity, getResources().getString(R.string.loading));
            HashMap<String, String> params = new HashMap<>();
            params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
            params.put(Constants.KEY_SUB_ID, String.valueOf(subscription.getSubId()));
            params.put(Constants.KEY_PAYMENT_PREFERENCE, String.valueOf(getPaymentOption().getOrdinal()));
            params.put(Constants.KEY_LATITUDE, String.valueOf(Data.latitude));
            params.put(Constants.KEY_LONGITUDE, String.valueOf(Data.longitude));
            if(getSelectedPromoCoupon() != null && getSelectedPromoCoupon().getId() > -1){
                if(getSelectedPromoCoupon() instanceof CouponInfo){
                    params.put(Constants.KEY_ACCOUNT_ID, String.valueOf(getSelectedPromoCoupon().getId()));
                } else if(getSelectedPromoCoupon() instanceof PromotionInfo){
                    params.put(Constants.KEY_ORDER_OFFER_ID, String.valueOf(getSelectedPromoCoupon().getId()));
                }
                params.put(Constants.KEY_MASTER_COUPON, String.valueOf(getSelectedPromoCoupon().getMasterCoupon()));
            }

            new HomeUtil().putDefaultParams(params);
            RestClient.getApiService().upgradeSubscription(params, new retrofit.Callback<PurchaseSubscriptionResponse>() {
                @Override
                public void success(final PurchaseSubscriptionResponse purchaseSubscriptionResponse, Response response) {
                    DialogPopup.dismissLoadingDialog();
                    String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                    Log.i("cancel Subscription response = ", "" + responseStr);
                    try {
                        JSONObject jObj = new JSONObject(responseStr);
                        int flag = jObj.optInt(Constants.KEY_FLAG, ApiResponseFlags.ACTION_COMPLETE.getOrdinal());
                        String message = JSONParser.getServerMessage(jObj);
                        if (flag == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {

                            DialogPopup.alertPopupWithListener(activity, "", message, getResources().getString(R.string.ok), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Data.userData.getSubscriptionData().setUserSubscriptions(purchaseSubscriptionResponse.getUserSubscriptions());
                                    Data.autoData.setCancellationChargesPopupTextLine1(purchaseSubscriptionResponse.getCancellationChargesPopupTextLine1());
                                    Data.autoData.setCancellationChargesPopupTextLine2(purchaseSubscriptionResponse.getCancellationChargesPopupTextLine2());
                                    activity.finish();
                                    activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
                                }
                            }, false);
                            Prefs.with(activity).save(SPLabels.CHECK_BALANCE_LAST_TIME,
                                    0l);
                        } else{
                            paySlider.setSlideInitial();

                            DialogPopup.alertPopup(activity, "", message);
                        }
                    } catch (Exception e) {
                        paySlider.setSlideInitial();

                        e.printStackTrace();
                        retryDialog(DialogErrorType.SERVER_ERROR);
                    }
                    DialogPopup.dismissLoadingDialog();
                }

                @Override
                public void failure(RetrofitError error) {
                    paySlider.setSlideInitial();
                    Log.e("customerFetchUserAddress error=", "" + error.toString());
                    DialogPopup.dismissLoadingDialog();
                    retryDialog(DialogErrorType.CONNECTION_LOST);
                }
            });

        } else {
            paySlider.setSlideInitial();
            retryDialog(DialogErrorType.NO_NET);
        }
    }

    private void apiRenewSubscription() {
        if (MyApplication.getInstance().isOnline()) {
            DialogPopup.showLoadingDialog(activity, getResources().getString(R.string.loading));
            HashMap<String, String> params = new HashMap<>();
            params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
            params.put(Constants.KEY_SUB_ID, String.valueOf(subscription.getSubId()));
            params.put(Constants.KEY_PAYMENT_PREFERENCE, String.valueOf(getPaymentOption().getOrdinal()));
            params.put(Constants.KEY_LATITUDE, String.valueOf(Data.latitude));
            params.put(Constants.KEY_LONGITUDE, String.valueOf(Data.longitude));
            if(getSelectedPromoCoupon() != null && getSelectedPromoCoupon().getId() > -1){
                if(getSelectedPromoCoupon() instanceof CouponInfo){
                    params.put(Constants.KEY_ACCOUNT_ID, String.valueOf(getSelectedPromoCoupon().getId()));
                } else if(getSelectedPromoCoupon() instanceof PromotionInfo){
                    params.put(Constants.KEY_ORDER_OFFER_ID, String.valueOf(getSelectedPromoCoupon().getId()));
                }
                params.put(Constants.KEY_MASTER_COUPON, String.valueOf(getSelectedPromoCoupon().getMasterCoupon()));
            }

            new HomeUtil().putDefaultParams(params);
            RestClient.getApiService().renewSubscription(params, new retrofit.Callback<PurchaseSubscriptionResponse>() {
                @Override
                public void success(final PurchaseSubscriptionResponse purchaseSubscriptionResponse, Response response) {

                    DialogPopup.dismissLoadingDialog();
                    String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                    Log.i("cancel Subscription response = ", "" + responseStr);
                    try {
                        JSONObject jObj = new JSONObject(responseStr);
                        int flag = jObj.optInt(Constants.KEY_FLAG, ApiResponseFlags.ACTION_COMPLETE.getOrdinal());
                        String message = JSONParser.getServerMessage(jObj);
                        if (flag == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
                            DialogPopup.alertPopupWithListener(activity, "", message, getResources().getString(R.string.ok), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Data.userData.getSubscriptionData().setUserSubscriptions(purchaseSubscriptionResponse.getUserSubscriptions());
                                    Data.autoData.setCancellationChargesPopupTextLine1(purchaseSubscriptionResponse.getCancellationChargesPopupTextLine1());
                                    Data.autoData.setCancellationChargesPopupTextLine2(purchaseSubscriptionResponse.getCancellationChargesPopupTextLine2());
                                    activity.finish();
                                    activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
                                }
                            }, false);
                            Prefs.with(activity).save(SPLabels.CHECK_BALANCE_LAST_TIME,
                                    0l);
                        } else{
                            paySlider.setSlideInitial();
                            DialogPopup.alertPopup(activity, "", message);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        paySlider.setSlideInitial();;
                        retryDialog(DialogErrorType.SERVER_ERROR);

                    }
                    DialogPopup.dismissLoadingDialog();
                }

                @Override
                public void failure(RetrofitError error) {
                    paySlider.setSlideInitial();
                    Log.e("customerFetchUserAddress error=", "" + error.toString());
                    DialogPopup.dismissLoadingDialog();
                    retryDialog(DialogErrorType.CONNECTION_LOST);
                }
            });

        } else {
            paySlider.setSlideInitial();
            retryDialog(DialogErrorType.NO_NET);
        }
    }

    private PromoCoupon selectedPromoCoupon;
    private PromoCoupon noSelectionCoupon = new CouponInfo(-1, "Don't apply coupon on this ride");
    public PromoCoupon getSelectedPromoCoupon() {
        return selectedPromoCoupon;
    }

    public void setSelectedPromoCoupon(PromoCoupon selectedPromoCoupon) {
        this.selectedPromoCoupon = selectedPromoCoupon;
    }

    TextWatcher selectIciciPaymentTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(getPaymentOption()==null || getPaymentOption()!= PaymentOption.ICICI_UPI)
                callbackPaymentOptionSelector.onPaymentOptionSelected(PaymentOption.ICICI_UPI);
        }
    };

    public void initiateRideEndPaymentAPI() {

        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.KEY_ENGAGEMENT_ID, String.valueOf(engagementId));
        params.put(Constants.KEY_PREFERRED_PAYMENT_MODE, String.valueOf(getPaymentOption().getOrdinal()));

        new ApiCommon<PaymentResponse>(activity).showLoader(true).execute(params, ApiName.INITIATE_RIDE_END_PAYMENT,
                new APICommonCallback<PaymentResponse>() {
                    @Override
                    public boolean onNotConnected() {
                        paySlider.setSlideInitial();
                        return false;
                    }

                    @Override
                    public boolean onException(Exception e) {
                        paySlider.setSlideInitial();
                        return false;

                    }

                    @Override
                    public void onSuccess(final PaymentResponse response, String message, int flag) {
                        try {
                            if(getPaymentOption() == PaymentOption.RAZOR_PAY){
                                // razor pay case send data to RazorPay Checkout page
                                JSONObject jObj = new JSONObject();
                                activity.setPurchaseSubscriptionResponse(response.getData().getEngagementId(),
                                        response.getData().getEngagementId());
                                activity.startRazorPayPayment(jObj.getJSONObject(Constants.KEY_RAZORPAY_PAYMENT_OBJECT), isRazorUPI);
                            } else {
                                if(Data.autoData != null && Data.autoData.getEndRideData() != null){
                                    Data.autoData.getEndRideData().setShowPaymentOptions(0);
                                }
                                DialogPopup.alertPopupWithListener(activity, "", message, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        activity.onBackPressed();
                                    }
                                });
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public boolean onError(PaymentResponse feedCommonResponse, String message, int flag) {
                        paySlider.setSlideInitial();
                        return false;
                    }

                    @Override
                    public boolean onFailure(RetrofitError error) {
                        paySlider.setSlideInitial();
                        return false;
                    }

                    @Override
                    public void onNegativeClick() {
                        paySlider.setSlideInitial();

                    }
                });
    }
    
    private Integer getAmount(){
        if(fromStar){
            return subscription.getAmount();
        } else {
            return (int)fareToPay;
        }
    }


}

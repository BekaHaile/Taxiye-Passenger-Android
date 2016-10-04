package com.sabkuchfresh.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.analytics.ecommerce.ProductAction;
import com.sabkuchfresh.analytics.FlurryEventLogger;
import com.sabkuchfresh.analytics.FlurryEventNames;
import com.sabkuchfresh.analytics.NudgeClient;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.home.FreshOrderCompleteDialog;
import com.sabkuchfresh.home.FreshWalletBalanceLowDialog;
import com.sabkuchfresh.retrofit.model.Category;
import com.sabkuchfresh.retrofit.model.PlaceOrderResponse;
import com.sabkuchfresh.retrofit.model.SubItem;
import com.sabkuchfresh.utils.AppConstant;
import com.sabkuchfresh.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.Events;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.apis.ApiFetchWalletBalance;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.CouponInfo;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.datastructure.ProductType;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.datastructure.PromotionInfo;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.home.dialogs.PromoCouponsDialog;
import product.clicklabs.jugnoo.promotion.ShareActivity;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FirebaseEvents;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.wallet.PaymentActivity;
import product.clicklabs.jugnoo.wallet.UserDebtDialog;
import product.clicklabs.jugnoo.wallet.models.PaymentActivityPath;
import product.clicklabs.jugnoo.wallet.models.PaymentModeConfigData;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class FreshPaymentFragment extends Fragment implements FlurryEventNames {

    private final String TAG = FreshPaymentFragment.class.getSimpleName();
    private LinearLayout linearLayoutRoot;
//	private TextView textViewPayForItems, textViewPayFromjc;

    RelativeLayout relativeLayoutSubTotalLayout, relativeLayoutJC, relativeLayoutPromo, relativeLayoutTotalLayout, relativeLayoutdelivery;
    TextView textViewTotalValue, textViewJCValue, textViewPromoValue, textViewPayableValue, textpaymentoption,
            textViewSubTotal, textViewSubTotalValue, textViewdelivery, textViewdeliveryValue;
    private View viewLine;


    private RelativeLayout relativeLayoutPaytm, relativeLayoutMobikwik, relativeLayoutFreeCharge, relativeLayoutCash;
    private LinearLayout linearLayoutWalletContainer;
    private ImageView imageViewPaytmRadio, imageViewRadioMobikwik, imageViewRadioFreeCharge, imageViewCashRadio;
    private TextView textViewPaytm, textViewPaytmValue, textViewMobikwik, textViewMobikwikValue,
            textViewFreeCharge, textViewFreeChargeValue;
    private Button buttonPlaceOrder;
    private RelativeLayout relativeLayoutOffers, relativeLayoutSelectedCoupon;
    private TextView textViewOffers, textViewSelectedOffer;


    private ArrayList<PromoCoupon> promoCoupons = new ArrayList<>();

    private View rootView;
    private FreshActivity activity;
    private boolean scrolled = false;
    private ScrollView scrollView;
    private TextView textViewScroll;

    // for payment screen
    private double subTotalAmount = 0;
    private double promoAmount = 0;
    private double deliveryAmount = 0;
    private double totalAmount = 0;
    private double jcAmount = 0;
    private double payableAmount = 0;

    private String promoCode = "";

    public FreshPaymentFragment() {
    }

    private List<Product> productList = new ArrayList<>();
    private PromoCoupon noSelectionCoupon = new CouponInfo(-1, "Don't apply coupon on this ride");

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(activity).registerReceiver(broadcastReceiverWalletUpdate,
                new IntentFilter(Constants.INTENT_ACTION_WALLET_UPDATE));
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_fresh_payment, container, false);


        activity = (FreshActivity) getActivity();
        activity.fragmentUISetup(this);

        linearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.linearLayoutRoot);
        try {
            if (linearLayoutRoot != null) {
                new ASSL(activity, linearLayoutRoot, 1134, 720, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//		textViewPayForItems = (TextView)rootView.findViewById(R.id.textViewPayForItems); textViewPayForItems.setTypeface(Fonts.mavenRegular(activity));
//        textViewPayFromjc = (TextView)rootView.findViewById(R.id.textViewPayFromjc); textViewPayFromjc.setTypeface(Fonts.mavenRegular(activity));
        ((TextView) rootView.findViewById(R.id.textViewCash)).setTypeface(Fonts.mavenLight(activity));

        viewLine = (View)rootView.findViewById(R.id.view_line);

        textViewTotalValue = (TextView) rootView.findViewById(R.id.textViewTotalValue);
        textViewTotalValue.setTypeface(Fonts.mavenRegular(activity));

        textViewJCValue = (TextView) rootView.findViewById(R.id.textViewJCValue);
        textViewJCValue.setTypeface(Fonts.mavenRegular(activity));

        textViewPromoValue = (TextView) rootView.findViewById(R.id.textViewPromoValue);
        textViewPromoValue.setTypeface(Fonts.mavenRegular(activity));

        textViewPayableValue = (TextView) rootView.findViewById(R.id.textViewPayableValue);
        textViewPayableValue.setTypeface(Fonts.mavenRegular(activity));

//        textViewAddPromo = (TextView) rootView.findViewById(textViewAddPromo);
//        textViewAddPromo.setTypeface(Fonts.mavenRegular(activity));

        textViewSubTotalValue = (TextView) rootView.findViewById(R.id.textViewSubTotalValue);
        textViewSubTotalValue.setTypeface(Fonts.mavenRegular(activity));

        textViewdeliveryValue = (TextView) rootView.findViewById(R.id.textViewdeliveryValue);
        textViewdeliveryValue.setTypeface(Fonts.mavenRegular(activity));

        ((TextView) rootView.findViewById(R.id.textViewSubTotal)).setTypeface(Fonts.mavenLight(activity));
        ((TextView) rootView.findViewById(R.id.textViewdelivery)).setTypeface(Fonts.mavenLight(activity));

        ((TextView) rootView.findViewById(R.id.textViewTotal)).setTypeface(Fonts.mavenLight(activity));
        ((TextView) rootView.findViewById(R.id.textViewJC)).setTypeface(Fonts.mavenLight(activity));
        ((TextView) rootView.findViewById(R.id.textViewPromo)).setTypeface(Fonts.mavenLight(activity));
        ((TextView) rootView.findViewById(R.id.textViewPayable)).setTypeface(Fonts.mavenLight(activity));
        textpaymentoption = ((TextView) rootView.findViewById(R.id.textViewPaymentOption));
        textpaymentoption.setTypeface(Fonts.mavenRegular(activity));




        relativeLayoutSubTotalLayout = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutSubTotalLayout);
        relativeLayoutJC = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutJC);
        relativeLayoutPromo = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutPromo);
        relativeLayoutTotalLayout = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutTotalLayout);
        relativeLayoutdelivery = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutdelivery);

        linearLayoutWalletContainer = (LinearLayout) rootView.findViewById(R.id.linearLayoutWalletContainer);
        relativeLayoutPaytm = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutPaytm);
        relativeLayoutMobikwik = (RelativeLayout)rootView.findViewById(R.id.relativeLayoutMobikwik);
        relativeLayoutFreeCharge = (RelativeLayout)rootView.findViewById(R.id.relativeLayoutFreeCharge);
        relativeLayoutCash = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutCash);
        imageViewPaytmRadio = (ImageView) rootView.findViewById(R.id.imageViewPaytmRadio);
        imageViewRadioMobikwik = (ImageView)rootView.findViewById(R.id.imageViewRadioMobikwik);
        imageViewRadioFreeCharge = (ImageView)rootView.findViewById(R.id.imageViewRadioFreeCharge);
        imageViewCashRadio = (ImageView) rootView.findViewById(R.id.imageViewCashRadio);

        textViewPaytmValue = (TextView)rootView.findViewById(R.id.textViewPaytmValue);textViewPaytmValue.setTypeface(Fonts.mavenLight(activity));
        textViewPaytm = (TextView)rootView.findViewById(R.id.textViewPaytm); textViewPaytm.setTypeface(Fonts.mavenLight(activity));
        textViewMobikwikValue = (TextView)rootView.findViewById(R.id.textViewMobikwikValue);textViewMobikwikValue.setTypeface(Fonts.mavenLight(activity));
        textViewMobikwik = (TextView)rootView.findViewById(R.id.textViewMobikwik); textViewMobikwik.setTypeface(Fonts.mavenLight(activity));
        textViewFreeChargeValue = (TextView)rootView.findViewById(R.id.textViewFreeChargeValue);textViewFreeChargeValue.setTypeface(Fonts.mavenLight(activity));
        textViewFreeCharge = (TextView)rootView.findViewById(R.id.textViewFreeCharge); textViewFreeCharge.setTypeface(Fonts.mavenLight(activity));
        ((TextView)rootView.findViewById(R.id.textViewCash)).setTypeface(Fonts.mavenLight(activity));

        buttonPlaceOrder = (Button) rootView.findViewById(R.id.buttonPlaceOrder);
        buttonPlaceOrder.setTypeface(Fonts.mavenRegular(activity));

//        appPromoEdittext.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                applyButton.setEnabled(true);
//                if(s.length()==0)
//                    applyButton.setBackgroundColor(activity.getResources().getColor(R.color.apply_btn_normal));
//                else
//                    applyButton.setBackgroundColor(activity.getResources().getColor(R.color.theme_color));
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
//
//        applyButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!TextUtils.isEmpty(appPromoEdittext.getText().toString().trim())) {
//                    applyPromo();
//                    int appType = Prefs.with(activity).getInt(Constants.APP_TYPE, Data.AppType);
//                    if(appType == AppConstant.ApplicationType.MEALS){
//                        MyApplication.getInstance().logEvent(FirebaseEvents.M_PAY+"_"+FirebaseEvents.ADD_PROMO, null);
//                    }else{
//                        MyApplication.getInstance().logEvent(FirebaseEvents.F_PAY+"_"+FirebaseEvents.ADD_PROMO, null);
//                    }
//                }
//                else {
//                    Toast.makeText(activity, "Please enter code", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
        relativeLayoutCash.setOnClickListener(onClickListenerPaymentOptionSelector);
        relativeLayoutPaytm.setOnClickListener(onClickListenerPaymentOptionSelector);
        relativeLayoutMobikwik.setOnClickListener(onClickListenerPaymentOptionSelector);
        relativeLayoutFreeCharge.setOnClickListener(onClickListenerPaymentOptionSelector);

        relativeLayoutOffers = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutOffers);
        textViewOffers = (TextView)rootView.findViewById(R.id.textViewOffers); textViewOffers.setTypeface(Fonts.mavenMedium(activity));
        relativeLayoutSelectedCoupon = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutSelectedCoupon);
        textViewSelectedOffer = (TextView)rootView.findViewById(R.id.textViewSelectedOffer); textViewSelectedOffer.setTypeface(Fonts.mavenRegular(activity));
        activity.setSelectedPromoCoupon(noSelectionCoupon);

        String lastClientId = Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId());
        if(lastClientId.equalsIgnoreCase(Config.getMealsClientId())){
            promoCoupons = Data.userData.getCoupons(ProductType.MEALS);
        } else if(lastClientId.equalsIgnoreCase(Config.getGroceryClientId())) {
            promoCoupons = Data.userData.getCoupons(ProductType.GROCERY);
        } else {
            promoCoupons = Data.userData.getCoupons(ProductType.FRESH);
        }
        if(promoCoupons != null) {
            if(promoCoupons.size() > 0){
                relativeLayoutOffers.setVisibility(View.VISIBLE);
                setCouponNameToDisplay();
            } else {
                relativeLayoutOffers.setVisibility(View.GONE);
            }
        }


        relativeLayoutOffers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductType productType;
                String lastClientId = Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId());
                if(lastClientId.equalsIgnoreCase(Config.getMealsClientId())){
                    productType = ProductType.MEALS;
                } else if(lastClientId.equalsIgnoreCase(Config.getGroceryClientId())) {
                    productType = ProductType.GROCERY;
                } else {
                    productType = ProductType.FRESH;
                }
                getPromoCouponsDialog().show(productType, promoCoupons);
            }
        });


        buttonPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeOrder();
                int appType = Prefs.with(activity).getInt(Constants.APP_TYPE, Data.AppType);
                if(appType == AppConstant.ApplicationType.MEALS){
                    MyApplication.getInstance().logEvent(FirebaseEvents.M_PAY+"_"+activity.getPaymentOption(), null);
                    MyApplication.getInstance().logEvent(FirebaseEvents.M_PAY+"_"+FirebaseEvents.PLACE_ORDER, null);
                }else{
                    MyApplication.getInstance().logEvent(FirebaseEvents.F_PAY+"_"+activity.getPaymentOption(), null);
                    MyApplication.getInstance().logEvent(FirebaseEvents.F_PAY+"_"+FirebaseEvents.PLACE_ORDER, null);
                }
                NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_FRESH_PLACE_ORDER_CLICKED, null);
                FlurryEventLogger.event(PAYMENT_SCREEN, ORDER_PLACED, ORDER_PLACED);
            }
        });


        try {
            subTotalAmount = activity.updateCartValuesGetTotalPrice().first;
            promoAmount = 0;

            if (activity.getProductsResponse().getDeliveryInfo().getMinAmount() > subTotalAmount) {
				deliveryAmount = activity.getProductsResponse().getDeliveryInfo().getDeliveryCharges();
			}
            jcAmount = getTotalJCPaid();
            totalAmount = subTotalAmount - promoAmount + deliveryAmount;
            payableAmount = subTotalAmount - promoAmount + deliveryAmount - jcAmount;
            updateUI();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FlurryEventLogger.checkoutTrackEvent(AppConstant.EventTracker.PAYMENT, activity.productList);
        fetchWalletBalance();



        return rootView;
    }

    private void updateUI() {
        totalAmount = subTotalAmount - promoAmount + deliveryAmount;
        payableAmount = subTotalAmount - promoAmount + deliveryAmount - jcAmount;
        if(payableAmount < 0) {
            payableAmount = 0;
        }
        if(subTotalAmount == payableAmount && promoAmount <= 0 && deliveryAmount <= 0 && jcAmount <= 0) {
            relativeLayoutSubTotalLayout.setVisibility(View.GONE);
            viewLine.setVisibility(View.GONE);
        } else {
            viewLine.setVisibility(View.VISIBLE);
            relativeLayoutSubTotalLayout.setVisibility(View.VISIBLE);
            textViewSubTotalValue.setText(String.format(activity.getResources().getString(R.string.rupees_value_format), Utils.getMoneyDecimalFormat().format(subTotalAmount)));
        }


        textViewPayableValue.setText(String.format(activity.getResources().getString(R.string.rupees_value_format),
                Utils.getMoneyDecimalFormat().format(payableAmount)));

        if (promoAmount > 0) {
            relativeLayoutPromo.setVisibility(View.VISIBLE);
            textViewPromoValue.setText(String.format(activity.getResources().getString(R.string.rupees_value_format), Utils.getMoneyDecimalFormat().format(promoAmount)));
            //textViewPromoValue.setText("" + promoAmount);
        } else {
            relativeLayoutPromo.setVisibility(View.GONE);
        }


        if (deliveryAmount>0) {
            relativeLayoutdelivery.setVisibility(View.VISIBLE);
            String deliveryCharge = String.format(activity.getResources().getString(R.string.rupees_value_format),
                    Utils.getMoneyDecimalFormat().format(activity.getProductsResponse().getDeliveryInfo().getDeliveryCharges()));
            textViewdeliveryValue.setText(deliveryCharge);

        } else {
            relativeLayoutdelivery.setVisibility(View.GONE);
        }

        if (jcAmount > 0) {
            relativeLayoutTotalLayout.setVisibility(View.VISIBLE);
            if(totalAmount>0) {
                relativeLayoutJC.setVisibility(View.VISIBLE);
                textViewJCValue.setText(String.format(activity.getResources().getString(R.string.rupees_value_format),
                        Utils.getMoneyDecimalFormat().format(jcAmount)));
            } else {
                relativeLayoutJC.setVisibility(View.GONE);
            }

            textViewTotalValue.setText(String.format(activity.getResources().getString(R.string.rupees_value_format),
                    Utils.getMoneyDecimalFormat().format(totalAmount)));
        } else {
            relativeLayoutJC.setVisibility(View.GONE);
            relativeLayoutTotalLayout.setVisibility(View.GONE);
        }

    }


    private View.OnClickListener onClickListenerPaymentOptionSelector = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                String offeringPrefix = Config.getMealsClientId()
                        .equalsIgnoreCase(Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()))?
                        FirebaseEvents.MEALS_PAYMENT_MODE : FirebaseEvents.FRESH_PAYMENT_MODE;

                Bundle bundle = new Bundle();
                switch (v.getId()){
                    case R.id.relativeLayoutPaytm:
                        MyApplication.getInstance().logEvent(FirebaseEvents.TRANSACTION+"_"+offeringPrefix +"_"
                                +FirebaseEvents.PAYTM, bundle);
                        MyApplication.getInstance().getWalletCore().paymentOptionSelectionAtFreshCheckout(activity, PaymentOption.PAYTM,
                                callbackPaymentOptionSelector);
                        break;

                    case R.id.relativeLayoutMobikwik:
                        MyApplication.getInstance().logEvent(FirebaseEvents.TRANSACTION+"_"+offeringPrefix +"_"
                                +FirebaseEvents.MOBIKWIK, bundle);
                        MyApplication.getInstance().getWalletCore().paymentOptionSelectionAtFreshCheckout(activity, PaymentOption.MOBIKWIK,
                                callbackPaymentOptionSelector);
                        break;

                    case R.id.relativeLayoutFreeCharge:
                        MyApplication.getInstance().logEvent(FirebaseEvents.TRANSACTION+"_"+offeringPrefix+"_"
                                +FirebaseEvents.FREECHARGE, bundle);
                        MyApplication.getInstance().getWalletCore().paymentOptionSelectionAtFreshCheckout(activity, PaymentOption.FREECHARGE,
                                callbackPaymentOptionSelector);
                        break;

                    case R.id.relativeLayoutCash:
                        MyApplication.getInstance().logEvent(FirebaseEvents.TRANSACTION+"_"+offeringPrefix+"_"
                                +FirebaseEvents.CASH, bundle);
                        MyApplication.getInstance().getWalletCore().paymentOptionSelectionAtFreshCheckout(activity, PaymentOption.CASH,
                                callbackPaymentOptionSelector);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private CallbackPaymentOptionSelector callbackPaymentOptionSelector = new CallbackPaymentOptionSelector() {
        @Override
        public void onPaymentOptionSelected(PaymentOption paymentOption) {
            activity.setPaymentOption(paymentOption);
            setPaymentOptionUI();
        }

        @Override
        public void onWalletAdd(PaymentOption paymentOption) {
            activity.setPaymentOption(paymentOption);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        orderPaymentModes();
        setPaymentOptionUI();
    }

    private BroadcastReceiver broadcastReceiverWalletUpdate = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            activity.setPaymentOption(MyApplication.getInstance().getWalletCore().getDefaultPaymentOption());
            onResume();
        }
    };



    private ApiFetchWalletBalance apiFetchWalletBalance = null;
    private void fetchWalletBalance() {
        try {
            if(apiFetchWalletBalance == null){
                apiFetchWalletBalance = new ApiFetchWalletBalance(activity, new ApiFetchWalletBalance.Callback() {
                    @Override
                    public void onSuccess() {
                        try {
                            activity.setPaymentOption(MyApplication.getInstance().getWalletCore().getDefaultPaymentOption());
                            orderPaymentModes();
                            setPaymentOptionUI();
                            activity.updateMenu();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure() {
                        try {
                            activity.setPaymentOption(MyApplication.getInstance().getWalletCore().getDefaultPaymentOption());
                            orderPaymentModes();
                            setPaymentOptionUI();
                            activity.updateMenu();
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
            apiFetchWalletBalance.getBalance(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void setPaymentOptionUI() {
        try {
            activity.setPaymentOption(MyApplication.getInstance().getWalletCore().getPaymentOptionFromInt(
                    MyApplication.getInstance().getWalletCore().getPaymentOptionAccAvailability(activity.getPaymentOption().getOrdinal())));

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
                textViewPaytm.setText(activity.getResources().getString(R.string.paytm_wallet));
            } else{
                textViewPaytmValue.setVisibility(View.GONE);
                textViewPaytm.setText(activity.getResources().getString(R.string.nl_add_paytm_wallet));
            }
            if(Data.userData.getMobikwikEnabled() == 1){
                textViewMobikwikValue.setVisibility(View.VISIBLE);
                textViewMobikwik.setText(activity.getResources().getString(R.string.mobikwik_wallet));
            } else{
                textViewMobikwikValue.setVisibility(View.GONE);
                textViewMobikwik.setText(activity.getResources().getString(R.string.add_mobikwik_wallet));
            }
            if(Data.userData.getFreeChargeEnabled() == 1){
                textViewFreeChargeValue.setVisibility(View.VISIBLE);
                textViewFreeCharge.setText(activity.getResources().getString(R.string.freecharge_wallet));
            } else{
                textViewFreeChargeValue.setVisibility(View.GONE);
                textViewFreeCharge.setText(activity.getResources().getString(R.string.add_freecharge_wallet));
            }

            if (activity.getPaymentOption() == PaymentOption.PAYTM) {
                imageViewPaytmRadio.setImageResource(R.drawable.ic_radio_button_selected);
                imageViewRadioMobikwik.setImageResource(R.drawable.ic_radio_button_normal);
                imageViewRadioFreeCharge.setImageResource(R.drawable.ic_radio_button_normal);
                imageViewCashRadio.setImageResource(R.drawable.ic_radio_button_normal);
            }
            else if (activity.getPaymentOption() == PaymentOption.MOBIKWIK) {
                imageViewPaytmRadio.setImageResource(R.drawable.ic_radio_button_normal);
                imageViewRadioMobikwik.setImageResource(R.drawable.ic_radio_button_selected);
                imageViewRadioFreeCharge.setImageResource(R.drawable.ic_radio_button_normal);
                imageViewCashRadio.setImageResource(R.drawable.ic_radio_button_normal);
            }
            else if (activity.getPaymentOption() == PaymentOption.FREECHARGE) {
                imageViewPaytmRadio.setImageResource(R.drawable.ic_radio_button_normal);
                imageViewRadioMobikwik.setImageResource(R.drawable.ic_radio_button_normal);
                imageViewRadioFreeCharge.setImageResource(R.drawable.ic_radio_button_selected);
                imageViewCashRadio.setImageResource(R.drawable.ic_radio_button_normal);
            }
            else {
                imageViewPaytmRadio.setImageResource(R.drawable.ic_radio_button_normal);
                imageViewRadioMobikwik.setImageResource(R.drawable.ic_radio_button_normal);
                imageViewRadioFreeCharge.setImageResource(R.drawable.ic_radio_button_normal);
                imageViewCashRadio.setImageResource(R.drawable.ic_radio_button_selected);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void placeOrder() {
        try {
            final int appType = Prefs.with(activity).getInt(Constants.APP_TYPE, Data.AppType);
            boolean goAhead = true;
            if (activity.getPaymentOption() == PaymentOption.PAYTM) {
				if (Data.userData.getPaytmBalance() < getTotalPriceWithDeliveryCharges()) {
					if (Data.userData.getPaytmBalance() < 0) {
						DialogPopup.alertPopup(activity, "", activity.getResources().getString(R.string.paytm_error_cash_select_cash));
					} else {
						showWalletBalanceLowDialog(PaymentOption.PAYTM);
					}
					goAhead = false;
				}
			}
			else if (activity.getPaymentOption() == PaymentOption.MOBIKWIK) {
				if (Data.userData.getMobikwikBalance() < getTotalPriceWithDeliveryCharges()) {
					if (Data.userData.getMobikwikBalance() < 0) {
						DialogPopup.alertPopup(activity, "", activity.getResources().getString(R.string.mobikwik_error_select_cash));
					} else {
						showWalletBalanceLowDialog(PaymentOption.MOBIKWIK);
					}
					goAhead = false;
				}
			}
            else if (activity.getPaymentOption() == PaymentOption.FREECHARGE) {
                if (Data.userData.getFreeChargeBalance() < getTotalPriceWithDeliveryCharges()) {
                    if (Data.userData.getFreeChargeBalance() < 0) {
                        DialogPopup.alertPopup(activity, "", activity.getResources().getString(R.string.freecharge_error_case_select_cash));
                    } else {
                        showWalletBalanceLowDialog(PaymentOption.FREECHARGE);
                    }
                    goAhead = false;
                }
            }
            if (goAhead) {
				buttonPlaceOrder.setEnabled(false);
				DialogPopup.alertPopupTwoButtonsWithListeners(activity, "",
						activity.getResources().getString(R.string.place_order_confirmation),
						activity.getResources().getString(R.string.ok),
						activity.getResources().getString(R.string.cancel),
						new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								if (activity.getPaymentOption().getOrdinal() == 1) {
									FlurryEventLogger.event(PAYMENT_SCREEN, PAYMENT_METHOD, CASH);
								} else {
									FlurryEventLogger.event(PAYMENT_SCREEN, PAYMENT_METHOD, PAYTM);
								}

                                if(appType == AppConstant.ApplicationType.MEALS){
                                    MyApplication.getInstance().logEvent(FirebaseEvents.M_PAY+"_"+FirebaseEvents.PLACE_ORDER+"_"+FirebaseEvents.OK, null);
                                }else{
                                    MyApplication.getInstance().logEvent(FirebaseEvents.F_PAY+"_"+FirebaseEvents.PLACE_ORDER+"_"+FirebaseEvents.OK, null);
                                }
								placeOrderApi();
							}
						},
						new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								buttonPlaceOrder.setEnabled(true);
                                if(appType == AppConstant.ApplicationType.MEALS){
                                    MyApplication.getInstance().logEvent(FirebaseEvents.M_PAY+"_"+FirebaseEvents.PLACE_ORDER+"_"+FirebaseEvents.CANCEL, null);
                                }else{
                                    MyApplication.getInstance().logEvent(FirebaseEvents.M_PAY+"_"+FirebaseEvents.PLACE_ORDER+"_"+FirebaseEvents.CANCEL, null);
                                }
							}
						}, false, false);
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void applyPromo() {
//        Utils.hideSoftKeyboard(activity, appPromoEdittext);
//        try {
//            if (AppStatus.getInstance(activity).isOnline(activity)) {
//                DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));
//
//                HashMap<String, String> params = new HashMap<>();
//                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
//                params.put(Constants.DELIVERY_LATITUDE, Prefs.with(activity).getString(activity.getResources().getString(R.string.pref_loc_lati), String.valueOf(Data.latitude)));
//                params.put(Constants.DELIVERY_LONGITUDE, Prefs.with(activity).getString(activity.getResources().getString(R.string.pref_loc_longi), String.valueOf(Data.longitude)));
//
//                params.put(Constants.ORDER_AMOUNT, String.valueOf(activity.getTotalPrice()));
//                params.put(Constants.PROMO_CODE, appPromoEdittext.getText().toString().trim());
//                params.put(Constants.KEY_CLIENT_ID, ""+ Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()));
//                params.put(Constants.INTERATED, "1");
//                params.put(Constants.KEY_CART, cartItems());
//
//                Log.i(TAG, "getAllProducts params=" + params.toString());
//
//                RestClient.getFreshApiService().applyPromo(params, new Callback<PlaceOrderResponse>() {
//                    @Override
//                    public void success(PlaceOrderResponse placeOrderResponse, Response response) {
//                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
//                        Log.i(TAG, "getAllProducts response = " + responseStr);
//                        try {
//                            JSONObject jObj = new JSONObject(responseStr);
//                            String message = JSONParser.getServerMessage(jObj);
//                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
//                                int flag = jObj.getInt(Constants.KEY_FLAG);
//                                if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
//                                    Toast.makeText(activity, ""+message, Toast.LENGTH_SHORT).show();
//                                    promoCode = appPromoEdittext.getText().toString().trim();
//                                    promoAmount = jObj.optDouble(Constants.DISCOUNT, 0);
//                                    appPromoEdittext.clearFocus();
//                                    applyButton.setEnabled(false);
//                                    applyButton.setBackgroundColor(activity.getResources().getColor(R.color.theme_color_pressed));
//                                    updateUI();
//                                    HashMap<String, Object> profileUpdate = new HashMap<String, Object>();
//                                    profileUpdate.put(Events.COUPONS_USED, promoCode);
//                                    MyApplication.getInstance().getCleverTap().profile.push(profileUpdate);
//                                } else {
//                                    DialogPopup.alertPopup(activity, "", message);
//                                    promoCode = "";
//                                    promoAmount = 0;
//                                    appPromoEdittext.setText("");
//                                    appPromoEdittext.clearFocus();
//                                    updateUI();
//                                }
//                            }
//                        } catch (Exception exception) {
//                            exception.printStackTrace();
//                            retryDialog(DialogErrorType.SERVER_ERROR, 0);
//                        }
//                        DialogPopup.dismissLoadingDialog();
//                    }
//
//                    @Override
//                    public void failure(RetrofitError error) {
//                        Log.e(TAG, "paytmAuthenticateRecharge error " + error.toString());
//                        DialogPopup.dismissLoadingDialog();
////                        123
//                        retryDialog(DialogErrorType.CONNECTION_LOST, 0);
//                    }
//                });
//            } else {
//                retryDialog(DialogErrorType.NO_NET, 0);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private String cartItems(){
        JSONArray jCart = new JSONArray();
        if (activity.getProductsResponse() != null
                && activity.getProductsResponse().getCategories() != null) {
            for (Category category : activity.getProductsResponse().getCategories()) {
                Log.d(TAG, "" + category.getCategoryName());
                for (SubItem subItem : category.getSubItems()) {
                    if (subItem.getSubItemQuantitySelected() > 0) {
                        try {
                            JSONObject jItem = new JSONObject();
                            //subItem.getSubItemName();
                            jItem.put(Constants.KEY_SUB_ITEM_ID, subItem.getSubItemId());
                            jItem.put(Constants.KEY_QUANTITY, subItem.getSubItemQuantitySelected());
                            jCart.put(jItem);

                        } catch (Exception e) {
                            e.printStackTrace();
                            return "";
                        }

                        try {
                            String categoryName = "", itemName = "";
                            double price = 0.0;
                            int qty = 0, itemId = 0;

                            qty = subItem.getSubItemQuantitySelected();
                            categoryName = category.getCategoryName();
                            itemId = subItem.getSubItemId();
                            itemName = subItem.getSubItemName();
                            price = subItem.getPrice();

                            Product product = new Product()
                                    .setCategory(categoryName)
                                    .setId("" + itemId)
                                    .setName(itemName)
                                    .setPrice(price)
                                    .setQuantity(qty);
//                                                                .setPosition(4);
                            productList.add(product);

                            HashMap<String, Object> item = new HashMap<>();
                            item.put(Events.PRODUCT_NAME, itemName);
                            item.put(Events.PRODUCT_ID, itemId);
                            item.put(Events.QUANTITY, qty);
                            items.add(item);

                        } catch (Exception e) {
                            e.printStackTrace();
                            return "";
                        }
                    }
                }
            }
        }
        return jCart.toString();
    }

    HashMap<String, Object> chargeDetails = new HashMap<String, Object>();
    ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();

    public void placeOrderApi() {
        try {
            if (AppStatus.getInstance(activity).isOnline(activity)) {
                productList.clear();
                DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));

                chargeDetails.clear();
                items.clear();


                chargeDetails.put("Payment mode", ""+activity.getPaymentOption());
                chargeDetails.put(Events.TOTAL_AMOUNT, ""+totalAmount);
                if(promoAmount>0) {
                    chargeDetails.put(Events.DISCOUNT_AMOUNT, "" + promoAmount);
                } else {
                    chargeDetails.put(Events.DISCOUNT_AMOUNT, "" + promoAmount);
                }

                chargeDetails.put(Events.START_TIME, ""+String.valueOf(activity.getSlotSelected().getStartTime()));
                chargeDetails.put(Events.END_TIME, ""+String.valueOf(activity.getSlotSelected().getEndTime()));

                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.KEY_LATITUDE, String.valueOf(Data.latitude));
                params.put(Constants.KEY_LONGITUDE, String.valueOf(Data.longitude));

                params.put(Constants.DELIVERY_LATITUDE, Prefs.with(activity).getString(activity.getResources().getString(R.string.pref_loc_lati), String.valueOf(Data.latitude)));
                params.put(Constants.DELIVERY_LONGITUDE, Prefs.with(activity).getString(activity.getResources().getString(R.string.pref_loc_longi), String.valueOf(Data.longitude)));

                params.put(Constants.KEY_PAYMENT_MODE, String.valueOf(activity.getPaymentOption().getOrdinal()));
                params.put(Constants.KEY_DELIVERY_SLOT_ID, String.valueOf(activity.getSlotSelected().getDeliverySlotId()));
                params.put(Constants.KEY_DELIVERY_ADDRESS, String.valueOf(activity.getSelectedAddress()));
                params.put(Constants.KEY_DELIVERY_NOTES, String.valueOf(activity.getSpecialInst()));
                params.put(Constants.KEY_CLIENT_ID, ""+ Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()));
                params.put(Constants.KEY_CART, cartItems());
                if(!TextUtils.isEmpty(promoCode)) {
                    params.put(Constants.PROMO_CODE, promoCode);
                    chargeDetails.put(Events.PROMOCODE, promoCode);
                }
                if(activity.getSelectedPromoCoupon() != null && activity.getSelectedPromoCoupon().getId() > -1){
                    if(activity.getSelectedPromoCoupon() instanceof CouponInfo){
                        params.put(Constants.KEY_ACCOUNT_ID, String.valueOf(activity.getSelectedPromoCoupon().getId()));
                    } else if(activity.getSelectedPromoCoupon() instanceof PromotionInfo){
                        params.put(Constants.KEY_ORDER_OFFER_ID, String.valueOf(activity.getSelectedPromoCoupon().getId()));
                    }
                }



                int type = Prefs.with(activity).getInt(Constants.APP_TYPE, Data.AppType);
                if(type == AppConstant.ApplicationType.MEALS) {
                    params.put("store_id", "2");
                    params.put("group_id", ""+activity.getProductsResponse().getCategories().get(0).getSubItems().get(0).getGroupId());
                    chargeDetails.put(Events.TYPE, "Meals");
                } else if(type == AppConstant.ApplicationType.GROCERY) {
                    chargeDetails.put(Events.TYPE, "Grocery");
                }else {
                    chargeDetails.put(Events.TYPE, "Fresh");
                }
                params.put(Constants.INTERATED, "1");


                Log.i(TAG, "getAllProducts params=" + params.toString());

                RestClient.getFreshApiService().placeOrder(params, new Callback<PlaceOrderResponse>() {
                    @Override
                    public void success(PlaceOrderResponse placeOrderResponse, Response response) {
                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                        Log.i(TAG, "getAllProducts response = " + responseStr);
//						DialogPopup.dismissLoadingDialog();
                        try {
                            JSONObject jObj = new JSONObject(responseStr);
                            String message = JSONParser.getServerMessage(jObj);
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                int flag = jObj.getInt(Constants.KEY_FLAG);
                                if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
                                    long time = 0L;
                                    Prefs.with(activity).save(SPLabels.CHECK_BALANCE_LAST_TIME, time);
                                    activity.resumeMethod();

                                    chargeDetails.put("Charged ID", placeOrderResponse.getOrderId());
                                    MyApplication.getInstance().charged(chargeDetails, items);


                                    ProductAction productAction = new ProductAction(ProductAction.ACTION_PURCHASE)
                                            .setTransactionId(String.valueOf(placeOrderResponse.getOrderId()))
                                            .setTransactionAffiliation("Fresh Store")
                                            .setTransactionRevenue(placeOrderResponse.getAmount())
                                            .setTransactionTax(0)
                                            //.setCheckoutStep(4)
                                            .setTransactionShipping(0);


                                    try {
                                        AppEventsLogger logger = AppEventsLogger.newLogger(getActivity());

                                        Bundle parameters = new Bundle();
                                        parameters.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "INR");
                                        parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "product");
                                        parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, String.valueOf(placeOrderResponse.getOrderId()));

                                        logger.logEvent(AppEventsConstants.EVENT_NAME_PURCHASED,
                                                placeOrderResponse.getAmount(),
                                                parameters);

                                    } catch (Exception e) {
                                    }

                                    FlurryEventLogger.checkoutTrackEvent(AppConstant.EventTracker.ORDER_PLACED, productList);
                                    FlurryEventLogger.orderedProduct(productList, productAction);

                                    new FreshOrderCompleteDialog(activity, new FreshOrderCompleteDialog.Callback() {
                                        @Override
                                        public void onDismiss() {
                                            activity.orderComplete();
                                        }
                                    }).show(String.valueOf(placeOrderResponse.getOrderId()),
                                            DateOperations.convertDayTimeAPViaFormat(activity.getSlotSelected().getStartTime())
                                                    + " - " + DateOperations.convertDayTimeAPViaFormat(activity.getSlotSelected().getEndTime()),
                                            activity.getSlotSelected().getDayName());
                                    activity.setSelectedPromoCoupon(noSelectionCoupon);

                                    NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_FRESH_ORDER_PLACED, null);

                                } else if (ApiResponseFlags.USER_IN_DEBT.getOrdinal() == flag) {
                                    final String message1 = jObj.optString(Constants.KEY_MESSAGE, "");
                                    final double userDebt = jObj.optDouble(Constants.KEY_USER_DEBT, 0);
                                    Log.e("USER_IN_DEBT message", "=" + message1);
                                    new UserDebtDialog(activity, Data.userData,
                                            new UserDebtDialog.Callback() {
                                                @Override
                                                public void successFullyDeducted(double userDebt) {
                                                    activity.setPaymentOption(MyApplication.getInstance().getWalletCore().getDefaultPaymentOption());
                                                    setPaymentOptionUI();
                                                    activity.updateMenu();
                                                }

                                            }).showUserDebtDialog(userDebt, message1);
                                } else if (ApiResponseFlags.INSUFFICIENT_BALANCE.getOrdinal() == flag) {
                                    DialogPopup.alertPopupWithListener(activity, "", message, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            fetchWalletBalance();
                                        }
                                    });
                                }
                                else {
                                    DialogPopup.alertPopup(activity, "", message);
                                }
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            retryDialog(DialogErrorType.SERVER_ERROR, 1);
                        }
//                        123
                        DialogPopup.dismissLoadingDialog();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e(TAG, "paytmAuthenticateRecharge error " + error.toString());
                        DialogPopup.dismissLoadingDialog();
//                        123
                        retryDialog(DialogErrorType.CONNECTION_LOST, 1);
                    }
                });
            } else {
                retryDialog(DialogErrorType.NO_NET, 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        buttonPlaceOrder.setEnabled(true);
    }

    private void retryDialog(DialogErrorType dialogErrorType, final int apiHit) {
        DialogPopup.dialogNoInternet(activity,
                dialogErrorType,
                new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        if(apiHit == 1)
                            placeOrderApi();
                        else if(apiHit == 0) {
                        //TODO commented api applyPromo();
                        }
                    }

                    @Override
                    public void neutralClick(View view) {

                    }

                    @Override
                    public void negativeClick(View view) {
                    }
                });
    }


    private void showWalletBalanceLowDialog(final PaymentOption paymentOption) {
        try {
            FreshWalletBalanceLowDialog.Callback callback = new FreshWalletBalanceLowDialog.Callback() {
                @Override
                public void onRechargeNowClicked() {
                    FlurryEventLogger.event(PAYMENT_SCREEN, PAYMENT_SCREEN, RECHARGE);
                    intentToWallet(paymentOption);
                }

                @Override
                public void onPayByCashClicked() {
                    FlurryEventLogger.event(PAYMENT_SCREEN, PAYMENT_SCREEN, PAY_VIA_CASH);
                }
            };
            if (paymentOption == PaymentOption.PAYTM && Data.userData.getPaytmEnabled() == 1) {
                String amount = Utils.getMoneyDecimalFormat().format(Math.ceil(Data.userData.getPaytmBalance() - Math.ceil(getTotalPriceWithDeliveryCharges())));
                new FreshWalletBalanceLowDialog(activity, callback).show(R.string.dont_have_enough_paytm_balance, amount, R.drawable.ic_paytm_big);
            }
            else if (paymentOption == PaymentOption.MOBIKWIK && Data.userData.getMobikwikEnabled() == 1) {
                String amount = Utils.getMoneyDecimalFormat().format(Math.ceil(Data.userData.getMobikwikBalance() - Math.ceil(getTotalPriceWithDeliveryCharges())));
                new FreshWalletBalanceLowDialog(activity, callback).show(R.string.dont_have_enough_mobikwik_balance, amount, R.drawable.ic_mobikwik_big);
            }
            else if (paymentOption == PaymentOption.FREECHARGE && Data.userData.getFreeChargeEnabled() == 1) {
                String amount = Utils.getMoneyDecimalFormat().format(Math.ceil(Data.userData.getFreeChargeBalance() - Math.ceil(getTotalPriceWithDeliveryCharges())));
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
                        df.format(Math.ceil(getTotalPriceWithDeliveryCharges()
                                - Data.userData.getPaytmBalance())));
            }
            else if (paymentOption == PaymentOption.MOBIKWIK) {
                intent.putExtra(Constants.KEY_PAYMENT_ACTIVITY_PATH,
                        (Data.userData.getMobikwikEnabled() == 1)? PaymentActivityPath.WALLET_ADD_MONEY.getOrdinal()
                                : PaymentActivityPath.ADD_WALLET.getOrdinal());
                intent.putExtra(Constants.KEY_PAYMENT_RECHARGE_VALUE,
                        df.format(Math.ceil(getTotalPriceWithDeliveryCharges()
                                - Data.userData.getMobikwikBalance())));
            }
            else if (paymentOption == PaymentOption.FREECHARGE) {
                intent.putExtra(Constants.KEY_PAYMENT_ACTIVITY_PATH,
                        (Data.userData.getFreeChargeEnabled() == 1)? PaymentActivityPath.WALLET_ADD_MONEY.getOrdinal()
                                : PaymentActivityPath.ADD_WALLET.getOrdinal());
                intent.putExtra(Constants.KEY_PAYMENT_RECHARGE_VALUE,
                        df.format(Math.ceil(getTotalPriceWithDeliveryCharges()
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

    private double getTotalPriceWithDeliveryCharges() {
        try {
            double totalAmount = activity.updateCartValuesGetTotalPrice().first;
            double amountPayable = totalAmount;
            if (activity.getProductsResponse().getDeliveryInfo().getMinAmount() > totalAmount) {
                amountPayable = amountPayable + activity.getProductsResponse().getDeliveryInfo().getDeliveryCharges();
            }
            if(promoAmount>0) {
                amountPayable = amountPayable - promoAmount;
            }
            double paid = Math.min(amountPayable, Data.userData.getJugnooBalance());
            amountPayable = amountPayable - paid;
            if(amountPayable<0)
                amountPayable = 0;
            return amountPayable;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private double getTotalJCPaid() {
        try {
            double totalAmount = activity.updateCartValuesGetTotalPrice().first;
            double amountPayable = totalAmount;
            if (activity.getProductsResponse().getDeliveryInfo().getMinAmount() > totalAmount) {
                amountPayable = amountPayable + activity.getProductsResponse().getDeliveryInfo().getDeliveryCharges();
            }
            double paid = Math.min(amountPayable, Data.userData.getJugnooBalance());
            return paid;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            activity.fragmentUISetup(this);
            onResume();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(activity).unregisterReceiver(broadcastReceiverWalletUpdate);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ASSL.closeActivity(linearLayoutRoot);
        System.gc();
    }



    public interface CallbackPaymentOptionSelector{
        void onPaymentOptionSelected(PaymentOption paymentOption);
        void onWalletAdd(PaymentOption paymentOption);
    }

    private void orderPaymentModes(){
        try{
            ArrayList<PaymentModeConfigData> paymentModeConfigDatas = MyApplication.getInstance().getWalletCore().getPaymentModeConfigDatas(Data.userData);
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
                        } else if (paymentModeConfigData.getPaymentOption() == PaymentOption.CASH.getOrdinal()) {
                            linearLayoutWalletContainer.addView(relativeLayoutCash);
                        }
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private PromoCouponsDialog promoCouponsDialog;
    public PromoCouponsDialog getPromoCouponsDialog(){
        if(promoCouponsDialog == null){
            promoCouponsDialog = new PromoCouponsDialog(activity, new PromoCouponsDialog.Callback() {
                @Override
                public void onCouponApplied() {
                    setCouponNameToDisplay();
                }

                @Override
                public void onSkipped() {
                    setCouponNameToDisplay();
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

    private void setCouponNameToDisplay(){
        try {
            if(activity.getSelectedPromoCoupon() != null && activity.getSelectedPromoCoupon().getId() > -1){
				relativeLayoutSelectedCoupon.setVisibility(View.VISIBLE);
                textViewSelectedOffer.setText(activity.getSelectedPromoCoupon().getTitle());
                textViewOffers.setText(R.string.offers);
			} else{
                relativeLayoutSelectedCoupon.setVisibility(View.GONE);
                textViewOffers.setText(R.string.select_offers);
			}
        } catch (Exception e) {
            e.printStackTrace();
            relativeLayoutSelectedCoupon.setVisibility(View.GONE);
            textViewOffers.setText(R.string.select_offers);
        }
    }

}

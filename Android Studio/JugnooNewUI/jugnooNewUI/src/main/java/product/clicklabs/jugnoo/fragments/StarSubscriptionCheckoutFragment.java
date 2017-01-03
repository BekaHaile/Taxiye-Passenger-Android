package product.clicklabs.jugnoo.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sabkuchfresh.analytics.FlurryEventLogger;
import com.sabkuchfresh.home.CallbackPaymentOptionSelector;
import com.sabkuchfresh.home.FreshWalletBalanceLowDialog;
import com.sabkuchfresh.retrofit.model.PurchaseSubscriptionResponse;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.JugnooStarActivity;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.apis.ApiFetchWalletBalance;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.CouponInfo;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.datastructure.PromotionInfo;
import product.clicklabs.jugnoo.datastructure.SubscriptionData;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.home.adapters.PromoCouponsAdapter;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FirebaseEvents;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.NonScrollListView;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;
import product.clicklabs.jugnoo.wallet.PaymentActivity;
import product.clicklabs.jugnoo.wallet.models.PaymentActivityPath;
import product.clicklabs.jugnoo.wallet.models.PaymentModeConfigData;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

import static com.sabkuchfresh.analytics.FlurryEventNames.CASH;
import static com.sabkuchfresh.analytics.FlurryEventNames.PAYMENT_METHOD;
import static com.sabkuchfresh.analytics.FlurryEventNames.PAYMENT_SCREEN;
import static com.sabkuchfresh.analytics.FlurryEventNames.PAYTM;
import static com.sabkuchfresh.analytics.FlurryEventNames.PAY_VIA_CASH;
import static com.sabkuchfresh.analytics.FlurryEventNames.RECHARGE;

/**
 * Created by ankit on 30/12/16.
 */

public class StarSubscriptionCheckoutFragment extends Fragment implements PromoCouponsAdapter.Callback {

    private View rootView;
    private JugnooStarActivity activity;
    private TextView tvPaymentPlan, tvPlanAmount;
    private Button bPlaceOrder;
    private LinearLayout linearLayoutOffers, linearLayoutRoot;
    private NonScrollListView listViewOffers;
    private PromoCouponsAdapter promoCouponsAdapter;
    private RelativeLayout relativeLayoutPaytm, relativeLayoutMobikwik, relativeLayoutFreeCharge;
    private ImageView imageViewPaytmRadio, imageViewAddPaytm, imageViewRadioMobikwik, imageViewAddMobikwik,
            imageViewRadioFreeCharge, imageViewAddFreeCharge;
    private TextView textViewPaytmValue, textViewMobikwikValue, textViewFreeChargeValue;
    private PaymentOption paymentOption;
    private LinearLayout linearLayoutWalletContainer;
    private SubscriptionData.Subscription subscription;
    private ArrayList<PromoCoupon> promoCoupons = new ArrayList<>();

    public static StarSubscriptionCheckoutFragment newInstance(String subscription){
        StarSubscriptionCheckoutFragment fragment = new StarSubscriptionCheckoutFragment();
        Bundle bundle = new Bundle();
        bundle.putString("plan", subscription);
        fragment.setArguments(bundle);
        return  fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_star_subscription_checkout, container, false);

        activity = (JugnooStarActivity) getActivity();
        linearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.linearLayoutRoot);
        try {
            if (linearLayoutRoot != null) {
                new ASSL(activity, linearLayoutRoot, 1134, 720, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Bundle bundle = getArguments();
            String plan = bundle.getString("plan", "");
            subscription = new Gson().fromJson(plan, SubscriptionData.Subscription.class);


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

            relativeLayoutPaytm.setOnClickListener(onClickListenerPaymentOptionSelector);
            relativeLayoutMobikwik.setOnClickListener(onClickListenerPaymentOptionSelector);
            relativeLayoutFreeCharge.setOnClickListener(onClickListenerPaymentOptionSelector);

            tvPaymentPlan.setText(subscription.getDescription());
            tvPlanAmount.setText(String.format(activity.getResources().getString(R.string.rupees_value_format_without_space),
                    Utils.getMoneyDecimalFormat().format(subscription.getAmount())));

            linearLayoutOffers = (LinearLayout) rootView.findViewById(R.id.linearLayoutOffers);
            listViewOffers = (NonScrollListView) rootView.findViewById(R.id.listViewOffers);
            promoCouponsAdapter = new PromoCouponsAdapter(activity, R.layout.list_item_fresh_promo_coupon, promoCoupons, this);
            listViewOffers.setAdapter(promoCouponsAdapter);

            setPaymentOption(MyApplication.getInstance().getWalletCore().getDefaultPaymentOption());

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

    private void updateCouponsDataView(){
        try {
            promoCoupons = Data.userData.getPromoCoupons();
            if(promoCoupons != null) {
                if(promoCoupons.size() > 0){
                    linearLayoutOffers.setVisibility(View.VISIBLE);
                } else {
                    linearLayoutOffers.setVisibility(View.GONE);
                }
                promoCouponsAdapter.setList(promoCoupons);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                    case R.id.bPlaceOrder:
                        if(paymentOption.getOrdinal() != 0 && paymentOption.getOrdinal() != 1) {
                            placeOrder();
                        } else{
                            Utils.showToast(activity, "Please select payment option");
                        }
                        break;
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

    private void placeOrder() {
        try {
            final int appType = Prefs.with(activity).getInt(Constants.APP_TYPE, Data.AppType);
            boolean goAhead = true;
            if (getPaymentOption() == PaymentOption.PAYTM) {
                if (Data.userData.getPaytmBalance() < subscription.getAmount()) {
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
                if (Data.userData.getMobikwikBalance() < subscription.getAmount()) {
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
                if (Data.userData.getFreeChargeBalance() < subscription.getAmount()) {
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
                bPlaceOrder.setEnabled(false);
                DialogPopup.alertPopupTwoButtonsWithListeners(activity, "",
                        activity.getResources().getString(R.string.place_order_confirmation),
                        activity.getResources().getString(R.string.ok),
                        activity.getResources().getString(R.string.cancel),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (getPaymentOption().getOrdinal() == 1) {
                                    FlurryEventLogger.event(PAYMENT_SCREEN, PAYMENT_METHOD, CASH);
                                } else {
                                    FlurryEventLogger.event(PAYMENT_SCREEN, PAYMENT_METHOD, PAYTM);
                                }

                                apiPurchaseSubscription();
                            }
                        },
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                bPlaceOrder.setEnabled(true);
                            }
                        }, false, false);
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
                    FlurryEventLogger.event(PAYMENT_SCREEN, PAYMENT_SCREEN, RECHARGE);
                    intentToWallet(paymentOption);
                }

                @Override
                public void onPayByCashClicked() {
                    FlurryEventLogger.event(PAYMENT_SCREEN, PAYMENT_SCREEN, PAY_VIA_CASH);
                }
            };
            if (paymentOption == PaymentOption.PAYTM && Data.userData.getPaytmEnabled() == 1) {
                String amount = com.sabkuchfresh.utils.Utils.getMoneyDecimalFormat().format(Math.ceil(Data.userData.getPaytmBalance() - Math.ceil(subscription.getAmount())));
                new FreshWalletBalanceLowDialog(activity, callback).show(R.string.dont_have_enough_paytm_balance, amount, R.drawable.ic_paytm_big);
            }
            else if (paymentOption == PaymentOption.MOBIKWIK && Data.userData.getMobikwikEnabled() == 1) {
                String amount = com.sabkuchfresh.utils.Utils.getMoneyDecimalFormat().format(Math.ceil(Data.userData.getMobikwikBalance() - Math.ceil(subscription.getAmount())));
                new FreshWalletBalanceLowDialog(activity, callback).show(R.string.dont_have_enough_mobikwik_balance, amount, R.drawable.ic_mobikwik_big);
            }
            else if (paymentOption == PaymentOption.FREECHARGE && Data.userData.getFreeChargeEnabled() == 1) {
                String amount = com.sabkuchfresh.utils.Utils.getMoneyDecimalFormat().format(Math.ceil(Data.userData.getFreeChargeBalance() - Math.ceil(subscription.getAmount())));
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
                        df.format(Math.ceil(subscription.getAmount()
                                - Data.userData.getPaytmBalance())));
            }
            else if (paymentOption == PaymentOption.MOBIKWIK) {
                intent.putExtra(Constants.KEY_PAYMENT_ACTIVITY_PATH,
                        (Data.userData.getMobikwikEnabled() == 1)? PaymentActivityPath.WALLET_ADD_MONEY.getOrdinal()
                                : PaymentActivityPath.ADD_WALLET.getOrdinal());
                intent.putExtra(Constants.KEY_PAYMENT_RECHARGE_VALUE,
                        df.format(Math.ceil(subscription.getAmount()
                                - Data.userData.getMobikwikBalance())));
            }
            else if (paymentOption == PaymentOption.FREECHARGE) {
                intent.putExtra(Constants.KEY_PAYMENT_ACTIVITY_PATH,
                        (Data.userData.getFreeChargeEnabled() == 1)? PaymentActivityPath.WALLET_ADD_MONEY.getOrdinal()
                                : PaymentActivityPath.ADD_WALLET.getOrdinal());
                intent.putExtra(Constants.KEY_PAYMENT_RECHARGE_VALUE,
                        df.format(Math.ceil(subscription.getAmount()
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
                            //activity.updateMenu();
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
                            //activity.updateMenu();
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

            if (getPaymentOption() == PaymentOption.PAYTM) {
                imageViewPaytmRadio.setImageResource(R.drawable.ic_radio_button_selected);
                imageViewRadioMobikwik.setImageResource(R.drawable.ic_radio_button_normal);
                imageViewRadioFreeCharge.setImageResource(R.drawable.ic_radio_button_normal);
            }
            else if (getPaymentOption() == PaymentOption.MOBIKWIK) {
                imageViewPaytmRadio.setImageResource(R.drawable.ic_radio_button_normal);
                imageViewRadioMobikwik.setImageResource(R.drawable.ic_radio_button_selected);
                imageViewRadioFreeCharge.setImageResource(R.drawable.ic_radio_button_normal);
            }
            else if (getPaymentOption() == PaymentOption.FREECHARGE) {
                imageViewPaytmRadio.setImageResource(R.drawable.ic_radio_button_normal);
                imageViewRadioMobikwik.setImageResource(R.drawable.ic_radio_button_normal);
                imageViewRadioFreeCharge.setImageResource(R.drawable.ic_radio_button_selected);
            }
            else {
                imageViewPaytmRadio.setImageResource(R.drawable.ic_radio_button_normal);
                imageViewRadioMobikwik.setImageResource(R.drawable.ic_radio_button_normal);
                imageViewRadioFreeCharge.setImageResource(R.drawable.ic_radio_button_normal);
            }
            //updateCartDataView();
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
    public void setSelectedCoupon(int position) {
        PromoCoupon promoCoupon;
        if (promoCoupons != null && position > -1 && position < promoCoupons.size()) {
            promoCoupon = promoCoupons.get(position);
        } else {
            promoCoupon = noSelectionCoupon;
        }
        if (MyApplication.getInstance().getWalletCore().displayAlertAndCheckForSelectedWalletCoupon(activity, getPaymentOption().getOrdinal(), promoCoupon)) {
            setSelectedPromoCoupon(promoCoupon);
        }
    }

    private void apiPurchaseSubscription() {
        if (AppStatus.getInstance(activity).isOnline(activity)) {
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
                            DialogPopup.alertPopupWithListener(activity, "", message, getResources().getString(R.string.ok), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Data.userData.getSubscriptionData().setUserSubscriptions(purchaseSubscriptionResponse.getUserSubscriptions());
                                    activity.finish();
                                    activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
                                }
                            }, false);
                            //DialogPopup.alertPopup(JugnooStarSubscribedActivity.this, "", message);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        retryDialog(DialogErrorType.SERVER_ERROR);
                    }
                    DialogPopup.dismissLoadingDialog();
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e("customerFetchUserAddress error=", "" + error.toString());
                    DialogPopup.dismissLoadingDialog();
                    retryDialog(DialogErrorType.CONNECTION_LOST);
                }
            });

        } else {
            retryDialog(DialogErrorType.NO_NET);
        }
    }

    private void retryDialog(DialogErrorType dialogErrorType) {
        DialogPopup.dialogNoInternet(activity,
                dialogErrorType,
                new Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        apiPurchaseSubscription();
                    }

                    @Override
                    public void neutralClick(View view) {

                    }

                    @Override
                    public void negativeClick(View view) {

                    }
                });
    }

    private PromoCoupon selectedPromoCoupon;
    private PromoCoupon noSelectionCoupon = new CouponInfo(-1, "Don't apply coupon on this ride");
    public PromoCoupon getSelectedPromoCoupon() {
        return selectedPromoCoupon;
    }

    public void setSelectedPromoCoupon(PromoCoupon selectedPromoCoupon) {
        this.selectedPromoCoupon = selectedPromoCoupon;
    }

}

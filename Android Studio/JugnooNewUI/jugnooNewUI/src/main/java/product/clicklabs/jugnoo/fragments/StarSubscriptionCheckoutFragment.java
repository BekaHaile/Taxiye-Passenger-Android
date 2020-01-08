package product.clicklabs.jugnoo.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.cardview.widget.CardView;
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
import com.sabkuchfresh.commoncalls.ApiCancelOrder;
import com.sabkuchfresh.dialogs.CheckoutRequestPaymentDialog;
import com.sabkuchfresh.enums.IciciPaymentOrderStatus;
import com.sabkuchfresh.fatafatchatpay.FatafatChatPayActivity;
import com.sabkuchfresh.feed.ui.api.APICommonCallback;
import com.sabkuchfresh.feed.ui.api.ApiCommon;
import com.sabkuchfresh.feed.ui.api.ApiName;
import com.sabkuchfresh.home.CallbackPaymentOptionSelector;
import com.sabkuchfresh.home.FreshWalletBalanceLowDialog;
import com.sabkuchfresh.retrofit.model.PlaceOrderResponse;
import com.sabkuchfresh.retrofit.model.PurchaseSubscriptionResponse;
import com.sabkuchfresh.retrofit.model.common.IciciPaymentRequestStatus;
import com.sabkuchfresh.utils.AppConstant;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import butterknife.ButterKnife;
import io.paperdb.Paper;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.JugnooStarActivity;
import product.clicklabs.jugnoo.JugnooStarSubscribedActivity;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.PaperDBKeys;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.RazorpayBaseActivity;
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
import retrofit.Callback;
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
    private RelativeLayout relativeLayoutIcici;
    private ArrayList<SubscriptionData.Subscription> subscriptionsActivityList;

    private LinearLayout llRideInfo;
    private TextView tvTotalFareValue, tvCashPaidValue, textViewPaymentVia;

    private boolean fromStar;
    private int engagementId;
    private double totalFare, fareToPay;
    private String currency;
    private String jugnooVpaHandle;
    private EditText edtIciciVpa;
    private TextView tvLabelIciciUpi,tvUPICashback;
    private ImageView imageViewIcici;
    private final static IntentFilter LOCAL_BROADCAST = new IntentFilter(Data.LOCAL_BROADCAST);
    private static final String FOR_STAR_SUBSCRIPTION = "for_star_subscription";
    private int orderId;
    private boolean isFromFatafatChat;
    private LinearLayout llFatafatChatPay,llTotalAmount,llJugnooCash,llAmtToBePaid;
    private TextView tvTotalAmount,tvJugnooCashLabel, tvJugnooCash,tvAmtToBePaid;
    // indicates if upi is pending
    private boolean isUpiPending;
    // amount after jugnoo cash deduction
    private double netPayableAmount;
    private CardView cvPaymentMethodContainer;
    private boolean isPaymentOptionSet;


    public static StarSubscriptionCheckoutFragment newInstance(String subscription, int type){
        StarSubscriptionCheckoutFragment fragment = new StarSubscriptionCheckoutFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(FOR_STAR_SUBSCRIPTION, true);
        bundle.putString("plan", subscription);
        bundle.putInt("type", type);
        fragment.setArguments(bundle);
        return  fragment;
    }

    public static StarSubscriptionCheckoutFragment newInstance(int engagementId, double totalFare, double fare, String currency){
        StarSubscriptionCheckoutFragment fragment = new StarSubscriptionCheckoutFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(FOR_STAR_SUBSCRIPTION, false);
        bundle.putInt(Constants.KEY_ENGAGEMENT_ID, engagementId);
        bundle.putDouble(Constants.KEY_TOTAL_FARE, totalFare);
        bundle.putDouble(Constants.KEY_FARE_TO_PAY, fare);
        bundle.putString(Constants.KEY_CURRENCY, currency);
        fragment.setArguments(bundle);
        return  fragment;
    }

    public static StarSubscriptionCheckoutFragment newInstance(double amountToPay, int orderId, boolean isUpiPending){
        StarSubscriptionCheckoutFragment fragment = new StarSubscriptionCheckoutFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(FOR_STAR_SUBSCRIPTION, false);
        bundle.putDouble(Constants.KEY_FARE_TO_PAY, amountToPay);
        bundle.putInt(Constants.KEY_ORDER_ID,orderId);
        bundle.putBoolean(Constants.KEY_IS_UPI_PENDING,isUpiPending);
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
            currency = bundle.getString(Constants.KEY_CURRENCY);
            if(bundle.containsKey(Constants.KEY_ORDER_ID)){
                orderId=bundle.getInt(Constants.KEY_ORDER_ID);
            }
            if(bundle.containsKey(Constants.KEY_IS_UPI_PENDING)){
                isUpiPending = bundle.getBoolean(Constants.KEY_IS_UPI_PENDING);
            }
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_star_subscription_checkout, container, false);
        noSelectionCoupon = new CouponInfo(-1, getString(R.string.dont_apply_coupon_on_this_ride));

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
            tvPaymentPlan.setText(MyApplication.getInstance().ACTIVITY_NAME_JUGNOO_STAR);
            tvPlanAmount = (TextView) rootView.findViewById(R.id.tvPlanAmount); tvPlanAmount.setTypeface(Fonts.mavenMedium(activity));
            bPlaceOrder = (Button) rootView.findViewById(R.id.bPlaceOrder); bPlaceOrder.setTypeface(Fonts.mavenMedium(activity)); bPlaceOrder.setOnClickListener(onClickListenerPaymentOptionSelector);
            linearLayoutWalletContainer = (LinearLayout) rootView.findViewById(R.id.linearLayoutWalletContainer);
            relativeLayoutPaytm = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutPaytm);
            relativeLayoutMobikwik = (RelativeLayout)rootView.findViewById(R.id.relativeLayoutMobikwik);
            relativeLayoutFreeCharge = (RelativeLayout)rootView.findViewById(R.id.relativeLayoutFreeCharge);
            relativeLayoutIcici = (RelativeLayout) rootView.findViewById(R.id.rlIciciUpi);
            edtIciciVpa = (EditText) rootView.findViewById(R.id.edtIciciVpa);
            tvLabelIciciUpi = (TextView) rootView.findViewById(R.id.tv_label_below_edt_icici);
            imageViewIcici = (ImageView) rootView.findViewById(R.id.ivRadioIciciUpi);
            cvPaymentMethodContainer = (CardView)rootView.findViewById(R.id.cvPaymentMethodContainer);
            cvPaymentMethodContainer.setVisibility(View.GONE);
            tvUPICashback = (TextView) rootView.findViewById(R.id.tvUPICashback);
            tvUPICashback.setTypeface(tvUPICashback.getTypeface(), Typeface.ITALIC);

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

            llRideInfo = (LinearLayout) rootView.findViewById(R.id.llRideInfo);
            llRideInfo.setVisibility(View.GONE);
            llFatafatChatPay = (LinearLayout) rootView.findViewById(R.id.llFatafatChatPay);
            llFatafatChatPay.setVisibility(View.GONE);
            llTotalAmount = (LinearLayout) rootView.findViewById(R.id.llTotalAmount);
            llJugnooCash = (LinearLayout) rootView.findViewById(R.id.llJugnooCash);
            llAmtToBePaid = (LinearLayout) rootView.findViewById(R.id.llAmtToBePaid);
            tvTotalAmount = (TextView) rootView.findViewById(R.id.tvTotalAmount);
            tvJugnooCashLabel = (TextView) rootView.findViewById(R.id.tvJugnooCashLabel);
            tvJugnooCashLabel.setText(getString(R.string.jugnoo_cash, getString(R.string.app_name)));
            tvJugnooCash = (TextView) rootView.findViewById(R.id.tvJugnooCash);
            tvAmtToBePaid = (TextView) rootView.findViewById(R.id.tvAmtToBePaid);
            tvTotalFareValue = (TextView) rootView.findViewById(R.id.tvTotalFareValue); tvTotalFareValue.setTypeface(tvTotalFareValue.getTypeface(), Typeface.BOLD);
            tvCashPaidValue = (TextView) rootView.findViewById(R.id.tvCashPaidValue); tvCashPaidValue.setTypeface(tvCashPaidValue.getTypeface(), Typeface.BOLD);
            textViewPaymentVia = (TextView) rootView.findViewById(R.id.textViewPaymentVia);


            relativeLayoutPaytm.setOnClickListener(onClickListenerPaymentOptionSelector);
            relativeLayoutMobikwik.setOnClickListener(onClickListenerPaymentOptionSelector);
            relativeLayoutFreeCharge.setOnClickListener(onClickListenerPaymentOptionSelector);
            rlUPI.setOnClickListener(onClickListenerPaymentOptionSelector);
            rlOtherModesToPay.setOnClickListener(onClickListenerPaymentOptionSelector);
            relativeLayoutIcici.setOnClickListener(onClickListenerPaymentOptionSelector);
            linearLayoutWalletContainer.removeAllViews();

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
                    tvPlanAmount.setText(String.format(activity.getResources().getString(R.string.rupees_value_format), Utils.getMoneyDecimalFormat().format(getAmount())));

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
                                tvAmount1.setText(String.format(activity.getResources().getString(R.string.rupees_value_format), Utils.getMoneyDecimalFormat().format(subscriptionsActivityList.get(i).getAmount())));

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
                                tvAmount2.setText(String.format(activity.getResources().getString(R.string.rupees_value_format), Utils.getMoneyDecimalFormat().format(subscriptionsActivityList.get(i).getAmount())));

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
                String fareRs = Utils.formatCurrencyValue(currency, fareToPay);
                netPayableAmount = fareToPay;
                paySlider.tvSlide.setText(getString(R.string.pay_format, fareRs));
                paySlider.sliderText.setText(R.string.swipe_right_to_pay);
                llRideInfo.setVisibility(View.VISIBLE);
                tvCashPaidValue.setText(fareRs);
                tvTotalFareValue.setText(Utils.formatCurrencyValue(currency, totalFare));
                textViewPaymentVia.setText(R.string.choose_payment_method);
            }
            else if(activity instanceof FatafatChatPayActivity){

                isFromFatafatChat = true;


                cvStarPlans.setVisibility(View.GONE);
                paySlider.sliderText.setText(R.string.swipe_right_to_pay);
                llRideInfo.setVisibility(View.GONE);
                textViewPaymentVia.setText(R.string.choose_payment_method);

                // initiate upi flow if payment is pending
                if(isUpiPending){
                    setPlaceOrderResponse(Data.getCurrentIciciUpiTransaction(AppConstant.ApplicationType.FEED));
                    if(jugnooVpaHandle==null){
                        jugnooVpaHandle = getPlaceOrderResponse().getIcici().getJugnooVpa();
                    }
                    onIciciUpiPaymentInitiated(Data.getCurrentIciciUpiTransaction(AppConstant.ApplicationType.FEED).getIcici(),
                            String.valueOf(Data.getCurrentIciciUpiTransaction(AppConstant.ApplicationType.FEED).getAmount()));
                }


            }


            linearLayoutOffers = (LinearLayout) rootView.findViewById(R.id.linearLayoutOffers);
            listViewOffers = (NonScrollListView) rootView.findViewById(R.id.listViewOffers);
            promoCouponsAdapter = new PromoCouponsAdapter(activity, R.layout.list_item_fresh_promo_coupon, promoCoupons, this);
            listViewOffers.setAdapter(promoCouponsAdapter);


            updateCouponsDataView();

        } catch (Exception e) {
            e.printStackTrace();
        }

        LocalBroadcastManager.getInstance(activity).registerReceiver(receiver, new IntentFilter(Constants.INTENT_ACTION_RAZOR_PAY_CALLBACK));


        return rootView;
    }



    @Override
    public void onResume() {
        super.onResume();
        fetchWalletBalance();

        try {

            if(isIciciPaymentRunnableInProgress){
                activity.getHandler().postDelayed(checkIciciUpiPaymentStatusRunnable,1 * 1000);
            }

            if(checkoutRequestPaymentDialog!=null && checkoutRequestPaymentDialog.isShowing()){
                checkoutRequestPaymentDialog.resumeTimer();
            }

            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(iciciStatusBroadcast, LOCAL_BROADCAST);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private BroadcastReceiver iciciStatusBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Integer orderStatus  = intent.getIntExtra(Constants.ICICI_ORDER_STATUS,Constants.NO_VALID_STATUS);
            if(orderStatus!=Constants.NO_VALID_STATUS){
                //Only if the payment is processing corresponding to that order ID
                if(getPlaceOrderResponse()!=null && getPlaceOrderResponse().getOrderId()==intent.getIntExtra(Constants.KEY_ORDER_ID,0)) {
                    onIciciStatusResponse(IciciPaymentRequestStatus.parseStatus(intent.getBooleanExtra(Constants.IS_MENUS_OR_DELIVERY, false), orderStatus, isFromFatafatChat),
                            intent.hasExtra(Constants.KEY_MESSAGE) ? intent.getStringExtra(Constants.KEY_MESSAGE) : "");
                }


            }

        }
    };

    @Override
    public void onPause() {
        super.onPause();
        activity.getHandler().removeCallbacks(checkIciciUpiPaymentStatusRunnable);
        if(checkoutRequestPaymentDialog!=null){
            checkoutRequestPaymentDialog.stopTimer();
        }
        try {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(iciciStatusBroadcast);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                    com.sabkuchfresh.utils.Utils.showToast(activity, activity.getString(R.string.error_enter_virtual_payment_address));
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
                new FreshWalletBalanceLowDialog(activity, callback).show(R.string.dont_have_enough_paytm_balance, amount, activity.getString(R.string.default_currency), activity.getString(R.string.default_currency), R.drawable.ic_paytm_big);
            }
            else if (paymentOption == PaymentOption.MOBIKWIK && Data.userData.getMobikwikEnabled() == 1) {
                String amount = com.sabkuchfresh.utils.Utils.getMoneyDecimalFormat().format(Math.ceil(Data.userData.getMobikwikBalance() - Math.ceil(getAmount())));
                new FreshWalletBalanceLowDialog(activity, callback).show(R.string.dont_have_enough_mobikwik_balance, amount, activity.getString(R.string.default_currency), activity.getString(R.string.default_currency), R.drawable.ic_mobikwik_big);
            }
            else if (paymentOption == PaymentOption.FREECHARGE && Data.userData.getFreeChargeEnabled() == 1) {
                String amount = com.sabkuchfresh.utils.Utils.getMoneyDecimalFormat().format(Math.ceil(Data.userData.getFreeChargeBalance() - Math.ceil(getAmount())));
                new FreshWalletBalanceLowDialog(activity, callback).show(R.string.dont_have_enough_freecharge_balance, amount, activity.getString(R.string.default_currency), activity.getString(R.string.default_currency), R.drawable.ic_freecharge_big);
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
                            // set default payment option only once
                            if(!isPaymentOptionSet){
                             isPaymentOptionSet = true;
                             setPaymentOption(MyApplication.getInstance().getWalletCore().getDefaultPaymentOption());
                            }
                            orderPaymentModes();
                            setPaymentOptionUI();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure() {
                        try {
                            // set default payment option only once
                            if(!isPaymentOptionSet){
                                isPaymentOptionSet = true;
                                setPaymentOption(MyApplication.getInstance().getWalletCore().getDefaultPaymentOption());
                            }
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
            // for fatafat chat pay send feed client id
            if(isFromFatafatChat){
                apiFetchWalletBalance.getBalance(true,true, null);
            }
            else {
                apiFetchWalletBalance.getBalance(true);
            }

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
                        } // show upi only if coming from fatafatChatPay
                        else if(paymentModeConfigData.getPaymentOption() == PaymentOption.ICICI_UPI.getOrdinal()
                                && activity instanceof FatafatChatPayActivity){
                            linearLayoutWalletContainer.addView(relativeLayoutIcici);
                            edtIciciVpa.removeTextChangedListener(selectIciciPaymentTextWatcher);
                            edtIciciVpa.setText(paymentModeConfigData.getUpiHandle());
                            if(paymentModeConfigData.getUpiHandle()!=null && paymentModeConfigData.getUpiHandle().length()>0){
                                edtIciciVpa.setSelection(paymentModeConfigData.getUpiHandle().length()-1);

                            }
                            edtIciciVpa.addTextChangedListener(selectIciciPaymentTextWatcher);
                            jugnooVpaHandle =  paymentModeConfigData.getJugnooVpaHandle();
                            tvLabelIciciUpi.setText(activity.getString(R.string.label_below_icici_payment_edt, jugnooVpaHandle));
                            tvUPICashback.setText(!TextUtils.isEmpty(paymentModeConfigData.getUpiCashbackValue())?paymentModeConfigData.getUpiCashbackValue():"");

                        }
                        else if(!fromStar){
                            if (paymentModeConfigData.getPaymentOption() == PaymentOption.RAZOR_PAY.getOrdinal()) {
                                linearLayoutWalletContainer.addView(rlOtherModesToPay);
                                tvOtherModesToPay.setText(paymentModeConfigData.getDisplayName());
                            } else if (paymentModeConfigData.getPaymentOption() == PaymentOption.UPI_RAZOR_PAY.getOrdinal()) {
                                linearLayoutWalletContainer.addView(rlUPI);
                                tvUPI.setText(paymentModeConfigData.getDisplayName());
                            }
                        }
                    }
                }
                cvPaymentMethodContainer.setVisibility(View.VISIBLE);
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

        @Override
        public void onWalletOptionClick() {

        }

        @Override
        public int getSelectedPaymentOption() {
            return getPaymentOption().getOrdinal();
        }

        @Override
        public void setSelectedPaymentOption(int paymentOption) {
            setPaymentOption(MyApplication.getInstance().getWalletCore().getPaymentOptionFromInt(paymentOption));
        }

        @Override
        public boolean isRazorpayEnabled() {
            return true;
        }
    };

    private void setPaymentOptionUI() {
        try {
            setPaymentOption(MyApplication.getInstance().getWalletCore().getPaymentOptionFromInt(
                    MyApplication.getInstance().getWalletCore().getPaymentOptionAccAvailability(getPaymentOption().getOrdinal())));

            textViewPaytmValue.setText(String.format(activity.getResources()
                    .getString(R.string.rupees_value_format), Data.userData.getPaytmBalanceStr()));
            textViewPaytmValue.setTextColor(Data.userData.getPaytmBalanceColor(activity));
            textViewMobikwikValue.setText(String.format(activity.getResources()
                    .getString(R.string.rupees_value_format), Data.userData.getMobikwikBalanceStr()));
            textViewMobikwikValue.setTextColor(Data.userData.getMobikwikBalanceColor(activity));
            textViewFreeChargeValue.setText(String.format(activity.getResources()
                    .getString(R.string.rupees_value_format), Data.userData.getFreeChargeBalanceStr()));
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
            }else if (getPaymentOption() == PaymentOption.ICICI_UPI) {
                imageViewIcici.setImageResource(R.drawable.ic_radio_button_selected);

            }

            edtIciciVpa.setVisibility(getPaymentOption() == PaymentOption.ICICI_UPI?View.VISIBLE:View.GONE);
            tvLabelIciciUpi.setVisibility(getPaymentOption() == PaymentOption.ICICI_UPI?View.VISIBLE:View.GONE);

            // in case we come from fatafat chat also enable jugnoo cash
            if(isFromFatafatChat){

                llFatafatChatPay.setVisibility(View.VISIBLE);
                if(Data.userData.getJugnooBalance()!=0){
                    llTotalAmount.setVisibility(View.VISIBLE);
                    llJugnooCash.setVisibility(View.VISIBLE);
                    llAmtToBePaid.setVisibility(View.VISIBLE);
                    tvTotalAmount.setText(activity.getString(R.string.rupees_value_format,
                            Utils.getDoubleTwoDigits((double) Math.round(fareToPay))));

                    double jcToShow = Math.min(fareToPay,Data.userData.getJugnooBalance());
                    tvJugnooCash.setText(activity.getString(R.string.rupees_value_format,
                            Utils.getDoubleTwoDigits((double) Math.round(jcToShow))));

                    // in case of upiPending fareToPay will be equal to netPayableAmt as object saved
                    // in db for icici pending transaction has net amount ( after jc deduction )
                    if(isUpiPending){
                        netPayableAmount = fareToPay;
                    }
                    else {
                       netPayableAmount = fareToPay - jcToShow;
                    }
                    if (netPayableAmount < 0) {
                        netPayableAmount = 0;
                    }
                    String netAmtToShow = (activity.getString(R.string.rupees_value_format,
                            Utils.getDoubleTwoDigits((double) Math.round(netPayableAmount))));
                    tvAmtToBePaid.setText(netAmtToShow);
                    // update pay slider value
                    String sliderText;
                    if(netPayableAmount==0){
                        sliderText = activity.getString(R.string.pay).toUpperCase();
                    }
                    else {
                        sliderText = activity.getString(R.string.pay).toUpperCase()+ " " +netAmtToShow;
                    }
                    paySlider.tvSlide.setText(sliderText);
                }
                else {
                    // only show net amount
                    llTotalAmount.setVisibility(View.GONE);
                    llJugnooCash.setVisibility(View.GONE);
                    llAmtToBePaid.setVisibility(View.VISIBLE);
                    netPayableAmount = fareToPay;
                    String netAmtToShow = activity.getString(R.string.rupees_value_format,
                            Utils.getDoubleTwoDigits((double) Math.round(fareToPay)));
                    tvAmtToBePaid.setText(netAmtToShow);
                    String sliderText = activity.getString(R.string.pay).toUpperCase()+ " " + netAmtToShow;
                    paySlider.tvSlide.setText(sliderText);

                }

            }
            else {
                llFatafatChatPay.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public PromoCoupon getSelectedCoupon() {
        return getSelectedPromoCoupon();
    }

    @Override
    public boolean setSelectedCoupon(int position, PromoCoupon pc) {
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
        } else if(isFromFatafatChat){
            makeFatafatChatPayment();
        }
        else {
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
    private PromoCoupon noSelectionCoupon = new CouponInfo(-1, "");
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

    /**
     * Hits the place order api for orders coming from chat
     */
    public void makeFatafatChatPayment(){

        HashMap<String,String> params = new HashMap<>();
        params.put(Constants.KEY_ACCESS_TOKEN,Data.userData.accessToken);
        params.put(Constants.KEY_ORDER_ID,String.valueOf(orderId));
        params.put(Constants.KEY_PAYMENT_MODE,String.valueOf(getPaymentOption().getOrdinal()));
        params.put(Constants.KEY_AMOUNT,String.valueOf(getAmount()));

        if (getPaymentOption().getOrdinal() == PaymentOption.ICICI_UPI.getOrdinal()) {
            params.put(Constants.KEY_VPA, edtIciciVpa.getText().toString().trim());
        }

        new ApiCommon<PaymentResponse>(activity).showLoader(true).execute(params, ApiName.FEED_PAY_FOR_ORDER,
                new APICommonCallback<PaymentResponse>() {
                    @Override
                    public boolean onNotConnected() {
                        slideInitialDelay();
                        return false;
                    }

                    @Override
                    public boolean onException(final Exception e) {
                        slideInitialDelay();
                        return false;
                    }

                    @Override
                    public void onSuccess(final PaymentResponse response, final String message, final int flag) {

                        try {
                            if(flag == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
                                if (getPaymentOption() == PaymentOption.RAZOR_PAY) {
                                    // razor pay case send data to RazorPay Checkout page
                                    activity.startRazorPayPayment(response.getData().getRazorpayData(), isRazorUPI);
                                } else {
                                    PaymentResponse.PaymentData paymentData = response.getData().getPaymentData();
                                    if(Data.userData != null ) {
                                        Data.userData.setJugnooBalance(paymentData.getJugnooBalance());
                                        if (getPaymentOption() == PaymentOption.PAYTM) {
                                            Data.userData.setPaytmBalance(Data.userData.getPaytmBalance() - paymentData.getPaytmDeducted());
                                            fatafatChatOrderPaidSuccess();
                                        } else if (getPaymentOption() == PaymentOption.MOBIKWIK) {
                                            Data.userData.setMobikwikBalance(Data.userData.getMobikwikBalance() - paymentData.getMobikwikDeducted());
                                            fatafatChatOrderPaidSuccess();
                                        } else if (getPaymentOption() == PaymentOption.FREECHARGE) {
                                            Data.userData.setFreeChargeBalance(Data.userData.getFreeChargeBalance() - paymentData.getFreechargeDeducted());
                                            fatafatChatOrderPaidSuccess();
                                        } else if(getPaymentOption()==PaymentOption.ICICI_UPI){

                                            if(paymentData.getIcici()!=null){
                                                // Icici Upi Payment Initiated, prepare the order response and add extra key for fatafat chat
                                                PlaceOrderResponse placeOrderResponse = new PlaceOrderResponse();
                                                placeOrderResponse.setAmount(paymentData.getAmount());
                                                placeOrderResponse.setOrderId(orderId);
                                                placeOrderResponse.setIcici(paymentData.getIcici());
                                                placeOrderResponse.setPaymentMode(String.valueOf(PaymentOption.ICICI_UPI.getOrdinal()));
                                                placeOrderResponse.setPayViaFatafatChat(true);

                                                setPlaceOrderResponse(placeOrderResponse);
                                                onIciciUpiPaymentInitiated(paymentData.getIcici(),String.valueOf(paymentData.getAmount()));
                                            }
                                            else {
                                                //handle success
                                                fatafatChatOrderPaidSuccess();
                                            }


                                        }
                                    }


                                }
                            } else {
                                DialogPopup.alertPopup(activity, "", message);
                                slideInitialDelay();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
                            slideInitialDelay();
                        }
                    }

                    @Override
                    public boolean onError(final PaymentResponse paymentResponse, final String message, final int flag) {
                        slideInitialDelay();
                        return false;
                    }

                    @Override
                    public boolean onFailure(final RetrofitError error) {
                        slideInitialDelay();
                        return false;
                    }

                    @Override
                    public void onNegativeClick() {
                        slideInitialDelay();

                    }
                });

    }


    public void initiateRideEndPaymentAPI() {

        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.KEY_ENGAGEMENT_ID, String.valueOf(engagementId));
        params.put(Constants.KEY_PREFERRED_PAYMENT_MODE, String.valueOf(getPaymentOption().getOrdinal()));

        new ApiCommon<PaymentResponse>(activity).showLoader(true).execute(params, ApiName.INITIATE_RIDE_END_PAYMENT,
                new APICommonCallback<PaymentResponse>() {
                    @Override
                    public boolean onNotConnected() {
                        slideInitialDelay();
                        return false;
                    }

                    @Override
                    public boolean onException(Exception e) {
                        slideInitialDelay();
                        return false;

                    }

                    @Override
                    public void onSuccess(final PaymentResponse response, String message, int flag) {
                        try {
                            if(flag == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
                                if (getPaymentOption() == PaymentOption.RAZOR_PAY) {
                                    // razor pay case send data to RazorPay Checkout page
                                    activity.setPurchaseSubscriptionResponse(engagementId,
                                            response.getData().getRazorpayData().getAuthOrderId());
                                    activity.startRazorPayPayment(response.getData().getRazorpayData(), isRazorUPI);
                                } else {
                                    PaymentResponse.PaymentData paymentData = response.getData().getPaymentData();
                                    if(Data.userData != null && Data.autoData != null && Data.autoData.getEndRideData() != null) {
                                        Data.userData.setJugnooBalance(paymentData.getJugnooBalance());
//                                        Data.autoData.getEndRideData().paidUsingWallet = paymentData.getJugnooDeducted();
                                        if (getPaymentOption() == PaymentOption.PAYTM) {
                                            Data.userData.setPaytmBalance(Data.userData.getPaytmBalance() - paymentData.getPaytmDeducted());
                                            Data.autoData.getEndRideData().paidUsingPaytm = paymentData.getPaytmDeducted();
                                        } else if (getPaymentOption() == PaymentOption.MOBIKWIK) {
                                            Data.userData.setMobikwikBalance(Data.userData.getMobikwikBalance() - paymentData.getMobikwikDeducted());
                                            Data.autoData.getEndRideData().paidUsingMobikwik = paymentData.getMobikwikDeducted();
                                        } else if (getPaymentOption() == PaymentOption.FREECHARGE) {
                                            Data.userData.setFreeChargeBalance(Data.userData.getFreeChargeBalance() - paymentData.getFreechargeDeducted());
                                            Data.autoData.getEndRideData().paidUsingFreeCharge = paymentData.getFreechargeDeducted();
                                        }
                                    }
                                    rideEndPaymentSuccess(paymentData.getRemaining(), message);
                                }
                            } else {
                                DialogPopup.alertPopup(activity, "", message);
                                slideInitialDelay();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
                            slideInitialDelay();
                        }
                    }

                    @Override
                    public boolean onError(PaymentResponse feedCommonResponse, String message, int flag) {
                        slideInitialDelay();
                        return false;
                    }

                    @Override
                    public boolean onFailure(RetrofitError error) {
                        slideInitialDelay();
                        return false;
                    }

                    @Override
                    public void onNegativeClick() {
                        slideInitialDelay();

                    }
                });
    }

    private Handler handler;
    private Handler getHandler(){
        if(handler == null){
            handler = new Handler();
        }
        return handler;
    }
    private Runnable runnable;
    private Runnable getRunnable(){
        if(runnable == null){
            runnable = new Runnable() {
                @Override
                public void run() {
                    paySlider.setSlideInitial();
                }
            };
        }
        return runnable;
    }
    private void slideInitialDelay(){
        getHandler().postDelayed(getRunnable(), 200);

    }



    /**
     * Called when payment has been successful for fatafat chat order
     */
    private void fatafatChatOrderPaidSuccess(){
       if(activity!=null && !activity.isFinishing()){
           paySlider.setSlideInitial();
           activity.onBackPressed();
       }
    }


    private void rideEndPaymentSuccess(double remaining, String message) {
        if (Data.autoData != null && Data.autoData.getEndRideData() != null) {
			Data.autoData.getEndRideData().setShowPaymentOptions(0);
			Data.autoData.getEndRideData().toPay = remaining;
		}
        DialogPopup.alertPopupWithListener(activity, "", message, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.onBackPressed();
                if(activity instanceof HomeActivity){
                    ((HomeActivity)activity).updateRideEndPayment();
                    ((HomeActivity)activity).setUserData();
                }
				paySlider.setSlideInitial();
			}
		});
    }

    private Integer getAmount(){
        if(fromStar){
            return subscription.getAmount();
        }else if(isFromFatafatChat){
            // if coming from fatafat return netamount ( that includes jugoo cash if present )
            return (int)netPayableAmount;
        } else {
            return (int)fareToPay;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(activity).unregisterReceiver(receiver);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        DialogPopup.dismissLoadingDialog();
                        String response = intent.getStringExtra(Constants.KEY_RESPONSE);
                        if (!TextUtils.isEmpty(response)) {
                            razorpayServiceCallback(new JSONObject(response));
                        } else {
                            razorpayServiceCallback(null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };


    public void razorpayServiceCallback(JSONObject jsonObject) {
        try {
            int flag = jsonObject.getInt(Constants.KEY_FLAG);
            String message = JSONParser.getServerMessage(jsonObject);
            if (flag == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
                double remaining = 0;
                try {
                    remaining = jsonObject.getJSONObject(Constants.KEY_DATA).getJSONObject(Constants.KEY_PAYMENT_DATA).optDouble(Constants.KEY_REMAINING, 0);
                    if(Data.autoData != null && Data.autoData.getEndRideData() != null) {
                        Data.autoData.getEndRideData().paidUsingRazorpay = jsonObject.getJSONObject(Constants.KEY_DATA).getJSONObject(Constants.KEY_PAYMENT_DATA).optDouble(Constants.KEY_RAZOR_PAY_DEDUCTED, 0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // if we come from chat then show fatafat pay success
                if(isFromFatafatChat){
                    fatafatChatOrderPaidSuccess();
                }
                else {
                    rideEndPaymentSuccess(remaining, message);
                }
            } else if (flag == ApiResponseFlags.ACTION_FAILED.getOrdinal()) {
                DialogPopup.alertPopup(activity, "", message);
                paySlider.setSlideInitial();
            }
        } catch (Exception e) {
            e.printStackTrace();
            DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
            paySlider.setSlideInitial();
        }
    }


    private CheckoutRequestPaymentDialog checkoutRequestPaymentDialog;
    private ApiCancelOrder apiCancelOrder;

    private void showRequestPaymentDialog(String amount, long expiryTimeLeft, ArrayList<String> reasonList,Long timerStartedAt) {

        if (checkoutRequestPaymentDialog == null) {
            checkoutRequestPaymentDialog = CheckoutRequestPaymentDialog.init(activity);
        }
        checkoutRequestPaymentDialog.setData(amount, timerStartedAt, expiryTimeLeft, reasonList, new CheckoutRequestPaymentDialog.CheckoutRequestPaymentListener() {
            @Override
            public void onCancelClick(String reason) {
                if(getPlaceOrderResponse()!=null){
                    if(apiCancelOrder ==null){
                        apiCancelOrder = new ApiCancelOrder(getActivity(), new ApiCancelOrder.Callback() {
                            @Override
                            public void onSuccess(String message) {
                                onIciciStatusResponse(IciciPaymentOrderStatus.CANCELLED,message);
                            }

                            @Override
                            public void onFailure() {

                            }

                            @Override
                            public void onRetry(View view) {

                            }

                            @Override
                            public void onNoRetry(View view) {

                            }
                        });
                    }

                    String clientId; int productType;

                    // if we come from fatafat chat pay, then return feed client id and Feed product type ( api hosted on fatafat )
                    if(isFromFatafatChat){
                        clientId = Config.getFeedClientId();
                        productType = ProductType.FEED.getOrdinal();
                    }
                    else {
                        clientId = Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId());
                        productType = isMenusOrDeliveryOpen()? ProductType.MENUS.getOrdinal():ProductType.FRESH.getOrdinal();
                    }

                    apiCancelOrder.hit(getPlaceOrderResponse().getOrderId(),clientId,-1,productType,reason,"");
                }
            }

            @Override
            public void onTimerComplete() {
                isIciciPaymentRunnableInProgress = false;
                activity.getHandler().removeCallbacks(checkIciciUpiPaymentStatusRunnable);
                checkIciciPaymentStatusApi();

            }

            @Override
            public void onRetryClick() {
                checkIciciPaymentStatusApi();
            }
        }, jugnooVpaHandle).showDialog();

    }

    public boolean isMenusOrDeliveryOpen(){
        return activity.getAppType()== AppConstant.ApplicationType.MENUS || activity.getAppType()==AppConstant.ApplicationType.DELIVERY_CUSTOMER;
    }

    private void onIciciUpiPaymentInitiated(PlaceOrderResponse.IciciUpi icici, String amount) {
        checkoutRequestPaymentDialog=null;
        currentStatus=null;
        isIciciPaymentRunnableInProgress = true;
        TOTAL_EXPIRY_TIME_ICICI_UPI = icici.getExpirationTimeMillis();
        DELAY_ICICI_UPI_STATUS_CHECK = icici.getPollingTimeMillis();
        Long timerStartedAt = icici.getTimerStartedAt()==null?System.currentTimeMillis():icici.getTimerStartedAt();
        icici.setTimerStartedAt(timerStartedAt);
        Data.saveCurrentIciciUpiTransaction(getPlaceOrderResponse(), AppConstant.ApplicationType.FEED);
        showRequestPaymentDialog(amount, TOTAL_EXPIRY_TIME_ICICI_UPI,icici.getReasonList(),icici.getTimerStartedAt()==null?System.currentTimeMillis():icici.getTimerStartedAt());
        activity.getHandler().postDelayed(checkIciciUpiPaymentStatusRunnable, DELAY_ICICI_UPI_STATUS_CHECK);

    }


    private  boolean isIciciPaymentRunnableInProgress;
    private IciciPaymentOrderStatus currentStatus ;
    private void onIciciStatusResponse(IciciPaymentOrderStatus status,String toastMessage) {
        if (currentStatus==null || status!=currentStatus) {
            switch (status) {
                case FAILURE:
                case EXPIRED:
                case CANCELLED:
                    isIciciPaymentRunnableInProgress = false;
                    activity.getHandler().removeCallbacks(checkIciciUpiPaymentStatusRunnable);
                    if (checkoutRequestPaymentDialog != null && checkoutRequestPaymentDialog.isShowing()) {
                        checkoutRequestPaymentDialog.dismiss();
                    }
                    Toast.makeText(activity, toastMessage, Toast.LENGTH_SHORT).show();
                    Data.deleteCurrentIciciUpiTransaction(AppConstant.ApplicationType.FEED);
                    slideInitialDelay();
                    break;
                case SUCCESSFUL:
                case PROCESSED:
                case COMPLETED:
                    isIciciPaymentRunnableInProgress = false;
                    activity.getHandler().removeCallbacks(checkIciciUpiPaymentStatusRunnable);

                    if (checkoutRequestPaymentDialog != null && checkoutRequestPaymentDialog.isShowing()) {
                        checkoutRequestPaymentDialog.dismiss();
                    }

                    //handle success
                    slideInitialDelay();
                    fatafatChatOrderPaidSuccess();
                    Data.deleteCurrentIciciUpiTransaction(AppConstant.ApplicationType.FEED);


                    break;
                case PENDING:
                    //Keep waiting for next status
                    break;



            }
        }
        currentStatus=status;


    }



    private long DELAY_ICICI_UPI_STATUS_CHECK = 30 * 1000;
    private long TOTAL_EXPIRY_TIME_ICICI_UPI = 4 * 60 * 1000;
    private Callback<IciciPaymentRequestStatus> iciciPaymentStatusCallback;

    private Runnable checkIciciUpiPaymentStatusRunnable = new Runnable() {
        @Override
        public void run() {

            checkIciciPaymentStatusApi();
            activity.getHandler().postDelayed(this, DELAY_ICICI_UPI_STATUS_CHECK);


        }
    };



    private void checkIciciPaymentStatusApi() {
        if (MyApplication.getInstance().isOnline()) {
            HashMap<String, String> params = new HashMap<>();
            HomeUtil.addDefaultParams(params);
            params.put(Constants.KEY_ORDER_ID, String.valueOf(getPlaceOrderResponse().getOrderId()));
            params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);

            // if we come from fatafat chat payment, send feed client id
            if(isFromFatafatChat){
                params.put(Constants.KEY_CLIENT_ID,Config.getFeedClientId());
            }
            else {
                params.put(Constants.KEY_CLIENT_ID, Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()));
            }
            if (iciciPaymentStatusCallback == null) {

                iciciPaymentStatusCallback = new Callback<IciciPaymentRequestStatus>() {
                    @Override
                    public void success(IciciPaymentRequestStatus commonResponse, Response response) {
                        if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, commonResponse.getFlag(), commonResponse.getError(), commonResponse.getMessage())) {

                            if (commonResponse.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
                                onIciciStatusResponse(commonResponse.getStatus(),commonResponse.getToastMessage());

                            }

                        }
                        if(checkoutRequestPaymentDialog!=null&&checkoutRequestPaymentDialog.isTimerExpired()){
                            checkoutRequestPaymentDialog.enableRetryButton();
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {

                        if(checkoutRequestPaymentDialog!=null&&checkoutRequestPaymentDialog.isTimerExpired()){
                            checkoutRequestPaymentDialog.enableRetryButton();
                            checkoutRequestPaymentDialog.toggleConnectionState(false);

                        }
                    }

                };
            }

                // the fatafat payment status for upi shall be used on the menus endpoint
                RestClient.getMenusApiService().checkPaymentStatus(params, iciciPaymentStatusCallback);

        }else{
            if(checkoutRequestPaymentDialog!=null&&checkoutRequestPaymentDialog.isTimerExpired()){
                checkoutRequestPaymentDialog.enableRetryButton();
                checkoutRequestPaymentDialog.toggleConnectionState(false);

            }
            Log.e("TAG", "No net tried to hit get status icici api");
        }
    }

    //placeOrderResponse cached for PAY and RAZORPAY payment callbacks
    private PlaceOrderResponse placeOrderResponse;
    public void setPlaceOrderResponse(PlaceOrderResponse placeOrderResponse){
        this.placeOrderResponse = placeOrderResponse;
        if(placeOrderResponse != null) {
            Paper.book().write(PaperDBKeys.DB_PLACE_ORDER_RESP, placeOrderResponse);
        } else {
            Paper.book().delete(PaperDBKeys.DB_PLACE_ORDER_RESP);
        }
    }

    public PlaceOrderResponse getPlaceOrderResponse(){
        if(placeOrderResponse == null){
            placeOrderResponse = Paper.book().read(PaperDBKeys.DB_PLACE_ORDER_RESP);
        }
        return placeOrderResponse;
    }
}

package com.sabkuchfresh.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.analytics.ecommerce.ProductAction;
import com.sabkuchfresh.analytics.FlurryEventLogger;
import com.sabkuchfresh.analytics.FlurryEventNames;
import com.sabkuchfresh.analytics.NudgeClient;
import com.sabkuchfresh.apis.ApiPaytmCheckBalance;
import com.sabkuchfresh.datastructure.AddPaymentPath;
import com.sabkuchfresh.datastructure.ApiResponseFlags;
import com.sabkuchfresh.datastructure.DialogErrorType;
import com.sabkuchfresh.datastructure.PaymentOption;
import com.sabkuchfresh.datastructure.SPLabels;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.home.FreshOrderCompleteDialog;
import com.sabkuchfresh.home.FreshPaytmBalanceLowDialog;
import com.sabkuchfresh.retrofit.RestClient;
import com.sabkuchfresh.retrofit.model.Category;
import com.sabkuchfresh.retrofit.model.PlaceOrderResponse;
import com.sabkuchfresh.retrofit.model.SubItem;
import com.sabkuchfresh.utils.ASSL;
import com.sabkuchfresh.utils.AppConstant;
import com.sabkuchfresh.utils.AppStatus;
import com.sabkuchfresh.utils.Constants;
import com.sabkuchfresh.utils.Data;
import com.sabkuchfresh.utils.DateOperations;
import com.sabkuchfresh.utils.DialogPopup;
import com.sabkuchfresh.utils.Fonts;
import com.sabkuchfresh.utils.JSONParser;
import com.sabkuchfresh.utils.Log;
import com.sabkuchfresh.utils.Prefs;
import com.sabkuchfresh.utils.Utils;
import com.sabkuchfresh.wallet.PaymentActivity;
import com.sabkuchfresh.wallet.UserDebtDialog;
import com.sabkuchfresh.widgets.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class FreshPaymentFragment extends Fragment implements FlurryEventNames {

    private final String TAG = FreshPaymentFragment.class.getSimpleName();
    private LinearLayout linearLayoutRoot;
//	private TextView textViewPayForItems, textViewPayFromjc;

    RelativeLayout relativeLayoutSubTotalLayout, relativeLayoutJC, relativeLayoutPromo, relativeLayoutTotalLayout, relativeLayoutdelivery;
    TextView textViewTotalValue, textViewJCValue, textViewPromoValue, textViewPayableValue, textViewAddPromo, textpaymentoption,
            textViewSubTotal, textViewSubTotalValue, textViewdelivery, textViewdeliveryValue;
    private EditText appPromoEdittext;
    private View viewLine;

    private LinearLayout linearLayoutCash;
    private ImageView imageViewCashRadio;

    private RelativeLayout relativeLayoutPaytm;
    private ImageView imageViewPaytmRadio;
    private TextView textViewPaytm, textViewPaytmValue;
    private ProgressWheel progressBarPaytm;
    private Button buttonPlaceOrder, applyButton;

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

    @Override
    public void onStart() {
        super.onStart();
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

        textViewAddPromo = (TextView) rootView.findViewById(R.id.textViewAddPromo);
        textViewAddPromo.setTypeface(Fonts.mavenRegular(activity));

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
        textpaymentoption = ((TextView) rootView.findViewById(R.id.textViewPatmentOption));
        textpaymentoption.setTypeface(Fonts.mavenRegular(activity));

        appPromoEdittext = (EditText) rootView.findViewById(R.id.ed_promo_code);
        appPromoEdittext.setTypeface(Fonts.mavenRegular(activity));

        applyButton = (Button) rootView.findViewById(R.id.apply_button);

        linearLayoutCash = (LinearLayout) rootView.findViewById(R.id.linearLayoutCash);
        imageViewCashRadio = (ImageView) rootView.findViewById(R.id.imageViewCashRadio);

        relativeLayoutSubTotalLayout = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutSubTotalLayout);
        relativeLayoutJC = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutJC);
        relativeLayoutPromo = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutPromo);
        relativeLayoutPaytm = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutPaytm);
        relativeLayoutTotalLayout = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutTotalLayout);
        relativeLayoutdelivery = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutdelivery);

        imageViewPaytmRadio = (ImageView) rootView.findViewById(R.id.imageViewPaytmRadio);
        textViewPaytm = (TextView) rootView.findViewById(R.id.textViewPaytm);
        textViewPaytm.setTypeface(Fonts.mavenLight(activity));
        textViewPaytmValue = (TextView) rootView.findViewById(R.id.textViewPaytmValue);
        textViewPaytmValue.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
        progressBarPaytm = (ProgressWheel) rootView.findViewById(R.id.progressBarPaytm);
        progressBarPaytm.setVisibility(View.GONE);
        buttonPlaceOrder = (Button) rootView.findViewById(R.id.buttonPlaceOrder);
        buttonPlaceOrder.setTypeface(Fonts.mavenRegular(activity));

        appPromoEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()==0)
                    applyButton.setBackgroundColor(activity.getResources().getColor(R.color.apply_btn_normal));
                else
                    applyButton.setBackgroundColor(activity.getResources().getColor(R.color.theme_color));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(appPromoEdittext.getText().toString().trim()))
                    applyPromo();
                else
                    Toast.makeText(activity, "Please enter code", Toast.LENGTH_SHORT).show();
            }
        });
        linearLayoutCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FlurryEventLogger.event(PAYMENT_SCREEN, METHOD_CHANGED, CASH);
                activity.setPaymentOption(PaymentOption.CASH);
                setPaymentOptionUI();
                NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_FRESH_CASH_CLICKED, null);
            }
        });

        relativeLayoutPaytm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    FlurryEventLogger.event(PAYMENT_SCREEN, METHOD_CHANGED, PAYTM);
                    if (Data.userData.getPaytmBalance() >= getTotalPriceWithDeliveryCharges()) {
                        activity.setPaymentOption(PaymentOption.PAYTM);
                        setPaymentOptionUI();
                        NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_FRESH_PAYTM_CLICKED, null);

                    } else if (Data.userData.getPaytmError() == 1) {
                        DialogPopup.alertPopup(activity, "", activity.getResources().getString(R.string.paytm_error_cash_select_cash));

                    } else {
                        showPaytmBalanceLowDialog();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        buttonPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_FRESH_PLACE_ORDER_CLICKED, null);
                FlurryEventLogger.event(PAYMENT_SCREEN, ORDER_PLACED, ORDER_PLACED);
                placeOrder();
            }
        });


        subTotalAmount = activity.updateCartValuesGetTotalPrice().first;
        promoAmount = 0;

        if (activity.getProductsResponse().getDeliveryInfo().getMinAmount() > subTotalAmount) {
            deliveryAmount = activity.getProductsResponse().getDeliveryInfo().getDeliveryCharges();
        }
        jcAmount = getTotalJCPaid();
        totalAmount = subTotalAmount - promoAmount + deliveryAmount;
        payableAmount = subTotalAmount - promoAmount + deliveryAmount - jcAmount;


//        scrollView = (ScrollView) rootView.findViewById(R.id.scrollView);
//        textViewScroll = (TextView) rootView.findViewById(R.id.textViewScroll);
//        linearLayoutRoot.getViewTreeObserver().addOnGlobalLayoutListener(new KeyboardLayoutListener(linearLayoutRoot, textViewScroll, new KeyboardLayoutListener.KeyBoardStateHandler() {
//            @Override
//            public void keyboardOpened() {
//                if (!scrolled) {
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//
//                                scrollView.smoothScrollTo(0, applyButton.getTop());
//
//                        }
//                    }, 100);
//                    scrolled = true;
//                }
//            }
//
//            @Override
//            public void keyBoardClosed() {
//                scrolled = false;
//            }
//        }));

//		textViewPayForItems.setText(String.format(activity.getResources().getString(R.string.pay_rupees_using_format),
//                Utils.getMoneyDecimalFormat().format(getTotalPriceWithDeliveryCharges())));
//
//        double paidViajc = getTotalJCPaid();
//        if(paidViajc>0) {
//            textViewPayFromjc.setVisibility(View.VISIBLE);
//            textViewPayFromjc.setText(String.format(activity.getResources().getString(R.string.pay_rupees_using_jc),
//                    Utils.getMoneyDecimalFormat().format(paidViajc)));
//        } else {
//            textViewPayFromjc.setVisibility(View.GONE);
//        }

        updateUI();

        FlurryEventLogger.checkoutTrackEvent(AppConstant.EventTracker.PAYMENT, activity.productList);
        getBalance();


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
            textViewPromoValue.setText("" + promoAmount);
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
            relativeLayoutJC.setVisibility(View.VISIBLE);
            relativeLayoutTotalLayout.setVisibility(View.VISIBLE);

            textViewJCValue.setText(String.format(activity.getResources().getString(R.string.rupees_value_format),
                    Utils.getMoneyDecimalFormat().format(jcAmount)));

            textViewTotalValue.setText(String.format(activity.getResources().getString(R.string.rupees_value_format),
                    Utils.getMoneyDecimalFormat().format(totalAmount)));
        } else {
            relativeLayoutJC.setVisibility(View.GONE);
            relativeLayoutTotalLayout.setVisibility(View.GONE);
        }



    }

    @Override
    public void onResume() {
        super.onResume();
        setPaymentOptionUI();
    }

    private ApiPaytmCheckBalance apiPaytmCheckBalance;

    public ApiPaytmCheckBalance getApiPaytmCheckBalance() {
        if (apiPaytmCheckBalance == null) {
            apiPaytmCheckBalance = new ApiPaytmCheckBalance(activity, new ApiPaytmCheckBalance.Callback() {
                @Override
                public void onSuccess() {
                    activity.setPaymentOption(PaymentOption.PAYTM);
                    setPaymentOptionUI();
                }

                @Override
                public void onFailure() {
                    activity.setPaymentOption(PaymentOption.CASH);
                    setPaymentOptionUI();
                }

                @Override
                public void onFinish() {

                }

                @Override
                public void onRetry(View view) {
                    getBalance();
                }

                @Override
                public void onNoRetry(View view) {

                }
            });
        }
        return apiPaytmCheckBalance;
    }

    private void getBalance() {
        try {
            getApiPaytmCheckBalance().getBalance(Data.userData.paytmEnabled, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setPaymentOptionUI() {
        try {
            if (PaymentOption.PAYTM == activity.getPaymentOption()) {
                if (Data.userData.getPaytmStatus().equalsIgnoreCase(Data.PAYTM_STATUS_ACTIVE)) {
                    progressBarPaytm.setVisibility(View.GONE);
                } else if (Data.userData.getPaytmStatus().equalsIgnoreCase(Data.PAYTM_STATUS_INACTIVE)) {
                    activity.setPaymentOption(PaymentOption.CASH);
                    progressBarPaytm.setVisibility(View.GONE);
                } else {
                    activity.setPaymentOption(PaymentOption.CASH);
                    progressBarPaytm.setVisibility(View.VISIBLE);
                }
            } else {
                activity.setPaymentOption(PaymentOption.CASH);
                progressBarPaytm.setVisibility(View.GONE);
            }
            textViewPaytmValue.setText(String.format(activity.getResources()
                    .getString(R.string.rupees_value_format_without_space), Data.userData.getPaytmBalanceStr()));
            textViewPaytmValue.setTextColor(Data.userData.getPaytmBalanceColor(activity));

            if (Data.userData.paytmEnabled == 1 && Data.userData.getPaytmStatus().equalsIgnoreCase(Data.PAYTM_STATUS_ACTIVE)) {
                textViewPaytmValue.setVisibility(View.VISIBLE);
                textViewPaytm.setText(activity.getResources().getString(R.string.nl_paytm_wallet));
            } else {
                textViewPaytmValue.setVisibility(View.GONE);
                textViewPaytm.setText(activity.getResources().getString(R.string.nl_add_paytm_wallet));
            }

            if (Data.userData.getPaytmError() == 1) {
                activity.setPaymentOption(PaymentOption.CASH);
                relativeLayoutPaytm.setVisibility(View.GONE);
            } else {
                relativeLayoutPaytm.setVisibility(View.VISIBLE);
            }

            if (activity.getPaymentOption() == null
                    || activity.getPaymentOption() == PaymentOption.CASH) {
                imageViewCashRadio.setImageResource(R.drawable.radio_selected_icon);
                imageViewPaytmRadio.setImageResource(R.drawable.radio_unselected_icon);
            } else {
                imageViewCashRadio.setImageResource(R.drawable.radio_unselected_icon);
                imageViewPaytmRadio.setImageResource(R.drawable.radio_selected_icon);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void placeOrder() {
        boolean goAhead = true;
        if (activity.getPaymentOption().getOrdinal() == PaymentOption.PAYTM.getOrdinal()) {
            if (Data.userData.getPaytmBalance() < getTotalPriceWithDeliveryCharges()) {
                if (Data.userData.getPaytmError() == 1) {
                    DialogPopup.alertPopup(activity, "", activity.getResources().getString(R.string.paytm_error_cash_select_cash));
                } else {
                    showPaytmBalanceLowDialog();
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
                            placeOrderApi();
                        }
                    },
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            buttonPlaceOrder.setEnabled(true);
                        }
                    }, false, false);
        }
    }

    private void applyPromo() {
        Utils.hideSoftKeyboard(activity, appPromoEdittext);
        try {
            if (AppStatus.getInstance(activity).isOnline(activity)) {
                DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));

                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.DELIVERY_LATITUDE, Prefs.with(activity).getString(activity.getResources().getString(R.string.pref_loc_lati), String.valueOf(Data.latitude)));
                params.put(Constants.DELIVERY_LONGITUDE, Prefs.with(activity).getString(activity.getResources().getString(R.string.pref_loc_longi), String.valueOf(Data.longitude)));

                params.put(Constants.ORDER_AMOUNT, String.valueOf(activity.getTotalPrice()));
                params.put(Constants.PROMO_CODE, appPromoEdittext.getText().toString().trim());


                Log.i(TAG, "getAllProducts params=" + params.toString());

                RestClient.getFreshApiService().applyPromo(params, new Callback<PlaceOrderResponse>() {
                    @Override
                    public void success(PlaceOrderResponse placeOrderResponse, Response response) {
                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                        Log.i(TAG, "getAllProducts response = " + responseStr);
                        try {
                            JSONObject jObj = new JSONObject(responseStr);
                            String message = JSONParser.getServerMessage(jObj);
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                int flag = jObj.getInt(Constants.KEY_FLAG);
                                if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
                                    Toast.makeText(activity, ""+message, Toast.LENGTH_SHORT).show();
                                    promoCode = appPromoEdittext.getText().toString().trim();
                                    promoAmount = jObj.optDouble(Constants.DISCOUNT, 0);
                                    appPromoEdittext.clearFocus();
                                    applyButton.setBackgroundColor(activity.getResources().getColor(R.color.theme_color_pressed));
                                    updateUI();

                                } else {
                                    DialogPopup.alertPopup(activity, "", message);
                                    promoCode = "";
                                    promoAmount = 0;
                                    appPromoEdittext.setText("");
                                    appPromoEdittext.clearFocus();
                                    updateUI();
                                }
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            retryDialog(DialogErrorType.SERVER_ERROR, 0);
                        }
                        DialogPopup.dismissLoadingDialog();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e(TAG, "paytmAuthenticateRecharge error " + error.toString());
                        DialogPopup.dismissLoadingDialog();
//                        123
                        retryDialog(DialogErrorType.CONNECTION_LOST, 0);
                    }
                });
            } else {
                retryDialog(DialogErrorType.NO_NET, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void placeOrderApi() {
        try {
            if (AppStatus.getInstance(activity).isOnline(activity)) {
                productList.clear();
                DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));

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

                if(!TextUtils.isEmpty(promoCode))
                    params.put(Constants.PROMO_CODE, promoCode);

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

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
                params.put(Constants.KEY_CART, jCart.toString());

                int type = Prefs.with(activity).getInt(Constants.APP_TYPE, Data.AppType);
                if(type == AppConstant.ApplicationType.MEALS) {
                    params.put("store_id", "2");
                    params.put("group_id", ""+activity.getProductsResponse().getCategories().get(0).getSubItems().get(0).getGroupId());
                }


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
                                    Prefs.with(activity).save(SPLabels.PAYTM_CHECK_BALANCE_LAST_TIME, time);
                                    activity.resumeMethod();
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

                                    NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_FRESH_ORDER_PLACED, null);

                                } else if (ApiResponseFlags.USER_IN_DEBT.getOrdinal() == flag) {
                                    final String message1 = jObj.optString(Constants.KEY_MESSAGE, "");
                                    final double userDebt = jObj.optDouble(Constants.KEY_USER_DEBT, 0);
                                    Log.e("USER_IN_DEBT message", "=" + message1);
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            new UserDebtDialog(activity, Data.userData,
                                                    new UserDebtDialog.Callback() {
                                                        @Override
                                                        public void successFullyDeducted(double userDebt) {
                                                            setPaymentOptionUI();
                                                            activity.updateMenu();
                                                        }

                                                    }).showUserDebtDialog(userDebt, message1, true);
                                        }
                                    });
                                } else {
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
                new Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        if(apiHit == 1)
                            placeOrderApi();
                        else if(apiHit == 0)
                            applyPromo();
                    }

                    @Override
                    public void neutralClick(View view) {

                    }

                    @Override
                    public void negativeClick(View view) {
                    }
                });
    }


    private void showPaytmBalanceLowDialog() {
        try {
            if (Data.userData.paytmEnabled == 1
                    && Data.userData.getPaytmStatus().equalsIgnoreCase(Data.PAYTM_STATUS_ACTIVE)) {
                String amount = Utils.getMoneyDecimalFormat().format(Data.userData.getPaytmBalance() - getTotalPriceWithDeliveryCharges());
                new FreshPaytmBalanceLowDialog(activity, amount, new FreshPaytmBalanceLowDialog.Callback() {
                    @Override
                    public void onRechargeNowClicked() {
                        FlurryEventLogger.event(PAYMENT_SCREEN, PAYMENT_SCREEN, RECHARGE);
                        intentToPaytm();
                    }

                    @Override
                    public void onPayByCashClicked() {
                        FlurryEventLogger.event(PAYMENT_SCREEN, PAYMENT_SCREEN, PAY_VIA_CASH);
                        linearLayoutCash.performClick();
                    }
                }).show();
            } else {
                intentToPaytm();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void intentToPaytm() {
        try {
            Intent intent = new Intent(activity, PaymentActivity.class);
            if (Data.userData.paytmEnabled == 1
                    && Data.userData.getPaytmStatus().equalsIgnoreCase(Data.PAYTM_STATUS_ACTIVE)) {
                DecimalFormat df = new DecimalFormat("#");
                intent.putExtra(Constants.KEY_ADD_PAYMENT_PATH, AddPaymentPath.PAYTM_RECHARGE.getOrdinal());
//                intent.putExtra(Constants.KEY_PAYMENT_PATH, 1);
                intent.putExtra(Constants.KEY_PAYMENT_RECHARGE_VALUE,
                        df.format(Math.ceil(getTotalPriceWithDeliveryCharges()
                                - Data.userData.getPaytmBalance())));
            } else {
                intent.putExtra(Constants.KEY_PAYMENT_PATH, 1);
                intent.putExtra(Constants.KEY_ADD_PAYMENT_PATH, AddPaymentPath.ADD_PAYTM.getOrdinal());
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
            double paid = Math.min(amountPayable, Data.userData.getJugnooBalance());

            amountPayable = amountPayable - paid;
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
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ASSL.closeActivity(linearLayoutRoot);
        System.gc();
    }


}

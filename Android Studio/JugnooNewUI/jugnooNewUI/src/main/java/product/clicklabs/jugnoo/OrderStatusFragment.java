package product.clicklabs.jugnoo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fugu.FuguConfig;
import com.sabkuchfresh.adapters.OrderItemsAdapter;
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.commoncalls.ApiCancelOrder;
import com.sabkuchfresh.fragments.OrderCancelReasonsFragment;
import com.sabkuchfresh.fragments.TrackOrderFragment;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.home.OrderStatus;
import com.sabkuchfresh.retrofit.model.menus.Charges;
import com.sabkuchfresh.utils.TextViewStrikeThrough;
import com.sabkuchfresh.widgets.LockableBottomSheetBehavior;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.datastructure.ProductType;
import product.clicklabs.jugnoo.datastructure.PushFlags;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.HistoryResponse;
import product.clicklabs.jugnoo.support.SupportActivity;
import product.clicklabs.jugnoo.support.TransactionUtils;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.NonScrollListView;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by ankit on 27/10/16.
 */

public class OrderStatusFragment extends Fragment implements GAAction, View.OnClickListener {

    private CoordinatorLayout relative;
    private RelativeLayout rlOrderStatus;
    private TextView tvOrderStatus, tvOrderStatusVal, tvOrderTime, tvOrderTimeVal, tvDeliveryTime, tvDeliveryTimeVal, tvDeliveryTo,
            tvDelveryPlace, tvDeliveryToVal,
            tvTotalAmountVal, tvAmountPayableVal;
    private ImageView ivDeliveryPlace, ivOrderCompleted, imageViewRestaurant, imageViewCallRestaurant;
    private Button bNeedHelp, buttonCancelOrder, reorderBtn, feedbackBtn, cancelfeedbackBtn;
    private int orderId, productType;
    private NonScrollListView listViewOrder;
    private OrderItemsAdapter orderItemsAdapter;
    private LinearLayout llFinalAmount, llDeliveryPlace, orderComplete, orderCancel;
    private ArrayList<HistoryResponse.OrderItem> subItemsOrders = new ArrayList<>();
    private HistoryResponse.Datum datum1;
    private FragmentActivity activity;
    private CardView cvOrderStatus, cvPaymentMethod;
    public TextView tvStatus0, tvStatus1, tvStatus2, tvStatus3;
    public ImageView ivStatus0, ivStatus1, ivStatus2, ivStatus3;
    public View lineStatus1, lineStatus2, lineStatus3;
    private View rootView;
    private ImageView ivTopShadow;
    private LinearLayout llExtraCharges, llMain;

    private TextView tvItemsQuantityVal;
    private TextViewStrikeThrough tvTotalAmountValOld;
    private TextView tvTotalAmountMessage;
    private TextView tvPaymentMethodVal;
    private RelativeLayout rlWalletDeducted;
    private LinearLayout llPaymentSummary;

    private NestedScrollView scrollView;
    private RelativeLayout rlContainer;
    private LockableBottomSheetBehavior bottomSheetBehavior;
    private RelativeLayout rlOrderStatusMapPeek;
    private LinearLayout llShadowPeek;
    private int llShadowPeekHeight, openLiveTracking;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_order_status, container, false);
        relative = (CoordinatorLayout) rootView.findViewById(R.id.relative);

        activity = getActivity();


        if (activity instanceof FreshActivity) {
            ((FreshActivity) activity).fragmentUISetup(this);
            GAUtils.trackScreenView(((FreshActivity) activity).getGaCategory() + ORDER_STATUS);
        }

        try {
            new ASSL(activity, relative, 1134, 720, false);
        } catch (Exception e) {
        }


        try {
            orderId = getArguments().getInt(Constants.KEY_ORDER_ID, 0);
            productType = getArguments().getInt(Constants.KEY_PRODUCT_TYPE, ProductType.MEALS.getOrdinal());
            openLiveTracking = getArguments().getInt(Constants.KEY_OPEN_LIVE_TRACKING, 0);
            setFragTitle();
        } catch (Exception e) {
            e.printStackTrace();
        }

        scrollView = (NestedScrollView) rootView.findViewById(R.id.scrollView);
        scrollView.setVisibility(View.GONE);
        bottomSheetBehavior = (LockableBottomSheetBehavior) BottomSheetBehavior.from(scrollView);
        bottomSheetBehavior.setPeekHeight(0);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetBehavior.setLocked(true);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(newState == BottomSheetBehavior.STATE_COLLAPSED){
                    TrackOrderFragment fragment = getTrackOrderFragment();
                    if(fragment != null){
                        fragment.setPaddingZero();
                    }
                } else if(newState == BottomSheetBehavior.STATE_EXPANDED) {
                    TrackOrderFragment fragment = getTrackOrderFragment();
                    if(fragment != null){
                        fragment.setPaddingSome();
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        rlOrderStatusMapPeek = (RelativeLayout) rootView.findViewById(R.id.rlOrderStatusMapPeek);
        rlOrderStatusMapPeek.setBackgroundColor(ContextCompat.getColor(activity, R.color.offer_popup_item_color));
        llShadowPeek = (LinearLayout) rootView.findViewById(R.id.llShadowPeek);
        llShadowPeek.setVisibility(View.GONE);
        llShadowPeekHeight = activity.getResources().getDimensionPixelSize(R.dimen.dp_172);
        rlContainer = (RelativeLayout) rootView.findViewById(R.id.rlContainer);
        rlContainer.setVisibility(View.GONE);
        llShadowPeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });


        llMain = (LinearLayout) rootView.findViewById(R.id.llMain);
        llMain.setVisibility(View.GONE);
        tvOrderStatus = (TextView) rootView.findViewById(R.id.tvOrderStatus);
        tvOrderStatus.setTypeface(Fonts.mavenMedium(activity));
        tvOrderStatusVal = (TextView) rootView.findViewById(R.id.tvOrderStatusVal);
        tvOrderStatusVal.setTypeface(Fonts.mavenMedium(activity));
        tvOrderTime = (TextView) rootView.findViewById(R.id.tvOrderTime);
        tvOrderTime.setTypeface(Fonts.mavenMedium(activity));
        tvOrderTimeVal = (TextView) rootView.findViewById(R.id.tvOrderTimeVal);
        tvOrderTimeVal.setTypeface(Fonts.mavenMedium(activity));
        tvDeliveryTime = (TextView) rootView.findViewById(R.id.tvDeliveryTime);
        tvDeliveryTime.setTypeface(Fonts.mavenMedium(activity));
        tvDeliveryTimeVal = (TextView) rootView.findViewById(R.id.tvDeliveryTimeVal);
        tvDeliveryTimeVal.setTypeface(Fonts.mavenMedium(activity));
        tvDeliveryTo = (TextView) rootView.findViewById(R.id.tvDeliveryTo);
        tvDeliveryTo.setTypeface(Fonts.mavenMedium(activity));
        tvDelveryPlace = (TextView) rootView.findViewById(R.id.tvDelveryPlace);
        tvDelveryPlace.setTypeface(Fonts.mavenMedium(activity), Typeface.BOLD);
        tvDeliveryToVal = (TextView) rootView.findViewById(R.id.tvDeliveryToVal);
        tvDeliveryToVal.setTypeface(Fonts.mavenMedium(activity));
        tvTotalAmountVal = (TextView) rootView.findViewById(R.id.tvTotalAmountVal);
        tvAmountPayableVal = (TextView) rootView.findViewById(R.id.tvAmountPayableVal);
        llFinalAmount = (LinearLayout) rootView.findViewById(R.id.llFinalAmount);
        llFinalAmount.setVisibility(View.GONE);
        bNeedHelp = (Button) rootView.findViewById(R.id.bNeedHelp);
        llDeliveryPlace = (LinearLayout) rootView.findViewById(R.id.llDeliveryPlace);
        ivDeliveryPlace = (ImageView) rootView.findViewById(R.id.ivDeliveryPlace);
        orderComplete = (LinearLayout) rootView.findViewById(R.id.order_complete);
        orderCancel = (LinearLayout) rootView.findViewById(R.id.order_cancel);
        ivOrderCompleted = (ImageView) rootView.findViewById(R.id.ivOrderCompleted);
        rlOrderStatus = (RelativeLayout) rootView.findViewById(R.id.rlOrderStatus);
        imageViewRestaurant = (ImageView) rootView.findViewById(R.id.imageViewRestaurant);
        imageViewRestaurant.setVisibility(View.GONE);
        imageViewCallRestaurant = (ImageView) rootView.findViewById(R.id.imageViewCallRestaurant);
        imageViewCallRestaurant.setVisibility(View.GONE);
        llExtraCharges = (LinearLayout) rootView.findViewById(R.id.llExtraCharges);
        llExtraCharges.setVisibility(View.GONE);

        // Order Status
        cvOrderStatus = (CardView) rootView.findViewById(R.id.cvOrderStatus);
        tvStatus0 = (TextView) rootView.findViewById(R.id.tvStatus0);
        tvStatus0.setTypeface(Fonts.mavenRegular(activity));
        tvStatus1 = (TextView) rootView.findViewById(R.id.tvStatus1);
        tvStatus1.setTypeface(Fonts.mavenRegular(activity));
        tvStatus2 = (TextView) rootView.findViewById(R.id.tvStatus2);
        tvStatus2.setTypeface(Fonts.mavenRegular(activity));
        tvStatus3 = (TextView) rootView.findViewById(R.id.tvStatus3);
        tvStatus3.setTypeface(Fonts.mavenRegular(activity));
        ivStatus0 = (ImageView) rootView.findViewById(R.id.ivStatus0);
        ivStatus1 = (ImageView) rootView.findViewById(R.id.ivStatus1);
        ivStatus2 = (ImageView) rootView.findViewById(R.id.ivStatus2);
        ivStatus3 = (ImageView) rootView.findViewById(R.id.ivStatus3);
        lineStatus1 = (View) rootView.findViewById(R.id.lineStatus1);
        lineStatus2 = (View) rootView.findViewById(R.id.lineStatus2);
        lineStatus3 = (View) rootView.findViewById(R.id.lineStatus3);
        ivTopShadow = (ImageView) rootView.findViewById(R.id.ivTopShadow);
        cvOrderStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        if (activity instanceof FreshActivity) {
            ivTopShadow.setVisibility(View.VISIBLE);
        }

        cvPaymentMethod = (CardView) rootView.findViewById(R.id.cvPaymentMethod);
        buttonCancelOrder = (Button) rootView.findViewById(R.id.buttonCancelOrder);
        buttonCancelOrder.setTypeface(Fonts.mavenRegular(activity));
        reorderBtn = (Button) rootView.findViewById(R.id.reorderBtn);
        reorderBtn.setTypeface(Fonts.mavenRegular(activity));
        feedbackBtn = (Button) rootView.findViewById(R.id.feedbackBtn);
        feedbackBtn.setTypeface(Fonts.mavenRegular(activity));

        cancelfeedbackBtn = (Button) rootView.findViewById(R.id.cancelfeedbackBtn);
        cancelfeedbackBtn.setTypeface(Fonts.mavenRegular(activity));

        tvItemsQuantityVal = (TextView) rootView.findViewById(R.id.tvItemsQuantityVal);
        tvItemsQuantityVal.setTypeface(Fonts.mavenMedium(activity), Typeface.BOLD);
        tvTotalAmountValOld = (TextViewStrikeThrough) rootView.findViewById(R.id.tvTotalAmountValOld);
        tvTotalAmountMessage = (TextView) rootView.findViewById(R.id.tvTotalAmountMessage);
        tvPaymentMethodVal = (TextView) rootView.findViewById(R.id.tvPaymentMethodVal);
        rlWalletDeducted = (RelativeLayout) rootView.findViewById(R.id.rlWalletDeducted);
        ((TextView) rootView.findViewById(R.id.tvItemSummary)).setTypeface(Fonts.mavenMedium(activity), Typeface.BOLD);
        ((TextView) rootView.findViewById(R.id.tvBillSummary)).setTypeface(Fonts.mavenMedium(activity), Typeface.BOLD);
        ((TextView) rootView.findViewById(R.id.tvPaymentSummary)).setTypeface(Fonts.mavenMedium(activity), Typeface.BOLD);
        llPaymentSummary = (LinearLayout) rootView.findViewById(R.id.llPaymentSummary);


        buttonCancelOrder.setOnClickListener(this);
        reorderBtn.setOnClickListener(this);
        feedbackBtn.setOnClickListener(this);
        cancelfeedbackBtn.setOnClickListener(this);


        listViewOrder = (NonScrollListView) rootView.findViewById(R.id.listViewCart);
        orderItemsAdapter = new OrderItemsAdapter(activity, subItemsOrders);
        listViewOrder.setAdapter(orderItemsAdapter);

        getOrderData(activity);


        bNeedHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                needHelpClick();
            }
        });

        imageViewCallRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(datum1.getRestaurantPhoneNo())) {
                    Utils.openCallIntent(activity, datum1.getRestaurantPhoneNo());
                }
            }
        });

        LocalBroadcastManager.getInstance(activity).registerReceiver(orderUpdateBroadcast, new IntentFilter(Constants.INTENT_ACTION_ORDER_STATUS_UPDATE));

        return relative;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(activity).unregisterReceiver(orderUpdateBroadcast);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (activity instanceof FreshActivity) {
                ((FreshActivity) activity).fragmentUISetup(this);
            }
            setFragTitle();
            if (datum1.getCancellable() == 1) {
                orderCancel.setVisibility(View.VISIBLE);
                orderComplete.setVisibility(View.GONE);
                if (activity instanceof SupportActivity) {
                    cancelfeedbackBtn.setVisibility(View.GONE);
                }

            } else {
                orderComplete.setVisibility(View.VISIBLE);
                orderCancel.setVisibility(View.GONE);
                cancelfeedbackBtn.setVisibility(View.GONE);
                if (activity instanceof RideTransactionsActivity) {
                    feedbackBtn.setText(R.string.need_help);
                    if (datum1.getCanReorder() == 1) {
                        reorderBtn.setVisibility(View.VISIBLE);
                    } else {
                        orderComplete.setVisibility(View.GONE);
                        orderCancel.setVisibility(View.VISIBLE);
                        buttonCancelOrder.setText(R.string.need_help);
                        buttonCancelOrder.setBackgroundResource(R.drawable.button_theme);
                        buttonCancelOrder.setTextColor(activity.getResources().getColor(R.color.white));
                    }
                } else {
                    if (datum1.getCanReorder() == 1 && !(activity instanceof FreshActivity)) {
                        reorderBtn.setVisibility(View.VISIBLE);
                        feedbackBtn.setVisibility(View.GONE);
                    } else if (datum1.getCanReorder() == 1 && (activity instanceof FreshActivity)) {
                        orderComplete.setVisibility(View.GONE);
                        orderCancel.setVisibility(View.VISIBLE);
                        buttonCancelOrder.setText(R.string.ok);
                        buttonCancelOrder.setBackgroundResource(R.drawable.button_theme);
                        buttonCancelOrder.setTextColor(activity.getResources().getColor(R.color.white));
                    } else if (datum1.getCanReorder() == 0 && !(activity instanceof FreshActivity)) {
                        orderComplete.setVisibility(View.GONE);
                        orderCancel.setVisibility(View.VISIBLE);
                        buttonCancelOrder.setText(R.string.ok);
                        buttonCancelOrder.setBackgroundResource(R.drawable.button_theme);
                        buttonCancelOrder.setTextColor(activity.getResources().getColor(R.color.white));
                    } else {
                        orderComplete.setVisibility(View.GONE);
                        orderCancel.setVisibility(View.VISIBLE);
                        buttonCancelOrder.setText(R.string.need_help);
                        buttonCancelOrder.setBackgroundResource(R.drawable.button_theme);
                        buttonCancelOrder.setTextColor(activity.getResources().getColor(R.color.white));
                    }

                }
            }
        }
    }

    private void setFragTitle() {
        if (activity instanceof RideTransactionsActivity) {
            ((RideTransactionsActivity) activity).setTitle("Order #" + orderId);
        } else if (activity instanceof SupportActivity) {
            ((SupportActivity) activity).setTitle("Order #" + orderId);
        } else if (activity instanceof FreshActivity) {
            ((FreshActivity) activity).getTopBar().title.setText("Order #" + orderId);
        }
    }

    /**
     * Method used to get order information
     */
    public void getOrderData(final Activity activity) {
        try {
            if (MyApplication.getInstance().isOnline()) {

                DialogPopup.showLoadingDialog(activity, "Loading...");

                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.KEY_ORDER_ID, "" + orderId);
                params.put(Constants.KEY_PRODUCT_TYPE, "" + productType);
                params.put(Constants.KEY_CLIENT_ID, "" + Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()));
                params.put(Constants.INTERATED, "1");

                Callback<HistoryResponse> callback = new Callback<HistoryResponse>() {
                    @Override
                    public void success(HistoryResponse historyResponse, Response response) {
                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                        Log.i("Server response", "response = " + responseStr);
                        try {
                            JSONObject jObj = new JSONObject(responseStr);
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                int flag = jObj.getInt("flag");
                                String message = JSONParser.getServerMessage(jObj);
                                if (ApiResponseFlags.RECENT_RIDES.getOrdinal() == flag) {
                                    llMain.setVisibility(View.VISIBLE);
                                    datum1 = historyResponse.getData().get(0);
                                    subItemsOrders.clear();
                                    subItemsOrders.addAll(historyResponse.getData().get(0).getOrderItems());
                                    orderItemsAdapter.notifyDataSetChanged();
                                    tvItemsQuantityVal.setText(String.valueOf(subItemsOrders.size()));
                                    setStatusResponse(historyResponse);
                                    openTrackOrderFragment();
                                } else {
                                    retryDialogOrderData(message, DialogErrorType.SERVER_ERROR);
                                }
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            retryDialogOrderData("", DialogErrorType.SERVER_ERROR);
                        }
                        DialogPopup.dismissLoadingDialog();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e("TAG", "getRecentRidesAPI error=" + error.toString());
                        DialogPopup.dismissLoadingDialog();
                        retryDialogOrderData("", DialogErrorType.CONNECTION_LOST);
                    }
                };

                new HomeUtil().putDefaultParams(params);
                if (productType == ProductType.MENUS.getOrdinal()) {
                    RestClient.getMenusApiService().orderHistory(params, callback);
                } else {
                    RestClient.getFreshApiService().orderHistory(params, callback);
                }
            } else {
                retryDialogOrderData("", DialogErrorType.NO_NET);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void retryDialogOrderData(String message, DialogErrorType dialogErrorType) {
        if (TextUtils.isEmpty(message)) {
            DialogPopup.dialogNoInternet(activity,
                    dialogErrorType,
                    new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
                        @Override
                        public void positiveClick(View view) {
                            getOrderData(activity);
                        }

                        @Override
                        public void neutralClick(View view) {

                        }

                        @Override
                        public void negativeClick(View view) {
                        }
                    });
        } else {
            DialogPopup.alertPopupTwoButtonsWithListeners(activity, "", message,
                    activity.getString(R.string.retry), activity.getString(R.string.cancel),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getOrderData(activity);
                        }
                    },
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            activity.onBackPressed();
                        }
                    }, false, false);
        }
    }


    private void showPossibleStatus(ArrayList<String> possibleStatus, int status) {
        setDefaultState();
        int selectedSize = (int) (35 * ASSL.Xscale());
        switch (possibleStatus.size()) {
            case 4:
                tvStatus3.setVisibility(View.VISIBLE);
                ivStatus3.setVisibility(View.VISIBLE);
                lineStatus3.setVisibility(View.VISIBLE);
                tvStatus3.setText(possibleStatus.get(3));
                if (status == 3) {
                    ivStatus3.setBackgroundResource(R.drawable.circle_order_status_green);
                    lineStatus3.setBackgroundColor(activity.getResources().getColor(R.color.order_status_green));
                } else if (status > 3) {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(selectedSize, selectedSize);
                    ivStatus3.setLayoutParams(layoutParams);
                    ivStatus3.setBackgroundResource(R.drawable.ic_order_status_green);
                    lineStatus3.setBackgroundColor(activity.getResources().getColor(R.color.order_status_green));
                }
            case 3:
                tvStatus2.setVisibility(View.VISIBLE);
                ivStatus2.setVisibility(View.VISIBLE);
                lineStatus2.setVisibility(View.VISIBLE);
                tvStatus2.setText(possibleStatus.get(2));
                if (status == 2) {
                    ivStatus2.setBackgroundResource(R.drawable.circle_order_status_green);
                    lineStatus2.setBackgroundColor(activity.getResources().getColor(R.color.order_status_green));
                } else if (status > 2) {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(selectedSize, selectedSize);
                    ivStatus2.setLayoutParams(layoutParams);
                    ivStatus2.setBackgroundResource(R.drawable.ic_order_status_green);
                    lineStatus2.setBackgroundColor(activity.getResources().getColor(R.color.order_status_green));
                }
            case 2:
                tvStatus1.setVisibility(View.VISIBLE);
                ivStatus1.setVisibility(View.VISIBLE);
                lineStatus1.setVisibility(View.VISIBLE);
                tvStatus1.setText(possibleStatus.get(1));
                if (status == 1) {
                    ivStatus1.setBackgroundResource(R.drawable.circle_order_status_green);
                    lineStatus1.setBackgroundColor(activity.getResources().getColor(R.color.order_status_green));
                } else if (status > 1) {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(selectedSize, selectedSize);
                    ivStatus1.setLayoutParams(layoutParams);
                    ivStatus1.setBackgroundResource(R.drawable.ic_order_status_green);
                    lineStatus1.setBackgroundColor(activity.getResources().getColor(R.color.order_status_green));
                }
            case 1:
                tvStatus0.setVisibility(View.VISIBLE);
                ivStatus0.setVisibility(View.VISIBLE);
                tvStatus0.setText(possibleStatus.get(0));
                if (status == 0) {
                    ivStatus0.setBackgroundResource(R.drawable.circle_order_status_green);
                } else if (status > 0) {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(selectedSize, selectedSize);
                    ivStatus0.setLayoutParams(layoutParams);
                    ivStatus0.setBackgroundResource(R.drawable.ic_order_status_green);
                }
                break;
        }
    }

    private void setDefaultState() {
        int selectedSize = (int) (25 * ASSL.Xscale());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(selectedSize, selectedSize);
        ivStatus3.setBackgroundResource(R.drawable.circle_order_status);
        ivStatus3.setLayoutParams(layoutParams);
        lineStatus3.setBackgroundColor(activity.getResources().getColor(R.color.rank_5));
        ivStatus2.setBackgroundResource(R.drawable.circle_order_status);
        ivStatus2.setLayoutParams(layoutParams);
        lineStatus2.setBackgroundColor(activity.getResources().getColor(R.color.rank_5));
        ivStatus1.setBackgroundResource(R.drawable.circle_order_status);
        ivStatus1.setLayoutParams(layoutParams);
        lineStatus1.setBackgroundColor(activity.getResources().getColor(R.color.rank_5));
        ivStatus0.setBackgroundResource(R.drawable.circle_order_status);
        ivStatus0.setLayoutParams(layoutParams);
    }

    /**
     * to show positive/negative fields
     *
     * @param llFinalAmount
     * @param fieldText
     * @param fieldTextVal
     * @param showNegative
     * @return
     */
    private View addFinalAmountView(LinearLayout llFinalAmount, String fieldText, Double fieldTextVal, boolean showNegative) {
        return addFinalAmountView(llFinalAmount, fieldText, fieldTextVal, showNegative, false, true, false);
    }

    private View addFinalAmountView(LinearLayout llFinalAmount, String fieldText, Double fieldTextVal,
                                    boolean showNegative, boolean showFree, boolean showDivider, boolean showAsterisk) {
        try {
            llFinalAmount.setVisibility(View.VISIBLE);
            LayoutInflater layoutInflater = (LayoutInflater) activity.getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.layout_order_amount_new, null);
            RelativeLayout relative = (RelativeLayout) view.findViewById(R.id.relative);
            TextView tvDelCharges = (TextView) view.findViewById(R.id.tvDelCharges);
            TextView tvDelChargesVal = (TextView) view.findViewById(R.id.tvDelChargesVal);
            tvDelCharges.setText(fieldText);
            if (showNegative) {
                tvDelChargesVal.setText("- " + activity.getString(R.string.rupees_value_format, Utils.getDoubleTwoDigits(fieldTextVal)));
                tvDelChargesVal.setTextColor(ContextCompat.getColor(activity, R.color.order_status_green));
            } else {
                if (fieldTextVal > 0) {
                    tvDelChargesVal.setText(activity.getString(R.string.rupees_value_format, Utils.getDoubleTwoDigits(fieldTextVal)));
                    tvDelChargesVal.setTextColor(ContextCompat.getColor(activity, R.color.text_color));
                } else {
                    if (showFree) {
                        tvDelChargesVal.setText(activity.getResources().getString(R.string.free));
                        tvDelChargesVal.setTextColor(ContextCompat.getColor(activity, R.color.order_status_green));
                    } else {
                        if (Utils.compareDouble(fieldTextVal, 0) == -1) {
                            tvDelChargesVal.setText("- " + activity.getString(R.string.rupees_value_format, Utils.getDoubleTwoDigits(Math.abs(fieldTextVal))));
                        } else {
                            tvDelChargesVal.setText(activity.getString(R.string.rupees_value_format, Utils.getDoubleTwoDigits(fieldTextVal)));
                        }
                        tvDelChargesVal.setTextColor(ContextCompat.getColor(activity, R.color.text_color));
                    }
                }
            }
            if (showAsterisk) {
                tvDelChargesVal.append("*");
            }
            View vDivider = view.findViewById(R.id.vDivider);
            if (!showDivider) {
                vDivider.setVisibility(View.INVISIBLE);
            }
            llFinalAmount.addView(view);
            ASSL.DoMagic(relative);
            return vDivider;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private double getSubTotalAmount(HistoryResponse historyResponse) {
        Double subTotal = 0d;
        for (HistoryResponse.OrderItem orderItem : historyResponse.getData().get(0).getOrderItems()) {
            subTotal = subTotal + orderItem.getItemAmount();
        }
        return subTotal;
    }

    private void setStatusResponse(HistoryResponse historyResponse) {
        try {
            HistoryResponse.Datum datum1 = historyResponse.getData().get(0);

            tvOrderStatusVal.setText(datum1.getOrderStatus());
            if ((datum1.getOrderStatusInt() == OrderStatus.ORDER_COMPLETED.getOrdinal())
                    || datum1.getOrderStatusInt() == OrderStatus.CASH_RECEIVED.getOrdinal()) {
                ivOrderCompleted.setVisibility(View.VISIBLE);
            } else {
                ivOrderCompleted.setVisibility(View.GONE);
            }

            if (historyResponse.getRecentOrdersPossibleStatus().size() > 0) {
                cvOrderStatus.setVisibility(View.VISIBLE);
                rlOrderStatus.setVisibility(View.GONE);
                setDefaultState();
                showPossibleStatus(historyResponse.getRecentOrdersPossibleStatus(), datum1.getOrderTrackingIndex());
            } else {
                cvOrderStatus.setVisibility(View.GONE);
                rlOrderStatus.setVisibility(View.VISIBLE);
            }


            tvOrderTimeVal.setText(DateOperations.convertDateViaFormat(DateOperations.utcToLocalWithTZFallback(datum1.getOrderTime())));

            if (datum1.getProductType() == ProductType.MENUS.getOrdinal()) {
                tvDeliveryTime.setText(activity.getString(R.string.delivered_from_colon));

                tvDeliveryTimeVal.setText("");
                final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
                final SpannableStringBuilder sb = new SpannableStringBuilder("      " + datum1.getRestaurantName());
                sb.setSpan(bss, 0, sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvDeliveryTimeVal.append(sb);
                tvDeliveryTimeVal.append("\n" + datum1.getRestaurantAddress());
                imageViewRestaurant.setVisibility(View.VISIBLE);
                imageViewCallRestaurant.setVisibility(TextUtils.isEmpty(datum1.getRestaurantPhoneNo()) ? View.GONE : View.VISIBLE);
            } else {
                if (datum1.getStartTime() != null && datum1.getEndTime() != null) {
                    tvDeliveryTimeVal.setText(datum1.getExpectedDeliveryDate() + " " +
                            DateOperations.convertDayTimeAPViaFormat(datum1.getStartTime()).replace("AM", "").replace("PM", "") + " - " + DateOperations.convertDayTimeAPViaFormat(datum1.getEndTime()));
                } else {
                    tvDeliveryTimeVal.setText(datum1.getExpectedDeliveryDate());
                }
                imageViewRestaurant.setVisibility(View.GONE);
                imageViewCallRestaurant.setVisibility(View.GONE);
            }
            tvDeliveryToVal.setText(datum1.getDeliveryAddress());


            tvOrderStatusVal.setTextColor(Color.parseColor(datum1.getOrderStatusColor()));

            if (!datum1.getDeliveryAddressType().equalsIgnoreCase("")) {
                llDeliveryPlace.setVisibility(View.VISIBLE);
                tvDelveryPlace.setText(datum1.getDeliveryAddressType());
                if (datum1.getDeliveryAddressType().equalsIgnoreCase(activity.getString(R.string.home))) {
                    ivDeliveryPlace.setImageResource(R.drawable.home);
                } else if (datum1.getDeliveryAddressType().equalsIgnoreCase(activity.getString(R.string.work))) {
                    ivDeliveryPlace.setImageResource(R.drawable.work);
                } else {
                    ivDeliveryPlace.setImageResource(R.drawable.ic_loc_other);
                }
            } else {
                llDeliveryPlace.setVisibility(View.GONE);
            }

            tvTotalAmountValOld.setVisibility(View.GONE);
            if (Double.compare(datum1.getDiscountedAmount(), datum1.getOrderAmount()) != 0) {
                tvTotalAmountValOld.setVisibility(View.VISIBLE);
                tvTotalAmountValOld.setText(activity.getString(R.string.rupees_value_format,
                        Utils.getMoneyDecimalFormat().format(datum1.getOrderAmount())));
            }
            tvTotalAmountVal.setText(activity.getString(R.string.rupees_value_format,
                    Utils.getMoneyDecimalFormat().format(datum1.getDiscountedAmount())));

            tvTotalAmountMessage.setVisibility(TextUtils.isEmpty(datum1.getNote()) ? View.GONE : View.VISIBLE);
            tvTotalAmountMessage.setText(datum1.getNote());
            boolean showAsterisk = !TextUtils.isEmpty(datum1.getNote());


            llExtraCharges.removeAllViews();
            addFinalAmountView(llExtraCharges, activity.getString(R.string.sub_total), datum1.getSubTotal(), false, false, true, showAsterisk);

            if ((datum1.getDiscount() != null) && (datum1.getDiscount() > 0)) {
                addFinalAmountView(llExtraCharges, activity.getString(R.string.discount), datum1.getDiscount(), true);
            }

            if (datum1.getCharges() != null) {
                for (Charges charges : datum1.getCharges()) {
                    try {
                        if (Double.parseDouble(charges.getValue()) > 0) {
                            addFinalAmountView(llExtraCharges, charges.getText(), Double.parseDouble(charges.getValue()), false);
                        }
                        else if (Double.parseDouble(charges.getValue()) < 0) {
                            addFinalAmountView(llExtraCharges, charges.getText(), Double.parseDouble(charges.getValue()), true, false, true, false);
                        }
                        else if (charges.getForceShow() == 1) {
                            addFinalAmountView(llExtraCharges, charges.getText(), Double.parseDouble(charges.getValue()), false, true, true, false);
                        }
                    } catch (Exception e) {
                    }
                }
            }


            llFinalAmount.removeAllViews();
            View vDivider = rootView.findViewById(R.id.vDividerPayment);
            if (datum1.getJugnooDeducted() > 0) {
                vDivider = addFinalAmountView(llFinalAmount, activity.getString(R.string.jugnoo_cash), datum1.getJugnooDeducted(), false);
            }

            if (datum1.getWalletDeducted() > 0) {
                rlWalletDeducted.setVisibility(View.VISIBLE);
                llPaymentSummary.removeView(rlWalletDeducted);
                llFinalAmount.addView(rlWalletDeducted);
                tvAmountPayableVal.setText(activity.getString(R.string.rupees_value_format,
                        Utils.getMoneyDecimalFormat().format(datum1.getWalletDeducted())));
                llFinalAmount.setVisibility(View.VISIBLE);
                vDivider = rootView.findViewById(R.id.vDividerPayment);
            } else {
                rlWalletDeducted.setVisibility(View.GONE);
            }

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tvPaymentMethodVal.getLayoutParams();
            tvPaymentMethodVal.setPadding(0, 0, 0, 0);
            tvPaymentMethodVal.setText("");
            tvPaymentMethodVal.setBackgroundResource(R.drawable.background_transparent);
            if (datum1.getPaymentMode() == PaymentOption.PAYTM.getOrdinal()) {
                params.setMargins((int) (ASSL.Xscale() * 10f), 0, 0, 0);
                tvPaymentMethodVal.setBackgroundResource(R.drawable.ic_paytm_small);
            } else if (datum1.getPaymentMode() == PaymentOption.MOBIKWIK.getOrdinal()) {
                params.setMargins((int) (ASSL.Xscale() * 25f), 0, 0, 0);
                tvPaymentMethodVal.setBackgroundResource(R.drawable.ic_mobikwik_small);
            } else if (datum1.getPaymentMode() == PaymentOption.FREECHARGE.getOrdinal()) {
                params.setMargins((int) (ASSL.Xscale() * 30f), 0, 0, 0);
                tvPaymentMethodVal.setBackgroundResource(R.drawable.ic_freecharge_small);
            } else if (datum1.getPaymentMode() == PaymentOption.JUGNOO_PAY.getOrdinal()) {
                tvPaymentMethodVal.setText(R.string.jugnoo_pay);
                params.setMargins((int) (ASSL.Xscale() * 35f), 0, 0, 0);
            } else if (datum1.getPaymentMode() == PaymentOption.RAZOR_PAY.getOrdinal()) {
                if (!TextUtils.isEmpty(datum1.getOtherPaymentModeText())) {
                    tvPaymentMethodVal.setText(datum1.getOtherPaymentModeText());
                } else {
                    tvPaymentMethodVal.setText(R.string.other_payment_mode);
                }
                params.setMargins((int) (ASSL.Xscale() * 35f), 0, 0, 0);
            } else if(datum1.getPaymentMode()==PaymentOption.ICICI_UPI.getOrdinal()){
                params.setMargins((int) (ASSL.Xscale() * 34f), 0, 0, 0);
                params.height= (int) (ASSL.minRatio() * 35f);
                params.width= (int) (ASSL.minRatio() * 90f);
                tvPaymentMethodVal.setBackgroundResource(R.drawable.upi_logo);
            }
            tvPaymentMethodVal.setLayoutParams(params);


            if (datum1.getPayableAmount() > 0) {
                vDivider = addFinalAmountView(llFinalAmount, activity.getString(R.string.cash), datum1.getPayableAmount(), false);
            }
            if (Utils.compareDouble(datum1.getOrderAdjustment(), 0) != 0) {
                vDivider = addFinalAmountView(llFinalAmount, activity.getString(R.string.order_adjustment), datum1.getOrderAdjustment(), false);
            }
            if (datum1.getRefundAmount() > 0) {
                vDivider = addFinalAmountView(llFinalAmount, activity.getString(R.string.refund), datum1.getRefundAmount(), false);
            }

            if (vDivider != null) {
                vDivider.setVisibility(View.INVISIBLE);
            }

            if (llFinalAmount.getChildCount() > 0 || datum1.getWalletDeducted() > 0) {
                cvPaymentMethod.setVisibility(View.VISIBLE);
            } else {
                cvPaymentMethod.setVisibility(View.GONE);
            }

            if (datum1.getCancellable() == 1) {
                orderCancel.setVisibility(View.VISIBLE);
                orderComplete.setVisibility(View.GONE);
                if (activity instanceof SupportActivity) {
                    cancelfeedbackBtn.setVisibility(View.GONE);
                }

            } else {
                orderComplete.setVisibility(View.VISIBLE);
                orderCancel.setVisibility(View.GONE);
                cancelfeedbackBtn.setVisibility(View.GONE);
                if (activity instanceof RideTransactionsActivity) {
                    feedbackBtn.setText(R.string.need_help);
                    if (datum1.getCanReorder() == 1) {
                        reorderBtn.setVisibility(View.VISIBLE);
                    } else {
                        orderComplete.setVisibility(View.GONE);
                        orderCancel.setVisibility(View.VISIBLE);
                        buttonCancelOrder.setText(R.string.need_help);
                        buttonCancelOrder.setBackgroundResource(R.drawable.button_theme);
                        buttonCancelOrder.setTextColor(activity.getResources().getColor(R.color.white));
                    }
                } else {
                    if (datum1.getCanReorder() == 1 && !(activity instanceof FreshActivity)) {
                        reorderBtn.setVisibility(View.VISIBLE);
                        feedbackBtn.setVisibility(View.GONE);
                    } else if (datum1.getCanReorder() == 1 && (activity instanceof FreshActivity)) {
                        orderComplete.setVisibility(View.GONE);
                        orderCancel.setVisibility(View.VISIBLE);
                        buttonCancelOrder.setText(R.string.ok);
                        buttonCancelOrder.setBackgroundResource(R.drawable.button_theme);
                        buttonCancelOrder.setTextColor(activity.getResources().getColor(R.color.white));
                    } else if (datum1.getCanReorder() == 0 && !(activity instanceof FreshActivity)) {
                        orderComplete.setVisibility(View.GONE);
                        orderCancel.setVisibility(View.VISIBLE);
                        buttonCancelOrder.setText(R.string.ok);
                        buttonCancelOrder.setBackgroundResource(R.drawable.button_theme);
                        buttonCancelOrder.setTextColor(activity.getResources().getColor(R.color.white));
                    } else if (datum1.getCanReorder() == 0 && (activity instanceof FreshActivity)) {
                        orderComplete.setVisibility(View.GONE);
                        orderCancel.setVisibility(View.VISIBLE);
                        buttonCancelOrder.setText(R.string.ok);
                        buttonCancelOrder.setBackgroundResource(R.drawable.button_theme);
                        buttonCancelOrder.setTextColor(activity.getResources().getColor(R.color.white));
                    } else {
                        orderComplete.setVisibility(View.GONE);
                        orderCancel.setVisibility(View.VISIBLE);
                        buttonCancelOrder.setText(R.string.need_help);
                        buttonCancelOrder.setBackgroundResource(R.drawable.button_theme);
                        buttonCancelOrder.setTextColor(activity.getResources().getColor(R.color.white));
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        int tag = v.getId();
        switch (tag) {
            case R.id.buttonCancelOrder:
                if (datum1.getCancellable() == 1) {
                    int storeId = datum1.getStoreId() == null ? 0 : datum1.getStoreId();
                    if (datum1.getShowCancellationReasons() == 1) {
                        int containerId = -1;
                        if (activity instanceof FreshActivity) {
                            containerId = ((FreshActivity) activity).getRelativeLayoutContainer().getId();
                        } else if (activity instanceof RideTransactionsActivity) {
                            containerId = ((RideTransactionsActivity) activity).getContainer().getId();
                        } else if (activity instanceof SupportActivity) {
                            containerId = ((SupportActivity) activity).getContainer().getId();
                        }
                        if (containerId > -1) {
                            activity.getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                                    .add(containerId, OrderCancelReasonsFragment.newInstance(datum1.getOrderId(),
                                            productType, storeId, datum1.getClientId()),
                                            OrderCancelReasonsFragment.class.getName())
                                    .addToBackStack(OrderCancelReasonsFragment.class.getName())
                                    .hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
                                            .getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
                                    .commitAllowingStateLoss();
                        }
                    } else {
                        DialogPopup.alertPopupTwoButtonsWithListeners(activity, "", "Are you sure you want to cancel this order?", getResources().getString(R.string.ok),
                                getResources().getString(R.string.cancel), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        cancelOrderApi();
                                        if (activity instanceof FreshActivity) {
                                            GAUtils.event(((FreshActivity) activity).getGaCategory(), ORDER_STATUS, ORDER + CANCELLED);
                                        }
                                    }
                                }, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                }, false, false);
                    }
                } else {
                    feedbackBtn.performClick();
                }
                break;
            case R.id.feedbackBtn:
                needHelpClick();
                break;
            case R.id.reorderBtn:
                saveHistoryCardToSP(datum1);
                break;
            case R.id.cancelfeedbackBtn:
                needHelpClick();
                if (activity instanceof FreshActivity) {
                    GAUtils.event(((FreshActivity) activity).getGaCategory(), ORDER_STATUS, NEED_HELP);
                } else {
                    GAUtils.event(GACategory.SIDE_MENU, ORDER + DETAILS, NEED_HELP + CLICKED);
                }
                break;
        }
    }

    private void needHelpClick() {
        if (activity instanceof RideTransactionsActivity) {
            if (Data.isFuguChatEnabled()) {
                try {
                    FuguConfig.getInstance().openChat(getActivity(), Data.CHANNEL_ID_FUGU_ISSUE_ORDER());
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.showToast(activity, activity.getString(R.string.something_went_wrong));
                }

            } else {
                new TransactionUtils().openRideIssuesFragment(activity,
                        ((RideTransactionsActivity) activity).getContainer(),
                        -1, -1, null, null, 0, false, 0, datum1, -1, -1, "");
            }
        } else {
            activity.onBackPressed();
        }
    }

    public void saveHistoryCardToSP(HistoryResponse.Datum orderHistory) {
        try {
            // TODO: 19/03/17 check for reorder cart fill
//            JSONObject jCart = new JSONObject();
//            if (orderHistory.getOrderItems() != null) {
//                Gson gson = new Gson();
//                for (HistoryResponse.OrderItem orderItem : orderHistory.getOrderItems()) {
//                    if (orderItem.getItemQuantity() > 0) {
//                        try {
//                            SubItem subItem1 = new SubItem();
//                            subItem1.setSubItemId(orderItem.getSubItemId());
//                            subItem1.setPrice(orderItem.getUnitAmount());
//                            subItem1.setStock(50);
//                            subItem1.setSubItemQuantitySelected(orderItem.getItemQuantity());
//                            subItem1.setSubItemName(orderItem.getItemName());
//                            subItem1.setBaseUnit(orderItem.getUnit());
//                            subItem1.setSubItemImage(orderItem.getSubItemImage());
//
//                            jCart.put(String.valueOf(orderItem.getSubItemId()), gson.toJson(subItem1, SubItem.class));
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//            jCart.put(Constants.KEY_CITY_ID, orderHistory.getCityId());
//            if(orderHistory.getProductType() == ProductType.FRESH.getOrdinal()) {
//                Prefs.with(activity).save(Constants.SP_FRESH_CART, jCart.toString());
//                sendMessage(0, orderHistory);
//            } else if(orderHistory.getProductType() == ProductType.GROCERY.getOrdinal()){
//                Prefs.with(activity).save(Constants.SP_GROCERY_CART, jCart.toString());
//                sendMessage(2, orderHistory);
//            } else if(orderHistory.getProductType() == ProductType.MENUS.getOrdinal()){
//                Prefs.with(activity).save(Constants.SP_MENUS_CART, jCart.toString());
//                sendMessage(3, orderHistory);
//            }

            DialogPopup.showLoadingDialog(activity, "Please wait...");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    activity.finish();
                }
            }, 1000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(int type, HistoryResponse.Datum orderHistory) {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent(Data.LOCAL_BROADCAST);
        // You can also include some extra data.
        intent.putExtra("message", "This is my message!");
        intent.putExtra("open_type", type);
        Data.setDatumToReOrder(orderHistory);
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
    }

    private BroadcastReceiver orderUpdateBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        int flag = intent.getIntExtra(Constants.KEY_FLAG, -1);
                        if (PushFlags.STATUS_CHANGED.getOrdinal() == flag) {
                            getOrderData(activity);
                        } else if (PushFlags.MENUS_STATUS.getOrdinal() == flag || PushFlags.MENUS_STATUS_SILENT.getOrdinal() == flag) {
                            Log.v("menus status ", "menus status tracking");
                            getOrderData(activity);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    };

    private ApiCancelOrder apiCancelOrder;

    private void cancelOrderApi() {
        if (apiCancelOrder == null) {
            apiCancelOrder = new ApiCancelOrder(activity, new ApiCancelOrder.Callback() {
                @Override
                public void onSuccess(String message) {
                    DialogPopup.alertPopupWithListener(activity, "", message, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Data.isOrderCancelled = true;
                            datum1.setCancellable(0);

                            Intent intent = new Intent(Data.LOCAL_BROADCAST);
                            intent.putExtra("message", "Order cancelled, refresh inventory");
                            intent.putExtra("open_type", 10);
                            LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);

                            activity.onBackPressed();
                        }
                    });
                }

                @Override
                public void onFailure() {

                }

                @Override
                public void onRetry(View view) {
                    cancelOrderApi();
                }

                @Override
                public void onNoRetry(View view) {

                }
            });
        }
        int storeId = datum1.getStoreId() == null ? 0 : datum1.getStoreId();
        apiCancelOrder.hit(datum1.getOrderId(), datum1.getClientId(),
                storeId,
                productType);
    }


    private void openTrackOrderFragment(){
        if(datum1.getLiveTracking() != null
                && datum1.getLiveTracking().getShowLiveTracking() == 1 && datum1.getLiveTracking().getDeliveryId() > 0) {
            rlOrderStatusMapPeek.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
            llShadowPeek.setVisibility(View.VISIBLE);
            rlContainer.setVisibility(View.VISIBLE);
            getChildFragmentManager().beginTransaction()
                    .replace(rlContainer.getId(), TrackOrderFragment.newInstance(Data.userData.accessToken,
                            datum1.getOrderId(), datum1.getLiveTracking().getDeliveryId(),
                            datum1.getLiveTracking().getPickupLatitude(), datum1.getLiveTracking().getPickupLongitude(),
                            datum1.getLiveTracking().getDeliveryLatitude(), datum1.getLiveTracking().getDeliveryLongitude(),
                            datum1.getLiveTracking().getShowDeliveryRoute(), datum1.getLiveTracking().getDriverPhoneNo(), llShadowPeekHeight,
                            openLiveTracking != 1),
                            TrackOrderFragment.class.getName())
                    .commit();

            if(openLiveTracking == 1){
                scrollView.setVisibility(View.GONE);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                bottomSheetBehavior.setLocked(true);
            } else {
                scrollView.setVisibility(View.VISIBLE);
                bottomSheetBehavior.setLocked(false);
            }
        } else {
            bottomSheetBehavior.setLocked(true);
            rlOrderStatusMapPeek.setBackgroundColor(ContextCompat.getColor(activity, R.color.offer_popup_item_color));
            llShadowPeek.setVisibility(View.GONE);
            rlContainer.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
        }
    }

    public boolean performBack(){
        if(openLiveTracking == 0 && bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            return true;
        }
        return false;
    }

    private TrackOrderFragment getTrackOrderFragment(){
        Fragment fragment = getChildFragmentManager().findFragmentByTag(TrackOrderFragment.class.getName());
        if(fragment != null){
            return (TrackOrderFragment) fragment;
        }
        return null;
    }

    private Handler handler = new Handler();

}

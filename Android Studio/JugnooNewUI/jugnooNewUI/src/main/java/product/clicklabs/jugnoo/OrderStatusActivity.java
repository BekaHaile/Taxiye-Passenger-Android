package product.clicklabs.jugnoo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.adapters.FreshCartItemsAdapter;
import com.sabkuchfresh.adapters.FreshOrderItemAdapter;
import com.sabkuchfresh.adapters.OrderItemsAdapter;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.OrderHistoryResponse;
import com.sabkuchfresh.retrofit.model.SubItem;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.Inflater;

import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.datastructure.ProductType;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.fragments.RideTransactionsFragment;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.HistoryResponse;
import product.clicklabs.jugnoo.support.SupportActivity;
import product.clicklabs.jugnoo.support.TransactionUtils;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
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

public class OrderStatusActivity extends Fragment implements View.OnClickListener{

    private RelativeLayout relative, rlAmountPayable, rlBilledAmount, rlRefund;
    private TextView tvOrderStatus, tvOrderStatusVal, tvOrderTime, tvOrderTimeVal, tvDeliveryTime, tvDeliveryTimeVal, tvDeliveryTo,
            tvDelveryPlace, tvDeliveryToVal, tvSubAmount, tvSubAmountVal, tvPaymentMethod, tvPaymentMethodCash, tvDeliveryCharges,
            tvDeliveryChargesVal, tvTotalAmount, tvTotalAmountVal, tvAmountPayable, tvAmountPayableVal, tvBilledAmount, tvBilledAmountVal,
            tvRefund, tvRefundVal;
    private ImageView ivPaymentMethodVal, ivDeliveryPlace;
    private Button bNeedHelp, buttonCancelOrder, reorderBtn, feedbackBtn, cancelfeedbackBtn;
    private int orderId;
    private NonScrollListView listViewOrder;
    private OrderItemsAdapter orderItemsAdapter;
    private LinearLayout llFinalAmount, llDeliveryPlace, orderComplete, orderCancel, llRefund;
    private ArrayList<HistoryResponse.OrderItem> subItemsOrders = new ArrayList<HistoryResponse.OrderItem>();
    private HistoryResponse.Datum orderHistory;
    private FragmentActivity activity;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_order_status, container, false);
        relative = (RelativeLayout) rootView.findViewById(R.id.relative);

        activity = getActivity();

        new ASSL(activity, relative, 1134, 720, false);


        try {
            orderId = getArguments().getInt(Constants.KEY_ORDER_ID, 0);
            setFragTitle();
        } catch (Exception e) {
            e.printStackTrace();
        }

        tvOrderStatus = (TextView) rootView.findViewById(R.id.tvOrderStatus); tvOrderStatus.setTypeface(Fonts.mavenMedium(activity));
        tvOrderStatusVal = (TextView) rootView.findViewById(R.id.tvOrderStatusVal); tvOrderStatusVal.setTypeface(Fonts.mavenMedium(activity));
        tvOrderTime = (TextView) rootView.findViewById(R.id.tvOrderTime); tvOrderTime.setTypeface(Fonts.mavenMedium(activity));
        tvOrderTimeVal = (TextView) rootView.findViewById(R.id.tvOrderTimeVal);tvOrderTimeVal.setTypeface(Fonts.mavenMedium(activity));
        tvDeliveryTime = (TextView) rootView.findViewById(R.id.tvDeliveryTime); tvDeliveryTime.setTypeface(Fonts.mavenMedium(activity));
        tvDeliveryTimeVal = (TextView) rootView.findViewById(R.id.tvDeliveryTimeVal); tvDeliveryTimeVal.setTypeface(Fonts.mavenMedium(activity));
        tvDeliveryTo = (TextView) rootView.findViewById(R.id.tvDeliveryTo); tvDeliveryTo.setTypeface(Fonts.mavenMedium(activity));
        tvDelveryPlace = (TextView) rootView.findViewById(R.id.tvDelveryPlace); tvDelveryPlace.setTypeface(Fonts.mavenMedium(activity), Typeface.BOLD);
        tvDeliveryToVal = (TextView) rootView.findViewById(R.id.tvDeliveryToVal); tvDeliveryToVal.setTypeface(Fonts.mavenMedium(activity));
        tvSubAmount = (TextView) rootView.findViewById(R.id.tvSubAmount); tvSubAmount.setTypeface(Fonts.mavenMedium(activity));
        tvSubAmountVal = (TextView) rootView.findViewById(R.id.tvSubAmountVal); tvSubAmountVal.setTypeface(Fonts.mavenMedium(activity));
        tvDeliveryCharges = (TextView) rootView.findViewById(R.id.tvDeliveryCharges); tvDeliveryCharges.setTypeface(Fonts.mavenRegular(activity));
        tvDeliveryChargesVal = (TextView) rootView.findViewById(R.id.tvDeliveryChargesVal); tvDeliveryChargesVal.setTypeface(Fonts.mavenMedium(activity));
        tvTotalAmount = (TextView) rootView.findViewById(R.id.tvTotalAmount); tvTotalAmount.setTypeface(Fonts.mavenMedium(activity));
        tvTotalAmountVal = (TextView) rootView.findViewById(R.id.tvTotalAmountVal); tvTotalAmountVal.setTypeface(Fonts.mavenMedium(activity));
        rlAmountPayable = (RelativeLayout) rootView.findViewById(R.id.rlAmountPayable);
        tvAmountPayable = (TextView) rootView.findViewById(R.id.tvAmountPayable); tvAmountPayable.setTypeface(Fonts.mavenMedium(activity));
        tvAmountPayableVal = (TextView) rootView.findViewById(R.id.tvAmountPayableVal); tvAmountPayableVal.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
        llFinalAmount = (LinearLayout) rootView.findViewById(R.id.llFinalAmount);
        tvPaymentMethod = (TextView) rootView.findViewById(R.id.tvPaymentMethod); tvPaymentMethod.setTypeface(Fonts.mavenRegular(activity));
        ivPaymentMethodVal = (ImageView) rootView.findViewById(R.id.ivPaymentMethodVal);
        tvPaymentMethodCash = (TextView) rootView.findViewById(R.id.tvPaymentMethodCash); tvPaymentMethodCash.setTypeface(Fonts.mavenMedium(activity));
        llRefund = (LinearLayout) rootView.findViewById(R.id.llRefund);
        rlBilledAmount = (RelativeLayout) rootView.findViewById(R.id.rlBilledAmount);
        rlRefund = (RelativeLayout) rootView.findViewById(R.id.rlRefund);
        tvBilledAmount = (TextView) rootView.findViewById(R.id.tvBilledAmount); tvBilledAmount.setTypeface(Fonts.mavenRegular(activity));
        tvBilledAmountVal = (TextView) rootView.findViewById(R.id.tvBilledAmountVal); tvBilledAmountVal.setTypeface(Fonts.mavenMedium(activity));
        tvRefund = (TextView) rootView.findViewById(R.id.tvRefund); tvRefund.setTypeface(Fonts.mavenRegular(activity));
        tvRefundVal = (TextView) rootView.findViewById(R.id.tvRefundVal); tvRefundVal.setTypeface(Fonts.mavenMedium(activity));
        bNeedHelp = (Button) rootView.findViewById(R.id.bNeedHelp);
        llDeliveryPlace = (LinearLayout) rootView.findViewById(R.id.llDeliveryPlace);
        ivDeliveryPlace = (ImageView) rootView.findViewById(R.id.ivDeliveryPlace);
        orderComplete = (LinearLayout) rootView.findViewById(R.id.order_complete);
        orderCancel = (LinearLayout) rootView.findViewById(R.id.order_cancel);

        buttonCancelOrder = (Button) rootView.findViewById(R.id.buttonCancelOrder);
        buttonCancelOrder.setTypeface(Fonts.mavenRegular(activity));
        reorderBtn = (Button) rootView.findViewById(R.id.reorderBtn);
        reorderBtn.setTypeface(Fonts.mavenRegular(activity));
        feedbackBtn = (Button) rootView.findViewById(R.id.feedbackBtn);
        feedbackBtn.setTypeface(Fonts.mavenRegular(activity));

        cancelfeedbackBtn = (Button) rootView.findViewById(R.id.cancelfeedbackBtn);
        cancelfeedbackBtn.setTypeface(Fonts.mavenRegular(activity));

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
                if (activity instanceof RideTransactionsActivity) {
                    new TransactionUtils().openRideIssuesFragment(activity,
                            ((RideTransactionsActivity) activity).getContainer(),
                            -1, -1, null, null, 0, false, 0, orderHistory);
                } else{
                    activity.onBackPressed();
                }
            }
        });

        return relative;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            setFragTitle();
            if (orderHistory.getCancellable() == 1) {
                orderCancel.setVisibility(View.VISIBLE);
                orderComplete.setVisibility(View.GONE);
                if(activity instanceof SupportActivity) {
                    cancelfeedbackBtn.setVisibility(View.GONE);
                }

            } else {
                orderComplete.setVisibility(View.VISIBLE);
                orderCancel.setVisibility(View.GONE);
                cancelfeedbackBtn.setVisibility(View.GONE);
                if (activity instanceof RideTransactionsActivity) {
                    feedbackBtn.setText(R.string.need_help);
                    if (orderHistory.getCanReorder() == 1) {
                        reorderBtn.setVisibility(View.VISIBLE);
                    } else {
                        orderComplete.setVisibility(View.GONE);
                        orderCancel.setVisibility(View.VISIBLE);
                        buttonCancelOrder.setText(R.string.need_help);
                    }
                } else {
                    if (orderHistory.getCanReorder() == 1 && !(activity instanceof FreshActivity)) {
                        reorderBtn.setVisibility(View.VISIBLE);
                        feedbackBtn.setVisibility(View.GONE);
                    } else if (orderHistory.getCanReorder() == 1 && (activity instanceof FreshActivity)) {
                        orderComplete.setVisibility(View.GONE);
                        orderCancel.setVisibility(View.VISIBLE);
                        buttonCancelOrder.setText(R.string.ok);
                    } else if (orderHistory.getCanReorder() == 0 && !(activity instanceof FreshActivity)) {
                        orderComplete.setVisibility(View.GONE);
                        orderCancel.setVisibility(View.VISIBLE);
                        buttonCancelOrder.setText(R.string.ok);
                    }  else {
                        orderComplete.setVisibility(View.GONE);
                        orderCancel.setVisibility(View.VISIBLE);
                        buttonCancelOrder.setText(R.string.need_help);
                    }

                }
            }
        }
    }

    private void setFragTitle(){
        if(activity instanceof RideTransactionsActivity) {
            ((RideTransactionsActivity) activity).setTitle("Order #" + orderId);
        } else if(activity instanceof SupportActivity) {
            ((SupportActivity) activity).setTitle("Order #" + orderId);
        }
    }

    /**
     * Method used to get order information
     */
    private void getOrderData(final Activity activity) {
        try {
            if(AppStatus.getInstance(activity).isOnline(activity)) {

                DialogPopup.showLoadingDialog(activity, "Loading...");

                HashMap<String, String> params = new HashMap<>();
                params.put("access_token", Data.userData.accessToken);
                params.put("order_id", "" + orderId);
                params.put(Constants.KEY_CLIENT_ID, ""+ Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()));
                params.put(Constants.INTERATED, "1");
                RestClient.getFreshApiService().orderHistory(params, new Callback<HistoryResponse>() {
                    @Override
                    public void success(HistoryResponse historyResponse, Response response) {
                        String responseStr = new String(((TypedByteArray)response.getBody()).getBytes());
                        Log.i("Server response", "response = " + responseStr);
                        try {

                            JSONObject jObj = new JSONObject(responseStr);
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                int flag = jObj.getInt("flag");
                                String message = JSONParser.getServerMessage(jObj);
                                if (ApiResponseFlags.RECENT_RIDES.getOrdinal() == flag) {
                                    orderHistory = historyResponse.getData().get(0);
                                    subItemsOrders.clear();
                                    subItemsOrders.addAll(historyResponse.getData().get(0).getOrderItems());
                                    orderItemsAdapter.notifyDataSetChanged();

                                    setStatusResponse(historyResponse);
                                } else {
                                    //updateListData(message);
                                }
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            //updateListData(Data.SERVER_ERROR_MSG);
                        }
                        DialogPopup.dismissLoadingDialog();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e("TAG", "getRecentRidesAPI error="+error.toString());
                        DialogPopup.dismissLoadingDialog();
                        //updateListData(Data.SERVER_NOT_RESOPNDING_MSG);
                    }
                });
            }
            else {
                //updateListData(Data.CHECK_INTERNET_MSG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cancelOrderApiCall(int orderId) {
        try {
            if (AppStatus.getInstance(activity).isOnline(activity)) {
                DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));

                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.KEY_FRESH_ORDER_ID, String.valueOf(orderId));
                params.put(Constants.KEY_CLIENT_ID, orderHistory.getClientId());
                params.put(Constants.INTERATED, "1");
                try {
                    if (orderHistory.getStoreId() != null) {
                        params.put(Constants.STORE_ID, "" + orderHistory.getStoreId());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                RestClient.getFreshApiService().cancelOrder(params, new Callback<OrderHistoryResponse>() {
                    @Override
                    public void success(OrderHistoryResponse orderHistoryResponse, Response response) {
                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                        Log.i("Order Status", "Fresh order cancel response = " + responseStr);
                        DialogPopup.dismissLoadingDialog();
                        long time = 0L;
                        Prefs.with(activity).save(SPLabels.CHECK_BALANCE_LAST_TIME, time);
                        try {
                            JSONObject jObj = new JSONObject(responseStr);
                            String message = JSONParser.getServerMessage(jObj);
                            if (orderHistoryResponse.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
                                DialogPopup.alertPopupWithListener(activity, "", message, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        Data.isOrderCancelled = true;
                                        orderHistory.setCancellable(0);
                                        activity.onBackPressed();
                                    }
                                });
                            } else {
                                DialogPopup.alertPopupWithListener(activity, "", message, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                });
                            }

                        } catch (Exception exception) {
                            exception.printStackTrace();
                            retryDialog(DialogErrorType.SERVER_ERROR);
                        }
                        DialogPopup.dismissLoadingDialog();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e("Order Status", "Fresh Cancel Order error" + error.toString());
                        DialogPopup.dismissLoadingDialog();
                        retryDialog(DialogErrorType.CONNECTION_LOST);
                    }
                });
            } else {
                retryDialog(DialogErrorType.NO_NET);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void retryDialog(DialogErrorType dialogErrorType) {
        DialogPopup.dialogNoInternet(activity,
                dialogErrorType,
                new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        cancelOrderApiCall(orderHistory.getOrderId());
                    }

                    @Override
                    public void neutralClick(View view) {

                    }

                    @Override
                    public void negativeClick(View view) {
                    }
                });
    }

    private void addFinalAmountView(LinearLayout llFinalAmount, String fieldText, String fieldTextVal){
        try {
            llFinalAmount.setVisibility(View.VISIBLE);
            LayoutInflater layoutInflater = (LayoutInflater) activity.getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.layout_order_amount_new, null);
            RelativeLayout relative = (RelativeLayout) view.findViewById(R.id.relative);
            TextView tvDelCharges = (TextView) view.findViewById(R.id.tvDelCharges);
            tvDelCharges.setTypeface(Fonts.mavenRegular(activity));
            TextView tvDelChargesVal = (TextView) view.findViewById(R.id.tvDelChargesVal);
            tvDelChargesVal.setTypeface(Fonts.mavenMedium(activity));
            tvDelCharges.setText(fieldText);
            tvDelChargesVal.setText("- " + String.format(getResources().getString(R.string.rupees_value_format), Utils.getMoneyDecimalFormatWithoutFloat().format(Float.parseFloat(fieldTextVal))));
            llFinalAmount.addView(view);
            ASSL.DoMagic(relative);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getSubTotalAmount(HistoryResponse historyResponse){
        int subTotal = Integer.parseInt(Utils.getMoneyDecimalFormat().format(historyResponse.getData().get(0).getOriginalOrderAmount() - historyResponse.getData().get(0).getDeliveryCharges()));
        return subTotal;
    }

    private int getBilledAmount(HistoryResponse historyResponse){
        int billedAmount = Integer.parseInt(Utils.getMoneyDecimalFormat().format(historyResponse.getData().get(0).getOrderAmount() + historyResponse.getData().get(0).getJugnooDeducted()
                + historyResponse.getData().get(0).getDiscount()));
        return billedAmount;
    }

    private void setStatusResponse(HistoryResponse historyResponse) {
        try {
            tvOrderStatusVal.setText(historyResponse.getData().get(0).getOrderStatus());
            tvOrderTimeVal.setText(historyResponse.getData().get(0).getOrderTime());
            tvOrderTimeVal.setText(DateOperations.convertDateViaFormat(DateOperations.utcToLocalWithTZFallback(historyResponse.getData().get(0).getOrderTime())));
            if (orderHistory.getStartTime() != null && orderHistory.getEndTime() != null) {
                tvDeliveryTimeVal.setText(historyResponse.getData().get(0).getExpectedDeliveryDate()+" "+
                        DateOperations.convertDayTimeAPViaFormat(orderHistory.getStartTime()) + " - " + DateOperations.convertDayTimeAPViaFormat(orderHistory.getEndTime()));
            } else {
                tvDeliveryTimeVal.setText(historyResponse.getData().get(0).getExpectedDeliveryDate());
            }
            tvDeliveryToVal.setText(historyResponse.getData().get(0).getDeliveryAddress());
            try {
                tvSubAmountVal.setText(String.format(getResources().getString(R.string.rupees_value_format), Integer.toString(getSubTotalAmount(historyResponse))));
            } catch (Exception e) {
                e.printStackTrace();
            }
            tvOrderStatusVal.setTextColor(Color.parseColor(historyResponse.getData().get(0).getOrderStatusColor()));

            if(!historyResponse.getData().get(0).getDeliveryAddressType().equalsIgnoreCase("")){
                llDeliveryPlace.setVisibility(View.VISIBLE);
                tvDelveryPlace.setText(historyResponse.getData().get(0).getDeliveryAddressType());
                if(historyResponse.getData().get(0).getDeliveryAddressType().equalsIgnoreCase(activity.getString(R.string.home))){
                    ivDeliveryPlace.setImageResource(R.drawable.home);
                } else if(historyResponse.getData().get(0).getDeliveryAddressType().equalsIgnoreCase(activity.getString(R.string.work))){
                    ivDeliveryPlace.setImageResource(R.drawable.work);
                } else{
                    ivDeliveryPlace.setImageResource(R.drawable.ic_loc_other);
                }
            } else{
                llDeliveryPlace.setVisibility(View.GONE);
            }

            tvPaymentMethodCash.setVisibility(View.GONE);
            if(historyResponse.getData().get(0).getPaymentMode() == PaymentOption.CASH.getOrdinal()){
                ivPaymentMethodVal.setVisibility(View.GONE);
                tvPaymentMethodCash.setVisibility(View.VISIBLE);
            } else if(historyResponse.getData().get(0).getPaymentMode() == PaymentOption.PAYTM.getOrdinal()){
                ivPaymentMethodVal.setImageResource(R.drawable.ic_paytm_small);
            } else if(historyResponse.getData().get(0).getPaymentMode() == PaymentOption.MOBIKWIK.getOrdinal()){
                ivPaymentMethodVal.setImageResource(R.drawable.ic_mobikwik_small);
            } else if(historyResponse.getData().get(0).getPaymentMode() == PaymentOption.FREECHARGE.getOrdinal()){
                ivPaymentMethodVal.setImageResource(R.drawable.ic_freecharge_small);
            } else{
                ivPaymentMethodVal.setVisibility(View.GONE);
                tvPaymentMethodCash.setVisibility(View.VISIBLE);
            }

            if((historyResponse.getData().get(0).getDeliveryCharges() != null) && (historyResponse.getData().get(0).getDeliveryCharges() != 0)){
                tvDeliveryChargesVal.setTextColor(activity.getResources().getColor(R.color.text_color));
                tvDeliveryChargesVal.setText(String.format(getResources().getString(R.string.rupees_value_format), String.valueOf(historyResponse.getData().get(0).getDeliveryCharges().intValue())));
            } else{
                tvDeliveryChargesVal.setTextColor(activity.getResources().getColor(R.color.order_status_green));
                tvDeliveryChargesVal.setText(activity.getResources().getString(R.string.free));
            }

            tvTotalAmountVal.setText(String.format(getResources().getString(R.string.rupees_value_format), String.valueOf((historyResponse.getData().get(0).getOriginalOrderAmount()).intValue())));

            /*if((historyResponse.getData().get(0).getDeliveryCharges() != null) && (historyResponse.getData().get(0).getDeliveryCharges() != 0)){
                addFinalAmountView(llFinalAmount, getResources().getString(R.string.delivery_charges).toString(), historyResponse.getData().get(0).getDeliveryCharges().toString());
                addFinalAmountView(llFinalAmount, getResources().getString(R.string.total_amount).toString(),
                        String.valueOf(getSubTotalAmount(historyResponse) + (historyResponse.getData().get(0).getDeliveryCharges()).intValue()));
            }*/

            if((historyResponse.getData().get(0).getDiscount() != null) && (historyResponse.getData().get(0).getDiscount() != 0)){
                addFinalAmountView(llFinalAmount, getResources().getString(R.string.discount).toString(), historyResponse.getData().get(0).getDiscount().toString());
            }

            if((historyResponse.getData().get(0).getJugnooDeducted() != null) && (historyResponse.getData().get(0).getJugnooDeducted() != 0)){
                addFinalAmountView(llFinalAmount, getResources().getString(R.string.jugnoo_cash).toString(), historyResponse.getData().get(0).getJugnooDeducted().toString());
            }

            if((historyResponse.getData().get(0).getDiscount() != null) && (historyResponse.getData().get(0).getDiscount() != 0)
                    || (historyResponse.getData().get(0).getJugnooDeducted() != null) && (historyResponse.getData().get(0).getJugnooDeducted() != 0)){
                rlAmountPayable.setVisibility(View.VISIBLE);
                tvAmountPayableVal.setText(String.format(getResources().getString(R.string.rupees_value_format)
                        , String.valueOf(historyResponse.getData().get(0).getOriginalOrderAmount().intValue() - historyResponse.getData().get(0).getJugnooDeducted().intValue()
                                - historyResponse.getData().get(0).getDiscount().intValue() - historyResponse.getData().get(0).getWalletDeducted().intValue())));
            } else{
                rlAmountPayable.setVisibility(View.GONE);
                llFinalAmount.setVisibility(View.GONE);
            }

            if(getBilledAmount(historyResponse) < historyResponse.getData().get(0).getOriginalOrderAmount().intValue()){
                llRefund.setVisibility(View.VISIBLE);
                tvBilledAmountVal.setText(String.format(getResources().getString(R.string.rupees_value_format), String.valueOf(getBilledAmount(historyResponse))));
                tvRefundVal.setText(String.format(getResources().getString(R.string.rupees_value_format), String.valueOf(historyResponse.getData().get(0).getOrderRefundAmount().intValue())));
            } else{
                llRefund.setVisibility(View.GONE);
            }


            if (orderHistory.getCancellable() == 1) {
                orderCancel.setVisibility(View.VISIBLE);
                orderComplete.setVisibility(View.GONE);
                if(activity instanceof SupportActivity) {
                    cancelfeedbackBtn.setVisibility(View.GONE);
                }

            } else {
                orderComplete.setVisibility(View.VISIBLE);
                orderCancel.setVisibility(View.GONE);
                cancelfeedbackBtn.setVisibility(View.GONE);
                if (activity instanceof RideTransactionsActivity) {
                    feedbackBtn.setText(R.string.need_help);
                    if (orderHistory.getCanReorder() == 1) {
                        reorderBtn.setVisibility(View.VISIBLE);
                    } else {
                        orderComplete.setVisibility(View.GONE);
                        orderCancel.setVisibility(View.VISIBLE);
                        buttonCancelOrder.setText(R.string.need_help);
                    }
                } else {
                    if (orderHistory.getCanReorder() == 1 && !(activity instanceof FreshActivity)) {
                        reorderBtn.setVisibility(View.VISIBLE);
                        feedbackBtn.setVisibility(View.GONE);
                    } else if (orderHistory.getCanReorder() == 1 && (activity instanceof FreshActivity)) {
                        orderComplete.setVisibility(View.GONE);
                        orderCancel.setVisibility(View.VISIBLE);
                        buttonCancelOrder.setText(R.string.ok);
                    } else if (orderHistory.getCanReorder() == 0 && !(activity instanceof FreshActivity)) {
                        orderComplete.setVisibility(View.GONE);
                        orderCancel.setVisibility(View.VISIBLE);
                        buttonCancelOrder.setText(R.string.ok);
                    } else if (orderHistory.getCanReorder() == 0 && (activity instanceof FreshActivity)) {
                        orderComplete.setVisibility(View.GONE);
                        orderCancel.setVisibility(View.VISIBLE);
                        buttonCancelOrder.setText(R.string.ok);
                    }  else {
                        orderComplete.setVisibility(View.GONE);
                        orderCancel.setVisibility(View.VISIBLE);
                        buttonCancelOrder.setText(R.string.need_help);
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
                if (orderHistory.getCancellable() == 1) {
                    DialogPopup.alertPopupTwoButtonsWithListeners(activity, "", "Are you sure you want to cancel this order?", getResources().getString(R.string.ok),
                            getResources().getString(R.string.cancel), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    cancelOrderApiCall(orderHistory.getOrderId());
                                }
                            }, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            }, false, false);
                } else {
                    feedbackBtn.performClick();
                }
                break;
            case R.id.feedbackBtn:
                if (activity instanceof RideTransactionsActivity) {
                    new TransactionUtils().openRideIssuesFragment(activity,
                            ((RideTransactionsActivity) activity).getContainer(),
                            -1, -1, null, null, 0, false, 0, orderHistory);
                } else{
                    activity.onBackPressed();
                }
                break;
            case R.id.reorderBtn:
                saveHistoryCardToSP(orderHistory);
                break;
            case R.id.cancelfeedbackBtn:
                new TransactionUtils().openRideIssuesFragment(activity,
                        ((RideTransactionsActivity) activity).getContainer(),
                        -1, -1, null, null, 0, false, 0, orderHistory);
                break;
        }
    }

    public void saveHistoryCardToSP(HistoryResponse.Datum orderHistory) {
        try {
            if(orderHistory.getProductType() == ProductType.FRESH.getOrdinal()) {
                Prefs.with(activity).save(Constants.SP_FRESH_CART, Constants.EMPTY_JSON_OBJECT);
            } else if(orderHistory.getProductType() == ProductType.GROCERY.getOrdinal()){
                Prefs.with(activity).save(Constants.SP_GROCERY_CART, Constants.EMPTY_JSON_OBJECT);
            }
            JSONObject jCart = new JSONObject();
            if (orderHistory != null && orderHistory.getOrderItems() != null) {
                for (HistoryResponse.OrderItem subItem : orderHistory.getOrderItems()) {
                    if (subItem.getItemQuantity() > 0) {
                        try {
                            jCart.put(String.valueOf(subItem.getSubItemId()), (int) subItem.getItemQuantity());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            if(orderHistory.getProductType() == ProductType.FRESH.getOrdinal()) {
                Prefs.with(activity).save(Constants.SP_FRESH_CART, jCart.toString());
                sendMessage(0);
            } else if(orderHistory.getProductType() == ProductType.GROCERY.getOrdinal()){
                Prefs.with(activity).save(Constants.SP_GROCERY_CART, jCart.toString());
                sendMessage(2);
            }

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

    private void sendMessage(int type) {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent(Data.LOCAL_BROADCAST);
        // You can also include some extra data.
        intent.putExtra("message", "This is my message!");
        intent.putExtra("open_type", type);
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
    }
}

package com.sabkuchfresh.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.sabkuchfresh.adapters.FreshOrderItemAdapter;
import com.sabkuchfresh.analytics.FlurryEventNames;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.OrderHistoryResponse;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.RideTransactionsActivity;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.HistoryResponse;
import product.clicklabs.jugnoo.support.TransactionUtils;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

@SuppressLint("ValidFragment")
public class FreshOrderSummaryFragment extends BaseFragment implements FlurryEventNames, Constants, View.OnClickListener {

    private final String TAG = FreshOrderSummaryFragment.class.getSimpleName();

    private RelativeLayout relativeLayoutRoot;
    private RecyclerView recyclerViewOrderItems;
    private FreshOrderItemAdapter freshOrderItemAdapter;

    private LinearLayout orderComplete;

    private Button buttonCancelOrder, reorderBtn, feedbackBtn;
    private View rootView;
    private FragmentActivity activity;

    private HistoryResponse.Datum orderHistory;

    public FreshOrderSummaryFragment() {

    }
    public FreshOrderSummaryFragment(HistoryResponse.Datum orderHistory) {
        this.orderHistory = orderHistory;
    }


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
        rootView = inflater.inflate(R.layout.fragment_fresh_order_summary, container, false);

        activity = (FragmentActivity) getActivity();

//        activity.fragmentUISetup(this);
        setActivityTitle();

        relativeLayoutRoot = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutRoot);
        try {
            if (relativeLayoutRoot != null) {
                new ASSL(activity, relativeLayoutRoot, 1134, 720, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        buttonCancelOrder = (Button) rootView.findViewById(R.id.buttonCancelOrder);
        buttonCancelOrder.setTypeface(Fonts.mavenRegular(activity));
        reorderBtn = (Button) rootView.findViewById(R.id.reorderBtn);
        reorderBtn.setTypeface(Fonts.mavenRegular(activity));
        feedbackBtn = (Button) rootView.findViewById(R.id.feedbackBtn);
        feedbackBtn.setTypeface(Fonts.mavenRegular(activity));

        buttonCancelOrder.setOnClickListener(this);
        reorderBtn.setOnClickListener(this);
        feedbackBtn.setOnClickListener(this);

        orderComplete = (LinearLayout) rootView.findViewById(R.id.order_complete);

        recyclerViewOrderItems = (RecyclerView) rootView.findViewById(R.id.recyclerViewOrderItems);
        recyclerViewOrderItems.setLayoutManager(new LinearLayoutManager(activity));
        recyclerViewOrderItems.setItemAnimator(new DefaultItemAnimator());
        recyclerViewOrderItems.setHasFixedSize(false);

        try {
                freshOrderItemAdapter = new FreshOrderItemAdapter(activity, orderHistory);
                recyclerViewOrderItems.setAdapter(freshOrderItemAdapter);

            if (orderHistory.getCancellable() == 1) {
                buttonCancelOrder.setVisibility(View.VISIBLE);
                orderComplete.setVisibility(View.GONE);
                buttonCancelOrder.setText(R.string.cancel_order);

            } else {
                orderComplete.setVisibility(View.VISIBLE);
                buttonCancelOrder.setVisibility(View.GONE);
                if (activity instanceof RideTransactionsActivity) {
                    feedbackBtn.setText(R.string.need_help);
                    if (orderHistory.getCanReorder() == 1) {
                        reorderBtn.setVisibility(View.VISIBLE);
                    } else {
                        orderComplete.setVisibility(View.GONE);
                        buttonCancelOrder.setVisibility(View.VISIBLE);
                        buttonCancelOrder.setText(R.string.need_help);
                    }
                } else {
                    if (orderHistory.getCanReorder() == 1 && !(activity instanceof FreshActivity)) {
                        reorderBtn.setVisibility(View.VISIBLE);
                        feedbackBtn.setVisibility(View.GONE);
                    } else {
                        orderComplete.setVisibility(View.GONE);
                        buttonCancelOrder.setVisibility(View.VISIBLE);
                        buttonCancelOrder.setText(R.string.need_help);
                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return rootView;
    }


    private void setActivityTitle() {
        if (activity instanceof RideTransactionsActivity) {
            ((RideTransactionsActivity) activity).setTitle(activity.getResources().getString(R.string.order_fragment));
        } else if (activity instanceof product.clicklabs.jugnoo.support.SupportActivity) {
            ((product.clicklabs.jugnoo.support.SupportActivity) activity).setTitle(activity.getResources().getString(R.string.order_fragment));
        } else if(activity instanceof FreshActivity) {
            ((FreshActivity) activity).fragmentUISetup(this);

        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        ASSL.closeActivity(relativeLayoutRoot);
        System.gc();
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            setActivityTitle();
            if (orderHistory.getCancellable() == 1) {
                buttonCancelOrder.setVisibility(View.VISIBLE);
                orderComplete.setVisibility(View.GONE);
                buttonCancelOrder.setText(R.string.cancel_order);

            } else {
                orderComplete.setVisibility(View.VISIBLE);
                buttonCancelOrder.setVisibility(View.GONE);
                if (activity instanceof RideTransactionsActivity) {
                    feedbackBtn.setText(R.string.need_help);
                    if (orderHistory.getCanReorder() == 1) {
                        reorderBtn.setVisibility(View.VISIBLE);
                    } else {
                        orderComplete.setVisibility(View.GONE);
                        buttonCancelOrder.setVisibility(View.VISIBLE);
                        buttonCancelOrder.setText(R.string.need_help);
                    }
                } else {
                    if (orderHistory.getCanReorder() == 1 && !(activity instanceof FreshActivity)) {
                        reorderBtn.setVisibility(View.VISIBLE);
                        feedbackBtn.setVisibility(View.GONE);
                    } else {
                        orderComplete.setVisibility(View.GONE);
                        buttonCancelOrder.setVisibility(View.VISIBLE);
                        buttonCancelOrder.setText(R.string.need_help);
                    }

                }
            }
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
                        Log.i(TAG, "Fresh order cancel response = " + responseStr);
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
                        Log.e(TAG, "Fresh Cancel Order error" + error.toString());
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
        }
    }

    public void saveHistoryCardToSP(HistoryResponse.Datum orderHistory) {
        try {
            Prefs.with(activity).save(Constants.SP_FRESH_CART, Constants.EMPTY_JSON_OBJECT);

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
            Prefs.with(activity).save(Constants.SP_FRESH_CART, jCart.toString());
            sendMessage();
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

    private void sendMessage() {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent(Data.LOCAL_BROADCAST);
        // You can also include some extra data.
        intent.putExtra("message", "This is my message!");
        intent.putExtra("open_type", 0);
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
    }

}

package sabkuchfresh.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.R;
import com.sabkuchfresh.adapters.FreshOrderItemAdapter;
import com.sabkuchfresh.analytics.FlurryEventNames;
import com.sabkuchfresh.datastructure.ApiResponseFlags;
import com.sabkuchfresh.datastructure.DialogErrorType;
import com.sabkuchfresh.datastructure.PaymentOption;
import com.sabkuchfresh.datastructure.SPLabels;
import com.sabkuchfresh.home.SupportActivity;
import com.sabkuchfresh.retrofit.RestClient;
import com.sabkuchfresh.retrofit.model.OrderHistory;
import com.sabkuchfresh.retrofit.model.OrderHistoryResponse;
import com.sabkuchfresh.retrofit.model.OrderItem;
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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class FreshOrderSummaryFragment extends BaseFragment implements FlurryEventNames, Constants, View.OnClickListener {

    private final String TAG = FreshOrderSummaryFragment.class.getSimpleName();

    private RelativeLayout relativeLayoutRoot;
    private RecyclerView recyclerViewOrderItems;
    private FreshOrderItemAdapter freshOrderItemAdapter;

    private TextView textViewOrderIdValue, textViewOrderDeliveryDateValue, textViewOrderDeliverySlotValue,
            textViewOrderTimeValue, textViewOrderAddressValue,
            textViewTotalAmountValue, textViewDeliveryChargesValue, textViewAmountPayableValue,
            textViewPaymentMode, textViewPaymentModeValue;

    private RelativeLayout discountLayout, jclayout, paytmlayout;
    private LinearLayout orderComplete;
    private TextView textViewDiscount, textViewDiscountValue, textViewjc, textViewjcValue, textViewpaytm, textViewpaytmValue;

    private Button buttonCancelOrder, reorderBtn, feedbackBtn;
    private OrderHistory orderHistory;

    private View rootView;
    private SupportActivity activity;

    private int orderType = 0;

    public FreshOrderSummaryFragment() {
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

        activity = (SupportActivity) getActivity();
        activity.fragmentUISetup(this);

        relativeLayoutRoot = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutRoot);
        try {
            if (relativeLayoutRoot != null) {
                new ASSL(activity, relativeLayoutRoot, 1134, 720, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ((TextView) rootView.findViewById(R.id.textViewOrderId)).setTypeface(Fonts.mavenRegular(activity));
        ((TextView) rootView.findViewById(R.id.textViewOrderDeliveryDate)).setTypeface(Fonts.mavenRegular(activity));
        ((TextView) rootView.findViewById(R.id.textViewOrderDeliverySlot)).setTypeface(Fonts.mavenRegular(activity));
        ((TextView) rootView.findViewById(R.id.textViewOrderReceipt)).setTypeface(Fonts.mavenRegular(activity));
        ((TextView) rootView.findViewById(R.id.textViewTotalAmount)).setTypeface(Fonts.mavenRegular(activity));
        ((TextView) rootView.findViewById(R.id.textViewDeliveryCharges)).setTypeface(Fonts.mavenRegular(activity));
        ((TextView) rootView.findViewById(R.id.textViewAmountPayable)).setTypeface(Fonts.mavenRegular(activity));
        ((TextView) rootView.findViewById(R.id.textViewPaymentBy)).setTypeface(Fonts.mavenRegular(activity));

        discountLayout = (RelativeLayout) rootView.findViewById(R.id.discountLayout);
        ;
        jclayout = (RelativeLayout) rootView.findViewById(R.id.jclayout);
        paytmlayout = (RelativeLayout) rootView.findViewById(R.id.paytmlayout);

        ((TextView) rootView.findViewById(R.id.textViewDiscount)).setTypeface(Fonts.mavenRegular(activity));
        ((TextView) rootView.findViewById(R.id.textViewjc)).setTypeface(Fonts.mavenRegular(activity));
        ((TextView) rootView.findViewById(R.id.textViewpaytm)).setTypeface(Fonts.mavenRegular(activity));

        textViewDiscountValue = (TextView) rootView.findViewById(R.id.textViewDiscountValue);
        textViewDiscountValue.setTypeface(Fonts.mavenRegular(activity));
        textViewjcValue = (TextView) rootView.findViewById(R.id.textViewjcValue);
        textViewjcValue.setTypeface(Fonts.mavenRegular(activity));
        textViewpaytmValue = (TextView) rootView.findViewById(R.id.textViewpaytmValue);
        textViewpaytmValue.setTypeface(Fonts.mavenRegular(activity));

        textViewOrderIdValue = (TextView) rootView.findViewById(R.id.textViewOrderIdValue);
        textViewOrderIdValue.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
        textViewOrderDeliveryDateValue = (TextView) rootView.findViewById(R.id.textViewOrderDeliveryDateValue);
        textViewOrderDeliveryDateValue.setTypeface(Fonts.mavenRegular(activity));
        textViewOrderDeliverySlotValue = (TextView) rootView.findViewById(R.id.textViewOrderDeliverySlotValue);
        textViewOrderDeliverySlotValue.setTypeface(Fonts.mavenRegular(activity));

        textViewOrderTimeValue = (TextView) rootView.findViewById(R.id.textViewOrderTimeValue);
        textViewOrderTimeValue.setTypeface(Fonts.mavenRegular(activity));
        textViewOrderAddressValue = (TextView) rootView.findViewById(R.id.textViewOrderAddressValue);
        textViewOrderAddressValue.setTypeface(Fonts.mavenRegular(activity));

        textViewTotalAmountValue = (TextView) rootView.findViewById(R.id.textViewTotalAmountValue);
        textViewTotalAmountValue.setTypeface(Fonts.mavenRegular(activity));
        textViewDeliveryChargesValue = (TextView) rootView.findViewById(R.id.textViewDeliveryChargesValue);
        textViewDeliveryChargesValue.setTypeface(Fonts.mavenRegular(activity));
        textViewAmountPayableValue = (TextView) rootView.findViewById(R.id.textViewAmountPayableValue);
        textViewAmountPayableValue.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
        textViewPaymentMode = (TextView) rootView.findViewById(R.id.textViewPaymentMode);
        textViewPaymentMode.setTypeface(Fonts.mavenRegular(activity));
        textViewPaymentModeValue = (TextView) rootView.findViewById(R.id.textViewPaymentModeValue);
        textViewPaymentModeValue.setTypeface(Fonts.mavenRegular(activity));
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
            if (activity.getOrderHistoryOpened() != null) {
                orderHistory = activity.getOrderHistoryOpened();

                try {
                    orderType = orderHistory.getStoreId();
                } catch (Exception w) {
                    orderType = 1;
                }

                freshOrderItemAdapter = new FreshOrderItemAdapter(activity, (ArrayList<OrderItem>) orderHistory.getOrderItems());
                recyclerViewOrderItems.setAdapter(freshOrderItemAdapter);

                textViewOrderIdValue.setText(String.valueOf(orderHistory.getOrderId()));

                textViewTotalAmountValue.setText(String.format(activity.getResources().getString(R.string.rupees_value_format),
                        Utils.getMoneyDecimalFormat().format(orderHistory.getOrderAmount() - orderHistory.getDeliveryCharges() + orderHistory.getJugnooDeducted()
                                + orderHistory.getDiscount())));
                textViewDeliveryChargesValue.setText(String.format(activity.getResources().getString(R.string.rupees_value_format), Utils.getMoneyDecimalFormat().format(orderHistory.getDeliveryCharges())));
                textViewAmountPayableValue.setText(String.format(activity.getResources().getString(R.string.rupees_value_format), Utils.getMoneyDecimalFormat().format(orderHistory.getOrderAmount())));
                if (orderHistory.getPaymentMode().equals(PaymentOption.PAYTM.getOrdinal())) {
                    textViewPaymentMode.setText(activity.getResources().getString(R.string.paytm));
                } else {
                    textViewPaymentMode.setText(activity.getResources().getString(R.string.cash));
                }
                textViewPaymentModeValue.setText(String.format(activity.getResources().getString(R.string.rupees_value_format), Utils.getMoneyDecimalFormat().format(orderHistory.getOrderAmount())));

                if (orderHistory.getStartTime() != null && orderHistory.getEndTime() != null) {
                    textViewOrderDeliverySlotValue.setText(DateOperations.convertDayTimeAPViaFormat(orderHistory.getStartTime()) + " - " + DateOperations.convertDayTimeAPViaFormat(orderHistory.getEndTime()));
                } else {
                    textViewOrderDeliverySlotValue.setText("");
                }
                if (orderHistory.getExpectedDeliveryDate() != null) {
                    textViewOrderDeliveryDateValue.setText(orderHistory.getExpectedDeliveryDate());
//                    textViewOrderDeliveryDateValue.setText(DateOperations.getDate(orderHistory.getExpectedDeliveryDate()));
                } else {
                    textViewOrderDeliveryDateValue.setText("");
                }

                if (orderHistory.getDiscount() > 0) {
                    textViewDiscountValue.setText(String.format(activity.getResources().getString(R.string.rupees_value_format), Utils.getMoneyDecimalFormat().format(orderHistory.getDiscount())));
                } else {
                    discountLayout.setVisibility(View.GONE);
                }
                if (orderHistory.getJugnooDeducted() > 0) {
                    textViewjcValue.setText(String.format(activity.getResources().getString(R.string.rupees_value_format), Utils.getMoneyDecimalFormat().format(orderHistory.getJugnooDeducted())));
                } else {
                    jclayout.setVisibility(View.GONE);
                }
                if (orderHistory.getPaytmDeducted() > 0) {
                    textViewpaytmValue.setText(String.format(activity.getResources().getString(R.string.rupees_value_format), Utils.getMoneyDecimalFormat().format(orderHistory.getPaytmDeducted())));
                } else {
                    paytmlayout.setVisibility(View.GONE);
                }
                textViewOrderTimeValue.setText(DateOperations.getDate(DateOperations.utcToLocalTZ(orderHistory.getOrderTime())));
                textViewOrderAddressValue.setText(orderHistory.getDeliveryAddress());

                if (orderHistory.getCancellable() == 1) {
                    buttonCancelOrder.setVisibility(View.VISIBLE);
                    orderComplete.setVisibility(View.GONE);
                    buttonCancelOrder.setText(R.string.cancel_order);

                } else {
                    if (orderHistory.getCanReorder() == 1) {
                        orderComplete.setVisibility(View.VISIBLE);
                        buttonCancelOrder.setVisibility(View.GONE);
                        if (orderHistory.getPendingFeedback() == 1) {
                            feedbackBtn.setText(R.string.feedback);
                        } else {
                            feedbackBtn.setText(R.string.ok);
                        }
                    } else {
                        buttonCancelOrder.setVisibility(View.VISIBLE);
                        orderComplete.setVisibility(View.GONE);

                        if (orderHistory.getPendingFeedback() == 1) {
                            buttonCancelOrder.setText(R.string.feedback);
                        } else {
                            buttonCancelOrder.setText(R.string.ok);
                        }
                    }
                }


//				if(orderHistory.getCancellable() == 1){
//					buttonCancelOrder.setText(R.string.cancel_order);
//                    orderComplete.setVisibility(View.GONE);
//				} else if(orderHistory.getPendingFeedback() == 1) {
//                    buttonCancelOrder.setVisibility(View.GONE);
//                    orderComplete.setVisibility(View.VISIBLE);
//                    feedbackBtn.setText(R.string.feedback);
//                }
//				else {
//                    orderComplete.setVisibility(View.VISIBLE);
//                    buttonCancelOrder.setVisibility(View.GONE);
//                    feedbackBtn.setText(R.string.ok);
//				}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//		buttonCancelOrder.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				try {
//					if(activity.getOrderHistoryOpened().getCancellable() == 1){
//
//						DialogPopup.alertPopupTwoButtonsWithListeners(activity, "", "Are you sure you want to cancel this order?", getResources().getString(R.string.ok),
//								getResources().getString(R.string.cancel), new View.OnClickListener() {
//							@Override
//							public void onClick(View v) {
//								cancelOrderApiCall(activity.getOrderHistoryOpened().getOrderId());
//							}
//						}, new View.OnClickListener() {
//							@Override
//							public void onClick(View v) {
//
//							}
//						}, false, false);
//					} else if(orderHistory.getPendingFeedback() == 1) {
////                        openFeedBack(orderHistory.getOrderId());
//                        activity.orderId = String.valueOf(orderHistory.getOrderId());
//                        activity.questionType = String.valueOf(orderHistory.getQuestionType());
//                        activity.question = String.valueOf(orderHistory.getQuestion());
//                        activity.skip = true;
//                        activity.openOrderFeedback();
//                    } else {
//						activity.onBackPressed();
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//
//			}
//		});

        return rootView;
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
            activity.fragmentUISetup(this);
            orderHistory = activity.getOrderHistoryOpened();

//            if(orderHistory.getCancellable() == 1){
//                buttonCancelOrder.setText(R.string.cancel_order);
//                orderComplete.setVisibility(View.GONE);
//            } else if(orderHistory.getPendingFeedback() == 1) {
//                buttonCancelOrder.setVisibility(View.GONE);
//                orderComplete.setVisibility(View.VISIBLE);
//                feedbackBtn.setText(R.string.feedback);
//            } else {
//                orderComplete.setVisibility(View.VISIBLE);
//                buttonCancelOrder.setVisibility(View.GONE);
//                feedbackBtn.setText(R.string.ok);
//            }

            if (orderHistory.getCancellable() == 1) {
                buttonCancelOrder.setVisibility(View.VISIBLE);
                orderComplete.setVisibility(View.GONE);
                buttonCancelOrder.setText(R.string.cancel_order);

            } else {
                if (orderHistory.getCanReorder() == 1) {
                    orderComplete.setVisibility(View.VISIBLE);
                    buttonCancelOrder.setVisibility(View.GONE);
                    if (orderHistory.getPendingFeedback() == 1) {
                        feedbackBtn.setText(R.string.feedback);
                    } else {
                        feedbackBtn.setText(R.string.ok);
                    }
                } else {
                    buttonCancelOrder.setVisibility(View.VISIBLE);
                    orderComplete.setVisibility(View.GONE);

                    if (orderHistory.getPendingFeedback() == 1) {
                        buttonCancelOrder.setText(R.string.feedback);
                    } else {
                        buttonCancelOrder.setText(R.string.ok);
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
                        Prefs.with(activity).save(SPLabels.PAYTM_CHECK_BALANCE_LAST_TIME, time);
//                        activity.resumeMethod();
                        try {
                            JSONObject jObj = new JSONObject(responseStr);
                            String message = JSONParser.getServerMessage(jObj);
                            if (orderHistoryResponse.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
                                DialogPopup.alertPopupWithListener(activity, "", message, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        activity.onBackPressed();
                                        activity.getFreshOrderHistoryFragment().getOrderHistory();
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
                new Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        cancelOrderApiCall(activity.getOrderHistoryOpened().getOrderId());
                    }

                    @Override
                    public void neutralClick(View view) {

                    }

                    @Override
                    public void negativeClick(View view) {
                    }
                });
    }

    private void openFeedBack(int orderId) {


        Intent intent = new Intent(activity, SupportActivity.class);
        intent.putExtra(Constants.FRAGMENT_SELECTED, AppConstant.SupportType.FEED_BACK);
        intent.putExtra(Constants.QUESTION, String.valueOf(orderHistory.getQuestion()));
        intent.putExtra(Constants.QUESTION_TYPE, "" + String.valueOf(orderHistory.getQuestionType()));
        intent.putExtra(Constants.ORDER_ID, String.valueOf(orderId));
        intent.putExtra(Constants.SKIP, true);

        startActivity(intent);
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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
                                    cancelOrderApiCall(activity.getOrderHistoryOpened().getOrderId());
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
                if (orderHistory.getPendingFeedback() == 1) {
//                        openFeedBack(orderHistory.getOrderId());
                    activity.orderId = String.valueOf(orderHistory.getOrderId());
                    activity.questionType = String.valueOf(orderHistory.getQuestionType());
                    activity.question = String.valueOf(orderHistory.getQuestion());
                    activity.skip = true;
                    activity.openOrderFeedback();
                } else {
                    activity.onBackPressed();
                }
                break;
            case R.id.reorderBtn:
                activity.saveHistoryCardToSP(orderHistory);
                break;
        }
    }
}

package com.jugnoo.pay.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jugnoo.pay.activities.SendMoneyActivity;
import com.jugnoo.pay.models.AccessTokenRequest;
import com.jugnoo.pay.models.SelectUser;
import com.jugnoo.pay.models.TransacHistoryResponse;
import com.jugnoo.pay.utils.ApiResponseFlags;
import com.jugnoo.pay.utils.CallProgressWheel;
import com.sabkuchfresh.utils.AppConstant;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * Created by cl-macmini-38 on 06/06/16.
 */
public class PendingTrnscAdapater extends RecyclerView.Adapter<PendingTrnscAdapater.ViewHolder> {
    ArrayList<TransacHistoryResponse.TransactionHistory> transactionHistoryList;
    private Activity activity;
    private final String REQUEST_TO = "Requested to";
    private final String PAYMENT_TO = "Payment to";
    private final String REQUESTED_BY = "Requested by";
    private final String REQUESTED_FROM = "Requested from";
    private final String COMPLETED = "Completed";
    private final String FAILED = "Failed";
    private final String DECLINED = "Declined";
    private final String CANCELLED = "Cancelled";
    private String accessToken;
    private EventHandler eventHandler;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_pending_payment, parent, false);
        return new ViewHolder(itemView);
    }

    public PendingTrnscAdapater(Activity activity, ArrayList<TransacHistoryResponse.TransactionHistory> transactionHistories,
                                EventHandler eventHandler) {
        this.transactionHistoryList = transactionHistories;
        this.activity = activity;
        accessToken =  Data.userData.accessToken;      //Prefs.with(activity).getString(SharedPreferencesName.ACCESS_TOKEN, "");
        this.eventHandler = eventHandler;
    }

    private void notifyAdapter(){
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        TransacHistoryResponse.TransactionHistory transactionHistory = transactionHistoryList.get(position);
        holder.textViewName.setText(transactionHistory.getName());
        holder.textViewNameInitial.setText(getInitials(transactionHistory.getName()));
        if (transactionHistory.getTxnType() == TransacHistoryResponse.Type.REQUEST_BY_PENDING.getOrdinal()) {
            holder.textViewRequestStatus.setText(R.string.requested_by);
            holder.buttonPayNow.setText(R.string.pay_now);
            holder.buttonDismiss.setText(R.string.decline);
            holder.buttonPayNow.setBackgroundResource(R.drawable.button_theme);
        }
        else if (transactionHistory.getTxnType() == TransacHistoryResponse.Type.REQUESTED_FROM_PENDING.getOrdinal()) {
            holder.textViewRequestStatus.setText(R.string.requested_from);
            holder.buttonPayNow.setText(R.string.remind);
            holder.buttonDismiss.setText(R.string.cancel);
            holder.buttonPayNow.setBackgroundResource((transactionHistory.getIs_remind_active() == 1) ? R.drawable.button_theme : R.drawable.button_grey);
            holder.buttonPayNow.setEnabled(transactionHistory.getIs_remind_active() == 1);
        }
        holder.textViewPaymentValue.setText(activity.getString(R.string.rupees_value_format, String.valueOf(transactionHistory.getAmount())));

        holder.buttonPayNow.setTag(position);
        holder.buttonDismiss.setTag(position);

        holder.buttonDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    final int pos = (int) view.getTag();
                    final TransacHistoryResponse.TransactionHistory transactionHistory = transactionHistoryList.get(pos);
                    String buttonText = "";
                    if(transactionHistory.getTxnType() == TransacHistoryResponse.Type.REQUEST_BY_PENDING.getOrdinal()){
						buttonText = activity.getString(R.string.decline_request_message);
					} else if (transactionHistory.getTxnType() == TransacHistoryResponse.Type.REQUESTED_FROM_PENDING.getOrdinal()){
						buttonText = activity.getString(R.string.cancel_request_message);
					}
                    DialogPopup.alertPopupTwoButtonsWithListeners(activity, "", buttonText,
                            activity.getString(R.string.ok),
                            activity.getString(R.string.cancel),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (transactionHistory.getTxnType() == TransacHistoryResponse.Type.REQUEST_BY_PENDING.getOrdinal()) {
                                        declineTranscApi(transactionHistory.getId(), pos);
                                    } else if (transactionHistory.getTxnType() == TransacHistoryResponse.Type.REQUESTED_FROM_PENDING.getOrdinal()) {
                                        cancelTranscApi(transactionHistory.getId(), pos);
                                    }
                                }
                            },
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            }, false, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        holder.buttonPayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final int pos = (int) v.getTag();
                    final TransacHistoryResponse.TransactionHistory transactionHistory = transactionHistoryList.get(pos);
                    if(transactionHistory.getTxnType() == TransacHistoryResponse.Type.REQUEST_BY_PENDING.getOrdinal()){
                        if (transactionHistory.getRequesterPhoneNo() != null && !transactionHistory.getRequesterPhoneNo().equalsIgnoreCase("")) {
                            SelectUser newData = new SelectUser();
                            newData.setName(transactionHistory.getName());
                            newData.setPhone(transactionHistory.getRequesterPhoneNo());
                            newData.setAmount(String.valueOf(transactionHistory.getAmount()));
                            newData.setOrderId(String.valueOf(transactionHistory.getId()));
                            newData.setMessage(String.valueOf(transactionHistory.getMessage()));

                            Intent intent = new Intent(activity, SendMoneyActivity.class);
                            intent.putExtra(AppConstant.REQUEST_STATUS, false);
                            intent.putExtra(AppConstant.REQUEST_STATUS_CONFIRMATION, true);
                            Bundle bun = new Bundle();
                            bun.putParcelable(AppConstant.CONTACT_DATA, newData);
                            intent.putExtras(bun);
                            activity.startActivity(intent);
                        }
                    }
                    else if (transactionHistory.getTxnType() == TransacHistoryResponse.Type.REQUESTED_FROM_PENDING.getOrdinal()){
                        apiRemindUser(transactionHistory.getId(), pos);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return this.transactionHistoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewNameInitial, textViewRequestStatus, textViewName, textViewMessage, textViewPaymentValue;
        public ImageView imageViewSep;
        public Button buttonDismiss, buttonPayNow;

        public ViewHolder(View view) {
            super(view);
            textViewNameInitial = (TextView) view.findViewById(R.id.textViewNameInitial); textViewNameInitial.setTypeface(Fonts.mavenMedium(activity), Typeface.BOLD);
            textViewRequestStatus = (TextView) view.findViewById(R.id.textViewRequestStatus); textViewRequestStatus.setTypeface(Fonts.mavenRegular(activity));
            textViewName = (TextView) view.findViewById(R.id.textViewName); textViewName.setTypeface(Fonts.mavenMedium(activity), Typeface.BOLD);
            textViewMessage = (TextView) view.findViewById(R.id.textViewMessage); textViewMessage.setTypeface(Fonts.mavenRegular(activity));
            textViewPaymentValue = (TextView) view.findViewById(R.id.textViewPaymentValue); textViewPaymentValue.setTypeface(Fonts.mavenMedium(activity), Typeface.BOLD);
            imageViewSep = (ImageView) view.findViewById(R.id.imageViewSep);
            buttonDismiss = (Button) view.findViewById(R.id.buttonDismiss); buttonDismiss.setTypeface(Fonts.mavenRegular(activity));
            buttonPayNow = (Button) view.findViewById(R.id.buttonPayNow); buttonPayNow.setTypeface(Fonts.mavenRegular(activity));
        }
    }


    // to cancel a particular transaction
    private void cancelTranscApi(int orderId, final int pos) {
        if (MyApplication.getInstance().isOnline()) {
            CallProgressWheel.showLoadingDialog(activity, activity.getString(R.string.please_wait));
            AccessTokenRequest accessTokenRequest = new AccessTokenRequest();
            accessTokenRequest.setAccess_token(accessToken);
            accessTokenRequest.setOrder_id(orderId);

            RestClient.getPayApiService().cancelTransc(accessTokenRequest, new Callback<TransacHistoryResponse>() {
                @Override
                public void success(TransacHistoryResponse transacHistoryResponse, Response response) {
                    CallProgressWheel.dismissLoadingDialog();
                    try {
                        int flag = transacHistoryResponse.getFlag();
                        if (flag == ApiResponseFlags.TXN_CANCELLED.getOrdinal()) {
                            eventHandler.refreshTransactions();
                        } else {
                            DialogPopup.alertPopup(activity, "", transacHistoryResponse.getMessage());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    CallProgressWheel.dismissLoadingDialog();
                    DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
                }
            });
        } else {
            DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_desc));
        }

    }



    // to decline a particular transaction
    private void declineTranscApi(int orderId, final int pos) {
        if (MyApplication.getInstance().isOnline()) {
            CallProgressWheel.showLoadingDialog(activity, activity.getString(R.string.please_wait));
            AccessTokenRequest accessTokenRequest = new AccessTokenRequest();
            accessTokenRequest.setAccess_token(accessToken);
            accessTokenRequest.setOrder_id(orderId);

            RestClient.getPayApiService().declineTransc(accessTokenRequest, new Callback<TransacHistoryResponse>() {
                @Override
                public void success(TransacHistoryResponse transacHistoryResponse, Response response) {
                    CallProgressWheel.dismissLoadingDialog();
                    try {
                        int flag = transacHistoryResponse.getFlag();
                        if (flag == ApiResponseFlags.TXN_DECLINED.getOrdinal()) {
                            eventHandler.refreshTransactions();
                        } else {
                            DialogPopup.alertPopup(activity, "", transacHistoryResponse.getMessage());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    CallProgressWheel.dismissLoadingDialog();
                    DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
                }
            });
        } else {
            DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_desc));
        }

    }


    public void apiRemindUser(final int orderId, final int pos) {
        try {
            if (MyApplication.getInstance().isOnline()) {
                CallProgressWheel.showLoadingDialog(activity, activity.getString(R.string.loading));
                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.KEY_CLIENT_ID, Config.getAutosClientId());
                params.put(Constants.KEY_ID, String.valueOf(orderId));

                new HomeUtil().putDefaultParams(params);
                RestClient.getPayApiService().remindUser(params, new Callback<SettleUserDebt>() {
                    @Override
                    public void success(SettleUserDebt settleUserDebt, Response response) {
                        CallProgressWheel.dismissLoadingDialog();
                        try{
                            if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == settleUserDebt.getFlag()){
                                transactionHistoryList.get(pos).setIs_remind_active(0);
                                notifyDataSetChanged();
                            } else {
                                DialogPopup.alertPopup(activity, "", settleUserDebt.getMessage());
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                            retryDialogRemindUser(DialogErrorType.SERVER_ERROR, orderId, pos);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        CallProgressWheel.dismissLoadingDialog();
                        retryDialogRemindUser(DialogErrorType.CONNECTION_LOST, orderId, pos);
                    }
                });
            } else {
                retryDialogRemindUser(DialogErrorType.NO_NET, orderId, pos);
            }
        } catch (Exception e) {
            DialogPopup.dismissLoadingDialog();
            e.printStackTrace();
        }

    }

    private void retryDialogRemindUser(DialogErrorType dialogErrorType, final int orderId, final int pos){
        DialogPopup.dialogNoInternet(activity,
                dialogErrorType,
                new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        apiRemindUser(orderId, pos);
                    }

                    @Override
                    public void neutralClick(View view) {

                    }

                    @Override
                    public void negativeClick(View view) {
                    }
                });
    }

    private String getInitials(String name){
        if(!TextUtils.isEmpty(name)){
            String[] arr = name.split(" ");
            if(arr.length > 1){
                return arr[0].substring(0, 1) + arr[1].substring(0, 1);
            } else if(arr.length == 1){
                return arr[0].substring(0, 1);
            }
        }
        return "";
    }

    public interface EventHandler{
        void refreshTransactions();
    }

}

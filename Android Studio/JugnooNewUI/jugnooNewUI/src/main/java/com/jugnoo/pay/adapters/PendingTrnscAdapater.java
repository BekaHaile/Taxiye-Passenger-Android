package com.jugnoo.pay.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
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
import com.jugnoo.pay.utils.CommonMethods;
import com.jugnoo.pay.utils.SingleButtonAlert;
import com.jugnoo.pay.utils.TwoButtonAlert;
import com.sabkuchfresh.utils.AppConstant;

import org.json.JSONObject;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.Fonts;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


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

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_pending_payment, parent, false);
        return new ViewHolder(itemView);
    }

    public PendingTrnscAdapater(Activity activity, ArrayList<TransacHistoryResponse.TransactionHistory> transactionHistories) {
        this.transactionHistoryList = transactionHistories;
        this.activity = activity;
        accessToken =  Data.userData.accessToken;      //Prefs.with(activity).getString(SharedPreferencesName.ACCESS_TOKEN, "");
    }

    private void notifyAdapter(){
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        TransacHistoryResponse.TransactionHistory transactionHistory = transactionHistoryList.get(position);
        holder.textViewName.setText(transactionHistory.getName());
        if (transactionHistory.getTxnType() == TransacHistoryResponse.Type.REQUEST_BY_PENDING.getOrdinal()) {
            holder.textViewRequestStatus.setText(R.string.requested_by);
            holder.buttonPayNow.setText(R.string.pay_now);
            holder.buttonDismiss.setText(R.string.decline);
        }
        else if (transactionHistory.getTxnType() == TransacHistoryResponse.Type.REQUESTED_FROM_PENDING.getOrdinal()) {
            holder.textViewRequestStatus.setText(R.string.requested_from);
            holder.buttonPayNow.setText(R.string.remind);
            holder.buttonDismiss.setText(R.string.cancel);
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
                    TwoButtonAlert.showAlert(activity, buttonText, AppConstant.NO, AppConstant.YES,
							new TwoButtonAlert.OnAlertOkCancelClickListener() {
						@Override
						public void onOkButtonClicked() {
							if (transactionHistory.getTxnType() == TransacHistoryResponse.Type.REQUEST_BY_PENDING.getOrdinal()) {
								declineTranscApi(transactionHistory.getId(), pos);
							} else if (transactionHistory.getTxnType() == TransacHistoryResponse.Type.REQUESTED_FROM_PENDING.getOrdinal()) {
                                cancelTranscApi(transactionHistory.getId(), pos);
                            }
                        }

						@Override
						public void onCancelButtonClicked() {

						}
					});
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
                        //TODO Remind api
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
        CallProgressWheel.showLoadingDialog(activity, "Please wait..");
        AccessTokenRequest accessTokenRequest = new AccessTokenRequest();
        accessTokenRequest.setAccess_token(accessToken);
        accessTokenRequest.setOrder_id(orderId);

        RestClient.getPayApiService().cancelTransc(accessTokenRequest, new Callback<TransacHistoryResponse>() {
            @Override
            public void success(TransacHistoryResponse transacHistoryResponse, Response response) {
                CallProgressWheel.dismissLoadingDialog();
                System.out.println("transacHistoryResponse.success");
                if (transacHistoryResponse != null) {
//                    Prefs.with(SignUpActivity.this).save(SharedPreferencesName.ACCESS_TOKEN, tokenGeneratedResponse.getToken());
//
                    int flag = transacHistoryResponse.getFlag();
                    if (flag == ApiResponseFlags.TXN_CANCELLED.getOrdinal()) {
                        TransacHistoryResponse.TransactionHistory obj = transactionHistoryList.get(pos);
                        transactionHistoryList.remove(obj);
                        notifyAdapter();
                    } else {
                        CommonMethods.callingBadToken((AppCompatActivity) activity, flag, transacHistoryResponse.getMessage());
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                CallProgressWheel.dismissLoadingDialog();
                System.out.println("transacHistoryResponse.failure");
                try {
                    if (error.getKind().equals(RetrofitError.Kind.NETWORK)) {
                        SingleButtonAlert.showAlert(activity, "No Internet Connection", "Ok");
//                        activity.showAlertNoInternet(activity);
                    } else {
                        String json = new String(((TypedByteArray) error.getResponse()
                                .getBody()).getBytes());

                        JSONObject jsonObject = new JSONObject(json);
                        SingleButtonAlert.showAlert(activity, jsonObject.getString("message"), AppConstant.OK);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }



    // to decline a particular transaction
    private void declineTranscApi(int orderId, final int pos) {
        CallProgressWheel.showLoadingDialog(activity, "Please wait..");
        AccessTokenRequest accessTokenRequest = new AccessTokenRequest();
        accessTokenRequest.setAccess_token(accessToken);
        accessTokenRequest.setOrder_id(orderId);

        RestClient.getPayApiService().declineTransc(accessTokenRequest, new Callback<TransacHistoryResponse>() {
            @Override
            public void success(TransacHistoryResponse transacHistoryResponse, Response response) {
                CallProgressWheel.dismissLoadingDialog();
                System.out.println("decline request");
                if (transacHistoryResponse != null) {
//                    Prefs.with(SignUpActivity.this).save(SharedPreferencesName.ACCESS_TOKEN, tokenGeneratedResponse.getToken());
//
                    int flag = transacHistoryResponse.getFlag();
                    if (flag == ApiResponseFlags.TXN_DECLINED.getOrdinal()) {
                        TransacHistoryResponse.TransactionHistory obj = transactionHistoryList.get(pos);
                        transactionHistoryList.remove(obj);
                        notifyAdapter();
                    } else {
                        CommonMethods.callingBadToken((AppCompatActivity) activity, flag, transacHistoryResponse.getMessage());
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                CallProgressWheel.dismissLoadingDialog();
                System.out.println("transacHistoryResponse.failure");
                try {
                    if (error.getKind().equals(RetrofitError.Kind.NETWORK)) {
                        SingleButtonAlert.showAlert(activity, "No Internet Connection", "Ok");
//                        activity.showAlertNoInternet(activity);
                    } else {
                        String json = new String(((TypedByteArray) error.getResponse()
                                .getBody()).getBytes());

                        JSONObject jsonObject = new JSONObject(json);
                        SingleButtonAlert.showAlert(activity, jsonObject.getString("message"), AppConstant.OK);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

}

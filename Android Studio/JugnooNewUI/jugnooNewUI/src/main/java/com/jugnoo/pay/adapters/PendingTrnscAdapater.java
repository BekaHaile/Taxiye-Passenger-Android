package com.jugnoo.pay.adapters;

import android.app.Activity;
import android.content.Intent;
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
import com.jugnoo.pay.activities.TransacHistoryActivity;
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

import java.util.List;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.retrofit.RestClient;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


/**
 * Created by cl-macmini-38 on 06/06/16.
 */
public class PendingTrnscAdapater extends RecyclerView.Adapter<PendingTrnscAdapater.MyViewHolder> {
    List<TransacHistoryResponse.TransactionHistory> transactionHistoryList;
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
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transac_history_item, parent, false);
        return new MyViewHolder(itemView);
    }

    public PendingTrnscAdapater(Activity activity, List<TransacHistoryResponse.TransactionHistory> transactionHistories) {
        this.transactionHistoryList = transactionHistories;
        this.activity = activity;
        accessToken =  Data.userData.accessToken;      //Prefs.with(activity).getString(SharedPreferencesName.ACCESS_TOKEN, "");
    }

    private void notifyAdapter(){
        notifyDataSetChanged();
        if(transactionHistoryList.size() < 1){
            ((TransacHistoryActivity)activity).getLinearLayoutNoTxn().setVisibility(View.VISIBLE);
        } else {
            ((TransacHistoryActivity)activity).getLinearLayoutNoTxn().setVisibility(View.GONE);
        }
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.nameTxt.setText(transactionHistoryList.get(position).getName());
        if (transactionHistoryList.get(position).getTxnType() == 1) {
            holder.requestedTypeTxt.setText(PAYMENT_TO);
            //holder.payBtn.setVisibility(View.VISIBLE);
            // holder.declinBtn.setVisibility(View.VISIBLE);
            //holder.declinBtn.setText("Decline");
            holder.requestImage.setBackgroundResource(R.drawable.payment);
        } else if(transactionHistoryList.get(position).getTxnType() == 2) {
            holder.requestedTypeTxt.setText(REQUESTED_FROM);
            holder.declinBtn.setVisibility(View.VISIBLE);
            holder.declinBtn.setText("Cancel");
            holder.requestImage.setBackgroundResource(R.drawable.requested);
            holder.payBtn.setVisibility(View.GONE);
        } else if(transactionHistoryList.get(position).getTxnType() == 3) {
            holder.requestedTypeTxt.setText(REQUESTED_BY);
            holder.declinBtn.setVisibility(View.VISIBLE);
            holder.declinBtn.setText("Decline");
            holder.requestImage.setBackgroundResource(R.drawable.requested);
            holder.payBtn.setVisibility(View.VISIBLE);
        }

        holder.dateTxt.setText(CommonMethods.getDateFromUTC(transactionHistoryList.get(position).getDate()));
        holder.amountTxt.setText("Rs. " + transactionHistoryList.get(position).getAmount());


        holder.declinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String buttonText = "Are you sure you want to cancel the request?";
                if(holder.declinBtn.getText().toString().equalsIgnoreCase("Decline")){
                    buttonText = "Are you sure you want to decline the request?";
                } else{
                    buttonText = "Are you sure you want to cancel the request?";
                }

                TwoButtonAlert.showAlert(activity, buttonText, AppConstant.NO, AppConstant.YES,
                        new TwoButtonAlert.OnAlertOkCancelClickListener() {
                    @Override
                    public void onOkButtonClicked() {
                        if (transactionHistoryList.get(position).getTxnType() == 2) {
                            cancelTranscApi(transactionHistoryList.get(position).getId(), position);
                        } else if (transactionHistoryList.get(position).getTxnType() == 3) {
                            declineTranscApi(transactionHistoryList.get(position).getId(), position);
                        }
                    }

                    @Override
                    public void onCancelButtonClicked() {

                    }
                });
            }
        });

        holder.payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (transactionHistoryList.get(position).getRequesterPhoneNo() != null
                        &&(!transactionHistoryList.get(position).getRequesterPhoneNo().equalsIgnoreCase(""))) {
                    //declineTranscApi(transactionHistoryList.get(position).getId(), position);
                    SelectUser newData = new SelectUser();
                    newData.setName("");
                    newData.setPhone(transactionHistoryList.get(position).getRequesterPhoneNo());
                    newData.setAmount(String.valueOf(transactionHistoryList.get(position).getAmount()));
                    newData.setOrderId(String.valueOf(transactionHistoryList.get(position).getId()));

                    Intent intent = new Intent(activity, SendMoneyActivity.class);
                    intent.putExtra(AppConstant.REQUEST_STATUS, false);
                    Bundle bun =new Bundle();
                    bun.putParcelable(AppConstant.CONTACT_DATA, newData);
                    intent.putExtras(bun);
                    activity.startActivity(intent);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return this.transactionHistoryList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView requestedTypeTxt, nameTxt, amountTxt, dateTxt, statusTxt;
        public ImageView requestImage, statusImage;
        public Button declinBtn, payBtn;

        public MyViewHolder(View view) {
            super(view);
            requestedTypeTxt = (TextView) view.findViewById(R.id.request_type_txt);
            nameTxt = (TextView) view.findViewById(R.id.contact_name_txt);
            amountTxt = (TextView) view.findViewById(R.id.amount_txt);
            dateTxt = (TextView) view.findViewById(R.id.date_txt);
            statusTxt = (TextView) view.findViewById(R.id.status_txt);

            requestImage = (ImageView) view.findViewById(R.id.request_type_img);
            statusImage = (ImageView) view.findViewById(R.id.status_img);

            statusTxt.setVisibility(View.GONE);
            statusImage.setVisibility(View.GONE);
            declinBtn = (Button) view.findViewById(R.id.decline_btn);
            payBtn = (Button) view.findViewById(R.id.pay_btn);
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

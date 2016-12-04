package com.jugnoo.pay.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jugnoo.pay.R;
import com.jugnoo.pay.models.TransacHistoryResponse;
import com.jugnoo.pay.utils.CommonMethods;

import java.util.List;


/**
 * Created by cl-macmini-38 on 06/06/16.
 */
public class TranscRecyslerAdapter extends RecyclerView.Adapter<TranscRecyslerAdapter.MyViewHolder> {
    List<TransacHistoryResponse.TransactionHistory> transactionHistoryList;
    private Activity activity;
    private final String REQUEST_TO = "Requested to";
    private final String REQUESTED_BY = "Requested by";
    private final String REQUESTED_FROM = "Requested from";
    private final String PAYMENT_FROM = "Payment from"; //4
    private final String PAYMENT_TO = "Payment to";
    private final String COMPLETED = "Completed";
    private final String RECEIVED = "Received";
    private final String FAILED = "Failed";
    private final String DECLINED = "Declined";
    private final String CANCELLED = "Cancelled";

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transac_history_item, parent, false);
        return new MyViewHolder(itemView);
    }

    public TranscRecyslerAdapter(Activity activity, List<TransacHistoryResponse.TransactionHistory> transactionHistories) {
        this.transactionHistoryList = transactionHistories;
        this.activity = activity;

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.nameTxt.setText(transactionHistoryList.get(position).getName());
        if (transactionHistoryList.get(position).getTxnType() == 1) {
            holder.requestedTypeTxt.setText(PAYMENT_TO);
            holder.requestImage.setBackgroundResource(R.drawable.payment);
        } else if (transactionHistoryList.get(position).getTxnType() == 2) {
            holder.requestedTypeTxt.setText(REQUESTED_FROM);
            holder.requestImage.setBackgroundResource(R.drawable.payment);
        } else if (transactionHistoryList.get(position).getTxnType() == 3) {
            holder.requestedTypeTxt.setText(REQUESTED_BY);
            holder.requestImage.setBackgroundResource(R.drawable.payment);
        } else if (transactionHistoryList.get(position).getTxnType() == 4) {
            holder.requestedTypeTxt.setText(PAYMENT_FROM);
            holder.requestImage.setBackgroundResource(R.drawable.requested);
        }


        int statusInt = transactionHistoryList.get(position).getStatus();
        if (statusInt == 1) {
            if(transactionHistoryList.get(position).getTxnType() == 4){
                holder.statusTxt.setText(RECEIVED);
            }else {
                holder.statusTxt.setText(COMPLETED);
            }
            holder.statusTxt.setTextColor(activity.getResources().getColor(R.color.booking_green_color));
            holder.statusImage.setBackgroundResource(R.drawable.ic_history_delivered_completed);
        } else if (statusInt == 3) {
            holder.statusTxt.setText(CANCELLED);
            holder.statusTxt.setTextColor(activity.getResources().getColor(R.color.past_booking_color));
            holder.statusImage.setBackgroundResource(R.drawable.ic_history_cancelled);
        } else if (statusInt == 4) {
            holder.statusTxt.setText(DECLINED);
            holder.statusTxt.setTextColor(activity.getResources().getColor(R.color.past_booking_color));
            holder.statusImage.setBackgroundResource(R.drawable.ic_history_cancelled);
        } else if (statusInt == 2) {
            holder.statusTxt.setText(FAILED);
            holder.statusTxt.setTextColor(activity.getResources().getColor(R.color.past_booking_color));
            holder.statusImage.setBackgroundResource(R.drawable.ic_history_cancelled);
        }

        holder.dateTxt.setText(CommonMethods.getDateFromUTC(transactionHistoryList.get(position).getDate()));
        holder.amountTxt.setText("Rs. " + transactionHistoryList.get(position).getAmount());
    }


    @Override
    public int getItemCount() {

        return this.transactionHistoryList.size();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView requestedTypeTxt, nameTxt, amountTxt, dateTxt, statusTxt;
        public ImageView requestImage, statusImage;

        public MyViewHolder(View view) {
            super(view);
            requestedTypeTxt = (TextView) view.findViewById(R.id.request_type_txt);
            nameTxt = (TextView) view.findViewById(R.id.contact_name_txt);
            amountTxt = (TextView) view.findViewById(R.id.amount_txt);
            dateTxt = (TextView) view.findViewById(R.id.date_txt);
            statusTxt = (TextView) view.findViewById(R.id.status_txt);

            requestImage = (ImageView) view.findViewById(R.id.request_type_img);
            statusImage = (ImageView) view.findViewById(R.id.status_img);


        }


    }


}

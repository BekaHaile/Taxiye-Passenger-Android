package product.clicklabs.jugnoo.wallet.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jugnoo.pay.activities.TranscCompletedActivity;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Utils;
import product.clicklabs.jugnoo.wallet.models.TransactionInfo;
import product.clicklabs.jugnoo.wallet.models.TransactionType;


public class WalletTransactionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_FOOTER = 2;
    private static final int TYPE_ITEM = 1;
    private Context context;
    private ArrayList<TransactionInfo> transactionInfoList;
    private int totalTransactions;
    private Callback callback;

    public WalletTransactionsAdapter(Context context, ArrayList<TransactionInfo> transactionInfoList,
                                     int totalTransactions, Callback callback) {
        this.context = context;
        this.transactionInfoList = transactionInfoList;
        this.totalTransactions = totalTransactions;
        this.callback = callback;
    }

    public void notifyList(int totalTransactions){
        this.totalTransactions = totalTransactions;
        this.notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_show_more, parent, false);

            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(720, RecyclerView.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(layoutParams);

            ASSL.DoMagic(v);
            return new ViewFooterHolder(v, context);
        } else{
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_wallet_transactions, parent, false);

            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(720, 156);
            v.setLayoutParams(layoutParams);

            ASSL.DoMagic(v);
            return new ViewHolder(v, context);
        }
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewholder, int position) {
        if(viewholder instanceof ViewHolder) {
            ViewHolder holder = (ViewHolder) viewholder;
            TransactionInfo transactionInfo = transactionInfoList.get(position);

            if(transactionInfo.getPay() == 0){
                holder.textViewTransactionDate.setText(transactionInfo.date);
                holder.textViewTransactionAmount.setText(String.format(context.getResources()
                                .getString(R.string.rupees_value_format_without_space),
                        Utils.getMoneyDecimalFormat().format(transactionInfo.amount)));
                holder.textViewTransactionTime.setText(transactionInfo.time);
                holder.textViewTransactionType.setText(transactionInfo.transactionText);

                if(TransactionType.CREDIT.getOrdinal() == transactionInfo.transactionType){
                    holder.textViewTransactionType.setTextColor(context.getResources().getColor(R.color.green_transaction_type));
                } else{
                    holder.textViewTransactionType.setTextColor(context.getResources().getColor(R.color.grey_dark));
                }

                holder.textViewTransactionMode.setVisibility(View.GONE);
                holder.tvStatusPay.setVisibility(View.GONE);
                if(transactionInfo.paytm == 1){
                    holder.textViewTransactionMode.setVisibility(View.VISIBLE);
                    holder.textViewTransactionMode.setText(context.getResources().getString(R.string.paytm_colon));
                } else if(transactionInfo.getMobikwik() == 1){
                    holder.textViewTransactionMode.setVisibility(View.VISIBLE);
                    holder.textViewTransactionMode.setText(context.getResources().getString(R.string.mobikwik_colon));
                } else if(transactionInfo.getFreecharge() == 1){
                    holder.textViewTransactionMode.setVisibility(View.VISIBLE);
                    holder.textViewTransactionMode.setText(context.getResources().getString(R.string.freecharge_colon));
                }
            }
            else if(transactionInfo.getPay() == 1){
                holder.textViewTransactionDate.setText(transactionInfo.getName());
                holder.textViewTransactionAmount.setText(context.getResources().getString(R.string.rupees_value_format_without_space,
                        Utils.getMoneyDecimalFormat().format(transactionInfo.amount)));
                holder.textViewTransactionTime.setText(transactionInfo.time);

                int statusInt = transactionInfo.getStatus();
                if (statusInt == 1) {
                    if(transactionInfo.transactionType == 4){
                        holder.textViewTransactionType.setText(R.string.received);
                    }else {
                        holder.textViewTransactionType.setText(R.string.completed);
                    }
                    holder.textViewTransactionType.setTextColor(context.getResources().getColor(R.color.green_rupee));
                } else if (statusInt == 3) {
                    holder.textViewTransactionType.setText(R.string.cancelled);
                    holder.textViewTransactionType.setTextColor(context.getResources().getColor(R.color.red_status));
                } else if (statusInt == 4) {
                    holder.textViewTransactionType.setText(R.string.declined);
                    holder.textViewTransactionType.setTextColor(context.getResources().getColor(R.color.red_status));
                } else if (statusInt == 2) {
                    holder.textViewTransactionType.setText(R.string.failed);
                    holder.textViewTransactionType.setTextColor(context.getResources().getColor(R.color.red_status));
                }


                holder.textViewTransactionMode.setVisibility(View.VISIBLE);
                holder.textViewTransactionMode.setText(context.getResources().getString(R.string.pay_colon));
                holder.tvStatusPay.setVisibility(View.VISIBLE);
                if (transactionInfo.transactionType == 1) {
                    holder.tvStatusPay.setText(R.string.paid_to);
                } else if (transactionInfo.transactionType == 2) {
                    holder.tvStatusPay.setText(R.string.requested_from);
                } else if (transactionInfo.transactionType == 3) {
                    holder.tvStatusPay.setText(R.string.requested_by);
                } else if (transactionInfo.transactionType == 4) {
                    holder.tvStatusPay.setText(R.string.paid_by);
                }
            }

            holder.relative.setTag(position);
            holder.relative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = (int) v.getTag();
                    TransactionInfo transactionInfo1 = transactionInfoList.get(pos);
                    if(transactionInfo1.getPay() == 1) {
                        Intent intent = new Intent(context, TranscCompletedActivity.class);
                        intent.putExtra(Constants.KEY_FETCH_TRANSACTION_SUMMARY, 1);
                        intent.putExtra(Constants.KEY_ORDER_ID, transactionInfoList.get(pos).transactionId);
                        intent.putExtra(Constants.KEY_TXN_TYPE, transactionInfoList.get(pos).transactionType);
                        context.startActivity(intent);
                        ((Activity)context).finish();
                    }
                }
            });
        }
        else if(viewholder instanceof ViewFooterHolder){
            ViewFooterHolder holder = (ViewFooterHolder) viewholder;
            holder.relativeLayoutShowMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onShowMoreClick();
                }
            });
        }

	}

    @Override
    public int getItemCount() {
        if(transactionInfoList == null || transactionInfoList.size() == 0){
            return 0;
        }
        else{
            if(totalTransactions > transactionInfoList.size()){
                return transactionInfoList.size() + 1;
            } else{
                return transactionInfoList.size();
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionFooter(position)) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    private boolean isPositionFooter(int position) {
        return position == transactionInfoList.size();
    }

    private TransactionInfo getItem(int position){
        if(isPositionFooter(position)){
            return null;
        }
        return transactionInfoList.get(position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewTransactionDate, textViewTransactionAmount, textViewTransactionTime,
                textViewTransactionType, textViewTransactionMode, tvStatusPay;
        public LinearLayout relative;
        public ViewHolder(View convertView, Context context) {
            super(convertView);
            textViewTransactionDate = (TextView) convertView.findViewById(R.id.textViewTransactionDate); textViewTransactionDate.setTypeface(Fonts.mavenRegular(context));
            textViewTransactionAmount = (TextView) convertView.findViewById(R.id.textViewTransactionAmount); textViewTransactionAmount.setTypeface(Fonts.mavenRegular(context));
            textViewTransactionTime = (TextView) convertView.findViewById(R.id.textViewTransactionTime); textViewTransactionTime.setTypeface(Fonts.mavenRegular(context));
            textViewTransactionType = (TextView) convertView.findViewById(R.id.textViewTransactionType); textViewTransactionType.setTypeface(Fonts.mavenRegular(context));
            textViewTransactionMode = (TextView) convertView.findViewById(R.id.textViewTransactionMode); textViewTransactionMode.setTypeface(Fonts.mavenRegular(context));
            tvStatusPay = (TextView) convertView.findViewById(R.id.tvStatusPay); tvStatusPay.setTypeface(Fonts.mavenMedium(context));
            relative = (LinearLayout) convertView.findViewById(R.id.relative);
        }
    }


    public class ViewFooterHolder extends RecyclerView.ViewHolder {
        public RelativeLayout relativeLayoutShowMore;
        public TextView textViewShowMore;
        public ViewFooterHolder(View convertView, Context context) {
            super(convertView);
            relativeLayoutShowMore = (RelativeLayout) convertView.findViewById(R.id.relativeLayoutShowMore);
            textViewShowMore = (TextView) convertView.findViewById(R.id.textViewShowMore); textViewShowMore.setTypeface(Fonts.mavenLight(context));
            textViewShowMore.setText(context.getResources().getString(R.string.show_more));
        }
    }

    public interface Callback{
        void onShowMoreClick();
    }

}

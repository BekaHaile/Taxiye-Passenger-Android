package com.sabkuchfresh.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.utils.Utils;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.retrofit.model.HistoryResponse;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;


public class FreshOrderItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    private ArrayList<HistoryResponse.OrderItem> orderItems;

    public static final int HEADER = 0;
    public static final int LIST = 1;
    public static final int FOOTER = 2;


    public FreshOrderItemAdapter(Activity activity, ArrayList<HistoryResponse.OrderItem> orderItems) {
        this.orderItems = orderItems;
        this.activity = activity;
    }

//    @Override
//    public FreshOrderItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_fresh_order_item, parent, false);
//
//        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(560, RecyclerView.LayoutParams.WRAP_CONTENT);
//        v.setLayoutParams(layoutParams);
//
//        ASSL.DoMagic(v);
//        return new ViewHolder(v, activity);
//    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == HEADER){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_order_details_header, parent, false);
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(layoutParams);
            ASSL.DoMagic(v);
            return new ViewHolderHeader(v, activity);

        } else if(viewType == FOOTER){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_order_details_footer, parent, false);
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(layoutParams);
            ASSL.DoMagic(v);
            return new ViewHolderFooter(v, activity);

        } else{
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_fresh_order_item, parent, false);

            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(560, RecyclerView.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(layoutParams);

            ASSL.DoMagic(v);
            return new ViewListHolder(v, activity);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try{
            if(holder instanceof ViewHolderHeader){

            } else if(holder instanceof ViewHolderFooter){

            } else if(holder instanceof ViewListHolder){

            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

//    @Override
//    public void onBindViewHolder(FreshOrderItemAdapter.ViewHolder holder, int position) {
//        try {
//            HistoryResponse.OrderItem orderItem = orderItems.get(position);
//
//            if(orderItem.getUnit() == null || "".equalsIgnoreCase(orderItem.getUnit())){
//                holder.textViewOrderItemName.setText(String.format(activity.getResources().getString(R.string.x_format),
//                        orderItem.getItemName(), String.valueOf(orderItem.getItemQuantity())));
//            } else{
//                holder.textViewOrderItemName.setText(String.format(activity.getResources().getString(R.string.item_name_unit_price_format),
//                        orderItem.getItemName(), orderItem.getUnit(), String.valueOf(orderItem.getItemQuantity())));
//            }
//
//            holder.textViewOrderItemPrice.setText(String.format(activity.getResources().getString(R.string.rupees_value_format),
//                    Utils.getMoneyDecimalFormat().format(orderItem.getItemAmount())));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }

    @Override
    public int getItemCount() {
        if(orderItems == null || orderItems.size() == 0){
            return 0;
        } else{
            return orderItems.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0) {

        } else if(position == orderItems.size()) {

        } else {

        }
    }

    public class ViewListHolder extends RecyclerView.ViewHolder {
        public TextView textViewOrderItemName, textViewOrderItemPrice;
        public RelativeLayout relative;
        public ViewListHolder(View convertView, Activity context) {
            super(convertView);
            textViewOrderItemName = (TextView) convertView.findViewById(R.id.textViewOrderItemName); textViewOrderItemName.setTypeface(Fonts.mavenRegular(context));
            textViewOrderItemPrice = (TextView) convertView.findViewById(R.id.textViewOrderItemPrice); textViewOrderItemPrice.setTypeface(Fonts.mavenRegular(context));
            relative = (RelativeLayout) convertView.findViewById(R.id.relative);
        }
    }

    static class ViewHolderHeader extends RecyclerView.ViewHolder {
        public TextView textViewSlotDay;
        public ViewHolderHeader(View itemView, Context context) {
            super(itemView);
            textViewSlotDay = (TextView)itemView.findViewById(R.id.textViewSlotDay); textViewSlotDay.setTypeface(Fonts.mavenRegular(context), Typeface.BOLD);
        }
    }

    static class ViewHolderFooter extends RecyclerView.ViewHolder {
        public ViewHolderFooter(View itemView, Context context) {
            super(itemView);
        }
    }


}

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
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.retrofit.model.HistoryResponse;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.Fonts;


public class FreshOrderItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    private ArrayList<HistoryResponse.OrderItem> orderItems;
    private HistoryResponse.Datum orderHistory;

    public static final int HEADER = 0;
    public static final int LIST = 1;
    public static final int FOOTER = 2;


    public FreshOrderItemAdapter(Activity activity, HistoryResponse.Datum orderHistory) {
        this.orderItems = (ArrayList<HistoryResponse.OrderItem>) orderHistory.getOrderItems();
        this.activity = activity;
        this.orderHistory = orderHistory;
    }

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
                ((ViewHolderHeader)holder).textViewOrderIdValue.setText(String.valueOf(orderHistory.getOrderId()));
                ((ViewHolderHeader)holder).textViewOrderTimeValue.setText(DateOperations.getDate(DateOperations.utcToLocalTZ(orderHistory.getOrderTime())));
                ((ViewHolderHeader)holder).textViewOrderAddressValue.setText(orderHistory.getDeliveryAddress());
                if (orderHistory.getExpectedDeliveryDate() != null) {
                    ((ViewHolderHeader)holder).textViewOrderDeliveryDateValue.setText(orderHistory.getExpectedDeliveryDate());
                } else {
                    ((ViewHolderHeader)holder).textViewOrderDeliveryDateValue.setText("");
                }
                if (orderHistory.getStartTime() != null && orderHistory.getEndTime() != null) {
                    ((ViewHolderHeader)holder).textViewOrderDeliverySlotValue.setText(DateOperations.convertDayTimeAPViaFormat(orderHistory.getStartTime()) + " - " + DateOperations.convertDayTimeAPViaFormat(orderHistory.getEndTime()));
                } else {
                    ((ViewHolderHeader)holder).textViewOrderDeliverySlotValue.setText("");
                }
            } else if(holder instanceof ViewHolderFooter){
                try {
                    ((ViewHolderFooter)holder).textViewTotalAmountValue.setText(String.format(activity.getResources().getString(R.string.rupees_value_format),
                            Utils.getMoneyDecimalFormat().format(orderHistory.getOrderAmount() - orderHistory.getDeliveryCharges() + orderHistory.getJugnooDeducted()
                                    + orderHistory.getDiscount())));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ((ViewHolderFooter)holder).textViewDeliveryChargesValue.setText(String.format(activity.getResources().getString(R.string.rupees_value_format), Utils.getMoneyDecimalFormat().format(orderHistory.getDeliveryCharges())));
                ((ViewHolderFooter)holder).textViewAmountPayableValue.setText(String.format(activity.getResources().getString(R.string.rupees_value_format), Utils.getMoneyDecimalFormat().format(orderHistory.getOrderAmount())));
                if (orderHistory.getPaymentMode().equals(PaymentOption.PAYTM.getOrdinal())) {
                    ((ViewHolderFooter)holder).textViewPaymentMode.setText(activity.getResources().getString(R.string.paytm));
                    ((ViewHolderFooter)holder).textViewpaytm.setText(activity.getString(R.string.paytm_wallet));
                }
                else if (orderHistory.getPaymentMode().equals(PaymentOption.MOBIKWIK.getOrdinal())) {
                    ((ViewHolderFooter)holder).textViewPaymentMode.setText(activity.getResources().getString(R.string.mobikwik));
                    ((ViewHolderFooter)holder).textViewpaytm.setText(activity.getString(R.string.mobikwik_wallet));
                }
                else {
                    ((ViewHolderFooter)holder).textViewPaymentMode.setText(activity.getResources().getString(R.string.cash));
                }
                ((ViewHolderFooter)holder).textViewPaymentModeValue.setText(String.format(activity.getResources().getString(R.string.rupees_value_format), Utils.getMoneyDecimalFormat().format(orderHistory.getOrderAmount())));

                if (orderHistory.getDiscount() > 0) {
                    ((ViewHolderFooter)holder).textViewDiscountValue.setText(String.format(activity.getResources().getString(R.string.rupees_value_format), Utils.getMoneyDecimalFormat().format(orderHistory.getDiscount())));
                } else {
                    ((ViewHolderFooter)holder).discountLayout.setVisibility(View.GONE);
                }
                if (orderHistory.getJugnooDeducted() > 0) {
                    ((ViewHolderFooter)holder).textViewjcValue.setText(String.format(activity.getResources().getString(R.string.rupees_value_format), Utils.getMoneyDecimalFormat().format(orderHistory.getJugnooDeducted())));
                } else {
                    ((ViewHolderFooter)holder).jclayout.setVisibility(View.GONE);
                }
                if (orderHistory.getWalletDeducted() != null && orderHistory.getWalletDeducted() > 0) {
                    ((ViewHolderFooter)holder).textViewpaytmValue.setText(activity.getString(R.string.rupees_value_format, Utils.getMoneyDecimalFormat().format(orderHistory.getWalletDeducted())));
                } else {
                    ((ViewHolderFooter)holder).paytmlayout.setVisibility(View.GONE);
                }
            } else if(holder instanceof ViewListHolder){
                try {
                    HistoryResponse.OrderItem orderItem = orderItems.get(position-1);

                    if(orderItem.getUnit() == null || "".equalsIgnoreCase(orderItem.getUnit())){
                        ((ViewListHolder)holder).textViewOrderItemName.setText(String.format(activity.getResources().getString(R.string.x_format),
                                orderItem.getItemName(), String.valueOf(orderItem.getItemQuantity())));
                    } else{
                        ((ViewListHolder)holder).textViewOrderItemName.setText(String.format(activity.getResources().getString(R.string.item_name_unit_price_format),
                                orderItem.getItemName(), orderItem.getUnit(), String.valueOf(orderItem.getItemQuantity())));
                    }

                    ((ViewListHolder)holder).textViewOrderItemPrice.setText(String.format(activity.getResources().getString(R.string.rupees_value_format),
                            Utils.getMoneyDecimalFormat().format(orderItem.getItemAmount())));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if(orderItems == null || orderItems.size() == 0){
            return 0;
        } else{
            return orderItems.size() + 2;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0) {
            return HEADER;
        } else if(position == orderItems.size() + 1) {
            return FOOTER;
        } else {
            return LIST;
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
        public TextView textViewOrderIdValue, textViewOrderTimeValue,  textViewOrderDeliveryDateValue,
                textViewOrderDeliverySlotValue, textViewOrderAddressValue;
        public ViewHolderHeader(View itemView, Context context) {
            super(itemView);

            ((TextView) itemView.findViewById(R.id.textViewOrderId)).setTypeface(Fonts.mavenRegular(context));
            ((TextView) itemView.findViewById(R.id.textViewOrderDeliveryDate)).setTypeface(Fonts.mavenRegular(context));
            ((TextView) itemView.findViewById(R.id.textViewOrderDeliverySlot)).setTypeface(Fonts.mavenRegular(context));
            ((TextView) itemView.findViewById(R.id.textViewOrderReceipt)).setTypeface(Fonts.mavenRegular(context));

            textViewOrderIdValue = (TextView) itemView.findViewById(R.id.textViewOrderIdValue);
            textViewOrderIdValue.setTypeface(Fonts.mavenRegular(context), Typeface.BOLD);

            textViewOrderDeliveryDateValue = (TextView) itemView.findViewById(R.id.textViewOrderDeliveryDateValue);
            textViewOrderDeliveryDateValue.setTypeface(Fonts.mavenRegular(context));

            textViewOrderDeliverySlotValue = (TextView) itemView.findViewById(R.id.textViewOrderDeliverySlotValue);
            textViewOrderDeliverySlotValue.setTypeface(Fonts.mavenRegular(context));

            textViewOrderTimeValue = (TextView) itemView.findViewById(R.id.textViewOrderTimeValue);
            textViewOrderTimeValue.setTypeface(Fonts.mavenRegular(context));

            textViewOrderAddressValue = (TextView) itemView.findViewById(R.id.textViewOrderAddressValue);
            textViewOrderAddressValue.setTypeface(Fonts.mavenRegular(context));

        }
    }

    static class ViewHolderFooter extends RecyclerView.ViewHolder {
        public TextView textViewTotalAmountValue, textViewDeliveryChargesValue, textViewAmountPayableValue,
                textViewPaymentMode, textViewPaymentModeValue, textViewDiscountValue,
                textViewjcValue, textViewpaytm, textViewpaytmValue;

        public RelativeLayout discountLayout, jclayout, paytmlayout;

        public ViewHolderFooter(View rootView, Context activity) {
            super(rootView);

            discountLayout = (RelativeLayout) rootView.findViewById(R.id.discountLayout);
            jclayout = (RelativeLayout) rootView.findViewById(R.id.jclayout);
            paytmlayout = (RelativeLayout) rootView.findViewById(R.id.paytmlayout);

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

            textViewDiscountValue = (TextView) rootView.findViewById(R.id.textViewDiscountValue);
            textViewDiscountValue.setTypeface(Fonts.mavenRegular(activity));

            textViewjcValue = (TextView) rootView.findViewById(R.id.textViewjcValue);
            textViewjcValue.setTypeface(Fonts.mavenRegular(activity));

            textViewpaytm = (TextView) rootView.findViewById(R.id.textViewpaytm);
            textViewpaytm.setTypeface(Fonts.mavenRegular(activity));

            textViewpaytmValue = (TextView) rootView.findViewById(R.id.textViewpaytmValue);
            textViewpaytmValue.setTypeface(Fonts.mavenRegular(activity));

            ((TextView) rootView.findViewById(R.id.textViewTotalAmount)).setTypeface(Fonts.mavenRegular(activity));
            ((TextView) rootView.findViewById(R.id.textViewDeliveryCharges)).setTypeface(Fonts.mavenRegular(activity));
            ((TextView) rootView.findViewById(R.id.textViewAmountPayable)).setTypeface(Fonts.mavenRegular(activity));
            ((TextView) rootView.findViewById(R.id.textViewPaymentBy)).setTypeface(Fonts.mavenRegular(activity));
            ((TextView) rootView.findViewById(R.id.textViewDiscount)).setTypeface(Fonts.mavenRegular(activity));
            ((TextView) rootView.findViewById(R.id.textViewjc)).setTypeface(Fonts.mavenRegular(activity));


        }
    }


}

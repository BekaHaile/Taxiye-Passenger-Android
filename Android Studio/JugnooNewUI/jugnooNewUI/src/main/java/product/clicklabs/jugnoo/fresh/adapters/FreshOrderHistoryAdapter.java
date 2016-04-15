package product.clicklabs.jugnoo.fresh.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.fresh.models.OrderHistory;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Utils;


public class FreshOrderHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_FOOTER = 2;
    private static final int TYPE_ITEM = 1;
    private Activity activity;
    private ArrayList<OrderHistory> orderHistories;
    private int totalOrders;
    private Callback callback;

    public FreshOrderHistoryAdapter(Activity activity, ArrayList<OrderHistory> orderHistories, Callback callback,
                                    int totalOrders) {
        this.orderHistories = orderHistories;
        this.activity = activity;
        this.callback = callback;
        this.totalOrders = totalOrders;
    }

    public void notifyList(int totalOrders, ArrayList<OrderHistory> orderHistories){
        this.totalOrders = totalOrders;
        this.orderHistories = orderHistories;
        this.notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_show_more, parent, false);

            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(720, RecyclerView.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(layoutParams);

            ASSL.DoMagic(v);
            return new ViewFooterHolder(v, activity);
        } else{
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_fresh_order, parent, false);

            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(720, RecyclerView.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(layoutParams);

            ASSL.DoMagic(v);
            return new ViewHolder(v, activity);
        }
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewholder, int position) {
        try {
            if(viewholder instanceof ViewHolder) {
				OrderHistory orderHistory = getItem(position);
				ViewHolder holder = (ViewHolder) viewholder;
				holder.relative.setTag(position);

				holder.textViewIdValue.setText(String.valueOf(orderHistory.getOrderId()));
				holder.textViewDeliveryAddressValue.setText(orderHistory.getDeliveryAddress());
				holder.textViewOrderDateValue.setText(DateOperations.convertDateViaFormat(DateOperations
                        .utcToLocalTZ(orderHistory.getOrderTime())));
                holder.textViewOrderStatusValue.setText(orderHistory.getOrderStatus());
                try{
                    holder.textViewOrderStatusValue.setTextColor(Color.parseColor(orderHistory.getOrderStatusColor()));
                } catch(Exception e){}

                holder.textViewAmount.setText(String.format(activity.getResources()
                                .getString(R.string.rupees_value_format_without_space),
                        Utils.getMoneyDecimalFormat().format(orderHistory.getOrderAmount())));


				holder.relative.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						try {
							int position = (int) v.getTag();
							callback.onClick(position, getItem(position));
						} catch (Exception e) {
							e.printStackTrace();
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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        if(orderHistories == null || orderHistories.size() == 0){
            return 0;
        }
        else{
            if(totalOrders > orderHistories.size()){
                return orderHistories.size() + 1;
            } else{
                return orderHistories.size();
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
        return position == orderHistories.size();
    }

    private OrderHistory getItem(int position){
        if(isPositionFooter(position)){
            return null;
        }
        return orderHistories.get(position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewIdValue, textViewDeliveryAddressValue, textViewOrderDateValue, textViewOrderStatusValue, textViewAmount;
        public RelativeLayout relative;
        public ViewHolder(View convertView, Activity context) {
            super(convertView);
            textViewIdValue = (TextView) convertView.findViewById(R.id.textViewIdValue); textViewIdValue.setTypeface(Fonts.mavenLight(context), Typeface.BOLD);
            textViewDeliveryAddressValue = (TextView) convertView.findViewById(R.id.textViewDeliveryAddressValue); textViewDeliveryAddressValue.setTypeface(Fonts.mavenLight(context), Typeface.BOLD);
            textViewOrderDateValue = (TextView) convertView.findViewById(R.id.textViewOrderDateValue); textViewOrderDateValue.setTypeface(Fonts.mavenLight(context), Typeface.BOLD);
            textViewAmount = (TextView) convertView.findViewById(R.id.textViewAmount); textViewAmount.setTypeface(Fonts.mavenRegular(context), Typeface.BOLD);
            textViewOrderStatusValue = (TextView) convertView.findViewById(R.id.textViewOrderStatusValue); textViewOrderStatusValue.setTypeface(Fonts.mavenLight(context), Typeface.BOLD);

            ((TextView)convertView.findViewById(R.id.textViewId)).setTypeface(Fonts.mavenLight(context), Typeface.BOLD);
            ((TextView)convertView.findViewById(R.id.textViewDeliveryAddress)).setTypeface(Fonts.mavenLight(context), Typeface.BOLD);
            ((TextView)convertView.findViewById(R.id.textViewOrderDate)).setTypeface(Fonts.mavenLight(context), Typeface.BOLD);
            ((TextView)convertView.findViewById(R.id.textViewOrderStatus)).setTypeface(Fonts.mavenLight(context), Typeface.BOLD);

            relative = (RelativeLayout) convertView.findViewById(R.id.relative);
        }
    }


    public class ViewFooterHolder extends RecyclerView.ViewHolder {
        public RelativeLayout relativeLayoutShowMore;
        public TextView textViewShowMore;
        public ViewFooterHolder(View convertView, Activity context) {
            super(convertView);
            relativeLayoutShowMore = (RelativeLayout) convertView.findViewById(R.id.relativeLayoutShowMore);
            textViewShowMore = (TextView) convertView.findViewById(R.id.textViewShowMore); textViewShowMore.setTypeface(Fonts.mavenLight(context));
            textViewShowMore.setText(context.getResources().getString(R.string.show_more));
        }
    }

    public interface Callback{
        void onClick(int position, OrderHistory orderHistory);
        void onShowMoreClick();
    }

}

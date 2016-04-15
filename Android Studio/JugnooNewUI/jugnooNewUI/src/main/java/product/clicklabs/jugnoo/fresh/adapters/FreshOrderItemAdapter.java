package product.clicklabs.jugnoo.fresh.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.fresh.models.OrderItem;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Utils;


public class FreshOrderItemAdapter extends RecyclerView.Adapter<FreshOrderItemAdapter.ViewHolder> {

    private Activity activity;
    private ArrayList<OrderItem> orderItems;

    public FreshOrderItemAdapter(Activity activity, ArrayList<OrderItem> orderItems) {
        this.orderItems = orderItems;
        this.activity = activity;
    }

    @Override
    public FreshOrderItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_fresh_order_item, parent, false);

        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(560, RecyclerView.LayoutParams.WRAP_CONTENT);
        v.setLayoutParams(layoutParams);

        ASSL.DoMagic(v);
        return new ViewHolder(v, activity);
    }


    @Override
    public void onBindViewHolder(FreshOrderItemAdapter.ViewHolder holder, int position) {
        try {
            OrderItem orderItem = orderItems.get(position);

            if(orderItem.getUnit() == null || "".equalsIgnoreCase(orderItem.getUnit())){
                holder.textViewOrderItemName.setText(String.format(activity.getResources().getString(R.string.x_format),
                        orderItem.getItemName(), String.valueOf(orderItem.getItemQuantity())));
            } else{
                holder.textViewOrderItemName.setText(String.format(activity.getResources().getString(R.string.item_name_unit_price_format),
                        orderItem.getItemName(), orderItem.getUnit(), String.valueOf(orderItem.getItemQuantity())));
            }

            holder.textViewOrderItemPrice.setText(String.format(activity.getResources().getString(R.string.rupees_value_format),
                    Utils.getMoneyDecimalFormat().format(orderItem.getItemAmount())));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        if(orderItems == null || orderItems.size() == 0){
            return 0;
        } else{
            return orderItems.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewOrderItemName, textViewOrderItemPrice;
        public RelativeLayout relative;
        public ViewHolder(View convertView, Activity context) {
            super(convertView);
            textViewOrderItemName = (TextView) convertView.findViewById(R.id.textViewOrderItemName); textViewOrderItemName.setTypeface(Fonts.mavenRegular(context));
            textViewOrderItemPrice = (TextView) convertView.findViewById(R.id.textViewOrderItemPrice); textViewOrderItemPrice.setTypeface(Fonts.mavenRegular(context));
            relative = (RelativeLayout) convertView.findViewById(R.id.relative);
        }
    }

}

package com.sabkuchfresh.adapters;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.DeliveryStore;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;


/**
 * Created by Ankit on 7/17/15.
 */
public class DeliveryStoresAdapter extends RecyclerView.Adapter<DeliveryStoresAdapter.ViewHolderSlot> implements ItemListener {

    private FreshActivity activity;
    private List<DeliveryStore> stores;
    private Callback callback;
    private RecyclerView recyclerView;

    public DeliveryStoresAdapter(FreshActivity activity, List<DeliveryStore> stores, RecyclerView recyclerView, Callback callback) {
        this.activity = activity;
        this.stores = stores;
        this.recyclerView = recyclerView;
        this.callback = callback;
    }

    public void setList(ArrayList<DeliveryStore> stores){
        this.stores = stores;
        notifyDataSetChanged();
    }

    @Override
    public DeliveryStoresAdapter.ViewHolderSlot onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_delivery_stores, parent, false);
        return new ViewHolderSlot(v, this);
    }

    @Override
    public void onBindViewHolder(DeliveryStoresAdapter.ViewHolderSlot mHolder, int position) {
        try {
            DeliveryStore store = stores.get(position);
            /*double rating = 4.3d;
            activity.setRatingAndGetColor(mHolder.tvRating, rating, "#8dd061", true);*/
            Log.d("position", "position = "+(position));

            mHolder.tvStoreName.setText(store.getVendorName());

            int numberItems = activity.getCart().getCartItems(store.getVendorId()).size();
            if(numberItems == 1){
                mHolder.tvCartItem.setText(activity.getString(R.string.one_item_in_cart));
            } else {
                mHolder.tvCartItem.setText(activity.getString(R.string.number_items_in_cart,
                        String.valueOf(numberItems)));
            }
            double cartAmount = activity.getCart().getCartTotal(store.getVendorId());
//            if(cartAmount > 0){
//                mHolder.tvMinOrder.setText(activity.getString(R.string.away_from_free_delivery_value,
//                        FeedUtils.getMoneyDecimalFormat().format(cartAmount)));
//            } else {
                mHolder.tvMinOrder.setText(activity.getString(R.string.minimum_order_with_value,
                        Utils.getMoneyDecimalFormat().format(store.getMinAmount())));
//            }
            if(store.getMinAmount() > 0 && cartAmount < store.getMinAmount()) {
                mHolder.tvDeliveryCharges.setText(activity.getString(R.string.delivery_charges_with_value,
                        Utils.getMoneyDecimalFormat().format(store.getDeliveryCharges())));
            } else{
                mHolder.tvDeliveryCharges.setText(activity.getString(R.string.free_delivery));
            }
            int bottom = mHolder.llContainer.getPaddingBottom();
            int top = mHolder.llContainer.getPaddingTop();
            int right = mHolder.llContainer.getPaddingEnd();
            int left = mHolder.llContainer.getPaddingStart();
            if(store.getIsSelected() == 1){
                mHolder.ivRadio.setImageResource(R.drawable.radio_active);
                mHolder.ivRadio.setImageResource(R.drawable.radio_active);
                mHolder.llContainer.setBackgroundResource(R.drawable.bg_white_layer_shadow);
                mHolder.llContainer.setPaddingRelative(left, top, right, bottom);
            } else{
                mHolder.ivRadio.setImageResource(R.drawable.radio_deactive);
                mHolder.llContainer.setBackgroundResource(R.drawable.bg_white_transparent_layer_shadow);
                mHolder.llContainer.setPaddingRelative(left, top, right, bottom);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

	}

    @Override
    public int getItemCount() {
        return stores == null ? 0 : stores.size();
    }

    @Override
    public void onClickItem(View viewClicked, View parentView) {
        int position = recyclerView.getChildLayoutPosition(parentView);

        switch (viewClicked.getId()){
            case R.id.llContainer:
                callback.onStoreSelected(position, stores.get(position));
                notifyDataSetChanged();
            break;
        }
    }


    static class ViewHolderSlot extends RecyclerView.ViewHolder {
        public LinearLayout llContainer;
        public ImageView ivRadio;
        public TextView tvStoreName, tvDeliveryCharges, tvDeliveryTime, tvCartItem, tvMinOrder, tvRating;
        public ViewHolderSlot(final View itemView, final ItemListener itemListener) {
            super(itemView);
            llContainer = (LinearLayout) itemView.findViewById(R.id.llContainer);
            ivRadio = (ImageView) itemView.findViewById(R.id.ivRadio);
            tvStoreName = (TextView)itemView.findViewById(R.id.tvStoreName); tvStoreName.setTypeface(tvStoreName.getTypeface(), Typeface.BOLD);
            tvDeliveryCharges = (TextView)itemView.findViewById(R.id.tvDeliveryCharges);
            tvDeliveryTime = (TextView)itemView.findViewById(R.id.tvDeliveryTime);
            tvCartItem = (TextView)itemView.findViewById(R.id.tvCartItem);
            tvMinOrder = (TextView)itemView.findViewById(R.id.tvMinOrder);
            tvRating = (TextView) itemView.findViewById(R.id.tvRating);
            llContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemListener.onClickItem(llContainer, itemView);
                }
            });
        }
    }

    public interface Callback{
        void onStoreSelected(int position, DeliveryStore deliveryStore);
    }
}

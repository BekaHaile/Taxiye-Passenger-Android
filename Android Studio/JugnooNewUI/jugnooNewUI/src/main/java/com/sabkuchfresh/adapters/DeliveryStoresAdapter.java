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
            double rating = 4.3d;
            activity.setRatingAndGetColor(mHolder.tvRating, rating, "#8dd061", true);
            Log.d("position", "position = "+(position));

            mHolder.tvStoreName.setText(store.getVendorName());
            mHolder.tvDeliveryCharges.setText(String.format(activity.getResources().getString(R.string.delivery_charges_with_value), store.getDeliveryCharges()));
            mHolder.tvMinOrder.setText(String.format(activity.getResources().getString(R.string.minimum_order_with_value), store.getMinDeliveryCharges()));
            int bottom = mHolder.llContainer.getPaddingBottom();
            int top = mHolder.llContainer.getPaddingTop();
            int right = mHolder.llContainer.getPaddingRight();
            int left = mHolder.llContainer.getPaddingLeft();
            if(store.getIsSelected() == 1){
                mHolder.ivRadio.setImageResource(R.drawable.radio_active);
                mHolder.ivRadio.setImageResource(R.drawable.radio_active);
                mHolder.llContainer.setBackgroundResource(R.drawable.bg_white_layer_shadow);
                mHolder.llContainer.setPadding(left, top, right, bottom);
                activity.getFreshFragment().getTvStoreName().setText(store.getVendorName());
            } else{
                mHolder.ivRadio.setImageResource(R.drawable.radio_deactive);
                mHolder.llContainer.setBackgroundResource(R.drawable.bg_white_transparent_layer_shadow);
                mHolder.llContainer.setPadding(left, top, right, bottom);
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
                for(int i=0; i<stores.size(); i++){
                    stores.get(i).setIsSelected(0);
                }
                stores.get(position).setIsSelected(1);
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

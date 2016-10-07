package com.sabkuchfresh.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.DeliveryAddress;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;

/**
 * Created by Gurmail S. Kang on 5/13/16.
 */
public class FreshAddressAdapter extends BaseAdapter {

    private FreshActivity activity;
    private List<DeliveryAddress> slots;
    private Callback callback;


    public FreshAddressAdapter(FreshActivity activity, List<DeliveryAddress> slots, Callback callback) {
        this.activity = activity;
        this.slots = slots;
        this.callback = callback;
    }

    public void setList(ArrayList<DeliveryAddress> slots){
        this.slots = slots;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return slots.size();
    }

    @Override
    public Object getItem(int position) {
        return slots.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderSlot holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_fresh_address, parent, false);
            ListView.LayoutParams layoutParams = new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, ListView.LayoutParams.WRAP_CONTENT);
            convertView.setLayoutParams(layoutParams);
            ASSL.DoMagic(convertView);
            holder = new ViewHolderSlot(convertView, activity);

            holder.linear.setTag(holder);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolderSlot) convertView.getTag();
        }

        onBindViewHolder(holder, position);

        return convertView;
    }

    public void onBindViewHolder(ViewHolderSlot holder, int position) {
        try {
            holder.id = position;
            DeliveryAddress slot = slots.get(position);
            holder.textViewLast.setText(slot.getLastAddress());
            if (position == slots.size() - 1) {
                holder.imageViewDivider.setVisibility(View.GONE);
            } else {
                holder.imageViewDivider.setVisibility(View.VISIBLE);
            }
            holder.linear.setTag(holder);
            holder.linear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int pos = ((ViewHolderSlot) v.getTag()).id;
                        callback.onSlotSelected(pos, slots.get(pos));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            holder.imageViewEditOther.setTag(holder);
            holder.imageViewEditOther.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int pos = ((ViewHolderSlot) v.getTag()).id;
                        callback.onEditClick(pos, slots.get(pos));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class ViewHolderSlot {
        public int id;
        public RelativeLayout linear;
        private ImageView imageViewDivider, imageViewEditOther;
        public TextView textViewLast;
        public ViewHolderSlot(View itemView, Activity context) {
            linear = (RelativeLayout) itemView.findViewById(R.id.relativeLayoutLast);
            imageViewEditOther = (ImageView) itemView.findViewById(R.id.imageViewEditOther);
            imageViewDivider = (ImageView) itemView.findViewById(R.id.imageViewDivider);
            textViewLast = (TextView)itemView.findViewById(R.id.textViewLast);
            textViewLast.setTypeface(Fonts.mavenMedium(context));
        }
    }

    public interface Callback{
        void onSlotSelected(int position, DeliveryAddress slot);
        void onEditClick(int position, DeliveryAddress slot);
    }

}

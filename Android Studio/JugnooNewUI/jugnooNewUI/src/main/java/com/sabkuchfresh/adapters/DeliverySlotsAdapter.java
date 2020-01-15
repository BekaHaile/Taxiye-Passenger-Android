package com.sabkuchfresh.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.Slot;
import com.sabkuchfresh.utils.Utils;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;


/**
 * Created by Shankar on 7/17/15.
 */
public class DeliverySlotsAdapter extends RecyclerView.Adapter<DeliverySlotsAdapter.ViewHolderSlot> {

    private FreshActivity activity;
    private ArrayList<Slot> slots;
    private Callback callback;

    public DeliverySlotsAdapter(FreshActivity activity, ArrayList<Slot> slots, Callback callback) {
        this.activity = activity;
        this.slots = slots;
        this.callback = callback;
    }

    public void setList(ArrayList<Slot> slots){
        this.slots = slots;
        notifyDataSetChanged();
    }

    @Override
    public DeliverySlotsAdapter.ViewHolderSlot onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_fresh_delivery_slot_horizontal, parent, false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, 95);
        v.setLayoutParams(layoutParams);
        ASSL.DoMagic(v);
        return new ViewHolderSlot(v, activity);
    }

    @Override
    public void onBindViewHolder(DeliverySlotsAdapter.ViewHolderSlot holder, int position) {
        try {
            Slot slot = slots.get(position);
            Log.d("position", "position = "+(position));
            holder.textViewSlotDay.setText(slot.getDayName());
            holder.textViewSlotTime.setText(slot.getTimeSlotDisplay());
            if(activity.getSlotSelected() == null
                    || !activity.getSlotSelected().getDeliverySlotId().equals(slot.getDeliverySlotId())
                    || getItemCount() == 1){
                holder.imageViewSelected.setVisibility(View.GONE);
            } else{
                holder.imageViewSelected.setVisibility(View.VISIBLE);
            }
            if(slot.getIsActiveSlot() == 1){
                holder.relative.setAlpha(1.0f);
            } else{
                holder.relative.setAlpha(0.3f);
            }
            if(position == getItemCount()-1){
                holder.imageViewSep.setVisibility(View.GONE);
            } else{
                holder.imageViewSep.setVisibility(View.VISIBLE);
            }

            holder.relative.setEnabled(slot.getIsActiveSlot() == 1);
            holder.relative.setTag(position);
            holder.relative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int pos = (int) v.getTag();
                        if(slots.get(pos).getIsActiveSlot() == 1) {
                            callback.onSlotSelected(pos, slots.get(pos));
                            notifyDataSetChanged();
                        } else {
                            Utils.showToast(activity, activity.getString(R.string.slot_is_disabled));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.relative.getLayoutParams();
            params.width = getItemWidth();
            holder.relative.setLayoutParams(params);
        } catch (Exception e) {
            e.printStackTrace();
        }

	}

    @Override
    public int getItemCount() {
        return slots == null ? 0 : slots.size();
    }

    private int getItemWidth(){
        int width = (int)(((getItemCount() > 3 ? 620f : 680f) / (getItemCount() > 3 ? 3 : getItemCount())) * ASSL.Xscale());
        int minWidth = (int) (160f * ASSL.Xscale());
        return width >= minWidth ? width : minWidth;
    }

    static class ViewHolderSlot extends RecyclerView.ViewHolder {
        public RelativeLayout relative;
        public ImageView imageViewSelected, imageViewSep;
        public TextView textViewSlotDay, textViewSlotTime;
        public ViewHolderSlot(View itemView, Context context) {
            super(itemView);
            relative = (RelativeLayout) itemView.findViewById(R.id.relative);
            imageViewSelected = (ImageView) itemView.findViewById(R.id.imageViewSelected);
            imageViewSep = (ImageView) itemView.findViewById(R.id.imageViewSep);
            textViewSlotDay = (TextView)itemView.findViewById(R.id.textViewSlotDay); textViewSlotDay.setTypeface(Fonts.mavenMedium(context));
            textViewSlotTime = (TextView)itemView.findViewById(R.id.textViewSlotTime); textViewSlotTime.setTypeface(Fonts.mavenRegular(context));
        }
    }

    public interface Callback{
        void onSlotSelected(int position, Slot slot);
    }
}

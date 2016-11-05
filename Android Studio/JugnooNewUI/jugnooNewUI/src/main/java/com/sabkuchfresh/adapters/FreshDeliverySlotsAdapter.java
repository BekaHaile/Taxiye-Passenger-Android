package com.sabkuchfresh.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.Slot;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.Fonts;


/**
 * Created by Shankar on 7/17/15.
 */
public class FreshDeliverySlotsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private FreshActivity activity;
    private ArrayList<Slot> slots;
    private Callback callback;

    public FreshDeliverySlotsAdapter(FreshActivity activity, ArrayList<Slot> slots, Callback callback) {
        this.activity = activity;
        this.slots = slots;
        this.callback = callback;
    }

    public void setList(ArrayList<Slot> slots){
        this.slots = slots;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == SlotViewType.SLOT_DAY.getOrdinal()){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_fresh_delivery_slot_day, parent, false);
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(layoutParams);
            ASSL.DoMagic(v);
            return new ViewHolderSlotDay(v, activity);

        } else if(viewType == SlotViewType.SLOT_TIME.getOrdinal()){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_fresh_delivery_slot, parent, false);
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(layoutParams);
            ASSL.DoMagic(v);
            return new ViewHolderSlot(v, activity);

        } else{
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_fresh_delivery_slot_divider, parent, false);
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(layoutParams);
            ASSL.DoMagic(v);
            return new ViewHolderSlotDiv(v, activity);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            if(holder instanceof ViewHolderSlotDay){
				((ViewHolderSlotDay)holder).textViewSlotDay.setText(slots.get(position).getDayName());
                if(slots.get(position).getIsActiveSlot() == 1){
                    ((ViewHolderSlotDay)holder).textViewSlotDay.setAlpha(1.0f);
                } else{
                    ((ViewHolderSlotDay)holder).textViewSlotDay.setAlpha(0.4f);
                }
			} else if(holder instanceof ViewHolderSlot){
                Slot slot = slots.get(position);

				((ViewHolderSlot)holder).textViewSlotTime.setText(DateOperations.convertDayTimeAPViaFormat(slot.getStartTime())
                        + " - " + DateOperations.convertDayTimeAPViaFormat(slot.getEndTime()));
                if(activity.getSlotSelected() == null
                        || !activity.getSlotSelected().getDeliverySlotId().equals(slot.getDeliverySlotId())){
                    ((ViewHolderSlot)holder).imageViewRadio.setImageResource(R.drawable.ic_radio_button_normal);
                } else{
                    ((ViewHolderSlot)holder).imageViewRadio.setImageResource(R.drawable.ic_radio_button_selected);
                }
                if(slot.getIsActiveSlot() == 1){
                    ((ViewHolderSlot)holder).textViewSlotTime.setAlpha(1.0f);
                    ((ViewHolderSlot)holder).imageViewRadio.setAlpha(1.0f);
                } else{
                    ((ViewHolderSlot)holder).textViewSlotTime.setAlpha(0.4f);
                    ((ViewHolderSlot)holder).imageViewRadio.setAlpha(0.4f);
                }
                ((ViewHolderSlot)holder).linear.setEnabled(slot.getIsActiveSlot() == 1);
				((ViewHolderSlot)holder).linear.setTag(position);
				((ViewHolderSlot)holder).linear.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						try {
							int pos = (int) v.getTag();
                            callback.onSlotSelected(pos, slots.get(pos));
                            notifyDataSetChanged();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
        } catch (Exception e) {
            e.printStackTrace();
        }

	}

    @Override
    public int getItemCount() {
        return slots == null ? 0 : slots.size();
    }

    @Override
    public int getItemViewType(int position) {
        return slots.get(position).getSlotViewType().getOrdinal();
    }

    static class ViewHolderSlot extends RecyclerView.ViewHolder {
        public LinearLayout linear;
        private ImageView imageViewRadio;
        public TextView textViewSlotTime;
        public ViewHolderSlot(View itemView, Context context) {
            super(itemView);
            linear = (LinearLayout) itemView.findViewById(R.id.linear);
            imageViewRadio = (ImageView) itemView.findViewById(R.id.imageViewRadio);
            textViewSlotTime = (TextView)itemView.findViewById(R.id.textViewSlotTime); textViewSlotTime.setTypeface(Fonts.mavenRegular(context));
        }
    }

    static class ViewHolderSlotDay extends RecyclerView.ViewHolder {
        public TextView textViewSlotDay;
        public ViewHolderSlotDay(View itemView, Context context) {
            super(itemView);
            textViewSlotDay = (TextView)itemView.findViewById(R.id.textViewSlotDay); textViewSlotDay.setTypeface(Fonts.mavenRegular(context), Typeface.BOLD);
        }
    }

    static class ViewHolderSlotDiv extends RecyclerView.ViewHolder {
        public ViewHolderSlotDiv(View itemView, Context context) {
            super(itemView);
        }
    }

    public interface Callback{
        void onSlotSelected(int position, Slot slot);
    }

    public enum SlotViewType {
        SLOT_TIME(0),
        SLOT_DAY(1),
        DIVIDER(2)
        ;

        private int ordinal;

        SlotViewType(int ordinal) {
            this.ordinal = ordinal;
        }

        public int getOrdinal() {
            return ordinal;
        }
    }

}

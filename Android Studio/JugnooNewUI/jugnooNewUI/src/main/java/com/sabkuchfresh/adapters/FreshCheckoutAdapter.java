package com.sabkuchfresh.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.Slot;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;

/**
 * Created by gurmail on 19/05/16.
 */
public class FreshCheckoutAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private FreshActivity activity;
    private ArrayList<Slot> slots;
    private Callback callback;
    protected String editTextStr = "";

    public FreshCheckoutAdapter(FreshActivity activity, ArrayList<Slot> slots, Callback callback) {
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
        if(viewType == SlotViewType.HEADER.getOrdinal()){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_checkout_header, parent, false);
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(layoutParams);
            ASSL.DoMagic(v);
            return new ViewHolderHeader(v, activity);

        } else if(viewType == SlotViewType.SLOT_TIME.getOrdinal()){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_fresh_delivery_slot, parent, false);
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(layoutParams);
            ASSL.DoMagic(v);
            return new ViewHolderSlot(v, activity);

        } else if(viewType == SlotViewType.FEED.getOrdinal()) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_checkout_edittext, parent, false);
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(layoutParams);
            ASSL.DoMagic(v);
            return new ViewHolderFeed(v, activity);

        } else if(viewType == SlotViewType.DIVIDER.getOrdinal()) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_fresh_slot_footer, parent, false);
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(layoutParams);
            ASSL.DoMagic(v);
            return new ViewHolderSlotDiv(v, activity);

        } else if(viewType == SlotViewType.SLOT_STATUS.getOrdinal()) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_fresh_delivery_slot_divider, parent, false);
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(layoutParams);
            ASSL.DoMagic(v);
            return new ViewHolderSlotDiv(v, activity);

        } else {
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
                if(slots.get(position).isEnabled()){
                    ((ViewHolderSlotDay)holder).textViewSlotDay.setAlpha(1.0f);
                } else{
                    ((ViewHolderSlotDay)holder).textViewSlotDay.setAlpha(0.4f);
                }
            } else if(holder instanceof ViewHolderHeader) {


                ((ViewHolderHeader)holder).textViewDeliveryChargesValue.setText(slots.get(position).getCamount());
//                ((ViewHolderHeader)holder).textViewAmountPayableValue.setText(slots.get(position).getCtotal());
                if(!TextUtils.isEmpty(slots.get(position).getCaddress())) {
                    ((ViewHolderHeader) holder).addressText.setText(slots.get(position).getCaddress());
                    ((ViewHolderHeader)holder).textViewDeliveryAddress.setClickable(false);
                    ((ViewHolderHeader) holder).addressText.setTextColor(activity.getResources().getColor(R.color.text_color));
                    ((ViewHolderHeader) holder).textViewDeliveryAddress.setVisibility(View.VISIBLE);
                    ((ViewHolderHeader)holder).textViewDeliveryAddress.setText(activity.getResources().getString(R.string.delivery_address));
                } else {
                    ((ViewHolderHeader)holder).textViewDeliveryAddress.setVisibility(View.GONE);
                    ((ViewHolderHeader) holder).addressText.setTextColor(activity.getResources().getColor(R.color.theme_color));
                    ((ViewHolderHeader) holder).addressText.setText(activity.getResources().getString(R.string.add_address));
                }


                ((ViewHolderHeader)holder).deliveryAddress.setTag(position);
                ((ViewHolderHeader)holder).deliveryAddress.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            callback.onAddressClick();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                ((ViewHolderHeader)holder).textViewDeliveryAddress.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            callback.onAddressClick();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            } else if(holder instanceof ViewHolderFeed) {


                ((ViewHolderFeed)holder).editText.setText(""+activity.getSpecialInst());
                ((ViewHolderFeed)holder).editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
//                        activity.setSplInstr("");
                        activity.setSplInstr(""+s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });


            } else if(holder instanceof ViewHolderSlot){
                Slot slot = slots.get(position);
                Log.d("position", "position = "+(position));
                ((ViewHolderSlot)holder).textViewSlotTime.setText(slot.getDayName()+", "+ DateOperations.convertDayTimeAPViaFormat(slot.getStartTime())
                        + " - " + DateOperations.convertDayTimeAPViaFormat(slot.getEndTime()));
                if(activity.getSlotToSelect() == null || !activity.getSlotToSelect().getDeliverySlotId().equals(slot.getDeliverySlotId())){
                    ((ViewHolderSlot)holder).imageViewRadio.setImageResource(R.drawable.ic_radio_button_normal);
                } else{
                    ((ViewHolderSlot)holder).imageViewRadio.setImageResource(R.drawable.ic_radio_button_selected);
                }
                if(slot.isEnabled()){
                    ((ViewHolderSlot)holder).textViewSlotTime.setAlpha(1.0f);
                    ((ViewHolderSlot)holder).imageViewRadio.setAlpha(1.0f);
                } else{
                    ((ViewHolderSlot)holder).textViewSlotTime.setAlpha(0.4f);
                    ((ViewHolderSlot)holder).imageViewRadio.setAlpha(0.4f);
                }
                ((ViewHolderSlot)holder).linear.setEnabled(slot.isEnabled());
                ((ViewHolderSlot)holder).linear.setTag(position);
                ((ViewHolderSlot)holder).linear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            int pos = (int) v.getTag();
                            //pos = pos - 2;
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
            textViewSlotTime = (TextView)itemView.findViewById(R.id.textViewSlotTime);
            textViewSlotTime.setTypeface(Fonts.mavenRegular(context));
        }
    }

    static class ViewHolderSlotDay extends RecyclerView.ViewHolder {
        public TextView textViewSlotDay;
        public ViewHolderSlotDay(View itemView, Context context) {
            super(itemView);
            textViewSlotDay = (TextView)itemView.findViewById(R.id.textViewSlotDay); textViewSlotDay.setTypeface(Fonts.mavenRegular(context), Typeface.BOLD);
        }
    }

    static class ViewHolderFeed extends RecyclerView.ViewHolder {
        public EditText editText;
        public ViewHolderFeed(View itemView, Context context) {
            super(itemView);
            editText = (EditText) itemView.findViewById(R.id.order_note_edittext);
            editText.setTypeface(Fonts.mavenRegular(context));
        }
    }

    static class ViewHolderHeader extends RecyclerView.ViewHolder {
        public RelativeLayout deliveryAddress;
        public TextView textViewDeliveryCharges, textViewDeliveryChargesValue,
//                textViewAmountPayable, textViewAmountPayableValue,
            textViewDeliveryTime, textViewDeliveryAddress, addressText;


        public ViewHolderHeader(View itemView, Context context) {
            super(itemView);
            deliveryAddress = (RelativeLayout) itemView.findViewById(R.id.delivery_address);

            textViewDeliveryCharges = (TextView)itemView.findViewById(R.id.textViewDeliveryCharges); textViewDeliveryCharges.setTypeface(Fonts.mavenRegular(context));
            textViewDeliveryChargesValue = (TextView)itemView.findViewById(R.id.textViewDeliveryChargesValue); textViewDeliveryChargesValue.setTypeface(Fonts.mavenRegular(context));
            textViewDeliveryAddress = (TextView)itemView.findViewById(R.id.textViewDeliveryAddress); textViewDeliveryAddress.setTypeface(Fonts.mavenRegular(context));
            textViewDeliveryTime = (TextView)itemView.findViewById(R.id.textViewDeliveryTime); textViewDeliveryTime.setTypeface(Fonts.mavenRegular(context));
            addressText = (TextView)itemView.findViewById(R.id.address_text); addressText.setTypeface(Fonts.mavenRegular(context));

        }
    }

    static class ViewHolderSlotDiv extends RecyclerView.ViewHolder {
        public ViewHolderSlotDiv(View itemView, Context context) {
            super(itemView);
        }
    }

    public interface Callback{
        void onSlotSelected(int position, Slot slot);
        void onAddressClick();
    }

    public enum SlotViewType {
        SLOT_TIME(0),
        SLOT_DAY(1),
        DIVIDER(2),
        HEADER(3),
        SLOT_STATUS(4),
        FEED(5)
        ;

        private int ordinal;

        SlotViewType(int ordinal) {
            this.ordinal = ordinal;
        }

        public int getOrdinal() {
            return ordinal;
        }
    }

    /**
     * Custom class which implements Text Watcher
     */
    private class CustomEtListener implements TextWatcher {
        private int position;

        /**
         * Updates the position according to onBindViewHolder
         *
         * @param position - position of the focused item
         */
        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // Change the value of array according to the position
            editTextStr = charSequence.toString();
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    }

}

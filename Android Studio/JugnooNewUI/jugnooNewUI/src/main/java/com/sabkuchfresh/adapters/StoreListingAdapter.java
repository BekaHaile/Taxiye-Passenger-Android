package com.sabkuchfresh.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.Store;
import product.clicklabs.jugnoo.utils.Fonts;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;

/**
 * Created by gurmail on 14/07/16.
 */
public class StoreListingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private FreshActivity activity;
    private ArrayList<Store> sortArray;
    private Callback callback;
    private int textColor = Color.parseColor("#FFFFFF");

    public StoreListingAdapter(FreshActivity activity, ArrayList<Store> sortArray, Callback callback) {
        this.activity = activity;
        this.sortArray = sortArray;
        this.callback = callback;
    }

    public void setList(ArrayList<Store> sortArray){
        this.sortArray = sortArray;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.store_list_item, parent, false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        v.setLayoutParams(layoutParams);
        ASSL.DoMagic(v);
        return new ViewHolderSlot(v, activity);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {

            ViewHolderSlot holderSlot = (ViewHolderSlot) holder;

            Store slot = sortArray.get(position);

            holderSlot.title.setText(slot.getTitle());
            holderSlot.description.setText(slot.getDescription());

            try {
                textColor = Color.parseColor(slot.getTextColor());
                holderSlot.title.setTextColor(textColor);
                holderSlot.description.setTextColor(textColor);
                GradientDrawable bgShape = (GradientDrawable) holderSlot.description.getBackground();
                bgShape.setStroke(2, textColor);
            } catch (Exception e) {

            }



//                ((ViewHolderSlot)holder).storeImage.setImageResource(R.drawable.radio_unselected_icon);
//
            holderSlot.linear.setTag(position);
            holderSlot.linear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int pos = (int) v.getTag();
                        int storeId = sortArray.get(pos).getStoreId();
                        callback.onSlotSelected(storeId);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            if(position == (sortArray.size()-1)) {
                holderSlot.belowImage.setVisibility(View.GONE);
            } else {
                holderSlot.belowImage.setVisibility(View.VISIBLE);
            }


            try {
//                Log.d("TAG", "slot.getImage() = "+slot.getImage());
                if (slot.getImage() != null && !"".equalsIgnoreCase(slot.getImage())) {

                    Picasso.with(activity).load(slot.getImage())
                            .placeholder(R.drawable.ic_fresh_noti_placeholder)
                            .error(R.drawable.ic_fresh_noti_placeholder)
                            .into(holderSlot.storeImage);
                } else {
                    holderSlot.storeImage.setImageResource(R.drawable.ic_fresh_noti_placeholder);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return sortArray == null ? 0 : sortArray.size();
    }


    static class ViewHolderSlot extends RecyclerView.ViewHolder {
        public LinearLayout linear;
        private ImageView storeImage, belowImage;
        public TextView title, description;
        public ViewHolderSlot(View itemView, Context context) {
            super(itemView);
            linear = (LinearLayout) itemView.findViewById(R.id.linear);
            storeImage = (ImageView) itemView.findViewById(R.id.store_image);
            belowImage = (ImageView) itemView.findViewById(R.id.below_image);
            title = (TextView)itemView.findViewById(R.id.title); title.setTypeface(Fonts.mavenRegular(context), Typeface.BOLD);
            description = (TextView)itemView.findViewById(R.id.description); description.setTypeface(Fonts.mavenRegular(context));

        }
    }


    public interface Callback {
        void onSlotSelected(int storeId);
    }

}

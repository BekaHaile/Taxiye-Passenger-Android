package com.sabkuchfresh.adapters;

import android.app.Activity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.feed.VehicleInfo;
import com.squareup.picasso.Picasso;

import java.util.List;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;

public class DeliveriesAdapter extends RecyclerView.Adapter<DeliveriesAdapter.ViewHolder> {

    private FreshActivity activity;
    private List<SearchResult> deliveries;
    private boolean[] mSelectionArray;

    public DeliveriesAdapter(FreshActivity activity, List<SearchResult> deliveries ){
        this.activity =  activity;
        this.deliveries = deliveries;
        mSelectionArray = new boolean[deliveries.size()];
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_delivery,parent,false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        v.setLayoutParams(layoutParams);
        return new DeliveriesAdapter.ViewHolder(v, activity);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        int pos = h.getAdapterPosition();

        if (h instanceof DeliveriesAdapter.ViewHolder) {
            DeliveriesAdapter.ViewHolder holder = h;

            String addressType;
            if (deliveries.get(pos).getName().equalsIgnoreCase(activity.getString(R.string.home))) {
                holder.ivDelAddressType.setImageResource(R.drawable.ic_home);
                addressType = activity.getString(R.string.home);
            } else if (deliveries.get(pos).getName().equalsIgnoreCase(activity.getString(R.string.work))) {
                holder.ivDelAddressType.setImageResource(R.drawable.ic_work);
                addressType = activity.getString(R.string.work);
            } else {
                holder.ivDelAddressType.setImageResource(R.drawable.ic_loc_other);
                addressType = deliveries.get(pos).getName();
            }


            ForegroundColorSpan textColorSpan = new ForegroundColorSpan(ContextCompat.getColor(activity, R.color.text_color));
            ForegroundColorSpan textHintColorSpan = new ForegroundColorSpan(ContextCompat.getColor(activity, R.color.text_color_hint));
            RelativeSizeSpan RELATIVE_SIZE_SPAN = new RelativeSizeSpan(1.15f);
            addressType = addressType.length() == 0 ? addressType : addressType + "\n";
            SpannableString spannableString = new SpannableString(addressType + deliveries.get(pos).getAddress());
            spannableString.setSpan(textHintColorSpan, 0, addressType.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            spannableString.setSpan(RELATIVE_SIZE_SPAN, 0, addressType.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            spannableString.setSpan(textColorSpan, spannableString.length() - deliveries.get(pos).getAddress().length(), spannableString.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            holder.tv_delivery_address.setText(spannableString);
            holder.tv_delivery_name.setText("Name: "+(deliveries.get(pos).getPersonName()==null?"-----":deliveries.get(pos).getPersonName()));
            holder.tv_delivery_phone.setText("Phone: "+(deliveries.get(pos).getPhone()==null?"----------":deliveries.get(pos).getPhone()));

            holder.ivDeleteAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.getAnywhereHomeFragment().addressDeleted(pos);
                }
            });

        }

    }

    @Override
    public int getItemCount() {
        return deliveries == null ? 0 : deliveries.size();
    }

    public interface OnItemSelectedListener {

        void onItemSelected(SearchResult item, int pos);
    }

    public void updateList(List<SearchResult> list){
        deliveries = list;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivDelAddressType, ivDeleteAddress;
        public TextView tv_delivery_address, tv_delivery_name,tv_delivery_phone;


        public ViewHolder(View itemView, Activity activity) {
            super(itemView);
            ivDelAddressType = (ImageView) itemView.findViewById(R.id.ivDelAddressType);
            ivDeleteAddress = (ImageView) itemView.findViewById(R.id.ivDeleteAddress);
            tv_delivery_address = (TextView)itemView.findViewById(R.id.tv_delivery_address);
            tv_delivery_address.setTypeface(Fonts.avenirNext(activity));
            tv_delivery_name = (TextView)itemView.findViewById(R.id.tv_delivery_name);
            tv_delivery_name.setTypeface(Fonts.mavenMedium(activity));
            tv_delivery_phone = (TextView)itemView.findViewById(R.id.tv_delivery_phone);
            tv_delivery_phone.setTypeface(Fonts.mavenMedium(activity));
        }
    }
}

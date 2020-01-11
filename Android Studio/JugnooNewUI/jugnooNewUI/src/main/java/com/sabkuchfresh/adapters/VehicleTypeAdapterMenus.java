package com.sabkuchfresh.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.UserCheckoutResponse;
import com.sabkuchfresh.utils.DiscountedFareTextView;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;

public class VehicleTypeAdapterMenus extends RecyclerView.Adapter<VehicleTypeAdapterMenus.ViewHolder> {

    private FreshActivity activity;
    private List<UserCheckoutResponse.VehiclesList> mVehiclesList;
    private boolean[] mSelectionArray;
    private int currentSelection = -1;
    private OnItemSelectedListener onClick;

    public VehicleTypeAdapterMenus(FreshActivity activity, List<UserCheckoutResponse.VehiclesList> mVehiclesList, int currentSelection , final OnItemSelectedListener onClick ){
        this.activity =  activity;
        this.mVehiclesList = mVehiclesList;
        this.currentSelection=currentSelection;
        mSelectionArray = new boolean[mVehiclesList.size()];
        if (mSelectionArray.length > 0 && currentSelection >= 0 && currentSelection < mSelectionArray.length) {
            mSelectionArray[currentSelection] = true;
        }
        this.onClick=onClick;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_vehicle,parent,false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, 125);
        v.setLayoutParams(layoutParams);

        ASSL.DoMagic(v);
        return new VehicleTypeAdapterMenus.ViewHolder(v, activity);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        int pos = h.getAdapterPosition();

        if (h instanceof VehicleTypeAdapterMenus.ViewHolder) {
            VehicleTypeAdapterMenus.ViewHolder holder = h;

            holder.textViewVehicleName.setText(mVehiclesList.get(pos).getName());
//            if(mVehiclesList.get(pos).getImage() == null || mVehiclesList.get(pos).getImage().isEmpty()){
//                holder.ivVehicleType.setImageResource(R.drawable.ic_bike_track_order_marker);
//            }
//            else {
//                Glide.with(holder.tvText.getContext()).load(mVehiclesList.get(pos).getImage()).into(holder.ivVehicleType);
//            }

//            if (mSelectionArray[pos]) {
//                Glide.with(holder.textViewVehicleName.getContext()).load(mVehiclesList.get(pos).getImages().getRideNowNormal()).into(holder.imageViewTab);
////                holder.ivImage.setImageResource(R.drawable.ic_radio_button_selected);
//            } else {
//                Glide.with(holder.textViewVehicleName.getContext()).load(mVehiclesList.get(pos).getImages().getTabNormal()).into(holder.imageViewTab);
//            }
            if(mSelectionArray[pos]) {
                Picasso.with(activity)
                        .load(mVehiclesList.get(pos).getImages().getTabHighlighted())
                        .placeholder(R.drawable.ic_auto_pool_tab_normal)
                        .into(holder.imageViewTab);
                holder.textViewVehicleName.setTextColor(activity.getResources().getColor(R.color.theme_color));
                holder.imageViewSelected.setBackgroundColor(activity.getResources().getColor(R.color.theme_color));
            }
            else
            {
                Picasso.with(activity)
                        .load(mVehiclesList.get(pos).getImages().getTabNormal())
                        .placeholder(R.drawable.ic_auto_pool_tab_normal)
                        .into(holder.imageViewTab);
                holder.textViewVehicleName.setTextColor(activity.getResources().getColor(R.color.text_color));
                holder.imageViewSelected.setBackgroundColor(activity.getResources().getColor(R.color.white));
            }
            if(position == mVehiclesList.size() - 1){
                holder.imageViewSep.setVisibility(View.GONE);
            }
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.relative.getLayoutParams();
            params.width = getItemWidth();
            holder.relative.setLayoutParams(params);

        }

    }
    private int getItemWidth(){
        int width = (int)((720f/ (getItemCount() > 3 ? 3 : getItemCount())) * ASSL.Xscale());
        int minWidth = (int) (100f * ASSL.Xscale());
        return width >= minWidth ? width : minWidth;
    }

    @Override
    public int getItemCount() {
        return mVehiclesList == null ? 0 : mVehiclesList.size();
    }

    public interface OnItemSelectedListener {

        void onItemSelected(UserCheckoutResponse.VehiclesList item, int pos);
    }

    public void updateList(List<UserCheckoutResponse.VehiclesList> list){
        mVehiclesList = list;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout relative;
        public ImageView imageViewSep, imageViewTab, imageViewMultipleSurge;
        public ImageView imageViewSelected;
        public TextView tvETA, textViewVehicleName,tvVehicleFare, tvOfferTag;
        public DiscountedFareTextView tvVehicleFareStrike;

        public ViewHolder(View itemView, Activity activity) {
            super(itemView);
            relative = (RelativeLayout) itemView.findViewById(R.id.relative);
            imageViewSep = (ImageView) itemView.findViewById(R.id.imageViewSep);
            imageViewTab = (ImageView) itemView.findViewById(R.id.imageViewTab);
            imageViewMultipleSurge = (ImageView) itemView.findViewById(R.id.imageViewMultipleSurge);
            imageViewMultipleSurge.setVisibility(View.GONE);
            imageViewSelected = (ImageView) itemView.findViewById(R.id.imageViewSelected);
            textViewVehicleName = (TextView)itemView.findViewById(R.id.textViewVehicleName);
            textViewVehicleName.setTypeface(Fonts.avenirNext(activity), Typeface.BOLD);
            tvETA = (TextView)itemView.findViewById(R.id.tvETA);
            tvETA.setVisibility(View.GONE);
            tvETA.setTypeface(Fonts.mavenMedium(activity));
            tvVehicleFare = (TextView)itemView.findViewById(R.id.tvVehicleFare);
            tvVehicleFare.setVisibility(View.GONE);
            tvVehicleFare.setTypeface(Fonts.mavenMedium(activity));
            tvOfferTag = (TextView)itemView.findViewById(R.id.tvOfferTag);
            tvOfferTag.setTypeface(Fonts.mavenRegular(activity));
            tvOfferTag.setVisibility(View.GONE);
            tvVehicleFareStrike = (DiscountedFareTextView)itemView.findViewById(R.id.tvVehicleFareStrike);
            tvVehicleFareStrike.setVisibility(View.GONE);
            tvVehicleFareStrike.setTypeface(Fonts.mavenRegular(activity));
            View linearLayoutContainer= itemView.findViewById(R.id.linearLayoutContainer);
            linearLayoutContainer.setBackgroundColor(Color.WHITE);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    int pos = getAdapterPosition();

                    if (currentSelection != -1 && currentSelection < mVehiclesList.size()) {
                        mSelectionArray[currentSelection] = false;
                        notifyItemChanged(currentSelection);
                    }

                    mSelectionArray[pos] = true;
                    currentSelection = pos;
                    notifyItemChanged(currentSelection);

                    onClick.onItemSelected(mVehiclesList.get(pos), pos);
                }
            });
        }
    }
}

package product.clicklabs.jugnoo.home.adapters;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.utils.DiscountedFareTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.dialogs.VehicleFareEstimateDialog;
import product.clicklabs.jugnoo.home.models.Region;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;

public class VehiclesTabAdapter extends RecyclerView.Adapter<VehiclesTabAdapter.ViewHolder> implements GAAction, GACategory{

    private HomeActivity activity;
    private ArrayList<Region> regions = new ArrayList<>();
    private boolean showRegionFares;
    private VehicleFareEstimateDialog estimateDialog;

    public VehiclesTabAdapter(HomeActivity activity, ArrayList<Region> regions,boolean showFares) {
        this.regions = regions;
        this.activity = activity;
        this.showRegionFares = showFares;
        this.estimateDialog = new VehicleFareEstimateDialog();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_vehicle, parent, false);

        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT,
                showRegionFares ?RecyclerView.LayoutParams.WRAP_CONTENT:125);
        v.setLayoutParams(layoutParams);

        ASSL.DoMagic(v);
        return new ViewHolder(v, activity, showRegionFares);
    }

    @Override
    public void onBindViewHolder(VehiclesTabAdapter.ViewHolder holder, int position) {
        Region region = regions.get(position);

        boolean selected = region.getOperatorId() == activity.getSlidingBottomPanel().getRequestRideOptionsFragment().getRegionSelected().getOperatorId()
                && region.getVehicleType().equals(activity.getSlidingBottomPanel().getRequestRideOptionsFragment().getRegionSelected().getVehicleType())
                && region.getRegionId().equals(activity.getSlidingBottomPanel().getRequestRideOptionsFragment().getRegionSelected().getRegionId())
                ;

        holder.textViewVehicleName.setText(region.getRegionName());
        holder.relative.setTag(position);
        int visibility = showRegionFares && region.getRegionFare()!=null ?View.VISIBLE:View.GONE;
        holder.tvVehicleFare.setVisibility(visibility);
        holder.tvVehicleFareStrike.setVisibility(View.GONE);
        holder.tvETA.setVisibility(visibility);
        holder.tvETA.setText(region.getEta() + " " + activity.getString(R.string.min));
        holder.tvOfferTag.setVisibility(View.GONE);
        if(showRegionFares && region.getRegionFare() != null){
            holder.tvVehicleFare.setText(region.getRegionFare().getFareText(region.getFareMandatory()));
            holder.tvVehicleFareStrike.setText(region.getRegionFare().getStrikedFareText(region.getFareMandatory()));
            holder.tvVehicleFareStrike.setVisibility(holder.tvVehicleFareStrike.getText().length() > 0 ? View.VISIBLE : View.GONE);
            String discount = region.getRegionFare().getDiscountText(region.getFareMandatory());
            if (selected && !TextUtils.isEmpty(discount)) {
                holder.tvOfferTag.setVisibility(View.VISIBLE);
                holder.tvOfferTag.setText(discount);
            }
        }


        if(region.getCustomerFareFactor() > 1.0){
            holder.imageViewMultipleSurge.setVisibility(View.VISIBLE);
        } else{
            holder.imageViewMultipleSurge.setVisibility(View.GONE);
        }

        holder.imageViewSelected.setVisibility(showRegionFares ? View.INVISIBLE : View.VISIBLE);
        try {
            if(selected){
                holder.textViewVehicleName.setTextColor(activity.getResources().getColor(R.color.theme_color));
                holder.imageViewSelected.setBackgroundColor(activity.getResources().getColor(R.color.theme_color));
                Picasso.with(activity)
                        .load(region.getImages().getTabHighlighted())
                        .placeholder(region.getTabSelected())
                        .into(holder.imageViewTab);
            } else{
                holder.textViewVehicleName.setTextColor(activity.getResources().getColorStateList(R.color.text_color_theme_color_selector));
                holder.imageViewSelected.setBackgroundColor(activity.getResources().getColor(R.color.white));
                Picasso.with(activity)
                        .load(region.getImages().getTabNormal())
                        .placeholder(region.getTabNormal())
                        .into(holder.imageViewTab);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        holder.relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                boolean changed = activity.setVehicleTypeSelected(position, true);
                if(showRegionFares && !changed){
                    estimateDialog.show(activity, regions.get(position));
                }
                try {
                    GAUtils.event(RIDES, HOME, regions.get(position).getRegionName()+" "+CLICKED);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.relative.getLayoutParams();
        params.width = getItemWidth();
        holder.relative.setLayoutParams(params);


	}

    @Override
    public int getItemCount() {
        return regions == null ? 0 : regions.size();
    }

    private int getItemWidth(){
        int width = (int)((720f / (getItemCount() > 3 ? 3 : getItemCount())) * ASSL.Xscale());
        int minWidth = (int) (100f * ASSL.Xscale());
        return width >= minWidth ? width : minWidth;
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout relative;
        public ImageView imageViewSep, imageViewTab, imageViewMultipleSurge;
        public ImageView imageViewSelected;
        public TextView tvETA, textViewVehicleName,tvVehicleFare, tvOfferTag;
        public DiscountedFareTextView tvVehicleFareStrike;

        public ViewHolder(View itemView, Activity activity,boolean showingConfirmLayout) {
            super(itemView);
            relative = (RelativeLayout) itemView.findViewById(R.id.relative);
            imageViewSep = (ImageView) itemView.findViewById(R.id.imageViewSep);
            imageViewTab = (ImageView) itemView.findViewById(R.id.imageViewTab);
            imageViewMultipleSurge = (ImageView) itemView.findViewById(R.id.imageViewMultipleSurge);
            imageViewSelected = (ImageView) itemView.findViewById(R.id.imageViewSelected);
            textViewVehicleName = (TextView)itemView.findViewById(R.id.textViewVehicleName);
            textViewVehicleName.setTypeface(Fonts.avenirNext(activity), Typeface.BOLD);
            tvETA = (TextView)itemView.findViewById(R.id.tvETA);
            tvETA.setTypeface(Fonts.mavenMedium(activity));
            tvVehicleFare = (TextView)itemView.findViewById(R.id.tvVehicleFare);
            tvVehicleFare.setTypeface(Fonts.mavenMedium(activity));
            tvOfferTag = (TextView)itemView.findViewById(R.id.tvOfferTag);
            tvOfferTag.setTypeface(Fonts.mavenRegular(activity));
            tvVehicleFareStrike = (DiscountedFareTextView)itemView.findViewById(R.id.tvVehicleFareStrike);
            tvVehicleFareStrike.setTypeface(Fonts.mavenRegular(activity));
            View linearLayoutContainer= itemView.findViewById(R.id.linearLayoutContainer);
            if(showingConfirmLayout){
               RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) imageViewSelected.getLayoutParams();
               layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
               layoutParams.addRule(RelativeLayout.BELOW,linearLayoutContainer.getId());
               imageViewSelected.setLayoutParams(layoutParams);

            }
        }
    }
}

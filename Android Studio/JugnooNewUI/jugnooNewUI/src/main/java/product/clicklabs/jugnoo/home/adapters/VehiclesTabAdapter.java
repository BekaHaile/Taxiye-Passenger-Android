package product.clicklabs.jugnoo.home.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.models.Region;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;


/**
 * Created by Ankit on 7/17/15.
 */
public class VehiclesTabAdapter extends RecyclerView.Adapter<VehiclesTabAdapter.ViewHolder> {

    private HomeActivity activity;
    private ArrayList<Region> regions = new ArrayList<>();

    public VehiclesTabAdapter(HomeActivity activity, ArrayList<Region> regions) {
        this.regions = regions;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_vehicle, parent, false);

        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(185, 110);
        v.setLayoutParams(layoutParams);

        ASSL.DoMagic(v);
        return new ViewHolder(v, activity);
    }

    @Override
    public void onBindViewHolder(VehiclesTabAdapter.ViewHolder holder, int position) {
        Region region = regions.get(position);

        holder.textViewVehicleName.setText(region.getRegionName());
        holder.relative.setTag(position);

        boolean selected = region.getVehicleType().equals(activity.getSlidingBottomPanel().getRegionSelected().getVehicleType())
                && region.getRegionId().equals(activity.getSlidingBottomPanel().getRegionSelected().getRegionId());
        if(position == 0) {
            holder.relative.setBackgroundResource(selected ? R.drawable.bg_grey_light_lrb : R.drawable.bg_transparent_grey_light_lrb_selector);
            holder.imageViewSep.setVisibility(View.VISIBLE);
        } else if(position == (getItemCount()-1)){
            holder.relative.setBackgroundResource(selected ? R.drawable.bg_grey_light_rrb : R.drawable.bg_transparent_grey_light_rrb_selector);
            holder.imageViewSep.setVisibility(View.GONE);
        } else{
            holder.relative.setBackgroundResource(selected ? R.drawable.bg_grey_light_b : R.drawable.bg_transparent_grey_light_b_selector);
            holder.imageViewSep.setVisibility(View.VISIBLE);
        }
        if(!selected){
            int pos = position + 1;
            if(pos < regions.size()
                    && regions.get(pos).getVehicleType().equals(activity.getSlidingBottomPanel().getRegionSelected().getVehicleType())
                    && regions.get(pos).getRegionId().equals(activity.getSlidingBottomPanel().getRegionSelected().getRegionId())){
                holder.imageViewSep.setVisibility(View.GONE);
            }
        }
        holder.imageViewVehicle.setImageResource(region.getVehicleIconSet().getIconTab());

        holder.relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                activity.setVehicleTypeSelected(position);
            }
        });

	}

    @Override
    public int getItemCount() {
        return regions == null ? 0 : regions.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout relative;
        public ImageView imageViewVehicle, imageViewSep;
        public TextView textViewVehicleName;
        public ViewHolder(View itemView, Activity activity) {
            super(itemView);
            relative = (RelativeLayout) itemView.findViewById(R.id.relative);
            imageViewVehicle = (ImageView) itemView.findViewById(R.id.imageViewVehicle);
            imageViewSep = (ImageView) itemView.findViewById(R.id.imageViewSep);
            textViewVehicleName = (TextView)itemView.findViewById(R.id.textViewVehicleName);
            textViewVehicleName.setTypeface(Fonts.mavenLight(activity));
        }
    }
}

package product.clicklabs.jugnoo.home.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.models.Region;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;

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

        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, 118);
        v.setLayoutParams(layoutParams);

        ASSL.DoMagic(v);
        return new ViewHolder(v, activity);
    }

    @Override
    public void onBindViewHolder(VehiclesTabAdapter.ViewHolder holder, int position) {
        Region region = regions.get(position);

        holder.textViewVehicleName.setText(region.getRegionName());
        holder.relative.setTag(position);

        boolean selected = region.getVehicleType().equals(activity.getSlidingBottomPanel().getRequestRideOptionsFragment().getRegionSelected().getVehicleType())
                && region.getRegionId().equals(activity.getSlidingBottomPanel().getRequestRideOptionsFragment().getRegionSelected().getRegionId());

        if(selected){
            holder.textViewVehicleName.setTextColor(activity.getResources().getColor(R.color.theme_color));
            holder.imageViewSelected.setBackgroundColor(activity.getResources().getColor(R.color.theme_color));
        } else{
            holder.textViewVehicleName.setTextColor(activity.getResources().getColorStateList(R.color.text_color_theme_color_selector));
            holder.imageViewSelected.setBackgroundColor(activity.getResources().getColor(R.color.white));
        }

        holder.relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                activity.setVehicleTypeSelected(position);
                if(activity.getSlidingBottomPanel().getSlidingUpPanelLayout().getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED){
                    activity.getSlidingBottomPanel().getSlidingUpPanelLayout().setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
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
        int width = (int)((720f / (getItemCount() > 4 ? 4 : getItemCount())) * ASSL.Xscale());
        int minWidth = (int) (100f * ASSL.Xscale());
        return width >= minWidth ? width : minWidth;
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout relative;
        public ImageView imageViewSep;
        public View imageViewSelected;
        public TextView textViewVehicleName;
        public ViewHolder(View itemView, Activity activity) {
            super(itemView);
            relative = (RelativeLayout) itemView.findViewById(R.id.relative);
            imageViewSep = (ImageView) itemView.findViewById(R.id.imageViewSep);
            imageViewSelected = (View) itemView.findViewById(R.id.imageViewSelected);
            textViewVehicleName = (TextView)itemView.findViewById(R.id.textViewVehicleName);
            textViewVehicleName.setTypeface(Fonts.mavenMedium(activity));
        }
    }
}

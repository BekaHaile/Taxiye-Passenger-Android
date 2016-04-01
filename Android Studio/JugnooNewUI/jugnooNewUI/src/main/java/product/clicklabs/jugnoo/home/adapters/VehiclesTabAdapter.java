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
import product.clicklabs.jugnoo.home.models.VehicleType;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;


/**
 * Created by Ankit on 7/17/15.
 */
public class VehiclesTabAdapter extends RecyclerView.Adapter<VehiclesTabAdapter.ViewHolder> {

    private HomeActivity activity;
    private ArrayList<VehicleType> vehicleTypes = new ArrayList<>();

    public VehiclesTabAdapter(HomeActivity activity, ArrayList<VehicleType> vehicleTypes) {
        this.vehicleTypes = vehicleTypes;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_vehicle, parent, false);

        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(162, 110);
        v.setLayoutParams(layoutParams);

        ASSL.DoMagic(v);
        return new ViewHolder(v, activity);
    }

    @Override
    public void onBindViewHolder(VehiclesTabAdapter.ViewHolder holder, int position) {
        VehicleType vehicleType = vehicleTypes.get(position);

        holder.textViewVehicleName.setText(vehicleType.getName());
        holder.relative.setTag(position);

        if(position == 0){
            holder.relative.setBackgroundResource(R.drawable.bg_transparent_grey_light_lrb_selector);
            holder.imageViewSep.setVisibility(View.VISIBLE);
        } else if(position == (getItemCount()-1)){
            holder.relative.setBackgroundResource(R.drawable.bg_transparent_grey_light_rrb_selector);
            holder.imageViewSep.setVisibility(View.GONE);
        } else{
            holder.relative.setBackgroundResource(R.drawable.bg_transparent_grey_light_b_selector);
            holder.imageViewSep.setVisibility(View.VISIBLE);
        }

        holder.relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();

            }
        });

	}

    @Override
    public int getItemCount() {
        return vehicleTypes == null ? 0 : vehicleTypes.size();
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

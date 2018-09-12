package product.clicklabs.jugnoo.home.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.models.Region;

/**
 * Created by mohitkr.dhiman on 12/09/18.
 */

public class ScheduleRideVehicleListAdapter extends RecyclerView.Adapter<ScheduleRideVehicleListAdapter.ViewHolder> {
    private HomeActivity activity;
    private List<Region> vehicleList;

    public ScheduleRideVehicleListAdapter(HomeActivity activity, List<Region> vehicleList) {
        this.activity = activity;
        this.vehicleList = vehicleList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View taskItem = ((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_item_schedule_ride_vehicles, parent, false);
        return new ViewHolder(taskItem);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final int adapterPos = holder.getAdapterPosition();

        holder.tvVehicleName.setText(vehicleList.get(adapterPos).getRegionName());
        Picasso.with(activity)
                .load(vehicleList.get(adapterPos).getImages().getRideNowNormal())
                .into(holder.ivVehicleImage);
    }

    @Override
    public int getItemCount() {
        return vehicleList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {


        TextView tvVehicleName, tvBaseFare, tvFarePerMinute, tvFarePerMile;
        ImageView ivVehicleImage, ivSelected;

        ViewHolder(View itemView) {
            super(itemView);
            tvVehicleName = (TextView) itemView.findViewById(R.id.tvVehicleName);
            tvBaseFare = (TextView) itemView.findViewById(R.id.tvBaseFare);
            tvFarePerMinute = (TextView) itemView.findViewById(R.id.tvFarePerMinute);
            tvFarePerMile = (TextView) itemView.findViewById(R.id.tvFarePerMile);
            ivVehicleImage = (ImageView) itemView.findViewById(R.id.ivVehicleImage);
            ivSelected = (ImageView) itemView.findViewById(R.id.ivSelected);
        }
    }

}

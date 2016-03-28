package product.clicklabs.jugnoo.home.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.models.Vehicle;
import product.clicklabs.jugnoo.home.models.VehicleType;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.LinearLayoutLayoutManagerResizableRecyclerView;

/**
 * Created by Ankit on 1/8/16.
 */
public class BottomVehiclesFragment extends Fragment {

    private View rootView;
    private HomeActivity activity;
    private LinearLayout linearLayoutRoot;
    private RecyclerView recyclerViewVehicles;
    private VehiclesAdapter vehiclesAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_bottom_vehicle_list, container, false);
        activity = (HomeActivity) getActivity();
        linearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.linearLayoutRoot);
        try {
            if(linearLayoutRoot != null) {
                new ASSL(getActivity(), linearLayoutRoot, 1134, 720, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        recyclerViewVehicles = (RecyclerView) rootView.findViewById(R.id.recyclerViewVehicles);
        recyclerViewVehicles.setLayoutManager(new LinearLayoutLayoutManagerResizableRecyclerView(activity));
        recyclerViewVehicles.setItemAnimator(new DefaultItemAnimator());
        recyclerViewVehicles.setHasFixedSize(false);
        vehiclesAdapter = new VehiclesAdapter(Data.vehicleTypes);
        recyclerViewVehicles.setAdapter(vehiclesAdapter);
        activity.getSlidingBottomPanel().getSlidingUpPanelLayout().setScrollableView(recyclerViewVehicles);

        return rootView;
    }


    public void update(ArrayList<VehicleType> vehicleTypes){
        vehiclesAdapter.setData(vehicleTypes);
    }


    public class VehiclesAdapter extends RecyclerView.Adapter<VehiclesAdapter.ViewHolder> {

        private ArrayList<VehicleType> vehicleTypes;

        public VehiclesAdapter(ArrayList<VehicleType> vehicleTypes) {
            this.vehicleTypes = vehicleTypes;
        }

        public void setData(ArrayList<VehicleType> vehicleTypes){
            this.vehicleTypes = vehicleTypes;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_vehicle, parent, false);

            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(720, ViewGroup.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(layoutParams);

            ASSL.DoMagic(v);
            return new ViewHolder(v, activity);
        }

        @Override
        public void onBindViewHolder(VehiclesAdapter.ViewHolder holder, int position) {
            VehicleType vehicleType = vehicleTypes.get(position);

            holder.textViewVehicleName.setText(vehicleType.getName());
            if(activity.getSlidingBottomPanel().getVehicleTypeSelected() != null
                    && activity.getSlidingBottomPanel().getVehicleTypeSelected()
                    .getId().equals(vehicleType.getId())){
                holder.imageViewRadio.setImageResource(R.drawable.radio_selected_icon);
            } else{
                holder.imageViewRadio.setImageResource(R.drawable.radio_unselected_icon);
            }
            if(vehicleType.getId() == Vehicle.AUTO.getId()){
                holder.imageViewVehicleType.setImageResource(R.drawable.ic_auto_orange);
            } else{
                holder.imageViewVehicleType.setImageResource(R.drawable.ic_bike_orange);
            }

            holder.linearLayoutRoot.setTag(position);
            holder.linearLayoutRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int position = (int) v.getTag();
                        activity.setVehicleTypeSelected(position);
                        notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return vehicleTypes == null ? 0 : vehicleTypes.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            public LinearLayout linearLayoutRoot;
            public ImageView imageViewRadio, imageViewVehicleType;
            public TextView textViewVehicleName;
            public ViewHolder(View itemView, Activity activity) {
                super(itemView);
                linearLayoutRoot = (LinearLayout) itemView.findViewById(R.id.linearLayoutRoot);
                imageViewRadio = (ImageView) itemView.findViewById(R.id.imageViewRadio);
                imageViewVehicleType = (ImageView) itemView.findViewById(R.id.imageViewVehicleType);
                textViewVehicleName = (TextView) itemView.findViewById(R.id.textViewVehicleName);
                textViewVehicleName.setTypeface(Fonts.mavenLight(activity));
            }
        }
    }
}

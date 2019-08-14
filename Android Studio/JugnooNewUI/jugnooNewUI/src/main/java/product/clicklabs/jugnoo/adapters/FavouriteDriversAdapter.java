package product.clicklabs.jugnoo.adapters;


import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.FavouriteDriversActivity;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.home.models.VehicleTypeValue;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.GetFavouriteDriver;
import product.clicklabs.jugnoo.retrofit.model.GetFetchUserDriverResponse;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class FavouriteDriversAdapter extends RecyclerView.Adapter<FavouriteDriversAdapter.ViewHolder> {

    private ArrayList<GetFetchUserDriverResponse> favouriteDriverlist;
    private Activity activity;
    private String TAG;
    int count = 0;

    public FavouriteDriversAdapter(ArrayList<GetFetchUserDriverResponse> fetchUserDriverResponseArrayList, Activity activity) {
        favouriteDriverlist = fetchUserDriverResponseArrayList;
        Log.e("data recycler", favouriteDriverlist.size() + "");
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.e("data recycler", "on create view called favot");
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_favourite_driver, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.i("data", favouriteDriverlist.get(position).driverName);

        holder.imageViewVehicleType.setBackgroundResource(R.drawable.circle_theme);
            holder.tvDriverNameValue.setText(favouriteDriverlist.get(position).getDriverName());
//            holder.tvVehicleTypeValue.setText(favouriteDriverlist.get(position).getVehicleType() + "");
        if(favouriteDriverlist.get(position).getVehicleType()== VehicleTypeValue.AUTOS.getOrdinal()){
            holder.tvVehicleTypeValue.setText( R.string.auto);
            holder.imageViewVehicleType.setImageResource(R.drawable.ic_rides);
        } else if(favouriteDriverlist.get(position).getVehicleType()== VehicleTypeValue.BIKES.getOrdinal()) {
            holder.tvVehicleTypeValue.setText("Bike");
            holder.imageViewVehicleType.setImageResource(R.drawable.ic_bike_white);
        }else if(favouriteDriverlist.get(position).getVehicleType()== VehicleTypeValue.TAXI.getOrdinal()){
            holder.tvVehicleTypeValue.setText( "Taxi");
            holder.imageViewVehicleType.setImageResource(R.drawable.ic_car_white);
        }
        else{
            holder.tvVehicleTypeValue.setText( "Auto");
            holder.imageViewVehicleType.setImageResource(R.drawable.ic_rides);
        }
            holder.tvDriverRatingValue.setText(favouriteDriverlist.get(position).getAvgRating() + "");
            holder.imgVwCrossDriver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    warningDailog(favouriteDriverlist.get(position).getDriverId(), position);

                }
            });
    }



    private void deleteDriverMapping(int driverId, int pos) {
        HashMap<String, String> params = new HashMap<>();

        params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
        params.put("action_type", "1");
        params.put(Constants.KEY_DRIVER_ID, String.valueOf(driverId));
        new HomeUtil().putDefaultParams(params);

        RestClient.getApiService().deleteUserDriverMapping(params, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                Log.i(TAG, "deleteUserDriverMapping response = " + responseStr);
                try {
                    JSONObject jObj = new JSONObject(responseStr);
                    int flag = jObj.getInt(Constants.KEY_FLAG);

                    if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
                        favouriteDriverlist.remove(pos);
                        notifyDataSetChanged();
                    } else {
                        retryDialog(driverId, pos);
                    }

                } catch (Exception e) {
                    e.printStackTrace();

                }

            }

            @Override
            public void failure(RetrofitError error) {

                Log.e("error", error.getMessage().toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return favouriteDriverlist == null ? 0 : favouriteDriverlist.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivDriverLine, imgVwCrossDriver, imageViewVehicleType;
        TextView tvDriverName, tvVehicleType, tvDriverRating, tvDriverNameValue, tvVehicleTypeValue, tvDriverRatingValue;

        public ViewHolder(View itemView) {
            super(itemView);

            ivDriverLine = (ImageView) itemView.findViewById(R.id.ivDriverLine);
            imageViewVehicleType = (ImageView) itemView.findViewById(R.id.imageViewVehicleType);
            imgVwCrossDriver = (ImageView) itemView.findViewById(R.id.imgVwCrossDriver);
            tvDriverName = (TextView) itemView.findViewById(R.id.tvDriverName);
//            tvDriverName.setTypeface(Fonts.mavenRegular(context));
            tvVehicleType = (TextView) itemView.findViewById(R.id.tvVehicleType);
            tvDriverRating = (TextView) itemView.findViewById(R.id.tvDriverRating);
            tvDriverNameValue = (TextView) itemView.findViewById(R.id.tvDriverNameValue);
            tvVehicleTypeValue = (TextView) itemView.findViewById(R.id.tvVehicleTypeValue);
            tvDriverRatingValue = (TextView) itemView.findViewById(R.id.tvDriverRatingValue);
//            tvVehicleType.setTypeface(Fonts.mavenRegular(context));
//            tvDriverRating.setTypeface(Fonts.mavenRegular(context));
//            tvDriverNameValue.setTypeface(Fonts.mavenRegular(context));
//            tvVehicleTypeValue.setTypeface(Fonts.mavenRegular(context));
//            tvDriverRatingValue.setTypeface(Fonts.mavenRegular(context));


        }
    }

    private void retryDialog(int driverId, int pos) {
        DialogPopup.alertPopupTwoButtonsWithListeners(activity, "", "Something went wrong. Please try again",
                activity.getString(R.string.retry), activity.getString(R.string.cancel),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteDriverMapping(driverId, pos);
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }, true, false);
    }

    private void warningDailog(int driverId, int pos) {

        DialogPopup.alertPopupTwoButtonsWithListeners(activity, "", "Are you sure you want delete your favourite driver",
                activity.getString(R.string.yes), activity.getString(R.string.no),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteDriverMapping(driverId, pos);
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }, true, false);
    }

}

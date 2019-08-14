package product.clicklabs.jugnoo.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.GetFetchUserDriverResponse;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Log;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class BlockedDriversAdapter extends RecyclerView.Adapter<BlockedDriversAdapter.ViewHolderBlockedDriver> {

    private ArrayList<GetFetchUserDriverResponse> blockedDriverlist;
    private Activity activity;
    private String TAG;


    public BlockedDriversAdapter(ArrayList<GetFetchUserDriverResponse> fetchUserDriverResponseArrayList,Activity activity){
        blockedDriverlist = fetchUserDriverResponseArrayList;
        this.activity = activity;
        Log.e("data recycler blocked driver", blockedDriverlist.size()+"");
    }

    @NonNull
    @Override
    public ViewHolderBlockedDriver onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.e("data recycler", "on create view called blocked");
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_blocked_driver, parent, false);
        return new ViewHolderBlockedDriver(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderBlockedDriver holder, int position) {
        Log.i("data",blockedDriverlist.get(position).driverName);

            holder.textViewDriverNameValue.setText(blockedDriverlist.get(position).getDriverName());
            holder.tvVehicleTypeValue.setText(blockedDriverlist.get(position).getVehicleType() + "");
            holder.tvBlockedDriverRatingValue.setText(blockedDriverlist.get(position).getAvgRating() + "");

            holder.imageViewBlockedCross.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    warningDailogBlocked(blockedDriverlist.get(position).getDriverId(), position);
                }
            });

    }



    @Override
    public int getItemCount() {
        return blockedDriverlist.size();
    }

    private void blockedDriverMapping(int driverId, int pos) {
        Log.i(TAG, "deleteUserDriverMapping response = " + "irhfguhrghij");
        HashMap<String, String> params = new HashMap<>();
        params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
        params.put("action_type","0");
        params.put(Constants.KEY_DRIVER_ID,String.valueOf(driverId));
        new HomeUtil().putDefaultParams(params);
        RestClient.getApiService().deleteUserDriverMapping(params, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                Log.i(TAG, "deleteUserDriverMapping response = " + responseStr);
                try{
                    JSONObject jObj = new JSONObject(responseStr);
                    int flag = jObj.getInt(Constants.KEY_FLAG);

                    if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal()==flag){
                        blockedDriverlist.remove(pos);
                        notifyDataSetChanged();
                    }
                    else {
                        retryDialog(driverId,pos);
                    }

                }catch(Exception e){
                    e.printStackTrace();

                }

            }

            @Override
            public void failure(RetrofitError error) {

                Log.e("error", error.getMessage().toString());
            }
        });

    }



    class ViewHolderBlockedDriver extends RecyclerView.ViewHolder {

        TextView tvBlockedDriverName,textViewDriverNameValue,tvBlockedVehicleType,tvVehicleTypeValue,tvBlockedDriverRating,tvBlockedDriverRatingValue;
        ImageView imageViewBlockedCross,imageViewBlockedDriverType;
        public ViewHolderBlockedDriver(View itemView) {
            super(itemView);


            tvBlockedDriverName = (TextView) itemView.findViewById(R.id.tvBlockedDriverName);
            textViewDriverNameValue = (TextView) itemView.findViewById(R.id.textViewDriverNameValue);
            tvBlockedVehicleType = (TextView) itemView.findViewById(R.id.tvBlockedVehicleType);
            tvVehicleTypeValue = (TextView) itemView.findViewById(R.id.tvVehicleTypeValue);
            tvBlockedDriverRating = (TextView) itemView.findViewById(R.id.tvBlockedDriverRating);
            tvBlockedDriverRatingValue = (TextView) itemView.findViewById(R.id.tvBlockedDriverRatingValue);

            imageViewBlockedCross = (ImageView) itemView.findViewById(R.id.imageViewBlockedCross);
            imageViewBlockedDriverType = (ImageView) itemView.findViewById(R.id.imageViewBlockedDriverType);
        }
    }

    private void warningDailogBlocked(int driverId, int pos) {
        DialogPopup.alertPopupTwoButtonsWithListeners(activity, "","Are you sure you want delete your Blocked driver" ,
                activity.getString(R.string.yes), activity.getString(R.string.no),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        blockedDriverMapping(driverId,pos);
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }, true, false);
    }

    private void retryDialog(int driverId,int pos){
        DialogPopup.alertPopupTwoButtonsWithListeners(activity, "","Something went wrong. Please try again" ,
                activity.getString(R.string.retry), activity.getString(R.string.cancel),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        blockedDriverMapping(driverId,pos);
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }, true, false);
    }



}

package product.clicklabs.jugnoo.home;

import android.app.Activity;
import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.home.models.PokestopInfo;
import product.clicklabs.jugnoo.home.models.PokestopTypeValue;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.FindPokestopResponse;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.CustomMapMarkerCreator;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by gurmail on 04/08/16.
 */
public class PokestopHelper {
    private static final String TAG = "PokestopHelper";

    private Context context;
    private GoogleMap map;
    private ASSL assl;

    public PokestopHelper(Context context, GoogleMap googleMap, ASSL assl){
        this.context = context;
        this.map = googleMap;
        this.assl = assl;
    }
    

    public void checkPokestopData(LatLng latLngMapCenter, int cityId) {
        try {
            if (Prefs.with(context).getInt(Constants.KEY_SHOW_POKEMON_DATA, 0) == 1
                    && Prefs.with(context).getInt(Constants.SP_POKESTOP_ENABLED_BY_USER, 0) == 1) {
                long updatedTimestamp = MyApplication.getInstance().getDatabase2().getPokestopDataUpdatedTimestamp(cityId);
                if((System.currentTimeMillis() - updatedTimestamp) > Constants.DAY_MILLIS) {
                    findPokeStop(latLngMapCenter, cityId);
                } else{
                    if(pokestopInfos.size() == 0 || cityIdOfData != cityId){
                        FindPokestopResponse findPokestopResponse = MyApplication.getInstance().getDatabase2().getPokestopData(cityId);
                        showPokestopData((ArrayList<PokestopInfo>) findPokestopResponse.getPokestops(), cityId);
                    }
                }
            } else{
                mapCleared();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Method used to find pokestop and gym
     */
    private void findPokeStop(LatLng latLngMapCenter, final int cityId) {
        try {
            if (MyApplication.getInstance().isOnline()) {
                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.KEY_MAP_CENTER_LATITUDE, String.valueOf(latLngMapCenter.latitude));
                params.put(Constants.KEY_MAP_CENTER_LONGITUDE, String.valueOf(latLngMapCenter.longitude));
                if (HomeActivity.myLocation == null) {
                    params.put(Constants.KEY_LATITUDE, String.valueOf(Data.latitude));
                    params.put(Constants.KEY_LONGITUDE, String.valueOf(Data.longitude));
                } else {
                    params.put(Constants.KEY_LATITUDE, String.valueOf(HomeActivity.myLocation.getLatitude()));
                    params.put(Constants.KEY_LONGITUDE, String.valueOf(HomeActivity.myLocation.getLongitude()));
                }
                params.put(Constants.KEY_CITY_ID, String.valueOf(cityId));


                new HomeUtil().putDefaultParams(params);
                RestClient.getApiService().findPokestop(params, new retrofit.Callback<FindPokestopResponse>() {
                    @Override
                    public void success(FindPokestopResponse findPokestopResponse, Response response) {
                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                        Log.i(TAG, "findpokestop response = " + responseStr);
                        try {
                            if(findPokestopResponse.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()){
                                showPokestopData((ArrayList<PokestopInfo>) findPokestopResponse.getPokestops(), cityId);
                                MyApplication.getInstance().getDatabase2().insertUpdatePokestopData(cityId, responseStr);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        try {
                            Log.e(TAG, "findpokestop error" + error.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<PokestopInfo> pokestopInfos = new ArrayList<>();
    private int cityIdOfData = 0;
    private ArrayList<Marker> markersPokemap = new ArrayList<>();
    private ArrayList<MarkerOptions> markerOptionsPokemap = new ArrayList<>();

    /**
     * Method used to show poke marker on map
     */
    private void showPokestopData(ArrayList<PokestopInfo> pokestopInfos, int cityId) {
        try {
            this.pokestopInfos = pokestopInfos;
            this.cityIdOfData = cityId;
            removePokestopMarkers();
            if(map != null) {
                float zIndex = 0.0f;
                for(PokestopInfo pokestopInfo : pokestopInfos){
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.title(pokestopInfo.getName());
                    markerOptions.snippet("poke_");
                    markerOptions.position(new LatLng(pokestopInfo.getLatitude(), pokestopInfo.getLongitude()));
                    markerOptions.anchor(0.5f, 0.5f);
                    markerOptions.zIndex(zIndex);
                    if(pokestopInfo.getType() == PokestopTypeValue.GYM.getOrdinal()){
                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator
                                .createMarkerBitmapForResource((Activity) context, R.drawable.ic_poke_gym, 42f, 43f)));
                    } else{
                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator
                                .createMarkerBitmapForResource((Activity) context, R.drawable.ic_poke_stop, 39f*0.9f, 40f*0.9f)));
                    }
                    markersPokemap.add(map.addMarker(markerOptions));
                    markerOptionsPokemap.add(markerOptions);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to remove markes
     */
    public void removePokestopMarkers() {
        try {
            markerOptionsPokemap.clear();
            for(Marker marker : markersPokemap){
                marker.remove();
            }
            markersPokemap.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void mapCleared(){
        pokestopInfos.clear();
        cityIdOfData = 0;
        removePokestopMarkers();
    }
    
}

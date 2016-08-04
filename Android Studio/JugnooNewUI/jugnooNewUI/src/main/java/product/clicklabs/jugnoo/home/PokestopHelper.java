package product.clicklabs.jugnoo.home;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.FacebookRegisterData;
import product.clicklabs.jugnoo.home.models.PokestopInfo;
import product.clicklabs.jugnoo.home.models.PokestopTypeValue;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.FindPokestopResponse;
import product.clicklabs.jugnoo.t20.models.Team;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomMapMarkerCreator;
import product.clicklabs.jugnoo.utils.FileUtils;
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
    private FileUtils fileUtils;
    private Gson gson;

    private final String POKE_FILE = "poke_file_";

    public PokestopHelper(Context context, GoogleMap googleMap, ASSL assl){
        this.context = context;
        this.map = googleMap;
        this.assl = assl;
        this.fileUtils = new FileUtils(context);
        this.gson = new Gson();
    }
    

    public void checkPokestopData(LatLng latLngMapCenter, int cityId) {
        try {
            if (Prefs.with(context).getInt(Constants.KEY_SP_POKESTOP_ENABLED, 0) == 1
                    && Prefs.with(context).getInt(Constants.SP_POKESTOP_ENABLED_BY_USER, 0) == 1) {
                File file = fileUtils.getFile(POKE_FILE+cityId);
                String data = fileUtils.readFromFile(file);
                if(TextUtils.isEmpty(data)
                    || ((System.currentTimeMillis() - file.lastModified()) > Constants.DAY_MILLIS)) {
                    findPokeStop(latLngMapCenter, cityId);
                } else{
                    FindPokestopResponse findPokestopResponse = gson.fromJson(data, FindPokestopResponse.class);
                    showPokestopData((ArrayList<PokestopInfo>) findPokestopResponse.getPokestops());
                }
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
            if (AppStatus.getInstance(context).isOnline(context)) {
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
                params.put(Constants.KEY_CITY, String.valueOf(cityId));


                RestClient.getApiServices().findPokestop(params, new retrofit.Callback<FindPokestopResponse>() {
                    @Override
                    public void success(FindPokestopResponse findPokestopResponse, Response response) {
                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                        Log.i(TAG, "findpokestop response = " + responseStr);
                        try {
                            if(findPokestopResponse.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()){
                                showPokestopData((ArrayList<PokestopInfo>) findPokestopResponse.getPokestops());
                                fileUtils.writeToFile(fileUtils.getFile(POKE_FILE+cityId), responseStr);
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
    private ArrayList<Marker> markerArrayListPokemap = new ArrayList<>();

    /**
     * Method used to show poke marker on map
     */
    private void showPokestopData(ArrayList<PokestopInfo> pokestopInfos) {
        try {
            this.pokestopInfos = pokestopInfos;
            removePokestopMarkers();
            if(map != null) {
                for(PokestopInfo pokestopInfo : pokestopInfos){
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.title(pokestopInfo.getName());
                    markerOptions.snippet("");
                    markerOptions.position(new LatLng(pokestopInfo.getLatitude(), pokestopInfo.getLongitude()));
                    markerOptions.anchor(0.5f, 0.5f);
                    if(pokestopInfo.getType() == PokestopTypeValue.GYM.getOrdinal()){
                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator
                                .createMarkerBitmapForResource((Activity) context, assl, R.drawable.alert_icon)));
                    } else{
                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator
                                .createMarkerBitmapForResource((Activity) context, assl, R.drawable.alert_icon)));
                    }
                    markerArrayListPokemap.add(map.addMarker(markerOptions));
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
            if(markerArrayListPokemap.size() > 0){
                for(Marker marker : markerArrayListPokemap){
                    marker.remove();
                }
                markerArrayListPokemap.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}

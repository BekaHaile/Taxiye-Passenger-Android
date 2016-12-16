package product.clicklabs.jugnoo.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoTools;

import java.util.ArrayList;
import java.util.HashMap;

import io.branch.referral.Branch;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.GCMIntentService;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.datastructure.PassengerScreenMode;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.datastructure.UserMode;
import product.clicklabs.jugnoo.home.models.VehicleIconSet;
import product.clicklabs.jugnoo.utils.FacebookLoginHelper;
import product.clicklabs.jugnoo.utils.MapUtils;
import product.clicklabs.jugnoo.utils.Prefs;

/**
 * Created by shankar on 4/3/16.
 */
public class HomeUtil {

	public void checkAndFillParamsForIgnoringAppOpen(Context context, HashMap<String, String> params){
		long currentTime = System.currentTimeMillis();
		long lastPushReceivedTime = Prefs.with(context).getLong(Constants.SP_LAST_PUSH_RECEIVED_TIME, (currentTime + 1));
		long diff = currentTime - lastPushReceivedTime;
		params.put(Constants.KEY_LAST_PUSH_TIME_DIFF, String.valueOf(diff));
	}

	public VehicleIconSet getVehicleIconSet(String name){
		if(VehicleIconSet.YELLOW_AUTO.getName().equalsIgnoreCase(name)){
			return VehicleIconSet.YELLOW_AUTO;
		}
		else if(VehicleIconSet.RED_AUTO.getName().equalsIgnoreCase(name)){
			return VehicleIconSet.RED_AUTO;
		}
		else if(VehicleIconSet.ORANGE_BIKE.getName().equalsIgnoreCase(name)){
			return VehicleIconSet.ORANGE_BIKE;
		}
		else if(VehicleIconSet.YELLOW_BIKE.getName().equalsIgnoreCase(name)){
			return VehicleIconSet.YELLOW_BIKE;
		}
		else if(VehicleIconSet.RED_BIKE.getName().equalsIgnoreCase(name)){
			return VehicleIconSet.RED_BIKE;
		}
		else if(VehicleIconSet.ORANGE_CAR.getName().equalsIgnoreCase(name)){
			return VehicleIconSet.ORANGE_CAR;
		}
		else if(VehicleIconSet.YELLOW_CAR.getName().equalsIgnoreCase(name)){
			return VehicleIconSet.YELLOW_CAR;
		}
		else if(VehicleIconSet.RED_CAR.getName().equalsIgnoreCase(name)){
			return VehicleIconSet.RED_CAR;
		}
		else if(VehicleIconSet.HELICOPTER.getName().equalsIgnoreCase(name)){
			return VehicleIconSet.HELICOPTER;
		}
		else{
			return VehicleIconSet.ORANGE_AUTO;
		}
	}

	public SearchResult getNearBySavedAddress(Context context, LatLng latLng){
		try {
			ArrayList<SearchResult> searchResults = new ArrayList<>();
			if (!Prefs.with(context).getString(SPLabels.ADD_HOME, "").equalsIgnoreCase("")) {
				String homeString = Prefs.with(context).getString(SPLabels.ADD_HOME, "");
				SearchResult searchResult = new Gson().fromJson(homeString, SearchResult.class);
				searchResults.add(searchResult);
			}
			if (!Prefs.with(context).getString(SPLabels.ADD_WORK, "").equalsIgnoreCase("")) {
				String workString = Prefs.with(context).getString(SPLabels.ADD_WORK, "");
				SearchResult searchResult = new Gson().fromJson(workString, SearchResult.class);
				searchResults.add(searchResult);
			}
			searchResults.addAll(Data.userData.getSearchResults());

			double distance = Double.MAX_VALUE;
			SearchResult selectedNearByAddress = null;
			for(int i=0; i<searchResults.size(); i++){
				double fetchedDistance = MapUtils.distance(latLng, searchResults.get(i).getLatLng());
				if ((fetchedDistance < 100) && (fetchedDistance < distance)) {
					distance = fetchedDistance;
					selectedNearByAddress = searchResults.get(i);
				}
			}

			return selectedNearByAddress;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void logoutFunc(Activity activity, String message){
		try {
			PicassoTools.clearCache(Picasso.with(activity));
		} catch (Exception e) {
			e.printStackTrace();
		}

		FacebookLoginHelper.logoutFacebook();

		GCMIntentService.clearNotifications(activity);

		Data.clearDataOnLogout(activity);

		HomeActivity.userMode = UserMode.PASSENGER;
		HomeActivity.passengerScreenMode = PassengerScreenMode.P_INITIAL;

		ActivityCompat.finishAffinity(activity);
		Intent intent = new Intent(activity, SplashNewActivity.class);
		if(message != null){
			intent.putExtra(Constants.KEY_LOGGED_OUT, 1);
			intent.putExtra(Constants.KEY_MESSAGE, message);
		}
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);

		Branch.getInstance(activity).logout();
	}

}

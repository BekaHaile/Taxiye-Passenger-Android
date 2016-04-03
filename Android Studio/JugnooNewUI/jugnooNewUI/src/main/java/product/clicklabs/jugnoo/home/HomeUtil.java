package product.clicklabs.jugnoo.home;

import android.content.Context;

import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.home.models.VehicleIconSet;
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
		if(VehicleIconSet.BIKE.getName().equalsIgnoreCase(name)){
			return VehicleIconSet.BIKE;
		} else{
			return VehicleIconSet.AUTO;
		}
	}

}

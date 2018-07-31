package product.clicklabs.jugnoo.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

import com.google.android.gms.location.LocationResult;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.LocationFetcher;
import product.clicklabs.jugnoo.utils.Utils;

public class LocationReceiverBG extends BroadcastReceiver {

	@Override
    public void onReceive(final Context context, Intent intent) {
        try {
            if(LocationResult.hasResult(intent)) {
                LocationResult locationResult = LocationResult.extractResult(intent);
                final Location location = locationResult.getLastLocation();
//                final Location location = (Location) intent.getExtras().get(LocationServices.FusedLocationApi.KEY_LOCATION_CHANGED);
                if (location != null && !Utils.mockLocationEnabled(location)) {
                    LocationFetcher.saveLatLngToSP(context, location.getLatitude(), location.getLongitude());
                    Intent intent1 = new Intent();
                    intent1.setAction(Constants.ACTION_LOCATION_UPDATE);
                    intent1.putExtra(Constants.KEY_LATITUDE, location.getLatitude());
                    intent1.putExtra(Constants.KEY_LONGITUDE, location.getLongitude());
                    context.sendBroadcast(intent1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
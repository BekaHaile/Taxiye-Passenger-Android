package product.clicklabs.jugnoo.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

import com.google.android.gms.location.LocationServices;

import product.clicklabs.jugnoo.LocationFetcher;
import product.clicklabs.jugnoo.utils.Log;

public class LocationReceiverBG extends BroadcastReceiver {

	@Override
    public void onReceive(final Context context, Intent intent) {
		final Location location = (Location) intent.getExtras().get(LocationServices.FusedLocationApi.KEY_LOCATION_CHANGED);
        if(location != null) {
            LocationFetcher.saveLatLngToSP(context, location.getLatitude(), location.getLongitude());
            Log.i("LocationReceiverBG", "onReceive location=" + location);
        }
    }

}
package product.clicklabs.jugnoo.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

import com.google.android.gms.location.LocationServices;

import product.clicklabs.jugnoo.LocationFetcher;
import product.clicklabs.jugnoo.sticky.GenieService;

public class LocationReceiver extends BroadcastReceiver {

	@Override
    public void onReceive(final Context context, Intent intent) {
		final Location location = (Location) intent.getExtras().get(LocationServices.FusedLocationApi.KEY_LOCATION_CHANGED);
        if(location != null && !Utils.mockLocationEnabled(location)) {
            LocationFetcher.saveLatLngToSP(context, location.getLatitude(), location.getLongitude());
            Intent serviceIntent = new Intent(context, GenieService.class);
            serviceIntent.putExtra("latitude", location.getLatitude());
            serviceIntent.putExtra("longitude", location.getLongitude());
            context.startService(serviceIntent);
        }
    }

}
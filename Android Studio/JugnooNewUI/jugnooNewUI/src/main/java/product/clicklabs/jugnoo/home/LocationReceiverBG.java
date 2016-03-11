package product.clicklabs.jugnoo.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

import com.google.android.gms.location.LocationServices;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.LocationFetcher;

public class LocationReceiverBG extends BroadcastReceiver {

	@Override
    public void onReceive(final Context context, Intent intent) {
		final Location location = (Location) intent.getExtras().get(LocationServices.FusedLocationApi.KEY_LOCATION_CHANGED);
        if(location != null) {
            LocationFetcher.saveLatLngToSP(context, location.getLatitude(), location.getLongitude());
            Intent intent1 = new Intent();
            intent1.setAction(Constants.ACTION_LOCATION_UPDATE);
            intent1.putExtra(Constants.KEY_LATITUDE, location.getLatitude());
            intent1.putExtra(Constants.KEY_LONGITUDE, location.getLongitude());
            context.sendBroadcast(intent1);
        }
    }

}
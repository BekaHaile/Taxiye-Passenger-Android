package product.clicklabs.jugnoo.utils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.location.LocationServices;

import product.clicklabs.jugnoo.LocationFetcher;
import product.clicklabs.jugnoo.sticky.GenieService;

public class LocationReceiver extends BroadcastReceiver {

	@Override
    public void onReceive(final Context context, Intent intent) {
		final Location location = (Location) intent.getExtras().get(LocationServices.FusedLocationApi.KEY_LOCATION_CHANGED);
        if(location != null) {
            LocationFetcher.saveLatLngToSP(context, location.getLatitude(), location.getLongitude());
            Intent serviceIntent = new Intent(context, GenieService.class);
            serviceIntent.putExtra("latitude", location.getLatitude());
            serviceIntent.putExtra("longitude", location.getLongitude());
            context.startService(serviceIntent);
        }
    }

}
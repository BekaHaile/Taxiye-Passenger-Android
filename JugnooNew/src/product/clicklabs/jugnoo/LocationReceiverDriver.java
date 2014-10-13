package product.clicklabs.jugnoo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.model.LatLng;

public class LocationReceiverDriver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
    	final Location location = (Location) intent.getExtras().get(LocationClient.KEY_LOCATION_CHANGED);
    	Database2 database2 = new Database2(context);
    	database2.insertDriverCurrentLocation(new LatLng(location.getLatitude(), location.getLongitude()));
    	database2.close();
    	
//    	new Thread(new Runnable() {
//			@Override
//			public void run() {
//				new DriverLocationDispatcher().sendLocationToServer(context);
//			}
//		}).start();
    }
    
}
package product.clicklabs.jugnoo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.widget.Toast;

import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.model.LatLng;

public class LocationReceiverDriver extends BroadcastReceiver implements DisplayToast{

    @Override
    public void onReceive(final Context context, Intent intent) {
    	final Location location = (Location) intent.getExtras().get(LocationClient.KEY_LOCATION_CHANGED);
    	new Thread(new Runnable() {
			@Override
			public void run() {
				Database2 database2 = new Database2(context);
		    	database2.updateDriverCurrentLocation(new LatLng(location.getLatitude(), location.getLongitude()));
		    	database2.close();
		    	Log.e("DriverLocationUpdateService location in pi reciever ", "=="+location);
		    	Log.writeLogToFile("LocationReciever", "Receiver "+new DateOperations().getCurrentTime()+" = "+location 
		    			+ " hasNet = "+AppStatus.getInstance(context).isOnline(context));
				new DriverLocationDispatcher().sendLocationToServer(LocationReceiverDriver.this, context, "LocationReciever");
			}
		}).start();
    }
    
    @Override
	public void displayToast(final Context context, final String text) {
    	// Get a handler that can be used to post to the main thread
    	Handler mainHandler = new Handler(context.getMainLooper());
    	Runnable myRunnable = new Runnable() {
			@Override
			public void run() {
				Toast.makeText(context, text, Toast.LENGTH_LONG).show();
			}
		};
    	mainHandler.post(myRunnable);
	}
    
}
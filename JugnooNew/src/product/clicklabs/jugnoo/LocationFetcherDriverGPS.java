package product.clicklabs.jugnoo;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

public class LocationFetcherDriverGPS {
	
	public LocationManager locationManager;
	public Context context;
	private long requestInterval;
	private PendingIntent locationIntent;
	
	
	private static final int LOCATION_PI_ID = 69781;
	
	
	
	/**
	 * Constructor for initializing LocationFetcher class' object
	 * @param context application context
	 */
	public LocationFetcherDriverGPS(Context context, long requestInterval){
		this.context = context;
		this.requestInterval = requestInterval;
		connect();
	}
	

	public void connect(){
		try {
			this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				Intent intent = new Intent(context, LocationReceiverDriverGPS.class);
				locationIntent = PendingIntent.getBroadcast(context, LOCATION_PI_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, requestInterval, 0, locationIntent);
				
			} else {
				if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
					Intent intent = new Intent(context, LocationReceiverDriverGPS.class);
					locationIntent = PendingIntent.getBroadcast(context, LOCATION_PI_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
					locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, requestInterval, 0, locationIntent);
				}
			}
			Log.e("LocationFetcherDriverGPS", "connected");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public void destroy(){
		try{
			locationManager.removeUpdates(locationIntent);
			locationIntent.cancel();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		Log.e("LocationFetcherDriverGPS", "destroyed");
	}

}


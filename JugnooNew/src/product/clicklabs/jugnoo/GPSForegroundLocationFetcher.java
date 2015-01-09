package product.clicklabs.jugnoo;

import android.content.ContentResolver;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

public class GPSForegroundLocationFetcher implements LocationListener{
	
	private LocationManager locationManager;
	private Context context;
	private GPSLocationUpdate gpsLocationUpdate;
	private long requestInterval;
	
	private Location location;
	
	private Handler checkLocationUpdateStartedHandler;
	private Runnable checkLocationUpdateStartedRunnable;

	private static final long CHECK_LOCATION_INTERVAL = 20000, LAST_LOCATON_TIME_THRESHOLD = 2 * 60000;
	
	public GPSForegroundLocationFetcher(GPSLocationUpdate gpsLocationUpdate, long requestInterval){
		this.context = (Context) gpsLocationUpdate;
		this.gpsLocationUpdate = gpsLocationUpdate;
		this.requestInterval = requestInterval;
	}
	
	
	
	
	/**
	 * Checks if location fetching is enabled in device or not
	 * @param context application context
	 * @return true if any location provider is enabled else false
	 */
	private synchronized boolean isLocationEnabled(Context context) {
		try{
			ContentResolver contentResolver = context.getContentResolver();
			boolean gpsStatus = Settings.Secure.isLocationProviderEnabled(contentResolver, LocationManager.GPS_PROVIDER);
			boolean netStatus = Settings.Secure.isLocationProviderEnabled(contentResolver, LocationManager.NETWORK_PROVIDER);
			return gpsStatus || netStatus;
		} catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	
	public synchronized void connect(){
		destroy();
		Log.e("GPS", "connect");
		if(isLocationEnabled(context)){
			if(locationManager != null){
				locationManager.removeUpdates(this);
			}
			this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, this.requestInterval, 0, this);
		}
		startCheckingLocationUpdates();
	}
	
	public synchronized void destroyWaitAndConnect(){
		Log.e("destroyWaitAndConnect", "=");
		destroy();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				connect();
			}
		}, 2000);
	}
	
	public synchronized void destroy(){
		Log.e("GPS", "destroy");
		try{
			this.location = null;
			if(locationManager != null){
				locationManager.removeUpdates(this);
			}
		} catch(Exception e){
			
		}
		stopCheckingLocationUpdates();
	}
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
	
	@Override
	public void onProviderEnabled(String provider) {
	}
	
	@Override
	public void onProviderDisabled(String provider) {
		this.location = null;
	}
	
	@Override
	public void onLocationChanged(Location location) {
		try{
			if(location!=null){
				Log.i("loc chanfged ________---------******","="+location);
				this.location = location;
				gpsLocationUpdate.onGPSLocationChanged(location);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private synchronized void startCheckingLocationUpdates(){
		checkLocationUpdateStartedHandler = new Handler();
		checkLocationUpdateStartedRunnable = new Runnable() {
			@Override
			public void run() {
				if(GPSForegroundLocationFetcher.this.location == null){
					destroyWaitAndConnect();
				}
				else{
					long timeSinceLastLocationFix = System.currentTimeMillis() - GPSForegroundLocationFetcher.this.location.getTime();
					if(timeSinceLastLocationFix > LAST_LOCATON_TIME_THRESHOLD){
						destroyWaitAndConnect();
					}
					else{
						checkLocationUpdateStartedHandler.postDelayed(checkLocationUpdateStartedRunnable, CHECK_LOCATION_INTERVAL);
					}
				}
			}
		};
		checkLocationUpdateStartedHandler.postDelayed(checkLocationUpdateStartedRunnable, CHECK_LOCATION_INTERVAL);
	}
	
	public synchronized void stopCheckingLocationUpdates(){
		try{
			if(checkLocationUpdateStartedHandler != null && checkLocationUpdateStartedRunnable != null){
				checkLocationUpdateStartedHandler.removeCallbacks(checkLocationUpdateStartedRunnable);
			}
		} catch(Exception e){
			e.printStackTrace();
		} finally{
			checkLocationUpdateStartedHandler = null;
			checkLocationUpdateStartedRunnable = null;
		}
	}
	
}

package product.clicklabs.jugnoo;

import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.Log;
import android.content.ContentResolver;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;

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
		connect();
	}
	
	/**
	 * Checks if location fetching is enabled in device or not
	 * @param context application context
	 * @return true if any location provider is enabled else false
	 */
	private boolean isLocationEnabled(Context context) {
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
	
	
	public void connect(){
		destroy();
		if(isLocationEnabled(context)){
			this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, this.requestInterval, 0, this);
		}
		startCheckingLocationUpdates();
	}
	
	public void destroyWaitAndConnect(){
		destroy();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				connect();
			}
		}, 2000);
	}
	
	public void destroy(){
		try{
			this.location = null;
			if(locationManager != null){
				locationManager.removeUpdates(this);
			}
		} catch(Exception e){
			
		} finally{
			locationManager = null;
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
				Log.i("GPS loc chanfged ________---------******","="+location);
				this.location = location;
				gpsLocationUpdate.onGPSLocationChanged(location);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void startCheckingLocationUpdates(){
		checkLocationUpdateStartedHandler = new Handler();
		checkLocationUpdateStartedRunnable = new Runnable() {
			@Override
			public void run() {
				Log.i("GPSForegroundLocationFetcher.this.location in handler runnable", "=="+GPSForegroundLocationFetcher.this.location);
				FlurryEventLogger.locationLog(GPSForegroundLocationFetcher.this.location);
				if(GPSForegroundLocationFetcher.this.location == null){
					destroyWaitAndConnect();
					FlurryEventLogger.locationRestart("null location");
				}
				else{
					long timeSinceLastLocationFix = System.currentTimeMillis() - GPSForegroundLocationFetcher.this.location.getTime();
					if(timeSinceLastLocationFix > LAST_LOCATON_TIME_THRESHOLD){
						destroyWaitAndConnect();
						FlurryEventLogger.locationRestart("old location");
					}
					else{
						checkLocationUpdateStartedHandler.postDelayed(checkLocationUpdateStartedRunnable, CHECK_LOCATION_INTERVAL);
					}
				}
			}
		};
		checkLocationUpdateStartedHandler.postDelayed(checkLocationUpdateStartedRunnable, CHECK_LOCATION_INTERVAL);
	}
	
	public void stopCheckingLocationUpdates(){
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

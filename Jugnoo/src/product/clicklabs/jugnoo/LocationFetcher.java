package product.clicklabs.jugnoo;


import android.content.ContentResolver;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

public class LocationFetcher {

	public boolean locationChanged;
	private Location location; // location
	double latitude = 30.7500; // latitude default chandigarh latlng
	double longitude = 76.7800; // longitude
	
	public MyLocationListener gpsListener, networkListener;
	
	public LocationManager locationManager;
	public int whichProvider;
	public String provider;
	
	/**
	 * Initialize location fetcher object with selected listeners
	 * @param context
	 * @param whichProvider 0 for network, 1 for GPS and 2 for both
	 */
	public LocationFetcher(Context context, int whichProvider){
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		this.whichProvider = whichProvider;
		this.locationChanged = false;
		
		if(whichProvider == 0){
			if(isNetworkLocationEnabled(context)){
				networkListener = new MyLocationListener();
				provider = LocationManager.NETWORK_PROVIDER;
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 0, networkListener);
			}
		}
		else if(whichProvider == 1){
			if(isGPSLocationEnabled(context)){
				gpsListener = new MyLocationListener();
				provider = LocationManager.GPS_PROVIDER;
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 0, gpsListener);
			}
		}
		else if(whichProvider == 2){
			if(isNetworkLocationEnabled(context) || isGPSLocationEnabled(context)){
				gpsListener = new MyLocationListener();
				networkListener = new MyLocationListener();
				
				if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
					provider = LocationManager.GPS_PROVIDER;
					locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 0, gpsListener);
				}
				
				if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
					provider = LocationManager.NETWORK_PROVIDER;
					locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 0, networkListener);
				}
				
			}
		}
	}
	
	
	
	/**
	 * Checks if location fetching through network is enabled in device or not
	 * @param context application context
	 * @return true if network location provider is enabled else false
	 */
	public boolean isNetworkLocationEnabled(Context context) {
		try{
			ContentResolver contentResolver = context.getContentResolver();
			boolean netStatus = Settings.Secure.isLocationProviderEnabled(contentResolver, LocationManager.NETWORK_PROVIDER);
			return netStatus;
		} catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Checks if location fetching through gps is enabled in device or not
	 * @param context application context
	 * @return true if gps location provider is enabled else false
	 */
	public boolean isGPSLocationEnabled(Context context) {
		try{
			ContentResolver contentResolver = context.getContentResolver();
			boolean gpsStatus = Settings.Secure.isLocationProviderEnabled(contentResolver, LocationManager.GPS_PROVIDER);
			return gpsStatus;
		} catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	
	
	
	/**
	 * Function to get latitude
	 * */
	public double getLatitude(){
		try{
		if(location == null){
			Log.e("getLatitude", "location == null");
			location = locationManager.getLastKnownLocation(provider);
			if(location != null){
				latitude = location.getLatitude();
			}
		}else{
			Log.e("getLatitude", "location == not null");
			latitude = location.getLatitude();
		}
		
		} catch(Exception e){Log.e("e","="+e.toString());}
		
		return latitude;
	}
	
	/**
	 * Function to get longitude
	 * */
	public double getLongitude(){
		try{
		if(location == null){
			location = locationManager.getLastKnownLocation(provider);
			if(location != null){
				longitude = location.getLatitude();
				Log.e("last location","="+longitude);
			}
			
		}
		else{
			longitude = location.getLongitude();
		}
		} catch(Exception e){Log.e("e","="+e.toString());}
		
		return longitude;
	}
	
	

	
	public void destroy(){
		try{
			Log.e("location","destroy gps");
			locationManager.removeUpdates(gpsListener);
		}catch(Exception e){
			Log.e("e", "="+e.toString());
		}
		try{
			Log.e("location","destroy network");
			locationManager.removeUpdates(networkListener);
		}catch(Exception e){
			Log.e("e", "="+e.toString());
		}
	}

	
	

	class MyLocationListener implements LocationListener {

		public void onLocationChanged(Location loc) {
			if(isBetterLocation(loc, LocationFetcher.this.location)){
				Log.e("**************************************", "Location changed "+loc);
				LocationFetcher.this.location = loc;
				LocationFetcher.this.locationChanged = true;
			}
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
			LocationFetcher.this.provider = provider;
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
		
		
		/**
		 * Checks if the new location is accurate enough or not
		 * @param location latest location
		 * @param currentBestLocation last accurate location
		 * @return
		 */
		protected boolean isBetterLocation(Location location, Location currentBestLocation) {
			int OLD_LOCATION_THRESHOLD = 1000 * 60 * 2;
			if (currentBestLocation == null) {
				// A new location is always better than no location
				return true;
			}

			// Check whether the new location fix is newer or older
			long timeDelta = location.getTime() - currentBestLocation.getTime();
			boolean isSignificantlyNewer = timeDelta > OLD_LOCATION_THRESHOLD;
			boolean isSignificantlyOlder = timeDelta < -OLD_LOCATION_THRESHOLD;
			boolean isNewer = timeDelta > 0;

			// If it's been more than two minutes since the current location, use
			// the new location
			// because the user has likely moved
			if (isSignificantlyNewer) {
				return true;
				// If the new location is more than two minutes older, it must be
				// worse
			} else if (isSignificantlyOlder) {
				return false;
			}

			// Check whether the new location fix is more or less accurate
			int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
			boolean isLessAccurate = accuracyDelta > 0;
			boolean isMoreAccurate = accuracyDelta < 0;
			boolean isSignificantlyLessAccurate = accuracyDelta > 200;

			// Check if the old and new location are from the same provider
			boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());

			// Determine location quality using a combination of timeliness and
			// accuracy
			if (isMoreAccurate) {
				return true;
			} else if (isNewer && !isLessAccurate) {
				return true;
			} else if (isNewer && !isSignificantlyLessAccurate
					&& isFromSameProvider) {
				return true;
			}
			return false;
		}

		/** Checks whether two providers are the same */
		private boolean isSameProvider(String provider1, String provider2) {
			if (provider1 == null) {
				return provider2 == null;
			}
			return provider1.equals(provider2);
		}
		
	}

	
	
	
	
}
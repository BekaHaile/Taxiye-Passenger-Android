package product.clicklabs.jugnoo;


import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class LocationFetcher1 {

	public Location location; // location
	
	public MyLocationListener gpsListener, networkListener;
	public Context context;
	public LocationManager locationManager;
	public int whichProvider;
	public String provider;
	
	private String LOCATION_SP = "location_sp1",
			LOCATION_LAT = "location_lat",
			LOCATION_LNG = "location_lng";
	
	/**
	 * Initialize location fetcher object with selected listeners
	 * @param context
	 * @param whichProvider 0 for network, 1 for GPS, 2 for both
	 */
	public LocationFetcher1(Context context, int whichProvider){
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		this.whichProvider = whichProvider;
		this.provider = "no";
		this.context = context;
		
		if(whichProvider == 0){
			if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
				gpsListener = new MyLocationListener();
				provider = LocationManager.GPS_PROVIDER;
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 0, gpsListener);
			}
//			if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
//				networkListener = new MyLocationListener();
//				provider = LocationManager.NETWORK_PROVIDER;
//				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30000, 0, networkListener);
//			}
//			else{
//				if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
//					gpsListener = new MyLocationListener();
//					provider = LocationManager.GPS_PROVIDER;
//					locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 0, gpsListener);
//				}
//			}
		}
		else if(whichProvider == 1){
			if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
				gpsListener = new MyLocationListener();
				provider = LocationManager.GPS_PROVIDER;
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, gpsListener);
			}
			else{
				if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
					networkListener = new MyLocationListener();
					provider = LocationManager.NETWORK_PROVIDER;
					locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, networkListener);
				}
			}
		}
		else if(whichProvider == 2) {
			if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
				gpsListener = new MyLocationListener();
				provider = LocationManager.GPS_PROVIDER;
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, gpsListener);
			}
			if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
				networkListener = new MyLocationListener();
				provider = LocationManager.NETWORK_PROVIDER;
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, networkListener);
			}
		}
			
		
		
	}
	
	
	public void saveLatLngToSP(double latitude, double longitude){
		SharedPreferences preferences = context.getSharedPreferences(LOCATION_SP, 0);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(LOCATION_LAT, ""+latitude);
		editor.putString(LOCATION_LNG, ""+longitude);
		editor.commit();
	}
	
	
	public double getSavedLatFromSP(){
		SharedPreferences preferences = context.getSharedPreferences(LOCATION_SP, 0);
		String latitude = preferences.getString(LOCATION_LAT, ""+ 0);
		Log.d("saved last lat", "=="+latitude);
		return Double.parseDouble(latitude);
	}
	
	public double getSavedLngFromSP(){
		SharedPreferences preferences = context.getSharedPreferences(LOCATION_SP, 0);
		String longitude = preferences.getString(LOCATION_LNG, ""+0);
		return Double.parseDouble(longitude);
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
			if(location != null){
				Log.d("loc not null","="+location);
				return location.getLatitude();
			}
		} catch(Exception e){Log.e("e","="+e.toString());}
		return getSavedLatFromSP();
	}
	
	/**
	 * Function to get longitude
	 * */
	public double getLongitude(){
		try{
			if(location != null){
				return location.getLongitude();
			}
		} catch(Exception e){Log.e("e","="+e.toString());}
		return getSavedLngFromSP();
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
			if(isBetterLocation(loc, LocationFetcher1.this.location)){
				Log.e("**************************************", "Location changed "+loc);
				LocationFetcher1.this.location = loc;
				LocationFetcher1.this.provider = loc.getProvider();
				saveLatLngToSP(location.getLatitude(), location.getLongitude());
				Toast.makeText(context, "location changed = "+location, Toast.LENGTH_SHORT).show();
			}
		}

		public void onProviderDisabled(String provider) {
			LocationFetcher1.this.provider = "no";
		}

		public void onProviderEnabled(String provider) {
			LocationFetcher1.this.provider = provider;
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
				if(location.getAccuracy() < 100){
					return true;
				}
				else{
					return false;
				}
			}

			// Check whether the new location fix is newer or older
			long timeDelta = location.getTime() - currentBestLocation.getTime();
			boolean isSignificantlyNewer = timeDelta > OLD_LOCATION_THRESHOLD;
			boolean isSignificantlyOlder = timeDelta < -OLD_LOCATION_THRESHOLD;
			boolean isNewer = timeDelta > 0;

//			Log.i("timeDelta", "="+timeDelta);
			
			

			// Check whether the new location fix is more or less accurate
			int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
			boolean isLessAccurate = accuracyDelta > 0;
			boolean isMoreAccurate = accuracyDelta < 0;
			boolean isSignificantlyLessAccurate = accuracyDelta > 100;

			if(location.getAccuracy() > 100){
				return false;
			}
			
			// If it's been more than two minutes since the current location, use
			// the new location because the user has likely moved
			if (isSignificantlyNewer) {
				return true;
				// If the new location is more than two minutes older, it must
				// be worse
			} else if (isSignificantlyOlder) {
				return false;
			}
			
//			Log.i("accuracyDelta", "="+accuracyDelta);

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
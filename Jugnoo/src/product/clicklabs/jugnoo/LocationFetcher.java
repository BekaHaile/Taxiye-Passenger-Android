package product.clicklabs.jugnoo;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class LocationFetcher implements GooglePlayServicesClient.ConnectionCallbacks,GooglePlayServicesClient.OnConnectionFailedListener,LocationListener {
	
	private final String TAG = this.getClass().getSimpleName();
	private LocationClient locationclient;
	private LocationRequest locationrequest;
	private Location location;
	
	private long requestInterval;
	private LocationUpdate locationUpdate;
	private Context context;
	
	
	private String LOCATION_SP = "location_sp",
			LOCATION_LAT = "location_lat",
			LOCATION_LNG = "location_lng";
	
	public int priority;
	
	public LocationFetcher(LocationUpdate locationUpdate, long requestInterval, int priority){
		this.locationUpdate = locationUpdate;
		this.context = (Context) locationUpdate;
		this.requestInterval = requestInterval;
		this.priority = priority;
		int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
		if(resp == ConnectionResult.SUCCESS){														// google play services working
			if(isLocationEnabled(context)){															// location fetching enabled
				locationclient = new LocationClient(context, this, this);
				locationclient.connect();
			}
			else{																					// location disabled
			}
		}
		else{																						// google play services not working
			Log.e("Google Play Service Error ","="+resp);
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
	 * Checks if location fetching is enabled in device or not
	 * @param context application context
	 * @return true if any location provider is enabled else false
	 */
	public boolean isLocationEnabled(Context context) {
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
			Log.e("location","destroy");
			if(locationclient!=null){
				if(locationclient.isConnected()){
					locationclient.removeLocationUpdates(this);
					locationclient.disconnect();
				}
				else if(locationclient.isConnecting()){
					locationclient.disconnect();
				}
			}
		}catch(Exception e){
			Log.e("e", "="+e.toString());
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		Log.e(TAG, "onConnected");
		
		locationrequest = LocationRequest.create();
		
		if(priority == 0){
			locationrequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
		}
		else if(priority == 1){
			locationrequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
		}
		else if(priority == 2){
			locationrequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		}
		
		locationrequest.setFastestInterval(requestInterval);
		locationrequest.setInterval(requestInterval);
		
		locationclient.requestLocationUpdates(locationrequest, LocationFetcher.this);
	}

	@Override
	public void onDisconnected() {
		Log.e(TAG, "onDisconnected");
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.e(TAG, "onConnectionFailed");

	}

	@Override
	public void onLocationChanged(Location location) {
		try{
			if(location!=null){
				this.location = location;
				locationUpdate.onLocationChanged(location, priority);
				saveLatLngToSP(location.getLatitude(), location.getLongitude());
			}
		}catch(Exception e){
			e.printStackTrace();
		}

	}


}
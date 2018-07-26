package product.clicklabs.jugnoo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;


public class LocationFetcher {

	private LocationRequest locationrequest;
	private FusedLocationProviderClient mFusedLocationClient;
	private LocationCallback mLocationCallback;


	private final String TAG = this.getClass().getSimpleName();
	private Location location, locationUnchecked;

	private LocationUpdate locationUpdate;
	private Activity context;
	private boolean connected;


	private static final String LOCATION_SP = "location_sp",
			LOCATION_LAT = "location_lat",
			LOCATION_LNG = "location_lng";

	public LocationFetcher(Activity context) {
		this.context = context;
		mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
		createLocationCallback();
	}

	public synchronized void connect(LocationUpdate locationUpdate, long requestInterval) {

		if(connected)return;
		this.locationUpdate = locationUpdate;
		destroy();
		GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
		int resp = googleApiAvailability.isGooglePlayServicesAvailable(context);
		if (resp == ConnectionResult.SUCCESS) {                                                        // google play services working
			createLocationRequest(requestInterval);
			startLocationUpdates(context);
		} else {                                                                                        // google play services not working
			Log.e("Google Play error", "=" + resp);
		}
	}

	public static void saveLatLngToSP(Context context, double latitude, double longitude) {
		SharedPreferences preferences = context.getSharedPreferences(LOCATION_SP, 0);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(LOCATION_LAT, "" + latitude);
		editor.putString(LOCATION_LNG, "" + longitude);
		editor.apply();
	}


	public static double getSavedLatFromSP(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(LOCATION_SP, 0);
		String latitude = preferences.getString(LOCATION_LAT, "" + Data.getIndiaCentre().latitude);
		return Double.parseDouble(latitude);
	}

	public static double getSavedLngFromSP(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(LOCATION_SP, 0);
		String longitude = preferences.getString(LOCATION_LNG, "" + Data.getIndiaCentre().longitude);
		return Double.parseDouble(longitude);
	}


	private void createLocationRequest(long interval) {
		locationrequest = new LocationRequest();
		locationrequest.setInterval(interval);
		locationrequest.setFastestInterval(interval / 2);
		locationrequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	}


	private synchronized void startLocationUpdates(Activity context) {
		if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			return;
		}

		mFusedLocationClient.getLastLocation()
				.addOnSuccessListener(context, new OnSuccessListener<Location>() {
					@Override
					public void onSuccess(Location location) {
						if (location != null) {
							Bundle bundle = location.getExtras();
							if(bundle == null){
								bundle = new Bundle();
							}
							bundle.putBoolean("cached", true);
							location.setExtras(bundle);
							onLocationChanged(location);
						}
					}
				});

		mFusedLocationClient.requestLocationUpdates(locationrequest,
				mLocationCallback, Looper.myLooper()).addOnCompleteListener(new OnCompleteListener<Void>() {
			@Override
			public void onComplete(@NonNull Task<Void> task) {
				connected = true;
			}
		});
    }


	/**
	 * Function to get latitude
	 * */
	public double getLatitude(){
		try{
			Location loc = getLocation();
			if(loc != null){
				return loc.getLatitude();
			}
		} catch(Exception e){Log.e("e", "=" + e.toString());}
		return getSavedLatFromSP(context);
	}
	
	/**
	 * Function to get longitude
	 * */
	public double getLongitude(){
		try{
			Location loc = getLocation();
			if(loc != null){
				return loc.getLongitude();
			}
		} catch(Exception e){Log.e("e", "=" + e.toString());}
		return getSavedLngFromSP(context);
	}

	private Location getLocation(){
		try{
			if(location != null){
				return location;
			}
			else{

			}
		} catch(Exception e){e.printStackTrace();}
		return null;
	}
	




	public synchronized void destroy(){
		try{
			this.location = null;
//			Log.e("location", "destroy");
			mFusedLocationClient.removeLocationUpdates(mLocationCallback)
					.addOnCompleteListener(context, new OnCompleteListener<Void>() {
						@Override
						public void onComplete(@NonNull Task<Void> task) {
							connected = false;
						}
					});
		}catch(Exception e){
			Log.e("e", "=" + e.toString());
		}
	}









	public void onLocationChanged(Location location) {
		try{
			Log.i(TAG, "onLocationChanged>"+location);
			locationUnchecked = location;
			if(location != null && !Utils.mockLocationEnabled(location)) {
				this.location = location;
				if(locationUpdate != null) {
					locationUpdate.onLocationChanged(location);
				}
				saveLatLngToSP(context, location.getLatitude(), location.getLongitude());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}


	public Location getLocationUnchecked(){
		try{
			return locationUnchecked;
		} catch(Exception e){e.printStackTrace();}
		return null;
	}

	private void createLocationCallback() {
		mLocationCallback = new LocationCallback() {
			@Override
			public void onLocationResult(LocationResult locationResult) {
				super.onLocationResult(locationResult);
				onLocationChanged(locationResult.getLastLocation());
			}
		};
	}


	public boolean isConnected() {
		return connected;
	}
}
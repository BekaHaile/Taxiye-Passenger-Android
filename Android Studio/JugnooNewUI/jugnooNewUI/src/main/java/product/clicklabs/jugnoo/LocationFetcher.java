package product.clicklabs.jugnoo;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;


public class LocationFetcher implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient googleApiClient;
    private LocationRequest locationrequest;



	private final String TAG = this.getClass().getSimpleName();
	private Location location, locationUnchecked;
	
	private long requestInterval;
	private LocationUpdate locationUpdate;
	private Context context;

	
	private static String LOCATION_SP = "location_sp",
			LOCATION_LAT = "location_lat",
			LOCATION_LNG = "location_lng";
	
	private Handler checkLocationUpdateStartedHandler;
	private Runnable checkLocationUpdateStartedRunnable;

	private static final long CHECK_LOCATION_INTERVAL = 20000, LAST_LOCATON_TIME_THRESHOLD = 2 * 60000;

	public LocationFetcher(LocationUpdate locationUpdate, long requestInterval) {
		this.locationUpdate = locationUpdate;
		this.context = (Context) locationUpdate;
		this.requestInterval = requestInterval;
		connect();
	}

	public LocationFetcher(Context context, LocationUpdate locationUpdate, long requestInterval){
		this.locationUpdate = locationUpdate;
		this.context = context;
		this.requestInterval = requestInterval;
		connect();
	}
	
	public synchronized void connect(){
		destroy();
		int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
		if(resp == ConnectionResult.SUCCESS){														// google play services working
			buildGoogleApiClient(context);
		}
		else{																						// google play services not working
			Log.e("Google Play error", "=" + resp);
		}
		startCheckingLocationUpdates();
	}
	
	public synchronized void destroyWaitAndConnect(){
		destroy();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				connect();
			}
		}, 2000);
	}
	
	public static synchronized void saveLatLngToSP(Context context, double latitude, double longitude){
		SharedPreferences preferences = context.getSharedPreferences(LOCATION_SP, 0);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(LOCATION_LAT, ""+latitude);
		editor.putString(LOCATION_LNG, "" + longitude);
		editor.commit();
	}


	public static double getSavedLatFromSP(Context context){
		SharedPreferences preferences = context.getSharedPreferences(LOCATION_SP, 0);
		String latitude = preferences.getString(LOCATION_LAT, "" + 0);
		Log.d("saved last lat", "==" + latitude);
		return Double.parseDouble(latitude);
	}

	public static double getSavedLngFromSP(Context context){
		SharedPreferences preferences = context.getSharedPreferences(LOCATION_SP, 0);
		String longitude = preferences.getString(LOCATION_LNG, "" + 0);
		return Double.parseDouble(longitude);
	}
	
	
	
	










	private void createLocationRequest(long interval) {
        locationrequest = new LocationRequest();
        locationrequest.setInterval(interval);
        locationrequest.setFastestInterval(interval / 2);
		locationrequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


	private synchronized void buildGoogleApiClient(Context context) {
        googleApiClient = new GoogleApiClient.Builder(context)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API).build();
        googleApiClient.connect();
    }

	private void startLocationUpdates(long interval) {
        createLocationRequest(interval);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationrequest, this);
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
				if(googleApiClient != null && googleApiClient.isConnected()){
					locationUnchecked = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
					Log.i(TAG, "getLocation = "+locationUnchecked);
					if(!Utils.mockLocationEnabled(locationUnchecked)) {
						location = locationUnchecked;
						return location;
					}
				}
			}
		} catch(Exception e){e.printStackTrace();}
		return null;
	}
	
	

	
	
	public synchronized void destroy(){
		try{
			this.location = null;
			Log.e("location", "destroy");
			if(googleApiClient!=null){
				if(googleApiClient.isConnected()){
                    LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
                    googleApiClient.disconnect();
				}
				else if(googleApiClient.isConnecting()){
                    googleApiClient.disconnect();
				}
			}
		}catch(Exception e){
			Log.e("e", "=" + e.toString());
		}
		stopCheckingLocationUpdates();
	}
	

	private synchronized void startRequest(){
		try {
            startLocationUpdates(requestInterval);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onConnected(Bundle connectionHint) {
		Log.e(TAG, "onConnected");
		// sending one cached location at connection establishment
		Location loc = getLocation();
		if(loc != null){
            Bundle bundle = loc.getExtras();
			if(bundle == null){
				bundle = new Bundle();
			}
            bundle.putBoolean("cached", true);
            loc.setExtras(bundle);
			locationUpdate.onLocationChanged(loc);
		}
		startRequest();
	}

    @Override
    public void onConnectionSuspended(int i) {
        this.location = null;
    }

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.e(TAG, "onConnectionFailed");
		this.location = null;
	}









	@Override
	public void onLocationChanged(Location location) {
		try{
			Log.i(TAG, "onLocationChanged = "+location);
			locationUnchecked = location;
			if(location != null && !Utils.mockLocationEnabled(location)) {
				this.location = location;
				locationUpdate.onLocationChanged(location);
				saveLatLngToSP(context, location.getLatitude(), location.getLongitude());
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
				if(LocationFetcher.this.location == null){
					destroyWaitAndConnect();
				}
				else{
					long timeSinceLastLocationFix = System.currentTimeMillis() - LocationFetcher.this.location.getTime();
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

	private synchronized void stopCheckingLocationUpdates(){
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


	public Location getLocationUnchecked(){
		try{
			return locationUnchecked;
		} catch(Exception e){e.printStackTrace();}
		return null;
	}


}
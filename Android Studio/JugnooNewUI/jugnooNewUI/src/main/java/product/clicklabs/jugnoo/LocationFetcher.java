package product.clicklabs.jugnoo;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
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

	
	private final String LOCATION_SP = "location_sp",
			LOCATION_LAT = "location_lat",
			LOCATION_LNG = "location_lng";

	public LocationFetcher(Context context){
		this.context = context;
	}

	public synchronized void connect(LocationUpdate locationUpdate, long requestInterval){
		this.locationUpdate = locationUpdate;
		this.requestInterval = requestInterval;
		destroy();
		GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
		int resp = googleApiAvailability.isGooglePlayServicesAvailable(context);
		if(resp == ConnectionResult.SUCCESS){														// google play services working
			buildGoogleApiClient(context);
		}
		else{																						// google play services not working
			Log.e("Google Play error", "=" + resp);
		}
	}
	
	public synchronized void saveLatLngToSP(double latitude, double longitude){
		SharedPreferences preferences = context.getSharedPreferences(LOCATION_SP, 0);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(LOCATION_LAT, ""+latitude);
		editor.putString(LOCATION_LNG, "" + longitude);
		editor.commit();
	}


	public double getSavedLatFromSP(){
		SharedPreferences preferences = context.getSharedPreferences(LOCATION_SP, 0);
		String latitude = preferences.getString(LOCATION_LAT, "" + 0);
//		Log.d("saved last lat", "==" + latitude);
		return Double.parseDouble(latitude);
	}

	public double getSavedLngFromSP(){
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
		return getSavedLatFromSP();
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
		return getSavedLngFromSP();
	}

	private Location getLocation(){
		try{
			if(location != null){
				return location;
			}
			else{
				if(googleApiClient != null && googleApiClient.isConnected()){
					locationUnchecked = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
//					Log.i(TAG, "getLocation = "+locationUnchecked);
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
//			Log.e("location", "destroy");
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
//		Log.e(TAG, "onConnected");
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
//		Log.e(TAG, "onConnectionFailed");
		this.location = null;
	}









	@Override
	public void onLocationChanged(Location location) {
		try{
//			Log.i(TAG, "onLocationChanged>"+location);
			locationUnchecked = location;
			if(location != null && !Utils.mockLocationEnabled(location)) {
				this.location = location;
				locationUpdate.onLocationChanged(location);
				saveLatLngToSP(location.getLatitude(), location.getLongitude());
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


}
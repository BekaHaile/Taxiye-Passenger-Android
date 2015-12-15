package product.clicklabs.jugnoo.utils;


import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import product.clicklabs.jugnoo.LocationFetcher;
import product.clicklabs.jugnoo.sticky.GenieService;


public class LocationFetcherBG implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {

	
	private final String TAG = this.getClass().getSimpleName();

	private GoogleApiClient googleApiClient;
	private LocationRequest locationrequest;
	private PendingIntent locationIntent;
	
	private long requestInterval;
	private Context context;
	
	private static final int LOCATION_PI_ID = 6978;
	
	
	
	/**
	 * Constructor for initializing LocationFetcher class' object
	 * @param context application context
	 */
	public LocationFetcherBG(Context context, long requestInterval){
		this.context = context;
		this.requestInterval = requestInterval;
		connect();
	}
	
	
	
	public boolean isConnected(){
		if(googleApiClient != null){
			return googleApiClient.isConnected();
		}
		return false;
	}



	public void connect(){
		int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
		if(resp == ConnectionResult.SUCCESS){														// google play services working
			buildGoogleApiClient(context);
		}
		else{																						// google play services not working
			Log.e("Google Play Service Error ","="+resp);
		}
	}
	
	
	
	public void destroy(){
		try{
			Log.e("location","destroy");
			if(googleApiClient!=null){
				if(googleApiClient.isConnected()){
					LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, locationIntent);
					locationIntent.cancel();
					googleApiClient.disconnect();
				}
				else if(googleApiClient.isConnecting()){
					googleApiClient.disconnect();
				}
			}
		}catch(Exception e){
			Log.e("e", "="+e.toString());
		}
	}



	protected void createLocationRequest(long interval) {
		locationrequest = new LocationRequest();
		locationrequest.setInterval(interval);
		locationrequest.setFastestInterval(interval / 2);
		locationrequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	}


	protected synchronized void buildGoogleApiClient(Context context) {
		googleApiClient = new GoogleApiClient.Builder(context)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API).build();
		googleApiClient.connect();
	}

	protected void startLocationUpdates(long interval) {
		createLocationRequest(interval);
		Intent intent = new Intent(context, LocationReceiver.class);
		locationIntent = PendingIntent.getBroadcast(context, LOCATION_PI_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationrequest, locationIntent);

		if(Utils.compareDouble(LocationFetcher.getSavedLatFromSP(context), 0) != 0
				&& Utils.compareDouble(LocationFetcher.getSavedLngFromSP(context), 0) != 0 ){
			Intent serviceIntent = new Intent(context, GenieService.class);
			serviceIntent.putExtra("latitude", LocationFetcher.getSavedLatFromSP(context));
			serviceIntent.putExtra("longitude", LocationFetcher.getSavedLngFromSP(context));
			context.startService(serviceIntent);
		}

	}





	@Override
	public void onConnected(Bundle connectionHint) {
		Log.e(TAG, "onConnected ********************************************************");
		startLocationUpdates(requestInterval);
	}

	@Override
	public void onConnectionSuspended(int i) {
		Log.e(TAG, "onDisconnected ********************************************************");
		destroy();
		connect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.e(TAG, "onConnectionFailed ********************************************************");
		destroy();
		connect();
	}

}


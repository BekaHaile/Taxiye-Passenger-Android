package product.clicklabs.jugnoo;


import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;

public class LocationFetcherDriver implements GooglePlayServicesClient.ConnectionCallbacks,GooglePlayServicesClient.OnConnectionFailedListener {

	
	private final String TAG = this.getClass().getSimpleName();
	private LocationClient locationclient;
	private LocationRequest locationrequest;
	public Location location; // location
	private PendingIntent locationIntent;
	
	private long requestInterval, fastestInterval;
	private Context context;
	
	private static final int LOCATION_PI_ID = 6978;
	
	
	/**
	 * Constructor for initializing LocationFetcher class' object
	 * @param context application context
	 */
	public LocationFetcherDriver(Context context, long requestInterval, long fastestInterval){
		this.context = context;
		this.requestInterval = requestInterval;
		this.fastestInterval = fastestInterval;
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
	
	
	
	public boolean isConnected(){
		if(locationclient != null){
			return locationclient.isConnected();
		}
		return false;
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
	
	

	
	
	public void destroy(){
		try{
			Log.e("location","destroy");
			if(locationclient!=null){
				locationIntent.cancel();
				locationclient.disconnect();
			}
		}catch(Exception e){
			Log.e("e", "="+e.toString());
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		Log.e(TAG, "onConnected");
		
		locationrequest = LocationRequest.create();
		locationrequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
		locationrequest.setFastestInterval(fastestInterval);
		locationrequest.setInterval(requestInterval);
		
		Intent intent = new Intent(context, LocationReceiverDriver.class);
		locationIntent = PendingIntent.getBroadcast(context, LOCATION_PI_ID, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		locationclient.requestLocationUpdates(locationrequest, locationIntent);
	}

	@Override
	public void onDisconnected() {
		Log.e(TAG, "onDisconnected");
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.e(TAG, "onConnectionFailed");

	}

}

class LocationReceiverDriver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
    	try {
    		
    		Database2 database2 = new Database2(context);
			String userMode = database2.getUserMode();
			database2.close();
			
			Log.e("DriverLocationUpdateService userMode in PI ", "=="+userMode);
			
			if(Database2.UM_DRIVER.equalsIgnoreCase(userMode)){
				PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
				WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakelockTag2");
				wakeLock.acquire();
				
				Location location = (Location) intent.getExtras().get(LocationClient.KEY_LOCATION_CHANGED);
//	        	Log.writeLogToFile(""+DateOperations.getCurrentTime() + "<>" + location);
				database2 = new Database2(context);
				String accessToken = database2.getDLDAccessToken();
				String deviceToken = database2.getDLDDeviceToken();
				String serverUrl = database2.getDLDServerUrl();
				database2.close();
				
				sendLocationToServer(location, accessToken, deviceToken, serverUrl);
				
				
				wakeLock.release();
			}
    		
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public void sendLocationToServer(final Location location, final String accessToken, final String deviceToken, final String serverUrl){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					
					ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					nameValuePairs.add(new BasicNameValuePair("access_token", accessToken));
					nameValuePairs.add(new BasicNameValuePair("latitude", ""+location.getLatitude()));
					nameValuePairs.add(new BasicNameValuePair("longitude", ""+location.getLongitude()));
					nameValuePairs.add(new BasicNameValuePair("device_token", deviceToken));
					
					Log.e("nameValuePairs in location pi reciever","="+nameValuePairs);
					
					SimpleJSONParser simpleJSONParser = new SimpleJSONParser();
					String result = simpleJSONParser.getJSONFromUrlParams(serverUrl+"/update_driver_location", nameValuePairs);
					
					Log.e("result","="+result);
//					Log.writeLogToFile(""+DateOperations.getCurrentTime() + "<>" + result);
					
					simpleJSONParser = null;
					nameValuePairs = null;
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
    
}
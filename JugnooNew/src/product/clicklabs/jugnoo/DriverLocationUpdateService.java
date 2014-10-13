package product.clicklabs.jugnoo;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class DriverLocationUpdateService extends Service {
	
	static int count = 0; 
	
	LocationFetcherDriver locationFetcherDriver;
	GPSLocationFetcher gpsLocationFetcher;
	
	
	LatLng lastLocation;
	
	
	String GOOGLE_PROJECT_ID = "506849624961", 
			PROPERTY_REG_ID = "registration_id", 
			PROPERTY_APP_VERSION = "appVersion";
	
	String SHARED_PREF_NAME = "myPref";
	String SP_ACCESS_TOKEN_KEY = "access_token";
	
	String accessToken = "", deviceToken = "", SERVER_URL = "";
	
	long serverUpdateTimePeriod = 60000;
	
	
	public DriverLocationUpdateService() {
		Log.e("DriverLocationUpdateService"," instance created");
		
		lastLocation = new LatLng(0, 0);
		count = 0; 
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	
	@Override
    public void onCreate() {
        
    }
 
	
	
    @Override
    public void onStart(Intent intent, int startId) {
    	//For time consuming an long tasks you can launch a new thread here...
       
        try{
        	
        	Log.i("Driver location update started", "=======");
        	updateServerData();
    		
    		Database2 database2 = new Database2(this);
    		
    		String fast = database2.getDriverServiceFast();
    		
    		Log.e("fast", "="+fast);
    		
    		if(fast.equalsIgnoreCase("no")){
    			serverUpdateTimePeriod = 60000;
    			if(locationFetcherDriver != null){
    				locationFetcherDriver.destroy();
    				locationFetcherDriver = null;
    			}
    			locationFetcherDriver = new LocationFetcherDriver(DriverLocationUpdateService.this, serverUpdateTimePeriod, 45000);
    			if(gpsLocationFetcher != null){
    				gpsLocationFetcher.destroy();
    				gpsLocationFetcher = null;
    			}
    		}
    		else{
    			serverUpdateTimePeriod = 15000;
    			if(locationFetcherDriver != null){
    				locationFetcherDriver.destroy();
    				locationFetcherDriver = null;
    			}
    			if(gpsLocationFetcher != null){
    				gpsLocationFetcher.destroy();
    				gpsLocationFetcher = null;
    			}
    			gpsLocationFetcher = new GPSLocationFetcher(DriverLocationUpdateService.this, accessToken, deviceToken, SERVER_URL, serverUpdateTimePeriod);
    		}
        	
    		
            database2.close();
            
            startCheckIfDriverTimer();
        	
        } catch(Exception e){
        	e.printStackTrace();
        }
        
    }
    
    
    public void updateServerData(){
    	Database2 database2 = new Database2(this);
        database2.updateDriverServiceRestartOnReboot("yes");
    	database2.updateJugnooOn("on");
    	
    	//TODO Toggle live to trial
		String DEV_SERVER_URL = "https://54.81.229.172:8012";
		String LIVE_SERVER_URL = "https://dev.jugnoo.in:4010";
		String TRIAL_SERVER_URL = "http://54.81.229.172:8001";
		
		String DEFAULT_SERVER_URL = DEV_SERVER_URL;
		
		
		String SETTINGS_SHARED_PREF_NAME = "settingsPref", SP_SERVER_LINK = "sp_server_link";
		
		SERVER_URL = DEFAULT_SERVER_URL;
		
		SharedPreferences preferences = getSharedPreferences(SETTINGS_SHARED_PREF_NAME, 0);
		String link = preferences.getString(SP_SERVER_LINK, DEFAULT_SERVER_URL);
		
		if(link.equalsIgnoreCase(TRIAL_SERVER_URL)){
			SERVER_URL = TRIAL_SERVER_URL;
		}
		else if(link.equalsIgnoreCase(LIVE_SERVER_URL)){
			SERVER_URL = LIVE_SERVER_URL;
		}
		else if(link.equalsIgnoreCase(DEV_SERVER_URL)){
			SERVER_URL = DEV_SERVER_URL;
		}
		
		SharedPreferences pref = getSharedPreferences(SHARED_PREF_NAME, 0);
		accessToken = pref.getString(SP_ACCESS_TOKEN_KEY, "");
		
		deviceToken = DriverLocationUpdateService.this.getSharedPreferences(SplashLogin.class.getSimpleName(), 
				Context.MODE_PRIVATE).getString("registration_id", "");
    	
		database2.insertDriverLocData(accessToken, deviceToken, SERVER_URL);
		database2.close();
    }
    
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	
    	if(isMyServiceRunning()){
    		Log.e("service already running","-===");
    	}
    	else{
    		Log.e("service running not","-===");
    	}
    	
    	super.onStartCommand(intent, flags, startId);
    	return Service.START_STICKY;
    }
    
    
    @Override
    public void onTaskRemoved(Intent rootIntent) {
    	try {
    		Database2 database2 = new Database2(this);
    		String jugnooOn = database2.getJugnooOn();
    		database2.close();
    		Log.e("onTaskRemoved jugnooOn =","="+jugnooOn);
    		if("on".equalsIgnoreCase(jugnooOn)){
    			Log.e("onTaskRemoved","="+rootIntent);
    			Intent restartService = new Intent(getApplicationContext(), this.getClass());
    			restartService.setPackage(getPackageName());
    			PendingIntent restartServicePI = PendingIntent.getService(getApplicationContext(), 1, restartService, PendingIntent.FLAG_ONE_SHOT);
    			AlarmManager alarmService = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
    			alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() +1000, restartServicePI);
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    
    
    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (DriverLocationUpdateService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    
    
    
 
    @Override
    public void onDestroy() {
    	Log.e("DriverLocationUpdateService onDestroy","=");
        if(locationFetcherDriver != null){
        	locationFetcherDriver.destroy();
        	locationFetcherDriver = null;
        }
        if(gpsLocationFetcher != null){
			gpsLocationFetcher.destroy();
			gpsLocationFetcher = null;
		}
        
        Database2 database2 = new Database2(DriverLocationUpdateService.this);
        database2.updateDriverServiceRestartOnReboot("no");
        database2.close();    
        
        cancelCheckIfDriverTimer();
    }
    
    
    double distance(LatLng start, LatLng end) {
		try {
			Location location1 = new Location("locationA");
			location1.setLatitude(start.latitude);
			location1.setLongitude(start.longitude);
			Location location2 = new Location("locationA");
			location2.setLatitude(end.latitude);
			location2.setLongitude(end.longitude);

			double distance = location1.distanceTo(location2);
			return distance;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;

	}
    
    
    
    
    
	Timer timerCheckIfDriver;
	TimerTask timerTaskCheckIfDriver;
	
	public void startCheckIfDriverTimer(){
		cancelCheckIfDriverTimer();
		try {
			timerCheckIfDriver = new Timer();
			timerTaskCheckIfDriver = new TimerTask() {
				@Override
				public void run() {
					Database2 database2 = new Database2(DriverLocationUpdateService.this);
					try {
						String accessToken = database2.getDLDAccessToken();
						database2.close();
						
						if("".equalsIgnoreCase(accessToken)){
							updateServerData();
						}
						
						database2 = new Database2(DriverLocationUpdateService.this);
						String userMode = database2.getUserMode();
						database2.close();
						
						Log.e("DriverLocationUpdateService userMode in timertask ", "=="+userMode);
						
						if(Database2.UM_DRIVER.equalsIgnoreCase(userMode)){
							new DriverLocationDispatcher().sendLocationToServer(DriverLocationUpdateService.this);
						}
						else{
							stopSelf();
						}
					} 
					catch (Exception e) {
						e.printStackTrace();
					}
					finally{
						database2.close();
					}
					
				}
			};
			timerCheckIfDriver.scheduleAtFixedRate(timerTaskCheckIfDriver, 1000, 60000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void cancelCheckIfDriverTimer(){
		try{
			if(timerTaskCheckIfDriver != null){
				timerTaskCheckIfDriver.cancel();
				timerTaskCheckIfDriver = null;
			}
			if(timerCheckIfDriver != null){
				timerCheckIfDriver.cancel();
				timerCheckIfDriver.purge();
				timerCheckIfDriver = null;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
    
    
}



class GPSLocationFetcher {

	public MyLocationListener gpsListener, networkListener;
	public LocationManager locationManager;
	public Location location;
	public String accessToken, deviceToken, SERVER_URL;
	public Context context;
	
	PowerManager powerManager;
	WakeLock wakeLock;
	
	/**
	 * Initialize location fetcher object with selected listeners
	 * @param context
	 */
	public GPSLocationFetcher(Context context, String accessToken, String deviceToken, String SERVER_URL, long updatePeriod) {
		this.context = context;
		this.accessToken = accessToken;
		this.deviceToken = deviceToken;
		this.SERVER_URL = SERVER_URL;
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			gpsListener = new MyLocationListener();
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, updatePeriod, 0, gpsListener);
		} else {
			if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
				networkListener = new MyLocationListener();
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, updatePeriod, 0, networkListener);
			}
		}
		powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakelockTag2");
	}
	
	public void destroy(){
		try{
			locationManager.removeUpdates(gpsListener);
		}catch(Exception e){
		}
		try{
			locationManager.removeUpdates(networkListener);
		}catch(Exception e){
		}
	}

	

	class MyLocationListener implements LocationListener {

		public void onLocationChanged(Location loc) {
			if(isBetterLocation(loc, GPSLocationFetcher.this.location)){
				Log.e("************************************** custom", "Location changed "+loc);
				GPSLocationFetcher.this.location = loc;
				sendLocationToServer(location);
			}
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
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

	
	
	public void sendLocationToServer(final Location location){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					
					Database2 database2 = new Database2(context);
					String userMode = database2.getUserMode();
					database2.close();
					
					Log.e("DriverLocationUpdateService userMode in PI ", "=="+userMode);
					
					if(Database2.UM_DRIVER.equalsIgnoreCase(userMode)){
						if ((wakeLock != null) &&           // we have a WakeLock
			    			    (wakeLock.isHeld() == false)) {  // but we don't hold it 
			    			wakeLock.acquire();
			    		}
						
						ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
						nameValuePairs.add(new BasicNameValuePair("access_token", accessToken));
						nameValuePairs.add(new BasicNameValuePair("latitude", ""+location.getLatitude()));
						nameValuePairs.add(new BasicNameValuePair("longitude", ""+location.getLongitude()));
						nameValuePairs.add(new BasicNameValuePair("device_token", deviceToken));
						
						Log.e("nameValuePairs in fast","="+nameValuePairs);
						
						HttpRequester simpleJSONParser = new HttpRequester();
						String result = simpleJSONParser.getJSONFromUrlParams(SERVER_URL+"/update_driver_location", nameValuePairs);
						
						Log.e("result","="+result);
						
						simpleJSONParser = null;
						nameValuePairs = null;
						
						wakeLock.release();
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					if(wakeLock.isHeld()){
	    				wakeLock.release();
	    			}
				}
			}
		}).start();
	}
}

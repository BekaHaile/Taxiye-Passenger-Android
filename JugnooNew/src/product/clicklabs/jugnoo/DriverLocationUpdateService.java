package product.clicklabs.jugnoo;

import android.app.Activity;
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
import android.os.IBinder;
import android.os.SystemClock;

import com.google.android.gms.maps.model.LatLng;

public class DriverLocationUpdateService extends Service {
	
//	LocationFetcherDriver locationFetcherDriver;
	LocationFetcherDriverGPS locationFetcherDriver;
	GPSLocationFetcher gpsLocationFetcher;
	
	long serverUpdateTimePeriod = 60000;
	
	
	public DriverLocationUpdateService() {
		Log.e("DriverLocationUpdateService"," instance created");
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
        try{
        	Log.i("Driver location update started", "=======");
        	updateServerData(this);
    		
    		Database2 database2 = new Database2(this);
    		
    		String fast = database2.getDriverServiceFast();
    		
    		Log.e("fast", "="+fast);
    		
    		
    		
    		if(fast.equalsIgnoreCase(Database2.NO)){
    			if(locationFetcherDriver != null){
    				locationFetcherDriver.destroy();
    				locationFetcherDriver = null;
    			}
//    			serverUpdateTimePeriod = 60000;
    			serverUpdateTimePeriod = 120000;
//    			locationFetcherDriver = new LocationFetcherDriver(DriverLocationUpdateService.this, serverUpdateTimePeriod);
    			locationFetcherDriver = new LocationFetcherDriverGPS(DriverLocationUpdateService.this, serverUpdateTimePeriod);
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
    			gpsLocationFetcher = new GPSLocationFetcher(DriverLocationUpdateService.this, serverUpdateTimePeriod);
    		}
        	
    		
            database2.close();
            
            setupLocationUpdateAlarm();
        	
        } catch(Exception e){
        	e.printStackTrace();
        }
        
    }
    
    
    public static void updateServerData(Context context){
    	String SHARED_PREF_NAME = "myPref";
    	String SP_ACCESS_TOKEN_KEY = "access_token";
    	String accessToken = "", deviceToken = "", SERVER_URL = "";
    	
    	Database2 database2 = new Database2(context);
    	
    	//TODO Toggle live to trial
		String DEV_SERVER_URL = "https://54.81.229.172:8012";
		String LIVE_SERVER_URL = "https://dev.jugnoo.in:4012";
		String TRIAL_SERVER_URL = "http://54.81.229.172:8200";
		
		String DEFAULT_SERVER_URL = DEV_SERVER_URL;
		
		
		
		
		String SETTINGS_SHARED_PREF_NAME = "settingsPref", SP_SERVER_LINK = "sp_server_link";
		
		SERVER_URL = DEFAULT_SERVER_URL;
		
		SharedPreferences preferences = context.getSharedPreferences(SETTINGS_SHARED_PREF_NAME, 0);
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
		
		SharedPreferences pref = context.getSharedPreferences(SHARED_PREF_NAME, 0);
		accessToken = pref.getString(SP_ACCESS_TOKEN_KEY, "");
		
		deviceToken = context.getSharedPreferences(SplashLogin.class.getSimpleName(), 
				Context.MODE_PRIVATE).getString("registration_id", "");
    	
		Log.e("SERVER_URL in updateService","="+SERVER_URL);
		
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
    		String serviceRestartOnReboot = database2.getDriverServiceRun();
    		database2.close();
    		Log.e("onTaskRemoved serviceRestartOnReboot =","="+serviceRestartOnReboot);
    		if(Database2.YES.equalsIgnoreCase(serviceRestartOnReboot)){
    			Log.e("onTaskRemoved","="+rootIntent);
    			Intent restartService = new Intent(getApplicationContext(), this.getClass());
    			restartService.setPackage(getPackageName());
    			PendingIntent restartServicePI = PendingIntent.getService(getApplicationContext(), 1, restartService, PendingIntent.FLAG_ONE_SHOT);
    			AlarmManager alarmService = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
    			alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, restartServicePI);
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
        
        cancelLocationUpdateAlarm();
        
    }
    
    
	
	
	
	private static int DRIVER_LOCATION_PI_REQUEST_CODE = 111;
	private static final String SEND_LOCATION = "product.clicklabs.jugnoo.SEND_LOCATION";
	private static final long ALARM_REPEAT_INTERVAL = 3 * 60000;
	
	
	public void setupLocationUpdateAlarm(){
	    // check task is scheduled or not
	    boolean alarmUp = (PendingIntent.getBroadcast(this, DRIVER_LOCATION_PI_REQUEST_CODE, 
	    		new Intent(this, DriverLocationUpdateAlarmReceiver.class).setAction(SEND_LOCATION), 
	    		PendingIntent.FLAG_NO_CREATE) != null);
	    Log.e("alarmUp", "="+alarmUp);
	    
	    if(alarmUp){
	    	cancelLocationUpdateAlarm();
	    }
	    
	        Intent intent = new Intent(this, DriverLocationUpdateAlarmReceiver.class);
	        intent.setAction(SEND_LOCATION);
	        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, DRIVER_LOCATION_PI_REQUEST_CODE, 
	        		intent, PendingIntent.FLAG_UPDATE_CURRENT);
	        
	        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
	        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), ALARM_REPEAT_INTERVAL, pendingIntent);

	}
	
	public void cancelLocationUpdateAlarm(){
		Intent intent = new Intent(this, DriverLocationUpdateAlarmReceiver.class);
		intent.setAction(SEND_LOCATION);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, DRIVER_LOCATION_PI_REQUEST_CODE, 
        		intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager) this.getSystemService(Activity.ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);
		pendingIntent.cancel();
	}
	
    
}



class GPSLocationFetcher {

	public MyLocationListener locationListener;
	public LocationManager locationManager;
	public Context context;
	
	/**
	 * Initialize location fetcher object with selected listeners
	 * @param context
	 */
	public GPSLocationFetcher(Context context, long updatePeriod) {
		this.context = context;
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			locationListener = new MyLocationListener();
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, updatePeriod, 0, locationListener);
		} else {
			if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
				locationListener = new MyLocationListener();
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, updatePeriod, 0, locationListener);
			}
		}
	}
	
	public void destroy(){
		try{
			locationManager.removeUpdates(locationListener);
		}catch(Exception e){
		}
	}

	

	class MyLocationListener implements LocationListener {

		public void onLocationChanged(final Location loc) {
			
			new Thread(new Runnable() {
				@Override
				public void run() {
					Database2 database2 = new Database2(context);
			    	database2.updateDriverCurrentLocation(new LatLng(loc.getLatitude(), loc.getLongitude()));
			    	database2.close();
			    	Log.e("DriverLocationUpdateService location in GPS only fast ", "=="+loc);
					new DriverLocationDispatcher().sendLocationToServer(context, "GPSReciever");
				}
			}).start();
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

	}

	
	
}

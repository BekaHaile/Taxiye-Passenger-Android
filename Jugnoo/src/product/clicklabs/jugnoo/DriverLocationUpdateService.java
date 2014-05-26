package product.clicklabs.jugnoo;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class DriverLocationUpdateService extends Service {
	
	static int count = 0; 
	
	static LocationFetcher locationFetcher;
	
	SendDriverLocationToServer sendDriverLocationToServer;
	
	LatLng lastLocation;
	
	public DriverLocationUpdateService() {
		Log.e("DriverLocationUpdateService"," instance created");
		
		lastLocation = Data.chandigarhLatLng;
		
		count = 0; 
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	
	@Override
    public void onCreate() {
        Toast.makeText(this, "The new Service was Created", Toast.LENGTH_LONG).show();
       
//        Notification notification = new Notification();
//        startForeground(1, notification);
        
        
    }
 
	
	
    @Override
    public void onStart(Intent intent, int startId) {
    	// For time consuming an long tasks you can launch a new thread here...
        Toast.makeText(this, " DriverLocationUpdateService Started", Toast.LENGTH_LONG).show();
       
        try{
        	locationFetcher = new LocationFetcher(getApplicationContext());
        	
        	if(sendDriverLocationToServer != null){
        		sendDriverLocationToServer.cancel(true);
        		sendDriverLocationToServer = null;
        	}
        	
        	sendDriverLocationToServer = new SendDriverLocationToServer();
        	sendDriverLocationToServer.execute();
        	
        	
        } catch(Exception e){
        	e.printStackTrace();
        }
        
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
    	Log.e("onTaskRemoved","="+rootIntent);
        Intent restartService = new Intent(getApplicationContext(),
                this.getClass());
        restartService.setPackage(getPackageName());
        PendingIntent restartServicePI = PendingIntent.getService(
                getApplicationContext(), 1, restartService,
                PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() +1000, restartServicePI);

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
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
        if(sendDriverLocationToServer != null){
    		sendDriverLocationToServer.cancel(true);
    		sendDriverLocationToServer = null;
    	}
        locationFetcher.destroy();
    }
    
    
    class SendDriverLocationToServer extends AsyncTask<String, Integer, String>{
    	
    	
    	@Override
    	protected void onPreExecute() {
    		super.onPreExecute();
    	}
    	
    	
    	@Override
    	protected String doInBackground(String... params) {
    		
    		try{

				Thread.sleep(10000);
    			
				
	    		if(locationFetcher != null){
	    			
	    			LatLng currentLatLng = new LatLng(locationFetcher.getLatitude(), locationFetcher.getLongitude());
	    			if(locationFetcher.distance(DriverLocationUpdateService.this.lastLocation, currentLatLng) >= 0){
		    			
	    				DriverLocationUpdateService.this.lastLocation = currentLatLng;
	    				
		    			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		    			nameValuePairs.add(new BasicNameValuePair("driver_id", "1"));
		    			nameValuePairs.add(new BasicNameValuePair("latitude", ""+currentLatLng.latitude));
		    			nameValuePairs.add(new BasicNameValuePair("longitude", ""+currentLatLng.longitude));
		    			
		    			Log.e("nameValuePairs "+count,"="+nameValuePairs);
		    			
		    			
		    			SimpleJSONParser simpleJSONParser = new SimpleJSONParser();
		    			String result = simpleJSONParser.getJSONFromUrlParams("http://jsonlint.com/", nameValuePairs);
		    			
	//	    			Log.e("result","="+result);
		    			
		    			simpleJSONParser = null;
		    			nameValuePairs = null;
		    			currentLatLng = null;
		    			
		    			if(result.equalsIgnoreCase(SimpleJSONParser.SERVER_TIMEOUT)){
		    				
		    			}
		    			else{
		    				
		    			}
		    			
		    			result = null;
	    			}
	    			
	    		}
    		} catch(Exception e){
    			e.printStackTrace();
    		}
    		return "";
    	}
    	
    	
    	@Override
    	protected void onPostExecute(String result) {
    		super.onPostExecute(result);
    		
    		count++;
        	
        	sendDriverLocationToServer = null;
        	
        	sendDriverLocationToServer = new SendDriverLocationToServer();
        	sendDriverLocationToServer.execute();
    		
    	}
    	
    	
    }
    
    
}
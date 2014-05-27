package product.clicklabs.jugnoo;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class CStartRideService extends Service {
	
	static int count = 0; 
	
	GetRideStatusFromServer getRideStatusFromServer;
	
	static DetectRideStart detectRideStart;
	
	
	public CStartRideService() {
		Log.e("DriverLocationUpdateService"," instance created");
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
       
        try{
        	if(getRideStatusFromServer != null){
        		getRideStatusFromServer.cancel(true);
        		getRideStatusFromServer = null;
        	}
        	
        	getRideStatusFromServer = new GetRideStatusFromServer();
        	getRideStatusFromServer.execute();
        	
        	
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
    	return Service.START_NOT_STICKY;
    }
    
    
    @Override
    public void onTaskRemoved(Intent rootIntent) {
    	Log.e("onTaskRemoved","="+rootIntent);
//        Intent restartService = new Intent(getApplicationContext(),
//                this.getClass());
//        restartService.setPackage(getPackageName());
//        PendingIntent restartServicePI = PendingIntent.getService(
//                getApplicationContext(), 1, restartService,
//                PendingIntent.FLAG_ONE_SHOT);
//        AlarmManager alarmService = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() +1000, restartServicePI);

    }
    
    
    
    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (CStartRideService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    
 
    @Override
    public void onDestroy() {
        if(getRideStatusFromServer != null){
    		getRideStatusFromServer.cancel(true);
    		getRideStatusFromServer = null;
    	}
    }
    
    
    class GetRideStatusFromServer extends AsyncTask<String, Integer, String>{
    	
    	
    	@Override
    	protected void onPreExecute() {
    		super.onPreExecute();
    	}
    	
    	
    	@Override
    	protected String doInBackground(String... params) {
    		
    		try{

				Thread.sleep(10000);
    			
	    				
		    			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		    			nameValuePairs.add(new BasicNameValuePair("driver_id", "1"));
		    			
		    			Log.e("nameValuePairs "+count,"="+nameValuePairs);
		    			
		    			
		    			SimpleJSONParser simpleJSONParser = new SimpleJSONParser();
		    			String result = simpleJSONParser.getJSONFromUrlParams("http://jsonlint.com/", nameValuePairs);
		    			
	//	    			Log.e("result","="+result);
		    			
		    			simpleJSONParser = null;
		    			nameValuePairs = null;
		    			
		    			if(result.equalsIgnoreCase(SimpleJSONParser.SERVER_TIMEOUT)){
		    				Log.e("timeout","=");
		    			}
		    			else{
		    				Log.e("sucess","=");
		    				
		    				
		    			}
		    			
		    			
		    			
		    			result = null;
	    			
    		} catch(Exception e){
    			e.printStackTrace();
    		}
    		return "";
    	}
    	
    	
    	@Override
    	protected void onPostExecute(String result) {
    		super.onPostExecute(result);
    		
    		Log.e("count","="+count);
			Log.e("HomeActivitydetectRideStart=","="+CStartRideService.detectRideStart);
			
			if(count == 2 && CStartRideService.detectRideStart != null){
				CStartRideService.detectRideStart.sendIntent();
				stopSelf();
			}
			else{
				count++;
	        	
	        	getRideStatusFromServer = null;
	        	
	        	getRideStatusFromServer = new GetRideStatusFromServer();
	        	getRideStatusFromServer.execute();
			}
    		
    	}
    	
    	
    }
    
    
}

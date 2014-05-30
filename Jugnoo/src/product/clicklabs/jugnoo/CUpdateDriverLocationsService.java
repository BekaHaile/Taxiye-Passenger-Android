package product.clicklabs.jugnoo;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

public class CUpdateDriverLocationsService extends Service {
	
	static int count = 0; 
	
	GetDriverLocationsFromServer getDriverLocationsFromServer;
	
	static RefreshDriverLocations refreshDriverLocations;
	
	public CUpdateDriverLocationsService() {
		Log.e("CUpdateDriverLocationsService"," instance created");
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
        	if(getDriverLocationsFromServer != null){
        		getDriverLocationsFromServer.cancel(true);
        		getDriverLocationsFromServer = null;
        	}
        	
        	getDriverLocationsFromServer = new GetDriverLocationsFromServer();
        	getDriverLocationsFromServer.execute();
        	
        	
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
    
    
    
    
    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (CUpdateDriverLocationsService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    
 
    @Override
    public void onDestroy() {
        if(getDriverLocationsFromServer != null){
    		getDriverLocationsFromServer.cancel(true);
    		getDriverLocationsFromServer = null;
    	}
        System.gc();
    }
    
    
    class GetDriverLocationsFromServer extends AsyncTask<String, Integer, String>{
    	
    	
    	@Override
    	protected void onPreExecute() {
    		super.onPreExecute();
    		 Log.e("GetDriverLocationsFromServer","working");
    	}
    	
    	
    	@Override
    	protected String doInBackground(String... params) {
    		
    		try{

    			if(count > 0){
    				try {
						Thread.sleep(60000);
					} catch (Exception e) {
					}
    			}
    		} catch(Exception e){
    			e.printStackTrace();
    		}
    		return "refresh";
    	}
    	
    	
    	@Override
    	protected void onPostExecute(String result) {
    		super.onPostExecute(result);
    		
    		Log.e("count","="+count);
			Log.e("CustomerUpdateDriverLocationsService.refreshDriverLocations=","="+CUpdateDriverLocationsService.refreshDriverLocations);
			
			if("refresh".equalsIgnoreCase(result)  && CUpdateDriverLocationsService.refreshDriverLocations != null){
				CUpdateDriverLocationsService.refreshDriverLocations.refreshDriverLocations(count);
			}
				count++;
	        	
	        	getDriverLocationsFromServer = null;
	        	
	        	getDriverLocationsFromServer = new GetDriverLocationsFromServer();
	        	getDriverLocationsFromServer.execute();
	        	Log.e("GetDriverLocationsFromServer","stopped");
    	}
    	
    	
    }
    
    
}

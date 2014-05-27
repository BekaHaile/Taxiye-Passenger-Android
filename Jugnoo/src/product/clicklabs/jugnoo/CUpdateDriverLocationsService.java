package product.clicklabs.jugnoo;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

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
    	}
    	
    	
    	@Override
    	protected String doInBackground(String... params) {
    		
    		try{

    			if(count > 0){
    				try {
						Thread.sleep(100000);
					} catch (Exception e) {
						e.printStackTrace();
					}
    			}
				
	    				
		    			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		    			nameValuePairs.add(new BasicNameValuePair("access_token", Data.userData.accessToken));
		    			nameValuePairs.add(new BasicNameValuePair("latitude", ""+Data.mapTarget.latitude));
		    			nameValuePairs.add(new BasicNameValuePair("longitude", ""+Data.mapTarget.longitude));
		    			
		    			Log.e("nameValuePairs "+count,"="+nameValuePairs);
		    			
		    			
		    			SimpleJSONParser simpleJSONParser = new SimpleJSONParser();
		    			String result = simpleJSONParser.getJSONFromUrlParams(Data.SERVER_URL + "/find_a_driver", nameValuePairs);
		    			
		    			Log.e("result","="+result);
		    			
		    			simpleJSONParser = null;
		    			nameValuePairs = null;
		    			
		    			if(result.equalsIgnoreCase(SimpleJSONParser.SERVER_TIMEOUT)){
		    				Log.e("timeout","=");
		    				
		    				
		    			}
		    			else{
		    				Log.e("sucess","=");

//		    				{"error": "some parameter missing","flag":0}
//		    				{"error": "Invalid access token","flag":1}
		    				
		    				try{
		    					JSONObject jObj = new JSONObject(result);
		    					
		    					if(!jObj.isNull("error")){
		    						int flag = jObj.getInt("flag");
		    						
		    						if(1 == flag){
		    							return "stop";
		    						}
		    						
		    					}
		    					else{
		    						
//		    						{
//		    						    "data": [
//		    						        {
//		    						            "user_id": 9,
//		    						            "latitude": 30.73625,
//		    						            "longitude": 76.778735
//		    						        }
//		    						    ]
//		    						}
		    						
		    						JSONArray data = jObj.getJSONArray("data");
		    						
		    						Data.driverInfos.clear();
		    						
		    						for(int i=0; i<data.length(); i++){
		    							
		    							JSONObject dataI = data.getJSONObject(i);
		    							
		    							String userId = dataI.getString("user_id");
		    							double latitude = dataI.getDouble("latitude");
		    							double longitude = dataI.getDouble("longitude");
		    							
		    							Data.driverInfos.add(new DriverInfo(userId, latitude, longitude));
		    						}
		    						
		    						return "refresh";
		    						
		    					}
		    					
		    				}
		    				catch(Exception e){
		    					e.printStackTrace();
		    				}
		    			}
		    			result = null;
	    			
    		} catch(Exception e){
    			e.printStackTrace();
    		}
    		return "error";
    	}
    	
    	
    	@Override
    	protected void onPostExecute(String result) {
    		super.onPostExecute(result);
    		
    		Log.e("count","="+count);
			Log.e("CustomerUpdateDriverLocationsService.refreshDriverLocations=","="+CUpdateDriverLocationsService.refreshDriverLocations);
			
			if("refresh".equalsIgnoreCase(result)  && CUpdateDriverLocationsService.refreshDriverLocations != null){
				CUpdateDriverLocationsService.refreshDriverLocations.refreshDriverLocations();
			}
			else if("stop".equalsIgnoreCase(result)){
				stopSelf();
			}
				count++;
	        	
	        	getDriverLocationsFromServer = null;
	        	
	        	getDriverLocationsFromServer = new GetDriverLocationsFromServer();
	        	getDriverLocationsFromServer.execute();
    		
    	}
    	
    	
    }
    
    
}

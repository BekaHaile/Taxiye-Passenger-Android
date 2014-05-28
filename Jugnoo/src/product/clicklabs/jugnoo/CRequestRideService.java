package product.clicklabs.jugnoo;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class CRequestRideService extends Service {
	
	static int count = 0; 
	
	RequestRideAsync requestRideAsync;
	
	public static RequestRideInterrupt requestRideInterrupt;
	
	public CRequestRideService() {
		Log.e("CRequestRideService"," instance created");
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
        	if(requestRideAsync != null){
        		requestRideAsync.cancel(true);
        		requestRideAsync = null;
        	}
        	
        	requestRideAsync = new RequestRideAsync(0, Data.mapTarget);
			requestRideAsync.execute();
        	
        	
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
            if (CRequestRideService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    
 
    @Override
    public void onDestroy() {
        if(requestRideAsync != null){
        	requestRideAsync.cancel(true);
    		requestRideAsync = null;
    	}
        System.gc();
    }
    
    
    class RequestRideAsync extends AsyncTask<String, Integer, String>{

		int driverPos;
		LatLng pickupLatLng;
		
		public RequestRideAsync(int driverPos, LatLng pickupLatLng){
			this.driverPos = driverPos;
			this.pickupLatLng = pickupLatLng;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(String... params) {
			Log.i("driverPos","="+driverPos);
			if(driverPos > 0){
				try{
					Thread.sleep(60000);
				} catch(Exception e){
					e.printStackTrace();
				}
			}
			
			
			Data.latitude = pickupLatLng.latitude;
			Data.longitude = pickupLatLng.longitude;

		
			String currentDriverId = "";
			String previousDriverId = "";
			if(driverPos == 0){
				previousDriverId = "";
				currentDriverId = ""+Data.driverInfos.get(driverPos).userId;
			}
			else if(driverPos > 0 && driverPos < Data.driverInfos.size()){
				previousDriverId = ""+Data.driverInfos.get(driverPos-1).userId;
				currentDriverId = ""+Data.driverInfos.get(driverPos).userId;
			}
			else if(driverPos == Data.driverInfos.size()){
				previousDriverId = ""+Data.driverInfos.get(driverPos-1).userId;
				currentDriverId = "";
			}
			
			int flag = 0;
			if(driverPos == Data.driverInfos.size()){
				flag = 1;
			}
			
			
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("access_token", Data.userData.accessToken));
			nameValuePairs.add(new BasicNameValuePair("user_id", currentDriverId));
			nameValuePairs.add(new BasicNameValuePair("pre_user_id", previousDriverId));
			nameValuePairs.add(new BasicNameValuePair("pre_engage_id", Data.cEngagementId));
			nameValuePairs.add(new BasicNameValuePair("flag", ""+flag));
			nameValuePairs.add(new BasicNameValuePair("pickup_latitude", ""+Data.latitude));
			nameValuePairs.add(new BasicNameValuePair("pickup_longitude", "" + Data.longitude));
			
			Log.i("access_token", "=" + Data.userData.accessToken);
			Log.i("user_id", "=" + currentDriverId);
			Log.i("pre_user_id", "=" + previousDriverId);
			Log.i("pre_engage_id", "=" + Data.cEngagementId);
			Log.i("flag", "=" + flag);
			Log.i("pickup_latitude", "=" + Data.latitude);
			Log.i("pickup_longitude", "=" + Data.longitude);
			
			
			SimpleJSONParser simpleJSONParser = new SimpleJSONParser();
			String result = simpleJSONParser.getJSONFromUrlParams(Data.SERVER_URL + "/send_req_for_ride", nameValuePairs);
			
			Log.i("result","="+result);
			
			simpleJSONParser = null;
			nameValuePairs = null;
			
			return result;
			
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			
			
			if(result.equalsIgnoreCase(SimpleJSONParser.SERVER_TIMEOUT)){
				Log.e("timeout","=");
			}
			else{
				try{
					JSONObject jObj = new JSONObject(result);
					
					if(!jObj.isNull("error")){
						int flag = jObj.getInt("flag");	
						if(0 == flag){ // {"error": "some parameter missing","flag":0}//error
						}
						else if(1 == flag){ // {{"error": 'Invalid access token',"flag":1}//error
						}
						else if(2 == flag){ // {"error": "driver not available now","flag":2}
						}
						else{
						}
					}
					else{
						//{"engagement_id":9,"driver_id":"9"}
						Data.cEngagementId = jObj.getString("engagement_id");
						Data.cDriverId = jObj.getString("driver_id");
						
					}
				} catch(Exception e){
					e.printStackTrace();
				}
			}
			
			if(driverPos < Data.driverInfos.size()-1){
				requestRideAsync = null;
				requestRideAsync = new RequestRideAsync(driverPos+1, pickupLatLng);
				requestRideAsync.execute();
			}
			else if(driverPos == Data.driverInfos.size()-1){
				requestRideAsync = null;
				requestRideAsync = new RequestRideAsync(driverPos+1, pickupLatLng);
				requestRideAsync.execute();
			}
			else{
				requestRideAsync = null;
				if(requestRideInterrupt != null){
					requestRideInterrupt.requestRideInterrupt(0);
				}
				
				stopSelf();
			}
			
			
		}
		
	}
    
    
}

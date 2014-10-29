package product.clicklabs.jugnoo;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import com.google.android.gms.maps.model.LatLng;

public class DriverLocationDispatcher {

	public void sendLocationToServer(Context context, final String filePrefix){
		Database2 database2 = new Database2(context);
		try {
			String userMode = database2.getUserMode();
			
			if(Database2.UM_DRIVER.equalsIgnoreCase(userMode)){
				PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
				WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakelockTag2");
				wakeLock.acquire();
				
				String accessToken = database2.getDLDAccessToken();
				String deviceToken = database2.getDLDDeviceToken();
				String serverUrl = database2.getDLDServerUrl();
				
				if((!"".equalsIgnoreCase(accessToken)) && (!"".equalsIgnoreCase(deviceToken)) && (!"".equalsIgnoreCase(serverUrl))){
					LatLng latLng = database2.getDriverCurrentLocation();
					if(latLng.latitude != 0 && latLng.longitude != 0){
						ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
						nameValuePairs.add(new BasicNameValuePair("access_token", accessToken));
						nameValuePairs.add(new BasicNameValuePair("latitude", "" + latLng.latitude));
						nameValuePairs.add(new BasicNameValuePair("longitude", "" + latLng.longitude));
						nameValuePairs.add(new BasicNameValuePair("device_token", deviceToken));
			
			
						HttpRequester simpleJSONParser = new HttpRequester();
						String result = simpleJSONParser.getJSONFromUrlParams(serverUrl + "/update_driver_location", nameValuePairs);
									
						Log.e("result in DLD", "=" + result);
			
						try{
							//{"log":"Updated"}
							JSONObject jObj = new JSONObject(result);
							if(jObj.has("log")){
								String log = jObj.getString("log");
								if("Updated".equalsIgnoreCase(log)){
									database2.updateDriverLastLocationTime();
									Log.writeLogToFile(filePrefix, "To Server "+new DateOperations().getCurrentTime()+" = "+result);
								}
							}
						} catch(Exception e){
							e.printStackTrace();
						}
						
						simpleJSONParser = null;
						nameValuePairs = null;
					}
				}
				
				wakeLock.release();
			}

			database2.close();
			
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		finally{
    		database2.close();
    	}
	}

	
	
	
	public void saveLocationToDatabase(final Context context, final Location location){
		new Thread(new Runnable() {
			@Override
			public void run() {
				Database2 database2 = new Database2(context);
		    	database2.updateDriverCurrentLocation(new LatLng(location.getLatitude(), location.getLongitude()));
		    	database2.close();
			}
		}).start();
	}
	
	
}

package product.clicklabs.jugnoo;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import com.google.android.gms.maps.model.LatLng;

public class DriverLocationDispatcher {

	public void sendLocationToServer(Context context){
		Database2 database2 = new Database2(context);
		try {
			String userMode = database2.getUserMode();
			Log.e("DriverLocationUpdateService userMode in DLD ", "=="+userMode);
			
			if(Database2.UM_DRIVER.equalsIgnoreCase(userMode)){
				PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
				WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakelockTag2");
				wakeLock.acquire();
				
				String accessToken = database2.getDLDAccessToken();
				String deviceToken = database2.getDLDDeviceToken();
				String serverUrl = database2.getDLDServerUrl();
				
				if((!"".equalsIgnoreCase(accessToken)) && (!"".equalsIgnoreCase(deviceToken)) && (!"".equalsIgnoreCase(serverUrl))){
					LatLng latLng = database2.getDriverCurrentLocation();
					ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					nameValuePairs.add(new BasicNameValuePair("access_token", accessToken));
					nameValuePairs.add(new BasicNameValuePair("latitude", "" + latLng.latitude));
					nameValuePairs.add(new BasicNameValuePair("longitude", "" + latLng.longitude));
					nameValuePairs.add(new BasicNameValuePair("device_token", deviceToken));
		
					Log.e("nameValuePairs in location DLD", "=" + nameValuePairs);
		
					HttpRequester simpleJSONParser = new HttpRequester();
					String result = simpleJSONParser.getJSONFromUrlParams(serverUrl + "/update_driver_location", nameValuePairs);
		
					Log.e("result in DLD", "=" + result);
		
					simpleJSONParser = null;
					nameValuePairs = null;
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

}

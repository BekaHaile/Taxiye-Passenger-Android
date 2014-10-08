package product.clicklabs.jugnoo;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

import com.google.android.gms.location.LocationClient;

public class LocationReceiverDriver extends BroadcastReceiver {

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
				
				if((!"".equalsIgnoreCase(accessToken)) && (!"".equalsIgnoreCase(deviceToken)) && (!"".equalsIgnoreCase(serverUrl))){
					sendLocationToServer(location, accessToken, deviceToken, serverUrl);
				}
				
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
					
					HttpRequester simpleJSONParser = new HttpRequester();
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
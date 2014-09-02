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

import com.google.android.gms.location.LocationClient;

public class LocationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

    	PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
    	WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakelockTag2");
		wakeLock.acquire();
    	
        Location location = (Location) intent.getExtras().get(LocationClient.KEY_LOCATION_CHANGED);
        
        Database2 database2 = new Database2(context);
        String accessToken = database2.getDLDAccessToken();
        String deviceToken = database2.getDLDDeviceToken();
        String serverUrl = database2.getDLDServerUrl();
        database2.close();
        
        sendLocationToServer(location, accessToken, deviceToken, serverUrl);
        Log.writeLogToFile(""+DateOperations.getCurrentTime() + "<>" + location);
        
        wakeLock.release();
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
					
					Log.e("nameValuePairs in fast","="+nameValuePairs);
					
					SimpleJSONParser simpleJSONParser = new SimpleJSONParser();
					String result = simpleJSONParser.getJSONFromUrlParams(serverUrl+"/update_driver_location", nameValuePairs);
					
					Log.e("result","="+result);
					
					simpleJSONParser = null;
					nameValuePairs = null;
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
    
}
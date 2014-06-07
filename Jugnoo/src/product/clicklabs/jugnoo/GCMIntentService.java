package product.clicklabs.jugnoo;

import org.json.JSONObject;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.maps.model.LatLng;

public class GCMIntentService extends IntentService {
	
	public static final int NOTIFICATION_ID = 1;
//    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GCMIntentService() {
        super("GcmIntentService");
    }

		protected void onError(Context arg0, String arg1) {
	    	 Log.e("Registration", "Got an error1!");
	         Log.e("Registration",arg1.toString());
	    }

	    protected boolean onRecoverableError(Context context, String errorId){
	    	Log.d("onRecoverableError", errorId);
	        return false;	    
	    }

	    
	  
		
	    protected void onMessage(Context context, Intent arg1) {
	    	try{
		    	 Log.e("Recieved a gcm message arg1...", ","+arg1.getExtras());
		    	 
		    	 if(!"".equalsIgnoreCase(arg1.getExtras().getString("message", ""))){
		    		 
		    		 String message = arg1.getExtras().getString("message");
		    		 
		    		 try{
	    				 JSONObject jObj = new JSONObject(message);
	    				 
	    				 int flag = jObj.getInt("flag");
	    				 
	    				 //flag 0 for customer ride request to driver show marker on map
	    				 if(0 == flag){
	    					 
	    					 String engagementId = jObj.getString("engagement_id");
	    					 String userId = jObj.getString("user_id");
	    					 double latitude = jObj.getDouble("latitude");
	    					 double longitude = jObj.getDouble("longitude");
	    					 
	    					 Log.e("HomeActivity.driverGetRequestPush in push ","="+HomeActivity.driverGetRequestPush);
	    					 
	    					 if(HomeActivity.driverGetRequestPush != null){
	    						 HomeActivity.driverGetRequestPush.changeRideRequest(engagementId, userId, new LatLng(latitude, longitude), true);
	    					 }
	    					 else{
	    						 notificationManager(context, "You have got a new ride request.");
	    					 }
	    					 
	    				 }
	    				 // flag 1 for driver request accepted  show customer cancel and then call driver
	    				 else if(1 == flag){
	    					 String driverId = jObj.getString("driver_id");
	    					 Data.cDriverId = driverId;
	    					 
	    					 String engagementId = jObj.getString("engagement_id");
	    					 Data.cEngagementId = engagementId;
	    					 
	    					 if(CRequestRideService.requestRideInterrupt != null){
			    				 CRequestRideService.requestRideInterrupt.requestRideInterrupt(1);
			    			 }
	    				 }
	    				// flag 2 for driver request canceled customer cancels the ride and show driver the popups
	    				 else if(2 == flag){
	    					 // {"engagement_id": engagementid, "flag": 2}

	    					 String engagementId = jObj.getString("engagement_id");
	    					 
	    					 Log.e("HomeActivity.driverGetRequestPush in push ","="+HomeActivity.driverGetRequestPush);
	    					 
	    					 if(HomeActivity.driverGetRequestPush != null){
	    						 HomeActivity.driverGetRequestPush.changeRideRequest(engagementId, "", new LatLng(0, 0), false);
	    					 }
	    					 
	    				 }
	    				// flag 3 for driver ride started show customer ride in progress  
	    				 else if(3 == flag){

	    					 if (HomeActivity.detectRideStart != null) {
	    						 HomeActivity.detectRideStart.startRideForCustomer(0);
	    					 }
	    				 }
	    				 // {"flag": 4,"log":"show"}  customer cancel 10 seconds done show driver start ride option
	    				 else if(4 == flag){

	    					 if (HomeActivity.driverStartRideInterrupt != null) {
	    						 HomeActivity.driverStartRideInterrupt.driverStartRideInterrupt();
	    					 }
	    				 }
	    				// {"flag": 5} end ride on customer side show review
	    				 else if(5 == flag){

//	    					 {"flag": 5, "fare": fare, "distance_travelled": distance_travelled}
	    					 
	    					 Data.totalDistance = jObj.getDouble("distance_travelled");
	    					 Data.totalFare = jObj.getDouble("fare");
	    					 
	    					 if (HomeActivity.customerEndRideInterrupt != null) {
	    						 HomeActivity.customerEndRideInterrupt.customerEndRideInterrupt();
	    					 }
	    				 }
	    				// flag 6 for driver ride canceled show customer ride canceled by driver  
	    				 else if(6 == flag){

	    					 if (HomeActivity.detectRideStart != null) {
	    						 HomeActivity.detectRideStart.startRideForCustomer(1);
	    					 }
	    				 }
	    				 else{
	    					 
	    				 }
	    				 
		    		 } catch(Exception e){
		    			 
		    		 }
		    		 
		    		 
		    		 
		    	 }
		    	 
	    		
	    	 }
	    	 catch(Exception e){
	    		 Log.e("Recieved exception message arg1...", ","+arg1);
	    		 Log.e("exception", ","+e);
	    	 }

	         
	    }

	  
	    @SuppressWarnings("deprecation")
		private void notificationManager(Context context, String message) {
	    	
			long when = System.currentTimeMillis();
			
			NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			
			Log.v("message",","+message);
			
			Intent notificationIntent = new Intent(context, SplashNewActivity.class);
			Log.v("notification_message",","+message);
			
			Notification notification = new Notification(R.drawable.jugnoo_icon, message, when);
			String title = "Jugnoo";
			
			// set intent so it does not start a new activity
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			
			PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

			notification.setLatestEventInfo(context, title, message, intent);
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
		    notification.defaults |= Notification.DEFAULT_ALL;
			notificationManager.notify(NOTIFICATION_ID, notification);
			
			
		}

	    @SuppressWarnings("deprecation")
		private void notificationManagerResume(Context context, String message) {
			
			NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			
			Log.v("notification_message",","+message);
			
			Notification notification = new Notification(R.drawable.jugnoo_icon,
		            "Jugnoo", System.currentTimeMillis());
		    notification.setLatestEventInfo(this, "Jugnoo",
		    		message, PendingIntent.getActivity(this,
		                    0, new Intent(this, HomeActivity.class)
		                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
		                                    | Intent.FLAG_ACTIVITY_SINGLE_TOP),
		                    PendingIntent.FLAG_CANCEL_CURRENT));
//		    notification.flags |= Notification.FLAG_ONGOING_EVENT;
		    notification.flags |= Notification.FLAG_AUTO_CANCEL;
		    notification.defaults |= Notification.DEFAULT_ALL;
			notificationManager.notify(NOTIFICATION_ID, notification);
		}
	    
	    
	    public static void clearNotifications(Context context){
	    	NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
	    	notificationManager.cancel(NOTIFICATION_ID);
	    }
	    
		protected void onRegistered(Context arg0, String arg1) {
	    	 Log.e("Registration", "!");
	         Log.e("Registration", arg1.toString());
	         Data.deviceToken = arg1.toString();
	    }

	    protected void onUnregistered(Context arg0, String arg1) {
	    	 Log.e("Registration", "Got an error4!");
	         Log.e("Registration", arg1.toString());
	    }

	    
	    
		 
	    @Override
	    public void onHandleIntent(Intent intent) {
	        Bundle extras = intent.getExtras();
	        
	        Log.e("onHandleIntent extras","="+extras);
	        
	        Log.e("extras.isEmpty()", "="+extras.isEmpty());
	        Log.e("Recieved a gcm message arg1...", ","+intent.getExtras());
	        
	        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
	        // The getMessageType() intent parameter must be the intent you received
	        // in your BroadcastReceiver.
	        String messageType = gcm.getMessageType(intent);

	        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
	            /*
	             * Filter messages based on message type. Since it is likely that GCM
	             * will be extended in the future with new message types, just ignore
	             * any message types you're not interested in, or that you don't
	             * recognize.
	             */
	            if (GoogleCloudMessaging.
	                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
//	                sendNotification("Send error: " + extras.toString());
	            } else if (GoogleCloudMessaging.
	                    MESSAGE_TYPE_DELETED.equals(messageType)) {
//	                sendNotification("Deleted messages on server: " +
//	                        extras.toString());
	            // If it's a regular GCM message, do some work.
	            } else if (GoogleCloudMessaging.
	                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
	                // This loop represents the service doing some work.

	            	String SHARED_PREF_NAME1 = "myPref", SP_ACCESS_TOKEN_KEY = "access_token";
	            	
	            	SharedPreferences pref1 = getSharedPreferences(SHARED_PREF_NAME1, 0);
	        		final String accessToken = pref1.getString(SP_ACCESS_TOKEN_KEY, "");
	        		if(!"".equalsIgnoreCase(accessToken)){
	            	
	    	    	try{
	    		    	 Log.e("Recieved a gcm message arg1...", ","+intent.getExtras());
	    		    	 
	    		    	 if(!"".equalsIgnoreCase(intent.getExtras().getString("message", ""))){
	    		    		 
	    		    		 String message = intent.getExtras().getString("message");
	    		    		 
	    		    		 try{
	    	    				 JSONObject jObj = new JSONObject(message);
	    	    				 
	    	    				 int flag = jObj.getInt("flag");
	    	    				 
	    	    				 //flag 0 for customer ride request to driver show marker on map
	    	    				 if(0 == flag){
	    	    					 
	    	    					 String engagementId = jObj.getString("engagement_id");
	    	    					 String userId = jObj.getString("user_id");
	    	    					 double latitude = jObj.getDouble("latitude");
	    	    					 double longitude = jObj.getDouble("longitude");
	    	    					 
	    	    					 Log.e("HomeActivity.driverGetRequestPush in push ","="+HomeActivity.driverGetRequestPush);
	    	    					 
	    	    					 if(HomeActivity.driverGetRequestPush != null){
	    	    						 notificationManagerResume(this, "You have got a new ride request.");
	    	    						 HomeActivity.driverGetRequestPush.changeRideRequest(engagementId, userId, new LatLng(latitude, longitude), true);
	    	    					 }
	    	    					 else{
	    	    						 
	    	    						 String SHARED_PREF_NAME = "myPref", 
	    	    								 SP_D_NEW_RIDE_REQUEST = "d_new_ride_request", 
	    	    								 SP_D_NR_ENGAGEMENT_ID = "d_nr_engagement_id",
	    	    									SP_D_NR_USER_ID = "d_nr_user_id",
	    	    									SP_D_NR_LATITUDE = "d_nr_latitude",
	    	    									SP_D_NR_LONGITUDE = "d_nr_longitude";
	    	    						 
	    	    						 SharedPreferences pref = getSharedPreferences(SHARED_PREF_NAME, 0);
	    	    						 Editor editor = pref.edit();
	    	    						 editor.putString(SP_D_NEW_RIDE_REQUEST, "yes");
	    	    						 editor.putString(SP_D_NR_ENGAGEMENT_ID, engagementId);
	    	    						 editor.putString(SP_D_NR_USER_ID, userId);
	    	    						 editor.putString(SP_D_NR_LATITUDE, ""+latitude);
	    	    						 editor.putString(SP_D_NR_LONGITUDE, ""+longitude);
	    	    						 editor.commit();
	    	    						 
	    	    						 notificationManager(this, "You have got a new ride request.");
	    	    						 
	    	    						 
	    	    					 }
	    	    					 
	    	    					 
	    	    					 
	    	    					 
	    	    				 }
	    	    				 // flag 1 for driver request accepted  show customer cancel for 5 sec and then call driver
	    	    				 else if(1 == flag){
	    	    					 String driverId = jObj.getString("driver_id");
	    	    					 Data.cDriverId = driverId;
	    	    					 
	    	    					 String engagementId = jObj.getString("engagement_id");
	    	    					 Data.cEngagementId = engagementId;
	    	    					 
	    	    					 if(CRequestRideService.requestRideInterrupt != null){
	    	    						 CRequestRideService.requestRideInterrupt.apiEnd();
	    			    				 CRequestRideService.requestRideInterrupt.requestRideInterrupt(1);
	    			    			 }
	    	    				 }
	    	    				// flag 2 for driver request canceled customer cancels the ride and show driver the popups
	    	    				 else if(2 == flag){
	    	    					 //{"engage_id":"427","flag":2}
	    	    					 String engagementId = jObj.getString("engage_id");
	    	    					 
	    	    					 Log.e("HomeActivity.driverGetRequestPush in push ","="+HomeActivity.driverGetRequestPush);
	    	    					 
	    	    					 clearNotifications(this);
	    	    					 
	    	    					 if(HomeActivity.driverGetRequestPush != null){
	    	    						 HomeActivity.driverGetRequestPush.changeRideRequest(engagementId, "", new LatLng(0, 0), false);
	    	    					 }
	    	    					 
    	    						 String SHARED_PREF_NAME = "myPref", 
    	    								 SP_D_NEW_RIDE_REQUEST = "d_new_ride_request", 
    	    								 SP_D_NR_ENGAGEMENT_ID = "d_nr_engagement_id",
    	    									SP_D_NR_USER_ID = "d_nr_user_id",
    	    									SP_D_NR_LATITUDE = "d_nr_latitude",
    	    									SP_D_NR_LONGITUDE = "d_nr_longitude";
    	    						 
    	    						 SharedPreferences pref = getSharedPreferences(SHARED_PREF_NAME, 0);
    	    						 Editor editor = pref.edit();
    	    						 editor.putString(SP_D_NEW_RIDE_REQUEST, "no");
    	    						 editor.putString(SP_D_NR_ENGAGEMENT_ID, "");
    	    						 editor.putString(SP_D_NR_USER_ID, "");
    	    						 editor.putString(SP_D_NR_LATITUDE, "");
    	    						 editor.putString(SP_D_NR_LONGITUDE, "");
    	    						 editor.commit();
	    	    					 
	    	    				 }
	    	    				// flag 3 for driver ride started show customer ride in progress  
	    	    				 else if(3 == flag){

	    	    					 if (HomeActivity.detectRideStart != null) {
	    	    						 notificationManagerResume(this, "Your ride has started.");
	    	    						 HomeActivity.detectRideStart.startRideForCustomer(0);
	    	    					 }
	    	    					 else{
	    	    						 String SHARED_PREF_NAME = "myPref",
	    	    						 SP_CUSTOMER_SCREEN_MODE = "customer_screen_mode",
	    	    								 P_IN_RIDE = "P_IN_RIDE";
	    	    						 SharedPreferences pref = getSharedPreferences(SHARED_PREF_NAME, 0);
	    	    						 Editor editor = pref.edit();
	    	    						 editor.putString(SP_CUSTOMER_SCREEN_MODE, P_IN_RIDE);
	    	    						 editor.commit();
	    	    						 
	    	    						 notificationManager(this, "Your ride has started.");
	    	    					 }
	    	    				 }
	    	    				 // {"flag": 4,"log":"show"}  customer cancel 5 seconds done show driver start ride option
	    	    				 else if(4 == flag){

	    	    					 if (HomeActivity.driverStartRideInterrupt != null) {
	    	    						 HomeActivity.driverStartRideInterrupt.driverStartRideInterrupt();
	    	    					 }
	    	    				 }
	    	    				// {"flag": 5} end ride on customer side show review
	    	    				 else if(5 == flag){

//	    	    					 {"flag": 5, "fare": fare, "distance_travelled": distance_travelled}
	    	    					 
	    	    					 Data.totalDistance = jObj.getDouble("distance_travelled");
	    	    					 Data.totalFare = jObj.getDouble("fare");
	    	    					 Data.waitTime = jObj.getString("wait_time");
	    	    					 
	    	    					 if (HomeActivity.customerEndRideInterrupt != null) {
	    	    						 notificationManagerResume(this, "Your ride has ended.");
	    	    						 HomeActivity.customerEndRideInterrupt.customerEndRideInterrupt();
	    	    					 }
	    	    					 else{
	    	    						 String SHARED_PREF_NAME = "myPref",
	    	    						 SP_CUSTOMER_SCREEN_MODE = "customer_screen_mode",
	    	    								 P_RIDE_END = "P_RIDE_END",
	    	    										 SP_C_TOTAL_DISTANCE = "c_total_distance",
	    	    											SP_C_TOTAL_FARE = "c_total_fare", 
	    	    											SP_C_WAIT_TIME = "c_wait_time";
	    	    						 SharedPreferences pref = getSharedPreferences(SHARED_PREF_NAME, 0);
	    	    						 Editor editor = pref.edit();
	    	    						 editor.putString(SP_CUSTOMER_SCREEN_MODE, P_RIDE_END);
	    	    						 editor.putString(SP_C_TOTAL_DISTANCE, ""+Data.totalDistance);
	    	    						 editor.putString(SP_C_TOTAL_FARE, ""+Data.totalFare);
	    	    						 editor.putString(SP_C_WAIT_TIME, Data.waitTime);
	    	    						 editor.commit();
	    	    						 
	    	    						 
	    	    						 
	    	    						 notificationManager(this, "Your ride has ended.");
	    	    					 }
	    	    				 }
	    	    				// flag 6 for driver ride canceled show customer ride canceled by driver  
	    	    				 else if(6 == flag){

	    	    					 if (HomeActivity.detectRideStart != null) {
	    	    						 HomeActivity.detectRideStart.startRideForCustomer(1);
	    	    					 }
	    	    				 }
	    	    				 // flag 7 for start and stop wait for customer
	    	    				 else if(7 == flag){
	    	    					 String message1 = jObj.getString("message");
	    	    					 notificationManager(this, ""+message1);
	    	    				 }
	    	    				 else{
	    	    					 
	    	    				 }
	    	    				 
	    		    		 } catch(Exception e){
	    		    			 
	    		    		 }
	    		    		 
	    		    		 
	    		    		 
	    		    	 }
	    		    	 
	    	    		
	    	    	 }
	    	    	 catch(Exception e){
	    	    		 Log.e("Recieved exception message arg1...", ","+intent);
	    	    		 Log.e("exception", ","+e);
	    	    	 }
	        		}
	    	         
	    	    
	            }
	        }
	        // Release the wake lock provided by the WakefulBroadcastReceiver.
	        GcmBroadcastReceiver.completeWakefulIntent(intent);
	    }


}

package product.clicklabs.jugnoo;

import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gms.maps.model.LatLng;

public class GCMIntentService extends GCMBaseIntentService {
	
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

	    					 if (CStartRideService.detectRideStart != null) {
	    						 CStartRideService.detectRideStart.startRideForCustomer();
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
	    				 else{
	    					 
	    				 }
	    				 
		    		 } catch(Exception e){
		    			 
		    		 }
		    		 
		    		 
		    	 }
		    	 
		         
//		        	 Log.v("gcm message: ", ""+arg1.getStringExtra("message"));
//		        	 
//		        	 ActivityManager activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
//						List<RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
//
//						for (int i = 0; i < procInfos.size(); i++) {
//							 Log.e("pos=" + i, "" + procInfos.get(i).processName);
//							if (procInfos.get(i).processName.equals("product.clicklabs.bistrocustomerapp")) {
////								if(HomeActivity.act != null){
////								HomeActivity.GetDataAsync exc = new HomeActivity.GetDataAsync();
////								exc.execute();
////								}
//							}
//						}
//	    	 }
//	 			
	    		
	    		
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
			
			Intent notificationIntent = new Intent(context, SplashLogin.class);
			Log.v("notification_message",","+message);
			
			Notification notification = new Notification(R.drawable.ic_launcher, message, when);
			String title = "Jugnoo";
			
			// set intent so it does not start a new activity
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			
			PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

			notification.setLatestEventInfo(context, title, message, intent);
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			notificationManager.notify(0, notification);
			
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

		 
	    



}

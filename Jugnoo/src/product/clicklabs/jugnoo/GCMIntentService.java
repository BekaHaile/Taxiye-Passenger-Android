package product.clicklabs.jugnoo;

import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.google.android.gcm.GCMBaseIntentService;

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
		    	 
		    	 if("".equalsIgnoreCase(arg1.getExtras().getString("message", ""))){
		    		 
		    		 String message = arg1.getExtras().getString("message");
		    		 
		    		 if("0".equalsIgnoreCase(message)){
		    			 if(CRequestRideService.requestRideInterrupt != null){
		    				 CRequestRideService.requestRideInterrupt.requestRideInterrupt(1);
		    			 }
		    		 }
		    		 else{
		    			 
		    			 try{
		    				 
		    				 JSONObject jObj = new JSONObject(message);
		    				 
		    			 } catch(Exception e){
		    				 e.printStackTrace();
		    			 }
		    			 
		    		 }
		    		 
		    	 }
		    	 
		         notificationManager(context, arg1.toString());
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
	    		
	    		 Log.e("no exception", ",");
	    		
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

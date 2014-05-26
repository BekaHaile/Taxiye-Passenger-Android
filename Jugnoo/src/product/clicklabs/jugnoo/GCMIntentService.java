package product.clicklabs.jugnoo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {
	
	static Intent notificationIntent;  
	static String notificationMessage;
	
	static Context context;
	
	
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
//		    	 Log.e("Recieved a description...", ","+arg1.getStringExtra("brand_name"));
//		    	 Log.e("Recieved a message arg1...", ","+arg1.getExtras());
//		    	 
//		    	 GCMIntentService.context=context;
//		    	 notificationMessage = arg1.getStringExtra("brand_name") + ": " + arg1.getStringExtra("message") ;
//		         notificationManager(context,arg1.getStringExtra("message"));
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
			
			notificationIntent = new Intent(context, SplashLogin.class);
			Log.v("notification_message",","+notificationMessage);
			
			Notification notification = new Notification(R.drawable.ic_launcher, notificationMessage, when);
			String title = "New Offer in Bistro";
			
			// set intent so it does not start a new activity
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			
			PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

			notification.setLatestEventInfo(context, title, notificationMessage, intent);
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

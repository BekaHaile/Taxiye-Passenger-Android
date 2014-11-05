package product.clicklabs.jugnoo;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GCMIntentService extends IntentService {
	
	public static final int NOTIFICATION_ID = 1;
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

	    
		

	  
		private void notificationManager(Context context, String message, boolean ring) {
	    	
			try {
				long when = System.currentTimeMillis();
				
				NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
				
				Log.v("message",","+message);
				
				Intent notificationIntent = new Intent(context, SplashNewActivity.class);
				
				
				notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
				
				NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
				builder.setAutoCancel(true);
				builder.setContentTitle("Jugnoo");
				builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
				builder.setContentText(message);
				builder.setTicker(message);
				
				if(ring){
					builder.setLights(Color.GREEN, 500, 500);
				}
				else{
					builder.setDefaults(Notification.DEFAULT_ALL);
				}
				
				builder.setWhen(when);
				builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.jugnoo_icon));
				builder.setSmallIcon(R.drawable.notif_icon);
				builder.setContentIntent(intent);
				
				
				
				
				
				Notification notification = builder.build();
				notificationManager.notify(NOTIFICATION_ID, notification);
				
				PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
				WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
				wl.acquire(15000);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}

		private void notificationManagerResume(Context context, String message, boolean ring) {
			
			try {
				long when = System.currentTimeMillis();
				
				NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
				
				Log.v("message",","+message);
				
				Intent notificationIntent = new Intent(context, HomeActivity.class);
				
				notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
				
				NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
				builder.setAutoCancel(true);
				builder.setContentTitle("Jugnoo");
				builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
				builder.setContentText(message);
				builder.setTicker(message);
				
				if(ring){
					builder.setLights(Color.GREEN, 500, 500);
				}
				else{
					builder.setDefaults(Notification.DEFAULT_ALL);
				}
				
				builder.setWhen(when);
				builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.jugnoo_icon));
				builder.setSmallIcon(R.drawable.notif_icon);
				builder.setContentIntent(intent);
				
				
				
				Notification notification = builder.build();
				notificationManager.notify(NOTIFICATION_ID, notification);
				
				PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
				WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
				wl.acquire(15000);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
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
	    	    				 
	    	    				 if(PushFlags.REQUEST.getOrdinal() == flag){
	    	    						 
//	    	    						 {   "engagement_id": engagement_id, 
//	    	    							 "user_id": data.customer_id, 
//	    	    							 "flag": g_enum_notificationFlags.REQUEST,
//	    	    							 "latitude": data.pickup_latitude, 
//	    	    							 "longitude": data.pickup_longitude, 
//	    	    							 "address": pickup_address
//	    	    							 "start_time": date}
	    	    					 
	    	    					 
	    	    						 String engagementId = jObj.getString("engagement_id");
		    	    					 String userId = jObj.getString("user_id");
		    	    					 double latitude = jObj.getDouble("latitude");
		    	    					 double longitude = jObj.getDouble("longitude");
		    	    					 String startTime = jObj.getString("start_time");
		    	    					 String address = jObj.getString("address");
		    	    					 
		    	    					 long startTimeMillis = new DateOperations().getMilliseconds(startTime);

		    	    					 startTime = new DateOperations().getSixtySecAfterCurrentTime();
		    	    					 
		    	    					 if(HomeActivity.appInterruptHandler != null){
		    	    						 if(UserMode.DRIVER == HomeActivity.userMode){
		    	    							 if(DriverScreenMode.D_INITIAL == HomeActivity.driverScreenMode ||
		    	    								DriverScreenMode.D_REQUEST_ACCEPT == HomeActivity.driverScreenMode ||
		    	    								DriverScreenMode.D_RIDE_END == HomeActivity.driverScreenMode){
		    	    								 
		    	    								 addDriverRideRequest(this, engagementId, userId, ""+latitude, ""+longitude, 
					    	    							 startTime, address);
					    	    					 startRing(this);
					    	    					 RequestTimeoutTimerTask requestTimeoutTimerTask = new RequestTimeoutTimerTask(this, engagementId);
					    	    					 requestTimeoutTimerTask.startTimer(0, 20000, startTimeMillis, 60000);
					    	    					 
		    	    								 notificationManagerResume(this, "You have got a new ride request.", true);
				    	    						 HomeActivity.appInterruptHandler.onNewRideRequest();
		    	    							 }
		    	    						 }
		    	    					 }
		    	    					 else{
		    	    						 notificationManager(this, "You have got a new ride request.", true);
		    	    						 
		    	    						 addDriverRideRequest(this, engagementId, userId, ""+latitude, ""+longitude, 
			    	    							 startTime, address);
			    	    					 startRing(this);
			    	    					 RequestTimeoutTimerTask requestTimeoutTimerTask = new RequestTimeoutTimerTask(this, engagementId);
			    	    					 requestTimeoutTimerTask.startTimer(0, 20000, startTimeMillis, 60000);
		    	    					 }
		    	    					 
	    	    					 
	    	    				 }
	    	    				 else if(PushFlags.RIDE_ACCEPTED.getOrdinal() == flag){
									if (HomeActivity.appInterruptHandler != null) {
										HomeActivity.appInterruptHandler.rideRequestAcceptedInterrupt(jObj);
										notificationManagerResume(this, "Your request has been accepted", false);
									} else {
										notificationManager(this, "Your request has been accepted", false);
									}
	    	    				 }
	    	    				 else if(PushFlags.ASSIGNING_DRIVERS.getOrdinal() == flag){
										if (HomeActivity.appInterruptHandler != null) {
											HomeActivity.appInterruptHandler.onAssigningDriversPushReceived();
											notificationManagerResume(this, "Assigning drivers", false);
										} else {
											notificationManager(this, "Assigning drivers", false);
										}
		    	    				 }
	    	    				 else if(PushFlags.REQUEST_CANCELLED.getOrdinal() == flag){
    	    						 
	    	    					 String engagementId = jObj.getString("engagement_id");
	    	    					 clearNotifications(this);
	    	    					 deleteDriverRideRequest(GCMIntentService.this, engagementId);
	    	    					 
	    	    					 stopRing();
	    	    					 
	    	    					 if(HomeActivity.appInterruptHandler != null){
	    	    						 HomeActivity.appInterruptHandler.onCancelRideRequest(engagementId, false);
	    	    					 }
	    	    					 
	    	    				 }
	    	    				 else if(PushFlags.RIDE_ACCEPTED_BY_OTHER_DRIVER.getOrdinal() == flag){
    	    						 
	    	    					 String engagementId = jObj.getString("engagement_id");
	    	    					 clearNotifications(this);
	    	    					 deleteDriverRideRequest(GCMIntentService.this, engagementId);
	    	    					 
	    	    					 stopRing();
	    	    					 
	    	    					 if(HomeActivity.appInterruptHandler != null){
	    	    						 HomeActivity.appInterruptHandler.onCancelRideRequest(engagementId, true);
	    	    					 }
	    	    					 
	    	    					 
	    	    				 }
	    	    				else if(PushFlags.REQUEST_TIMEOUT.getOrdinal() == flag){
    	    						 
	    	    					 String engagementId = jObj.getString("engagement_id");
	    	    					 clearNotifications(this);
	    	    					 deleteDriverRideRequest(GCMIntentService.this, engagementId);
	    	    					 
	    	    					 stopRing();
	    	    					 
	    	    					 if(HomeActivity.appInterruptHandler != null){
	    	    						 HomeActivity.appInterruptHandler.onRideRequestTimeout(engagementId);
	    	    					 }
	    	    					 
	    	    				 }
	    	    				 else if(PushFlags.RIDE_STARTED.getOrdinal() == flag){

	    	    					 if (HomeActivity.appInterruptHandler != null) {
	    	    						 notificationManagerResume(this, "Your ride has started.", false);
	    	    						 HomeActivity.appInterruptHandler.startRideForCustomer(0);
	    	    					 }
	    	    					 else{
	    	    						 String SHARED_PREF_NAME = "myPref",
	    	    						 SP_CUSTOMER_SCREEN_MODE = "customer_screen_mode",
	    	    								 P_IN_RIDE = "P_IN_RIDE";
	    	    						 SharedPreferences pref = getSharedPreferences(SHARED_PREF_NAME, 0);
	    	    						 Editor editor = pref.edit();
	    	    						 editor.putString(SP_CUSTOMER_SCREEN_MODE, P_IN_RIDE);
	    	    						 editor.commit();
	    	    						 
	    	    						 notificationManager(this, "Your ride has started.", false);
	    	    					 }
	    	    				 }
	    	    				 else if(PushFlags.RIDE_ENDED.getOrdinal() == flag){

	    	    					 Data.totalDistance = jObj.getDouble("distance_travelled");
	    	    					 Data.totalFare = jObj.getDouble("fare");
	    	    					 Data.waitTime = jObj.getString("wait_time");
	    	    					 
	    	    					 if (HomeActivity.appInterruptHandler != null) {
	    	    						 if(PassengerScreenMode.P_IN_RIDE == HomeActivity.passengerScreenMode){
	    	    							 notificationManagerResume(this, "Your ride has ended.", false);
		    	    						 HomeActivity.appInterruptHandler.customerEndRideInterrupt(jObj);
	    	    						 }
	    	    						
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
	    	    						 
	    	    						 
	    	    						 notificationManager(this, "Your ride has ended.", false);
	    	    					 }
	    	    				 }
	    	    				 else if(PushFlags.RIDE_REJECTED_BY_DRIVER.getOrdinal() == flag){
	    	    					 if (HomeActivity.appInterruptHandler != null) {
	    	    						 HomeActivity.appInterruptHandler.startRideForCustomer(1);
	    	    					 }
	    	    				 }
	    	    				 else if(PushFlags.WAITING_STARTED.getOrdinal() == flag || PushFlags.WAITING_ENDED.getOrdinal() == flag){
	    	    					 String message1 = jObj.getString("message");
	    	    					 if (HomeActivity.activity == null) {
		    	    					 notificationManager(this, ""+message1, false);
	    	    					 }
	    	    					 else{
		    	    					 notificationManagerResume(this, ""+message1, false);
	    	    					 }
	    	    				 }
	    	    				 else if(PushFlags.NO_DRIVERS_AVAILABLE.getOrdinal() == flag){
	    	    					 String log = jObj.getString("log");
	    	    					 if (HomeActivity.appInterruptHandler != null) {
	    	    						 HomeActivity.appInterruptHandler.onNoDriversAvailablePushRecieved(log);
	    	    					 }
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

	    
	    
	    public static MediaPlayer mediaPlayer;
	    public static Vibrator vibrator;
	    
		public static void startRing(Context context){
			try {
				stopRing();
				vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
				if(vibrator.hasVibrator()){
					long[] pattern = {0, 1350, 3900, 
											1350, 3900, 
											1350, 3900, 
											1350, 3900, 
											1350, 3900, 
											1350, 3900, 
											1350, 3900, 
											1350, 3900, 
											1350, 3900, 
											1350, 3900, 
											1350, 3900 };
					vibrator.vibrate(pattern, -1);
				}
				AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
//				am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
				am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
				mediaPlayer = MediaPlayer.create(context, R.raw.telephone_ring);
				mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				    @Override
				    public void onCompletion(MediaPlayer mp) {
						mediaPlayer.stop();
				    	mediaPlayer.reset();
				    	mediaPlayer.release();
				    	vibrator.cancel();
				    }
				});
				mediaPlayer.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
		public static void stopRing(){
			try {
				if(vibrator != null){
					vibrator.cancel();
				}
				if(mediaPlayer != null && mediaPlayer.isPlaying()){
					mediaPlayer.stop();
					mediaPlayer.reset();
					mediaPlayer.release();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally{
				vibrator = null;
				mediaPlayer = null;
			}
		}

	    
	    
	    
	    public void addDriverRideRequest(Context context, String engagementId, String userId, String latitude, String longitude, 
	    		String startTime, String address){
	    	try {
				Database2 database2 = new Database2(context);
				 database2.insertDriverRequest(engagementId, userId, latitude, longitude, startTime, address);
				 database2.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
	    }
	    
	    public int deleteDriverRideRequest(Context context, String engagementId){
	    	try {
				Database2 database2 = new Database2(context);
				int count = database2.deleteDriverRequest(engagementId);
				database2.close();
				return count;
			} catch (Exception e) {
				e.printStackTrace();
			}
	    	return 0;
	    }
	    
	    class RequestTimeoutTimerTask{
	    	
	    	public Timer timer;
	    	public TimerTask timerTask;
	    	public Context context;
	    	
	    	public String engagementId;
	    	
	    	public long startTime, lifeTime, endTime, period, executionTime;
	    	
	    	public RequestTimeoutTimerTask(Context context, String engagementId){
	    		this.context = context;
	    		this.engagementId = engagementId;
	    	}
	    	
	    	public void startTimer(long delay, long period, long startTime, long lifeTime){
	    		stopTimer();
	    		
	    		this.startTime = startTime;
	    		this.lifeTime = lifeTime;
	    		this.endTime = startTime + lifeTime;
	    		this.period = period;
	    		this.executionTime = -1;
	    		
	    		timer = new Timer();
	    		timerTask = new TimerTask() {
	    			@Override
	    			public void run() {
	    				long start = System.currentTimeMillis();
	    				
	    				if(executionTime == -1){
							executionTime = RequestTimeoutTimerTask.this.startTime;
						}
	    				
		    			if(executionTime >= RequestTimeoutTimerTask.this.endTime){
		    				int count = deleteDriverRideRequest(context, engagementId);
		    				if(count > 0){
		    					if(HomeActivity.appInterruptHandler != null){
			    					HomeActivity.appInterruptHandler.onRideRequestTimeout(engagementId);
			    				}
		    					clearNotifications(context);
		    					stopRing();
		    				}
		    				stopTimer();
		    			}
		    			long stop = System.currentTimeMillis();
					    long elapsedTime = stop - start;
					    if(executionTime != -1){
					    	if(elapsedTime >= RequestTimeoutTimerTask.this.period){
					    		executionTime = executionTime + elapsedTime;
					    	}
					    	else{
					    		executionTime = executionTime + RequestTimeoutTimerTask.this.period;
					    	}
					    }
					    Log.i("RequestTimeoutTimerTask execution", "="+(RequestTimeoutTimerTask.this.endTime - executionTime));
	    			}
	    		};
	    		timer.scheduleAtFixedRate(timerTask, delay, period);
	    	}
	    	
	    	public void stopTimer(){
	    		try{
	    			Log.e("RequestTimeoutTimerTask","stopTimer");
	    			startTime = 0;
	    			lifeTime = 0;
	    			if(timerTask != null){
	    				timerTask.cancel();
	    				timerTask = null;
	    			}
	    			if(timer != null){
	    				timer.cancel();
	    				timer.purge();
	    				timer = null;
	    			}
	    		} catch(Exception e){
	    			e.printStackTrace();
	    		}
	    	}
	    	
	    	
	    }
	    
}






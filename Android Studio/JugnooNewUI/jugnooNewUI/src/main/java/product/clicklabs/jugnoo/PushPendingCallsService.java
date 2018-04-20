package product.clicklabs.jugnoo;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;

import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;


public class PushPendingCallsService extends Service {
	
	public PushPendingCallsService() {
		Log.e("PushPendinsCallsService", " instance created");
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
        	Log.i("PushPendinsCallsService started", "=======");
            Prefs.with(this).save(SPLabels.PENDING_CALLS_RETRY_COUNT, 0);
            startUploadPathAlarm();
        } catch(Exception e){
        	e.printStackTrace();
        }
    }
    
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	super.onStartCommand(intent, flags, startId);
    	return Service.START_STICKY;
    }
    
    
    @Override
    public void onTaskRemoved(Intent rootIntent) {
    	try {
	    	Log.e("onTaskRemoved","="+rootIntent);
	    	Intent restartService = new Intent(getApplicationContext(), this.getClass());
	    	restartService.setPackage(getPackageName());
	    	PendingIntent restartServicePI = PendingIntent.getService(getApplicationContext(), 1, restartService, PendingIntent.FLAG_ONE_SHOT);
	    	AlarmManager alarmService = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
	    	alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, restartServicePI);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
 
    @Override
    public void onDestroy() {
    	Log.e("PushPendinsCallsService onDestroy", "=");
        cancelUploadPathAlarm(this);
    }




    public static final int PUSH_PENDING_CALLS_PI_REQUEST_CODE = 112;
    public static final String PUSH_PENDING_CALLS = BuildConfig.APPLICATION_ID+".PUSH_PENDING_CALLS";
    public static final long ALARM_REPEAT_INTERVAL = 60000;


    public void startUploadPathAlarm() {
        // check task is scheduled or not
        boolean alarmUp = (PendingIntent.getBroadcast(this, PUSH_PENDING_CALLS_PI_REQUEST_CODE,
            new Intent(this, PushPendingCallsReceiver.class).setAction(PUSH_PENDING_CALLS),
            PendingIntent.FLAG_NO_CREATE) != null);

        if (alarmUp) {
            cancelUploadPathAlarm(this);
        }

        Intent intent = new Intent(this, PushPendingCallsReceiver.class);
        intent.setAction(PUSH_PENDING_CALLS);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, PUSH_PENDING_CALLS_PI_REQUEST_CODE,
            intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), ALARM_REPEAT_INTERVAL, pendingIntent);
    }

    public static void cancelUploadPathAlarm(Context context) {
        Intent intent = new Intent(context, PushPendingCallsReceiver.class);
        intent.setAction(PUSH_PENDING_CALLS);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, PUSH_PENDING_CALLS_PI_REQUEST_CODE,
            intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

}

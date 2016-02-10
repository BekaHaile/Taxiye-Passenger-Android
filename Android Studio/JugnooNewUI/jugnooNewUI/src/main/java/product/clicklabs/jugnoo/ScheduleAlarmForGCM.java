package product.clicklabs.jugnoo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import product.clicklabs.jugnoo.utils.Prefs;

/**
 * Created by shankar on 2/10/16.
 */
public class ScheduleAlarmForGCM {

	private Context context;
	private PendingIntent pendingIntent;
	private final long INTERVAL = 24 * 60 * 60 * 1000; //24 * 60 * 60 * 1000
	private final String LAST_TIME_RESTART = "ScheduleAlarmForGCM_last_time_restart";

	public ScheduleAlarmForGCM(Context context){
		this.context = context;
		pendingIntent = PendingIntent.getBroadcast(context, 0, getIntent(), 0);
	}

	private Intent getIntent(){
		return new Intent(context, RefreshGCMReceiver.class);
	}

	public void start() {
		long lastTimeRestart = Prefs.with(context).getLong(LAST_TIME_RESTART, System.currentTimeMillis()-(5* INTERVAL));
		long currentTime = System.currentTimeMillis();
		if(lastTimeRestart + (4* INTERVAL) <= currentTime){
			cancel();
			AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, currentTime + 2000, INTERVAL, pendingIntent);
			Prefs.with(context).save(LAST_TIME_RESTART, currentTime);
		} else{
		}
	}

	public void cancel() {
		AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		manager.cancel(pendingIntent);
	}

}

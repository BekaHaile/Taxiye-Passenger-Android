package product.clicklabs.jugnoo;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class DriverServiceOperations {

	private static int DRIVER_SERVICE_RESTART_PI_REQUEST_CODE = 123;
	private static final String START_SERVICE = "product.clicklabs.jugnoo.START_SERVICE";
	
	
	public PendingIntent createPendingIntent(Context context){
		Intent receiverIntent = new Intent(context, DriverServiceRestartAlarmReceiver.class);
		receiverIntent.setAction(START_SERVICE);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, DRIVER_SERVICE_RESTART_PI_REQUEST_CODE, 
				receiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		return pendingIntent;
	}
	
	public void cancelAlarm(Context context){
		PendingIntent pendingIntent = createPendingIntent(context);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);
		pendingIntent.cancel();
	}
	
	public void setupAlarm(Context context, long milliseconds){
		cancelAlarm(context);
		PendingIntent pendingIntent = createPendingIntent(context);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, milliseconds, pendingIntent);
	}
	
	
	public Calendar getRestartSetCalendar(){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.add(Calendar.MINUTE, 6);
		return calendar;
	}
	
	
	
	
	
	public void startDriverService(Context context){
		Database2 database2 = new Database2(context);
		try{
			database2.updateDriverServiceRun(Database2.YES);
			database2.updateDriverServiceTimeToRestart(0);
			
			context.stopService(new Intent(context, DriverLocationUpdateService.class));
			context.startService(new Intent(context, DriverLocationUpdateService.class));
			
			cancelAlarm(context);
		} catch(Exception e){
			e.printStackTrace();
		}
		finally{
			database2.close();
		}
	}
	
	
	
	
	
	
	
	
	public void stopAndScheduleDriverService(Context context){
		Database2 database2 = new Database2(context);
		try{
			database2.updateDriverServiceRun(Database2.NO);
			
			context.stopService(new Intent(context, DriverLocationUpdateService.class));
			Calendar calendar = getRestartSetCalendar();
			setupAlarm(context, calendar.getTimeInMillis());
			database2.updateDriverServiceTimeToRestart(calendar.getTimeInMillis());
			Log.e("stopAndScheduleDriverService", "=current = "+ System.currentTimeMillis() + " adv = " + getRestartSetCalendar().getTimeInMillis());
		} catch(Exception e){
			e.printStackTrace();
		}
		finally{
			database2.close();
		}
	}
	
	
	
	
	
	
	
	
	
	public void rescheduleDriverService(Context context){
		Database2 database2 = new Database2(context);
		try{
			long timeToRestartService = database2.getDriverServiceTimeToRestart();
			
			context.stopService(new Intent(context, DriverLocationUpdateService.class));
			
			setupAlarm(context, timeToRestartService);
		} catch(Exception e){
			e.printStackTrace();
		}
		finally{
			database2.close();
		}
	}
	
	
	
	
	
	public void stopService(Context context){
		Database2 database2 = new Database2(context);
		try{
			cancelAlarm(context);
			context.stopService(new Intent(context, DriverLocationUpdateService.class));
			database2.updateDriverServiceRun(Database2.NEVER);
		} catch(Exception e){
			e.printStackTrace();
		}
		finally{
			database2.close();
		}
	}
	
	
	
	public void checkStartService(Context context){
		Database2 database2 = new Database2(context);
		try{
			final String serviceRestartOnReboot = database2.getDriverServiceRun();
			if(Database2.YES.equalsIgnoreCase(serviceRestartOnReboot)){
				context.stopService(new Intent(context, DriverLocationUpdateService.class));
				context.startService(new Intent(context, DriverLocationUpdateService.class));
				cancelAlarm(context);
	    	}
			Log.e("checkStartService", "=serviceRestartOnReboot = "+serviceRestartOnReboot);
		} catch(Exception e){
			e.printStackTrace();
		}
		finally{
			database2.close();
		}
	}
	
	
}

package product.clicklabs.jugnoo.home;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.SystemClock;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.datastructure.PassengerScreenMode;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;

public class LocationUpdateService extends Service {

	private final String TAG = LocationUpdateService.class.getSimpleName();
	private LocationFetcherBG locationFetcherBG;

	public LocationUpdateService() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			try{
				Log.i(TAG, "customonReceive lat="+intent.getDoubleExtra(Constants.KEY_LATITUDE, 0));
				Log.i(TAG, "customonReceive lng="+intent.getDoubleExtra(Constants.KEY_LONGITUDE, 0));
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	};

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "service onStartCommand");

		registerReceiver(receiver, new IntentFilter(Constants.ACTION_LOCATION_UPDATE));

		long locationUpdateInterval = Prefs.with(this).getLong(Constants.KEY_SP_LOCATION_UPDATE_INTERVAL,
				Constants.LOCATION_UPDATE_INTERVAL);

		if (locationFetcherBG != null) {
			locationFetcherBG.destroy();
			locationFetcherBG = null;
		}
		locationFetcherBG = new LocationFetcherBG(this, locationUpdateInterval, LocationReceiverBG.class);


		return Service.START_STICKY;
	}

	@Override
	public void onDestroy() {
		if(receiver != null){
			unregisterReceiver(receiver);
		}
		if (locationFetcherBG != null) {
			locationFetcherBG.destroy();
			locationFetcherBG = null;
		}
	}

	@Override
	public void onTaskRemoved(Intent rootIntent) {
		try {
			if(PassengerScreenMode.P_IN_RIDE.getOrdinal() == Prefs.with(this)
					.getInt(Constants.SP_CURRENT_STATE, PassengerScreenMode.P_INITIAL.getOrdinal())) {
				Intent restartService = new Intent(getApplicationContext(), this.getClass());
				restartService.setPackage(getPackageName());
				PendingIntent restartServicePI = PendingIntent.getService(getApplicationContext(), 1, restartService, PendingIntent.FLAG_ONE_SHOT);
				AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
				alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, restartServicePI);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

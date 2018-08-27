package product.clicklabs.jugnoo.home;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Pair;

import java.util.HashMap;

import product.clicklabs.jugnoo.AccessTokenGenerator;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.GCMIntentService;
import product.clicklabs.jugnoo.LocationFetcher;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.datastructure.PassengerScreenMode;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class LocationUpdateService extends Service {

	private final String TAG = LocationUpdateService.class.getSimpleName();
	private LocationFetcherBG locationFetcherBG = null;
	private CustomLocationReceiver locationReceiver;
	private boolean oneShot;

	public LocationUpdateService() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		throw new UnsupportedOperationException("Not yet implemented");
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "service onStartCommand");
		try {
			if(intent.getIntExtra(Constants.STOP_FOREGROUND, 0) == 1){
				stopForeground(true);
				stopSelf();
				return Service.START_NOT_STICKY;
			}
			if(intent.getIntExtra(Constants.ACTION_UPDATE_STATE, 0) == 1){
				updateState();
				return Service.START_STICKY;
			}
			MyApplication.getInstance().initializeServerURL(this);
			try {
				oneShot = intent.getBooleanExtra(Constants.KEY_ONE_SHOT, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
			locationReceiver = new CustomLocationReceiver(oneShot);
			registerReceiver(locationReceiver, new IntentFilter(Constants.ACTION_LOCATION_UPDATE));

			long locationUpdateInterval = Prefs.with(this).getLong(Constants.KEY_SP_CUSTOMER_LOCATION_UPDATE_INTERVAL,
					Constants.LOCATION_UPDATE_INTERVAL);

			if (locationFetcherBG != null) {
				locationFetcherBG.destroy();
				locationFetcherBG = null;
			}
			locationFetcherBG = new LocationFetcherBG(this, locationUpdateInterval);
			if(!oneShot) {
				startForeground(101, getNotification());
			}

			return Service.START_STICKY;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Service.START_NOT_STICKY;
	}

	private void updateState(){
		if(!oneShot) {
			startForeground(101, getNotification());
		}
	}

	@Override
	public void onDestroy() {
		unregisterRec();
	}

	private void unregisterRec(){
		if(locationReceiver != null){
			unregisterReceiver(locationReceiver);
		}
		if (locationFetcherBG != null) {
			locationFetcherBG.destroy();
			locationFetcherBG = null;
		}
	}

	@Override
	public void onTaskRemoved(Intent rootIntent) {
		try {
			int mode = Prefs.with(this).getInt(Constants.SP_CURRENT_STATE, PassengerScreenMode.P_INITIAL.getOrdinal());
			if(!oneShot && (PassengerScreenMode.P_REQUEST_FINAL.getOrdinal() == mode
					|| PassengerScreenMode.P_DRIVER_ARRIVED.getOrdinal() == mode
					|| PassengerScreenMode.P_IN_RIDE.getOrdinal() == mode)) {
				Log.i(TAG, "service onTaskRemoved");
//				stopSelf();
//				Intent restartService = new Intent(getApplicationContext(), this.getClass());
//				restartService.setPackage(getPackageName());
//				restartService.putExtra(Constants.KEY_ONE_SHOT, false);
//				PendingIntent restartServicePI = PendingIntent.getService(getApplicationContext(), 1, restartService, PendingIntent.FLAG_ONE_SHOT);
//				AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//				if (alarmService != null) {
//					alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 5000, restartServicePI);
//				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class CustomLocationReceiver extends BroadcastReceiver{

		private boolean oneShot, emergencyLoc;

		public CustomLocationReceiver(boolean oneShot){
			this.oneShot = oneShot;
			emergencyLoc = false;
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			try{
				double latitude = intent.getDoubleExtra(Constants.KEY_LATITUDE, 0);
				double longitude = intent.getDoubleExtra(Constants.KEY_LONGITUDE, 0);
				if(Double.compare(latitude, 0) == 0 && Double.compare(longitude, 0) == 0){
					latitude = LocationFetcher.getSavedLatFromSP(context);
					longitude = LocationFetcher.getSavedLngFromSP(context);
				}
				emergencyLoc = intent.getBooleanExtra(Constants.KEY_EMERGENCY_LOC, false);
				Log.i(TAG, "customonReceive lat=" + latitude + ", lng=" + longitude);

				if(oneShot && locationFetcherBG != null){
					locationFetcherBG.destroy();
					locationFetcherBG = null;
				}

				Pair<String, Integer> pair = AccessTokenGenerator.getAccessTokenPair(context);
				if (!"".equalsIgnoreCase(pair.first)) {
					HashMap<String, String> params = new HashMap<>();
					params.put(Constants.KEY_ACCESS_TOKEN, pair.first);
					params.put(Constants.KEY_LATITUDE, String.valueOf(latitude));
					params.put(Constants.KEY_LONGITUDE, String.valueOf(longitude));

					new HomeUtil().putDefaultParams(params);
					if(oneShot || emergencyLoc){
						RestClient.getApiService().saveCustomerEmergencyLocation(params, new Callback<SettleUserDebt>() {
							@Override
							public void success(SettleUserDebt settleUserDebt, Response response) {
								String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
								Log.i(TAG, "updateCustomerLocation responseStr=" + responseStr);
								if(oneShot) {
									stopSelf();
								}
							}

							@Override
							public void failure(RetrofitError error) {
								Log.e(TAG, "updateCustomerLocation error=" + error);
								if(oneShot) {
									stopSelf();
								}
							}
						});
						emergencyLoc = false;
					} else{
						String engagementId = Prefs.with(context).getString(Constants.SP_CURRENT_ENGAGEMENT_ID, "");
						Log.i(TAG, "customonReceive engagementId=" + engagementId);
						if(!"".equalsIgnoreCase(engagementId)) {
							params.put(Constants.KEY_ENGAGEMENT_ID, engagementId);
							Log.i(TAG, "customonReceive params=" + params);

							new HomeUtil().putDefaultParams(params);
							RestClient.getApiService().updateCustomerRideLocation(params, new Callback<SettleUserDebt>() {
								@Override
								public void success(SettleUserDebt settleUserDebt, Response response) {
									String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
									Log.i(TAG, "updateCustomerLocation responseStr=" + responseStr);
								}

								@Override
								public void failure(RetrofitError error) {
									Log.e(TAG, "updateCustomerLocation error=" + error);
								}
							});
						}
					}

				}
			} catch (Exception e){
				e.printStackTrace();
			}
		}

	}

	private Notification getNotification() {
		GCMIntentService.setSilentNotificationChannel(this, Constants.NOTIF_CHANNEL_SILENT);
		long when = System.currentTimeMillis();
		Intent notificationIntent;
		if(HomeActivity.appInterruptHandler != null){
			notificationIntent = new Intent(this, HomeActivity.class);
		} else{
			notificationIntent = new Intent(this, SplashNewActivity.class);
		}

		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent intent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		int mode = Prefs.with(this).getInt(Constants.SP_CURRENT_STATE, PassengerScreenMode.P_INITIAL.getOrdinal());
		int strRes = R.string.ride_in_progress;
		if(PassengerScreenMode.P_REQUEST_FINAL.getOrdinal() == mode){
			strRes = R.string.driver_enroute;
		} else if(PassengerScreenMode.P_DRIVER_ARRIVED.getOrdinal() == mode){
			strRes = R.string.arrived_at_pickup;
		}

		NotificationCompat.Builder builder = new NotificationCompat.Builder(this, Constants.NOTIF_CHANNEL_SILENT);
		builder.setAutoCancel(true);
		builder.setContentTitle(getString(R.string.app_name));
		builder.setStyle(new NotificationCompat.BigTextStyle().bigText(getString(strRes)));
		builder.setContentText(getString(strRes));
		builder.setTicker(getString(R.string.app_name));
		builder.setWhen(when);
		builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
		builder.setSmallIcon(R.mipmap.notification_icon);
		builder.setChannelId(Constants.NOTIF_CHANNEL_SILENT);
		builder.setContentIntent(intent);
		builder.setPriority(Notification.PRIORITY_DEFAULT);
		return builder.build();
	}

}

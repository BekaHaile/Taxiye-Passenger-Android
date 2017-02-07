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
import android.util.Pair;

import java.util.HashMap;

import product.clicklabs.jugnoo.AccessTokenGenerator;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.LocationFetcher;
import product.clicklabs.jugnoo.MyApplication;
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

			return Service.START_STICKY;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Service.START_NOT_STICKY;
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
				stopSelf();
				Intent restartService = new Intent(getApplicationContext(), this.getClass());
				restartService.setPackage(getPackageName());
				restartService.putExtra(Constants.KEY_ONE_SHOT, false);
				PendingIntent restartServicePI = PendingIntent.getService(getApplicationContext(), 1, restartService, PendingIntent.FLAG_ONE_SHOT);
				AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
				alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 5000, restartServicePI);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class CustomLocationReceiver extends BroadcastReceiver{

		private final String TAG = CustomLocationReceiver.class.getSimpleName();
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
					LocationFetcher lf = new LocationFetcher(context);
					latitude = lf.getSavedLatFromSP();
					longitude = lf.getSavedLngFromSP();
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

}

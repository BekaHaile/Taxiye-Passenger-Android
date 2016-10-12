package product.clicklabs.jugnoo.home.trackinglog;

import android.app.IntentService;
import android.content.Intent;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.utils.Log;

public class TrackingLogSyncIntentService extends IntentService {

	public TrackingLogSyncIntentService(){
		this("TrackingLogSyncIntentService");
	}

	public TrackingLogSyncIntentService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		try{
			Log.i("TrackingLogSyncIntentService", "on handle intent");
			String accessToken = intent.getStringExtra(Constants.KEY_ACCESS_TOKEN);
			new TrackingLogHelper(this).checkAndUploadRemainingFiles(accessToken);
		} catch (Exception e){
			e.printStackTrace();
		}
		stopSelf();
	}
}

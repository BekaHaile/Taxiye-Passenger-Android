package product.clicklabs.jugnoo.home;

import android.app.IntentService;
import android.content.Intent;
import android.util.Pair;

import product.clicklabs.jugnoo.AccessTokenGenerator;
import product.clicklabs.jugnoo.Constants;

public class SyncIntentService extends IntentService {

	public SyncIntentService(){
		this("SyncIntentService");
	}

	public SyncIntentService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		try{
			String startTime = intent.getStringExtra(Constants.KEY_START_TIME);
			String endTime = intent.getStringExtra(Constants.KEY_END_TIME);
			Pair<String, Integer> pair = AccessTokenGenerator.getAccessTokenPair(this);
//			if (PermissionCommon.isGranted( Manifest.permission.READ_SMS,this) && !"".equalsIgnoreCase(pair.first)) {
//				new FetchAndSendMessages(this, pair.first, true, startTime, endTime).syncUp();
//			}
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}

package product.clicklabs.jugnoo.home;

import android.app.IntentService;
import android.content.Intent;

public class SyncIntentService extends IntentService {

	public SyncIntentService(){
		this("SyncIntentService");
	}

	public SyncIntentService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
	}
}

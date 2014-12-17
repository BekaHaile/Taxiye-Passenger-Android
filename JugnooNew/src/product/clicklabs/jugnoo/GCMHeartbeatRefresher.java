package product.clicklabs.jugnoo;

import product.clicklabs.jugnoo.utils.Log;
import android.content.Context;
import android.content.Intent;

public class GCMHeartbeatRefresher {

	public static void refreshGCMHeartbeat(Context context){
		try {
			context.sendBroadcast(new Intent("com.google.android.intent.action.GTALK_HEARTBEAT"));
			context.sendBroadcast(new Intent("com.google.android.intent.action.MCS_HEARTBEAT"));
			Log.i("GCMHeartbeatRefresher ", "done");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}

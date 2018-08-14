package product.clicklabs.jugnoo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.analytics.CampaignTrackingReceiver;

import java.util.Set;

import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;

/**
 * Created by shankar on 3/22/16.
 */
public class CustomCampaignTrackingReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		// Pass the intent to other receivers.
		try {
			Bundle bundle = intent.getExtras();
			Set<String> set = bundle.keySet();
			for(String key : set){
				Log.e("CustomCampaignTrackingReceiver", key+" => "+bundle.get(key));
			}
			Prefs.with(context).save(Constants.SP_INSTALL_REFERRER_CONTENT, intent.getStringExtra(Constants.KEY_REFERRER));
		} catch (Exception e) {
			e.printStackTrace();
		}


		// When you're done, pass the intent to the Google Analytics receiver.
		new CampaignTrackingReceiver().onReceive(context, intent);
//		new com.google.ads.conversiontracking.InstallReceiver().onReceive(context, intent);

	}
}

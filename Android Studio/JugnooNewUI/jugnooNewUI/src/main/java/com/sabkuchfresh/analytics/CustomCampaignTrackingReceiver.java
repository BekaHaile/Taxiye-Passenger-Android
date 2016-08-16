package com.sabkuchfresh.analytics;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.analytics.CampaignTrackingReceiver;
import com.sabkuchfresh.utils.Constants;
import com.sabkuchfresh.utils.Log;
import com.sabkuchfresh.utils.Prefs;

import java.util.Set;

/**
 * Created by gurmail on 23/05/16.
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
    }
}
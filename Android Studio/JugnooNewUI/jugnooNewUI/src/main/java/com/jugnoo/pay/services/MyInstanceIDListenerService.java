package com.jugnoo.pay.services;

/**
 * Created by socomo20 on 8/18/15.
 */

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import product.clicklabs.jugnoo.utils.Prefs;

public class MyInstanceIDListenerService extends FirebaseInstanceIdService {

    private static final String TAG = "MyInstanceIDLS";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is also called
     * when the InstanceID token is initially generated, so this is where
     * you retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        try {
            // Get updated InstanceID token.
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            Log.d(TAG, "Refreshed token: " + refreshedToken);

            Prefs.with(this).save(Constants.SP_DEVICE_TOKEN, refreshedToken);

                Intent intent = new Intent(Constants.INTENT_ACTION_DEVICE_TOKEN_UPDATE);
                intent.putExtra(Constants.KEY_DEVICE_TOKEN, refreshedToken);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
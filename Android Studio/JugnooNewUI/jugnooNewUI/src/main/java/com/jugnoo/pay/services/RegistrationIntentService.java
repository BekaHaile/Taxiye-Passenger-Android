package com.jugnoo.pay.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.jugnoo.pay.R;
import com.jugnoo.pay.utils.AppConstants;

import java.io.IOException;

/**
 * Created by ashutosh on 3/1/2016.
 */
public class RegistrationIntentService extends IntentService {
    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};


    public RegistrationIntentService() {
        super(TAG);
    }

    //in this method we perform that task which takes longer duration of time
    @Override
    protected void onHandleIntent(Intent intent) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        Toast.makeText(getApplicationContext(), "inside on handle intent", Toast.LENGTH_SHORT).show();

        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.google_project_id),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            sendRegistrationToServer(token);

            subscribeTopics(token);

            Log.d(TAG, "GCM TOKEN" + token);
            sharedPreferences.edit().putBoolean(AppConstants.SENT_TOKEN_TO_SERVER, true).apply();


        } catch (Exception e) {

            Log.d(TAG, "Failed to complete token refresh", e);

            sharedPreferences.edit().putBoolean(AppConstants.SENT_TOKEN_TO_SERVER, false).apply();
        }

        Intent registrationComplete = new Intent(AppConstants.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);


    }


    private void sendRegistrationToServer(String token) {


    }

    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
}

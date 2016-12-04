package com.jugnoo.pay.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.jugnoo.pay.R;
import com.jugnoo.pay.activities.SplashActivity;

/**
 * Created by cl_macmini_86 on 4/13/16.
 */
public class DeviceTokenGenerator {

    private static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    GoogleCloudMessaging gcm;
    String regId;

    public DeviceTokenGenerator(Context context) {
        gcm = GoogleCloudMessaging.getInstance(context);
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    public void generateDeviceToken(Context context, IDeviceTokenReceiver deviceTokenReceiver) {
        regId = getRegistrationId(context);
        if (regId.isEmpty()) {
            registerInBackground(context, deviceTokenReceiver);
        } else {
            deviceTokenReceiver.deviceTokenReceived(regId);
        }
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i("dfs", "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i("sdfs", "App version changed.");
            return "";
        }
        return registrationId;
    }

    private void registerInBackground(final Context context, final IDeviceTokenReceiver deviceTokenReceiver) {

            if (gcm == null) {
                gcm = GoogleCloudMessaging.getInstance(context);
            }
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        //regId = gcm.register("278984430493");
                        regId = gcm.register((context.getString(R.string.google_project_id)));
                        setRegistrationId(context, regId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        deviceTokenReceiver.deviceTokenReceived(regId);
                    }
                }
            }).start();

    }

    private void setRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, getAppVersion(context));
        editor.commit();
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return context.getSharedPreferences(SplashActivity.class.getSimpleName(), Context.MODE_PRIVATE);
    }
}

package com.jugnoo.pay.utils;

import android.app.Application;

import com.google.firebase.iid.FirebaseInstanceId;

/**
 * Created by ankit on 28/09/16.
 */
public class MyApplication extends Application{

    private static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public String getDeviceToken(){
        String deviceToken = Prefs.with(this).getString(Constants.SP_DEVICE_TOKEN, "not_found");
        if(deviceToken.equalsIgnoreCase("not_found")){
            deviceToken = FirebaseInstanceId.getInstance().getToken();
            if(deviceToken == null){
                deviceToken = "not_found";
            }
        }
        return deviceToken;
    }

}

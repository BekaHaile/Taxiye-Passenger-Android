package com.fugu.utils;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import com.fugu.BuildConfig;
import com.fugu.FuguConfig;
import com.fugu.agent.database.AgentCommonData;
import com.fugu.database.CommonData;

public class UniqueIMEIID {
    public static String getUniqueIMEIId(Context activity) {
        String android_id = "";
        try {
            try {
                android_id = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
            } catch (Exception e) {
                if(FuguConfig.DEBUG)
                    e.printStackTrace();
            }
            android_id = TextUtils.isEmpty(android_id) ? Build.SERIAL : android_id;
        } catch (Exception e) {
            if(FuguConfig.DEBUG)
                e.printStackTrace();
        }

        android_id = TextUtils.isEmpty(android_id) ? "12345" : android_id;

        if(!TextUtils.isEmpty(AgentCommonData.getPackageName()))
            android_id = android_id + AgentCommonData.getPackageName();
        else
            android_id = android_id + CommonData.getPackageName(activity);

        //String iid = InstanceID.getInstance(activity).getId();
        return android_id;
    }
}

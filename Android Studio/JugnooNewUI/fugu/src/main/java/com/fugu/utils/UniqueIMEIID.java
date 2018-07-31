package com.fugu.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

public class UniqueIMEIID {

	public static String getUniqueIMEIId(Context activity){
		try {
			String imei;
			if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
				TelephonyManager telephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
				imei = telephonyManager.getDeviceId();
			} else {
				imei = Build.SERIAL;
			}
			return imei;
		} catch (Exception e) {
			e.printStackTrace();
			return Build.SERIAL;
		}
	}


}

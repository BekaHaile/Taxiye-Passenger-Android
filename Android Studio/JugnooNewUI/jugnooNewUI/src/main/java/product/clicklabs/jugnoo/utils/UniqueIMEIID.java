package product.clicklabs.jugnoo.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import product.clicklabs.jugnoo.BuildConfig;

public class UniqueIMEIID {


	public static String getUniqueIMEIId(Context context) {

		String imei = null,meid= null,serial = null,androidSecureId = null;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
				TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
					if (telephonyManager != null) {
						imei = telephonyManager.getImei();
						meid = telephonyManager.getMeid();
					}
					serial = Build.getSerial();
				} else {
					imei = telephonyManager.getDeviceId();
					serial = Build.SERIAL;

				}

			}
		} else {
			TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			if(telephonyManager!=null)imei = telephonyManager.getDeviceId();
			serial = Build.SERIAL;
		}


		String suffix = BuildConfig.DEBUG ? BuildConfig.FLAVOR:"";

		androidSecureId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
		if(!TextUtils.isEmpty(imei))return imei+suffix+"lkj";
		if(!TextUtils.isEmpty(serial))return serial+suffix+"lkj";

		return androidSecureId+suffix+"lkj";




	}

}

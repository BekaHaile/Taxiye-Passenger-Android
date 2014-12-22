package product.clicklabs.jugnoo.utils;

import android.content.Context;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

public class DeviceUniqueID {

	public static String getUniqueId(Context context){
		try {
			TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			String imei = telephonyManager.getDeviceId();
			Log.e("imei", "="+imei);
			if(imei != null && !imei.isEmpty()){
				return imei;
			}
			String androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
			Log.e("androidId", "="+androidId);
			if(androidId != null && !androidId.isEmpty()){
				return androidId;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "not_found";
	}
	
}

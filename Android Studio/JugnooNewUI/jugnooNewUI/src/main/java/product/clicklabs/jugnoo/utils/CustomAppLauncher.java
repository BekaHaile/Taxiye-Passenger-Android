package product.clicklabs.jugnoo.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

public class CustomAppLauncher {

	//product.clicklabs.postmygreetings
	
	public static void launchApp(Context activity, String packageName){
		try {
			Intent intent;
			PackageManager manager = activity.getPackageManager();
		    intent = manager.getLaunchIntentForPackage(packageName);
		    if (intent == null){
		        throw new PackageManager.NameNotFoundException();
		    }
		    intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    activity.startActivity(intent);
		} catch (Exception e) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("https://play.google.com/store/apps/details?id="+packageName));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			activity.startActivity(intent);
		}
	}
	
}

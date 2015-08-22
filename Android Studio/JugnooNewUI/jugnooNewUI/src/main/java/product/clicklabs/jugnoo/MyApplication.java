package product.clicklabs.jugnoo;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.multidex.MultiDex;

import io.branch.referral.Branch;

/**
 * Created by socomo20 on 8/22/15.
 */
public class MyApplication extends Application{

	@Override
	public void onCreate() {
		super.onCreate();
		if(!this.isTestModeEnabled()) {
			Branch.getInstance(this);
		} else {
			Branch.getTestInstance(this);
		}
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}

	private boolean isTestModeEnabled() {
		boolean isTestMode_ = false;
		String testModeKey = "io.branch.sdk.TestMode";

		try {
			ApplicationInfo e = this.getPackageManager().getApplicationInfo(this.getPackageName(), 128);
			if(e.metaData != null) {
				isTestMode_ = e.metaData.getBoolean(testModeKey, false);
			}
		} catch (PackageManager.NameNotFoundException var4) {
			var4.printStackTrace();
		}

		return isTestMode_;
	}
}

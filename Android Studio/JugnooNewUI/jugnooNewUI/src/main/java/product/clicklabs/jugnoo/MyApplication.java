package product.clicklabs.jugnoo;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.multidex.MultiDex;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.analytics.ecommerce.ProductAction;

import java.util.List;
import java.util.Map;

import io.branch.referral.Branch;
import product.clicklabs.jugnoo.utils.AnalyticsTrackers;

/**
 * Created by socomo20 on 8/22/15.
 */
public class MyApplication extends Application{

	public static final String TAG = MyApplication.class
			.getSimpleName();

	private static MyApplication mInstance;
	public String ACTIVITY_NAME_PLAY = "GAME";
	public String ACTIVITY_NAME_FREE_RIDES = "FREE RIDES";
	public String ACTIVITY_NAME_WALLET = "WALLET";
	public String ACTIVITY_NAME_INBOX= "INBOX";
	public String ACTIVITY_NAME_PROMOTIONS = "PROMOTIONS";
	public String ACTIVITY_NAME_HISTORY = "HISTORY";
	public String ACTIVITY_NAME_OFFERS = "PROMOTIONS";
	public String ACTIVITY_NAME_REFER_A_DRIVER = "REFER A DRIVER";
	public String ACTIVITY_NAME_SUPPORT = "SUPPORT";
	public String ACTIVITY_NAME_ABOUT = "ABOUT";

	@Override
	public void onCreate() {
		super.onCreate();
		if(!this.isTestModeEnabled()) {
			Branch.getInstance(this);
		} else {
			Branch.getTestInstance(this);
		}
		Branch.getAutoInstance(this);


		mInstance = this;

		AnalyticsTrackers.initialize(this);
		AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
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



	public static synchronized MyApplication getInstance() {
		return mInstance;
	}

	public synchronized Tracker getGoogleAnalyticsTracker() {
		AnalyticsTrackers analyticsTrackers = AnalyticsTrackers.getInstance();
		return analyticsTrackers.get(AnalyticsTrackers.Target.APP);
	}

	/***
	 * Tracking screen view
	 *
	 * @param screenName screen name to be displayed on GA dashboard
	 */
	public void trackScreenView(String screenName) {
		Tracker t = getGoogleAnalyticsTracker();
		t.enableAdvertisingIdCollection(true);
		// Set screen name.
		t.setScreenName(screenName);

		// Send a screen view.
		t.send(new HitBuilders.ScreenViewBuilder().build());

		GoogleAnalytics.getInstance(this).dispatchLocalHits();
	}

	public void setGAUserId(String mUserId) {
		Tracker t = getGoogleAnalyticsTracker();
		t.enableAdvertisingIdCollection(true);
		// Set screen name.
		t.setClientId(mUserId);

		// Send a screen view.
		t.send(new HitBuilders.ScreenViewBuilder().build());

		GoogleAnalytics.getInstance(this).dispatchLocalHits();
	}

	/***
	 * Tracking exception
	 *
	 * @param e exception to be tracked
	 */
	public void trackException(Exception e) {
		if (e != null) {
			Tracker t = getGoogleAnalyticsTracker();

			t.send(new HitBuilders.ExceptionBuilder()
							.setDescription(
									new StandardExceptionParser(this, null)
											.getDescription(Thread.currentThread().getName(), e))
							.setFatal(false)
							.build()
			);
		}
	}

	/***
	 * Tracking event
	 *
	 * @param category event category
	 * @param action   action of the event
	 * @param label    label
	 */
	public void trackEvent(String category, String action, String label) {
		Tracker t = getGoogleAnalyticsTracker();
		t.enableAdvertisingIdCollection(true);
		// Build and send an Event.
		t.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).build());
	}

	/***
	 * Tracking event
	 *
	 * @param category event category
	 * @param action   action of the event
	 * @param label    label
	 * @param map hash map for key value pairs
	 */
	public void trackEvent(String category, String action, String label, Map<String, String> map) {
		Tracker t = getGoogleAnalyticsTracker();

		HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder();
		eventBuilder.setCategory(category).setAction(action).setLabel(label);
		for (String key : map.keySet()) {
			eventBuilder.set(key, map.get(key));
		}

		// Build and send an Event.
		t.send(eventBuilder.build());
	}

	public void transactions(List<Product> product, ProductAction productAction) {
		HitBuilders.ScreenViewBuilder builder = new HitBuilders.ScreenViewBuilder();
		for(int i=0;i<product.size();i++) {
			builder.addProduct(product.get(i));
		}

		builder.setProductAction(productAction);

		Tracker t = getGoogleAnalyticsTracker();
		t.enableAdvertisingIdCollection(true);
		t.setScreenName("transaction");
		t.send(builder.build());

//        t.send(new HitBuilders.ScreenViewBuilder()
//                .setNewSession()
//                .build());


	}

}

//package com.sabkuchfresh;
//
//import android.app.Application;
//import android.content.Context;
//import android.content.pm.ApplicationInfo;
//import android.content.pm.PackageManager;
//import android.support.multidex.MultiDex;
//
//import com.google.android.gms.analytics.GoogleAnalytics;
//import com.google.android.gms.analytics.HitBuilders;
//import com.google.android.gms.analytics.StandardExceptionParser;
//import com.google.android.gms.analytics.Tracker;
//import com.google.android.gms.analytics.ecommerce.Product;
//import com.google.android.gms.analytics.ecommerce.ProductAction;
//import com.sabkuchfresh.analytics.AnalyticsTrackers;
//import com.sabkuchfresh.fragments.NotificationCenterFragment;
//import com.squareup.otto.Bus;
//
//import java.util.List;
//import java.util.Map;
//
//import io.branch.referral.Branch;
//
///**
// * Created by socomo20 on 8/22/15.
// */
//public class MyApplication extends Application {
//
//	public static final String TAG = MyApplication.class
//			.getSimpleName();
//
//	private static MyApplication mInstance;
//    public static NotificationCenterFragment displayPushHandler = null;
//
//    /**
//     * Reference to the bus (OTTO By Square)
//     */
//    private Bus mBus;
//
//    public Branch branch;
//
//	@Override
//	public void onCreate() {
//		super.onCreate();
//        if(!this.isTestModeEnabled()) {
//            branch = Branch.getInstance(this);
//        } else {
//            branch = Branch.getTestInstance(this);
//        }
//
////        if(!UserInfo.INSTANCE.getId().equals("")) {
////            branch.setIdentity(UserInfo.INSTANCE.getId());
////        }
//
//        branch.getAutoInstance(this);
//
//        mBus = new Bus();
//        mBus.register(this);
//
//		mInstance = this;
//
//		AnalyticsTrackers.initialize(this);
//		AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
//
//
//
//
//
//	}
//
//	@Override
//	protected void attachBaseContext(Context base) {
//		super.attachBaseContext(base);
//		MultiDex.install(this);
//	}
//
//
//    public Bus getBus() {
//        return mBus;
//    }
//
//	private boolean isTestModeEnabled() {
//		boolean isTestMode_ = false;
//		String testModeKey = "io.branch.sdk.TestMode";
//
//		try {
//			ApplicationInfo e = this.getPackageManager().getApplicationInfo(this.getPackageName(), 128);
//			if(e.metaData != null) {
//				isTestMode_ = e.metaData.getBoolean(testModeKey, false);
//			}
//		} catch (PackageManager.NameNotFoundException var4) {
//			var4.printStackTrace();
//		}
//
//		return isTestMode_;
//	}
//
//
//
//	public static synchronized MyApplication getInstance() {
//		return mInstance;
//	}
//
//	public synchronized Tracker getGoogleAnalyticsTracker() {
//		AnalyticsTrackers analyticsTrackers = AnalyticsTrackers.getInstance();
//		return analyticsTrackers.get(AnalyticsTrackers.Target.APP);
//	}
//
//	/***
//	 * Tracking screen view
//	 *
//	 * @param screenName screen name to be displayed on GA dashboard
//	 */
//	public void trackScreenView(String screenName) {
//		Tracker t = getGoogleAnalyticsTracker();
//        t.enableAdvertisingIdCollection(true);
//		// Set screen name.
//		t.setScreenName(screenName);
//
//		// Send a screen view.
//		t.send(new HitBuilders.ScreenViewBuilder().build());
//
//		GoogleAnalytics.getInstance(this).dispatchLocalHits();
//	}
//
//	/***
//	 * Tracking exception
//	 *
//	 * @param e exception to be tracked
//	 */
//	public void trackException(Exception e) {
//		if (e != null) {
//			Tracker t = getGoogleAnalyticsTracker();
//            t.enableAdvertisingIdCollection(true);
//			t.send(new HitBuilders.ExceptionBuilder()
//							.setDescription(
//									new StandardExceptionParser(this, null)
//											.getDescription(Thread.currentThread().getName(), e))
//							.setFatal(false)
//							.build()
//			);
//		}
//	}
//
//	/***
//	 * Tracking event
//	 *
//	 * @param category event category
//	 * @param action   action of the event
//	 * @param label    label
//	 */
//	public void trackEvent(String category, String action, String label) {
//		Tracker t = getGoogleAnalyticsTracker();
//        t.enableAdvertisingIdCollection(true);
//		// Build and send an Event.
//		t.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).build());
//	}
//
//	/***
//	 * Tracking event
//	 *
//	 * @param category event category
//	 * @param action   action of the event
//	 * @param label    label
//	 * @param map hash map for key value pairs
//	 */
//	public void trackEvent(String category, String action, String label, Map<String, String> map) {
//		Tracker t = getGoogleAnalyticsTracker();
//        t.enableAdvertisingIdCollection(true);
//		HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder();
//		eventBuilder.setCategory(category).setAction(action).setLabel(label);
//		for (String key : map.keySet()) {
//			eventBuilder.set(key, map.get(key));
//		}
//
//		// Build and send an Event.
//        t.send(eventBuilder.build());
//	}
//
//    public void trackEvent(String userId, String category, String action, String label, Map<String, String> map) {
//
//        Tracker t = getGoogleAnalyticsTracker();
//        t.enableAdvertisingIdCollection(true);
//        t.setClientId(userId);
//        HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder();
//        eventBuilder.setCategory(category).setAction(action).setLabel(label);
//        for (String key : map.keySet()) {
//            eventBuilder.set(key, map.get(key));
//        }
//
//        // Build and send an Event.
//        t.send(eventBuilder.build());
//        GoogleAnalytics.getInstance(this).dispatchLocalHits();
//    }
//
//    public void advertising() {
//        Tracker t = getGoogleAnalyticsTracker();
//        t.enableAdvertisingIdCollection(true);
//    }
//
//    public static ProductAction productAction;
//    private static ProductAction getProductAction() {
//        if(productAction == null) {
//            productAction = new ProductAction(ProductAction.ACTION_CHECKOUT);
//        }
//        return productAction;
//    }
//
//    public void eventTracker(int position, List<Product> product) {
//// Add the step number and additional info about the checkout to the action.
//        ProductAction productAction = getProductAction();
//        productAction.setCheckoutStep(position);
//        HitBuilders.ScreenViewBuilder builder = new HitBuilders.ScreenViewBuilder();
//                for(int i=0;i<product.size();i++) {
//                    builder.addProduct(product.get(i));
//                }
//        builder.setProductAction(productAction);
//
//        Tracker t = getGoogleAnalyticsTracker();
//        t.send(builder.build());
//
//
//    }
//
//    public void transactions(List<Product> product, ProductAction productAction) {
//        HitBuilders.ScreenViewBuilder builder = new HitBuilders.ScreenViewBuilder();
//        for(int i=0;i<product.size();i++) {
//            builder.addProduct(product.get(i));
//        }
//
//        builder.setProductAction(productAction);
//
//        Tracker t = getGoogleAnalyticsTracker();
//        t.setScreenName("transaction");
//        t.send(builder.build());
//
////        t.send(new HitBuilders.ScreenViewBuilder()
////                .setNewSession()
////                .build());
//
//
//    }
//
//
//}

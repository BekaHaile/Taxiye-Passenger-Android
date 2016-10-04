package product.clicklabs.jugnoo;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.multidex.MultiDex;

import com.clevertap.android.sdk.ActivityLifecycleCallback;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.exceptions.CleverTapMetaDataNotFoundException;
import com.clevertap.android.sdk.exceptions.CleverTapPermissionsNotSatisfied;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.analytics.ecommerce.ProductAction;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.kochava.android.tracker.Feature;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.branch.referral.Branch;
import io.fabric.sdk.android.Fabric;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.config.ConfigMode;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.home.AppSwitcher;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.AnalyticsTrackers;
import product.clicklabs.jugnoo.utils.CleverTapUtils;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.wallet.WalletCore;

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
	public String ACTIVITY_NAME_NOTIFICATION_SETTING = "SET PREFERENCES";

    /**
     * The {@code FirebaseAnalytics} used to record screen views.
     */
    // [START declare_analytics]
    private FirebaseAnalytics mFirebaseAnalytics;
    // [END declare_analytics]

    private Feature kTracker;

	/**
	 * Reference to the bus (OTTO By Square)
	 */
	private Bus mBus;
	public Branch branch;

	private CleverTapAPI cleverTap;

	@Override
	public void onCreate() {
		ActivityLifecycleCallback.register(this);
		super.onCreate();
		try {
			Fabric.with(this, new Crashlytics());
			if(!this.isTestModeEnabled()) {
                Branch.getInstance(this);
            } else {
                Branch.getTestInstance(this);
            }
			Branch.getAutoInstance(this);

			if(!this.isTestModeEnabled()) {
				branch = Branch.getInstance(this);
			} else {
				branch = Branch.getTestInstance(this);
			}

			if(BuildConfig.DEBUG_MODE) {
//				Feature.setErrorDebug(true);
//				Feature.enableDebug(true);
				CleverTapAPI.setDebugLevel(1);
			}
            kTracker = new Feature( this , Config.KOCHAVA_KEY );

			mInstance = this;
			mBus = new Bus();
			mBus.register(this);

            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

			AnalyticsTrackers.initialize(this);
			AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
		} catch (Exception e) {
			e.printStackTrace();
		}
		getCleverTap();
	}

	public Bus getBus() {
		return mBus;
	}

    public FirebaseAnalytics getFirebaseAnalytics() {
        if(mFirebaseAnalytics == null) {
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        }
        return mFirebaseAnalytics;
    }


    public void logEvent(String content, Bundle bundle) {
		if(content.length()>31) {
			content = content.substring(0, 31);
		}
		if(bundle != null) {
			getFirebaseAnalytics().logEvent(content, bundle);
		} else{
			Bundle bundle1 = new Bundle();
			getFirebaseAnalytics().logEvent(content, bundle1);
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

        if(category.equalsIgnoreCase(Constants.REVENUE + Constants.SLASH + Constants.ACTIVATION + Constants.SLASH + Constants.RETENTION)) {
            Bundle bundle = new Bundle();
            logEvent("Transaction_"+action+"_"+label, bundle);
        }


	}



	public void trackEvent(String userId, String category, String action, String label, Map<String, String> map) {

		Tracker t = getGoogleAnalyticsTracker();
		t.enableAdvertisingIdCollection(true);
		t.setClientId(userId);
		HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder();
		eventBuilder.setCategory(category).setAction(action).setLabel(label);
		for (String key : map.keySet()) {
			eventBuilder.set(key, map.get(key));
		}

		// Build and send an Event.
		t.send(eventBuilder.build());
		GoogleAnalytics.getInstance(this).dispatchLocalHits();
	}


		/**
         *
         * @param category
         * @param action
         * @param label
         * @param value
         */
	public void trackEvent(String category, String action, String label, long value) {
		Tracker t = getGoogleAnalyticsTracker();
		t.enableAdvertisingIdCollection(true);
		// Build and send an Event.
		t.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).setValue(value).build());
        if(category.equalsIgnoreCase(Constants.REVENUE + Constants.SLASH + Constants.ACTIVATION + Constants.SLASH + Constants.RETENTION)) {
            Bundle bundle = new Bundle();
            bundle.putLong("value", value);
            logEvent("Transaction_"+action+"_"+label, bundle);
        }
	}


	public static ProductAction productAction;
	private static ProductAction getProductAction() {
		if(productAction == null) {
			productAction = new ProductAction(ProductAction.ACTION_CHECKOUT);
		}
		return productAction;
	}

	public void eventTracker(int position, List<Product> product) {
// Add the step number and additional info about the checkout to the action.
		ProductAction productAction = getProductAction();
		productAction.setCheckoutStep(position);
		HitBuilders.ScreenViewBuilder builder = new HitBuilders.ScreenViewBuilder();
		for(int i=0;i<product.size();i++) {
			builder.addProduct(product.get(i));
		}
		builder.setProductAction(productAction);

		Tracker t = getGoogleAnalyticsTracker();
		t.send(builder.build());


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


	private WalletCore walletCore;
	public WalletCore getWalletCore(){
		if(walletCore == null){
			walletCore = new WalletCore(this);
		}
		return walletCore;
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

    public Feature getkTracker() {
        if(kTracker == null) {
            kTracker = new Feature(this, Config.KOCHAVA_KEY);
        }
        return kTracker;
    }

	private AppSwitcher appSwitcher;
	public AppSwitcher getAppSwitcher(){
		if(appSwitcher == null){
			appSwitcher = new AppSwitcher();
		}
		return appSwitcher;
	}

	public CleverTapAPI getCleverTap() {
		if(cleverTap == null) {
			try {
				cleverTap = CleverTapAPI.getInstance(getApplicationContext());
			} catch (CleverTapMetaDataNotFoundException e) {
				// thrown if you haven't specified your CleverTap Account ID or Token in your AndroidManifest.xml
			} catch (CleverTapPermissionsNotSatisfied e) {
				// thrown if you haven’t requested the required permissions in your AndroidManifest.xml
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return cleverTap;
	}

	public void setLocationToCleverTap() {
		try {
			Location location = getCleverTap().getLocation();
			getCleverTap().setLocation(location);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method used to send event on clever tap
	 * @param eventName
	 * @param prodViewedAction
     */
	public void sendCleverTapEvent(String eventName, HashMap<String, Object> prodViewedAction) {
		try{
			prodViewedAction.put(Events.TIMING, System.currentTimeMillis()/1000);
		} catch (Exception e){
			e.printStackTrace();
		}

		getCleverTap().event.push(eventName, prodViewedAction);
	}

	public void charged(HashMap<String, Object> chargeDetails, ArrayList<HashMap<String, Object>> items) {
		try {
			try{
				chargeDetails.put(Events.TIMING, System.currentTimeMillis()/1000);
			} catch (Exception e){
				e.printStackTrace();
			}
			getCleverTap().event.push(CleverTapAPI.CHARGED_EVENT, chargeDetails, items);
		} catch (Exception e) {
			// You have to specify the first parameter to push()
			// as CleverTapAPI.CHARGED_EVENT
		}
	}

	public void udpateUserData(String key, ArrayList<String> coupons) {
		try {
			if(coupons.size()>0)
				getCleverTap().profile.setMultiValuesForKey(key, coupons);
			else {
				HashMap<String, Object> profileUpdate = new HashMap<>();
				profileUpdate.put(key, "NA");
				getCleverTap().profile.push(profileUpdate);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private CleverTapUtils cleverTapUtils;
	public CleverTapUtils getCleverTapUtils(){
		if(cleverTapUtils == null){
			cleverTapUtils = new CleverTapUtils();
		}
		return cleverTapUtils;
	}


	public Database2 getDatabase2(){
		return Database2.getInstance(this);
	}

	public void initializeServerURL(Context context) {
		String link = Prefs.with(context).getString(SPLabels.SERVER_SELECTED, Config.getDefaultServerUrl());

		ConfigMode configModeToSet;
		if (link.equalsIgnoreCase(Config.getLiveServerUrl())
				|| link.equalsIgnoreCase(Config.getLegacyServerUrl())) {
			configModeToSet = ConfigMode.LIVE;
		} else if (link.equalsIgnoreCase(Config.getDevServerUrl())) {
			configModeToSet = ConfigMode.DEV;
		} else if (link.equalsIgnoreCase(Config.getDev1ServerUrl())) {
			configModeToSet = ConfigMode.DEV_1;
		} else if (link.equalsIgnoreCase(Config.getDev2ServerUrl())) {
			configModeToSet = ConfigMode.DEV_2;
		} else if (link.equalsIgnoreCase(Config.getDev3ServerUrl())) {
			configModeToSet = ConfigMode.DEV_3;
		} else {
			Config.CUSTOM_SERVER_URL = link;
			configModeToSet = ConfigMode.CUSTOM;
		}
		String freshServerUrlToSet = Prefs.with(context).getString(SPLabels.FRESH_SERVER_SELECTED, Config.getFreshDefaultServerUrl());

		if(configModeToSet != Config.getConfigMode() || !Config.getFreshServerUrl().equalsIgnoreCase(freshServerUrlToSet)){
			RestClient.clearRestClient();
		}
		Config.setConfigMode(configModeToSet);
		Config.FRESH_SERVER_URL = freshServerUrlToSet;

		Prefs.with(context).save(SPLabels.SERVER_SELECTED, Config.getServerUrl());

		RestClient.setupRestClient();
		RestClient.setupFreshApiRestClient();
	}

}

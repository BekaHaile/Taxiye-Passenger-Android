package product.clicklabs.jugnoo;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.multidex.MultiDex;
import android.widget.Toast;

import com.clevertap.android.sdk.ActivityLifecycleCallback;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.exceptions.CleverTapMetaDataNotFoundException;
import com.clevertap.android.sdk.exceptions.CleverTapPermissionsNotSatisfied;
import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.analytics.ecommerce.ProductAction;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.otto.Bus;
import com.tsengvn.typekit.Typekit;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.branch.referral.Branch;
import io.fabric.sdk.android.Fabric;
import io.paperdb.Paper;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.config.ConfigMode;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.home.AppSwitcher;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.AnalyticsTrackers;
import product.clicklabs.jugnoo.utils.CleverTapUtils;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.wallet.WalletCore;

/**
 * Created by socomo20 on 8/22/15.
 */
public class MyApplication extends Application {

    public static final String TAG = MyApplication.class
            .getSimpleName();

    private static MyApplication mInstance;
    public String ACTIVITY_NAME_PLAY = "GAME";
    public String ACTIVITY_NAME_FREE_RIDES = "FREE RIDES";
    public String ACTIVITY_NAME_WALLET = "WALLET";
    public String ACTIVITY_NAME_INBOX = "INBOX";
    public String ACTIVITY_NAME_PROMOTIONS = "PROMOTIONS";
    public String ACTIVITY_NAME_HISTORY = "HISTORY";
    public String ACTIVITY_NAME_OFFERS = "PROMOTIONS";
    public String ACTIVITY_NAME_REFER_A_DRIVER = "REFER A DRIVER";
    public String ACTIVITY_NAME_SUPPORT = "SUPPORT";
    public String ACTIVITY_NAME_ABOUT = "ABOUT";
    public String ACTIVITY_NAME_JUGNOO_STAR = "JUGNOO STAR";
    public String ACTIVITY_NAME_NOTIFICATION_SETTING = "SET PREFERENCES";


    private Bus mBus;
    public Branch branch;

    private CleverTapAPI cleverTap;

    @Override
    public void onCreate() {
        ActivityLifecycleCallback.register(this);


        /**
         Edited by Parminder Singh on 1/30/17 at 3:47 PM
         **/

        Typekit.getInstance()
                .add("maven", Typekit.createFromAsset(this, "fonts/maven_pro_medium.ttf"))
                .add(getString(R.string.maven_r), Typekit.createFromAsset(this, "fonts/maven_pro_regular.otf"))
                .add("avenir",Typekit.createFromAsset(this,  "fonts/avenir_next_demi.otf"))
				.add("avenir_book",Typekit.createFromAsset(this,  "fonts/avenir_book.ttf"))
				.add(getString(R.string.maven_l), Typekit.createFromAsset(this, "fonts/maven_pro_light_300.otf"))
		;



        super.onCreate();
		FacebookSdk.sdkInitialize(getApplicationContext());
		AppEventsLogger.activateApp(this);

//		if (LeakCanary.isInAnalyzerProcess(this)) {
//			// This process is dedicated to LeakCanary for heap analysis.
//			// You should not init your app in this process.
//			return;
//		}
//		LeakCanary.install(this);

		Paper.init(this);


        try {
            Fabric.with(this, new Crashlytics());
            if (!this.isTestModeEnabled()) {
                Branch.getInstance(this);
            } else {
                Branch.getTestInstance(this);
            }
            Branch.getAutoInstance(this);

            if (!this.isTestModeEnabled()) {
                branch = Branch.getInstance(this);
            } else {
                branch = Branch.getTestInstance(this);
            }

            if (BuildConfig.DEBUG_MODE) {
                CleverTapAPI.setDebugLevel(1);
            }

            mInstance = this;
            mBus = new Bus();
            mBus.register(this);

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
            if (e.metaData != null) {
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

    private synchronized Tracker getGoogleAnalyticsTracker() {
        AnalyticsTrackers analyticsTrackers = AnalyticsTrackers.getInstance();
        return analyticsTrackers.get(AnalyticsTrackers.Target.APP);
    }

    /***
     * Tracking screen view
     * @param screenName screen name to be displayed on GA dashboard
     */
    public void trackScreenView(String screenName) {
		Tracker t = getGoogleAnalyticsTracker();
		t.setScreenName(screenName);
		t.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void setGAUserId(String mUserId) {
        Tracker t = getGoogleAnalyticsTracker();
        t.enableAdvertisingIdCollection(true);
        t.setClientId(mUserId);
        t.send(new HitBuilders.ScreenViewBuilder().build());

        GoogleAnalytics.getInstance(this).dispatchLocalHits();
    }

    /***
     * Tracking exception
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
     * @param category event category
     * @param action   action of the event
     * @param label    label
     */
    public void trackEvent(String category, String action, String label) {
        Tracker t = getGoogleAnalyticsTracker();
        t.enableAdvertisingIdCollection(true);
        t.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).build());
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
        t.send(eventBuilder.build());
        GoogleAnalytics.getInstance(this).dispatchLocalHits();
    }


    public void trackEvent(String category, String action, String label, long value) {
        Tracker t = getGoogleAnalyticsTracker();
        t.enableAdvertisingIdCollection(true);
        t.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).setValue(value).build());
    }


    public static ProductAction productAction;

    private static ProductAction getProductAction() {
        if (productAction == null) {
            productAction = new ProductAction(ProductAction.ACTION_CHECKOUT);
        }
        return productAction;
    }

    public void eventTracker(int position, List<Product> product) {
        ProductAction productAction = getProductAction();
        productAction.setCheckoutStep(position);
        HitBuilders.ScreenViewBuilder builder = new HitBuilders.ScreenViewBuilder();
        for (int i = 0; i < product.size(); i++) {
            builder.addProduct(product.get(i));
        }
        builder.setProductAction(productAction);
        Tracker t = getGoogleAnalyticsTracker();
        t.send(builder.build());
    }


    public void transactionEvent(List<Product> product, ProductAction productAction) {
        HitBuilders.ScreenViewBuilder builder = new HitBuilders.ScreenViewBuilder();
        for (int i = 0; i < product.size(); i++) {
            builder.addProduct(product.get(i));
        }
        builder.setProductAction(productAction);
        Tracker t = getGoogleAnalyticsTracker();
        t.enableAdvertisingIdCollection(true);
        t.setScreenName("transaction");
        t.send(builder.build());
    }


    private WalletCore walletCore;

    public WalletCore getWalletCore() {
        if (walletCore == null) {
            walletCore = new WalletCore(this);
        }
        return walletCore;
    }


    public String getDeviceToken() {
        String deviceToken = Prefs.with(this).getString(Constants.SP_DEVICE_TOKEN, "not_found");
        if (deviceToken.equalsIgnoreCase("not_found")) {
            deviceToken = FirebaseInstanceId.getInstance().getToken();
            if (deviceToken == null) {
                deviceToken = "not_found";
            }
        }
        return deviceToken;
    }


    private AppSwitcher appSwitcher;

    public AppSwitcher getAppSwitcher() {
        if (appSwitcher == null) {
            appSwitcher = new AppSwitcher();
        }
        return appSwitcher;
    }

    public CleverTapAPI getCleverTap() {
        if (cleverTap == null) {
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
			prodViewedAction.put(Events.TIMING, new Date());
		} catch (Exception e){
			e.printStackTrace();
		}

		getCleverTap().event.push(eventName, prodViewedAction);
	}

	/**
	 * Clevertap charged event for app offerring transactions
	 * @param chargeDetails
	 * @param items
	 */
	public void charged(HashMap<String, Object> chargeDetails, ArrayList<HashMap<String, Object>> items) {
		try {
			try{
				chargeDetails.put(Events.TIMING, new Date());
			} catch (Exception e){
				e.printStackTrace();
			}
			getCleverTap().event.push(CleverTapAPI.CHARGED_EVENT, chargeDetails, items);
		} catch (Exception e) {
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

    public void updateUserDataAddInMultiValue(String key, String value) {
        try {
            getCleverTap().profile.addMultiValueForKey(key, value);
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

	public Database getDatabase(){
		return Database.getInstance(this);
	}

	public void initializeServerURL(Context context) {
		String link = Prefs.with(context).getString(SPLabels.SERVER_SELECTED, Config.getDefaultServerUrl());

		ConfigMode configModeToSet;
		if (link.equalsIgnoreCase(Config.getLiveServerUrl())
				|| link.equalsIgnoreCase(Config.getLegacyServerUrl())) {
			configModeToSet = ConfigMode.LIVE;
		} else if (link.equalsIgnoreCase(Config.getDevServerUrl())) {
			configModeToSet = ConfigMode.DEV;
		} else {
			Config.CUSTOM_SERVER_URL = link;
			configModeToSet = ConfigMode.CUSTOM;
		}
		String freshServerUrlToSet = Prefs.with(context).getString(SPLabels.FRESH_SERVER_SELECTED, Config.getFreshDefaultServerUrl());
		String menusServerUrlToSet = Prefs.with(context).getString(SPLabels.MENUS_SERVER_SELECTED, Config.getMenusDefaultServerUrl());
		String fatafatServerUrlToSet = Prefs.with(context).getString(SPLabels.FATAFAT_SERVER_SELECTED, Config.getFatafatDefaultServerUrl());
		String payServerUrlToSet = Prefs.with(context).getString(SPLabels.PAY_SERVER_SELECTED, Config.getPayDefaultServerUrl());

		if(configModeToSet != Config.getConfigMode()
				|| !Config.getFreshServerUrl().equalsIgnoreCase(freshServerUrlToSet)
				|| !Config.getMenusServerUrl().equalsIgnoreCase(menusServerUrlToSet)
				|| !Config.getFatafatServerUrl().equalsIgnoreCase(fatafatServerUrlToSet)
				|| !Config.getPayServerUrl().equalsIgnoreCase(payServerUrlToSet)){
			RestClient.clearRestClients();
		}
		Config.setConfigMode(configModeToSet);
		Config.FRESH_SERVER_URL = freshServerUrlToSet;
		Config.MENUS_SERVER_URL = menusServerUrlToSet;
		Config.FATAFAT_SERVER_URL = fatafatServerUrlToSet;

		Prefs.with(context).save(SPLabels.SERVER_SELECTED, Config.getServerUrl());
		Prefs.with(context).save(SPLabels.FRESH_SERVER_SELECTED, Config.getFreshServerUrl());
		Prefs.with(context).save(SPLabels.MENUS_SERVER_SELECTED, Config.getMenusServerUrl());
		Prefs.with(context).save(SPLabels.FATAFAT_SERVER_SELECTED, Config.getFatafatServerUrl());

		RestClient.setupAllClients();
	}


	public int appVersion(){
		try {
			PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			return pInfo.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public String osVersion(){
		return android.os.Build.VERSION.RELEASE;
	}

	public String country(){
		return getResources().getConfiguration().locale.getDisplayCountry(Locale.getDefault());
	}

	public String deviceName(){
		return android.os.Build.MANUFACTURER + android.os.Build.MODEL;
	}

	private LocationFetcher locationFetcher;
	public LocationFetcher getLocationFetcher(){
		if(locationFetcher == null){
			locationFetcher = new LocationFetcher(this);
		}
		return locationFetcher;
	}

	private Toast toast;
	public Toast getToast(){
		return toast;
	}
	public void setToast(Toast toast){
		this.toast = toast;
	}


	public boolean isOnline() {
		try {
			ConnectivityManager connectManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetwork = connectManager.getActiveNetworkInfo();
			if (activeNetwork != null) { // connected to the internet
				if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
					return true;
				} else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
					return true;
				}
			} else {
				return false;
			}
			return false;
		} catch (Exception e) {
			System.out.println("CheckConnectivity Exception: " + e.getMessage());
		}
		return false;
	}

	private AppEventsLogger appEventsLogger;
	public AppEventsLogger getAppEventsLogger(){
		if(appEventsLogger == null){
			appEventsLogger = AppEventsLogger.newLogger(this);
		}
		return appEventsLogger;
	}

	private HomeUtil homeUtil;
	public HomeUtil getHomeUtil(){
		if(homeUtil == null){
			homeUtil = new HomeUtil();
		}
		return homeUtil;
	}

}

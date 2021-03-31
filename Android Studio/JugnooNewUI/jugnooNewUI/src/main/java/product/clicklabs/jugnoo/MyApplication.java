package product.clicklabs.jugnoo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.analytics.ecommerce.ProductAction;
import com.google.firebase.iid.FirebaseInstanceId;
import com.razorpay.Checkout;
import com.sabkuchfresh.fatafatchatpay.ChatCustomActionBroadCastReceiver;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.otto.Bus;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.branch.referral.Branch;
import io.paperdb.Paper;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.config.ConfigMode;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.home.AppSwitcher;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.AnalyticsTrackers;
import product.clicklabs.jugnoo.utils.Foreground;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.typekit.Typekit;
import product.clicklabs.jugnoo.wallet.WalletCore;

/**
 * Created by socomo20 on 8/22/15.
 */
public class MyApplication extends MultiDexApplication {

    public static final String TAG = MyApplication.class
            .getSimpleName();

    private static MyApplication mInstance;
    public String ACTIVITY_NAME_PLAY ;
    public String ACTIVITY_NAME_FREE_RIDES ;
    public String ACTIVITY_NAME_WALLET ;
    public String ACTIVITY_NAME_INBOX;
    public String ACTIVITY_NAME_PROMOTIONS;
    public String ACTIVITY_NAME_HISTORY;
    public String ACTIVITY_NAME_OFFERS;
    public String ACTIVITY_NAME_REFER_A_DRIVER ;
    public String ACTIVITY_NAME_SUPPORT;
    public String ACTIVITY_NAME_ABOUT;
    public String ACTIVITY_NAME_JUGNOO_STAR;
    public String ACTIVITY_NAME_NOTIFICATION_SETTING;


    private Bus mBus;
    public Branch branch;
    private BroadcastReceiver fuguChatCustomActionReceiver;
    public Activity mActivity;
    private Intent mOpenActivityAfterFinishTutorial;

    public Activity getmCurrentActivity() {
        return this.mActivity;
    }
    public void setmCurrentActivity(final Activity mCurrentActivity) {
        this.mActivity = mCurrentActivity;
    }

    public Intent getmOpenActivityAfterFinishTutorial() {
        return this.mOpenActivityAfterFinishTutorial;
    }
    public void setmOpenActivityAfterFinishTutorial(final Intent mOpenActivityAfterFinishTutorial) {
        this.mOpenActivityAfterFinishTutorial = mOpenActivityAfterFinishTutorial;
    }

    @Override
    public void onCreate() {


        /**
         Edited by Parminder Singh on 1/30/17 at 3:47 PM
         **/
        Typekit.getInstance()
                .add("maven", Typekit.createFromAsset(this, "fonts/maven_pro_medium.ttf"))
                .add(getString(R.string.maven_r), Typekit.createFromAsset(this, "fonts/maven_pro_regular.ttf"))
                .add("avenir", Typekit.createFromAsset(this, "fonts/avenir_next_demi.otf"))
                .add("avenir_book", Typekit.createFromAsset(this, "fonts/avenir_book.ttf"))
                .add(getString(R.string.maven_l), Typekit.createFromAsset(this, "fonts/maven_pro_light_300.otf"))
                .add(getString(R.string.montserrat_m), Typekit.createFromAsset(this, "fonts/montserrat_medium.ttf"))
        ;


        super.onCreate();
        Foreground.init(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        Checkout.clearUserData(getApplicationContext());
        Checkout.preload(getApplicationContext());


//		if (LeakCanary.isInAnalyzerProcess(this)) {
//			// This process is dedicated to LeakCanary for heap analysis.
//			// You should not init your app in this process.
//			return;
//		}
//		LeakCanary.install(this);

        Paper.init(this);


        try {
        	if(BuildConfig.DEBUG){
				// Branch logging for debugging
				Branch.enableLogging();
			}

			branch = Branch.getAutoInstance(this);


            mInstance = this;
            mBus = new Bus();
            mBus.register(this);

            AnalyticsTrackers.initialize(this);
            AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ACTIVITY_NAME_PLAY = getResources().getString(R.string.game_caps);
        ACTIVITY_NAME_FREE_RIDES = getResources().getString(R.string.free_rides_caps);
        ACTIVITY_NAME_WALLET = getResources().getString(R.string.wallet_caps);
        ACTIVITY_NAME_INBOX = getResources().getString(R.string.inbox_caps);
        ACTIVITY_NAME_PROMOTIONS = getResources().getString(R.string.promotions_caps);
        ACTIVITY_NAME_HISTORY = getResources().getString(R.string.history_caps);
        ACTIVITY_NAME_OFFERS = getResources().getString(R.string.promotions_caps);
        ACTIVITY_NAME_REFER_A_DRIVER = getResources().getString(R.string.refer_a_driver_caps);
        ACTIVITY_NAME_SUPPORT = getResources().getString(R.string.support_caps);
        ACTIVITY_NAME_ABOUT = getResources().getString(R.string.about_caps);
        ACTIVITY_NAME_JUGNOO_STAR = getResources().getString(R.string.jugnoo_star_caps, getString(R.string.app_name).toUpperCase());
        ACTIVITY_NAME_NOTIFICATION_SETTING = getResources().getString(R.string.set_preferences_caps);

        fuguChatCustomActionReceiver = new ChatCustomActionBroadCastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("FUGU_CUSTOM_ACTION_SELECTED");
        LocalBroadcastManager.getInstance(getApplicationContext()).
                registerReceiver(fuguChatCustomActionReceiver, filter);

    }

    public Bus getBus() {
        return mBus;
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        super.attachBaseContext(LocaleHelper.onAttach(base, "ar"));
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

    public Activity getmActivity() {
        return mActivity;
    }

    public void setmActivity(Activity mActivity) {
        this.mActivity = mActivity;
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


    public Database2 getDatabase2() {
        return Database2.getInstance(this);
    }

    public Database getDatabase() {
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

        if (configModeToSet != Config.getConfigMode()
                || !Config.getFreshServerUrl().equalsIgnoreCase(freshServerUrlToSet)
                || !Config.getMenusServerUrl().equalsIgnoreCase(menusServerUrlToSet)
                || !Config.getFatafatServerUrl().equalsIgnoreCase(fatafatServerUrlToSet)
                || !Config.getPayServerUrl().equalsIgnoreCase(payServerUrlToSet)) {
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


    public int appVersion() {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public String osVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    public String country() {
        return getResources().getConfiguration().locale.getDisplayCountry(Locale.getDefault());
    }

    public String deviceName() {
        return android.os.Build.MANUFACTURER + android.os.Build.MODEL;
    }




    private Toast toast;

    public Toast getToast() {
        return toast;
    }

    public void setToast(Toast toast) {
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

    public AppEventsLogger getAppEventsLogger() {
        if (appEventsLogger == null) {
            appEventsLogger = AppEventsLogger.newLogger(this);
        }
        return appEventsLogger;
    }

    private HomeUtil homeUtil;

    public HomeUtil getHomeUtil() {
        if (homeUtil == null) {
            homeUtil = new HomeUtil();
        }
        return homeUtil;
    }


    public Locale getCurrentLocale() {
        return getResources().getConfiguration().locale;
    }

    private static Picasso picassoSingleton;
    public static Picasso getPicasso(Context context){
        if(picassoSingleton == null){
            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient.setConnectTimeout(40, TimeUnit.SECONDS);
            picassoSingleton = new Picasso.Builder(context)
                    .downloader(new OkHttpDownloader(okHttpClient))
                    .build();
        }
        return picassoSingleton;
    }


}

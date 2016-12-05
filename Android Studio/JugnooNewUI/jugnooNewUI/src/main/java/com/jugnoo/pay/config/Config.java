package com.jugnoo.pay.config;

import android.content.Context;

import com.jugnoo.pay.utils.Prefs;

import product.clicklabs.jugnoo.datastructure.SPLabels;

/**
 * Created by cl-macmini-38 on 18/05/16.
 */
public class Config {


//    static String GCM_PROJECT_NUMBER = "";
    static String BASE_URL = "";
    static String FLURRY_KEY = "";
    private static final String PAY_CLIENT_ID = "PkjfJf27lkUvjKT9"; //PkjfJf27lkUvjKT9 //EEBUOvQq7RRJBxJm
    private static final String CLIENT_SHARED_SECRET = "nqaK7HTwDT3epcpR5JuMWwojFv0KJnIv"; //4F9fECabbd77fba02D2497f880F44e6f
    private static String SUPPORT_NUMBER = "+919023121121";
    static AppMode appMode = AppMode.LIVE;

    private static final String DEV_SERVER_URL = "https://test.jugnoo.in:8099/pay/v2";
    private static final String LIVE_SERVER_URL = "https://prod-push.jugnoo.in:4099/pay/v2";

    private static final String DEFAULT_SERVER_URL = LIVE_SERVER_URL;       // DEV_SERVER_URL;
    private static String SERVER_URL = DEFAULT_SERVER_URL;


    static public String getBaseURL() {
        init(appMode);
        return BASE_URL;
    }

static public String getFlurryKey() {

        init(appMode);

        return FLURRY_KEY;
    }

//    static public String getGCMProjectNumber() {
//
//        init(appMode);
//
//        return GCM_PROJECT_NUMBER;
//    }


    /**
     * Initialize all the variable in this method
     *
     * @param appMode
     */
    public static void init(AppMode appMode) {

        switch (appMode) {
            case DEV:

                // BASE_URL = "http://52.87.201.40:8000";
                BASE_URL = "https://test.jugnoo.in:8099/pay/v2";       //NEW dev
                //  BASE_URL = "http://52.91.250.144:3000";       //NEW test
                FLURRY_KEY = "MNZJSQ9YV376F3NM39VZ";
//                GCM_PROJECT_NUMBER = "563232976573";
                break;

            case TEST:

//                BASE_URL = "http://api.deets.clicklabs.in:1441/";
                BASE_URL = "https://test.jugnoo.in:8099/pay/v2/";

                FLURRY_KEY = "MNZJSQ9YV376F3NM39VZ";
//                GCM_PROJECT_NUMBER = "563232976573";
                break;

            case LIVE:

                BASE_URL = "https://prod-push.jugnoo.in:4099/pay/v2";   // /-removed last slash after v2
                FLURRY_KEY = "MNZJSQ9YV376F3NM39VZ";
//                GCM_PROJECT_NUMBER = "563232976573";
                break;

        }


    }


    public static String getAutosClientId() {
        return PAY_CLIENT_ID;
    }

    public static String getClientSharedSecret() {
        return CLIENT_SHARED_SECRET;
    }

    public static void saveSupportNumber(Context context, String supportContact){
        Prefs.with(context).save(SPLabels.SUPPORT_CONTACT, supportContact);
        SUPPORT_NUMBER = supportContact;
    }

    public static String getServerUrl() {
        init(appMode);
        return SERVER_URL;
    }

    public static String getSupportNumber(Context context) {
        SUPPORT_NUMBER = Prefs.with(context).getString(SPLabels.SUPPORT_CONTACT, SUPPORT_NUMBER);
        return SUPPORT_NUMBER;
    }

    public enum AppMode {
        DEV, TEST, LIVE
    }

}

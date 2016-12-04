package com.jugnoo.pay.utils;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.jugnoo.pay.datastructure.PreviousAccountInfo;
import com.jugnoo.pay.datastructure.SPLabels;
import com.jugnoo.pay.datastructure.UserData;

import java.util.ArrayList;

/**
 * Created by ankit on 21/10/16.
 */
public class Data {

    public static Toast toast;
    public static final String SERVER_ERROR_MSG = "Connection lost. Please try again later.";
    public static final String SERVER_NOT_RESOPNDING_MSG = "Connection lost. Please try again later.";
    public static final String CHECK_INTERNET_TITLE = "CONNECTION LOST";
    public static final String CHECK_INTERNET_MSG = "Oops! Your Internet is not working.\nPlease try again.";
    public static final String INVALID_ACCESS_TOKEN = "invalid access token";
    public static final String DEVICE_TYPE = "0";
    public static String country = "", deviceName = "", osVersion = "", uniqueDeviceId = "";
    public static int appVersion;
    public static final String SHARED_PREF_NAME = "myPref";
    public static final String SP_ACCESS_TOKEN_KEY = "access_token",
            SP_TOTAL_DISTANCE = "total_distance",
            SP_WAIT_TIME = "wait_time",
            SP_RIDE_TIME = "ride_time",
            SP_RIDE_START_TIME = "ride_start_time",
            SP_LAST_LATITUDE = "last_latitude",
            SP_LAST_LONGITUDE = "last_longitude"
                    ;

    public static double loginLatitude = 30.7333, loginLongitude = 76.7794;
    public static UserData userData;
    public static boolean locationSettingsNoPressed = false;
    public static boolean linkFoundOnce = false;
    public static FacebookUserData facebookUserData;
    public static GoogleSignInAccount googleSignInAccount;
    public static Uri splashIntentUri;
    public static String deepLinkReferralCode = "";
    public static ArrayList<PreviousAccountInfo> previousAccountInfoList = new ArrayList<PreviousAccountInfo>();

    public static void clearDataOnLogout(Context context){
        try{
            userData = null;

            country = ""; deviceName = ""; appVersion = 0; osVersion = "";
            facebookUserData = null;
            previousAccountInfoList = new ArrayList<PreviousAccountInfo>();

            AccessTokenGenerator.saveLogoutToken(context);
            clearSPLabelPrefs(context);

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private static void clearSPLabelPrefs(Context context) {
        try {
            Prefs.with(context).remove(SPLabels.REFERRAL_OPEN_DATE_MILLIS);
            Prefs.with(context).remove(SPLabels.REFERRAL_TRANSACTION_COUNT);
            Prefs.with(context).remove(SPLabels.REFERRAL_APP_OPEN_COUNT);
            Prefs.with(context).remove(SPLabels.USER_IDENTIFIER);
            Prefs.with(context).remove(SPLabels.BRANCH_LINK_DESCRIPTION);
            Prefs.with(context).remove(SPLabels.BRANCH_LINK_IMAGE);
            Prefs.with(context).remove(SPLabels.BRANCH_SMS_LINK);
            Prefs.with(context).remove(SPLabels.BRANCH_WHATSAPP_LINK);
            Prefs.with(context).remove(SPLabels.BRANCH_FACEBOOK_LINK);
            Prefs.with(context).remove(SPLabels.BRANCH_EMAIL_LINK);
            Prefs.with(context).remove(SPLabels.ADD_HOME);
            Prefs.with(context).remove(SPLabels.ADD_WORK);
            Prefs.with(context).remove(SPLabels.ADD_GYM);
            Prefs.with(context).remove(SPLabels.ADD_FRIEND);
            Prefs.with(context).remove(SPLabels.NOTIFICATION_UNREAD_COUNT);

            Prefs.with(context).remove(Constants.SP_ANALYTICS_LAST_MESSAGE_READ_TIME);
            Prefs.with(context).remove(Constants.SP_EMERGENCY_MODE_ENABLED);
            Prefs.with(context).remove(Constants.SP_USER_ID);

            Prefs.with(context).remove(SPLabels.UPLOAD_CONTACT_NO_THANKS);
            Prefs.with(context).remove(SPLabels.APP_MONITORING_TRIGGER_TIME);
            Prefs.with(context).remove(SPLabels.UPLOAD_CONTACTS_ERROR);
            Prefs.with(context).remove(SPLabels.CHECK_BALANCE_LAST_TIME);
            Prefs.with(context).remove(SPLabels.LOGIN_UNVERIFIED_DATA_TYPE);
            Prefs.with(context).remove(SPLabels.LOGIN_UNVERIFIED_DATA);

            Prefs.with(context).remove(SPLabels.BRANCH_DESKTOP_URL);
            Prefs.with(context).remove(SPLabels.BRANCH_ANDROID_URL);
            Prefs.with(context).remove(SPLabels.BRANCH_IOS_URL);
            Prefs.with(context).remove(SPLabels.BRANCH_FALLBACK_URL);

            Prefs.with(context).remove(SPLabels.ENTERED_DESTINATION);
            Prefs.with(context).remove(SPLabels.LAST_PICK_UP);
            Prefs.with(context).remove(SPLabels.LAST_DESTINATION);

            Prefs.with(context).remove(Constants.SP_EMERGENCY_MODE_ENABLED);

            Prefs.with(context).remove(Constants.KEY_SP_T20_WC_SCHEDULE_VERSION);
            Prefs.with(context).remove(Constants.SP_T20_DIALOG_BEFORE_START_CROSSED);
            Prefs.with(context).remove(Constants.SP_T20_DIALOG_IN_RIDE_CROSSED);

            Prefs.with(context).remove(Constants.SP_CURRENT_STATE);
            Prefs.with(context).remove(Constants.SP_CURRENT_ENGAGEMENT_ID);
            Prefs.with(context).remove(Constants.SP_LAST_PUSH_RECEIVED_TIME);
            Prefs.with(context).remove(Constants.SP_REFERRAL_CODE);
            Prefs.with(context).remove(Constants.SP_PUSH_DIALOG_CONTENT);
            Prefs.with(context).remove(Constants.KEY_SP_FUGU_CAMPAIGN_NAME);
            Prefs.with(context).remove(Constants.SP_POOL_INTRO_SHOWN);

            Prefs.with(context).remove(Constants.SP_LAST_ADDED_WALLET);
            Prefs.with(context).remove(Constants.SP_LAST_USED_WALLET);
            Prefs.with(context).remove(Constants.SP_LAST_MONEY_ADDED_WALLET);
            Prefs.with(context).remove(Constants.SP_POKESTOP_ENABLED_BY_USER);

            Prefs.with(context).remove(SPLabels.REFERRAL_OPEN_DATE_MILLIS);
            Prefs.with(context).remove(SPLabels.REFERRAL_TRANSACTION_COUNT);
            Prefs.with(context).remove(SPLabels.REFERRAL_APP_OPEN_COUNT);
            Prefs.with(context).remove(SPLabels.USER_IDENTIFIER);
            Prefs.with(context).remove(SPLabels.BRANCH_LINK_DESCRIPTION);
            Prefs.with(context).remove(SPLabels.BRANCH_LINK_IMAGE);
            Prefs.with(context).remove(SPLabels.BRANCH_SMS_LINK);
            Prefs.with(context).remove(SPLabels.BRANCH_WHATSAPP_LINK);
            Prefs.with(context).remove(SPLabels.BRANCH_FACEBOOK_LINK);
            Prefs.with(context).remove(SPLabels.BRANCH_EMAIL_LINK);
            Prefs.with(context).remove(SPLabels.ADD_HOME);
            Prefs.with(context).remove(SPLabels.ADD_WORK);
            Prefs.with(context).remove(SPLabels.ADD_GYM);
            Prefs.with(context).remove(SPLabels.ADD_FRIEND);
            Prefs.with(context).remove(SPLabels.NOTIFICATION_UNREAD_COUNT);

            Prefs.with(context).remove(Constants.SP_ANALYTICS_LAST_MESSAGE_READ_TIME);
            Prefs.with(context).remove(Constants.SP_EMERGENCY_MODE_ENABLED);
            Prefs.with(context).remove(Constants.SP_USER_ID);

            Prefs.with(context).remove(SPLabels.UPLOAD_CONTACT_NO_THANKS);
            Prefs.with(context).remove(SPLabels.APP_MONITORING_TRIGGER_TIME);
            Prefs.with(context).remove(SPLabels.UPLOAD_CONTACTS_ERROR);
            Prefs.with(context).remove(SPLabels.LOGIN_UNVERIFIED_DATA_TYPE);
            Prefs.with(context).remove(SPLabels.LOGIN_UNVERIFIED_DATA);

            Prefs.with(context).remove(SPLabels.BRANCH_DESKTOP_URL);
            Prefs.with(context).remove(SPLabels.BRANCH_ANDROID_URL);
            Prefs.with(context).remove(SPLabels.BRANCH_IOS_URL);
            Prefs.with(context).remove(SPLabels.BRANCH_FALLBACK_URL);

            Prefs.with(context).remove(Constants.SP_EMERGENCY_MODE_ENABLED);

            Prefs.with(context).remove(Constants.KEY_SP_T20_WC_SCHEDULE_VERSION);
            Prefs.with(context).remove(Constants.SP_T20_DIALOG_BEFORE_START_CROSSED);
            Prefs.with(context).remove(Constants.SP_T20_DIALOG_IN_RIDE_CROSSED);


            Prefs.with(context).remove(Constants.SP_FRESH_CART);
            Prefs.with(context).remove(Constants.SP_MEAL_CART);


//            TODO ask gurmail if(!BuildConfig.DEBUG_MODE)
//			Prefs.with(context).removeAll();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

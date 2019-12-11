package product.clicklabs.jugnoo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.maps.model.LatLng;
import com.hippo.CaptureUserData;
import com.hippo.HippoColorConfig;
import com.hippo.HippoConfig;
import com.hippo.HippoConfigAttributes;
import com.sabkuchfresh.retrofit.model.PlaceOrderResponse;

import java.net.URLDecoder;
import java.util.ArrayList;

import io.paperdb.Paper;
import product.clicklabs.jugnoo.apis.ApiTrackPush;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.config.ConfigMode;
import product.clicklabs.jugnoo.datastructure.AppLinkIndex;
import product.clicklabs.jugnoo.datastructure.AutoData;
import product.clicklabs.jugnoo.datastructure.DeliveryData;
import product.clicklabs.jugnoo.datastructure.MenuInfoTags;
import product.clicklabs.jugnoo.datastructure.PayData;
import product.clicklabs.jugnoo.datastructure.PreviousAccountInfo;
import product.clicklabs.jugnoo.datastructure.ProductType;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.datastructure.UserData;
import product.clicklabs.jugnoo.home.models.MenuInfo;
import product.clicklabs.jugnoo.retrofit.model.HistoryResponse;
import product.clicklabs.jugnoo.retrofit.model.LoginResponse;
import product.clicklabs.jugnoo.utils.FacebookUserData;
import product.clicklabs.jugnoo.utils.FuguColorConfigStrings;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;

/**
 * Stores common static data for access for all activities across the application
 *
 * @author shankar
 */
public class Data {


    public static boolean linkFoundOnce = false;


    public static final String NO_PROMO_APPLIED = "No Promo Code applied";

    public static final String SHARED_PREF_NAME = "myPref";
    public static final String SP_ACCESS_TOKEN_KEY = "access_token",

    SP_TOTAL_DISTANCE = "total_distance",
            SP_WAIT_TIME = "wait_time",
            SP_RIDE_TIME = "ride_time",
            SP_RIDE_START_TIME = "ride_start_time",
            SP_LAST_LATITUDE = "last_latitude",
            SP_LAST_LONGITUDE = "last_longitude";


    public static String P_RIDE_END = "P_RIDE_END", P_IN_RIDE = "P_IN_RIDE", P_DRIVER_ARRIVED = "P_DRIVER_ARRIVED", P_REQUEST_FINAL = "P_REQUEST_FINAL",
            P_ASSIGNING = "P_ASSIGNING";


    public static Activity context = null;




    public static double latitude = 0, longitude = 0;
    public static double loginLatitude = 0, loginLongitude = 0;


    public static UserData userData;
    public static AutoData autoData;
    private static LoginResponse.Menus  freshData;
    private static LoginResponse.Meals mealsData;
    private static LoginResponse.Menus groceryData;
    private static DeliveryData DeliveryData;
    private static LoginResponse.Menus menusData;
    private static LoginResponse.DeliveryCustomer  deliveryCustomer;
    private static PayData payData;
    private static LoginResponse.Feed feedData;
    private static LoginResponse.Pros prosData;


    public static final String DEVICE_TYPE = "0";
    public static String uniqueDeviceId = "";


    public static FacebookUserData facebookUserData;
    public static GoogleSignInAccount googleSignInAccount;
    public static String webActivityTitle = "";


    public static ArrayList<PreviousAccountInfo> previousAccountInfoList = new ArrayList<PreviousAccountInfo>();
    public static String kitPhoneNumber = "";

    public static String deepLinkClassName = "", deepLinkReferralCode = "";
    public static int deepLinkIndex, deepLinkOrderId, deepLinkProductType;
    public static int deepLinkPickup = -1;
    public static double deepLinkPickupLatitude, deepLinkPickupLongitude;
    public static boolean locationSettingsNoPressed = false, locationAddressSettingsNoPressed = false;


    public static final long BRANCH_LINK_TIME_DIFF = 7 * 24 * 60 * 60 * 1000;


    public static int TRANSFER_FROM_JEANIE = 0;
    public static int AppType = 1;
    public static boolean isOrderCancelled = false;
    public static boolean isSupportRideIssueUpdated = false;
    public static String currentActivity = null;
    public static boolean activityResumed = false;
    private static CaptureUserData fuguUserData;

    public static int jungleApisDisable = 0;

    public static void clearDataOnLogout(Activity context) {
        try {
            userData = null;
            autoData = null;
            freshData = null;
            mealsData = null;
            feedData = null;
            prosData = null;
            DeliveryData = null;
            deepLinkReferralCode = "";

            facebookUserData = null;
            previousAccountInfoList = new ArrayList<PreviousAccountInfo>();

            AccessTokenGenerator.saveLogoutToken(context);
            clearSPLabelPrefs(context);
            HippoConfig.clearHippoData(context);

        } catch (Exception e) {
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
			Prefs.with(context).remove(Constants.KEY_USER_ID);

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
            Prefs.with(context).remove(Constants.SP_RESTAURANT_ID_TO_DEEP_LINK);
            Prefs.with(context).remove(Constants.SP_RESTAURANT_FEEDBACK_ID_TO_DEEP_LINK);
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


//            Prefs.with(context).remove(Constants.SP_MENUS_CART);
            Prefs.with(context).remove(Constants.SP_FRESH_CHECKOUT_SAVE_DATA);
            Prefs.with(context).remove(Constants.SP_MEALS_CHECKOUT_SAVE_DATA);
            Prefs.with(context).remove(Constants.SP_GROCERY_CHECKOUT_SAVE_DATA);
            Prefs.with(context).remove(Constants.SP_MENUS_CHECKOUT_SAVE_DATA);

            Prefs.with(context).remove(Constants.KEY_SP_LAST_OPENED_CLIENT_ID);
            Prefs.with(context).remove(SPLabels.USERNAME_UPDATED);
            Prefs.with(context).remove(Constants.SP_PAYTM_LAST_BALANCE);
            Prefs.with(context).remove(Constants.SP_MOBIKWIK_LAST_BALANCE);
            Prefs.with(context).remove(Constants.SP_FREECHARGE_LAST_BALANCE);
            Prefs.with(context).remove(Constants.KEY_TUTORIAL_SKIPPED);
            Prefs.with(context).remove(Constants.SP_YOUTUBE_TUTORIAL_SKIPPED);

            context.getSharedPreferences(Data.SHARED_PREF_NAME, context.MODE_PRIVATE).edit()
                    .remove(Constants.SP_CONTACTS_SYNCED).commit();

            Prefs.with(context).remove(context.getResources().getString(R.string.pref_address_selected));

            Paper.book().delete(PaperDBKeys.DB_FRESH_CART);
            Paper.book().delete(PaperDBKeys.DB_MEALS_CART);
            Paper.book().delete(PaperDBKeys.DB_PREVIOUS_VENDOR);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static final String REGISTRATION_COMPLETE = "REGISTRATION_COMPLETE";
    public static final String REGISTRATION_FAILED = "REGISTRATION_FAILED";
    public static final String DEVICE_TOKEN = "DEVICE_TOKEN";
    public static final String ERROR = "ERROR";
    public static final String INTENT_CLASS_NAME = "intentClassName";

    public static Uri splashIntentUri;


    public static void getDeepLinkIndexFromIntent(Context context, Intent newIntent) {
        Data.deepLinkIndex = -1;
        Data.deepLinkPickup = -1;
        Data.deepLinkReferralCode = "";
        try {
            Intent intent = newIntent;
            String action = intent.getAction();
            Uri data = intent.getData();
            Log.e("action", "=" + action);
            Log.e("data", "=" + data);


            // Deep linking share.jugnoo.in/review/<review_id> with restaurant feed page
            boolean dontTryParsingDeeplink = false;
            if (data != null && !TextUtils.isEmpty(data.getPath()) && data.getPath().contains(Constants.KEY_REVIEW)) {
                try {
                    String[] arr = data.getPath().split(Constants.KEY_REVIEW + "/");
                    String[] arr1 = arr[1].split("/");
                    Prefs.with(context).save(Constants.SP_RESTAURANT_ID_TO_DEEP_LINK, arr1[0]);
                    Data.deepLinkIndex = AppLinkIndex.MENUS_PAGE.getOrdinal();
                    dontTryParsingDeeplink = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            // CAMPAIGN TRACK PUSH
            // if intent extras has campaign_id it hit /track_push api on server
            // for that campaign id with status opened
            if (intent.getIntExtra(Constants.KEY_CAMPAIGN_ID, 0) > 0) {
                int campaignId = intent.getIntExtra(Constants.KEY_CAMPAIGN_ID, 0);
                new ApiTrackPush().hit(context, campaignId, ApiTrackPush.Status.OPENED);
            }

            // Referral code cached from intent query param
            if (data.getQueryParameter(Constants.KEY_REFERRAL_CODE) != null) {
                Data.deepLinkReferralCode = data.getQueryParameter(Constants.KEY_REFERRAL_CODE);
            }


            // Parsing deep index value if it is not from restaurant deep link
            if (!dontTryParsingDeeplink && data.getQueryParameter(Constants.KEY_DEEPINDEX) != null) {
                Data.deepLinkIndex = Integer.parseInt(data.getQueryParameter(Constants.KEY_DEEPINDEX));

                //restaurant_id deeplinked in case of custom scheme
                if(data.getQueryParameter("restaurant_id") != null){
                    Prefs.with(context).save(Constants.SP_RESTAURANT_ID_TO_DEEP_LINK, data.getQueryParameter("restaurant_id"));
                }

                // For deep linking with Offering Order Status Fragment
                if (intent.getIntExtra(Constants.KEY_ORDER_ID, 0) != 0) {
                    Data.deepLinkOrderId = intent.getIntExtra(Constants.KEY_ORDER_ID, 0);
                    Data.deepLinkProductType = intent.getIntExtra(Constants.KEY_PRODUCT_TYPE, ProductType.MEALS.getOrdinal());
                }
                // For deep linking with Feed particular post_id
                else if (intent.getIntExtra(Constants.KEY_POST_ID, -1) != -1) {
                    Prefs.with(context).save(Constants.SP_POST_ID_TO_OPEN, intent.getIntExtra(Constants.KEY_POST_ID, -1));
                    Prefs.with(context).save(Constants.SP_POST_NOTIFICATION_ID_TO_OPEN, intent.getIntExtra(Constants.KEY_POST_NOTIFICATION_ID, -1));
                }
            }


            if (intent.hasExtra(Constants.KEY_TAB_INDEX)) {
                Data.tabLinkIndex = intent.getIntExtra(Constants.KEY_TAB_INDEX, 0);
            }

//			if(intent.hasExtra(Constants.KEY_SP_LAST_OPENED_CLIENT_ID)){
//				Prefs.with(context).save(Constants.KEY_SP_PUSH_OPENED_CLIENT_ID, intent.getStringExtra(Constants.KEY_SP_LAST_OPENED_CLIENT_ID));
//				Prefs.with(context).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, intent.getStringExtra(Constants.KEY_SP_LAST_OPENED_CLIENT_ID));
//			}

            else if (data.getQueryParameter("pickup_lat") != null && data.getQueryParameter("pickup_lng") != null) {
                Data.deepLinkPickup = 1;
                Data.deepLinkPickupLatitude = Double.parseDouble(data.getQueryParameter("pickup_lat"));
                Data.deepLinkPickupLongitude = Double.parseDouble(data.getQueryParameter("pickup_lng"));

                Log.e("deepLinkPickup =", "=" + Data.deepLinkPickup);
                Log.e("deepLinkPickupLatitude =", "=" + Data.deepLinkPickupLatitude);
                Log.e("deepLinkPickupLongitude =", "=" + Data.deepLinkPickupLongitude);
            } else {
                throw new Exception();
            }

        } catch (Exception e) {
//			e.printStackTrace();
            //jungooautos://open?link_click_id=link-178470536899245547&target_url=http%3A%2F%2Fshare.jugnoo.in%2Fm%2F7MPH22Lyln%3Fdeepindex%3D0
            try {
                Intent intent = newIntent;
                Uri data = intent.getData();
                Log.e("data", "=" + data);

                String targetUrl = URLDecoder.decode(data.getQueryParameter("target_url"), "UTF-8");
                Uri dataTarget = Uri.parse(targetUrl);

                if (dataTarget.getQueryParameter(Constants.KEY_REFERRAL_CODE) != null) {
                    Data.deepLinkReferralCode = dataTarget.getQueryParameter(Constants.KEY_REFERRAL_CODE);
                }

                if (dataTarget.getQueryParameter(Constants.KEY_DEEPINDEX) != null) {
                    Data.deepLinkIndex = Integer.parseInt(dataTarget.getQueryParameter(Constants.KEY_DEEPINDEX));
                }

                if (intent.hasExtra(Constants.KEY_TAB_INDEX)) {
                    Data.tabLinkIndex = intent.getIntExtra(Constants.KEY_TAB_INDEX, 0);
                }

//				if(intent.hasExtra(Constants.KEY_SP_LAST_OPENED_CLIENT_ID)){
//					Prefs.with(context).save(Constants.KEY_SP_PUSH_OPENED_CLIENT_ID, intent.getStringExtra(Constants.KEY_SP_LAST_OPENED_CLIENT_ID));
//					Prefs.with(context).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, intent.getStringExtra(Constants.KEY_SP_LAST_OPENED_CLIENT_ID));
//				}

                else if (dataTarget.getQueryParameter("pickup_lat") != null && dataTarget.getQueryParameter("pickup_lng") != null) {
                    Data.deepLinkPickup = 1;
                    Data.deepLinkPickupLatitude = Double.parseDouble(dataTarget.getQueryParameter("pickup_lat"));
                    Data.deepLinkPickupLongitude = Double.parseDouble(dataTarget.getQueryParameter("pickup_lng"));

                    Log.e("deepLinkPickup =", "=" + Data.deepLinkPickup);
                    Log.e("deepLinkPickupLatitude =", "=" + Data.deepLinkPickupLatitude);
                    Log.e("deepLinkPickupLongitude =", "=" + Data.deepLinkPickupLongitude);
                }
            } catch (Exception e1) {
//				e1.printStackTrace();
            }

        }

        try {
            if (AppLinkIndex.FRESH_PAGE.getOrdinal() == Data.deepLinkIndex) {
                Prefs.with(context).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId());
            } else if (AppLinkIndex.MEAL_PAGE.getOrdinal() == Data.deepLinkIndex) {
                Prefs.with(context).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getMealsClientId());
            } else if (AppLinkIndex.MENUS_PAGE.getOrdinal() == Data.deepLinkIndex) {
                Prefs.with(context).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getMenusClientId());
            } else if (AppLinkIndex.DELIVERY_CUSTOMER_PAGE.getOrdinal() == Data.deepLinkIndex) {
                Prefs.with(context).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getDeliveryCustomerClientId());
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        //Data.deepLinkReferralCode = Prefs.with(context).getString(Constants.DEEP_LINK_REFERRAL_CODE, "");
        Log.e("Deeplink =", "=" + Data.deepLinkIndex);
        Log.e("deepLinkReferralCode =", "=" + Data.deepLinkReferralCode);

        try {
            if (newIntent.getExtras() != null && newIntent.hasExtra(Constants.KEY_SP_LAST_OPENED_CLIENT_ID)) {
                Prefs.with(context).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, newIntent.getStringExtra(Constants.KEY_SP_LAST_OPENED_CLIENT_ID));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static final String LOCAL_BROADCAST = "fatafat-cart-broadcast";
    public static final String LATLNG = "latlng";
    public static String PAYTM_STATUS_ACTIVE = "ACTIVE",
            PAYTM_STATUS_INACTIVE = "INACTIVE";


    //public static int isfatafat = 0;


    public static int tabLinkIndex = 0;


    public static LoginResponse.Menus getFreshData() {
        return freshData;
    }

    public static void setFreshData(LoginResponse.Menus freshData) {
        Data.freshData = freshData;
    }

    public static LoginResponse.Menus getGroceryData() {
        return groceryData;
    }

    public static void setGroceryData(LoginResponse.Menus groceryData) {
        Data.groceryData = groceryData;
    }

    public static LoginResponse.Meals getMealsData() {
        return mealsData;
    }

    public static void setMealsData(LoginResponse.Meals mealsData) {
        Data.mealsData = mealsData;
    }


    public static DeliveryData getDeliveryData() {
        return DeliveryData;
    }

    public static void setDeliveryData(DeliveryData deliveryData) {
        DeliveryData = deliveryData;
    }

    public static LoginResponse.Menus getMenusData() {
        return menusData;
    }
    public static LoginResponse.DeliveryCustomer getDeliveryCustomerData() {
        return deliveryCustomer;
    }

    public static void setMenusData(LoginResponse.Menus menusData) {
        Data.menusData = menusData;
    }

    public static void setDeliveryCustomerData(LoginResponse.DeliveryCustomer deliveryCustomerData){
        Data.deliveryCustomer = deliveryCustomerData;
    }

    public static PayData getPayData() {
        return payData;
    }

    public static void setPayData(PayData payData) {
        Data.payData = payData;
    }

    private static LatLng latLngOfJeanieLastShown;

    public static LatLng getLatLngOfJeanieLastShown() {
        return latLngOfJeanieLastShown;
    }

    public static void setLatLngOfJeanieLastShown(LatLng latLngOfJeanieLastShown) {
        Data.latLngOfJeanieLastShown = latLngOfJeanieLastShown;
    }

    private static HistoryResponse.Datum datumToReOrder;

    public static HistoryResponse.Datum getDatumToReOrder() {
        return datumToReOrder;
    }

    public static void setDatumToReOrder(HistoryResponse.Datum datumToReOrder) {
        Data.datumToReOrder = datumToReOrder;
    }

    private static boolean jeanieShownInSession = false;

    public static void setJeanieShownInSession(boolean someBoolean) {
        jeanieShownInSession = someBoolean;
    }

    public static boolean isJeanieShownInSession() {
        return jeanieShownInSession;
    }

    public static LoginResponse.Feed getFeedData() {
        return feedData;
    }

    public static void setFeedData(LoginResponse.Feed feedData) {
        Data.feedData = feedData;
    }

    public static String getFeedName(Context context) {
        if (Data.getFeedData() != null && !TextUtils.isEmpty(Data.getFeedData().getFeedName())) {
            return Data.getFeedData().getFeedName();
        } else {
            return context.getString(R.string.fatafat_offering_name);
        }
    }

    public static CaptureUserData getFuguUserData() {
        if (userData == null)
            return null;


        return new CaptureUserData.Builder().userUniqueKey(userData.userIdentifier).fullName(userData.userName).email(userData.userEmail)
                .phoneNumber(userData.phoneNo)
                .city(userData.getCity())
                .latitude(Data.loginLatitude)
                .longitude(Data.loginLongitude)
                //.channelInfoJson(userData.getFuguChannelInfoJson())
                .build();


    }

	static final int FUGU_APP_TYPE = 1;
    static void initializeFuguHandler(Activity context, CaptureUserData userData, String token) {
		HippoConfigAttributes configAttributes;
		if (Config.getConfigMode() == ConfigMode.LIVE) {
            configAttributes = new HippoConfigAttributes.Builder()
                    .setEnvironment("live")
                    .setAppKey(Prefs.with(context).getString(Constants.KEY_FUGU_APP_KEY, context.getString(R.string.fugu_key)))
                    .setAppType(String.valueOf(Prefs.with(context).getInt(Constants.KEY_FUGU_APP_TYPE, Data.FUGU_APP_TYPE)))
                    .setCaptureUserData(userData)
                    .setProvider(context.getString(R.string.file_provider_name))
                    .setDeviceToken(token)
					.setColorConfig(getHippoColorConfig())
                    .build();
            HippoConfig.initHippoConfig(context, configAttributes);
        } else {
            configAttributes = new HippoConfigAttributes.Builder()
					.setEnvironment("test")
					.setShowLog(true)
                    .setAppKey(Prefs.with(context).getString(Constants.KEY_FUGU_APP_KEY, context.getString(R.string.fugu_key)))
                    .setAppType(String.valueOf(Prefs.with(context).getInt(Constants.KEY_FUGU_APP_TYPE, Data.FUGU_APP_TYPE)))
                    .setCaptureUserData(userData)
                    .setProvider(context.getString(R.string.file_provider_name))
                    .setDeviceToken(token)
                    .setColorConfig(getHippoColorConfig())
                    .build();
            HippoConfig.initHippoConfig(context, configAttributes);
        }
    }

    private static HippoColorConfig getHippoColorConfig(){
		return new HippoColorConfig.Builder()
				.hippoActionBarBg(FuguColorConfigStrings.FUGU_ACTION_BAR_BG)
				.hippoActionBarText(FuguColorConfigStrings.FUGU_ACTION_BAR_TEXT)
				.hippoBgMessageYou(FuguColorConfigStrings.FUGU_BG_MESSAGE_YOU)
				.hippoBgMessageFrom(FuguColorConfigStrings.FUGU_BG_MESSAGE_FROM)
				.hippoPrimaryTextMsgYou(FuguColorConfigStrings.FUGU_PRIMARY_TEXT_MSG_YOU)
				.hippoMessageRead(FuguColorConfigStrings.FUGU_MESSAG_EREAD)
				.hippoPrimaryTextMsgFrom(FuguColorConfigStrings.FUGU_PRIMARY_TEXT_MSG_FROM)
				.hippoSecondaryTextMsgYou(FuguColorConfigStrings.FUGU_SECONDARY_TEXT_MSG_YOU)
				.hippoSecondaryTextMsgFrom(FuguColorConfigStrings.FUGU_SECONDARY_TEXT_MSG_FROM)
				.hippoTextColorPrimary(FuguColorConfigStrings.FUGU_TEXT_COLOR_PRIMARY)
				.hippoTextColorSecondary(FuguColorConfigStrings.FUGU_TEXT_COLOR_SECONDARY)
				.hippoChannelDateText(FuguColorConfigStrings.FUGU_CHANNEL_DATE_TEXT)
				.hippoChatBg(FuguColorConfigStrings.FUGU_CHAT_BG)
				.hippoBorderColor(FuguColorConfigStrings.FUGU_BORDER_COLOR)
				.hippoChatDateText(FuguColorConfigStrings.FUGU_CHAT_DATE_TEXT)
				.hippoThemeColorPrimary(FuguColorConfigStrings.FUGU_THEME_COLOR_PRIMARY)
				.hippoThemeColorSecondary(FuguColorConfigStrings.FUGU_THEME_COLOR_SECONDARY)
				.hippoTypeMessageBg(FuguColorConfigStrings.FUGU_TYPE_MESSAGE_BG)
				.hippoTypeMessageHint(FuguColorConfigStrings.FUGU_TYPE_MESSAGE_HINT)
				.hippoTypeMessageText(FuguColorConfigStrings.FUGU_TYPE_MESSAGE_TEXT)
				.hippoChannelBg(FuguColorConfigStrings.FUGU_CHANNEL_BG)
				.hippoChannelItemBg(FuguColorConfigStrings.FUGU_CHANNEL_BG)
				.build();
	}


	public static boolean isFuguChatEnabled() {
        if(Data.userData != null) {
            ArrayList<MenuInfo> itemsToShow = Data.userData.getMenuInfoList();
            for (MenuInfo menuInfo : itemsToShow) {
                if (MenuInfoTags.FUGU_SUPPORT.getTag().equalsIgnoreCase(menuInfo.getTag())) {
                    return true;
                }
            }
        }
        return false;
    }
    public static boolean isMenuTagEnabled(MenuInfoTags tag) {
        if(Data.userData != null) {
            ArrayList<MenuInfo> itemsToShow = Data.userData.getMenuInfoList();
            for (MenuInfo menuInfo : itemsToShow) {
                if (tag.getTag().equalsIgnoreCase(menuInfo.getTag())) {
                    return true;
                }
            }
        }
        return false;
    }


    private static Bundle fuguChatBundle;

    public static Bundle getFuguChatBundle() {
        return fuguChatBundle;
    }

    public static void setFuguChatBundle(Bundle fuguChatBundle) {
        Data.fuguChatBundle = fuguChatBundle;
    }

    private static boolean recentAddressesFetched = false;

    public static boolean isRecentAddressesFetched() {
        return recentAddressesFetched;
    }

    public static void setRecentAddressesFetched(boolean recentAddressesFetched) {
        Data.recentAddressesFetched = recentAddressesFetched;
    }

    public static long CHANNEL_ID_FUGU_ISSUE_RIDE(){
        return Config.getConfigMode()==ConfigMode.LIVE?43:43;
    }

    public static long CHANNEL_ID_FUGU_ISSUE_ORDER(){
        return Config.getConfigMode()==ConfigMode.LIVE?56:435;
    }

    public static long CHANNEL_ID_FUGU_BULK_MEALS(){
        return Config.getConfigMode()==ConfigMode.LIVE?556:437;
    }

    public static long CHANNEL_ID_FUGU_MENUS_DELIVERY_LATE(){
        return Config.getConfigMode()==ConfigMode.LIVE?57:610;
    }
    public static long CHANNEL_ID_FUGU_DELIVERY_CUSTOMER_DELIVERY_LATE(){
        return Config.getConfigMode()==ConfigMode.LIVE?57:610;
    }

	public static void saveCurrentIciciUpiTransaction(PlaceOrderResponse placeOrderResponse, int applicationType){

        Paper.book().write(Constants.PLACE_ORDER_DATA + applicationType,placeOrderResponse);


	}
	public static PlaceOrderResponse getCurrentIciciUpiTransaction(int applicationType){



		return Paper.book().read(Constants.PLACE_ORDER_DATA+applicationType,null);


	}

	public static void deleteCurrentIciciUpiTransaction(int applicationType){

    	Paper.book().delete(Constants.PLACE_ORDER_DATA+ applicationType);


	}

    public static LoginResponse.Pros getProsData() {
        return prosData;
    }

    public static void setProsData(LoginResponse.Pros prosData) {
        Data.prosData = prosData;
    }

    private static LatLng indiaCentre = null;
    public static LatLng getIndiaCentre(){
        if(indiaCentre == null){
            indiaCentre = new LatLng(22.971723, 78.754263);
        }
        return indiaCentre;
    }




    private static final String JUGNOO_LAST_ACTIVITY_OPEN = "jugnoo_last_activity_open";
    public static void setLastActivityOnForeground(Activity activity){
        Prefs.with(activity).save(JUGNOO_LAST_ACTIVITY_OPEN,activity.getClass().getName());
    }

    public static Class getLastActivityOnForeground(Context context) throws Exception {
        String lastActivityOpen = Prefs.with(context).getString(JUGNOO_LAST_ACTIVITY_OPEN,null);
            return Class.forName(lastActivityOpen);
    }


}

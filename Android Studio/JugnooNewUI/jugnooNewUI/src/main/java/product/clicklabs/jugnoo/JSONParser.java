package product.clicklabs.jugnoo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.facebook.appevents.AppEventsConstants;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.sabkuchfresh.datastructure.PopupData;
import com.sabkuchfresh.retrofit.model.Store;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import product.clicklabs.jugnoo.apis.ApiFindADriver;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.AutoData;
import product.clicklabs.jugnoo.datastructure.CancelOption;
import product.clicklabs.jugnoo.datastructure.CancelOptionsList;
import product.clicklabs.jugnoo.datastructure.CouponInfo;
import product.clicklabs.jugnoo.datastructure.DiscountType;
import product.clicklabs.jugnoo.datastructure.DriverInfo;
import product.clicklabs.jugnoo.datastructure.EmergencyContact;
import product.clicklabs.jugnoo.datastructure.EndRideData;
import product.clicklabs.jugnoo.datastructure.EngagementStatus;
import product.clicklabs.jugnoo.datastructure.FeedbackReason;
import product.clicklabs.jugnoo.datastructure.FreshData;
import product.clicklabs.jugnoo.datastructure.LoginVia;
import product.clicklabs.jugnoo.datastructure.PassengerScreenMode;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.datastructure.PaytmRechargeInfo;
import product.clicklabs.jugnoo.datastructure.PreviousAccountInfo;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.datastructure.PromotionInfo;
import product.clicklabs.jugnoo.datastructure.ReferralMessages;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.datastructure.UserData;
import product.clicklabs.jugnoo.datastructure.UserMode;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.home.models.MenuInfo;
import product.clicklabs.jugnoo.home.models.RateAppDialogContent;
import product.clicklabs.jugnoo.home.models.Region;
import product.clicklabs.jugnoo.home.models.RideEndGoodFeedbackViewType;
import product.clicklabs.jugnoo.home.models.VehicleIconSet;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.Driver;
import product.clicklabs.jugnoo.retrofit.model.FareStructure;
import product.clicklabs.jugnoo.retrofit.model.FindADriverResponse;
import product.clicklabs.jugnoo.retrofit.model.LoginResponse;
import product.clicklabs.jugnoo.t20.models.Schedule;
import product.clicklabs.jugnoo.t20.models.Team;
import product.clicklabs.jugnoo.utils.BranchMetricsUtils;
import product.clicklabs.jugnoo.utils.DateComparatorCoupon;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.FbEvents;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.NudgeClient;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.SHA256Convertor;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class JSONParser implements Constants {


    private final String TAG = JSONParser.class.getSimpleName();


    public JSONParser() {

    }

    public static String getServerMessage(JSONObject jObj) {
        String message = Data.SERVER_ERROR_MSG;
        try {
            if (jObj.has("message")) {
                message = jObj.getString("message");
            } else if (jObj.has("log")) {
                message = jObj.getString("log");
            } else if (jObj.has("error")) {
                message = jObj.getString("error");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }


    public void parseUserData(Context context, JSONObject userData, LoginResponse.UserData loginUserData) throws Exception {

        String userName = userData.optString("user_name", "");
        String phoneNo = userData.optString("phone_no", "");
        String userImage = userData.optString("user_image", "");
        String referralCode = userData.optString(KEY_REFERRAL_CODE, "");
        double jugnooBalance = userData.optDouble(KEY_JUGNOO_BALANCE, 0);
        String userEmail = userData.optString("user_email", "");
        int emailVerificationStatus = userData.optInt("email_verification_status", 1);
        String jugnooFbBanner = userData.optString("jugnoo_fb_banner", "");
        int numCouponsAvailable = userData.optInt("num_coupons_available", 0);
        String authKey = userData.optString("auth_key", "");
        AccessTokenGenerator.saveAuthKey(context, authKey);
        String authSecret = authKey + Config.getClientSharedSecret();
        String accessToken = SHA256Convertor.getSHA256String(authSecret);

        String userIdentifier = userData.optString("user_identifier", userEmail);

        Prefs.with(context).save(SP_KNOWLARITY_MISSED_CALL_NUMBER,
                userData.optString(KEY_KNOWLARITY_MISSED_CALL_NUMBER, ""));
        Prefs.with(context).save(SP_OTP_VIA_CALL_ENABLED,
                userData.optInt(KEY_OTP_VIA_CALL_ENABLED, 1));
		int promoSuccess = userData.optInt(KEY_PROMO_SUCCESS, 1);
        String promoMessage = userData.optString(KEY_PROMO_MESSAGE,
                context.getResources().getString(R.string.promocode_invalid_message_on_signup));


        int showJugnooJeanie = userData.optInt("jugnoo_sticky", 0);
        int cToDReferralEnabled = userData.optInt("c2d_referral_enabled", 0);
        Prefs.with(context).save(SPLabels.SHOW_JUGNOO_JEANIE, showJugnooJeanie);

        if(userData.has("user_saved_addresses")){
            JSONArray userSavedAddressArray = userData.getJSONArray("user_saved_addresses");
            for(int i=0; i<userSavedAddressArray.length(); i++){
                JSONObject jsonObject = userSavedAddressArray.getJSONObject(i);
                if(jsonObject.optString("type").equalsIgnoreCase("home")){
                    if(!jsonObject.optString("address").equalsIgnoreCase("")){
                        JSONObject json = new JSONObject();
                        json.put("address", jsonObject.optString("address"));
                        json.put("name", jsonObject.optString("type"));
                        json.put("placeId", jsonObject.optString("google_place_id"));
                        String strResult = json.toString();
                        Prefs.with(context).save(SPLabels.ADD_HOME, strResult);
                    }else {
                        Prefs.with(context).save(SPLabels.ADD_HOME, "");
                    }

                }else if(jsonObject.optString("type").equalsIgnoreCase("work")){
                    if(!jsonObject.optString("address").equalsIgnoreCase("")){
                        JSONObject json = new JSONObject();
                        json.put("address", jsonObject.optString("address"));
                        json.put("name", jsonObject.optString("type"));
                        json.put("placeId", jsonObject.optString("google_place_id"));
                        String strResult = json.toString();
                        Prefs.with(context).save(SPLabels.ADD_WORK, strResult);
                    }else {
                        Prefs.with(context).save(SPLabels.ADD_HOME, "");
                    }
                }
            }
        }

        String defaultBranchDesktopUrl = Prefs.with(context).getString(SPLabels.BRANCH_DESKTOP_URL, "");
        String defaultBranchAndroidUrl = Prefs.with(context).getString(SPLabels.BRANCH_ANDROID_URL, "");
        String defaultBranchIosUrl = Prefs.with(context).getString(SPLabels.BRANCH_IOS_URL, "");
        String defaultBranchFallbackUrl = Prefs.with(context).getString(SPLabels.BRANCH_FALLBACK_URL, "");

        String branchDesktopUrl = userData.optString(KEY_BRANCH_DESKTOP_URL, defaultBranchDesktopUrl);
        String branchAndroidUrl = userData.optString(KEY_BRANCH_ANDROID_URL, defaultBranchAndroidUrl);
        String branchIosUrl = userData.optString(KEY_BRANCH_IOS_URL, defaultBranchIosUrl);
        String branchFallbackUrl = userData.optString(KEY_BRANCH_FALLBACK_URL, defaultBranchFallbackUrl);


        String jugnooCashTNC = userData.optString(KEY_JUGNOO_CASH_TNC,
                context.getResources().getString(R.string.jugnoo_cash_tnc));

        String inAppSupportPanelVersion = userData.optString(KEY_SP_IN_APP_SUPPORT_PANEL_VERSION, "0");

        int getGogu = userData.optInt(KEY_GET_GOGU, 0);

        String userId = userData.optString(KEY_USER_ID, phoneNo);
        Prefs.with(context).save(SP_USER_ID, userId);

        String inviteEarnScreenImage = userData.optString(KEY_INVITE_EARN_SCREEN_IMAGE_ANDROID, "");

        int t20WCEnable = userData.optInt(KEY_T20_WC_ENABLE, 0);
        String t20WCScheduleVersion = userData.optString(KEY_SP_T20_WC_SCHEDULE_VERSION, "0");
        String t20WCInfoText = userData.optString(KEY_T20_WC_INFO_TEXT, "");
        String publicAccessToken = userData.optString(KEY_PUBLIC_ACCESS_TOKEN, "");

        Prefs.with(context).save(KEY_SP_DEVICE_TOKEN_REFRESH_INTERVAL, userData.optLong(KEY_SP_DEVICE_TOKEN_REFRESH_INTERVAL,
                DEFAULT_DEVICE_TOKEN_REFRESH_INTERVAL));


        int gamePredictEnable = userData.optInt(KEY_GAME_PREDICT_ENABLE, 0);
        String gamePredictUrl = userData.optString(KEY_GAME_PREDICT_URL, "https://jugnoo.in/wct20");
        String gamePredictIconUrl = "", gamePredictName = "", gamePredictNew = "";



        String fatafatUrlLink = userData.optString("fatafat_url_link", "");


        int notificationPreferenceEnabled = userData.optInt(KEY_NOTIFICATION_PREFERENCE_ENABLED, 0);

        try {
            String gamePredictViewData = userData.optString(KEY_GAME_PREDICT_VIEW_DATA, "");
            gamePredictIconUrl = gamePredictViewData.split(VIEW_DATA_SPLITTER)[0];
            gamePredictName = gamePredictViewData.split(VIEW_DATA_SPLITTER)[1];
            gamePredictNew = gamePredictViewData.split(VIEW_DATA_SPLITTER)[2];
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(Prefs.with(context).getInt(SP_FIRST_LOGIN_COMPLETE, 0) == 0){
            long appOpenTime = Prefs.with(context).getLong(SP_FIRST_OPEN_TIME, System.currentTimeMillis());
            long diff = System.currentTimeMillis() - appOpenTime;
            long diffSeconds = diff / 1000;
            HashMap<String, String> map = new HashMap<>();
            map.put(KEY_TIME_DIFF_SEC, String.valueOf(diffSeconds));
            FlurryEventLogger.event(context, FlurryEventNames.LOGIN_SINCE_FIRST_APP_OPEN_DIFF, map);
            Prefs.with(context).save(SP_FIRST_LOGIN_COMPLETE, 1);
        }

        String city = userData.optString(KEY_CITY, "");
        String cityReg = userData.optString(KEY_CITY_REG, "");

        int referralLeaderboardEnabled = userData.optInt(KEY_REFERRAL_LEADERBOARD_ENABLED, 1);
        int referralActivityEnabled = userData.optInt(KEY_REFERRAL_ACTIVITY_ENABLED, 1);

        int paytmEnabled = userData.optInt(KEY_PAYTM_ENABLED, 0);
        int mobikwikEnabled = userData.optInt(KEY_MOBIKWIK_ENABLED, 0);
        int mealsEnabled = userData.optInt(KEY_MEALS_ENABLED, 0);
        int freshEnabled = userData.optInt(KEY_FRESH_ENABLED, 0);
        int deliveryEnabled = userData.optInt(KEY_DELIVERY_ENABLED, 0);
        String defaultClientId = userData.optString(KEY_DEFAULT_CLIENT_ID, Config.getAutosClientId());

        int inviteFriendButton = userData.optInt(KEY_INVITE_FRIEND_BUTTON, 0);

        ArrayList<PromoCoupon> promoCoupons = new ArrayList<>();


        Data.userData = new UserData(userIdentifier, accessToken, authKey, userName, userEmail, emailVerificationStatus,
                userImage, referralCode, phoneNo, jugnooBalance,
                jugnooFbBanner, numCouponsAvailable,
                promoSuccess, promoMessage, showJugnooJeanie,
                branchDesktopUrl, branchAndroidUrl, branchIosUrl, branchFallbackUrl,
                jugnooCashTNC, inAppSupportPanelVersion, getGogu, userId, inviteEarnScreenImage,
                t20WCEnable, t20WCScheduleVersion, t20WCInfoText, publicAccessToken,
                gamePredictEnable, gamePredictUrl, gamePredictIconUrl, gamePredictName, gamePredictNew,
                cToDReferralEnabled,
                city, cityReg, referralLeaderboardEnabled, referralActivityEnabled,
                fatafatUrlLink, paytmEnabled, mobikwikEnabled, notificationPreferenceEnabled,
                mealsEnabled, freshEnabled, deliveryEnabled, inviteFriendButton, defaultClientId);


        Data.userData.updateWalletBalances(userData.optJSONObject(KEY_WALLET_BALANCE), true);

        MyApplication.getInstance().getWalletCore().parsePaymentModeConfigDatas(userData, Data.userData);

        try {
            Data.userData.setEmergencyContactsList(JSONParser.parseEmergencyContacts(userData));
            Data.userData.setMenuInfoList((ArrayList<MenuInfo>) loginUserData.getMenuInfoList());
            if(Data.userData.getPromoCoupons() == null){
                Data.userData.setPromoCoupons(new ArrayList<PromoCoupon>());
            } else{
                Data.userData.getPromoCoupons().clear();
            }
            if(loginUserData.getPromotions() != null)
                Data.userData.getPromoCoupons().addAll(loginUserData.getPromotions());
            if(loginUserData.getCoupons() != null)
                Data.userData.getPromoCoupons().addAll(loginUserData.getCoupons());

            //parsePromoCoupons(loginUserData);
            if(loginUserData.getSupportNumber() != null){
				Config.saveSupportNumber(context, loginUserData.getSupportNumber());
			}
            Data.userData.setReferralMessages(parseReferralMessages(loginUserData));
            performUserAppMonitoring(context, userData);

            Prefs.with(context).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, defaultClientId);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void parseAutoData(Context context, JSONObject autoData, LoginResponse.Autos autosData) throws Exception{
        try {
            String destinationHelpText = autoData.optString("destination_help_text", "");
            String rideSummaryBadText = autoData.optString("ride_summary_text", context.getResources().getString(R.string.ride_summary_bad_text));
            String cancellationChargesPopupTextLine1 = autoData.optString("cancellation_charges_popup_text_line1", "");
            String cancellationChargesPopupTextLine2 = autoData.optString("cancellation_charges_popup_text_line2", "");
            String inRideSendInviteTextBold = autoData.optString("in_ride_send_invite_text_bold", context.getResources().getString(R.string.send_invites));
            String inRideSendInviteTextNormal = autoData.optString("in_ride_send_invite_text_normal", context.getResources().getString(R.string.send_invites_2));
            String confirmScreenFareEstimateEnable = autoData.optString("confirm_screen_fare_estimate_enabled", "0");
            String poolDestinationPopupText1 = autoData.optString("pool_destination_popup_text1", context.getResources().getString(R.string.pool_rides_offer_guaranteed_fares));
            String poolDestinationPopupText2 = autoData.optString("pool_destination_popup_text2", context.getResources().getString(R.string.please_provide_pickup_and_dest));
            String poolDestinationPopupText3 = autoData.optString("pool_destination_popup_text3", context.getResources().getString(R.string.you_will_not_change_dest));
            int rideEndGoodFeedbackViewType = autoData.optInt("ride_end_good_feedback_view_type", RideEndGoodFeedbackViewType.RIDE_END_IMAGE_1.getOrdinal());
            String rideEndGoodFeedbackText = autoData.optString("ride_end_good_feedback_text", context.getResources().getString(R.string.end_ride_with_image_text));
            String baseFarePoolText = autoData.optString("base_fare_pool_text", "");

            Prefs.with(context).save(Constants.KEY_SHOW_POKEMON_DATA, autoData.optInt(KEY_SHOW_POKEMON_DATA, 0));
            Prefs.with(context).save(KEY_SP_CUSTOMER_LOCATION_UPDATE_INTERVAL, autoData.optLong(KEY_SP_CUSTOMER_LOCATION_UPDATE_INTERVAL,
					LOCATION_UPDATE_INTERVAL));

            int referAllStatus = autoData.optInt(KEY_REFER_ALL_STATUS); // if 0 show popup, else not show
            String referAllText = autoData.optString(KEY_REFER_ALL_TEXT, context.getResources().getString(R.string.upload_contact_message));
            String referAllTitle = autoData.optString(KEY_REFER_ALL_TITLE, context.getResources().getString(R.string.upload_contact_title));

            int referAllStatusLogin = autoData.optInt(KEY_REFER_ALL_STATUS_LOGIN, 1);
            String referAllTextLogin = autoData.optString(KEY_REFER_ALL_TEXT_LOGIN, "");
            String referAllTitleLogin = autoData.optString(KEY_REFER_ALL_TITLE_LOGIN, "");

            Data.autoData = new AutoData(destinationHelpText, rideSummaryBadText, cancellationChargesPopupTextLine1
					, cancellationChargesPopupTextLine2, inRideSendInviteTextBold, inRideSendInviteTextNormal, confirmScreenFareEstimateEnable,
					poolDestinationPopupText1, poolDestinationPopupText2, poolDestinationPopupText3, rideEndGoodFeedbackViewType,
					rideEndGoodFeedbackText, baseFarePoolText,
					referAllStatus, referAllText, referAllTitle, referAllStatusLogin, referAllTextLogin, referAllTitleLogin);


            if(Data.autoData.getPromoCoupons() == null){
                Data.autoData.setPromoCoupons(new ArrayList<PromoCoupon>());
            } else{
                Data.autoData.getPromoCoupons().clear();
            }
            if(autosData.getPromotions() != null)
                Data.autoData.getPromoCoupons().addAll(autosData.getPromotions());
            if(autosData.getCoupons() != null)
                Data.autoData.getPromoCoupons().addAll(autosData.getCoupons());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void parseDeliveryData(LoginResponse.Delivery deliveryData) {
        try {
            if(Data.getDeliveryData().getPromoCoupons() == null){
                Data.getDeliveryData().setPromoCoupons(new ArrayList<PromoCoupon>());
            } else{
                Data.getDeliveryData().getPromoCoupons().clear();
            }
            if(deliveryData.getPromotions() != null)
                Data.getDeliveryData().getPromoCoupons().addAll(deliveryData.getPromotions());
            if(deliveryData.getCoupons() != null)
                Data.getDeliveryData().getPromoCoupons().addAll(deliveryData.getCoupons());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void parseMealsData(LoginResponse.Meals mealsData) {
        try {
            if(Data.getMealsData().getPromoCoupons() == null){
                Data.getMealsData().setPromoCoupons(new ArrayList<PromoCoupon>());
            } else{
                Data.getMealsData().getPromoCoupons().clear();
            }
            if(mealsData.getPromotions() != null)
                Data.getMealsData().getPromoCoupons().addAll(mealsData.getPromotions());
            if(mealsData.getCoupons() != null)
                Data.getMealsData().getPromoCoupons().addAll(mealsData.getCoupons());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void parseFreshData(JSONObject jFatafatData, LoginResponse.Fresh freshData){
        try{
            String orderId = jFatafatData.optString(KEY_FEEDBACK_ORDER_ID, "");
            String question = jFatafatData.optString(KEY_QUESTION, "");
            int questionType = jFatafatData.optInt(KEY_QUESTION_TYPE, 0);
            int pendingFeedback = jFatafatData.optInt(KEY_PENDING_FEEDBACK, 0);
            double amount = jFatafatData.optDouble(KEY_FEEDBACK_AMOUNT, 0);
            String feedbackDeliveryDate = jFatafatData.optString(KEY_FEEDBACK_DATE, "");
            int feedbackViewType = jFatafatData.optInt(KEY_FEEDBACK_VIEW_TYPE, 0);

            PopupData popupData = null;
            try {
                if (jFatafatData.has("popup_data")) {
                    popupData = new PopupData();
                    popupData.popup_id = jFatafatData.getJSONObject("popup_data").optInt("popup_id", 0);
                    popupData.title_text = jFatafatData.getJSONObject("popup_data").optString("title_text", "");
                    popupData.desc_text = jFatafatData.getJSONObject("popup_data").optString("desc_text", "");
                    popupData.image_url = jFatafatData.getJSONObject("popup_data").optString("image_url", "");
                    popupData.cancel_title = jFatafatData.getJSONObject("popup_data").optString("cancel_title", "");
                    popupData.ok_title = jFatafatData.getJSONObject("popup_data").optString("ok_title", "OK");
                    popupData.is_cancellable = jFatafatData.getJSONObject("popup_data").optInt("is_cancellable", 1);
                    popupData.deep_index = jFatafatData.getJSONObject("popup_data").optInt("deep_index", 0);
                    popupData.ext_url = jFatafatData.getJSONObject("popup_data").optString("ext_url", "");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            ArrayList<Store> stores = new ArrayList<>();
            Store store = null;
            try {
                if(jFatafatData.has("stores")) {
                    JSONArray storesArr = jFatafatData.getJSONArray("stores");
                    for(int i=0;i<storesArr.length();i++) {
                        JSONObject jsonObject = storesArr.getJSONObject(i);
                        store = new Store();
                        store.setStoreId(jsonObject.optInt("store_id"));
                        store.setTitle(jsonObject.optString("title"));
                        store.setDescription(jsonObject.optString("description"));
                        store.setImage(jsonObject.optString("image"));
                        store.setTextColor(jsonObject.optString("text_color"));

                        stores.add(store);

                    }
                }
            } catch (Exception e){ e.printStackTrace(); }

            Data.setFreshData(new FreshData(question, orderId, questionType, pendingFeedback, stores, popupData,
                    amount, feedbackDeliveryDate, feedbackViewType));

            try {
                if(Data.getFreshData().getPromoCoupons() == null){
                    Data.getFreshData().setPromoCoupons(new ArrayList<PromoCoupon>());
                } else{
                    Data.getFreshData().getPromoCoupons().clear();
                }
                if(freshData.getPromotions() != null)
                    Data.getFreshData().getPromoCoupons().addAll(freshData.getPromotions());
                if(freshData.getCoupons() != null)
                    Data.getFreshData().getPromoCoupons().addAll(freshData.getCoupons());
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }


    public String parseAccessTokenLoginData(Context context, String response, LoginResponse loginResponse,
                                            LoginVia loginVia) throws Exception {

        JSONObject jObj = new JSONObject(response);

        //Fetching login data
        JSONObject jUserDataObject = jObj.getJSONObject(KEY_USER_DATA);
        JSONObject jAutosObject = jObj.optJSONObject(KEY_AUTOS);
        JSONObject jFreshObject = jObj.optJSONObject(KEY_FRESH);

        parseUserData(context, jUserDataObject, loginResponse.getUserData());
        parseAutoData(context, jAutosObject, loginResponse.getAutos());
        parseFreshData(jFreshObject, loginResponse.getFresh());
        parseMealsData(loginResponse.getMeals());
        parseDeliveryData(loginResponse.getDelivery());

        MyApplication.getInstance().getWalletCore().setDefaultPaymentOption();


        parseFindDriverResp(loginResponse.getAutos());


        //Fetching user current status
        JSONObject jUserStatusObject = jObj.getJSONObject(KEY_AUTOS).getJSONObject(KEY_STATUS);
        String resp = parseCurrentUserStatus(context, loginResponse.getAutos().getCurrentUserStatus(), jUserStatusObject);

        parseCancellationReasons(loginResponse.getAutos());
        parseFeedbackReasonArrayList(loginResponse.getAutos());

        loginAnalyticEvents(context, loginVia);

        return resp;
    }

    public static String getAppSource(Context context){
        StringBuilder sb = new StringBuilder();
        sb.append(Prefs.with(context).getString(Constants.SP_INSTALL_REFERRER_CONTENT, ""));
        if(sb.length() > 0){
            sb.append("&");
        }
        sb.append(KEY_DOWNLOAD_SOURCE).append("=").append(Config.getDownloadSource());
        return sb.toString();
    }

    private void nudgeSignupVerifiedEvent(Context context, String userId, String phoneNo, String email, String userName,
                                          String referralCode, String referralCodeEntered){
        try {
            JSONObject map = new JSONObject();
            map.put(KEY_PHONE_NO, phoneNo);
            map.put(KEY_EMAIL, email);
            map.put(KEY_USER_NAME, userName);
            map.put(KEY_LATITUDE, Data.loginLatitude);
            map.put(KEY_LONGITUDE, Data.loginLongitude);
            map.put(KEY_REFERRAL_CODE, referralCode);
            map.put(KEY_REFERRAL_CODE_ENTERED, referralCodeEntered);
            NudgeClient.trackEventUserId(context, FlurryEventNames.NUDGE_SIGNUP_VERIFIED, map);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void parseFindDriverResp(LoginResponse.Autos autos){
        try {
            //current_user_status = 1 driver or 2 user
            parseDriversToShow(autos.getDrivers());

            Data.autoData.setFareFactor(1);
            if(autos.getFareFactor() != null) {
                Data.autoData.setFareFactor(autos.getFareFactor());
            }
            Data.autoData.setDriverFareFactor(1);
            if(autos.getDriverFareFactor() != null) {
                Data.autoData.setDriverFareFactor(autos.getDriverFareFactor());
            }
            if (autos.getFarAwayCity() == null) {
				Data.autoData.setFarAwayCity("");
			} else {
				Data.autoData.setFarAwayCity(autos.getFarAwayCity());
			}

            Data.autoData.setCampaigns(autos.getCampaigns());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try{
            if(Data.autoData.getRegions() == null){
                Data.autoData.setRegions(new ArrayList<Region>());
            } else{
                Data.autoData.getRegions().clear();
            }
            if(autos.getRegions() != null) {
                HomeUtil homeUtil = new HomeUtil();
                for (Region region : autos.getRegions()) {
                    region.setVehicleIconSet(homeUtil.getVehicleIconSet(region.getIconSet()));
                    region.setIsDefault(false);
                    Data.autoData.getRegions().add(region);
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }

        try {
            if(autos.getFareStructure() != null) {
				Data.autoData.setFareStructure(null);
				for (FareStructure fareStructure : autos.getFareStructure()) {
					String startTime = fareStructure.getStartTime();
					String endTime = fareStructure.getEndTime();
					String localStartTime = DateOperations.getUTCTimeInLocalTimeStamp(startTime);
					String localEndTime = DateOperations.getUTCTimeInLocalTimeStamp(endTime);
					long diffStart = DateOperations.getTimeDifference(DateOperations.getCurrentTime(), localStartTime);
					long diffEnd = DateOperations.getTimeDifference(DateOperations.getCurrentTime(), localEndTime);
					double convenienceCharges = 0;
					if (fareStructure.getConvenienceCharge() != null) {
						convenienceCharges = fareStructure.getConvenienceCharge();
					}
					if (diffStart >= 0 && diffEnd <= 0) {
						product.clicklabs.jugnoo.datastructure.FareStructure fareStructure1 = new product.clicklabs.jugnoo.datastructure.FareStructure(fareStructure.getFareFixed(),
								fareStructure.getFareThresholdDistance(),
								fareStructure.getFarePerKm(),
								fareStructure.getFarePerMin(),
								fareStructure.getFareThresholdTime(),
								fareStructure.getFarePerWaitingMin(),
								fareStructure.getFareThresholdWaitingTime(), convenienceCharges, true,
								fareStructure.getDisplayBaseFare(),
								fareStructure.getDisplayFareText());
						for (int i = 0; i < Data.autoData.getRegions().size(); i++) {
							try {
								if (Data.autoData.getRegions().get(i).getVehicleType().equals(fareStructure.getVehicleType())
										&& Data.autoData.getRegions().get(i).getRideType().equals(fareStructure.getRideType())
										) {
									Data.autoData.getRegions().get(i).setFareStructure(fareStructure1);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						if (Data.autoData.getFareStructure() == null) {
							Data.autoData.setFareStructure(fareStructure1);
						}
					}
				}
				if(Data.autoData.getFareStructure() == null){
					Data.autoData.setFareStructure(getDefaultFareStructure());
				}
			}else{
				Data.autoData.setFareStructure(getDefaultFareStructure());
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parsePromoCoupons(LoginResponse.UserData userData){
        try{
            if(Data.userData.getPromoCoupons() == null){
                Data.userData.setPromoCoupons(new ArrayList<PromoCoupon>());
            } else{
                Data.userData.getPromoCoupons().clear();
            }
            if(userData.getCoupons() != null) {
                for (CouponInfo coupon : userData.getCoupons()) {
                    Data.userData.getPromoCoupons().add(new CouponInfo(coupon.id,
                            coupon.title,
                            coupon.subtitle,
                            coupon.description,
                            coupon.expiryDate));
                }
            }
            if(userData.getPromotions() != null) {
                for (PromotionInfo promotion : userData.getPromotions()) {
                    Data.userData.getPromoCoupons().add(new PromotionInfo(promotion.id,
                            promotion.title,
                            promotion.terms));
                }
            }
            if(userData.getCityId() != null){
                Data.userData.setCurrentCity(userData.getCityId());
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public static product.clicklabs.jugnoo.datastructure.FareStructure getDefaultFareStructure(){
        return new product.clicklabs.jugnoo.datastructure.FareStructure(10, 0, 3, 1, 0, 0, 0, 0, false, null, null);
    }

    public static product.clicklabs.jugnoo.datastructure.FareStructure getFareStructure(){
        if(Data.autoData == null || Data.autoData.getFareStructure() == null) {
            return getDefaultFareStructure();
        } else{
            return Data.autoData.getFareStructure();
        }
    }

    public ReferralMessages parseReferralMessages(LoginResponse.UserData userData) {
        String referralMessage = "Share your referral code " + Data.userData.referralCode +
                " with your friends and they will get a FREE ride because of your referral and once they have used Jugnoo, you will earn a FREE ride (up to Rs. 100) as well.";
        String referralSharingMessage = "Hey, \nUse Jugnoo app to call an auto at your doorsteps. It is cheap, convenient and zero haggling." +
                " Use this referral code: " + Data.userData.referralCode + " to get FREE ride up to Rs. 100." +
                "\nDownload it from here: http://smarturl.it/jugnoo";
        String fbShareCaption = "Use " + Data.userData.referralCode + " as code & get a FREE ride";
        String fbShareDescription = "Try Jugnoo app to call an auto at your doorsteps with just a tap.";
        String referralCaption = "<center><font face=\"verdana\" size=\"2\">Invite <b>friends</b> and<br/>get <b>FREE rides</b></font></center>";
        int referralCaptionEnabled = 0;
        String referralEmailSubject = "Hey! Have you used Jugnoo Autos yet?";
		String referralPopupText = "Up to Rs. 100 in Jugnoo Cash";
        String referralShortMessage = "", referralMoreInfoMessage = "";

        try {
            if (userData.getReferralMessage() != null) {
                referralMessage = userData.getReferralMessage();
            }
            if (userData.getReferralSharingMessage() != null) {
                referralSharingMessage = userData.getReferralSharingMessage();
            }
            if (userData.getFbShareCaption() != null) {
                fbShareCaption = userData.getFbShareCaption();
            }
            if (userData.getFbShareDescription() != null) {
                fbShareDescription = userData.getFbShareDescription();
            }
            if (userData.getReferralCaption() != null) {
                referralCaption = userData.getReferralCaption();
                referralCaption = referralCaption.replaceAll("</br>", "<br/>");
            }
            if(userData.getReferralEmailSubject() != null){
                referralEmailSubject = userData.getReferralEmailSubject();
            }
			if (userData.getReferralPopupText() != null) {
				referralPopupText = userData.getReferralPopupText();
			}
            if(userData.getInviteEarnShortMsg() != null){
                referralShortMessage = userData.getInviteEarnShortMsg();
            }
            if(userData.getInviteEarnMoreInfo() != null){
                referralMoreInfoMessage = userData.getInviteEarnMoreInfo();
            }
		} catch (Exception e) {
            e.printStackTrace();
        }

        ReferralMessages referralMessages = new ReferralMessages(referralMessage, referralSharingMessage, fbShareCaption, fbShareDescription, referralCaption, referralCaptionEnabled,
            referralEmailSubject, referralPopupText, referralShortMessage, referralMoreInfoMessage);

        return referralMessages;
    }


    public void parseLastRideData(JSONObject jObj) {
        try {
            JSONObject jLastRideData = jObj.getJSONObject("last_ride");
            Data.autoData.setcSessionId("");
            Data.autoData.setcEngagementId(jLastRideData.getString("engagement_id"));

            JSONObject jDriverInfo = jLastRideData.getJSONObject("driver_info");
            Data.autoData.setcDriverId(jDriverInfo.getString("id"));

            Data.autoData.setPickupLatLng(new LatLng(0, 0));
            Data.autoData.setDropLatLng(null);

            Data.autoData.setAssignedDriverInfo(new DriverInfo(Data.autoData.getcDriverId(), jDriverInfo.getString("name"), jDriverInfo.getString("user_image"),
                    jDriverInfo.getString("driver_car_image"), jDriverInfo.getString("driver_car_no")));

            try {
                if (jLastRideData.has("rate_app")) {
                    Data.autoData.setCustomerRateAppFlag(jLastRideData.getInt("rate_app"));
                    Data.autoData.setRateAppDialogContent(parseRateAppDialogContent(jLastRideData));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

			Data.autoData.setEndRideData(parseEndRideData(jLastRideData, jLastRideData.getString("engagement_id"), Data.autoData.getFareStructure().getFixedFare()));

            HomeActivity.passengerScreenMode = PassengerScreenMode.P_RIDE_END;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



	public static EndRideData parseEndRideData(JSONObject jLastRideData, String engagementId, double initialBaseFare) throws Exception{
		double baseFare = initialBaseFare;
		if (jLastRideData.has("base_fare")) {
			baseFare = jLastRideData.getDouble("base_fare");
		}

		double fareFactor = 1;
		if(jLastRideData.has("fare_factor")){
			fareFactor = jLastRideData.getDouble("fare_factor");
		}

		double luggageCharge = 0;
		if(jLastRideData.has("total_luggage_charges")){
			luggageCharge = jLastRideData.getDouble("total_luggage_charges");
		}

		double convenienceCharge = 0;
		if(jLastRideData.has("convenience_charge")){
			convenienceCharge = jLastRideData.getDouble("convenience_charge");
		}

		double discount = 0;
		ArrayList<DiscountType> discountTypes = new ArrayList<>();
		try{
			JSONArray jDiscountsArr =  jLastRideData.getJSONArray("discount");
			for(int i=0; i<jDiscountsArr.length(); i++){
				DiscountType discountType = new DiscountType(jDiscountsArr.getJSONObject(i).getString("key"),
						jDiscountsArr.getJSONObject(i).getDouble("value"), 0);
				if(discountType.value > 0) {
					discountTypes.add(discountType);
					discount = discount + discountType.value;
				}
			}
		} catch(Exception e){
			e.printStackTrace();
			try {
				discount = jLastRideData.getDouble("discount");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			discountTypes.clear();
		}

        double sumAdditionalCharges = 0;
        try {
            JSONArray additionalChargesJson = jLastRideData.optJSONArray("additional_charges");
            for(int i=0; i<additionalChargesJson.length(); i++){
                JSONObject obj = additionalChargesJson.getJSONObject(i);
                DiscountType discountType = new DiscountType(obj.optString("text"), obj.optDouble("amount"), obj.optInt("reference_id"));
                if(discountType.value > 0) {
                    discountTypes.add(discountType);
                    sumAdditionalCharges = sumAdditionalCharges + discountType.value;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String driverName = "", driverCarNumber = "", driverImage = "";
		if(jLastRideData.has("driver_name")){
			driverName = jLastRideData.getString("driver_name");
		}
		if(jLastRideData.has("driver_car_number")){
			driverCarNumber = jLastRideData.getString("driver_car_number");
		}
		if(jLastRideData.has("driver_car_no")){
			driverCarNumber = jLastRideData.getString("driver_car_no");
		}
        driverImage = jLastRideData.optString("driver_image", "");
        int isPooled = jLastRideData.optInt(KEY_IS_POOLED);

        double rideTime = -1;
		if(jLastRideData.has("ride_time")){
			rideTime = jLastRideData.getDouble("ride_time");
		}

		double waitTime = -1;
		if(jLastRideData.has("wait_time")){
			waitTime = jLastRideData.getDouble("wait_time");
		}

		int waitingChargesApplicable = jLastRideData.optInt("waiting_charges_applicable", 0);
		double paidUsingPaytm = jLastRideData.optDouble(KEY_PAID_USING_PAYTM, 0);

        engagementId = jLastRideData.optString(KEY_ENGAGEMENT_ID, "0");

        String rideDate = jLastRideData.optString(KEY_RIDE_DATE, "");
        String phoneNumber = jLastRideData.optString(KEY_PHONE_NO, "");
        String tripTotal = jLastRideData.optString(KEY_TRIP_TOTAL, "");

        int vehicleType = jLastRideData.optInt(KEY_VEHICLE_TYPE, VEHICLE_AUTO);
        String iconSet = jLastRideData.optString(KEY_ICON_SET, VehicleIconSet.ORANGE_AUTO.getName());
        String engagementDate = jLastRideData.optString("engagement_date", "");


        double paidUsingMobikwik = jLastRideData.optDouble(KEY_PAID_USING_MOBIKWIK, 0);
        int totalRide = jLastRideData.optInt(Constants.KEY_TOTAL_RIDES_AS_USER, 0);

		return new EndRideData(engagementId, driverName, driverCarNumber, driverImage,
				jLastRideData.getString("pickup_address"),
				jLastRideData.getString("drop_address"),
				jLastRideData.getString("pickup_time"),
				jLastRideData.getString("drop_time"),
				jLastRideData.getDouble("fare"), luggageCharge, convenienceCharge,
				discount,
				jLastRideData.getDouble("paid_using_wallet"),
				jLastRideData.getDouble("to_pay"),
				jLastRideData.getDouble("distance"),
				rideTime, waitTime,
				baseFare, fareFactor, discountTypes, waitingChargesApplicable, paidUsingPaytm,
                rideDate, phoneNumber, tripTotal, vehicleType, iconSet, isPooled,
                sumAdditionalCharges, engagementDate, paidUsingMobikwik, totalRide);
	}



    public String getUserStatus(Context context, String accessToken, int currentUserStatus, ApiFindADriver apiFindADriver,
                                LatLng latLng) {
        try {
            long startTime = System.currentTimeMillis();
            HashMap<String, String> nameValuePairs = new HashMap<>();
            nameValuePairs.put(KEY_ACCESS_TOKEN, accessToken);
            nameValuePairs.put(KEY_LATITUDE, String.valueOf(latLng.latitude));
            nameValuePairs.put(KEY_LONGITUDE, String.valueOf(latLng.longitude));
            Response response = RestClient.getApiServices().getCurrentUserStatus(nameValuePairs);
            String responseStr = new String(((TypedByteArray)response.getBody()).getBytes());
            FlurryEventLogger.eventApiResponseTime(FlurryEventNames.API_GET_CURRENT_USER_STATUS, startTime);
            Log.i(TAG, "getCurrentUserStatus response="+responseStr);
            if (response == null || responseStr == null) {
                return Constants.SERVER_TIMEOUT;
            } else {
                JSONObject jObject1 = new JSONObject(responseStr);
                String resp = parseCurrentUserStatus(context, currentUserStatus, jObject1);

                if(PassengerScreenMode.P_INITIAL == HomeActivity.passengerScreenMode
                        || PassengerScreenMode.P_RIDE_END == HomeActivity.passengerScreenMode) {
                    Gson gson = new Gson();
                    FindADriverResponse findADriverResponse = gson.fromJson(responseStr, FindADriverResponse.class);
                    apiFindADriver.parseFindADriverResponse(findADriverResponse);
                }

                return resp;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Constants.SERVER_TIMEOUT;
        }
    }


    public String parseCurrentUserStatus(Context context, int currentUserStatus, JSONObject jObject1) {
		Log.e("parseCurrentUserStatus jObject1", "="+jObject1);
        String returnResponse = "";

        if (currentUserStatus == 2) {

            String screenMode = "";

            int engagementStatus = -1;
            String engagementId = "", sessionId = "", userId = "", latitude = "", longitude = "",
                    driverName = "", driverImage = "", driverCarImage = "", driverPhone = "", driverRating = "", driverCarNumber = "",
                    pickupLatitude = "", pickupLongitude = "";
            int freeRide = 0, preferredPaymentMode = PaymentOption.CASH.getOrdinal();
			String promoName = "", eta = "";
            double fareFactor = 1.0, dropLatitude = 0, dropLongitude = 0, fareFixed = 0, bearing = 0.0;
            Schedule scheduleT20 = null;
            int vehicleType = VEHICLE_AUTO;
            String iconSet = VehicleIconSet.ORANGE_AUTO.getName();
            String cancelRideThrashHoldTime = "", poolStatusString = "";
            int cancellationCharges = 0, isPooledRide = 0;
            long cancellationTimeOffset = 0;
            ArrayList<String> fellowRiders = new ArrayList<>();


            HomeActivity.userMode = UserMode.PASSENGER;

            try {

                if (jObject1.has("error")) {
                    returnResponse = Constants.SERVER_TIMEOUT;
                    return returnResponse;
                } else {




                    int flag = jObject1.getInt("flag");

                    if(Data.userData != null) {
                        Data.userData.setPaytmRechargeInfo(parsePaytmRechargeInfo(jObject1));
                    }

                    if (ApiResponseFlags.ASSIGNING_DRIVERS.getOrdinal() == flag) {

                        sessionId = jObject1.getString("session_id");
                        double assigningLatitude = 0, assigningLongitude = 0;
                        if (jObject1.has(KEY_LATITUDE) && jObject1.has(KEY_LONGITUDE)) {
                            assigningLatitude = jObject1.getDouble(KEY_LATITUDE);
                            assigningLongitude = jObject1.getDouble(KEY_LONGITUDE);
                            Log.e("assigningLatitude,assigningLongitude ====@@@", "" + assigningLatitude + "," + assigningLongitude);
                        }

                        Data.autoData.setPickupLatLng(new LatLng(assigningLatitude, assigningLongitude));
                        parseDropLatLng(jObject1);

                        engagementStatus = EngagementStatus.REQUESTED.getOrdinal();
                    } else if (ApiResponseFlags.ENGAGEMENT_DATA.getOrdinal() == flag) {
                        JSONArray lastEngInfoArr = jObject1.getJSONArray("last_engagement_info");
                        JSONObject jObject = lastEngInfoArr.getJSONObject(0);

                        engagementStatus = jObject.getInt("status");

                        if (EngagementStatus.ACCEPTED.getOrdinal() == engagementStatus ||
                               EngagementStatus.STARTED.getOrdinal() == engagementStatus ||
                                        EngagementStatus.ARRIVED.getOrdinal() == engagementStatus) {
                            engagementId = jObject.getString("engagement_id");
                            sessionId = jObject.getString("session_id");
                            userId = jObject.getString("driver_id");
                            latitude = jObject.getString("current_location_latitude");
                            longitude = jObject.getString("current_location_longitude");
                            driverName = jObject.getString("user_name");
                            driverImage = jObject.getString("user_image");
                            driverCarImage = jObject.getString("driver_car_image");
                            driverPhone = jObject.getString("phone_no");
                            driverRating = jObject.getString("rating");
                            pickupLatitude = jObject.getString("pickup_latitude");
                            pickupLongitude = jObject.getString("pickup_longitude");

                            try {
                                if(jObject.has(KEY_OP_DROP_LATITUDE) && jObject.has(KEY_OP_DROP_LONGITUDE)) {
                                    dropLatitude = jObject.getDouble(KEY_OP_DROP_LATITUDE);
                                    dropLongitude = jObject.getDouble(KEY_OP_DROP_LONGITUDE);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (jObject.has("driver_car_no")) {
                                driverCarNumber = jObject.getString("driver_car_no");
                            }
                            if (jObject.has("free_ride")) {
                                freeRide = jObject.getInt("free_ride");
                            }

                            promoName = getPromoName(jObject);

                            if (jObject.has("eta")) {
                                eta = jObject.getString("eta");
                            }

                            if (jObject.has("fare_factor")) {
                                fareFactor = jObject.getDouble("fare_factor");
                            }

                            try{
                                fareFixed = jObject.optJSONObject("fare_details").optDouble("fare_fixed", 0);
                            } catch(Exception e){
                                e.printStackTrace();
                            }
							preferredPaymentMode = jObject.optInt("preferred_payment_mode", PaymentOption.CASH.getOrdinal());

                            scheduleT20 = parseT20Schedule(jObject);

                            vehicleType = jObject.optInt(KEY_VEHICLE_TYPE, VEHICLE_AUTO);
                            iconSet = jObject.optString(KEY_ICON_SET, VehicleIconSet.ORANGE_AUTO.getName());

                            try{
                                cancelRideThrashHoldTime = jObject.optString("cancel_ride_threshold_time", "");
                                cancellationCharges = jObject.optInt("cancellation_charge", 0);
                                isPooledRide = jObject.optInt(KEY_IS_POOLED, 0);
                                JSONObject poolData = jObject.optJSONObject("pool_data");
                                bearing = jObject.optDouble("bearing");
                                if(poolData != null) {
                                    poolStatusString = poolData.optString("message", context.getResources().getString(R.string.sharing_your_ride_with));
                                    JSONArray userNames = poolData.optJSONArray("user_names");
                                    for (int i = 0; i < userNames.length(); i++) {
                                        fellowRiders.add(userNames.getJSONObject(i).optString("user_name"));
                                    }
                                }
                            } catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    } else if (ApiResponseFlags.LAST_RIDE.getOrdinal() == flag) {
                        parseLastRideData(jObject1);
                        return returnResponse;
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
                engagementStatus = -1;
                returnResponse = Constants.SERVER_TIMEOUT;
                return returnResponse;
            }

            if (EngagementStatus.REQUESTED.getOrdinal() == engagementStatus) {
                screenMode = Data.P_ASSIGNING;
            } else if (EngagementStatus.ACCEPTED.getOrdinal() == engagementStatus) {
                screenMode = Data.P_REQUEST_FINAL;
            } else if (EngagementStatus.ARRIVED.getOrdinal() == engagementStatus) {
                screenMode = Data.P_DRIVER_ARRIVED;
            } else if (EngagementStatus.STARTED.getOrdinal() == engagementStatus) {
                screenMode = Data.P_IN_RIDE;
            } else {
                screenMode = "";
            }


            if ("".equalsIgnoreCase(screenMode)) {
                HomeActivity.passengerScreenMode = PassengerScreenMode.P_INITIAL;
                clearSPData(context);
            } else if (Data.P_ASSIGNING.equalsIgnoreCase(screenMode)) {
                HomeActivity.passengerScreenMode = PassengerScreenMode.P_ASSIGNING;
                Data.autoData.setcSessionId(sessionId);
                clearSPData(context);
            } else {

                Data.autoData.setcSessionId(sessionId);
                Data.autoData.setcEngagementId(engagementId);
                Data.autoData.setcDriverId(userId);

                Data.autoData.setPickupLatLng(new LatLng(Double.parseDouble(pickupLatitude), Double.parseDouble(pickupLongitude)));
                if((Utils.compareDouble(dropLatitude, 0) == 0) && (Utils.compareDouble(dropLongitude, 0) == 0)){
                    Data.autoData.setDropLatLng(null);
                } else{
                    Data.autoData.setDropLatLng(new LatLng(dropLatitude, dropLongitude));
                }

                double dLatitude = Double.parseDouble(latitude);
                double dLongitude = Double.parseDouble(longitude);




                Data.autoData.setAssignedDriverInfo(new DriverInfo(userId, dLatitude, dLongitude, driverName,
                        driverImage, driverCarImage, driverPhone, driverRating, driverCarNumber, freeRide, promoName, eta,
                        fareFixed, preferredPaymentMode, scheduleT20, vehicleType, iconSet, cancelRideThrashHoldTime, cancellationCharges,
                        isPooledRide, poolStatusString, fellowRiders, bearing));

                Data.autoData.setFareFactor(fareFactor);

                Log.e("Data.autoData.getAssignedDriverInfo() on login", "=" + Data.autoData.getAssignedDriverInfo().latLng);


                if (Data.P_REQUEST_FINAL.equalsIgnoreCase(screenMode)) {
                    HomeActivity.passengerScreenMode = PassengerScreenMode.P_REQUEST_FINAL;
                }
                else if (Data.P_DRIVER_ARRIVED.equalsIgnoreCase(screenMode)) {
                    HomeActivity.passengerScreenMode = PassengerScreenMode.P_DRIVER_ARRIVED;
                }
                else if (Data.P_IN_RIDE.equalsIgnoreCase(screenMode)) {
                    HomeActivity.passengerScreenMode = PassengerScreenMode.P_IN_RIDE;
                }
            }
        }

        return returnResponse;
    }


    public static String getPromoName(JSONObject jObject) {
        String promoName = Data.NO_PROMO_APPLIED;
        try {
            String coupon = "", promotion = "";
            try {
                if (jObject.has("coupon")) {
                    coupon = jObject.getString("coupon");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (jObject.has("promotion")) {
                    promotion = jObject.getString("promotion");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!"".equalsIgnoreCase(coupon)) {
                promoName = coupon;
            } else if (!"".equalsIgnoreCase(promotion)) {
                promoName = promotion;
            } else {
                promoName = Data.NO_PROMO_APPLIED;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return promoName;
    }


    public void clearSPData(final Context context) {
        SharedPreferences pref = context.getSharedPreferences(Data.SHARED_PREF_NAME, 0);
        Editor editor = pref.edit();

        editor.putString(Data.SP_TOTAL_DISTANCE, "-1");
        editor.putString(Data.SP_WAIT_TIME, "0");
        editor.putString(Data.SP_RIDE_TIME, "0");
        editor.putString(Data.SP_RIDE_START_TIME, "" + System.currentTimeMillis());
        editor.putString(Data.SP_LAST_LATITUDE, "0");
        editor.putString(Data.SP_LAST_LONGITUDE, "0");

        editor.commit();

        Database.getInstance(context).deleteSavedPath();
        Database.getInstance(context).close();
    }


    public void parseDriversToShow(List<Driver> drivers) {
        try {
            Data.autoData.getDriverInfos().clear();
            if(drivers != null) {
                for (Driver driver : drivers) {
                    String userId = String.valueOf(driver.getUserId());
                    double latitude = driver.getLatitude();
                    double longitude = driver.getLongitude();
                    String userName = driver.getUserName();
                    String phoneNo = driver.getPhoneNo();
                    String rating = String.valueOf(driver.getRating());
                    String userImage = "";
                    String driverCarImage = "";
                    String carNumber = "";
                    double bearing = driver.getBearing() == null ? 0 : driver.getBearing();
                    int vehicleType = driver.getVehicleType() == null ? VEHICLE_AUTO : driver.getVehicleType();
                    Data.autoData.getDriverInfos().add(new DriverInfo(userId, latitude, longitude, userName, userImage, driverCarImage,
                            phoneNo, rating, carNumber, 0, bearing, vehicleType, (ArrayList<Integer>)driver.getRegionIds()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void parseCancellationReasons(LoginResponse.Autos autos) {
        try {
            ArrayList<CancelOption> options = new ArrayList<CancelOption>();
            options.add(new CancelOption("Driver is late"));
            options.add(new CancelOption("Driver denied duty"));
            options.add(new CancelOption("Changed my mind"));
            options.add(new CancelOption("Booked another auto"));

            Data.autoData.setCancelOptionsList(new CancelOptionsList(options, "Cancellation of a ride more than 5 minutes after the driver is allocated " +
                    "will lead to cancellation charges of Rs. 20", ""));

            LoginResponse.Cancellation jCancellation = autos.getCancellation();
            String message = jCancellation.getMessage();
            String additionalReason = jCancellation.getAddnReason();
            options.clear();
            for (String reason : jCancellation.getReasons()) {
                options.add(new CancelOption(reason));
            }

            Data.autoData.setCancelOptionsList(new CancelOptionsList(options, message, additionalReason));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void parseFeedbackReasonArrayList(LoginResponse.Autos autos){
        if(Data.autoData.getFeedbackReasons() == null) {
            Data.autoData.setFeedbackReasons(new ArrayList<FeedbackReason>());
        }
        try{
            Data.autoData.getFeedbackReasons().clear();
            for(String resason : autos.getBadRatingReasons()){
                Data.autoData.getFeedbackReasons().add(new FeedbackReason(resason));
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }


    public static ArrayList<PreviousAccountInfo> parsePreviousAccounts(JSONObject jsonObject) {
        ArrayList<PreviousAccountInfo> previousAccountInfoList = new ArrayList<PreviousAccountInfo>();
        try {

            JSONArray jPreviousAccountsArr = jsonObject.getJSONArray("users");
            for (int i = 0; i < jPreviousAccountsArr.length(); i++) {
                JSONObject jPreviousAccount = jPreviousAccountsArr.getJSONObject(i);
                previousAccountInfoList.add(new PreviousAccountInfo(jPreviousAccount.getInt("user_id"),
                        jPreviousAccount.getString("user_name"),
                        jPreviousAccount.getString("user_email"),
                        jPreviousAccount.getString("phone_no"),
                        jPreviousAccount.getString("date_registered")));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


        return previousAccountInfoList;
    }




    public static ArrayList<CouponInfo> parseCouponsArray(JSONObject jObj){
        ArrayList<CouponInfo> couponInfoList = new ArrayList<CouponInfo>();
        try{
            if (jObj.has("coupons")) {
                JSONArray couponsData = jObj.getJSONArray("coupons");
                if (couponsData.length() > 0) {
                    for (int i = 0; i < couponsData.length(); i++) {
                        JSONObject coData = couponsData.getJSONObject(i);

                        CouponInfo couponInfo = new CouponInfo(coData.getInt("account_id"),
                            coData.getString("title"),
                            coData.getString("subtitle"),
                            coData.getString("description"),
                            coData.getString("expiry_date")
                            );

                        couponInfoList.add(couponInfo);
                    }
                    Collections.sort(couponInfoList, new DateComparatorCoupon());
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return couponInfoList;
    }


    public static ArrayList<PromotionInfo> parsePromotionsArray(JSONObject jObj){
        ArrayList<PromotionInfo> promotionInfoList = new ArrayList<PromotionInfo>();

        try{
            if (jObj.has("promotions")) {
                JSONArray promotionsData = jObj.getJSONArray("promotions");
                if (promotionsData.length() > 0) {
                    for (int i = 0; i < promotionsData.length(); i++) {
                        JSONObject poData = promotionsData.getJSONObject(i);

                        PromotionInfo promotionInfo = new PromotionInfo(poData.getInt("promo_id"), poData.getString("title"),
                            poData.getString("terms_n_conds"), poData.getString("end_on"));

                        promotionInfoList.add(promotionInfo);
                    }
//                    Collections.sort(promotionInfoList, new DateComparatorPromotion());
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return promotionInfoList;
    }


    public static ArrayList<EmergencyContact> parseEmergencyContacts(JSONObject jObj){
        ArrayList<EmergencyContact> emergencyContactsList = new ArrayList<>();
        try{
            JSONArray jEmergencyContactsArr = jObj.getJSONArray(KEY_EMERGENCY_CONTACTS);

            for(int i=0; i<jEmergencyContactsArr.length(); i++){
                JSONObject jECont = jEmergencyContactsArr.getJSONObject(i);
                emergencyContactsList.add(new EmergencyContact(jECont.getInt(KEY_ID),
                        jECont.getString(KEY_NAME),
                        jECont.getString(KEY_PHONE_NO)));
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return emergencyContactsList;
    }







    public static Schedule parseT20Schedule(JSONObject jObj){
        Schedule schedule = null;
        try {
            if(jObj.has(KEY_T20_SCHEDULE)){
				JSONObject jSchedule = jObj.getJSONObject(KEY_T20_SCHEDULE);

                JSONObject jTeam1 = jSchedule.getJSONObject(KEY_TEAM_1);
                Team team1 = new Team(jTeam1.getInt(KEY_TEAM_ID),
                        jTeam1.getString(KEY_TEAM_NAME),
                        jTeam1.getString(KEY_TEAM_SHORT_NAME),
                        jTeam1.getString(KEY_TEAM_FLAG_IMAGE_URL));

                JSONObject jTeam2 = jSchedule.getJSONObject(KEY_TEAM_2);
                Team team2 = new Team(jTeam2.getInt(KEY_TEAM_ID),
                        jTeam2.getString(KEY_TEAM_NAME),
                        jTeam2.getString(KEY_TEAM_SHORT_NAME),
                        jTeam2.getString(KEY_TEAM_FLAG_IMAGE_URL));

                schedule = new Schedule(jSchedule.getInt(KEY_SCHEDULE_ID), team1, team2,
                        jSchedule.getString(KEY_MATCH_TIME));
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
        return schedule;
    }


    public static PaytmRechargeInfo parsePaytmRechargeInfo(JSONObject jObj){
        PaytmRechargeInfo paytmRechargeInfo = null;
        try {
            JSONObject jPRI;
            if(jObj.has(KEY_PAYTM_TRANSFER_DATA)) {
                jPRI = jObj.getJSONObject(KEY_PAYTM_TRANSFER_DATA);
            } else{
                jPRI = jObj;
            }
            paytmRechargeInfo = new PaytmRechargeInfo(jPRI.getString(KEY_TRANSFER_ID),
                    jPRI.getString(KEY_TRANSFER_PHONE),
                    jPRI.getString(KEY_TRANSFER_AMOUNT),
                    jPRI.getString(KEY_TRANSFER_SENDER_NAME));
        } catch (Exception e) {
            paytmRechargeInfo = null;
        }
        return paytmRechargeInfo;
    }

    public static void parseDropLatLng(JSONObject jObject1){
        try {
            if (jObject1.has(KEY_OP_DROP_LATITUDE) && jObject1.has(KEY_OP_DROP_LONGITUDE)) {
                double dropLatitude = jObject1.getDouble(KEY_OP_DROP_LATITUDE);
                double dropLongitude = jObject1.getDouble(KEY_OP_DROP_LONGITUDE);
                if((Utils.compareDouble(dropLatitude, 0) == 0) && (Utils.compareDouble(dropLongitude, 0) == 0)){
                    Data.autoData.setDropLatLng(null);
                } else{
                    Data.autoData.setDropLatLng(new LatLng(dropLatitude, dropLongitude));
                }
			} else{
                Data.autoData.setDropLatLng(null);
			}
        } catch (Exception e) {
            e.printStackTrace();
            Data.autoData.setDropLatLng(null);
        }
    }

    private void couponsEvent(Context context){
        try{
            JSONObject map = new JSONObject();
            for(PromoCoupon promoCoupon : Data.userData.getPromoCoupons()){
                map.put(promoCoupon.getTitle(), 1);
            }
            NudgeClient.trackEventUserId(context, FlurryEventNames.NUDGE_COUPON_AVAILABLE, map);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public static RateAppDialogContent parseRateAppDialogContent(JSONObject jObj){
        try{
            JSONObject jRA = jObj.getJSONObject(KEY_RATE_APP_DIALOG_CONTENT);
            return new RateAppDialogContent(jRA.getString(KEY_TITLE),
                    jRA.getString(KEY_TEXT),
                    jRA.getString(KEY_CONFIRM_BUTTON_TEXT),
                    jRA.getString(KEY_CANCEL_BUTTON_TEXT),
                    jRA.getString(KEY_URL));
        } catch(Exception e){
            e.printStackTrace();
            return new RateAppDialogContent("Rate Us",
                    "Liked our services!!! Please rate us on Play Store",
                    "RATE NOW",
                    "LATER",
                    "https://play.google.com/store/apps/details?id=product.clicklabs.jugnoo") ;
        }
    }


    public void performUserAppMonitoring(Context context, JSONObject jObj){
        try {
            int userAppMonitoring = jObj.optInt(KEY_USER_APP_MONITORING, 0);
            if(userAppMonitoring == 1){
				double serverTimeInDays = jObj.optDouble(KEY_USER_APP_MONITORING_DURATION, 1.0);
				long serverTimeInMillis = (long)(serverTimeInDays * (double)(24 * 60 * 60 * 1000));
				long currentTime = System.currentTimeMillis();
				long savedTime = Prefs.with(context).getLong(SPLabels.APP_MONITORING_TRIGGER_TIME, currentTime);

				if(savedTime <= currentTime){
					Intent intent = new Intent(context, FetchAppDataService.class);
					intent.putExtra(KEY_ACCESS_TOKEN, Data.userData.accessToken);
					intent.putExtra(KEY_APP_MONITORING_TIME_TO_SAVE, (currentTime + serverTimeInMillis));
					context.startService(intent);
				}
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loginAnalyticEvents(Context context, LoginVia loginVia){
        try {
            FlurryEventLogger.setGAUserId(Data.userData.getUserId());
            NudgeClient.initialize(context, Data.userData.getUserId(), Data.userData.userName,
                    Data.userData.userEmail, Data.userData.phoneNo,
                    Data.userData.getCity(), Data.userData.getCityReg(), Data.userData.referralCode);
            if(loginVia == LoginVia.EMAIL_OTP
                    || loginVia == LoginVia.FACEBOOK_OTP
                    || loginVia == LoginVia.GOOGLE_OTP) {
                MyApplication.getInstance().getkTracker().event(Constants.KOCHAVA_REG_KEY, ""+loginVia);
                couponsEvent(context);
                String referralCodeEntered = Prefs.with(context).getString(SP_REFERRAL_CODE, "");
                Prefs.with(context).save(SP_REFERRAL_CODE, "");
                nudgeSignupVerifiedEvent(context, Data.userData.getUserId(), Data.userData.phoneNo,
                        Data.userData.userEmail, Data.userData.userName, Data.userData.referralCode, referralCodeEntered);
                BranchMetricsUtils.logEvent(context, FlurryEventNames.BRANCH_EVENT_REGISTRATION, false);
                FbEvents.logEvent(context, FlurryEventNames.FB_EVENT_REGISTRATION);
                FbEvents.logEvent(context, AppEventsConstants.EVENT_NAME_COMPLETED_REGISTRATION);
            }
            JSONObject map = new JSONObject();
            map.put(KEY_SOURCE, getAppSource(context));
            NudgeClient.trackEventUserId(context, FlurryEventNames.NUDGE_LOGIN_APP_SOURCE, map);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

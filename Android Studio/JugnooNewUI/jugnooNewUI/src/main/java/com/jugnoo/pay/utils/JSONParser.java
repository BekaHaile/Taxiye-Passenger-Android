package com.jugnoo.pay.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.jugnoo.pay.R;
import com.jugnoo.pay.config.Config;
import com.jugnoo.pay.datastructure.EmergencyContact;
import com.jugnoo.pay.datastructure.LoginVia;
import com.jugnoo.pay.datastructure.PreviousAccountInfo;
import com.jugnoo.pay.datastructure.ReferralMessages;
import com.jugnoo.pay.datastructure.SPLabels;
import com.jugnoo.pay.datastructure.UserData;
import com.jugnoo.pay.models.LoginResponse;
import com.jugnoo.pay.models.MenuInfo;
import com.jugnoo.pay.models.PromoCoupon;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

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
        int fabButtonEnable = userData.optInt("fab_button_enabled", 0);
        Prefs.with(context).save(SPLabels.SHOW_FAB_SETTING, fabButtonEnable);
        int integratedJugnooEnabled = userData.optInt(KEY_INTEGRATED_JUGNOO_ENABLED, 0);


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
        String gamePredictUrl = userData.optString(KEY_GAME_PREDICT_URL, "");
        String gamePredictIconUrl = "", gamePredictName = "", gamePredictNew = "";



        String fatafatUrlLink = userData.optString("fatafat_url_link", "");


        int notificationPreferenceEnabled = userData.optInt(KEY_NOTIFICATION_PREFERENCE_ENABLED, 0);


        if(Prefs.with(context).getInt(SP_FIRST_LOGIN_COMPLETE, 0) == 0){
            long appOpenTime = Prefs.with(context).getLong(SP_FIRST_OPEN_TIME, System.currentTimeMillis());
            long diff = System.currentTimeMillis() - appOpenTime;
            long diffSeconds = diff / 1000;
            HashMap<String, String> map = new HashMap<>();
            map.put(KEY_TIME_DIFF_SEC, String.valueOf(diffSeconds));
            Prefs.with(context).save(SP_FIRST_LOGIN_COMPLETE, 1);
        }

        String city = userData.optString(KEY_CITY, "");
        String cityReg = userData.optString(KEY_CITY_REG, "");

        int referralLeaderboardEnabled = userData.optInt(KEY_REFERRAL_LEADERBOARD_ENABLED, 1);
        int referralActivityEnabled = userData.optInt(KEY_REFERRAL_ACTIVITY_ENABLED, 1);

        int paytmEnabled = userData.optInt(KEY_PAYTM_ENABLED, 0);
        int mobikwikEnabled = userData.optInt(KEY_MOBIKWIK_ENABLED, 0);
        int freeChargeEnabled = userData.optInt(KEY_FREECHARGE_ENABLED, 0);

        int mealsEnabled = userData.optInt(KEY_MEALS_ENABLED, 0);
        int freshEnabled = userData.optInt(KEY_FRESH_ENABLED, 0);
        int deliveryEnabled = userData.optInt(KEY_DELIVERY_ENABLED, 0);
        int groceryEnabled = userData.optInt(KEY_GROCERY_ENABLED, 0);
        String defaultClientId = userData.optString(KEY_DEFAULT_CLIENT_ID, Config.getAutosClientId());

        int inviteFriendButton = userData.optInt(KEY_INVITE_FRIEND_BUTTON, 0);
        int topupCardEnabled = userData.optInt(KEY_TOPUP_CARD_ENABLED, 0);

        // added on 24-11-2016
        String faqLink = userData.optString("faq_link", "");
        //--------------

        Data.userData = new UserData(userIdentifier, accessToken, authKey, userName, userEmail, emailVerificationStatus,
                userImage, referralCode, phoneNo, jugnooBalance,
                jugnooFbBanner,
                promoSuccess, promoMessage, showJugnooJeanie,
                branchDesktopUrl, branchAndroidUrl, branchIosUrl, branchFallbackUrl,
                jugnooCashTNC, inAppSupportPanelVersion, getGogu, userId, inviteEarnScreenImage,
                t20WCEnable, t20WCScheduleVersion, t20WCInfoText, publicAccessToken,
                gamePredictEnable, gamePredictUrl, gamePredictIconUrl, gamePredictName, gamePredictNew,
                cToDReferralEnabled,
                city, cityReg, referralLeaderboardEnabled, referralActivityEnabled,
                fatafatUrlLink, paytmEnabled, mobikwikEnabled, freeChargeEnabled, notificationPreferenceEnabled,
                mealsEnabled, freshEnabled, deliveryEnabled, groceryEnabled, inviteFriendButton, defaultClientId, integratedJugnooEnabled,
                topupCardEnabled, faqLink);


        Data.userData.updateWalletBalances(userData.optJSONObject(KEY_WALLET_BALANCE), true);

        Data.userData.setJeanieIntroDialogContent(loginUserData.getJeanieIntroDialogContent());


        try {
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

//            if(Prefs.with(context).getString(Constants.KEY_SP_PUSH_OPENED_CLIENT_ID, "").equals("")) {
                Prefs.with(context).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, defaultClientId);
//            } else {
//                Prefs.with(context).save(Constants.KEY_SP_PUSH_OPENED_CLIENT_ID, "");
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    public void parseAccessTokenLoginData(Context context, String response, LoginResponse loginResponse,
                                            LoginVia loginVia) throws Exception {

        JSONObject jObj = new JSONObject(response);

        //Fetching login data
        JSONObject jUserDataObject = jObj.getJSONObject(KEY_USER_DATA);
        JSONObject jAutosObject = jObj.optJSONObject(KEY_AUTOS);
        JSONObject jFreshObject = jObj.optJSONObject(KEY_FRESH);
        JSONObject jMealsObject = jObj.optJSONObject(KEY_MEALS);
        JSONObject jGroceryObject = jObj.optJSONObject(KEY_GROCERY);

        parseUserData(context, jUserDataObject, loginResponse.getUserData());



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
        String title = Constants.FB_LINK_SHARE_NAME;

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
            if(userData.getSharingOgTitle() != null){
                title = userData.getSharingOgTitle();
            }
		} catch (Exception e) {
            e.printStackTrace();
        }

        ReferralMessages referralMessages = new ReferralMessages(referralMessage, referralSharingMessage, fbShareCaption, fbShareDescription, referralCaption, referralCaptionEnabled,
            referralEmailSubject, referralPopupText, referralShortMessage, referralMoreInfoMessage, title);

        return referralMessages;
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


}

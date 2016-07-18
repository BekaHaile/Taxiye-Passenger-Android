package product.clicklabs.jugnoo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.facebook.appevents.AppEventsConstants;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import product.clicklabs.jugnoo.apis.ApiFindADriver;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.CancelOption;
import product.clicklabs.jugnoo.datastructure.CancelOptionsList;
import product.clicklabs.jugnoo.datastructure.CouponInfo;
import product.clicklabs.jugnoo.datastructure.DiscountType;
import product.clicklabs.jugnoo.datastructure.DriverInfo;
import product.clicklabs.jugnoo.datastructure.EmergencyContact;
import product.clicklabs.jugnoo.datastructure.EndRideData;
import product.clicklabs.jugnoo.datastructure.EngagementStatus;
import product.clicklabs.jugnoo.datastructure.FeedbackReason;
import product.clicklabs.jugnoo.datastructure.LoginVia;
import product.clicklabs.jugnoo.datastructure.PassengerScreenMode;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.datastructure.PaytmRechargeInfo;
import product.clicklabs.jugnoo.datastructure.PreviousAccountInfo;
import product.clicklabs.jugnoo.datastructure.PriorityTipCategory;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.datastructure.PromotionInfo;
import product.clicklabs.jugnoo.datastructure.ReferralMessages;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.datastructure.UserData;
import product.clicklabs.jugnoo.datastructure.UserMode;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.home.models.Region;
import product.clicklabs.jugnoo.home.models.RideEndGoodFeedbackViewType;
import product.clicklabs.jugnoo.home.models.VehicleIconSet;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.Coupon;
import product.clicklabs.jugnoo.retrofit.model.Driver;
import product.clicklabs.jugnoo.retrofit.model.FareStructure;
import product.clicklabs.jugnoo.retrofit.model.FindADriverResponse;
import product.clicklabs.jugnoo.retrofit.model.LoginResponse;
import product.clicklabs.jugnoo.retrofit.model.Promotion;
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


    public UserData parseUserData(Context context, JSONObject userData) throws Exception {

        double fareFactor = 1.0;

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

		Data.knowlarityMissedCallNumber = userData.optString("knowlarity_missed_call_number", "");
        Data.otpViaCallEnabled = userData.optInt(KEY_OTP_VIA_CALL_ENABLED, 1);
		int promoSuccess = userData.optInt(KEY_PROMO_SUCCESS, 1);
        String promoMessage = userData.optString(KEY_PROMO_MESSAGE,
                context.getResources().getString(R.string.promocode_invalid_message_on_signup));


        int contactSaved = userData.optInt("refer_all_status"); // if 0 show popup, else not show
        String referAllText = userData.optString("refer_all_text", context.getResources().getString(R.string.upload_contact_message));
		String referAllTitle = userData.optString("refer_all_title", context.getResources().getString(R.string.upload_contact_title));

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

        Prefs.with(context).save(KEY_SP_CUSTOMER_LOCATION_UPDATE_INTERVAL, userData.optLong(KEY_SP_CUSTOMER_LOCATION_UPDATE_INTERVAL,
                LOCATION_UPDATE_INTERVAL));


        int gamePredictEnable = userData.optInt(KEY_GAME_PREDICT_ENABLE, 0);
        String gamePredictUrl = userData.optString(KEY_GAME_PREDICT_URL, "https://jugnoo.in/wct20");
        String gamePredictIconUrl = "", gamePredictName = "", gamePredictNew = "";
        String destinationHelpText = userData.optString("destination_help_text", "");
        String rideSummaryBadText = userData.optString("ride_summary_text", context.getResources().getString(R.string.ride_summary_bad_text));
        String cancellationChargesPopupTextLine1 = userData.optString("cancellation_charges_popup_text_line1", "");
        String cancellationChargesPopupTextLine2 = userData.optString("cancellation_charges_popup_text_line2", "");
        String inRideSendInviteTextBold = userData.optString("in_ride_send_invite_text_bold", context.getResources().getString(R.string.send_invites));
        String inRideSendInviteTextNormal = userData.optString("in_ride_send_invite_text_normal", context.getResources().getString(R.string.send_invites_2));
        String fatafatUrlLink = userData.optString("fatafat_url_link", "");
        String confirmScreenFareEstimateEnable = userData.optString("confirm_screen_fare_estimate_enabled", "0");
        String poolDestinationPopupText1 = userData.optString("pool_destination_popup_text1", context.getResources().getString(R.string.pool_rides_offer_guaranteed_fares));
        String poolDestinationPopupText2 = userData.optString("pool_destination_popup_text2", context.getResources().getString(R.string.please_provide_pickup_and_dest));
        String poolDestinationPopupText3 = userData.optString("pool_destination_popup_text3", context.getResources().getString(R.string.you_will_not_change_dest));
        int inviteFriendButton = userData.optInt("invite_friend_button", 0);
        int rideEndGoodFeedbackViewType = userData.optInt("ride_end_good_feedback_view_type", RideEndGoodFeedbackViewType.RIDE_END_IMAGE_1.getOrdinal());
        String rideEndGoodFeedbackText = userData.optString("ride_end_good_feedback_text", context.getResources().getString(R.string.end_ride_with_image_text));

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

        int referAllStatusLogin = userData.optInt(KEY_REFER_ALL_STATUS_LOGIN, 1);
        String referAllTextLogin = userData.optString(KEY_REFER_ALL_TEXT_LOGIN, "");
        String referAllTitleLogin = userData.optString(KEY_REFER_ALL_TITLE_LOGIN, "");

        String city = userData.optString(KEY_CITY, "");
        String cityReg = userData.optString(KEY_CITY_REG, "");

        int referralLeaderboardEnabled = userData.optInt(KEY_REFERRAL_LEADERBOARD_ENABLED, 1);
        int referralActivityEnabled = userData.optInt(KEY_REFERRAL_ACTIVITY_ENABLED, 1);


        int paytmEnabled = userData.optInt(KEY_PAYTM_ENABLED, 0);
        int mobikwikEnabled = userData.optInt(KEY_MOBIKWIK_ENABLED, 0);

        MyApplication.getInstance().getWalletCore().parsePaymentModeConfigDatas(userData);

        UserData userDataObj = new UserData(userIdentifier, accessToken, authKey, userName, userEmail, emailVerificationStatus,
                userImage, referralCode, phoneNo, jugnooBalance, fareFactor,
                jugnooFbBanner, numCouponsAvailable,
                contactSaved, referAllText, referAllTitle,
                promoSuccess, promoMessage, showJugnooJeanie,
                branchDesktopUrl, branchAndroidUrl, branchIosUrl, branchFallbackUrl,
                jugnooCashTNC, inAppSupportPanelVersion, getGogu, userId, inviteEarnScreenImage,
                t20WCEnable, t20WCScheduleVersion, t20WCInfoText, publicAccessToken,
                gamePredictEnable, gamePredictUrl, gamePredictIconUrl, gamePredictName, gamePredictNew,
                referAllStatusLogin, referAllTextLogin, referAllTitleLogin, cToDReferralEnabled,
                city, cityReg, referralLeaderboardEnabled, referralActivityEnabled, destinationHelpText,
                cancellationChargesPopupTextLine1, cancellationChargesPopupTextLine2, rideSummaryBadText,
                inRideSendInviteTextBold, inRideSendInviteTextNormal, fatafatUrlLink, confirmScreenFareEstimateEnable,
                poolDestinationPopupText1, poolDestinationPopupText2, poolDestinationPopupText3,
                inviteFriendButton, rideEndGoodFeedbackViewType, rideEndGoodFeedbackText,
                paytmEnabled, mobikwikEnabled);

        userDataObj.updateWalletBalances(userData.optJSONObject(KEY_WALLET_BALANCE), true);

        return userDataObj;
    }


    public String parseAccessTokenLoginData(Context context, String response, LoginResponse loginResponse,
                                            LoginVia loginVia) throws Exception {

        JSONObject jObj = new JSONObject(response);

        //Fetching login data
        JSONObject jLoginObject = jObj.getJSONObject("login");

        Data.userData = parseUserData(context, jLoginObject);

        MyApplication.getInstance().getWalletCore().setDefaultPaymentOption();

        //emergency contacts
        if(Data.emergencyContactsList == null){
            Data.emergencyContactsList = new ArrayList<>();
        }
        Data.emergencyContactsList.clear();
        Data.emergencyContactsList.addAll(JSONParser.parseEmergencyContacts(jLoginObject));

        parseFindDriverResp(loginResponse);
        parsePromoCoupons(loginResponse);
        Data.menuInfoList = new ArrayList<>();
        Data.menuInfoList.addAll(loginResponse.getLogin().getMenuInfoList());

		if(loginResponse.getLogin().getSupportNumber() != null){
			Config.saveSupportNumber(context, loginResponse.getLogin().getSupportNumber());
		}

        //Fetching user current status
        JSONObject jUserStatusObject = jObj.getJSONObject(KEY_STATUS);
        String resp = parseCurrentUserStatus(context, loginResponse.getLogin().getCurrentUserStatus(), jUserStatusObject);

        parseCancellationReasons(loginResponse);
        parseFeedbackReasonArrayList(loginResponse);

        Data.referralMessages = parseReferralMessages(loginResponse);

        int userAppMonitoring = jLoginObject.optInt(KEY_USER_APP_MONITORING, 0);
        if(userAppMonitoring == 1){
			double serverTimeInDays = jLoginObject.optDouble(KEY_USER_APP_MONITORING_DURATION, 1.0);
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

        try {
            FlurryEventLogger.setGAUserId(Data.userData.getUserId());
            NudgeClient.initialize(context, Data.userData.getUserId(), Data.userData.userName,
                    Data.userData.userEmail, Data.userData.phoneNo,
                    Data.userData.getCity(), Data.userData.getCityReg(), Data.userData.referralCode);
            if(loginVia == LoginVia.EMAIL_OTP
                    || loginVia == LoginVia.FACEBOOK_OTP
                    || loginVia == LoginVia.GOOGLE_OTP) {
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


    private void parseFindDriverResp(LoginResponse loginResponse){
        try {
            //current_user_status = 1 driver or 2 user
            parseDriversToShow(loginResponse.getDrivers());

            Data.priorityTipCategory = PriorityTipCategory.NO_PRIORITY_DIALOG.getOrdinal();
            if (loginResponse.getLogin().getPriorityTipCategory() != null) {
				Data.priorityTipCategory = loginResponse.getLogin().getPriorityTipCategory();
			}
            if(loginResponse.getLogin().getFareFactor() != null) {
                Data.userData.fareFactor = loginResponse.getLogin().getFareFactor();
            }
            if(loginResponse.getLogin().getDriverFareFactor() != null) {
                Data.userData.setDriverFareFactor(loginResponse.getLogin().getDriverFareFactor());
            } else{
                Data.userData.setDriverFareFactor(1);
            }
            if (loginResponse.getLogin().getFarAwayCity() == null) {
				Data.farAwayCity = "";
			} else {
				Data.farAwayCity = loginResponse.getLogin().getFarAwayCity();
			}

            if (loginResponse.getLogin().getFreshAvailable() == null) {
                Data.freshAvailable = 0;
            } else {
                Data.freshAvailable = loginResponse.getLogin().getFreshAvailable();
            }
            Data.campaigns = loginResponse.getLogin().getCampaigns();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parsePromoCoupons(LoginResponse loginResponse){
        try{
            if(Data.regions == null){
                Data.regions = new ArrayList<>();
            } else{
                Data.regions.clear();
            }
            if(loginResponse.getLogin().getRegions() != null) {
                HomeUtil homeUtil = new HomeUtil();
                for (Region region : loginResponse.getLogin().getRegions()) {
                    region.setVehicleIconSet(homeUtil.getVehicleIconSet(region.getIconSet()));
                    Data.regions.add(region);
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        try{
            if(Data.promoCoupons == null){
                Data.promoCoupons = new ArrayList<>();
            } else{
                Data.promoCoupons.clear();
            }
            if(loginResponse.getLogin().getCoupons() != null) {
                for (Coupon coupon : loginResponse.getLogin().getCoupons()) {
                    Data.promoCoupons.add(new CouponInfo(coupon.getAccountId(),
                            coupon.getCouponType(),
                            coupon.getStatus(),
                            coupon.getTitle(),
                            coupon.getSubtitle(),
                            coupon.getDescription(),
                            coupon.getImage(),
                            coupon.getRedeemedOn(),
                            coupon.getExpiryDate(), "", ""));
                }
            }
            if(loginResponse.getLogin().getPromotions() != null) {
                for (Promotion promotion : loginResponse.getLogin().getPromotions()) {
                    Data.promoCoupons.add(new PromotionInfo(promotion.getPromoId(),
                            promotion.getTitle(),
                            promotion.getTermsNConds()));
                }
            }

            if(loginResponse.getLogin().getFareStructure() != null) {
                Data.fareStructure = null;
                for (FareStructure fareStructure : loginResponse.getLogin().getFareStructure()) {
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
                                fareStructure.getFareThresholdWaitingTime(), convenienceCharges, true);
                        for (int i = 0; i < Data.regions.size(); i++) {
                            try {
                                if (Data.regions.get(i).getVehicleType().equals(fareStructure.getVehicleType())
                                        && Data.regions.get(i).getRideType().equals(fareStructure.getRideType())
                                        ) {
                                    Data.regions.get(i).setFareStructure(fareStructure1);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (Data.fareStructure == null) {
                            Data.fareStructure = fareStructure1;
                        }
                    }
                }
                if(Data.fareStructure == null){
                    Data.fareStructure = getDefaultFareStructure();
                }
            }else{
                Data.fareStructure = getDefaultFareStructure();
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private product.clicklabs.jugnoo.datastructure.FareStructure getDefaultFareStructure(){
        return new product.clicklabs.jugnoo.datastructure.FareStructure(10, 0, 3, 1, 0, 0, 0, 0, false);
    }

    public static product.clicklabs.jugnoo.datastructure.FareStructure getFareStructure(){
        if(Data.fareStructure == null) {
            return new product.clicklabs.jugnoo.datastructure.FareStructure(10, 0, 3, 1, 0, 0, 0, 0, false);
        } else{
            return Data.fareStructure;
        }
    }

    public ReferralMessages parseReferralMessages(LoginResponse loginResponse) {
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
            if (loginResponse.getReferralMessage() != null) {
                referralMessage = loginResponse.getReferralMessage();
            }
            if (loginResponse.getReferralSharingMessage() != null) {
                referralSharingMessage = loginResponse.getReferralSharingMessage();
            }
            if (loginResponse.getFbShareCaption() != null) {
                fbShareCaption = loginResponse.getFbShareCaption();
            }
            if (loginResponse.getFbShareDescription() != null) {
                fbShareDescription = loginResponse.getFbShareDescription();
            }
            if (loginResponse.getReferralCaption() != null) {
                referralCaption = loginResponse.getReferralCaption();
                referralCaption = referralCaption.replaceAll("</br>", "<br/>");
            }
            if(loginResponse.getReferralEmailSubject() != null){
                referralEmailSubject = loginResponse.getReferralEmailSubject();
            }
			if (loginResponse.getReferralPopupText() != null) {
				referralPopupText = loginResponse.getReferralPopupText();
			}
            if(loginResponse.getInviteEarnShortMsg() != null){
                referralShortMessage = loginResponse.getInviteEarnShortMsg();
            }
            if(loginResponse.getInviteEarnMoreInfo() != null){
                referralMoreInfoMessage = loginResponse.getInviteEarnMoreInfo();
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
            Data.cSessionId = "";
            Data.cEngagementId = jLastRideData.getString("engagement_id");

            JSONObject jDriverInfo = jLastRideData.getJSONObject("driver_info");
            Data.cDriverId = jDriverInfo.getString("id");

            Data.pickupLatLng = new LatLng(0, 0);
            Data.dropLatLng = null;

            Data.assignedDriverInfo = new DriverInfo(Data.cDriverId, jDriverInfo.getString("name"), jDriverInfo.getString("user_image"),
                    jDriverInfo.getString("driver_car_image"), jDriverInfo.getString("driver_car_no"));

            try {
                if (jLastRideData.has("rate_app")) {
                    Data.customerRateAppFlag = jLastRideData.getInt("rate_app");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

			Data.endRideData = parseEndRideData(jLastRideData, jLastRideData.getString("engagement_id"), Data.fareStructure.fixedFare);

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
                discountTypes.add(new DiscountType(obj.optString("text"), obj.optDouble("amount"), obj.optInt("reference_id")));
                sumAdditionalCharges = sumAdditionalCharges + obj.optDouble("amount");
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
                sumAdditionalCharges, engagementDate, paidUsingMobikwik);
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

                        Data.pickupLatLng = new LatLng(assigningLatitude, assigningLongitude);
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
                Data.cSessionId = sessionId;
                clearSPData(context);
            } else {

                Data.cSessionId = sessionId;
                Data.cEngagementId = engagementId;
                Data.cDriverId = userId;

                Data.pickupLatLng = new LatLng(Double.parseDouble(pickupLatitude), Double.parseDouble(pickupLongitude));
                if((Utils.compareDouble(dropLatitude, 0) == 0) && (Utils.compareDouble(dropLongitude, 0) == 0)){
                    Data.dropLatLng = null;
                } else{
                    Data.dropLatLng = new LatLng(dropLatitude, dropLongitude);
                }

                double dLatitude = Double.parseDouble(latitude);
                double dLongitude = Double.parseDouble(longitude);




                Data.assignedDriverInfo = new DriverInfo(userId, dLatitude, dLongitude, driverName,
                        driverImage, driverCarImage, driverPhone, driverRating, driverCarNumber, freeRide, promoName, eta,
                        fareFixed, preferredPaymentMode, scheduleT20, vehicleType, iconSet, cancelRideThrashHoldTime, cancellationCharges,
                        isPooledRide, poolStatusString, fellowRiders, bearing);

                Data.userData.fareFactor = fareFactor;

                Log.e("Data.assignedDriverInfo on login", "=" + Data.assignedDriverInfo.latLng);


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
            Data.driverInfos.clear();
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
                    Data.driverInfos.add(new DriverInfo(userId, latitude, longitude, userName, userImage, driverCarImage,
                            phoneNo, rating, carNumber, 0, bearing, vehicleType, (ArrayList<Integer>)driver.getRegionIds()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void parseCancellationReasons(LoginResponse loginResponse) {
        try {
            ArrayList<CancelOption> options = new ArrayList<CancelOption>();
            options.add(new CancelOption("Driver is late"));
            options.add(new CancelOption("Driver denied duty"));
            options.add(new CancelOption("Changed my mind"));
            options.add(new CancelOption("Booked another auto"));

            Data.cancelOptionsList = new CancelOptionsList(options, "Cancellation of a ride more than 5 minutes after the driver is allocated " +
                    "will lead to cancellation charges of Rs. 20", "");

            LoginResponse.Cancellation jCancellation = loginResponse.getCancellation();
            String message = jCancellation.getMessage();
            String additionalReason = jCancellation.getAddnReason();
            options.clear();
            for (String reason : jCancellation.getReasons()) {
                options.add(new CancelOption(reason));
            }

            Data.cancelOptionsList = new CancelOptionsList(options, message, additionalReason);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void parseFeedbackReasonArrayList(LoginResponse loginResponse){
        if(Data.feedbackReasons == null) {
            Data.feedbackReasons = new ArrayList<>();
        }
        try{
            Data.feedbackReasons.clear();
            for(String resason : loginResponse.getBadRatingReasons()){
                Data.feedbackReasons.add(new FeedbackReason(resason));
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
                            coData.getInt("coupon_type"),
                            coData.getInt("status"),
                            coData.getString("title"),
                            coData.getString("subtitle"),
                            coData.getString("description"),
                            coData.getString("image"),
                            coData.getString("redeemed_on"),
                            coData.getString("expiry_date"),
                            coData.getString("start_time"),
                            coData.getString("end_time")
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
                            poData.getString("terms_n_conds"), poData.getString("validity_text"), poData.getString("end_on"));

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
            JSONArray jEmergencyContactsArr = jObj.getJSONArray("emergency_contacts");

            for(int i=0; i<jEmergencyContactsArr.length(); i++){
                JSONObject jECont = jEmergencyContactsArr.getJSONObject(i);
                emergencyContactsList.add(new EmergencyContact(jECont.getInt("id"),
                        jECont.getString("name"),
                        jECont.getString("phone_no")));
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
                    Data.dropLatLng = null;
                } else{
                    Data.dropLatLng = new LatLng(dropLatitude, dropLongitude);
                }
			} else{
                Data.dropLatLng = null;
			}
        } catch (Exception e) {
            e.printStackTrace();
            Data.dropLatLng = null;
        }
    }

    private void couponsEvent(Context context){
        try{
            JSONObject map = new JSONObject();
            for(PromoCoupon promoCoupon : Data.promoCoupons){
                map.put(promoCoupon.getTitle(), 1);
            }
            NudgeClient.trackEventUserId(context, FlurryEventNames.NUDGE_COUPON_AVAILABLE, map);
        } catch(Exception e){
            e.printStackTrace();
        }
    }



}

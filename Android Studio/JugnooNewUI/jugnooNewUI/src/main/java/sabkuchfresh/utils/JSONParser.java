package sabkuchfresh.utils;

import android.app.Activity;
import android.content.Context;

import com.sabkuchfresh.R;
import com.sabkuchfresh.TokenGenerator.AccessTokenGenerator;
import com.sabkuchfresh.TokenGenerator.SHA256Convertor;
import com.sabkuchfresh.analytics.FlurryEventNames;
import com.sabkuchfresh.analytics.NudgeClient;
import com.sabkuchfresh.config.Config;
import com.sabkuchfresh.datastructure.ApiResponseFlags;
import com.sabkuchfresh.datastructure.LoginVia;
import com.sabkuchfresh.datastructure.PopupData;
import com.sabkuchfresh.datastructure.PreviousAccountInfo;
import com.sabkuchfresh.datastructure.SPLabels;
import com.sabkuchfresh.datastructure.UserData;
import com.sabkuchfresh.retrofit.model.LoginResponse;
import com.sabkuchfresh.retrofit.model.Store;

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


    public UserData parseUserData(Context context, JSONObject userData) throws Exception {

        double fareFactor = 1.0;

        String shareImage = JsonUtils.readString(userData, "menu_share_icon", false, false);
        String shateName = JsonUtils.readString(userData, "menu_share_text", false, false);

        String referralBanner = JsonUtils.readString(userData, "referral_banner", false, false);
        String referralShortDesc = JsonUtils.readString(userData, "referral_short_desc", false, false);
        String referralDescription = JsonUtils.readString(userData, "referral_description", false, false);;
        String referralShareTitle = JsonUtils.readString(userData, "referral_share_title", false, false);
        String referralShareText = JsonUtils.readString(userData, "referral_share_text", false, false);
        String referralShareImage = JsonUtils.readString(userData, "referral_share_image", false, false);
        String referralShareMessage = userData.optString("referral_share_message", "");

        String orderId = JsonUtils.readString(userData, "feedback_order_id", false, false);
        String question = JsonUtils.readString(userData, "question", false, false);
        int questionType = JsonUtils.readInt(userData, "question_type", false, false);
        int pendingFeedback = JsonUtils.readInt(userData, "pending_feedback", false, false);

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

        Data.isfatafat = userData.optInt("fatafat_enabled", 0);

		int paytmEnabled = userData.optInt("paytm_enabled", 0);
        int contactSaved = userData.optInt("refer_all_status"); // if 0 show popup, else not show
        String referAllText = userData.optString("refer_all_text", context.getResources().getString(R.string.upload_contact_message));
		String referAllTitle = userData.optString("refer_all_title", context.getResources().getString(R.string.upload_contact_title));

        int showJugnooJeanie = userData.optInt("jugnoo_sticky", 0);
        int cToDReferralEnabled = userData.optInt("c2d_referral_enabled");
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

        String mSupportContact = userData.optString("support_contact", "");
        Prefs.with(context).save(context.getResources().getString(R.string.pref_support_contact), mSupportContact);

        String jugnooCashTNC = userData.optString(KEY_JUGNOO_CASH_TNC, context.getResources().getString(R.string.jugnoo_cash_tnc));

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

//        try {
//            String gamePredictViewData = userData.optString(KEY_GAME_PREDICT_VIEW_DATA, "");
//            gamePredictIconUrl = gamePredictViewData.split(VIEW_DATA_SPLITTER)[0];
//            gamePredictName = gamePredictViewData.split(VIEW_DATA_SPLITTER)[1];
//            gamePredictNew = gamePredictViewData.split(VIEW_DATA_SPLITTER)[2];
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


        PopupData popupData = null;
        try {
            if (userData.has("popup_data")) {
                popupData = new PopupData();
                popupData.popup_id = userData.getJSONObject("popup_data").optInt("popup_id", 0);
                popupData.title_text = userData.getJSONObject("popup_data").optString("title_text", "");
                popupData.desc_text = userData.getJSONObject("popup_data").optString("desc_text", "");
                popupData.image_url = userData.getJSONObject("popup_data").optString("image_url", "");
                popupData.cancel_title = userData.getJSONObject("popup_data").optString("cancel_title", "");
                popupData.ok_title = userData.getJSONObject("popup_data").optString("ok_title", "OK");
                popupData.is_cancellable = userData.getJSONObject("popup_data").optInt("is_cancellable", 1);
                popupData.deep_index = userData.getJSONObject("popup_data").optInt("deep_index", 0);
                popupData.ext_url = userData.getJSONObject("popup_data").optString("ext_url", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<Store> stores = new ArrayList<>();
        Store store = null;
        try {
            if(userData.has("stores")) {
                JSONArray storesArr = userData.getJSONArray("stores");
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



        if(Prefs.with(context).getInt(SP_FIRST_LOGIN_COMPLETE, 0) == 0){
            long appOpenTime = Prefs.with(context).getLong(SP_FIRST_OPEN_TIME, System.currentTimeMillis());
            long diff = System.currentTimeMillis() - appOpenTime;
            long diffSeconds = diff / 1000;
            HashMap<String, String> map = new HashMap<>();
            map.put(KEY_TIME_DIFF_SEC, String.valueOf(diffSeconds));
            Prefs.with(context).save(SP_FIRST_LOGIN_COMPLETE, 1);
        }

        int referAllStatusLogin = userData.optInt(KEY_REFER_ALL_STATUS_LOGIN, 1);
        String referAllTextLogin = userData.optString(KEY_REFER_ALL_TEXT_LOGIN, "");
        String referAllTitleLogin = userData.optString(KEY_REFER_ALL_TITLE_LOGIN, "");
        int inviteFriendButton = userData.optInt("invite_friend_button", 0);
        int defaultStoreId = userData.optInt("default_store_id", 0);

        return new UserData(userIdentifier, accessToken, authKey, userName, userEmail, emailVerificationStatus,
                userImage, referralCode, phoneNo, jugnooBalance, fareFactor,
                jugnooFbBanner, numCouponsAvailable, paytmEnabled,
                contactSaved, referAllText, referAllTitle,
                promoSuccess, promoMessage, showJugnooJeanie,
                branchDesktopUrl, branchAndroidUrl, branchIosUrl, branchFallbackUrl,
                jugnooCashTNC, inAppSupportPanelVersion, getGogu, userId, inviteEarnScreenImage,
                t20WCEnable, t20WCScheduleVersion, t20WCInfoText, publicAccessToken,
                gamePredictEnable, gamePredictUrl, gamePredictIconUrl, gamePredictName, gamePredictNew,
                referAllStatusLogin, referAllTextLogin, referAllTitleLogin, cToDReferralEnabled, shareImage, shateName,
                referralBanner, referralShortDesc, referralDescription, referralShareTitle,
                referralShareText, referralShareImage, question, questionType, orderId, pendingFeedback, popupData, stores,
                inviteFriendButton, referralShareMessage, defaultStoreId);

    }


    public String parseAccessTokenLoginData(Context context, String response, LoginResponse loginResponse,
                                            LoginVia loginVia) throws Exception {

        JSONObject jObj = new JSONObject(response);

        //Fetching login data
        JSONObject jLoginObject = jObj.getJSONObject("login");

        Data.userData = parseUserData(context, jLoginObject);

		if(loginResponse.getLogin().getSupportNumber() != null){
			Config.saveSupportNumber(context, loginResponse.getLogin().getSupportNumber());
		}

        try {
            NudgeClient.initialize(context, Data.userData.getUserId(), Data.userData.userName,
                    Data.userData.userEmail, Data.userData.phoneNo);
            if(loginVia == LoginVia.EMAIL_OTP
                    || loginVia == LoginVia.FACEBOOK_OTP
                    || loginVia == LoginVia.GOOGLE_OTP) {
                String referralCodeEntered = Prefs.with(context).getString(SP_REFERRAL_CODE, "");
                Prefs.with(context).save(SP_REFERRAL_CODE, "");
                nudgeSignupVerifiedEvent(context, Data.userData.getUserId(), Data.userData.phoneNo,
                        Data.userData.userEmail, Data.userData.userName, Data.userData.referralCode, referralCodeEntered);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
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






	public static void parsePaytmBalanceStatus(Activity activity, JSONObject jObj){
		try {
			if (Data.userData != null) {
				int flag = jObj.optInt("flag", ApiResponseFlags.ACTION_COMPLETE.getOrdinal());
				if (ApiResponseFlags.PAYTM_BALANCE_ERROR.getOrdinal() == flag) {
					setPaytmErrorCase();
				} else {
					Data.userData.setPaytmError(0);
					String paytmStatus = jObj.optString("STATUS", Data.PAYTM_STATUS_INACTIVE);
					if (paytmStatus.equalsIgnoreCase(Data.PAYTM_STATUS_ACTIVE)) {
						String balance = jObj.optString("WALLETBALANCE", "0");
						Data.userData.setPaytmBalance(Double.parseDouble(balance));
						Data.userData.setPaytmStatus(paytmStatus);
					} else {
						Data.userData.setPaytmStatus(Data.PAYTM_STATUS_INACTIVE);
						Data.userData.setPaytmBalance(0);
					}
					Prefs.with(activity).save(SPLabels.PAYTM_CHECK_BALANCE_LAST_TIME, System.currentTimeMillis());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setPaytmErrorCase(){
		try {
			if (Data.userData != null) {
				Data.userData.setPaytmError(1);
				Data.userData.setPaytmBalance(0);
				Data.userData.setPaytmStatus(Data.PAYTM_STATUS_ACTIVE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}





}

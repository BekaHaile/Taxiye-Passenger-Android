package product.clicklabs.jugnoo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.facebook.appevents.AppEventsConstants;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.retrofit.model.PlaceOrderResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import io.paperdb.Paper;
import product.clicklabs.jugnoo.apis.ApiFindADriver;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.AutoData;
import product.clicklabs.jugnoo.datastructure.BidInfo;
import product.clicklabs.jugnoo.datastructure.CancelOption;
import product.clicklabs.jugnoo.datastructure.CancelOptionsList;
import product.clicklabs.jugnoo.datastructure.DiscountType;
import product.clicklabs.jugnoo.datastructure.DriverInfo;
import product.clicklabs.jugnoo.datastructure.EmergencyContact;
import product.clicklabs.jugnoo.datastructure.EndRideData;
import product.clicklabs.jugnoo.datastructure.EngagementStatus;
import product.clicklabs.jugnoo.datastructure.FeedBackInfo;
import product.clicklabs.jugnoo.datastructure.FeedbackReason;
import product.clicklabs.jugnoo.datastructure.LoginVia;
import product.clicklabs.jugnoo.datastructure.MenuInfoTags;
import product.clicklabs.jugnoo.datastructure.PassengerScreenMode;
import product.clicklabs.jugnoo.datastructure.PayData;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.datastructure.PaytmRechargeInfo;
import product.clicklabs.jugnoo.datastructure.PreviousAccountInfo;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.datastructure.ReferralMessages;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.datastructure.UserData;
import product.clicklabs.jugnoo.datastructure.UserMode;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.home.models.MenuInfo;
import product.clicklabs.jugnoo.home.models.RateAppDialogContent;
import product.clicklabs.jugnoo.home.models.Region;
import product.clicklabs.jugnoo.home.models.RideEndGoodFeedbackViewType;
import product.clicklabs.jugnoo.home.models.RideTypeValue;
import product.clicklabs.jugnoo.home.models.VehicleIconSet;
import product.clicklabs.jugnoo.rentals.models.GpsLockStatus;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.Driver;
import product.clicklabs.jugnoo.retrofit.model.FareStructure;
import product.clicklabs.jugnoo.retrofit.model.FetchUserAddressResponse;
import product.clicklabs.jugnoo.retrofit.model.FindADriverResponse;
import product.clicklabs.jugnoo.retrofit.model.LoginResponse;
import product.clicklabs.jugnoo.retrofit.model.NearbyPickupRegions;
import product.clicklabs.jugnoo.t20.models.Schedule;
import product.clicklabs.jugnoo.t20.models.Team;
import product.clicklabs.jugnoo.utils.BranchMetricsUtils;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.FbEvents;
import product.clicklabs.jugnoo.utils.LocaleHelper;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.SHA256Convertor;
import product.clicklabs.jugnoo.utils.Utils;
import product.clicklabs.jugnoo.wallet.WalletCore;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class JSONParser implements Constants {



    private final String TAG = JSONParser.class.getSimpleName();


    public JSONParser() {

    }

    public static String getServerMessage(JSONObject jObj) {
        String message = MyApplication.getInstance().getString(R.string.connection_lost_please_try_again);
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
        String countryCode = "+" + userData.optString(Constants.KEY_COUNTRY_CODE, "91");
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
                userData.optInt(KEY_OTP_VIA_CALL_ENABLED,
                        Prefs.with(context).getInt(Constants.SP_OTP_VIA_CALL_ENABLED, 0)));
		int promoSuccess = userData.optInt(KEY_PROMO_SUCCESS, 1);
        String promoMessage = userData.optString(KEY_PROMO_MESSAGE,
                context.getResources().getString(R.string.promocode_invalid_message_on_signup));


        int showJugnooJeanie = userData.optInt("jugnoo_sticky", 0);
        int cToDReferralEnabled = userData.optInt("c2d_referral_enabled", 0);
        Prefs.with(context).save(SPLabels.SHOW_JUGNOO_JEANIE, showJugnooJeanie);
        int fabButtonEnable = userData.optInt("fab_button_enabled", 0);
        Prefs.with(context).save(SPLabels.SHOW_FAB_SETTING, fabButtonEnable);
        int integratedJugnooEnabled = userData.optInt(KEY_INTEGRATED_JUGNOO_ENABLED, 0);
        int slideCheckoutPayEnabled = userData.optInt(SLIDE_CHECKOUT_PAY_ENABLED, 0);
        int signupOnboarding = userData.optInt(KEY_SIGNUP_ONBOARDING, 0);


        String defaultBranchDesktopUrl = Prefs.with(context).getString(SPLabels.BRANCH_DESKTOP_URL, "");
        String defaultBranchAndroidUrl = Prefs.with(context).getString(SPLabels.BRANCH_ANDROID_URL, "");
        String defaultBranchIosUrl = Prefs.with(context).getString(SPLabels.BRANCH_IOS_URL, "");
        String defaultBranchFallbackUrl = Prefs.with(context).getString(SPLabels.BRANCH_FALLBACK_URL, "");

        String branchDesktopUrl = userData.optString(KEY_BRANCH_DESKTOP_URL, defaultBranchDesktopUrl);
        String branchAndroidUrl = userData.optString(KEY_BRANCH_ANDROID_URL, defaultBranchAndroidUrl);
        String branchIosUrl = userData.optString(KEY_BRANCH_IOS_URL, defaultBranchIosUrl);
        String branchFallbackUrl = userData.optString(KEY_BRANCH_FALLBACK_URL, defaultBranchFallbackUrl);


        String jugnooCashTNC = userData.optString(KEY_JUGNOO_CASH_TNC,
                context.getString(R.string.jugnoo_cash_tnc, context.getString(R.string.app_name),
                context.getString(R.string.app_name),
                context.getString(R.string.app_name)));

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

        try {
            String gamePredictViewData = userData.optString(KEY_GAME_PREDICT_VIEW_DATA, "");
            gamePredictIconUrl = gamePredictViewData.split(VIEW_DATA_SPLITTER)[0];
            gamePredictName = gamePredictViewData.split(VIEW_DATA_SPLITTER)[1];
            gamePredictNew = gamePredictViewData.split(VIEW_DATA_SPLITTER)[2];
        } catch (Exception e) {
        }

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
        int mPesaEnabled = userData.optInt(KEY_MPESA_ENABLED, 1);

        int autosEnabled = userData.optInt(KEY_AUTOS_ENABLED, 0);
        int mealsEnabled = userData.optInt(KEY_MEALS_ENABLED, 0);
        int freshEnabled = userData.optInt(KEY_FRESH_ENABLED, 0);
        int deliveryEnabled = userData.optInt(KEY_DELIVERY_ENABLED, 0);
        int groceryEnabled = userData.optInt(KEY_GROCERY_ENABLED, 0);
        int menusEnabled = userData.optInt(KEY_MENUS_ENABLED, 0);
        int payEnabled = userData.optInt(KEY_PAY_ENABLED, 0);
        int feedEnabled = userData.optInt(KEY_FEED_ENABLED, 0);
        int prosEnabled = userData.optInt(KEY_PROS_ENABLED, 0);
        int deliveryCustomerEnabled = userData.optInt(KEY_DELIVERY_CUSTOMER_ENABLED, 0);
        String defaultClientId = userData.optString(KEY_DEFAULT_CLIENT_ID, Config.getAutosClientId());

        int inviteFriendButton = userData.optInt(KEY_INVITE_FRIEND_BUTTON, 0);
        int topupCardEnabled = userData.optInt(KEY_TOPUP_CARD_ENABLED, 0);

        int showHomeScreen = userData.optInt(SHOW_HOME_SCREEN, 0);
        int showSubscriptionData = userData.optInt(SHOW_SUBSCRIPTION_DATA, 0);
        int showJeanieHelpText = userData.optInt(KEY_SHOW_JEANIE_HELP_TEXT, 0);
        int showOfferDialog = userData.optInt(KEY_SHOW_OFFER_DIALOG, 1);
        int showTutorial = userData.optInt(KEY_SHOW_TUTORIAL, 0);
        int regAs = userData.optInt(Constants.KEY_REG_AS, 0);
        int cityId = userData.optInt(Constants.KEY_CITY_ID, 0);


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
                fatafatUrlLink, paytmEnabled, mobikwikEnabled, freeChargeEnabled,mPesaEnabled, notificationPreferenceEnabled,
                mealsEnabled, freshEnabled, deliveryEnabled, groceryEnabled, menusEnabled, payEnabled, feedEnabled, prosEnabled,
                deliveryCustomerEnabled,inviteFriendButton, defaultClientId, integratedJugnooEnabled,
                topupCardEnabled, showHomeScreen, showSubscriptionData, slideCheckoutPayEnabled, showJeanieHelpText,
                showOfferDialog, showTutorial, signupOnboarding,autosEnabled, countryCode, regAs, cityId);

		Prefs.with(context).save(KEY_USER_ID, userId);

        Prefs.with(context).save(Constants.SP_LAST_PHONE_NUMBER_SAVED, phoneNo);
        Prefs.with(context).save(Constants.SP_LAST_COUNTRY_CODE_SAVED, countryCode);

        Data.userData.setSubscriptionData(loginUserData.getSubscriptionData());
        Data.userData.setShowJugnooStarInAcccount(loginUserData.getShowJugnooStarInAccount());

        Data.userData.updateWalletBalances(userData.optJSONObject(KEY_WALLET_BALANCE), true);

        Data.userData.setJeanieIntroDialogContent(loginUserData.getJeanieIntroDialogContent());

        Data.userData.setSignupTutorial(loginUserData.getSignUpTutorial());

        parseSavedAddresses(context, userData, KEY_USER_SAVED_ADDRESSES);

        Data.userData.setExpandJeanie(loginUserData.getExpandJeanie());
        Data.userData.setExpandedGenieText(loginUserData.getExpandedGenieText());

        // Chat info objects needed for fugu tabs
        if(userData.optJSONArray(KEY_CHAT_INFO)!=null){
            Data.userData.setFuguChannelInfoJson(userData.optJSONArray(KEY_CHAT_INFO).toString());
        }

        SplashNewActivity.openHomeSwitcher = loginUserData.getShowHome() == 1 && Data.deepLinkIndex == -1;

        MyApplication.getInstance().getWalletCore().parsePaymentModeConfigDatas(userData.optJSONObject(KEY_WALLET_BALANCE));
        MyApplication.getInstance().getWalletCore().parsePaymentGatewayModeConfigs(loginUserData.getPaymentGatewayModeConfigData());

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
            Data.userData.setReferralMessages(parseReferralMessages(context, loginUserData));
            Data.userData.getReferralMessages().setReferralsCount(loginUserData.getReferralsCount());
            Data.userData.getReferralMessages().setReferralEarnedToday(loginUserData.getReferralEarnedToday());
            Data.userData.getReferralMessages().setReferralEarnedTotal(loginUserData.getReferralEarnedTotal());
            performUserAppMonitoring(context, userData);

//            if(Prefs.with(context).getString(Constants.KEY_SP_PUSH_OPENED_CLIENT_ID, "").equals("")) {
                Prefs.with(context).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, defaultClientId);
//            } else {
//                Prefs.with(context).save(Constants.KEY_SP_PUSH_OPENED_CLIENT_ID, "");
//            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

	private void reorderMenu(Context context) {

    	if(Data.userData != null && Data.userData.getMenuInfoList() != null){

			//free rides for life check
			if(Data.userData != null && Data.userData.getReferralMessages().getMultiLevelReferralEnabled()){
				int index = Data.userData.getMenuInfoList().indexOf(new MenuInfo(MenuInfoTags.FREE_RIDES.getTag()));
				int indexNew = Data.userData.getMenuInfoList().indexOf(new MenuInfo(MenuInfoTags.FREE_RIDES_NEW.getTag()));
				if(index > -1){
					Data.userData.getMenuInfoList().remove(index);
					MenuInfo menuInfo = new MenuInfo(context.getString(R.string.free_rides_for_life), MenuInfoTags.FREE_RIDES_NEW.getTag());
					Data.userData.getMenuInfoList().add(0, menuInfo);
				} else if(indexNew == -1){
					MenuInfo menuInfo = new MenuInfo(context.getString(R.string.free_rides_for_life), MenuInfoTags.FREE_RIDES_NEW.getTag());
					Data.userData.getMenuInfoList().add(0, menuInfo);
				}
			}


			//setting priority
    		for(int i = 0; i < Data.userData.getMenuInfoList().size(); i++){
				MenuInfo menuInfo = Data.userData.getMenuInfoList().get(i);
    			if(menuInfo != null) {
    				if(menuInfo.getPriority() == null){
						if (menuInfo.getTag().equalsIgnoreCase(MenuInfoTags.FREE_RIDES_NEW.getTag())
								|| menuInfo.getTag().equalsIgnoreCase(MenuInfoTags.FREE_RIDES.getTag())) {
							menuInfo.setPriority(1);
						}
						else if (menuInfo.getTag().equalsIgnoreCase(MenuInfoTags.OFFERS.getTag())) {
							menuInfo.setPriority(2);
						}
						else if (menuInfo.getTag().equalsIgnoreCase(MenuInfoTags.HISTORY.getTag())) {
							menuInfo.setPriority(3);
						}
						else if (menuInfo.getTag().equalsIgnoreCase(MenuInfoTags.WALLET.getTag())) {
							menuInfo.setPriority(4);
						}
						else if (menuInfo.getTag().equalsIgnoreCase(MenuInfoTags.INBOX.getTag())) {
							menuInfo.setPriority(5);
						}
						else if (menuInfo.getTag().equalsIgnoreCase(MenuInfoTags.FUGU_SUPPORT.getTag())) {
							menuInfo.setPriority(6);
						}
					}
					if (menuInfo.getTag().equalsIgnoreCase(MenuInfoTags.CHANGE_LOCALE.getTag())) {
						menuInfo.setName(context.getString(R.string.change_language));
						menuInfo.setShowInAccount(1);
					} else if (menuInfo.getTag().equalsIgnoreCase(MenuInfoTags.HISTORY.getTag())) {
						menuInfo.setName(context.getString(R.string.your_trips));
					}
				}
			}

    		//sorting
			Collections.sort(Data.userData.getMenuInfoList(), new Comparator<MenuInfo>() {
				@Override
				public int compare(MenuInfo o1, MenuInfo o2) {
					if(o1.getPriority() == null && o2.getPriority() == null){
						return 0;
					}
					else if(o1.getPriority() != null && o2.getPriority() == null){
						return -1;
					}
					else if(o1.getPriority() == null && o2.getPriority() != null){
						return 1;
					}
					else {
						if(o1.getPriority() > o2.getPriority()){
							return 1;
						}
						else if(o1.getPriority() < o2.getPriority()){
							return -1;
						}
						else {
							return 0;
						}
					}
				}
			});

		}

	}

	public void parseAutoData(Context context, JSONObject autoData, LoginResponse.Autos autosData) throws Exception{
        try {
            String destinationHelpText = autoData.optString("destination_help_text", "");
            String rideSummaryBadText = autoData.optString("ride_summary_text", context.getResources().getString(R.string.ride_summary_bad_text));
            String cancellationChargesPopupTextLine1 = autoData.optString("cancellation_charges_popup_text_line1", "");
            String cancellationChargesPopupTextLine2 = autoData.optString("cancellation_charges_popup_text_line2", "");
            String inRideSendInviteTextBold = autoData.optString("in_ride_send_invite_text_bold", context.getResources().getString(R.string.send_invites));
            String inRideSendInviteTextNormal = autoData.optString("in_ride_send_invite_text_normal", "");
            String inRideSendInviteTextBoldV2 = autoData.optString("in_ride_send_invite_text_bold_v2", "");
            String inRideSendInviteTextNormalV2 = autoData.optString("in_ride_send_invite_text_normal_v2", "");
            int rideStartInviteTextDeepIndexV2 = autoData.optInt("ride_start_invite_text_deep_index_v2", 0);
            int isBluetoothEnabled = autoData.optInt("bluetooth_tracker_enabled", 0);
            String confirmScreenFareEstimateEnable = autoData.optString("confirm_screen_fare_estimate_enabled", "0");
            String poolDestinationPopupText1 = autoData.optString("pool_destination_popup_text1", context.getResources().getString(R.string.pool_rides_offer_guaranteed_fares));
            String poolDestinationPopupText2 = autoData.optString("pool_destination_popup_text2", context.getResources().getString(R.string.please_provide_pickup_and_dest));
            String poolDestinationPopupText3 = autoData.optString("pool_destination_popup_text3", context.getResources().getString(R.string.you_will_not_change_dest));
            int rideEndGoodFeedbackViewType = autoData.optInt(KEY_RIDE_END_GOOD_FEEDBACK_VIEW_TYPE, RideEndGoodFeedbackViewType.RIDE_END_IMAGE_1.getOrdinal());
            String rideEndGoodFeedbackText = autoData.optString(KEY_RIDE_END_GOOD_FEEDBACK_TEXT, context.getString(R.string.end_ride_with_image_text, context.getString(R.string.app_name)));
            String baseFarePoolText = autoData.optString("base_fare_pool_text", "");
            int customerVerificationStatus = autoData.optInt(KEY_CUSTOMER_VERIFICATION_STATUS,0);

            int multiDestAllowed=autoData.optInt(Constants.MULTIPLE_DESTINATIONS_ALLOWED,0);
            Prefs.with(context).save(Constants.MULTIPLE_DESTINATIONS_ALLOWED,multiDestAllowed);
            Prefs.with(context).save(Constants.KEY_SHOW_POKEMON_DATA, autoData.optInt(KEY_SHOW_POKEMON_DATA, 0));
            Prefs.with(context).save(KEY_SP_CUSTOMER_LOCATION_UPDATE_INTERVAL, autoData.optLong(KEY_SP_CUSTOMER_LOCATION_UPDATE_INTERVAL,
					LOCATION_UPDATE_INTERVAL));

            int referAllStatus = autoData.optInt(KEY_REFER_ALL_STATUS); // if 0 show popup, else not show
            String referAllText = autoData.optString(KEY_REFER_ALL_TEXT, context.getResources().getString(R.string.upload_contact_message));
            String referAllTitle = autoData.optString(KEY_REFER_ALL_TITLE, context.getResources().getString(R.string.upload_contact_title));

            int referAllStatusLogin = autoData.optInt(KEY_REFER_ALL_STATUS_LOGIN, 1);
            String referAllTextLogin = autoData.optString(KEY_REFER_ALL_TEXT_LOGIN, "");
            String referAllTitleLogin = autoData.optString(KEY_REFER_ALL_TITLE_LOGIN, "");
            int isRazorpayEnabled = autoData.optInt(KEY_IS_RAZORPAY_ENABLED, 0);

            String fuguAppKey = autoData.optString(KEY_FUGU_APP_KEY, context.getString(R.string.fugu_key));
            int fuguAppType = autoData.optInt(KEY_FUGU_APP_TYPE, Data.FUGU_APP_TYPE);
            Prefs.with(context).save(Constants.KEY_FUGU_APP_KEY, fuguAppKey);
            Prefs.with(context).save(Constants.KEY_FUGU_APP_TYPE, fuguAppType);
            int isTipEnabled = autoData.optInt(KEY_TIP_ENABLED, 0);
            int resendEmailInvoiceEnabled = autoData.optInt(KEY_RESEND_EMAIL_INVOICE_ENABLED, context.getResources().getInteger(R.integer.resend_email_invoice_enabled));

//            int gpsLockStatus;
//            try {
//                gpsLockStatus = autoData.getInt(KEY_GPS_LOCK_STATUS);
//                if (gpsLockStatus == RentalRideStatus.END_RIDE_REQUESTED.getOrdinal()) {
//                    // Data.autoData.setRentalInRideStatus(RentalRideStatus.END_RIDE_REQUESTED.getOrdinal());
//                    HomeActivity.rentalInRideStatus = RentalRideStatus.END_RIDE_REQUESTED.getOrdinal();
//
//                } else if (gpsLockStatus == RentalRideStatus.ONGOING.getOrdinal()) {
//                    //   Data.autoData.setRentalInRideStatus(RentalRideStatus.ONGOING.getOrdinal());
//                    HomeActivity.rentalInRideStatus = RentalRideStatus.ONGOING.getOrdinal();
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

            NearbyPickupRegions nearbyPickupRegionses = autosData.getNearbyPickupRegions();

            Data.autoData = new AutoData(destinationHelpText, rideSummaryBadText, cancellationChargesPopupTextLine1
					, cancellationChargesPopupTextLine2, inRideSendInviteTextBold, inRideSendInviteTextNormal, confirmScreenFareEstimateEnable,
					poolDestinationPopupText1, poolDestinationPopupText2, poolDestinationPopupText3, rideEndGoodFeedbackViewType,
					rideEndGoodFeedbackText, baseFarePoolText, referAllStatus, referAllText, referAllTitle, referAllStatusLogin, referAllTextLogin
                    , referAllTitleLogin, nearbyPickupRegionses, inRideSendInviteTextBoldV2, inRideSendInviteTextNormalV2, rideStartInviteTextDeepIndexV2,
                    isRazorpayEnabled,isTipEnabled, autosData.getShowRegionSpecificFare(), resendEmailInvoiceEnabled,isBluetoothEnabled, customerVerificationStatus);

            Data.autoData.setUseRecentLocAtRequest(autosData.getUseRecentLocAtRequest());
            Data.autoData.setUseRecentLocAutoSnapMinDistance(autosData.getUseRecentLocAutoSnapMinDistance());
            Data.autoData.setUseRecentLocAutoSnapMaxDistance(autosData.getUseRecentLocAutoSnapMaxDistance());
            Data.autoData.setReferralPopupContent(autosData.getReferralPopupContent());
            Data.autoData.setFaultConditions(autosData.getFaultConditions());

            Data.userData.setGender(autoData.optInt(Constants.KEY_GENDER, 0));
            Log.d(TAG, "parseUserData Key Gender: gender saved" + autoData.optInt(Constants.KEY_GENDER, 0));
            Data.userData.setDateOfBirth(autoData.optString(Constants.KEY_DATE_OF_BIRTH, ""));

            Prefs.with(context).save(KEY_CUSTOMER_GENDER_FILTER, autoData.optInt(KEY_CUSTOMER_GENDER_FILTER,
                    context.getResources().getInteger(R.integer.customer_gender_filter)));
            Log.d(TAG, "parseConfigParams: filter" + autoData.optInt(KEY_CUSTOMER_GENDER_FILTER,
                    context.getResources().getInteger(R.integer.customer_gender_filter)));
            Prefs.with(context).save(KEY_CUSTOMER_DOB_INPUT, autoData.optInt(KEY_CUSTOMER_DOB_INPUT,
                    context.getResources().getInteger(R.integer.customer_dob_input)));

			long bidRequestRideTimeout = autoData.optLong(KEY_BID_REQUEST_RIDE_TIMEOUT, 420000);
			long bidTimeout = autoData.optLong(KEY_BID_TIMEOUT, 30000);
            Data.autoData.setBidRequestRideTimeout(bidRequestRideTimeout);
            Data.autoData.setBidTimeout(bidTimeout);
            Data.autoData.setMultiDestAllowed(multiDestAllowed==1);
            if(Data.autoData.getPromoCoupons() == null){
                Data.autoData.setPromoCoupons(new ArrayList<PromoCoupon>());
            } else{
                Data.autoData.getPromoCoupons().clear();
            }
            if(autosData.getPromotions() != null)
                Data.autoData.getPromoCoupons().addAll(autosData.getPromotions());
            if(autosData.getCoupons() != null)
                Data.autoData.getPromoCoupons().addAll(autosData.getCoupons());

            Prefs.with(context).save(KEY_FACEBOOK_PAGE_ID, autoData.optString(KEY_FACEBOOK_PAGE_ID, context.getString(R.string.facebook_page_id)));
            Prefs.with(context).save(KEY_FACEBOOK_PAGE_URL, autoData.optString(KEY_FACEBOOK_PAGE_URL, context.getString(R.string.facebook_page_url)));
            Prefs.with(context).save(KEY_WEB_LANDING_PAGE, autoData.optString(KEY_WEB_LANDING_PAGE, context.getString(R.string.web_landing_page)));
            Prefs.with(context).save(KEY_SHOW_ABOUT, autoData.optInt(KEY_SHOW_ABOUT, 1));
            Prefs.with(context).save(KEY_RIDE_FEEDBACK_RATING_BAR, autoData.optInt(KEY_RIDE_FEEDBACK_RATING_BAR, 0));

            Prefs.with(context).save(KEY_MAPS_API_CLIENT, autoData.optString(KEY_MAPS_API_CLIENT, BuildConfig.MAPS_CLIENT));
            Prefs.with(context).save(KEY_MAPS_API_PRIVATE_KEY, autoData.optString(KEY_MAPS_API_PRIVATE_KEY, BuildConfig.MAPS_PRIVATE_KEY));
            Prefs.with(context).save(KEY_MAPS_API_BROWSER_KEY, autoData.optString(KEY_MAPS_API_BROWSER_KEY, BuildConfig.MAPS_BROWSER_KEY));
            Prefs.with(context).save(KEY_MAPS_API_SIGN, autoData.optInt(KEY_MAPS_API_SIGN, BuildConfig.MAPS_APIS_SIGN ? 1 : 0));

            Prefs.with(context).save(KEY_STRIPE_KEY_LIVE, autoData.optString(KEY_STRIPE_KEY_LIVE, BuildConfig.STRIPE_KEY_LIVE));
            Prefs.with(context).save(Constants.KEY_CUSTOMER_SUPPORT_NUMBER, autoData.optString(Constants.KEY_CUSTOMER_SUPPORT_NUMBER, ""));

            Prefs.with(context).save(Constants.KEY_CUSTOMER_SUPPORT_EMAIL,
                    autoData.optString(Constants.KEY_CUSTOMER_SUPPORT_EMAIL, context.getString(R.string.default_support_email)));
            Prefs.with(context).save(Constants.KEY_CUSTOMER_SUPPORT_EMAIL_SUBJECT,
                    autoData.optString(Constants.KEY_CUSTOMER_SUPPORT_EMAIL_SUBJECT, context.getString(R.string.support_mail_subject, context.getString(R.string.app_name))));

            Utils.setCurrencyPrecision(context, autoData.optInt(Constants.KEY_CURRENCY_PRECISION, 1));
            Prefs.with(context).save(Constants.SCHEDULE_CURRENT_TIME_DIFF,
                    autoData.optInt(Constants.SCHEDULE_CURRENT_TIME_DIFF,30));
             Prefs.with(context).save(Constants.SCHEDULE_DAYS_LIMIT,
                    autoData.optInt(Constants.SCHEDULE_DAYS_LIMIT,2));

             Prefs.with(context).save(Constants.KEY_C_2_D_REFERRAL_IMAGE, autoData.optString(Constants.KEY_C_2_D_REFERRAL_IMAGE, ""));
             Prefs.with(context).save(Constants.KEY_C_2_D_REFERRAL_INFO, autoData.optString(Constants.KEY_C_2_D_REFERRAL_INFO, ""));
             Prefs.with(context).save(Constants.KEY_C_2_D_REFERRAL_DETAILS, autoData.optString(Constants.KEY_C_2_D_REFERRAL_DETAILS, ""));

            parseConfigParams(context, autoData);

            parseSafetyInfoData(autosData);

			if(Data.userData != null){
            	Data.userData.getReferralMessages().setMultiLevelReferralEnabled(autosData.getMultiLevelReferralEnabled());
            	Data.userData.getReferralMessages().setReferralImages(autosData.getReferralImages());

				reorderMenu(context);
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	private void parseSafetyInfoData(LoginResponse.Autos autosData) {
    	Data.autoData.setSafetyInfoData(autosData.getSafetyInfoData());

//		ArrayList<String> infoData = new ArrayList<>();
//		infoData.add("Our drivers are being checked regularly");
//		infoData.add("Hand sanitization facilities have been provided in the cabs");
//		infoData.add("Cabs are being sanitized throughly regularly");
//		Data.autoData.setSafetyInfoData(new SafetyInfoData(infoData, "Your safety is our top concern!",
//				"https://fchat.s3.ap-south-1.amazonaws.com/default/TjI1WUF4U0.1587365616963.png",
//				"https://fchat.s3.ap-south-1.amazonaws.com/default/TjI1WUF4U0.1587365616963.png"));
	}

	private void parseConfigParams(Context context, JSONObject autoData) {
    	String specifiedCountry = context.getResources().getBoolean(R.bool.specified_country_search_result_enabled) ?
				context.getString(R.string.specified_country_search_result) : "";
		Prefs.with(context).save(KEY_SPECIFIED_COUNTRY_PLACES_SEARCH, autoData.optString(KEY_SPECIFIED_COUNTRY_PLACES_SEARCH, specifiedCountry));
        int showFaq = context.getResources().getInteger(R.integer.visibility_faq) == context.getResources().getInteger(R.integer.view_visible) ? 1 : 0;
        Prefs.with(context).save(KEY_SHOW_FAQ, autoData.optInt(KEY_SHOW_FAQ, showFaq));
        int showTakeCash = context.getResources().getInteger(R.integer.visibility_take_cash) == context.getResources().getInteger(R.integer.view_visible) ? 1 : 0;
        Prefs.with(context).save(KEY_SHOW_TAKE_CASH_AT_RIDE_END, autoData.optInt(KEY_SHOW_TAKE_CASH_AT_RIDE_END, showTakeCash));
        int showAmount = context.getResources().getInteger(R.integer.show_amount_on_ride_summary) == context.getResources().getInteger(R.integer.view_visible) ? 1 : 0;
        Prefs.with(context).save(KEY_SHOW_FARE_DETAILS_AT_RIDE_END, autoData.optInt(KEY_SHOW_FARE_DETAILS_AT_RIDE_END, showAmount));
        int showFareInRideHistory = context.getResources().getInteger(R.integer.visibility_ride_history_amount) == context.getResources().getInteger(R.integer.view_visible) ? 1 : 0;
        Prefs.with(context).save(KEY_SHOW_FARE_IN_RIDE_HISTORY, autoData.optInt(KEY_SHOW_FARE_IN_RIDE_HISTORY, showFareInRideHistory));
        Prefs.with(context).save(KEY_SHOW_BASE_FARE_IN_RIDE_SUMMARY, autoData.optInt(KEY_SHOW_BASE_FARE_IN_RIDE_SUMMARY, showFareInRideHistory));
        Prefs.with(context).save(KEY_SHOW_IN_RIDE_PAYMENT_OPTION, autoData.optInt(KEY_SHOW_IN_RIDE_PAYMENT_OPTION, showFareInRideHistory));
        int showJugnooCash = context.getResources().getInteger(R.integer.visibility_jugnoo_cash_in_wallet) == context.getResources().getInteger(R.integer.view_visible) ? 1 : 0;
        Prefs.with(context).save(KEY_SHOW_JUGNOO_CASH_IN_WALLET, autoData.optInt(KEY_SHOW_JUGNOO_CASH_IN_WALLET, showJugnooCash));
        int fareEstimateHover = context.getResources().getInteger(R.integer.visibility_fare_estimate_hover) == context.getResources().getInteger(R.integer.view_visible) ? 1 : 0;
        Prefs.with(context).save(KEY_SHOW_FARE_ESTIMATE_HOVER_BUTTON, autoData.optInt(KEY_SHOW_FARE_ESTIMATE_HOVER_BUTTON, fareEstimateHover));
        int cashAboveAll = context.getResources().getInteger(R.integer.cash_above_all);
        Prefs.with(context).save(KEY_CASH_ABOVE_ALL_WALLETS, autoData.optInt(KEY_CASH_ABOVE_ALL_WALLETS, cashAboveAll));
        Prefs.with(context).save(KEY_FORCE_MPESA_PAYMENT, autoData.optInt(KEY_FORCE_MPESA_PAYMENT, context.getResources().getBoolean(R.bool.force_mpesa_payment) ? 1 : 0));
        Prefs.with(context).save(KEY_CUSTOMER_FETCH_INRIDE_PATH_INTERVAL,
                autoData.optInt(KEY_CUSTOMER_FETCH_INRIDE_PATH_INTERVAL,
                        context.getResources().getInteger(R.integer.customer_fetch_inride_path_interval)));
        Prefs.with(context).save(KEY_CUSTOMER_FETCH_DRIVER_LOCATION_INTERVAL,
                autoData.optInt(KEY_CUSTOMER_FETCH_DRIVER_LOCATION_INTERVAL,
                        context.getResources().getInteger(R.integer.customer_fetch_driver_location_interval)));
        Prefs.with(context).save(KEY_HIT_PLACE_DETAILS_AFTER_GEOCODE, autoData.optBoolean(KEY_HIT_PLACE_DETAILS_AFTER_GEOCODE,
				context.getResources().getBoolean(R.bool.hit_place_details_after_geocode)));
        Prefs.with(context).save(KEY_CUSTOMER_CANCEL_RIDE_ENABLED, autoData.optInt(KEY_CUSTOMER_CANCEL_RIDE_ENABLED,
				context.getResources().getInteger(R.integer.customer_cancel_ride_enabled)));

        Prefs.with(context).save(KEY_CUSTOMER_PLAY_SOUND_RIDE_ACCEPT, autoData.optInt(KEY_CUSTOMER_PLAY_SOUND_RIDE_ACCEPT,
				context.getResources().getInteger(R.integer.customer_play_sound_ride_accept)));

        Prefs.with(context).save(KEY_CUSTOMER_PLAY_SOUND_RIDE_ARRIVED, autoData.optInt(KEY_CUSTOMER_PLAY_SOUND_RIDE_ARRIVED,
				context.getResources().getInteger(R.integer.customer_play_sound_ride_arrived)));

        Prefs.with(context).save(KEY_CUSTOMER_PLAY_SOUND_RIDE_START, autoData.optInt(KEY_CUSTOMER_PLAY_SOUND_RIDE_START,
				context.getResources().getInteger(R.integer.customer_play_sound_ride_start)));

        Prefs.with(context).save(KEY_CUSTOMER_PLAY_SOUND_RIDE_END, autoData.optInt(KEY_CUSTOMER_PLAY_SOUND_RIDE_END,
				context.getResources().getInteger(R.integer.customer_play_sound_ride_end)));

        Prefs.with(context).save(KEY_PICKUP_DROP_LIKE_ENABLED, autoData.optInt(KEY_PICKUP_DROP_LIKE_ENABLED,
				context.getResources().getInteger(R.integer.pickup_drop_like_enabled)));
        Prefs.with(context).save(KEY_CUSTOMER_GOOGLE_APIS_LOGGING, autoData.optInt(KEY_CUSTOMER_GOOGLE_APIS_LOGGING,
				context.getResources().getInteger(R.integer.google_apis_logging)));
        boolean scheduleRideFallback = context.getResources().getBoolean(R.bool.schedule_ride_enabled);
        Prefs.with(context).save(SCHEDULE_RIDE_ENABLED, autoData.optBoolean(SCHEDULE_RIDE_ENABLED,scheduleRideFallback));

        Prefs.with(context).save(KEY_CUSTOMER_GEOCODE_TIME_LIMIT, autoData.optLong(KEY_CUSTOMER_GEOCODE_TIME_LIMIT,
                (long)context.getResources().getInteger(R.integer.customer_geocode_time_limit)));
        Prefs.with(context).save(KEY_CUSTOMER_GEOCODE_HIT_LIMIT, autoData.optInt(KEY_CUSTOMER_GEOCODE_HIT_LIMIT,
                context.getResources().getInteger(R.integer.customer_geocode_hit_limit)));
        Prefs.with(context).save(KEY_CUSTOMER_GEOCODE_LIMIT_ENABLED, autoData.optInt(KEY_CUSTOMER_GEOCODE_LIMIT_ENABLED,
                context.getResources().getInteger(R.integer.customer_geocode_limit_enabled)));

		Prefs.with(context).save(KEY_CUSTOMER_SHOW_INCLUDE_TOLL_IN_SUMMARY, autoData.optInt(KEY_CUSTOMER_SHOW_INCLUDE_TOLL_IN_SUMMARY,
				context.getResources().getInteger(R.integer.customer_show_include_toll_in_summary)));
		Prefs.with(context).save(KEY_CUSTOMER_SETTLE_DEBT_AFTER_ADD_CARD, autoData.optInt(KEY_CUSTOMER_SETTLE_DEBT_AFTER_ADD_CARD,
				context.getResources().getInteger(R.integer.customer_hit_settle_debt_after_add_card)));
		Prefs.with(context).save(KEY_CUSTOMER_DIRECTIONS_FOR_DRIVER_ENROUTE, autoData.optInt(KEY_CUSTOMER_DIRECTIONS_FOR_DRIVER_ENROUTE,
				context.getResources().getInteger(R.integer.customer_directions_for_driver_enroute)));
		Prefs.with(context).save(KEY_CUSTOMER_HIT_GEOCODE_FREE_ROAM, autoData.optInt(KEY_CUSTOMER_HIT_GEOCODE_FREE_ROAM,
				context.getResources().getInteger(R.integer.customer_hit_geocode_free_roam)));
		Prefs.with(context).save(KEY_CUSTOMER_PICKUP_FREE_ROAM_ALLOWED, autoData.optInt(KEY_CUSTOMER_PICKUP_FREE_ROAM_ALLOWED,
				context.getResources().getInteger(R.integer.customer_pickup_free_roam_allowed)));
		Prefs.with(context).save(KEY_CUSTOMER_SHOW_ADD_SAVED_PLACE, autoData.optInt(KEY_CUSTOMER_SHOW_ADD_SAVED_PLACE,
				context.getResources().getInteger(R.integer.customer_show_add_saved_place)));
        Prefs.with(context).save(HIPPO_SUPPORT_FAQ_NAME, autoData.optString(HIPPO_SUPPORT_FAQ_NAME,
                context.getString(R.string.customer_hippo_support_faq_name)));
        Prefs.with(context).save(KEY_CUSTOMER_TIP_VALUES, autoData.optString(KEY_CUSTOMER_TIP_VALUES,
                context.getString(R.string.customer_tip_values)));
        Prefs.with(context).save(KEY_CUSTOMER_SHOW_SURGE_ICON, autoData.optInt(KEY_CUSTOMER_SHOW_SURGE_ICON,
                context.getResources().getInteger(R.integer.customer_show_surge_icon)));
        Prefs.with(context).save(KEY_CUSTOMER_PRIORITY_TIP_TITLE, autoData.optString(KEY_CUSTOMER_PRIORITY_TIP_TITLE,
                context.getString(R.string.customer_priority_tip_title)));
        Prefs.with(context).save(KEY_CUSTOMER_PRIORITY_TIP_DESCRIPTION, autoData.optString(KEY_CUSTOMER_PRIORITY_TIP_DESCRIPTION,
                context.getString(R.string.customer_priority_tip_description)));
        Prefs.with(context).save(KEY_CUSTOMER_CURRENCY_CODE_WITH_FARE_ESTIMATE, autoData.optInt(KEY_CUSTOMER_CURRENCY_CODE_WITH_FARE_ESTIMATE,
                context.getResources().getInteger(R.integer.customer_currency_code_with_fare_estimate)));
        Prefs.with(context).save(KEY_CUSTOMER_SHOW_WALLET_TRANSACTIONS, autoData.optInt(KEY_CUSTOMER_SHOW_WALLET_TRANSACTIONS,
                context.getResources().getInteger(R.integer.customer_show_wallet_transactions)));
        Prefs.with(context).save(KEY_CUSTOMER_SHOW_WALLET_CASH, autoData.optInt(KEY_CUSTOMER_SHOW_WALLET_CASH,
                context.getResources().getInteger(R.integer.customer_show_wallet_cash)));
        Prefs.with(context).save(KEY_CUSTOMER_GOOGLE_TRAFFIC_ENABLED, autoData.optInt(KEY_CUSTOMER_GOOGLE_TRAFFIC_ENABLED,
                context.getResources().getInteger(R.integer.customer_google_traffic_enabled)));
        Prefs.with(context).save(KEY_CUSTOMER_REQUEST_RIDE_BID_FAST_INTERVAL, autoData.optInt(KEY_CUSTOMER_REQUEST_RIDE_BID_FAST_INTERVAL,
                context.getResources().getInteger(R.integer.customer_request_ride_fast_interval)));
        Prefs.with(context).save(KEY_CUSTOMER_ARRIVED_BEEP_ENABLED, autoData.optInt(KEY_CUSTOMER_ARRIVED_BEEP_ENABLED,
                context.getResources().getInteger(R.integer.customer_arrived_beep_enabled)));

        Prefs.with(context).save(KEY_CUSTOMER_PARTNER_URL, autoData.optString(KEY_CUSTOMER_PARTNER_URL,
                context.getString(R.string.customer_partner_url)));
        Prefs.with(context).save(KEY_CUSTOMER_OPEN_PARTNER_DIALOG, autoData.optInt(KEY_CUSTOMER_OPEN_PARTNER_DIALOG,
                context.getResources().getInteger(R.integer.customer_open_partner_dialog)));
        Prefs.with(context).save(KEY_CUSTOMER_PARTNER_DIALOG_VIEW_COUNT, autoData.optInt(KEY_CUSTOMER_PARTNER_DIALOG_VIEW_COUNT,
                context.getResources().getInteger(R.integer.customer_partner_dialog_view_count)));
        Prefs.with(context).save(KEY_CUSTOMER_PARTNER_DIALOG_TITLE, autoData.optString(KEY_CUSTOMER_PARTNER_DIALOG_TITLE,
                context.getString(R.string.customer_partner_dialog_title)));
        Prefs.with(context).save(KEY_CUSTOMER_PARTNER_DIALOG_MESSAGE, autoData.optString(KEY_CUSTOMER_PARTNER_DIALOG_MESSAGE,
                context.getString(R.string.customer_partner_dialog_message)));
        Prefs.with(context).save(KEY_CUSTOMER_SHOW_CONVENIENCE_CHARGE_FARE_ESTIMATE, autoData.optInt(KEY_CUSTOMER_SHOW_CONVENIENCE_CHARGE_FARE_ESTIMATE,
                context.getResources().getInteger(R.integer.customer_show_convenience_charge_fare_estimate)));
        Prefs.with(context).save(KEY_CUSTOMER_REG_AS_DRIVER_PHONE_EDIT_ALERT, autoData.optInt(KEY_CUSTOMER_REG_AS_DRIVER_PHONE_EDIT_ALERT,
                context.getResources().getInteger(R.integer.customer_reg_as_driver_phone_edit_alert)));
        Prefs.with(context).save(KEY_CUSTOMER_REG_AS_DRIVER_PHONE_EDIT_ALERT_MESSAGE, autoData.optString(KEY_CUSTOMER_REG_AS_DRIVER_PHONE_EDIT_ALERT_MESSAGE,
                context.getString(R.string.registered_as_driver_phone_number_will_be_edited)));
        Prefs.with(context).save(KEY_CUSTOMER_GOOGLE_CACHING_ENABLED, autoData.optInt(KEY_CUSTOMER_GOOGLE_CACHING_ENABLED,
                context.getResources().getInteger(R.integer.customer_google_caching_enabled)));
        Prefs.with(context).save(KEY_CUSTOMER_GPS_LOCK_STATUS_POLLING_INTERVAL, autoData.optLong(KEY_CUSTOMER_GPS_LOCK_STATUS_POLLING_INTERVAL,
                context.getResources().getInteger(R.integer.customer_gps_lock_status_polling_interval)));

		Prefs.with(context).save(KEY_CUSTOMER_TUTORIAL_BANNER_TEXT, autoData.optString(KEY_CUSTOMER_TUTORIAL_BANNER_TEXT, ""));
		Prefs.with(context).save(KEY_CUSTOMER_LOCATION_ON_MAP_ON_TOP, autoData.optInt(KEY_CUSTOMER_LOCATION_ON_MAP_ON_TOP, 1));
		Prefs.with(context).save(KEY_CUSTOMER_BID_INCREMENT, autoData.optString(KEY_CUSTOMER_BID_INCREMENT, String.valueOf(0D)));

		Prefs.with(context).save(KEY_CUSTOMER_SHOW_BOUNCING_MARKER, autoData.optInt(KEY_CUSTOMER_SHOW_BOUNCING_MARKER,
				context.getResources().getBoolean(R.bool.show_bouncing_marker)?1:0));
		Prefs.with(context).save(KEY_CUSTOMER_SHOW_SAVE_LOCATION_DIALOG, autoData.optInt(KEY_CUSTOMER_SHOW_SAVE_LOCATION_DIALOG,
				context.getResources().getBoolean(R.bool.show_save_location_dialog)?1:0));

		Prefs.with(context).save(KEY_CUSTOMER_REGION_FARE_CHECK_ENABLED, autoData.optInt(KEY_CUSTOMER_REGION_FARE_CHECK_ENABLED, 0));
		Prefs.with(context).save(KEY_CUSTOMER_PICKUP_ADDRESS_EMPTY_CHECK_ENABLED, autoData.optInt(KEY_CUSTOMER_PICKUP_ADDRESS_EMPTY_CHECK_ENABLED, 0));
		Prefs.with(context).save(KEY_CUSTOMER_DIRECTIONS_CACHING, autoData.optInt(KEY_CUSTOMER_DIRECTIONS_CACHING,
				context.getResources().getInteger(R.integer.customer_directions_caching)));
		Prefs.with(context).save(KEY_CUSTOMER_REMOVE_PICKUP_ADDRESS_HIT, autoData.optInt(KEY_CUSTOMER_REMOVE_PICKUP_ADDRESS_HIT,
				context.getResources().getInteger(R.integer.remove_pickup_address_hit)));
		Prefs.with(context).save(KEY_CUSTOMER_REQUEST_RIDE_POPUP, autoData.optInt(KEY_CUSTOMER_REQUEST_RIDE_POPUP,
				context.getResources().getInteger(R.integer.show_confirm_popup_before_ride_request)));

		Prefs.with(context).save(KEY_CUSTOMER_YOUTUBE_API_KEY, autoData.optString(KEY_CUSTOMER_YOUTUBE_API_KEY,
				context.getString(R.string.youtube_api_key)));
		Prefs.with(context).save(KEY_DIRECTIONS_MAX_DISTANCE_THRESHOLD, autoData.optString(KEY_DIRECTIONS_MAX_DISTANCE_THRESHOLD, "200000.0"));

		Prefs.with(context).save(KEY_DRIVER_TO_PICKUP_PATH_ENABLED, autoData.optInt(KEY_DRIVER_TO_PICKUP_PATH_ENABLED, 1));
		Prefs.with(context).save(KEY_SHOW_DRIVER_MARKER_IN_RIDE, autoData.optInt(KEY_SHOW_DRIVER_MARKER_IN_RIDE, 1));
		Prefs.with(context).save(KEY_SHOW_RIDE_COVERED_PATH, autoData.optInt(KEY_SHOW_RIDE_COVERED_PATH, 0));
		Prefs.with(context).save(KEY_HIPPO_TICKET_FOR_RIDE_ISSUES, autoData.optInt(KEY_HIPPO_TICKET_FOR_RIDE_ISSUES,
				context.getResources().getInteger(R.integer.hippo_ticket_for_ride_issues)));
		Prefs.with(context).save(KEY_HIPPO_TICKET_RIDE_FAQ_NAME, autoData.optString(KEY_HIPPO_TICKET_RIDE_FAQ_NAME,
				context.getString(R.string.hippo_ticket_ride_faq_name)));

		Prefs.with(context).save(KEY_HIPPO_CALL_ENABLED, autoData.optInt(KEY_HIPPO_CALL_ENABLED,
				context.getResources().getInteger(R.integer.hippo_call_enabled)));
		Prefs.with(context).save(KEY_HIPPO_CALL_TYPE, autoData.optString(KEY_HIPPO_CALL_TYPE, "audio"));

		Prefs.with(context).save(KEY_PROMO_BANNER_DATA, autoData.optString(KEY_PROMO_BANNER_DATA, ""));
		Prefs.with(context).save(KEY_DRIVER_TRACKING_USING_STREAM_ENABLED, autoData.optInt(KEY_DRIVER_TRACKING_USING_STREAM_ENABLED, 1));
		Prefs.with(context).save(KEY_DRIVER_MARKER_ANIM_DURATION_INRIDE, autoData.optLong(KEY_DRIVER_MARKER_ANIM_DURATION_INRIDE, 9000));
		Prefs.with(context).save(KEY_DRIVER_MARKER_ANIM_DURATION_ACCEPT, autoData.optLong(KEY_DRIVER_MARKER_ANIM_DURATION_ACCEPT, 9000));

		Prefs.with(context).save(KEY_REINVITE_USERS_ENABLED, autoData.optInt(KEY_REINVITE_USERS_ENABLED, 1));
		Prefs.with(context).save(KEY_HIDE_REGIONS_WITH_NO_DRIVERS, autoData.optInt(KEY_HIDE_REGIONS_WITH_NO_DRIVERS,
				context.getResources().getInteger(R.integer.hide_regions_with_no_drivers)));
		Prefs.with(context).save(KEY_PAY_VIA_UPI_ENABLED, autoData.optInt(KEY_PAY_VIA_UPI_ENABLED,
				context.getResources().getInteger(R.integer.pay_via_upi_enabled)));
		Prefs.with(context).save(SHOW_CUSTOMER_VERIFICATION, autoData.optInt(SHOW_CUSTOMER_VERIFICATION,
				context.getResources().getInteger(R.integer.show_customer_verification)));
		Prefs.with(context).save(KEY_DEACTIVATE_ACCOUNT_ENABLED, autoData.optInt(KEY_DEACTIVATE_ACCOUNT_ENABLED,
				context.getResources().getInteger(R.integer.deactivate_account_enabled)));
		Prefs.with(context).save(KEY_REQUESTED_FOR_ACCOUNT_DELETION, autoData.optInt(KEY_REQUESTED_FOR_ACCOUNT_DELETION, 0));

		parseCityConfigVariables(context, autoData, String.valueOf(Data.userData != null ? Data.userData.getCityId() : 0));

		parseJungleApiObjects(context, autoData);
	}

	private void parseCityConfigVariables(Context context, JSONObject userData, String cityId){
		try{
			JSONObject cityMainObj = userData.optJSONObject(Constants.KEY_CITY_OBJ);

			JSONObject cityDefaultObj = getCityIdObj(cityMainObj, String.valueOf(0));

			JSONObject cityObj = getCityIdObj(cityMainObj, cityId);

			saveCityLevelParam(context, cityDefaultObj, cityObj, KEY_PROMO_BANNER_DATA, true);

		} catch(Exception e){
			e.printStackTrace();
		}
	}

	private JSONObject getCityIdObj(JSONObject cityMainObj, String cityId){
		if(cityMainObj.has(cityId)){
			return cityMainObj.optJSONObject(cityId);
		} else {
			Iterator<String> keys = cityMainObj.keys();
			while(keys.hasNext()){
				String key = keys.next();
				if(key.startsWith(cityId+",") || key.endsWith(","+cityId) || key.contains(","+cityId+",")){
					return cityMainObj.optJSONObject(key);
				}
			}
		}

		return new JSONObject();
	}

	private void saveCityLevelParam(Context context, JSONObject cityDefaultObj, JSONObject cityObj, String key, boolean isString) {
		if(cityObj.has(key)) {
			if(!isString) {
				Prefs.with(context).save(key, cityObj.optInt(key));
			} else {
				Prefs.with(context).save(key, cityObj.optString(key));
			}
		} else if(cityDefaultObj.has(key)) {
			if(!isString) {
				Prefs.with(context).save(key, cityDefaultObj.optInt(key));
			} else {
				Prefs.with(context).save(key, cityDefaultObj.optString(key));
			}
		}
	}

	private void parseJungleApiObjects(Context context, JSONObject userData) {
		try {
			if(Data.jungleApisDisable == 1){
				Prefs.with(context).save(KEY_JUNGLE_DIRECTIONS_OBJ, EMPTY_JSON_OBJECT);
				Prefs.with(context).save(KEY_CFE_JUNGLE_DIRECTIONS_OBJ, EMPTY_JSON_OBJECT);
				Prefs.with(context).save(KEY_JUNGLE_DISTANCE_MATRIX_OBJ, EMPTY_JSON_OBJECT);
				Prefs.with(context).save(KEY_JUNGLE_GEOCODE_OBJ, EMPTY_JSON_OBJECT);
				Prefs.with(context).save(KEY_JUNGLE_AUTOCOMPLETE_OBJ, EMPTY_JSON_OBJECT);
				Data.jungleApisDisable = 0;
				return;
			}

			String fmToken = userData.optString(KEY_JUNGLE_FM_API_KEY_ANDROID_CUSTOMER, "");
			Prefs.with(context).save(KEY_JUNGLE_FM_API_KEY_ANDROID_CUSTOMER, fmToken);

			String jungleObjStr = BuildConfig.DEBUG ? JUNGLE_JSON_OBJECT : EMPTY_JSON_OBJECT;
			JSONObject jungleObj = userData.optJSONObject(KEY_JUNGLE_DIRECTIONS_OBJ);
			if(jungleObj != null){
				jungleObjStr = jungleObj.toString();
			}

			Prefs.with(context).save(KEY_JUNGLE_DIRECTIONS_OBJ, jungleObjStr);

			JSONObject jungleCFEDirectionsObj = userData.optJSONObject(KEY_CFE_JUNGLE_DIRECTIONS_OBJ);
			Prefs.with(context).save(KEY_CFE_JUNGLE_DIRECTIONS_OBJ, jungleCFEDirectionsObj!=null ? jungleCFEDirectionsObj.toString(): jungleObjStr);

			JSONObject jungleDMObj = userData.optJSONObject(KEY_JUNGLE_DISTANCE_MATRIX_OBJ);
			Prefs.with(context).save(KEY_JUNGLE_DISTANCE_MATRIX_OBJ, jungleDMObj!=null ? jungleDMObj.toString(): jungleObjStr);

			JSONObject jungleGObj = userData.optJSONObject(KEY_JUNGLE_GEOCODE_OBJ);
			Prefs.with(context).save(KEY_JUNGLE_GEOCODE_OBJ, jungleGObj!=null ? jungleGObj.toString(): jungleObjStr);

			JSONObject jungleACbj = userData.optJSONObject(KEY_JUNGLE_AUTOCOMPLETE_OBJ);
			Prefs.with(context).save(KEY_JUNGLE_AUTOCOMPLETE_OBJ, jungleACbj!=null ? jungleACbj.toString(): jungleObjStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void allowedAuthChannelTimeConfigVariables(Context context, JSONObject jObj){

        Prefs.with(context).save(Constants.KEY_LOGIN_CHANNEL, jObj.optInt(Constants.KEY_LOGIN_CHANNEL, 0));
        Prefs.with(context).save(Constants.KEY_SHOW_FACEBOOK_LOGIN, jObj.optInt(Constants.KEY_SHOW_FACEBOOK_LOGIN, 1));
        Prefs.with(context).save(Constants.KEY_SHOW_GOOGLE_LOGIN, jObj.optInt(Constants.KEY_SHOW_GOOGLE_LOGIN, 1));

        Prefs.with(context).save(Constants.KEY_TERMS_OF_USE_URL, jObj.optString(Constants.KEY_TERMS_OF_USE_URL, context.getString(R.string.terms_of_use_url)));
        Prefs.with(context).save(Constants.KEY_SHOW_TERMS, jObj.optInt(Constants.KEY_SHOW_TERMS, 1));

        Prefs.with(context).save(Constants.KEY_DEFAULT_COUNTRY_CODE, jObj.optString(Constants.KEY_DEFAULT_COUNTRY_CODE));
        Prefs.with(context).save(Constants.KEY_DEFAULT_SUB_COUNTRY_CODE, jObj.optString(Constants.KEY_DEFAULT_SUB_COUNTRY_CODE));
        Prefs.with(context).save(Constants.KEY_DEFAULT_COUNTRY_ISO, jObj.optString(Constants.KEY_DEFAULT_COUNTRY_ISO));
        Prefs.with(context).save(SP_OTP_VIA_CALL_ENABLED, jObj.optInt(KEY_OTP_VIA_CALL_ENABLED, 0));

        Prefs.with(context).save(KEY_CUSTOMER_GENDER_FILTER, jObj.optInt(KEY_CUSTOMER_GENDER_FILTER,
                context.getResources().getInteger(R.integer.customer_gender_filter)));
        android.util.Log.d("In JSONPARSER", "parseConfigParams: gender filer" + jObj.optInt(KEY_CUSTOMER_GENDER_FILTER,
                context.getResources().getInteger(R.integer.customer_gender_filter)));
        Prefs.with(context).save(KEY_CUSTOMER_DOB_INPUT, jObj.optInt(KEY_CUSTOMER_DOB_INPUT,
                context.getResources().getInteger(R.integer.customer_dob_input)));
    }

	public static void parseAndSetLocale(Context context, JSONObject autoData) {
        if(autoData.has(KEY_DEFAULT_LANG) && Prefs.with(context).getString(KEY_DEFAULT_LANG, "eee").equals("eee")) {
            Prefs.with(context).save(KEY_DEFAULT_LANG, autoData.optString(KEY_DEFAULT_LANG, context.getString(R.string.default_lang)));
            LocaleHelper.setLocale(context, Prefs.with(context).getString(KEY_DEFAULT_LANG, context.getString(R.string.default_lang)));
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
        }
    }


    public <T extends LoginResponse.Menus> void setPromoCoupons(T loginData){
        try {
            if(loginData.getPromoCoupons() == null){
                loginData.setPromoCoupons(new ArrayList<PromoCoupon>());
            } else{
                loginData.getPromoCoupons().clear();
            }
            if(loginData.getPromotions() != null)
                loginData.getPromoCoupons().addAll(loginData.getPromotions());
            if(loginData.getCoupons() != null)
                loginData.getPromoCoupons().addAll(loginData.getCoupons());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void parsePayData(LoginResponse.Pay payData){
        try{
            if(payData != null) {
                Data.setPayData(new PayData(payData));

                try {
                    if (Data.getPayData().getPromoCoupons() == null) {
                        Data.getPayData().setPromoCoupons(new ArrayList<PromoCoupon>());
                    } else {
                        Data.getPayData().getPromoCoupons().clear();
                    }
                    if (payData.getPromotions() != null)
                        Data.getPayData().getPromoCoupons().addAll(payData.getPromotions());
                    if (payData.getCoupons() != null)
                        Data.getPayData().getPromoCoupons().addAll(payData.getCoupons());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void parseFeedData(LoginResponse.Feed feedData){
        try{
            if(feedData != null) {
                Data.setFeedData(feedData);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void parseProsData(Context context, LoginResponse.Pros prosData){
        try{
            if(prosData != null) {
                Prefs.with(context).save(Constants.SP_PROS_LAST_COMPLETE_JOB_ID, prosData.getJobId());
                prosData.setJobId(0);
                Data.setProsData(prosData);
            }
        } catch (Exception e){
            e.printStackTrace();
    }
        }


    public String parseAccessTokenLoginData(Context context, String response, LoginResponse loginResponse,
                                            LoginVia loginVia, LatLng latLng) throws Exception {

        JSONObject jObj = new JSONObject(response);

        //Fetching login data
        JSONObject jUserDataObject = jObj.getJSONObject(KEY_USER_DATA);
        JSONObject jAutosObject = jObj.optJSONObject(KEY_AUTOS);
        parseUserData(context, jUserDataObject, loginResponse.getUserData());
        parseAutoData(context, jAutosObject, loginResponse.getAutos());

        if(loginResponse.getMenus()!=null){
            Data.setMenusData(loginResponse.getMenus());
            setPromoCoupons(Data.getMenusData());

        }
        if(loginResponse.getDeliveryCustomer()!=null){
            Data.setDeliveryCustomerData(loginResponse.getDeliveryCustomer());
            setPromoCoupons(Data.getDeliveryCustomerData());

        }
        if(loginResponse.getMeals()!=null){
            Data.setMealsData(loginResponse.getMeals());
            setPromoCoupons(Data.getMealsData());

        }
        if(loginResponse.getFresh()!=null){
            Data.setFreshData(loginResponse.getFresh());
            setPromoCoupons(Data.getFreshData());

        }
        if(loginResponse.getGrocery()!=null){
            Data.setGroceryData(loginResponse.getGrocery());
            setPromoCoupons(Data.getGroceryData());


        }
        if(loginResponse.getFeed() != null) {
            Data.setFeedData(loginResponse.getFeed());
        }




        parsePayData(loginResponse.getPay());
        parseDeliveryData(loginResponse.getDelivery());
        parseProsData(context, loginResponse.getPros());
        MyApplication.getInstance().getWalletCore().setDefaultPaymentOption(null);
        parseFindDriverResp(loginResponse.getAutos(), context);

        //Fetching user current status
        JSONObject jUserStatusObject = jObj.getJSONObject(KEY_AUTOS).getJSONObject(KEY_STATUS);
        String resp = parseCurrentUserStatus(context, loginResponse.getAutos().getCurrentUserStatus(), jUserStatusObject);

        parseRateAppFlagContent(context, jUserDataObject);

        parseCancellationReasons(loginResponse.getAutos());
        parseFeedbackReasonArrayList(loginResponse.getAutos());

        loginAnalyticEvents(context, loginVia);

        Prefs.with(context).save(SP_FRESH_LAST_ADDRESS_OBJ, EMPTY_JSON_OBJECT);
        Prefs.with(context).save(SP_ASKLOCAL_LAST_ADDRESS_OBJ, EMPTY_JSON_OBJECT);
        Data.setLatLngOfJeanieLastShown(latLng);

        resetMenusFilters(context);

        resetUseCouponSP(context);
        resetIsVegToggle(context);

        Paper.book().delete(PaperDBKeys.HISTORY_PRODUCT_TYPES);

        try {

			FirebaseInstanceId.getInstance().getInstanceId()
					.addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
						@Override
						public void onComplete(@NonNull Task<InstanceIdResult> task) {
							if (!task.isSuccessful()) {
								Log.w(TAG, "getInstanceId failed");
								return;
							}

							// Get new Instance ID token
							String token = task.getResult().getToken();

							if((Data.isFuguChatEnabled() || Data.isMenuTagEnabled(MenuInfoTags.TICKET_SUPPORT))
									&& Data.getFuguUserData()!=null) {
								Data.initializeFuguHandler((Activity) context, Data.getFuguUserData(), token);
							}
						}
					});

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


    private void parseFindDriverResp(LoginResponse.Autos autos, Context context){
        try {
            parseDriversToShow(autos.getDrivers());

//            Data.autoData.setNoDriverFoundTip(0.0);
            Data.autoData.setServiceTypes(autos.getServiceTypes());

            Data.autoData.setFareFactor(1);
            if(autos.getFareFactor() != null) {
                Data.autoData.setFareFactor(autos.getFareFactor());
            }
            Data.autoData.setDistanceUnit(autos.getDistanceUnit());
            Data.autoData.setCurrency(autos.getCurrency());

            Data.autoData.setDriverFareFactor(1);
            if(autos.getDriverFareFactor() != null) {
                Data.autoData.setDriverFareFactor(autos.getDriverFareFactor());
            }

			Data.autoData.setCampaigns(autos.getCampaigns());

			if(autos.getCityId() != null){
				Data.userData.setCurrentCity(autos.getCityId());
			}

            if (autos.getFarAwayCity() == null) {
				Data.autoData.setFarAwayCity("");
			} else {
				Data.autoData.setFarAwayCity(autos.getFarAwayCity());
			}

            if(autos.getBottomRequestUIEnabled() != null) {
				Data.autoData.setNewBottomRequestUIEnabled(autos.getBottomRequestUIEnabled());
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
        try{
            if(Data.autoData.getRegions() == null){
                Data.autoData.setRegions(new ArrayList<Region>());
            } else{
                Data.autoData.clearRegions();
            }
            if(autos.getRegions() != null) {
                double minRegionFare = autos.getRegions().size() > 0 && autos.getRegions().get(0).getRegionFare() != null ? autos.getRegions().get(0).getRegionFare().getFare() : 20.0,
                        maxRegionFare = autos.getRegions().size() > 0 && autos.getRegions().get(0).getRegionFare() != null ? autos.getRegions().get(0).getRegionFare().getFare() : 5000.0;
                HomeUtil homeUtil = new HomeUtil();
                for (Region region : autos.getRegions()) {
                    region.setVehicleIconSet(homeUtil.getVehicleIconSet(region.getIconSet()));
                    region.setIsDefault(false);
					if(region.isRegionAccGender(context, Data.userData)) {
						Data.autoData.addRegion(region);
					}
                    if(region.getRegionFare() != null && region.getRegionFare().getFare() < minRegionFare) {
                        minRegionFare = region.getRegionFare().getFare();
                    }
                    if(region.getRegionFare() != null && region.getRegionFare().getFare() > maxRegionFare) {
                        maxRegionFare = region.getRegionFare().getFare();
                    }
                }
                Prefs.with(context).save(Constants.KEY_MIN_REGION_FARE, (float) (minRegionFare * 0.8));
                Prefs.with(context).save(Constants.KEY_MAX_REGION_FARE, (float) (maxRegionFare * 10));
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
								fareStructure.getDisplayFareText(), fareStructure.getOperatorId(), autos.getCurrency(),
                                autos.getDistanceUnit());
						ArrayList<Region> regions = Data.autoData.getRegions();
						for (int i = 0; i < regions.size(); i++) {
							try {
								if (regions.get(i).getOperatorId() == fareStructure.getOperatorId()
                                        && regions.get(i).getVehicleType().equals(fareStructure.getVehicleType())
										&& regions.get(i).getRideType().equals(fareStructure.getRideType())
										) {
									regions.get(i).setFareStructure(fareStructure1);
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

        try{
            if(autos.getPointsOfInterestAddresses() != null){
                Data.userData.getPointsOfInterestAddresses().clear();
                Data.userData.getPointsOfInterestAddresses().addAll(autos.getPointsOfInterestAddresses());
            }
        } catch (Exception e){}
    }

    public static product.clicklabs.jugnoo.datastructure.FareStructure getDefaultFareStructure(){
        return new product.clicklabs.jugnoo.datastructure.FareStructure(10, 0, 3, 1, 0, 0, 0, 0, false, null, null, 0, null, null);
    }

    public static product.clicklabs.jugnoo.datastructure.FareStructure getFareStructure(){
        if(Data.autoData == null || Data.autoData.getFareStructure() == null) {
            return getDefaultFareStructure();
        } else{
            return Data.autoData.getFareStructure();
        }
    }

    public ReferralMessages parseReferralMessages(Context context, LoginResponse.UserData userData) {
        String referralSharingMessage = "Hey, \nUse "+context.getString(R.string.app_name)+" app to request ride at your doorsteps. It is cheap, convenient and zero haggling." +
                " Use this referral code: " + Data.userData.referralCode + " to earn benefits" +
                "\nDownload it from here: ";
        String fbShareCaption = "Use " + Data.userData.referralCode + " as referral code & earn benefits";
        String fbShareDescription = "Try "+context.getString(R.string.app_name)+" app to request ride at your doorsteps with just a tap.";
        String referralCaption = "<center><font face=\"verdana\" size=\"2\">Invite friends and<br/>get earn benefits</font></center>";
        String referralEmailSubject = "Hey! Have you used "+context.getString(R.string.app_name)+" yet?";
        String referralShortMessage = "", referralMoreInfoMessage = "";
        String title = context.getString(R.string.app_name);

        try {
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

        ReferralMessages referralMessages = new ReferralMessages(referralSharingMessage, fbShareCaption, fbShareDescription,
            referralEmailSubject, referralShortMessage, referralMoreInfoMessage, title);

        return referralMessages;
    }


    public void parseLastRideData(Context context, JSONObject jObj) {
        try {
            JSONObject jLastRideData = jObj.getJSONObject("last_ride");
            Data.autoData.setcSessionId("");
            Data.autoData.setNoDriverFoundTip(0.0);
            Data.autoData.setcEngagementId(jLastRideData.getString("engagement_id"));

            JSONObject jDriverInfo = jLastRideData.getJSONObject("driver_info");
            Data.autoData.setcDriverId(jDriverInfo.getString("id"));

            Data.autoData.setPickupSearchResult(null);
            Data.autoData.setDropLatLng(null);
            Data.autoData.setDropAddress("");

            Data.autoData.setAssignedDriverInfo(new DriverInfo(Data.autoData.getcDriverId(), jDriverInfo.getString("name"), jDriverInfo.getString("user_image"),
                    jDriverInfo.getString("driver_car_image"), jDriverInfo.getString("driver_car_no"),
                    jDriverInfo.optInt(KEY_OPERATOR_ID, 0)));
            int vehicleType = jLastRideData.optInt(KEY_VEHICLE_TYPE, VEHICLE_AUTO);
            String iconSet = jLastRideData.optString(KEY_ICON_SET, VehicleIconSet.ORANGE_AUTO.getName());
            Data.autoData.getAssignedDriverInfo().setVehicleType(vehicleType);
            Data.autoData.getAssignedDriverInfo().setVehicleIconSet(iconSet);

            try {
                int rideEndGoodFeedbackViewType = jLastRideData.optInt(KEY_RIDE_END_GOOD_FEEDBACK_VIEW_TYPE, Data.autoData.getRideEndGoodFeedbackViewType());
                Data.autoData.setRideEndGoodFeedbackViewType(rideEndGoodFeedbackViewType);
            } catch (Exception e) {
                e.printStackTrace();
            }

			Data.autoData.setEndRideData(parseEndRideData(jLastRideData, jLastRideData.getString("engagement_id"), Data.autoData.getFareStructure().getFixedFare()));

            HomeActivity.passengerScreenMode = PassengerScreenMode.P_RIDE_END;
            Prefs.with(context).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getAutosClientId());
            Prefs.with(context).save(Constants.KEY_EMERGENCY_NO, jLastRideData.optString(KEY_EMERGENCY_NO, context.getString(R.string.police_number)));

			//Driver FeedBack
			if (jObj.has(KEY_FEEDBACK_INFO)) {
				JSONArray jsonArray = jObj.getJSONArray(KEY_FEEDBACK_INFO);
				parseFeedBackInfo(jsonArray);
			}

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void parseFeedBackInfo(JSONArray jsonArray){
        ArrayList<FeedBackInfo.ImageBadges> imageBadges;
        ArrayList<FeedbackReason> textBadges;
       if(jsonArray.length()>0) {
           for (int i = 0; i < jsonArray.length(); i++) {
               imageBadges=new ArrayList<>();
               textBadges=new ArrayList<>();
               JSONObject jObj = jsonArray.optJSONObject(i);
               if(jObj.has("image_badges")){
                   JSONArray imgArr=jObj.optJSONArray("image_badges");
                   for(int j=0;j<imgArr.length();j++){
                       JSONObject imageBadge=imgArr.optJSONObject(j);
                       imageBadges.add( new FeedBackInfo.ImageBadges(imageBadge.optString("name"),imageBadge.optInt("badge_id"),imageBadge.optString("image"), imageBadge.optInt("can_comment", 0) == 1));
                   }
               }
               if(jObj.has("text_badges")){
                   JSONArray txtArr=jObj.optJSONArray("text_badges");
                   for(int j=0;j<txtArr.length();j++){
                       JSONObject textBadge=txtArr.optJSONObject(j);
                       textBadges.add( new FeedbackReason(textBadge.optString("name"),textBadge.optInt("badge_id"),textBadge.optInt("can_comment")==1));
                   }
               }
               Data.autoData.getFeedBackInfoRatingData().add(new FeedBackInfo(jObj.optInt("rating"),jObj.optString("desc"),imageBadges,textBadges));

           }
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
            if(additionalChargesJson != null) {
				for (int i = 0; i < additionalChargesJson.length(); i++) {
					JSONObject obj = additionalChargesJson.getJSONObject(i);
					DiscountType discountType = new DiscountType(obj.optString("text"), obj.optDouble("amount"), obj.optInt("reference_id"));
					if (discountType.value > 0) {
						discountTypes.add(discountType);
						sumAdditionalCharges = sumAdditionalCharges + discountType.value;
					}
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

        String driverUpiId = jLastRideData.optString(KEY_DRIVER_UPI, "");

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
        int operatorId = jLastRideData.optInt(KEY_OPERATOR_ID, 0);
        String iconSet = jLastRideData.optString(KEY_ICON_SET, VehicleIconSet.ORANGE_AUTO.getName());
        String iconUrl = jLastRideData.optString(Constants.KEY_INVOICE_ICON);
        String engagementDate = jLastRideData.optString("engagement_date", "");

        double paidUsingMobikwik = jLastRideData.optDouble(KEY_PAID_USING_MOBIKWIK, 0);
        double paidUsingMpesa = jLastRideData.optDouble(KEY_PAID_USING_MPESA, 0);
        double paidUsingFreeCharge = jLastRideData.optDouble(KEY_PAID_USING_FREECHARGE, 0);
        double paidUsingRazorpay = jLastRideData.optDouble(KEY_PAID_USING_RAZORPAY, 0);
        double paidUsingStripeCard = jLastRideData.optDouble(KEY_PAID_USING_STRIPE, 0);
        String last_4 = jLastRideData.optString(KEY_LAST_4, null);
        double netCustomerTax = jLastRideData.optDouble(KEY_NET_CUSTOMER_TAX, 0);
        double taxPercentage = jLastRideData.optDouble(KEY_TAX_PERCENTAGE, 0);

        int totalRide = jLastRideData.optInt(Constants.KEY_TOTAL_RIDES_AS_USER, 0);
        int status = jLastRideData.optInt(Constants.KEY_STATUS, EngagementStatus.ENDED.getOrdinal());
        String currency = jLastRideData.optString(Constants.KEY_CURRENCY);
        String distanceUnit = jLastRideData.optString(Constants.KEY_DISTANCE_UNIT);

        String supportNumber = jLastRideData.optString(KEY_SUPPORT_NUMBER, "");

        FuguChannelData fuguChannelData = new FuguChannelData();
        parseFuguChannelDetails(jLastRideData, fuguChannelData);
        int showPaymentOptions = jLastRideData.optInt(Constants.KEY_SHOW_PAYMENT_OPTIONS, 0);
        int paymentOption = jLastRideData.optInt(Constants.KEY_PREFERRED_PAYMENT_MODE, PaymentOption.CASH.getOrdinal());
        double tollCharge = jLastRideData.optDouble(Constants.KEY_TOLL_CHARGE, 0.0);
        double driverTipAmount = jLastRideData.optDouble(Constants.KEY_TIP_AMOUNT, 0.0);
        double luggageChargesNew = jLastRideData.optDouble(Constants.KEY_LUGGAGE_CHARGES, 0.0);
        int reverseBid = jLastRideData.optInt(Constants.KEY_REVERSE_BID, 0);
        int isCorporateRide = jLastRideData.optInt(Constants.KEY_IS_CORPORATE_RIDE, 0);
        String partnerName = jLastRideData.optString(Constants.KEY_PARTNER_NAME, "");
        int showTipOption = jLastRideData.optInt(Constants.KEY_SHOW_TIP_OPTION, 1);
        double paidUsingPOS = jLastRideData.optDouble(Constants.KEY_PAID_USING_POS, 0);
		int meterFareApplicable = jLastRideData.optInt(Constants.KEY_METER_FARE_APPLICABLE, 0);

		int driverId = 0;
		try {
			driverId = jLastRideData.optInt(KEY_DRIVER_ID,
					!TextUtils.isEmpty(Data.autoData.getcDriverId()) ? Integer.parseInt(Data.autoData.getcDriverId()) : 0);
			if(jLastRideData.has(KEY_DRIVER_INFO)){
				JSONObject jDriverInfo = jLastRideData.optJSONObject(KEY_DRIVER_INFO);
				driverId = jDriverInfo.optInt(KEY_ID);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		JSONArray jCardDetails =  jLastRideData.optJSONArray(Constants.KEY_CARD_DETAILS);
        ArrayList<DiscountType> stripeCardsAmount = new ArrayList<>();
        if(jCardDetails != null) {
            for (int i = 0; i < jCardDetails.length(); i++) {
                DiscountType discountType = new DiscountType(WalletCore.getStripeCardDisplayString(MyApplication.getInstance(),
                        jCardDetails.getJSONObject(i).getString(Constants.KEY_LAST_4)),
                        jCardDetails.getJSONObject(i).getDouble(Constants.KEY_AMOUNT_PAID),
                        jCardDetails.getJSONObject(i).getInt(Constants.KEY_ID));
                if (discountType.value > 0) {
                    int index = stripeCardsAmount.indexOf(discountType);
                    if(index > -1){
                        stripeCardsAmount.get(index).setValue(stripeCardsAmount.get(index).getValue() + discountType.value);
                    } else {
                        stripeCardsAmount.add(discountType);
                    }
                }
            }
        }

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
                sumAdditionalCharges, engagementDate, paidUsingMobikwik, paidUsingFreeCharge,paidUsingMpesa,paidUsingRazorpay,
                paidUsingStripeCard, last_4, totalRide, status, supportNumber
                ,jLastRideData.optString("invoice_additional_text_cabs", ""),
                fuguChannelData.getFuguChannelId(), fuguChannelData.getFuguChannelName(), fuguChannelData.getFuguTags(),
                showPaymentOptions, paymentOption, operatorId, currency, distanceUnit, iconUrl, tollCharge,
                driverTipAmount, luggageChargesNew,netCustomerTax,taxPercentage, reverseBid, isCorporateRide,
                partnerName, showTipOption, paidUsingPOS, stripeCardsAmount, meterFareApplicable, driverId, driverUpiId);
	}


	public static void parseFuguChannelDetails(JSONObject jLastRideData, FuguChannelData fuguChannelData) {
        if(jLastRideData.has(KEY_FUGU_CHANNEL_ID) && fuguChannelData != null){
            fuguChannelData.setFuguChannelId(jLastRideData.optString(KEY_FUGU_CHANNEL_ID));
            fuguChannelData.setFuguChannelName(jLastRideData.optString(KEY_FUGU_CHANNEL_NAME));
            JSONArray jTags = jLastRideData.optJSONArray(KEY_FUGU_TAGS);
            ArrayList<String> fuguTags = new ArrayList<>();
            for(int i=0; i<jTags.length(); i++){
                try {
                    fuguTags.add(jTags.getString(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            fuguChannelData.setFuguTags(fuguTags);
        }
    }

    public String getUserStatus(Context context, String accessToken, int currentUserStatus, ApiFindADriver apiFindADriver,
                                LatLng latLng) {
        try {
            long startTime = System.currentTimeMillis();
            HashMap<String, String> nameValuePairs = new HashMap<>();
            nameValuePairs.put(KEY_ACCESS_TOKEN, accessToken);
            nameValuePairs.put(KEY_LATITUDE, String.valueOf(latLng.latitude));
            nameValuePairs.put(KEY_LONGITUDE, String.valueOf(latLng.longitude));
            if(Data.userData.getSubscriptionData().getUserSubscriptions() != null && Data.userData.getSubscriptionData().getUserSubscriptions().size() > 0) {
                nameValuePairs.put(Constants.KEY_AUTOS_BENEFIT_ID, String.valueOf(Data.userData.getSubscriptionData().getUserSubscriptions().get(0).getBenefitIdAutos()));
            }
            new HomeUtil().putDefaultParams(nameValuePairs);
            long apiTime = System.currentTimeMillis();
            Response response = RestClient.getApiService().getCurrentUserStatus(nameValuePairs);
            apiTime = System.currentTimeMillis() - apiTime;
            String responseStr = new String(((TypedByteArray)response.getBody()).getBytes());
            if (response == null || responseStr == null) {
                return Constants.SERVER_TIMEOUT;
            } else {
                JSONObject jObject1 = new JSONObject(responseStr);
                if(apiTime > 2000
                        && Data.autoData != null && !"".equalsIgnoreCase(Data.autoData.getcSessionId())
                        && HomeActivity.passengerScreenMode != null && HomeActivity.passengerScreenMode == PassengerScreenMode.P_ASSIGNING
                        && jObject1.optInt(Constants.KEY_FLAG, ApiResponseFlags.ASSIGNING_DRIVERS.getOrdinal()) == ApiResponseFlags.NO_ACTIVE_SESSION.getOrdinal()){
                    Log.w(TAG, "special case of state restore api lagging");
                    return Constants.REJECT_API;
                }
                String resp = parseCurrentUserStatus(context, currentUserStatus, jObject1);

                if(PassengerScreenMode.P_INITIAL == HomeActivity.passengerScreenMode
                        || PassengerScreenMode.P_RIDE_END == HomeActivity.passengerScreenMode) {
                    Gson gson = new Gson();
                    FindADriverResponse findADriverResponse = gson.fromJson(responseStr, FindADriverResponse.class);
                    if(Data.autoData != null){
                        findADriverResponse.setIsRazorpayEnabled(Data.autoData.getIsRazorpayEnabled());
                    }
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
        String returnResponse = "";

        if (currentUserStatus == 2) {

            String screenMode = "";

            int engagementStatus = -1;
            String engagementId = "", sessionId = "", userId = "", latitude = "", longitude = "",
                    driverName = "", driverImage = "", driverCarImage = "", driverPhone = "", driverRating = "", driverCarNumber = "",
                    pickupLatitude = "", pickupLongitude = "", pickupAddress = "", dropAddress = "";
            int freeRide = 0, preferredPaymentMode = PaymentOption.CASH.getOrdinal();
			String promoName = "", eta = "";
            double fareFactor = 1.0, dropLatitude = 0, dropLongitude = 0, fareFixed = 0, bearing = 0.0, tip = 0.0;
            Schedule scheduleT20 = null;
            int vehicleType = VEHICLE_AUTO, regionId;
            String iconSet = VehicleIconSet.ORANGE_AUTO.getName();
            String cancelRideThrashHoldTime = "", poolStatusString = "";
            int cancellationCharges = 0, isPooledRide = 0, chatEnabled = 0;
            long cancellationTimeOffset = 0;
            ArrayList<String> fellowRiders = new ArrayList<>();
            PlaceOrderResponse.ReferralPopupContent referralPopupContent = null;
            FuguChannelData fuguChannelData = new FuguChannelData();
            int operatorId = 0;
            String currency = null;
            ArrayList<BidInfo> bidInfos = new ArrayList<>();
            String vehicleIconUrl = null;
            Double tipAmount  = null;
            int isCorporateRide = 0;
            String cardId = "0";
            int rideType = RideTypeValue.NORMAL.getOrdinal();
            int gpsLockStatus = GpsLockStatus.UNLOCK.getOrdinal();
            int fareMandatory = 0;
            double tipBeforeRequestRide = 0.0;
            String userIdentifier = "";

            String vehicleImage ="";
            String vehicleName = "";
            JSONArray multidest=null;

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
                    Data.userData.setFreshEnabled(jObject1.optInt("fresh_enabled", Data.userData.getFreshEnabled()));
                    Data.userData.setMealsEnabled(jObject1.optInt("meals_enabled", Data.userData.getMealsEnabled()));
                    Data.userData.setDeliveryEnabled(jObject1.optInt("delivery_enabled", Data.userData.getDeliveryEnabled()));
                    parseFuguChannelDetails(jObject1, fuguChannelData);

                    if (ApiResponseFlags.ASSIGNING_DRIVERS.getOrdinal() == flag) {

                        sessionId = jObject1.getString("session_id");
                        tip = jObject1.optDouble("tip_amount", 0.0);
                        regionId = jObject1.optInt("region_id", -1);
                        Prefs.with(context).save(KEY_REGION_ID, regionId);
                        double assigningLatitude = 0, assigningLongitude = 0;
                        if (jObject1.has(KEY_LATITUDE) && jObject1.has(KEY_LONGITUDE)) {
                            assigningLatitude = jObject1.getDouble(KEY_LATITUDE);
                            assigningLongitude = jObject1.getDouble(KEY_LONGITUDE);
                            Log.e("assigningLatitude,assigningLongitude ====@@@", "" + assigningLatitude + "," + assigningLongitude);
                        }

                        LatLng assigningLatLng = new LatLng(assigningLatitude, assigningLongitude);
                        Data.autoData.setPickupSearchResult(jObject1.optString(KEY_PICKUP_LOCATION_ADDRESS, ""), assigningLatLng);
                        Log.w("pickuplogging", "state restore assigning"+assigningLatLng);
                        parseDropLatLng(jObject1);

                        bidInfos = JSONParser.parseBids(context, Constants.KEY_BIDS, jObject1);
                        Data.autoData.setIsReverseBid(jObject1.optInt(Constants.KEY_REVERSE_BID, 0));
                        Data.autoData.setInitialBidValue(jObject1.optDouble(Constants.KEY_INITIAL_BID_VALUE, 0D));
                        Prefs.with(context).save(KEY_REVERSE_BID, Data.autoData.getIsReverseBid());

						Prefs.with(context).save(KEY_REQUEST_RIDE_START_TIME,
								DateOperations.getMilliseconds(DateOperations.utcToLocalWithTZFallback(jObject1.optString(KEY_START_TIME,
										DateOperations.getCurrentTimeInUTC()))));

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
                            pickupAddress = jObject.optString(KEY_PICKUP_LOCATION_ADDRESS, "");
                            chatEnabled = jObject.optInt("chat_enabled", 0);
                            operatorId = jObject.optInt(KEY_OPERATOR_ID, 0);
                            currency = jObject.optString(KEY_CURRENCY);
                            vehicleIconUrl = jObject.optString(Constants.KEY_MARKER_ICON);
                            tipAmount= jObject.optDouble(Constants.KEY_TIP_AMOUNT);
                            isCorporateRide= jObject.optInt(Constants.KEY_IS_CORPORATE_RIDE, 0);
                            cardId= jObject.optString(Constants.KEY_CARD_ID, "0");
                            Prefs.with(context).save(Constants.KEY_EMERGENCY_NO, jObject.optString(KEY_EMERGENCY_NO, context.getString(R.string.police_number)));

							Data.autoData.setIsReverseBid(jObject.optInt(Constants.KEY_REVERSE_BID, 0));
							Prefs.with(context).save(KEY_REVERSE_BID, Data.autoData.getIsReverseBid());

                            try {
                                if(jObject.has(KEY_OP_DROP_LATITUDE) && jObject.has(KEY_OP_DROP_LONGITUDE)) {
                                    dropLatitude = jObject.getDouble(KEY_OP_DROP_LATITUDE);
                                    dropLongitude = jObject.getDouble(KEY_OP_DROP_LONGITUDE);
                                    dropAddress = jObject.optString(KEY_DROP_LOCATION_ADDRESS, "");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (jObject.has("driver_car_no")) {
                                driverCarNumber = jObject.getString("driver_car_no");
                            }
                            vehicleImage = jObject.optString("vehicle_image");
                            if (jObject.has("free_ride")) {
                                freeRide = jObject.getInt("free_ride");
                            }
                            if(jObject.has("vehicle_color")) {
                                vehicleName = vehicleName.concat(jObject.optString("vehicle_color") + " ");
                            }
                            if(jObject.has("vehicle_brand")) {
                                vehicleName = vehicleName.concat(jObject.optString("vehicle_brand") + " ");
                            }
                            if(jObject.has("vehicle_model")) {
                                vehicleName = vehicleName.concat(jObject.optString("vehicle_model"));
                            }

                            promoName = getPromoName(context, jObject);

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
							preferredPaymentMode = jObject.optInt(Constants.KEY_PREFERRED_PAYMENT_MODE, PaymentOption.CASH.getOrdinal());

                            scheduleT20 = parseT20Schedule(jObject);

                            vehicleType = jObject.optInt(KEY_VEHICLE_TYPE, VEHICLE_AUTO);
                            rideType = jObject.optInt(KEY_RIDE_TYPE, RideTypeValue.NORMAL.getOrdinal());
                            iconSet = jObject.optString(KEY_ICON_SET, VehicleIconSet.ORANGE_AUTO.getName());
                            gpsLockStatus = jObject.optInt(KEY_GPS_LOCK_STATUS,GpsLockStatus.UNLOCK.getOrdinal());
							fareMandatory = jObject.optInt(Constants.KEY_FARE_MANDATORY,0);
							tipBeforeRequestRide = jObject.optDouble(Constants.KEY_TIP_PROVIDED_BEFORE_RIDE_REQUEST, 0.0);
							userIdentifier = jObject.optString(Constants.KEY_DRIVER_IDENTIFIER, "");


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

                            try {
                                JSONObject jReferralPopupContent = jObject.optJSONObject(KEY_REFERRAL_POPUP_CONTENT);
                                referralPopupContent = new Gson().fromJson(jReferralPopupContent.toString(), PlaceOrderResponse.ReferralPopupContent.class);
                            } catch (Exception e){}


//                            HomeActivity.rentalInRideStatus = RentalRideStatus.ONGOING.getOrdinal();
//                            try
//                            {
//                                gpsLockStatus = jObject.getInt(KEY_GPS_LOCK_STATUS);
//                                if(gpsLockStatus == RentalRideStatus.END_RIDE_REQUESTED.getOrdinal())
//                                {
//                                   // Data.autoData.setRentalInRideStatus(RentalRideStatus.END_RIDE_REQUESTED.getOrdinal());
//                                      HomeActivity.rentalInRideStatus = RentalRideStatus.END_RIDE_REQUESTED.getOrdinal();

//                                }
//                                else if(gpsLockStatus == RentalRideStatus.ONGOING.getOrdinal())
//                                {
//                                 //   Data.autoData.setRentalInRideStatus(RentalRideStatus.ONGOING.getOrdinal());
//                                       HomeActivity.rentalInRideStatus = RentalRideStatus.ONGOING.getOrdinal();
//                                }
//
//                            }
//                            catch (Exception e){
//                                e.printStackTrace();
//                            }


                        }
                    } else if (ApiResponseFlags.LAST_RIDE.getOrdinal() == flag) {
                        parseLastRideData(context, jObject1);
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
                Data.autoData.setNoDriverFoundTip(tip);
                Data.autoData.setBidInfos(bidInfos);
                Prefs.with(context).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getAutosClientId());
                clearSPData(context);
            } else {

                Data.autoData.setcSessionId(sessionId);
                Data.autoData.setcEngagementId(engagementId);
                Data.autoData.setcDriverId(userId);

                Data.autoData.setPickupSearchResult(pickupAddress, new LatLng(Double.parseDouble(pickupLatitude), Double.parseDouble(pickupLongitude)));
                Log.w("pickuplogging", "state restore req final"+Data.autoData.getPickupLatLng());
                if((Utils.compareDouble(dropLatitude, 0) == 0) && (Utils.compareDouble(dropLongitude, 0) == 0)){
                    Data.autoData.setDropLatLng(null);
                    Data.autoData.setDropAddress("");
                } else{
                    Data.autoData.setDropLatLng(new LatLng(dropLatitude, dropLongitude));
                    Data.autoData.setDropAddress(dropAddress);
                }

                double dLatitude = Double.parseDouble(latitude);
                double dLongitude = Double.parseDouble(longitude);




                Data.autoData.setAssignedDriverInfo(new DriverInfo(context, userId, dLatitude, dLongitude, driverName,
                        driverImage, driverCarImage, driverPhone, driverRating, driverCarNumber, freeRide, promoName, eta,
                        fareFixed, preferredPaymentMode, scheduleT20, vehicleType, iconSet, cancelRideThrashHoldTime, cancellationCharges,
                        isPooledRide, poolStatusString, fellowRiders, bearing, chatEnabled, operatorId, currency, vehicleIconUrl,tipAmount,
                        isCorporateRide, cardId, rideType, gpsLockStatus, fareMandatory, tipBeforeRequestRide, userIdentifier,vehicleImage,vehicleName));

                Data.autoData.setFareFactor(fareFactor);
                Data.autoData.setReferralPopupContent(referralPopupContent);
                JSONObject lastEngInfo=jObject1.optJSONArray("last_engagement_info").optJSONObject(0);
                multidest=lastEngInfo.optJSONArray("multiple_destinations");
                if(multidest!=null&&multidest.length()!=0)
                {
                    Data.autoData.getMultiDestList().clear();
                    Data.autoData.getMultiDestList().addAll(Objects.requireNonNull(AutoData.parseMultiDestList(multidest)));
                }
                Data.autoData.setFuguChannelId(fuguChannelData.getFuguChannelId());
                Data.autoData.setFuguChannelName(fuguChannelData.getFuguChannelName());
                Data.autoData.setFuguTags(fuguChannelData.getFuguTags());

                Log.e("Data.autoData.getAssignedDriverInfo() on login", "=" + Data.autoData.getAssignedDriverInfo().latLng);


                if (Data.P_REQUEST_FINAL.equalsIgnoreCase(screenMode)) {
                    HomeActivity.passengerScreenMode = PassengerScreenMode.P_REQUEST_FINAL;
                    Prefs.with(context).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getAutosClientId());
                }
                else if (Data.P_DRIVER_ARRIVED.equalsIgnoreCase(screenMode)) {
                    HomeActivity.passengerScreenMode = PassengerScreenMode.P_DRIVER_ARRIVED;
                    Prefs.with(context).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getAutosClientId());
                }
                else if (Data.P_IN_RIDE.equalsIgnoreCase(screenMode)) {
                    HomeActivity.passengerScreenMode = PassengerScreenMode.P_IN_RIDE;
                    Prefs.with(context).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getAutosClientId());
                }
                if(rideType == RideTypeValue.BIKE_RENTAL.getOrdinal()){
                    HomeActivity.passengerScreenMode = PassengerScreenMode.P_IN_RIDE;
                }
            }
        }

        return returnResponse;
    }


    public static String getPromoName(Context context, JSONObject jObject) {
        String promoName = context.getString(R.string.no_promo_code_applied);
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
                promoName = context.getString(R.string.no_promo_code_applied);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return promoName;
    }


    public void clearSPData(final Context context) {
        MyApplication.getInstance().getDatabase().deleteSavedPath();
    }


    public static void parseDriversToShow(List<Driver> drivers) {
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
                    String brandingStatus = driver.getBrandingStatus();
                    Data.autoData.getDriverInfos().add(new DriverInfo(userId, latitude, longitude, userName, userImage, driverCarImage,
                            phoneNo, rating, carNumber, 0, bearing, vehicleType, (ArrayList<Integer>)driver.getRegionIds(),
                            brandingStatus, driver.getOperatorId(), driver.getPaymentMethod(),driver.getDeviceToken(),driver.getExternalId()));
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
                    "will lead to cancellation charges of \u20B9 20", ""));

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






    public static ArrayList<EmergencyContact> parseEmergencyContacts(JSONObject jObj){
        ArrayList<EmergencyContact> emergencyContactsList = new ArrayList<>();
        try{
            JSONArray jEmergencyContactsArr = jObj.getJSONArray(KEY_EMERGENCY_CONTACTS);

            for(int i=0; i<jEmergencyContactsArr.length(); i++){
                JSONObject jECont = jEmergencyContactsArr.getJSONObject(i);
                emergencyContactsList.add(new EmergencyContact(jECont.getInt(KEY_ID),
                        jECont.getString(KEY_NAME),
                        jECont.getString(KEY_PHONE_NO),jECont.getString(KEY_COUNTRY_CODE)));
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
                    Data.autoData.setDropAddress("");
                } else{
                    Data.autoData.setDropLatLng(new LatLng(dropLatitude, dropLongitude));
                    Data.autoData.setDropAddress(jObject1.optString(KEY_DROP_LOCATION_ADDRESS, Data.autoData.getDropAddress()));
                }
			} else{
                Data.autoData.setDropLatLng(null);
                Data.autoData.setDropAddress("");
			}
        } catch (Exception e) {
            e.printStackTrace();
            Data.autoData.setDropLatLng(null);
            Data.autoData.setDropAddress("");
        }
    }


    public static RateAppDialogContent parseRateAppDialogContent(Context context, JSONObject jObj){
        try{
            JSONObject jRA = jObj.getJSONObject(KEY_RATE_APP_DIALOG_CONTENT);
            return new RateAppDialogContent(jRA.getString(KEY_TITLE),
                    jRA.getString(KEY_TEXT),
                    jRA.getString(KEY_CONFIRM_BUTTON_TEXT),
                    jRA.getString(KEY_CANCEL_BUTTON_TEXT),
                    jRA.getString(KEY_NEVER_BUTTON_TEXT),
                    jRA.getString(KEY_URL));
        } catch(Exception e){
            e.printStackTrace();
            return new RateAppDialogContent("Glad you liked our services",
                    "Do you find "+context.getString(R.string.app_name)+" useful?\nIf yes, we would appreciate if you could rate us on the Play Store",
                    "Rate Now",
                    "Not Now",
                    "Never Ask Again",
                    "https://play.google.com/store/apps/details?id="+BuildConfig.APPLICATION_ID) ;
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
            GAUtils.setGAUserId(Data.userData.getUserId());
            if(loginVia == LoginVia.EMAIL_OTP
                    || loginVia == LoginVia.FACEBOOK_OTP
                    || loginVia == LoginVia.GOOGLE_OTP) {
                String referralCodeEntered = Prefs.with(context).getString(SP_REFERRAL_CODE, "");
                Prefs.with(context).save(SP_REFERRAL_CODE, "");
                BranchMetricsUtils.logEvent(context, GAAction.REGISTRATION, false);
                FbEvents.logEvent(context, AppEventsConstants.EVENT_NAME_COMPLETED_REGISTRATION);

                String walletSelected = Prefs.with(context).getString(SP_WALLET_AT_SIGNUP, "NA");
                Prefs.with(context).save(SP_WALLET_AT_SIGNUP, "");


            }
            JSONObject map = new JSONObject();
            map.put(KEY_SOURCE, getAppSource(context));

        } catch (Exception e) {
            e.printStackTrace();
        }


    }




    public static void parseRateAppFlagContent(Context context, JSONObject jsonObject){
        try {
            if (jsonObject.has(KEY_RATE_APP)) {
                Data.userData.setCustomerRateAppFlag(jsonObject.getInt(KEY_RATE_APP));
            }
            if(jsonObject.has(KEY_RATE_APP_DIALOG_CONTENT)){
                Data.userData.setRateAppDialogContent(JSONParser.parseRateAppDialogContent(context,jsonObject));
            }
            if(Data.userData.getCustomerRateAppFlag() == 1){
                if(Data.autoData != null) {
                    Data.autoData.setRideEndGoodFeedbackViewType(RideEndGoodFeedbackViewType.RIDE_END_NONE.getOrdinal());
                }
                if(Data.getMealsData() != null) {
                    Data.getMealsData().setFeedbackViewType(RideEndGoodFeedbackViewType.RIDE_END_NONE.getOrdinal());
                }
                if(Data.getFreshData() != null) {
                    Data.getFreshData().setFeedbackViewType(RideEndGoodFeedbackViewType.RIDE_END_NONE.getOrdinal());
                }
                if(Data.getGroceryData() != null) {
                    Data.getGroceryData().setFeedbackViewType(RideEndGoodFeedbackViewType.RIDE_END_NONE.getOrdinal());
                }
                if(Data.getMenusData() != null) {
                    Data.getMenusData().setFeedbackViewType(RideEndGoodFeedbackViewType.RIDE_END_NONE.getOrdinal());
                }
                if(Data.getDeliveryCustomerData() != null) {
                    Data.getDeliveryCustomerData().setFeedbackViewType(RideEndGoodFeedbackViewType.RIDE_END_NONE.getOrdinal());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String getSearchResultStringFromJSON(JSONObject jsonObject){
        try {
            JSONObject json = new JSONObject();
            json.put(KEY_ADDRESS, jsonObject.optString(KEY_ADDRESS, ""));
            json.put(KEY_NAME, jsonObject.optString(KEY_TYPE, ""));
            json.put(KEY_PLACEID, jsonObject.optString(KEY_GOOGLE_PLACE_ID, ""));
            json.put(KEY_LATITUDE, jsonObject.optDouble(KEY_LATITUDE, 0));
            json.put(KEY_LONGITUDE, jsonObject.optDouble(KEY_LONGITUDE, 0));
            json.put(KEY_ID, jsonObject.optInt(KEY_ID, 0));
            json.put(KEY_IS_CONFIRMED, jsonObject.optInt(KEY_IS_CONFIRMED, 0));
            json.put(KEY_FREQ, jsonObject.optInt(KEY_FREQ, 0));
            return json.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return EMPTY_JSON_OBJECT;
        }
    }

    public void parseSavedAddresses(Context context, JSONObject userData, String arrayKey){
        try {
            if(userData.has(arrayKey)){
                Data.userData.getSearchResults().clear();
                Prefs.with(context).save(SPLabels.ADD_HOME, "");
                Prefs.with(context).save(SPLabels.ADD_WORK, "");
				JSONArray userSavedAddressArray = userData.getJSONArray(arrayKey);
				boolean homeSaved = false, workSaved = false;
				Gson gson = new Gson();
				for(int i=0; i<userSavedAddressArray.length(); i++){
					JSONObject jsonObject = userSavedAddressArray.getJSONObject(i);
					if(jsonObject.optString(KEY_TYPE).equalsIgnoreCase(TYPE_HOME) && !homeSaved){
						if(!jsonObject.optString(KEY_ADDRESS).equalsIgnoreCase("")){
							Prefs.with(context).save(SPLabels.ADD_HOME, getSearchResultStringFromJSON(jsonObject));
						}else {
							Prefs.with(context).save(SPLabels.ADD_HOME, "");
						}
						homeSaved = true;
					} else if(jsonObject.optString(KEY_TYPE).equalsIgnoreCase(TYPE_WORK) && !workSaved){
						if(!jsonObject.optString(KEY_ADDRESS).equalsIgnoreCase("")){
							Prefs.with(context).save(SPLabels.ADD_WORK, getSearchResultStringFromJSON(jsonObject));
						}else {
							Prefs.with(context).save(SPLabels.ADD_WORK, "");
						}
						workSaved = true;
					} else if(!TextUtils.isEmpty(jsonObject.optString(KEY_TYPE)) && !jsonObject.optString(KEY_ADDRESS).equalsIgnoreCase("")) {
						Data.userData.getSearchResults().add(gson.fromJson(getSearchResultStringFromJSON(jsonObject), SearchResult.class));
					} else if (TextUtils.isEmpty(jsonObject.optString(KEY_TYPE)) && !jsonObject.optString(KEY_ADDRESS).equalsIgnoreCase("")) {
						SearchResult searchResult = gson.fromJson(getSearchResultStringFromJSON(jsonObject), SearchResult.class);
						searchResult.setType(SearchResult.Type.RECENT);
						Data.userData.getSearchResultsRecent().add(searchResult);
					}
				}
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void parseSavedAddressesFromNew(Context context, FetchUserAddressResponse addressResponse){
        try {
            if(addressResponse.getAddresses() != null) {
                Data.userData.getSearchResults().clear();
                Data.userData.getSearchResultsRecent().clear();
                Data.userData.getSearchResultsAdditional().clear();
                Prefs.with(context).save(SPLabels.ADD_HOME, "");
                Prefs.with(context).save(SPLabels.ADD_WORK, "");
                boolean homeSaved = false, workSaved = false;
                Gson gson = new Gson();

                // Fresh last selected address fetched from SP to check if that address is deleted/updated by user recently
                // hashing searchResult object matched with Fresh selected address in a variable
                SearchResult searchResultLastFMM = gson.fromJson(Prefs.with(context)
                        .getString(Constants.SP_FRESH_LAST_ADDRESS_OBJ, Constants.EMPTY_JSON_OBJECT), SearchResult.class);
                SearchResult searchResultMatched = null;
                int otherLocationCount = 0;
                for (int i = 0; i < addressResponse.getAddresses().size(); i++) {
                    FetchUserAddressResponse.Address address = addressResponse.getAddresses().get(i);
                    SearchResult searchResult = new SearchResult(address.getType(), address.getAddr(), address.getPlaceId(),
                            address.getLat(), address.getLng(), address.getId(), address.getIsConfirmed(), address.getFreq());

                    if (address.getType().equalsIgnoreCase(TYPE_HOME) && !homeSaved) {
                        if (!TextUtils.isEmpty(searchResult.getAddress())) {
                            Prefs.with(context).save(SPLabels.ADD_HOME, gson.toJson(searchResult, SearchResult.class));
                        } else {
                            Prefs.with(context).save(SPLabels.ADD_HOME, "");
                        }
                        homeSaved = true;
                    } else if (address.getType().equalsIgnoreCase(TYPE_WORK) && !workSaved) {
                        if (!TextUtils.isEmpty(searchResult.getAddress())) {
                            Prefs.with(context).save(SPLabels.ADD_WORK, gson.toJson(searchResult, SearchResult.class));
                        } else {
                            Prefs.with(context).save(SPLabels.ADD_WORK, "");
                        }
                        workSaved = true;
                    } else if (!TextUtils.isEmpty(searchResult.getAddress())
                            && !TextUtils.isEmpty(address.getType())
                            && address.getId() > 0) {
                        if(address.getType().startsWith("Other")) {
                            otherLocationCount += 1;
                            searchResult.setName(context.getString(R.string.favourite,  String.valueOf(otherLocationCount)));
                        }
                        Data.userData.getSearchResults().add(searchResult);

                    } else if (!TextUtils.isEmpty(searchResult.getAddress())
                            && TextUtils.isEmpty(address.getType())) {
                        searchResult.setType(SearchResult.Type.RECENT);
                        Data.userData.getSearchResultsRecent().add(searchResult);
                    }

                    // to check if Fresh last selected address has valid id and to match with every address from server
                    // then hashing that searchresult object
                    if(searchResultLastFMM.getId() != null && searchResultLastFMM.getId() > 0){
                        if((searchResult.getId() != null && searchResult.getId() > 0
                                && searchResult.getId().equals(searchResultLastFMM.getId()))){
                            searchResultMatched = searchResult;
                        }
                    }
                }

                // check if Fresh selected address has some valid id
                if(searchResultLastFMM.getId() != null && searchResultLastFMM.getId() > 0){
                    // if Fresh last selected address is not matched with server's list of updated addresses
                    // set Fresh selected address id to -10 and saving back for marking this case to update
                    // address related local variables of FreshActivity
                    if(searchResultMatched == null) {
                        searchResultLastFMM.setId(-10);
                        searchResultLastFMM.setName("");
                    }
                    // else if matched update Fresh selected address by server's updated address object
                    else {
                        searchResultLastFMM = searchResultMatched;
                    }
                    Prefs.with(context).save(Constants.SP_FRESH_LAST_ADDRESS_OBJ, gson.toJson(searchResultLastFMM, SearchResult.class));
                }

                if(Data.autoData.getUseRecentLocAtRequest() == 1) {
                    for (FetchUserAddressResponse.Address address : addressResponse.getAdditionalAddresses()) {
                        SearchResult searchResult = new SearchResult("", address.getAddr(), address.getPlaceId(),
                                address.getLat(), address.getLng(), address.getId(), address.getIsConfirmed(), address.getFreq());
                        searchResult.setType(SearchResult.Type.RECENT);
                        Data.userData.getSearchResultsAdditional().add(searchResult);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resetMenusFilters(Context context) {
        Prefs.with(context).save(Constants.SP_MENUS_FILTER_SORT_BY_OBJ, "");
        Prefs.with(context).save(Constants.SP_MENUS_FILTER_CUISINES_GSON, "");
        Prefs.with(context).save(Constants.SP_MENUS_FILTER_QUICK_OBJ, "");

        Prefs.with(context).save(Constants.SP_DELIVERY_CUSTOMER_FILTER_SORT_BY_OBJ, "");
        Prefs.with(context).save(Constants.SP_DELIVERY_CUSTOMER_FILTER_CUISINES_GSON, "");
        Prefs.with(context).save(Constants.SP_DELIVERY_CUSTOMER_FILTER_QUICK_OBJ, "");
    }


    private void resetUseCouponSP(Context context) {
        Prefs.with(context).save(Constants.SP_USE_COUPON_ + Config.getAutosClientId(), -1);
        Prefs.with(context).save(Constants.SP_USE_COUPON_IS_COUPON_ + Config.getAutosClientId(), false);

        Prefs.with(context).save(Constants.SP_USE_COUPON_ + Config.getFreshClientId(), -1);
        Prefs.with(context).save(Constants.SP_USE_COUPON_IS_COUPON_ + Config.getFreshClientId(), false);

        Prefs.with(context).save(Constants.SP_USE_COUPON_ + Config.getMealsClientId(), -1);
        Prefs.with(context).save(Constants.SP_USE_COUPON_IS_COUPON_ + Config.getMealsClientId(), false);

        Prefs.with(context).save(Constants.SP_USE_COUPON_ + Config.getMenusClientId(), -1);
        Prefs.with(context).save(Constants.SP_USE_COUPON_IS_COUPON_ + Config.getMenusClientId(), false);

        Prefs.with(context).save(Constants.SP_USE_COUPON_ + Config.getDeliveryCustomerClientId(), -1);
        Prefs.with(context).save(Constants.SP_USE_COUPON_IS_COUPON_ + Config.getDeliveryCustomerClientId(), false);
    }

    private void resetIsVegToggle(Context context){
        Prefs.with(context).save(KEY_SP_IS_VEG_TOGGLE, 0);
    }

    public static class FuguChannelData{
        private String fuguChannelId, fuguChannelName;
        private ArrayList<String> fuguTags;

        public String getFuguChannelId() {
            return fuguChannelId;
        }

        public void setFuguChannelId(String fuguChannelId) {
            this.fuguChannelId = fuguChannelId;
        }

        public String getFuguChannelName() {
            return fuguChannelName;
        }

        public void setFuguChannelName(String fuguChannelName) {
            this.fuguChannelName = fuguChannelName;
        }

        public ArrayList<String> getFuguTags() {
            return fuguTags;
        }

        public void setFuguTags(ArrayList<String> fuguTags) {
            this.fuguTags = fuguTags;
        }
    }

    public static ArrayList<BidInfo> parseBids(Context context, String arrayKeyName, JSONObject jsonObject){
        ArrayList<BidInfo> bidInfos = new ArrayList<>();
        try{
            if(jsonObject.has(arrayKeyName)){
                JSONArray jsonArray = jsonObject.getJSONArray(arrayKeyName);
                for(int i=0; i<jsonArray.length(); i++){
                    JSONObject object = jsonArray.getJSONObject(i);
                    bidInfos.add(new BidInfo(object.optInt(Constants.KEY_ENGAGEMENT_ID),
                            object.optDouble(Constants.KEY_BID_VALUE),
                            object.optString(Constants.KEY_CURRENCY),
                            object.optDouble(Constants.KEY_ACCEPT_DISTANCE),object.optString(Constants.KEY_ACCEPT_DISTANCE_TEXT),
                            object.optDouble(Constants.KEY_DRIVER_RATING),
                            object.optString(Constants.KEY_CREATED_AT, DateOperations.getCurrentTimeInUTC()),
                            object.optString(Constants.KEY_DRIVER_IMAGE),
                            object.optString(Constants.KEY_DRIVER_NAME),
                            object.optString(Constants.KEY_VEHICLE_NAME),
                            object.optString(Constants.KEY_DRIVER_ETA)
							));
                }
            }
        } catch (Exception ignored){
        }
        return bidInfos;
    }

    public static void parseSignupOnboardingKeys(Context context, JSONObject jObj){
        if(jObj.has(Constants.KEY_AUTOS)) {
            Prefs.with(context).save(Constants.KEY_SHOW_PROMO_ONBOARDING, jObj.optJSONObject(Constants.KEY_AUTOS).optInt(Constants.KEY_SHOW_PROMO_ONBOARDING, 1));
            Prefs.with(context).save(Constants.KEY_SHOW_SKIP_ONBOARDING, jObj.optJSONObject(Constants.KEY_AUTOS).optInt(Constants.KEY_SHOW_SKIP_ONBOARDING, 1));
        }
    }

}

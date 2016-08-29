package product.clicklabs.jugnoo.datastructure;

import android.content.Context;

import com.sabkuchfresh.utils.AppConstant;

import org.json.JSONObject;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.home.models.JeanieIntroDialogContent;
import product.clicklabs.jugnoo.home.models.MenuInfo;
import product.clicklabs.jugnoo.utils.Utils;
import product.clicklabs.jugnoo.wallet.models.PaymentModeConfigData;

public class UserData {
	public String userIdentifier, accessToken, authKey, userName, userEmail, userImage, referralCode, phoneNo, jugnooFbBanner;
	public int emailVerificationStatus;
	private double jugnooBalance;
	public int numCouponsAvaliable;
	public int showJugnooJeanie;
	private int promoSuccess;
	private String promoMessage;

	private String branchDesktopUrl, branchAndroidUrl, branchIosUrl, branchFallbackUrl;

	private String jugnooCashTNC;
	private String inAppSupportPanelVersion, userId;
	private int getGogu;
	private String inviteEarnScreenImage;

	private int t20WCEnable;
	private String t20WCScheduleVersion, t20WCInfoText;
	private String publicAccessToken;

	private int gamePredictEnable, cToDReferralEnabled;
	private String gamePredictUrl, gamePredictIconUrl, gamePredictName, gamePredictNew;

	private PaytmRechargeInfo paytmRechargeInfo = null;
	private String city, cityReg;

	private int referralLeaderboardEnabled, referralActivityEnabled;
	private String fatafatUrlLink;


	private int paytmEnabled;
	private double paytmBalance = -1;

	private int mobikwikEnabled, integratedJugnooEnabled;
	private double mobikwikBalance = -1;

	private int notificationPreferenceEnabled = 0, mealsEnabled, freshEnabled, deliveryEnabled, inviteFriendButton;

	private ArrayList<EmergencyContact> emergencyContactsList = new ArrayList<>();
	private int currentCity = 1;
	private ArrayList<PromoCoupon> promoCoupons = new ArrayList<>();
	private ArrayList<MenuInfo> menuInfoList = new ArrayList<>();

	private ReferralMessages referralMessages;

	private String defaultClientId;

	private JeanieIntroDialogContent jeanieIntroDialogContent;

	public UserData(String userIdentifier, String accessToken, String authKey, String userName, String userEmail, int emailVerificationStatus,
					String userImage, String referralCode, String phoneNo, double jugnooBalance,
					String jugnooFbBanner, int numCouponsAvaliable,
					int promoSuccess, String promoMessage,
					int showJugnooJeanie,
					String branchDesktopUrl, String branchAndroidUrl, String branchIosUrl, String branchFallbackUrl,
					String jugnooCashTNC, String inAppSupportPanelVersion, int getGogu,
					String userId, String inviteEarnScreenImage,
					int t20WCEnable, String t20WCScheduleVersion, String t20WCInfoText, String publicAccessToken,
					int gamePredictEnable, String gamePredictUrl, String gamePredictIconUrl, String gamePredictName, String gamePredictNew,
					int cToDReferralEnabled,
					String city, String cityReg, int referralLeaderboardEnabled, int referralActivityEnabled,
					String fatafatUrlLink,
					int paytmEnabled, int mobikwikEnabled, int notificationPreferenceEnabled,
					int mealsEnabled, int freshEnabled, int deliveryEnabled, int inviteFriendButton, String defaultClientId,
					int integratedJugnooEnabled){
        this.userIdentifier = userIdentifier;
		this.accessToken = accessToken;
		this.authKey = authKey;
		this.userName = userName;
		this.userEmail = userEmail;
		this.emailVerificationStatus = emailVerificationStatus;
		
		this.userImage = userImage;
		this.referralCode = referralCode;
		this.phoneNo = phoneNo;
		this.jugnooBalance = jugnooBalance;

		this.jugnooFbBanner = jugnooFbBanner;
		this.numCouponsAvaliable = numCouponsAvaliable;

		this.promoSuccess = promoSuccess;
		this.promoMessage = promoMessage;

		this.showJugnooJeanie = showJugnooJeanie;

		this.branchDesktopUrl = branchDesktopUrl;
		this.branchAndroidUrl = branchAndroidUrl;
		this.branchIosUrl = branchIosUrl;
		this.branchFallbackUrl = branchFallbackUrl;

		this.jugnooCashTNC = jugnooCashTNC;
		this.inAppSupportPanelVersion = inAppSupportPanelVersion;
		this.getGogu = getGogu;
		this.userId = userId;
		this.inviteEarnScreenImage = inviteEarnScreenImage;

		this.t20WCEnable = t20WCEnable;
		this.t20WCScheduleVersion = t20WCScheduleVersion;
		this.t20WCInfoText = t20WCInfoText;

		this.publicAccessToken = publicAccessToken;

		this.gamePredictEnable = gamePredictEnable;
		this.gamePredictUrl = gamePredictUrl;
		this.gamePredictIconUrl = gamePredictIconUrl;
		this.gamePredictName = gamePredictName;
		this.gamePredictNew = gamePredictNew;

		this.cToDReferralEnabled = cToDReferralEnabled;

		this.city = city;
		this.cityReg = cityReg;

		this.referralLeaderboardEnabled = referralLeaderboardEnabled;
		this.referralActivityEnabled = referralActivityEnabled;
		this.fatafatUrlLink = fatafatUrlLink;
		this.notificationPreferenceEnabled = notificationPreferenceEnabled;

		checkUserImage();

		this.paytmEnabled = paytmEnabled;
		this.mobikwikEnabled = mobikwikEnabled;
		this.mealsEnabled = mealsEnabled;
		this.freshEnabled = freshEnabled;
		this.deliveryEnabled = deliveryEnabled;

		this.inviteFriendButton = inviteFriendButton;

		this.defaultClientId = defaultClientId;
		this.integratedJugnooEnabled = integratedJugnooEnabled;
	}

	private void checkUserImage(){
		if(userImage == null
				|| userImage.contains("brand_images/user.png")){
			userImage = "";
		}
		userImage = userImage.replace("http://graph.facebook", "https://graph.facebook");
	}

	public double getJugnooBalance() {
		return jugnooBalance;
	}

	public void setJugnooBalance(double jugnooBalance) {
		this.jugnooBalance = jugnooBalance;
	}

	public double getPaytmBalance(){
		return paytmBalance;
	}

	public String getPaytmBalanceStr(){
		if(paytmEnabled != 1 || paytmBalance < 0){
			return "--";
		} else {
			return Utils.getMoneyDecimalFormatWithoutFloat().format(paytmBalance);
		}
	}

	public void setPaytmBalance(double paytmBalance) {
		this.paytmBalance = paytmBalance;
	}

	public double getTotalWalletBalance() {
		double walletTotal = 0;
		for(PaymentModeConfigData paymentModeConfigData : MyApplication.getInstance().getWalletCore().getPaymentModeConfigDatas(this)){
			if(paymentModeConfigData.getEnabled() == 1) {
				if (paymentModeConfigData.getPaymentOption() == PaymentOption.PAYTM.getOrdinal()
						&& paytmEnabled == 1 && paytmBalance > -1) {
					walletTotal = walletTotal + paytmBalance;
				} else if (paymentModeConfigData.getPaymentOption() == PaymentOption.MOBIKWIK.getOrdinal()
						&& mobikwikEnabled == 1 && mobikwikBalance > -1) {
					walletTotal = walletTotal + mobikwikBalance;
				}
			}
		}
		return jugnooBalance + walletTotal;
	}

	public int getPromoSuccess() {
		return promoSuccess;
	}

	public void setPromoSuccess(int promoSuccess) {
		this.promoSuccess = promoSuccess;
	}

	public void deletePaytm(){
		this.paytmEnabled = 0;
		this.paytmBalance = -1;
	}

	public String getBranchDesktopUrl() {
		return branchDesktopUrl;
	}

	public void setBranchDesktopUrl(String branchDesktopUrl) {
		this.branchDesktopUrl = branchDesktopUrl;
	}

	public String getBranchAndroidUrl() {
		return branchAndroidUrl;
	}

	public void setBranchAndroidUrl(String branchAndroidUrl) {
		this.branchAndroidUrl = branchAndroidUrl;
	}

	public String getBranchIosUrl() {
		return branchIosUrl;
	}

	public void setBranchIosUrl(String branchIosUrl) {
		this.branchIosUrl = branchIosUrl;
	}

	public String getBranchFallbackUrl() {
		return branchFallbackUrl;
	}

	public void setBranchFallbackUrl(String branchFallbackUrl) {
		this.branchFallbackUrl = branchFallbackUrl;
	}

	public String getJugnooCashTNC() {
		return jugnooCashTNC;
	}

	public void setJugnooCashTNC(String jugnooCashTNC) {
		this.jugnooCashTNC = jugnooCashTNC;
	}

	public String getPromoMessage() {
		return promoMessage;
	}

	public void setPromoMessage(String promoMessage) {
		this.promoMessage = promoMessage;
	}


	public int getPaytmBalanceColor(Context context){
		if(paytmBalance < 0){
			return context.getResources().getColor(R.color.theme_red_color);
		} else{
			return context.getResources().getColor(R.color.theme_green_color);
		}
	}

	public int getJugnooBalanceColor(Context context){
		if(jugnooBalance < 0){
			return context.getResources().getColor(R.color.theme_red_color);
		} else{
			return context.getResources().getColor(R.color.theme_green_color);
		}
	}



	public String getInAppSupportPanelVersion() {
		return inAppSupportPanelVersion;
	}

	public void setInAppSupportPanelVersion(String inAppSupportPanelVersion) {
		this.inAppSupportPanelVersion = inAppSupportPanelVersion;
	}

	public int getGetGogu() {
		return getGogu;
	}

	public void setGetGogu(int getGogu) {
		this.getGogu = getGogu;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getInviteEarnScreenImage() {
		return inviteEarnScreenImage;
	}

	public void setInviteEarnScreenImage(String inviteEarnScreenImage) {
		this.inviteEarnScreenImage = inviteEarnScreenImage;
	}

	public int getT20WCEnable() {
		return t20WCEnable;
	}

	public void setT20WCEnable(int t20WCEnable) {
		this.t20WCEnable = t20WCEnable;
	}

	public String getT20WCScheduleVersion() {
		return t20WCScheduleVersion;
	}

	public void setT20WCScheduleVersion(String t20WCScheduleVersion) {
		this.t20WCScheduleVersion = t20WCScheduleVersion;
	}

	public String getT20WCInfoText() {
		return t20WCInfoText;
	}

	public void setT20WCInfoText(String t20WCInfoText) {
		this.t20WCInfoText = t20WCInfoText;
	}

	public PaytmRechargeInfo getPaytmRechargeInfo() {
		return paytmRechargeInfo;
	}

	public void setPaytmRechargeInfo(PaytmRechargeInfo paytmRechargeInfo) {
		this.paytmRechargeInfo = paytmRechargeInfo;
	}

	public String getPublicAccessToken() {
		return publicAccessToken;
	}

	public void setPublicAccessToken(String publicAccessToken) {
		this.publicAccessToken = publicAccessToken;
	}

	public String getGamePredictUrl() {
		return gamePredictUrl;
	}

	public void setGamePredictUrl(String gamePredictUrl) {
		this.gamePredictUrl = gamePredictUrl;
	}

	public String getGamePredictIconUrl() {
		return gamePredictIconUrl;
	}

	public void setGamePredictIconUrl(String gamePredictIconUrl) {
		this.gamePredictIconUrl = gamePredictIconUrl;
	}

	public String getGamePredictName() {
		return gamePredictName;
	}

	public void setGamePredictName(String gamePredictName) {
		this.gamePredictName = gamePredictName;
	}

	public String getGamePredictNew() {
		return gamePredictNew;
	}

	public void setGamePredictNew(String gamePredictNew) {
		this.gamePredictNew = gamePredictNew;
	}

	public int getGamePredictEnable() {
		return gamePredictEnable;
	}

	public void setGamePredictEnable(int gamePredictEnable) {
		this.gamePredictEnable = gamePredictEnable;
	}

	public int getcToDReferralEnabled() {
		return cToDReferralEnabled;
	}

	public void setcToDReferralEnabled(int cToDReferralEnabled) {
		this.cToDReferralEnabled = cToDReferralEnabled;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCityReg() {
		return cityReg;
	}

	public void setCityReg(String cityReg) {
		this.cityReg = cityReg;
	}

	public int getReferralLeaderboardEnabled() {
		return referralLeaderboardEnabled;
	}

	public void setReferralLeaderboardEnabled(int referralLeaderboardEnabled) {
		this.referralLeaderboardEnabled = referralLeaderboardEnabled;
	}

	public int getReferralActivityEnabled() {
		return referralActivityEnabled;
	}

	public void setReferralActivityEnabled(int referralActivityEnabled) {
		this.referralActivityEnabled = referralActivityEnabled;
	}

	public String getFatafatUrlLink() {
		return fatafatUrlLink;
	}

	public void setFatafatUrlLink(String fatafatUrlLink) {
		this.fatafatUrlLink = fatafatUrlLink;
	}

	public int getMobikwikEnabled() {
		return mobikwikEnabled;
	}

	public void setMobikwikEnabled(int mobikwikEnabled) {
		this.mobikwikEnabled = mobikwikEnabled;
	}

	public double getMobikwikBalance() {
		return mobikwikBalance;
	}

	public String getMobikwikBalanceStr(){
		if(mobikwikEnabled != 1 || mobikwikBalance < 0){
			return "--";
		} else {
			return Utils.getMoneyDecimalFormatWithoutFloat().format(mobikwikBalance);
		}
	}

	public void setMobikwikBalance(double mobikwikBalance) {
		this.mobikwikBalance = mobikwikBalance;
	}

	public int getMobikwikBalanceColor(Context context){
		if(getMobikwikBalance() < 0){
			return context.getResources().getColor(R.color.theme_red_color);
		} else{
			return context.getResources().getColor(R.color.theme_green_color);
		}
	}

	public void deleteMobikwik(){
		this.mobikwikEnabled = 0;
		this.mobikwikBalance = -1;
	}


	public int getPaytmEnabled() {
		return paytmEnabled;
	}

	public void setPaytmEnabled(int paytmEnabled) {
		this.paytmEnabled = paytmEnabled;
	}

	public void updateWalletBalances(JSONObject jObj, boolean removeWalletIfNoKey){
		try {
			setJugnooBalance(jObj.optDouble(Constants.KEY_JUGNOO_BALANCE, getJugnooBalance()));
			if(jObj.has(Constants.KEY_PAYTM_BALANCE)){
				setPaytmBalance(jObj.optDouble(Constants.KEY_PAYTM_BALANCE, getPaytmBalance()));
				if(getPaytmBalance() > 0 || removeWalletIfNoKey) {
					setPaytmEnabled(1);
				}
			} else{
				if(removeWalletIfNoKey) {
					deletePaytm();
				}
			}

			if(jObj.has(Constants.KEY_MOBIKWIK_BALANCE)){
				setMobikwikBalance(jObj.optDouble(Constants.KEY_MOBIKWIK_BALANCE, getMobikwikBalance()));
				if(getMobikwikBalance() > 0 || removeWalletIfNoKey) {
					setMobikwikEnabled(1);
				}
			} else{
				if(removeWalletIfNoKey) {
					deleteMobikwik();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public int getNotificationSettingEnabled() {
		return notificationPreferenceEnabled;
	}

	public void setNotificationSettingEnabled(int isSettingEnabled) {
		this.notificationPreferenceEnabled = isSettingEnabled;
	}

	public int getMealsEnabled() {
		return mealsEnabled;
	}

	public void setMealsEnabled(int mealsEnabled) {
		this.mealsEnabled = mealsEnabled;
	}

	public int getFreshEnabled() {
		return freshEnabled;
	}

	public void setFreshEnabled(int freshEnabled) {
		this.freshEnabled = freshEnabled;
	}

	public int getDeliveryEnabled() {
		return deliveryEnabled;
	}

	public void setDeliveryEnabled(int deliveryEnabled) {
		this.deliveryEnabled = deliveryEnabled;
	}

	public int getInviteFriendButton() {
		return inviteFriendButton;
	}

	public void setInviteFriendButton(int inviteFriendButton) {
		this.inviteFriendButton = inviteFriendButton;
	}

	public ArrayList<EmergencyContact> getEmergencyContactsList() {
		return emergencyContactsList;
	}

	public void setEmergencyContactsList(ArrayList<EmergencyContact> emergencyContactsList) {
		this.emergencyContactsList = emergencyContactsList;
	}

	public int getCurrentCity() {
		return currentCity;
	}

	public void setCurrentCity(int currentCity) {
		this.currentCity = currentCity;
	}

	public ArrayList<PromoCoupon> getPromoCoupons() {
		return promoCoupons;
	}

	public void setPromoCoupons(ArrayList<PromoCoupon> promoCoupons) {
		this.promoCoupons = promoCoupons;
	}

	public ArrayList<MenuInfo> getMenuInfoList() {
		return menuInfoList;
	}

	public void setMenuInfoList(ArrayList<MenuInfo> menuInfoList) {
		this.menuInfoList = menuInfoList;
	}

	public ReferralMessages getReferralMessages() {
		return referralMessages;
	}

	public void setReferralMessages(ReferralMessages referralMessages) {
		this.referralMessages = referralMessages;
	}

	public String getDefaultClientId() {
		return defaultClientId;
	}

	public void setDefaultClientId(String defaultClientId) {
		this.defaultClientId = defaultClientId;
	}

	public int getIntegratedJugnooEnabled() {
		return integratedJugnooEnabled;
	}

	public void setIntegratedJugnooEnabled(int integratedJugnooEnabled) {
		this.integratedJugnooEnabled = integratedJugnooEnabled;
	}

	public String getTotalCouponCount() {
		int count = 0;
		try {
			if(promoCoupons != null) {
				count = promoCoupons.size();
			}
			if(Data.autoData != null && Data.autoData.getPromoCoupons() != null) {
				count += Data.autoData.getPromoCoupons().size();
			}
			if(Data.getFreshData() != null && Data.getFreshData().getPromoCoupons() != null) {
				count += Data.getFreshData().getPromoCoupons().size();
			}
			if(Data.getMealsData() != null && Data.getMealsData().getPromoCoupons() != null) {
				count += Data.getMealsData().getPromoCoupons().size();
			}
			if(Data.getDeliveryData() != null && Data.getDeliveryData().getPromoCoupons() != null) {
				count += Data.getDeliveryData().getPromoCoupons().size();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return String.valueOf(count);
	}

	public ArrayList<PromoCoupon> getCoupons(int appType) {
		ArrayList<PromoCoupon> coupons = new ArrayList<>();
		if(appType == AppConstant.AppType.AUTO) {
			for(int i = 0;i<promoCoupons.size();i++) {
				PromoCoupon promoCoupon = promoCoupons.get(i);
				try {
					if ((promoCoupon instanceof CouponInfo && ((CouponInfo) promoCoupon).autos.equals(1)) ||
                            (promoCoupon instanceof PromotionInfo && ((PromotionInfo) promoCoupon).autos.equals(1))) {
                        coupons.add(promoCoupon);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			coupons.addAll(Data.autoData.getPromoCoupons());
		} else if(appType == AppConstant.AppType.FRESH) {
			for(int i = 0;i<promoCoupons.size();i++) {
				PromoCoupon promoCoupon = promoCoupons.get(i);
				try {
					if ((promoCoupon instanceof CouponInfo && ((CouponInfo) promoCoupon).fresh.equals(1)) ||
                            (promoCoupon instanceof PromotionInfo && ((PromotionInfo) promoCoupon).fresh.equals(1))) {
                        coupons.add(promoCoupon);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			coupons.addAll(Data.getFreshData().getPromoCoupons());
		} else if(appType == AppConstant.AppType.MEALS) {
			for(int i = 0;i<promoCoupons.size();i++) {
				PromoCoupon promoCoupon = promoCoupons.get(i);
				try {
					if ((promoCoupon instanceof CouponInfo && ((CouponInfo) promoCoupon).meals.equals(1)) ||
                            (promoCoupon instanceof PromotionInfo && ((PromotionInfo) promoCoupon).meals.equals(1))) {
                        coupons.add(promoCoupon);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			coupons.addAll(Data.getMealsData().getPromoCoupons());
		} else if(appType == AppConstant.AppType.DELIVERY) {
			for(int i = 0;i<promoCoupons.size();i++) {
				PromoCoupon promoCoupon = promoCoupons.get(i);
				try {
					if ((promoCoupon instanceof CouponInfo && ((CouponInfo) promoCoupon).delivery.equals(1)) ||
							(promoCoupon instanceof PromotionInfo && ((PromotionInfo) promoCoupon).delivery.equals(1))) {
						coupons.add(promoCoupon);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			coupons.addAll(Data.getDeliveryData().getPromoCoupons());
		}

		return coupons;
	}

	public JeanieIntroDialogContent getJeanieIntroDialogContent() {
		return jeanieIntroDialogContent;
	}

	public void setJeanieIntroDialogContent(JeanieIntroDialogContent jeanieIntroDialogContent) {
		this.jeanieIntroDialogContent = jeanieIntroDialogContent;
	}

//	"meals_enabled": 1,
//			"fresh_enabled": 1,
//			"delivery_enabled": 1,
//			"default_client_id": "FHkmrtv6zn0KuGcW",
}

package product.clicklabs.jugnoo.datastructure;

import android.app.Activity;
import android.content.Context;

import org.json.JSONObject;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.models.JeanieIntroDialogContent;
import product.clicklabs.jugnoo.home.models.MenuInfo;
import product.clicklabs.jugnoo.home.models.RateAppDialogContent;
import product.clicklabs.jugnoo.retrofit.model.FetchUserAddressResponse;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;
import product.clicklabs.jugnoo.wallet.models.PaymentModeConfigData;

public class UserData {
	public String userIdentifier, accessToken, authKey, userName, userEmail, userImage, referralCode, phoneNo, jugnooFbBanner;
	public int emailVerificationStatus;
	private double jugnooBalance;
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

	private int freeChargeEnabled;
	private double freeChargeBalance = -1;

	private int notificationPreferenceEnabled = 0, mealsEnabled, freshEnabled, deliveryEnabled, groceryEnabled, menusEnabled, deliveryCustomerEnabled,
			inviteFriendButton, payEnabled, feedEnabled, prosEnabled,autosEnabled;

	private ArrayList<EmergencyContact> emergencyContactsList = new ArrayList<>();
	private int currentCity = 1;
	private ArrayList<PromoCoupon> promoCoupons = new ArrayList<>();
	private ArrayList<MenuInfo> menuInfoList = new ArrayList<>();

	private ReferralMessages referralMessages;

	private String defaultClientId;

	private JeanieIntroDialogContent jeanieIntroDialogContent;

	private int customerRateAppFlag = 0, topupCardEnabled;
	private RateAppDialogContent rateAppDialogContent;

	private ArrayList<SearchResult> searchResults = new ArrayList<>();
	private ArrayList<SearchResult> searchResultsRecent = new ArrayList<>();
	private ArrayList<SearchResult> searchResultsAdditional = new ArrayList<>();
	private ArrayList<FetchUserAddressResponse.Address> pointsOfInterestAddresses = new ArrayList<>();

	private int showHomeScreen, showSubscriptionData, slideCheckoutPayEnabled;
	private SubscriptionData subscriptionData;

	private int showJeanieHelpText;
	private int showOfferDialog;
	private int showTutorial;
	private SignupTutorial signupTutorial;
	private int expandJeanie, signupOnboarding;
	private String expandedGenieText;
	private String upiHandle;
	private int showJugnooStarInAcccount;
	private String fuguChannelInfoJson;

	public String getFuguChannelInfoJson() {
		return fuguChannelInfoJson;
	}

	public void setFuguChannelInfoJson(final String fuguChannelInfoJson) {
		this.fuguChannelInfoJson = fuguChannelInfoJson;
	}

	public UserData(String userIdentifier, String accessToken, String authKey, String userName, String userEmail, int emailVerificationStatus,
					String userImage, String referralCode, String phoneNo, double jugnooBalance,
					String jugnooFbBanner,
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
					int paytmEnabled, int mobikwikEnabled, int freeChargeEnabled, int notificationPreferenceEnabled,
					int mealsEnabled, int freshEnabled, int deliveryEnabled, int groceryEnabled, int menusEnabled,
					int payEnabled, int feedEnabled, int prosEnabled, int deliveryCustomerEnabled,
					int inviteFriendButton, String defaultClientId,
					int integratedJugnooEnabled, int topupCardEnabled, int showHomeScreen, int showSubscriptionData,
					int slideCheckoutPayEnabled, int showJeanieHelpText, int showOfferDialog, int showTutorial, int signupOnboarding, int autosEnabled){
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
		this.freeChargeEnabled = freeChargeEnabled;

		this.autosEnabled = autosEnabled;
		this.mealsEnabled = mealsEnabled;
		this.freshEnabled = freshEnabled;
		this.groceryEnabled = groceryEnabled;
		this.menusEnabled = menusEnabled;
		this.payEnabled = payEnabled;
		this.deliveryEnabled = deliveryEnabled;
		this.feedEnabled = feedEnabled;
		this.prosEnabled = prosEnabled;
		this.deliveryCustomerEnabled = deliveryCustomerEnabled;

		this.inviteFriendButton = inviteFriendButton;

		this.defaultClientId = defaultClientId;
		this.integratedJugnooEnabled = integratedJugnooEnabled;
		this.topupCardEnabled = topupCardEnabled;

		this.showHomeScreen = showHomeScreen;
		this.showSubscriptionData = showSubscriptionData;
		this.slideCheckoutPayEnabled = slideCheckoutPayEnabled;
		this.showJeanieHelpText = showJeanieHelpText;
		this.showOfferDialog = showOfferDialog;
		this.showTutorial = showTutorial;
		this.signupOnboarding = signupOnboarding;

		onlyAutosEnabled();

	}

	private void onlyAutosEnabled() {
		this.autosEnabled = 1;
		this.mealsEnabled = 0;
		this.freshEnabled = 0;
		this.groceryEnabled = 0;
		this.menusEnabled = 0;
		this.payEnabled = 0;
		this.deliveryEnabled = 0;
		this.feedEnabled = 0;
		this.prosEnabled = 0;
		this.deliveryCustomerEnabled = 0;
		this.defaultClientId = Config.getAutosClientId();
		this.integratedJugnooEnabled = 0;
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
		Prefs.with(MyApplication.getInstance()).save(Constants.SP_PAYTM_LAST_BALANCE, String.valueOf(paytmBalance));
	}

	public double getTotalWalletBalance() {
		double walletTotal = 0;
		for(PaymentModeConfigData paymentModeConfigData : MyApplication.getInstance().getWalletCore().getPaymentModeConfigDatas()){
			if(paymentModeConfigData.getEnabled() == 1) {
				if (paymentModeConfigData.getPaymentOption() == PaymentOption.PAYTM.getOrdinal()
						&& paytmEnabled == 1 && paytmBalance > -1) {
					walletTotal = walletTotal + paytmBalance;
				} else if (paymentModeConfigData.getPaymentOption() == PaymentOption.MOBIKWIK.getOrdinal()
						&& mobikwikEnabled == 1 && mobikwikBalance > -1) {
					walletTotal = walletTotal + mobikwikBalance;
				} else if (paymentModeConfigData.getPaymentOption() == PaymentOption.FREECHARGE.getOrdinal()
						&& freeChargeEnabled == 1 && freeChargeBalance > -1) {
					walletTotal = walletTotal + freeChargeBalance;
				} else if (paymentModeConfigData.getPaymentOption() == PaymentOption.CASH.getOrdinal()) {
					walletTotal = walletTotal + jugnooBalance;
				}
			}
		}
		return walletTotal;
	}

	public int getPromoSuccess() {
		return promoSuccess;
	}

	public void setPromoSuccess(int promoSuccess) {
		this.promoSuccess = promoSuccess;
	}

	public void deletePaytm(){
		this.paytmEnabled = 0;
		this.setPaytmBalance(-1);
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
		Prefs.with(MyApplication.getInstance()).save(Constants.SP_MOBIKWIK_LAST_BALANCE, String.valueOf(mobikwikBalance));
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
		this.setMobikwikBalance(-1);
	}


    /**
     * For FreeCharge
     */


    public int getFreeChargeEnabled() {
        return freeChargeEnabled;
    }

    public void setFreeChargeEnabled(int freeChargeEnabled) {
        this.freeChargeEnabled = freeChargeEnabled;
    }

    public double getFreeChargeBalance() {
        return freeChargeBalance;
    }

    public String getFreeChargeBalanceStr(){
        if(freeChargeEnabled != 1 || freeChargeBalance < 0){
            return "--";
        } else {
            return Utils.getMoneyDecimalFormatWithoutFloat().format(freeChargeBalance);
        }
    }

    public void setFreeChargeBalance(double freeChargeBalance) {
        this.freeChargeBalance = freeChargeBalance;
		Prefs.with(MyApplication.getInstance()).save(Constants.SP_FREECHARGE_LAST_BALANCE, String.valueOf(freeChargeBalance));
    }

    public int getFreeChargeBalanceColor(Context context){
        if(freeChargeBalance < 0){
            return context.getResources().getColor(R.color.theme_red_color);
        } else{
            return context.getResources().getColor(R.color.theme_green_color);
        }
    }

    public void deleteFreeCharge(){
        this.freeChargeEnabled = 0;
        this.setFreeChargeBalance(-1);
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
				double spBalance = Double.parseDouble(Prefs.with(MyApplication.getInstance()).getString(Constants.SP_PAYTM_LAST_BALANCE, "-1"));
				double serverBalance = jObj.optDouble(Constants.KEY_PAYTM_BALANCE, getPaytmBalance());
				setPaytmBalance(serverBalance > -1 ? serverBalance : spBalance);
				if(getPaytmBalance() > 0 || removeWalletIfNoKey) {
					setPaytmEnabled(1);
				}
			} else{
				if(removeWalletIfNoKey) {
					deletePaytm();
				}
			}

			if(jObj.has(Constants.KEY_MOBIKWIK_BALANCE)){
				double spBalance = Double.parseDouble(Prefs.with(MyApplication.getInstance()).getString(Constants.SP_MOBIKWIK_LAST_BALANCE, "-1"));
				double serverBalance = jObj.optDouble(Constants.KEY_MOBIKWIK_BALANCE, getMobikwikBalance());
				setMobikwikBalance(serverBalance > -1 ? serverBalance : spBalance);
				if(getMobikwikBalance() > 0 || removeWalletIfNoKey) {
					setMobikwikEnabled(1);
				}
			} else{
				if(removeWalletIfNoKey) {
					deleteMobikwik();
				}
			}

            if(jObj.has(Constants.KEY_FREECHARGE_BALANCE)) {
				double spBalance = Double.parseDouble(Prefs.with(MyApplication.getInstance()).getString(Constants.SP_FREECHARGE_LAST_BALANCE, "-1"));
				double serverBalance = jObj.optDouble(Constants.KEY_FREECHARGE_BALANCE, getFreeChargeBalance());
				setFreeChargeBalance(serverBalance > -1 ? serverBalance : spBalance);
                if(getFreeChargeBalance() > 0 || removeWalletIfNoKey) {
                    setFreeChargeEnabled(1);
                }
            } else {
                if(removeWalletIfNoKey) {
                    deleteFreeCharge();
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
		this.mealsEnabled = 0;
	}

	public int getFreshEnabled() {
		return freshEnabled;
	}

	public void setFreshEnabled(int freshEnabled) {
		this.freshEnabled = 0;
	}

	public int getGroceryEnabled() {
		return groceryEnabled;
	}

	public void setGroceryEnabled(int groceryEnabled) {
		this.groceryEnabled = 0;
	}

	public int getDeliveryEnabled() {
		return deliveryEnabled;
	}

	public void setDeliveryEnabled(int deliveryEnabled) {
		this.deliveryEnabled = 0;
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

	public int getShowOfferDialog() {
		return showOfferDialog;
	}

	public void setShowOfferDialog(int showOfferDialog) {
		this.showOfferDialog = showOfferDialog;
	}

	public int getTotalCouponCount() {
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
			if(Data.getGroceryData() != null && Data.getGroceryData().getPromoCoupons() != null) {
				count += Data.getGroceryData().getPromoCoupons().size();
			}
			if(Data.getMenusData() != null && Data.getMenusData().getPromoCoupons() != null) {
				count += Data.getMenusData().getPromoCoupons().size();
			}
			if(Data.getDeliveryCustomerData() != null && Data.getDeliveryCustomerData().getPromoCoupons() != null) {
				count += Data.getDeliveryCustomerData().getPromoCoupons().size();
			}
			if(Data.getPayData() != null && Data.getPayData().getPromoCoupons() != null) {
				count += Data.getPayData().getPromoCoupons().size();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}


	public ArrayList<PromoCoupon> getCoupons(ProductType productType, Activity activity) {
		ArrayList<PromoCoupon> coupons = new ArrayList<>();

		if (productType == ProductType.AUTO) {
			if (Data.autoData != null) {
				coupons.addAll(Data.autoData.getPromoCoupons());
			}
		} else if (productType == ProductType.FRESH) {
			if (Data.getFreshData() != null) {
				coupons.addAll(Data.getFreshData().getPromoCoupons());
			}
		} else if (productType == ProductType.MEALS) {
			if (Data.getMealsData() != null) {
				coupons.addAll(Data.getMealsData().getPromoCoupons());
			}
		} else if (productType == ProductType.GROCERY) {
			if (Data.getGroceryData() != null) {
				coupons.addAll(Data.getGroceryData().getPromoCoupons());
			}
		} else if (productType == ProductType.MENUS) {
			if (Data.getMenusData() != null) {
				coupons.addAll(Data.getMenusData().getPromoCoupons());
			}
		}else if (productType == ProductType.DELIVERY_CUSTOMER) {
			if (Data.getDeliveryCustomerData() != null) {
				coupons.addAll(Data.getDeliveryCustomerData().getPromoCoupons());
			}
		} else if (productType == ProductType.PAY) {
			if (Data.getPayData() != null) {
				coupons.addAll(Data.getPayData().getPromoCoupons());
			}
		}

		ArrayList<PromoCoupon> validPC = new ArrayList<>();
		ArrayList<PromoCoupon> invalidPC = new ArrayList<>();
		if(activity instanceof HomeActivity){
			int currentVehicleTypeSelected = ((HomeActivity) activity).getVehicleTypeSelected();
			int currentOperatorId = ((HomeActivity) activity).getOperatorIdSelected();
			for(PromoCoupon pc : coupons){
				if (!pc.isVehicleTypeExists(currentVehicleTypeSelected, currentOperatorId)){
					continue;
				}
				if (pc.getIsValid() == 1) {
					validPC.add(pc);
				} else {
					invalidPC.add(pc);
				}
			}
		}else{
			for(PromoCoupon pc : coupons){

					if(pc.getIsValid() == 1){
						validPC.add(pc);
					} else {
						invalidPC.add(pc);
					}
			}
		}

		coupons.clear();
		coupons.addAll(validPC);
		for (int i = 0; i < promoCoupons.size(); i++) {
			PromoCoupon promoCoupon = promoCoupons.get(i);
			try {
				if ((productType == ProductType.AUTO && promoCoupon.getAutos().equals(1))
						|| (productType == ProductType.FRESH && promoCoupon.getFresh().equals(1))
						|| (productType == ProductType.MEALS && promoCoupon.getMeals().equals(1))
						|| (productType == ProductType.GROCERY && promoCoupon.getGrocery().equals(1))
						|| (productType == ProductType.MENUS && promoCoupon.getMenus().equals(1))
						|| (productType == ProductType.DELIVERY_CUSTOMER && promoCoupon.getDeliveryCustomer().equals(1))
						|| (productType == ProductType.PAY && promoCoupon.getPay().equals(1))) {
					coupons.add(promoCoupon);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		coupons.addAll(invalidPC);

		return coupons;
	}
	public PromoCoupon getDefaultCoupon(int vehicleType, int operatorId, HomeActivity homeActivity){
		ArrayList<PromoCoupon> promoCoupons = getCoupons(ProductType.AUTO,homeActivity) ;
		if(promoCoupons!=null){

			for(PromoCoupon promoCoupon: promoCoupons){
				  if(promoCoupon.getIsSelected()==1 && promoCoupon.isVehicleTypeExists(vehicleType, operatorId))
				  	return promoCoupon;
			}
		}
		return null;
	}

	public JeanieIntroDialogContent getJeanieIntroDialogContent() {
		return jeanieIntroDialogContent;
	}

	public void setJeanieIntroDialogContent(JeanieIntroDialogContent jeanieIntroDialogContent) {
		this.jeanieIntroDialogContent = jeanieIntroDialogContent;
	}

	public int getCustomerRateAppFlag() {
		return customerRateAppFlag;
	}

	public void setCustomerRateAppFlag(int customerRateAppFlag) {
		this.customerRateAppFlag = customerRateAppFlag;
	}

	public RateAppDialogContent getRateAppDialogContent() {
		return rateAppDialogContent;
	}

	public void setRateAppDialogContent(RateAppDialogContent rateAppDialogContent) {
		this.rateAppDialogContent = rateAppDialogContent;
	}

	public ArrayList<SearchResult> getSearchResults() {
		return searchResults;
	}

	public void setSearchResults(ArrayList<SearchResult> searchResults) {
		this.searchResults = searchResults;
	}

	public int getTopupCardEnabled() {
		return topupCardEnabled;
	}

	public void setTopupCardEnabled(int topupCardEnabled) {
		this.topupCardEnabled = topupCardEnabled;
	}

	public ArrayList<SearchResult> getSearchResultsRecent() {
		return searchResultsRecent;
	}

	public void setSearchResultsRecent(ArrayList<SearchResult> searchResultsRecent) {
		this.searchResultsRecent = searchResultsRecent;
	}

	public int getMenusEnabled() {
		return menusEnabled;
	}
    public int getDeliveryCustomerEnabled() {
		return deliveryCustomerEnabled;
	}

	public int getAutosEnabled() {
		return autosEnabled;
	}

	public void setMenusEnabled(int menusEnabled) {
		this.menusEnabled = 0;
	}

	public int getPayEnabled() {
		return payEnabled;
	}

	public void setPayEnabled(int payEnabled) {
		this.payEnabled = 0;
	}


	public int getShowHomeScreen() {
		return showHomeScreen;
	}

	public void setShowHomeScreen(int showHomeScreen) {
		this.showHomeScreen = showHomeScreen;
	}

	public boolean isSubscriptionActive(){
		return getSubscriptionData().getUserSubscriptions() != null && getSubscriptionData().getUserSubscriptions().size() > 0;
	}

	public SubscriptionData getSubscriptionData() {
		if(subscriptionData == null){
			subscriptionData = new SubscriptionData();
		}
		return subscriptionData;
	}

	public void setSubscriptionData(SubscriptionData subscriptionData) {
		this.subscriptionData = subscriptionData;
	}

	public int getShowSubscriptionData() {
		return showSubscriptionData;
	}

	public void setShowSubscriptionData(int showSubscriptionData) {
		this.showSubscriptionData = showSubscriptionData;
	}

	public ArrayList<SearchResult> getSearchResultsAdditional() {
		return searchResultsAdditional;
	}

	public void setSearchResultsAdditional(ArrayList<SearchResult> searchResultsAdditional) {
		this.searchResultsAdditional = searchResultsAdditional;
	}

	/**
	 * From find_a_driver api, used for showing famous request locations on map
	 * @return ArrayList of FetchUserAddressResponse.Address
	 */
	public ArrayList<FetchUserAddressResponse.Address> getPointsOfInterestAddresses() {
		return pointsOfInterestAddresses;
	}

	public void setPointsOfInterestAddresses(ArrayList<FetchUserAddressResponse.Address> pointsOfInterestAddresses) {
		this.pointsOfInterestAddresses = pointsOfInterestAddresses;
	}

	public int getSlideCheckoutPayEnabled() {
		return slideCheckoutPayEnabled;
	}

	public void setSlideCheckoutPayEnabled(int slideCheckoutPayEnabled) {
		this.slideCheckoutPayEnabled = slideCheckoutPayEnabled;
	}

	public int getShowJeanieHelpText() {
		return showJeanieHelpText;
	}

	public void setShowJeanieHelpText(int showJeanieHelpText) {
		this.showJeanieHelpText = showJeanieHelpText;
	}

	public int getShowTutorial() {
		return showTutorial;
	}

	public void setShowTutorial(int showTutorial) {
		this.showTutorial = showTutorial;
	}

	public SignupTutorial getSignupTutorial() {
		return signupTutorial;
	}

	public void setSignupTutorial(SignupTutorial signupTutorial) {
		this.signupTutorial = signupTutorial;
	}
	public int getFeedEnabled() {
		return feedEnabled;
	}

	public void setFeedEnabled(int feedEnabled) {
		this.feedEnabled = 0;
	}

	public int getExpandJeanie() {
		return expandJeanie;
	}

	public void setExpandJeanie(int expandJeanie) {
		this.expandJeanie = expandJeanie;
	}

	public String getExpandedGenieText() {
		return expandedGenieText;
	}

	public void setExpandedGenieText(String expandedGenieText) {
		this.expandedGenieText = expandedGenieText;
	}

	public String getUpiHandle() {
		return upiHandle;
	}

	public void setUpiHandle(String upiHandle) {
		this.upiHandle = upiHandle;
	}

	public boolean getShowJugnooStarInAcccount() {
		return showJugnooStarInAcccount==1;
	}


	public void setShowJugnooStarInAcccount(int showJugnooStarInAcccount) {
		this.showJugnooStarInAcccount = showJugnooStarInAcccount;
	}
	public int getSignupOnboarding() {
		return signupOnboarding;
	}

	public void setSignupOnboarding(int signupOnboarding) {
		this.signupOnboarding = signupOnboarding;
	}

	public int getProsEnabled() {
		return prosEnabled;
	}

	public void setProsEnabled(int prosEnabled) {
		this.prosEnabled = 0;
	}

	public void setDeliveryCustomerEnabled(Integer deliveryCustomerEnabled) {
		this.deliveryCustomerEnabled = 0;
	}

	public void setAutosEnabled(Integer autosEnabled) {
		this.autosEnabled = 1;
	}

	public boolean isRidesAndFatafatEnabled(){
		return getDeliveryCustomerEnabled() == 1;
	}

}

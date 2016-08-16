package com.sabkuchfresh.datastructure;

import android.content.Context;

import com.sabkuchfresh.R;
import com.sabkuchfresh.retrofit.model.Store;
import com.sabkuchfresh.utils.Data;
import com.sabkuchfresh.utils.Utils;

import java.util.ArrayList;

public class UserData {
	public String userIdentifier, accessToken, authKey, userName, userEmail, userImage, referralCode, phoneNo, jugnooFbBanner;
	public int emailVerificationStatus;
	private double jugnooBalance, paytmBalance, totalWalletBalance;
	public int numCouponsAvaliable;
	public double fareFactor;
	public int showJugnooJeanie;
	private String paytmStatus;
	public int paytmEnabled, paytmError;
	public int contactSaved;
    public String referAllText, referAllTitle;
	private int referAllStatusLogin;
	private String referAllTextLogin, referAllTitleLogin;
	private int promoSuccess;
	private String promoMessage;

	private String branchDesktopUrl, branchAndroidUrl, branchIosUrl, branchFallbackUrl;

    public String shareImage, shateName;

	private String jugnooCashTNC;
	private String inAppSupportPanelVersion, userId;
	private int getGogu;
	private String inviteEarnScreenImage;

	private int t20WCEnable, defaultStoreId;
	private String t20WCScheduleVersion, t20WCInfoText;
	private String publicAccessToken;

	private int gamePredictEnable, cToDReferralEnabled;
	private String gamePredictUrl, gamePredictIconUrl, gamePredictName, gamePredictNew;

    public String referralBanner, referralShortDesc, referralDescription, referralShareTitle,
    referralShareText, referralShareImage, question, orderId, referralShareMessage;

    public int questionType, inviteFriendButton;

    public int pendingFeedback;

    public ArrayList<Store> stores;

    private PopupData popupData;

	public UserData(String userIdentifier, String accessToken, String authKey, String userName, String userEmail, int emailVerificationStatus,
					String userImage, String referralCode, String phoneNo, double jugnooBalance, double fareFactor,
					String jugnooFbBanner, int numCouponsAvaliable, int paytmEnabled,
					int contactSaved, String referAllText, String referAllTitle,
					int promoSuccess, String promoMessage,
					int showJugnooJeanie,
					String branchDesktopUrl, String branchAndroidUrl, String branchIosUrl, String branchFallbackUrl,
					String jugnooCashTNC, String inAppSupportPanelVersion, int getGogu,
					String userId, String inviteEarnScreenImage,
					int t20WCEnable, String t20WCScheduleVersion, String t20WCInfoText, String publicAccessToken,
					int gamePredictEnable, String gamePredictUrl, String gamePredictIconUrl, String gamePredictName, String gamePredictNew,
					int referAllStatusLogin, String referAllTextLogin, String referAllTitleLogin, int cToDReferralEnabled,
					String shareImage, String shateName, String referralBanner, String referralShortDesc, String referralDescription, String referralShareTitle,
					String referralShareText, String referralShareImage, String question, int questionType, String orderId,
					int pendingFeedback, PopupData popupData, ArrayList<Store> stores, int inviteFriendButton, String referralShareMessage,
					int defaultStoreId){

//        this.popupData = popupData;
        try {setPopupData(popupData); } catch (Exception e){}

        this.stores = stores;
        this.shareImage = shareImage;
        this.shateName = shateName;
        this.referralBanner = referralBanner;
        this.referralShortDesc = referralShortDesc;
        this.referralDescription = referralDescription;
        this.referralShareTitle = referralShareTitle;
        this.referralShareText = referralShareText;
        this.referralShareImage = referralShareImage;
        this.question = question;
        this.questionType = questionType;
        this.orderId = orderId;
        this.pendingFeedback = pendingFeedback;

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
		this.fareFactor = fareFactor;
		
		this.jugnooFbBanner = jugnooFbBanner;
		this.numCouponsAvaliable = numCouponsAvaliable;
		this.paytmBalance = 0;
		this.paytmEnabled = paytmEnabled;
		this.paytmError = 0;
		this.contactSaved = contactSaved;
        this.referAllText = referAllText;
		this.referAllTitle = referAllTitle;

		this.promoSuccess = promoSuccess;
		this.promoMessage = promoMessage;

		this.showJugnooJeanie = showJugnooJeanie;

		if(1 == this.paytmEnabled) {
			this.paytmStatus = "";
		}
		else{
			this.paytmStatus = Data.PAYTM_STATUS_INACTIVE;
		}
		setTotalWalletBalance();

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

		this.referAllStatusLogin = referAllStatusLogin;
		this.referAllTextLogin = referAllTextLogin;
		this.referAllTitleLogin = referAllTitleLogin;
		this.cToDReferralEnabled = cToDReferralEnabled;
		this.inviteFriendButton = inviteFriendButton;
		this.referralShareMessage = referralShareMessage;
		this.defaultStoreId = defaultStoreId;
	}

	public double getJugnooBalance() {
		return jugnooBalance;
	}

	public void setJugnooBalance(double jugnooBalance) {
		this.jugnooBalance = jugnooBalance;
		setTotalWalletBalance();
	}

	public double getPaytmBalance(){
		return paytmBalance;
	}

	public String getPaytmBalanceStr(){
		if(this.paytmError == 1){
			return "--";
		} else{
			return Utils.getMoneyDecimalFormat().format(getPaytmBalance());
		}
	}

	public void setPaytmError(int paytmError){
		this.paytmError = paytmError;
	}

	public int getPaytmError(){
		return paytmError;
	}

	public void setPaytmBalance(double paytmBalance) {
		this.paytmBalance = paytmBalance;
		setTotalWalletBalance();
	}

	public double getTotalWalletBalance() {
		return totalWalletBalance;
	}

	private void setTotalWalletBalance() {
		this.totalWalletBalance = this.jugnooBalance + this.paytmBalance;
	}

	public String getPaytmStatus() {
		return paytmStatus;
	}

	public void setPaytmStatus(String paytmStatus) {
		this.paytmStatus = paytmStatus;
	}

	public int getPromoSuccess() {
		return promoSuccess;
	}

	public void setPromoSuccess(int promoSuccess) {
		this.promoSuccess = promoSuccess;
	}

	public void deletePaytm(){
		this.paytmEnabled = 0;
		setPaytmBalance(0);
		this.paytmError = 0;
		this.paytmStatus = Data.PAYTM_STATUS_INACTIVE;
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
		int color = context.getResources().getColor(R.color.theme_green_color);
		if(getPaytmBalance() < 0){
			color = context.getResources().getColor(R.color.theme_red_color);
		}
		if(getPaytmError() == 1){
			color = context.getResources().getColor(R.color.theme_red_color);
		}
		return color;
	}

	public int getJugnooBalanceColor(Context context){
		int color = context.getResources().getColor(R.color.theme_green_color);
		if(getJugnooBalance() < 0){
			color = context.getResources().getColor(R.color.theme_red_color);
		}
		return color;
	}

	public int getTotalBalanceColor(Context context){
		int color = context.getResources().getColor(R.color.theme_green_color);
		if(getTotalWalletBalance() < 0){
			color = context.getResources().getColor(R.color.theme_red_color);
		}
		return color;
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

	public String getReferAllTextLogin() {
		return referAllTextLogin;
	}

	public void setReferAllTextLogin(String referAllTextLogin) {
		this.referAllTextLogin = referAllTextLogin;
	}

	public int getReferAllStatusLogin() {
		return referAllStatusLogin;
	}

	public void setReferAllStatusLogin(int referAllStatusLogin) {
		this.referAllStatusLogin = referAllStatusLogin;
	}

	public String getReferAllTitleLogin() {
		return referAllTitleLogin;
	}

	public void setReferAllTitleLogin(String referAllTitleLogin) {
		this.referAllTitleLogin = referAllTitleLogin;
	}

	public int getcToDReferralEnabled() {
		return cToDReferralEnabled;
	}

	public void setcToDReferralEnabled(int cToDReferralEnabled) {
		this.cToDReferralEnabled = cToDReferralEnabled;
	}

    public PopupData getPopupData() {
        return popupData;
    }

    public void setPopupData(PopupData popupData) {
        this.popupData = popupData;
    }

	public int getInviteFriendButton() {
		return inviteFriendButton;
	}

	public void setInviteFriendButton(int inviteFriendButton) {
		this.inviteFriendButton = inviteFriendButton;
	}

	public String getReferralBanner() {
		return referralBanner;
	}

	public void setReferralBanner(String referralBanner) {
		this.referralBanner = referralBanner;
	}

	public String getReferralShareImage() {
		return referralShareImage;
	}

	public void setReferralShareImage(String referralShareImage) {
		this.referralShareImage = referralShareImage;
	}

	public String getReferralShareMessage() {
		return referralShareMessage;
	}

	public void setReferralShareMessage(String referralShareMessage) {
		this.referralShareMessage = referralShareMessage;
	}

	public int getDefaultStoreId() {
		return defaultStoreId;
	}

	public void setDefaultStoreId(int defaultStoreId) {
		this.defaultStoreId = defaultStoreId;
	}
}

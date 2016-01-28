package product.clicklabs.jugnoo.datastructure;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.utils.Utils;

public class UserData {
	public String userIdentifier, accessToken, authKey, userName, userEmail, userImage, referralCode, phoneNo, nukkadIcon, jugnooMealsPackageName, jugnooFbBanner;
	public int canSchedule, canChangeLocation, schedulingLimitMinutes, isAvailable, exceptionalDriver, gcmIntent, christmasIconEnable, nukkadEnable, enableJugnooMeals,
		freeRideIconDisable;
	public int emailVerificationStatus;
	private double jugnooBalance, paytmBalance, totalWalletBalance;
	public int numCouponsAvaliable;
	public double fareFactor;
	public double sharingFareFixed;
	public int showJugnooSharing, showJugnooJeanie;
	private String paytmStatus;
	public int paytmEnabled, paytmError, contactSaved;
    public String referAllText, referAllTitle;
	private int promoSuccess;
	private String promoMessage;

	private String branchDesktopUrl, branchAndroidUrl, branchIosUrl, branchFallbackUrl;

	private String jugnooCashTNC;

	public UserData(String userIdentifier, String accessToken, String authKey, String userName, String userEmail, int emailVerificationStatus,
			String userImage, String referralCode, String phoneNo,
			int canSchedule, int canChangeLocation, int schedulingLimitMinutes, int isAvailable, int exceptionalDriver, int gcmIntent, int christmasIconEnable, 
			int nukkadEnable, String nukkadIcon, int enableJugnooMeals, String jugnooMealsPackageName, int freeRideIconDisable, double jugnooBalance, double fareFactor,
			String jugnooFbBanner, int numCouponsAvaliable, double sharingFareFixed, int showJugnooSharing, int paytmEnabled,
                    int contactSaved, String referAllText, String referAllTitle,
					int promoSuccess, String promoMessage,
					int showJugnooJeanie,
					String branchDesktopUrl, String branchAndroidUrl, String branchIosUrl, String branchFallbackUrl,
					String jugnooCashTNC){
        this.userIdentifier = userIdentifier;
		this.accessToken = accessToken;
		this.authKey = authKey;
		this.userName = userName;
		this.userEmail = userEmail;
		this.emailVerificationStatus = emailVerificationStatus;
		
		this.userImage = userImage;
		this.referralCode = referralCode;
		this.phoneNo = phoneNo;
		this.canSchedule = canSchedule;
		this.canChangeLocation = canChangeLocation;
		this.schedulingLimitMinutes = schedulingLimitMinutes;
		this.isAvailable = isAvailable;
		this.exceptionalDriver = exceptionalDriver;
		this.gcmIntent = gcmIntent;
		this.christmasIconEnable = christmasIconEnable;
		this.nukkadEnable = nukkadEnable;
		this.nukkadIcon = nukkadIcon;
		this.enableJugnooMeals = enableJugnooMeals;
		this.jugnooMealsPackageName = jugnooMealsPackageName;
		this.freeRideIconDisable = freeRideIconDisable;
		this.jugnooBalance = jugnooBalance;
		this.fareFactor = fareFactor;
		
		this.jugnooFbBanner = jugnooFbBanner;
		this.numCouponsAvaliable = numCouponsAvaliable;
		this.sharingFareFixed = sharingFareFixed;
		this.showJugnooSharing = showJugnooSharing;
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

	public void setTotalWalletBalance() {
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
}

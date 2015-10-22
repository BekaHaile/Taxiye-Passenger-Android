package product.clicklabs.jugnoo.datastructure;

public class UserData {
	public String userIdentifier, accessToken, authKey, userName, userEmail, userImage, referralCode, phoneNo, nukkadIcon, jugnooMealsPackageName, jugnooFbBanner;
	public int canSchedule, canChangeLocation, schedulingLimitMinutes, isAvailable, exceptionalDriver, gcmIntent, christmasIconEnable, nukkadEnable, enableJugnooMeals,
		freeRideIconDisable;
	public int emailVerificationStatus;
	private double jugnooBalance, paytmBalance, totalWalletBalance;
	public int numCouponsAvaliable;
	public double fareFactor;
	public double sharingFareFixed;
	public int showJugnooSharing;
	private String paytmStatus;

	public UserData(String userIdentifier, String accessToken, String authKey, String userName, String userEmail, int emailVerificationStatus,
			String userImage, String referralCode, String phoneNo,
			int canSchedule, int canChangeLocation, int schedulingLimitMinutes, int isAvailable, int exceptionalDriver, int gcmIntent, int christmasIconEnable, 
			int nukkadEnable, String nukkadIcon, int enableJugnooMeals, String jugnooMealsPackageName, int freeRideIconDisable, double jugnooBalance, double fareFactor,
			String jugnooFbBanner, int numCouponsAvaliable, double sharingFareFixed, int showJugnooSharing){
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
		this.paytmStatus = "";
		setTotalWalletBalance();
	}

	public double getJugnooBalance() {
		return jugnooBalance;
	}

	public void setJugnooBalance(double jugnooBalance) {
		this.jugnooBalance = jugnooBalance;
		setTotalWalletBalance();
	}

	public double getPaytmBalance() {
		return paytmBalance;
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
}

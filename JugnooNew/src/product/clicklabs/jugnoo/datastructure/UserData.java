package product.clicklabs.jugnoo.datastructure;

public class UserData {
	public String accessToken, userName, userImage, referralCode, phoneNo;
	public int canSchedule, canChangeLocation, schedulingLimitMinutes, isAvailable, exceptionalDriver, gcmIntent, christmasIconEnable, nukkadEnable;
	public UserData(String accessToken, String userName, String userImage, String referralCode, String phoneNo,
			int canSchedule, int canChangeLocation, int schedulingLimitMinutes, int isAvailable, int exceptionalDriver, int gcmIntent, int christmasIconEnable, int nukkadEnable){
		this.accessToken = accessToken;
		this.userName = userName;
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
	}
}

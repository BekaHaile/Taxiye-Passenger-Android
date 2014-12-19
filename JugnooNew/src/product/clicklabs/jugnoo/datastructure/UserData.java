package product.clicklabs.jugnoo.datastructure;

public class UserData {
	public String accessToken, userName, userImage, referralCode;
	public int canSchedule, canChangeLocation, schedulingLimitMinutes, isAvailable, exceptionalDriver, gcmIntent, christmasIconEnable;
	public UserData(String accessToken, String userName, String userImage, String referralCode, 
			int canSchedule, int canChangeLocation, int schedulingLimitMinutes, int isAvailable, int exceptionalDriver, int gcmIntent, int christmasIconEnable){
		this.accessToken = accessToken;
		this.userName = userName;
		this.userImage = userImage;
		this.referralCode = referralCode;
		this.canSchedule = canSchedule;
		this.canChangeLocation = canChangeLocation;
		this.schedulingLimitMinutes = schedulingLimitMinutes;
		this.isAvailable = isAvailable;
		this.exceptionalDriver = exceptionalDriver;
		this.gcmIntent = gcmIntent;
		this.christmasIconEnable = christmasIconEnable;
	}
}

package product.clicklabs.jugnoo.datastructure;

public class UserData {
	public String accessToken, userName, userImage, referralCode;
	public int canSchedule, canChangeLocation, schedulingLimitMinutes;
	public UserData(String accessToken, String userName, String userImage, String referralCode, int canSchedule, int canChangeLocation, int schedulingLimitMinutes){
		this.accessToken = accessToken;
		this.userName = userName;
		this.userImage = userImage;
		this.referralCode = referralCode;
		this.canSchedule = canSchedule;
		this.canChangeLocation = canChangeLocation;
		this.schedulingLimitMinutes = schedulingLimitMinutes;
	}
}

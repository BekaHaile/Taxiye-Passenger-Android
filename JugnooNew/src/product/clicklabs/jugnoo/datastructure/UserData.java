package product.clicklabs.jugnoo.datastructure;

public class UserData {
	public String accessToken, userName, userImage, referralCode;
	public int canSchedule, canChangeLocation;
	public UserData(String accessToken, String userName, String userImage, String referralCode, int canSchedule, int canChangeLocation){
		this.accessToken = accessToken;
		this.userName = userName;
		this.userImage = userImage;
		this.referralCode = referralCode;
		this.canSchedule = canSchedule;
		this.canChangeLocation = canChangeLocation;
	}
}

package product.clicklabs.jugnoo.datastructure;

public class UserData {
	public String accessToken;
	public String userName;
	public String userImage;
	public String id;
	public String referralCode;
	
	public UserData(String accessToken, String userName, String userImage, String id, String referralCode){
		this.accessToken = accessToken;
		this.userName = userName;
		this.userImage = userImage;
		this.id = id;
		this.referralCode = referralCode;
	}
}

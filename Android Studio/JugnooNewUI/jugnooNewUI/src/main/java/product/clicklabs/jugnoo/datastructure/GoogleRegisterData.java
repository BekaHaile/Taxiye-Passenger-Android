package product.clicklabs.jugnoo.datastructure;

/**
 * Created by socomo20 on 12/18/15.
 */
public class GoogleRegisterData {

	public String id, name, email, image;
	public String phoneNo, password, referralCode;
	public String accessToken;
	public String googleAccessToken;

	public GoogleRegisterData(String id, String name, String email, String image, String phoneNo, String password,
							  String referralCode, String accessToken, String googleAccessToken){
		this.id = id;
		this.name = name;
		this.email = email;
		this.image = image;
		this.phoneNo = phoneNo;
		this.password = password;
		this.referralCode = referralCode;
		this.accessToken = accessToken;
		this.googleAccessToken = googleAccessToken;
	}

}

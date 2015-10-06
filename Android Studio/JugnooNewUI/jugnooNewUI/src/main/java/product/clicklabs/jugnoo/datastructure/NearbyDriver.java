package product.clicklabs.jugnoo.datastructure;

public class NearbyDriver {

	public String userId, userName, autoId, userImage, driverCarNo, driverCarImage;
	public boolean ticked;

	public NearbyDriver(String userId, String userName, String autoId, String userImage, String driverCarNo, String driverCarImage){
		this.userId = userId;
		this.userName = userName;
		this.autoId = autoId;
		this.userImage = userImage;
		this.driverCarNo = driverCarNo;
		this.driverCarImage = driverCarImage;
		this.ticked = false;
	}
}

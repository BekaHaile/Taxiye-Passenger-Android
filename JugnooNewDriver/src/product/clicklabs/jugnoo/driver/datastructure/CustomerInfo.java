package product.clicklabs.jugnoo.driver.datastructure;

public class CustomerInfo {
	public String userId, name, image, phoneNumber, rating, schedulePickupTime;
	public int freeRide;
	public CouponInfo couponInfo;
	public double jugnooBalance;
	
	public CustomerInfo(String userId, String name, String image, String phoneNumber, String rating, int freeRide, CouponInfo couponInfo, double jugnooBalance){
		this.userId = userId;
		this.name = name;
		this.image = image;
		this.phoneNumber = phoneNumber;
		this.rating = rating;
		this.schedulePickupTime = "";
		this.freeRide = freeRide;
		this.couponInfo = couponInfo;
		this.jugnooBalance = jugnooBalance;
	}
	
	@Override
	public String toString() {
		return "userId = "+userId+" couponInfo = "+couponInfo+" jugnooBalance = "+jugnooBalance;
	}
	
}

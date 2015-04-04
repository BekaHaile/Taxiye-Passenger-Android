package product.clicklabs.jugnoo.driver.datastructure;

public class CustomerInfo {
	public String userId, name, image, phoneNumber, rating, schedulePickupTime;
	public int freeRide;
	public CouponInfo couponInfo;
	public PromoInfo promoInfo;
	public double jugnooBalance;
	
	public CustomerInfo(String userId, String name, String image, String phoneNumber, String rating, int freeRide, 
			CouponInfo couponInfo, PromoInfo promoInfo, double jugnooBalance){
		this.userId = userId;
		this.name = name;
		this.image = image;
		this.phoneNumber = phoneNumber;
		this.rating = rating;
		this.schedulePickupTime = "";
		this.freeRide = freeRide;
		this.couponInfo = couponInfo;
		this.promoInfo = promoInfo;
		this.jugnooBalance = jugnooBalance;
	}
	
	public CustomerInfo(String userId, String name, String image, String phoneNumber, CouponInfo couponInfo, PromoInfo promoInfo){
		this.userId = userId;
		this.name = name;
		this.image = image;
		this.phoneNumber = phoneNumber;
		this.rating = "5";
		this.schedulePickupTime = "";
		this.freeRide = 0;
		this.couponInfo = couponInfo;
		this.promoInfo = promoInfo;
		this.jugnooBalance = 0;
	}
	
	@Override
	public String toString() {
		return "userId = "+userId+" couponInfo = "+couponInfo+" jugnooBalance = "+jugnooBalance+" promoInfo = "+promoInfo;
	}
	
}

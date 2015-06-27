package product.clicklabs.jugnoo.datastructure;

public class CouponInfo extends PromoCoupon{
	
	public int couponType;
	public int status;
	public String title;
	public String subtitle;
	public String description;
	public String image;
	public String redeemedOn;
	public String expiryDate;
	
	public CouponInfo(int id, int couponType, int status, String title, String subtitle, String description, String image,
			String redeemedOn, String expiryDate){
		this.id = id;
		this.couponType = couponType;
		this.status = status;
		this.title = title;
		this.subtitle = subtitle;
		this.description = description;
		this.image = image;
		this.redeemedOn = redeemedOn;
		this.expiryDate = expiryDate;
	}
	
	public CouponInfo(int id, String title){
		this.id = id;
		this.title = title;
		this.couponType = 1;
		this.status = 0;
		this.subtitle = "";
		this.description = "";
		this.image = "";
		this.redeemedOn = "";
		this.expiryDate = "";
	}
	
	@Override
	public boolean equals(Object o) {
		try{
			if((((CouponInfo)o).id == this.id)){
				return true;
			}
			else{
				return false;
			}
		} catch(Exception e){
			return false;
		}
	}
	
}

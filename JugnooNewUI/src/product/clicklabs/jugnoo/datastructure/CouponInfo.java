package product.clicklabs.jugnoo.datastructure;

public class CouponInfo extends PromoCoupon{
	
	public int type;
	public int status;
	public String title;
	public String subtitle;
	public String description;
	public String image;
	public String redeemedOn;
	public String expiryDate;
	double discountPrecent, maximumDiscountableValue;
	
	public CouponInfo(int id, int type, int status, String title, String subtitle, String description, String image, 
			String redeemedOn, String expiryDate, double discountPrecent, double maximumDiscountableValue){
		this.id = id;
		this.type = type;
		this.status = status;
		this.title = title;
		this.subtitle = subtitle;
		this.description = description;
		this.image = image;
		this.redeemedOn = redeemedOn;
		this.expiryDate = expiryDate;
		this.discountPrecent = discountPrecent;
		this.maximumDiscountableValue = maximumDiscountableValue;
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

package product.clicklabs.jugnoo.datastructure;

public class CouponInfo extends PromoCoupon{
	
	public int couponType;
	public int status;
	private String title;
	public String subtitle;
	public String description;
	public String image;
	public String redeemedOn;
	public String expiryDate;
    public String startTime;
    public String endTime;
	
	public CouponInfo(int id, int couponType, int status, String title, String subtitle, String description, String image,
			String redeemedOn, String expiryDate, String startTime, String endTime){
		this.id = id;
		this.couponType = couponType;
		this.status = status;
		this.title = title;
		this.subtitle = subtitle;
		this.description = description;
		this.image = image;
		this.redeemedOn = redeemedOn;
		this.expiryDate = expiryDate;
        this.startTime = startTime;
        this.endTime = endTime;
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
        this.startTime = "";
        this.endTime = "";
	}

	@Override
	public String getTitle() {
		return title;
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

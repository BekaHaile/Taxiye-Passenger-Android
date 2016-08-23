package product.clicklabs.jugnoo.datastructure;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CouponInfo extends PromoCoupon{
	@SerializedName("account_id")
	@Expose
	public Integer id;
	@SerializedName("title")
	@Expose
	private String title;
	@SerializedName("subtitle")
	@Expose
	public String subtitle;
	@SerializedName("description")
	@Expose
	public String description;
	@SerializedName("expiry_date")
	@Expose
	public String expiryDate;

	public CouponInfo(int id, String title, String subtitle, String description, String expiryDate){
		this.id = id;
		this.title = title;
		this.subtitle = subtitle;
		this.description = description;
		this.expiryDate = expiryDate;
	}
	
	public CouponInfo(int id, String title){
		this.id = id;
		this.title = title;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public String getTitle() {
		return title+" "+subtitle;
	}

	@Override
	public boolean equals(Object o) {
		try{
			if((((CouponInfo)o).id.equals(this.id))){
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

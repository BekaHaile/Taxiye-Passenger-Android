package product.clicklabs.jugnoo.datastructure;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PromotionInfo extends PromoCoupon{
	@SerializedName("promo_id")
	@Expose
	public Integer id;
	@SerializedName("title")
	@Expose
	public String title;
	@SerializedName("terms_n_conds")
	@Expose
	public String terms;
	@SerializedName("end_on")
	@Expose
	public String endOn;
	@SerializedName("autos")
	@Expose
	public Integer autos;
	@SerializedName("fresh")
	@Expose
	public Integer fresh;
	@SerializedName("meals")
	@Expose
	public Integer meals;
	@SerializedName("delivery")
	@Expose
	public Integer delivery;
	@SerializedName("grocery")
	@Expose
	public Integer grocery;
	@SerializedName("master_coupon")
	@Expose
	private Integer masterCoupon = 0;
	@SerializedName("discount")
	@Expose
	private Double discount = 0d;


	public PromotionInfo(int id, String title, String terms, String endOn){
		this.id = id;
		this.title = title;
		this.terms = terms;
		this.endOn = endOn;
	}

    public PromotionInfo(int id, String title, String terms){
        this.id = id;
        this.title = title;
        this.terms = terms;
		this.endOn = "";
    }

	@Override
	public int getId() {
		return id;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public boolean equals(Object o) {
		try{
			if((((PromotionInfo)o).id.equals(this.id))){
				return true;
			}
			else{ 
				return false;
			}
		} catch(Exception e){
			return false;
		}
	}

	public Integer getMasterCoupon() {
		if(masterCoupon == null){
			return 0;
		}
		return masterCoupon;
	}

	public void setMasterCoupon(Integer masterCoupon) {
		this.masterCoupon = masterCoupon;
	}

	@Override
	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}
}

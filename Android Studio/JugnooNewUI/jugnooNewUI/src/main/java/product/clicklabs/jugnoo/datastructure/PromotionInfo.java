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
	private Integer autos;
	@SerializedName("fresh")
	@Expose
	private Integer fresh;
	@SerializedName("meals")
	@Expose
	private Integer meals;
	@SerializedName("delivery")
	@Expose
	private Integer delivery;
	@SerializedName("grocery")
	@Expose
	private Integer grocery;
	@SerializedName("menus")
	@Expose
	private Integer menus;
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
			return matchPromoCoupon((PromoCoupon) o);
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

	public Integer getAutos() {
		if(autos == null){
			return 0;
		}
		return autos;
	}

	public void setAutos(Integer autos) {
		this.autos = autos;
	}

	public Integer getFresh() {
		if(fresh == null){
			return 0;
		}
		return fresh;
	}

	public void setFresh(Integer fresh) {
		this.fresh = fresh;
	}

	public Integer getMeals() {
		if(meals == null){
			return 0;
		}
		return meals;
	}

	public void setMeals(Integer meals) {
		this.meals = meals;
	}

	public Integer getDelivery() {
		if(delivery == null){
			return 0;
		}
		return delivery;
	}

	public void setDelivery(Integer delivery) {
		this.delivery = delivery;
	}

	public Integer getGrocery() {
		if(grocery == null){
			return 0;
		}
		return grocery;
	}

	public void setGrocery(Integer grocery) {
		this.grocery = grocery;
	}

	public Integer getMenus() {
		if(menus == null){
			return 0;
		}
		return menus;
	}

	public void setMenus(Integer menus) {
		this.menus = menus;
	}
}

package product.clicklabs.jugnoo.datastructure;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class CouponInfo extends PromoCoupon implements Serializable{
	@SerializedName("account_id")
	@Expose
	public Integer id;
	@SerializedName("title")
	@Expose
	public String title;
	@SerializedName("subtitle")
	@Expose
	public String subtitle;
	@SerializedName("description")
	@Expose
	public String description;
	@SerializedName("expiry_date")
	@Expose
	public String expiryDate;
	@SerializedName("autos")
	@Expose
	private Integer autos = 0;
	@SerializedName("fresh")
	@Expose
	private Integer fresh = 0;
	@SerializedName("meals")
	@Expose
	private Integer meals = 0;
	@SerializedName("delivery")
	@Expose
	private Integer delivery = 0;
	@SerializedName("grocery")
	@Expose
	private Integer grocery = 0;
	@SerializedName("menus")
	@Expose
	private Integer menus = 0;
	@SerializedName("delivery_customer")
	@Expose
	private Integer deliveryCustomer = 0;
	@SerializedName("pay")
	@Expose
	private Integer pay = 0;
	@SerializedName("master_coupon")
	@Expose
	private Integer masterCoupon = 0;
	@SerializedName("discount")
	@Expose
	private Double discount = 0d;
	@SerializedName("is_selected")
	@Expose
	private Integer isSelected;
	@SerializedName("is_valid")
	@Expose
	private Integer isValid;
	@SerializedName("invalid_message")
	@Expose
	private String invalidMessage;
	@SerializedName("allowed_vehicles")
	@Expose
	private ArrayList<Integer> allowedVehicles;
	@SerializedName("operator_id")
	@Expose
	private int operatorId;

	@SerializedName("coupon_id")
	@Expose
	private Integer couponId;

	@SerializedName("show_promo_box")
	@Expose
	private int showPromoBox;
	@SerializedName("message_to_display")
	@Expose
	private String messageToDisplay;
	@SerializedName("is_promo_applied")
	@Expose
	private int isPromoApplied;
	@SerializedName("promo_name")
	@Expose
	private String promoName;
	@SerializedName("coupon_type")
	@Expose
	private int type;
	@SerializedName("drop_radius")
	@Expose
	private double dropRadius;
	@SerializedName("drop_location_coordinates")
	@Expose
	private ArrayList<LatLngCoordinates> dropLocationCoordinates;

	@Override
	public String getPromoName() {
		return promoName;
	}



	@Override
	public boolean showPromoBox(){
		return showPromoBox ==1;
	}

	@Override
	public boolean isPromoApplied(){
		return isPromoApplied ==1;
	}



	@Override
	public String messageToDisplay(){
		return messageToDisplay;
	}

	@Override
	public void setIsPromoApplied(boolean isPromoApplied) {
		this.isPromoApplied = isPromoApplied?1:0;
	}

	@Override
	public void setMessageToDisplay(String messageToDisplay) {
		this.messageToDisplay = messageToDisplay;
	}


	private boolean checkWithCouponId = false;

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
		if(title == null){
			title = "";
		}
		if(subtitle == null){
			subtitle = "";
		}
		return title+" "+subtitle;
	}

	public String getTitleOnly() {
		return title;
	}

	@Override
	public boolean equals(Object o) {
		try{
			if(isCheckWithCouponId()){
				return o instanceof CouponInfo && ((CouponInfo)o).getCouponId().equals(getCouponId());
			} else {
				return matchPromoCoupon((PromoCoupon) o);
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

	@Override
	public Integer getDeliveryCustomer() {

		if(deliveryCustomer == null){
			return 0;
		}
		return deliveryCustomer;
	}

	public void setMenus(Integer menus) {
		this.menus = menus;
	}

	public Integer getPay() {
		if(pay == null){
			return 0;
		}
		return pay;
	}

	public void setPay(Integer pay) {
		this.pay = pay;
	}

	public Integer getIsSelected() {
		if(isSelected == null){
			isSelected = 0;
		}
		return isSelected;
	}

	public void setIsSelected(Integer isSelected) {
		this.isSelected = isSelected;
	}

	public Integer getIsValid() {
		if(isValid == null){
			isValid = 1;
		}
		return isValid;
	}

	public void setIsValid(Integer isValid) {
		this.isValid = isValid;
	}

	public String getInvalidMessage() {
		return invalidMessage;
	}

	public void setInvalidMessage(String invalidMessage) {
		this.invalidMessage = invalidMessage;
	}

	@Override
	public String getExpiryDate() {
		return expiryDate;
	}

	public Integer getCouponId() {
		if(couponId == null){
			couponId = Integer.valueOf(id);
		}
		return couponId;
	}

	public void setCouponId(Integer couponId) {
		this.couponId = couponId;
	}

	public boolean isCheckWithCouponId() {
		return checkWithCouponId;
	}

	public void setCheckWithCouponId(boolean checkWithCouponId) {
		this.checkWithCouponId = checkWithCouponId;
	}
	public ArrayList<Integer> getAllowedVehicles() {
		return allowedVehicles;
	}


	public int getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(int operatorId) {
		this.operatorId = operatorId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public double getDropRadius() {
		return dropRadius;
	}

	public void setDropRadius(double dropRadius) {
		this.dropRadius = dropRadius;
	}

	public ArrayList<LatLngCoordinates> getDropLocationCoordinates() {
		if(dropLocationCoordinates == null){
			dropLocationCoordinates = new ArrayList<>();
		}
		return dropLocationCoordinates;
	}

	public void setDropLocationCoordinates(ArrayList<LatLngCoordinates> dropLocationCoordinates) {
		this.dropLocationCoordinates = dropLocationCoordinates;
	}
}

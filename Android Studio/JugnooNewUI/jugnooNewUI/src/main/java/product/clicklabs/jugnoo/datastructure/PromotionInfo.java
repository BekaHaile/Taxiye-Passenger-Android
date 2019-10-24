package product.clicklabs.jugnoo.datastructure;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class PromotionInfo extends PromoCoupon implements Serializable{
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
	@SerializedName("delivery_customer")
	@Expose
	private Integer deliveryCustomer;
	@SerializedName("pay")
	@Expose
	private Integer pay;
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
	@SerializedName("promo_type")
	@Expose
	private int type;
	@SerializedName("drop_radius")
	@Expose
	private double dropRadius;
	@SerializedName("drop_location_coordinates")
	@Expose
	private ArrayList<LatLngCoordinates> dropLocationCoordinates;
	@SerializedName("promo_text")
	@Expose
	private String promoText;
	@SerializedName("applicable_on")
	@Expose
	private String applicableOn;
	@SerializedName("is_scratched")
	@Expose
	private int isScratched;
	@SerializedName("coupon_card_type")
	@Expose
	private int couponCardType;
	@SerializedName("benefit_type")
	@Expose
	private int benefitType;

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
	public void setIsPromoApplied(boolean isPromoApplied) {
		this.isPromoApplied = isPromoApplied?1:0;
	}

	@Override
	public void setMessageToDisplay(String messageToDisplay) {
		this.messageToDisplay = messageToDisplay;
	}


	@Override
	public String messageToDisplay(){
		return messageToDisplay;
	}

	public ArrayList<Integer> getAllowedVehicles() {
		return allowedVehicles;
	}

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
	public int couponId() {
		return id;
	}

	@Override
	public int benefitType() {
		return benefitType;
	}

	@Override
	public String getTitle() {
		if(title == null){
			title = "";
		}
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

	@Override
	public boolean isScratched() {
		return isScratched == 1;
	}

	@Override
	public void setIsScratched(int isScratched) {
		this.isScratched = isScratched;
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
		return endOn;
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

	public String getApplicableOn() {
		return applicableOn;
	}

	public void setApplicableOn(String applicableOn) {
		this.applicableOn = applicableOn;
	}

	public String getPromoText() {
		return promoText;
	}

	public void setPromoText(String promoText) {
		this.promoText = promoText;
	}

	public int getIsScratched() {
		return isScratched;
	}

	public Integer getCouponCardType() {
		return couponCardType;
	}
}

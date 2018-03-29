package com.sabkuchfresh.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sabkuchfresh.datastructure.ApplicablePaymentMode;
import com.sabkuchfresh.retrofit.model.menus.Charges;
import com.sabkuchfresh.retrofit.model.menus.Tax;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.datastructure.CouponInfo;
import product.clicklabs.jugnoo.datastructure.PromotionInfo;

/**
 * Created by shankar on 4/9/16.
 */
public class UserCheckoutResponse{

	@SerializedName("flag")
	@Expose
	private Integer flag;
	@SerializedName("message")
	@Expose
	private String message;
	@SerializedName("error")
	@Expose
	private String error;
	@SerializedName("checkout_data")
	@Expose
	private CheckoutData checkoutData;
	@SerializedName("promotions")
	@Expose
	private List<PromotionInfo> promotions = new ArrayList<>();
	@SerializedName("coupons")
	@Expose
	private List<CouponInfo> coupons = new ArrayList<>();

	@SerializedName("subscription")
	@Expose
	private Subscription subscription;
	@SerializedName("payment_info")
	@Expose
	private PaymentInfo paymentInfo;
	@SerializedName("vendor_info")
	@Expose
	private VendorInfo vendorInfo;
	@SerializedName("delivery_info")
	@Expose
	private DeliveryInfo deliveryInfo;
	@SerializedName("cart")
	@Expose
	private List<CartItem> cartItems;
	@SerializedName("cityId")
	@Expose
	private Integer cityId;
	@SerializedName("chat_available")
	@Expose
	private int chatAvailable;

	public int getChatAvailable() {
		return chatAvailable;
	}

	@SerializedName("subscription_info")
	@Expose
	private SubscriptionInfo subscriptionInfo;
	@SerializedName("show_star_subscriptions")
	@Expose
	private Integer showStarSubscriptions;
	@SerializedName("star_subscription_title")
	@Expose
	private String starSubscriptionTitle;
	@SerializedName("star_subscription_text")
	@Expose
	private String starSubscriptionText;
	@SerializedName("charges")
	@Expose
	private List<Charges> charges = null;
	@SerializedName("restaurant_info")
	@Expose
	private RestaurantInfo restaurantInfo;
	@SerializedName("refresh_on_cart_change")
	@Expose
	private Integer refreshOnCartChange;
	@SerializedName("taxes")
	@Expose
	private List<Tax> taxes;
	@SerializedName("discount_info")
	private DiscountInfo discountInfo;
	@SerializedName("text_early_bird_discount_enabled")
	private String  mealsDiscountEnabled;
	@SerializedName("text_early_bird_discount_disabled")
	private String mealsDiscountDisabled;

	public VendorInfo getVendorInfo() {
		return vendorInfo;
	}

	/**
	 *
	 * @return
	 * The flag
	 */
	public Integer getFlag() {
		return flag;
	}

	/**
	 *
	 * @param flag
	 * The flag
	 */
	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	/**
	 *
	 * @return
	 * The message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 *
	 * @param message
	 * The message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 *
	 * @return
	 * The checkoutData
	 */
	public CheckoutData getCheckoutData() {
		return checkoutData;
	}

	/**
	 *
	 * @param checkoutData
	 * The checkoutData
	 */
	public void setCheckoutData(CheckoutData checkoutData) {
		this.checkoutData = checkoutData;
	}


	public List<PromotionInfo> getPromotions() {
		return promotions;
	}

	public void setPromotions(List<PromotionInfo> promotions) {
		this.promotions = promotions;
	}

	public List<CouponInfo> getCoupons() {
		return coupons;
	}

	public void setCoupons(List<CouponInfo> coupons) {
		this.coupons = coupons;
	}

	public Subscription getSubscription() {
		if(subscription == null){
			subscription = new Subscription();
		}
		return subscription;
	}

	public DeliveryInfo getDeliveryInfo() {
		return deliveryInfo;
	}

	public void setDeliveryInfo(DeliveryInfo deliveryInfo) {
		this.deliveryInfo = deliveryInfo;
	}

	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
	}

	public List<CartItem> getCartItems() {
		return cartItems;
	}

	public void setCartItems(List<CartItem> cartItems) {
		this.cartItems = cartItems;
	}

	public Integer getCityId() {
		return cityId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	public SubscriptionInfo getSubscriptionInfo() {
		return subscriptionInfo;
	}

	public void setSubscriptionInfo(SubscriptionInfo subscriptionInfo) {
		this.subscriptionInfo = subscriptionInfo;
	}

	public PaymentInfo getPaymentInfo() {
		return paymentInfo;
	}

	public Integer getShowStarSubscriptions() {
		if(showStarSubscriptions == null)
			showStarSubscriptions = 0;
		return showStarSubscriptions;
	}

	public void setShowStarSubscriptions(Integer showStarSubscriptions) {
		this.showStarSubscriptions = showStarSubscriptions;
	}

	public String getStarSubscriptionText() {
		return starSubscriptionText;
	}

	public void setStarSubscriptionText(String starSubscriptionText) {
		this.starSubscriptionText = starSubscriptionText;
	}

	public String getStarSubscriptionTitle() {
		return starSubscriptionTitle;
	}

	public void setStarSubscriptionTitle(String starSubscriptionTitle) {
		this.starSubscriptionTitle = starSubscriptionTitle;
	}

	public List<Charges> getCharges() {
		return charges;
	}

	public void setCharges(List<Charges> charges) {
		this.charges = charges;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public RestaurantInfo getRestaurantInfo() {
		return restaurantInfo;
	}

	public void setRestaurantInfo(RestaurantInfo restaurantInfo) {
		this.restaurantInfo = restaurantInfo;
	}

	public Integer getRefreshOnCartChange() {
		if(refreshOnCartChange == null){
			refreshOnCartChange = 0;
		}
		return refreshOnCartChange;
	}

	public void setRefreshOnCartChange(Integer refreshOnCartChange) {
		this.refreshOnCartChange = refreshOnCartChange;
	}

	public List<Tax> getTaxes() {
		return taxes;
	}

	public void setTaxes(List<Tax> taxes) {
		this.taxes = taxes;
	}

	public double getTotalTaxValue(double subTotal, double discount){
		double totalTax = 0;
		if(taxes != null){
			for(Tax tax : taxes){
				totalTax = totalTax + tax.getCalculatedValue(subTotal, discount);
			}
		}
		return totalTax;
	}

	public DiscountInfo getDiscountInfo() {
		return discountInfo;
	}

	public String getDiscountSwitchMessage(boolean isDiscountEnabled) {
		return isDiscountEnabled?mealsDiscountEnabled:mealsDiscountDisabled;
	}

	public class PaymentInfo{
		@SerializedName("applicable_payment_mode")
		@Expose
		private Integer applicablePaymentMode;

		public Integer getApplicablePaymentMode() {
			return applicablePaymentMode;
		}
	}

	public class VendorInfo{
		@SerializedName("applicable_payment_mode")
		@Expose
		private Integer applicablePaymentMode;

		public int getApplicablePaymentMode() {
			if(applicablePaymentMode!=null){
				return applicablePaymentMode;
			}
			return ApplicablePaymentMode.BOTH.getOrdinal();
		}
	}

	public class Subscription {

		@SerializedName("discount")
		@Expose
		private Double discount;
		@SerializedName("discount_is_percent")
		@Expose
		private Integer discountIsPercent;
		@SerializedName("minimum_order_amount")
		@Expose
		private Double minimumOrderAmount;
		@SerializedName("delivery_charges")
		@Expose
		private Double deliveryCharges;
		@SerializedName("cashback")
		@Expose
		private Double cashback;
		@SerializedName("cashback_is_percent")
		@Expose
		private Integer cashbackIsPercent;
		@SerializedName("cashback_text")
		@Expose
		private String cashbackText;

		public Double getDiscount(Double totalAmount) {
			if(discount == null){
				discount = 0d;
			}
			return getDiscountIsPercent().equals(0) ? discount : (totalAmount * (discount / 100d));
		}

		public void setDiscount(Double discount) {
			this.discount = discount;
		}

		public Double getMinimumOrderAmount() {
			return minimumOrderAmount;
		}

		public void setMinimumOrderAmount(Double minimumOrderAmount) {
			this.minimumOrderAmount = minimumOrderAmount;
		}

		public Double getDeliveryCharges() {
			return deliveryCharges;
		}

		public void setDeliveryCharges(Double deliveryCharges) {
			this.deliveryCharges = deliveryCharges;
		}

		public Integer getDiscountIsPercent() {
			if(discountIsPercent == null){
				return 0;
			}
			return discountIsPercent;
		}

		public void setDiscountIsPercent(Integer discountIsPercent) {
			this.discountIsPercent = discountIsPercent;
		}


		public Double getCashback(Double totalAmount) {
			if(cashback == null){
				cashback = 0d;
			}
			return getCashbackIsPercent().equals(0) ? cashback : (totalAmount * (cashback / 100d));
		}


		public void setCashback(Double cashback) {
			this.cashback = cashback;
		}

		public Integer getCashbackIsPercent() {
			if(cashbackIsPercent == null){
				return 1;
			}
			return cashbackIsPercent;
		}

		public void setCashbackIsPercent(Integer cashbackIsPercent) {
			this.cashbackIsPercent = cashbackIsPercent;
		}

		public String getCashbackText() {
			return cashbackText;
		}

		public void setCashbackText(String cashbackText) {
			this.cashbackText = cashbackText;
		}
	}

	public class DeliveryInfo {

		@SerializedName("delivery_charges")
		@Expose
		private Double deliveryCharges;
		@SerializedName("selected_slot")
		@Expose
		private Integer selectedSlot;

		public Double getDeliveryCharges() {
			return deliveryCharges;
		}

		public void setDeliveryCharges(Double deliveryCharges) {
			this.deliveryCharges = deliveryCharges;
		}

		public Integer getSelectedSlot() {
			return selectedSlot;
		}

		public void setSelectedSlot(Integer selectedSlot) {
			this.selectedSlot = selectedSlot;
		}
	}

	public class CartItem {

		@SerializedName("sub_item_id")
		@Expose
		private Integer subItemId;
		@SerializedName("quantity")
		@Expose
		private Integer quantity;
		@SerializedName("super_category_id")
		@Expose
		private Integer superCategoryId;
		@SerializedName("category_id")
		@Expose
		private Integer categoryId;
		@SerializedName("sub_item_name")
		@Expose
		private String subItemName;
		@SerializedName("sub_item_image")
		@Expose
		private String subItemImage;
		@SerializedName("price")
		@Expose
		private Double price;
		@SerializedName("status")
		@Expose
		private Integer status;

		public Integer getSubItemId() {
			return subItemId;
		}

		public void setSubItemId(Integer subItemId) {
			this.subItemId = subItemId;
		}

		public Integer getQuantity() {
			if(quantity == null){
				quantity = 0;
			}
			return quantity;
		}

		public void setQuantity(Integer quantity) {
			this.quantity = quantity;
		}

		public Integer getSuperCategoryId() {
			return superCategoryId;
		}

		public void setSuperCategoryId(Integer superCategoryId) {
			this.superCategoryId = superCategoryId;
		}

		public Integer getCategoryId() {
			return categoryId;
		}

		public void setCategoryId(Integer categoryId) {
			this.categoryId = categoryId;
		}

		public String getSubItemName() {
			return subItemName;
		}

		public void setSubItemName(String subItemName) {
			this.subItemName = subItemName;
		}

		public String getSubItemImage() {
			return subItemImage;
		}

		public void setSubItemImage(String subItemImage) {
			this.subItemImage = subItemImage;
		}

		public Double getPrice() {
			return price;
		}

		public void setPrice(Double price) {
			this.price = price;
		}

		public Integer getStatus() {
			if(status == null){
				status = 0;
			}
			return status;
		}

		public void setStatus(Integer status) {
			this.status = status;
		}

		public CartItem(){}

		public CartItem(Integer subItemId){
			this.subItemId = subItemId;
		}

		@Override
		public boolean equals(Object o) {
			return (o instanceof CartItem) && ((CartItem)o).getSubItemId().equals(subItemId);
		}
	}

	public class SubscriptionInfo {

		@SerializedName("subscription_id")
		@Expose
		private Integer subscriptionId;
		@SerializedName("discount")
		@Expose
		private Integer discount;
		@SerializedName("discount_is_percent")
		@Expose
		private Integer discountIsPercent;
		@SerializedName("minimum_order_amount")
		@Expose
		private Integer minimumOrderAmount;
		@SerializedName("delivery_charges")
		@Expose
		private Integer deliveryCharges;
		@SerializedName("cashback")
		@Expose
		private Integer cashback;
		@SerializedName("cashback_is_percent")
		@Expose
		private Integer cashbackIsPercent;
		@SerializedName("cashback_text")
		@Expose
		private String cashbackText;
		@SerializedName("price")
		@Expose
		private Integer price;
		@SerializedName("plan_string")
		@Expose
		private String planString;
		@SerializedName("description")
		@Expose
		private String description;

		public Integer getSubscriptionId() {
			return subscriptionId;
		}

		public void setSubscriptionId(Integer subscriptionId) {
			this.subscriptionId = subscriptionId;
		}

		public Integer getDiscount() {
			return discount;
		}

		public void setDiscount(Integer discount) {
			this.discount = discount;
		}

		public Integer getDiscountIsPercent() {
			return discountIsPercent;
		}

		public void setDiscountIsPercent(Integer discountIsPercent) {
			this.discountIsPercent = discountIsPercent;
		}

		public Integer getMinimumOrderAmount() {
			return minimumOrderAmount;
		}

		public void setMinimumOrderAmount(Integer minimumOrderAmount) {
			this.minimumOrderAmount = minimumOrderAmount;
		}

		public Integer getDeliveryCharges() {
			return deliveryCharges;
		}

		public void setDeliveryCharges(Integer deliveryCharges) {
			this.deliveryCharges = deliveryCharges;
		}

		public Integer getCashback() {
			return cashback;
		}

		public void setCashback(Integer cashback) {
			this.cashback = cashback;
		}

		public Integer getCashbackIsPercent() {
			return cashbackIsPercent;
		}

		public void setCashbackIsPercent(Integer cashbackIsPercent) {
			this.cashbackIsPercent = cashbackIsPercent;
		}

		public String getCashbackText() {
			return cashbackText;
		}

		public void setCashbackText(String cashbackText) {
			this.cashbackText = cashbackText;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getPlanString() {
			return planString;
		}

		public void setPlanString(String planString) {
			this.planString = planString;
		}

		public Integer getPrice() {
			return price;
		}

		public void setPrice(Integer price) {
			this.price = price;
		}
	}

	public class RestaurantInfo{
		@SerializedName("address")
		@Expose
		private String address;

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}
	}
}

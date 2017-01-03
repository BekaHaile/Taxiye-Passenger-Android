package com.sabkuchfresh.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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

	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
	}

	public class Subscription {

		@SerializedName("discount")
		@Expose
		private Double discount;
		@SerializedName("minimum_order_amount")
		@Expose
		private Double minimumOrderAmount;
		@SerializedName("delivery_charges")
		@Expose
		private Double deliveryCharges;

		public Double getDiscount() {
			if(discount == null){
				return 0d;
			}
			return discount;
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

	}
}

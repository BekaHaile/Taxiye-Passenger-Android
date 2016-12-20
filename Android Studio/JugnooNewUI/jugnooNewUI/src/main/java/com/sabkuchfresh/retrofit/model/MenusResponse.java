package com.sabkuchfresh.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shankar on 11/15/16.
 */
public class MenusResponse {

	@SerializedName("flag")
	@Expose
	private Integer flag;
	@SerializedName("message")
	@Expose
	private String message;
	@SerializedName("vendors")
	@Expose
	private List<Vendor> vendors = new ArrayList<Vendor>();
	@SerializedName("support_contact")
	@Expose
	private String supportContact;
	@SerializedName("filter")
	@Expose
	private Filters filters;
	@SerializedName("recent_orders")
	@Expose
	private List<RecentOrder> recentOrders = new ArrayList<RecentOrder>();
	@SerializedName("recent_orders_possible_status")
	@Expose
	private List<String> recentOrdersPossibleStatus = new ArrayList<String>();
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
	 * The vendors
	 */
	public List<Vendor> getVendors() {
		return vendors;
	}

	/**
	 *
	 * @param vendors
	 * The vendors
	 */
	public void setVendors(List<Vendor> vendors) {
		this.vendors = vendors;
	}

	/**
	 *
	 * @return
	 * The supportContact
	 */
	public String getSupportContact() {
		return supportContact;
	}

	/**
	 *
	 * @param supportContact
	 * The support_contact
	 */
	public void setSupportContact(String supportContact) {
		this.supportContact = supportContact;
	}

	public Filters getFilters() {
		return filters;
	}

	public void setFilters(Filters filters) {
		this.filters = filters;
	}

	public List<RecentOrder> getRecentOrders() {
		return recentOrders;
	}

	public void setRecentOrders(List<RecentOrder> recentOrders) {
		this.recentOrders = recentOrders;
	}

	public List<String> getRecentOrdersPossibleStatus() {
		return recentOrdersPossibleStatus;
	}

	public void setRecentOrdersPossibleStatus(List<String> recentOrdersPossibleStatus) {
		this.recentOrdersPossibleStatus = recentOrdersPossibleStatus;
	}



	public class Vendor {

		@SerializedName("restaurant_id")
		@Expose
		private Integer restaurantId;
		@SerializedName("name")
		@Expose
		private String name;
		@SerializedName("image")
		@Expose
		private String image;
		@SerializedName("minimum_order_amount")
		@Expose
		private Double minimumOrderAmount;
		@SerializedName("is_closed")
		@Expose
		private Integer isClosed;

		@SerializedName("is_available")
		@Expose
		private Integer isAvailable;

		@SerializedName("distance")
		@Expose
		private Integer distance;
		@SerializedName("cuisines")
		@Expose
		private List<String> cuisines = new ArrayList<String>();
		@SerializedName("price_range")
		@Expose
		private Integer priceRange;
		@SerializedName("delivery_time")
		@Expose
		private Integer deliveryTime;
		@SerializedName("min_delivery_time")
		@Expose
		private Integer minDeliveryTime;
		@SerializedName("opens_at")
		@Expose
		private String opensAt;
		@SerializedName("popularity")
		@Expose
		private Integer popularity;
		@SerializedName("applicable_payment_mode")
		@Expose
		private Integer applicablePaymentMode;
		@SerializedName("service_tax")
		@Expose
		private Double serviceTax;
		@SerializedName("value_added_tax")
		@Expose
		private Double valueAddedTax;
		@SerializedName("packing_charges")
		@Expose
		private Double packingCharges;
		@SerializedName("packing_charges_percent")
		@Expose
		private Double packagingChargesInPercent;



		/**
		 *
		 * @return
		 * The restaurantId
		 */
		public Integer getRestaurantId() {
			return restaurantId;
		}

		/**
		 *
		 * @param restaurantId
		 * The restaurantId
		 */
		public void setRestaurantId(Integer restaurantId) {
			this.restaurantId = restaurantId;
		}

		/**
		 *
		 * @return
		 * The name
		 */
		public String getName() {
			return name;
		}

		/**
		 *
		 * @param name
		 * The vendor_name
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 *
		 * @return
		 * The image
		 */
		public String getImage() {
			return image;
		}

		/**
		 *
		 * @param image
		 * The image
		 */
		public void setImage(String image) {
			this.image = image;
		}

		/**
		 *
		 * @return
		 * The minimumOrderAmount
		 */
		public Double getMinimumOrderAmount() {
			return minimumOrderAmount;
		}

		/**
		 *
		 * @param minimumOrderAmount
		 * The minimum_order_amount
		 */
		public void setMinimumOrderAmount(Double minimumOrderAmount) {
			this.minimumOrderAmount = minimumOrderAmount;
		}

		/**
		 *
		 * @return
		 * The isClosed
		 */
		public Integer getIsClosed() {
			return isClosed;
		}

		/**
		 *
		 * @param isClosed
		 * The is_closed
		 */
		public void setIsClosed(Integer isClosed) {
			this.isClosed = isClosed;
		}


		public Integer getIsAvailable() {
			return isAvailable;
		}

		/**
		 *
		 * @param isAvailable
		 * The is_closed
		 */
		public void setIsAvailable(Integer isAvailable) {
			this.isAvailable = isAvailable;
		}

		/**
		 *
		 * @return
		 * The distance
		 */
		public Integer getDistance() {
			return distance;
		}

		/**
		 *
		 * @param distance
		 * The distance
		 */
		public void setDistance(Integer distance) {
			this.distance = distance;
		}

		/**
		 *
		 * @return
		 * The cuisines
		 */
		public List<String> getCuisines() {
			return cuisines;
		}

		/**
		 *
		 * @param cuisines
		 * The cuisines
		 */
		public void setCuisines(List<String> cuisines) {
			this.cuisines = cuisines;
		}

		public Integer getPriceRange() {
			return priceRange;
		}

		public void setPriceRange(Integer priceRange) {
			this.priceRange = priceRange;
		}

		public Integer getDeliveryTime() {
			return deliveryTime;
		}

		public void setDeliveryTime(Integer deliveryTime) {
			this.deliveryTime = deliveryTime;
		}

		public String getOpensAt() {
			return opensAt;
		}

		public void setOpensAt(String opensAt) {
			this.opensAt = opensAt;
		}

		public Integer getPopularity() {
			return popularity;
		}

		public void setPopularity(Integer popularity) {
			this.popularity = popularity;
		}

		public Integer getMinDeliveryTime() {
			return minDeliveryTime;
		}

		public void setMinDeliveryTime(Integer minDeliveryTime) {
			this.minDeliveryTime = minDeliveryTime;
		}

		public Integer getApplicablePaymentMode() {
			return applicablePaymentMode;
		}

		public void setApplicablePaymentMode(Integer applicablePaymentMode) {
			this.applicablePaymentMode = applicablePaymentMode;
		}

		public Double getServiceTax() {
			return serviceTax;
		}

		public void setServiceTax(Double serviceTax) {
			this.serviceTax = serviceTax;
		}

		public Double getValueAddedTax() {
			return valueAddedTax;
		}

		public void setValueAddedTax(Double vat) {
			this.valueAddedTax = vat;
		}

		public Double getPackingCharges() {
			return packingCharges;
		}

		public void setPackingCharges(Double packingCharges) {
			this.packingCharges = packingCharges;
		}

		public Double getPackagingChargesInPercent() {
			return packagingChargesInPercent;
		}

		public void setPackagingChargesInPercent(Double packagingChargesInPercent) {
			this.packagingChargesInPercent = packagingChargesInPercent;
		}

	}


	public class Filters{

		@SerializedName("price_range")
		@Expose
		private List<Integer> priceRange = new ArrayList<Integer>();
		@SerializedName("delivery_time")
		@Expose
		private List<Integer> deliveryTime = new ArrayList<Integer>();
		@SerializedName("minimum_order_amount")
		@Expose
		private List<Integer> minimumOrderAmount = new ArrayList<Integer>();
		@SerializedName("is_closed")
		@Expose
		private List<Integer> isClosed = new ArrayList<Integer>();
		@SerializedName("distance")
		@Expose
		private List<Integer> distance = new ArrayList<Integer>();
		@SerializedName("cuisines")
		@Expose
		private List<String> cuisines = new ArrayList<String>();


		public List<String> getCuisines() {
			return cuisines;
		}

		public void setCuisines(List<String> cuisines) {
			this.cuisines = cuisines;
		}

		public List<Integer> getPriceRange() {
			return priceRange;
		}

		public void setPriceRange(List<Integer> priceRange) {
			this.priceRange = priceRange;
		}

		public List<Integer> getDeliveryTime() {
			return deliveryTime;
		}

		public void setDeliveryTime(List<Integer> deliveryTime) {
			this.deliveryTime = deliveryTime;
		}

		public List<Integer> getMinimumOrderAmount() {
			return minimumOrderAmount;
		}

		public void setMinimumOrderAmount(List<Integer> minimumOrderAmount) {
			this.minimumOrderAmount = minimumOrderAmount;
		}

		public List<Integer> getIsClosed() {
			return isClosed;
		}

		public void setIsClosed(List<Integer> isClosed) {
			this.isClosed = isClosed;
		}

		public List<Integer> getDistance() {
			return distance;
		}

		public void setDistance(List<Integer> distance) {
			this.distance = distance;
		}
	}

}

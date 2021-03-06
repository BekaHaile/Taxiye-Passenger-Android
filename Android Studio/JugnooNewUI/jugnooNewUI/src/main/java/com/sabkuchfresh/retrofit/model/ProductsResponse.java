package com.sabkuchfresh.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shankar on 4/7/16.
 */
public class ProductsResponse {

	@SerializedName("flag")
	@Expose
	private Integer flag;
	@SerializedName("message")
	@Expose
	private String message;
	@SerializedName("categories")
	@Expose
	private List<Category> categories = new ArrayList<>();

	@SerializedName("show_message")
	@Expose
	private Integer showMessage;
	@SerializedName("support_contact")
	@Expose
	private String supportContact;
	@SerializedName("recent_orders")
	@Expose
	private List<RecentOrder> recentOrders = new ArrayList<RecentOrder>();
	@SerializedName("recent_orders_possible_status")
	@Expose
	private List<String> recentOrdersPossibleStatus = new ArrayList<String>();
	@SerializedName("subscription_message")
	@Expose
	private String subscriptionMessage;
	@SerializedName("delivery_info")
	@Expose
	private DeliveryInfo deliveryInfo;
	@SerializedName("delivery_stores")
	@Expose
	private List<DeliveryStore> deliveryStores = new ArrayList<DeliveryStore>();
	@SerializedName("meals_bulk_banner")
	@Expose
	private MealsBulkBanner mealsBulkBanner;

	@SerializedName("discount_info")
	@Expose
	private DiscountInfo discountInfo;


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
	 * The categories
	 */
	public List<Category> getCategories() {
		return categories;
	}

	/**
	 *
	 * @param categories
	 * The categories
	 */
	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}


	public Integer getShowMessage() {
		return showMessage;
	}

	public void setShowMessage(Integer showMessage) {
		this.showMessage = showMessage;
	}

	public String getSupportContact() {
		return supportContact;
	}

	public void setSupportContact(String supportContact) {
		this.supportContact = supportContact;
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

	public String getSubscriptionMessage() {
		return subscriptionMessage;
	}

	public void setSubscriptionMessage(String subscriptionMessage) {
		this.subscriptionMessage = subscriptionMessage;
	}

	public DeliveryInfo getDeliveryInfo() {
		return deliveryInfo;
	}

	public void setDeliveryInfo(DeliveryInfo deliveryInfo) {
		this.deliveryInfo = deliveryInfo;
	}

	public List<DeliveryStore> getDeliveryStores() {
		return deliveryStores;
	}

	public void setDeliveryStores(List<DeliveryStore> deliveryStores) {
		this.deliveryStores = deliveryStores;
	}

	public MealsBulkBanner getMealsBulkBanner() {
		return mealsBulkBanner;
	}

	public DiscountInfo getDiscountInfo() {
		return discountInfo;
	}

	public class MealsBulkBanner {
		@SerializedName("image_url_2x")
		private String imageUrl2X;
		@SerializedName("image_url_3x")
		private String imageUrl3X;
		@SerializedName("meals_banner_enabled")
		private int mealsBannerEnabled;
		@SerializedName("open_next_page")
		private int openNextPage;
		@SerializedName("next_page_image_2x")
		private String nextPageImage;

		public String getImageUrl2X() {
			return imageUrl2X;
		}

		public String getImageUrl3X() {
			return imageUrl3X;
		}

		public int getMealsBannerEnabled() {
			return mealsBannerEnabled;
		}

		public int getOpenNextPage() {
			return openNextPage;
		}

		public String getNextPageImage() {
			return nextPageImage;
		}
	}

}

package com.sabkuchfresh.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shankar on 1/21/17.
 */

public class SuperCategoriesData {

	@SerializedName("flag")
	@Expose
	private Integer flag;
	@SerializedName("message")
	@Expose
	private String message;
	@SerializedName("super_categories")
	@Expose
	private List<SuperCategory> superCategories = null;
	@SerializedName("support_contact")
	@Expose
	private String supportContact;
	@SerializedName("show_message")
	@Expose
	private Integer showMessage;
	@SerializedName("delivery_info")
	@Expose
	private DeliveryInfo deliveryInfo;
	@SerializedName("ads")
	@Expose
	private List<SuperCategory> ads = new ArrayList<>();
	@SerializedName("subscription_message")
	@Expose
	private String subscriptionMessage;


	public Integer getFlag() {
		return flag;
	}

	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<SuperCategory> getSuperCategories() {
		return superCategories;
	}

	public void setSuperCategories(List<SuperCategory> superCategories) {
		this.superCategories = superCategories;
	}

	public String getSupportContact() {
		return supportContact;
	}

	public void setSupportContact(String supportContact) {
		this.supportContact = supportContact;
	}

	public Integer getShowMessage() {
		return showMessage;
	}

	public void setShowMessage(Integer showMessage) {
		this.showMessage = showMessage;
	}

	public DeliveryInfo getDeliveryInfo() {
		return deliveryInfo;
	}

	public void setDeliveryInfo(DeliveryStore deliveryStore) {
		this.deliveryInfo = new DeliveryInfo();
		deliveryInfo.setCityId(deliveryStore.getCityId());
		deliveryInfo.setDeliveryCharges(deliveryStore.getDeliveryCharges());
		deliveryInfo.setDynamicDeliveryCharges(deliveryStore.getDynamicDeliveryCharges());
		deliveryInfo.setMinAmount(deliveryStore.getMinAmount());
		deliveryInfo.setStoreId(deliveryStore.getStoreId());
		deliveryInfo.setVendorAddress(deliveryStore.getVendorAddress());
		deliveryInfo.setVendorId(deliveryStore.getVendorId());
		deliveryInfo.setVendorName(deliveryStore.getVendorName());
		deliveryInfo.setVendorPhone(deliveryStore.getVendorPhone());
	}

	public List<SuperCategory> getAds() {
		return ads;
	}

	public void setAds(List<SuperCategory> ads) {
		this.ads = ads;
	}

	public String getSubscriptionMessage() {
		return subscriptionMessage;
	}

	public void setSubscriptionMessage(String subscriptionMessage) {
		this.subscriptionMessage = subscriptionMessage;
	}

	public class SuperCategory implements Serializable{

		@SerializedName("super_category_id")
		@Expose
		private Integer superCategoryId;
		@SerializedName("super_category_name")
		@Expose
		private String superCategoryName;
		@SerializedName("priority_id")
		@Expose
		private Integer priorityId;
		@SerializedName("super_category_image")
		@Expose
		private String superCategoryImage;
		@SerializedName("show_category_banner")
		@Expose
		private Integer showCategoryBanner;
		@SerializedName("super_category_banner")
		@Expose
		private SuperCategoryBanner superCategoryBanner;
		@SerializedName("categories")
		@Expose
		private List<Category> categories;
		@SerializedName("is_enabled")
		@Expose
		private Integer isEnabled;
		@SerializedName("title")
		@Expose
		private String title;
		@SerializedName("description")
		@Expose
		private String description;
		@SerializedName("image_url")
		@Expose
		private String imageUrl;
		@SerializedName("vendor_id")
		@Expose
		private Integer vendorId;
		@SerializedName("is_ad")
		@Expose
		private Integer isAd;

		public Integer getSuperCategoryId() {
			return superCategoryId;
		}

		public void setSuperCategoryId(Integer superCategoryId) {
			this.superCategoryId = superCategoryId;
		}

		public String getSuperCategoryName() {
			return superCategoryName;
		}

		public void setSuperCategoryName(String superCategoryName) {
			this.superCategoryName = superCategoryName;
		}

		public Integer getPriorityId() {
			return priorityId;
		}

		public void setPriorityId(Integer priorityId) {
			this.priorityId = priorityId;
		}

		public String getSuperCategoryImage() {
			return superCategoryImage;
		}

		public void setSuperCategoryImage(String superCategoryImage) {
			this.superCategoryImage = superCategoryImage;
		}

		public Integer getShowCategoryBanner() {
			return showCategoryBanner;
		}

		public void setShowCategoryBanner(Integer showCategoryBanner) {
			this.showCategoryBanner = showCategoryBanner;
		}

		public SuperCategoryBanner getSuperCategoryBanner() {
			return superCategoryBanner;
		}

		public void setSuperCategoryBanner(SuperCategoryBanner superCategoryBanner) {
			this.superCategoryBanner = superCategoryBanner;
		}

		public List<Category> getCategories() {
			return categories;
		}

		public void setCategories(List<Category> categories) {
			this.categories = categories;
		}

		public Integer getIsEnabled() {
			return isEnabled;
		}

		public void setIsEnabled(Integer isEnabled) {
			this.isEnabled = isEnabled;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getImageUrl() {
			return imageUrl;
		}

		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}

		public Integer getVendorId() {
			return vendorId;
		}

		public void setVendorId(Integer vendorId) {
			this.vendorId = vendorId;
		}

		public Integer getIsAd() {
			return isAd;
		}

		public void setIsAd(Integer isAd) {
			this.isAd = isAd;
		}
	}

	public class SuperCategoryBanner {

		@SerializedName("small_image")
		@Expose
		private Object smallImage;
		@SerializedName("large_image")
		@Expose
		private Object largeImage;
		@SerializedName("description")
		@Expose
		private Object description;
		@SerializedName("banner_text")
		@Expose
		private String bannerText;
		@SerializedName("banner_text_color")
		@Expose
		private String bannerTextColor;
		@SerializedName("banner_color")
		@Expose
		private String bannerColor;

		public Object getSmallImage() {
			return smallImage;
		}

		public void setSmallImage(Object smallImage) {
			this.smallImage = smallImage;
		}

		public Object getLargeImage() {
			return largeImage;
		}

		public void setLargeImage(Object largeImage) {
			this.largeImage = largeImage;
		}

		public Object getDescription() {
			return description;
		}

		public void setDescription(Object description) {
			this.description = description;
		}

		public String getBannerText() {
			return bannerText;
		}

		public void setBannerText(String bannerText) {
			this.bannerText = bannerText;
		}

		public String getBannerTextColor() {
			return bannerTextColor;
		}

		public void setBannerTextColor(String bannerTextColor) {
			this.bannerTextColor = bannerTextColor;
		}

		public String getBannerColor() {
			return bannerColor;
		}

		public void setBannerColor(String bannerColor) {
			this.bannerColor = bannerColor;
		}
	}

}

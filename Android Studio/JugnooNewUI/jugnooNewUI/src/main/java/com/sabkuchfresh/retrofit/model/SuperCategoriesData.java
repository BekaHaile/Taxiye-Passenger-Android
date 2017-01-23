package com.sabkuchfresh.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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

	public void setDeliveryInfo(DeliveryInfo deliveryInfo) {
		this.deliveryInfo = deliveryInfo;
	}

	public class SuperCategory {

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

	}


}

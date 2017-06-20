package com.sabkuchfresh.pros.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by shankar on 20/06/17.
 */

public class ProsProductData{

	@SerializedName("message")
	@Expose
	private String message;
	@SerializedName("status")
	@Expose
	private int status;
	@SerializedName("data")
	@Expose
	private List<ProsProductDatum> data = null;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public List<ProsProductDatum> getData() {
		return data;
	}

	public void setData(List<ProsProductDatum> data) {
		this.data = data;
	}

	public static class ProsProductDatum implements Serializable{

		@SerializedName("product_id")
		@Expose
		private int productId;
		@SerializedName("name")
		@Expose
		private String name;
		@SerializedName("description")
		@Expose
		private Object description;
		@SerializedName("parent_category_id")
		@Expose
		private int parentCategoryId;
		@SerializedName("user_id")
		@Expose
		private int userId;
		@SerializedName("form_id")
		@Expose
		private int formId;
		@SerializedName("is_enabled")
		@Expose
		private int isEnabled;
		@SerializedName("image_url")
		@Expose
		private String imageUrl;
		@SerializedName("geofence_id")
		@Expose
		private Object geofenceId;
		@SerializedName("layout_id")
		@Expose
		private String layoutId;
		@SerializedName("priority")
		@Expose
		private Object priority;
		@SerializedName("creation_datetime")
		@Expose
		private String creationDatetime;
		@SerializedName("pricing_id")
		@Expose
		private int pricingId;
		@SerializedName("base_unit_id")
		@Expose
		private int baseUnitId;
		@SerializedName("price")
		@Expose
		private int price;
		@SerializedName("unit_description")
		@Expose
		private String unitDescription;
		@SerializedName("unit")
		@Expose
		private String unit;
		@SerializedName("conversion_factor")
		@Expose
		private int conversionFactor;
		@SerializedName("layout_data")
		@Expose
		private ProsCatalogueData.LayoutData layoutData;

		public int getProductId() {
			return productId;
		}

		public void setProductId(int productId) {
			this.productId = productId;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Object getDescription() {
			return description;
		}

		public void setDescription(Object description) {
			this.description = description;
		}

		public int getParentCategoryId() {
			return parentCategoryId;
		}

		public void setParentCategoryId(int parentCategoryId) {
			this.parentCategoryId = parentCategoryId;
		}

		public int getUserId() {
			return userId;
		}

		public void setUserId(int userId) {
			this.userId = userId;
		}

		public int getFormId() {
			return formId;
		}

		public void setFormId(int formId) {
			this.formId = formId;
		}

		public int getIsEnabled() {
			return isEnabled;
		}

		public void setIsEnabled(int isEnabled) {
			this.isEnabled = isEnabled;
		}

		public String getImageUrl() {
			return imageUrl;
		}

		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}

		public Object getGeofenceId() {
			return geofenceId;
		}

		public void setGeofenceId(Object geofenceId) {
			this.geofenceId = geofenceId;
		}

		public String getLayoutId() {
			return layoutId;
		}

		public void setLayoutId(String layoutId) {
			this.layoutId = layoutId;
		}

		public Object getPriority() {
			return priority;
		}

		public void setPriority(Object priority) {
			this.priority = priority;
		}

		public String getCreationDatetime() {
			return creationDatetime;
		}

		public void setCreationDatetime(String creationDatetime) {
			this.creationDatetime = creationDatetime;
		}

		public int getPricingId() {
			return pricingId;
		}

		public void setPricingId(int pricingId) {
			this.pricingId = pricingId;
		}

		public int getBaseUnitId() {
			return baseUnitId;
		}

		public void setBaseUnitId(int baseUnitId) {
			this.baseUnitId = baseUnitId;
		}

		public int getPrice() {
			return price;
		}

		public void setPrice(int price) {
			this.price = price;
		}

		public String getUnitDescription() {
			return unitDescription;
		}

		public void setUnitDescription(String unitDescription) {
			this.unitDescription = unitDescription;
		}

		public String getUnit() {
			return unit;
		}

		public void setUnit(String unit) {
			this.unit = unit;
		}

		public int getConversionFactor() {
			return conversionFactor;
		}

		public void setConversionFactor(int conversionFactor) {
			this.conversionFactor = conversionFactor;
		}

		public ProsCatalogueData.LayoutData getLayoutData() {
			return layoutData;
		}

		public void setLayoutData(ProsCatalogueData.LayoutData layoutData) {
			this.layoutData = layoutData;
		}

	}

}
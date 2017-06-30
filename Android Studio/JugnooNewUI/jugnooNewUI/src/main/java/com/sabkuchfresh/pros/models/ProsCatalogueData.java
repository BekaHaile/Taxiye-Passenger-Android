package com.sabkuchfresh.pros.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by shankar on 20/06/17.
 */

public class ProsCatalogueData {

	@SerializedName("message")
	@Expose
	private String message;
	@SerializedName("error")
	@Expose
	private String error;
	@SerializedName("flag")
	@Expose
	private int flag;
	@SerializedName("catalogue_data")
	@Expose
	private List<List<ProsCatalogueDatum>> data = null;
	@SerializedName("current_orders")
	@Expose
	private List<CurrentOrder> currentOrders = null;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<List<ProsCatalogueDatum>> getData() {
		return data;
	}

	public void setData(List<List<ProsCatalogueDatum>> data) {
		this.data = data;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public List<CurrentOrder> getCurrentOrders() {
		return currentOrders;
	}

	public void setCurrentOrders(List<CurrentOrder> currentOrders) {
		this.currentOrders = currentOrders;
	}

	public static class ProsCatalogueDatum implements Serializable {

		@SerializedName("catalogue_id")
		@Expose
		private int catalogueId;
		@SerializedName("parent_category_id")
		@Expose
		private Object parentCategoryId;
		@SerializedName("user_id")
		@Expose
		private int userId;
		@SerializedName("form_id")
		@Expose
		private int formId;
		@SerializedName("name")
		@Expose
		private String name;
		@SerializedName("description")
		@Expose
		private Object description;
		@SerializedName("image_url")
		@Expose
		private String imageUrl;
		@SerializedName("layout_type")
		@Expose
		private Object layoutType;
		@SerializedName("child_layout_type")
		@Expose
		private Object childLayoutType;
		@SerializedName("layout_id")
		@Expose
		private String layoutId;
		@SerializedName("priority")
		@Expose
		private Object priority;
		@SerializedName("is_dummy")
		@Expose
		private int isDummy;
		@SerializedName("is_enabled")
		@Expose
		private int isEnabled;
		@SerializedName("level")
		@Expose
		private int level;
		@SerializedName("creation_datetime")
		@Expose
		private String creationDatetime;
		@SerializedName("parent_index")
		@Expose
		private Object parentIndex;
		@SerializedName("layout_data")
		@Expose
		private LayoutData layoutData;

		public int getCatalogueId() {
			return catalogueId;
		}

		public void setCatalogueId(int catalogueId) {
			this.catalogueId = catalogueId;
		}

		public Object getParentCategoryId() {
			return parentCategoryId;
		}

		public void setParentCategoryId(Object parentCategoryId) {
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

		public String getImageUrl() {
			return imageUrl;
		}

		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}

		public Object getLayoutType() {
			return layoutType;
		}

		public void setLayoutType(Object layoutType) {
			this.layoutType = layoutType;
		}

		public Object getChildLayoutType() {
			return childLayoutType;
		}

		public void setChildLayoutType(Object childLayoutType) {
			this.childLayoutType = childLayoutType;
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

		public int getIsDummy() {
			return isDummy;
		}

		public void setIsDummy(int isDummy) {
			this.isDummy = isDummy;
		}

		public int getIsEnabled() {
			return isEnabled;
		}

		public void setIsEnabled(int isEnabled) {
			this.isEnabled = isEnabled;
		}

		public int getLevel() {
			return level;
		}

		public void setLevel(int level) {
			this.level = level;
		}

		public String getCreationDatetime() {
			return creationDatetime;
		}

		public void setCreationDatetime(String creationDatetime) {
			this.creationDatetime = creationDatetime;
		}

		public Object getParentIndex() {
			return parentIndex;
		}

		public void setParentIndex(Object parentIndex) {
			this.parentIndex = parentIndex;
		}

		public LayoutData getLayoutData() {
			return layoutData;
		}

		public void setLayoutData(LayoutData layoutData) {
			this.layoutData = layoutData;
		}

	}

	public class Image {

		@SerializedName("data")
		@Expose
		private String data;
		@SerializedName("style")
		@Expose
		private int style;

		public String getData() {
			return data;
		}

		public void setData(String data) {
			this.data = data;
		}

		public int getStyle() {
			return style;
		}

		public void setStyle(int style) {
			this.style = style;
		}

	}

	public class LayoutData  {

		@SerializedName("_id")
		@Expose
		private String id;
		@SerializedName("name")
		@Expose
		private String name;
		@SerializedName("is_enabled")
		@Expose
		private int isEnabled;
		@SerializedName("user_id")
		@Expose
		private int userId;
		@SerializedName("lines")
		@Expose
		private List<Line> lines = null;
		@SerializedName("images")
		@Expose
		private List<Image> images = null;
		@SerializedName("buttons")
		@Expose
		private List<Button> buttons = null;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getIsEnabled() {
			return isEnabled;
		}

		public void setIsEnabled(int isEnabled) {
			this.isEnabled = isEnabled;
		}

		public int getUserId() {
			return userId;
		}

		public void setUserId(int userId) {
			this.userId = userId;
		}

		public List<Line> getLines() {
			return lines;
		}

		public void setLines(List<Line> lines) {
			this.lines = lines;
		}

		public List<Image> getImages() {
			return images;
		}

		public void setImages(List<Image> images) {
			this.images = images;
		}

		public List<Button> getButtons() {
			return buttons;
		}

		public void setButtons(List<Button> buttons) {
			this.buttons = buttons;
		}

	}

	public class Line {

		@SerializedName("data")
		@Expose
		private String data;
		@SerializedName("style")
		@Expose
		private int style;

		public String getData() {
			return data;
		}

		public void setData(String data) {
			this.data = data;
		}

		public int getStyle() {
			return style;
		}

		public void setStyle(int style) {
			this.style = style;
		}

	}

	public class Button {

		@SerializedName("type")
		@Expose
		private int type;

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

	}

	public class CurrentOrder {

		@SerializedName("job_id")
		@Expose
		private int jobId;
		@SerializedName("job_time")
		@Expose
		private String jobTime;
		@SerializedName("job_status")
		@Expose
		private int jobStatus;
		@SerializedName("job_description")
		@Expose
		private String jobDescription;
		@SerializedName("job_address")
		@Expose
		private String jobAddress;
		@SerializedName("job_pickup_datetime")
		@Expose
		private String jobPickupDatetime;
		@SerializedName("job_delivery_datetime")
		@Expose
		private String jobDeliveryDatetime;
		@SerializedName("support_category")
		@Expose
		private int supportCategory;

		public int getJobId() {
			return jobId;
		}

		public void setJobId(int jobId) {
			this.jobId = jobId;
		}

		public String getJobTime() {
			return jobTime;
		}

		public void setJobTime(String jobTime) {
			this.jobTime = jobTime;
		}

		public int getJobStatus() {
			return jobStatus;
		}

		public void setJobStatus(int jobStatus) {
			this.jobStatus = jobStatus;
		}

		public String getJobDescription() {
			return jobDescription;
		}

		public String getJobNameSplitted() {
			String[] arr = jobDescription.split("\\#\\#\\#\\#\\^\\^\\^\\^\\#\\#\\#\\#");
			return arr[0];
		}

		public void setJobDescription(String jobDescription) {
			this.jobDescription = jobDescription;
		}

		public String getJobAddress() {
			return jobAddress;
		}

		public void setJobAddress(String jobAddress) {
			this.jobAddress = jobAddress;
		}

		public String getJobPickupDatetime() {
			return jobPickupDatetime;
		}

		public void setJobPickupDatetime(String jobPickupDatetime) {
			this.jobPickupDatetime = jobPickupDatetime;
		}

		public String getJobDeliveryDatetime() {
			return jobDeliveryDatetime;
		}

		public void setJobDeliveryDatetime(String jobDeliveryDatetime) {
			this.jobDeliveryDatetime = jobDeliveryDatetime;
		}

		public int getSupportCategory() {
			return supportCategory;
		}

		public void setSupportCategory(int supportCategory) {
			this.supportCategory = supportCategory;
		}
	}

}

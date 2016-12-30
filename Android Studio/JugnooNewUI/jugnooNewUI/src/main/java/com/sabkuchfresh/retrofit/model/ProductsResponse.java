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
	@SerializedName("delivery_info")
	@Expose
	private DeliveryInfo deliveryInfo;
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
	@SerializedName("charges")
	@Expose
	private List<Charges> charges = new ArrayList<Charges>();

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

	public DeliveryInfo getDeliveryInfo() {
		return deliveryInfo;
	}

	public void setDeliveryInfo(DeliveryInfo deliveryInfo) {
		this.deliveryInfo = deliveryInfo;
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

	public List<Charges> getCharges() {
		return charges;
	}

	public void setCharges(List<Charges> charges) {
		this.charges = charges;
	}

	public class Charges
	{
		@SerializedName("id")
		@Expose
		private Integer id;
		@SerializedName("text")
		@Expose
		private String text;
		@SerializedName("type")
		@Expose
		private Integer type;
		@SerializedName("is_percent")
		@Expose
		private Integer isPercent;
		@SerializedName("value")
		@Expose
		private String value;
		@SerializedName("included_values")
		@Expose
		private List<Integer> includeValue = new ArrayList<Integer>();
		@SerializedName("force_show")
		@Expose
		private Integer forceShow;


		public Charges(){}
		public Charges(Integer id){
			this.id = id;
		}

		@Override
		public boolean equals(Object o) {
			if(o instanceof Charges){
				return ((Charges)o).getId().equals(getId());
			} else {
				return false;
			}
		}

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public Integer getType() {
			if(type == null){
				return 0;
			}
			return type;
		}

		public void setType(Integer type) {
			this.type = type;
		}

		public Integer getIsPercent() {
			return isPercent;
		}

		public void setIsPercent(Integer isPercent) {
			this.isPercent = isPercent;
		}


		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public List<Integer> getIncludeValue() {
			if(includeValue == null){
				includeValue = new ArrayList<>();
			}
			return includeValue;
		}

		public void setIncludeValue(List<Integer> includeValue){
			this.includeValue = includeValue;
		}

		public Integer getForceShow() {
			return forceShow;
		}

		public void setForceShow(Integer forceShow) {
			this.forceShow = forceShow;
		}
	}


	public enum ChargeType{
		SUBTOTAL_LEVEL(0),
		ITEM_LEVEL(1)
		;

		private int ordinal;
		ChargeType(int ordinal){
			this.ordinal = ordinal;
		}

		public int getOrdinal() {
			return ordinal;
		}
	}

}

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


	public class Vendor {

		@SerializedName("vid")
		@Expose
		private Integer vid;
		@SerializedName("vendor_name")
		@Expose
		private String vendorName;
		@SerializedName("image")
		@Expose
		private String image;
		@SerializedName("minimum_order_amount")
		@Expose
		private Double minimumOrderAmount;
		@SerializedName("is_closed")
		@Expose
		private Integer isClosed;
		@SerializedName("distance")
		@Expose
		private Integer distance;
		@SerializedName("cuisines")
		@Expose
		private List<String> cuisines = new ArrayList<String>();

		/**
		 *
		 * @return
		 * The vid
		 */
		public Integer getVid() {
			return vid;
		}

		/**
		 *
		 * @param vid
		 * The vid
		 */
		public void setVid(Integer vid) {
			this.vid = vid;
		}

		/**
		 *
		 * @return
		 * The vendorName
		 */
		public String getVendorName() {
			return vendorName;
		}

		/**
		 *
		 * @param vendorName
		 * The vendor_name
		 */
		public void setVendorName(String vendorName) {
			this.vendorName = vendorName;
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

	}
}

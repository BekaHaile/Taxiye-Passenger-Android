package product.clicklabs.jugnoo.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shankar on 11/4/16.
 */

public class FetchUserAddressResponse {

	@SerializedName("flag")
	@Expose
	private Integer flag;
	@SerializedName("message")
	@Expose
	private String message;
	@SerializedName("addresses")
	@Expose
	private List<Address> addresses = new ArrayList<Address>();
	@SerializedName("additional_addresses")
	@Expose
	private List<Address> additionalAddresses = new ArrayList<Address>();

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
	 * The addresses
	 */
	public List<Address> getAddresses() {
		return addresses;
	}

	/**
	 *
	 * @param addresses
	 * The addresses
	 */
	public void setAddresses(List<Address> addresses) {
		this.addresses = addresses;
	}

	public List<Address> getAdditionalAddresses() {
		return additionalAddresses;
	}

	public void setAdditionalAddresses(List<Address> additionalAddresses) {
		this.additionalAddresses = additionalAddresses;
	}

	public class Address {

		@SerializedName("addr")
		@Expose
		private String addr;
		@SerializedName("lat")
		@Expose
		private Double lat;
		@SerializedName("lng")
		@Expose
		private Double lng;
		@SerializedName("freq")
		@Expose
		private Integer freq;
		@SerializedName("type")
		@Expose
		private String type;
		@SerializedName("id")
		@Expose
		private Integer id = 0;
		@SerializedName("google_place_id")
		@Expose
		private String placeId;
		@SerializedName("is_confirmed")
		@Expose
		private Integer isConfirmed = 0;

		/**
		 *
		 * @return
		 * The addr
		 */
		public String getAddr() {
			if(addr != null) {
				return addr;
			} else{
				return "";
			}
		}

		/**
		 *
		 * @param addr
		 * The addr
		 */
		public void setAddr(String addr) {
			this.addr = addr;
		}

		/**
		 *
		 * @return
		 * The lat
		 */
		public Double getLat() {
			return lat;
		}

		/**
		 *
		 * @param lat
		 * The lat
		 */
		public void setLat(Double lat) {
			this.lat = lat;
		}

		/**
		 *
		 * @return
		 * The lng
		 */
		public Double getLng() {
			return lng;
		}

		/**
		 *
		 * @param lng
		 * The lng
		 */
		public void setLng(Double lng) {
			this.lng = lng;
		}

		/**
		 *
		 * @return
		 * The freq
		 */
		public Integer getFreq() {
			return freq;
		}

		/**
		 *
		 * @param freq
		 * The freq
		 */
		public void setFreq(Integer freq) {
			this.freq = freq;
		}

		/**
		 *
		 * @return
		 * The type
		 */
		public String getType() {
			if(type != null) {
				return type;
			} else{
				return "";
			}
		}

		/**
		 *
		 * @param type
		 * The type
		 */
		public void setType(String type) {
			this.type = type;
		}

		public Integer getId() {
			if(id != null) {
				return id;
			} else{
				return 0;
			}
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public String getPlaceId() {
			if(placeId != null) {
				return placeId;
			} else{
				return "";
			}
		}

		public void setPlaceId(String placeId) {
			this.placeId = placeId;
		}

		public Integer getIsConfirmed() {
			if(isConfirmed != null) {
				return isConfirmed;
			} else{
				return 0;
			}
		}

		public void setIsConfirmed(Integer isConfirmed) {
			this.isConfirmed = isConfirmed;
		}
	}
}

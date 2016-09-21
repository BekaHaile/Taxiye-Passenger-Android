package product.clicklabs.jugnoo.home.trackinglog;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shankar on 9/21/16.
 */
public class TrackingLogReponse {

	@SerializedName("flag")
	@Expose
	private Integer flag;
	@SerializedName("ios_data")
	@Expose
	private List<Datum> iosData = new ArrayList<Datum>();
	@SerializedName("android_data")
	@Expose
	private List<Datum> androidData = new ArrayList<Datum>();

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
	 * The iosData
	 */
	public List<Datum> getIosData() {
		return iosData;
	}

	/**
	 *
	 * @param iosData
	 * The ios_data
	 */
	public void setIosData(List<Datum> iosData) {
		this.iosData = iosData;
	}

	/**
	 *
	 * @return
	 * The androidData
	 */
	public List<Datum> getAndroidData() {
		return androidData;
	}

	/**
	 *
	 * @param androidData
	 * The android_data
	 */
	public void setAndroidData(List<Datum> androidData) {
		this.androidData = androidData;
	}


	public class Datum {

		@SerializedName("data")
		@Expose
		private String data;
		@SerializedName("timestamp")
		@Expose
		private String timestamp;

		private ViewType viewType;
		private String label;
		/**
		 *
		 * @return
		 * The data
		 */
		public String getData() {
			return data;
		}

		/**
		 *
		 * @param data
		 * The data
		 */
		public void setData(String data) {
			this.data = data;
		}

		/**
		 *
		 * @return
		 * The timestamp
		 */
		public String getTimestamp() {
			return timestamp;
		}

		/**
		 *
		 * @param timestamp
		 * The timestamp
		 */
		public void setTimestamp(String timestamp) {
			this.timestamp = timestamp;
		}

		public ViewType getViewType() {
			return viewType;
		}

		public void setViewType(ViewType viewType) {
			this.viewType = viewType;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}
	}

	public enum ViewType{
		LABEL(0), DATA(1);

		private int ordinal;

		ViewType(int ordinal){
			this.ordinal = ordinal;
		}

		public int getOrdinal(){
			return ordinal;
		}
	}

}

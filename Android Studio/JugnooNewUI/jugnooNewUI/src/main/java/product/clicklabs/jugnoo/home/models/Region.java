package product.clicklabs.jugnoo.home.models;

import android.content.Context;
import android.graphics.drawable.StateListDrawable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.FareStructure;
import product.clicklabs.jugnoo.utils.Utils;

/**
 * Created by shankar on 3/19/16.
 */
public class Region {

	@SerializedName("vehicle_type")
	@Expose
	private Integer vehicleType;
	@SerializedName("region_id")
	@Expose
	private Integer regionId;
	@SerializedName("operator_id")
	@Expose
	private int operatorId;
	@SerializedName("ride_type")
	@Expose
	private Integer rideType;
	@SerializedName("max_people")
	@Expose
	private Integer maxPeople;
	@SerializedName("region_name")
	@Expose
	private String regionName;
	@SerializedName("icon_set")
	@Expose
	private String iconSet;
	@SerializedName("customer_fare_factor")
	@Expose
	private Double customerFareFactor;
	@SerializedName("driver_fare_factor")
	@Expose
	private Double driverFareFactor;
	@SerializedName("priority_tip_category")
	@Expose
	private Integer priorityTipCategory;
	@SerializedName("destination_mandatory")
	@Expose
	private  Integer destinationMandatory = 0;
	@SerializedName("images")
	@Expose
	private Images images;
	@SerializedName("offer_texts")
	@Expose
	private OfferTexts offerTexts;

	private VehicleIconSet vehicleIconSet;

	private FareStructure fareStructure;

	@SerializedName("eta")
	@Expose
	private String eta;
	@SerializedName("deepindex")
	@Expose
	private Integer deepindex;
	@SerializedName("show_fare_estimate")
	@Expose
	private int showFareEstimate;
	@SerializedName("reverse_bidding_enabled")
	@Expose
	private int reverseBid;

	private boolean isDefault = false;

	public Region(){
		this.vehicleType = Constants.VEHICLE_AUTO;
		this.regionId = Constants.VEHICLE_AUTO;
		this.regionName = "Auto";
		this.iconSet = VehicleIconSet.ORANGE_AUTO.getName();
		this.vehicleIconSet = VehicleIconSet.ORANGE_AUTO;
		this.eta = "-";
		this.fareStructure = JSONParser.getDefaultFareStructure();
		this.rideType = 0;
		this.maxPeople = 3;
		this.customerFareFactor = 1.0;
		this.driverFareFactor = 1.0;
		this.priorityTipCategory = 0;
		this.isDefault = true;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public void setIsDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	public int getShowFareEstimate() {
		return showFareEstimate;
	}

	public void setShowFareEstimate(int showFareEstimate) {
		this.showFareEstimate = showFareEstimate;
	}

	public int getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(int operatorId) {
		this.operatorId = operatorId;
	}

	public int getReverseBid() {
		return reverseBid;
	}

	public void setReverseBid(int reverseBid) {
		this.reverseBid = reverseBid;
	}

	public class OfferTexts {

		@SerializedName("text1")
		@Expose
		private String text1;
		@SerializedName("text2")
		@Expose
		private String text2;

		/**
		 *
		 * @return
		 * The text1
		 */
		public String getText1() {
			return text1;
		}

		/**
		 *
		 * @param text1
		 * The text1
		 */
		public void setText1(String text1) {
			this.text1 = text1;
		}

		/**
		 *
		 * @return
		 * The text2
		 */
		public String getText2() {
			if(text2 == null){
				return "";
			}
			return text2;
		}

		/**
		 *
		 * @param text2
		 * The text2
		 */
		public void setText2(String text2) {
			this.text2 = text2;
		}

	}

	public class Images {

		@SerializedName("tab_normal")
		@Expose
		private String tabNormal;
		@SerializedName("tab_highlighted")
		@Expose
		private String tabHighlighted;
		@SerializedName("ride_now_normal")
		@Expose
		private String rideNowNormal;
		@SerializedName("ride_now_highlighted")
		@Expose
		private String rideNowHighlighted;
		@SerializedName("marker_icon")
		@Expose
		private String markerIcon;

		/**
		 * @return The tabNormal
		 */
		public String getTabNormal() {
			return tabNormal;
		}

		/**
		 * @param tabNormal The tab_normal
		 */
		public void setTabNormal(String tabNormal) {
			this.tabNormal = tabNormal;
		}

		/**
		 * @return The tabHighlighted
		 */
		public String getTabHighlighted() {
			return tabHighlighted;
		}

		/**
		 * @param tabHighlighted The tab_highlighted
		 */
		public void setTabHighlighted(String tabHighlighted) {
			this.tabHighlighted = tabHighlighted;
		}

		/**
		 * @return The rideNowNormal
		 */
		public String getRideNowNormal() {
			return rideNowNormal;
		}

		/**
		 * @param rideNowNormal The ride_now_normal
		 */
		public void setRideNowNormal(String rideNowNormal) {
			this.rideNowNormal = rideNowNormal;
		}

		/**
		 * @return The rideNowHighlighted
		 */
		public String getRideNowHighlighted() {
			return rideNowHighlighted;
		}

		/**
		 * @param rideNowHighlighted The ride_now_highlighted
		 */
		public void setRideNowHighlighted(String rideNowHighlighted) {
			this.rideNowHighlighted = rideNowHighlighted;
		}

		public String getMarkerIcon() {
			return markerIcon;
		}

		public void setMarkerIcon(String markerIcon) {
			this.markerIcon = markerIcon;
		}
	}


	public Integer getVehicleType() {
		return vehicleType;
	}

	public void setVehicleType(Integer vehicleType) {
		this.vehicleType = vehicleType;
	}

	public FareStructure getFareStructure() {
		return fareStructure;
	}

	public void setFareStructure(FareStructure fareStructure) {
		this.fareStructure = fareStructure;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public String getEta() {
		if(eta == null){
			return "-";
		} else {
			return eta;
		}
	}

	public void setEta(String eta) {
		this.eta = eta;
	}

	public String getIconSet() {
		return iconSet;
	}

	public void setIconSet(String iconSet) {
		this.iconSet = iconSet;
	}

	public VehicleIconSet getVehicleIconSet() {
		return vehicleIconSet;
	}

	public void setVehicleIconSet(VehicleIconSet vehicleIconSet) {
		this.vehicleIconSet = vehicleIconSet;
	}

	public int getTabNormal(){
		if(vehicleType == VehicleTypeValue.AUTOS.getOrdinal() && rideType == RideTypeValue.POOL.getOrdinal()){
			return R.drawable.ic_auto_pool_tab_normal;
		} else {
			return getVehicleIconSet().getIconTab();
		}
	}

	public int getTabSelected(){
		if(vehicleType == VehicleTypeValue.AUTOS.getOrdinal() && rideType == RideTypeValue.POOL.getOrdinal()){
			return R.drawable.ic_auto_pool_tab_selected;
		} else {
			return getVehicleIconSet().getIconTabSelected();
		}
	}

	public StateListDrawable getRequestSelector(Context context){
		if(vehicleType == VehicleTypeValue.AUTOS.getOrdinal() && rideType == RideTypeValue.POOL.getOrdinal()){
			return Utils.getSelector(context, R.drawable.ic_auto_pool_request_normal, R.drawable.ic_auto_pool_request_selected);
		} else {
			return getVehicleIconSet().getRequestSelector(context);
		}
	}

	public Integer getRegionId() {
		return regionId;
	}

	public void setRegionId(Integer regionId) {
		this.regionId = regionId;
	}

	public Integer getRideType() {
		return rideType;
	}

	public void setRideType(Integer rideType) {
		this.rideType = rideType;
	}

	public Images getImages() {
		if(images == null){
			images = new Images();
		}
		return images;
	}

	public void setImages(Images images) {
		this.images = images;
	}

	public Integer getMaxPeople() {
		return maxPeople;
	}

	public void setMaxPeople(Integer maxPeople) {
		this.maxPeople = maxPeople;
	}

	public OfferTexts getOfferTexts() {
		return offerTexts;
	}

	public void setOfferTexts(OfferTexts offerTexts) {
		this.offerTexts = offerTexts;
	}

	public Double getCustomerFareFactor() {
		return customerFareFactor;
	}

	public void setCustomerFareFactor(Double customerFareFactor) {
		this.customerFareFactor = customerFareFactor;
	}

	public Integer getPriorityTipCategory() {
		return priorityTipCategory;
	}

	public void setPriorityTipCategory(Integer priorityTipCategory) {
		this.priorityTipCategory = priorityTipCategory;
	}

	public Double getDriverFareFactor() {
		return driverFareFactor;
	}

	public void setDriverFareFactor(Double driverFareFactor) {
		this.driverFareFactor = driverFareFactor;
	}

	public Integer getDestinationMandatory() {
		return destinationMandatory;
	}

	public void setDestinationMandatory(Integer destinationMandatory) {
		this.destinationMandatory = destinationMandatory;
	}

	public Integer getDeepindex() {
		if(deepindex == null){
			return -1;
		}
		return deepindex;
	}

	public void setDeepindex(Integer deepindex) {
		this.deepindex = deepindex;
	}
}
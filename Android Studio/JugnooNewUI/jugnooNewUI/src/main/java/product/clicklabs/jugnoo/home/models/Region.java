package product.clicklabs.jugnoo.home.models;

import android.content.Context;
import android.graphics.drawable.StateListDrawable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.FareStructure;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.retrofit.model.Package;
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

	@SerializedName("region_fare")
	@Expose
	private RegionFare regionFare;
	@SerializedName("fare_mandatory")
	@Expose
	private int fareMandatory;
	@SerializedName("description")
	@Expose
	private String description;
	@SerializedName("packages")
	@Expose
	private ArrayList<Package> packages;

	@SerializedName("customer_notes_enabled")
	@Expose
	private int customerNotes;

	private ArrayList<Integer> availablePaymentModes;

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
		this.availablePaymentModes = new ArrayList<>();
//		availablePaymentModes.add(PaymentOption.CASH.getOrdinal());
//		availablePaymentModes.add(PaymentOption.STRIPE_CARDS.getOrdinal());
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

	public RegionFare getRegionFare() {
		return regionFare;
	}

	public ArrayList<Package> getPackages() {
		return packages;
	}

	public ArrayList<Integer> getAvailablePaymentModes() {
		if(availablePaymentModes == null) {
			availablePaymentModes = new ArrayList<>();
		}
		return availablePaymentModes;
	}

	public void setAvailablePaymentModes(ArrayList<Integer> availablePaymentModes) {
		this.availablePaymentModes = availablePaymentModes;
	}

	public void setPackages(ArrayList<Package> packages) {
		this.packages = packages;
	}

	public String getDescription() {
		if(description == null){
			description = "";
		}
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public class RegionFare{
		@SerializedName("fare")
		@Expose
		private double fare;
		@SerializedName("fare_without_discount")
		@Expose
		private double fareWithoutDiscount;
		@SerializedName("currency")
		@Expose
		private String currency;
		@SerializedName("striked_fare_text")
		@Expose
		private String strikedFareText;
		@SerializedName("fare_text")
		@Expose
		private String fareText;
		@SerializedName("pool_fare_id")
		@Expose
		private int poolFareId;

		public double getFare() {
			return fare;
		}


		public String getDiscountText(int fareMandatory){
//			if(fareMandatory == 1 && fareWithoutDiscount > 0 && fareWithoutDiscount != fare){
//				double percent = (fareWithoutDiscount - fare)*100.0/fare;
//				return ((int)percent)+"%\noff";
//			}
			return "";
		}

		public CharSequence getFareText(int fareMandatory){
//			if(fareMandatory == 1){
//				return Utils.formatCurrencyValue(currency, fare);
//			} else {
				return fareText;
//			}
		}
		public CharSequence getStrikedFareText(int fareMandatory){
//			if(fareMandatory == 1){
//				return Utils.formatCurrencyValue(currency, fareWithoutDiscount);
//			} else {
				return strikedFareText;
//			}
		}

		public int getPoolFareId() {
			return poolFareId;
		}

		public void setPoolFareId(int poolFareId) {
			this.poolFareId = poolFareId;
		}
	}


	public int getFareMandatory() {
		return fareMandatory;
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
		}
		else if(vehicleType == VehicleTypeValue.BIKES.getOrdinal() && rideType == RideTypeValue.POOL.getOrdinal()){
			return Utils.getSelector(context, R.drawable.ic_bike_request_normal, R.drawable.ic_bike_request_pressed);
		}else {
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

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Region && ((Region) obj).regionId.equals(regionId));
	}

	public int getCustomerNotesEnabled() {
		return customerNotes;
	}

	public void setCustomerNotesEnabled(int customerNotes) {
		this.customerNotes = customerNotes;
	}
}
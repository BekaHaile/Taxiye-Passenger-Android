package product.clicklabs.jugnoo.datastructure;

import java.util.ArrayList;
import java.util.Locale;

import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.home.models.VehicleIconSet;

public class EndRideData {

	private String fuguChannelId;
	private String fuguChannelName;
	private ArrayList<String> fuguTags;
	public String engagementId;
	public String driverName;
	public String driverCarNumber, driverImage,
		pickupAddress, dropAddress,
		pickupTime, dropTime;
	public double fare, tollCharge, luggageCharge, convenienceCharge, discount, paidUsingWallet, toPay,
		distance, rideTime, waitTime, baseFare, fareFactor, finalFare, sumAdditionalCharges;
	public double paidUsingPaytm, paidUsingMobikwik, paidUsingFreeCharge,paidUsingMpesa, paidUsingRazorpay;
	public int waitingChargesApplicable;
	public ArrayList<DiscountType> discountTypes, stripeCardsAmount;
	private String rideDate, phoneNumber, tripTotal, engagementDate, invoiceAdditionalTextCabs;
	private int vehicleType, isPooled;
	private VehicleIconSet vehicleIconSet;
	private int totalRide;
	private int status;
	private String supportNumber;
	private int showPaymentOptions, paymentOption;
	private int operatorId;
	private String currency;
	private String distanceUnit;
	private double paidUsingStripe;
	private double netCustomerTax;
	private String last_4;
	private double taxPercentage;
	private String iconUrl;
	private double driverTipAmount;
	private double luggageChargesNew;
	private int reverseBid;
	private int isCorporateRide;
	private String partnerName;
	private int showTipOption, meterFareApplicable;
	private double paidUsingPOS;
	private int driverId;
	private String driverUpiId;

	public EndRideData(String engagementId, String driverName, String driverCarNumber, String driverImage,
					   String pickupAddress, String dropAddress, String pickupTime, String dropTime,
					   double fare, double luggageCharge, double convenienceCharge, double discount, double paidUsingWallet,
					   double toPay, double distance, double rideTime, double waitTime, double baseFare, double fareFactor,
					   ArrayList<DiscountType> discountTypes, int waitingChargesApplicable, double paidUsingPaytm,
					   String rideDate, String phoneNumber, String tripTotal, int vehicleType, String iconSet, int isPooled,
					   double sumAdditionalCharges, String engagementDate, double paidUsingMobikwik, double paidUsingFreeCharge, double paidUsingMpesa, double paidUsingRazorpay,
					   double paidUsingStripe, String last_4, int totalRide, int status, String supportNumber, String invoiceAdditionalTextCabs,
					   String fuguChannelId, String fuguChannelName, ArrayList<String> fuguTags, int showPaymentOptions,
					   int paymentOption, int operatorId, String currency, String distanceUnit, String iconUrl, double tollCharge, double driverTipAmount,
					   double luggageChargesNew, double netCustomerTax, double taxPercentage, int reverseBid, int isCorporateRide,
					   String partnerName, int showTipOption,double paidUsingPOS, ArrayList<DiscountType> stripeCardsAmount, int meterFareApplicable,
					   int driverId, String driverUpiId){
		this.totalRide = totalRide;
		this.engagementId = engagementId;
		this.driverName = driverName;
		this.driverCarNumber = driverCarNumber.toUpperCase(Locale.ENGLISH);
		this.driverImage = driverImage;
		this.pickupAddress = pickupAddress;
		this.dropAddress = dropAddress;
		this.pickupTime = pickupTime;
		this.dropTime = dropTime;

		this.fare = fare;
		this.tollCharge = tollCharge;
		this.luggageCharge = luggageCharge;
		this.convenienceCharge = convenienceCharge;
		this.discount = discount;
		this.paidUsingWallet = paidUsingWallet;
		this.toPay = toPay;
		this.paidUsingPaytm = paidUsingPaytm;
		
		this.distance = distance;
		this.rideTime = rideTime;
		this.waitTime = waitTime;
		this.baseFare = baseFare;
        this.fareFactor = fareFactor;
		this.paymentOption = paymentOption;
		this.showTipOption = showTipOption;
		this.discountTypes = new ArrayList<>();
		this.discountTypes.addAll(discountTypes);
		discountTypes.clear();

		if(this.rideTime > -1){
			if(this.waitTime > -1){
				this.rideTime = this.rideTime + this.waitTime;
			}
		}
		this.waitingChargesApplicable = waitingChargesApplicable;
		if(this.waitingChargesApplicable == 0 && this.waitTime > 0){
			this.waitingChargesApplicable = 1;
		}
		this.sumAdditionalCharges = sumAdditionalCharges;


		this.rideDate = rideDate;
		this.phoneNumber = phoneNumber;
		this.tripTotal = tripTotal;
		this.vehicleType = vehicleType;
		this.vehicleIconSet = HomeUtil.getVehicleIconSet(iconSet);
		this.isPooled = isPooled;
		this.engagementDate = engagementDate;
		this.totalRide = totalRide;

		this.paidUsingMobikwik = paidUsingMobikwik;
		this.paidUsingFreeCharge = paidUsingFreeCharge;
		this.paidUsingMpesa = paidUsingMpesa;
		this.paidUsingRazorpay = paidUsingRazorpay;

		this.engagementDate = engagementDate;
		this.status = status;

		this.supportNumber = supportNumber;
		this.invoiceAdditionalTextCabs = invoiceAdditionalTextCabs;

		this.fuguChannelId = fuguChannelId;
		this.fuguChannelName = fuguChannelName;
		this.fuguTags = fuguTags;
		this.showPaymentOptions = showPaymentOptions;
		this.operatorId = operatorId;
		this.currency = currency;
		this.distanceUnit = distanceUnit;
		this.iconUrl = iconUrl;
		this.paidUsingStripe = paidUsingStripe;
		this.last_4 = last_4;
		this.isCorporateRide = isCorporateRide;
		this.partnerName = partnerName;
		this.driverTipAmount = driverTipAmount;
		this.luggageChargesNew = luggageChargesNew;
		this.netCustomerTax = netCustomerTax;
		this.taxPercentage = taxPercentage;
		this.reverseBid = reverseBid;
		this.paidUsingPOS = paidUsingPOS;
		this.stripeCardsAmount = stripeCardsAmount;
		this.meterFareApplicable = meterFareApplicable;
		this.driverId = driverId;
		this.driverUpiId = driverUpiId;

		this.finalFare = meterFareApplicable == 0 ? (this.fare + this.luggageCharge + this.convenienceCharge + this.luggageChargesNew - this.discount + this.sumAdditionalCharges
				+ this.tollCharge + this.driverTipAmount + this.netCustomerTax) : this.fare;
	}


	public void setDriverNameCarName(String driverName, String driverCarNumber){
		this.driverName = driverName;
		this.driverCarNumber = driverCarNumber;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getTripTotal() {
		return tripTotal;
	}

	public void setTripTotal(String tripTotal) {
		this.tripTotal = tripTotal;
	}

	public int getVehicleType() {
		return vehicleType;
	}

	public void setVehicleType(int vehicleType) {
		this.vehicleType = vehicleType;
	}

	public VehicleIconSet getVehicleIconSet() {
		return vehicleIconSet;
	}

	public void setVehicleIconSet(VehicleIconSet vehicleIconSet) {
		this.vehicleIconSet = vehicleIconSet;
	}

	public int getIsPooled() {
		return isPooled;
	}

	public void setIsPooled(int isPooled) {
		this.isPooled = isPooled;
	}

	public ArrayList<DiscountType> getDiscountTypes() {
		return discountTypes;
	}

	public void setDiscountTypes(ArrayList<DiscountType> discountTypes) {
		this.discountTypes = discountTypes;
	}

	public ArrayList<DiscountType> getStripeCardsAmount() {
		if(stripeCardsAmount == null){
			stripeCardsAmount = new ArrayList<>();
		}
		return stripeCardsAmount;
	}

	public void setStripeCardsAmount(ArrayList<DiscountType> stripeCardsAmount) {
		this.stripeCardsAmount = stripeCardsAmount;
	}

	public String getEngagementDate() {
		return engagementDate;
	}

	public void setEngagementDate(String engagementDate) {
		this.engagementDate = engagementDate;
	}

	public int getTotalRide() {
		return totalRide;
	}

	public void setTotalRide(int totalRide) {
		this.totalRide = totalRide;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getSupportNumber() {
		return supportNumber;
	}

	public void setSupportNumber(String supportNumber) {
		this.supportNumber = supportNumber;
	}

	public String getInvoiceAdditionalTextCabs() {
		return invoiceAdditionalTextCabs;
	}

	public void setInvoiceAdditionalTextCabs(String invoiceAdditionalTextCabs) {
		this.invoiceAdditionalTextCabs = invoiceAdditionalTextCabs;
	}

	public String getFuguChannelId() {
		return fuguChannelId;
	}

	public String getFuguChannelName() {
		return fuguChannelName;
	}

	public ArrayList<String> getFuguTags() {
		return fuguTags;
	}

	public int getShowPaymentOptions() {
		return showPaymentOptions;
	}

	public void setShowPaymentOptions(int showPaymentOptions) {
		this.showPaymentOptions = showPaymentOptions;
	}

	public int getPaymentOption() {
		return paymentOption;
	}

	public void setPaymentOption(int paymentOption) {
		this.paymentOption = paymentOption;
	}

	public int getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(int operatorId) {
		this.operatorId = operatorId;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getDistanceUnit() {
		return distanceUnit;
	}

	public void setDistanceUnit(String distanceUnit) {
		this.distanceUnit = distanceUnit;
	}

	public double getPaidUsingStripe() {
		return paidUsingStripe;
	}

	public double getNetCustomerTax() {
		return netCustomerTax;
	}

	public String getLast_4() {
		return last_4;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public double getDriverTipAmount() {
		return driverTipAmount;
	}

	public void setDriverTipAmount(double driverTipAmount) {
		this.driverTipAmount = driverTipAmount;
	}

	public double getLuggageChargesNew() {
		return luggageChargesNew;
	}

	public void setLuggageChargesNew(double luggageChargesNew) {
		this.luggageChargesNew = luggageChargesNew;
	}

	public double getTaxPercentage() {
		return taxPercentage;
	}

	public int getReverseBid() {
		return reverseBid;
	}

	public int getIsCorporateRide() {
		return isCorporateRide;
	}

	public void setIsCorporateRide(int isCorporateRide) {
		this.isCorporateRide = isCorporateRide;
	}

	public String getPartnerName() {
		return partnerName;
	}

	public void setPartnerName(String partnerName) {
		this.partnerName = partnerName;
	}

	public double getPaidUsingPOS() {
		return paidUsingPOS;
	}

	public int getShowTipOption() {
		return showTipOption;
	}

	public void setShowTipOption(int showTipOption) {
		this.showTipOption = showTipOption;
	}

	public int getMeterFareApplicable() {
		return meterFareApplicable;
	}

	public int getDriverId() {
		return driverId;
	}

	public void setDriverId(int driverId) {
		this.driverId = driverId;
	}

	public String getDriverUpiId() {
		return driverUpiId;
	}

	public void setDriverUpiId(String driverUpiId) {
		this.driverUpiId = driverUpiId;
	}
}
